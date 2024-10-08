package com.shubham.book.auth;

import com.shubham.book.email.EmailService;
import com.shubham.book.email.EmailTemplateName;
import com.shubham.book.role.RoleRepository;
import com.shubham.book.security.JwtService;
import com.shubham.book.user.Token;
import com.shubham.book.user.TokenRepository;
import com.shubham.book.user.User;
import com.shubham.book.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    private final AuthenticationManager authenticationManager;

    public void register(RegistrationRequest request) throws MessagingException {

        var userRole = roleRepository.findByName("USER").orElseThrow(
                () -> new RuntimeException("Error: Role is not found.")
        );

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);

        sendValidationEmail(user);

    }

    private void sendValidationEmail(User user) throws MessagingException {

        var newToken = generateAndSaveActivationToken(user);
        // email
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Activate your account"
                );

    }

    private String generateAndSaveActivationToken(User user) {

        // generate token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);

        return generatedToken;
    }

    private String generateActivationCode(int length) {

        String characters = "0123456789";

        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword() // Do not encode the password here
                    )
            );

            var user = (User) auth.getPrincipal();
            var claims = new HashMap<String, Object>();
            claims.put("fullName", user.getFullName());

            var jwtToken = jwtService.generateToken(claims, user);

            return AuthenticationResponse.builder().token(jwtToken).build();
        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid email/password supplied");
        }
    }

    public void activateAccount(String token) throws MessagingException {

        Token savedToken = tokenRepository.findByToken(token).orElseThrow(
            // TODO execption has to be defined
                () -> new RuntimeException("Error: Token not found"));

        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Error: Token has expired");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }
}

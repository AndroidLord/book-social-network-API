package com.shubham.book.feedback;

import com.shubham.book.book.BookRepository;
import com.shubham.book.common.PageResponse;
import com.shubham.book.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedBackService {

    private final BookRepository bookRepository;
    private final FeedBackRepository feedbackRepository;
    private final FeedBackMapper feedbackMapper;

    public FeedBack saveFeedback(FeedBackRequest request, Authentication connectedUser) {

        var book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new IllegalArgumentException("No book found with id " + request.bookId()));
        User user = (User) connectedUser.getPrincipal();

         if(book.isArchived() || !book.getOwner().equals(user)) {
            throw new IllegalArgumentException("You can't give feedback for an archived book or a non-shareable book");
         }

         if(book.getOwner().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can't give feedback for your own book");
         }

        var feedBack = feedbackMapper.toFeedBack(request);


        return feedbackRepository.save(feedBack);
    }

    public PageResponse<FeedbackResponse> findAllFeedbackByBookId(
            Integer bookId,
            Integer page,
            Integer size,
            Authentication connectedUser) {

        Pageable pageable = PageRequest.of(page, size);
        User user = (User) connectedUser.getPrincipal();
        Page<FeedBack> feedBacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses =
            feedBacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedBacks.getNumber(),
                feedBacks.getSize(),
                feedBacks.getTotalElements(),
                feedBacks.getTotalPages(),
                feedBacks.isFirst(),
                feedBacks.isLast()
        );
    }
}

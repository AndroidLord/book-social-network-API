package com.shubham.book.feedback;

import com.shubham.book.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@Tag(name = "FeedBack", description = "The FeedBack API")
public class FeedBackController {

    private final FeedBackService feedBackService;

    @PostMapping
    public FeedBack saveFeedback(
            @Valid @RequestBody FeedBackRequest request,
            Authentication connectedUser
    ) {
        return feedBackService.saveFeedback(request, connectedUser);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBookId(
            @PathVariable Integer bookId,
            @RequestParam(defaultValue = "0", name = "page") Integer page,
            @RequestParam(defaultValue = "10", name = "size") Integer size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(feedBackService.findAllFeedbackByBookId(bookId, page, size, connectedUser));
    }



}

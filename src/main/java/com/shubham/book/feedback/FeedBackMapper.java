package com.shubham.book.feedback;

import com.shubham.book.book.Book;
import com.shubham.book.user.User;
import org.springframework.stereotype.Service;

@Service
public class FeedBackMapper {
    public FeedBack toFeedBack(FeedBackRequest request) {
        return FeedBack.builder()
                .note(request.note())
                .comment(request.comment())
                .book(
                        Book.builder()
                                .id(request.bookId())
                                .build()
                )
                .build();
    }

    public FeedbackResponse toFeedbackResponse(FeedBack feedBack, Integer id) {

        return FeedbackResponse.builder()
                .note(feedBack.getNote())
                .comment(feedBack.getComment())
                .ownFeedback(feedBack.getBook().getOwner().getId().equals(id))
                .build();

    }
}

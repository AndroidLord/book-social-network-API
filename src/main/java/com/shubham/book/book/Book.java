package com.shubham.book.book;

import com.shubham.book.common.BaseEntity;
import com.shubham.book.feedback.FeedBack;
import com.shubham.book.history.BookTransactionHistory;
import com.shubham.book.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


import java.util.List;


// lombok annotations
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor

// Main annotations
@Entity
public class Book extends BaseEntity {

    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    // User relationship
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    // Feedback relationship
    @OneToMany(mappedBy = "book")
    private List<FeedBack> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;


    @Transient
    public double getRate() {

        if(feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        return feedbacks.stream()
                .mapToDouble(FeedBack::getNote)
                .average()
                .orElse(0.0);
    }

}

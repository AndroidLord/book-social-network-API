package com.shubham.book.feedback;

import com.shubham.book.book.Book;
import com.shubham.book.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


// lombok annotations
@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor

// Main annotations
@Entity
public class FeedBack extends BaseEntity {


    private Double note;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

}

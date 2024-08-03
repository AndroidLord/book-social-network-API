package com.shubham.book.history;


import com.shubham.book.book.Book;
import com.shubham.book.common.BaseEntity;
import com.shubham.book.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class BookTransactionHistory extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private boolean returned;
    private boolean returnApproved;

}

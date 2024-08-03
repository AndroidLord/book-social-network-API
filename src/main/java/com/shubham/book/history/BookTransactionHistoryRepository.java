package com.shubham.book.history;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory,Integer> {


    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.user.id = :id
            """)
    Page<BookTransactionHistory> findAllBorrowedBooks(Integer id, Pageable pageable);



    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.owner.id = :id
            """)
    Page<BookTransactionHistory> findAllReturnedBooks(Integer id, Pageable pageable);

    @Query("""
            SELECT (COUNT(*) > 0) AS isBorrowed
                        FROM BookTransactionHistory bookHistory
                        WHERE bookHistory.book.id = :bookId
                        AND bookHistory.user.id = :id
                        AND bookHistory.returnApproved = false
                        """)
    boolean isAlreadyBorrowedByUser(Integer bookId, Integer id);


    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.id = :bookId
            AND history.user.id = :user_id
            AND history.returned = false
            AND history.returnApproved = false
            """)
    Optional<BookTransactionHistory> findByBookIdAndUserId(Integer bookId, Integer user_id);

    @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.id = :bookId
            AND history.book.owner.id = :owner_id
            AND history.returned = true
            AND history.returnApproved = false
            """)
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(Integer bookId, Integer owner_id);
}

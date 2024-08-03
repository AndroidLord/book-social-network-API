package com.shubham.book.book;

import com.shubham.book.common.PageResponse;
import com.shubham.book.exception.OperationNotPermittedException;
import com.shubham.book.file.FileStorageService;
import com.shubham.book.history.BookTransactionHistory;
import com.shubham.book.history.BookTransactionHistoryRepository;
import com.shubham.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.shubham.book.book.BookSpecification.*;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionRepository;
    private final FileStorageService fileStorageService;

    public Integer save(BookRequest request, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(request);

        book.setOwner(user);

        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer id) {
        return bookRepository.findById(id)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID::" + id));
    }

    public PageResponse<BookResponse> getAllBooks(int page, int size, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(user.getId(), pageable);
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();

        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }


    public PageResponse<BookResponse> findAllBooksByOwner(Integer page, Integer size, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponses = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBook(Integer page, Integer size, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> borrowedBooks = transactionRepository.findAllBorrowedBooks(user.getId(), pageable);
        List<BorrowedBookResponse> borrowedBookResponses = borrowedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                borrowedBookResponses,
                borrowedBooks.getNumber(),
                borrowedBooks.getSize(),
                borrowedBooks.getTotalElements(),
                borrowedBooks.getTotalPages(),
                borrowedBooks.isFirst(),
                borrowedBooks.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBook(Integer page, Integer size, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> returnedBooks = transactionRepository.findAllReturnedBooks(user.getId(), pageable);
        List<BorrowedBookResponse> returnedBookResponses = returnedBooks.stream()
                .map(bookMapper::toBorrowedBookResponse)
                .toList();
        return new PageResponse<>(
                returnedBookResponses,
                returnedBooks.getNumber(),
                returnedBooks.getSize(),
                returnedBooks.getTotalElements(),
                returnedBooks.getTotalPages(),
                returnedBooks.isFirst(),
                returnedBooks.isLast()
        );

    }

    public Integer updateShareableStatus(Integer bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID::" + bookId));
        User user = (User) connectedUser.getPrincipal();

        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books shareable status");
        }

        book.setShareable(!book.isShareable());
        bookRepository.save(book);

        return book.getId();
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID::" + bookId));
        User user = (User) connectedUser.getPrincipal();

        if (!book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You cannot update others books archive status");
        }

        book.setArchived(!book.isShareable());
        bookRepository.save(book);

        return book.getId();
    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID::" + bookId));
        User user = (User) connectedUser.getPrincipal();

        // Check if book is available for borrowing
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("Book is not available for borrowing");
        }
        // Check if user has already borrowed the book
        if (book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow your own book");
        }

        final boolean isAlreadyBorrowed = transactionRepository.isAlreadyBorrowedByUser(bookId, user.getId());
        if (isAlreadyBorrowed) {
            throw new OperationNotPermittedException("The Requested book is already borrowed by you");
        }
        var transaction = BookTransactionHistory.builder()
                .book(book)
                .user(user)
                .returned(false)
                .returnApproved(false)
                .build();
        var transaction_id = transactionRepository.save(transaction).getId();
        return transaction_id;
    }

    public Integer returnBorrowBook(Integer bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID::" + bookId));
        User user = (User) connectedUser.getPrincipal();

        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException("Book is not available for borrowing");
        }

        if (book.getOwner().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("You cannot return your own book");
        }

        var transaction = transactionRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID::" + bookId));
        transaction.setReturned(true);
        return transactionRepository.save(transaction).getId();
    }

    public Integer approvedReturnBorrowedBook(Integer bookId, Authentication connectedUser) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID::" + bookId));
        User user_owner = (User) connectedUser.getPrincipal();

        if (book.getOwner().getId().equals(user_owner.getId())) {
            throw new OperationNotPermittedException("You cannot approve return of your own book");
        }

        var transaction = transactionRepository.findByBookIdAndOwnerId(bookId, user_owner.getId())
                .orElseThrow(() -> new EntityNotFoundException("The book is not returned yet!"));
        transaction.setReturnApproved(true);
        return transactionRepository.save(transaction).getId();
    }


    public void uploadBookCoverPicture(Integer bookId, MultipartFile file, Authentication connectedUser) {

        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with ID::" + bookId));
        User user = (User) connectedUser.getPrincipal();

        var bookCover = fileStorageService.saveFile(file, user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);

    }
}

package com.shubham.book.book;

import com.shubham.book.common.PageResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("book")
@RequiredArgsConstructor
@Tag(name = "Book", description = "Book API")
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<Integer> createBook(
            @Valid @RequestBody BookRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.save(request, connectedUser));
    }

    @PostMapping("{book-id}")
    public ResponseEntity<BookResponse> getBook(
            @PathVariable("book-id") Integer book_id
    ) {
        return ResponseEntity.ok(bookService.findById(book_id));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> getAllBook(
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.getAllBooks(page, size, connectedUser));
    }

    @GetMapping("/ownwer")
    public ResponseEntity<PageResponse<BookResponse>> findAllBookByOwnwer(
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllBooksByOwner(page, size, connectedUser));
    }

    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllBorrowedBook(
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllBorrowedBook(page, size, connectedUser));
    }


    @GetMapping("/returned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAllReturnedBook(
            @RequestParam(value = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(bookService.findAllReturnedBook(page, size, connectedUser));
    }


    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable("book-id") Integer book_id,
            Authentication connectedUser) {

        return ResponseEntity.ok(bookService.updateShareableStatus(book_id, connectedUser));
    }

    @PatchMapping("/archive/{book-id}")
    public ResponseEntity<Integer> updateArchiveStatus(
            @PathVariable("book-id") Integer book_id,
            Authentication connectedUser) {

        return ResponseEntity.ok(bookService.updateArchivedStatus(book_id, connectedUser));
    }

    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable("book-id") Integer book_id,
            Authentication connectedUser) {

        return ResponseEntity.ok(bookService.borrowBook(book_id, connectedUser));
    }

    @PostMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook(
            @PathVariable("book-id") Integer book_id,
            Authentication connectedUser) {

        return ResponseEntity.ok(bookService.returnBorrowBook(book_id, connectedUser));
    }

    @PostMapping("/borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowBook(
            @PathVariable("book-id") Integer book_id,
            Authentication connectedUser) {
        return ResponseEntity.ok(bookService.approvedReturnBorrowedBook(book_id, connectedUser));
    }

    @PostMapping(value = "/cover/{book-id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadBookCoverPicture(
            @PathVariable("book-id") Integer book_id,
            @Parameter()
            @RequestParam("file") MultipartFile file,
            Authentication connectedUser
    ) {
        bookService.uploadBookCoverPicture(book_id, file, connectedUser);
        return ResponseEntity.accepted().build();
    }

}

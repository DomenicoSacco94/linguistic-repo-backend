package com.linguistics.backendRepo.controller;

import com.linguistics.backendRepo.exceptions.BadRequestException;
import com.linguistics.backendRepo.exceptions.BookNotFoundException;
import com.linguistics.backendRepo.exceptions.DuplicateBookException;
import com.linguistics.backendRepo.model.Book;
import com.linguistics.backendRepo.repository.BookRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.linguistics.backendRepo.validator.BookValidator.validateBook;
import static com.linguistics.backendRepo.validator.BookValidator.validateBookFile;

//TODO Dockerization
//TODO DEPLOYMENT

@RestController("/")
public class BooksController {

    private BookRepository bookRepository;

    public BooksController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/books")
    public List<Book> index() {
        return bookRepository.getAllBy();
    }

    @GetMapping("/books/{id}")
    public Book getBook(@PathVariable String id) throws BookNotFoundException {
        Book book = bookRepository.findItemById(id);
        if (book == null) {
            throw new BookNotFoundException("This book id could not be found: " + id);
        }
        return book;
    }

    @PostMapping("/books")
    public Book getBookByTitle(@RequestBody Book book) {
        String title = book.getTitle();
        Book retrievedBook = bookRepository.findItemByTitle(title);
        if (retrievedBook == null) {
            throw new BookNotFoundException("This book could not be found: " + title);
        }
        return retrievedBook;
    }

    @PostMapping(value = "/addBookAsFile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String newBookFile(@RequestParam HashMap<String, String> formData, @RequestParam("file") MultipartFile file) {
        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (validateBookFile(formData, fileBytes)) {
            throw new BadRequestException("Please fill the mandatory fields");
        }
        Book book = Book.getBookFromFile(formData, fileBytes);
        if (bookRepository.findItemByTitle(book.getTitle()) != null) {
            throw new DuplicateBookException("This book already exists: " + book.getTitle());
        }
        bookRepository.save(book);
        return book.getTitle();
    }

    @PostMapping("/addBookAsText")
    public Book newBook(@RequestBody Book book) {
        if (validateBook(book)) {
            throw new BadRequestException("Please fill the mandatory fields");
        }
        if (bookRepository.findItemByTitle(book.getTitle()) != null) {
            throw new DuplicateBookException("This book already exists: " + book.getTitle());
        }
        return bookRepository.save(book);
    }

    @DeleteMapping("/books/{id}")
    void deleteBook(@PathVariable String id) throws BookNotFoundException {
        Book book = bookRepository.findItemById(id);
        if (book == null) {
            throw new BookNotFoundException("This book id could not be found: " + id);
        }
        bookRepository.deleteById(id);
    }

}

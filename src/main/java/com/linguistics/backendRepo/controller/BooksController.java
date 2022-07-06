package com.linguistics.backendRepo.controller;

import com.linguistics.backendRepo.config.ExceptionHandlerAdvice;
import com.linguistics.backendRepo.exceptions.BadRequestException;
import com.linguistics.backendRepo.exceptions.BookNotFoundException;
import com.linguistics.backendRepo.exceptions.DuplicateBookException;
import com.linguistics.backendRepo.model.Book;
import com.linguistics.backendRepo.model.BookRequest;
import com.linguistics.backendRepo.repository.BookRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//TODO add tests
//TODO Dockerization

@RestController("/")
public class BooksController {

    private BookRepository bookRepository;

    public BooksController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/books")
    public List<Book> index() {
        return bookRepository.findAll();
    }

    @PostMapping("/book")
    public Book singleParam(@RequestBody BookRequest bookRequest) {
        String title = bookRequest.getTitle();
        Book book = bookRepository.findItemByTitle(title);
        if(book == null) {
            throw new BookNotFoundException("This book could not be found: " + title);
        }
        return book;
    }

    @PostMapping("/addBook")
    Book newBook(@RequestBody Book book) {
            if(book.getTitle() == null || book.getLang() == null) {
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
            if(book == null) {
                throw new BookNotFoundException("This book id could not be found: " + id);
            }
            bookRepository.deleteById(id);
    }

}

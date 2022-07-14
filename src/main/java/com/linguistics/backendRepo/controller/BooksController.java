package com.linguistics.backendRepo.controller;

import com.linguistics.backendRepo.exceptions.BookNotFoundException;
import com.linguistics.backendRepo.model.Book;
import com.linguistics.backendRepo.service.BookService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

//TODO DEPLOYMENT

@RestController("/")
public class BooksController {

    private BookService bookService;

    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public List<Book> index() {
        return bookService.getAllBooks();
    }

    @GetMapping("/books/{id}")
    public Book getBook(@PathVariable String id) throws BookNotFoundException {
        return bookService.getBookById(id);
    }

    @PostMapping("/books")
    public Book getBookByTitle(@RequestBody Book book) {
        return bookService.getBookByTitle(book);
    }

    @PostMapping(value = "/addBookAsFile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String newBookFile(@RequestParam HashMap<String, String> formData, @RequestParam("file") MultipartFile file) {
        return bookService.saveBookAsFile(formData, file);
    }

    @PostMapping("/addBookAsText")
    public String newBook(@RequestBody Book book) {
        return bookService.saveBookAsText(book);
    }

    @DeleteMapping("/books/{id}")
    void deleteBook(@PathVariable String id) throws BookNotFoundException {
        bookService.deleteBookById(id);
    }

}

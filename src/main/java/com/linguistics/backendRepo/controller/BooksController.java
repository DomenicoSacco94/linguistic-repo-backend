package com.linguistics.backendRepo.controller;

import com.linguistics.backendRepo.exceptions.BadRequestException;
import com.linguistics.backendRepo.exceptions.BookNotFoundException;
import com.linguistics.backendRepo.exceptions.DuplicateBookException;
import com.linguistics.backendRepo.exceptions.InternalServerException;
import com.linguistics.backendRepo.model.Book;
import com.linguistics.backendRepo.repository.BookRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

//TODO add tests
//TODO implement utils classes for conversion and validation
//TODO Dockerization

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
        Book book = new Book();
        try {
            if (formData == null ||
                    formData.get("lang") == null ||
                    formData.get("lang").isEmpty() ||
                    formData.get("title") == null ||
                    formData.get("title").isEmpty() ||
                    file.getBytes() == null) {
                throw new BadRequestException("Please fill the mandatory fields");
            }
            book.setGenre(formData.get("genre"));
            book.setLang(formData.get("lang"));
            book.setTitle(formData.get("title"));
            book.setRawContent(file.getBytes());
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalServerException("Sorry, something went horribly wrong :(");
        }
        if (bookRepository.findItemByTitle(book.getTitle()) != null) {
            throw new DuplicateBookException("This book already exists: " + book.getTitle());
        }
        bookRepository.save(book);
        return book.getTitle();
    }

    @PostMapping("/addBookAsText")
    public Book newBook(@RequestBody Book book) {
        if (book.getTitle() == null || book.getLang() == null || book.getContent() == null) {
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

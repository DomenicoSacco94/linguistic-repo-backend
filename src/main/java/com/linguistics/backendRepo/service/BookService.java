package com.linguistics.backendRepo.service;

import com.linguistics.backendRepo.exceptions.BadRequestException;
import com.linguistics.backendRepo.exceptions.BookNotFoundException;
import com.linguistics.backendRepo.exceptions.DuplicateBookException;
import com.linguistics.backendRepo.model.Book;
import com.linguistics.backendRepo.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.linguistics.backendRepo.validator.BookValidator.validateBook;
import static com.linguistics.backendRepo.validator.BookValidator.validateBookFile;

@Service
public class BookService {
    private BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks() {
        return bookRepository.getAllBy();
    }

    public Book getBookById(String id) {
        Book book = bookRepository.findItemById(id);
        if (book == null) {
            throw new BookNotFoundException("This book id could not be found: " + id);
        }
        return book;
    }

    public Book getBookByTitle(Book book) {
        String title = book.getTitle();
        Book retrievedBook = bookRepository.findItemByTitle(title);
        if (retrievedBook == null) {
            throw new BookNotFoundException("This book could not be found: " + title);
        }
        return retrievedBook;
    }

    public String saveBookAsFile(HashMap<String, String> formData, MultipartFile file) {
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

    public String saveBookAsText(Book book) {
        if (validateBook(book)) {
            throw new BadRequestException("Please fill the mandatory fields");
        }
        if (bookRepository.findItemByTitle(book.getTitle()) != null) {
            throw new DuplicateBookException("This book already exists: " + book.getTitle());
        }
        bookRepository.save(book);
        return book.getTitle();
    }

    public void deleteBookById(String id) {
        Book book = bookRepository.findItemById(id);
        if (book == null) {
            throw new BookNotFoundException("This book id could not be found: " + id);
        }
        bookRepository.deleteById(id);
    }
}

package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linguistics.backendRepo.BackendRepoApplication;
import com.linguistics.backendRepo.config.ExceptionHandlerAdvice;
import com.linguistics.backendRepo.controller.BooksController;
import com.linguistics.backendRepo.exceptions.BookNotFoundException;
import com.linguistics.backendRepo.model.Book;
import com.linguistics.backendRepo.repository.BookRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BackendRepoApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BooksControllerTest {

    @LocalServerPort
    private int port;

    private MockMvc mvc;

    @Autowired
    BooksController booksController;

    @MockBean
    BookRepository bookRepository;

    @Before
    public void init() {
        bookRepository = Mockito.mock(BookRepository.class);
    }

    @BeforeEach
    public void setup() {

        mvc = MockMvcBuilders.standaloneSetup(booksController)
                .setControllerAdvice(new ExceptionHandlerAdvice())
                .build();
    }

    @Test
    void shouldRetrieveAllBooks() throws Exception {

        Book book1 = new Book(null, "book1", "content book 1", null);
        Book book2 = new Book(null, "book2", null, new byte[]{0,0,0,0});
        Book book3 = new Book(null, "book3", "content book 3", null);

        List<Book> books = List.of(new Book[]{book1, book2, book3});

        when(bookRepository.getAllBy()).thenReturn(books);

        MockHttpServletResponse response = mvc.perform(
                        get("http://localhost:" + port + "/books")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ObjectMapper mapper = new ObjectMapper();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(mapper.writeValueAsString(books));
    }

    @Test
    void shouldRetrieveBookById() throws Exception {

        String bookId = "123";

        Book book1 = new Book(bookId, "book1", "content book 1", null);

        when(bookRepository.findItemById(bookId)).thenReturn(book1);

        MockHttpServletResponse response = mvc.perform(
                        get("http://localhost:" + port + "/books/" + bookId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ObjectMapper mapper = new ObjectMapper();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(mapper.readValue(response.getContentAsString(), Book.class).getId().equals(bookId)).isTrue();
    }

    @Test
    void shouldRetrieveBookByIdNotFound() throws Exception {

        String bookId = "123";
        when(bookRepository.findItemById(bookId)).thenReturn(null);
        MockHttpServletResponse response = mvc.perform(
                        get("http://localhost:" + port + "/books/" + bookId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo("This book id could not be found: " + bookId);
    }


    @Test
    void shouldRetrieveBookByTitle() throws Exception {

        String bookTitle = "book 1";

        Book book1 = new Book(null, bookTitle, null, null);

        when(bookRepository.findItemByTitle(book1.getTitle())).thenReturn(book1);

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mvc.perform(
                        post("http://localhost:" + port + "/books/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(book1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(mapper.readValue(response.getContentAsString(), Book.class).getTitle().equals(bookTitle)).isTrue();
    }

    @Test
    void shouldRetrieveBookByTitleNotFound() throws Exception {

        String bookTitle = "book 1";

        Book book1 = new Book(null, bookTitle, null, null);

        when(bookRepository.findItemByTitle(book1.getTitle())).thenReturn(null);

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mvc.perform(
                        post("http://localhost:" + port + "/books/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(book1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEqualTo("This book could not be found: " + book1.getTitle());
    }
}

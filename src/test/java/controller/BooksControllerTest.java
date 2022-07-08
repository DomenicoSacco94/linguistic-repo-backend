package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linguistics.backendRepo.BackendRepoApplication;
import com.linguistics.backendRepo.config.ExceptionHandlerAdvice;
import com.linguistics.backendRepo.controller.BooksController;
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
        Book book1 = new Book(null, "book1", "ENG", "content book 1", null,"someGenre");
        Book book2 = new Book(null, "book2", "ENG", null, new byte[]{0,0,0,0},"someGenre");
        Book book3 = new Book(null, "book3", "ENG", "content book 3", null,"someGenre");
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
    void shouldDeleteBookById() throws Exception {
        String bookId = "123";
        when(bookRepository.findItemById(bookId)).thenReturn(null);

        MockHttpServletResponse response = mvc.perform(
                        delete("http://localhost:" + port + "/books/" + bookId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ObjectMapper mapper = new ObjectMapper();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @Test
    void shouldDeleteBookByIdConflict() throws Exception {
        String bookId = "123";
        Book book1 = new Book(bookId, "book1", "ENG", "content book 1", null,"someGenre");
        when(bookRepository.findItemById(bookId)).thenReturn(book1);

        MockHttpServletResponse response = mvc.perform(
                        delete("http://localhost:" + port + "/books/" + bookId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ObjectMapper mapper = new ObjectMapper();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void shouldRetrieveBookById() throws Exception {
        String bookId = "123";
        Book book1 = new Book(bookId, "book1", "ENG", "content book 1", null,"someGenre");
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
        Book book1 = new Book(null, bookTitle, "ENG", null, null,"someGenre");
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
        Book book1 = new Book(null, bookTitle, "ENG", null, null,"someGenre");
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

    @Test
    void shouldSaveBookByText() throws Exception {
        String bookTitle = "book title";
        Book book1 = new Book(null, bookTitle, "ENG", "some content", null,"someGenre");
        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book1);
        ObjectMapper mapper = new ObjectMapper();
        MockHttpServletResponse response = mvc.perform(
                        post("http://localhost:" + port + "/addBookAsText")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(book1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(mapper.readValue(response.getContentAsString(), Book.class).getTitle().equals(bookTitle)).isTrue();
    }

    @Test
    void shouldSaveBookByTextConflict() throws Exception {
        String bookTitle = "book title";
        Book book1 = new Book(null, bookTitle, "ENG", "some content", null,"someGenre");
        when(bookRepository.findItemByTitle(book1.getTitle())).thenReturn(book1);
        ObjectMapper mapper = new ObjectMapper();
        MockHttpServletResponse response = mvc.perform(
                        post("http://localhost:" + port + "/addBookAsText")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(book1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @Test
    void shouldSaveBookByTextNoContent() throws Exception {
        String bookTitle = "book title";
        Book book1 = new Book(null, bookTitle, "ENG", null, null,"someGenre");
        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book1);
        ObjectMapper mapper = new ObjectMapper();
        MockHttpServletResponse response = mvc.perform(
                        post("http://localhost:" + port + "/addBookAsText")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(book1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo("Please fill the mandatory fields");
    }

    @Test
    void shouldSaveBookByTextNoLanguage() throws Exception {
        String bookTitle = "book title";
        Book book1 = new Book(null, bookTitle, null, "test content", null,"someGenre");
        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book1);
        ObjectMapper mapper = new ObjectMapper();
        MockHttpServletResponse response = mvc.perform(
                        post("http://localhost:" + port + "/addBookAsText")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(book1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo("Please fill the mandatory fields");
    }

    @Test
    void shouldSaveBookByTextNoTitle() throws Exception {
        Book book1 = new Book(null, null, "ENG", "test content", null,"someGenre");
        when(bookRepository.save(Mockito.any(Book.class))).thenReturn(book1);
        ObjectMapper mapper = new ObjectMapper();
        MockHttpServletResponse response = mvc.perform(
                        post("http://localhost:" + port + "/addBookAsText")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(book1))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo("Please fill the mandatory fields");
    }

    @Test
    void shouldUploadBook() throws Exception {
        Book book = new Book();
        when(bookRepository.save(book)).thenReturn(book);
        MockHttpServletResponse response = mvc.perform(multipart("http://localhost:" + port + "/addBookAsFile")
                        .file("file", "Test Content".getBytes())
                        .param("lang", "ENG")
                        .param("title", "Test Content")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.MULTIPART_FORM_DATA))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString().equals("Test Content")).isTrue();
    }

    @Test
    void shouldUploadExistingFile() throws Exception {
        Book book = new Book();
        String bookTitle = "Test Content";
        when(bookRepository.findItemByTitle(bookTitle )).thenReturn(book);
        MockHttpServletResponse response = mvc.perform(multipart("http://localhost:" + port + "/addBookAsFile")
                        .file("file", bookTitle .getBytes())
                        .param("lang", "ENG")
                        .param("title", bookTitle )
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.MULTIPART_FORM_DATA))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.getContentAsString()).isEqualTo("This book already exists: " + bookTitle);
    }

    @Test
    void shouldUploadBookNoFile() throws Exception {
        Book book = new Book();
        when(bookRepository.save(book)).thenReturn(book);
        MockHttpServletResponse response = mvc.perform(multipart("http://localhost:" + port + "/addBookAsFile")
                        .param("lang", "ENG")
                        .param("title", "Test Content")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.MULTIPART_FORM_DATA))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void shouldUploadBookNoTitle() throws Exception {
        Book book = new Book();
        when(bookRepository.save(book)).thenReturn(book);
        MockHttpServletResponse response = mvc.perform(multipart("http://localhost:" + port + "/addBookAsFile")
                        .file("file", "Test Content".getBytes())
                        .param("lang", "ENG")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.MULTIPART_FORM_DATA))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo("Please fill the mandatory fields");
    }

    @Test
    void shouldUploadBookNoLang() throws Exception {
        Book book = new Book();
        when(bookRepository.save(book)).thenReturn(book);
        MockHttpServletResponse response = mvc.perform(multipart("http://localhost:" + port + "/addBookAsFile")
                        .file("file", "Test Content".getBytes())
                        .param("title", "Test Content")
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.MULTIPART_FORM_DATA))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo("Please fill the mandatory fields");
    }
}

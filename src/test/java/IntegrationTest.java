import com.fasterxml.jackson.databind.ObjectMapper;
import com.linguistics.backendRepo.BackendRepoApplication;
import com.linguistics.backendRepo.config.ExceptionHandlerAdvice;
import com.linguistics.backendRepo.controller.BooksController;
import com.linguistics.backendRepo.model.Book;
import com.linguistics.backendRepo.repository.BookRepository;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BackendRepoApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @LocalServerPort
    private int port;

    private MockMvc mvc;

    @Autowired
    BooksController booksController;

    Book referenceBook;

    @Autowired
    BookRepository bookRepository;

    @BeforeEach
    public void init() {
        Book book1 = new Book(null, "book1", "ENG", "content book 1", null, "someGenre");
        Book book2 = new Book(null, "book2", "ENG", null, new byte[]{0,0,0,0},"someGenre");
        Book book3 = new Book(null, "book3", "ENG", "content book 3", null,"someGenre");
        referenceBook = bookRepository.save(book1);
        bookRepository.save(book2);
        bookRepository.save(book3);
    }

    @AfterEach
    public void resetDb() {
        bookRepository.deleteAll();
    }


    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.standaloneSetup(booksController)
                .setControllerAdvice(new ExceptionHandlerAdvice())
                .build();
    }

    @Test
    void shouldRetrieveAllBooks() throws Exception {
        MockHttpServletResponse response = mvc.perform(
                        get("http://localhost:" + port + "/books")
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        ObjectMapper mapper = new ObjectMapper();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        List retrievedBooks = mapper.readValue(response.getContentAsString(), List.class);
        Book referenceBook = mapper.readValue(mapper.writeValueAsString(retrievedBooks.get(0)), Book.class);
        assertThat(retrievedBooks.size()==3).isTrue();
        assertThat(referenceBook.getId()!= null).isTrue();
        assertThat(referenceBook.getTitle()!= null).isTrue();
        assertThat(referenceBook.getLang()!= null).isTrue();
        assertThat(referenceBook.getGenre()!= null).isTrue();
        assertThat(referenceBook.getContent()== null).isTrue();
        assertThat(referenceBook.getRawContent()== null).isTrue();
    }

    @Test
    void shouldDeleteBookById() throws Exception {
        String bookId = referenceBook.getId();
        MockHttpServletResponse response = mvc.perform(
                        delete("http://localhost:" + port + "/books/" + bookId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(bookRepository.findItemById(referenceBook.getId())).isEqualTo(null);
    }

    @Test
    void shouldRetrieveBookById() throws Exception {
        String bookId = referenceBook.getId();
        MockHttpServletResponse response = mvc.perform(
                        get("http://localhost:" + port + "/books/" + bookId)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        ObjectMapper mapper = new ObjectMapper();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Book retrievedBook = mapper.readValue(response.getContentAsString(), Book.class);
        assertThat(mapper.writeValueAsString(retrievedBook).equals(mapper.writeValueAsString(referenceBook))).isTrue();
    }

    @Test
    void shouldRetrieveBookByTitle() throws Exception {
        String bookTitle = referenceBook.getTitle();
        Book bookRequest = new Book(null, bookTitle, null, null, null,null);
        ObjectMapper mapper = new ObjectMapper();
        MockHttpServletResponse response = mvc.perform(
                        post("http://localhost:" + port + "/books/")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(bookRequest))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Book retrievedBook = mapper.readValue(response.getContentAsString(), Book.class);
        assertThat(mapper.writeValueAsString(retrievedBook).equals(mapper.writeValueAsString(referenceBook))).isTrue();
    }

    @Test
    void shouldSaveBookByText() throws Exception {
        String bookTitle = "book title";
        Book book = new Book(null, bookTitle, "ENG", "some content", null,"someGenre");
        ObjectMapper mapper = new ObjectMapper();
        MockHttpServletResponse response = mvc.perform(
                        post("http://localhost:" + port + "/addBookAsText")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(book))
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Book savedBook = mapper.readValue(response.getContentAsString(), Book.class);
        book.setId(savedBook.getId());
        assertThat(mapper.writeValueAsString(savedBook)).isEqualTo(mapper.writeValueAsString(book));
    }

    @Test
    void shouldUploadBook() throws Exception {
        Book book = new Book(null,"Test title","ENG", null, "Test content".getBytes(), "someGenre");
        MockHttpServletResponse response = mvc.perform(multipart("http://localhost:" + port + "/addBookAsFile")
                        .file("file", book.getRawContent())
                        .param("lang", book.getLang())
                        .param("title", book.getTitle())
                        .param("genre", book.getGenre())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.MULTIPART_FORM_DATA))
                .andReturn().getResponse();

        ObjectMapper mapper = new ObjectMapper();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        Book savedBook = bookRepository.findItemByTitle(book.getTitle());
        book.setId(savedBook.getId());
        assertThat(mapper.writeValueAsString(savedBook)).isEqualTo(mapper.writeValueAsString(book));
    }

}

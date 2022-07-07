package controller;

import com.linguistics.backendRepo.BackendRepoApplication;
import com.linguistics.backendRepo.model.Book;
import com.linguistics.backendRepo.repository.BookRepository;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BackendRepoApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BooksControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    @MockBean
    BookRepository bookRepository;

    @Before
    public void before() {
        bookRepository = Mockito.mock(BookRepository.class);
    }

    @Test
    void shouldRetrieveAllBooks() {

        Book book1 = new Book(null, "book1", "content book 1", null);
        Book book2 = new Book(null, "book2", null, new byte[]{0,0,0,0});
        Book book3 = new Book(null, "book3", "content book 3", null);

        List<Book> books = List.of(new Book[]{book1, book2, book3});

        when(bookRepository.findAll()).thenReturn(books);

        List<Book> response = this.restTemplate.getForObject("http://localhost:" + port + "/books", List.class);

        assertThat(response)
                .hasSize(3);
    }
}

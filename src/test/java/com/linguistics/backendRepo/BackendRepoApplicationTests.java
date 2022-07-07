package com.linguistics.backendRepo;

import com.linguistics.backendRepo.controller.BooksController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BackendRepoApplicationTests {

	@Autowired
	private BooksController booksController;

	@Test
	void contextLoads() {
		assertThat(booksController).isNotNull();
	}

}

package com.linguistics.backendRepo;

import com.linguistics.backendRepo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@SpringBootApplication
public class BackendRepoApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendRepoApplication.class, args);
	}

}

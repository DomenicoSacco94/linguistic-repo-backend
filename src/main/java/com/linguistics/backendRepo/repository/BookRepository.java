package com.linguistics.backendRepo.repository;

import com.linguistics.backendRepo.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BookRepository extends MongoRepository<Book, String> {

    @Query("{name:'?0'}")
    Book findItemByTitle(String title);

    @Query(value="{genre:'?0'}", fields="{'title' : 1}")
    List<Book> findAll(String genre);

    public long count();

}

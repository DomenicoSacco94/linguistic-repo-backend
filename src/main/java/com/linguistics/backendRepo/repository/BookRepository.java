package com.linguistics.backendRepo.repository;

import com.linguistics.backendRepo.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BookRepository extends MongoRepository<Book, String> {

    @Query("{title:'?0'}")
    Book findItemByTitle(String title);

    @Query("{id:'?0'}")
    Book findItemById(String id);

    @Query(fields = "{'title' : 1, 'lang' : 1, 'genre':  1}")
    List<Book> getAllBy();
    public long count();

}

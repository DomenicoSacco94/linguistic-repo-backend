package com.linguistics.backendRepo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Blob;

@Document("Book")
public class Book {
    @Id
    private String id;

    private String genre;
    private String title;

    private Blob content;

    public Book(String id, String title, Blob content) {
        this.id = id;
        this.title = title;
        this.content = content;

    }

    public String getId() {
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Blob getContent() {
        return content;
    }

    public void setContent(Blob content) {
        this.content = content;
    }
}

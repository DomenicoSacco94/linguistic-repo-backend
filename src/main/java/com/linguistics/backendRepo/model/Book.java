package com.linguistics.backendRepo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.HashMap;

@Document("Book")
public class Book implements Serializable {
    @Id
    private String id;

    private String genre;

    @Indexed(unique = true)
    private String title;
    private String content;
    private byte[] rawContent;
    private String lang;

    public Book(String id, String title, String lang, String content, byte[] rawContent) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.rawContent = rawContent;
        this.lang = lang;
    }

    public static Book getBookFromFile(HashMap<String, String> formData, byte[] fileBytes) {
        Book book = new Book();
        book.setGenre(formData.get("genre"));
        book.setLang(formData.get("lang"));
        book.setTitle(formData.get("title"));
        book.setRawContent(fileBytes);
        return book;
    }

    public Book() {
    }

    public String getId() {
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getRawContent() {
        return rawContent;
    }

    public void setRawContent(byte[] rawContent) {
        this.rawContent = rawContent;
    }
}

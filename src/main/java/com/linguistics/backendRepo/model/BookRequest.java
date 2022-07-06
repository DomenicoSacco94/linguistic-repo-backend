package com.linguistics.backendRepo.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Book")
public class BookRequest {
    private String genre;
    @Indexed(unique = true)
    private String title;
    private String content;
    private String lang;

    public BookRequest(String genre, String title, String content, String lang) {
        this.genre = genre;
        this.title = title;
        this.content = content;
        this.lang = lang;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
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

    public void setContent(String textContent) {
        this.content = textContent;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

}

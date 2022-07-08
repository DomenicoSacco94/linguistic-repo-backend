package com.linguistics.backendRepo.validator;

import com.linguistics.backendRepo.model.Book;

import java.util.HashMap;

public class BookValidator {

    public static boolean validateBook(Book book) {
        return book.getTitle() == null || book.getLang() == null || book.getContent() == null;
    }

    public static boolean validateBookFile(HashMap<String, String> formData, byte[] fileBytes) {
        return formData == null ||
                formData.get("lang") == null ||
                formData.get("lang").isEmpty() ||
                formData.get("title") == null ||
                formData.get("title").isEmpty() ||
                fileBytes == null;
    }
}

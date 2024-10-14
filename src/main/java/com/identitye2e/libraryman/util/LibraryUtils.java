package com.identitye2e.libraryman.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.identitye2e.libraryman.model.Book;

public class LibraryUtils {

  public static boolean isNotBlank(String value) {
    return value != null && !value.isBlank();
  }

  public static Book buildBook(
      String isbn, String title, String author, int publicationYear, int copies) {
    return Book.builder()
        .isbn(isbn)
        .title(title)
        .author(author)
        .publicationYear(publicationYear)
        .availableCopies(copies)
        .build();
  }

  public static String getBookJSon(Book mockBook) {
    String mockBookJson = null;
    try {
      ObjectMapper Obj = new ObjectMapper();
      mockBookJson = Obj.writeValueAsString(mockBook);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return mockBookJson;
  }

  public static String buildBookJSon(
      String isbn, String title, String author, int publicationYear, int copies) {
    return getBookJSon(buildBook(isbn, title, author, publicationYear, copies));
  }
}

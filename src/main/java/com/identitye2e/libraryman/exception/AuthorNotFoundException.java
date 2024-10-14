package com.identitye2e.libraryman.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Author has no books in the library.") // 404
public class AuthorNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  public AuthorNotFoundException(String author) {
    super("No book found written by author: " + author);
  }
}

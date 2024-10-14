package com.identitye2e.libraryman.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Book already exists.") // 409
public class BookAlreadyExistException extends Exception {

  private static final long serialVersionUID = 1L;

  public BookAlreadyExistException(String isbn) {
    super("A book already exists with same isbn: " + isbn);
  }
}

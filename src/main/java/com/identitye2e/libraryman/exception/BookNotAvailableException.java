package com.identitye2e.libraryman.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Book not available for borrow.") // 409
public class BookNotAvailableException extends Exception {

  private static final long serialVersionUID = 1L;

  public BookNotAvailableException(String isbn) {
    super("All copies were already borrowed for the book with isbn: " + isbn);
  }
}

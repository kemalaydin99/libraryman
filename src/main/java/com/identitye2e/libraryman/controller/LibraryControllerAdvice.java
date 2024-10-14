package com.identitye2e.libraryman.controller;

import com.identitye2e.libraryman.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class LibraryControllerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler({AuthorNotFoundException.class})
  public ResponseEntity<Object> handleAuthorNotFoundException(Exception ex, WebRequest request) {
    return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({BookNotFoundException.class})
  public ResponseEntity<Object> handleBookNotFoundException(Exception ex, WebRequest request) {
    return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler({BookAlreadyExistException.class})
  public ResponseEntity<Object> handleBookAlreadyExistException(Exception ex, WebRequest request) {
    return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler({BookNotAvailableException.class})
  public ResponseEntity<Object> handleBookNotAvailableException(Exception ex, WebRequest request) {
    return new ResponseEntity<Object>(ex.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler({TooManyRequestException.class})
  public ResponseEntity<Object> handleTooManyRequestException(Exception ex, WebRequest request) {
    return new ResponseEntity<Object>(
        ex.getMessage(), new HttpHeaders(), HttpStatus.TOO_MANY_REQUESTS);
  }
}

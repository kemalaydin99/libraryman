package com.identitye2e.libraryman.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS, reason = "Too many incoming requests.") // 429
public class TooManyRequestException extends Exception {

  private static final long serialVersionUID = 1L;

  public TooManyRequestException() {
    super("Too many incoming requests, please try again later.");
  }
}

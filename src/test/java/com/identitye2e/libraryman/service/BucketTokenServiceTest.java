package com.identitye2e.libraryman.service;

import com.identitye2e.libraryman.exception.TooManyRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BucketTokenServiceTest {

  @Autowired BucketTokenService bucketTokenService;

  @Test
  void checkTooManyRequestException() throws TooManyRequestException {
    assertThrows(
        TooManyRequestException.class,
        () -> {
          while (bucketTokenService.checkBucketTokens(true))
            ;
        },
        "Too many incoming requests, please try again later.");
  }

  @Test
  void checkBucketTokens() throws TooManyRequestException {
    assertDoesNotThrow(
        () -> {
          while (bucketTokenService.checkBucketTokens(false))
            ;
        },
        "Too many incoming requests, please try again later.");
  }
}

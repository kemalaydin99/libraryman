package com.identitye2e.libraryman.service;

import com.identitye2e.libraryman.exception.TooManyRequestException;
import com.identitye2e.libraryman.util.TokenBucket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BucketTokenService {

  public boolean checkBucketTokens(boolean throwException) throws TooManyRequestException {
    if (TokenBucket.getNextToken()) return true;
    else if (throwException) throw new TooManyRequestException();
    return false;
  }
}

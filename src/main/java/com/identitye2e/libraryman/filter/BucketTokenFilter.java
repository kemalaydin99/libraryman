package com.identitye2e.libraryman.filter;

import com.identitye2e.libraryman.service.BucketTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@AllArgsConstructor
public class BucketTokenFilter extends OncePerRequestFilter {

  private final BucketTokenService bucketTokenService;

  @SneakyThrows
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
    if (!bucketTokenService.checkBucketTokens(false)) {
      response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(), "Request limit has been exceeded.");
    }
    response.setStatus(HttpStatus.ACCEPTED.value());

    filterChain.doFilter(request, response);
  }
}

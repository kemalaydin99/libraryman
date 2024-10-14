package com.identitye2e.libraryman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class LibrarymanApplication {

  public static void main(String[] args) {
    SpringApplication.run(LibrarymanApplication.class, args);
  }
}

package com.identitye2e.libraryman;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest
@WebAppConfiguration
class LibrarymanApplicationTests {

  @Test
  void contextLoads() {}

  @Test
  void applicationTest() {
    LibrarymanApplication.main(new String[] {});
  }
}

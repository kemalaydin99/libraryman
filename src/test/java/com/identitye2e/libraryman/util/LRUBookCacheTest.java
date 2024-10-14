package com.identitye2e.libraryman.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class LRUBookCacheTest {

  private static LRUBookCache lruBookCache;

  @BeforeAll
  static void initTests() {
    lruBookCache = new LRUBookCache();
    lruBookCache.setCapacity(0);
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        "True:isbn 1:Book 1:Author 1:2001:1",
        "True:isbn 2:Book 2:Author 2:2002:2",
        "False:isbn 3:Book 3:Author 3:2003:3"
      },
      delimiter = ':')
  void test_GetShouldReturnExistingBooks(
      boolean exists, String isbn, String title, String author, int year, int copies) {
    if (exists) lruBookCache.put(LibraryUtils.buildBook(isbn, title, author, year, copies));
    assertEquals(exists, lruBookCache.get(isbn) != null);
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        "True:isbn 1:Book 1:Author 1:2001:1",
        "True:isbn 2:Book 2:Author 2:2002:2",
        "False:isbn 3:Book 3:Author 3:2003:3"
      },
      delimiter = ':')
  void test_ContainsKeyShouldReturnExists(
      boolean exists, String isbn, String title, String author, int year, int copies) {
    if (exists) lruBookCache.put(LibraryUtils.buildBook(isbn, title, author, year, copies));
    assertEquals(exists, lruBookCache.containsKey(isbn));
  }

  @Test
  void test_RemoveShouldReturnTrueForExistingBooks() {
    lruBookCache.put(LibraryUtils.buildBook("ISBN YES", "Book X", "Author X", 2024, 1));
    assertTrue(lruBookCache.remove("ISBN YES"));
  }

  @Test
  void test_RemoveShouldReturnFalseForNonExistingBooks() {
    assertFalse(lruBookCache.remove("ISBN NON"));
  }

  @Test
  void test_FindBooksByAuthorShouldReturnBooksForExistingAuthor() {
    lruBookCache.put(LibraryUtils.buildBook("ISBN YES", "Book X", "Author X", 2024, 1));
    assertFalse(lruBookCache.findBooksByAuthor("Author X").isEmpty());
  }

  @Test
  void test_FindBooksByAuthorShouldReturnNullForNonExistingAuthor() {
    assertTrue(lruBookCache.findBooksByAuthor("Author NON").isEmpty());
  }

  @Test
  void test_CapacityTest() {
    int initialCapacity = lruBookCache.getCapacity();
    for (int i = 1; i <= initialCapacity * 2; i++) {
      lruBookCache.put(LibraryUtils.buildBook("ISBN " + i, "Book " + i, "Author " + i, 2024, 1));
    }
    assertEquals(initialCapacity, lruBookCache.getCapacity());
  }

  @Test
  void test_MoveFrontTest() {
    lruBookCache.clear();
    lruBookCache.put(LibraryUtils.buildBook("ISBN 1", "Book 1", "Author 1", 2024, 1));
    lruBookCache.put(LibraryUtils.buildBook("ISBN 2", "Book 2", "Author 2", 2024, 1));
    lruBookCache.put(LibraryUtils.buildBook("ISBN 3", "Book 3", "Author 3", 2024, 1));
    lruBookCache.put(LibraryUtils.buildBook("ISBN 3", "Book 3", "Author 3", 2024, 1));
    assertEquals("ISBN 3", lruBookCache.peek().getIsbn());
  }
}

package com.identitye2e.libraryman.service;

import com.identitye2e.libraryman.exception.AuthorNotFoundException;
import com.identitye2e.libraryman.exception.BookAlreadyExistException;
import com.identitye2e.libraryman.exception.BookNotAvailableException;
import com.identitye2e.libraryman.exception.BookNotFoundException;
import com.identitye2e.libraryman.model.Book;
import com.identitye2e.libraryman.util.LibraryUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LibraryServiceTest {

  private final List isbnList =
      List.of("9780132350884", "0201633612", "0134757599", "9780201616224", "0262533057");
  @Autowired LibraryService libraryService;

  private static Stream<Arguments> provideISBNInfo() {
    return Stream.of(
        Arguments.of("9780132350884"),
        Arguments.of("0201633612"),
        Arguments.of("0134757599"),
        Arguments.of("0262533057"));
  }

  @Test
  void test_GetBookListReturnsAllBooks() {
    assertTrue(libraryService.getBookList().get().size() >= isbnList.size());
  }

  @Test
  void test_AddEmptyBookReturnFalse() throws BookAlreadyExistException {
    assertFalse(libraryService.addBook(null));
  }

  @Test
  void test_AddBookReturnTrue() throws BookAlreadyExistException {
    assertTrue(
        libraryService.addBook(LibraryUtils.buildBook("ISBN 1", "Book 1", "Author 1", 2024, 1)));
  }

  @Test
  void test_AddSameBookThrowsException() throws BookAlreadyExistException {
    assertTrue(
        libraryService.addBook(LibraryUtils.buildBook("ISBN A1", "Book 1", "Author 1", 2024, 1)));
    assertThrows(
        BookAlreadyExistException.class,
        () ->
            libraryService.addBook(
                LibraryUtils.buildBook("ISBN A1", "Book 1", "Author 1", 2024, 1)),
        "A book already exists with same isbn: ISBN A1");
  }

  @Test
  void test_RemoveBookReturnTrue() throws BookNotFoundException, BookAlreadyExistException {
    String isbnToBeRemoved = "ISBN TO BE REMOVED";
    assertTrue(
        libraryService.addBook(
            LibraryUtils.buildBook(isbnToBeRemoved, "Book R", "Author R", 2024, 1)));

    assertTrue(libraryService.removeBook(isbnToBeRemoved));

    // Idempotent DELETE should return exception on 2nd call.
    assertThrows(
        BookNotFoundException.class,
        () -> libraryService.removeBook(isbnToBeRemoved),
        "No book exists with isbn: " + isbnToBeRemoved);
  }

  @Test
  void test_RemoveEmptyBookReturnFalse() throws BookNotFoundException {
    assertFalse(libraryService.removeBook(null));
  }

  @ParameterizedTest
  @CsvSource({"9780132350884, 0201633612, 0134757599, 9780201616224, 0262533057"})
  void test_FindBookByISBNReturnBook(String isbn) throws BookNotFoundException {
    assertEquals(isbn, libraryService.findBookByISBN(isbn).get().getIsbn());
  }

  @Test
  void test_FindBookByISBNReturnBook() throws BookNotFoundException {
    assertThrows(
        BookNotFoundException.class,
        () -> libraryService.findBookByISBN("NON EXISTING ISBN"),
        "No book exists with isbn: NON EXISTING ISBN");
  }

  @Test
  void test_FindBooksByAuthorReturnsBookList() throws AuthorNotFoundException {
    List<Book> books = libraryService.findBooksByAuthor("Robert C. Martin").get();
    assertFalse(books == null || books.isEmpty());
    assertEquals("Robert C. Martin", books.get(0).getAuthor());
  }

  @Test
  void test_FindBooksByAuthorThrowsException() {
    assertThrows(
        AuthorNotFoundException.class,
        () -> libraryService.findBooksByAuthor("NON EXISTING AUTHOR"),
        "No book found written by author: NON EXISTING AUTHOR");
  }

  @ParameterizedTest
  @MethodSource("provideISBNInfo")
  void test_BorrowBookShouldDecrementCopies(String isbn) throws Exception {
    Book borrowBook = libraryService.findBookByISBN(isbn).get();
    int availableCopies = borrowBook.getAvailableCopies();
    borrowBook = libraryService.borrowBook(isbn).get();
    assertEquals(availableCopies - 1, borrowBook.getAvailableCopies());
  }

  @Test
  void test_BorrowBookShouldThrowNotAvailableException() throws BookNotFoundException {
    assertThrows(
        BookNotAvailableException.class,
        () -> {
          while (true) libraryService.borrowBook("9780132350884");
        },
        "All copies were already borrowed for the book with isbn: 9780132350884");

    assertTrue(libraryService.returnBook("9780132350884").isPresent());
  }

  @Test
  void test_BorrowBookShouldThrowBookNotFoundException() {
    assertThrows(
        BookNotFoundException.class,
        () -> libraryService.borrowBook("NON EXISTING ISBN"),
        "No book found with isbn: NON EXISTING ISBN");
  }

  @ParameterizedTest
  @MethodSource("provideISBNInfo")
  void test_ReturnBookDecrementCopies(String isbn) throws Exception {
    Book returnedBook = libraryService.findBookByISBN(isbn).get();
    int availableCopies = returnedBook.getAvailableCopies();
    returnedBook = libraryService.returnBook(isbn).get();
    assertEquals(availableCopies + 1, returnedBook.getAvailableCopies());
  }
}

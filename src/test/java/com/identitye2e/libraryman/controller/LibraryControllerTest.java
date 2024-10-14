package com.identitye2e.libraryman.controller;

import com.identitye2e.libraryman.exception.BookNotAvailableException;
import com.identitye2e.libraryman.exception.TooManyRequestException;
import com.identitye2e.libraryman.model.Book;
import com.identitye2e.libraryman.service.BucketTokenService;
import com.identitye2e.libraryman.service.LibraryService;
import com.identitye2e.libraryman.util.LibraryUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithAnonymousUser
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LibraryControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private LibraryService libraryService;
  @Autowired private BucketTokenService bucketTokenService;

  @Test
  @DisplayName("01- Test mvc post Book")
  @Order(1)
  @WithMockUser(username = "user", roles = "USER")
  void test01_AddBookShouldReturnOK() throws Exception {
    String mockBookJson = LibraryUtils.buildBookJSon("NEW ISBN", "NEW BOOK", "NEW AUTHOR", 2024, 1);

    Mockito.when(libraryService.addBook(Mockito.any(Book.class))).thenReturn(true);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/libraryman/api/v1/books/add")
            .accept(MediaType.APPLICATION_JSON)
            .content(mockBookJson)
            .contentType(MediaType.APPLICATION_JSON);

    MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    assertEquals(mockBookJson, response.getContentAsString());
  }

  @Test
  @DisplayName("02- Test remove a Book should return OK & removed book JSON")
  @Order(2)
  void test02_removedBookShouldNotBeFound() throws Exception {
    Mockito.when(libraryService.removeBook(Mockito.any())).thenReturn(true);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.delete("/libraryman/api/v1/books/delete/0262533057")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();
    assertEquals(HttpStatus.ACCEPTED.value(), response.getStatus());
    assertEquals(
        "Book with ISBN 0262533057 is successfully deleted.", response.getContentAsString());

    mockMvc
        .perform(MockMvcRequestBuilders.get("/libraryman/api/v1/books/findBookByISBN/0262533057"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  @DisplayName("03- Test find a Book by ISBN should return OK & book JSON")
  @Order(3)
  void test03_findBookByISBNShouldReturnBookJSON() throws Exception {
    Book mockBook =
        LibraryUtils.buildBook("9780132350884", "Clean Code", "Robert C. Martin", 2008, 10);

    String mockBookJson = LibraryUtils.getBookJSon(mockBook);

    Mockito.when(libraryService.findBookByISBN(Mockito.any()))
        .thenReturn(Optional.ofNullable(mockBook));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/libraryman/api/v1/books/findBookByISBN/9780132350884"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(mockBookJson)));
  }

  @ParameterizedTest
  @CsvSource(
      value = {
        "isbn 1:Book 1:Author 1:2001:1",
        "isbn 2:Book 2:Author 2:2002:2",
        "isbn 3:Book 3:Author 3:2003:3"
      },
      delimiter = ':')
  @DisplayName("04- Test get all books list should return all books")
  @Order(4)
  void test04_getBookList(String isbn, String title, String author, int year, int copies)
      throws Exception {
    Book mockBook = LibraryUtils.buildBook(isbn, title, author, year, copies);

    String mockBookJson = LibraryUtils.getBookJSon(mockBook);

    Mockito.when(libraryService.getBookList()).thenReturn(Optional.ofNullable(List.of(mockBook)));

    mockMvc
        .perform(MockMvcRequestBuilders.get("/libraryman/api/v1/books/getAllBooks"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(mockBookJson)));
  }

  @Test
  @DisplayName("05- Test findBooksByAuthor should return books belongs to the Author.")
  @Order(5)
  void test05_findBooksByAuthor() throws Exception {
    Book mockBook =
        LibraryUtils.buildBook("9780132350884", "Clean Code", "Robert C. Martin", 2008, 10);

    String mockBookJson = LibraryUtils.getBookJSon(mockBook);

    Mockito.when(libraryService.findBooksByAuthor(Mockito.any()))
        .thenReturn(Optional.ofNullable(List.of(mockBook)));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/libraryman/api/v1/books/getAuthorBooks/Robert C. Martin"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(mockBookJson)));
  }

  @Test
  @DisplayName("06- Test borrowBook should decrement available copies.")
  @Order(6)
  void test06_borrowBookShouldDecrementCopies() throws Exception {
    int availableCopies = 1;
    Book mockBook =
        LibraryUtils.buildBook("NEW ISBN", "NEW BOOK", "NEW AUTHOR", 2024, availableCopies);
    String mockBookJson = LibraryUtils.getBookJSon(mockBook);

    Mockito.when(libraryService.addBook(Mockito.any(Book.class))).thenReturn(true);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/libraryman/api/v1/books/add")
            .accept(MediaType.APPLICATION_JSON)
            .content(mockBookJson)
            .contentType(MediaType.APPLICATION_JSON);

    MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    assertEquals(mockBookJson, response.getContentAsString());

    mockBook.setAvailableCopies(availableCopies - 1);
    Mockito.when(libraryService.borrowBook(Mockito.any())).thenReturn(Optional.of(mockBook));
    mockBookJson = LibraryUtils.getBookJSon(mockBook);

    requestBuilder =
        MockMvcRequestBuilders.put("/libraryman/api/v1/books/borrow/NEW ISBN")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    response = mockMvc.perform(requestBuilder).andReturn().getResponse();
    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(mockBookJson, response.getContentAsString());
  }

  @Test
  @DisplayName("07- Test borrow a book with 0 copies should reject.")
  @Order(7)
  void test07_borrowBookWithNoAvailableCopiesShouldReject() throws Exception {
    int availableCopies = 0;
    Book mockBook =
        LibraryUtils.buildBook("NEW ISBN", "NEW BOOK", "NEW AUTHOR", 2024, availableCopies);
    String mockBookJson = LibraryUtils.getBookJSon(mockBook);

    Mockito.when(libraryService.addBook(Mockito.any(Book.class))).thenReturn(true);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/libraryman/api/v1/books/add")
            .accept(MediaType.APPLICATION_JSON)
            .content(mockBookJson)
            .contentType(MediaType.APPLICATION_JSON);

    MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    assertEquals(mockBookJson, response.getContentAsString());

    Mockito.when(libraryService.borrowBook(Mockito.any()))
        .thenThrow(new BookNotAvailableException("NEW ISBN"));

    requestBuilder =
        MockMvcRequestBuilders.put("/libraryman/api/v1/books/borrow/NEW ISBN")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    response = mockMvc.perform(requestBuilder).andReturn().getResponse();
    assertEquals(HttpStatus.CONFLICT.value(), response.getStatus());
    assertEquals(
        "All copies were already borrowed for the book with isbn: NEW ISBN",
        response.getContentAsString());
  }

  @Test
  @DisplayName("08- Test returnBook should return OK & increment available copies.")
  @Order(8)
  void test08_returnBookShouldIncrementCopies() throws Exception {
    int availableCopies = 0;
    Book mockBook =
        LibraryUtils.buildBook("NEW ISBN", "NEW BOOK", "NEW AUTHOR", 2024, availableCopies);
    String mockBookJson = LibraryUtils.getBookJSon(mockBook);

    Mockito.when(libraryService.addBook(Mockito.any(Book.class))).thenReturn(true);

    RequestBuilder requestBuilder =
        MockMvcRequestBuilders.post("/libraryman/api/v1/books/add")
            .accept(MediaType.APPLICATION_JSON)
            .content(mockBookJson)
            .contentType(MediaType.APPLICATION_JSON);

    MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();
    assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    assertEquals(mockBookJson, response.getContentAsString());

    mockBook.setAvailableCopies(availableCopies + 1);
    Mockito.when(libraryService.returnBook(Mockito.any())).thenReturn(Optional.of(mockBook));
    mockBookJson = LibraryUtils.getBookJSon(mockBook);

    requestBuilder =
        MockMvcRequestBuilders.put("/libraryman/api/v1/books/return/NEW ISBN")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

    response = mockMvc.perform(requestBuilder).andReturn().getResponse();
    assertEquals(HttpStatus.OK.value(), response.getStatus());
    assertEquals(mockBookJson, response.getContentAsString());
  }

  @Test
  @DisplayName("09- Test find a unknown Book by ISBN should return NOT_FOUND")
  @Order(9)
  void test09_findBookByISBNShouldFailForUnknownISBN() throws Exception {
    Mockito.when(libraryService.findBookByISBN(Mockito.any()))
        .thenReturn(Optional.ofNullable(null));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                "/libraryman/api/v1/books/findBookByISBN/XXX_UNKNOWN_ISBN_XXX"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(
            MockMvcResultMatchers.content()
                .string(Matchers.containsString("No book found with isbn: XXX_UNKNOWN_ISBN_XXX")));
  }

  @Test
  @DisplayName("10- Test findBooksByAuthor should return Author not found for Unknown Author.")
  @Order(10)
  void test10_findBooksByAuthorShouldFailForUnknownAuthor() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(
                "/libraryman/api/v1/books/getAuthorBooks/YYY_UNKNOWN_AUTHOR_YYY"))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(
            MockMvcResultMatchers.content()
                .string(
                    Matchers.containsString(
                        "No book found written by author: YYY_UNKNOWN_AUTHOR_YYY")));
  }

  @Test
  @DisplayName("11- Test add existing book should be rejected")
  @Order(11)
  void test11_AddExistingBookShouldBeRejected() throws Exception {
    String mockBookJson = LibraryUtils.buildBookJSon("NEW ISBN", "NEW BOOK", "NEW AUTHOR", 2024, 1);

    Mockito.when(libraryService.addBook(Mockito.any(Book.class))).thenReturn(true, false);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/libraryman/api/v1/books/add")
                .accept(MediaType.APPLICATION_JSON)
                .content(mockBookJson)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString(mockBookJson)));

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/libraryman/api/v1/books/add")
                .accept(MediaType.APPLICATION_JSON)
                .content(mockBookJson)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isConflict())
        .andExpect(
            MockMvcResultMatchers.content()
                .string(Matchers.containsString("A book already exists with same isbn: NEW ISBN")));
  }

  @Test
  @DisplayName("12- Test remove non existing book should be rejected")
  @Order(12)
  void test12_RemoveNonExistingBookShouldBeRejected() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.delete("/libraryman/api/v1/books/delete/UNKNOWN_ISBN")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isConflict())
        .andExpect(
            MockMvcResultMatchers.content()
                .string(
                    Matchers.containsString("Book with ISBN UNKNOWN_ISBN could not be deleted.")));
  }

  @Test
  @DisplayName("13- Test Too Many Requests")
  @Order(13)
  void test13_TooManyRequestsShouldThrowTooManyRequestException() throws Exception {
    Book mockBook = LibraryUtils.buildBook("TEST ISBN", "TEST Book", "TEST Author", 2024, 10);

    Mockito.when(libraryService.findBookByISBN(Mockito.any()))
        .thenReturn(Optional.ofNullable(mockBook));

    assertThrows(
        TooManyRequestException.class,
        () ->
            mockMvc
                .perform(
                    MockMvcRequestBuilders.get("/libraryman/api/v1/books/findBookByISBN/TEST ISBN"))
                .andDo(
                    (t) -> {
                      int b = 1000;
                      while (b-- > 0) bucketTokenService.checkBucketTokens(true);
                    })
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests()),
        "Expected TooManyRequestException is not thrown.");
  }
}

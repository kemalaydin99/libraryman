package com.identitye2e.libraryman.controller;

import com.identitye2e.libraryman.exception.AuthorNotFoundException;
import com.identitye2e.libraryman.exception.BookAlreadyExistException;
import com.identitye2e.libraryman.exception.BookNotAvailableException;
import com.identitye2e.libraryman.exception.BookNotFoundException;
import com.identitye2e.libraryman.model.Book;
import com.identitye2e.libraryman.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @noinspection ALL
 */
@RestController
@RequestMapping("libraryman/api/v1/books/")
public class LibraryController {

  @Autowired LibraryService libraryService;

  @PostMapping("/add")
  public ResponseEntity<Book> addBook(@RequestBody Book book)
      throws BookAlreadyExistException, BookNotFoundException {
    if (libraryService.addBook(book)) {
      return new ResponseEntity<>(book, HttpStatus.CREATED);
    } else {
      throw new BookAlreadyExistException(book.getIsbn());
    }
  }

  @DeleteMapping("/delete/{isbn}")
  public ResponseEntity<String> removeBook(@PathVariable("isbn") String isbn)
      throws BookNotFoundException {
    if (libraryService.removeBook(isbn))
      return new ResponseEntity<>(
          "Book with ISBN " + isbn + " is successfully deleted.", HttpStatus.ACCEPTED);
    else
      return new ResponseEntity<>(
          "Book with ISBN " + isbn + " could not be deleted.", HttpStatus.CONFLICT);
  }

  @GetMapping("/findBookByISBN/{isbn}")
  public ResponseEntity<Book> findBookByISBN(@PathVariable("isbn") String isbn)
      throws BookNotFoundException {
    return new ResponseEntity<>(
        libraryService.findBookByISBN(isbn).orElseThrow(() -> new BookNotFoundException(isbn)),
        HttpStatus.OK);
  }

  @GetMapping("/getAllBooks")
  public ResponseEntity<List<Book>> getBookList() throws BookNotFoundException {
    return new ResponseEntity<>(
        libraryService
            .getBookList()
            .orElseThrow(() -> new BookNotFoundException("There is no books in the database.")),
        HttpStatus.OK);
  }

  @GetMapping("/getAuthorBooks/{author}")
  public ResponseEntity<List<Book>> findBooksByAuthor(@PathVariable("author") String author)
      throws AuthorNotFoundException {
    return new ResponseEntity<>(
        libraryService
            .findBooksByAuthor(author)
            .orElseThrow(() -> new AuthorNotFoundException(author)),
        HttpStatus.OK);
  }

  @PutMapping("/borrow/{isbn}")
  public ResponseEntity<Book> borrowBook(@PathVariable("isbn") String isbn)
      throws BookNotFoundException, BookNotAvailableException {
    return new ResponseEntity<>(
        libraryService.borrowBook(isbn).orElseThrow(() -> new BookNotFoundException(isbn)),
        HttpStatus.OK);
  }

  @PutMapping("/return/{isbn}")
  public ResponseEntity<Book> returnBook(@PathVariable("isbn") String isbn)
      throws BookNotFoundException {
    return new ResponseEntity<>(
        libraryService.returnBook(isbn).orElseThrow(() -> new BookNotFoundException(isbn)),
        HttpStatus.OK);
  }
}

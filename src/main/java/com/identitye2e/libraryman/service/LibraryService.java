package com.identitye2e.libraryman.service;

import com.identitye2e.libraryman.exception.AuthorNotFoundException;
import com.identitye2e.libraryman.exception.BookAlreadyExistException;
import com.identitye2e.libraryman.exception.BookNotAvailableException;
import com.identitye2e.libraryman.exception.BookNotFoundException;
import com.identitye2e.libraryman.model.Book;
import com.identitye2e.libraryman.repository.BooksRepository;
import com.identitye2e.libraryman.util.LRUBookCache;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.identitye2e.libraryman.util.LibraryUtils.isNotBlank;

@Service
@AllArgsConstructor
public class LibraryService {

  private static final LRUBookCache libraryCache = new LRUBookCache();
  private final BooksRepository booksRepository;

  private Book getBook(String isbn) {
    Book book = libraryCache.get(isbn);
    if (book == null) {
      book = booksRepository.findByIsbn(isbn).orElse(null);
      if (book != null) {
        libraryCache.put(book);
      }
    }
    return book;
  }

  private boolean saveBook(Book book) {
    libraryCache.put(book);
    return booksRepository.save(book) != null;
  }

  public Optional<List<Book>> getBookList() {
    return Optional.ofNullable(booksRepository.findAll());
  }

  @Transactional
  public boolean addBook(Book book) throws BookAlreadyExistException {
    if (book != null && isNotBlank(book.getIsbn())) {
      if (getBook(book.getIsbn()) != null) {
        throw new BookAlreadyExistException(book.getIsbn());
      }
      return saveBook(book);
    }
    return false;
  }

  @Transactional
  public boolean removeBook(String isbn) throws BookNotFoundException {
    if (isNotBlank(isbn)) {
      if (getBook(isbn) != null) {
        libraryCache.remove(isbn);
        booksRepository.deleteById(isbn);
        return true;
      } else {
        throw new BookNotFoundException(isbn);
      }
    }
    return false;
  }

  public Optional<Book> findBookByISBN(String isbn) throws BookNotFoundException {
    if (isNotBlank(isbn)) {
      Book book = getBook(isbn);
      if (book != null) return Optional.ofNullable(book);
    }

    throw new BookNotFoundException(isbn);
  }

  public Optional<List<Book>> findBooksByAuthor(String author) throws AuthorNotFoundException {
    if (isNotBlank(author)) {
      List<Book> books = libraryCache.findBooksByAuthor(author);
      if (books == null || books.isEmpty()) {
        books = booksRepository.findByAuthor(author).orElse(null);

        if (books != null && !books.isEmpty()) {
          books.forEach(libraryCache::put);
        }
      }

      if (books != null && !books.isEmpty()) return Optional.ofNullable(books);
    }

    throw new AuthorNotFoundException(author);
  }

  public Optional<Book> borrowBook(String isbn)
      throws BookNotFoundException, BookNotAvailableException {
    Book book = findBookByISBN(isbn).orElse(null);
    if (book != null && book.getAvailableCopies() > 0) {
      synchronized (book) {
        if (book.getAvailableCopies() > 0) { // Double check synchronised object
          book.setAvailableCopies(book.getAvailableCopies() - 1);
          saveBook(book);
        }
      }
      return Optional.ofNullable(book);
    }

    throw new BookNotAvailableException(isbn);
  }

  public Optional<Book> returnBook(String isbn) throws BookNotFoundException {
    Book book = findBookByISBN(isbn).orElse(null);
    if (book != null) {
      synchronized (book) {
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        saveBook(book);
      }
    }
    return Optional.ofNullable(book);
  }
}

package com.identitye2e.libraryman.repository;

import com.identitye2e.libraryman.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksRepository extends JpaRepository<Book, String> {

  List<Book> findAll();

  Optional<Book> findByIsbn(String isbn);

  Optional<List<Book>> findByAuthor(String author);

  Book save(Book book);
}

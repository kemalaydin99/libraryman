package com.identitye2e.libraryman.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "BOOKS",
    uniqueConstraints = {@UniqueConstraint(columnNames = "ISBN")},
    indexes = {@Index(name = "i_books_author", columnList = "author", unique = false)})
public class Book {
  @Id
  @Column(name = "ISBN", nullable = false)
  private String isbn;

  @Column(name = "TITLE", nullable = false)
  private String title;

  @Column(name = "AUTHOR", nullable = false)
  private String author;

  @Column(name = "PUBLICATION_YEAR", nullable = false)
  private int publicationYear;

  @Column(name = "AVAILABLE_COPIES", nullable = false)
  private int availableCopies;
}

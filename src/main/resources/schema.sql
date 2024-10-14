drop table if exists BOOKS;

create table BOOKS (
  ISBN        varchar(30) not null primary key,
  TITLE       varchar(100) not null,
  AUTHOR      varchar(100) not null,
  PUBLICATION_YEAR int,
  AVAILABLE_COPIES int
);

 INSERT INTO BOOKS (ISBN, TITLE, AUTHOR, PUBLICATION_YEAR, AVAILABLE_COPIES) VALUES ('9780132350884', 'Clean Code', 'Robert C. Martin', '2008', 10);
 INSERT INTO BOOKS (ISBN, TITLE, AUTHOR, PUBLICATION_YEAR, AVAILABLE_COPIES) VALUES ('0201633612',    'Design patterns : Elements of reusable object-oriented software', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides', 1995, 5);
 INSERT INTO BOOKS (ISBN, TITLE, AUTHOR, PUBLICATION_YEAR, AVAILABLE_COPIES) VALUES ('0134757599',    'Refactoring: Improving the Design of Existing Code', 'Martin Fowler', 2019, 1);
 INSERT INTO BOOKS (ISBN, TITLE, AUTHOR, PUBLICATION_YEAR, AVAILABLE_COPIES) VALUES ('9780201616224', 'The Pragmatic Programmer', 'Andrew Hunt, David Thomas', 1999, 0);
 INSERT INTO BOOKS (ISBN, TITLE, AUTHOR, PUBLICATION_YEAR, AVAILABLE_COPIES) VALUES ('0262533057',    'Introduction to Algorithms', 'T Cormen, C Leiserson, R Rivest, C Stein', 2009, 1);

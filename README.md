### _Kemal Aydin (kemalaydin99@hotmail.com)_

-----------------------------------------------------------
##  Concurrent Library Management System with RESTful API
A Restful API implementation build using Java (22), Spring 6.x & Spring Boot 3.x, H2 local DB, Lombok, Jackson.

### Base Components 
* LRUBookCache: A Concurrent LRU implementation with limited size. It consists of a ConcurrentHashMap which holds Book data and 
ConcurrentLinkedDeque which manages an isbn list according to their usage orders.
Concurrent data structures ensures Thread-Safety and don't create performance degrades with non-locking design. 
Inorder to provide atomicity critical parts are synchronised as well as @Transactional annotations used in Service components.
 
* TokenBucket: A Rate Limiter component that implements Token Bucket algorithm.
  - 100 requests are allowed API wide per 10 second windows.
  - Number of available tokens are reset if the last check has expired.
  - In order to support thread safety, class methods are defined as static and
  availableTokens defined as Thread safe AtomicInteger.

### RESTFul API endpoints:
* addBook(Book book): Adds a new book to the library
* removeBook(String isbn): Removes a book from the library by ISBN
* findBookByISBN(String isbn): Returns a book by its ISBN
* findBooksByAuthor(String author): Returns a list of books by a given author
* borrowBook(String isbn): Decreases the available copies of a book by 1
* returnBook(String isbn): Increases the available copies of a book by 1

### Unit tests and Code Coverage:
* 51 Unit tests added to maximise code coverage:   
* Class Coverage: 100%
* Method Coverage: 98%
* Line Coverage: 96%

## HOW TO BUILD AND RUN TESTS
-----------------------------------------------------------
* Run tests com.identitye2e.libraryman in libraryman from IDE.
  or run the following command if the maven is installed and added to PATH environment variable.
> mvn clean install

-----------------------------------------------------------

## How to run Application
Run LibrarymanApplication.main from an IDE: T
The app starts on port 8088 by default.

## API Endpoints 
###_(After starting LibrarymanApplication.main, browse the following endpoints.)_
 
* ### Get All Books:
`[http://localhost:8088/libraryman/api/v1/books/getAllBooks]`
###### *Response:*
[
{
"isbn": "9780132350884",
"title": "Clean Code",
"author": "Robert C. Martin",
"publicationYear": 2008,
"availableCopies": 10
}, ... ]

* ### Get Book By ISBN: /libraryman/api/v1/books/findBookByISBN/<ISBN>
`(http://localhost:8088/libraryman/api/v1/books/findBookByISBN/9780201616224)`
###### *Response:*
{
"isbn": "9780201616224",
"title": "The Pragmatic Programmer",
"author": "Andrew Hunt, David Thomas",
"publicationYear": 1999,
"availableCopies": 0
}

* ### Get Author Books: /libraryman/api/v1/books/getAuthorBooks/<Author Name>
`(http://localhost:8088/libraryman/api/v1/books/getAuthorBooks/Robert%20C.%20Martin)`
###### *Response:*
[
{
"isbn": "9780132350884",
"title": "Clean Code",
"author": "Robert C. Martin",
"publicationYear": 2008,
"availableCopies": 10
}
]

-----------------------------------------------------------
###DATA MANUPLATION ENDPOINTS (POST/DELETE/PUT) 
Perform using PostMan or similar tools (TODO: Swagger will be configured)
 
* ### Add Book : `POST => /libraryman/api/v1/books/add`
###### *Body Data:*
{
"isbn": "1234567890",
"title": "The New Book",
"author": "Kemal Aydin",
"publicationYear": 2024,
"availableCopies": 1
}

* ### Delete Book : `DELETE => /libraryman/api/v1/books/delete/{isbn}` //Example isbn: 9780132350884
###### *Body Data: null*
###### *Response : (Deleted Book JSON is returned.)*
[
{
"isbn": "9780132350884",
"title": "Clean Code",
"author": "Robert C. Martin",
"publicationYear": 2008,
"availableCopies": 10
}
]


* ### Borrow Book: `PUT => /libraryman/api/v1/books/borrow/{isbn}` //Example isbn: 0201633612
###### *Body Data: null*
###### *Response : (Borrowed Book JSON is returned.)*
{
"isbn": "0201633612",
"title": "Design patterns : Elements of reusable object-oriented software",
"author": "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides",
"publicationYear": 1995,
"availableCopies": 4
}


* ### Return Book: `PUT => /libraryman/api/v1/books/return/{isbn}` //Example isbn: 0201633612
###### *Body Data: null*
###### *Response : (Borrowed Book JSON is returned.)*
{
"isbn": "0201633612",
"title": "Design patterns : Elements of reusable object-oriented software",
"author": "Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides",
"publicationYear": 1995,
"availableCopies": 5
}

### _Kemal Aydin (kemalaydin99@hotmail.com)_

-----------------------------------------------------------
##  Concurrent Library Management System with RESTful API
A Restful API implementation build using Spring 6.x & Spring Boot 3.x, H2 local DB.

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

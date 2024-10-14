package com.identitye2e.libraryman.util;

import com.identitye2e.libraryman.model.Book;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

public class LRUBookCache {
  private final int MIN_ITEMS = 10;
  private final int MAX_ITEMS = 100;

  private final Map<String, Book> lruBooksMap = new ConcurrentHashMap<>();
  private final ConcurrentLinkedDeque<String> lruIsbnQueue = new ConcurrentLinkedDeque<>();
  private int capacity;

  public LRUBookCache() {
    this.capacity = MAX_ITEMS;
  }

  public int getCapacity() {
    return this.capacity;
  }

  public int setCapacity(int capacity) {
    this.capacity = Math.min(MAX_ITEMS, Math.max(MIN_ITEMS, capacity));
    return getCapacity();
  }

  public Book get(String key) {
    if (!containsKey(key)) {
      return null;
    }

    moveToFront(key);

    return lruBooksMap.get(key);
  }

  public void put(Book book) {
    if (book != null) {
      if (containsKey(book.getIsbn())) {
        moveToFront(book.getIsbn());
      } else {
        lruBooksMap.put(book.getIsbn(), book);
        lruIsbnQueue.addFirst(book.getIsbn());
      }

      checkSize();
    }
  }

  private void moveToFront(String isbn) {
    if (lruIsbnQueue.size() > 1) {
      lruIsbnQueue.remove(isbn);
      lruIsbnQueue.addFirst(isbn);
    } else if (lruIsbnQueue.size() == 0) {
      lruIsbnQueue.add(isbn);
    }
  }

  private void checkSize() {
    if (lruBooksMap.size() > capacity) {
      String leastUsedIsbn = lruIsbnQueue.removeLast();
      lruBooksMap.remove(leastUsedIsbn);
    }
  }

  public boolean containsKey(String isbn) {
    return lruBooksMap.containsKey(isbn);
  }

  public boolean remove(String isbn) {
    lruBooksMap.remove(isbn);
    return lruIsbnQueue.remove(isbn);
  }

  public List<Book> findBooksByAuthor(String author) {
    return lruBooksMap.entrySet().stream()
        .filter(b -> b.getValue().getAuthor().equalsIgnoreCase(author))
        .map(e -> e.getValue())
        .collect(Collectors.toList());
  }

  public void clear() {
    lruBooksMap.clear();
    lruIsbnQueue.clear();
  }

  public Book peek() {
    String key = lruIsbnQueue.peek();
    return lruBooksMap.get(key);
  }
}

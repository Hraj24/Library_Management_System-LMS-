package org.example.lms.service;



import org.example.lms.entity.Book;
import org.example.lms.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public Book addBook(Book book) {

        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists.");
        }

        if (book.getTotalCopies() == null || book.getTotalCopies() <= 0) {
            book.setTotalCopies(1);
        }
        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            book.setAvailableCopies(book.getTotalCopies());
        }
        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

    public List<Book> searchBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> searchBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    @Transactional
    public Book updateBook(Long id, Book updatedBook) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            // ISBN should generally not be changed for an existing book, but if allowed:
            // if (!book.getIsbn().equals(updatedBook.getIsbn())) {
            //     if (bookRepository.findByIsbn(updatedBook.getIsbn()).isPresent()) {
            //         throw new IllegalArgumentException("New ISBN " + updatedBook.getIsbn() + " already exists for another book.");
            //     }
            //     book.setIsbn(updatedBook.getIsbn());
            // }
            book.setPublicationYear(updatedBook.getPublicationYear());
            book.setTotalCopies(updatedBook.getTotalCopies());

            if (updatedBook.getAvailableCopies() != null) {
                book.setAvailableCopies(Math.min(updatedBook.getAvailableCopies(), book.getTotalCopies()));
                book.setAvailableCopies(Math.max(0, book.getAvailableCopies()));
            } else {
                book.setAvailableCopies(Math.min(book.getAvailableCopies(), book.getTotalCopies()));
            }

            return bookRepository.save(book);
        }).orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + id));
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book not found with ID: " + id);
        }
        bookRepository.deleteById(id);
    }

    @Transactional
    public void decreaseAvailableCopies(Book book) {
        if (book.getAvailableCopies() > 0) {
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);
        } else {
            throw new IllegalStateException("No available copies for book: " + book.getTitle());
        }
    }

    @Transactional
    public void increaseAvailableCopies(Book book) {
        if (book.getAvailableCopies() < book.getTotalCopies()) {
            book.setAvailableCopies(book.getAvailableCopies() + 1);
            bookRepository.save(book);
        } else {
            System.err.println("Warning: Attempted to increase available copies beyond total copies for book: " + book.getTitle());
        }
    }
}
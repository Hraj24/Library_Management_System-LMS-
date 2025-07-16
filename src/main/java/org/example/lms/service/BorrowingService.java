package org.example.lms.service;



import org.example.lms.entity.Book;
import org.example.lms.entity.Borrowing;
import org.example.lms.entity.Member;
import org.example.lms.repository.BookRepository;
import org.example.lms.repository.BorrowingRepository;
import org.example.lms.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookService bookService;
    private final MemberService memberService;

    @Autowired
    public BorrowingService(BorrowingRepository borrowingRepository, BookService bookService, MemberService memberService) {
        this.borrowingRepository = borrowingRepository;
        this.bookService = bookService;
        this.memberService = memberService;
    }

    @Transactional
    public Borrowing borrowBook(Long bookId, Long memberId, int loanDurationDays) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));
        Member member = memberService.getMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("Book '" + book.getTitle() + "' is currently not available for borrowing.");
        }


        if (borrowingRepository.findByBookAndMemberAndReturnedFalse(book, member).isPresent()) {
            throw new IllegalStateException("Member '" + member.getName() + "' has already borrowed '" + book.getTitle() + "' and has not returned it yet.");
        }

        Borrowing borrowing = new Borrowing();
        borrowing.setBook(book);
        borrowing.setMember(member);
        borrowing.setBorrowDate(LocalDate.now());
        borrowing.setDueDate(LocalDate.now().plusDays(loanDurationDays));
        borrowing.setReturned(false);


        bookService.decreaseAvailableCopies(book);

        return borrowingRepository.save(borrowing);
    }

    @Transactional
    public Borrowing returnBook(Long borrowingId) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new IllegalArgumentException("Borrowing record not found with ID: " + borrowingId));

        if (borrowing.getReturned()) {
            throw new IllegalStateException("Book has already been returned for this borrowing record.");
        }

        borrowing.setReturnDate(LocalDate.now());
        borrowing.setReturned(true);


        bookService.increaseAvailableCopies(borrowing.getBook());

        return borrowingRepository.save(borrowing);
    }

    public List<Borrowing> getAllBorrowings() {
        return borrowingRepository.findAll();
    }

    public Optional<Borrowing> getBorrowingById(Long id) {
        return borrowingRepository.findById(id);
    }

    public List<Borrowing> getBorrowingsByMember(Long memberId) {
        Member member = memberService.getMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));
        return borrowingRepository.findByMemberOrderByBorrowDateDesc(member);
    }

    public List<Borrowing> getActiveBorrowingsForMember(Long memberId) {
        Member member = memberService.getMemberById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));
        return borrowingRepository.findByMemberAndReturnedFalse(member);
    }

    public List<Borrowing> getBorrowingsByBook(Long bookId) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));
        return borrowingRepository.findByBookOrderByBorrowDateDesc(book);
    }
}
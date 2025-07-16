package org.example.lms.controller;



import org.example.lms.entity.Borrowing;
import org.example.lms.service.BorrowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrowings")
public class BorrowingController {

    private final BorrowingService borrowingService;

    @Autowired
    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    // POST /api/borrowings/borrow
    // Request Body example: {"bookId": 1, "memberId": 1, "loanDurationDays": 14}
    @PostMapping("/borrow")
    public ResponseEntity<Borrowing> borrowBook(@RequestBody Map<String, Long> payload) {
        Long bookId = payload.get("bookId");
        Long memberId = payload.get("memberId");
        Integer loanDurationDays = payload.get("loanDurationDays") != null ? payload.get("loanDurationDays").intValue() : 14; // Default to 14 days

        if (bookId == null || memberId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Borrowing newBorrowing = borrowingService.borrowBook(bookId, memberId, loanDurationDays);
            return new ResponseEntity<>(newBorrowing, HttpStatus.CREATED);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // PUT /api/borrowings/return/{borrowingId}
    @PutMapping("/return/{borrowingId}")
    public ResponseEntity<Borrowing> returnBook(@PathVariable Long borrowingId) {
        try {
            Borrowing returnedBorrowing = borrowingService.returnBook(borrowingId);
            return new ResponseEntity<>(returnedBorrowing, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // GET /api/borrowings
    @GetMapping
    public ResponseEntity<List<Borrowing>> getAllBorrowings() {
        List<Borrowing> borrowings = borrowingService.getAllBorrowings();
        if (borrowings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(borrowings, HttpStatus.OK);

    }

    // GET /api/borrowings/{id}

    @GetMapping("/{id}")
    public ResponseEntity<Borrowing> getBorrowingById(@PathVariable Long id) {
        return borrowingService.getBorrowingById(id)
                .map(borrowing -> new ResponseEntity<>(borrowing, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    // GET /api/borrowings/member/{memberId}

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<Borrowing>> getBorrowingsByMember(@PathVariable Long memberId) {
        try {
            List<Borrowing> borrowings = borrowingService.getBorrowingsByMember(memberId);
            if (borrowings.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(borrowings, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
    }

    // GET /api/borrowings/member/{memberId}/active

    @GetMapping("/member/{memberId}/active")
    public ResponseEntity<List<Borrowing>> getActiveBorrowingsForMember(@PathVariable Long memberId) {
        try {
            List<Borrowing> borrowings = borrowingService.getActiveBorrowingsForMember(memberId);
            if (borrowings.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(borrowings, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }
    }

    // GET /api/borrowings/book/{bookId}
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Borrowing>> getBorrowingsByBook(@PathVariable Long bookId) {
        try {
            List<Borrowing> borrowings = borrowingService.getBorrowingsByBook(bookId);
            if (borrowings.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(borrowings, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
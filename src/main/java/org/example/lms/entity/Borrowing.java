package org.example.lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "borrowings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // Many borrowings can be associated with one book
    @JoinColumn(name = "book_id", nullable = false) // Specifies the foreign key column
    private Book book;

    @ManyToOne // Many borrowings can be associated with one member
    @JoinColumn(name = "member_id", nullable = false) // Specifies the foreign key column
    private Member member;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "return_date") // Can be null
    private LocalDate returnDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private Boolean returned = false; // Default to false
}
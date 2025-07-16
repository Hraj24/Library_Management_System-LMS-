package org.example.lms.repository;



import org.example.lms.entity.Borrowing;
import org.example.lms.entity.Book;
import org.example.lms.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long> {

    List<Borrowing> findByMemberAndReturnedFalse(Member member);

    List<Borrowing> findByBookAndReturnedFalse(Book book);

    Optional<Borrowing> findByBookAndMemberAndReturnedFalse(Book book, Member member);

    List<Borrowing> findByMemberOrderByBorrowDateDesc(Member member);

    List<Borrowing> findByBookOrderByBorrowDateDesc(Book book);
}
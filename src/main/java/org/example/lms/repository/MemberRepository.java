package org.example.lms.repository;



import org.example.lms.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);


    Optional<Member> findByPhoneNumber(String phoneNumber);


    List<Member> findByNameContainingIgnoreCase(String name);
}
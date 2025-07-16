package org.example.lms.service;



import org.example.lms.entity.Member;
import org.example.lms.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Member addMember(Member member) {
        if (memberRepository.findByEmail(member.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Member with email " + member.getEmail() + " already exists.");
        }
        if (memberRepository.findByPhoneNumber(member.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Member with phone number " + member.getPhoneNumber() + " already exists.");
        }
        return memberRepository.save(member);
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Optional<Member> getMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }

    public List<Member> searchMembersByName(String name) {
        return memberRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public Member updateMember(Long id, Member updatedMember) {
        return memberRepository.findById(id).map(member -> {
            member.setName(updatedMember.getName());
            member.setAddress(updatedMember.getAddress());

           
            if (!member.getPhoneNumber().equals(updatedMember.getPhoneNumber())) {
                if (memberRepository.findByPhoneNumber(updatedMember.getPhoneNumber()).isPresent()) {
                    throw new IllegalArgumentException("New phone number " + updatedMember.getPhoneNumber() + " already exists for another member.");
                }
                member.setPhoneNumber(updatedMember.getPhoneNumber());
            }


            if (!member.getEmail().equals(updatedMember.getEmail())) {
                if (memberRepository.findByEmail(updatedMember.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("New email " + updatedMember.getEmail() + " already exists for another member.");
                }
                member.setEmail(updatedMember.getEmail());
            }

            return memberRepository.save(member);
        }).orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + id));
    }

    @Transactional
    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new IllegalArgumentException("Member not found with ID: " + id);
        }
        memberRepository.deleteById(id);
    }
}
package com.hotrodoan.repository;

import com.hotrodoan.model.GymBranch;
import com.hotrodoan.model.Member;
import com.hotrodoan.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByUser(User user);
    Page<Member> findAll(Pageable pageable);
    Page<Member> findByAddressContaining(String address, Pageable pageable);
    int countByGymBranch(GymBranch gymBranch);
}

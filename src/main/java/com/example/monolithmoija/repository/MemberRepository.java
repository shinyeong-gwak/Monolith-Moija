package com.example.monolithmoija.repository;

import com.example.monolithmoija.dto.PostRes;
import com.example.monolithmoija.entities.Member;
import com.example.monolithmoija.entities.TeamId;
import com.example.monolithmoija.entities.Waiting;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, TeamId> {
    Optional<Member> findByRecruitIdAndUserId(Long recruitId, String userId);

    boolean existsByRecruitIdAndUserId(Long postId, String userId);

    List<Member> findAllByRecruitId(Long recruitId);

    @Transactional
    void deleteByUserId(String userId);

    List<Member> findAllByUserId(String userId);
}

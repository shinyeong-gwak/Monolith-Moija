package com.example.monolithmoija.repository;

import com.example.monolithmoija.entities.Clip;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClipRepository extends JpaRepository<Clip,Long> {
    boolean existsByRecruitIdAndUserId(Long recruitId, String userId);

    @Transactional
    void deleteByRecruitIdAndUserId(Long recruitId, String userId);

    List<Clip> findAllByUserId(String userId);
}

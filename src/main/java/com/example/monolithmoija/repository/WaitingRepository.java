package com.example.monolithmoija.repository;

import com.example.monolithmoija.entities.Waiting;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WaitingRepository extends JpaRepository<Waiting, Long> {
    Optional<Waiting> findByRecruitIdAndUserId(Long recruitId, String userId);

    boolean existsByRecruitIdAndUserId(Long teamId, String userId);
    Optional<Waiting> findByWaitingId(Long waitingId);

    //대기 수락하기.
    @Transactional
    @Modifying
    @Query(value = "UPDATE Waiting w SET w.isPermitted=true WHERE w.recruitId = :recruitId AND w.userId= :userId")
    void waitingPermmit(@Param("recruitId")Long recruitId, @Param("userId")String userId);

    List<Waiting> findAllByRecruitId(Long recruitId);

    List<Waiting> findAllByUserId(String userId);
}

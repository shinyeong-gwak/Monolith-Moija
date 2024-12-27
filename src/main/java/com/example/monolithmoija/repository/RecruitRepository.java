package com.example.monolithmoija.repository;

import com.example.monolithmoija.entities.Recruit;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;


@Repository
public interface RecruitRepository extends JpaRepository<Recruit,Long> {

    //좋아요 API
    @Modifying
    @Transactional
    @Query(value="update Recruit r set r.likes = r.likes + 1 where r.recruitId = :recruitId")
    Integer updateLikeUp(@Param("recruitId") Long recruitId);
    @Modifying
    @Transactional
    @Query(value="update Recruit r set r.likes = r.likes - 1 where r.recruitId = :recruitId")
    Integer updateLikeDown(@Param("recruitId") Long recruitId);

    //조회수 API
    @Modifying
    @Transactional
    @Query(value="update Recruit r set r.views = r.views + 1 where r.recruitId = :recruitId")
    Integer updateView(@Param("recruitId") Long recruitId);
    @Modifying
    @Transactional
    @Query(value="update Recruit r set r.isAvailable=false where r.recruitId = :recruitId")
    Integer notAvailable(@Param("recruitId") Long recruitId);

    //sy-gwak today
    Optional<Recruit> findByRecruitIdAndIsAvailableTrue(@Param("recruitId") Long Id);
    @Query("SELECT r.title FROM Recruit r WHERE r.recruitId = :recruitId AND r.isAvailable = true")
    Optional<String> findTitleByRecruitIdAndIsAvailableTrue(@Param("recruitId") Long Id);
    Optional<Recruit> findByRecruitId(Long Id);

    Page<Recruit> findAllByLeaderIdAndIsAvailableTrueAndStateRecruitTrue(String leaderId, Pageable pageable);
    Page<Recruit> findAllByLeaderIdAndIsAvailableTrueAndStateRecruitFalse(String leaderId, Pageable pageable);
    List<Recruit> findAllByLeaderId(String leaderId);
    List<Recruit> findAllByIsAvailableTrueOrderByStateRecruitDescLatestWriteDesc();
    List<Recruit> findAllByCategoryAndIsAvailableTrueOrderByStateRecruit(String category);
    // sy-gwak today
    List<Recruit> findAllByCategoryContainingAndIsAvailableTrueAndStateRecruitTrue(String category);//, Pageable pageable);
    List<Recruit> findAllByCategoryContainingAndIsAvailableTrueAndStateRecruitFalse(String category);//, Pageable pageable);

    //user 체크하기 위함
    @Query("SELECT r.leaderId FROM Recruit r WHERE r.recruitId = :recruitId")
    Optional<String> findLeaderIdByRecruitId(@Param("recruitId") Long recruitId);


    @Modifying
    @Transactional
    @Query("UPDATE Recruit r SET r.latestWrite= :latest where r.recruitId= :recruitId")
    void updateTimeLatest(@Param(value = "latest") Timestamp latest, @Param(value = "recruitId") Long recruitId);

    boolean existsByRecruitIdAndIsAvailableTrue(Long recruitId);

    @Modifying
    @Transactional
    @Query("UPDATE Recruit r SET r.stateRecruit=:stateRecruit where r.recruitId= :postId")
    void updateStateRecruit(@Param(value = "postId")Long postId, @Param(value = "stateRecruit")boolean stateRecruit);

    @Query("SELECT r.stateRecruit FROM Recruit r WHERE r.recruitId= :postId")
    boolean isRecruiting(@Param(value = "postId")Long postId);

    Page<Recruit> findAllByTitleContainingAndIsAvailableTrue(String title,Pageable pageable);
    List<Recruit> findAllByTitleContaining(String title);
    Page<Recruit> findAllByContentsContainingAndIsAvailableTrue(String contents,Pageable pageable);
    List<Recruit> findAllByContentsContaining(String contents);
    Page<Recruit> findAllByLeaderIdContainingAndIsAvailableTrue(String userId,Pageable pageable);
    List<Recruit> findAllByLeaderIdContaining(String userId);

    @Modifying
    @Transactional
    @Query("UPDATE Recruit r SET r.reliabilityRecruit= :score where r.recruitId= :postId")
    void updateReliabilityRecruit(@Param(value = "postId")Long postId, @Param(value = "score")float score);

}

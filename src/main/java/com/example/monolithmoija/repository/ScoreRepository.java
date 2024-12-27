package com.example.monolithmoija.repository;

import com.example.monolithmoija.entities.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score,Long> {

    boolean existsByGrantIdAndGrantedId(String userId, String grantedId);

    int countByGrantedId(String userId);

    Score findByGrantIdAndGrantedId(String myId, String userId);
}

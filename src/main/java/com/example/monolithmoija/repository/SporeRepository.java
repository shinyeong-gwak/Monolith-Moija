package com.example.monolithmoija.repository;

import com.example.monolithmoija.entities.Spore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SporeRepository extends JpaRepository<Spore,Long> {

    int countByGrantedId(Long postId);

    boolean existsByGrantedIdAndGrantId(Long postId, String userId);
    @Query("SELECT s.spore FROM Spore s WHERE s.grantedId = :recruitId AND s.grantId = :userId")
    Optional<Float> getByGrantedIdAndGrantId(@Param("recruitId")Long recruitId, @Param("userId")String userId);
}

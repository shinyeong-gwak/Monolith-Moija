package com.example.monolithmoija.service;

import com.example.monolithmoija.entities.Spore;
import com.example.monolithmoija.repository.SporeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

@Slf4j@Service@RequiredArgsConstructor
public class SporeService {
    @Autowired
    SporeRepository sporeRepository;

    public int countByRecruitId(Long postId) {
        return sporeRepository.countByGrantedId(postId);
    }

    public boolean existsByPostIdAndUserId(Long postId, String userId) {
        return sporeRepository.existsByGrantedIdAndGrantId(postId,userId);
    }

    public void saveGrant(Long postId, String userId, float score) {
        Spore spore = Spore.builder()
                .grantedId(postId)
                .grantId(userId)
                .spore(score)
                .build();
        sporeRepository.save(spore);
    }

    @Query("SELECT r.title FROM Recruit r WHERE r.recruitId = :recruitId AND r.isAvailable = true")
    public float findByPostIdAndUserId(Long recruitId, String userId) {
        return sporeRepository.getByGrantedIdAndGrantId(recruitId,userId).isPresent() ? sporeRepository.getByGrantedIdAndGrantId(recruitId,userId).get(): 0.0f;
    }
}

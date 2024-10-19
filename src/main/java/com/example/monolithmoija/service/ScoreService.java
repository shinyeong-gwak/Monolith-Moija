package com.example.monolithmoija.service;

import com.example.monolithmoija.entity.Score;
import com.example.monolithmoija.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service@Slf4j@RequiredArgsConstructor
public class ScoreService {
    @Autowired
    ScoreRepository scoreRepository;
    public boolean existsByGrantAndGranted(String userId, String userId1) {
        return scoreRepository.existsByGrantIdAndGrantedId(userId,userId1);
    }

    public int countByGrantedId(String userId) {
        return scoreRepository.countByGrantedId(userId);
    }

    public void saveGrant(String userId, String userId1, float reli) {
        Score score = Score.builder()
                .grantId(userId)
                .grantedId(userId1)
                .score(reli)
                .build();
        scoreRepository.save(score);
    }

    public Float getMyGrantScore(String myId, String userId) {
        Score score = scoreRepository.findByGrantIdAndGrantedId(myId,userId);
        return score.getScore();
    }
}

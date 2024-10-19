package com.example.monolithmoija.service;

import com.example.monolithmoija.dto.PostReq;
import com.example.monolithmoija.entities.Like;
import com.example.monolithmoija.global.BaseException;
import com.example.monolithmoija.repository.LikeRepository;
import com.example.monolithmoija.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.monolithmoija.global.BaseResponseStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    RecruitRepository recruitRepository;
    public void userPostLike(PostReq.PostLikeReq likeReq,String userId) throws BaseException {
        Long recruitId = likeReq.getRecruitId();
        //내 게시물의 좋아요를 누를 때
//        if(recruitRepository.findLeaderIdByRecruitId(recruitId).isPresent() &&
//                recruitRepository.findLeaderIdByRecruitId(recruitId).get().equals(userId)) {
//            throw new BaseException(CANNOT_LIKE_MINE);
//        }
        //좋아요를 누를때
        if(likeReq.getVote() == 1) {
            if(likeRepository.existsByRecruitIdAndUserId(recruitId,userId)) {
                throw new BaseException(LIKE_ALREADY_EXISTS);
            } else {
                //좋아요 수 처리
                recruitRepository.updateLikeUp(recruitId);
                //좋아요 누른 사람과 관계 처리
                Like like = Like.builder()
                        .recruitId(recruitId)
                        .userId(userId)
                        .build();
                likeRepository.save(like);
            }
        //좋아요를 취소할 때
        } else if (likeReq.getVote() == 0) {
            if(likeRepository.existsByRecruitIdAndUserId(recruitId,userId)) {
                likeRepository.deleteByRecruitIdAndUserId(recruitId,userId);
                recruitRepository.updateLikeDown(recruitId);
            } else {
                throw new BaseException(LIKE_NOT_EXISTS);
            }
        } else {
            throw new BaseException(BAD_ACCESS);
        }
    }

    public boolean existsByRecruitIdAndUserId(Long recruitId,String userId) {
        return likeRepository.existsByRecruitIdAndUserId(recruitId, userId);
    }
}

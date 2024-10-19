package com.example.monolithmoija.service;

import com.example.monolithmoija.dto.PostReq;
import com.example.monolithmoija.dto.PostRes;
import com.example.monolithmoija.entities.Clip;
import com.example.monolithmoija.entities.Recruit;
import com.example.monolithmoija.global.BaseException;
import com.example.monolithmoija.repository.ClipRepository;
import com.example.monolithmoija.repository.RecruitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.monolithmoija.global.BaseResponseStatus.*;
import static com.example.monolithmoija.global.BaseResponseStatus.BAD_ACCESS;
import static com.example.monolithmoija.service.PostService.makeList;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClipService {
    @Autowired
    ClipRepository clipRepository;
    @Autowired
    RecruitRepository recruitRepository;



    public void userPostClip(PostReq.PostClipReq clipReq, String userId) throws BaseException {
        Long recruitId = clipReq.getRecruitId();
        //내 게시물에 스크랩시 !!!!!!!!!!!!!!!!!!이거 수정 필요 일단 나중에
        if(recruitRepository.findLeaderIdByRecruitId(recruitId).isPresent() &&
                recruitRepository.findLeaderIdByRecruitId(recruitId).get().equals(userId)) {
            throw new BaseException(CANNOT_CLIP_MINE);
        }
        //클립 누를때
        if(clipReq.getClip() == 1) {
            if(clipRepository.existsByRecruitIdAndUserId(recruitId,userId)) {
                throw new BaseException(CLIP_ALREADY_EXISTS);
            } else {
                //클립한 사람과 모집의 관계 처리.
                Clip clip = Clip.builder()
                        .recruitId(recruitId)
                        .userId(userId)
                        .build();
                clipRepository.saveAndFlush(clip);
            }
            //클립을 취소할 때
        } else if (clipReq.getClip() == 0) {
            if(clipRepository.existsByRecruitIdAndUserId(recruitId,userId)) {
                clipRepository.deleteByRecruitIdAndUserId(recruitId,userId);
            } else {
                throw new BaseException(CLIP_NOT_EXISTS);
            }
        } else {
            throw new BaseException(BAD_ACCESS);
        }
    }

    public List<PostRes.ListPostRes> viewUsersClip(String userId) {
        List<Clip> clips = clipRepository.findAllByUserId(userId);
        //predicate식의 list 방식으로 다시 만들어야겠네...
        List<Recruit> recruits = clips.stream().map(Clip::getRecruit).toList();
        return makeList(recruits);

    }

    public boolean existsByRecruitIdAndUserId(Long recruitId, String userId) {
        return clipRepository.existsByRecruitIdAndUserId(recruitId,userId);
    }
}

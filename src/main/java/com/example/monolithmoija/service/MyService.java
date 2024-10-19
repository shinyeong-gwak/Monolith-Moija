package com.example.monolithmoija.service;

import com.example.monolithmoija.dto.MypageRes;
import com.example.monolithmoija.dto.PostRes;
import com.example.monolithmoija.entities.Member;
import com.example.monolithmoija.global.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.monolithmoija.global.BaseResponseStatus.*;

import static com.example.monolithmoija.service.PostService.makeList;

@Service
@Slf4j
@RequiredArgsConstructor
public class MyService {
    @Autowired
    MemberService memberService;
    @Autowired
    PostService postService;
    public List<PostRes.ListPostRes> loadRecruitList(String userId) throws BaseException {
        //all latest는 적용되지 않음...
        Pageable pageable = PageRequest.of(0,30);
        return postService.list(Optional.empty(),"latest",Optional.empty(),Optional.empty(), Optional.of(userId),pageable);
    }
    public List<PostRes.ListPostRes> loadSendList(String userId) throws BaseException {
        //all latest는 적용되지 않음...
        Pageable pageable = PageRequest.of(0,30);
        return postService.list(Optional.empty(),"latest",Optional.empty(),Optional.empty(), Optional.of(userId),pageable);
    }

    public List<MypageRes.MemListRes> loadMemberList(Long postId) throws BaseException {
        if(!postService.existPost(postId)) {
            throw new BaseException(BAD_ACCESS);
        }
        List<Member> members = memberService.findAllByRecruitId(postId);
        if(members.isEmpty()) {
            throw new BaseException(BAD_ACCESS);
        }
        return members.stream().sorted(Member::isLeader).map(m -> new MypageRes.MemListRes(m.getUser().getNickname(), m.getUserId())).collect(Collectors.toList());
    }

    public void kickMember(Long postId, String userNickname) throws BaseException {
        List<Member> members = memberService.findAllByRecruitId(postId);
        //팀 멤버에 킥하려는 애가 없는 경우
        if (members.stream().noneMatch(m -> userNickname.equals(m.getUser().getNickname()))) {
            throw new BaseException(USER_NOT_EXISTS);
        } else {
            members.stream().filter(m -> userNickname.equals(m.getUser().getNickname())).forEach(m -> {
                memberService.deleteByUserId(m.getUserId());
            });
        }

    }

    public List<PostRes.ListPostRes> findBymemberedTeam(String userId) {
        List<Member> members= memberService.findAllByUserId(userId);
        //완전한 삭제가 되지 않은 게시글 처리
        members.removeIf(m -> !m.getRecruit().isAvailable());
        return makeList(members.stream().map(Member::getRecruit).toList());
    }
}

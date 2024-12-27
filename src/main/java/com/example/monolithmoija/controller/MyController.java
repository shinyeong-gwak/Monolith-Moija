package com.example.monolithmoija.controller;

import com.example.monolithmoija.dto.*;
import com.example.monolithmoija.entity.Account;
import com.example.monolithmoija.global.BaseException;
import com.example.monolithmoija.global.BaseResponse;
import com.example.monolithmoija.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.monolithmoija.global.BaseResponseStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyController {
    @Autowired
    MyService myService;
    @Autowired
    ClipService clipService;
    @Autowired
    UserService userService;
    @Autowired
    WaitingService waitingService;

    @PostMapping("/team/list")
    public BaseResponse<List> loadTeamList(
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        //이거 유저는 널이면 로그인 시키는 게 너무 많은거 어떻게 기본적으로 처리할 수 있는지 찾아봐야함.
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        List<PostRes.ListPostRes> response = myService.loadRecruitList(account.getUsername());

        return new BaseResponse<List>(response);
    }

    @PostMapping("/member/{postId}")
    public List<MypageRes.MemListRes> loadMemberList(
            @PathVariable(value = "postId") Long postId,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        /**
         * 내가 글의 제작자인지 확인을 하지 않아요~~~~
         * 지금 매우 위험한 상태에요~ -> 하지만 안해도 되는걸로 생각이 된다!
         */
        List<MypageRes.MemListRes> response = myService.loadMemberList(postId);

        return response;
    }
    @PostMapping("/member/kick/{postId}")
    public BaseResponse<Void> kickMember(
            @PathVariable(value = "postId") Long postId,
            @RequestPart(name = "req") MypageReq.MyKickReq myKickReq,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        /**
         * 내가 글의 제작자인지 확인을 하지 않아요~~~~
         * 지금 매우 위험한 상태에요~
         */
        myService.kickMember(postId,myKickReq.getUserNickname());
        return new BaseResponse<Void>(SUCCESS);
    }
    @PostMapping("/waiting/list")
    public BaseResponse<List> loadWaitingList(
            @AuthenticationPrincipal Account account
    ) throws BaseException{
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        List<PostRes.ListPostRes> target = myService.loadRecruitList(account.getUsername());
        List<MypageRes.WaitingListRes> response = waitingService.loadWaitingList(target);
        return new BaseResponse<List>(response);
    }

    @PostMapping("/send/list")
    public BaseResponse<List> loadSendList(
            @AuthenticationPrincipal Account account
    ) throws BaseException{
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        //List<MypageRes.AskListRes> response = waitingService.loadMyRequest(account.getUsername());
        List<PostRes.ListPostRes> myList = myService.loadRecruitList(account.getUsername());
        List<MypageRes.AcceptRes> response = myList.stream().map(l -> new MypageRes.AcceptRes(l.getTitle(),l.getPost_id())).toList();
        return new BaseResponse<List>(response);
    }

    @PostMapping("/waiting/{waitingId}")
    public BaseResponse<MypageRes.WaitingRes> viewWaiting(
            @PathVariable(value = "waitingId") Long waitingId,
            @AuthenticationPrincipal Account account
    )throws BaseException {
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        /**
         * 내가 글의 제작자인지 확인을 하지 않아요~~~~
         * 지금 매우 위험한 상태에요~
         */
        MypageRes.WaitingRes response = waitingService.viewWaiting(waitingId,account.getUsername());
        return new BaseResponse<>(response);
    }

    @PostMapping("/accept/{waitingId}")
    public BaseResponse<Void> acceptWaiting(
            @PathVariable(value = "waitingId") Long waitingId,
            @AuthenticationPrincipal Account account
    )throws BaseException {
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        /**
         * 내가 글의 제작자인지 확인을 하지 않아요~~~~
         * 지금 매우 위험한 상태에요~
         */
        waitingService.acceptOrDeny(waitingId,true,account.getUsername());
        return new BaseResponse<>(SUCCESS);
    }
    @PostMapping("/deny/{waitingId}")
    public BaseResponse<Void> denyWaiting(
        @PathVariable(value = "waitingId") Long waitingId,
        @AuthenticationPrincipal Account account
    )throws BaseException {
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        /**
         * 내가 글의 제작자인지 확인을 하지 않아요~~~~
         * 지금 매우 위험한 상태에요~
         */
        waitingService.acceptOrDeny(waitingId,false,account.getUsername());
        return new BaseResponse<>(SUCCESS);
    }

    @PostMapping("/clip")
    public BaseResponse<List> viewMyClip(
            @AuthenticationPrincipal Account account
    ) throws BaseException{
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        List<PostRes.ListPostRes> response = clipService.viewUsersClip(account.getUsername());
        
        return new BaseResponse<List>(response);
    }

    @PostMapping("/joined-team")
    public BaseResponse<List> viewMyJoinTeam(
            @AuthenticationPrincipal Account account
    ) throws BaseException{
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        List<PostRes.ListPostRes> response = new ArrayList<>(myService.findBymemberedTeam(account.getUsername()));
        //response.forEach(s-> System.out.println("my all team : "+s.getTitle()+" / "+ s.getPost_id()));
        List<PostRes.ListPostRes> myRecruit = new ArrayList<>(myService.loadRecruitList(account.getUsername()));
        //myRecruit.forEach(s-> System.out.println("im leader team : "+s.getTitle()+" / "+s.getPost_id()));
        //내가 참여한 모임 - 내가 연 모임 => 내가 조인한 모임
        myRecruit.forEach(l -> response.removeIf(rl -> Objects.equals(rl.getPost_id(), l.getPost_id())));
        if(response.isEmpty())
            throw new BaseException(NOT_EXISTS);
        return new BaseResponse<List>(response);
    }

    @PostMapping("/profile")
    public BaseResponse<UserRes.ProfileRes> viewMyProfile(
            @AuthenticationPrincipal Account account
    ) throws BaseException, IOException {
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        UserRes.ProfileRes response = userService.loadProfile(account.getUsername());
        return new BaseResponse<>(response);
    }
    @PutMapping(value="/profile/edit/photo",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<Void> editPhoto(
            @RequestPart(value = "file") MultipartFile file,
            @AuthenticationPrincipal Account account
    ) throws BaseException, IOException, NoSuchAlgorithmException, InvalidKeyException {
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        userService.saveProfile(file.getOriginalFilename(), file,account.getUsername());
        return new BaseResponse<Void>(SUCCESS);
    }
    @PutMapping("/profile/edit/nick")
    public BaseResponse<Void> editNick(
            @RequestPart(name = "req") String newNickname,
            @AuthenticationPrincipal Account account
    ) throws BaseException {
        if(account == null)
            throw new BaseException(LOGIN_FIRST);
        userService.editNickname(newNickname,account.getUsername());
        return new BaseResponse<Void>(SUCCESS);
    }



}

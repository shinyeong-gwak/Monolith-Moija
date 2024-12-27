package com.example.monolithmoija.controller;

import com.example.monolithmoija.dto.*;
import com.example.monolithmoija.entity.Account;
import com.example.monolithmoija.entity.ChatDTO;
import com.example.monolithmoija.global.BaseException;
import com.example.monolithmoija.global.BaseResponse;
import com.example.monolithmoija.global.BaseResponseStatus;
import com.example.monolithmoija.service.MessageService;
import com.example.monolithmoija.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {
    @Autowired
    private StompRabbitController rabbitController;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;

    @PostMapping("/box")
    public List<ChatRoomDTO> myChatRoom(@AuthenticationPrincipal Account account) {

        return messageService.myChatRoom(account.getUsername());
    }

    //앞으로 exchange에 사용될 아이디를 뱉어준다. 참고로 시큐리티가 연결되지 않아서 매우 취약하다.
    @PostMapping(value = "/create/one-to-one",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Map<String,String> newChatRoom(@AuthenticationPrincipal Account account, @RequestPart(name = "chat") ChatCreateDTO chatCreateDTO ) {
        Map<String,String> response = messageService.newChatRoom(account.getUsername(),chatCreateDTO);
        // 최초의 메시지를 보내놓는
        rabbitController.send(ChatDTO.builder()
                        .type(Type.TALK)//edit
                        .memberId(account.getUsername())
                        .nickname(chatCreateDTO.nickname())
                        .regDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
                        .message("<"+chatCreateDTO.postTitle()+">의 모임장 님이 "+chatCreateDTO.nickname()+"님의 채팅 요청을 수락했어요!")
                .build(), response.get("chatRoomId"));
        return response;
    }

    // 이전에 내가 선택한 그 대화방에 이전에 있었던 대화를 로드해준다.(이때 소켓 연결 후 대화는 치지 않음)
    // 근데 페이지네이션 될때는 어떡하지.. 아마 프론트에서...소켓으로 받은 메시지 + 이전 리스트로 받은 메시지 잘 분배해서 처리해줘야할듯...
    @PostMapping(value="/list",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public BaseResponse<Page<ChatDTO>> getPreviousChat(@RequestPart(name = "chat") ChatListDTO chatListDTO,@AuthenticationPrincipal Account account) throws BaseException {
        //위임받은 인증
        if(!userService.isRoomOwner(account.getUsername(),chatListDTO.chatRoomId()))
            throw new BaseException(BaseResponseStatus.NOT_PRIVILEGE);
        Pageable pageable = PageRequest.of(chatListDTO.page_number(), chatListDTO.page_size());
        return new BaseResponse<>(messageService.getPreviousChat(chatListDTO.chatRoomId(),pageable));
    }

}
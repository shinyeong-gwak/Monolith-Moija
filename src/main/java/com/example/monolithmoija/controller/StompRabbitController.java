package com.example.monolithmoija.controller;


import com.example.monolithmoija.dto.Type;
import com.example.monolithmoija.entity.ChatDTO;
import com.example.monolithmoija.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@RequiredArgsConstructor
@Log4j2
public class StompRabbitController {

    @Autowired@Qualifier("chatRabbitTemplate")
    private final RabbitTemplate chatRabbitTemplate;
    @Autowired
    private MessageService messageService;

    private final static String CHAT_EXCHANGE_NAME = "chat.exchange";
//    private final static String CHAT_QUEUE_NAME = "chat.queue";


    @MessageMapping("chat.enter.{chatRoomId}")
    public void enter(@Payload ChatDTO chat, @DestinationVariable String chatRoomId){
        
        chat.setMessage(chat.getNickname()+"님께서 입장하셨습니다.");
        chat.setRegDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
        chat.setType(Type.ENTER);

        chatRabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat); // exchange
        messageService.storeMessage(chat,"message-"+chatRoomId);
    }

    @MessageMapping("chat.message.{chatRoomId}")
    public void send(@Payload ChatDTO chat, @DestinationVariable String chatRoomId){
        //디폴트 타임존은 서울이긴 한데,
        //아마 프론트에서는 나중에 이를 접속한 클라이언트 기반으로 핸들링할 필요 있을 듯
        chat.setRegDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
        chat.setType(Type.TALK);

        chatRabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
        messageService.storeMessage(chat,"message-"+chatRoomId);
    }

    //채팅방에 접속할때마다 읽음처리를 하고 북마크를 끼워놓 듯이 전송
    @MessageMapping("chat.read.{chatRoomId}")
    public void read(@Payload ChatDTO chat, @DestinationVariable String chatRoomId) {
        chat.setRegDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));
        chat.setType(Type.READ);
        chatRabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME,"room."+chatRoomId,chat);
        messageService.storeMessage(chat,"message-"+chatRoomId);
    }

}
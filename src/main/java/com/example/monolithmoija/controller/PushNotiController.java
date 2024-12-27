package com.example.monolithmoija.controller;

import com.example.monolithmoija.dto.NotifyDTO;
import com.example.monolithmoija.entity.PushNoti;
import com.example.monolithmoija.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.time.ZonedDateTime;



@RestController
@RequiredArgsConstructor
@Log4j2
public class PushNotiController {
    @Autowired @Qualifier("notifyRabbitTemplate")
    private final RabbitTemplate notifyRabbitTemplate;

    private final static String NOTIFY_EXCHANGE_NAME = "notify.exchange";
    @Autowired
    private NotificationService notificationService;

    @Value(value = "&{server.broadcast.key}")
    private String key;

    // 이거는 postService나 채팅이나 관련된 api에서 호출해야한다.
    @PostMapping("/push/pub/noti/{userId}")
    @MessageMapping("notify.publish.{userId}")
    public void pubNoti(@Payload @RequestBody NotifyDTO notifyDTO, @DestinationVariable @PathVariable String userId) {
        /*
         * type 0 : 서버로부터의 메시지
         * type 1 : 가입 요청 승낙됨
         * type 2 : 내 글이 스크랩됨
         * type 3 : 내 글에 요청이 생김
         * type 4 : 받은 채팅
         * **/
        PushNoti pushNoti = PushNoti.builder()
                .pushType(notifyDTO.pushType())
                .message(notifyDTO.message())
                .nonRead(true)
                .pubDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
        // 라우팅 키는 user.user123
        notifyRabbitTemplate.convertAndSend(NOTIFY_EXCHANGE_NAME,"user."+userId, pushNoti);
        notificationService.storePersonalDB(pushNoti,userId);
    }

    //모두에게 가게끔 바꿔야함.
    public void broadcast(@Payload NotifyDTO notifyDTO,String userId) {
        /*
         * type 0 : 서버로부터의 메시지
         * type 1 : 가입 요청 승낙됨
         * type 2 : 내 글이 스크랩됨
         * type 3 : 내 글에 요청이 생김
         * type 4 : 받은 채팅
         * **/
        PushNoti pushNoti = PushNoti.builder()
                .pushType(notifyDTO.pushType())
                .message(notifyDTO.message())
                .nonRead(true)
                .pubDate(ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
                .build();
        // 라우팅 키는 user.user123
        notifyRabbitTemplate.convertAndSend(NOTIFY_EXCHANGE_NAME,"user."+userId, pushNoti);
    }

}

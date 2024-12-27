package com.example.monolithmoija.controller;

import com.example.monolithmoija.dto.NotifyDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component@RequiredArgsConstructor@Slf4j
public class BroadcastScheduler {

    @Autowired
    private PushNotiController pushNotiController;

    @Scheduled(cron = "10 * * * * *")
    public void notiChatting() {
        String msg = String.format("회원님은 읽지 않은 채팅 메시지가 n개 있습니다.");
        pushNotiController.broadcast(new NotifyDTO(msg,4,"http://front.mo.ija.kro.kr/chatList"),"user5");
    }

    @Scheduled(cron = "10 * * * * *")
    public void notiSafe() {
        String msg = String.format("모이자는 단 둘이 모임을 잡기보단, 여러 사람이 모였을 때 충분히 대화해 본 이후 만나는 것을 추천해요.");
        pushNotiController.broadcast(new NotifyDTO(msg,0,""),"user5");
    }


}

package com.example.monolithmoija.service;

import com.example.monolithmoija.entity.PushNoti;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service@RequiredArgsConstructor@Slf4j
public class NotificationService {
    @Autowired
    private MongoService mongoService;

    public void storePersonalDB(PushNoti pushNoti, String userId) {
        mongoService.storeMessage(pushNoti,"pushnoti-"+userId);
    }
}

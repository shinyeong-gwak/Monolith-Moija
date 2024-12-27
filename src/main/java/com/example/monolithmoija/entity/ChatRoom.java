package com.example.monolithmoija.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data@Document(collection = "chatroom")@Builder
public class ChatRoom {
    @Id
    String chatRoomId;
    //이거 다른 디비니까 조인이 있으면 잘못설계한거긴 한데....
    //일단 cascade나.... 아님 조회하는거 좀 잘 해야함....
    Long recruitId;
    @Field(name = "chat_name")
    String chatName;
}

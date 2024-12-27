package com.example.monolithmoija.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "chatmember")@Data@Builder
public class ChatMember {
    @Id
    String cMemberId;
    @DBRef
    ChatRoom chatRoom;
    //이것도 조인이라서 이 챗멤버가 캐스케이드 되는 조건이 필요함....
    @Field(name = "user_id")
    String userId;

}

package com.example.monolithmoija.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;

@Data@Document(collection = "pushnoti")@Builder@NoArgsConstructor@AllArgsConstructor
public class PushNoti {
    @Id
    String pushId;
    String message;
    @Field("non_read")
    Boolean nonRead;
    @Field("push_type")
    Integer pushType;
    String link;
    @Field("pub_date")
    ZonedDateTime pubDate;

}

/*
{
    "message":"안녕하세요?",
    "nonRead":true,
    "pushType":1,
    "link":"naver.com",
    "pubDate":"2024-07-22T19:56:33+09:00"
}

 */

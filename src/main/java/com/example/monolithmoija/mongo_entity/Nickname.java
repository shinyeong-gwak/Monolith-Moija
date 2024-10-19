package com.example.monolithmoija.mongo_entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Document(collection = "nickname_changes")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Nickname {
    @Id
    @Field("nickname_id")
    private String userId;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}

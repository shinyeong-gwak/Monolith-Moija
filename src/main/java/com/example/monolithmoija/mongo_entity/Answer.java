package com.example.monolithmoija.mongo_entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "answers")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Answer {
    @Id
    @Field("answer_id")
    private String answerId;
    private Long waitingId;
    private String userId;
    private String answer;

}

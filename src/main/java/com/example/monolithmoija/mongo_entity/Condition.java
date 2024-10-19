package com.example.monolithmoija.mongo_entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "conditions")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Condition {
    @Id
    @Field("condition_id")
    private String conditionId;
    private Long recruitId; // ObjectId("recruit_id_1")
    private String question;
    private String answer;
}
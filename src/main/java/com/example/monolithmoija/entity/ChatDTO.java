package com.example.monolithmoija.entity;

import com.example.monolithmoija.dto.Type;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder@Setter@Getter
public class ChatDTO {
    private Type type;

    private String memberId;

    private String nickname;

    private String message;

    //@JsonDeserialize
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private ZonedDateTime regDate;
}
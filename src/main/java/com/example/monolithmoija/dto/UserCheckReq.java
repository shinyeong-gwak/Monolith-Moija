package com.example.monolithmoija.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class UserCheckReq {
    @Getter
    public static class UserIdReq {
        @JsonProperty("user_id")
        String userId;
        @JsonProperty("post_id")
        Long recruitId;
    }
}

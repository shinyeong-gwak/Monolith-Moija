package com.example.monolithmoija.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash(value = "enable")
public class EnableAccount {
    @Id
    private String uuid;
    private String userEmail;
    @TimeToLive
    private long ttl;

}

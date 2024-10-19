package com.example.monolithmoija.dto;

import com.example.monolithmoija.entities.Member;
import com.example.monolithmoija.entities.Recruit;
import com.example.monolithmoija.entities.Waiting;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MypageRes {
    @Getter@Setter@Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WaitingListRes {
        List<MemDto> users;
        String title;
        @JsonProperty("post_id")
        Long postId;
        @JsonProperty("latest_write")
        Timestamp latestWrite;


    }

    @Getter@Setter@Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AskListRes {
        //리더가 되거나 / 요청자가 되거나
        MemDto users;
        String title;
        @JsonProperty("post_id")
        Long postId;
        @JsonProperty("latest_write")
        Timestamp latestWrite;
        public static AskListRes from(Waiting waitings) {
            Recruit recruit = waitings.getRecruit();
            MemDto memDto = MypageRes.MemDto.builder()
                    .waitingId(waitings.getWaitingId())
                    .is_ask(waitings.isAsk())
                    .nickname(waitings.getUser().getNickname())
                    .build();

            return AskListRes.builder()
                    .title(recruit.getTitle())
                    .postId(recruit.getRecruitId())
                    .latestWrite(recruit.getLatestWrite())
                    .users(memDto)
                    .build();
        }

    }

    @Setter@Builder@Getter@AllArgsConstructor@NoArgsConstructor
    public static class MemDto {
        String nickname;
        Long waitingId;
        boolean is_ask;
    }

    @Setter@Builder@Getter@AllArgsConstructor@NoArgsConstructor
    public static class WaitingRes {
        @JsonProperty("user_id")
        String userId;
        String nickname;
        @JsonProperty("reliability_user")
        Float reliabilityUser;
        @JsonProperty("profile_url")
        String profileUrl;
        String gender;
        //프로필사진 추가 나중에...
        String genaration;
        List<QnADTO> qnas;
        boolean is_ask;

    }
    @Setter@Builder@Getter@AllArgsConstructor@NoArgsConstructor
    public static class MemListRes {
        String nickname;
        @JsonProperty("user_id")
        String userId;
        boolean grant;

        public MemListRes(String nickname, String userId) {
            this.nickname = nickname;
            this.userId = userId;
        }
    }
    @Setter@Builder@Getter@AllArgsConstructor@NoArgsConstructor
    public static class AcceptRes {
        String title;
        Long postId;
    }
}

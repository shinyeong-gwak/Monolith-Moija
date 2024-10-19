package com.example.monolithmoija.entities;

import com.example.monolithmoija.dto.MypageRes;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "member")
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@IdClass(value = TeamId.class)
public class Member {
    @SequenceGenerator(
            name = "team_id_seq",
            sequenceName = "team_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_id_seq")
    @Id
    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Id
    @Column(name = "recruit_id", nullable = false)
    private Long recruitId;

    @Column(name = "score_team")
    private Float scoreTeam;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "recruit_id", referencedColumnName = "recruit_id", insertable = false, updatable = false)
    private Recruit recruit;
    public int isLeader(Member mem) {
        return mem.getRecruit().getLeaderId().equals(mem.getUserId()) ? 1:-1;
    }
}

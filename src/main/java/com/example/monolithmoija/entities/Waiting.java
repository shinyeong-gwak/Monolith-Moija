package com.example.monolithmoija.entities;

import com.example.monolithmoija.entity.User;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "waiting")
@AllArgsConstructor
@NoArgsConstructor
public class Waiting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "waiting_id", nullable = false)
    private Long waitingId;

    @Column(name = "is_permitted", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isPermitted;

    @Column(name = "is_ask", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isAsk;

    @Column(name = "num_answer", nullable = false, columnDefinition = "SMALLINT DEFAULT 0")
    private short numAnswer;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "recruit_id", nullable = false)
    private Long recruitId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "recruit_id", referencedColumnName = "recruit_id", insertable = false, updatable = false)
    private Recruit recruit;

}

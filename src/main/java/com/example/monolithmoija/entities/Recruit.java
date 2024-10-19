package com.example.monolithmoija.entities;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@Table(name = "recruit")
@AllArgsConstructor
@NoArgsConstructor
public class Recruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruit_id", nullable = false)
    private Long recruitId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "contents", nullable = false, length = 2000)
    private String contents;

    @Column(name = "category", nullable = false, columnDefinition = "VARCHAR(15) DEFAULT 'etc'")
    private String category;

    @Column(name = "reliability_recruit", nullable = false, columnDefinition = "FLOAT DEFAULT 0")
    private float reliabilityRecruit;

    @Column(name = "penalty", nullable = false, columnDefinition = "INT DEFAULT 0")
    private int penalty;

    @Column(name = "num_condition", nullable = false, columnDefinition = "INT(10) DEFAULT 0")
    private int numCondition;

    @Column(name = "state_recruit", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private boolean stateRecruit;

    @Column(name = "views", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long views;

    @Column(name = "likes", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long likes;

    @Column(name = "time_first_write", nullable = false)
    private Timestamp timeFirstWrite;

    @Column(name = "is_changed", nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private boolean isChanged;

    @Column(name = "time_last_write")
    private Timestamp timeLastWrite;

    @Column(name = "is_available", nullable = false, columnDefinition = "BOOLEAN DEFAULT true")
    private boolean isAvailable;

    @Column(name = "latest_write", nullable = false)
    private Timestamp latestWrite;

    @Column(name = "leader_id", nullable = false)
    private String leaderId;

    @ManyToOne(cascade = CascadeType.REMOVE, optional = false)
    @JoinColumn(name = "leader_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User leader;

}
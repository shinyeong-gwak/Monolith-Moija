package com.example.monolithmoija.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Entity
@Table(name = "likes")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id", nullable = false)
    private Long clipId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "recruit_id", nullable = false)
    private Long recruitId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false, unique = true)
    private User user;

    @ManyToOne
    @JoinColumn(name = "recruit_id", referencedColumnName = "recruit_id", insertable = false, updatable = false, unique = true)
    private Recruit recruit;

}

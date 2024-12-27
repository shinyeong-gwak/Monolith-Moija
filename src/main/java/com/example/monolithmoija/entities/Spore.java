package com.example.monolithmoija.entities;

import com.example.monolithmoija.entity.User;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "spore")
@AllArgsConstructor
@NoArgsConstructor
public class Spore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "spore_id", nullable = false)
    private Long sporeId;

    @Column(name = "spore")
    private Float spore;

    @Column(name = "grant_id", nullable = false)
    private String grantId;

    @Column(name = "granted_id", nullable = false)
    private Long grantedId;

    @ManyToOne
    @JoinColumn(name = "grant_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User grantUser;

    @ManyToOne
    @JoinColumn(name = "granted_id", referencedColumnName = "recruit_id", insertable = false, updatable = false)
    private Recruit grantedPost;

}

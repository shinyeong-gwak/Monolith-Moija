//package com.example.monolithmoija.entities;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.sql.Blob;
//import java.sql.Timestamp;
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Entity
//@Getter
//@Setter
//@Builder
//@Table(name = "user")
//@AllArgsConstructor
//@NoArgsConstructor
//public class User{
//    @Id
//    @Column(name = "user_id", nullable = false)
//    private String userId;
//
//    @Column(name = "nickname", nullable = false)
//    private String nickname;
//
//    @Column(name = "gender", nullable = false)
//    private boolean gender;
//
//    @Column(name = "birth", nullable = false)
//    private LocalDate birth;
//
//    @Column(name = "phone_number", nullable = false)
//    private String phoneNumber;
//
//    @Column(name = "name", nullable = false)
//    private String name;
//
//    @Column(name = "profile")
//    private String profile;
//
//    @Column(name = "time_join", nullable = false)
//    private Timestamp timeJoin;
//
//    @Column(name = "reliability_user", nullable = false, columnDefinition = "FLOAT DEFAULT 3")
//    private float reliabilityUser;
//
//    @Column(name = "password", nullable = false)
//    private String password;
//
//    //임시1!!!!!!!!!!!
//    @Column(name = "is_enabled", nullable = false)
//    private boolean isEnabled;
//    @Column(name = "is_account_nonlocked", nullable = false)
//    private boolean isAccountNonLocked;
//    @Column(name= "email", nullable = false)
//    private String email;
//    @Column(name = "uuid", nullable = false)
//    private String uuid;
//
//}
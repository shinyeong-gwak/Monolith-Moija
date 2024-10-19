package com.example.monolithmoija.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


@Setter
@Getter
public class Account extends User {
    private String nickname;
    private String uuid;

    public Account(String username, String password, Collection<GrantedAuthority> authorities, String nickname, String uuid) {
        super(username,password,authorities);
        this.nickname = nickname;
        this.uuid = uuid;
    }
}

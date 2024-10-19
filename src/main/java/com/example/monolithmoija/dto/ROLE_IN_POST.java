package com.example.monolithmoija.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ROLE_IN_POST {
    Viewer("V"),
    Leader("L"),
    Member("M"),
    TempMember("T");

    final String rolename;
    ROLE_IN_POST(String rolename) {
        this.rolename = rolename;
    }
    @JsonValue
    public String getValue() {
        return rolename;
    }

}

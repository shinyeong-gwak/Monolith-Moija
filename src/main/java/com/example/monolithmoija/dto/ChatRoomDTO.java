package com.example.monolithmoija.dto;

import com.example.monolithmoija.entity.ChatRoom;

import java.time.ZonedDateTime;

public record ChatRoomDTO(String lastChat, ZonedDateTime receivedTime, int nonRead, ChatRoom chatRoom, String youId) {
}

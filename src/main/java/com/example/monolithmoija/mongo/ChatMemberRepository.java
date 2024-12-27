package com.example.monolithmoija.mongo;

import com.example.monolithmoija.entity.ChatMember;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMemberRepository extends MongoRepository<ChatMember,String> {
    List<ChatMember> findAllByUserId(String userId);

    boolean existsByUserIdAndChatRoom_ChatRoomId(String userId, String chatRoomId);

    ChatMember findByChatRoom_ChatRoomIdAndUserIdNot(String chatRoomId,String userId);
}

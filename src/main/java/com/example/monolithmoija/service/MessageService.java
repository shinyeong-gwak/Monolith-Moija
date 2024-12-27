package com.example.monolithmoija.service;

import com.example.monolithmoija.dto.ChatCreateDTO;
import com.example.monolithmoija.dto.ChatRoomDTO;
import com.example.monolithmoija.entity.ChatDTO;
import com.example.monolithmoija.entity.ChatMember;
import com.example.monolithmoija.entity.ChatRoom;
import com.example.monolithmoija.mongo.ChatMemberRepository;
import com.example.monolithmoija.mongo.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service@Slf4j@RequiredArgsConstructor
public class MessageService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatMemberRepository chatMemberRepository;
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<ChatRoomDTO> myChatRoom(String userId) {
        //읽지않은 채팅 개수 알아오기
        //마지막 채팅 불러오기
        List<ChatRoom> chatRooms = chatMemberRepository.findAllByUserId(userId).stream().map(ChatMember::getChatRoom).toList();
        if(chatRooms.isEmpty()) {
            return new ArrayList<>(List.of());
        }
        return chatRooms.stream().map(chatRoom -> {
            String chatRoomId = chatRoom.getChatRoomId();
            ChatMember youId = chatMemberRepository.findByChatRoom_ChatRoomIdAndUserIdNot(chatRoomId,userId);
            return new ChatRoomDTO(
                    (String)getLastChat(chatRoomId).get("message"),
                    (ZonedDateTime) getLastChat(chatRoomId).get("receivedTime"),
                    getNonRead(chatRoom.getChatRoomId()),
                    chatRoom,
                    youId.getUserId()
            );
        }).collect(Collectors.toList());
    }

    private int getNonRead(String chatRoomId) {
        // "type"이 "READ"인 문서의 "Regdate" 값을 가져옵니다.
        Query readQuery = new Query(Criteria.where("type").is("READ"));
        readQuery.with(Sort.by(Sort.Direction.DESC, "regDate"));
        readQuery.fields().include("regDate");
        readQuery.limit(1);

        // "type"이 "READ"인 문서의 "Regdate" 값을 가져옵니다.
        ChatDTO chat = mongoTemplate.findOne(readQuery, ChatDTO.class,"message-"+chatRoomId);
        if (chat == null) {
            return countTalk();
        }

        // "READ" 이후의 "Regdate"를 가져옵니다.
        ZonedDateTime readRegdate = chat.getRegDate();

        countTalk(readRegdate);

        return countTalk(readRegdate);
    }
    private int countTalk() {
        // "type"이 "TALK"이고 "Regdate"가 "READ" 이후인 문서의 개수를 가져옵니다.
        Query talkCountQuery = new Query(Criteria.where("type").is("TALK"));
        int talkCount = (int) mongoTemplate.count(talkCountQuery, ChatDTO.class);
        if(talkCount > 99) {
            return 100;
        }
        return talkCount;
    }

    private int countTalk(ZonedDateTime readRegdate) {
        // "type"이 "TALK"이고 "Regdate"가 "READ" 이후인 문서의 개수를 가져옵니다.
        Query talkCountQuery = new Query(Criteria.where("type").is("TALK").and("Regdate").gt(readRegdate));
        int talkCount = (int) mongoTemplate.count(talkCountQuery, ChatDTO.class);
        if(talkCount > 99) {
            return 100;
        }
        return talkCount;
    }

    private Map<String,Object> getLastChat(String chatRoomId) {
        Query readQuery = new Query(Criteria.where("type").is("TALK"));
        readQuery.with(Sort.by(Sort.Direction.DESC, "regDate"));
        //readQuery.fields().include("regDate");
        readQuery.limit(1);
        ChatDTO chat = mongoTemplate.findOne(readQuery, ChatDTO.class,"message-"+chatRoomId);
        if(chat == null) {
            return Map.of("message","새로운 대화를 시작해보세요.");
        }

        return Map.of("message",chat.getMessage(),"receivedTime",chat.getRegDate());
    }

    public Map<String,String> newChatRoom(String userId, ChatCreateDTO chatCreateDTO) {
        String title = chatCreateDTO.postTitle().length() > 7 ? chatCreateDTO.postTitle().substring(0,7):chatCreateDTO.postTitle();
        String chatTitle = "["+chatCreateDTO.nickname() + "]님은 <" + title + "...> 에 들어가고 싶어요";

        // 채팅방 생성
        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                        .chatName(chatTitle)
                        .recruitId(chatCreateDTO.postId())
                .build());
        String chatRoomId = chatRoom.getChatRoomId();
        // 방장을 멤버로 추가
        chatMemberRepository.save(
                ChatMember.builder()
                        .chatRoom(chatRoom)
                        .userId(userId)
                        .build()
        );
        // 요청자를 멤버로 추가
        chatMemberRepository.save(
                ChatMember.builder()
                        .chatRoom(chatRoom)
                        .userId(chatCreateDTO.userId())
                        .build()
        );


        return Map.of("chatRoomId",chatRoomId);
    }

    public void storeMessage(ChatDTO chat,String collectionName) {
        mongoTemplate.save(chat,collectionName);
    }

    public Page<ChatDTO> getPreviousChat(String chatRoomId,Pageable pageable) {
        Query query = new Query()
                .with(pageable)
                .skip((long) pageable.getPageSize() * pageable.getPageNumber()) // offset
                .limit(pageable.getPageSize());
        query.with(Sort.by(Sort.Direction.DESC, "regDate"));
        List<ChatDTO> chats = mongoTemplate.find(query, ChatDTO.class, "message-"+chatRoomId);
        Page<ChatDTO> chatPage = PageableExecutionUtils.getPage(
                chats,
                pageable,
                () -> mongoTemplate.count(query.skip(-1).limit(-1), ChatDTO.class)
                // query.skip(-1).limit(-1)의 이유는 현재 쿼리가 페이징 하려고 하는 offset 까지만 보기에 이를 맨 처음부터 끝까지로 set 해줘 정확한 도큐먼트 개수를 구한다.
        );
        return chatPage;
    }
}

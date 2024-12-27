package com.example.monolithmoija.dto;

/*{
 *  "message" : 내 모임 ""에 지원자가 생겼어요. 내가 한 질문에는 어떤 답변을 작성했을지 확인해보세요!
 * }
 */
public record NotifyDTO(String message, int pushType, String link) {};

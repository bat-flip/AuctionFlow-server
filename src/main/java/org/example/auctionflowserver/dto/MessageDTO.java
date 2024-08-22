package org.example.auctionflowserver.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageDTO {
    private Long messageId;
    private KakaoUserDto user;  // KakaoUserDto를 사용하여 사용자 정보를 포함
    private Long chatRoomId;  // 채팅방 Id
    private String content;
    private LocalDateTime sendAt;
    private Boolean isRead;


}

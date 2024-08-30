package org.example.auctionflowserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomDTO {
    private Long chatRoomId;
    private UserDTO seller;
    private UserDTO buyer;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserDTO {
        private Long userId;
        private String email;
        private Long kakaoId;
        private String nickname;
        private String profileImageUrl;
    }
}

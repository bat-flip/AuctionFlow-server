package org.example.auctionflowserver.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserDto {
    private Long id;
    private String nickname;
    private String email;
    private String profileImageUrl;
}

package org.example.auctionflowserver.controller;

import org.example.auctionflowserver.dto.ItemResponse;
import org.example.auctionflowserver.entity.User;
import org.example.auctionflowserver.service.MyListService;
import org.example.auctionflowserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mypage")
public class MyListController {

    @Autowired
    private UserService userService;

    @Autowired
    private MyListService myListService;

    @GetMapping("/like")
    public List<ItemResponse> myLikeList(@AuthenticationPrincipal OAuth2User oAuth2User){
        String email = oAuth2User.getAttribute("email");
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        return myListService.getLikeList(user);
    }

//    @GetMapping("/sell")
//    public List<ItemResponse> myItemList(@AuthenticationPrincipal OAuth2User oAuth2User, @RequestParam int statusType){
//        String email = oAuth2User.getAttribute("email");
//        User user = userService.findUserByEmail(email);
//        if (user == null) {
//            throw new RuntimeException("사용자를 찾을 수 없습니다.");
//        }
//
//        // ItemType 1 : 전체, 2 : 진행 중(item_bid_status : active), 3: 경매 종료(item_bid_status : end)
//        // 사용자가 참여한 bid 테이블의 item에 한해서 조회 => 마이페이지 내의 기능
//        return myListService.getMyItemList(user, statusType)
//
//    }

}

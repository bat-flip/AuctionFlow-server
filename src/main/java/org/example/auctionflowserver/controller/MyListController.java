package org.example.auctionflowserver.controller;

import org.example.auctionflowserver.dto.ItemResponse;
import org.example.auctionflowserver.entity.Item;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mypage")
public class MyListController {

    @Autowired
    private UserService userService;

    @Autowired
    private MyListService myListService;

    // 찜 목록 조회
    @GetMapping("/like")
    public List<ItemResponse> myLikeList(@AuthenticationPrincipal OAuth2User oAuth2User){
        String email = oAuth2User.getAttribute("email");
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        return myListService.getLikeList(user);
    }

    // 마이페이지 구매(참여)내역
    @GetMapping("/mylist")
    public List<ItemResponse> myItemList(@AuthenticationPrincipal OAuth2User oAuth2User, @RequestParam int statusType){
        String email = oAuth2User.getAttribute("email");
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        List<Item> items = myListService.getMyItemList(user);

        // statusType 1 : 전체, 2 : 진행 중(item_bid_status : active), 3: 경매 종료(item_bid_status : end)
        final String statusFilter;
        if (statusType == 2) {
            statusFilter = "active";
        } else if (statusType == 3) {
            statusFilter = "end";
        } else {
            statusFilter = null;
        }

        // 상품 응답
        return items.stream()
                .filter(item -> statusFilter == null || item.getItemBidStatus().equals(statusFilter))
                .map(myListService::convertItemToItemResponse)
                .collect(Collectors.toList());

    }


}

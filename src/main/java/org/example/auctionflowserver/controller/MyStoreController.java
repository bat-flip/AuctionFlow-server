package org.example.auctionflowserver.controller;

import org.example.auctionflowserver.dto.StoreDTO;
import org.example.auctionflowserver.entity.User;
import org.example.auctionflowserver.service.MyStoreService;
import org.example.auctionflowserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mypage/store")
public class MyStoreController {
    @Autowired
    private MyStoreService myStoreService;

    @Autowired
    private UserService userService;

    // 상점 생성 => 사용자 당 하나의 상점 생성 가능
    @PostMapping
    public StoreDTO createStore(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestBody StoreDTO storeDTO) {
        String email = oauth2User.getAttribute("email");
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        return myStoreService.createStore(user, storeDTO);
    }

    // 사용자 상점 조회
    @GetMapping ("/storeInfo")
    public StoreDTO showUserStore(@AuthenticationPrincipal OAuth2User oAuth2User){
        String email = oAuth2User.getAttribute("email");
        User user = userService.findUserByEmail(email);
        if(user == null){
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        return myStoreService.getUserStoreInfo(user);
    }

    @PatchMapping
    public StoreDTO updateStore(@AuthenticationPrincipal OAuth2User oauth2User,
                                @RequestBody StoreDTO storeDTO) {
        String email = oauth2User.getAttribute("email");
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        return myStoreService.updateStore(user, storeDTO);
    }

    @DeleteMapping
    public String deleteStore(
            @AuthenticationPrincipal OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        myStoreService.deleteStore(user);
        return "상점이 성공적으로 삭제되었습니다.";
    }
}

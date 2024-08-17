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

    @PatchMapping("/{storeId}")
    public StoreDTO updateStore(@AuthenticationPrincipal OAuth2User oauth2User,
                                @PathVariable Long storeId, @RequestBody StoreDTO storeDTO) {
        String email = oauth2User.getAttribute("email");
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        return myStoreService.updateStore(user, storeId, storeDTO);
    }

    @GetMapping("/{storeId}")
    public StoreDTO getStore(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @PathVariable Long storeId) {
        String email = oauth2User.getAttribute("email");
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        return myStoreService.getStore(user, storeId);
    }

    @DeleteMapping("/{storeId}")
    public void deleteStore(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @PathVariable Long storeId) {
        String email = oauth2User.getAttribute("email");
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        myStoreService.deleteStore(user, storeId);
    }
}

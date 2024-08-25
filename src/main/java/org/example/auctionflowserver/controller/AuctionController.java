package org.example.auctionflowserver.controller;

import org.example.auctionflowserver.entity.Item;
import org.example.auctionflowserver.entity.User;
import org.example.auctionflowserver.service.BidService;
import org.example.auctionflowserver.service.ItemService;
import org.example.auctionflowserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/auction")
public class AuctionController {

    @Autowired
    private BidService bidService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping("/bid")
    public ResponseEntity<?> placeBid(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestParam Long itemId,
            @RequestParam BigDecimal bidAmount) {

        String email = oauth2User.getAttribute("email");
        User user = userService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
        }

        try {
            bidService.placeBid(user, itemId, bidAmount);
            return ResponseEntity.ok("입찰이 성공적으로 완료되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/end/{itemId}")
    public ResponseEntity<?> endAuction(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @PathVariable Long itemId) {
        try {
            String email = oauth2User.getAttribute("email");
            User currentUser = userService.findUserByEmail(email);
            if (currentUser == null) {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }

            Item item = itemService.getItemById1(itemId);
            if (item == null) {
                return ResponseEntity.status(404).body("아이템을 찾을 수 없습니다.");
            }

            // 현재 로그인한 사용자가 아이템의 등록자인지 확인
            if (!currentUser.equals(item.getUser())) {
                return ResponseEntity.status(403).body("경매를 종료할 권한이 없습니다.");
            }

            if (!"active".equals(item.getItemBidStatus())) {
                return ResponseEntity.badRequest().body("경매가 진행 중이 아닙니다.");
            }

            item.setItemBidStatus("end");
            item.setUpdatedAt(LocalDateTime.now());
            itemService.saveItem(item);  // saveItem 메서드를 추가하여 Item 업데이트

            // 경매가 종료된 경우 웹소켓을 통해 클라이언트에 메시지 전송
            messagingTemplate.convertAndSend("/topic/auction/" + itemId, "경매가 종료되었습니다.");

            return ResponseEntity.ok("경매가 종료되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

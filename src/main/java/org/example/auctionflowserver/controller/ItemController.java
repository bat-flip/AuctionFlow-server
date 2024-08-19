package org.example.auctionflowserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.auctionflowserver.dto.ItemCreateRequest;
import org.example.auctionflowserver.dto.ItemResponse;
import org.example.auctionflowserver.entity.User;
import org.example.auctionflowserver.service.ItemService;
import org.example.auctionflowserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(
            @AuthenticationPrincipal OAuth2User oauth2User,
            @RequestPart("item") String itemJson, // JSON 데이터를 문자열로 받음
            @RequestPart("images") List<MultipartFile> imageFiles) throws IOException {

        // JSON을 ItemCreateRequest 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        ItemCreateRequest itemCreateRequest = objectMapper.readValue(itemJson, ItemCreateRequest.class);

        // 사용자 확인 및 처리
        String email = oauth2User.getAttribute("email");
        User user = userService.findUserByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // 이미지 파일 설정
        itemCreateRequest.setProductImageFiles(imageFiles);

        // 아이템 등록 처리
        ItemResponse itemResponse = itemService.registerItem(user, itemCreateRequest);
        return ResponseEntity.ok(itemResponse);
    }
    @GetMapping("/{itemId}")
    public ItemResponse getItemById(@PathVariable Long itemId) {
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        List<ItemResponse> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    // 상품 검색
    @GetMapping("/search")
    public ResponseEntity<?> searchItems(@RequestParam String keyword) {
        List<ItemResponse> items = itemService.itemSearchByName(keyword);
        if (items.isEmpty()) {
            return ResponseEntity.ok("해당하는 상품이 없습니다");
        }
        return ResponseEntity.ok(items);
    }
}

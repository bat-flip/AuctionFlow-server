package org.example.auctionflowserver.controller;

import org.example.auctionflowserver.dto.ChatRoomRequest;
import org.example.auctionflowserver.dto.KakaoUserDto;
import org.example.auctionflowserver.dto.MessageDTO;
import org.example.auctionflowserver.dto.MessageRequest;
import org.example.auctionflowserver.entity.ChatRoom;
import org.example.auctionflowserver.entity.Message;
import org.example.auctionflowserver.entity.User;
import org.example.auctionflowserver.repository.UserRepository;
import org.example.auctionflowserver.service.ChatService;
import org.example.auctionflowserver.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RestController
@RequestMapping("/chat")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // 채팅방 생성
    @PostMapping("/chatroom")
    @ResponseBody
    public ChatRoom createChatRoom(@RequestBody ChatRoomRequest request,
                                   @AuthenticationPrincipal OAuth2User oAuth2User) {
        // 판매자 찾기
        String sellerEmail = oAuth2User.getAttribute("email");
        User seller = userRepository.findByEmail(sellerEmail);
        if (seller == null) {
            throw new RuntimeException("판매자를 찾을 수 없습니다.");
        }

        // 구매자 이메일
        String buyerEmail = request.getBuyerEmail();
        System.out.println("Request Buyer Email: " + buyerEmail);
        User buyer = userRepository.findByEmail(buyerEmail);
        if (buyer == null) {
            throw new RuntimeException("구매자를 찾을 수 없습니다.");
        }

        return chatService.createChatRoom(seller, buyer);
    }

    // 채팅 메세지 전송
    @PostMapping("/message")
    @ResponseBody
    public MessageDTO sendMessage(@AuthenticationPrincipal OAuth2User oAuth2User,
                                  @RequestBody MessageRequest messageRequest) {
        String email = oAuth2User.getAttribute("email");
        User sender = userService.findUserByEmail(email);
        if (sender == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        Message message = chatService.sendMessage(sender, messageRequest.getChatRoomId(), messageRequest.getContent());

        return convertToMessageDTO(message);
    }

    // dto로 변환 매서드
    private MessageDTO convertToMessageDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(message.getMessageId());
        dto.setContent(message.getContent());
        dto.setSendAt(message.getSendAt());
        dto.setIsRead(message.getIsRead());
        dto.setChatRoomId(message.getChatRoom().getChatRoomId());

        KakaoUserDto userDto = new KakaoUserDto();
        userDto.setId(message.getUser().getUserId());
        userDto.setEmail(message.getUser().getEmail());
        userDto.setNickname(message.getUser().getNickname());
        userDto.setProfileImageUrl(message.getUser().getProfileImageUrl());
        dto.setUser(userDto);

        return dto;
    }

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    // WebSocket을 통해 메세지 전송
    @MessageMapping("/sendMessage")
    //@SendTo("/topic/messages")
    public MessageDTO sendMessageWebSocket(@Payload MessageRequest messageRequest,
                                           @AuthenticationPrincipal OAuth2User oAuth2User) {

        String email = oAuth2User.getAttribute("email");
        User sender = userService.findUserByEmail(email);
        if (sender == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 메시지 저장 및 전송
        Message message = chatService.sendMessage2(sender, messageRequest.getChatRoomId(), messageRequest.getContent());

        logger.info("Message sent by {}: {}", sender.getEmail(), message.getContent());

        // DTO로 변환
        return convertToMessageDTO(message);
    }





}

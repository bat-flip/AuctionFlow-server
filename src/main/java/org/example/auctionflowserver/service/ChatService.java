package org.example.auctionflowserver.service;

import org.example.auctionflowserver.dto.ChatRoomDTO;
import org.example.auctionflowserver.entity.ChatRoom;
import org.example.auctionflowserver.entity.Message;
import org.example.auctionflowserver.entity.User;
import org.example.auctionflowserver.repository.ChatRoomRepository;
import org.example.auctionflowserver.repository.MessageRepository;
import org.example.auctionflowserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    // 채팅방 생성
    public ChatRoom createChatRoom(User seller, User buyer) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setSeller(seller);
        chatRoom.setBuyer(buyer);
        return chatRoomRepository.save(chatRoom);
    }

    // 메세지 전송
    public Message sendMessage(User sender, Long chatRoomId, String content) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoomId);
        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 유효하지 않습니다.");
        }

        ChatRoom chatRoom = chatRoomOptional.get();
        Message message = new Message();
        message.setUser(sender);
        message.setChatRoom(chatRoom);
        message.setContent(content);
        message.setSendAt(LocalDateTime.now());
        message.setIsRead(false);

        return messageRepository.save(message);
    }

    @Transactional
    public Message sendMessage2(User sender, Long chatRoomId, String content) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoomId);
        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 유효하지 않습니다.");
        }

        ChatRoom chatRoom = chatRoomOptional.get();
        Message message = new Message();
        message.setUser(sender);
        message.setChatRoom(chatRoom);
        message.setContent(content);
        message.setSendAt(LocalDateTime.now());
        message.setIsRead(false);

        return messageRepository.save(message);
    }

    // 시스템 메세지(낙찰 시 자동으로 전송)
    public Message sendSystemMessage(Long chatRoomId, String content) {
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findById(chatRoomId);
        if (chatRoomOptional.isEmpty()) {
            throw new IllegalArgumentException("채팅방이 유효하지 않습니다.");
        }

        ChatRoom chatRoom = chatRoomOptional.get();
        Message message = new Message();
        message.setUser(null);  // 시스템 메시지인 경우 null로 설정
        message.setChatRoom(chatRoom);
        message.setContent(content);
        message.setSendAt(LocalDateTime.now());
        message.setIsRead(false);

        return messageRepository.save(message);
    }

    public List<ChatRoomDTO> findChatRoomDTOsByUser(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findBySellerOrBuyer(user, user);
        return chatRooms.stream()
                .map(chatRoom -> new ChatRoomDTO(
                        chatRoom.getChatRoomId(),
                        new ChatRoomDTO.UserDTO(
                                chatRoom.getSeller().getUserId(),
                                chatRoom.getSeller().getEmail(),
                                chatRoom.getSeller().getKakaoId(),
                                chatRoom.getSeller().getNickname(),
                                chatRoom.getSeller().getProfileImageUrl()
                        ),
                        new ChatRoomDTO.UserDTO(
                                chatRoom.getBuyer().getUserId(),
                                chatRoom.getBuyer().getEmail(),
                                chatRoom.getBuyer().getKakaoId(),
                                chatRoom.getBuyer().getNickname(),
                                chatRoom.getBuyer().getProfileImageUrl()
                        )
                ))
                .collect(Collectors.toList());
    }

}

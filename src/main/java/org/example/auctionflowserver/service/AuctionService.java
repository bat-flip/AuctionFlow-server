package org.example.auctionflowserver.service;

import org.example.auctionflowserver.entity.Bid;
import org.example.auctionflowserver.entity.ChatRoom;
import org.example.auctionflowserver.entity.Item;
import org.example.auctionflowserver.entity.User;
import org.example.auctionflowserver.repository.BidRepository;
import org.example.auctionflowserver.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;

@Service
public class AuctionService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ChatService chatService;


    // 경매 종료 시 자동으로 낙찰자 선택 및 채팅방 생성, 알림 메세지 전송
    public void endAuction(Long itemId) {
        // 1. 경매 종료된 상품 조회
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new IllegalArgumentException("상품 정보를 찾을 수 없습니다.");
        }

        Item item = optionalItem.get();

        // 2. 최고 입찰자 찾기
        Bid highestBid = item.getBids().stream()
                .max(Comparator.comparing(Bid::getBidAmount))
                .orElseThrow(() -> new IllegalStateException("경매 정보를 찾을 수 없습니다."));

        User buyer = highestBid.getUser();
        User seller = item.getUser();


        // 3. 채팅방 생성 및 시스템 메세지 전송
        ChatRoom chatRoom = chatService.createChatRoom(seller, buyer);
        String systemMessageContent = "축하합니다. 해당 상품에 낙찰되셨습니다. 낙찰 상품 정보 : " + item.getTitle();
        chatService.sendSystemMessage(chatRoom.getChatRoomId(), systemMessageContent);


        // 4. 경매 아이템 상태 업데이트     active -> end로 변경
        item.setItemBidStatus("end");
        itemRepository.save(item);
    }
}

package org.example.auctionflowserver.service;

import org.example.auctionflowserver.dto.BidNotification;
import org.example.auctionflowserver.entity.Bid;
import org.example.auctionflowserver.entity.Item;
import org.example.auctionflowserver.entity.User;
import org.example.auctionflowserver.repository.BidRepository;
import org.example.auctionflowserver.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Transactional  // 트랜잭션을 시작하는 어노테이션
    public void placeBid(User user, Long itemId, BigDecimal bidAmount) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 아이템을 찾을 수 없습니다: " + itemId));

        if (!"active".equals(item.getItemBidStatus())) {
            throw new RuntimeException("경매가 진행 중이 아닙니다.");
        }
        // 기존 입찰자 중 최고 입찰액보다 높은 경우에만 입찰
        BigDecimal highestBid = item.getBids().stream()
                .map(Bid::getBidAmount)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        if (bidAmount.compareTo(highestBid) > 0&& bidAmount.compareTo(item.getStartingBid()) >= 0) {
            // 입찰 처리
            Bid bid = new Bid();
            bid.setUser(user);
            bid.setItem(item);
            bid.setBidAmount(bidAmount);
            bid.setBidTime(LocalDateTime.now());

            bidRepository.save(bid);
            // 웹소켓을 통해 실시간 입찰 정보 전송
            BidNotification notification = new BidNotification(user.getUserId(), bidAmount);
            messagingTemplate.convertAndSend("/topic/auction/" + itemId, notification);
        }
        else if (bidAmount.compareTo(item.getStartingBid()) < 0) {
            throw new RuntimeException("입찰 금액이 시작 가격보다 낮습니다.");
        }else {
            throw new RuntimeException("입찰 금액이 현재 최고 입찰가보다 낮습니다.");
        }
    }
}

package org.example.auctionflowserver.service;

import org.example.auctionflowserver.entity.Item;
import org.example.auctionflowserver.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuctionScheduler {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private AuctionService auctionService;

    @Scheduled(fixedRate = 60000)  // 1분마다 실행
    public void checkAuctionEnd() {
        List<Item> endingItems = itemRepository.findAllByAuctionEndTimeBeforeAndItemBidStatus(LocalDateTime.now(), "active");

        for (Item item : endingItems) {
            auctionService.endAuction(item.getItemId());
        }
    }
}
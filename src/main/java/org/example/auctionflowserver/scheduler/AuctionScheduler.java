package org.example.auctionflowserver.scheduler;

import org.example.auctionflowserver.entity.Item;
import org.example.auctionflowserver.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AuctionScheduler {

    @Autowired
    private ItemService itemService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Scheduled(cron = "0 * * * * ?")
    public void checkAuctionEndTimes() {
        List<Item> activeItems = itemService.getAllItems().stream()
                .filter(item -> {
                    if ("active".equals(item.getItemBidStatus()) && item.getAuctionEndTime() != null) {
                        return item.getAuctionEndTime().isBefore(LocalDateTime.now());
                    } else {
                        return false;
                    }
                })
                .toList();

        for (Item item : activeItems) {
            item.setItemBidStatus("end");
            itemService.saveItem(item);

            messagingTemplate.convertAndSend("/topic/auction/" + item.getItemId(), "경매가 종료되었습니다.");
        }
    }
}

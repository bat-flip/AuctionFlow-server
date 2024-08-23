package org.example.auctionflowserver.scheduler;

import org.example.auctionflowserver.entity.Item;
import org.example.auctionflowserver.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AuctionScheduler {

    @Autowired
    private ItemService itemService;

    @Scheduled(cron = "0 * * * * ?") // 매 분마다 실행
    public void checkAuctionEndTimes() {
        List<Item> activeItems = itemService.getAllItems().stream()
                .filter(item -> "active".equals(item.getItemBidStatus()) && item.getAuctionEndTime().isBefore(LocalDateTime.now()))
                .toList();

        for (Item item : activeItems) {
            item.setItemBidStatus("end");
            itemService.saveItem(item); // 상태 업데이트 및 저장
        }
    }
}

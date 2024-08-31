package org.example.auctionflowserver.scheduler;

import org.example.auctionflowserver.entity.Bid;
import org.example.auctionflowserver.entity.Item;
import org.example.auctionflowserver.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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
            Optional<Bid> highestBid = item.getBids().stream()
                    .max(Comparator.comparing(Bid::getBidAmount));

            item.setItemBidStatus("end");
            itemService.saveItem(item);

            if (highestBid.isPresent()) {
                String message = String.format("경매가 종료되었습니다. 최고 입찰자는 사용자 ID: %d, 입찰 금액: %s원입니다.",
                        highestBid.get().getUser().getUserId(),
                        highestBid.get().getBidAmount().toString());
                messagingTemplate.convertAndSend("/topic/auction/" + item.getItemId(), message);
            } else {
                messagingTemplate.convertAndSend("/topic/auction/" + item.getItemId(), "경매가 종료되었습니다. 입찰자가 없습니다.");
            }
        }

//        for (Item item : activeItems) {
//            item.setItemBidStatus("end");
//            itemService.saveItem(item);
//
//            messagingTemplate.convertAndSend("/topic/auction/" + item.getItemId(), "경매가 종료되었습니다.");
//        }
    }
}

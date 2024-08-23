package org.example.auctionflowserver.dto;

import java.math.BigDecimal;

public class BidNotification {
    private Long userId;
    private BigDecimal bidAmount;

    public BidNotification(Long userId, BigDecimal bidAmount) {
        this.userId = userId;
        this.bidAmount = bidAmount;
    }
}

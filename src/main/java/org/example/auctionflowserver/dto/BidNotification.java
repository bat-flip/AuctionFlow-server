package org.example.auctionflowserver.dto;

import java.math.BigDecimal;

public class BidNotification {
    private Long userId;
    private BigDecimal bidAmount;

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }
}

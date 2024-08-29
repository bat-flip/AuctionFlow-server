package org.example.auctionflowserver.dto;

public class ChatRoomRequest {
    private String buyerEmail; // 구매자의 이메일

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }
}


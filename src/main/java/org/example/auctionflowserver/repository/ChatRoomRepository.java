package org.example.auctionflowserver.repository;

import org.example.auctionflowserver.entity.ChatRoom;
import org.example.auctionflowserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findBySellerOrBuyer(User seller, User buyer);
}

package org.example.auctionflowserver.repository;

import org.example.auctionflowserver.entity.Bid;
import org.example.auctionflowserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByUser(User user);

    List<Bid> findByItem_ItemId(Long itemId);
}

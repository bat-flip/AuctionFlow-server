package org.example.auctionflowserver.repository;

import org.example.auctionflowserver.entity.Bid;
import org.example.auctionflowserver.entity.Item;
import org.example.auctionflowserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByUser(User user);
    @Query("SELECT i FROM Item i WHERE (i.title LIKE %:keyword% OR i.description LIKE %:keyword%) AND i.itemBidStatus = 'active' ORDER BY i.itemId DESC")
    List<Item> findByTitleOrDescription(@Param("keyword") String keyword);

}

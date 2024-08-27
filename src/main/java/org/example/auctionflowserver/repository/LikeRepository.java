package org.example.auctionflowserver.repository;

import org.example.auctionflowserver.entity.Item;
import org.example.auctionflowserver.entity.Like;
import org.example.auctionflowserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByUser(User user);

    Optional<Like> findByUserAndItem(User user, Item item);
}

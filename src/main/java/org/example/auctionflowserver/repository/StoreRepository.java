package org.example.auctionflowserver.repository;

import org.example.auctionflowserver.entity.Like;
import org.example.auctionflowserver.entity.Store;
import org.example.auctionflowserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Store findByUser(User user);
}

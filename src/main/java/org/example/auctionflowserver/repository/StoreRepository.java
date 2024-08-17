package org.example.auctionflowserver.repository;

import org.example.auctionflowserver.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}

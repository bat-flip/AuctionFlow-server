package org.example.auctionflowserver.repository;

import org.example.auctionflowserver.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}

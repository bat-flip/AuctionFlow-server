package org.example.auctionflowserver.repository;

import org.example.auctionflowserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);

}
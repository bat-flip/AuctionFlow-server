package org.example.auctionflowserver.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;
    private LocalDateTime sendAt;
    private Boolean isRead;

    @ManyToOne
    @JoinColumn(name = "send_to")
    private User sendTo;


}

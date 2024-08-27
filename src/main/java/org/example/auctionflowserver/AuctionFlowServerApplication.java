package org.example.auctionflowserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AuctionFlowServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctionFlowServerApplication.class, args);
    }

}

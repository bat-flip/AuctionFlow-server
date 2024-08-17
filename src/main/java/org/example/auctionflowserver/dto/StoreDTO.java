package org.example.auctionflowserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StoreDTO {
    private Long storeId;
    private String name;
    private String content;
    private int postcode;
    private String basicAddr;
    private String detailAddr;
    private Long userId;
}

package org.example.auctionflowserver.service;

import org.example.auctionflowserver.dto.ItemResponse;
import org.example.auctionflowserver.entity.Bid;
import org.example.auctionflowserver.entity.Item;
import org.example.auctionflowserver.entity.Like;
import org.example.auctionflowserver.entity.User;
import org.example.auctionflowserver.repository.BidRepository;
import org.example.auctionflowserver.repository.ItemRepository;
import org.example.auctionflowserver.repository.LikeRepository;
import org.example.auctionflowserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyListService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BidRepository bidRepository;

    public List<ItemResponse> getLikeList(User user){
        List<Like> likes = likeRepository.findByUser(user);
        return likes.stream()
                .map(like -> convertItemToItemResponse(like.getItem()))
                .collect(Collectors.toList());
    }

    public List<Item> getMyItemList(User user) {
        List<Bid> bids = bidRepository.findByUser(user);
        return bids.stream()
                .map(Bid::getItem)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Item> getSellList(User user) {
        return itemRepository.findByUser(user);
    }

    public ItemResponse convertItemToItemResponse(Item item) {
        ItemResponse itemResponse = new ItemResponse();
        itemResponse.setItemId(item.getItemId());
        itemResponse.setCategoryId(item.getCategory().getCategoryId());
        itemResponse.setUserId(item.getUser().getUserId());
        itemResponse.setProductImageUrls(item.getProductImages().stream()
                .map(itemImage -> itemImage.getImageUrl())
                .collect(Collectors.toList()));
        itemResponse.setTitle(item.getTitle());
        itemResponse.setProductStatus(item.getProductStatus());
        itemResponse.setDescription(item.getDescription());
        itemResponse.setStartingBid(item.getStartingBid());
        itemResponse.setCreatedAt(item.getCreatedAt());
        itemResponse.setUpdatedAt(item.getUpdatedAt());
        itemResponse.setAuctionEndTime(item.getAuctionEndTime());
        itemResponse.setItemBidStatus(item.getItemBidStatus());
        return itemResponse;
    }






}

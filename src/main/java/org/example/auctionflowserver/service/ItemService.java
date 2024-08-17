package org.example.auctionflowserver.service;

import org.example.auctionflowserver.dto.ItemCreateRequest;
import org.example.auctionflowserver.dto.ItemResponse;
import org.example.auctionflowserver.entity.Category;
import org.example.auctionflowserver.entity.Item;
import org.example.auctionflowserver.entity.ItemImage;
import org.example.auctionflowserver.entity.User;
import org.example.auctionflowserver.repository.CategoryRepository;
import org.example.auctionflowserver.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private S3Service s3Service;

    public ItemResponse registerItem(User user, ItemCreateRequest itemCreateRequest) {
        Item item = new Item();

        // 카테고리 설정
        Category category = categoryRepository.findById(itemCreateRequest.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + itemCreateRequest.getCategoryId()));
        item.setCategory(category);

        // 사용자 설정
        item.setUser(user);

        // 아이템 속성 설정
        item.setTitle(itemCreateRequest.getTitle());
        item.setProductStatus(itemCreateRequest.getProductStatus());
        item.setDescription(itemCreateRequest.getDescription());
        item.setStartingBid(itemCreateRequest.getStartingBid());
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        item.setAuctionEndTime(itemCreateRequest.getAuctionEndTime());
        item.setItemBidStatus(itemCreateRequest.getItemBidStatus());

        // 이미지 업로드 및 URL 설정
        List<String> imageUrls;
        try {
            imageUrls = s3Service.uploadFiles(itemCreateRequest.getProductImageFiles(), "item-images");
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload images", e);
        }

        // 이미지 엔티티 생성
        List<ItemImage> itemImages = itemCreateRequest.getProductImageFiles().stream()
                .map(file -> {
                    String imageUrl = imageUrls.get(itemCreateRequest.getProductImageFiles().indexOf(file));
                    ItemImage itemImage = new ItemImage();
                    itemImage.setImageUrl(imageUrl);
                    itemImage.setItem(item);
                    return itemImage;
                })
                .collect(Collectors.toList());

        item.setProductImages(itemImages);

        // 아이템 저장
        Item savedItem = itemRepository.save(item);
        return mapToItemResponse(savedItem);
    }

    public ItemResponse getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + itemId));
        return mapToItemResponse(item);
    }

    // 모든 아이템을 조회하는 메서드 추가
    public List<ItemResponse> getAllItems() {
        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());
    }

    private ItemResponse mapToItemResponse(Item item) {
        ItemResponse response = new ItemResponse();
        response.setItemId(item.getItemId());
        response.setCategoryId(item.getCategory().getCategoryId());
        response.setUserId(item.getUser().getUserId());
        response.setProductImageUrls(item.getProductImages().stream()
                .map(ItemImage::getImageUrl)
                .collect(Collectors.toList()));
        response.setTitle(item.getTitle());
        response.setProductStatus(item.getProductStatus());
        response.setDescription(item.getDescription());
        response.setStartingBid(item.getStartingBid());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        response.setAuctionEndTime(item.getAuctionEndTime());
        response.setItemBidStatus(item.getItemBidStatus());
        return response;
    }
}

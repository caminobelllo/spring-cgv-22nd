package com.ceos22.cgv_clone.domain.store.service;

import com.ceos22.cgv_clone.domain.member.entity.Member;
import com.ceos22.cgv_clone.domain.member.repository.MemberRepository;
import com.ceos22.cgv_clone.domain.store.dto.ProductOrderDetailDto;
import com.ceos22.cgv_clone.domain.store.dto.ProductOrderRequestDto;
import com.ceos22.cgv_clone.domain.store.dto.ProductOrderResponseDto;
import com.ceos22.cgv_clone.domain.store.dto.ProductOrderSummaryDto;
import com.ceos22.cgv_clone.domain.store.entity.Product;
import com.ceos22.cgv_clone.domain.store.entity.ProductOrder;
import com.ceos22.cgv_clone.domain.store.entity.ProductOrderItem;
import com.ceos22.cgv_clone.domain.store.repository.InventoryRepository;
import com.ceos22.cgv_clone.domain.store.repository.ProductOrderItemRepository;
import com.ceos22.cgv_clone.domain.store.repository.ProductOrderRepository;
import com.ceos22.cgv_clone.domain.store.repository.ProductRepository;
import com.ceos22.cgv_clone.domain.theater.entity.Cinema;
import com.ceos22.cgv_clone.domain.theater.repository.CinemaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductOrderQueryService {

    private final ProductOrderRepository productOrderRepository;
    private final ProductOrderItemRepository productOrderItemRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final MemberRepository memberRepository;
    private final CinemaRepository cinemaRepository;

    /** 주문 생성 + 응답 반환 */
    @Transactional
    public ProductOrderResponseDto createOrder(ProductOrderRequestDto req) {
        // 1) 기본 로딩
        Member member = memberRepository.findById(req.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        Cinema cinema = cinemaRepository.findById(req.getCinemaId())
                .orElseThrow(() -> new IllegalArgumentException("Cinema not found"));

        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("items required");
        }

        // 쿼리 한 번에 조회되도록
        List<Long> productIds = req.getItems().stream()
                .map(ProductOrderRequestDto.OrderItem::getProductId)
                .toList();

        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        // 요청된 상품 ID 중 DB에 없는 것이 있는지 확인
        if (productMap.size() != productIds.size()) {
            throw new IllegalArgumentException("Some products not found");
        }

        // 2) 총액 계산 & 응답 아이템 준비
        int totalPrice = 0;
        List<ProductOrderResponseDto.OrderItem> responseItems = new ArrayList<>();

        for (ProductOrderRequestDto.OrderItem item : req.getItems()) {
            Product product = productMap.get(item.getProductId());
            totalPrice += product.getPrice() * item.getQuantity();

            responseItems.add(ProductOrderResponseDto.OrderItem.builder()
                    .productName(product.getName())
                    .quantity(item.getQuantity())
                    .price(product.getPrice()) // 단가 스냅샷
                    .build());
        }


        // 3) 주문 헤더 생성/저장 (엔티티에 정적 팩토리 있어야 함)
        ProductOrder order = ProductOrder.create(member, cinema, totalPrice);
        productOrderRepository.save(order);

        // 4) 재고 차감 + 주문 라인 생성/저장
        List<ProductOrderItem> lines = new ArrayList<>();
        for (ProductOrderRequestDto.OrderItem item : req.getItems()) {
            Product product = productMap.get(item.getProductId());

            // 재고 차감
            int updatedRows = inventoryRepository.decrement(cinema.getId(), product.getId(), item.getQuantity());
            if (updatedRows != 1) {
                // 재고 부족 시 트랜잭션이 롤백되므로 이미 차감된 다른 상품 재고도 원상 복구됩니다.
                throw new IllegalStateException("Out of stock for product: " + product.getName());
            }

            lines.add(ProductOrderItem.of(order, product, item.getQuantity()));
        }
        productOrderItemRepository.saveAll(lines);


        // 5) 응답 DTO
        return ProductOrderResponseDto.builder()
                .orderId(order.getId())
                .memberId(member.getId())
                .cinemaId(cinema.getId())
                .totalPrice(totalPrice)
                .items(responseItems)
                .build();
    }

    public List<ProductOrderSummaryDto> getOrdersByMember(Long memberId) {
        return productOrderRepository.findByMemberIdOrderByIdDesc(memberId)
                .stream()
                .map(o -> new ProductOrderSummaryDto(
                        o.getId(),
                        o.getCinema().getId(),
                        o.getTotalPrice(),
                        o.getCreatedAt()
                ))
                .toList();
    }

    public ProductOrderDetailDto getOrder(Long orderId) {
        ProductOrder order = productOrderRepository.findDetailsById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        List<ProductOrderItem> items = productOrderItemRepository.findByProductOrderId(orderId);

        return new ProductOrderDetailDto(
                order.getId(),
                order.getMember().getId(),
                order.getCinema().getId(),
                order.getTotalPrice(),
                items.stream()
                        .map(i -> ProductOrderResponseDto.OrderItem.builder()
                                .productName(i.getProduct().getName())
                                .quantity(i.getOrderQuantity())
                                .price(i.getProduct().getPrice())
                                .build())
                        .toList()
        );
    }
}

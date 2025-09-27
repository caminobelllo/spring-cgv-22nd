package com.ceos22.cgv_clone.domain.store.controller;

import com.ceos22.cgv_clone.domain.store.dto.ProductOrderDetailDto;
import com.ceos22.cgv_clone.domain.store.dto.ProductOrderRequestDto;
import com.ceos22.cgv_clone.domain.store.dto.ProductOrderResponseDto;
import com.ceos22.cgv_clone.domain.store.dto.ProductOrderSummaryDto;
import com.ceos22.cgv_clone.domain.store.service.ProductOrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductOrderQueryController {

    private final ProductOrderQueryService productOrderService;

    // 주문
    @PostMapping("/store/orders")
    public ResponseEntity<ProductOrderResponseDto> create(@RequestBody ProductOrderRequestDto req) {
        return ResponseEntity.ok(productOrderService.createOrder(req));
    }

    // 회원의 주문 목록
    @GetMapping("/store/orders")
    public List<ProductOrderSummaryDto> list(@RequestParam Long memberId) {
        return productOrderService.getOrdersByMember(memberId);
    }

    // 주문 단건 상세
    @GetMapping("/store/orders/{orderId}")
    public ProductOrderDetailDto detail(@PathVariable Long orderId) {
        return productOrderService.getOrder(orderId);
    }
}

package com.ceos22.cgv_clone.domain.store.controller;

import com.ceos22.cgv_clone.domain.store.dto.ProductOrderDetailDto;
import com.ceos22.cgv_clone.domain.store.dto.ProductOrderRequestDto;
import com.ceos22.cgv_clone.domain.store.dto.ProductOrderResponseDto;
import com.ceos22.cgv_clone.domain.store.dto.ProductOrderSummaryDto;
import com.ceos22.cgv_clone.domain.store.service.ProductOrderQueryService;
import com.ceos22.cgv_clone.global.apiPayload.CustomResponse;
import com.ceos22.cgv_clone.global.apiPayload.code.success.SuccessCode;
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
    public CustomResponse<ProductOrderResponseDto> create(@RequestBody ProductOrderRequestDto req) {
        return CustomResponse.onSuccess(SuccessCode.CREATED, productOrderService.createOrder(req));
    }

    // 회원의 주문 목록
    @GetMapping("/store/orders")
    public CustomResponse<List<ProductOrderSummaryDto>> list(@RequestParam Long memberId) {
        return CustomResponse.onSuccess(SuccessCode.OK, productOrderService.getOrdersByMember(memberId));
    }

    // 주문 단건 상세
    @GetMapping("/store/orders/{orderId}")
    public CustomResponse<ProductOrderDetailDto> detail(@PathVariable Long orderId) {
        return CustomResponse.onSuccess(SuccessCode.OK, productOrderService.getOrder(orderId));
    }
}

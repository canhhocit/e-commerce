package sv.project.e_commerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sv.project.e_commerce.dto.request.OrderRequest;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.dto.response.OrderResponse;
import sv.project.e_commerce.model.entity.User;
import sv.project.e_commerce.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Đơn hàng", description = "Quản lý đơn hàng")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

        OrderService orderService;

        @PostMapping
        @Operation(summary = "Đặt hàng từ giỏ hàng")
        public ApiResponse<OrderResponse> createOrder(
                        @AuthenticationPrincipal User user,
                        @Valid @RequestBody OrderRequest request) {
                return ApiResponse.<OrderResponse>builder()
                                .result(orderService.createOrder(user, request))
                                .build();
        }

        @GetMapping
        @Operation(summary = "Xem lịch sử đơn hàng của tôi")
        public ApiResponse<List<OrderResponse>> getMyOrders(
                        @AuthenticationPrincipal User user) {
                return ApiResponse.<List<OrderResponse>>builder()
                                .result(orderService.getUserOrders(user))
                                .build();
        }

        @GetMapping("/{id}")
        @Operation(summary = "Xem chi tiết đơn hàng")
        public ApiResponse<OrderResponse> getOrder(
                        @AuthenticationPrincipal User user,
                        @PathVariable Long id) {
                return ApiResponse.<OrderResponse>builder()
                                .result(orderService.getOrderById(user, id))
                                .build();
        }
}

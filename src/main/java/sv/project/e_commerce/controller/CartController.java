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
import sv.project.e_commerce.dto.request.CartItemRequest;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.model.entity.Cart;
import sv.project.e_commerce.model.entity.User;
import sv.project.e_commerce.service.CartService;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Giỏ hàng", description = "Quản lý giỏ hàng")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

        CartService cartService;

        @GetMapping
        @Operation(summary = "Xem giỏ hàng hiện tại")
        public ApiResponse<Cart> getCart(
                        @AuthenticationPrincipal User user) {
                return ApiResponse.<Cart>builder()
                                .result(cartService.getCart(user))
                                .build();
        }

        @PostMapping("/add")
        @Operation(summary = "Thêm sản phẩm vào giỏ hàng")
        public ApiResponse<Void> addToCart(
                        @AuthenticationPrincipal User user,
                        @Valid @RequestBody CartItemRequest request) {
                cartService.addItemToCart(user, request.getProductId(), request.getQuantity());
                return ApiResponse.<Void>builder()
                                .message("Thêm vào giỏ hàng thành công")
                                .build();
        }

        @DeleteMapping("/remove/{productId}")
        @Operation(summary = "Xoá sản phẩm khỏi giỏ hàng")
        public ApiResponse<Void> removeFromCart(
                        @AuthenticationPrincipal User user,
                        @PathVariable Long productId) {
                cartService.removeProductFromCart(user, productId);
                return ApiResponse.<Void>builder()
                                .message("Đã xoá khỏi giỏ hàng")
                                .build();
        }
}
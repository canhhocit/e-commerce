// package sv.project.e_commerce.controller;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.security.SecurityRequirement;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.*;
// import sv.project.e_commerce.dto.ApiResponse;
// import sv.project.e_commerce.dto.CartItemRequest;
// import sv.project.e_commerce.entity.Cart;
// import sv.project.e_commerce.entity.User;
// import sv.project.e_commerce.service.CartService;

// @RestController
// @RequestMapping("/api/v1/cart")
// @RequiredArgsConstructor
// @Tag(name = "Cart", description = "Quản lý giỏ hàng")
// @SecurityRequirement(name = "bearerAuth")
// public class CartController {

//     private final CartService cartService;

//     @GetMapping
//     @Operation(summary = "Xem giỏ hàng hiện tại")
//     public ResponseEntity<ApiResponse<Cart>> getCart(
//             @AuthenticationPrincipal User user) {
//         Cart cart = cartService.getCart(user);
//         return ResponseEntity.ok(ApiResponse.success(cart));
//     }

//     @PostMapping("/add")
//     @Operation(summary = "Thêm sản phẩm vào giỏ hàng")
//     public ResponseEntity<ApiResponse<Void>> addToCart(
//             @AuthenticationPrincipal User user,
//             @Valid @RequestBody CartItemRequest request) {
//         cartService.addItemToCart(user, request.getProductId(), request.getQuantity());
//         return ResponseEntity.ok(ApiResponse.success("Thêm vào giỏ hàng thành công", null));
//     }

//     @DeleteMapping("/remove/{productId}")
//     @Operation(summary = "Xoá sản phẩm khỏi giỏ hàng")
//     public ResponseEntity<ApiResponse<Void>> removeFromCart(
//             @AuthenticationPrincipal User user,
//             @PathVariable Long productId) {
//         cartService.removeProductFromCart(user, productId);
//         return ResponseEntity.ok(ApiResponse.success("Đã xoá khỏi giỏ hàng", null));
//     }
// }
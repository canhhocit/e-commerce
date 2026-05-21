package sv.project.e_commerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import sv.project.e_commerce.dto.request.OrderRequest;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.dto.response.OrderResponse;
import sv.project.e_commerce.exception.AppException;
import sv.project.e_commerce.exception.ErrorCode;
import sv.project.e_commerce.model.entity.User;
import sv.project.e_commerce.repository.UserRepository;
import sv.project.e_commerce.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController

@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Đơn hàng", description = "Quản lý đơn hàng")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

        OrderService orderService;
        UserRepository userRepository;

        private User getUser(Jwt jwt) {
                return userRepository.findByUsername(jwt.getSubject())
                                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        }

        @PostMapping
        @Operation(summary = "Đặt hàng từ giỏ hàng")
        public ApiResponse<OrderResponse> createOrder(
                        @AuthenticationPrincipal Jwt jwt,
                        @Valid @RequestBody OrderRequest request) {
                return ApiResponse.<OrderResponse>builder()
                                .result(orderService.createOrder(getUser(jwt), request))
                                .build();
        }

        @GetMapping
        @Operation(summary = "Xem lịch sử đơn hàng")
        public ApiResponse<List<OrderResponse>> getMyOrders(
                        @AuthenticationPrincipal Jwt jwt) {
                return ApiResponse.<List<OrderResponse>>builder()
                                .result(orderService.getUserOrders(getUser(jwt)))
                                .build();
        }

        @GetMapping("/{id}")
        @Operation(summary = "Xem chi tiết đơn hàng")
        public ApiResponse<OrderResponse> getOrder(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long id) {
                return ApiResponse.<OrderResponse>builder()
                                .result(orderService.getOrderById(getUser(jwt), id))
                                .build();
        }

        // Admin endpoints
        @GetMapping("/all")
        @Operation(summary = "Xem tất cả đơn hàng (Admin)")
        public ApiResponse<List<OrderResponse>> getAllOrders() {
                return ApiResponse.<List<OrderResponse>>builder()
                                .result(orderService.getAllOrders())
                                .build();
        }

        @PutMapping("/{id}/status")
        @Operation(summary = "Cập nhật trạng thái đơn hàng (Admin)")
        public ApiResponse<OrderResponse> updateOrderStatus(
                        @PathVariable Long id,
                        @RequestParam String status) {
                return ApiResponse.<OrderResponse>builder()
                                .result(orderService.updateOrderStatus(id,
                                                sv.project.e_commerce.model.entity.OrderStatus.valueOf(status)))
                                .build();
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Xóa đơn hàng (Admin)")
        public ApiResponse<String> deleteOrder(@PathVariable Long id) {
                orderService.deleteOrder(id);
                return ApiResponse.<String>builder()
                                .result("Đã xóa đơn hàng thành công")
                                .build();
        }

        @PostMapping("/{id}/pay")
        @Operation(summary = "Thanh toán giả lập cho đơn hàng")
        public ApiResponse<OrderResponse> payOrder(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long id) {
                return ApiResponse.<OrderResponse>builder()
                                .result(orderService.payOrder(getUser(jwt), id))
                                .build();
        }

        @GetMapping("/{id}/invoice")
        @Operation(summary = "Tải hóa đơn PDF của đơn hàng")
        public ResponseEntity<byte[]> downloadInvoice(
                        @AuthenticationPrincipal Jwt jwt,
                        @PathVariable Long id) {
                byte[] pdfBytes = orderService.generateInvoicePdf(getUser(jwt), id);
                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice_" + id + ".pdf")
                                .contentType(MediaType.APPLICATION_PDF)
                                .body(pdfBytes);
        }
}


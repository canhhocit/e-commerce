package sv.project.e_commerce.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.dto.response.ProductResponse;
import sv.project.e_commerce.exception.AppException;
import sv.project.e_commerce.exception.ErrorCode;
import sv.project.e_commerce.model.entity.User;
import sv.project.e_commerce.repository.UserRepository;
import sv.project.e_commerce.service.AIService;
import sv.project.e_commerce.service.ProductService;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Phân tích khuôn mặt AI", description = "APIs phân tích dáng khuôn mặt và gợi ý sản phẩm phù hợp")
@SecurityRequirement(name = "bearerAuth")
public class AIController {

    AIService aiService;
    ProductService productService;
    UserRepository userRepository;

    private User getUser(Jwt jwt) {
        return userRepository.findByUsername(jwt.getSubject())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @PostMapping(value = "/analyze", consumes = "multipart/form-data")
    @Operation(summary = "Tải ảnh lên và phân tích dáng khuôn mặt", description = "Trả về dáng khuôn mặt ROUND, OVAL, SQUARE hoặc HEART và các thông số chi tiết")
    public ApiResponse<java.util.Map<String, Object>> analyzeFace(
            @AuthenticationPrincipal Jwt jwt,
            @RequestPart("image") MultipartFile image,
            @RequestPart(value = "imageLeft", required = false) MultipartFile imageLeft,
            @RequestPart(value = "imageRight", required = false) MultipartFile imageRight) {
        
        java.util.Map<String, Object> result = aiService.analyzeFaceForUser(getUser(jwt), image, imageLeft, imageRight);
        return ApiResponse.<java.util.Map<String, Object>>builder()
                .result(result)
                .build();
    }

    @GetMapping("/recommend")
    @Operation(summary = "Gợi ý sản phẩm tóc giả phù hợp dựa trên dáng khuôn mặt hiện tại của User")
    public ApiResponse<Page<ProductResponse>> getRecommendations(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        User user = getUser(jwt);
        String faceShape = user.getFaceShape();
        
        if (faceShape == null || faceShape.trim().isEmpty()) {
            throw new AppException(ErrorCode.KEY_INVALID);
        }

        Page<ProductResponse> products = productService.getProducts(page, size, "id", "DESC", null, null, faceShape, false);
        return ApiResponse.<Page<ProductResponse>>builder()
                .result(products)
                .build();
    }
}

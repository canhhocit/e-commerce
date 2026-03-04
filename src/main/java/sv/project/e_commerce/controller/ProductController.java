package sv.project.e_commerce.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import sv.project.e_commerce.dto.request.ProductCreateRequest;
import sv.project.e_commerce.dto.request.ProductUpdateRequest;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.dto.response.ProductResponse;
import sv.project.e_commerce.service.ProductService;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/products")
@Tag(name = "Sản phẩm", description = "Các endpoint quản lý sản phẩm, bao gồm cả xử lý hình ảnh")
public class ProductController {
        ProductService productService;

        // Add
        @Operation(summary = "Thêm sản phẩm", description = "Tạo một sản phẩm mới với hình ảnh tùy chọn")
        @SecurityRequirement(name = "bearerAuth")
        @PostMapping(consumes = "multipart/form-data")
        public ApiResponse<ProductResponse> addProduct(
                        @RequestPart("product") @Valid ProductCreateRequest request,
                        @RequestPart(value = "image", required = false) MultipartFile image) {
                return ApiResponse.<ProductResponse>builder()
                                .result(productService.addProduct(request, image))
                                .build();
        }

        // findAll
        @Operation(summary = "Lấy tất cả sản phẩm", description = "Lấy danh sách sản phẩm có phân trang, hỗ trợ lọc theo tên và danh mục")
        @GetMapping
        public ApiResponse<Page<ProductResponse>> getProducts(
                        @RequestParam(defaultValue = "1") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "id") String sortBy,
                        @RequestParam(defaultValue = "DESC") String direction,
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) Long categoryId) {
                return ApiResponse.<Page<ProductResponse>>builder()
                                .result(productService.getProducts(page, size, sortBy, direction, name, categoryId))
                                .build();
        }

        // findOne
        @Operation(summary = "Lấy sản phẩm theo ID", description = "Lấy thông tin chi tiết một sản phẩm theo ID")
        @GetMapping("/{id}")
        public ApiResponse<ProductResponse> getProduct(@PathVariable Long id) {
                return ApiResponse.<ProductResponse>builder()
                                .result(productService.getProductById(id))
                                .build();
        }

        // update
        @Operation(summary = "Cập nhật sản phẩm", description = "Cập nhật thông tin và/hoặc hình ảnh của một sản phẩm hiện có")
        @SecurityRequirement(name = "bearerAuth")
        @PutMapping(value = "/{id}", consumes = "multipart/form-data")
        public ApiResponse<ProductResponse> updateProduct(
                        @PathVariable Long id,
                        @RequestPart("product") @Valid ProductUpdateRequest request,
                        @RequestPart(value = "image", required = false) MultipartFile image) {
                return ApiResponse.<ProductResponse>builder()
                                .result(productService.updateProduct(id, request, image))
                                .build();
        }

        // delete
        @Operation(summary = "Xóa sản phẩm", description = "Xóa một sản phẩm theo ID")
        @SecurityRequirement(name = "bearerAuth")
        @DeleteMapping("/{id}")
        public ApiResponse<String> deleteProduct(@PathVariable Long id) {
                return ApiResponse.<String>builder()
                                .result(productService.deleteProduct(id))
                                .build();
        }
}
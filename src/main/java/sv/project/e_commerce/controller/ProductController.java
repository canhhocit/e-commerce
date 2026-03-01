package sv.project.e_commerce.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
public class ProductController {
    ProductService productService;

    // Add
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<ProductResponse> addProduct(
            @RequestPart("product") @Valid ProductCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.addProduct(request, image))
                .build();
    }

    // findAll
    @GetMapping
    public ApiResponse<List<ProductResponse>> getProducts() {
        return ApiResponse.<List<ProductResponse>>builder()
                .result(productService.getProducts())
                .build();
    }

    // findOne
    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> getProduct(@PathVariable Long id) {
        return ApiResponse.<ProductResponse>builder()
                .result(productService.getProductById(id))
                .build();
    }

    // update
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
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Long id) {
        return ApiResponse.<String>builder()
                .result(productService.deleteProduct(id))
                .build();
    }
}
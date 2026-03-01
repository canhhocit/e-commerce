package sv.project.e_commerce.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import sv.project.e_commerce.dto.request.CategoryRequest;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.dto.response.CategoryResponse;
import sv.project.e_commerce.service.CategoryService;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/categories")
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    public ApiResponse<CategoryResponse> createCategory(@RequestBody @Valid CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.addCategory(request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getCategories() {
        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categoryService.getCategories())
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getCategory(@PathVariable Long id) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.getCategoryById(id))
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryRequest request) {
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryService.upateCategory(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCategory(@PathVariable Long id) {
        return ApiResponse.<String>builder()
                .result(categoryService.deleteCategory(id))
                .build();
    }
}
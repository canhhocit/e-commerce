package sv.project.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sv.project.e_commerce.dto.request.CategoryRequest;
import sv.project.e_commerce.dto.response.ApiResponse;
import sv.project.e_commerce.dto.response.CategoryResponse;
import sv.project.e_commerce.exception.AppException;
import sv.project.e_commerce.exception.ErrorCode;
import sv.project.e_commerce.mapper.CategoryMapper;
import sv.project.e_commerce.model.entity.Category;
import sv.project.e_commerce.repository.CategoryRepository;

@Service
@RequiredArgsConstructor

public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    // C
    public ApiResponse<CategoryResponse> createCategory(CategoryRequest request) {
        if(categoryRepository.existsByNameAndEnabledTrue(request.getName())){
                throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        Category category = categoryMapper.toCategory(request);
        category.setEnabled(true);
        return ApiResponse.<CategoryResponse>builder()
                .result(categoryMapper.toCategotyResponse(categoryRepository.save(category)))
                .build();
    }

    // R
    // 1
    public ApiResponse<CategoryResponse> getCategoryById(Long id) {
        Category category = categoryRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return ApiResponse.<CategoryResponse>builder()
                .result(categoryMapper.toCategotyResponse(category))
                .build();
    }

    // n
    public ApiResponse<List<CategoryResponse>> getCategories() {
        List<CategoryResponse> categories = categoryRepository.findAllByEnabledTrue().stream()
                .map(categoryMapper::toCategotyResponse).toList();

        return ApiResponse.<List<CategoryResponse>>builder()
                .result(categories)
                .build();
    }

    // U
    public ApiResponse<CategoryResponse> upateCategory(Long id, CategoryRequest request) {
        Optional<Category> categoryByName = categoryRepository.findByNameAndEnabledTrue(request.getName());
        if (categoryByName.isPresent() && !categoryByName.get().getId().equals(id)) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        Category currCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryMapper.updateCategory(currCategory, request);

        return ApiResponse.<CategoryResponse>builder()
                .result(categoryMapper.toCategotyResponse(categoryRepository.save(currCategory)))
                .build();
    }

    // D
    public ApiResponse<String> deleteCategory(Long id) {
        Category currcategory = categoryRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        currcategory.setEnabled(false);
        categoryRepository.save(currcategory);
        return ApiResponse.<String>builder().result("'" + currcategory.getName() + "' has been deleted!")
                .build();
    }

}

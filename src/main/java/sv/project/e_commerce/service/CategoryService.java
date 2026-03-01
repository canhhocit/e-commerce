package sv.project.e_commerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import sv.project.e_commerce.dto.request.CategoryRequest;
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

    // add
    public CategoryResponse addCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameAndEnabledTrue(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        Category category = categoryMapper.toCategory(request);
        category.setEnabled(true);
        return categoryMapper.toCategotyResponse(categoryRepository.save(category));
    }

    // findOne
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryMapper.toCategotyResponse(category);
    }

    // findAll
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAllByEnabledTrue().stream()
                .map(categoryMapper::toCategotyResponse).toList();
    }

    // update
    public CategoryResponse upateCategory(Long id, CategoryRequest request) {
        Optional<Category> categoryByName = categoryRepository.findByNameAndEnabledTrue(request.getName());
        if (categoryByName.isPresent() && !categoryByName.get().getId().equals(id)) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        Category currCategory = categoryRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        categoryMapper.updateCategory(currCategory, request);
        return categoryMapper.toCategotyResponse(categoryRepository.save(currCategory));
    }

    // delete
    public String deleteCategory(Long id) {
        Category currcategory = categoryRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        currcategory.setEnabled(false);
        categoryRepository.save(currcategory);
        return "'" + currcategory.getName() + "' has been deleted!";
    }
}
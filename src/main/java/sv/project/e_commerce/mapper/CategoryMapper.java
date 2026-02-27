package sv.project.e_commerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import sv.project.e_commerce.dto.request.CategoryRequest;
import sv.project.e_commerce.dto.response.CategoryResponse;
import sv.project.e_commerce.model.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    Category toCategory(CategoryRequest request);

    CategoryResponse toCategotyResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    void updateCategory(@MappingTarget Category category, CategoryRequest request);
}

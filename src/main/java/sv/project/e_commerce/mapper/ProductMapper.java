package sv.project.e_commerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import sv.project.e_commerce.dto.request.ProductCreateRequest;
import sv.project.e_commerce.dto.request.ProductUpdateRequest;
import sv.project.e_commerce.dto.response.ProductResponse;
import sv.project.e_commerce.model.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    Product toProduct(ProductCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);
    
    @Mapping(source = "category.id", target = "categoryId")
    ProductResponse toProductResponse(Product product);
}

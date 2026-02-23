package sv.project.e_commerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sv.project.e_commerce.dto.ProductDTO;
import sv.project.e_commerce.dto.ProductRequest;
import sv.project.e_commerce.model.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    ProductDTO toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "active", constant = "true")
    Product toEntity(ProductRequest request);
}

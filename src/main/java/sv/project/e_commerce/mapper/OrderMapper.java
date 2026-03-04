package sv.project.e_commerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import sv.project.e_commerce.dto.response.OrderItemResponse;
import sv.project.e_commerce.dto.response.OrderResponse;
import sv.project.e_commerce.model.entity.Order;
import sv.project.e_commerce.model.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "items", source = "items")
    OrderResponse toOrderResponse(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}

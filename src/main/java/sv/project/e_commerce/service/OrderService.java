package sv.project.e_commerce.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.project.e_commerce.dto.request.OrderRequest;
import sv.project.e_commerce.dto.response.OrderResponse;
import sv.project.e_commerce.exception.AppException;
import sv.project.e_commerce.exception.ErrorCode;
import sv.project.e_commerce.mapper.OrderMapper;
import sv.project.e_commerce.model.entity.*;
import sv.project.e_commerce.repository.OrderRepository;
import sv.project.e_commerce.repository.ProductRepository;
import sv.project.e_commerce.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderService {
    OrderRepository orderRepository;
    CartService cartService;
    OrderMapper orderMapper;
    ProductRepository productRepository;
    UserRepository userRepository;

    @Transactional
    public OrderResponse createOrder(User user, OrderRequest request) {
        User dbUser = userRepository.findByIdAndEnabledTrue(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart cart = cartService.getCart(dbUser);
        if (cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            orderItems.add(orderItem);
            totalAmount += orderItem.getPrice() * orderItem.getQuantity();

            // Update product stock
            product.setStock(product.getStock() - orderItem.getQuantity());
            productRepository.save(product);
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);

        Order savedOrder = orderRepository.save(order);

        // Clear cart after order
        cartService.clearCart(user);

        return orderMapper.toOrderResponse(savedOrder);
    }

    public List<OrderResponse> getUserOrders(User user) {
        return orderRepository.findByUser(user).stream()
                .map(orderMapper::toOrderResponse)
                .toList();
    }

    public OrderResponse getOrderById(User user, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        // Basic check to ensure user owns the order or is admin
        if (!order.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return orderMapper.toOrderResponse(order);
    }
}

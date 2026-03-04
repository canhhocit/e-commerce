package sv.project.e_commerce.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.project.e_commerce.exception.AppException;
import sv.project.e_commerce.exception.ErrorCode;
import sv.project.e_commerce.model.entity.Cart;
import sv.project.e_commerce.model.entity.CartItem;
import sv.project.e_commerce.model.entity.Product;
import sv.project.e_commerce.model.entity.User;
import sv.project.e_commerce.repository.CartItemRepository;
import sv.project.e_commerce.repository.CartRepository;
import sv.project.e_commerce.repository.ProductRepository;
import sv.project.e_commerce.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    CartRepository cartRepository;
    CartItemRepository cartItemRepository;
    ProductRepository productRepository;
    UserRepository userRepository;

    public Cart getCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setItems(new ArrayList<>());
                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public void addItemToCart(User user, Long productId, Integer quantity) {
        // Double check user status
        User dbUser = userRepository.findByIdAndEnabledTrue(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_DISABLED));

        Product product = productRepository.findByIdAndActiveTrue(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Cart cart = getCart(dbUser);

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }

    @Transactional
    public void removeProductFromCart(User user, Long productId) {
        User dbUser = userRepository.findByIdAndEnabledTrue(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_DISABLED));

        Cart cart = cartRepository.findByUser(dbUser)
                .orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION)); // Should have a cart

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(User user) {
        Cart cart = getCart(user);
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
    }
}

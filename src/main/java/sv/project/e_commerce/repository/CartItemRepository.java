package sv.project.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.project.e_commerce.model.entity.Cart;
import sv.project.e_commerce.model.entity.CartItem;
import sv.project.e_commerce.model.entity.Product;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}

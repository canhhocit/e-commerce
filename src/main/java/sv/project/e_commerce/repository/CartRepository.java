package sv.project.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.project.e_commerce.model.entity.Cart;
import sv.project.e_commerce.model.entity.User;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}

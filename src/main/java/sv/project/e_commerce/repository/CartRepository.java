package sv.project.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import sv.project.e_commerce.model.entity.Cart;
import sv.project.e_commerce.model.entity.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}

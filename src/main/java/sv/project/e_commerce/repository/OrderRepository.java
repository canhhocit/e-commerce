package sv.project.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import sv.project.e_commerce.model.entity.Order;
import sv.project.e_commerce.model.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}

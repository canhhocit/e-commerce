package sv.project.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.project.e_commerce.model.entity.Order;
import sv.project.e_commerce.model.entity.User;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}

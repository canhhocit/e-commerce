package sv.project.e_commerce.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sv.project.e_commerce.model.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.active = true")
    Page<Product> searchByNameAndActiveTrue(@Param("name") String name, Pageable pageable);

    Page<Product> findByActiveTrue(Pageable pageable);

    Optional<Product> findByIdAndActiveTrue(Long id);

    List<Product> findAllByActiveTrue();

    boolean existsByNameAndActiveTrue(String name);

}

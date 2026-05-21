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

    Page<Product> findByCategoryIdAndActiveTrue(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) AND p.active = true")
    Page<Product> searchByCategoryIdAndNameAndActiveTrue(@Param("categoryId") Long categoryId,
            @Param("name") String name, Pageable pageable);

    boolean existsByNameAndActiveTrue(String name);

    // Filter by BOTH active and in-stock (used for USER side)
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stock > 0")
    Page<Product> findForUser(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stock > 0 AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> searchByNameForUser(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stock > 0 AND p.category.id = :categoryId")
    Page<Product> findByCategoryIdForUser(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stock > 0 AND p.category.id = :categoryId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> searchByCategoryIdAndNameForUser(@Param("categoryId") Long categoryId, @Param("name") String name,
            Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stock > 0 AND LOWER(p.suitableFaceShapes) LIKE LOWER(CONCAT('%', :faceShape, '%'))")
    Page<Product> findByFaceShapeForUser(@Param("faceShape") String faceShape, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stock > 0 AND LOWER(p.suitableFaceShapes) LIKE LOWER(CONCAT('%', :faceShape, '%'))")
    List<Product> findByFaceShapeForUserList(@Param("faceShape") String faceShape);
}


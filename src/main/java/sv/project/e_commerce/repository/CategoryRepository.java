package sv.project.e_commerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.project.e_commerce.model.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByNameAndEnabledTrue(String name);

    boolean existsByNameAndEnabledTrue(String name);

    Optional<Category> findByIdAndEnabledTrue(Long id);

    List<Category> findAllByEnabledTrue();
}

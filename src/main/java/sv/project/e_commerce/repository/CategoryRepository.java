package sv.project.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.project.e_commerce.model.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}

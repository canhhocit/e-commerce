package sv.project.e_commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sv.project.e_commerce.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndEnabledTrue(String username);

    Optional<User> findByEmailAndEnabledTrue(String email);

    List<User> findAllByEnabledTrue();

    Optional<User> findByVerificationToken(String verificationToken);

    boolean existsByUsername(String username);

    boolean existsByEmailAndEnabledTrue(String email);
}

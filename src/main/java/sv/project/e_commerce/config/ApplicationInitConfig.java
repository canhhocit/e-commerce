package sv.project.e_commerce.config;


import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;



import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import sv.project.e_commerce.model.entity.User;
import sv.project.e_commerce.model.enums.Role;
import sv.project.e_commerce.repository.UserRepository;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

  PasswordEncoder passwordEncoder;

  @Bean
  @ConditionalOnProperty(
      prefix = "spring",
      value = "datasource.driver-class-name",
      havingValue = "com.mysql.cj.jdbc.Driver")
  ApplicationRunner applicationRunner(UserRepository userRepo) {
    log.info("CONFIG: Init Application");
    // dc khởi chạy mỗi khi đc start
    return args -> {
      if (userRepo.findByUsername("admin").isEmpty()) {
        User user =
            User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .fullName("ADMIN-MANAGERMENT")
                .role(Role.ADMIN)
                .build();
        userRepo.save(user);
        log.info("admin user has been created with default: (username,password) - (admin,admin) , please change it !");
      } else {
        log.info("Admin user already exists");
      }
    };
  }
}

package sv.project.e_commerce.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sv.project.e_commerce.model.enums.Role;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled;

    private String verificationToken;

    private String faceShape;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Cart cart;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orders;

}


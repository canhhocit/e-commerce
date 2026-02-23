package sv.project.e_commerce.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import sv.project.e_commerce.model.enums.Role;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String fullName;

    private String phone;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean enabled = true;

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

   
}

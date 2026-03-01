package sv.project.e_commerce.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    private Double price;

    private Integer stock;

    private String imageUrl;

    private Boolean active;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

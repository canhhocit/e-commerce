package sv.project.e_commerce.dto.request;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateRequest {

    @NotBlank(message = "PRODUCT_NAME_REQUIRED")
    @Size(min = 5, message = "PRODUCT_NAME_INVALID")
    private String name;

    private String description;

    @NotNull(message = "PRICE_REQUIRED")
    @Min(value = 0, message = "INVALID_PRICE")
    private Double price;

    @NotNull(message = "STOCK_REQUIRED")
    @Min(value = 0, message = "INVALID_STOCK")
    private Integer stock;

    private MultipartFile image;

    @NotNull(message = "CATEGORY_ID_REQUIRED")
    private Long categoryId;
}

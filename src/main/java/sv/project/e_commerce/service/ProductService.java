package sv.project.e_commerce.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import sv.project.e_commerce.dto.request.ProductCreateRequest;
import sv.project.e_commerce.dto.request.ProductUpdateRequest;
import sv.project.e_commerce.dto.response.ProductResponse;
import sv.project.e_commerce.exception.AppException;
import sv.project.e_commerce.exception.ErrorCode;
import sv.project.e_commerce.mapper.ProductMapper;
import sv.project.e_commerce.model.entity.Category;
import sv.project.e_commerce.model.entity.Product;
import sv.project.e_commerce.repository.CategoryRepository;
import sv.project.e_commerce.repository.ProductRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;
    FileStorageService fileStorageService;

    // Add
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse addProduct(ProductCreateRequest request, MultipartFile image) {
        Category category = categoryRepository.findByIdAndEnabledTrue(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (productRepository.existsByNameAndActiveTrue(request.getName())) {
            throw new AppException(ErrorCode.PRODUCT_NAME_EXISTED);
        }
        Product product = productMapper.toProduct(request);
        product.setCategory(category);
        product.setActive(true);
        // save img
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.save(image);
            product.setImageUrl(imageUrl);
        }
        return productMapper.toProductResponse(productRepository.save(product));
    }

    // findOne
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toProductResponse(product);
    }

    // findAll
    public List<ProductResponse> getProducts() {
        return productRepository.findAllByActiveTrue()
                .stream().map(productMapper::toProductResponse).toList();
    }

    public Page<ProductResponse> getProducts(int page, int size, String sortBy, String direction, String name,
            Long categoryId, String faceShape, boolean forAdmin) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Product> productPage;
        if (forAdmin) {
            productPage = productRepository.findAll(pageable);
        } else {
            if (faceShape != null && !faceShape.trim().isEmpty() && categoryId != null) {
                productPage = productRepository.findByCategoryIdAndFaceShapeForUser(categoryId, faceShape, pageable);
            } else if (faceShape != null && !faceShape.trim().isEmpty()) {
                productPage = productRepository.findByFaceShapeForUser(faceShape, pageable);
            } else if (categoryId != null) {
                if (name != null && !name.isEmpty()) {
                    productPage = productRepository.searchByCategoryIdAndNameForUser(categoryId, name, pageable);
                } else {
                    productPage = productRepository.findByCategoryIdForUser(categoryId, pageable);
                }
            } else {
                if (name != null && !name.isEmpty()) {
                    productPage = productRepository.searchByNameForUser(name, pageable);
                } else {
                    productPage = productRepository.findForUser(pageable);
                }
            }
        }
        return productPage.map(productMapper::toProductResponse);
    }


    // update
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request, MultipartFile image) {
        Product currProduct = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        productMapper.updateProduct(currProduct, request);
        // save img
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.save(image);
            currProduct.setImageUrl(imageUrl);
        }
        return productMapper.toProductResponse(productRepository.save(currProduct));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse updateStatus(Long id, boolean active) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        product.setActive(active);
        return productMapper.toProductResponse(productRepository.save(product));
    }

    // delete
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProduct(Long id) {
        Product currProduct = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        productRepository.delete(currProduct);
        return "'" + currProduct.getName() + "' was deleted";
    }
}
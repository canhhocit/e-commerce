package sv.project.e_commerce.service;

import java.util.List;

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
    public ProductResponse addProduct(ProductCreateRequest request, MultipartFile image) {
        Category category = categoryRepository.findByIdAndEnabledTrue(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if(productRepository.existsByNameAndActiveTrue(request.getName())){
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
        Product product = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toProductResponse(product);
    }

    // findAll
    public List<ProductResponse> getProducts() {
        return productRepository.findAllByActiveTrue()
                .stream().map(productMapper::toProductResponse).toList();
    }

    // update
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request, MultipartFile image) {
        Product currProduct = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        
        productMapper.updateProduct(currProduct, request);
        // save img
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.save(image);
            currProduct.setImageUrl(imageUrl);
        }
        return productMapper.toProductResponse(productRepository.save(currProduct));
    }

    // delete
    public String deleteProduct(Long id) {
        Product currProduct = productRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        currProduct.setActive(false);
        productRepository.save(currProduct);
        return "'" + currProduct.getName() + "' was deleted";
    }
}
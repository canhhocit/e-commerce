package sv.project.e_commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sv.project.e_commerce.dto.ProductDTO;
import sv.project.e_commerce.dto.ProductRequest;
import sv.project.e_commerce.exception.ResourceNotFoundException;
import sv.project.e_commerce.mapper.ProductMapper;
import sv.project.e_commerce.model.entity.Product;
import sv.project.e_commerce.repository.CategoryRepository;
import sv.project.e_commerce.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(String search, Pageable pageable) {
        Page<Product> products;
        if (search != null && !search.isBlank()) {
            products = productRepository.searchByName(search, pageable);
        } else {
            products = productRepository.findByActiveTrue(pageable);
        }
        return products.map(productMapper::toDto);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional
    public ProductDTO createProduct(ProductRequest request) {
        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);

        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        return productMapper.toDto(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setActive(false); // Soft delete
        productRepository.save(product);
    }
}

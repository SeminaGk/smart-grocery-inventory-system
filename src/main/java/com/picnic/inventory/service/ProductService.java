package com.picnic.inventory.service;

import com.picnic.inventory.dto.ProductCreateDTO;
import com.picnic.inventory.dto.ProductResponseDTO;
import com.picnic.inventory.model.Product;
import com.picnic.inventory.model.Category;
import com.picnic.inventory.model.Supplier;
import com.picnic.inventory.repository.ProductRepository;
import com.picnic.inventory.repository.CategoryRepository;
import com.picnic.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;
    
    @Autowired
    public ProductService(ProductRepository productRepository, 
                         CategoryRepository categoryRepository,
                         SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }
    
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<ProductResponseDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToResponseDTO);
    }
    
    public Optional<ProductResponseDTO> getProductBySku(String sku) {
        return productRepository.findBySku(sku)
                .map(this::convertToResponseDTO);
    }
    
    public Optional<ProductResponseDTO> getProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode)
                .map(this::convertToResponseDTO);
    }
    
    public ProductResponseDTO createProduct(ProductCreateDTO createDTO) {
        // Validate SKU and barcode uniqueness
        if (productRepository.findBySku(createDTO.getSku()).isPresent()) {
            throw new IllegalArgumentException("Product with SKU " + createDTO.getSku() + " already exists");
        }
        
        if (productRepository.findByBarcode(createDTO.getBarcode()).isPresent()) {
            throw new IllegalArgumentException("Product with barcode " + createDTO.getBarcode() + " already exists");
        }
        
        Product product = new Product();
        product.setName(createDTO.getName());
        product.setDescription(createDTO.getDescription());
        product.setSku(createDTO.getSku());
        product.setBarcode(createDTO.getBarcode());
        product.setPrice(createDTO.getPrice());
        product.setStockQuantity(createDTO.getStockQuantity());
        product.setMinStockLevel(createDTO.getMinStockLevel());
        product.setExpirationDate(createDTO.getExpirationDate());
        product.setIsPerishable(createDTO.getIsPerishable());
        product.setStorageLocation(createDTO.getStorageLocation());
        
        // Set category if provided
        if (createDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(createDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + createDTO.getCategoryId()));
            product.setCategory(category);
        }
        
        // Set supplier if provided
        if (createDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(createDTO.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + createDTO.getSupplierId()));
            product.setSupplier(supplier);
        }
        
        Product savedProduct = productRepository.save(product);
        return convertToResponseDTO(savedProduct);
    }
    
    public ProductResponseDTO updateProduct(Long id, ProductCreateDTO updateDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        // Check for SKU uniqueness if it's being changed
        if (!product.getSku().equals(updateDTO.getSku())) {
            if (productRepository.findBySku(updateDTO.getSku()).isPresent()) {
                throw new IllegalArgumentException("Product with SKU " + updateDTO.getSku() + " already exists");
            }
        }
        
        // Check for barcode uniqueness if it's being changed
        if (!product.getBarcode().equals(updateDTO.getBarcode())) {
            if (productRepository.findByBarcode(updateDTO.getBarcode()).isPresent()) {
                throw new IllegalArgumentException("Product with barcode " + updateDTO.getBarcode() + " already exists");
            }
        }
        
        // Update fields
        product.setName(updateDTO.getName());
        product.setDescription(updateDTO.getDescription());
        product.setSku(updateDTO.getSku());
        product.setBarcode(updateDTO.getBarcode());
        product.setPrice(updateDTO.getPrice());
        product.setStockQuantity(updateDTO.getStockQuantity());
        product.setMinStockLevel(updateDTO.getMinStockLevel());
        product.setExpirationDate(updateDTO.getExpirationDate());
        product.setIsPerishable(updateDTO.getIsPerishable());
        product.setStorageLocation(updateDTO.getStorageLocation());
        
        // Update category if provided
        if (updateDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(updateDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + updateDTO.getCategoryId()));
            product.setCategory(category);
        }
        
        // Update supplier if provided
        if (updateDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(updateDTO.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + updateDTO.getSupplierId()));
            product.setSupplier(supplier);
        }
        
        Product savedProduct = productRepository.save(product);
        return convertToResponseDTO(savedProduct);
    }
    
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
    
    // Business logic methods
    public List<ProductResponseDTO> getLowStockProducts() {
        return productRepository.findLowStockProducts().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductResponseDTO> getProductsExpiringWithinDays(int days) {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        return productRepository.findProductsExpiringWithinDays(today, futureDate).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductResponseDTO> getExpiredProducts() {
        return productRepository.findExpiredProducts(LocalDate.now()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public List<ProductResponseDTO> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    public ProductResponseDTO updateStock(Long id, Integer newQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        
        product.setStockQuantity(newQuantity);
        Product savedProduct = productRepository.save(product);
        return convertToResponseDTO(savedProduct);
    }
    
    public Double getTotalInventoryValue() {
        return productRepository.getTotalInventoryValue();
    }
    
    // Helper method to convert Product to ProductResponseDTO
    private ProductResponseDTO convertToResponseDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setSku(product.getSku());
        dto.setBarcode(product.getBarcode());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setMinStockLevel(product.getMinStockLevel());
        dto.setExpirationDate(product.getExpirationDate());
        dto.setIsPerishable(product.getIsPerishable());
        dto.setStorageLocation(product.getStorageLocation());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        
        // Set category name
        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }
        
        // Set supplier name
        if (product.getSupplier() != null) {
            dto.setSupplierName(product.getSupplier().getName());
        }
        
        // Set business logic flags
        dto.setIsLowStock(product.isLowStock());
        dto.setIsExpired(product.isExpired());
        dto.setIsExpiringSoon(product.isExpiringSoon(7)); // 7 days threshold
        
        return dto;
    }
}
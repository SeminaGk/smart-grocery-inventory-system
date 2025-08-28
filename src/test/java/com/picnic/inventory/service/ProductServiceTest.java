package com.picnic.inventory.service;

import com.picnic.inventory.dto.ProductCreateDTO;
import com.picnic.inventory.dto.ProductResponseDTO;
import com.picnic.inventory.model.Product;
import com.picnic.inventory.model.Category;
import com.picnic.inventory.model.Supplier;
import com.picnic.inventory.repository.ProductRepository;
import com.picnic.inventory.repository.CategoryRepository;
import com.picnic.inventory.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private SupplierRepository supplierRepository;
    
    @InjectMocks
    private ProductService productService;
    
    private Product testProduct;
    private Category testCategory;
    private Supplier testSupplier;
    private ProductCreateDTO testProductCreateDTO;
    
    @BeforeEach
    void setUp() {
        // Create test category
        testCategory = new Category("Fruits", "Fresh fruits");
        testCategory.setId(1L);
        
        // Create test supplier
        testSupplier = new Supplier("Fresh Foods Inc", "contact@freshfoods.com", "123-456-7890", "123 Main St");
        testSupplier.setId(1L);
        
        // Create test product
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Organic Apples");
        testProduct.setDescription("Fresh organic apples");
        testProduct.setSku("ORG-APP-001");
        testProduct.setBarcode("1234567890123");
        testProduct.setPrice(new BigDecimal("4.99"));
        testProduct.setStockQuantity(50);
        testProduct.setMinStockLevel(10);
        testProduct.setExpirationDate(LocalDate.now().plusDays(14));
        testProduct.setIsPerishable(true);
        testProduct.setStorageLocation("Refrigerator A1");
        testProduct.setCategory(testCategory);
        testProduct.setSupplier(testSupplier);
        
        // Create test DTO
        testProductCreateDTO = new ProductCreateDTO();
        testProductCreateDTO.setName("Organic Apples");
        testProductCreateDTO.setDescription("Fresh organic apples");
        testProductCreateDTO.setSku("ORG-APP-001");
        testProductCreateDTO.setBarcode("1234567890123");
        testProductCreateDTO.setPrice(new BigDecimal("4.99"));
        testProductCreateDTO.setStockQuantity(50);
        testProductCreateDTO.setMinStockLevel(10);
        testProductCreateDTO.setExpirationDate(LocalDate.now().plusDays(14));
        testProductCreateDTO.setIsPerishable(true);
        testProductCreateDTO.setStorageLocation("Refrigerator A1");
        testProductCreateDTO.setCategoryId(1L);
        testProductCreateDTO.setSupplierId(1L);
    }
    
    @Test
    void testGetAllProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);
        
        // When
        List<ProductResponseDTO> result = productService.getAllProducts();
        
        // Then
        assertEquals(1, result.size());
        assertEquals("Organic Apples", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }
    
    @Test
    void testGetProductById_Found() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // When
        Optional<ProductResponseDTO> result = productService.getProductById(1L);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals("Organic Apples", result.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetProductById_NotFound() {
        // Given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When
        Optional<ProductResponseDTO> result = productService.getProductById(999L);
        
        // Then
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(999L);
    }
    
    @Test
    void testCreateProduct_Success() {
        // Given
        when(productRepository.findBySku("ORG-APP-001")).thenReturn(Optional.empty());
        when(productRepository.findByBarcode("1234567890123")).thenReturn(Optional.empty());
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(testSupplier));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        ProductResponseDTO result = productService.createProduct(testProductCreateDTO);
        
        // Then
        assertNotNull(result);
        assertEquals("Organic Apples", result.getName());
        assertEquals("ORG-APP-001", result.getSku());
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void testCreateProduct_DuplicateSku() {
        // Given
        when(productRepository.findBySku("ORG-APP-001")).thenReturn(Optional.of(testProduct));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.createProduct(testProductCreateDTO)
        );
        assertEquals("Product with SKU ORG-APP-001 already exists", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void testCreateProduct_DuplicateBarcode() {
        // Given
        when(productRepository.findBySku("ORG-APP-001")).thenReturn(Optional.empty());
        when(productRepository.findByBarcode("1234567890123")).thenReturn(Optional.of(testProduct));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.createProduct(testProductCreateDTO)
        );
        assertEquals("Product with barcode 1234567890123 already exists", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void testGetLowStockProducts() {
        // Given
        Product lowStockProduct = new Product();
        lowStockProduct.setStockQuantity(5);
        lowStockProduct.setMinStockLevel(10);
        List<Product> lowStockProducts = Arrays.asList(lowStockProduct);
        when(productRepository.findLowStockProducts()).thenReturn(lowStockProducts);
        
        // When
        List<ProductResponseDTO> result = productService.getLowStockProducts();
        
        // Then
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findLowStockProducts();
    }
    
    @Test
    void testUpdateStock_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        
        // When
        ProductResponseDTO result = productService.updateStock(1L, 75);
        
        // Then
        assertNotNull(result);
        verify(productRepository, times(1)).save(any(Product.class));
    }
    
    @Test
    void testUpdateStock_NegativeQuantity() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.updateStock(1L, -5)
        );
        assertEquals("Stock quantity cannot be negative", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }
    
    @Test
    void testDeleteProduct_Success() {
        // Given
        when(productRepository.existsById(1L)).thenReturn(true);
        
        // When
        productService.deleteProduct(1L);
        
        // Then
        verify(productRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteProduct_NotFound() {
        // Given
        when(productRepository.existsById(999L)).thenReturn(false);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> productService.deleteProduct(999L)
        );
        assertEquals("Product not found with id: 999", exception.getMessage());
        verify(productRepository, never()).deleteById(999L);
    }
}
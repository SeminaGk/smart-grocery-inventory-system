package com.picnic.inventory.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.picnic.inventory.dto.ProductCreateDTO;
import com.picnic.inventory.model.Category;
import com.picnic.inventory.model.Product;
import com.picnic.inventory.model.Supplier;
import com.picnic.inventory.repository.CategoryRepository;
import com.picnic.inventory.repository.ProductRepository;
import com.picnic.inventory.repository.SupplierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
public class ProductControllerIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private SupplierRepository supplierRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Category testCategory;
    private Supplier testSupplier;
    
    @BeforeEach
    void setUp() {
        // Clean up database
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        supplierRepository.deleteAll();
        
        // Create test data
        testCategory = new Category("Test Category", "Test Description");
        testCategory = categoryRepository.save(testCategory);
        
        testSupplier = new Supplier("Test Supplier", "test@supplier.com", "123-456-7890", "123 Test St");
        testSupplier = supplierRepository.save(testSupplier);
    }
    
    @Test
    void testGetAllProducts_EmptyList() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
    
    @Test
    void testCreateProduct_Success() throws Exception {
        ProductCreateDTO productCreateDTO = new ProductCreateDTO();
        productCreateDTO.setName("Test Product");
        productCreateDTO.setDescription("Test Description");
        productCreateDTO.setSku("TEST-001");
        productCreateDTO.setBarcode("1234567890123");
        productCreateDTO.setPrice(new BigDecimal("9.99"));
        productCreateDTO.setStockQuantity(100);
        productCreateDTO.setMinStockLevel(10);
        productCreateDTO.setExpirationDate(LocalDate.now().plusDays(30));
        productCreateDTO.setIsPerishable(true);
        productCreateDTO.setStorageLocation("A1");
        productCreateDTO.setCategoryId(testCategory.getId());
        productCreateDTO.setSupplierId(testSupplier.getId());
        
        String productJson = objectMapper.writeValueAsString(productCreateDTO);
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.sku", is("TEST-001")))
                .andExpect(jsonPath("$.price", is(9.99)))
                .andExpect(jsonPath("$.stockQuantity", is(100)));
    }
    
    @Test
    void testCreateProduct_ValidationError() throws Exception {
        ProductCreateDTO productCreateDTO = new ProductCreateDTO();
        // Missing required fields
        productCreateDTO.setName(""); // Invalid - empty name
        
        String productJson = objectMapper.writeValueAsString(productCreateDTO);
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetProductById_Found() throws Exception {
        // Create a product first
        Product product = new Product();
        product.setName("Test Product");
        product.setSku("TEST-001");
        product.setBarcode("1234567890123");
        product.setPrice(new BigDecimal("9.99"));
        product.setStockQuantity(100);
        product.setMinStockLevel(10);
        product.setCategory(testCategory);
        product.setSupplier(testSupplier);
        product = productRepository.save(product);
        
        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.sku", is("TEST-001")))
                .andExpect(jsonPath("$.categoryName", is("Test Category")))
                .andExpect(jsonPath("$.supplierName", is("Test Supplier")));
    }
    
    @Test
    void testGetProductById_NotFound() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 999L))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void testGetLowStockProducts() throws Exception {
        // Create a low stock product
        Product lowStockProduct = new Product();
        lowStockProduct.setName("Low Stock Product");
        lowStockProduct.setSku("LOW-001");
        lowStockProduct.setBarcode("1234567890124");
        lowStockProduct.setPrice(new BigDecimal("5.99"));
        lowStockProduct.setStockQuantity(5); // Below minimum
        lowStockProduct.setMinStockLevel(10);
        lowStockProduct.setCategory(testCategory);
        lowStockProduct.setSupplier(testSupplier);
        productRepository.save(lowStockProduct);
        
        mockMvc.perform(get("/api/products/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Low Stock Product")))
                .andExpect(jsonPath("$[0].isLowStock", is(true)));
    }
    
    @Test
    void testUpdateProductStock() throws Exception {
        // Create a product
        Product product = new Product();
        product.setName("Test Product");
        product.setSku("TEST-001");
        product.setBarcode("1234567890123");
        product.setPrice(new BigDecimal("9.99"));
        product.setStockQuantity(100);
        product.setMinStockLevel(10);
        product.setCategory(testCategory);
        product.setSupplier(testSupplier);
        product = productRepository.save(product);
        
        String stockUpdate = "{\"quantity\": 150}";
        
        mockMvc.perform(patch("/api/products/{id}/stock", product.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(stockUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity", is(150)));
    }
    
    @Test
    void testDeleteProduct_Success() throws Exception {
        // Create a product
        Product product = new Product();
        product.setName("Test Product");
        product.setSku("TEST-001");
        product.setBarcode("1234567890123");
        product.setPrice(new BigDecimal("9.99"));
        product.setStockQuantity(100);
        product.setMinStockLevel(10);
        product.setCategory(testCategory);
        product.setSupplier(testSupplier);
        product = productRepository.save(product);
        
        mockMvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isNoContent());
        
        // Verify product is deleted
        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isNotFound());
    }
}
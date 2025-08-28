package com.picnic.inventory.controller;

import com.picnic.inventory.dto.ProductCreateDTO;
import com.picnic.inventory.dto.ProductResponseDTO;
import com.picnic.inventory.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management operations")
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieve a list of all products in the inventory")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved products")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved product")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU", description = "Retrieve a product by its SKU")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved product")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponseDTO> getProductBySku(
            @Parameter(description = "Product SKU") @PathVariable String sku) {
        return productService.getProductBySku(sku)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/barcode/{barcode}")
    @Operation(summary = "Get product by barcode", description = "Retrieve a product by its barcode")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved product")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponseDTO> getProductByBarcode(
            @Parameter(description = "Product barcode") @PathVariable String barcode) {
        return productService.getProductByBarcode(barcode)
                .map(product -> ResponseEntity.ok(product))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Create new product", description = "Add a new product to the inventory")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<ProductResponseDTO> createProduct(@Valid @RequestBody ProductCreateDTO productCreateDTO) {
        try {
            ProductResponseDTO createdProduct = productService.createProduct(productCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Valid @RequestBody ProductCreateDTO productUpdateDTO) {
        try {
            ProductResponseDTO updatedProduct = productService.updateProduct(id, productUpdateDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Remove a product from the inventory")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @Parameter(description = "Search term") @RequestParam String name) {
        List<ProductResponseDTO> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock products", description = "Retrieve products that are running low on stock")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved low stock products")
    public ResponseEntity<List<ProductResponseDTO>> getLowStockProducts() {
        List<ProductResponseDTO> products = productService.getLowStockProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/expiring")
    @Operation(summary = "Get expiring products", description = "Retrieve products that are expiring within specified days")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved expiring products")
    public ResponseEntity<List<ProductResponseDTO>> getExpiringProducts(
            @Parameter(description = "Number of days") @RequestParam(defaultValue = "7") int days) {
        List<ProductResponseDTO> products = productService.getProductsExpiringWithinDays(days);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/expired")
    @Operation(summary = "Get expired products", description = "Retrieve products that have already expired")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved expired products")
    public ResponseEntity<List<ProductResponseDTO>> getExpiredProducts() {
        List<ProductResponseDTO> products = productService.getExpiredProducts();
        return ResponseEntity.ok(products);
    }
    
    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock", description = "Update the stock quantity of a product")
    @ApiResponse(responseCode = "200", description = "Stock updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid stock quantity")
    @ApiResponse(responseCode = "404", description = "Product not found")
    public ResponseEntity<ProductResponseDTO> updateProductStock(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @RequestBody Map<String, Integer> stockUpdate) {
        try {
            Integer newQuantity = stockUpdate.get("quantity");
            if (newQuantity == null) {
                return ResponseEntity.badRequest().build();
            }
            
            ProductResponseDTO updatedProduct = productService.updateStock(id, newQuantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/inventory-value")
    @Operation(summary = "Get total inventory value", description = "Calculate the total value of all products in inventory")
    @ApiResponse(responseCode = "200", description = "Successfully calculated inventory value")
    public ResponseEntity<Map<String, Double>> getTotalInventoryValue() {
        Double totalValue = productService.getTotalInventoryValue();
        return ResponseEntity.ok(Map.of("totalValue", totalValue != null ? totalValue : 0.0));
    }
}
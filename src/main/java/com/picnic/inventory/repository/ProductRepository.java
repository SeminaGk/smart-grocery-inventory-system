package com.picnic.inventory.repository;

import com.picnic.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Find product by SKU
    Optional<Product> findBySku(String sku);
    
    // Find product by barcode
    Optional<Product> findByBarcode(String barcode);
    
    // Find products by category
    List<Product> findByCategoryId(Long categoryId);
    
    // Find products by supplier
    List<Product> findBySupplierId(Long supplierId);
    
    // Search products by name containing (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // Find products with low stock
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.minStockLevel")
    List<Product> findLowStockProducts();
    
    // Find products expiring within specified days
    @Query("SELECT p FROM Product p WHERE p.expirationDate IS NOT NULL AND p.expirationDate BETWEEN :today AND :futureDate")
    List<Product> findProductsExpiringWithinDays(@Param("today") LocalDate today, @Param("futureDate") LocalDate futureDate);
    
    // Find expired products
    @Query("SELECT p FROM Product p WHERE p.expirationDate IS NOT NULL AND p.expirationDate < :today")
    List<Product> findExpiredProducts(@Param("today") LocalDate today);
    
    // Find perishable products
    List<Product> findByIsPerishableTrue();
    
    // Find products by storage location
    List<Product> findByStorageLocationContainingIgnoreCase(String location);
    
    // Count products by category
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id = :categoryId")
    long countProductsByCategory(@Param("categoryId") Long categoryId);
    
    // Count products by supplier
    @Query("SELECT COUNT(p) FROM Product p WHERE p.supplier.id = :supplierId")
    long countProductsBySupplier(@Param("supplierId") Long supplierId);
    
    // Get total inventory value
    @Query("SELECT SUM(p.price * p.stockQuantity) FROM Product p")
    Double getTotalInventoryValue();
}
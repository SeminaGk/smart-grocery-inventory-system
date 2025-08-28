package com.picnic.inventory.repository;

import com.picnic.inventory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    // Find supplier by name
    Optional<Supplier> findByName(String name);
    
    // Find supplier by email
    Optional<Supplier> findByEmail(String email);
    
    // Find suppliers by name containing (case-insensitive)
    List<Supplier> findByNameContainingIgnoreCase(String name);
    
    // Find suppliers by contact person
    List<Supplier> findByContactPersonContainingIgnoreCase(String contactPerson);
    
    // Check if supplier email exists
    boolean existsByEmail(String email);
}
package com.picnic.inventory.repository;

import com.picnic.inventory.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Find category by name
    Optional<Category> findByName(String name);
    
    // Find categories by name containing (case-insensitive)
    List<Category> findByNameContainingIgnoreCase(String name);
    
    // Check if category name exists
    boolean existsByName(String name);
}
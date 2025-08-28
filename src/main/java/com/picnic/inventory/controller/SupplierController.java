package com.picnic.inventory.controller;

import com.picnic.inventory.model.Supplier;
import com.picnic.inventory.service.SupplierService;
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
@RequestMapping("/api/suppliers")
@Tag(name = "Suppliers", description = "Supplier management operations")
public class SupplierController {
    
    private final SupplierService supplierService;
    
    @Autowired
    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }
    
    @GetMapping
    @Operation(summary = "Get all suppliers", description = "Retrieve a list of all suppliers")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved suppliers")
    public ResponseEntity<List<Supplier>> getAllSuppliers() {
        List<Supplier> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(suppliers);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID", description = "Retrieve a specific supplier by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved supplier")
    @ApiResponse(responseCode = "404", description = "Supplier not found")
    public ResponseEntity<Supplier> getSupplierById(
            @Parameter(description = "Supplier ID") @PathVariable Long id) {
        return supplierService.getSupplierById(id)
                .map(supplier -> ResponseEntity.ok(supplier))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Create new supplier", description = "Add a new supplier")
    @ApiResponse(responseCode = "201", description = "Supplier created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data or email already exists")
    public ResponseEntity<Supplier> createSupplier(@RequestBody Map<String, String> supplierData) {
        try {
            String name = supplierData.get("name");
            String email = supplierData.get("email");
            String phone = supplierData.get("phone");
            String address = supplierData.get("address");
            
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Supplier createdSupplier = supplierService.createSupplier(name.trim(), email, phone, address);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdSupplier);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update supplier", description = "Update an existing supplier")
    @ApiResponse(responseCode = "200", description = "Supplier updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    @ApiResponse(responseCode = "404", description = "Supplier not found")
    public ResponseEntity<Supplier> updateSupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long id,
            @RequestBody Map<String, String> supplierData) {
        try {
            String name = supplierData.get("name");
            String email = supplierData.get("email");
            String phone = supplierData.get("phone");
            String address = supplierData.get("address");
            String contactPerson = supplierData.get("contactPerson");
            
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            Supplier updatedSupplier = supplierService.updateSupplier(id, name.trim(), email, phone, address, contactPerson);
            return ResponseEntity.ok(updatedSupplier);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete supplier", description = "Remove a supplier")
    @ApiResponse(responseCode = "204", description = "Supplier deleted successfully")
    @ApiResponse(responseCode = "404", description = "Supplier not found")
    public ResponseEntity<Void> deleteSupplier(
            @Parameter(description = "Supplier ID") @PathVariable Long id) {
        try {
            supplierService.deleteSupplier(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search suppliers", description = "Search suppliers by name")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved search results")
    public ResponseEntity<List<Supplier>> searchSuppliers(
            @Parameter(description = "Search term") @RequestParam String name) {
        List<Supplier> suppliers = supplierService.searchSuppliersByName(name);
        return ResponseEntity.ok(suppliers);
    }
}
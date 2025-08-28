package com.picnic.inventory.service;

import com.picnic.inventory.model.Supplier;
import com.picnic.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SupplierService {
    
    private final SupplierRepository supplierRepository;
    
    @Autowired
    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }
    
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }
    
    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }
    
    public Optional<Supplier> getSupplierByName(String name) {
        return supplierRepository.findByName(name);
    }
    
    public Supplier createSupplier(String name, String email, String phone, String address) {
        if (email != null && supplierRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Supplier with email '" + email + "' already exists");
        }
        
        Supplier supplier = new Supplier(name, email, phone, address);
        return supplierRepository.save(supplier);
    }
    
    public Supplier updateSupplier(Long id, String name, String email, String phone, String address, String contactPerson) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found with id: " + id));
        
        // Check email uniqueness if it's being changed
        if (email != null && !email.equals(supplier.getEmail()) && supplierRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Supplier with email '" + email + "' already exists");
        }
        
        supplier.setName(name);
        supplier.setEmail(email);
        supplier.setPhone(phone);
        supplier.setAddress(address);
        supplier.setContactPerson(contactPerson);
        return supplierRepository.save(supplier);
    }
    
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new IllegalArgumentException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }
    
    public List<Supplier> searchSuppliersByName(String name) {
        return supplierRepository.findByNameContainingIgnoreCase(name);
    }
}
-- Smart Grocery Inventory Database Setup
-- Run this script to set up PostgreSQL database

-- Create database and user
CREATE DATABASE grocery_inventory;
CREATE USER inventory_user WITH PASSWORD 'inventory_pass';
GRANT ALL PRIVILEGES ON DATABASE grocery_inventory TO inventory_user;

-- Connect to the database
\c grocery_inventory;

-- Grant schema permissions
GRANT ALL ON SCHEMA public TO inventory_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO inventory_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO inventory_user;

-- Sample data for testing (optional)
-- The application will create tables automatically, but you can insert sample data after startup

-- Sample Categories
INSERT INTO categories (name, description, created_at) VALUES 
('Fruits', 'Fresh fruits and produce', NOW()),
('Vegetables', 'Fresh vegetables and greens', NOW()),
('Dairy', 'Milk, cheese, and dairy products', NOW()),
('Meat', 'Fresh meat and poultry', NOW()),
('Bakery', 'Bread, pastries, and baked goods', NOW());

-- Sample Suppliers
INSERT INTO suppliers (name, email, phone, address, contact_person, created_at) VALUES 
('Fresh Farms Inc', 'orders@freshfarms.com', '555-0101', '123 Farm Road, Agricultural District', 'John Smith', NOW()),
('Dairy Valley Co', 'supply@dairyvalley.com', '555-0102', '456 Milk Lane, Dairy County', 'Sarah Johnson', NOW()),
('Green Produce Ltd', 'info@greenproduceltd.com', '555-0103', '789 Garden Ave, Produce City', 'Mike Chen', NOW());

-- Sample Products (insert after categories and suppliers are created)
INSERT INTO products (name, description, sku, barcode, price, stock_quantity, min_stock_level, 
                     expiration_date, is_perishable, storage_location, category_id, supplier_id, created_at) VALUES 
('Organic Bananas', 'Fresh organic bananas from Ecuador', 'ORG-BAN-001', '1234567890123', 2.99, 150, 25, 
 CURRENT_DATE + INTERVAL '7 days', true, 'Produce Section A1', 1, 1, NOW()),
('Whole Milk', 'Fresh whole milk 1L', 'MILK-WHL-001', '1234567890124', 3.49, 80, 15, 
 CURRENT_DATE + INTERVAL '5 days', true, 'Refrigerator B2', 3, 2, NOW()),
('Red Apples', 'Crisp red apples', 'APP-RED-001', '1234567890125', 4.99, 120, 20, 
 CURRENT_DATE + INTERVAL '14 days', true, 'Produce Section A2', 1, 1, NOW()),
('Cheddar Cheese', 'Aged cheddar cheese 200g', 'CHE-CHE-001', '1234567890126', 5.99, 45, 10, 
 CURRENT_DATE + INTERVAL '30 days', true, 'Refrigerator B1', 3, 2, NOW()),
('Fresh Bread', 'Daily baked white bread', 'BRD-WHT-001', '1234567890127', 2.49, 30, 5, 
 CURRENT_DATE + INTERVAL '2 days', true, 'Bakery Section C1', 5, 3, NOW());

-- Indexes for better performance (optional - Spring Boot will create basic ones)
CREATE INDEX IF NOT EXISTS idx_products_sku ON products(sku);
CREATE INDEX IF NOT EXISTS idx_products_barcode ON products(barcode);
CREATE INDEX IF NOT EXISTS idx_products_expiration ON products(expiration_date);
CREATE INDEX IF NOT EXISTS idx_products_stock_level ON products(stock_quantity, min_stock_level);

-- Comments
COMMENT ON TABLE products IS 'Main products table for inventory management';
COMMENT ON TABLE categories IS 'Product categories for organization';
COMMENT ON TABLE suppliers IS 'Supplier information and contacts';

-- Print success message
\echo 'Database setup completed successfully!'
\echo 'You can now start the Spring Boot application.'
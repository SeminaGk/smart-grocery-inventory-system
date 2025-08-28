# 🛒 Smart Grocery Inventory Management System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-red.svg)](https://maven.apache.org/)
[![Tests](https://img.shields.io/badge/Tests-19%20Passed-brightgreen.svg)]()

> 🎯 **

A modern, comprehensive inventory management system designed for grocery warehouses and retail stores. Built with Java 21, Spring Boot 3.x, and PostgreSQL, this application provides real-time inventory tracking, automated stock alerts, and expiration date monitoring.

## 🎯 Project Overview

This system addresses critical challenges in grocery inventory management:
- **Real-time stock tracking** with automated low-stock alerts
- **Expiration date monitoring** for perishable items
- **Comprehensive product management** with categories and suppliers
- **RESTful API** for easy integration with existing systems
- **Robust validation** and error handling

Perfect for businesses in the grocery delivery industry, similar to companies like Picnic that manage complex warehouse operations.

## ✨ Key Features

### 📦 Product Management
- Complete CRUD operations for products
- SKU and barcode-based product identification
- Price and stock quantity tracking
- Storage location management
- Perishable item flagging with expiration dates

### 📊 Inventory Intelligence
- **Low Stock Alerts**: Automatic detection when products fall below minimum levels
- **Expiration Monitoring**: Track products expiring within customizable timeframes
- **Total Inventory Value**: Real-time calculation of inventory worth
- **Smart Search**: Find products by name, SKU, or barcode

### 🏪 Business Management
- **Category Management**: Organize products into logical groups
- **Supplier Management**: Track supplier information and contacts
- **Relationship Mapping**: Link products to categories and suppliers

### 🔧 Technical Features
- **REST API**: Complete OpenAPI/Swagger documentation
- **Data Validation**: Comprehensive input validation with meaningful error messages
- **Database Integration**: Robust PostgreSQL integration with JPA/Hibernate
- **Test Coverage**: Unit and integration tests for reliability
- **Error Handling**: Graceful error handling with appropriate HTTP status codes

## 🛠️ Tech Stack

- **Java 21** - Latest LTS version with modern features
- **Spring Boot 3.2.0** - Production-ready framework
- **Spring Data JPA** - Data persistence layer
- **PostgreSQL** - Primary database
- **H2 Database** - In-memory database for testing
- **Maven** - Dependency management and build tool
- **JUnit 5** - Testing framework
- **Swagger/OpenAPI** - API documentation
- **Bean Validation** - Input validation

## 🚀 Quick Start

### Prerequisites
- Java 21 or higher
- Maven 3.6+
- PostgreSQL 12+

### Database Setup
1. Install PostgreSQL and create a database:
```sql
CREATE DATABASE grocery_inventory;
CREATE USER inventory_user WITH PASSWORD 'inventory_pass';
GRANT ALL PRIVILEGES ON DATABASE grocery_inventory TO inventory_user;
```

2. The application will automatically create tables on startup.

### Running the Application

1. **Clone the repository**
```bash
git clone <repository-url>
cd smart-grocery-inventory
```

2. **Build the project**
```bash
mvn clean compile
```

3. **Run tests**
```bash
mvn test
```

4. **Start the application**
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 🔍 API Documentation
Once running, visit:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## 📚 API Endpoints

### Products
- `GET /api/products` - Get all products
- `POST /api/products` - Create new product
- `GET /api/products/{id}` - Get product by ID
- `PUT /api/products/{id}` - Update product
- `DELETE /api/products/{id}` - Delete product
- `GET /api/products/search?name={name}` - Search products
- `GET /api/products/low-stock` - Get low stock products
- `GET /api/products/expiring?days={days}` - Get products expiring soon
- `PATCH /api/products/{id}/stock` - Update stock quantity

### Categories
- `GET /api/categories` - Get all categories
- `POST /api/categories` - Create new category
- `GET /api/categories/{id}` - Get category by ID
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category

### Suppliers
- `GET /api/suppliers` - Get all suppliers
- `POST /api/suppliers` - Create new supplier
- `GET /api/suppliers/{id}` - Get supplier by ID
- `PUT /api/suppliers/{id}` - Update supplier
- `DELETE /api/suppliers/{id}` - Delete supplier

## 📋 Sample API Usage

### Creating a Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Organic Bananas",
    "description": "Fresh organic bananas from Ecuador",
    "sku": "ORG-BAN-001",
    "barcode": "1234567890123",
    "price": 2.99,
    "stockQuantity": 150,
    "minStockLevel": 20,
    "expirationDate": "2024-09-15",
    "isPerishable": true,
    "storageLocation": "Produce Section A",
    "categoryId": 1,
    "supplierId": 1
  }'
```

### Getting Low Stock Products
```bash
curl -X GET http://localhost:8080/api/products/low-stock
```

### Updating Stock Quantity
```bash
curl -X PATCH http://localhost:8080/api/products/1/stock \
  -H "Content-Type: application/json" \
  -d '{"quantity": 200}'
```

## 🧪 Testing

Run the full test suite:
```bash
mvn test
```

The project includes:
- **Unit Tests**: Test individual service methods
- **Integration Tests**: Test complete API workflows
- **Database Tests**: Test repository operations

## 🏗️ Project Structure

```
src/
├── main/
│   ├── java/com/picnic/inventory/
│   │   ├── controller/     # REST API controllers
│   │   ├── dto/           # Data Transfer Objects
│   │   ├── model/         # JPA entities
│   │   ├── repository/    # Data access layer
│   │   ├── service/       # Business logic
│   │   └── SmartGroceryInventoryApplication.java
│   └── resources/
│       ├── application.properties
│       └── application-test.properties
└── test/
    └── java/com/picnic/inventory/
        ├── controller/    # Integration tests
        └── service/       # Unit tests
```

## 🔧 Configuration

### Database Configuration
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/grocery_inventory
spring.datasource.username=inventory_user
spring.datasource.password=inventory_pass
```

### Application Properties
- `server.port=8080` - Application port
- `spring.jpa.hibernate.ddl-auto=update` - Database schema management
- `spring.jpa.show-sql=true` - Show SQL queries in logs

## 🚀 Deployment

### Production Build
```bash
mvn clean package
java -jar target/smart-grocery-inventory-1.0.0.jar
```

### Docker Support
Create a `Dockerfile` for containerized deployment:
```dockerfile
FROM openjdk:21-jre-slim
COPY target/smart-grocery-inventory-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m 'Add amazing feature'`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

You can freely use.

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- PostgreSQL community for the robust database
- OpenAPI initiative for API documentation standards

---

**Built with ❤️ for the grocery industry**

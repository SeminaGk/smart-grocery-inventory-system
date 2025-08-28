# 🧪 Local Testing Guide

## Quick Setup Instructions

### 1. Install Required Tools

**Java 21:**
```bash
# On macOS with Homebrew
brew install openjdk@21

# On Windows, download from Oracle or use Chocolatey
choco install openjdk21
```

**Maven:**
```bash
# On macOS with Homebrew
brew install maven

# On Windows with Chocolatey  
choco install maven
```

**PostgreSQL (Optional - can use Docker instead):**
```bash
# On macOS with Homebrew
brew install postgresql
brew services start postgresql

# On Windows with Chocolatey
choco install postgresql
```

### 2. Quick Test Commands

```bash
# Navigate to project
cd "/Users/semina/JAVA project/smart-grocery-inventory"

# Make scripts executable (on macOS/Linux)
chmod +x dev-scripts.sh

# Check dependencies
./dev-scripts.sh check-deps

# Option A: Use Docker for database (recommended)
./dev-scripts.sh docker-db

# Option B: Use local PostgreSQL
./dev-scripts.sh db-setup

# Build project
./dev-scripts.sh build

# Run tests
./dev-scripts.sh test

# Start application
./dev-scripts.sh run
```

### 3. Verify It's Working

Once started, visit these URLs:

- **Application**: http://localhost:8080
- **API Docs**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

### 4. Test API Endpoints

**Create a Category:**
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Fruits", "description": "Fresh fruits"}'
```

**Create a Product:**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Organic Apples",
    "description": "Fresh organic apples",
    "sku": "ORG-APP-001",
    "barcode": "1234567890123",
    "price": 4.99,
    "stockQuantity": 50,
    "minStockLevel": 10,
    "expirationDate": "2024-12-15",
    "isPerishable": true,
    "storageLocation": "Refrigerator A1",
    "categoryId": 1
  }'
```

**Get All Products:**
```bash
curl http://localhost:8080/api/products
```

### 5. Troubleshooting

**Port 8080 in use:**
- Change port in `application.properties`: `server.port=8081`

**Database connection issues:**
- Check PostgreSQL is running: `brew services list | grep postgres`
- Verify database exists: `psql -U postgres -l`

**Build errors:**
- Check Java version: `java -version` (should be 21+)
- Check Maven version: `mvn -version`

### 6. Expected Test Results

**Unit Tests:** Should pass ~10 tests covering service logic
**Integration Tests:** Should pass ~8 tests covering full API workflows
**Build:** Should create a JAR file in `target/` directory

If all tests pass and the application starts successfully, you're ready for demos!
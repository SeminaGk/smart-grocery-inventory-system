#!/bin/bash

# Smart Grocery Inventory - Development Helper Scripts
# Make this file executable: chmod +x dev-scripts.sh

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Help function
show_help() {
    echo "Smart Grocery Inventory - Development Helper"
    echo ""
    echo "Usage: ./dev-scripts.sh [command]"
    echo ""
    echo "Commands:"
    echo "  setup         - Initial project setup"
    echo "  build         - Build the project"
    echo "  test          - Run all tests"
    echo "  run           - Start the application"
    echo "  clean         - Clean build artifacts"
    echo "  db-setup      - Set up PostgreSQL database"
    echo "  docker-db     - Start PostgreSQL in Docker"
    echo "  check-deps    - Check required dependencies"
    echo "  help          - Show this help message"
    echo ""
}

# Check dependencies
check_dependencies() {
    print_info "Checking required dependencies..."
    
    local missing_deps=()
    
    if ! command_exists java; then
        missing_deps+=("java")
    else
        java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f 2)
        print_success "Java found: $java_version"
    fi
    
    if ! command_exists mvn; then
        missing_deps+=("maven")
    else
        mvn_version=$(mvn -version | head -n 1 | cut -d' ' -f 3)
        print_success "Maven found: $mvn_version"
    fi
    
    if ! command_exists psql; then
        print_warning "PostgreSQL client not found (optional for local development)"
    else
        print_success "PostgreSQL client found"
    fi
    
    if ! command_exists docker; then
        print_warning "Docker not found (optional for containerized database)"
    else
        print_success "Docker found"
    fi
    
    if [ ${#missing_deps[@]} -gt 0 ]; then
        print_error "Missing required dependencies: ${missing_deps[*]}"
        echo "Please install the missing dependencies and try again."
        exit 1
    fi
    
    print_success "All required dependencies are installed!"
}

# Initial setup
setup_project() {
    print_info "Setting up Smart Grocery Inventory project..."
    
    check_dependencies
    
    print_info "Building project..."
    mvn clean compile
    
    print_success "Project setup completed!"
    print_info "Next steps:"
    echo "  1. Set up database: ./dev-scripts.sh db-setup"
    echo "  2. Run tests: ./dev-scripts.sh test"
    echo "  3. Start application: ./dev-scripts.sh run"
}

# Build project
build_project() {
    print_info "Building project..."
    mvn clean compile
    print_success "Build completed!"
}

# Run tests
run_tests() {
    print_info "Running tests..."
    mvn test
    print_success "Tests completed!"
}

# Run application
run_application() {
    print_info "Starting Smart Grocery Inventory application..."
    print_warning "Make sure PostgreSQL is running and database is set up!"
    mvn spring-boot:run
}

# Clean project
clean_project() {
    print_info "Cleaning project..."
    mvn clean
    print_success "Project cleaned!"
}

# Set up database
setup_database() {
    print_info "Setting up PostgreSQL database..."
    
    if command_exists psql; then
        print_info "Running database setup script..."
        psql -U postgres -f database-setup.sql
        print_success "Database setup completed!"
    else
        print_error "PostgreSQL client (psql) not found!"
        print_info "Please install PostgreSQL or use Docker:"
        print_info "./dev-scripts.sh docker-db"
    fi
}

# Start PostgreSQL in Docker
start_docker_db() {
    print_info "Starting PostgreSQL in Docker..."
    
    if ! command_exists docker; then
        print_error "Docker not found! Please install Docker first."
        exit 1
    fi
    
    # Check if container already exists
    if docker ps -a | grep -q postgres-inventory; then
        print_info "Starting existing PostgreSQL container..."
        docker start postgres-inventory
    else
        print_info "Creating new PostgreSQL container..."
        docker run --name postgres-inventory \
            -e POSTGRES_PASSWORD=postgres \
            -e POSTGRES_DB=grocery_inventory \
            -p 5432:5432 \
            -d postgres:15
    fi
    
    print_success "PostgreSQL is running on localhost:5432"
    print_info "Database: grocery_inventory"
    print_info "Username: postgres"
    print_info "Password: postgres"
    
    # Wait for database to be ready
    print_info "Waiting for database to be ready..."
    sleep 5
    
    # Run setup script
    print_info "Setting up database schema..."
    docker exec -i postgres-inventory psql -U postgres -d grocery_inventory < database-setup.sql
    print_success "Database setup completed!"
}

# Main script logic
case "${1:-help}" in
    "setup")
        setup_project
        ;;
    "build")
        build_project
        ;;
    "test")
        run_tests
        ;;
    "run")
        run_application
        ;;
    "clean")
        clean_project
        ;;
    "db-setup")
        setup_database
        ;;
    "docker-db")
        start_docker_db
        ;;
    "check-deps")
        check_dependencies
        ;;
    "help"|*)
        show_help
        ;;
esac
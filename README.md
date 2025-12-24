# E-commerce Microservices

This project consists of two microservices implementing an event-driven architecture (Saga Pattern) for order processing and inventory management.

## Microservices
1. **Order Service**: Handles order placement, state management, and orchestration.
2. **Inventory Service**: Manages products and inventory levels.

## Tech Stack
- **Java 17 & Spring Boot 3**
- **MySQL** (Per-service database with persistent volumes)
- **AWS SQS** (Event-driven communication, JSON payloads)
- **Bucket4j** (Rate Limiting) 
- **Spring Retry** (Resilience for consumers)
- **Docker & Docker Compose**

## Features
- **Saga Pattern**: Asynchronous order processing with compensation (Refund/Cancel on inventory failure).
- **State Management**: Strict order status transitions (PENDING -> CONFIRMED -> SHIPPED -> DELIVERED).
- **Cancellation Reasons**: Captures specific reasons for order cancellation (e.g., "Insufficient inventory").
- **Optimistic Locking**: Handles concurrent inventory updates using `@Version`.
- **Resilience**: 
    - Retry mechanism for SQS Consumers.
    - Rate limiting on Order creation (Bucket capacity is 60 and allow 1 token refill per sec).
- **Persistence**: Named Docker volumes for Database persistence.

## Prerequisites
- **Java 17+**
- **Docker & Docker Compose**
- **AWS Credentials**: Real AWS credentials (SQS access) are required as LocalStack has been removed.

## Setup & Running

1. **Environment Configuration**:
   Create a `.env` file in the root directory with the following variables:
   ```properties
   DB_USERNAME=root
   DB_PASSWORD=yourpassword
   AWS_ACCESS_KEY=your_access_key
   AWS_SECRET_KEY=your_secret_key
   AWS_REGION=ap-south-1
   ```

2. **Build the Application**:
    ```bash
    mvn clean package -DskipTests
    ```
    (Run this in root or both `order-service` and `inventory-service` directories if using a monorepo structure).

3. **Start Infrastructure**:
    ```bash
    docker-compose up --build
    ```

## API Endpoints

### Order Service (Port 8080)
- **Create Order**: `POST /api/orders`
    ```json
    {
      "productId": 1,
      "quantity": 2
    }
    ```
- **Get Order**: `GET /api/orders/{orderId}`
- **Update Status**: `PATCH /api/orders/{orderId}/status`
    ```json
    {
      "status": "SHIPPED"
    }
    ```
- **Cancel Order**: (Internal/System triggered on failure)

### Product Service (Port 8081)
- **Get All Products**: `GET /api/products`
- **Create Product**: `POST /api/products`
    ```json
    {
      "name": "Laptop",
      "price": 999.99,
      "quantity": 10
    }
    ```
- **Update Inventory**: `PUT /api/products`
    ```json
    {
      "id": 1,
      "quantity": 50
    }
    ```

## Postman & AWS Deployment
To test the deployed services on AWS using Postman:

1.  **Environment Variables**:
    Set following environment variables in postman collection:
    *   `orderBaseUrl`: `http://13.235.129.65:8080/api`
    *   `inventoryBaseUrl`: `http://13.235.129.65:8081/api`

# Scaling

Order-service and product-service are stateless, and we can simply run multiple instances (e.g., Kubernetes Pods) behind a Load Balancer to handle more traffic.
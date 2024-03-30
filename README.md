# ShopEase

## Description

ShopEase is a simple e-commerce application developed using Spring Boot. It provides basic functionalities like managing products, users, coupons, and orders. Users can view products, apply coupons, and place orders.

## Features

- CRUD operations for Products, Users, Coupons, and Orders.
- Applying coupons to orders for discounts.
- Managing user-specific orders and coupons.
- RESTful APIs for interacting with the application.

## Setup and Installation

### Prerequisites

- Java JDK (11 or higher)
- Maven
- Git

### Clone Repository

```
git clone https://github.com/your-username/ShopEase.git  
```

### Build Project

```
cd ShopEase
mvn clean install
```

### Run Application
```
java -jar target/ShopEase-1.0-SNAPSHOT.jar
```

The application will start on localhost:8080.

## API Endpoints
Product
- GET /inventory: Get all products.


User

- GET /users: Get all users.
- GET /users/{userId}: Get user by ID.
- POST /users: Create a new user.
- PUT /users/{userId}: Update user details.
- DELETE /users/{userId}: Delete user by ID.

Coupon

- GET /fetchCoupons: Get all coupons.


Order

- GET /{userId}/orders/{orderId}: Get order by ID.
- GET /{userId}/orders: Get all orders by User
- POST /{userId}/order?qty=: Order products
- POST /{userId}/order?qty=&coupon="": Order products with discount
- POST {userId}/{orderId}/pay?amount=: Pay money for order



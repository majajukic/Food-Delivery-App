# Microservices Food Delivery App

This project is a **microservices-based food delivery application** built using **Spring Boot** and **Spring Cloud** technologies. The application is containerized using **Docker** and leverages **Okta** for authentication and user registration. It employs asynchronous communication through **Kafka** and uses **Junit4** for unit and integration tests.

## Diagram

![Architecture Diagram](./food-delivery-microservices-diagram.jpg)

> The above diagram illustrates the architecture of the microservices ecosystem, including the service registry, API gateway, Okta authentication, individual services, Kafka for asynchronous communication, and the databases for each service.

## Microservices Overview

### 1. **Service Registry (Eureka Server)**

- **Technology**: Spring Cloud Eureka
- **Purpose**: Acts as the service registry where all microservices register themselves for discovery.
- **Docker Container**: `eureka-server`
- **Port**: `8761`
- **Communication**: Other microservices communicate with Eureka to discover each other for seamless integration.

### 2. **API Gateway**

- **Technology**: Spring Cloud Gateway
- **Purpose**: The API gateway acts as the entry point for all client requests and routes the requests to the appropriate microservices. It integrates with **Okta** for authentication and auhtorizaion (there are 2 user roles: *Admin* and *Customer*). It authenticates requests and propagates the authentication context down the pipeline for other services.
- **Docker Container**: `api-gateway`
- **Port**: `9090`
- **Dependencies**:
  - Eureka Server
  - Redis (utilized for load-balancing: 1 request per 1 user per second)
- **Environment Variables**:
  - `SPRING_OKTA_CLIENT_ID`
  - `SPRING_OKTA_CLIENT_SECRET`
  - `EUREKA_SERVER_ADDRESS`
    
### 3. **Restaurant Service**

- **Technology**: Spring Boot
- **Purpose**: Manages restaurants and dishes. It contains authorized endpoints for getting restaurant and dish data, updating and deleting restaurants, adding dishes to restaurants and updating dishes in restaurants.
- **Docker Container**: `restaurant-service`
- **Port**: `8081`
- **Dependencies**:
  - Eureka Server
- **Environment Variables**:
  - `DB_HOST`
  - `SPRING_DATASOURCE_PASSWORD`
  - `EUREKA_SERVER_ADDRESS`
- **Database**: MS SQL database hosted in a Docker container (container name: `restaurant-db`).

### 4. **User Service**

- **Technology**: Spring Boot
- **Purpose**: Communicates with **Okta** to retrieve currently logged in user's profile information.
- **Docker Container**: `user-service`
- **Port**: `8085`
- **Dependencies**:
  - Eureka Server
- **Environment Variables**:
  - `SPRING_OKTA_CLIENT_ID`
  - `SPRING_OKTA_CLIENT_SECRET`
  - `EUREKA_SERVER_ADDRESS`

### 5. **Payment Service**

- **Technology**: Spring Boot
- **Purpose**: Handles payment processing and payment transactions. Contains authorized endpoints for saving transaction details and retrieving transaction details. This service is called internally by Order service and it can't be used as a standalone.
- **Docker Container**: `payment-service`
- **Port**: `8083`
- **Dependencies**:
  - Eureka Server
  - Payment DB (`payment-db`)
- **Environment Variables**:
  - `DB_HOST`
  - `SPRING_DATASOURCE_PASSWORD`
  - `EUREKA_SERVER_ADDRESS`
- **Database**: MS SQL database hosted in a Docker container (container name: `payment-db`).

### 6. **Delivery Service**

- **Technology**: Spring Boot
- **Purpose**: Manages delivery orders and tracks delivery statuses. Contains authorized endpoints for creating new delivery records and retrieving delivery details. This service is called internally by Order service and it can't be used as a standalone. It also utilizes Kafka for dispatching an asynchronous event (delivery-topic).
- **Docker Container**: `delivery-service`
- **Port**: `8084`
- **Dependencies**:
  - Eureka Server
  - Kafka (for async communication)
  - Delivery DB (`delivery-db`)
- **Environment Variables**:
  - `DB_HOST`
  - `SPRING_DATASOURCE_PASSWORD`
  - `EUREKA_SERVER_ADDRESS`
- **Database**: MS SQL database hosted in a Docker container (container name: `delivery-db`).
- **Asynchronous Communication**:
  - Uses **Kafka** to communicate with the **Order Service** for async order updates.

### 7. **Order Service**

- **Technology**: Spring Boot
- **Purpose**: Manages customer orders. It contains authorized endpoints for order management. This is a central microservice that communicates with other services. It checks the availibility of the dish in a restaurant (Restaurant service), it initiates the payment process (Payment service) and if the payment is successful, it initiates the delivery process (Delivery service).
- **Docker Container**: `order-service`
- **Port**: `8082`
- **Dependencies**:
  - Eureka Server
  - Kafka (for async communication)
  - Delivery Service
  - Payment Service
  - Restaurant Service
  - Order DB (`order-db`)
- **Environment Variables**:
  - `DB_HOST`
  - `SPRING_DATASOURCE_PASSWORD`
  - `SPRING_OKTA_CLIENT_ID`
  - `SPRING_OKTA_CLIENT_SECRET`
  - `EUREKA_SERVER_ADDRESS`
- **Asynchronous Communication**:
  - Uses **Kafka** to communicate with the **Delivery Service** for updating order status.

## Asynchronous Communication Using Kafka

- **Kafka Setup**: The **Delivery Service** and **Order Service** communicate asynchronously using **Apache Kafka**. Kafka enables real-time, event-driven communication between microservices, which allows decoupling of services.
- **Kafka Topics**:
  - `delivery-topic`: Used for order status updates in Order service once the delivery in Delivery service is completed.

## Testing with JUnit4, Mockito, and WireMock

- **JUnit4**: Used for writing unit and integration tests to ensure the correctness of individual microservices.
- **Mockito**: A mocking framework used in unit tests to simulate service behavior and dependencies.
- **WireMock**: Used to simulate external APIs for testing integration and communication between services.

## Docker Containers

Each microservice contains a *Dockerfile* and is packaged as a **Docker container**. Additionally, the following services are also containerized:

- **Redis**: Used for load-balancing.
- **Kafka**: For event-driven communication between services.
- **Zookeeper**: Required for Kafka's coordination and management.
- **MS SQL Databases**: Each microservice (restaurant, order, payment and delivery) has its own database container.

## Docker-compose.yml file

Alongside the *Dockerfile* for each of the microservices, there is a `docker-compose.yml` file at the root of the project. This file is used to configure and run all the services, including microservices, Kafka, Redis, and the database containers. Below are the details of its construction:

### Versioning
The `docker-compose.yml` file uses version `3.8`, which supports advanced features such as health checks, secrets, and enhanced networking.

### Services

#### Microservices and Dependencies
Each microservice and its dependencies (Redis, Kafka, Zookeeper, MS SQL Server, etc.) are defined as separate services under the `services` section. Below are the key components for each service:

- **Image**: Specifies the Docker image for the service. For custom services, images are tagged with the project namespace (e.g., `fooddeliveryapp/<service-name>`).
- **Container Name**: Assigns a user-friendly name for easier identification during debugging.
- **Ports**: Maps internal container ports to external host ports for service accessibility.
- **Networks**: Ensures all services communicate within a shared Docker bridge network, `food-delivery-app_default`.
- **Environment Variables**: Passes configuration settings, such as database credentials or Okta client credentials, to services dynamically.

#### Service Dependencies
The `depends_on` directive is used to specify dependencies between services. For example, services like Kafka, Zookeeper, and Eureka must start before dependent microservices.  

### Databases

#### Dedicated Database Containers
Each microservice has a dedicated MS SQL Server database container to ensure:

- **Data Isolation**: Each service has its own database, preventing conflicts.
- **Scaling**: Services and databases can be scaled independently.

#### Configuration:
- **Volumes**: Used to initialize the databases using SQL scripts (e.g., `db-init-script.sql`).
- **Environment Variables**: 
  - `ACCEPT_EULA`: Indicates acceptance of the MS SQL Server license agreement.
  - `SA_PASSWORD`: Configures the admin password for the database.

### Environment Variables
Sensitive configurations, such as database passwords and Okta credentials, are passed as environment variables for security. 

- These variables can be securely stored in a `.env` file and referenced automatically during `docker-compose` execution.
- **Important**: The provided `docker-compose.yml` file references environment variables that represent personal database and Okta credentials. To test this project, you should set up your own versions of these variables in your local environment. Also, since **Okta** is used both as an auth provider and user registry, you should add some of your own test users to the portal to be able to test authentication and authorization in the app.

### Networking
All containers operate within the custom bridge network (`food-delivery-app_default`), enabling secure communication between services without exposing internal connections to the host machine.

### Running the Project

To build and run all services, use the following command:

```bash
docker-compose up --build
```

## Example of placing an order and getting order details

Once the containers are all up and running you can go to **http://localhost:9090/authentication/login** and login with the test user who is a **Customer** (you have to add some users and roles to your **Okta** portal for this beforehand). After successful user verification, you get back the reponse which contains *access-token*.  The value of that token should be sent inside the Request Headers when testing. In **Postman** you can then create a new POST request to route **http://localhost:9090/orders/process-order**. The body of the request should have some data (assuming you have also added some restaurant and dish data to the database):
```
```json
{
  "restaurantId": "replace-with-valid-restaurant-id",
  "paymentMode": "CARD",
  "items": [
    {
      "dishId": "replace-with-valid-dish-id,
      "quantity": 2
    },
    {
      "dishId": "replace-with-valid-dish-id",
      "quantity": 1
    }
  ]
}
```
If everything goes smoothly, the new order is saved, and you should get its ID back. Then, you can log in as a user who is an **Admin** and use the newly retrieved *access-token* in the header of the GET request to endpoint **http://localhost:9090/orders/<orderId>**
You should get order details for the newly placed order as the reponse in a format like so:
```
{
    "orderId": "806308bb-7abb-4666-a353-e3689677fe6d",
    "createdAt": "2024-12-09T09:16:55.778499",
    "status": "DELIVERED",
    "amount": 670.0,
    "madeBy": "janedoe@gmail.com",
    "dishes": [
        {
            "dishId": "aea59ee7-dca1-4fd4-8235-e29e9e32b275",
            "name": "California roll",
            "price": 260.0,
            "description": "Ukusna rolnica sa tunom, krem sirom, avokadom i susamom.",
            "availability": true
        },
        {
            "dishId": "a37ee368-f839-4d42-9829-e7254ca61a9f",
            "name": "Vege roll",
            "price": 150.0,
            "description": "Vege rolnica sa krastavcem, susamom i avokadom.",
            "availability": true
        }
    ],
    "paymentDetails": {
        "paymentId": "66401871-4d37-436d-a18f-9767dab052ef",
        "paymentMode": "CARD",
        "status": "SUCCESSFUL",
        "payedOn": "2024-12-09T09:16:57.517308"
    },
    "deliveryDetails": {
        "deliveryId": "f1817fe1-666c-4dd1-9c1b-5bd4ef28d4c0",
        "deliveryStatus": "DELIVERED",
        "initiatedAt": "2024-12-09T09:16:59.45091",
        "deliveredAt": "2024-12-09T09:17:09.595168"
    }
}
```

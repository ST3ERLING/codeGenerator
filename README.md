# CodeGenerator

CodeGenerator is a platform designed to automate software development by generating fully executable project structures. It guides users through metadata entry, entity definition (manually or via class diagrams), and dependency selection. 

---

## Folder Structure

The project is organized into the following components:

1. **CodeGenerator Microservice**
   - **Description**: Core service that processes inputs and generates project structures.
   - **Port**: 8080
   - **Path**: `/CodeGenerator/code-generator`

2. **Prompt Microservice**
   - **Description**: AI-driven module using API GPT for code suggestions and enhancements.
   - **Port**: 8081
   - **Path**: `/CodeGenerator/prompt`

3. **UserService Microservice**
   - **Description**: Manages user data and authentication.
   - **Port**: 8082
   - **Path**: `/CodeGenerator/user-service`
   - **Database**: MySQL

4. **API Gateway**
   - **Description**: Handles routing between microservices.
   - **Port**: 8888
   - **Path**: `/CodeGenerator/api-gateway`

5. **Front-End (React)**
   - **Description**: User-friendly interface for managing inputs and viewing project outputs.
   - **Port**: 3000
   - **Path**: `/CodeGenerator/frontend`

---

## Getting Started

### Prerequisites
- **Java 21**
- **Docker & Docker Compose**
- **MySQL**

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/ST3ERLING/codeGenerator.git
   cd CodeGenerator
   ```

2. Build and run the microservices:

   - **CodeGenerator Microservice**
     ```bash
     cd code-generator
     mvn spring-boot:run
     ```

   - **Prompt Microservice**
     ```bash
     cd ../prompt
     mvn spring-boot:run
     ```

   - **UserService Microservice**
     ```bash
     cd ../user-service
     mvn spring-boot:run
     ```

3. Start the API Gateway:

   ```bash
   cd ../api-gateway
   mvn spring-boot:run
   ```

4. Launch the front-end:

   ```bash
   cd ../frontend
   npm install
   npm start
   ```

5. Set up and run the database:

   ```bash
   docker run --name mysql-container -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=codegen -p 3306:3306 -d mysql:latest
   ```

6. Use Docker Compose for the complete environment:

   ```bash
   docker-compose up
   ```

---

## API Endpoints

| **Endpoint**          | **Description**                  |
|------------------------|----------------------------------|
| `/generate/**`         | Handles project generation tasks. |
| `/prompt/**`           | Routes AI-driven code suggestions. |
| `/user/**`             | Manages user operations.         |

---

## How It Works

1. **Step 1**: Users log in and input metadata.
2. **Step 2**: Define entities manually or upload a class diagram image.
3. **Step 3**: Specify dependencies for the project.
4. **Result**: The system generates an executable project structure with controllers, services, entities, and relationships.

---

## Tools and Technologies

- **Backend**: Spring Boot, API GPT
- **Frontend**: React, Tailwind CSS
- **Database**: MySQL
- **DevOps**: Docker, Docker Compose
- **Architecture**: Microservices connected via API Gateway

---

This README provides all the essential steps and information for running and understanding CodeGenerator. For more details, visit [GitHub Repository](https://github.com/ST3ERLING/codeGenerator.git).

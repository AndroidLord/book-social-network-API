## Book Social Network API Overview

The **Book Social Network API** is a comprehensive platform designed for book distribution and management. It is built using **Spring Boot**, **Spring JPA**, **Lombok**, **Spring Security**, and **Swagger UI**, following the **MVC architecture**. The API provides various functionalities related to book management, user authentication, feedback, and more.

## **Brief Overview:**
- User Authentication and Authorization
- Book Management
- Feedback Management
- Book History Management
- Security and Exception Handling
- Database

  
## Key Features

### 1. **User Authentication and Authorization**:
   - **JWT Authentication**: Secure endpoints with JSON Web Tokens.
   - **Email Verification**: Adds an extra layer of security with account activation through email verification.
   - Example API Paths:
     - **Register User**: `POST https://localhost:8080/auth/register`
     - **Authenticate User**: `POST https://localhost:8080/auth/authenticate`
     - **Activate Account**: `GET https://localhost:8080/auth/activate-account?token={token}`

### 2. **Book Management**:
   - **CRUD Operations**: Manage books with functionalities to create, update, delete, and retrieve books.
   - **Archiving and Publishing**: Books can be archived or published.
   - **Two-Way Book Return Policy**: Handles book lending with agreements between lender and owner.
   - **Book Cover Management**: Allows uploading and updating of book cover images.
   - **Pagination**: Lists of books are paginated for better performance.
   - Example API Paths:
     - **Create Book**: `POST https://localhost:8080/book`
     - **Get Book by ID**: `GET https://localhost:8080/book/{book-id}`
     - **Get All Books**: `GET https://localhost:8080/book`
     - **Get Books by Owner**: `GET https://localhost:8080/book/owner`
     - **Get Borrowed Books**: `GET https://localhost:8080/book/borrowed`
     - **Get Returned Books**: `GET https://localhost:8080/book/returned`
     - **Update Shareable Status**: `PATCH https://localhost:8080/book/shareable/{book-id}`
     - **Update Archive Status**: `PATCH https://localhost:8080/book/archive/{book-id}`
     - **Borrow Book**: `POST https://localhost:8080/book/borrow/{book-id}`
     - **Return Borrowed Book**: `POST https://localhost:8080/book/borrow/return/{book-id}`
     - **Approve Return**: `POST https://localhost:8080/book/borrow/return/approve/{book-id}`
     - **Upload Book Cover**: `POST https://localhost:8080/book/cover/{book-id}`

### 3. **Feedback Management**:
   - **CRUD Operations**: Users can provide feedback on books, with pagination support.
   - Example API Paths:
     - **Save Feedback**: `POST https://localhost:8080/feedback`
     - **Get Feedback by Book ID**: `GET https://localhost:8080/feedback/book/{bookId}`

### 4. **Book History Management**:
   - **Track User and Book History**: Keep detailed records of book transactions and user interactions.

### 5. **Security and Exception Handling**:
   - **Custom Exceptions**: Implement custom exception handling for various error scenarios.
   - **Exception Handlers**: Manage exceptions gracefully across the application.
   - **Filter for Unauthenticated Access**: Registration and authentication endpoints are accessible without JWT verification.

### Technical Stack

- **Backend Framework**: Spring Boot
- **Data Persistence**: Spring JPA (Java Persistence API)
- **Security**: Spring Security with JWT for authentication and email verification for account activation
- **Database**: PostgreSQL
- **Utilities**: Lombok for reducing boilerplate code
- **Documentation**: Swagger UI for API documentation
- **Architecture**: MVC (Model-View-Controller)
- **File Handling**: Multipart file handling for book cover uploads

This project provides a robust framework for managing a social network for book distribution, with a focus on secure access, detailed record-keeping, and user interaction.

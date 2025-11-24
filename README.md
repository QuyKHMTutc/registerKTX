# 🏢 Dormitory Management System (Hệ Thống Quản Lý Ký Túc Xá)

A comprehensive web-based dormitory management system built with **Spring Boot** and **Thymeleaf**, designed to streamline student housing operations including room allocation, contract management, and payment processing.

## 📋 Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Getting Started](#-getting-started)
- [API Documentation](#-api-documentation)
- [Security](#-security)
- [Screenshots](#-screenshots)

## ✨ Features

### For Students
- 📝 **Online Application**: Submit dormitory applications during open registration periods
- 🏠 **Room Preferences**: Request specific rooms or room types
- 💰 **Payment Tracking**: View invoices and payment history
- 📄 **Contract Management**: Access and review housing contracts
- 🔔 **Notifications**: Receive real-time updates on application status

### For Administrators
- ✅ **Application Review**: Approve or reject student applications with detailed audit trails
- 🏢 **Room Management**: Create, update, and monitor room availability
- 👥 **User Management**: Manage student and staff accounts
- 📊 **Dashboard**: Overview of occupancy rates, pending applications, and financial metrics
- 🔔 **Admin Notifications**: Get notified when new applications are submitted
- 📅 **Registration Periods**: Configure open/close dates for application submissions

### System Features
- 🔐 **Role-Based Access Control**: Separate interfaces for students and administrators
- 🛡️ **Security Headers**: Protection against XSS, clickjacking, and CSRF attacks
- ✅ **Input Validation**: Jakarta Bean Validation for data integrity
- 📱 **Responsive Design**: Modern UI with glassmorphism effects and mobile support
- 🌐 **Internationalization**: Vietnamese language support throughout

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security with custom authentication
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA (Hibernate)
- **Validation**: Jakarta Bean Validation
- **Build Tool**: Maven

### Frontend
- **Template Engine**: Thymeleaf
- **CSS Framework**: Bootstrap 5
- **Icons**: Font Awesome
- **Fonts**: Google Fonts (Inter, Outfit)
- **JavaScript**: Vanilla JS with AJAX

### DevOps
- **Containerization**: Docker & Docker Compose
- **Version Control**: Git

## 🏗️ Architecture

### Design Patterns
- **MVC Pattern**: Clear separation of concerns
- **Service Layer**: Business logic abstraction
- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Data transfer objects for API boundaries
- **Global Exception Handling**: Centralized error management

### Project Structure
```
src/main/java/org/example/
├── config/          # Configuration classes
├── constant/        # Application constants
├── controller/      # REST and View controllers
├── dto/             # Data Transfer Objects
├── exception/       # Custom exceptions and global handler
├── model/           # JPA entities
├── repository/      # Spring Data repositories
├── security/        # Security configuration
├── service/         # Business logic layer
└── util/            # Utility classes

src/main/resources/
├── static/          # CSS, JS, images
└── templates/       # Thymeleaf templates
    ├── admin/       # Admin interface
    ├── auth/        # Login/Register pages
    └── user/        # Student interface
```

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- MySQL 8.0
- Maven 3.6+
- Docker (optional)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/dactaphanmem.git
   cd dactaphanmem
   ```

2. **Configure Database**
   
   Create a MySQL database:
   ```sql
   CREATE DATABASE ktx_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

   Update `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ktx_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the Application**
   - Application: http://localhost:8080
   - Default Admin: `admin` / `admin123`
   - Default Student: `SV001` / `password`

### Using Docker

```bash
docker-compose up -d
```

## 📡 API Documentation
The project uses **Swagger/OpenAPI** for automated API documentation.

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Key Endpoints

#### Admin
- `GET /api/admin/phong`: Get all rooms
- `POST /api/admin/don-dang-ky/{id}/approve`: Approve application

#### User
- `POST /api/user/don-dang-ky`: Submit application
- `GET /api/user/hoa-don`: Get invoices

## 🔒 Security

### Implemented Security Measures
- ✅ **CSRF Protection**: Enabled for all state-changing operations
- ✅ **Password Encryption**: BCrypt hashing
- ✅ **Session Management**: Single concurrent session per user
- ✅ **Security Headers**:
  - `X-Frame-Options: DENY` (Clickjacking protection)
  - `Content-Security-Policy` (XSS mitigation)
  - `X-Content-Type-Options: nosniff`
- ✅ **Input Validation**: Server-side validation on all DTOs
- ✅ **Role-Based Access Control**: Separate permissions for students and admins

### Authentication Flow
1. User submits credentials via `/auth/login`
2. Spring Security validates against database
3. Custom success handler redirects based on role
4. Session created with CSRF token

## 📸 Screenshots

### Student Interface
![Student Dashboard](docs/images/student-dashboard.png)
*Modern glassmorphism design with intuitive navigation*

### Admin Interface
![Admin Dashboard](docs/images/admin-dashboard.png)
*Comprehensive admin panel with real-time notifications*

### Application Review
![Application Review](docs/images/application-review.png)
*Streamlined application approval workflow*

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your LinkedIn](https://linkedin.com/in/yourprofile)
- Email: your.email@example.com

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Bootstrap for the responsive UI components
- Font Awesome for the icon library

---

⭐ **Star this repository if you find it helpful!**

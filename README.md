# User Management System
# Overview
This is a Spring Boot application for managing users with role-based access control. It includes features such as user registration, login, and admin-only operations like viewing, adding, editing, and deleting users.

# Features
Public Features
Home Page (/): Redirects to the login page.
Login (/login): Custom login page.
Authenticated users are redirected to the user list (/users).
Registration (/register):
Allows new users to create an account.
Validates user input for unique email and password length.
Admin-Only Features
View All Users (/users):

# Lists all registered users.
Accessible only by users with the ADMIN role.
Add User (/add):

Provides a form to add a new user.
Validates user input for unique email and password length.
Edit User (/edit/{id}):
Allows admins to update user details.
Includes validation for password length.
Delete User (/delete/{id}):
Deletes a user by their ID.
Prevents an admin from deleting their own account.
Validates user existence before deletion.

# Validation Rules
Email: Must be unique and in a valid email format.
Password: Must not be empty and at least 7 characters long.
Other Fields: Validated based on application-specific requirements.

# Security
Authentication:
Uses Spring Security with a custom UserDetailsService to authenticate users by their email.
Authorization:
Role-based access control with @PreAuthorize.
Only admins can access user management operations.
Password Encryption:
Uses BCryptPasswordEncoder for securely hashing passwords.

# Technology Used
# Backend
1. Java 17: Programming language used to build the application.
2. Spring Boot 3.x: Framework for rapid application development.
3. Spring Security: For authentication, authorization, and password encryption.
4. Spring Data JPA: For database interaction using the Java Persistence API (JPA).
5. Thymeleaf: For building server-side HTML templates.
6. MySql: Used to store user's data.
# Frontend
HTML5, CSS3: For designing the user interface.
Thymeleaf: Integrated template engine for dynamic content rendering.

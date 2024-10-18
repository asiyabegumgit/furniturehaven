# Furniture Haven

Welcome to the Furniture Haven project! This application provides a comprehensive platform for browsing and managing furniture products.

## Table of Contents

- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Admin Page](#admin-page)
- [Security Configuration](#security-configuration)
- [Spring Boot](#spring-boot)
- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Overview

Furniture Haven is designed to offer an intuitive shopping experience. The application features a dynamic header and footer that adapt based on user authentication status.

### Header Fragment

The header includes:
- **Brand Logo and Name**: Displays the brand name "Furniture Haven."
- **Search Bar**: Allows users to search for products by category and keyword.
- **Navigation Links**: Links to the home page, user login, and cart.
- **User Authentication**: Displays different options based on whether the user is logged in or anonymous.

### Footer Fragment

The footer includes:
- **Company Links**: Links to the About Us, Return Policy, and Privacy Policy pages.
- **Support Links**: Links to FAQ, Contact Us, and Shipping pages.
- **Social Media Links**: Icons linking to Facebook, Instagram, and Twitter.

## Technologies Used

- **HTML5**: For the structure of the web pages.
- **CSS3**: For styling and layout.
- **JavaScript**: For dynamic interactions (e.g., dropdown menus).
- **Thymeleaf**: For server-side rendering and templating.
- **Font Awesome**: For icons in navigation.
- **Spring Boot**: For building the application with minimal configuration.

## Features

- **User Registration**: Sign up for new accounts.
- **User Authentication**: Login functionality with options for "Forgot Password" and "Reset Password."
- **Dynamic Search Functionality**: Easily search for products.
- **Furniture Product List Page**: Browse through available products.
- **Product Detail Page**: View details for each product.
- **Add to Cart Functionality**: Easily add products to the shopping cart.
- **Checkout and Place Order**: Complete the purchase process.
- **Responsive Design**: Adjusts to different screen sizes.
- **Dropdown Menus**: For user actions.
- **Admin Functionality**: Manage products effectively.

## Admin Page

The admin page allows administrators to manage products effectively. Key components include:

- **File Upload**: An option to upload CSV files containing product data.
- **Search Functionality**: Allows filtering products by category and keyword.
- **Product List**: Displays existing products in a table format, with options to edit or delete products.

## Security Configuration

The application uses Spring Security to manage user authentication and authorization. Below are key components of the security configuration:

- **Public Access**: Certain paths (like login, home, and product listings) are accessible to all users.
- **Role-Based Access Control**: Different roles (like CUSTOMER and ADMIN) are used to restrict access to specific features.
- **Custom Login Handling**: Users are redirected to different pages based on their roles upon successful login.
- **Password Encoding**: Passwords are securely hashed using `BCryptPasswordEncoder`.

## Spring Boot

Furniture Haven is built using **Spring Boot**, a powerful framework that simplifies the development of Java applications. Spring Boot provides:

- **Auto-Configuration**: Automatically configures Spring features based on the dependencies present in the project.
- **Standalone Applications**: Easily run applications as standalone Java applications.
- **Production-Ready Features**: Out-of-the-box support for metrics, health checks, and externalized configuration.
- **Microservices Ready**: Makes it easy to build microservices by providing built-in support for Spring Cloud.


## Installation

To set up the project locally, follow these steps:

1. Clone the repository:
   
   git clone <repository-url>
   
Navigate to the project directory:
cd furniture-haven

Build the project using Maven:
bash

mvn clean install
Usage
To run the application, use the following command:

mvn spring-boot:run
Visit http://localhost:8080 in your web browser to access the application.



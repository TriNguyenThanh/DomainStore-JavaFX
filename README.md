# DomainStore

## Introduction

DomainStore is a desktop application developed using Java Swing, allowing users to easily buy and sell domain names. The application supports domain status checking, shopping cart functionality, online payment, and transaction history.

## Features

- View domain prices and ownership status.
- Register and log in to a customer account.
- Purchase domain names after checking their validity through the RDAP API.
- Online payment via VNPay API.
- Check transaction history after successful payment.

## Technologies Used

- **Programming Language**: Java
- **Development Tool**: Intellij
- **Database**: MySQL
- **Framework**: Maven
- **Integrated APIs**: RDAP, VNPay

## Installation and Running the Application

### System Requirements

- **Java JDK** 11 or later
- **MySQL Server** 8.0
- **NetBeans** (or any IDE supporting Maven)

### Installation Guide

1. Clone the repository:
   ```sh
   git clone https://github.com/TriNguyenThanh/DomainStore.git
   cd domain-marketplace
   ```
2. Configure the MySQL database:
   - Create a new database.
   - Import the `domainmanagement.sql` file included in the project.
   - Update MySQL connection details in the application configuration file.
3. Run the application using Intellij or Maven:
   ```sh
   mvn clean install
   mvn exec:java
   ```

## Usage Guide

1. Open the application, register, or log in to your account.
2. Search for the desired domain name and check its ownership status.
3. Add the domain to the shopping cart and proceed with payment via VNPay.
4. Check the transaction history after successful payment.

## Contribution

All contributions are welcome! Please create a **pull request** or open an **issue** for any suggestions or bugs.

## Contact

- **Email**: [tringuyenntt1505@gmail.com](mailto:tringuyenntt1505@gmail.com)
- **GitHub**: [TriNguyenThanh/DomainStore](https://github.com/TriNguyenThanh/DomainStore.git)

---

**DomainStore - 2025**
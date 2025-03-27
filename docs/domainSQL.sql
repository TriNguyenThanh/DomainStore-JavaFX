-- Tạo database
DROP DATABASE IF EXISTS DomainManagement;
CREATE DATABASE IF NOT EXISTS DomainManagement;
USE DomainManagement;
-- Bảng người dùng
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL, 
    email VARCHAR(255) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    cccd VARCHAR(20) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('user', 'admin') DEFAULT 'user',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Thêm tài khoản người dùng đúng cách (không cần chỉ định ID)
INSERT INTO users (full_name, email, phone, cccd, password_hash, role)
VALUES 
('Nguyễn Văn A', 'nguyenvana@example.com', '0987654321', '027205011960', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0987654321 password: pass123456@
('Trần Thị B', 'tranthib@example.com', '0912345678', '027205011961', '$argon2i$v=19$m=65536,t=3,p=1$LdOwimdB3obvKIlQQSxO7A$Ty2IDQ8LH8+lpNYSUfXdMuqjw1s/KjxzzLGWSRg76wQ', 'user'),
-- user:0912345678 password: +yTVq&!a196,
('Lê Văn C', 'levanc@example.com', '0923456789', '027205011962', '$argon2i$v=19$m=65536,t=3,p=1$Q05CH4ZAAx0QUErpJSaULw$Fdp72yb2HC82vjgcthIJn53g5GXxQhPAY+h8KOdRXns', 'user'),
-- user:0923456789 password: xK5MH5mhuh{)
('Phạm Minh D', 'phamminhd@example.com', '0934567890', '027205011963', '$argon2i$v=19$m=65536,t=3,p=1$kNwVly9ImSz1L1URUuLfpQ$pi26c5+SiQkJuApmzeGAvxmBJLKVAxXhgbmKfBe46TM', 'user'),
-- user:0934567890 password: |Srm7AZ!FV{[
('Ngô Thị E', 'ngothie@example.com', '0945678901', '027205011964', '$argon2i$v=19$m=65536,t=3,p=1$uM/0YWIZVLTs7X/5q1aPHA$Xz74lXljO8WyM2iujg6VOnjuv4PJMN9HB8Kl9ptFEs0', 'user'),
-- user:0945678901 password: 7(W}Q4f'oSmO
('Hoàng Văn F', 'hoangvanf@example.com', '0956789012', '027205011965', '$argon2i$v=19$m=65536,t=3,p=1$8Ku4VUecVTAYrJ3xYifwLg$cG4472+kX7cREZxGjibtxgi0mAGeVNrFGH3+qYESHlw', 'user'),
-- user:0956789012 password: Y7r<gHclQd2L
('Bùi Thị G', 'buithig@example.com', '0967890123', '027205011966', '$argon2i$v=19$m=65536,t=3,p=1$C/LJL5jYwDf+DOg/4bqKxA$FFz+VbmyyBQ+JQCXJrR4l/YS4a420537CUctikd4I+E', 'user'),
-- user:0967890123 password: jIn>@m%u^>Bo
('Đặng Văn H', 'dangvanh@example.com', '0978901234', '027205011967', '$argon2i$v=19$m=65536,t=3,p=1$HvyY7UFzCsdRpoQX3yoGoQ$O0SaLMh3aJ6jWoAlv/9MmShQB5hIechd33mYfiMkF4w', 'user'),
-- user:0978901234 password: ~OIo{nHIFZP
('Võ Minh I', 'vominhi@example.com', '0989012345', '027205011968', '$argon2i$v=19$m=65536,t=3,p=1$8Q0I7BRCJis+FeKIXyzdkA$bhcSURF3RU8FD3JL8WeXc6M6hdxjRnhbNnYyN0WqY6Y', 'user'),
-- user:0989012345 password: (teS(oI=,)RU
('Nguyễn Thị J', 'nguyenthij@example.com', '0990123456', '027205011969', '$argon2i$v=19$m=65536,t=3,p=1$v2u8f25JRw+2TNh7MFVsZQ$zTFyCH1O+6hDiE0Ge7yaQfkKHDO0rey+Dh1oMwzvv+Y', 'user'),
-- user:0990123456 password: OP=)b[/Rw#+/
('Đỗ Văn K', 'dovank@example.com', '0901234567', '027205011970', '$argon2i$v=19$m=65536,t=3,p=1$PrbWwD0raM2W1O1X5Cet5g$4AZgEXc+/TXPPjev0lDfwIqS3g0H5ncxnZw+YnZ7vqg', 'user');
-- user:0901234567 password: h.u_XZE^9Y[e

-- bảng quản lý level of domain
CREATE TABLE TopLevelDomain (
	id INT AUTO_INCREMENT PRIMARY KEY,
	TLD_text CHAR(10) NOT NULL,
	price INT UNSIGNED NOT NULL
);

-- Thêm một số topLevelDomain
INSERT INTO TopLevelDomain (TLD_text, price)
VALUES 
('.com', 299000),
('.net', 39000),
('.site', 39000),
('.xyz', 49000),
('.info', 179000),
('.vn', 449000),
('.io.vn', 49000);

-- Bảng quản lý tên miền
CREATE TABLE domains (
    id INT AUTO_INCREMENT PRIMARY KEY,
    domain_name VARCHAR(255) NOT NULL,
    tld_id int not null,
    foreign key (tld_id) references TopLevelDomain(id),
    status ENUM('available', 'sold') DEFAULT 'available',
    active_date DATE null,
    years int null,
    owner_id INT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Thêm một số tên miền mẫu
INSERT INTO domains (domain_name, tld_id, status) 
VALUES 
('example', 1, 'available'),
('example', 3, 'available'),
('testdomain', 6, 'available'),
('mywebsite', 1, 'available'),
('yourdomain', 2, 'available'),
('newproject', 3, 'available'),
('techhub', 4, 'available'),
('globalinfo', 5, 'available'),
('vietnamexpert', 6, 'available'),
('startupvn', 7, 'available'),
('coolbrand', 1, 'available'),
('bestservices', 2, 'available'),
('amazingtech', 3, 'available');

-- Thêm chỉ mục để tối ưu truy vấn
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_domains_name ON domains(domain_name);

CREATE TABLE Transactions (
    id CHAR(10) PRIMARY KEY NOT NULL,
    user_id INT NOT NULL,
    transaction_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
INSERT INTO Transactions (id, user_id, transaction_date)
VALUES 
('HD001', 1, '2025-03-02'),
('HD002', 2, '2025-03-05'),
('HD003', 3, '2025-03-10'),
('HD004', 4, '2025-03-15'),
('HD005', 5, '2025-03-20');

CREATE TABLE Transactions_info (
    transactions_id CHAR(10) NOT NULL,
    domain_id INT NOT NULL,
   -- domain_years INT NOT NULL,
    price INT UNSIGNED NOT NULL, 
    
    FOREIGN KEY (transactions_id) REFERENCES Transactions(id),
    FOREIGN KEY (Domain_id) REFERENCES domains(id),
    PRIMARY KEY(Domain_id, transactions_id)
);
INSERT INTO Transactions_info (Transactions_id, Domain_id, price)
VALUES 
('HD001', 1, 598000),
('HD002', 2, 449000),  -- User 2 mua domain testdomain (.vn)
('HD003', 3, 39000),   -- User 3 mua domain newproject (.site)
('HD004', 4, 179000),  -- User 4 mua domain globalinfo (.info)
('HD005', 5, 299000);  -- User 5 mua domain coolbrand (.com)

CREATE TABLE PaymentMethod (
    id INT AUTO_INCREMENT PRIMARY KEY,
    method ENUM('VNPay', 'MoMo', 'CreditCard', 'ZaloPay')
);
INSERT INTO PaymentMethod (method)
VALUES
('VNPay'),
('MoMo'),
('CreditCard'),
('ZaloPay');

CREATE TABLE PaymentHistory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id CHAR(10) NOT NULL,
    payment_id CHAR(10) NOT NULL,
    payment_method INT,
    payment_status ENUM('completed', 'failed'),  
    payment_date DATE,
    
    FOREIGN KEY (transaction_id) REFERENCES Transactions(id),
    FOREIGN KEY (payment_method) REFERENCES PaymentMethod(id)
);

INSERT INTO PaymentHistory (transaction_id, payment_id, payment_method, payment_status, payment_date)
VALUES
('HD001', '68765486', 1, 'completed', '2025-03-02'),
('HD002', '68765487', 2, 'completed', '2025-03-05'),
('HD003', '68765488', 3, 'completed', '2025-03-10'),
('HD004', '68765489', 1, 'completed', '2025-03-15'),
('HD005', '68765490', 4, 'failed', '2025-03-20'); -- Thanh toán thất bại
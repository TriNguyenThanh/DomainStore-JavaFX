DROP DATABASE DOMAINMANAGEMENT;
CREATE DATABASE DOMAINMANAGEMENT;
USE DOMAINMANAGEMENT;

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

CREATE TABLE TopLevelDomain (
	id INT AUTO_INCREMENT PRIMARY KEY,
	TLD_text CHAR(10) NOT NULL,
	price INT UNSIGNED NOT NULL
);

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

CREATE TABLE carts (
	id int auto_increment PRIMARY KEY,
    cus_id INT NOT NULL,
    domain_id INT NOT NULL,
    years INT CHECK (years > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (cus_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (domain_id) REFERENCES domains(id) ON DELETE CASCADE
);
-- Thêm chỉ mục để tối ưu truy vấn
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_domains_name ON domains(domain_name);

CREATE TABLE Transactions (
    id CHAR(10) PRIMARY KEY NOT NULL,
    user_id INT NOT NULL,
    transaction_date DATE NOT NULL,
    transaction_status ENUM('pendingConfirm', 'pendingPayment', 'completed', 'cancelled') DEFAULT 'pendingConfirm',
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE Transactions_info (
    transactions_id CHAR(10) NOT NULL,
    domain_id INT NOT NULL,
    price INT UNSIGNED NOT NULL, 
    
    FOREIGN KEY (transactions_id) REFERENCES Transactions(id),
    FOREIGN KEY (Domain_id) REFERENCES domains(id),
    PRIMARY KEY(Domain_id, transactions_id)
);

CREATE TABLE PaymentMethod (
    id INT AUTO_INCREMENT PRIMARY KEY,
    method ENUM('VNPay', 'MoMo', 'CreditCard', 'ZaloPay')
);

CREATE TABLE PaymentHistory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id CHAR(10) NOT NULL,
    payment_id CHAR(10) NOT NULL DEFAULT '',
    payment_method INT,
    payment_status ENUM('completed', 'failed'),  
    payment_date DATE,
    
    FOREIGN KEY (transaction_id) REFERENCES Transactions(id),
    FOREIGN KEY (payment_method) REFERENCES PaymentMethod(id)
);
INSERT INTO users (full_name, email, phone, cccd, password_hash, role)
VALUES 
('Nguyễn Thành Trí', 'tringuyen@example.com', '0987654321', '027205011960', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'admin'),
-- user:0987654321 password: pass123456@
('Âu Dương Tấn', 'duongtan@example.com', '0912345678', '027205011961', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'admin'),
-- user:0912345678 password: pass123456@
('Lê Nguyễn Anh Dự', 'dule@example.com', '0923456789', '027205011962', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'admin'),
-- user:0923456789 password: pass123456@
('Phạm Anh Dũng', 'dung.pham@example.com', '0934567890', '027205011963', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0934567890 password: pass123456@
('Ngô Thanh Hoa', 'hoa.ngo@example.com', '0945678901', '027205011964', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0945678901 password: pass123456@
('Hoàng Tuấn Khang', 'khang.hoang@example.com', '0956789012', '027205011965', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0956789012 password: pass123456@
('Bùi Diễm Lan', 'lan.bui@example.com', '0967890123', '027205011966', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0967890123 password: pass123456@
('Đặng Thành Nam', 'nam.dang@example.com', '0978901234', '027205011967', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0978901234 password: pass123456@
('Võ Minh Nhật', 'nhat.vo@example.com', '0989012345', '027205011968', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0989012345 password: pass123456@
('Nguyễn Thị Oanh', 'oanh.nguyen@example.com', '0990123456', '027205011969', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0990123456 password: pass123456@
('Đỗ Quang Phúc', 'phuc.do@example.com', '0901234567', '027205011970', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user');
-- user:0901234567 password: pass123456@

INSERT INTO TopLevelDomain (TLD_text, price) 
VALUES 
('.com', 299000),
('.net', 39000),
('.org', 299000),
('.info', 179000),
('.biz', 99000),
('.co', 499000),
('.io', 890000),
('.ai', 1499000),
('.me', 299000),
('.tv', 399000),
('.xyz', 49000),
('.love', 59000),
('.vn', 449000),
('.dev', 690000),
('.site', 39000),
('.online', 39000),
('.cloud', 59000),
('.click', 59000),
('.group', 99000),  -- Thêm .group
('.mom', 129000),   -- Thêm .mom
('.asia', 69000),   -- Thêm .asia
('.icu', 49000),    -- Thêm .icu
('.art', 59000),    -- Thêm .art
('.blog', 59000);   -- Thêm .blog

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
('amazingtech', 3, 'available'),
('supercool', 1, 'available'),
('fastservice', 2, 'available'),
('amazingproject', 3, 'available'),
('yourbrand', 4, 'available'),
('nextbigthing', 5, 'available'),
('enterprisehub', 6, 'available'),
('futurenow', 7, 'available'),
('techstartup', 8, 'available'),
('digitalworld', 9, 'available'),
('smartcity', 10, 'available'),
('globalmarket', 11, 'available'),
('nextlevel', 12, 'available'),
('connectasia', 13, 'available'),
('cloudhub', 14, 'available'),
('universeclick', 15, 'available'),
('amazingblog', 16, 'available'),
('futurecloud', 17, 'available'),
('bestclick', 18, 'available'),
('stronggroup', 19, 'available'),
('supermom', 20, 'available'),
('techasia', 21, 'available'),
('globalbusiness', 22, 'available'),
('creativeart', 23, 'available'),
('topblog', 24, 'available');


INSERT INTO carts (cus_id, domain_id, years) VALUES
(4, 2, 2),
(4, 3, 1),
(5, 6, 3),
(6, 7, 1),
(7, 8, 2),
(8, 9, 5),
(9, 10, 1),
(10, 11, 4),
(11, 12, 2),
(4, 5, 3),
(5, 7, 2),
(6, 8, 1),
(7, 9, 4),
(8, 10, 5),
(9, 11, 2),
(10, 12, 3),
(11, 13, 1),
(5, 14, 2),
(6, 15, 3);

INSERT INTO Transactions (id, user_id, transaction_date, transaction_status)
VALUES
('HD001', 1, '2025-03-26', 'completed'),
('HD002', 3, '2025-03-26', 'completed'),
('HD003', 5, '2025-03-25', 'pendingPayment'),
('HD004', 2, '2025-03-24', 'cancelled'),
('HD005', 6, '2025-03-27', 'completed'),
('HD006', 7, '2025-03-28', 'pendingPayment'),
('HD007', 8, '2025-03-29', 'completed'),
('HD008', 9, '2025-03-30', 'cancelled'),
('HD009', 10, '2025-03-31', 'completed'),
('HD010', 11, '2025-04-01', 'pendingConfirm');

INSERT INTO Transactions_info (transactions_id, domain_id, price)
VALUES
('HD001', 1, 299000),
('HD001', 4, 299000),
('HD002', 5, 39000),
('HD003', 6, 39000),
('HD003', 7, 49000),
('HD003', 8, 179000),
('HD005', 3, 299000),
('HD005', 6, 499000),
('HD006', 7, 890000),
('HD007', 9, 1499000),
('HD007', 10, 299000),
('HD009', 11, 399000),
('HD010', 12, 299000);
-- Giao dịch HD004: không có domain nào (có thể do bị huỷ)

INSERT INTO PaymentMethod (method)
VALUES
('VNPay'),
('MoMo'),
('CreditCard'),
('ZaloPay');

INSERT INTO PaymentHistory (transaction_id, payment_method, payment_status, payment_date)
VALUES
('HD001', 1, 'completed', '2025-03-26'),
('HD002', 2, 'completed', '2025-03-26'),
('HD003', 3, 'failed', '2025-03-25'),
('HD005', 2, 'completed', '2025-03-27'),
('HD006', 3, 'failed', '2025-03-28'),
('HD007', 1, 'completed', '2025-03-29'),
('HD009', 4, 'completed', '2025-03-31');









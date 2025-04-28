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
('.com', 299000),	-- 1
('.net', 39000),	-- 2
('.org', 299000),	-- 3
('.info', 179000),	-- 4
('.biz', 99000),	-- 5
('.co', 499000),	-- 6
('.io', 890000),	-- 7
('.ai', 1499000),	-- 8
('.me', 299000),	-- 9
('.tv', 399000),	-- 10
('.xyz', 49000),	-- 11
('.love', 59000),	-- 12
('.vn', 449000),	-- 13
('.dev', 690000),	-- 14
('.site', 39000),	-- 15
('.online', 39000),	-- 16
('.cloud', 59000),	-- 17
('.click', 59000),	-- 18
('.group', 99000),  -- Thêm .group  19
('.mom', 129000),   -- Thêm .mom	20
('.asia', 69000),   -- Thêm .asia	21
('.icu', 49000),    -- Thêm .icu	22
('.art', 59000),    -- Thêm .art	23
('.blog', 59000);   -- Thêm .blog	24

INSERT INTO domains (domain_name, tld_id, status, active_date, years, owner_id)
VALUES



('diamonielts', 1, 'available', null, null ,null),
('example', 3, 'sold', '2024-03-15 17:00:00', 1, 3),
('testdomain', 6, 'sold', '2024-03-15 17:00:00', 1, 3),
('mywebsite', 1, 'sold', '2024-03-15 17:00:00', 1, 3),
('yourdomain', 2, 'sold', null, 1, 2),
('newproject', 3, 'sold', null, 1, 2),
('spicydonut', 4, 'available', null, null ,null),
('globalban', 5, 'available', null, null ,null),
('vietnamexpert', 5, 'available', null, null ,null),
('startupvn', 5, 'available', null, null ,null),
('hotbrandno2', 1, 'available', null, null ,null),
('bestservices', 2, 'sold', null, 1, 2),
('surprisedtech', 3, 'available', null, null ,null),
('supercool', 1, 'sold', '2024-04-20 10:00:00', 1, 4),
('fastservice', 2, 'sold', '2024-01-21 19:30:00', 2, 1),
('amazingproject', 3, 'sold', '2024-04-20 10:00:00', 1, 4),
('yourbrandnah', 4, 'available', null, null ,null),
('nextbigthing', 5, 'available', null, null ,null),
('enterprisehub', 6, 'sold', '2024-04-20 10:00:00', 1, 4),
('futurenow', 24, 'available', null, null ,null),
('techstartup', 8, 'sold', null, 1, 5),
('digitalworld', 9, 'sold', null , 1, 8),
('smartcity', 10, 'sold', null , 1, 8),
('globalmarket', 11, 'sold', null , 1, 8),
('nextlevel', 12, 'sold', '2024-11-25 9:30:00', 1, 11),
('connectasia', 13, 'sold', null, 1, 5),
('cloudhub', 14, 'sold', null, 1, 5),
('universeclick', 15, 'available', null, null ,null),
('amazingblog', 16, 'available', null, null ,null),
('futurecloud', 17, 'sold', '2024-11-25 9:30:00', 1, 11),
('bestclickno1', 18, 'available', null, null ,null),
('stronggroup', 19, 'available', null, null ,null),
('supermom', 20, 'sold', '2025-03-15 11:20:00', 1, 9),
('techasia', 21, 'sold', '2025-03-15 11:20:00', 1, 9),
('globalbusiness', 22, 'sold', '2025-03-15 11:20:00', 1, 9),
('creativeart', 23, 'available', null, null ,null),
('topblog', 24, 'available', null, null ,null);

INSERT INTO carts (cus_id, domain_id, years) VALUES
(1, 1, 1),
(1, 20, 1),
(2, 11, 1),
(2, 13, 1),
(6, 31, 1),
(6, 36, 1),
(6, 37, 1),
(7, 7, 1),
(7, 8, 1),
(7, 9, 1),
(7, 10, 1),
(10, 17, 1),
(10, 18, 1),
(10, 28, 1),
(10, 32, 1);


INSERT INTO Transactions (id, user_id, transaction_date, transaction_status) VALUES
('HD001', 1, '2024-01-21', 'completed'),
('HD002', 2, '2024-01-22', 'cancelled'),
('HD003', 2, '2024-02-10', 'pendingConfirm'),
('HD004', 3, '2024-03-15', 'completed'),
('HD005', 7, '2024-03-20', 'cancelled'),
('HD006', 4, '2024-04-20', 'completed'),
('HD007', 8, '2024-08-10', 'pendingConfirm'),
('HD008', 10, '2024-08-20', 'cancelled'),
('HD009', 11, '2024-11-25', 'completed'),
('HD010', 9, '2025-03-15', 'completed'),
('HD011', 5, '2025-03-25', 'pendingPayment');


INSERT INTO Transactions_info (transactions_id, domain_id, price) VALUES
-- HD001
('HD001', 15, 39000),

-- HD002 (cancelled)
('HD002', 11, 299000),
('HD002', 13, 299000),

-- HD003
('HD003', 5, 39000),
('HD003', 12, 299000),
('HD003', 6, 179000),

-- HD004
('HD004', 2, 299000),
('HD004', 3, 499000),
('HD004', 4, 299000),

-- HD005 (cancelled)
('HD005', 7, 890000),
('HD005', 8, 179000),
('HD005', 9, 1499000),
('HD005', 10, 299000),

-- HD006
('HD006', 14, 299000),
('HD006', 16, 299000),
('HD006', 19, 99000),

-- HD007
('HD007', 22, 299000),
('HD007', 23, 399000),
('HD007', 24, 49000),

-- HD008 (cancelled)
('HD008', 17, 179000),
('HD008', 18, 299000),
('HD008', 28, 59000),
('HD008', 32, 129000),

-- HD009
('HD009', 25, 449000),
('HD009', 30, 59000),

-- HD010
('HD010', 33, 69000),
('HD010', 34, 59000),
('HD010', 35, 59000),

-- HD011
('HD011', 21, 1499000),
('HD011', 26, 690000),
('HD011', 27, 39000);


INSERT INTO PaymentMethod (method)
VALUES
('VNPay'),
('MoMo'),
('CreditCard'),
('ZaloPay');

INSERT INTO PaymentHistory (transaction_id, payment_method, payment_status, payment_date) VALUES
('HD001', 1, 'completed', '2024-01-21'),
-- HD002: cancelled
('HD004', 3, 'completed', '2024-03-15'),
-- HD005: cancelled 
('HD006', 4, 'completed', '2024-04-20'),
-- HD008: cancelled
('HD009', 2, 'completed', '2024-11-25'),
('HD010', 3, 'completed', '2025-03-15');
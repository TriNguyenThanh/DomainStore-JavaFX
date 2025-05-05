DROP DATABASE DOMAINMANAGEMENT;
CREATE DATABASE DOMAINMANAGEMENT;
USE DOMAINMANAGEMENT;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL, 
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    cccd VARCHAR(20) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('user', 'admin') DEFAULT 'user',
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_cccd ON users(cccd);

CREATE TABLE TopLevelDomain (
	id INT AUTO_INCREMENT PRIMARY KEY,
	TLD_text CHAR(10) NOT NULL,
	price INT UNSIGNED NOT NULL
);

CREATE INDEX idx_tld_text ON TopLevelDomain(TLD_text);

CREATE TABLE domains (	
    id INT AUTO_INCREMENT PRIMARY KEY,
    domain_name VARCHAR(255) NOT NULL,
    tld_id int not null,
    foreign key (tld_id) references TopLevelDomain(id),
    status ENUM('available', 'sold') DEFAULT 'available',
    active_date DATE null,
    years int null,
    price int default 0,
    owner_id INT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_domains_id ON domains(id);
CREATE INDEX idx_domains_name ON domains(domain_name);
CREATE INDEX idx_domains_status ON domains(status);
CREATE INDEX idx_domains_owner_id ON domains(owner_id);
CREATE INDEX idx_domains_active_date ON domains(active_date);
CREATE INDEX idx_domains_tld_id ON domains(tld_id);

CREATE TABLE carts (
	id int auto_increment PRIMARY KEY,
    cus_id INT NOT NULL,
    domain_id INT NOT NULL,
    years INT CHECK (years > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (cus_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (domain_id) REFERENCES domains(id) ON DELETE CASCADE
);

CREATE INDEX idx_carts_id ON carts(id);
CREATE INDEX idx_carts_cus_id ON carts(cus_id);
CREATE INDEX idx_carts_domain_id ON carts(domain_id);


CREATE TABLE Transactions (
    id CHAR(10) PRIMARY KEY NOT NULL,
    user_id INT NOT NULL,
    transaction_date DATE NOT NULL,
    transaction_status ENUM('pendingConfirm', 'pendingPayment', 'completed', 'cancelled') DEFAULT 'pendingConfirm',
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_transactions_user_id ON Transactions(user_id);
CREATE INDEX idx_transactions_status ON Transactions(transaction_status);
CREATE INDEX idx_transactions_date ON Transactions(transaction_date);

CREATE TABLE Transactions_info (
    transactions_id CHAR(10) NOT NULL,
    domain_id INT NOT NULL,
    price INT UNSIGNED NOT NULL,

    FOREIGN KEY (transactions_id) REFERENCES Transactions(id) ON DELETE CASCADE,
    FOREIGN KEY (Domain_id) REFERENCES domains(id),
    PRIMARY KEY(Domain_id, transactions_id)
);

CREATE INDEX idx_transactions_info_transaction_id ON Transactions_info(transactions_id);
CREATE INDEX idx_transactions_info_domain_id ON Transactions_info(domain_id);


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

CREATE INDEX idx_payment_history_transaction_id ON PaymentHistory(transaction_id);
CREATE INDEX idx_payment_history_status ON PaymentHistory(payment_status);
CREATE INDEX idx_payment_history_date ON PaymentHistory(payment_date);


INSERT INTO users (full_name, email, phone, cccd, password_hash, role)
VALUES
('Nguyễn Thành Trí', 'tringuyenntt1505@gmail.com', '0987654321', '027205011960', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'admin'),
-- user:0987654321 password: pass123456@
('Lê Nguyễn Anh Dự', 'dule1028a@gmail.com', '0912345678', '027205011961', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'admin'),
-- user:0912345678 password: pass123456@
('Âu Dương Tấn', 'auduongtan321@gmail.com', '0923456789', '027205011962', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'admin'),
-- user:0923456789 password: pass123456@
('Phạm Anh Dũng', 'dung.pham@gmail.com', '0934567890', '027205011963', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0934567890 password: pass123456@
('Ngô Thanh Hoa', 'hoa.ngo@gmail.com', '0945678901', '027205011964', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0945678901 password: pass123456@
('Hoàng Tuấn Khang', 'khang.hoang@gmail.com', '0956789012', '027205011965', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0956789012 password: pass123456@
('Bùi Diễm Lan', 'lan.bui@gmail.com', '0967890123', '027205011966', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0967890123 password: pass123456@
('Đặng Thành Nam', 'nam.dang@gmail.com', '0978901234', '027205011967', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0978901234 password: pass123456@
('Võ Minh Nhật', 'nhat.vo@gmail.com', '0989012345', '027205011968', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0989012345 password: pass123456@
('Nguyễn Thị Oanh', 'oanh.nguyen@gmail.com', '0990123456', '027205011969', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user'),
-- user:0990123456 password: pass123456@
('Đỗ Quang Phúc', 'phuc.do@gmail.com', '0901234567', '027205011970', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'user');
-- user:0901234567 password: pass123456@

INSERT INTO TopLevelDomain (TLD_text, price) VALUES
('.com', 299000),
('.net', 390000),
('.org', 299000),
('.info', 179000),
('.biz', 99000),
('.co', 499000),
('.io', 890000),
('.ai', 1499000),
('.me', 299000),
('.tv', 399000),
('.xyz', 199000),
('.love', 259000),
('.vn', 449000),
('.dev', 690000),
('.site', 239000),
('.online', 339000),
('.cloud', 159000),
('.click', 159000),
('.group', 299000),
('.mom', 129000),
('.asia', 99000),
('.icu', 99000),
('.art', 99000),
('.blog', 599000),
('.com.vn', 550000),
('.net.vn', 450000),
('.org.vn', 350000),
('.edu.vn', 100000);

INSERT INTO domains (domain_name, tld_id, status, active_date, years, owner_id)
VALUES
('diamonielts', 1, 'available', null, 1 ,null),
('example', 3, 'sold', '2024-05-07 17:00:00', 1, 3),
('testdomain', 6, 'sold', '2024-05-07 17:00:00', 1, 3),
('mywebsite', 1, 'sold', '2024-05-07 17:00:00', 1, 3),
('yourdomain123', 2, 'available', null , 1, null),
('newproject3213', 3, 'available', null , 1, null),
('spicydonut1', 4, 'available', null, 1 ,null),
('globalban3', 5, 'available', null, 1 ,null),
('vietnamexpert4', 5, 'available', null, 1 ,null),
('startupvn5', 5, 'available', null, 1 ,null),
('hotbrandno2', 1, 'available', null, 1 ,null),
('bestservicesno12', 2, 'available',null, 1, null),
('surprisedtech1', 3, 'available', null, 1 ,null),
('supercool', 1, 'sold', '2025-04-20 10:00:00', 1, 4),
('fastservice', 2, 'sold', '2025-01-21 19:30:00', 2, 1),
('amazingproject', 3, 'sold', '2025-04-20 10:00:00', 1, 4),
('yourbrandnah5', 4, 'available', null, 1 ,null),
('nextbigthing2', 5, 'available', null, 1 ,null),
('enterprisehub', 6, 'sold', '2025-04-20 10:00:00', 1, 4),
('futurenow52', 24, 'available', null, 1 ,null),
('techstartupbo', 8, 'available', null, 1, null),
('eradigitalworld', 9, 'available', null, 1, null),
('smartcityhcm', 10, 'available', null , 1, null),
('globalsupermarket23', 11, 'available', null , 1, null),
('nextlevel', 12, 'sold', '2024-11-25 9:30:00', 1, 11),
('connectasia321', 13, 'available', null, 1, null),
('cloudhub', 14, 'available', null, 1, null),
('universeclick1', 15, 'available', null, 1 ,null),
('amazingblog5', 16, 'available', null, 1 ,null),
('futurecloud', 17, 'sold', '2024-11-25 9:30:00', 1, 11),
('bestclickno1', 18, 'available', null, 1 ,null),
('stronggroup15', 19, 'available', null, 1 ,null),
('supermom', 20, 'sold', '2025-03-15 11:20:00', 1, 9),
('techasia', 21, 'sold', '2025-03-15 11:20:00', 1, 9),
('globalbusiness', 22, 'sold', '2025-03-15 11:20:00', 1, 9),
('creativeart12', 23, 'available', null, 1 ,null),
('topblog21', 24, 'available', null, 1 ,null),
('topblog21', 1, 'available', null, 1 ,null);

UPDATE domains
JOIN TopLevelDomain tld ON domains.tld_id = tld.id
SET domains.price = tld.price;

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
('HD011', 5, '2025-03-25', 'pendingConfirm');


INSERT INTO Transactions_info (transactions_id, domain_id, price) VALUES
-- HD001
('HD001', 15, 780000),

-- HD002 (cancelled)
('HD002', 11, 299000),
('HD002', 13, 299000),

-- HD003
('HD003', 5, 390000),
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

INSERT INTO PaymentHistory (transaction_id, payment_id, payment_method, payment_status, payment_date) VALUES
('HD001', '14931583', 1, 'completed', '2025-01-21'),
-- HD002: cancelled
('HD004', '14936383', 1, 'completed', '2025-05-07'),
-- HD005: cancelled
('HD006', '14938357', 1, 'completed', '2025-04-20'),
-- HD008: cancelled
('HD009', '14933277', 1, 'completed', '2024-11-25'),
('HD010', '14939457', 1, 'completed', '2025-03-15');

-- tạo sự kiện tự động cập nhật lại domain khi hết hạn kích hoạt
SET GLOBAL event_scheduler = ON;
DELIMITER //

CREATE EVENT reset_expired_domains
ON SCHEDULE EVERY 1 DAY
DO
BEGIN
    UPDATE domains
    SET
        status = 'available',
        owner_id = NULL,
        active_date = NULL,
        years = NULL
    WHERE
        status = 'sold'
        AND active_date IS NOT NULL
        AND DATE_ADD(active_date, INTERVAL years YEAR) < CURDATE();
END;
//

DELIMITER ;
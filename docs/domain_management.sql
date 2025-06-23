DROP DATABASE DOMAINMANAGEMENT;
CREATE DATABASE DOMAINMANAGEMENT; USE DOMAINMANAGEMENT;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL, 
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL, ROLE ENUM('USER', 'ADMIN') DEFAULT 'USER',
    is_deleted BOOLEAN DEFAULT FALSE,
    otp VARCHAR(10),
    otp_created_at DATETIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_phone ON users(phone);

CREATE TABLE TopLevelDomain (
	id INT AUTO_INCREMENT PRIMARY KEY,
	TLD_text CHAR(10) NOT NULL,
	price BIGINT UNSIGNED NOT NULL
);

CREATE INDEX idx_tld_text ON TopLevelDomain(TLD_text);

CREATE TABLE domains (
    id INT AUTO_INCREMENT PRIMARY KEY,
    domain_name VARCHAR(255) NOT NULL,
    tld_id INT NOT NULL, FOREIGN KEY (tld_id) REFERENCES TopLevelDomain(id), STATUS ENUM('AVAILABLE', 'SOLD') DEFAULT 'AVAILABLE',
    active_date DATETIME NULL,
    years INT NULL,
    price BIGINT UNSIGNED DEFAULT 0,
    owner_id INT NULL, FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_domains_id ON domains(id);
CREATE INDEX idx_domains_name ON domains(domain_name);
CREATE INDEX idx_domains_status ON domains(STATUS);
CREATE INDEX idx_domains_owner_id ON domains(owner_id);
CREATE INDEX idx_domains_active_date ON domains(active_date);
CREATE INDEX idx_domains_tld_id ON domains(tld_id);

CREATE TABLE carts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cus_id INT NOT NULL,
    domain_id INT NOT NULL,
    years INT CHECK (years > 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (cus_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (domain_id) REFERENCES domains(id) ON DELETE CASCADE
);

CREATE INDEX idx_carts_id ON carts(id);
CREATE INDEX idx_carts_cus_id ON carts(cus_id);
CREATE INDEX idx_carts_domain_id ON carts(domain_id);

CREATE TABLE PaymentMethod (
    id INT PRIMARY KEY,
    method ENUM('N/A', 'VNPay', 'MoMo', 'CreditCard', 'ZaloPay')
);

CREATE TABLE Transactions (
    id VARCHAR(10) PRIMARY KEY NOT NULL,
    user_id INT NOT NULL,
    transaction_date TIMESTAMP NOT NULL,
    is_renewal BOOLEAN DEFAULT 0,
    method INT DEFAULT 0,
    transaction_status ENUM('CONFIRM', 'PAYMENT', 'COMPLETED', 'CANCELLED') DEFAULT 'CONFIRM',
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (method) REFERENCES PaymentMethod(id)
);

CREATE INDEX idx_transactions_user_id ON Transactions(user_id);
CREATE INDEX idx_transactions_status ON Transactions(transaction_status);
CREATE INDEX idx_transactions_date ON Transactions(transaction_date);

CREATE TABLE transactions_info (
    transactions_id VARCHAR(10) NOT NULL,
    domain_id INT NOT NULL,
    price BIGINT UNSIGNED DEFAULT 0,
    years INT DEFAULT 0,
    FOREIGN KEY (transactions_id) REFERENCES Transactions(id) ON DELETE CASCADE,
    FOREIGN KEY (Domain_id) REFERENCES domains(id),
    PRIMARY KEY(Domain_id, transactions_id)
);

CREATE INDEX idx_transactions_info_transaction_id ON Transactions_info(transactions_id);
CREATE INDEX idx_transactions_info_domain_id ON Transactions_info(domain_id);

CREATE TABLE PaymentHistory (
    id INT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(10) NOT NULL,
    payment_id VARCHAR(15) NOT NULL DEFAULT '',
    payment_method INT,
    payment_status ENUM('COMPLETED', 'FAILED'),
    payment_date TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES Transactions(id),
    FOREIGN KEY (payment_method) REFERENCES PaymentMethod(id)
);

CREATE INDEX idx_payment_history_transaction_id ON PaymentHistory(transaction_id);
CREATE INDEX idx_payment_history_status ON PaymentHistory(payment_status);
CREATE INDEX idx_payment_history_date ON PaymentHistory(payment_date);

INSERT INTO users (full_name, email, phone, password_hash, ROLE) VALUES
('Nguyễn Thành Trí', 'tringuyenntt1505@gmail.com', '0987654321','$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'ADMIN'),
-- user:0987654321 password: pass123456@
('Lê Nguyễn Anh Dự', 'dule1028a@gmail.com', '0912345678','$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'ADMIN'),
-- user:0912345678 password: pass123456@
('Âu Dương Tấn', 'auduongtan321@gmail.com', '0923456789','$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'ADMIN'),
-- user:0923456789 password: pass123456@
('Phạm Anh Dũng', 'dung.pham@gmail.com', '0934567890','$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'USER'),
-- user:0934567890 password: pass123456@
('Ngô Thanh Hoa', 'hoa.ngo@gmail.com', '0945678901','$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'USER'),
-- user:0945678901 password: pass123456@
('Hoàng Tuấn Khang', 'khang.hoang@gmail.com', '0956789012','$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'USER'),
-- user:0956789012 password: pass123456@
('Bùi Diễm Lan', 'lan.bui@gmail.com', '0967890123','$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'USER'),
-- user:0967890123 password: pass123456@
('Đặng Thành Nam', 'nam.dang@gmail.com', '0978901234','$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'USER'),
-- user:0978901234 password: pass123456@
('Võ Minh Nhật', 'nhat.vo@gmail.com', '0989012345','$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'USER'),
-- user:0989012345 password: pass123456@
('Nguyễn Thị Oanh', 'oanh.nguyen@gmail.com', '0990123456','$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'USER'),
-- user:0990123456 password: pass123456@
('Đỗ Quang Phúc', 'phuc.do@gmail.com', '0901234567', '$argon2i$v=19$m=65536,t=3,p=1$IpdMbu22itJFyvwg1Q5hww$Rf7j4imDyXFIMpHjtAuEV9jeFBs90wwR4Oi+OTBSYEU', 'USER');
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
INSERT INTO domains (domain_name, tld_id, STATUS, active_date, years, owner_id) VALUES
('diamonielts', 1, 'AVAILABLE', NULL, 1, NULL),
('example', 3, 'SOLD', '2024-05-07 17:00:00', 1, 3),
('testdomain', 6, 'SOLD', '2024-05-07 17:00:00', 1, 3),
('mywebsite', 1, 'SOLD', '2024-05-07 17:00:00', 1, 3),
('yourdomain123', 2, 'SOLD', NULL, 1, NULL),
('newproject3213', 3, 'SOLD', NULL, 1, NULL),
('spicydonut1', 4, 'AVAILABLE', NULL, 1, NULL),
('globalban3', 5, 'AVAILABLE', NULL, 1, NULL),
('vietnamexpert4', 5, 'AVAILABLE', NULL, 1, NULL),
('startupvn5', 5, 'AVAILABLE', NULL, 1, NULL),
('hotbrandno2', 1, 'AVAILABLE', NULL, 1, NULL),
('bestservicesno12', 2, 'SOLD', NULL, 1, NULL),
('surprisedtech1', 3, 'AVAILABLE', NULL, 1, NULL),
('supercool', 1, 'SOLD', '2025-04-20 10:00:00', 1, 4),
('fastservice', 2, 'SOLD', '2025-01-21 19:30:00', 2, 1),
('amazingproject', 3, 'SOLD', '2025-04-20 10:00:00', 1, 4),
('yourbrandnah5', 4, 'AVAILABLE', NULL, 1, NULL),
('nextbigthing2', 5, 'AVAILABLE', NULL, 1, NULL),
('enterprisehub', 6, 'SOLD', '2025-04-20 10:00:00', 1, 4),
('futurenow52', 24, 'AVAILABLE', NULL, 1, NULL),
('techstartupbo', 8, 'SOLD', NULL, 1, NULL),
('eradigitalworld', 9, 'SOLD', NULL, 1, NULL),
('smartcityhcm', 10, 'SOLD', NULL, 1, NULL),
('globalsupermarket23', 11, 'SOLD', NULL, 1, NULL),
('nextlevel', 12, 'SOLD', '2024-11-25 9:30:00', 1, 11),
('connectasia321', 13, 'SOLD', NULL, 1, NULL),
('cloudhub', 14, 'SOLD', NULL, 1, NULL),
('universeclick1', 15, 'AVAILABLE', NULL, 1, NULL),
('amazingblog5', 16, 'AVAILABLE', NULL, 1, NULL),
('futurecloud', 17, 'SOLD', '2024-11-25 9:30:00', 1, 11),
('bestclickno1', 18, 'AVAILABLE', NULL, 1, NULL),
('stronggroup15', 19, 'AVAILABLE', NULL, 1, NULL),
('supermom', 20, 'SOLD', '2025-03-15 11:20:00', 1, 9),
('techasia', 21, 'SOLD', '2025-03-15 11:20:00', 1, 9),
('globalbusiness', 22, 'SOLD', '2025-03-15 11:20:00', 1, 9),
('creativeart12', 23, 'AVAILABLE', NULL, 1, NULL),
('topblog21', 24, 'AVAILABLE', NULL, 1, NULL),
('topblog21', 1, 'AVAILABLE', NULL, 1, NULL);
UPDATE domains
JOIN TopLevelDomain tld ON domains.tld_id = tld.id SET domains.price = tld.price;
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
INSERT INTO PaymentMethod (id, method) VALUES
(0, 'N/A'),
(1, 'VNPay'),
(2, 'MoMo'),
(3, 'CreditCard'),
(4, 'ZaloPay');
INSERT INTO Transactions (id, user_id, transaction_date, method, transaction_status) VALUES
('HD001', 1, '2024-01-21 07:03:00', 1, 'COMPLETED'),
('HD002', 2, '2024-01-22 10:15:50', 0, 'CANCELLED'),
('HD003', 2, '2024-02-10 08:05:00', 0, 'CONFIRM'),
('HD004', 3, '2024-03-15 02:17:43', 1, 'COMPLETED'),
('HD005', 7, '2024-03-20 13:55:12', 0, 'CANCELLED'),
('HD006', 4, '2024-04-20 09:23:38', 1, 'COMPLETED'),
('HD007', 8, '2024-08-10 18:41:07', 0, 'CONFIRM'),
('HD008', 10, '2024-08-20 00:59:26', 0, 'CANCELLED'),
('HD009', 11, '2024-11-25 21:34:05', 1, 'COMPLETED'),
('HD010', 9, '2025-03-15 06:02:50', 1, 'COMPLETED'),
('HD011', 5, '2025-03-25 14:28:19', 0, 'CONFIRM');

INSERT INTO Transactions_info (transactions_id, domain_id, years) VALUES
-- HD001
('HD001', 15, 2),

-- HD002 (cancelled)
('HD002', 11, 1),
('HD002', 13, 1),

-- HD003
('HD003', 5, 1),
('HD003', 12, 1),
('HD003', 6, 1),

-- HD004
('HD004', 2, 1),
('HD004', 3, 1),
('HD004', 4, 1),

-- HD005 (cancelled)
('HD005', 7, 1),
('HD005', 8, 1),
('HD005', 9, 1),
('HD005', 10, 1),

-- HD006
('HD006', 14, 1),
('HD006', 16, 1),
('HD006', 19, 1),

-- HD007
('HD007', 22, 1),
('HD007', 23, 1),
('HD007', 24, 1),

-- HD008 (cancelled)
('HD008', 17, 1),
('HD008', 18, 1),
('HD008', 28, 1),
('HD008', 32, 1),

-- HD009
('HD009', 25, 1),
('HD009', 30, 1),

-- HD010
('HD010', 33, 1),
('HD010', 34, 1),
('HD010', 35, 1),

-- HD011
('HD011', 21, 1),
('HD011', 26, 1),
('HD011', 27, 1);

UPDATE Transactions_info tsi
JOIN Domains d ON d.id = tsi.domain_id
JOIN TopLevelDomain tld ON d.tld_id = tld.id SET tsi.price = d.years * tld.price;

INSERT INTO PaymentHistory (transaction_id, payment_id, payment_method, payment_status, payment_date) VALUES
('HD001', '14931583', 1, 'COMPLETED', '2025-01-21 08:15:32'),
-- HD002: cancelled
('HD004', '14936383', 1, 'COMPLETED', '2025-05-07 11:42:07'),
-- HD005: cancelled
('HD006', '14938357', 1, 'COMPLETED', '2025-04-20 14:29:55'),
-- HD008: cancelled
('HD009', '14933277', 1, 'COMPLETED', '2024-11-25 19:03:46'),
('HD010', '14939457', 1, 'COMPLETED', '2025-03-15 07:58:20');


SET GLOBAL event_scheduler = ON;
DELIMITER //

-- tạo sự kiện tự động cập nhật lại domain khi hết hạn kích hoạt (cập nhật theo ngày)
CREATE EVENT reset_expired_domains ON SCHEDULE EVERY 1 DAY
DO
BEGIN
	UPDATE domains SET STATUS = 'AVAILABLE',
		owner_id = NULL,
		active_date = NULL,
		years = NULL
	WHERE STATUS = 'SOLD' AND active_date IS NOT NULL AND DATE_ADD(active_date, INTERVAL years YEAR) < CURDATE();
END;

-- tạo sự kiện tự động hủy hóa đơn nếu quá thời hạn (5 phút cập nhật 1 lần)
CREATE EVENT IF NOT EXISTS delete_expired_pending_transactions
ON SCHEDULE EVERY 5 MINUTE
DO
BEGIN
	-- Cập nhật lại trạng thái tên miền trước khi xoá
    UPDATE domains d
    JOIN transactions_info tsi ON d.id = tsi.domain_id
    JOIN transactions t ON tsi.transactions_id = t.id
    SET
        d.status = 'AVAILABLE',
        d.years = 0
    WHERE
		t.is_renewal = 0 AND
        (
            (t.transaction_status = 'CONFIRM' AND TIMESTAMPDIFF(MINUTE, t.transaction_date, NOW()) >= 15)
            OR
            (t.transaction_status = 'PAYMENT' AND TIMESTAMPDIFF(HOUR, t.transaction_date, NOW()) >= 24)
        );

    -- Xóa hóa đơn 'CONFIRM' quá 10 phút
    DELETE FROM transactions
    WHERE transaction_status = 'CONFIRM'
	AND TIMESTAMPDIFF(MINUTE, transaction_date, NOW()) >= 15;

    -- Xóa hóa đơn 'PAYMENT' quá 12 giờ
    DELETE FROM transactions
    WHERE transaction_status = 'PAYMENT'
      AND TIMESTAMPDIFF(HOUR, transaction_date, NOW()) >= 12;
END;

-- Tạo sự kiện xóa OTP sau 5 phút
CREATE EVENT IF NOT EXISTS clear_expired_otp
ON SCHEDULE EVERY 5 MINUTE
DO
BEGIN
    UPDATE users
    SET otp = NULL,
        otp_created_at = NULL
    WHERE otp IS NOT NULL
      AND TIMESTAMPDIFF(MINUTE, otp_created_at, NOW()) >= 5;
END;
//
DELIMITER ;
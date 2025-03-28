
package com.utc2.domainstore.dao;


import com.utc2.domainstore.entity.database.PaymentHistoryModel;
import com.utc2.domainstore.entity.database.PaymentStatusEnum;
import com.utc2.domainstore.utils.JDBC;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class PaymentHistoryDAO implements DAOInterface<PaymentHistoryModel>{
    
    public static PaymentHistoryDAO getInstance(){
        return new PaymentHistoryDAO();
    }
    
    @Override
    public int insert(PaymentHistoryModel paymentHistory) {
        int rowsAffected = 0;
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection();
            
            // Bước 2: Chuẩn bị câu lệnh để chèn dữ liệu
            String sql = "INSERT INTO paymenthistory(transaction_id, payment_id, payment_method, payment_status, payment_date) "
                    + "VALUES(?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            
            // Bước 3: Gán giá trị cho các tham số 
            pst.setString(1, paymentHistory.getTransactionId());
            pst.setString(2, paymentHistory.getPaymentCode());
            pst.setInt(3, paymentHistory.getPaymentMethodId());
            pst.setString(4, paymentHistory.getPaymentStatus().name());
            pst.setDate(5, Date.valueOf(paymentHistory.getPaymentDate()));
            
            // Bước 4: Thực thi câu lệnh INSERT và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            
            // Bước 5: Đóng kết nối
            System.out.println("Thêm dữ liệu thành công !! Có " + rowsAffected + " thay đổi");
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return rowsAffected;
    }

    @Override
    public int update(PaymentHistoryModel paymentHistory) {
        int rowsAffected = 0;
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection();
            
            // Bước 2: Chuẩn bị câu lệnh để cập nhật dữ liệu
            String sql = "UPDATE paymenthistory"
                    + " SET transaction_id = ?, payment_id = ?, payment_method = ?, "
                    + "payment_status = ?, payment_date = ?"
                    + " WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
            // Bước 3: Gán giá trị cho các tham số 
            pst.setString(1, paymentHistory.getTransactionId());
            pst.setString(2, paymentHistory.getPaymentCode());
            pst.setInt(3, paymentHistory.getPaymentMethodId());
            pst.setString(4, paymentHistory.getPaymentStatus().name());
            pst.setDate(5, Date.valueOf(paymentHistory.getPaymentDate()));
            pst.setInt(6, paymentHistory.getPaymentId());
            
            // Bước 4: Thực thi câu lệnh UPDATE và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            System.out.println("Cập nhật dữ liệu thành công !! Có " + rowsAffected + " thay đổi");
            // Bước 5: Đóng kết nối 
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return rowsAffected;
    }

    @Override
    public int delete(PaymentHistoryModel paymentHistory) {
        int rowsAffected = 0;
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection(); 
            
            // Bước 2: Chuẩn bị câu lệnh để xoá dữ liệu
            String sql = "DELETE FROM paymenthistory"
                    + " WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
             // Bước 3: Gán giá trị id
            pst.setInt(1, paymentHistory.getPaymentId());
            
            // Bước 4: Thực thi câu lệnh UPDATE và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            System.out.println("Xoá dữ liệu thành công !! Có " + rowsAffected + " thay đổi");
            // Bước 5: Đóng kết nối 
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rowsAffected;
    }

    @Override
    public PaymentHistoryModel selectById(PaymentHistoryModel paymentHistory) {
        PaymentHistoryModel p = null;
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection(); 
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT * FROM paymenthistory WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, paymentHistory.getPaymentId());
            
            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();
            
            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            while(rs.next()){
                // Lấy dữ liệu từ ResultSet
                String transactionId = rs.getString("transaction_id");
                String paymentCode = rs.getString("payment_id");
                int paymentMethodId = rs.getInt("payment_method");
                String status = rs.getString("payment_status");
                PaymentStatusEnum paymentStatus = PaymentStatusEnum.valueOf(status.toUpperCase());
                LocalDate paymentDate = rs.getDate("payment_date").toLocalDate();
                
                p = new PaymentHistoryModel(paymentHistory.getPaymentId(), transactionId, paymentCode, 
                        paymentMethodId, paymentStatus, paymentDate);
            }
            // Bước 5: Đóng kết nối 
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return p;
    }

    @Override
    public ArrayList<PaymentHistoryModel> selectAll() {
        ArrayList<PaymentHistoryModel> listPaymentHistory = new ArrayList<>();
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection(); 
            
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT * FROM domainmanagement.paymenthistory";
            PreparedStatement pst = con.prepareStatement(sql);
            
            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();
            
            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            while(rs.next()){
                // Lấy dữ liệu từ ResultSet
                int paymentId = rs.getInt("id");
                String transactionId = rs.getString("transaction_id");
                String paymentCode = rs.getString("payment_id");
                int paymentMethodId = rs.getInt("payment_method");
                String status = rs.getString("payment_status");
                PaymentStatusEnum paymentStatus = PaymentStatusEnum.valueOf(status.toUpperCase());
                LocalDate paymentDate = rs.getDate("payment_date").toLocalDate();
                
                PaymentHistoryModel p = new PaymentHistoryModel(paymentId,transactionId, paymentCode, paymentMethodId, 
                        paymentStatus, paymentDate);
                listPaymentHistory.add(p);
            }
            // Bước 5: Đóng kết nối 
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return listPaymentHistory;
    }

    @Override
    public ArrayList<PaymentHistoryModel> selectByCondition(String condition) {
        ArrayList<PaymentHistoryModel> listPaymentHistory = new ArrayList<>();
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection(); 
            
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT p.id, p.transaction_id, ts.user_id, c.full_name, p.payment_id, " +
                    "p.payment_method, p.payment_status, p.payment_date, ts.transaction_date " +
                    "FROM paymenthistory p " +
                    "join transactions ts on p.transaction_id = ts.id " +
                    "join users c on ts.user_id = c.id " +
                    "WHERE " + condition + ";";
            PreparedStatement pst = con.prepareStatement(sql);
            
            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();
            
            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            while(rs.next()){
                // Lấy dữ liệu từ ResultSet
                int paymentId = rs.getInt("id");
                String transactionId = rs.getString("transaction_id");
                String paymentCode = rs.getString("payment_id");
                int paymentMethodId = rs.getInt("payment_method");
                String status = rs.getString("payment_status").toUpperCase();
                PaymentStatusEnum paymentStatus = PaymentStatusEnum.valueOf(status);
                LocalDate paymentDate = rs.getDate("payment_date").toLocalDate();
                
                PaymentHistoryModel p = new PaymentHistoryModel(paymentId, transactionId, paymentCode, paymentMethodId, 
                        paymentStatus, paymentDate);
                listPaymentHistory.add(p);
            }
            // Bước 5: Đóng kết nối 
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return listPaymentHistory;
    }
}
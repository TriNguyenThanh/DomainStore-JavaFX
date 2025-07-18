
package com.utc2.domainstore.repository;


import com.utc2.domainstore.entity.database.PaymentHistoryModel;
import com.utc2.domainstore.entity.database.PaymentStatusEnum;
import com.utc2.domainstore.config.JDBC;
import com.utc2.domainstore.entity.database.TransactionModel;

import java.sql.*;
import java.util.ArrayList;

public class PaymentHistoryRepository implements IRepository<PaymentHistoryModel>{
    
    public static PaymentHistoryRepository getInstance(){
        return new PaymentHistoryRepository();
    }
    
    @Override
    public int insert(PaymentHistoryModel paymentHistory) {
        int rowsAffected = 0;
        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();

        try {
            // Bước 2: Chuẩn bị câu lệnh để chèn dữ liệu
            String sql = "INSERT INTO paymenthistory(transaction_id, payment_id, payment_method, payment_status, payment_date) "
                    + "VALUES(?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            
            // Bước 3: Gán giá trị cho các tham số 
            pst.setString(1, paymentHistory.getTransactionId());
            pst.setString(2, paymentHistory.getPaymentCode());
            pst.setInt(3, paymentHistory.getPaymentMethodId());
            pst.setString(4, paymentHistory.getPaymentStatus().name());
            pst.setTimestamp(5, paymentHistory.getPaymentDate());
            
            // Bước 4: Thực thi câu lệnh INSERT và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            
            // Bước 5: Đóng kết nối
            System.out.println("Thêm dữ liệu thành công !! Có " + rowsAffected + " thay đổi");
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            JDBC.closeConnection(con);
//            System.out.println("Payment - Insert: Đã đóng kết nối cơ sở dữ liệu");
        }
        return rowsAffected;
    }

    @Override
    public int update(PaymentHistoryModel paymentHistory) {
        int rowsAffected = 0;
        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();
        try {
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
            pst.setTimestamp(5, paymentHistory.getPaymentDate());
            pst.setInt(6, paymentHistory.getPaymentId());
            
            // Bước 4: Thực thi câu lệnh UPDATE và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            System.out.println("Cập nhật dữ liệu thành công !! Có " + rowsAffected + " thay đổi");
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Payment - Update: Đã đóng kết nối cơ sở dữ liệu");
        }
        return rowsAffected;
    }

    @Override
    public int delete(PaymentHistoryModel paymentHistory) {
        int rowsAffected = 0;
        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();
        try {
            // Bước 2: Chuẩn bị câu lệnh để xoá dữ liệu
            String sql = "DELETE FROM paymenthistory"
                    + " WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
             // Bước 3: Gán giá trị id
            pst.setInt(1, paymentHistory.getPaymentId());
            
            // Bước 4: Thực thi câu lệnh UPDATE và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            System.out.println("Xoá dữ liệu thành công !! Có " + rowsAffected + " thay đổi");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Payment - Delete: Đã đóng kết nối cơ sở dữ liệu");
        }
        return rowsAffected;
    }

    @Override
    public PaymentHistoryModel selectById(PaymentHistoryModel paymentHistory) {
        PaymentHistoryModel p = new PaymentHistoryModel();
        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();
        try {
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT * FROM paymenthistory WHERE transaction_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, paymentHistory.getTransactionId());
            
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
                Timestamp paymentDate = rs.getTimestamp("payment_date");
                
                p = new PaymentHistoryModel(paymentId, transactionId, paymentCode,
                        paymentMethodId, paymentStatus, paymentDate);
            }
            pst.close();
            if(p.getPaymentCode() != null) return p;
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Payment - SelectById: Đã đóng kết nối cơ sở dữ liệu");
        }
        return null;
    }

    @Override
    public ArrayList<PaymentHistoryModel> selectAll() {
        ArrayList<PaymentHistoryModel> listPaymentHistory = new ArrayList<>();

        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();
        try {
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
                Timestamp paymentDate = rs.getTimestamp("payment_date");
                
                PaymentHistoryModel p = new PaymentHistoryModel(paymentId,transactionId, paymentCode, paymentMethodId, 
                        paymentStatus, paymentDate);
                listPaymentHistory.add(p);
            }
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Payment - SelectAll: Đã đóng kết nối cơ sở dữ liệu");
        }
        return listPaymentHistory;
    }

    @Override
    public ArrayList<PaymentHistoryModel> selectByCondition(String condition) {
        ArrayList<PaymentHistoryModel> listPaymentHistory = new ArrayList<>();

        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();

        try {
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
                Timestamp paymentDate = rs.getTimestamp("payment_date");
                
                PaymentHistoryModel p = new PaymentHistoryModel(paymentId, transactionId, paymentCode, paymentMethodId, 
                        paymentStatus, paymentDate);
                listPaymentHistory.add(p);
            }
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Payment - SelectByCondition: Đã đóng kết nối cơ sở dữ liệu");
        }
        return listPaymentHistory;
    }
}
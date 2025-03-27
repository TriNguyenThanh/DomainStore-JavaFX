
package com.utc2.domainstore.dao;

import com.utc2.domainstore.entity.database.TransactionInfoModel;
import com.utc2.domainstore.utils.JDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TransactionInfoDAO implements DAOInterface<TransactionInfoModel>{

    public static TransactionInfoDAO getInstance(){
        return new TransactionInfoDAO();
    }
    
    @Override
    public int insert(TransactionInfoModel transactionInfo) {
        int rowsAffected = 0;
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection();
            
            // Bước 2: Chuẩn bị câu lệnh để chèn dữ liệu
            String sql = "INSERT INTO transactions_info(transactions_id, domain_id, price)"
                    + " VALUES(?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);
            
            // Bước 3: Gán giá trị cho các tham số 
            pst.setString(1, transactionInfo.getTransactionId());
            pst.setInt(2, transactionInfo.getDomainId());
            pst.setInt(3, transactionInfo.getPrice());
            
            // Bước 4: Thực thi câu lệnh INSERT và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            System.out.println("Thêm dữ liệu thành công !! Có " + rowsAffected + " thay đổi");
            // Bước 5: Đóng kết nối
//            System.out.println("Thêm dữ liệu thành công !! Có " + rowsAffected + " thay đổi");
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return rowsAffected;
    }
    
    @Override
    public int update(TransactionInfoModel transactionInfo) {
        int rowsAffected = 0;
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection(); 
            
            // Bước 2: Chuẩn bị câu lệnh để xoá dữ liệu
            String sql = "UPDATE transactions_info "
                    + "SET price = ?"
                    + " WHERE transactions_id = ? and domain_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
             // Bước 3: Gán giá trị id
            pst.setInt(1, transactionInfo.getPrice()); 
            pst.setString(2, transactionInfo.getTransactionId());
            pst.setInt(3, transactionInfo.getDomainId());
            // Bước 4: Thực thi câu lệnh UPDATE và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            System.out.println("Cập nhật dữ liệu thành công !! Có " + rowsAffected + " thay đổi");
            // Bước 5: Đóng kết nối 
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return rowsAffected;
    }
    
    @Override
    public int delete(TransactionInfoModel transactionInfo) {
        int rowsAffected = 0;
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection(); 
            
            // Bước 2: Chuẩn bị câu lệnh để xoá dữ liệu
            String sql = "DELETE FROM transactions_info"
                    + " WHERE transactions_id = ? and domain_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            
             // Bước 3: Gán giá trị id
            pst.setString(1, transactionInfo.getTransactionId());
            pst.setInt(2, transactionInfo.getDomainId());
            // Bước 4: Thực thi câu lệnh UPDATE và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            System.out.println("Xoá dữ liệu thành công !! Có " + rowsAffected + " thay đổi");
            // Bước 5: Đóng kết nối 
            JDBC.closeConnection(con);
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return rowsAffected;
    }

    @Override
    public TransactionInfoModel selectById(TransactionInfoModel transactionInfo) {
        TransactionInfoModel t = new TransactionInfoModel();
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection(); 
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT * FROM transactions_info WHERE transactions_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, transactionInfo.getTransactionId());
            
            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();
            
            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            while(rs.next()){
                // Lấy dữ liệu từ ResultSet
                t.setTransactionId(rs.getString("transactions_id"));
                t.setDomainId(rs.getInt("domain_id"));
                t.setPrice(rs.getInt("price"));
            }
            // Bước 5: Đóng kết nối 
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return t;
    }

    @Override
    public ArrayList<TransactionInfoModel> selectAll() {
        ArrayList<TransactionInfoModel> listTransactionInfo = new ArrayList<>();
        TransactionInfoModel t = new TransactionInfoModel();
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection(); 
            
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT * FROM transactions_info";
            PreparedStatement pst = con.prepareStatement(sql);
            
            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();
            
            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            while(rs.next()){
                // Lấy dữ liệu từ ResultSet
                t.setTransactionId(rs.getString("transactions_id"));
                t.setDomainId(rs.getInt("domain_id"));
                t.setPrice(rs.getInt("price"));
                
                listTransactionInfo.add(t);
            }
            // Bước 5: Đóng kết nối 
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return listTransactionInfo;
    }

    @Override
    public ArrayList<TransactionInfoModel> selectByCondition(String condition) {
        ArrayList<TransactionInfoModel> listTransactionInfo = new ArrayList<>();
        TransactionInfoModel t = new TransactionInfoModel();
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection(); 
            
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT * FROM transactions_info where " + condition + ";";
            PreparedStatement pst = con.prepareStatement(sql);
            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();
            
            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            while(rs.next()){
                // Lấy dữ liệu từ ResultSet
                t.setTransactionId(rs.getString("transactions_id"));
                t.setDomainId(rs.getInt("domain_id"));
                t.setPrice(rs.getInt("price"));
                
                listTransactionInfo.add(t);
            }
            // Bước 5: Đóng kết nối 
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return listTransactionInfo;
    }

}


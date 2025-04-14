package com.utc2.domainstore.repository;


import com.utc2.domainstore.entity.database.PaymentStatusEnum;
import com.utc2.domainstore.entity.database.TransactionInfoModel;
import com.utc2.domainstore.entity.database.TransactionModel;
import com.utc2.domainstore.entity.database.TransactionStatusEnum;
import com.utc2.domainstore.config.JDBC;

import java.sql.*;
import java.util.ArrayList;

public class TransactionRepository implements IRepository<TransactionModel> {

    public static TransactionRepository getInstance() {
        return new TransactionRepository();
    }

    @Override
    public int insert(TransactionModel transaction) {
        int rowsAffected = 0;
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection();

            // Bước 2: Chuẩn bị câu lệnh để chèn dữ liệu
            String sql = "INSERT INTO transactions(id, user_id, transaction_date)"
                    + " VALUES(?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);

            // Bước 3: Gán giá trị cho các tham số 
            pst.setString(1, transaction.getTransactionId());
            pst.setInt(2, transaction.getUserId());
            pst.setDate(3, Date.valueOf(transaction.getTransactionDate()));

            // Bước 4: Thực thi câu lệnh INSERT và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            for(TransactionInfoModel ti : transaction.getTransactionInfos()){
                TransactionInfoRepository.getInstance().insert(ti);
                rowsAffected++;
            }
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
    public int update(TransactionModel transaction) {
        int rowsAffected = 0;
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection();

            // Bước 2: Chuẩn bị câu lệnh để cập nhật dữ liệu
            String sql = "UPDATE transactions "
                    + "SET user_id = ?,transaction_date = ? "
                    + "WHERE id = ?;";
            PreparedStatement pst = con.prepareStatement(sql);

            // Bước 3: Gán giá trị cho các tham số 
            pst.setInt(1, transaction.getUserId());
            pst.setDate(2, Date.valueOf(transaction.getTransactionDate()));
            pst.setString(3, transaction.getTransactionId());
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
    public int delete(TransactionModel transaction) {
        int rowsAffected = 0;
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection();

            // Bước 2: Chuẩn bị câu lệnh để xoá dữ liệu
            String sql = "DELETE FROM transactions"
                    + " WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);

            // Bước 3: Gán giá trị id
            pst.setString(1, transaction.getTransactionId());

            // Bước 4: Thực thi câu lệnh UPDATE và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            System.out.println("Xoá dữ liệu thành công !! Có " + rowsAffected + " thay đổi");
            // Bước 5: Đóng kết nối 
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return rowsAffected;
    }

    @Override
    public TransactionModel selectById(TransactionModel transaction) {
        TransactionModel t = new TransactionModel();
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection();
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT ts.id transactions_id, ts.user_id, ts.transaction_date,"
                    + "tsi.domain_id, tsi.price, d.years, ts.transaction_status, p.payment_status, p.payment_date"
                    + " FROM transactions ts "
                    + "join transactions_info tsi on ts.id = tsi.transactions_id "
                    + "join domains d on tsi.domain_id = d.id "
                    + "join paymenthistory p on ts.id = p.transaction_id "
                    + "WHERE transactions_id = ?;";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, transaction.getTransactionId());

            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();

            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            while (rs.next()) {
                // Lấy dữ liệu từ ResultSet
                if (t.getTransactionInfos().isEmpty()) {
                    t.setTransactionId(rs.getString("transactions_id"));
                    t.setUserId(rs.getInt("user_id"));
                    t.setTransactionDate(rs.getDate("transaction_date").toLocalDate());
                    t.setTransactionStatus(TransactionStatusEnum.valueOf(rs.getString("transaction_status").toUpperCase()));
                    t.setTotalCost(0);
                    t.setPaymentStatus(PaymentStatusEnum.valueOf(rs.getString("payment_status").toUpperCase()));
                }
                TransactionInfoModel tsi = new TransactionInfoModel(rs.getString("transactions_id"),
                        rs.getInt("domain_id"), rs.getInt("price"));
                t.setTotalCost(t.getTotalCost() + tsi.getPrice());
                t.getTransactionInfos().add(tsi);
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
    public ArrayList<TransactionModel> selectAll() {
        ArrayList<TransactionModel> listTransaction = new ArrayList<>();
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection();

            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT ts.id transactions_id, ts.user_id, ts.transaction_date,"
                    + "tsi.domain_id, tsi.price, d.years, ts.transaction_status, p.payment_status, p.payment_date"
                    + " FROM transactions ts "
                    + "join transactions_info tsi on ts.id = tsi.transactions_id "
                    + "join domains d on tsi.domain_id = d.id "
                    + "join paymenthistory p on ts.id = p.transaction_id "
                    + "ORDER BY ts.id;";
            PreparedStatement pst = con.prepareStatement(sql);

            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();

            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            while (rs.next()) {
                String transactionId = rs.getString("transactions_id");
                int userId = rs.getInt("user_id");
                int price = rs.getInt("price");
                TransactionInfoModel tsi = new TransactionInfoModel(transactionId, userId, price);

                // Tìm xem transaction này đã có trong danh sách chưa
                TransactionModel existingTransaction = null;
                for (TransactionModel tran : listTransaction) {
                    if (tran.getTransactionId().equals(transactionId)) {
                        existingTransaction = tran;
                        break;
                    }
                }
                if (existingTransaction != null) {
                    // Nếu đã tồn tại -> cập nhật cost và thêm info
                    existingTransaction.setTotalCost(existingTransaction.getTotalCost() + price);
                    existingTransaction.getTransactionInfos().add(tsi);
                } else {
                    // Nếu chưa có -> tạo mới
                    TransactionModel t = new TransactionModel();
                    t.setTransactionId(transactionId);
                    t.setUserId(userId);
                    t.setTransactionDate(rs.getDate("transaction_date").toLocalDate());
                    t.setTransactionStatus(TransactionStatusEnum.valueOf(rs.getString("transaction_status").toUpperCase()));
                    t.setPaymentStatus(PaymentStatusEnum.valueOf(rs.getString("payment_status").toUpperCase()));
                    t.setTotalCost(price);
                    t.getTransactionInfos().add(tsi);
                    listTransaction.add(t);
                }
            }
            // Bước 5: Đóng kết nối 
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return listTransaction;
    }

    @Override
    public ArrayList<TransactionModel> selectByCondition(String condition) {
        ArrayList<TransactionModel> listTransaction = new ArrayList<>();
        try {
            // Bước 1: Mở kết nối đến database
            Connection con = JDBC.getConnection();

            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT ts.id transactions_id, ts.user_id, ts.transaction_date,"
                    + "tsi.domain_id, tsi.price, d.years, ts.transaction_status, p.payment_status, p.payment_date"
                    + " FROM transactions ts "
                    + "join transactions_info tsi on ts.id = tsi.transactions_id "
                    + "join domains d on tsi.domain_id = d.id "
                    + "join paymenthistory p on ts.id = p.transaction_id "
                    + "WHERE " + condition + ";" ;
            PreparedStatement pst = con.prepareStatement(sql);

            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();

            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            while (rs.next()) {
                String transactionId = rs.getString("transactions_id");
                int userId = rs.getInt("user_id");
                int price = rs.getInt("price");
                TransactionInfoModel tsi = new TransactionInfoModel(transactionId, userId, price);

                // Tìm xem transaction này đã có trong danh sách chưa
                TransactionModel existingTransaction = null;
                for (TransactionModel tran : listTransaction) {
                    if (tran.getTransactionId().equals(transactionId)) {
                        existingTransaction = tran;
                        break;
                    }
                }
                if (existingTransaction != null) {
                    // Nếu đã tồn tại -> cập nhật cost và thêm info
                    existingTransaction.setTotalCost(existingTransaction.getTotalCost() + price);
                    existingTransaction.getTransactionInfos().add(tsi);
                } else {
                    // Nếu chưa có -> tạo mới
                    TransactionModel t = new TransactionModel();
                    t.setTransactionId(transactionId);
                    t.setUserId(userId);
                    t.setTransactionDate(rs.getDate("transaction_date").toLocalDate());
                    t.setTransactionStatus(TransactionStatusEnum.valueOf(rs.getString("transaction_status").toUpperCase()));
                    t.setPaymentStatus(PaymentStatusEnum.valueOf(rs.getString("payment_status").toUpperCase()));
                    t.setTotalCost(price);
                    t.getTransactionInfos().add(tsi);
                    listTransaction.add(t);
                }
            }
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return listTransaction;
    }

}

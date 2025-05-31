package com.utc2.domainstore.repository;

import com.utc2.domainstore.config.JDBC;
import com.utc2.domainstore.entity.database.TransactionInfoModel;
import com.utc2.domainstore.entity.database.TransactionModel;
import com.utc2.domainstore.entity.database.TransactionStatusEnum;

import java.sql.*;
import java.util.ArrayList;

public class TransactionRepository implements IRepository<TransactionModel> {

    public static TransactionRepository getInstance() {
        return new TransactionRepository();
    }

    @Override
    public int insert(TransactionModel transaction) {
        int rowsAffected = 0;
        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();

        try {
            // Bước 2: Chuẩn bị câu lệnh để chèn dữ liệu
            String sql = "INSERT INTO transactions(id, user_id, transaction_date, is_renewal)"
                    + " VALUES(?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(sql);

            // Bước 3: Gán giá trị cho các tham số 
            pst.setString(1, transaction.getTransactionId());
            pst.setInt(2, transaction.getUserId());
            pst.setTimestamp(3, transaction.getTransactionDate());
            pst.setBoolean(4, transaction.getRenewal());

            // Bước 4: Thực thi câu lệnh INSERT và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            for (TransactionInfoModel ti : transaction.getTransactionInfos()) {
                TransactionInfoRepository.getInstance().insert(ti);
                rowsAffected++;
            }
            // Bước 5: Đóng kết nối
            System.out.println("Thêm dữ liệu thành công !! Có " + rowsAffected + " thay đổi");
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            JDBC.closeConnection(con);
//            System.out.println("Transaction - Insert: Đã đóng kết nối cơ sở dữ liệu");
        }
        return rowsAffected;
    }

    @Override
    public int update(TransactionModel transaction) {
        int rowsAffected = 0;
        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();

        try {
            // Bước 2: Chuẩn bị câu lệnh để cập nhật dữ liệu
            String sql = "UPDATE transactions "
                    + "SET user_id = ?,transaction_date = ?, method = ?, transaction_status = ? "
                    + "WHERE id = ?;";
            PreparedStatement pst = con.prepareStatement(sql);

            // Bước 3: Gán giá trị cho các tham số 
            pst.setInt(1, transaction.getUserId());
            pst.setTimestamp(2, transaction.getTransactionDate());
            pst.setInt(3, transaction.getPaymentMethod());
            pst.setString(4, String.valueOf(transaction.getTransactionStatus()));
            pst.setString(5, transaction.getTransactionId());
            // Bước 4: Thực thi câu lệnh UPDATE và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            System.out.println("Cập nhật dữ liệu thành công !! Có " + rowsAffected + " thay đổi");

            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Transaction - Update: Đã đóng kết nối cơ sở dữ liệu");
        }
        return rowsAffected;
    }

    @Override
    public int delete(TransactionModel transaction) {
        int rowsAffected = 0;
        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();

        try {
            // Bước 2: Chuẩn bị câu lệnh để xoá dữ liệu
            String sql = "DELETE FROM transactions"
                    + " WHERE id = ?";
            PreparedStatement pst = con.prepareStatement(sql);

            // Bước 3: Gán giá trị id
            pst.setString(1, transaction.getTransactionId());

            // Bước 4: Thực thi câu lệnh UPDATE và lấy số dòng bị ảnh hưởng
            rowsAffected = pst.executeUpdate();
            System.out.println("Xoá dữ liệu thành công !! Có " + rowsAffected + " thay đổi");

            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Transaction - Delete: Đã đóng kết nối cơ sở dữ liệu");
        }
        return rowsAffected;
    }

    @Override
    public TransactionModel selectById(TransactionModel transaction) {
        TransactionModel t = new TransactionModel();
        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();
        try {
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT ts.*,"
                    + "tsi.domain_id, tsi.price, d.years"
                    + " FROM transactions ts "
                    + "JOIN transactions_info tsi on ts.id = tsi.transactions_id "
                    + "JOIN domains d on tsi.domain_id = d.id "
                    + "WHERE ts.id = ?;";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, transaction.getTransactionId());

            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();

            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            while (rs.next()) {
                // Lấy dữ liệu từ ResultSet
                if (t.getTransactionInfos().isEmpty()) {
                    t.setTransactionId(rs.getString("ts.id"));
                    t.setUserId(rs.getInt("user_id"));
                    t.setTransactionDate(rs.getTimestamp("transaction_date"));
                    t.setRenewal(rs.getBoolean("is_renewal"));
                    t.setPaymentMethod(rs.getInt("method"));
                    t.setTransactionStatus(TransactionStatusEnum.valueOf(rs.getString("transaction_status")));
                    t.setTotalCost(0L);
                }
                TransactionInfoModel tsi = new TransactionInfoModel(rs.getString("ts.id"),
                        rs.getInt("domain_id"), rs.getLong("price"));
                t.setTotalCost(t.getTotalCost() + tsi.getPrice());
                t.getTransactionInfos().add(tsi);
            }
            pst.close();
            if(t.getUserId() != null) return t;
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Transaction - SelectById: Đã đóng kết nối cơ sở dữ liệu");
        }
        return null;
    }

    @Override
    public ArrayList<TransactionModel> selectAll() {
        ArrayList<TransactionModel> listTransaction = new ArrayList<>();
        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();

        try {
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT ts.*,"
                    + "tsi.domain_id, tsi.price, d.years"
                    + " FROM transactions ts "
                    + "JOIN transactions_info tsi on ts.id = tsi.transactions_id "
                    + "JOIN domains d on tsi.domain_id = d.id "
                    + "ORDER BY ts.id;";
            PreparedStatement pst = con.prepareStatement(sql);

            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();

            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            while (rs.next()) {
                String transactionId = rs.getString("ts.id");
                int userId = rs.getInt("user_id");
                Long price = rs.getLong("price");
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
                    t.setTransactionDate(rs.getTimestamp("transaction_date"));
                    t.setTransactionStatus(TransactionStatusEnum.valueOf(rs.getString("transaction_status")));
                    t.setRenewal(rs.getBoolean("is_renewal"));
                    t.setTotalCost(price);
                    t.getTransactionInfos().add(tsi);
                    listTransaction.add(t);
                }
            }
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Transaction - SelectAll: Đã đóng kết nối cơ sở dữ liệu");
        }
        return listTransaction;
    }

    @Override
    public ArrayList<TransactionModel> selectByCondition(String condition) {
        ArrayList<TransactionModel> listTransaction = new ArrayList<>();
        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();

        try {
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT ts.*,"
                    + "tsi.domain_id, tsi.price, d.years"
                    + " FROM transactions ts "
                    + "JOIN transactions_info tsi on ts.id = tsi.transactions_id "
                    + "JOIN domains d on tsi.domain_id = d.id "
                    + "WHERE " + condition + ";";
            PreparedStatement pst = con.prepareStatement(sql);

            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();

            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            while (rs.next()) {
                String transactionId = rs.getString("id");
                int userId = rs.getInt("user_id");
                Long price = rs.getLong("price");
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
                    t.setTransactionDate(rs.getTimestamp("transaction_date"));
                    t.setTransactionStatus(TransactionStatusEnum.valueOf(rs.getString("transaction_status")));
                    t.setRenewal(rs.getBoolean("is_renewal"));
                    t.setTotalCost(price);
                    t.getTransactionInfos().add(tsi);
                    listTransaction.add(t);
                }
            }
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Transaction - SelectByCondition: Đã đóng kết nối cơ sở dữ liệu");
        }
        return listTransaction;
    }

    public ArrayList<TransactionModel> selectAll_V3() {
        ArrayList<TransactionModel> listTransaction = new ArrayList<>();

        Connection con = JDBC.getConnection();
        try {
            String sql = "SELECT * FROM transactions ts "
                    + "ORDER BY ts.id;";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            TransactionModel t = new TransactionModel();
            while (rs.next()) {
                t.setTransactionId(rs.getString("id"));
                t.setUserId(rs.getInt("user_id"));
                t.setTransactionDate(rs.getTimestamp("transaction_date"));
                t.setRenewal(rs.getBoolean("is_renewal"));
                t.setTransactionStatus(TransactionStatusEnum.valueOf(rs.getString("transaction_status").toUpperCase()));
                listTransaction.add(t);
            }
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Transaction - SelectAll_V3: Đã đóng kết nối cơ sở dữ liệu");
        }
        return listTransaction;
    }

    public TransactionModel selectById_V2(String transactionId) {
        TransactionModel t = new TransactionModel();

        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();
        try {
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT * FROM transactions WHERE id = ? ORDER BY id DESC;";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, transactionId);

            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();

            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            if (rs.next()) {
                t.setTransactionId(rs.getString("id"));
                t.setUserId(rs.getInt("user_id"));
                t.setTransactionDate(rs.getTimestamp("transaction_date"));
                t.setPaymentMethod(rs.getInt("method"));
                t.setRenewal(rs.getBoolean("is_renewal"));
                t.setTransactionStatus(TransactionStatusEnum.valueOf(rs.getString("transaction_status")));
            }
            pst.close();
            return t;
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Transaction - SelectById_V2: Đã đóng kết nối cơ sở dữ liệu");
        }
        return null;
    }

    public ArrayList<TransactionModel> selectByCondition_V2(String condition) {
        ArrayList<TransactionModel> listTransaction = new ArrayList<>();
        // Bước 1: Mở kết nối đến database
        Connection con = JDBC.getConnection();

        try {
            // Bước 2: Chuẩn bị câu lệnh SQL để truy vấn dữ liệu
            String sql = "SELECT ts.*"
                    + " FROM transactions ts "
                    + "WHERE " + condition + " ORDER BY ts.transaction_date DESC;";
            PreparedStatement pst = con.prepareStatement(sql);

            // Bước 3: Thực thi truy vấn và nhận kết quả
            ResultSet rs = pst.executeQuery();

            // Bước 4: Duyệt qua kết quả và xử lý dữ liệu
            TransactionModel t = new TransactionModel();
            while (rs.next()) {
                t.setTransactionId(rs.getString("id"));
                t.setUserId(rs.getInt("user_id"));
                t.setTransactionDate(rs.getTimestamp("transaction_date"));
                t.setRenewal(rs.getBoolean("is_renewal"));
                t.setTransactionStatus(TransactionStatusEnum.valueOf(rs.getString("transaction_status").toUpperCase()));
                listTransaction.add(t);
            }
            pst.close();
        } catch (SQLException | NullPointerException e) {
            System.out.println(e.getMessage());
        } finally {
            // Bước 5: Đóng kết nối
            JDBC.closeConnection(con);
//            System.out.println("Transaction - SelectByCondition: Đã đóng kết nối cơ sở dữ liệu");
        }
        return listTransaction;
    }
}

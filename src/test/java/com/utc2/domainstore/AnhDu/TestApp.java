package com.utc2.domainstore.AnhDu;

public class TestApp {
    public static void main(String[] args) {

// ------------ PaymentService -----------
//        PaymentService t = new PaymentService();
//        System.out.println(t.getUserPaymentHistory(1));
// ------------ TransactionService -----------
//        TransactionService t = new TransactionService();
//        System.out.println(t.getUserTransactionHistory(1));
//        System.out.println(t.getAllTransaction());
//        System.out.println(t.getTransactionInfomation("HD001"));
        // ------------ PaymentHistory -----------
        // Select All
//        ArrayList<PaymentHistoryModel> payments = PaymentHistoryDAO.getInstance().selectAll();
//        for(PaymentHistoryModel p : payments)
//            System.out.println(p);
        // SelectById
//        PaymentHistoryModel p =new PaymentHistoryModel();
//        p.setPaymentId(1);
//        System.out.println(PaymentHistoryDAO.getInstance().selectById(p));
        // Insert
//        PaymentHistoryModel p =new PaymentHistoryModel("HD001", "74389326", 1, PaymentStatusEnum.FAILED, LocalDate.parse("2024-03-11"));
//        PaymentHistoryDAO.getInstance().insert(p);
        //Update
//        PaymentHistoryModel p = new PaymentHistoryModel(2, "HD001", "74389326", 1, PaymentStatusEnum.COMPLETED, LocalDate.parse("2024-03-11"));
//        PaymentHistoryDAO.getInstance().update(p);
        // Delete
//        PaymentHistoryModel p =new PaymentHistoryModel();
//        p.setPaymentId(2);
//        PaymentHistoryDAO.getInstance().delete(p);
        // ------------ Transaction -----------
        // Select All
//        ArrayList<TransactionModel> transactions = TransactionDAO.getInstance().selectAll();
//        for(TransactionModel tran : transactions){
//            System.out.println(tran);
//        }
        // SelectById
//        TransactionModel t =new TransactionModel();
//        t.setTransactionId("HD001");
//        System.out.println(TransactionDAO.getInstance().selectById(t));
        // Insert
//        TransactionModel t = new TransactionModel("HD002", 1, LocalDate.parse("2024-03-14"));
//        TransactionInfoModel ti1 = new TransactionInfoModel("HD002", 1, 123546);
//        TransactionInfoModel ti2 = new TransactionInfoModel("HD002", 2, 654321);
//        t.getTransactionInfos().add(ti1);
//        t.getTransactionInfos().add(ti2);
//        TransactionDAO.getInstance().insert(t);
        //Update
//        TransactionModel t = new TransactionModel("HD002", 1, LocalDate.parse("2024-05-03"));
//        TransactionDAO.getInstance().update(t);
        // Delete
//        TransactionModel t = new TransactionModel(); t.setTransactionId("HD002");
//        TransactionDAO.getInstance().delete(t);
        // ------------ Transaction Info -----------
        // Select All
//        ArrayList<TransactionInfoModel> listTransactionInfo = TransactionInfoDAO.getInstance().selectAll();
//        for(TransactionInfoModel t : listTransactionInfo)
//            System.out.println(t);
        // SelectById
//        TransactionInfoModel t =new TransactionInfoModel(); t.setTransactionId("HD001");
//        System.out.println(TransactionInfoDAO.getInstance().selectById(t));
        // Insert
//        TransactionInfoModel t = new TransactionInfoModel("HD001", 2, 66666);
//        TransactionInfoDAO.getInstance().insert(t);
        //Update
//        TransactionInfoModel t = new TransactionInfoModel("HD001", 2, 78000);
//        TransactionInfoDAO.getInstance().update(t);
        // Delete
//        TransactionInfoModel t = new TransactionInfoModel("HD001", 2, 78000);
//        TransactionInfoDAO.getInstance().delete(t);
    }
}

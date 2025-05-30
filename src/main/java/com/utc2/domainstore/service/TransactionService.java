package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.*;
import com.utc2.domainstore.repository.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class TransactionService implements ITransactionService {

    private final ArrayList<TransactionModel> transactions = TransactionRepository.getInstance().selectAll_V3();
    private final TransactionRepository transactionRepository = new TransactionRepository();
    private final TransactionInfoRepository transactionInfoRepository = new TransactionInfoRepository();

    @Override
    public JSONObject getAllTransaction() {
        JSONArray jsonArray = new JSONArray();
        for (TransactionModel t : transactionRepository.selectAll()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", t.getTransactionId());
            jsonObject.put("date", t.getTransactionDate());
            jsonObject.put("status", t.getTransactionStatus());
            jsonObject.put("total_price", t.getTotalCost());
            jsonObject.put("user_id", t.getUserId());
            jsonArray.put(jsonObject);
        }
        JSONObject result = new JSONObject();
        result.put("transactions", jsonArray);
        return result;
    }

    @Override
    public JSONObject getAllUserTransaction(JSONObject json) {
        int userId = json.getInt("user_id");
        JSONArray jsonArray = new JSONArray();
        for (TransactionModel t : transactionRepository.selectByCondition("user_id = " + userId)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", t.getTransactionId());
            jsonObject.put("date", t.getTransactionDate());
            jsonObject.put("status", t.getTransactionStatus());
            jsonObject.put("total_price", t.getTotalCost());
            jsonObject.put("user_id", t.getUserId());
            jsonArray.put(jsonObject);
        }
        JSONObject result = new JSONObject();
        result.put("transactions", jsonArray);
        return result;
    }

    @Override
    public JSONObject getTransactionInfomation(JSONObject json) {
        String transactionId = json.getString("transaction_id");
        JSONArray jsonArray = new JSONArray();
        TransactionModel t = TransactionRepository.getInstance().selectById_V2(transactionId);
//        t.setTransactionId(transactionId);
        for (TransactionInfoModel ti : transactionRepository.selectById(t).getTransactionInfos()) {
            JSONObject jsonObject = new JSONObject();
            DomainModel d = new DomainModel();
            d.setId(ti.getDomainId());
            DomainModel domain = DomainRepository.getInstance().selectById(d);
            jsonObject.put("name", domain.getDomainName() + domain.getTopLevelDomainbyId(domain.getTldId()).getTldText());
            jsonObject.put("status", domain.getStatus());
            jsonObject.put("price", domain.getPrice());
            jsonObject.put("years", domain.getYears());
            jsonArray.put(jsonObject);
        }
        JSONObject result = new JSONObject();
        result.put("user_id", t.getUserId());
        result.put("domains", jsonArray);
        return result;
    }

    @Override
    public JSONObject createTransaction(JSONObject json) {
        // request: domains (JSONObject)
        // response: transactionId (String), total(int), status (success / failed)

        //Nếu request rỗng
        if (json.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", "failed");
            jsonObject.put("message", "Dữ liệu đầu vào không hợp lệ");
            System.out.println("Tạo hoá đơn thất bại");
            return jsonObject;
        }
        int userId = json.getInt("user_id"); // lấy id người dùng

        // Nếu tên miền đã nằm trong hoá đơn trước đó
        JSONArray domains = json.getJSONArray("domains");
        for (int i = 0; i < domains.length(); i++){
            JSONObject jsonObject = domains.getJSONObject(i);
            int domainId = getDomainByName(jsonObject.getString("name"));
            String domainName = jsonObject.getString("name");
            if(!transactionRepository.selectByCondition
                    ("user_id = " + userId + " AND " + "domain_id = " + domainId).isEmpty()){
                JSONObject response = new JSONObject();
                response.put("status", "failed");
                response.put("message", "Tên miền " + domainName + " đã có trong hoá đơn !!");
                System.out.println("Tên miền " + domainName + " đã có trong hoá đơn !!");
                return response;
            }
        }
        // Nếu tên miền ở trạng thái sold
        for (int i = 0; i < domains.length(); i++){
            JSONObject jsonObject = domains.getJSONObject(i);
            String domainName = jsonObject.getString("name");
            int domainId = getDomainByName(domainName);
            DomainModel d = DomainRepository.getInstance().selectById(new DomainModel(domainId, null, 0, null, null, 0));
            if(d.getStatus().equals(DomainStatusEnum.SOLD)){
                JSONObject response = new JSONObject();
                response.put("status", "failed");
                response.put("message", "Tên miền " + domainName + " đã được bán !!");
                System.out.println("Tên miền " + domainName + " đã được bán !!");
                return response;
            }
        }
        String transactionId = generateTransactionId(); // tạo id hoá đơn

        TransactionModel tran = new TransactionModel();
        tran.setTransactionId(transactionId);
        tran.setUserId(userId); // request
        tran.setTransactionDate(Timestamp.valueOf(LocalDateTime.now()));
        transactionRepository.insert(tran); // thêm hoá đơn mới

        Long total = processTransactionDetails(transactionId, domains); // tính tổng của 1 hoá đơn

        // trả response cho frontend
        JSONObject response = new JSONObject();
        response.put("transactionId", transactionId);
        response.put("total", total);
        response.put("status", "success");
        response.put("message", "Tạo hoá đơn thành công: " + transactionId);
        System.out.println("Tạo hoá đơn thành công: " + transactionId);
        return response;
    }

    @Override
    public void updateTransactionStatus(String transactionId, TransactionStatusEnum status) {
        // Lưu tên tên miền để thông báo về email
        List<String> domains = new ArrayList<>();

        TransactionModel tran = transactionRepository.selectById_V2(transactionId);
        CustomerModel cus = CustomerRepository.getInstance().selectById(new CustomerModel(tran.getUserId()));
        ArrayList<TransactionInfoModel> listTranInfo = TransactionInfoRepository.getInstance().selectByCondition("transactions_id = '" + transactionId + "'");

        if (TransactionStatusEnum.COMPLETED.equals(status)) {
            // Trạng thái thành công
            for (TransactionInfoModel transactionInfoModel : listTranInfo) {
                // duyệt qua từng chi tiết hoá đơn

                // lấy thông tin tên miền
                DomainModel domain = getInfoDomain(transactionInfoModel);
                // Thêm tên miền vào List<String>
                domains.add(domain.getDomainName()
                        + domain.getTopLevelDomainbyId(domain.getTldId()).getTldText());

                // Cập nhật thông tin domain
                domain.setStatus(DomainStatusEnum.SOLD);
                domain.setActiveDate(Timestamp.valueOf(LocalDateTime.now()));
                domain.setOwnerId(cus.getId());
                domain.setPrice(domain.getTopLevelDomainbyId(domain.getTldId()).getPrice());

                DomainRepository.getInstance().update(domain);
            }
            // gửi thông báo email
            SoldDomainNotifierServices notifier = new SoldDomainNotifierServices();
            notifier.notifySoldDomains(cus.getEmail(), domains);
        } else if (TransactionStatusEnum.CANCELLED.equals(status)) {
            for (TransactionInfoModel transactionInfoModel : listTranInfo) {
                // lấy thông tin tên miền
                DomainModel domain = getInfoDomain(transactionInfoModel);
                domain.setStatus(DomainStatusEnum.AVAILABLE);
                domain.setYears(0);
                DomainRepository.getInstance().update(domain);
            }
        }
        System.out.println("UpdateTransactionStatus: Đã cập nhật thông tin tên miền ");
        // Cập nhật trạng thái hoá đơn
        tran.setTransactionStatus(status);
        transactionRepository.update(tran);
        System.out.println("Cập nhật trạng thái hoá đơn thành công: " + status);
    }

    //Tạo transactionId
    private String generateTransactionId() {
        if (transactions.isEmpty()) return "HD001";
        String lastId = transactions.getLast().getTransactionId();
        int number = Integer.parseInt(lastId.substring(2));
        ++number;
        System.out.println("Tạo mã hoá đơn thành công: " + number);
        return String.format("HD%03d", number);
    }

    // Lấy domain_id
    private int getDomainByName(String name) {
        int index = name.indexOf('.');
        String domainName = name.substring(0, index);
        String tldText = name.substring(index);
        TopLevelDomainModel tld = TopLevelDomainRepository.getInstance().getTLDByName(tldText);
        DomainModel d = DomainRepository.getInstance().getDomainByNameAndTld(name, tld.getId());
        if (d != null) {
            System.out.println("Lấy thành công domain: " + d.getDomainName() + " - " + d.getId());
            return d.getId();
        } else {
            System.out.println("Lấy domain thất bại");
            throw new NoSuchElementException("Domain not found!");
        }
    }

    // Listener để cập nhật trạng thái hoá đơn
    private Long processTransactionDetails(String transactionId, JSONArray jsonArray) {
        long total = 0L;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int domainId = getDomainByName(jsonObject.getString("name"));
            int year = jsonObject.getInt("years");
            long price = jsonObject.getLong("price") * year;
            DomainModel d = DomainRepository.getInstance().selectById(new DomainModel(domainId, null, 0, null, null, 0));
            d.setYears(year); d.setStatus(DomainStatusEnum.SOLD);
            DomainRepository.getInstance().update(d);
            total += price;
            transactionInfoRepository.insert(new TransactionInfoModel(transactionId, domainId, price));
        }
        return total;
    }
    private DomainModel getInfoDomain(TransactionInfoModel transactionInfoModel){
        int domainId = transactionInfoModel.getDomainId(); // tìm id tên miền
        DomainModel d = new DomainModel();
        d.setId(domainId);
        // Lấy dữ liệu tên miền
        return DomainRepository.getInstance().selectById(d);
    }
}

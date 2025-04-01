package com.utc2.domainstore.DuongTan;

import com.utc2.domainstore.entity.database.DomainModel;
import com.utc2.domainstore.repository.CustomerRepository;
import com.utc2.domainstore.repository.DomainRepository;
import com.utc2.domainstore.repository.TopLevelDomainRepository;
import com.utc2.domainstore.entity.database.CustomerModel;
import com.utc2.domainstore.entity.database.RoleEnum;
import com.utc2.domainstore.entity.database.TopLevelDomainModel;
import com.utc2.domainstore.utils.PasswordUtils;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public class testCustomerDAO {

    public static void customerDAOTest(int i) {
        CustomerRepository customerDAO = CustomerRepository.getInstance();

        switch (i) {
            case 1:
                // Thêm người dùng mới với mật khẩu đã băm
                String plainPassword = "pasasd";
                String hashedPassword = PasswordUtils.hashedPassword(plainPassword); 

                CustomerModel newUser = new CustomerModel(
                    0, "Tan", "tan@gmail.com", 
                    "0327876534", "082265218564", hashedPassword, 
                    RoleEnum.user, new Timestamp(System.currentTimeMillis())
                );

                int insertResult = customerDAO.insert(newUser);
                System.out.println("Insert User: " + insertResult);
                break;

            case 2:
                //  Lấy danh sách tất cả người dùng
                List<CustomerModel> users = customerDAO.selectAll();
                System.out.println("Danh sách người dùng:");
                for (CustomerModel user : users) {
                    System.out.println(user);
                }
                break;
            
            case 3:
                // Tìm người dùng theo ID
                CustomerModel findUser = new CustomerModel(1, "", "", "", "", "",RoleEnum.user, null);
                CustomerModel result = customerDAO.selectById(findUser);
                System.out.println("User found: " + result);
                break;
            
            case 4:
                // Cập nhật thông tin người dùng
                String newPassword = "newPassword123";
                String hashPassword = PasswordUtils.hashedPassword(newPassword);
                CustomerModel updateUser = new CustomerModel(1, "Updated User", "updated@gmail.com", "0987654321", "123456789012", hashPassword, RoleEnum.admin, new Timestamp(System.currentTimeMillis()));
                int updateResult = customerDAO.update(updateUser);
                System.out.println("Update User: " + updateResult);
                break;
            
            case 5:
                // Xóa người dùng
                CustomerModel deleteUser = new CustomerModel(5, "", "", "", "", "", RoleEnum.user, null);
                int deleteResult = customerDAO.delete(deleteUser);
                System.out.println("Delete User: " + deleteResult);
                break;
        }
    }

public static void domainDAOTest(int i) {
        DomainRepository domainDAO = DomainRepository.getInstance();

        switch (i) {
            case 1:
                // Thêm domain mới
                DomainModel newDomain = new DomainModel(
                        0, 
                        "example.com", 
                        1, 
                        DomainModel.DomainStatusEnum.AVAILABLE, 
                        new Date(System.currentTimeMillis()), // active_date
                        2, 
                        1, 
                        new Date(System.currentTimeMillis()) // created_at
                );
                int insertDomainResult = domainDAO.insert(newDomain);
                System.out.println("Insert Domain: " + (insertDomainResult > 0 ? "Success" : "Failed"));
                break;

            case 2:
                // Lấy danh sách tất cả domains
                List<DomainModel> domains = domainDAO.selectAll();
                System.out.println("Danh sách domains:");
                for (DomainModel domain : domains) {
                    System.out.println(domain);
                }
                break;
            
            case 3:
                // Tìm domain theo ID
                DomainModel findDomain = new DomainModel(1, "", 0, null, null, 0, null, null);
                DomainModel domainResult = domainDAO.selectById(findDomain);
                System.out.println("Domain found: " + (domainResult != null ? domainResult : "Not found"));
                break;
            
            case 4:
                // Cập nhật thông tin domain
                DomainModel updateDomain = new DomainModel(
                        1, 
                        "updated-example.com", 
                        1, 
                        DomainModel.DomainStatusEnum.SOLD, 
                        new Date(System.currentTimeMillis()), 
                        3, 
                        2, 
                        new Date(System.currentTimeMillis()) 
                );
                int updateDomainResult = domainDAO.update(updateDomain);
                System.out.println("Update Domain: " + (updateDomainResult > 0 ? "Success" : "Failed"));
                break;
            
            case 5:
                // Xóa domain
                DomainModel deleteDomain = new DomainModel(3, "", 0, null, null, 0, null, null);
                int deleteDomainResult = domainDAO.delete(deleteDomain);
                System.out.println("Delete Domain: " + (deleteDomainResult > 0 ? "Success" : "Failed"));
                break;
            
            default:
                System.out.println("Invalid test case number.");
        }
    }
    public static void topLevelDomainDAOTest(int i) {
        TopLevelDomainRepository tldDAO = new TopLevelDomainRepository();

        switch (i) {
            case 1:
                // Thêm TLD mới
                TopLevelDomainModel newTLD = new TopLevelDomainModel(0, ".tech", 1500);
                int insertResult = tldDAO.insert(newTLD);
                System.out.println("Insert TLD: " + (insertResult > 0 ? "Success, ID = " + insertResult : "Failed"));
                break;

            case 2:
                // Lấy danh sách tất cả TLDs
                List<TopLevelDomainModel> tlds = tldDAO.selectAll();
                System.out.println("Danh sách TLDs:");
                for (TopLevelDomainModel tld : tlds) {
                    System.out.println(tld);
                }
                break;
            
            case 3:
                // Tìm TLD theo ID
                int tldId = 1; // ID cần tìm
                TopLevelDomainModel findTLD = new TopLevelDomainModel(tldId, "", 0);
                TopLevelDomainModel result = tldDAO.selectById(findTLD);
                System.out.println("TLD found: " + (result != null ? result : "Not found"));
                break;
            
            case 4:
                // Cập nhật TLD
                TopLevelDomainModel updateTLD = new TopLevelDomainModel(1, ".tech-updated", 1800);
                int updateResult = tldDAO.update(updateTLD);
                System.out.println("Update TLD: " + (updateResult > 0 ? "Success" : "Failed"));
                break;
            
            case 5:
                // Xóa TLD
                int deleteTLDId = 8; // ID cần xóa
                TopLevelDomainModel deleteTLD = new TopLevelDomainModel(deleteTLDId, "", 0);
                int deleteResult = tldDAO.delete(deleteTLD);
                System.out.println("Delete TLD: " + (deleteResult > 0 ? "Success" : "Failed"));
                break;

            default:
                System.out.println("Invalid test case number.");
        }
    }
    
    public static void UITest() {
//        UserSession.login("KH001", "admin");
//        MainForm.start();
//        LoginForm.start();
    }

    public static void main(String[] args) {
//      customerDAOTest(4);
        
//      domainDAOTest(3);

//        topLevelDomainDAOTest(1);
        
        //  Kiểm tra giao diện UI
        UITest();
    }
}

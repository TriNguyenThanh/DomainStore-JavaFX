package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.*;
import com.utc2.domainstore.repository.CustomerRepository;
import com.utc2.domainstore.repository.DomainRepository;
import com.utc2.domainstore.repository.PaymentHistoryRepository;
import com.utc2.domainstore.repository.TransactionRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenerateService implements IGenerateService {
    private final DomainRepository domainRepo = new DomainRepository();

    @Override
    public void generateInvoicePDF(String transactionId) {
        TransactionModel tran = TransactionRepository.getInstance().selectById(new TransactionModel(transactionId, null, null));
        CustomerModel cus = CustomerRepository.getInstance().selectById(new CustomerModel(tran.getUserId()));
        PaymentHistoryModel payment = PaymentHistoryRepository.getInstance().selectById(new PaymentHistoryModel(transactionId, null, null, null, null));
        copyFont();
//        URL resource = GenerateService.class.getClassLoader().getResource("report/invoice_domain.jasper");
//        String jasperFilePath = null;
//        if (resource != null) jasperFilePath = resource.getPath();
        try {
//            JasperReport jasperReport = JasperCompileManager.compileReport(jasperFilePath);
            InputStream jasperStream = getClass().getResourceAsStream("/report/invoice_domain.jasper");
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            Map<String, Object> data = new HashMap<>();
            DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter outputFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            data.put("fullName", cus.getFullName());
            data.put("phone", cus.getPhone());
            data.put("email", cus.getEmail());
            String transactionDate = String.valueOf(tran.getTransactionDate());
            LocalDate parsedDate = LocalDate.parse(transactionDate, inputFormat);
            data.put("transactionDate", parsedDate.format(outputFormat));

            parsedDate = LocalDate.parse(String.valueOf(Date.valueOf(LocalDate.now())), inputFormat);
            data.put("invoiceDate", parsedDate.format(outputFormat));
            data.put("transactionId", transactionId);
            if (payment != null) {
                data.put("paymentMethod", String.valueOf(PaymentTypeEnum.getPaymentMethod(payment.getPaymentMethodId())));
                if (PaymentStatusEnum.COMPLETED.equals(payment.getPaymentStatus())) {
                    data.put("paymentStatus", "ĐÃ THANH TOÁN");
                } else data.put("paymentStatus", "CHƯA THANH TOÁN");
                data.put("accountName", cus.getFullName());
                data.put("accountNumber", "9704198526191432198");
                data.put("bank", "NCB - Ngân hàng Quốc Dân");
                data.put("content", "Thanh toán hóa đơn " + transactionId);
            } else {
                data.put("paymentMethod", "Không");
                data.put("paymentStatus", "CHƯA THANH TOÁN");
                data.put("accountName", "Không");
                data.put("accountNumber", "Không");
                data.put("bank", "Không");
                data.put("content", "Không");
            }
            List<Map<String, Object>> list = new ArrayList<>();
            list.add(data);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

            List<Map<String, Object>> dataDomain = new ArrayList<>();
            for (TransactionInfoModel t : tran.getTransactionInfos()) {
                DomainModel domain = domainRepo.selectById(new DomainModel(t.getDomainId(), null, 0, null, null, 0));
                Map<String, Object> mp = new HashMap<>();
                TopLevelDomainModel tld = domain.getTopLevelDomainbyId(domain.getTldId());
                mp.put("domainName", domain.getDomainName() + tld.getTldText());
                mp.put("years", domain.getYears());
                mp.put("price", tld.getPrice());
                dataDomain.add(mp);
            }
            JRBeanCollectionDataSource tableDataSource = new JRBeanCollectionDataSource(dataDomain);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("TABLE_DATA_SOURCE", tableDataSource);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            System.out.println("Đang tiến hành xuất PDF ......");
            File invoiceDir = new File("invoices");
            if (!invoiceDir.exists()) {
                invoiceDir.mkdirs();
            }
            // Xử lý tên file an toàn
            String safeName = cus.getFullName().replaceAll("\\s+", "");
            // Tạo đường dẫn file
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_ddMMyyyy_HHmmss");
            String dateTime = LocalDateTime.now().format(formatter);
            String pdfPath = invoiceDir.getAbsolutePath() + File.separator + transactionId + "_" + safeName + dateTime + ".pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);

            String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"; // Đường dẫn đến file chrome.exe
            String command = "cmd /c start \"\" \"" + pdfPath + "\"";
            Runtime.getRuntime().exec(command);
//            new ProcessBuilder(chromePath, pdfPath).start();
        } catch (JRException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void copyFont() {
        String sourceDir = "C:\\Windows\\Fonts";
        String destinationDir = "C:\\Fonts_Arial";

        try {
            Path sourcePath = Paths.get(sourceDir);
            Path destinationPath = Paths.get(destinationDir);

            if (!Files.exists(sourcePath)) {
                throw new IOException("Thư mục nguồn không tồn tại: " + sourceDir);
            }

            Files.createDirectories(destinationPath);

            try (var stream = Files.walk(sourcePath)) {
                stream.forEach(source -> {
                    try {
                        Path destination = destinationPath.resolve(sourcePath.relativize(source));
                        if (Files.isDirectory(source)) {
                            Files.createDirectories(destination);
                        } else if (!Files.exists(destination)) { // Chỉ copy nếu file đích chưa tồn tại
                            Files.copy(source, destination);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi copy project: " + e.getMessage());
        }
    }
}

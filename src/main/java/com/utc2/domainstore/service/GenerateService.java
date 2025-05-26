package com.utc2.domainstore.service;

import com.utc2.domainstore.entity.database.*;
import com.utc2.domainstore.repository.CustomerRepository;
import com.utc2.domainstore.repository.DomainRepository;
import com.utc2.domainstore.repository.PaymentHistoryRepository;
import com.utc2.domainstore.repository.TransactionRepository;
import com.utc2.domainstore.utils.EmailUtil;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
        try {
            System.out.println("Đang tiến hành xuất PDF ......");
            JasperReport jasperReport = input("/report/invoice_domain.jasper");
            Map<String, Object> data = new HashMap<>();
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            data.put("fullName", cus.getFullName());
            data.put("phone", cus.getPhone());
            data.put("email", cus.getEmail());
            data.put("transactionDate", outputFormat.format(tran.getTransactionDate()));
            data.put("invoiceDate", outputFormat.format(Timestamp.valueOf(LocalDateTime.now())));
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
            System.out.println("Lấy dữ liệu thành công ...");
            JRBeanCollectionDataSource tableDataSource = new JRBeanCollectionDataSource(dataDomain);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("TABLE_DATA_SOURCE", tableDataSource);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            File invoiceDir = new File("invoices");
            if (!invoiceDir.exists()) {
                System.out.println(invoiceDir.mkdirs());
            }
            System.out.println("Đang lưu file ...");
            // Xử lý tên file an toàn
            String safeName = cus.getFullName().replaceAll("\\s+", "");
            // Tạo đường dẫn file
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_ddMMyyyy_HHmmss");
            String dateTime = LocalDateTime.now().format(formatter);
            String pdfPath = invoiceDir.getAbsolutePath() + File.separator + transactionId + "_" + safeName + dateTime + ".pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, pdfPath);

            System.out.println("Đang gửi về mail ...");
            EmailUtil.sendEmailFile(cus.getEmail(), "Hoá đơn dịch vụ UTC2 Domain Store",
                    "Cảm ơn Quý khách đã sử dụng dịch vụ UTC2 Domain Store", pdfPath);

            String command = "cmd /c start \"\" \"" + pdfPath + "\"";
            Runtime.getRuntime().exec(command);

            System.out.println("Xuất PDF thành công !!");
        } catch (JRException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void exportExcel(String type) {
        copyFont();
        try {
            JasperReport jasperReport;
            if (type.equals("domain"))
                jasperReport = input("/report/export_domain.jasper");
            else if (type.equals("user"))
                jasperReport = input("/report/export_user.jasper");
            else {
                System.out.println("Dữ liệu đầu vào không hợp lệ !!");
                return;
            }

            List<Map<String, Object>> TABLE_DATASET = new ArrayList<>();
            if (type.equals("domain")) {
                for (DomainModel d : DomainRepository.getInstance().selectAll()) {
                    Map<String, Object> mp = new HashMap<>();
                    mp.put("id", d.getId());
                    mp.put("domain_name", d.getDomainName());
                    mp.put("tld_id", d.getTldId());
                    mp.put("status", String.valueOf(d.getStatus()));
                    if (d.getActiveDate() != null) mp.put("active_date", String.valueOf(d.getActiveDate()));
                    else mp.put("active_date", "");
                    mp.put("years", d.getYears());
                    mp.put("price", d.getPrice());
                    if (d.getOwnerId() != null) mp.put("owner_id", d.getOwnerId());
                    else mp.put("owner_id", 0);
                    mp.put("created_at", String.valueOf(d.getCreatedAt()));
                    TABLE_DATASET.add(mp);
                }
            } else {
                for (CustomerModel c : CustomerRepository.getInstance().selectAll()) {
                    Map<String, Object> mp = new HashMap<>();
                    mp.put("id", c.getId());
                    mp.put("full_name", c.getFullName());
                    mp.put("email", c.getEmail());
                    mp.put("phone", c.getPhone());
                    mp.put("role", String.valueOf(c.getRole()));
                    mp.put("password", c.getPasswordHash());
                    if (c.getIsDeleted()) mp.put("is_deleted", "Khoá");
                    else mp.put("is_deleted", "Hoạt động");
                    mp.put("created_at", String.valueOf(c.getCreatedAt()));
                    TABLE_DATASET.add(mp);
                }
            }

            JRBeanCollectionDataSource tableDataSource = new JRBeanCollectionDataSource(TABLE_DATASET);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("TABLE_DATA_SOURCE", tableDataSource);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            System.out.println("Đang tiến hành xuất Excel ......");
            File invoiceDir = new File("invoices");
            if (!invoiceDir.exists()) {
                System.out.println(invoiceDir.mkdirs());
            }
            // Tạo đường dẫn file
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("_ddMMyyyy_HHmmss");
            String dateTime = LocalDateTime.now().format(formatter);
            String Path = invoiceDir.getAbsolutePath() + File.separator + type + "_" + dateTime + ".xlsx";

            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(Path));
            SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
            config.setOnePagePerSheet(false);
            config.setDetectCellType(true);
            config.setCollapseRowSpan(false);

            exporter.setConfiguration(config);
            exporter.exportReport();

            //String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"; // Đường dẫn đến file chrome.exe
            String command = "cmd /c start \"\" \"" + Path + "\"";
            Runtime.getRuntime().exec(command);
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

    private JasperReport input(String file) throws JRException {
        InputStream jasperStream = getClass().getResourceAsStream(file);
        return (JasperReport) JRLoader.loadObject(jasperStream);
    }
}

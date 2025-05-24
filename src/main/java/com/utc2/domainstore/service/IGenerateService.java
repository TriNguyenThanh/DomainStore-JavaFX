package com.utc2.domainstore.service;

public interface IGenerateService {
    void generateInvoicePDF(String transactionId);
    void exportExcel(String type);
}

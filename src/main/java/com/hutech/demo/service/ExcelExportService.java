package com.hutech.demo.service;

import com.hutech.demo.model.Order;
import com.hutech.demo.model.OrderDetail;
import com.hutech.demo.model.Product;
import com.hutech.demo.model.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    public ByteArrayInputStream exportProductListToExcel(List<Product> products) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Tên sách", "Tác giả", "Giá", "Mô tả", "Thể loại"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(product.getAuthor());
                row.createCell(3).setCellValue(product.getPrice());
                row.createCell(4).setCellValue(product.getDescription());
                row.createCell(5).setCellValue(product.getCategory().getName());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ByteArrayInputStream exportOrderListToExcel(List<Order> orders) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Orders");

            // Headers
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Tên khách hàng", "Email", "Địa chỉ", "Số điện thoại", "Thanh Toán", "Sản Phẩm", "Ghi chú", "Hành động"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Data rows
            int rowNum = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getCustomerName());
                row.createCell(2).setCellValue(order.getEmail());
                row.createCell(3).setCellValue(order.getStreetAddress());
                row.createCell(4).setCellValue(order.getPhoneNumber());
                row.createCell(5).setCellValue(order.getThanhToan());

                // Concatenate order details into one cell
                StringBuilder products = new StringBuilder();
                for (OrderDetail detail : order.getOrderDetails()) {
                    products.append(detail.getQuantity()).append(" x ").append(detail.getProduct().getName()).append("\n");
                }
                row.createCell(6).setCellValue(products.toString());

                row.createCell(7).setCellValue(order.getNote());
                // Add action link or button if needed
                // For example: row.createCell(8).setCellValue("View or Edit");

                // You can add more columns as needed

            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public ByteArrayInputStream exportUserListToExcel(List<User> users) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");

            // Tạo header
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Tên đăng nhập");
            headerRow.createCell(2).setCellValue("Email");
            headerRow.createCell(3).setCellValue("Số điện thoại");
            // Thêm dữ liệu
            int rowIdx = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getUsername());
                row.createCell(2).setCellValue(user.getEmail());
                row.createCell(3).setCellValue(user.getPhone());}

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to export data to Excel file: " + e.getMessage());
        }
    }

}


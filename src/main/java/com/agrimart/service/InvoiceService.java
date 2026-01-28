package com.agrimart.service;

import com.agrimart.entity.Order;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class InvoiceService {

    public byte[] generateInvoice(Order order) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.DARK_GRAY);
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.GRAY);
            Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);

            // 1. Header (Company Name)
            Paragraph header = new Paragraph("AGRIMART", headerFont);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);

            Paragraph subHeader = new Paragraph("Order Invoice", subHeaderFont);
            subHeader.setAlignment(Element.ALIGN_CENTER);
            subHeader.setSpacingAfter(20);
            document.add(subHeader);

            // 2. Order & Customer Details Table
            PdfPTable detailsTable = new PdfPTable(2);
            detailsTable.setWidthPercentage(100);
            detailsTable.setSpacingAfter(20);

            // Order Info
            PdfPCell orderCell = new PdfPCell();
            orderCell.setBorder(Rectangle.NO_BORDER);

            // Use Public ID if available, else fallback
            String displayId = (order.getPublicOrderId() != null) ? order.getPublicOrderId()
                    : String.valueOf(order.getId());
            orderCell.addElement(new Paragraph("Order ID: #" + displayId, regularFont));

            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("dd-MMM-yyyy HH:mm");
            String orderDate = (order.getOrderDate() != null) ? order.getOrderDate().format(formatter) : "N/A";
            orderCell.addElement(new Paragraph("Date: " + orderDate, regularFont));

            orderCell.addElement(new Paragraph("Status: " + order.getStatus(), regularFont));
            orderCell.addElement(new Paragraph("Payment: " + order.getPaymentMode(), regularFont));
            if (order.getRazorpayPaymentId() != null) {
                orderCell.addElement(new Paragraph("Txn ID: " + order.getRazorpayPaymentId(), regularFont));
            }

            // Customer Info (Defensive checks)
            PdfPCell userCell = new PdfPCell();
            userCell.setBorder(Rectangle.NO_BORDER);
            userCell.addElement(new Paragraph("Billed To:", subHeaderFont));

            // USE CHECKOUT NAME (not account name)
            String name = (order.getFullName() != null) ? order.getFullName() : "Guest";
            userCell.addElement(new Paragraph(name, regularFont));

            // Address Parsing (Assuming stored as comma separated or just raw string)
            String address = order.getAddress() != null ? order.getAddress() : "No Address Provided";
            userCell.addElement(new Paragraph(address, regularFont));

            String city = order.getCity() != null ? order.getCity() : "";
            String pincode = order.getPincode() != null ? order.getPincode() : "";
            if (!city.isEmpty() || !pincode.isEmpty()) {
                userCell.addElement(new Paragraph(city + " - " + pincode, regularFont));
            }

            if (order.getMobile() != null) {
                userCell.addElement(new Paragraph("Phone: " + order.getMobile(), regularFont));
            }

            detailsTable.addCell(orderCell);
            detailsTable.addCell(userCell);
            document.add(detailsTable);

            // 3. Items Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 4, 1, 2, 2 }); // Relative widths

            // Table Headers
            String[] headers = { "Product", "Qty", "Price", "Total" };
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, tableHeaderFont));
                cell.setBackgroundColor(new Color(22, 163, 74)); // Green-600 like
                cell.setPadding(8);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Table Data
            if (order.getOrderItems() != null) {
                for (com.agrimart.entity.OrderItem item : order.getOrderItems()) {
                    String pName = (item.getProduct() != null) ? item.getProduct().getName() : "Unknown Product";
                    PdfPCell nameCell = new PdfPCell(new Phrase(pName, regularFont));
                    nameCell.setPadding(5);
                    table.addCell(nameCell);

                    PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), regularFont));
                    qtyCell.setPadding(5);
                    qtyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(qtyCell);

                    PdfPCell priceCell = new PdfPCell(new Phrase("₹" + item.getPrice(), regularFont));
                    priceCell.setPadding(5);
                    priceCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(priceCell);

                    double lineTotal = item.getPrice() * item.getQuantity();
                    PdfPCell totalCell = new PdfPCell(new Phrase("₹" + lineTotal, regularFont));
                    totalCell.setPadding(5);
                    totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    table.addCell(totalCell);
                }
            } else {
                PdfPCell noItems = new PdfPCell(new Phrase("No items found in order", regularFont));
                noItems.setColspan(4);
                noItems.setPadding(10);
                noItems.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(noItems);
            }

            document.add(table);

            // 4. Grand Total
            Paragraph totalPara = new Paragraph("Grand Total: ₹" + order.getTotalAmount(), headerFont);
            totalPara.setAlignment(Element.ALIGN_RIGHT);
            totalPara.setSpacingBefore(10);
            document.add(totalPara);

            document.close();
            return out.toByteArray();
        }
    }
}

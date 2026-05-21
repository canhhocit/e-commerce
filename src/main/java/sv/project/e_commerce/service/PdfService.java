package sv.project.e_commerce.service;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import sv.project.e_commerce.model.entity.Order;
import sv.project.e_commerce.model.entity.OrderItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateOrderInvoice(Order order) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            BaseFont baseFont;
            try {
                String fontPath = "C:/Windows/Fonts/Arial.ttf";
                if (!new File(fontPath).exists()) {
                    fontPath = "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"; // Linux fallback
                }
                if (!new File(fontPath).exists()) {
                    baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
                } else {
                    baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                }
            } catch (Exception e) {
                baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            }

            Font headerFont = new Font(baseFont, 22, Font.BOLD, java.awt.Color.BLACK);
            Font infoFont = new Font(baseFont, 11, Font.NORMAL, java.awt.Color.BLACK);
            Font headFont = new Font(baseFont, 11, Font.BOLD, java.awt.Color.BLACK);
            Font totalFont = new Font(baseFont, 16, Font.BOLD, java.awt.Color.RED);
            Font footerFont = new Font(baseFont, 10, Font.ITALIC, java.awt.Color.GRAY);

            Paragraph header = new Paragraph("HOA DON THANH TOAN (INVOICE)", headerFont);
            header.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(header);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Ma don hang (Order ID): #" + order.getId(), infoFont));
            document.add(new Paragraph("Khach hang (Customer): " + order.getUser().getFullName(), infoFont));
            document.add(new Paragraph("Email: " + order.getUser().getEmail(), infoFont));
            document.add(new Paragraph("Dia chi (Address): " + order.getShippingAddress(), infoFont));
            document.add(new Paragraph("Ngay tao (Date): " + order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), infoFont));
            document.add(new Paragraph("Trang thai (Status): " + order.getStatus().name(), infoFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4f, 1f, 2f, 2.5f});

            String[] headers = {"San pham (Product)", "So luong (Qty)", "Don gia (Price)", "Thanh tien (Subtotal)"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, headFont));
                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                cell.setPadding(8);
                cell.setBackgroundColor(java.awt.Color.LIGHT_GRAY);
                table.addCell(cell);
            }

            for (OrderItem item : order.getItems()) {
                table.addCell(new Phrase(item.getProduct().getName(), infoFont));
                PdfPCell qtyCell = new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), infoFont));
                qtyCell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                table.addCell(qtyCell);
                
                table.addCell(new Phrase(String.format("%,.0f", item.getPrice()) + " VND", infoFont));
                table.addCell(new Phrase(String.format("%,.0f", item.getPrice() * item.getQuantity()) + " VND", infoFont));
            }
            document.add(table);

            Paragraph totals = new Paragraph();
            totals.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
            totals.add(new Phrase("\nTONG TIEN DA THANH TOAN (TOTAL PAID): " + String.format("%,.0f", order.getTotalAmount()) + " VND", totalFont));
            document.add(totals);

            document.add(new Paragraph("\n\n"));
            Paragraph footer = new Paragraph("Cam on quy khach da mua hang tai HairFit!", footerFont);
            footer.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }
}

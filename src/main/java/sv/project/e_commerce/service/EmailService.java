package sv.project.e_commerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import sv.project.e_commerce.model.entity.Order;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token) {
        String subject = "Email Verification";
        String verificationUrl = "http://localhost:8080/mouse-shop/auth/verify?token=" + token;
        String message = "Please click the link below to verify your email:\n" + verificationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }

    public void sendInvoiceEmail(String to, Order order, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Hoa don thanh toan don hang #" + order.getId() + " - HairFit");
            helper.setText("Chao " + order.getUser().getFullName() + ",\n\n"
                    + "Cam on ban da mua sam tai HairFit. Don hang #" + order.getId() + " cua ban da duoc thanh toan thanh cong.\n"
                    + "Chung toi xin gui kem hoa don thanh toan duoi dang tep PDF.\n\n"
                    + "Tran trong,\n"
                    + "Doi ngu HairFit");

            // Attach PDF
            helper.addAttachment("invoice_" + order.getId() + ".pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

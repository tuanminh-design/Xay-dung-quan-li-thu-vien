package com.thuvien.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtil {
    // Thay bằng Email và Mật khẩu ứng dụng (App Password) của bạn
    private static final String MY_EMAIL = "thuvien.enterprise.demo@gmail.com";
    private static final String MY_PASSWORD = "abcd efgh ijkl mnop"; // App password 16 ký tự

    public static void sendEmail(String toEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MY_EMAIL, MY_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MY_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("📧 Đã gửi Email nhắc nhở thành công tới: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("❌ Lỗi gửi Email tới " + toEmail + ": " + e.getMessage());
        }
    }
}
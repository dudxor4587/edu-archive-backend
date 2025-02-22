package com.backend.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Async
    public void sendApprovalEmail(String to, String name) {
        String subject = "Edu Archive 회원가입 승인 완료";
        String body = createEmailBody(name);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        }
    }

    private String createEmailBody(String name) {
        return "<html>"
                + "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">"
                + "<h2 style=\"color: #333333; text-align: center;\">회원가입 승인 완료</h2>"
                + "<p style=\"font-size: 16px; color: #555555;\">안녕하세요, <strong style=\"color: #2c3e50;\">" + name + "님!</strong></p>"
                + "<p style=\"font-size: 16px; color: #555555;\">귀하의 회원가입 요청이 승인되었습니다.</p>"
                + "<p style=\"font-size: 16px; color: #555555;\">이제 정상적으로 서비스를 이용하실 수 있습니다.</p>"
                + "<p style=\"text-align: center;\">"
                + "<a href=\"https://edu-archive.site\" "
                + "style=\"background-color: #3498db; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;\">"
                + "로그인하기"
                + "</a>"
                + "</p>"
                + "<p style=\"font-size: 14px; color: #777777; text-align: center;\">감사합니다.</p>"
                + "<footer style=\"text-align: center; font-size: 12px; color: #bbb;\">"
                + "<p>Edu Archive</p>"
                + "</footer>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

    @Async
    public void sendAdminNotificationEmail(String name) {
        String subject = "새로운 회원가입 요청";
        String body = createAdminNotificationBody(name);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo("dudxor4587@gmail.com");
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        }
    }

    private String createAdminNotificationBody(String name) {
        return "<html>"
                + "<body style=\"font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;\">"
                + "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\">"
                + "<h2 style=\"color: #333333; text-align: center;\">새로운 회원가입 요청</h2>"
                + "<p style=\"font-size: 16px; color: #555555;\">새로운 회원가입 요청이 있습니다. 아래 정보를 확인해주세요.</p>"
                + "<p style=\"font-size: 16px; color: #555555;\">회원 이름: <strong style=\"color: #2c3e50;\">" + name + "</strong></p>"
                + "<p style=\"text-align: center;\">"
                + "<a href=\"https://edu-archive.site\" "
                + "style=\"background-color: #3498db; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;\">"
                + "회원가입 승인하기"
                + "</a>"
                + "</p>"
                + "<footer style=\"text-align: center; font-size: 12px; color: #bbb;\">"
                + "<p>Edu Archive</p>"
                + "</footer>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
}

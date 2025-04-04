package com.example.soeiapi.services;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.entities.UserResetPasswordTokenEntity;
import com.example.soeiapi.repositories.UserRepository;
import com.example.soeiapi.repositories.UserResetPasswordTokenRespository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
    @Value("${web.base.url}")
    private String webBaseUrl;

    // logger
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    private final UserRepository userRepository;
    private final UserResetPasswordTokenRespository userResetPasswordTokenRespository;
    private final TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender mailSender;

    public MailService(UserRepository userRepository, TemplateEngine templateEngine,
            UserResetPasswordTokenRespository userResetPasswordTokenRespository) {
        this.userResetPasswordTokenRespository = userResetPasswordTokenRespository;
        this.userRepository = userRepository;
        this.templateEngine = templateEngine;
    }

    public void sendResetPasswordEmailByUserId(Long userID, Integer tokenExpiryMinutes) {
        try {

            UserEntity user = userRepository.findById(userID).orElseThrow(() -> new Exception("User not found"));
            UserResetPasswordTokenEntity userResetPasswordToken = userResetPasswordTokenRespository
                    .findByUser_UserId(user.getUserId())
                    .orElseThrow(() -> new Exception("User reset password token not found"));
            String token = userResetPasswordToken.getToken();

            String resetPasswordUrl = webBaseUrl + "/#/reset-password/" + token;
            String tokenExpiry = (tokenExpiryMinutes / 60) >= 1 ? String.valueOf(tokenExpiryMinutes / 60) + " hours"
                    : String.valueOf(tokenExpiryMinutes) + " minutes";

            String subject = "Motor Compulsory Centralized System: Account Creation-Action required (Reset Password)";
            String templateName = "reset-password-email-template";
            Map<String, Object> variables = Map.of(
                    "dear", user.getUsername(),
                    "tokenExpiry", tokenExpiry,
                    "resetPasswordUrl", resetPasswordUrl);
            List<String> to = List.of(user.getEmail());
            List<String> cc = List.of();
            List<String> bcc = List.of();

            sendHtmlEmail(to, cc, bcc, subject, templateName, variables);

            logger.info("Email sent successfully to " + user.getEmail());

        } catch (Exception e) {
            logger.error("Error sending email: " + e.getMessage());
            throw new RuntimeException("Error sending email: " + e.getMessage());
        }
    }

    public void sendHtmlEmail(List<String> to, List<String> cc, List<String> bcc, String subject, String templateName,
            Map<String, Object> variables) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        // Process Thymeleaf template
        Context context = new Context();
        context.setVariables(variables);
        String htmlContent = templateEngine.process(templateName, context);

        helper.setSubject(subject);
        helper.setFrom(new InternetAddress("no-reply@agl.com.la", "Motor Compulsory Centralized System",
                StandardCharsets.UTF_8.name()));
        helper.setTo(to.toArray(new String[0]));
        // cc
        if (cc != null && !cc.isEmpty()) {
            helper.setCc(cc.toArray(new String[0])); // Converts List to Array
        }

        // bcc
        if (bcc != null && !bcc.isEmpty()) {
            helper.setBcc(bcc.toArray(new String[0])); // Converts List to Array
        }
        helper.setText(htmlContent, true); // Enable HTML

        mailSender.send(message);

        // SimpleMailMessage message = new SimpleMailMessage();
        // message.setTo(emailTestSendRequetDto.getTo());
        // message.setCc("phanakhone@agl.com.la", "julee@agl.com.la");
        // message.setSubject("Motor Compulsory Centralized System: Account
        // Creation-Action required");
        // message.setText(body);
        // message.setFrom("no-reply@agl.com.la");

        // mailSender.send(message);
    }

}

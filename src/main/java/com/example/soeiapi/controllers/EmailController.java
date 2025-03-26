package com.example.soeiapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.soeiapi.dto.EmailTestSendRequetDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/super-admin/email")
public class EmailController {
    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/test-send")
    public String sendTestMail(@RequestBody EmailTestSendRequetDto emailTestSendRequetDto) {

        try {
            // Motor Compulsory Centralized System<no-reply@agl.com.la>
            // subject: "Motor Compulsory Centralized System: Account Creation-Action
            // required"

            /*
             * Dear Company_name,
             * Welcome to the Motor Compulsory Centralized System! In order to complete the
             * account creation process, you need to verify your email address.
             * 
             * Click link below to confirm your email address. The link is valid for 24
             * hours from the time of this email
             * 
             * Link: // edit profile
             * 
             * Company_ID : In_AGL_0001
             * Username: ins_agl_0001
             * passsword: 123213 (temporary password) not send
             * 
             * Best regards,
             * Super Admin(SOEI)
             * 
             * footer
             * This is a system generated email, please do not respond to this message.
             * Replies to this e-mail will not be read.
             * 
             */
            String body = "<body style=\"font-family: 'Poppins', Arial, sans-serif;\">" +
                    "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
                    "<tr>" +
                    "<td align=\"center\" style=\"padding: 20px;\">" +
                    "<table class=\"content\" width=\"600\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" style=\"border-collapse: collapse; border: 1px solid #cccccc;\">"
                    +
                    "<!-- Header -->" +
                    "<tr>" +
                    "<td class=\"header\" style=\"background-color: #345C72; text-align: center; color: white; font-size: 24px;\">"
                    +
                    // "<img src=\"data:image/png;base64," + _img_Logo + "\" alt=\"description\"/>"
                    // +
                    "</td>" +
                    "</tr>" +
                    "<!-- Body -->" +
                    "<tr>" +
                    "<td class=\"body\" style=\"padding: 40px; text-align: left; font-size: 16px; line-height: 1.6;\">"
                    +
                    "Dear " + "test" + " " + "test" + ",<br><br>" +
                    "<p>Thank you for trusting in Allianz Insurance Laos to protect your risk.</p>" +

                    "<p>Best regards,<br> Allianz Insurance Laos</p>" +
                    "</td>" +
                    "</tr>" +
                    "<!-- Footer -->" +
                    "<tr>" +
                    "<td class=\"footer\" style=\"background-color: #21618c; padding: 10px; text-align: left; color: white; font-size: 14px;\">"
                    +
                    "<p><span style=\"font-weight: bold;\">Important note:</span> If you did not purchase insurance with Allianz General Laos, please ignore this message as it's possible that someone else entered your email address by mistake. Any information you provided will be used and shared as described in <a href=\"https://www.azlaos.com/en_LA/privacy-notice.html\">Allianz Privacy Policy</a>.</p>"
                    +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</table>" +
                    "</body>";

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailTestSendRequetDto.getTo());
            message.setCc("phanakhone@agl.com.la", "julee@agl.com.la");
            message.setSubject("Motor Compulsory Centralized System: Account Creation-Action required");
            message.setText(body);
            message.setFrom("no-reply@agl.com.la");

            mailSender.send(message);
            return "Email sent successfully to " + emailTestSendRequetDto.getTo();
        } catch (Exception e) {
            return "Error sending email: " + e.getMessage();
        }
    }

}

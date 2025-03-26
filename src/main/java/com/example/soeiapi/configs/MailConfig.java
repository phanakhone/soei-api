package com.example.soeiapi.configs;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

// @Configuration
// public class MailConfig {
// @Value("${spring.mail.host}")
// private String mailHost;

// @Value("${spring.mail.port}")
// private int mailPort;

// // @Value("${spring.mail.username}")
// // private String mailUsername;

// // @Value("${spring.mail.password}")
// // private String mailPassword;

// @Value("${mail.proxy.host:}")
// private String proxyHost;

// @Value("${mail.proxy.port:0}")
// private int proxyPort;

// @Value("${mail.proxy.type:HTTP}")
// private String proxyType;

// @Bean
// public JavaMailSender getJavaMailSender() {
// JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
// mailSender.setHost(mailHost);
// mailSender.setPort(mailPort);
// // mailSender.setUsername(mailUsername);
// // mailSender.setPassword(mailPassword);

// Properties props = mailSender.getJavaMailProperties();
// props.put("mail.smtp.auth", "false");
// props.put("mail.smtp.starttls.enable", "false");

// if (!proxyHost.isEmpty() && proxyPort > 0) {
// if ("SOCKS".equalsIgnoreCase(proxyType)) {
// props.put("mail.smtp.socks.host", proxyHost);
// props.put("mail.smtp.socks.port", String.valueOf(proxyPort));
// System.setProperty("socksProxyHost", proxyHost);
// System.setProperty("socksProxyPort", String.valueOf(proxyPort));
// } else { // Default to HTTP proxy
// props.put("mail.smtp.proxy.host", proxyHost);
// props.put("mail.smtp.proxy.port", String.valueOf(proxyPort));
// System.setProperty("http.proxyHost", proxyHost);
// System.setProperty("http.proxyPort", String.valueOf(proxyPort));
// }
// }

// mailSender.setJavaMailProperties(props);
// return mailSender;
// }
// }

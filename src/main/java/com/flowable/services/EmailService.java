package com.flowable.services;

import java.util.Properties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
   private JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

   public EmailService() {
      this.mailSender.setHost("smtp.gmail.com");
      this.mailSender.setPort(587);
      this.mailSender.setUsername("prudhvi.deep.mutyala@gmail.com");
      this.mailSender.setPassword("yaupnlgwhokykwwm");
      Properties props = this.mailSender.getJavaMailProperties();
      props.put("mail.transport.protocol", "smtp");
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.debug", "true");
   }

   public void sendEmail(String to, String body, String subject) {
      System.out.println("Mailsender instance " + this.mailSender);
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      mailMessage.setFrom("prudhvi.deep.mutyala@gmail.com");
      mailMessage.setTo(to);
      mailMessage.setText(body);
      mailMessage.setSubject(subject);
      this.mailSender.send(mailMessage);
   }
}
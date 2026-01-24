package com.batko.cinematicketbooking.infrastructure.email;

import com.batko.cinematicketbooking.infrastructure.data.exception.EmailSendingException;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class EmailServiceImpl implements EmailService {

  private static final String USERNAME = "cinemabookingoriginal@gmail.com";
  private static final String SUPPORT_EMAIL = "moki22810sobakacom@gmail.com";
  private static final String APP_PASSWORD = System.getenv("APP_PASSWORD");

  private final Session session;

  public EmailServiceImpl() {
    if (APP_PASSWORD == null || APP_PASSWORD.isBlank()) {
      throw new EmailSendingException(
          "Critical error: Environment variable 'APP_PASSWORD' not found. " +
              "Please add it to the Run Configuration settings"
      );
    }
    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    props.put("mail.smtp.ssl.protocols", "TLSv1.2");

    Authenticator auth = new Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(USERNAME, APP_PASSWORD);
      }
    };

    this.session = Session.getInstance(props, auth);
  }

  @Override
  public void sendEmail(String toEmail, String subject, String body) {
    try {
      MimeMessage msg = new MimeMessage(session);

      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
      msg.addHeader("format", "flowed");
      msg.addHeader("Content-Transfer-Encoding", "8bit");

      msg.setFrom(new InternetAddress(USERNAME, "Cinema App Notification"));
      msg.setReplyTo(InternetAddress.parse(SUPPORT_EMAIL, false));
      msg.setSubject(subject, "UTF-8");
      msg.setContent(body, "text/html; charset=UTF-8");
      msg.setSentDate(new Date());
      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));

      System.out.println("Sending email to: " + toEmail + "...");
      Transport.send(msg);
      System.out.println("Email Sent Successfully!!");

    } catch (Exception e) {
      e.printStackTrace();
      throw new EmailSendingException("Failed to send email to " + toEmail, e);
    }
  }
}
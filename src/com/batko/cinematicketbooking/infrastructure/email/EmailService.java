package com.batko.cinematicketbooking.infrastructure.email;

@FunctionalInterface
public interface EmailService {

  void sendEmail(String toEmail, String subject, String body);
}
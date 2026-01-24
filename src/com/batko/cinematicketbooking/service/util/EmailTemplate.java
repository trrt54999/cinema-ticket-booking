package com.batko.cinematicketbooking.service.util;

public final class EmailTemplate {

  private EmailTemplate() {
  }

  public static String generateDefaultTemplate(String code) {

    String imageUrl = "https://cdn.aptoide.com/imgs/b/f/1/bf129f96c7b7df3fad7230a71e2c2166_icon.png";

    String htmlBody = """
        <div style="font-family: Arial, sans-serif; text-align: center; border: 1px solid #ccc; padding: 20px; border-radius: 10px;">
            <h1 style="color: #d32f2f;"> &#127916; Cinema Ticket Booking &#127871;</h1>
        
            <p><b>Welcome! Thank you for registering!</b></p>
        
            <img src="%s" alt="Cinema Logo" width="150" height="150" style="display: block; margin: 0 auto;">
        
            <p>Your confirmation code:</p>
            <h2 style="background-color: #eee; padding: 10px; display: inline-block; letter-spacing: 5px;">%s</h2>
        
            <p style="color: gray; font-size: 12px;">If you have not registered, please ignore this email.</p>
        </div>
        """.formatted(imageUrl, code);

    return htmlBody;
  }
}
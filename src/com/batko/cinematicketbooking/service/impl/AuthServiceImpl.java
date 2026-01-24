package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.enums.UserRole;
import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.UserRepository;
import com.batko.cinematicketbooking.infrastructure.email.EmailServiceImpl;
import com.batko.cinematicketbooking.service.contract.AuthService;
import com.batko.cinematicketbooking.service.dto.user.UserStoreDto;
import com.batko.cinematicketbooking.service.util.CodeGenerator;
import com.batko.cinematicketbooking.service.util.EmailTemplate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// todo до кінця розібратися з цим вкладеним класом
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepo;
  private final UnitOfWork<User> userUoW;
  private final EmailServiceImpl emailService;

  private final Map<String, PendingRegistration> pendingUsers = new ConcurrentHashMap<>();

  public AuthServiceImpl(UserRepository userRepo, UnitOfWork<User> userUoW,
      EmailServiceImpl emailService) {
    this.userRepo = userRepo;
    this.userUoW = userUoW;
    this.emailService = emailService;
  }

  @Override
  public void initiateRegistration(UserStoreDto dto) {
    if (userRepo.findByEmail(dto.email()).isPresent()) {
      throw new IllegalArgumentException("User with this email already exists.");
    }

    String verificationCode = CodeGenerator.generateVerificationCode();

    pendingUsers.put(dto.email(), new PendingRegistration(dto, verificationCode));

    String htmlBody = EmailTemplate.generateDefaultTemplate(verificationCode);
    emailService.sendEmail(dto.email(), "Welcome to cinema app!", htmlBody);
  }

  @Override
  public void confirmRegistration(String email, String code) {
    PendingRegistration pending = pendingUsers.get(email);

    if (pending == null) {
      throw new IllegalArgumentException("Registration session not found or expired.");
    }

    if (pending.getVerificationCode().equals(code)) {
      UserStoreDto dto = pending.getDto();
      User user = new User(
          dto.firstName(),
          dto.lastName(),
          dto.email(),
          dto.password(), // TODO: Додати хешування тут
          dto.age(),
          UserRole.USER
      );

      userUoW.registerNew(user);
      userUoW.commit();

      pendingUsers.remove(email);

    } else {
      pending.incrementAttempts();

      if (pending.getAttempts() >= 3) {
        pendingUsers.remove(email);
        throw new IllegalArgumentException("Too many invalid attempts. Registration cancelled.");
      }

      throw new IllegalArgumentException(
          "Invalid code. Try again. Attempts left: " + (3 - pending.getAttempts()));
    }
  }

  @Override
  public User login(String email, String password) {
    User user = userRepo.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!user.getPasswordHash().equals(password)) {
      throw new IllegalArgumentException("Invalid password");
    }
    return user;
  }

  private static class PendingRegistration {

    private final UserStoreDto dto;
    private final String verificationCode;
    private int attempts;

    public PendingRegistration(UserStoreDto dto, String verificationCode) {
      this.dto = dto;
      this.verificationCode = verificationCode;
      this.attempts = 0;
    }

    public void incrementAttempts() {
      this.attempts++;
    }

    public int getAttempts() {
      return attempts;
    }

    public UserStoreDto getDto() {
      return dto;
    }

    public String getVerificationCode() {
      return verificationCode;
    }
  }
}
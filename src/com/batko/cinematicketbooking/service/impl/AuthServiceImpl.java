package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.enums.UserRole;
import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.UserRepository;
import com.batko.cinematicketbooking.infrastructure.email.EmailServiceImpl;
import com.batko.cinematicketbooking.service.contract.AuthService;
import com.batko.cinematicketbooking.service.dto.UserLoginDto;
import com.batko.cinematicketbooking.service.dto.UserVerificationDto;
import com.batko.cinematicketbooking.service.dto.user.UserStoreDto;
import com.batko.cinematicketbooking.service.util.CodeGenerator;
import com.batko.cinematicketbooking.service.util.EmailTemplate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
  public void confirmRegistration(UserVerificationDto dto) {
    PendingRegistration pending = pendingUsers.get(dto.email());

    if (pending == null) {
      throw new IllegalArgumentException("Registration session not found or expired.");
    }

    if (pending.getVerificationCode().equals(dto.verificationCode())) {
      UserStoreDto storeDto = pending.getDto();
      User user = new User(
          storeDto.firstName(),
          storeDto.lastName(),
          storeDto.email(),
          storeDto.password(), // TODO: Додати хешування тут
          storeDto.age(),
          UserRole.USER
      );

      userUoW.registerNew(user);
      userUoW.commit();

      pendingUsers.remove(dto.email());

    } else {
      pending.incrementAttempts();

      if (pending.getAttempts() >= 3) {
        pendingUsers.remove(dto.email());
        throw new IllegalArgumentException("Too many invalid attempts. Registration cancelled.");
      }

      throw new IllegalArgumentException(
          "Invalid code. Try again. Attempts left: " + (3 - pending.getAttempts()));
    }
  }

  @Override
  public User login(UserLoginDto dto) {
    User user = userRepo.findByEmail(dto.email())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!user.getPasswordHash().equals(dto.password())) {
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
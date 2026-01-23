package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.enums.UserRole;
import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.UserRepository;
import com.batko.cinematicketbooking.service.contract.AuthService;
import com.batko.cinematicketbooking.service.dto.user.UserStoreDto;

public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepo;
  private final UnitOfWork<User> userUoW;

  public AuthServiceImpl(UserRepository userRepo, UnitOfWork<User> userUoW) {
    this.userRepo = userRepo;
    this.userUoW = userUoW;
  }

  @Override
  public void register(UserStoreDto dto) {
    if (userRepo.findByEmail(dto.email()).isPresent()) {
      throw new IllegalArgumentException("User with this email already exists.");
    }
    User user = new User(dto.firstName(),
        dto.lastName(),
        dto.email(),
        dto.password(), // TODO не забути про хеш!
        dto.age(),
        UserRole.USER);

    userUoW.registerNew(user);
    userUoW.commit();
  }

  @Override
  public User login(String email, String password) {
    User user = userRepo.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // todo буде інша потім, примітивна поки нема хешу
    if (!user.getPasswordHash().equals(password)) {
      throw new IllegalArgumentException("Invalid password");
    }
    return user;
  }
}
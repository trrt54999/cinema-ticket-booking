package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.UserRepository;
import com.batko.cinematicketbooking.service.contract.UserService;
import com.batko.cinematicketbooking.service.dto.user.UserUpdateDto;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

public class UserServiceImpl implements UserService {

  private final UserRepository userRepo;
  private final UnitOfWork<User> userUoW;

  public UserServiceImpl(UserRepository userRepo, UnitOfWork<User> userUoW) {
    this.userRepo = userRepo;
    this.userUoW = userUoW;
  }

  @Override
  public User update(UUID id, UserUpdateDto dto) {
    User user = userRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    boolean isDirty = false;

    if (dto.firstName() != null && !dto.firstName().equals(user.getFirstName())) {
      user.setFirstName(dto.firstName());
      isDirty = true;
    }
    if (dto.lastName() != null && !dto.lastName().equals(user.getLastName())) {
      user.setLastName(dto.lastName());
      isDirty = true;
    }
    if (dto.age() != null && !dto.age().equals(user.getAge())) {
      user.setAge(dto.age());
      isDirty = true;
    }
    if (dto.password() != null && !dto.password().isBlank()) {
      String hashedPassword = BCrypt.hashpw(dto.password(), BCrypt.gensalt());

      user.setPasswordHash(hashedPassword);
      isDirty = true;
    }
    if (isDirty) {
      userUoW.registerDirty(user);
      userUoW.commit();
    }

    return user;
  }
}
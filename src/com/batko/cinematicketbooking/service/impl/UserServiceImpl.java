package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.UserRepository;
import com.batko.cinematicketbooking.service.contract.UserService;
import com.batko.cinematicketbooking.service.dto.user.UserUpdateDto;
import java.util.UUID;

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

    if (dto.firstName() != null) {
      user.setFirstName(dto.firstName());
      isDirty = true;
    }
    if (dto.lastName() != null) {
      user.setLastName(dto.lastName());
      isDirty = true;
    }
    if (dto.age() > 0) {
      user.setAge(dto.age());
      isDirty = true;
    }
    // todo maybe зміна паролю..
    if (isDirty) {
      userUoW.registerDirty(user);
      userUoW.commit();
    }

    return user;
  }
}
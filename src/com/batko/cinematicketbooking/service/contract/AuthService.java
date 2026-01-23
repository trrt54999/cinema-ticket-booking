package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.service.dto.user.UserStoreDto;

public interface AuthService {

  void register(UserStoreDto dto);

  User login(String email, String password);
}
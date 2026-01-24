package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.service.dto.user.UserStoreDto;

public interface AuthService {

  void initiateRegistration(UserStoreDto dto);

  void confirmRegistration(String email, String code);

  User login(String email, String password);
}

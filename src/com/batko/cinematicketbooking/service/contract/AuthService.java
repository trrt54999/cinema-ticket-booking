package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.service.dto.user.UserLoginDto;
import com.batko.cinematicketbooking.service.dto.user.UserStoreDto;
import com.batko.cinematicketbooking.service.dto.user.UserVerificationDto;

public interface AuthService {

  void initiateRegistration(UserStoreDto dto);

  void confirmRegistration(UserVerificationDto dto);

  User login(UserLoginDto dto);
}

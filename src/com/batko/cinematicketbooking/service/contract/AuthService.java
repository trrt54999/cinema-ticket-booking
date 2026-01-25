package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.service.dto.UserLoginDto;
import com.batko.cinematicketbooking.service.dto.UserVerificationDto;
import com.batko.cinematicketbooking.service.dto.user.UserStoreDto;

public interface AuthService {

  void initiateRegistration(UserStoreDto dto);

  void confirmRegistration(UserVerificationDto dto);

  User login(UserLoginDto dto);
}

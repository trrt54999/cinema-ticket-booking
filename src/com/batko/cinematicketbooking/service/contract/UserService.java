package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.service.dto.user.UserUpdateDto;
import java.util.UUID;

public interface UserService {

  User update(UUID id, UserUpdateDto dto);
}

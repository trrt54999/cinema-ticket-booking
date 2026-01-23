package com.batko.cinematicketbooking;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.impl.json.DataContext;
import com.batko.cinematicketbooking.infrastructure.data.repository.UserRepository;
import com.batko.cinematicketbooking.service.contract.AuthService;
import com.batko.cinematicketbooking.service.dto.user.UserStoreDto;
import com.batko.cinematicketbooking.service.impl.AuthServiceImpl;

public class Main {

  static void main(String[] args) {
    System.out.println("Hello world");

    DataContext dataContext = DataContext.getInstance();
    UserRepository userRepository = dataContext.getUserRepository();
    UnitOfWork<User> userUoW = new UnitOfWork<>(userRepository);

    AuthService authService = new AuthServiceImpl(userRepository, userUoW);

    UserStoreDto userStoreDto = new UserStoreDto("faeewqa", "deqwsa",
        "ewqqwwe2@gmail.com", "12qwerqs", 12);

    authService.register(userStoreDto);
  }
}
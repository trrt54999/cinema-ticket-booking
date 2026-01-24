package com.batko.cinematicketbooking;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.impl.json.DataContext;
import com.batko.cinematicketbooking.infrastructure.data.repository.UserRepository;
import com.batko.cinematicketbooking.infrastructure.email.EmailServiceImpl;
import com.batko.cinematicketbooking.service.contract.AuthService;
import com.batko.cinematicketbooking.service.dto.user.UserStoreDto;
import com.batko.cinematicketbooking.service.impl.AuthServiceImpl;
import java.util.Scanner;

public class Main {

  static void main(String[] args) {
    System.out.println("Hello world");

    DataContext dataContext = DataContext.getInstance();
    UserRepository userRepository = dataContext.getUserRepository();
    UnitOfWork<User> userUoW = new UnitOfWork<>(userRepository);
    EmailServiceImpl emailService = new EmailServiceImpl();

    AuthService authService = new AuthServiceImpl(userRepository, userUoW, emailService);

    String myEmail = "danelol2008@gmail.com";
    UserStoreDto userStoreDto = new UserStoreDto("faeewqa", "deqwsa",
        myEmail, "12qwerqs", 12);

    authService.initiateRegistration(userStoreDto);

    Scanner scanner = new Scanner(System.in);

    while (true) {
      System.out.print("Please, enter code from email: ");
      String codeFromUser = scanner.nextLine().trim();

      try {
        authService.confirmRegistration(myEmail, codeFromUser);

        System.out.println("Success! Account created!");
        break;

      } catch (IllegalArgumentException e) {
        System.out.println("Error: " + e.getMessage());

        if (e.getMessage().contains("cancelled") || e.getMessage().contains("expired")) {
          System.out.println("Registration failed!");
          break;
        }
      }
    }
  }
}
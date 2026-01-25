package com.batko.cinematicketbooking.ui.menu;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.impl.json.DataContext;
import com.batko.cinematicketbooking.infrastructure.data.repository.UserRepository;
import com.batko.cinematicketbooking.infrastructure.email.EmailServiceImpl;
import com.batko.cinematicketbooking.service.contract.AuthService;
import com.batko.cinematicketbooking.service.dto.UserLoginDto;
import com.batko.cinematicketbooking.service.dto.UserVerificationDto;
import com.batko.cinematicketbooking.service.dto.user.UserStoreDto;
import com.batko.cinematicketbooking.service.impl.AuthServiceImpl;
import java.util.Scanner;

public class AuthMenu {

  private final Scanner s = new Scanner(System.in);
  private final EmailServiceImpl emailService = new EmailServiceImpl();
  private final DataContext dataContext = DataContext.getInstance();
  private final UserRepository userRepository = dataContext.getUserRepository();
  private final UnitOfWork<User> userUoW = new UnitOfWork<>(userRepository);
  private final AuthService authService = new AuthServiceImpl(userRepository, userUoW,
      emailService);

  public void run() {
    System.out.println("Hello! Welcome to cinema ticket booking!");
    boolean running = true;
    while (running) {
      System.out.print("""
          1. Registration
          2. Login
          3. Exit
          """);
      System.out.print("Choice: ");

      String input = s.nextLine().trim();

      switch (input) {
        case "1" -> registration();
        case "2" -> login();
        case "3" -> running = false;
        default -> System.out.println("Invalid choice! Try again.");
      }
    }
  }

  private void registration() {
    System.out.print("Please, enter your first name: ");
    String firstName = s.nextLine().trim();

    System.out.print("Please, enter your last name: ");
    String lastName = s.nextLine().trim();

    System.out.print("Please, enter your email: ");
    String email = s.nextLine().trim();

    System.out.print("Please, enter your password: ");
    String password = s.nextLine().trim();

    int age;

    while (true) {
      System.out.print("Please, enter your age: ");
      String input = s.nextLine().trim();

      try {
        age = Integer.parseInt(input);

        break;
      } catch (NumberFormatException _) {
        System.out.println("Invalid input! Please enter a number.");
      }
    }

    try {
      UserStoreDto userStoreDto = new UserStoreDto(firstName, lastName,
          email, password, age);

      authService.initiateRegistration(userStoreDto);
    } catch (IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
      return;
    }

    while (true) {
      System.out.print("Please, enter code from email: ");
      String codeFromUser = s.nextLine().trim();

      try {
        UserVerificationDto dto = new UserVerificationDto(email, codeFromUser);

        authService.confirmRegistration(dto);

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

  private void login() {
    System.out.print("Please, enter your email: ");
    String email = s.nextLine().trim();

    System.out.print("Please, enter your password: ");
    String password = s.nextLine().trim();

    try {
      UserLoginDto dto = new UserLoginDto(email, password);

      authService.login(dto);

      System.out.println("Success login!");
    } catch (IllegalArgumentException e) {
      System.out.println("Error " + e.getMessage());
    }
  }
}
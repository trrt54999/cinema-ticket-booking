package com.batko.cinematicketbooking.presentation;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.service.contract.AuthService;
import com.batko.cinematicketbooking.service.contract.GenreService;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.contract.SeatGeneratorService;
import com.batko.cinematicketbooking.service.contract.SeatService;
import com.batko.cinematicketbooking.service.contract.SessionService;
import com.batko.cinematicketbooking.service.contract.TicketService;
import com.batko.cinematicketbooking.service.dto.UserLoginDto;
import com.batko.cinematicketbooking.service.dto.UserVerificationDto;
import com.batko.cinematicketbooking.service.dto.user.UserStoreDto;
import java.util.Scanner;

public class AuthMenu {

  private final Scanner s = new Scanner(System.in);
  private final AuthService authService;
  private final GenreService genreService;
  private final MovieService movieService;
  private final SessionService sessionService;
  private final HallService hallService;
  private final TicketService ticketService;
  private final SeatService seatService;
  private final SeatGeneratorService seatGeneratorService;

  public AuthMenu(AuthService authService, GenreService genreService, MovieService movieService,
      SessionService sessionService, HallService hallService, TicketService ticketService,
      SeatService seatService, SeatGeneratorService seatGeneratorService) {
    this.authService = authService;
    this.genreService = genreService;
    this.movieService = movieService;
    this.sessionService = sessionService;
    this.hallService = hallService;
    this.ticketService = ticketService;
    this.seatService = seatService;
    this.seatGeneratorService = seatGeneratorService;
  }

  public void run() {
    System.out.println("Hello! Welcome to cinema ticket booking!");
    boolean running = true;
    while (running) {
      System.out.print("""
          1. Registration
          2. Login
          0. Exit
          """);
      System.out.print("Choice: ");

      String input = s.nextLine().trim();

      switch (input) {
        case "1" -> registration();
        case "2" -> login();
        case "0" -> running = false;
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
      System.out.print("Please, enter code from email (or '0' to cancel): ");
      String codeFromUser = s.nextLine().trim();

      if (codeFromUser.equals("0")) {
        System.out.println("Registration cancelled by user.");
        break;
      }

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

      User user = authService.login(dto);

      System.out.println("Success login! Welcome, " + user.getFirstName() + "\uD83C\uDF0D");

      navigateByRole(user);
    } catch (IllegalArgumentException e) {
      System.out.println("Error " + e.getMessage());
    }
  }

  private void navigateByRole(User user) {
    switch (user.getRole()) {
      case MANAGER -> new ManagerMenu(
          user,
          s,
          genreService,
          movieService,
          sessionService,
          hallService,
          seatService,
          seatGeneratorService
      ).managerMenu();
      case USER -> new UserMenu(user, s, movieService, ticketService, sessionService,
          hallService, seatService).userMenu();
      default -> System.out.println("Unknown role.");
    }
  }
}
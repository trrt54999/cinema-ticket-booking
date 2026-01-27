package com.batko.cinematicketbooking.presentation;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.presentation.util.ConsoleUiUtils;
import com.batko.cinematicketbooking.presentation.util.EmojiConstants;
import com.batko.cinematicketbooking.presentation.util.JLineMenuRenderer;
import com.batko.cinematicketbooking.service.contract.AuthService;
import com.batko.cinematicketbooking.service.contract.GenreService;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.contract.SeatGeneratorService;
import com.batko.cinematicketbooking.service.contract.SeatService;
import com.batko.cinematicketbooking.service.contract.SessionService;
import com.batko.cinematicketbooking.service.contract.TicketService;
import com.batko.cinematicketbooking.service.contract.UserService;
import com.batko.cinematicketbooking.service.dto.user.UserLoginDto;
import com.batko.cinematicketbooking.service.dto.user.UserStoreDto;
import com.batko.cinematicketbooking.service.dto.user.UserVerificationDto;
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
  private final UserService userService;

  public AuthMenu(AuthService authService, GenreService genreService, MovieService movieService,
      SessionService sessionService, HallService hallService, TicketService ticketService,
      SeatService seatService, SeatGeneratorService seatGeneratorService, UserService userService) {
    this.authService = authService;
    this.genreService = genreService;
    this.movieService = movieService;
    this.sessionService = sessionService;
    this.hallService = hallService;
    this.ticketService = ticketService;
    this.seatService = seatService;
    this.seatGeneratorService = seatGeneratorService;
    this.userService = userService;
  }

  public void run() {
    System.out.println(EmojiConstants.MOVIES + " Hello! Welcome to cinema ticket booking!");
    boolean running = true;
    while (running) {
      JLineMenuRenderer.renderMenu(EmojiConstants.AUTHENTICATION_TITLE + " AUTHENTICATION",
          "1. " + EmojiConstants.REGISTRATION + " Registration",
          "2. " + EmojiConstants.LOGIN + " Login",
          "0. " + EmojiConstants.EXIT + " Exit");

      String input = s.nextLine().trim();

      switch (input) {
        case "1" -> registration();
        case "2" -> login();
        case "0" -> running = false;
        default -> System.out.println(EmojiConstants.FORBIDDEN + " Invalid choice! Try again.");
      }
    }
  }

  private void registration() {
    JLineMenuRenderer.printHeader(EmojiConstants.REGISTRATION + " NEW USER REGISTRATION");

    System.out.print(EmojiConstants.POINT_RIGHT + " Please, enter your first name: ");
    String firstName = s.nextLine().trim();

    System.out.print(EmojiConstants.POINT_RIGHT + " Please, enter your last name: ");
    String lastName = s.nextLine().trim();

    System.out.print(EmojiConstants.EMAIL + " Please, enter your email: ");
    String email = s.nextLine().trim();

    System.out.print(EmojiConstants.PASSWORD + " Please, enter your password: ");
    String password = ConsoleUiUtils.readPassword(s);

    int age;

    while (true) {
      System.out.print(EmojiConstants.AGE + " Please, enter your age: ");
      String input = s.nextLine().trim();

      try {
        age = Integer.parseInt(input);
        break;
      } catch (NumberFormatException _) {
        System.out.println(EmojiConstants.ERROR + " Invalid input! Please enter a number.");
      }
    }

    try {
      UserStoreDto userStoreDto = new UserStoreDto(firstName, lastName,
          email, password, age);

      authService.initiateRegistration(userStoreDto);
    } catch (IllegalArgumentException e) {
      System.out.println(EmojiConstants.ERROR + " Error: " + e.getMessage());
      return;
    }

    while (true) {
      System.out.print(
          EmojiConstants.INBOX + " Please, enter code from email (or '0' to cancel): ");
      String codeFromUser = s.nextLine().trim();

      if (codeFromUser.equals("0")) {
        System.out.println(EmojiConstants.WARNING + " Registration cancelled by user.");
        break;
      }

      try {
        UserVerificationDto dto = new UserVerificationDto(email, codeFromUser);
        authService.confirmRegistration(dto);

        System.out.println(EmojiConstants.SUCCESS + " Success! Account created!");
        break;

      } catch (IllegalArgumentException e) {
        System.out.println(EmojiConstants.ERROR + " Error: " + e.getMessage());

        if (e.getMessage().contains("cancelled") || e.getMessage().contains("expired")) {
          System.out.println(EmojiConstants.ERROR + " Registration failed!");
          break;
        }
      }
    }
  }

  private void login() {
    JLineMenuRenderer.printHeader(EmojiConstants.LOGIN + " LOGIN");

    System.out.print(EmojiConstants.EMAIL + " Please, enter your email: ");
    String email = s.nextLine().trim();

    System.out.print(EmojiConstants.PASSWORD + " Please, enter your password: ");
    String password = ConsoleUiUtils.readPassword(s);

    try {
      UserLoginDto dto = new UserLoginDto(email, password);

      User user = authService.login(dto);

      String coloredName = ConsoleUiUtils.toCyan(user.getFirstName());
      System.out.println(EmojiConstants.SUCCESS + " Success login! Welcome, " + coloredName + " "
          + EmojiConstants.WORLD);

      navigateByRole(user);
    } catch (IllegalArgumentException e) {
      System.out.println(EmojiConstants.ERROR + " Error " + e.getMessage());
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
          seatGeneratorService,
          userService).managerMenu();
      case USER -> new UserMenu(user, s, movieService, ticketService, sessionService,
          hallService, seatService, userService).userMenu();
      default -> System.out.println(EmojiConstants.QUESTION + " Unknown role.");
    }
  }
}
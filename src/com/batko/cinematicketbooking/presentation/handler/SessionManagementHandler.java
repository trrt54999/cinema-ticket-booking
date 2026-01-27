package com.batko.cinematicketbooking.presentation.handler;

import com.batko.cinematicketbooking.domain.model.Hall;
import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.domain.model.Session;
import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.presentation.util.ConsoleUiUtils;
import com.batko.cinematicketbooking.presentation.util.EmojiConstants;
import com.batko.cinematicketbooking.presentation.util.JLineMenuRenderer;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.contract.SessionService;
import com.batko.cinematicketbooking.service.dto.session.SessionStoreDto;
import com.batko.cinematicketbooking.service.dto.session.SessionUpdateDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class SessionManagementHandler {

  private static final String INVALID_NUMBER = EmojiConstants.ERROR + " Invalid number.";
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm");

  private final User user;
  private final Scanner scanner;
  private final SessionService sessionService;
  private final MovieService movieService;
  private final HallService hallService;

  public SessionManagementHandler(User user, Scanner scanner, SessionService sessionService,
      MovieService movieService, HallService hallService) {
    this.user = user;
    this.scanner = scanner;
    this.sessionService = sessionService;
    this.movieService = movieService;
    this.hallService = hallService;
  }

  public void handle() {
    boolean back = false;
    while (!back) {
      JLineMenuRenderer.renderMenu(EmojiConstants.MANAGE + " MANAGE SESSIONS",
          "1. " + EmojiConstants.MOVIES + " List sessions (by movie)",
          "2. " + EmojiConstants.ADD + " Add new session",
          "3. \uD83D\uDCDD Edit session",
          "4. \uD83D\uDDD1\uFE0F Delete session",
          "0. " + EmojiConstants.LOGOUT + " Back to Main Menu"
      );

      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1" -> listSessionsByMovie();
        case "2" -> createSession();
        case "3" -> editSession();
        case "4" -> deleteSession();
        case "0" -> back = true;
        default -> System.out.println(EmojiConstants.ERROR + " Invalid choice!");
      }
    }
  }

  private void listSessionsByMovie() {
    JLineMenuRenderer.printHeader(EmojiConstants.MOVIES + " SELECT MOVIE TO VIEW SESSIONS");
    UUID movieId = selectMovieId();
    if (movieId == null) {
      return;
    }

    List<Session> sessions = sessionService.getByMovie(movieId);
    printSessionsTable(sessions);

    System.out.println("\nPress Enter to continue...");
    scanner.nextLine();
  }

  private void createSession() {
    JLineMenuRenderer.printHeader(
        EmojiConstants.ADD + " " + EmojiConstants.TICKET + " ADD NEW SESSION");

    UUID movieId = selectMovieId();
    if (movieId == null) {
      return;
    }

    UUID hallId = selectHallId();
    if (hallId == null) {
      return;
    }

    int price = readPrice();
    LocalDateTime startTime = readDateTime();

    try {
      SessionStoreDto dto = new SessionStoreDto(hallId, movieId, user.getId(), price, startTime);
      sessionService.create(dto);
      System.out.println(EmojiConstants.SUCCESS + " Success! Session created.");
    } catch (IllegalArgumentException e) {
      System.out.println(EmojiConstants.ERROR + " Error creating session: " + e.getMessage());
    }
  }

  private void editSession() {
    Session session = selectSession("SELECT SESSION TO EDIT");
    if (session == null) {
      return;
    }

    System.out.println(EmojiConstants.MANAGE + " Editing session for: " + session.getStartTime()
        .format(FORMATTER));
    System.out.println("Leave field empty to keep current value.");

    System.out.print(
        EmojiConstants.POINT_RIGHT + " Enter new price (current: " + session.getPrice() + "$): ");
    String priceInput = scanner.nextLine().trim();
    Integer newPrice = priceInput.isEmpty() ? session.getPrice() : Integer.parseInt(priceInput);

    System.out.print(
        EmojiConstants.POINT_RIGHT + " Enter new start time (current: " + session.getStartTime()
            .format(FORMATTER) + "): ");
    String timeInput = scanner.nextLine().trim();
    LocalDateTime newTime = timeInput.isEmpty() ? session.getStartTime() : parseDateTime(timeInput);

    System.out.println(EmojiConstants.POINT_RIGHT + " Current Hall ID: " + session.getHallId());
    System.out.print(EmojiConstants.POINT_RIGHT + " Change hall? (y/n): ");
    String changeHall = scanner.nextLine().trim();
    UUID newHallId = session.getHallId();
    if (changeHall.equalsIgnoreCase("y")) {
      UUID h = selectHallId();
      if (h != null) {
        newHallId = h;
      }
    }

    try {
      if (newTime != null) {
        SessionUpdateDto dto = new SessionUpdateDto(newHallId, session.getMovieId(), newPrice,
            newTime);
        sessionService.update(session.getId(), dto);
        System.out.println(EmojiConstants.SUCCESS + " Session updated successfully.");
      }
    } catch (IllegalArgumentException e) {
      System.out.println(EmojiConstants.ERROR + " Error updating session: " + e.getMessage());
    }
  }

  private void deleteSession() {
    Session session = selectSession("SELECT SESSION TO DELETE");
    if (session == null) {
      return;
    }

    System.out.print(EmojiConstants.WARNING + " Are you sure you want to delete session at "
        + session.getStartTime().format(FORMATTER) + "? (y/n): ");

    if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
      try {
        sessionService.delete(session.getId());
        System.out.println(EmojiConstants.SUCCESS + " Session deleted.");
      } catch (Exception e) {
        System.out.println(EmojiConstants.ERROR + " Error deleting session: " + e.getMessage());
      }
    } else {
      System.out.println("Deletion cancelled.");
    }
  }

  private Session selectSession(String title) {
    JLineMenuRenderer.printHeader(title);
    System.out.println("First, select a movie:");
    UUID movieId = selectMovieId();
    if (movieId == null) {
      return null;
    }

    List<Session> sessions = sessionService.getByMovie(movieId);
    if (sessions.isEmpty()) {
      System.out.println(EmojiConstants.WARNING + " No sessions found for this movie.");
      return null;
    }

    printSessionsTable(sessions);

    System.out.print(EmojiConstants.POINT_RIGHT + " Enter session # to select (0 to cancel): ");
    String input = scanner.nextLine().trim();
    if (input.equals("0")) {
      return null;
    }

    int index = parseIndex(input, sessions.size());
    if (index != -1) {
      return sessions.get(index);
    }
    System.out.println(INVALID_NUMBER);
    return null;
  }

  private void printSessionsTable(List<Session> sessions) {
    System.out.println("──────────────────────────────────────────────────────────");
    System.out.printf("%-4s | %-16s | %-8s | %s%n", "#", "Time", "Price", "Hall");
    System.out.println("──────────────────────────────────────────────────────────");
    for (int i = 0; i < sessions.size(); i++) {
      Session s = sessions.get(i);
      String hallName = ConsoleUiUtils.getHallNameSafe(hallService, s.getHallId());
      String priceStr = String.format("%-8s", s.getPrice() + "$");

      System.out.printf("%-4d | %-16s | %s | %s%n",
          (i + 1),
          s.getStartTime().format(FORMATTER),
          ConsoleUiUtils.toGreen(priceStr),
          hallName);
    }
    System.out.println("──────────────────────────────────────────────────────────");
  }

  private UUID selectMovieId() {
    List<Movie> movies = movieService.getAll();
    if (movies.isEmpty()) {
      System.out.println(EmojiConstants.WARNING + " Error: No movies found.");
      return null;
    }

    for (int i = 0; i < movies.size(); i++) {
      System.out.printf("%d. %s%n", (i + 1), movies.get(i).getTitle());
    }
    System.out.print(EmojiConstants.POINT_RIGHT + " Select movie # (0 to cancel): ");

    String input = scanner.nextLine().trim();
    if (input.equals("0")) {
      return null;
    }

    int index = parseIndex(input, movies.size());
    if (index != -1) {
      return movies.get(index).getId();
    }

    System.out.println(INVALID_NUMBER);
    return null;
  }

  private UUID selectHallId() {
    List<Hall> halls = hallService.getAll();
    if (halls.isEmpty()) {
      System.out.println(EmojiConstants.WARNING + " No halls found.");
      return null;
    }
    for (int i = 0; i < halls.size(); i++) {
      System.out.printf("%d. %s%n", (i + 1), halls.get(i).getName());
    }
    System.out.print(EmojiConstants.POINT_RIGHT + " Select hall # (0 to cancel): ");

    String input = scanner.nextLine().trim();
    if (input.equals("0")) {
      return null;
    }

    int index = parseIndex(input, halls.size());
    if (index != -1) {
      return halls.get(index).getId();
    }

    System.out.println(INVALID_NUMBER);
    return null;
  }

  private int readPrice() {
    while (true) {
      System.out.print(EmojiConstants.POINT_RIGHT + " Enter price ($): ");
      try {
        int p = Integer.parseInt(scanner.nextLine().trim());
        if (p > 0) {
          return p;
        }
        System.out.println(EmojiConstants.ERROR + " Price must be positive.");
      } catch (NumberFormatException _) {
        System.out.println(INVALID_NUMBER);
      }
    }
  }

  private LocalDateTime readDateTime() {
    while (true) {
      System.out.print(EmojiConstants.POINT_RIGHT + " Enter start time (yyyy-MM-dd HH:mm): ");
      String input = scanner.nextLine().trim();
      LocalDateTime dt = parseDateTime(input);
      if (dt != null) {
        if (dt.isBefore(LocalDateTime.now())) {
          System.out.println(EmojiConstants.WARNING + " Cannot be in the past.");
        } else {
          return dt;
        }
      }
    }
  }

  private LocalDateTime parseDateTime(String input) {
    try {
      return LocalDateTime.parse(input, FORMATTER);
    } catch (DateTimeParseException _) {
      System.out.println(EmojiConstants.ERROR + " Invalid format.");
      return null;
    }
  }

  private int parseIndex(String input, int size) {
    try {
      int index = Integer.parseInt(input) - 1;
      if (index >= 0 && index < size) {
        return index;
      }
    } catch (IllegalArgumentException _) {
    }
    return -1;
  }
}
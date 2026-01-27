package com.batko.cinematicketbooking.presentation.handler;

import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.domain.model.Session;
import com.batko.cinematicketbooking.presentation.util.ConsoleUiUtils;
import com.batko.cinematicketbooking.presentation.util.EmojiConstants;
import com.batko.cinematicketbooking.presentation.util.JLineMenuRenderer;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.contract.SessionService;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class MovieBrowserHandler {

  private final Scanner scanner;
  private final MovieService movieService;
  private final SessionService sessionService;
  private final HallService hallService;

  public MovieBrowserHandler(Scanner scanner, MovieService movieService,
      SessionService sessionService, HallService hallService) {
    this.scanner = scanner;
    this.movieService = movieService;
    this.sessionService = sessionService;
    this.hallService = hallService;
  }

  public void handle() {
    while (true) {
      List<Movie> movies = movieService.getAll();
      if (movies.isEmpty()) {
        System.out.println(EmojiConstants.WARNING + " Error: No movies found.");
        return;
      }

      String[] menuOptions = new String[movies.size() + 1];
      for (int i = 0; i < movies.size(); i++) {
        Movie m = movies.get(i);
        menuOptions[i] = String.format("%d. %s (%d min)", (i + 1), m.getTitle(),
            m.getDurationMinutes());
      }
      menuOptions[movies.size()] = "0. " + EmojiConstants.LOGOUT + " Back";

      JLineMenuRenderer.renderMenu(EmojiConstants.MOVIES + " AVAILABLE MOVIES", menuOptions);

      String input = scanner.nextLine().trim();

      if (input.equals("0")) {
        return;
      }

      try {
        int index = Integer.parseInt(input) - 1;
        if (index >= 0 && index < movies.size()) {
          Movie selectedMovie = movies.get(index);
          viewSessions(selectedMovie);
        } else {
          System.out.println(EmojiConstants.ERROR + " Invalid movie number.");
        }
      } catch (NumberFormatException _) {
        System.out.println(EmojiConstants.ERROR + " Please enter a number.");
      }
    }
  }

  private void viewSessions(Movie movie) {
    printMovieDetailsCard(movie);

    System.out.println("\n" + EmojiConstants.TICKET + " Sessions for '" + ConsoleUiUtils.toCyan(
        movie.getTitle()) + "'");

    List<Session> sessions = sessionService.getByMovie(movie.getId());

    if (sessions.isEmpty()) {
      System.out.println(EmojiConstants.WARNING + " No scheduled sessions for this movie yet.");
    } else {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

      System.out.println("────────────────────────────────────────────────────");
      System.out.printf("%-4s | %-16s | %-8s | %s%n", "#", "Time", "Price", "Hall");
      System.out.println("────────────────────────────────────────────────────");

      for (int i = 0; i < sessions.size(); i++) {
        Session session = sessions.get(i);
        String hallName = ConsoleUiUtils.getHallNameSafe(hallService, session.getHallId());
        String time = session.getStartTime().format(formatter);
        String priceStr = String.format("%-8s", session.getPrice() + "$");
        String coloredPrice = ConsoleUiUtils.toGreen(priceStr);

        System.out.printf("%-4d | %-16s | %s | %s%n",
            (i + 1),
            time,
            coloredPrice,
            hallName);
      }
      System.out.println("────────────────────────────────────────────────────");
    }

    System.out.println("\nPress Enter to continue...");
    scanner.nextLine();
  }

  private void printMovieDetailsCard(Movie movie) {
    int width = 60;
    String border = "─".repeat(width - 2);
    String cyanColor = "\u001B[36m";
    String yellowColor = "\u001B[33m";
    String resetColor = "\u001B[0m";

    System.out.println(cyanColor + "┌" + border + "┐" + resetColor);

    System.out.println(cyanColor + "│" + resetColor + " Title:    "
        + ConsoleUiUtils.toYellow(padRight(movie.getTitle().toUpperCase(), width - 14))
        + cyanColor + " │" + resetColor);

    System.out.println(cyanColor + "│" + resetColor + " Duration: "
        + padRight(movie.getDurationMinutes() + " min", width - 14)
        + cyanColor + " │" + resetColor);

    System.out.println(cyanColor + "├" + border + "┤" + resetColor);

    System.out.println(
        cyanColor + "│" + resetColor + padRight(" Description:", width - 3) + cyanColor + " │"
            + resetColor);

    List<String> descriptionLines = ConsoleUiUtils.wrapText(movie.getDescription(), width - 4);
    for (String line : descriptionLines) {
      System.out.println(
          cyanColor + "│ " + resetColor + padRight(line, width - 4) + cyanColor + " │"
              + resetColor);
    }

    if (descriptionLines.isEmpty()) {
      System.out.println(
          cyanColor + "│ " + resetColor + padRight("No description available.", width - 4)
              + cyanColor + " │" + resetColor);
    }

    System.out.println(cyanColor + "└" + border + "┘" + resetColor);
  }

  private String padRight(String text, int width) {
    if (text == null) {
      return " ".repeat(width);
    }
    if (text.length() > width) {
      return text.substring(0, width);
    }
    return text + " ".repeat(width - text.length());
  }
}
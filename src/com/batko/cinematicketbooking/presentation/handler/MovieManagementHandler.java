package com.batko.cinematicketbooking.presentation.handler;

import com.batko.cinematicketbooking.domain.model.Genre;
import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.presentation.util.ConsoleUiUtils;
import com.batko.cinematicketbooking.presentation.util.EmojiConstants;
import com.batko.cinematicketbooking.presentation.util.JLineMenuRenderer;
import com.batko.cinematicketbooking.service.contract.GenreService;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.dto.movie.MovieStoreDto;
import com.batko.cinematicketbooking.service.dto.movie.MovieUpdateDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class MovieManagementHandler {

  private final User user;
  private final Scanner scanner;
  private final MovieService movieService;
  private final GenreService genreService;

  public MovieManagementHandler(User user, Scanner scanner, MovieService movieService,
      GenreService genreService) {
    this.user = user;
    this.scanner = scanner;
    this.movieService = movieService;
    this.genreService = genreService;
  }

  public void handle() {
    boolean back = false;
    while (!back) {
      JLineMenuRenderer.renderMenu(EmojiConstants.MANAGE + " MANAGE MOVIES",
          "1. " + EmojiConstants.MOVIES + " List all movies",
          "2. " + EmojiConstants.ADD + " Add new movie",
          "3. \uD83D\uDCDD Edit movie",
          "4. \uD83D\uDDD1\uFE0F Delete movie",
          "0. " + EmojiConstants.LOGOUT + " Back to Main Menu"
      );

      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1" -> listMovies();
        case "2" -> createMovie();
        case "3" -> editMovie();
        case "4" -> deleteMovie();
        case "0" -> back = true;
        default -> System.out.println(EmojiConstants.ERROR + " Invalid choice!");
      }
    }
  }

  private void listMovies() {
    JLineMenuRenderer.printHeader(EmojiConstants.MOVIES + " ALL MOVIES");
    List<Movie> movies = movieService.getAll();
    if (movies.isEmpty()) {
      System.out.println(EmojiConstants.WARNING + " No movies found.");
    } else {
      movies.forEach(
          m -> System.out.printf(" • %s (%d min)%n", m.getTitle(), m.getDurationMinutes()));
    }
    System.out.println("\nPress Enter to continue...");
    scanner.nextLine();
  }

  private void createMovie() {
    JLineMenuRenderer.printHeader(EmojiConstants.ADD + " ADD NEW MOVIE");

    System.out.print(EmojiConstants.POINT_RIGHT + " Enter movie title: ");
    String title = scanner.nextLine().trim();

    System.out.print(EmojiConstants.POINT_RIGHT + " Enter movie description: ");
    String description = scanner.nextLine().trim();

    int duration = readDuration();

    List<Genre> allGenres = genreService.getAll();
    if (allGenres.isEmpty()) {
      System.out.println(EmojiConstants.WARNING
          + " Warning: No genres available in the system. Create a genre first.");
      return;
    }

    List<UUID> selectedGenreIds = selectGenres(allGenres);

    try {
      MovieStoreDto dto = new MovieStoreDto(
          title,
          description,
          duration,
          user.getId(),
          selectedGenreIds);

      movieService.create(dto);
      System.out.println(EmojiConstants.SUCCESS + " Success! Movie added.");
    } catch (IllegalArgumentException e) {
      System.out.println(EmojiConstants.ERROR + " Error adding movie: " + e.getMessage());
    }
  }

  private void editMovie() {
    Movie movie = selectMovie("SELECT MOVIE TO EDIT");
    if (movie == null) {
      return;
    }

    System.out.println(EmojiConstants.MANAGE + " Editing movie: " + movie.getTitle());
    System.out.println("Leave field empty to keep current value.");

    System.out.print(
        EmojiConstants.POINT_RIGHT + " Enter new title (current: " + movie.getTitle() + "): ");
    String titleInput = scanner.nextLine().trim();
    String newTitle = titleInput.isEmpty() ? movie.getTitle() : titleInput;

    System.out.print(
        EmojiConstants.POINT_RIGHT + " Enter new description (current: " + movie.getDescription()
            + "): ");
    String descInput = scanner.nextLine().trim();
    String newDescription = descInput.isEmpty() ? movie.getDescription() : descInput;

    System.out.print(
        EmojiConstants.POINT_RIGHT + " Enter new duration (current: " + movie.getDurationMinutes()
            + " min): ");
    String durInput = scanner.nextLine().trim();
    Integer newDuration =
        durInput.isEmpty() ? movie.getDurationMinutes() : Integer.parseInt(durInput);

    try {
      MovieUpdateDto dto = new MovieUpdateDto(newTitle, newDescription, newDuration);
      movieService.update(movie.getId(), dto);
      System.out.println(EmojiConstants.SUCCESS + " Movie updated successfully.");
    } catch (Exception e) {
      System.out.println(EmojiConstants.ERROR + " Error updating movie: " + e.getMessage());
    }
  }

  private void deleteMovie() {
    Movie movie = selectMovie("SELECT MOVIE TO DELETE");
    if (movie == null) {
      return;
    }

    System.out.print(
        EmojiConstants.WARNING + " Are you sure you want to delete '" + movie.getTitle()
            + "'? (y/n): ");
    if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
      try {
        movieService.delete(movie.getId());
        System.out.println(EmojiConstants.SUCCESS + " Movie deleted.");
      } catch (Exception e) {
        System.out.println(EmojiConstants.ERROR + " Error deleting movie: " + e.getMessage());
      }
    } else {
      System.out.println("Deletion cancelled.");
    }
  }

  private int readDuration() {
    while (true) {
      System.out.print(EmojiConstants.POINT_RIGHT + " Enter movie duration (in minutes): ");
      try {
        return Integer.parseInt(scanner.nextLine().trim());
      } catch (NumberFormatException _) {
        System.out.println(EmojiConstants.ERROR + " Invalid number.");
      }
    }
  }

  private Movie selectMovie(String title) {
    List<Movie> movies = movieService.getAll();
    if (movies.isEmpty()) {
      System.out.println(EmojiConstants.WARNING + " No movies found.");
      return null;
    }

    String[] options = new String[movies.size() + 1];
    for (int i = 0; i < movies.size(); i++) {
      options[i] = String.format("%d. %s", (i + 1), movies.get(i).getTitle());
    }
    options[movies.size()] = "0. Cancel";

    JLineMenuRenderer.renderMenu(title, options);

    String input = scanner.nextLine().trim();
    if (input.equals("0")) {
      return null;
    }

    try {
      int index = Integer.parseInt(input) - 1;
      if (index >= 0 && index < movies.size()) {
        return movies.get(index);
      }
    } catch (IllegalArgumentException _) {
    }

    System.out.println(EmojiConstants.ERROR + " Invalid selection.");
    return null;
  }

  private List<UUID> selectGenres(List<Genre> availableGenres) {
    List<UUID> selectedIds = new ArrayList<>();

    while (true) {
      String[] menuOptions = new String[availableGenres.size() + 1];

      for (int i = 0; i < availableGenres.size(); i++) {
        Genre g = availableGenres.get(i);
        String mark = selectedIds.contains(g.getId()) ? ConsoleUiUtils.toGreen("[x]") : "[ ]";
        menuOptions[i] = String.format("%d. %s %s", (i + 1), mark, g.getName());
      }
      menuOptions[availableGenres.size()] = "0. " + EmojiConstants.SUCCESS + " Finish selection";

      JLineMenuRenderer.renderMenu("SELECT GENRES (Toggle)", menuOptions);

      String input = scanner.nextLine().trim();

      if (input.equals("0")) {
        return selectedIds;
      }

      try {
        int index = Integer.parseInt(input) - 1;
        if (index >= 0 && index < availableGenres.size()) {
          UUID genreId = availableGenres.get(index).getId();

          if (selectedIds.contains(genreId)) {
            selectedIds.remove(genreId);
          } else {
            selectedIds.add(genreId);
          }
        } else {
          System.out.println(EmojiConstants.ERROR + " Invalid genre number.");
        }
      } catch (NumberFormatException _) {
        System.out.println(EmojiConstants.ERROR + " Please enter a number.");
      }
    }
  }
}
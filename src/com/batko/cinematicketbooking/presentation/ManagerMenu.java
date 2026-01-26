package com.batko.cinematicketbooking.presentation;

import com.batko.cinematicketbooking.domain.model.Genre;
import com.batko.cinematicketbooking.domain.model.Hall;
import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.service.contract.GenreService;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.contract.SeatGeneratorService;
import com.batko.cinematicketbooking.service.contract.SeatService;
import com.batko.cinematicketbooking.service.contract.SessionService;
import com.batko.cinematicketbooking.service.dto.genre.GenreStoreDto;
import com.batko.cinematicketbooking.service.dto.hall.HallStoreDto;
import com.batko.cinematicketbooking.service.dto.movie.MovieStoreDto;
import com.batko.cinematicketbooking.service.dto.session.SessionStoreDto;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ManagerMenu {

  private static final String INVALID_NUMBER = "Invalid number.";
  private final User user;
  private final Scanner scanner;
  private final GenreService genreService;
  private final MovieService movieService;
  private final SessionService sessionService;
  private final HallService hallService;
  private final SeatService seatService;
  private final SeatGeneratorService seatGeneratorService;

  public ManagerMenu(User user, Scanner scanner, GenreService genreService,
      MovieService movieService, SessionService sessionService, HallService hallService,
      SeatService seatService, SeatGeneratorService seatGeneratorService) {
    this.user = user;
    this.scanner = scanner;
    this.genreService = genreService;
    this.movieService = movieService;
    this.sessionService = sessionService;
    this.hallService = hallService;
    this.seatService = seatService;
    this.seatGeneratorService = seatGeneratorService;
  }

  public void managerMenu() {
    boolean running = true;
    while (running) {
      System.out.println("===MANAGER MENU===");
      System.out.println("""
          1. Add movie
          2. Add session
          3. Manage genres
          4. Create New Hall (and generate seats)
          0. Logout
          """);
      System.out.print("Choice: ");

      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1" -> addMovie();
        case "2" -> addSession();
        case "3" -> manageGenres();
        case "4" -> createNewHall();
        case "0" -> running = false;
        default -> System.out.println("Invalid choice! Please, try again.");
      }
    }
  }

  private void addMovie() {
    System.out.println("\n=== Add New Movie ===");

    System.out.print("Enter movie title: ");
    String title = scanner.nextLine().trim();

    System.out.print("Enter movie description: ");
    String description = scanner.nextLine().trim();

    int duration;
    while (true) {
      System.out.print("Enter movie duration (in minutes): ");
      try {
        duration = Integer.parseInt(scanner.nextLine().trim());
        break;
      } catch (NumberFormatException _) {
        System.out.println("Invalid number. Please try again.");
      }
    }
    List<Genre> allGenres = genreService.getAll();

    if (allGenres.isEmpty()) {
      System.out.println("Warning: No genres available in the system. Create a genre first.");
      return;
    }
    List<UUID> selectedGenreIds = selectGenres(allGenres);
    try {
      MovieStoreDto dto = new MovieStoreDto(
          title,
          description,
          duration,
          user.getId(),
          selectedGenreIds
      );

      movieService.create(dto);
      System.out.println("Success! Movie added.");
    } catch (IllegalArgumentException e) {
      System.out.println("Error adding movie: " + e.getMessage());
    }
  }

  private List<UUID> selectGenres(List<Genre> availableGenres) {
    List<UUID> selectedIds = new ArrayList<>();

    System.out.println("Available Genres:");
    for (int i = 0; i < availableGenres.size(); i++) {
      System.out.println((i + 1) + ". " + availableGenres.get(i).getName());
    }

    System.out.print("Select genres by number (comma separated, e.g., '1,3') or '0' to skip:");
    String genreInput = scanner.nextLine().trim();

    if (genreInput.equals("0") || genreInput.isBlank()) {
      return selectedIds;
    }

    String[] choices = genreInput.split(",");

    for (String choice : choices) {
      try {
        int index = Integer.parseInt(choice.trim()) - 1;

        if (index >= 0 && index < availableGenres.size()) {
          UUID genreId = availableGenres.get(index).getId();
          if (!selectedIds.contains(genreId)) {
            selectedIds.add(genreId);
          }
        } else {
          System.out.println("Invalid genre number ignored: " + (index + 1));
        }
      } catch (NumberFormatException _) {
        System.out.println("Invalid input ignored: " + choice);
      }
    }
    return selectedIds;
  }

  private void addSession() {
    System.out.println("\n=== Add Session ===");

    UUID movieId = selectMovieId();
    if (movieId == null) {
      return;
    }
    UUID hallId = selectHallId();
    if (hallId == null) {
      return;
    }
    int price;
    while (true) {
      System.out.print("Enter price: ");
      try {
        price = Integer.parseInt(scanner.nextLine().trim());
        if (price > 0) {
          break;
        }
        System.out.println("Price must be positive.");
      } catch (NumberFormatException _) {
        System.out.println(INVALID_NUMBER);
      }
    }
    LocalDateTime startTime = null;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    while (startTime == null) {
      System.out.print("Enter start time (format: yyyy-MM-dd HH:mm, e.g. 2025-12-31 18:30): ");
      String timeInput = scanner.nextLine().trim();
      try {
        startTime = LocalDateTime.parse(timeInput, formatter);

        if (startTime.isBefore(LocalDateTime.now())) {
          System.out.println("Error: Cannot create session in the past!");
          startTime = null;
        }
      } catch (DateTimeParseException _) {
        System.out.println("Invalid format! Please use yyyy-MM-dd HH:mm");
      }
    }
    try {
      SessionStoreDto dto = new SessionStoreDto(
          hallId,
          movieId,
          user.getId(),
          price,
          startTime
      );
      sessionService.create(dto);
      System.out.println("Success! Session created.");
    } catch (IllegalArgumentException e) {
      System.out.println("Error creating session: " + e.getMessage());
    }
  }

  private void manageGenres() {
    System.out.println("\n=== Manage genres ===");

    boolean back = false;
    while (!back) {
      System.out.println("""
          1. List all genres
          2. Add new genre
          0. Back to Main Menu
          """);
      System.out.print("Choice: ");

      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1" -> listGenres();
        case "2" -> createGenre();
        case "0" -> back = true;
        default -> System.out.println("Invalid choice!");
      }
    }
  }

  private void listGenres() {
    List<Genre> genres = genreService.getAll();

    if (genres.isEmpty()) {
      System.out.println("No genres found.");
    } else {
      genres.forEach(g -> System.out.println("- " + g.getName()));
    }
    System.out.println("\nPress Enter to continue...");
    scanner.nextLine();
  }

  private void createGenre() {
    System.out.print("Enter genre name: ");
    String name = scanner.nextLine().trim();

    try {
      GenreStoreDto dto = new GenreStoreDto(name);
      genreService.create(dto);
      System.out.println("Success! Genre '" + name + "' created.");
    } catch (IllegalArgumentException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private UUID selectMovieId() {
    List<Movie> movies = movieService.getAll();
    if (movies.isEmpty()) {
      System.out.println("Error: No movies found. Add a movie first.");
      return null;
    }

    System.out.println("Select a Movie:");
    for (int i = 0; i < movies.size(); i++) {
      System.out.println((i + 1) + ". " + movies.get(i).getTitle());
    }

    while (true) {
      System.out.print("Enter movie number (or 0 to cancel): ");
      try {
        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (index == -1) {
          return null;
        }
        if (index >= 0 && index < movies.size()) {
          return movies.get(index).getId();
        }
        System.out.println(INVALID_NUMBER);
      } catch (NumberFormatException _) {
        System.out.println("Invalid input.");
      }
    }
  }

  private UUID selectHallId() {
    List<Hall> halls = hallService.getAll();
    if (halls.isEmpty()) {
      System.out.println("Error: No halls found in system.");
      return null;
    }

    System.out.println("Select a Hall:");
    for (int i = 0; i < halls.size(); i++) {
      System.out.println((i + 1) + ". " + halls.get(i).getName());
    }

    while (true) {
      System.out.print("Enter hall number (or 0 to cancel): ");
      try {
        int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (index == -1) {
          return null;
        }
        if (index >= 0 && index < halls.size()) {
          return halls.get(index).getId();
        }
        System.out.println(INVALID_NUMBER);
      } catch (NumberFormatException _) {
        System.out.println("Invalid input.");
      }
    }
  }


  private void createNewHall() {
    System.out.println("\n=== Create New Hall ===");

    System.out.print("Enter hall name: ");
    String name = scanner.nextLine().trim();

    int rows;
    while (true) {
      System.out.print("Enter number of rows (max 24): ");
      try {
        rows = Integer.parseInt(scanner.nextLine().trim());
        if (rows > 0 && rows <= 24) {
          break;
        }
        System.out.println("Invalid rows count.");
      } catch (NumberFormatException _) {
        System.out.println(INVALID_NUMBER);
      }
    }

    int seatsPerRow;
    while (true) {
      System.out.print("Enter seats per row (max 32): ");
      try {
        seatsPerRow = Integer.parseInt(scanner.nextLine().trim());
        if (seatsPerRow > 0 && seatsPerRow <= 32) {
          break;
        }
        System.out.println("Invalid seats count.");
      } catch (NumberFormatException _) {
        System.out.println(INVALID_NUMBER);
      }
    }

    int vipRows;
    while (true) {
      System.out.print("Enter number of VIP rows (from the back, max " + rows + "): ");
      try {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
          vipRows = 0;
          break;
        }
        vipRows = Integer.parseInt(input);

        if (vipRows >= 0 && vipRows <= rows) {
          break;
        }
        System.out.println(
            "VIP rows cannot exceed total rows (" + rows + ") and must be positive.");
      } catch (NumberFormatException _) {
        System.out.println(INVALID_NUMBER);
      }
    }

    try {
      HallStoreDto hallDto = new HallStoreDto(name, rows, seatsPerRow);
      Hall newHall = hallService.create(hallDto);
      System.out.println("Hall '" + newHall.getName() + "' structure created.");

      System.out.println("Generating seats...");
      seatGeneratorService.generateSeatsForNewHall(newHall, vipRows);
    } catch (IllegalArgumentException e) {
      System.out.println("Error creating hall: " + e.getMessage());
    }
  }
}
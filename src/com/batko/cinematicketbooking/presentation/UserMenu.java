package com.batko.cinematicketbooking.presentation;

import com.batko.cinematicketbooking.domain.enums.SeatType;
import com.batko.cinematicketbooking.domain.enums.TicketStatus;
import com.batko.cinematicketbooking.domain.model.Hall;
import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.domain.model.Seat;
import com.batko.cinematicketbooking.domain.model.Session;
import com.batko.cinematicketbooking.domain.model.Ticket;
import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.contract.SeatService;
import com.batko.cinematicketbooking.service.contract.SessionService;
import com.batko.cinematicketbooking.service.contract.TicketService;
import com.batko.cinematicketbooking.service.dto.ticket.TicketStoreDto;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class UserMenu {

  private final User user;
  private final Scanner scanner;
  private final MovieService movieService;
  private final TicketService ticketService;
  private final SessionService sessionService;
  private final HallService hallService;
  private final SeatService seatService;

  public UserMenu(User user, Scanner scanner, MovieService movieService,
      TicketService ticketService, SessionService sessionService, HallService hallService,
      SeatService seatService) {
    this.user = user;
    this.scanner = scanner;
    this.movieService = movieService;
    this.ticketService = ticketService;
    this.sessionService = sessionService;
    this.hallService = hallService;
    this.seatService = seatService;
  }

  public void userMenu() {
    boolean running = true;

    while (running) {
      System.out.println("""
          1. View available movies (& Sessions)
          2. Buy ticket
          0. Logout""");

      System.out.print("Choice: ");
      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1" -> moviesAndSessions();
        case "2" -> buyTicketFlow();
        case "0" -> running = false;
        default -> System.out.println("Invalid choice! Please, try again.");
      }
    }
  }

  private void moviesAndSessions() {
    List<Movie> movies = movieService.getAll();
    if (movies.isEmpty()) {
      System.out.println("Error: No movies found. Add a movie first.");
    }

    System.out.println("\n--- Available Movies ---");
    for (int i = 0; i < movies.size(); i++) {
      Movie m = movies.get(i);
      System.out.printf("%d. %s (%d min)%n", (i + 1), m.getTitle(), m.getDurationMinutes());
    }

    while (true) {
      System.out.print("\nEnter movie number to view sessions (or '0' to back): ");
      String input = scanner.nextLine().trim();

      if (input.equals("0")) {
        return;
      }

      try {
        int index = Integer.parseInt(input) - 1;
        if (index >= 0 && index < movies.size()) {
          Movie selectedMovie = movies.get(index);
          viewSessions(selectedMovie);
          break;
        } else {
          System.out.println("Invalid movie number.");
        }
      } catch (NumberFormatException _) {
        System.out.println("Please enter a number.");
      }
    }
  }

  private void viewSessions(Movie movie) {
    System.out.println("\n--- Sessions for '" + movie.getTitle() + "' ---");

    List<Session> sessions = sessionService.getByMovie(movie.getId());

    if (sessions.isEmpty()) {
      System.out.println("No scheduled sessions for this movie yet.");
    } else {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

      for (int i = 0; i < sessions.size(); i++) {
        Session session = sessions.get(i);

        String hallName = "Unknown Hall";
        try {
          var hall = hallService.getById(session.getHallId());
          hallName = hall.getName();
        } catch (IllegalArgumentException _) {
          // Просто відображаємо  hallName = "Unknown Hall", якщо зал відсутній
        }
        System.out.printf("%d. Time: %s | Price: %d$ | Hall: %s%n",
            (i + 1),
            session.getStartTime().format(formatter),
            session.getPrice(),
            hallName);
      }
    }
    System.out.println("\nPress Enter to continue...");
    scanner.nextLine();
  }

  private void buyTicketFlow() {
    Movie selectedMovie = selectMovie();
    if (selectedMovie == null) {
      return;
    }

    Session selectedSession = selectSession(selectedMovie);
    if (selectedSession == null) {
      return;
    }

    processBookingLoop(selectedSession);
  }

  private Movie selectMovie() {
    List<Movie> movies = movieService.getAll();
    if (movies.isEmpty()) {
      System.out.println("No movies available.");
      return null;
    }

    System.out.println("\n--- Select Movie ---");
    for (int i = 0; i < movies.size(); i++) {
      System.out.printf("%d. %s%n", (i + 1), movies.get(i).getTitle());
    }
    System.out.print("Enter movie number (0 to cancel): ");

    int index = getUserInputIndex(movies.size());
    return (index == -1) ? null : movies.get(index);
  }

  private Session selectSession(Movie movie) {
    List<Session> sessions = sessionService.getByMovie(movie.getId());
    if (sessions.isEmpty()) {
      System.out.println("No sessions available for this movie.");
      return null;
    }

    System.out.println("\n--- Select Session for " + movie.getTitle() + " ---");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    for (int i = 0; i < sessions.size(); i++) {
      Session s = sessions.get(i);
      String hallName = getHallNameSafe(s.getHallId());
      System.out.printf("%d. %s | %s | %d$%n", (i + 1), s.getStartTime().format(formatter),
          hallName, s.getPrice());
    }

    System.out.print("Enter session number (0 to cancel): ");

    int index = getUserInputIndex(sessions.size());
    return (index == -1) ? null : sessions.get(index);
  }

  private void processBookingLoop(Session session) {
    Hall hall = hallService.getById(session.getHallId());
    List<Seat> seats = seatService.getAllByHall(hall.getId());

    while (true) {
      List<Ticket> soldTickets = ticketService.getTicketsBySession(session.getId());

      printSeatMap(hall, seats, soldTickets);

      System.out.println("\n--- Buy Ticket ---");
      System.out.print("Enter row and seat number (e.g., '3 5') or '0' to cancel: ");
      String input = scanner.nextLine().trim();

      if (input.equals("0")) {
        break;
      }

      attemptBooking(input, session, seats, soldTickets);
    }
  }

  private void attemptBooking(String input, Session session, List<Seat> seats,
      List<Ticket> soldTickets) {
    String[] parts = input.split("\\s+");
    if (parts.length != 2) {
      System.out.println("Please enter ROW and NUMBER separated by space.");
      return;
    }

    try {
      int row = Integer.parseInt(parts[0]);
      int number = Integer.parseInt(parts[1]);

      Seat selectedSeat = findSeat(seats, row, number);

      if (selectedSeat == null) {
        System.out.println("Seat does not exist.");
        return;
      }

      if (isSeatBooked(selectedSeat, soldTickets)) {
        System.out.println("Sorry, this seat is already booked.");
        return;
      }

      ticketService.create(
          new TicketStoreDto(user.getId(), session.getId(), selectedSeat.getId()));
      System.out.println("Success! Ticket purchased.");
      System.out.println("\nPress Enter to continue...");
      scanner.nextLine();
    } catch (NumberFormatException _) {
      System.out.println("Invalid number format.");
    } catch (Exception e) {
      System.out.println("Error buying ticket: " + e.getMessage());
    }
  }

  private int getUserInputIndex(int listSize) {
    try {
      String line = scanner.nextLine().trim();
      int index = Integer.parseInt(line) - 1;
      if (index >= 0 && index < listSize) {
        return index;
      }
      if (!line.equals("0")) {
        System.out.println("Invalid selection.");
      }
    } catch (NumberFormatException _) {
      System.out.println("Invalid input (not a number).");
    }
    return -1;
  }

  private String getHallNameSafe(UUID hallId) {
    try {
      return hallService.getById(hallId).getName();
    } catch (IllegalArgumentException _) {
      return "Unknown Hall";
    }
  }

  private Seat findSeat(List<Seat> seats, int row, int number) {
    return seats.stream()
        .filter(s -> s.getRow() == row && s.getNumber() == number)
        .findFirst()
        .orElse(null);
  }

  private boolean isSeatBooked(Seat seat, List<Ticket> soldTickets) {
    return soldTickets.stream()
        .anyMatch(t -> t.getSeatId().equals(seat.getId())
            && t.getStatus() != TicketStatus.CANCELED);
  }

  private void printSeatMap(Hall hall, List<Seat> seats, List<Ticket> soldTickets) {
    System.out.println("\nConsole Screen (Hall: " + hall.getName() + ")");
    System.out.println("--------------------------------------------------");

    for (int row = 1; row <= hall.getRows(); row++) {
      System.out.printf("Row %-2d: ", row);
      for (int num = 1; num <= hall.getSeatsPerRow(); num++) {
        final int r = row;
        final int n = num;

        Seat seat = seats.stream()
            .filter(s -> s.getRow() == r && s.getNumber() == n)
            .findFirst()
            .orElse(null);

        String symbol = "   ";

        if (seat != null) {
          boolean isBooked = soldTickets.stream()
              .anyMatch(t -> t.getSeatId().equals(seat.getId())
                  && t.getStatus() != TicketStatus.CANCELED);

          if (isBooked) {
            symbol = "[X]";
          } else if (seat.getSeatType() == SeatType.VIP) {
            symbol = "[V]";
          } else {
            symbol = "[ ]";
          }
        }
        System.out.print(symbol + " ");
      }
      System.out.println();
    }

    System.out.println("--------------------------------------------------");
    System.out.println("Legend: [ ] - Available, [X] - Booked, [V] - VIP");
  }
}
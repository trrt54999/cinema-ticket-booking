package com.batko.cinematicketbooking.presentation.handler;

import com.batko.cinematicketbooking.domain.enums.SeatType;
import com.batko.cinematicketbooking.domain.enums.TicketStatus;
import com.batko.cinematicketbooking.domain.model.Hall;
import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.domain.model.Seat;
import com.batko.cinematicketbooking.domain.model.Session;
import com.batko.cinematicketbooking.domain.model.Ticket;
import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.presentation.util.ConsoleUiUtils;
import com.batko.cinematicketbooking.presentation.util.EmojiConstants;
import com.batko.cinematicketbooking.presentation.util.JLineMenuRenderer;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.contract.SeatService;
import com.batko.cinematicketbooking.service.contract.SessionService;
import com.batko.cinematicketbooking.service.contract.TicketService;
import com.batko.cinematicketbooking.service.dto.ticket.TicketStoreDto;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class BookingHandler {

  private final User user;
  private final Scanner scanner;
  private final MovieService movieService;
  private final SessionService sessionService;
  private final HallService hallService;
  private final SeatService seatService;
  private final TicketService ticketService;

  public BookingHandler(User user, Scanner scanner, MovieService movieService,
      SessionService sessionService, HallService hallService, SeatService seatService,
      TicketService ticketService) {
    this.user = user;
    this.scanner = scanner;
    this.movieService = movieService;
    this.sessionService = sessionService;
    this.hallService = hallService;
    this.seatService = seatService;
    this.ticketService = ticketService;
  }

  public void handle() {
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
    while (true) {
      List<Movie> movies = movieService.getAll();
      if (movies.isEmpty()) {
        System.out.println(EmojiConstants.WARNING + " No movies available.");
        return null;
      }

      String[] menuOptions = new String[movies.size() + 1];
      for (int i = 0; i < movies.size(); i++) {
        menuOptions[i] = String.format("%d. %s", (i + 1), movies.get(i).getTitle());
      }
      menuOptions[movies.size()] = "0. Back";

      JLineMenuRenderer.renderMenu("SELECT MOVIE FOR BOOKING", menuOptions);

      String input = scanner.nextLine().trim();
      if (input.equals("0")) {
        return null;
      }

      int index = parseIndex(input, movies.size());
      if (index != -1) {
        return movies.get(index);
      }
      System.out.println(EmojiConstants.ERROR + " Invalid movie number.");
    }
  }

  private Session selectSession(Movie movie) {
    while (true) {
      List<Session> sessions = sessionService.getByMovie(movie.getId());
      if (sessions.isEmpty()) {
        System.out.println(EmojiConstants.WARNING + " No sessions available for this movie.");
        return null;
      }

      System.out.println("\n" + EmojiConstants.TICKET + " Sessions for " + ConsoleUiUtils.toCyan(
          movie.getTitle()));
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

      System.out.println("─────────────────────────────────────────────────────────");
      System.out.printf("%-4s | %-16s | %-8s | %s%n", "#", "Time", "Price", "Hall");
      System.out.println("─────────────────────────────────────────────────────────");

      for (int i = 0; i < sessions.size(); i++) {
        Session s = sessions.get(i);
        String hallName = ConsoleUiUtils.getHallNameSafe(hallService, s.getHallId());
        System.out.printf("%-4d | %-16s | %-8s | %s%n",
            (i + 1),
            s.getStartTime().format(formatter),
            ConsoleUiUtils.toGreen(s.getPrice() + "$"),
            hallName);
      }
      System.out.println("─────────────────────────────────────────────────────────");

      System.out.print(EmojiConstants.POINT_RIGHT + " Enter session number (0 to cancel): ");
      String input = scanner.nextLine().trim();

      if (input.equals("0")) {
        return null;
      }

      int index = parseIndex(input, sessions.size());
      if (index != -1) {
        return sessions.get(index);
      }
      System.out.println(EmojiConstants.ERROR + " Invalid session number.");
    }
  }

  private void processBookingLoop(Session session) {
    Hall hall = hallService.getById(session.getHallId());
    List<Seat> seats = seatService.getAllByHall(hall.getId());

    while (true) {
      List<Ticket> soldTickets = ticketService.getTicketsBySession(session.getId());

      printSeatMap(hall, seats, soldTickets);

      System.out.println("\n" + EmojiConstants.TICKET + " --- Buy Ticket ---");
      System.out.print(EmojiConstants.POINT_RIGHT
          + " Enter row and seat number (e.g., '3 5') or '0' to cancel: ");
      String input = scanner.nextLine().trim();

      if (input.equals("0")) {
        break;
      }

      if (attemptBooking(input, session, seats, soldTickets)) {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
      }
    }
  }

  private boolean attemptBooking(String input, Session session, List<Seat> seats,
      List<Ticket> soldTickets) {
    String[] parts = input.split("\\s+");
    if (parts.length != 2) {
      System.out.println(EmojiConstants.ERROR + " Please enter ROW and NUMBER separated by space.");
      return false;
    }

    try {
      int row = Integer.parseInt(parts[0]);
      int number = Integer.parseInt(parts[1]);

      Seat selectedSeat = seats.stream()
          .filter(s -> s.getRow() == row && s.getNumber() == number)
          .findFirst()
          .orElse(null);

      if (selectedSeat == null) {
        System.out.println(EmojiConstants.ERROR + " Seat does not exist.");
        return false;
      }

      boolean isBooked = soldTickets.stream()
          .anyMatch(t -> t.getSeatId().equals(selectedSeat.getId())
              && t.getStatus() != TicketStatus.CANCELED);

      if (isBooked) {
        System.out.println(EmojiConstants.FORBIDDEN + " Sorry, this seat is already booked.");
        return false;
      }

      ticketService.create(
          new TicketStoreDto(user.getId(), session.getId(), selectedSeat.getId()));
      System.out.println(EmojiConstants.SUCCESS + " Success! Ticket purchased.");
      return true;
    } catch (NumberFormatException _) {
      System.out.println(EmojiConstants.ERROR + " Invalid number format.");
    } catch (Exception e) {
      System.out.println(EmojiConstants.ERROR + " Error buying ticket: " + e.getMessage());
    }
    return false;
  }

  private void printSeatMap(Hall hall, List<Seat> seats, List<Ticket> soldTickets) {
    int prefixWidth = 8;
    int seatUnitWidth = 4;
    int totalWidth = prefixWidth + (hall.getSeatsPerRow() * seatUnitWidth);

    String separator = "─".repeat(totalWidth);

    String screenText = "[ SCREEN ]";
    int screenPadding = (totalWidth - screenText.length()) / 2;
    screenPadding = Math.max(0, screenPadding);

    String centeredScreen = " ".repeat(screenPadding) + screenText;

    System.out.println("\n" + ConsoleUiUtils.toCyan(centeredScreen));
    System.out.println(ConsoleUiUtils.toCyan(separator));

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
            symbol = ConsoleUiUtils.toRed("[X]");
          } else if (seat.getSeatType() == SeatType.VIP) {
            symbol = ConsoleUiUtils.toYellow("[V]");
          } else {
            symbol = ConsoleUiUtils.toGreen("[ ]");
          }
        }
        System.out.print(symbol + " ");
      }
      System.out.println();
    }

    System.out.println(ConsoleUiUtils.toCyan(separator));
    System.out.println("Legend: " + ConsoleUiUtils.toGreen("[ ]") + " Available | "
        + ConsoleUiUtils.toYellow("[V]") + " VIP | "
        + ConsoleUiUtils.toRed("[X]") + " Booked");
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
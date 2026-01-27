package com.batko.cinematicketbooking.presentation.handler;

import com.batko.cinematicketbooking.domain.enums.TicketStatus;
import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.domain.model.Session;
import com.batko.cinematicketbooking.domain.model.Ticket;
import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.presentation.util.ConsoleUiUtils;
import com.batko.cinematicketbooking.presentation.util.EmojiConstants;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.contract.SeatService;
import com.batko.cinematicketbooking.service.contract.SessionService;
import com.batko.cinematicketbooking.service.contract.TicketService;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TicketHandler {

  private final User user;
  private final Scanner scanner;
  private final TicketService ticketService;
  private final SessionService sessionService;
  private final MovieService movieService;
  private final HallService hallService;
  private final SeatService seatService;

  public TicketHandler(User user, Scanner scanner, TicketService ticketService,
      SessionService sessionService, MovieService movieService, HallService hallService,
      SeatService seatService) {
    this.user = user;
    this.scanner = scanner;
    this.ticketService = ticketService;
    this.sessionService = sessionService;
    this.movieService = movieService;
    this.hallService = hallService;
    this.seatService = seatService;
  }

  public void handle() {
    System.out.println("\n" + EmojiConstants.MY_TICKETS + " === MY TICKETS ===");
    List<Ticket> tickets = new ArrayList<>(ticketService.getTicketsByUser(user.getId()));

    if (tickets.isEmpty()) {
      System.out.println(EmojiConstants.WARNING + " You have no tickets.");
      return;
    }

    tickets.sort((t1, t2) -> t2.getPurchaseDate().compareTo(t1.getPurchaseDate()));

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    for (int i = 0; i < tickets.size(); i++) {
      Ticket t = tickets.get(i);

      String statusDisplay;
      if (t.getStatus() == TicketStatus.CANCELED) {
        statusDisplay = ConsoleUiUtils.toRed(EmojiConstants.ERROR + " [CANCELED]");
      } else {
        statusDisplay = ConsoleUiUtils.toGreen(EmojiConstants.SUCCESS + " [ACTIVE]");
      }

      String movieTitle = "Unknown Movie";
      String sessionTime = "Unknown Time";
      String seatInfo = "Unknown Seat";

      try {
        Session s = sessionService.getById(t.getSessionId());
        sessionTime = s.getStartTime().format(formatter);
        Movie m = movieService.getById(s.getMovieId());
        movieTitle = ConsoleUiUtils.toYellow(m.getTitle());

        var hall = hallService.getById(s.getHallId());
        var seat = seatService.getById(t.getSeatId());
        seatInfo = String.format("Row %d, Seat %d (%s)", seat.getRow(), seat.getNumber(),
            hall.getName());

      } catch (Exception _) {
      }

      System.out.println("------------------------------------------------------------");
      System.out.printf("%d. %s  %s%n", (i + 1), statusDisplay, movieTitle);
      System.out.printf("    %s Time: %s%n", EmojiConstants.TICKET, sessionTime);
      System.out.printf("    %s Place: %s%n", EmojiConstants.SEAT, seatInfo);
    }
    System.out.println("------------------------------------------------------------");

    while (true) {
      System.out.print(
          "\nEnter ticket number to " + ConsoleUiUtils.toRed("CANCEL") + " (or '0' to back): ");
      int index = ConsoleUiUtils.getUserInputIndex(scanner, tickets.size());

      if (index == -1) {
        return;
      }

      Ticket selectedTicket = tickets.get(index);

      if (selectedTicket.getStatus() == TicketStatus.CANCELED) {
        System.out.println(EmojiConstants.WARNING + " This ticket is already canceled.");
        continue;
      }

      System.out.print(
          EmojiConstants.QUESTION + " Are you sure you want to cancel this ticket? (y/n): ");
      String confirm = scanner.nextLine().trim();

      if (confirm.equalsIgnoreCase("y")) {
        ticketService.cancel(selectedTicket.getId());
        System.out.println(EmojiConstants.SUCCESS + " Ticket canceled successfully.");
        return;
      }
    }
  }
}
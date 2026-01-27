package com.batko.cinematicketbooking.presentation;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.presentation.handler.BookingHandler;
import com.batko.cinematicketbooking.presentation.handler.MovieBrowserHandler;
import com.batko.cinematicketbooking.presentation.handler.ProfileHandler;
import com.batko.cinematicketbooking.presentation.handler.TicketHandler;
import com.batko.cinematicketbooking.presentation.util.EmojiConstants;
import com.batko.cinematicketbooking.presentation.util.JLineMenuRenderer;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.contract.SeatService;
import com.batko.cinematicketbooking.service.contract.SessionService;
import com.batko.cinematicketbooking.service.contract.TicketService;
import com.batko.cinematicketbooking.service.contract.UserService;
import java.util.Scanner;

public class UserMenu {

  private final Scanner scanner;
  private final MovieBrowserHandler movieBrowserHandler;
  private final BookingHandler bookingHandler;
  private final TicketHandler ticketHandler;
  private final ProfileHandler profileHandler;
  private final UserService userService;

  public UserMenu(User user, Scanner scanner, MovieService movieService,
      TicketService ticketService, SessionService sessionService, HallService hallService,
      SeatService seatService, UserService userService) {
    this.scanner = scanner;
    this.movieBrowserHandler = new MovieBrowserHandler(scanner, movieService, sessionService,
        hallService);
    this.bookingHandler = new BookingHandler(user, scanner, movieService, sessionService,
        hallService, seatService, ticketService);
    this.ticketHandler = new TicketHandler(user, scanner, ticketService, sessionService,
        movieService, hallService, seatService);
    this.userService = userService;
    this.profileHandler = new ProfileHandler(user, scanner, userService);
  }

  public void userMenu() {
    boolean running = true;

    while (running) {
      JLineMenuRenderer.renderMenu(EmojiConstants.USER_MENU_TITLE + " USER MENU",
          "1. " + EmojiConstants.MOVIES + " View available movies (& Sessions)",
          "2. " + EmojiConstants.TICKET + " Buy ticket",
          "3. " + EmojiConstants.MY_TICKETS + " My tickets",
          "4. " + EmojiConstants.USER_MENU_TITLE + " My profile",
          "0. " + EmojiConstants.LOGOUT + " Logout");

      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1" -> movieBrowserHandler.handle();
        case "2" -> bookingHandler.handle();
        case "3" -> ticketHandler.handle();
        case "4" -> profileHandler.handle();
        case "0" -> running = false;
        default ->
            System.out.println(EmojiConstants.FORBIDDEN + " Invalid choice! Please, try again.");
      }
    }
  }
}
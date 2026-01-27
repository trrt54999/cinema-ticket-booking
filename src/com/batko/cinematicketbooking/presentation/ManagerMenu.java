package com.batko.cinematicketbooking.presentation;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.presentation.handler.GenreManagementHandler;
import com.batko.cinematicketbooking.presentation.handler.HallManagementHandler;
import com.batko.cinematicketbooking.presentation.handler.MovieManagementHandler;
import com.batko.cinematicketbooking.presentation.handler.ProfileHandler;
import com.batko.cinematicketbooking.presentation.handler.SessionManagementHandler;
import com.batko.cinematicketbooking.presentation.util.EmojiConstants;
import com.batko.cinematicketbooking.presentation.util.JLineMenuRenderer;
import com.batko.cinematicketbooking.service.contract.GenreService;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.contract.SeatGeneratorService;
import com.batko.cinematicketbooking.service.contract.SessionService;
import com.batko.cinematicketbooking.service.contract.UserService;
import java.util.Scanner;

public class ManagerMenu {

  private final Scanner scanner;
  private final MovieManagementHandler movieHandler;
  private final SessionManagementHandler sessionHandler;
  private final GenreManagementHandler genreHandler;
  private final HallManagementHandler hallHandler;
  private final ProfileHandler profileHandler;
  private final UserService userService;


  public ManagerMenu(User user, Scanner scanner, GenreService genreService,
      MovieService movieService, SessionService sessionService, HallService hallService,
      SeatGeneratorService seatGeneratorService, UserService userService) {
    this.scanner = scanner;
    this.movieHandler = new MovieManagementHandler(user, scanner, movieService, genreService);
    this.sessionHandler = new SessionManagementHandler(user, scanner, sessionService, movieService,
        hallService);
    this.genreHandler = new GenreManagementHandler(scanner, genreService);
    this.hallHandler = new HallManagementHandler(scanner, hallService, seatGeneratorService);
    this.userService = userService;
    this.profileHandler = new ProfileHandler(user, scanner, userService);
  }

  public void managerMenu() {
    boolean running = true;
    while (running) {
      JLineMenuRenderer.renderMenu(EmojiConstants.MANAGER_MENU_TITLE + " MANAGER MENU",
          "1. " + EmojiConstants.MOVIES + " Manage movies",
          "2. " + EmojiConstants.TICKET + " Manage sessions",
          "3. " + EmojiConstants.MANAGE + " Manage genres",
          "4. " + EmojiConstants.HALL + " Create New Hall (& Seats)",
          "5. " + EmojiConstants.USER_MENU_TITLE + " My profile",
          "0. " + EmojiConstants.LOGOUT + " Logout"
      );

      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1" -> movieHandler.handle();
        case "2" -> sessionHandler.handle();
        case "3" -> genreHandler.handle();
        case "4" -> hallHandler.handle();
        case "5" -> profileHandler.handle();
        case "0" -> running = false;
        default ->
            System.out.println(EmojiConstants.FORBIDDEN + " Invalid choice! Please, try again.");
      }
    }
  }
}
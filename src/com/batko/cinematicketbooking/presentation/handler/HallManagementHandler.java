package com.batko.cinematicketbooking.presentation.handler;

import com.batko.cinematicketbooking.domain.model.Hall;
import com.batko.cinematicketbooking.presentation.util.EmojiConstants;
import com.batko.cinematicketbooking.presentation.util.JLineMenuRenderer;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.contract.SeatGeneratorService;
import com.batko.cinematicketbooking.service.dto.hall.HallStoreDto;
import java.util.Scanner;

public class HallManagementHandler {

  private static final String INVALID_NUMBER = EmojiConstants.ERROR + " Invalid number.";
  private final Scanner scanner;
  private final HallService hallService;
  private final SeatGeneratorService seatGeneratorService;

  public HallManagementHandler(Scanner scanner, HallService hallService,
      SeatGeneratorService seatGeneratorService) {
    this.scanner = scanner;
    this.hallService = hallService;
    this.seatGeneratorService = seatGeneratorService;
  }

  public void handle() {
    JLineMenuRenderer.printHeader(EmojiConstants.HALL + " CREATE NEW HALL");

    System.out.print(EmojiConstants.POINT_RIGHT + " Enter hall name: ");
    String name = scanner.nextLine().trim();

    int rows;
    while (true) {
      System.out.print(EmojiConstants.POINT_RIGHT + " Enter number of rows (max 24): ");
      try {
        rows = Integer.parseInt(scanner.nextLine().trim());
        if (rows > 0 && rows <= 24) {
          break;
        }
        System.out.println(EmojiConstants.ERROR + " Invalid rows count (1-24).");
      } catch (NumberFormatException _) {
        System.out.println(INVALID_NUMBER);
      }
    }

    int seatsPerRow;
    while (true) {
      System.out.print(EmojiConstants.POINT_RIGHT + " Enter seats per row (max 32): ");
      try {
        seatsPerRow = Integer.parseInt(scanner.nextLine().trim());
        if (seatsPerRow > 0 && seatsPerRow <= 32) {
          break;
        }
        System.out.println(EmojiConstants.ERROR + " Invalid seats count (1-32).");
      } catch (NumberFormatException _) {
        System.out.println(INVALID_NUMBER);
      }
    }

    int vipRows;
    while (true) {
      System.out.print(
          EmojiConstants.POINT_RIGHT + " Enter number of VIP rows (from the back, max " + rows
              + "): ");
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
        System.out.println(EmojiConstants.ERROR + " VIP rows cannot exceed total rows (" + rows
            + ") and must be positive.");
      } catch (NumberFormatException _) {
        System.out.println(INVALID_NUMBER);
      }
    }

    try {
      HallStoreDto hallDto = new HallStoreDto(name, rows, seatsPerRow);
      Hall newHall = hallService.create(hallDto);
      System.out.println(
          EmojiConstants.SUCCESS + " Hall '" + newHall.getName() + "' structure created.");

      System.out.println(EmojiConstants.MANAGE + " Generating seats...");
      seatGeneratorService.generateSeatsForNewHall(newHall, vipRows);
      System.out.println(EmojiConstants.SUCCESS + " All seats generated successfully.");
    } catch (IllegalArgumentException e) {
      System.out.println(EmojiConstants.ERROR + " Error creating hall: " + e.getMessage());
    }
  }
}
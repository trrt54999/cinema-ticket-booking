package com.batko.cinematicketbooking.presentation.handler;

import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.presentation.util.ConsoleUiUtils;
import com.batko.cinematicketbooking.presentation.util.EmojiConstants;
import com.batko.cinematicketbooking.presentation.util.JLineMenuRenderer;
import com.batko.cinematicketbooking.service.contract.UserService;
import com.batko.cinematicketbooking.service.dto.user.UserUpdateDto;
import java.util.Scanner;

public class ProfileHandler {

  private final User user;
  private final Scanner scanner;
  private final UserService userService;

  public ProfileHandler(User user, Scanner scanner, UserService userService) {
    this.user = user;
    this.scanner = scanner;
    this.userService = userService;
  }

  public void handle() {
    boolean back = false;
    while (!back) {
      JLineMenuRenderer.renderMenu("MY PROFILE",
          "1. " + EmojiConstants.USER_MENU_TITLE + " View Profile Info",
          "2. " + EmojiConstants.PEN + " Edit Personal Info",
          "3. " + EmojiConstants.AUTHENTICATION_TITLE + " Change Password",
          "0. " + EmojiConstants.SUCCESS + " Back");

      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1" -> viewProfile();
        case "2" -> editInfo();
        case "3" -> changePassword();
        case "0" -> back = true;
        default -> System.out.println(EmojiConstants.ERROR + " Invalid choice!");
      }
    }
  }

  private void viewProfile() {
    printProfileCard();
    System.out.println("\nPress Enter to continue...");
    scanner.nextLine();
  }

  private void printProfileCard() {
    int innerWidth = 46;
    String border = "─".repeat(innerWidth);

    System.out.println(ConsoleUiUtils.toCyan("\n┌" + border + "┐"));

    String headerTitle = "USER PROFILE";
    String emoji = EmojiConstants.USER_MENU_TITLE;
    String headerContent = emoji + "  " + ConsoleUiUtils.toYellow(headerTitle);
    printRowContent(headerContent, innerWidth, true);

    System.out.println(ConsoleUiUtils.toCyan("├" + border + "┤"));

    printRow("First Name", user.getFirstName(), innerWidth);
    printRow("Last Name", user.getLastName(), innerWidth);
    printRow("Email", user.getEmail(), innerWidth);
    printRow("Age", String.valueOf(user.getAge()), innerWidth);
    printRow("Role", user.getRole().toString(), innerWidth);

    System.out.println(ConsoleUiUtils.toCyan("└" + border + "┘\n"));
  }

  private void printRow(String label, String value, int width) {
    String prefix = String.format("  %-5s  ", label + ":");
    String content = prefix + value;
    printRowContent(content, width, false);
  }

  private void printRowContent(String content, int width, boolean center) {
    int visibleLength = JLineMenuRenderer.getVisibleLength(content);
    int padding = width - visibleLength;

    if (padding < 0) {
      padding = 0;
    }

    String leftPad = "";
    String rightPad = "";

    if (center) {
      int left = padding / 2;
      leftPad = " ".repeat(left);
      rightPad = " ".repeat(padding - left);
    } else {
      rightPad = " ".repeat(padding);
    }

    System.out.println(
        ConsoleUiUtils.toCyan("│") + leftPad + content + rightPad + ConsoleUiUtils.toCyan("│"));
  }

  private void editInfo() {
    JLineMenuRenderer.printHeader("🖊️ EDIT INFO");
    System.out.println("Leave field empty to keep current value.");

    System.out.print(EmojiConstants.POINT_RIGHT + " First Name (" + user.getFirstName() + "): ");
    String fName = scanner.nextLine().trim();
    String newFirstName = fName.isEmpty() ? user.getFirstName() : fName;

    System.out.print(EmojiConstants.POINT_RIGHT + " Last Name (" + user.getLastName() + "): ");
    String lName = scanner.nextLine().trim();
    String newLastName = lName.isEmpty() ? user.getLastName() : lName;

    Integer newAge = user.getAge();
    while (true) {
      System.out.print(EmojiConstants.POINT_RIGHT + " Age (" + user.getAge() + "): ");
      String ageInput = scanner.nextLine().trim();
      if (ageInput.isEmpty()) {
        break;
      }
      try {
        newAge = Integer.parseInt(ageInput);
        break;
      } catch (NumberFormatException _) {
        System.out.println(EmojiConstants.ERROR + " Invalid number.");
      }
    }

    try {
      UserUpdateDto dto = new UserUpdateDto(newFirstName, newLastName, null, newAge);
      userService.update(user.getId(), dto);
      System.out.println(EmojiConstants.SUCCESS + " Profile updated!");
    } catch (Exception e) {
      System.out.println(EmojiConstants.ERROR + " Update failed: " + e.getMessage());
    }
  }

  private void changePassword() {
    JLineMenuRenderer.printHeader(EmojiConstants.AUTHENTICATION_TITLE + " CHANGE PASSWORD");

    System.out.print(EmojiConstants.POINT_RIGHT + " Enter new password (min 6 chars): ");
    String p1 = ConsoleUiUtils.readPassword(scanner);

    if (p1.isEmpty()) {
      System.out.println("Cancelled.");
      return;
    }

    System.out.print(EmojiConstants.POINT_RIGHT + " Confirm new password: ");
    String p2 = ConsoleUiUtils.readPassword(scanner);

    if (!p1.equals(p2)) {
      System.out.println(EmojiConstants.ERROR + " Passwords do not match!");
      return;
    }

    try {
      UserUpdateDto dto = new UserUpdateDto(null, null, p1, null);
      userService.update(user.getId(), dto);
      System.out.println(EmojiConstants.SUCCESS + " Password changed successfully!");
    } catch (Exception e) {
      System.out.println(EmojiConstants.ERROR + " Error: " + e.getMessage());
    }
  }
}
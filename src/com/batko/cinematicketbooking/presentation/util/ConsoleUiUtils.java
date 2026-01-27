package com.batko.cinematicketbooking.presentation.util;

import com.batko.cinematicketbooking.service.contract.HallService;
import java.io.Console;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

public final class ConsoleUiUtils {

  private ConsoleUiUtils() {
  }

  public static String toGreen(String text) {
    return new AttributedStringBuilder()
        .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN))
        .append(text).toAnsi();
  }

  public static String toCyan(String text) {
    return new AttributedStringBuilder()
        .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN))
        .append(text).toAnsi();
  }

  public static String toYellow(String text) {
    return new AttributedStringBuilder()
        .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW))
        .append(text).toAnsi();
  }

  public static String toRed(String text) {
    return new AttributedStringBuilder()
        .style(AttributedStyle.DEFAULT.foreground(AttributedStyle.RED))
        .append(text).toAnsi();
  }

  public static List<String> wrapText(String text, int maxWidth) {
    if (text == null || text.isEmpty()) {
      return Collections.emptyList();
    }
    List<String> lines = new ArrayList<>();
    String[] words = text.split(" ");
    StringBuilder currentLine = new StringBuilder();

    for (String word : words) {
      if (currentLine.length() + word.length() + 1 > maxWidth) {
        lines.add(currentLine.toString());
        currentLine = new StringBuilder(word);
      } else {
        if (currentLine.length() > 0) {
          currentLine.append(" ");
        }
        currentLine.append(word);
      }
    }
    if (currentLine.length() > 0) {
      lines.add(currentLine.toString());
    }
    return lines;
  }

  public static int getUserInputIndex(Scanner scanner, int listSize) {
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

  public static String readPassword(Scanner scanner) {
    Console console = System.console();
    if (console != null) {
      char[] passwordChars = console.readPassword();
      return new String(passwordChars);
    } else {
      return scanner.nextLine().trim();
    }
  }

  public static String getHallNameSafe(HallService hallService, UUID hallId) {
    try {
      return hallService.getById(hallId).getName();
    } catch (IllegalArgumentException _) {
      return "Unknown Hall";
    }
  }
}
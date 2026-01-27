package com.batko.cinematicketbooking.presentation.util;

import java.io.IOException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;

public final class JLineMenuRenderer {

  private static Terminal terminal;

  static {
    try {
      terminal = TerminalBuilder.builder().system(true).dumb(true).build();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private JLineMenuRenderer() {
  }

  public static void printHeader(String title) {
    int width = 60;
    AttributedStyle borderStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN);
    AttributedStyle titleStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW).bold();

    AttributedStringBuilder asb = new AttributedStringBuilder();

    asb.style(borderStyle);
    asb.append("\n┌").append("─".repeat(width - 2)).append("┐").append("\n");

    asb.append("│");
    asb.style(titleStyle).append(centerText(title, width - 2));
    asb.style(borderStyle).append("│").append("\n");

    asb.style(borderStyle);
    asb.append("└").append("─".repeat(width - 2)).append("┘").append("\n");

    System.out.print(asb.toAnsi());
  }

  public static void renderMenu(String title, String... options) {
    int width = 60;

    AttributedStyle borderStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN);
    AttributedStyle titleStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW).bold();
    AttributedStyle optionStyle = AttributedStyle.DEFAULT.foreground(AttributedStyle.WHITE);

    AttributedStringBuilder asb = new AttributedStringBuilder();

    asb.style(borderStyle);
    asb.append("┌").append("─".repeat(width - 2)).append("┐").append("\n");

    asb.append("│");
    asb.style(titleStyle).append(centerText(title, width - 2));
    asb.style(borderStyle).append("│").append("\n");

    asb.style(borderStyle);
    asb.append("├").append("─".repeat(width - 2)).append("┤").append("\n");

    for (String option : options) {
      asb.style(borderStyle).append("│ ");
      asb.style(optionStyle).append(padRight(option, width - 4));
      asb.style(borderStyle).append(" │").append("\n");
    }

    asb.style(borderStyle);
    asb.append("└").append("─".repeat(width - 2)).append("┘").append("\n");

    System.out.print(asb.toAnsi());

    AttributedStringBuilder prompt = new AttributedStringBuilder();
    prompt.style(AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
    prompt.append("➤ Choice: ");

    prompt.style(AttributedStyle.DEFAULT);

    System.out.print(prompt.toAnsi());
  }

  private static String centerText(String text, int width) {
    if (text == null) {
      return " ".repeat(width);
    }
    int visibleLength = getVisibleLength(text);

    if (visibleLength >= width) {
      return text.substring(0, Math.min(text.length(), width));
    }

    int padding = (width - visibleLength) / 2;
    int rightPadding = width - visibleLength - padding;

    return " ".repeat(padding) + text + " ".repeat(rightPadding);
  }

  private static String padRight(String text, int width) {
    if (text == null) {
      return " ".repeat(width);
    }
    int visibleLength = getVisibleLength(text);

    if (visibleLength >= width) {
      return text.substring(0, Math.min(text.length(), width));
    }

    return text + " ".repeat(width - visibleLength);
  }

  public static int getVisibleLength(String text) {
    int len = AttributedString.fromAnsi(text).columnLength();

    if (text.contains(EmojiConstants.SUCCESS)) {
      len += 1;
    }
    if (text.contains(EmojiConstants.ADD)) {
      len += 1;
    }
    return len;
  }
}
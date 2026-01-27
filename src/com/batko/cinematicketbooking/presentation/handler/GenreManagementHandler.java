package com.batko.cinematicketbooking.presentation.handler;

import com.batko.cinematicketbooking.domain.model.Genre;
import com.batko.cinematicketbooking.presentation.util.EmojiConstants;
import com.batko.cinematicketbooking.presentation.util.JLineMenuRenderer;
import com.batko.cinematicketbooking.service.contract.GenreService;
import com.batko.cinematicketbooking.service.dto.genre.GenreStoreDto;
import com.batko.cinematicketbooking.service.dto.genre.GenreUpdateDto;
import java.util.List;
import java.util.Scanner;

public class GenreManagementHandler {

  private final Scanner scanner;
  private final GenreService genreService;

  public GenreManagementHandler(Scanner scanner, GenreService genreService) {
    this.scanner = scanner;
    this.genreService = genreService;
  }

  public void handle() {
    boolean back = false;
    while (!back) {
      JLineMenuRenderer.renderMenu(EmojiConstants.MANAGE + " MANAGE GENRES",
          "1. " + EmojiConstants.MOVIES + " List all genres",
          "2. " + EmojiConstants.ADD + " Add new genre",
          "3. \uD83D\uDCDD Edit genre",   // 📝
          "4. \uD83D\uDDD1\uFE0F Delete genre", // 🗑️
          "0. " + EmojiConstants.LOGOUT + " Back to Main Menu"
      );

      String choice = scanner.nextLine().trim();

      switch (choice) {
        case "1" -> listGenres();
        case "2" -> createGenre();
        case "3" -> editGenre();
        case "4" -> deleteGenre();
        case "0" -> back = true;
        default -> System.out.println(EmojiConstants.ERROR + " Invalid choice!");
      }
    }
  }

  private void listGenres() {
    List<Genre> genres = genreService.getAll();

    JLineMenuRenderer.printHeader(EmojiConstants.MOVIES + " ALL GENRES");

    if (genres.isEmpty()) {
      System.out.println(EmojiConstants.WARNING + " No genres found.");
    } else {
      genres.forEach(g -> System.out.println(" • " + g.getName()));
    }
    System.out.println("\nPress Enter to continue...");
    scanner.nextLine();
  }

  private void createGenre() {
    System.out.print(EmojiConstants.POINT_RIGHT + " Enter genre name: ");
    String name = scanner.nextLine().trim();

    try {
      GenreStoreDto dto = new GenreStoreDto(name);
      genreService.create(dto);
      System.out.println(EmojiConstants.SUCCESS + " Success! Genre '" + name + "' created.");
    } catch (IllegalArgumentException e) {
      System.out.println(EmojiConstants.ERROR + " Error: " + e.getMessage());
    }
  }

  private void editGenre() {
    Genre genre = selectGenre("SELECT GENRE TO EDIT");
    if (genre == null) {
      return;
    }

    System.out.println(EmojiConstants.POINT_RIGHT + " Current name: " + genre.getName());
    System.out.print(EmojiConstants.POINT_RIGHT + " Enter new name: ");
    String newName = scanner.nextLine().trim();

    if (newName.isEmpty()) {
      System.out.println(EmojiConstants.WARNING + " Name cannot be empty.");
      return;
    }

    try {
      genreService.update(genre.getId(), new GenreUpdateDto(newName));
      System.out.println(EmojiConstants.SUCCESS + " Genre updated successfully.");
    } catch (IllegalArgumentException e) {
      System.out.println(EmojiConstants.ERROR + " Error updating genre: " + e.getMessage());
    }
  }

  private void deleteGenre() {
    Genre genre = selectGenre("SELECT GENRE TO DELETE");
    if (genre == null) {
      return;
    }

    System.out.print(EmojiConstants.WARNING + " Are you sure you want to delete '" + genre.getName()
        + "'? (y/n): ");
    String confirm = scanner.nextLine().trim();

    if (confirm.equalsIgnoreCase("y")) {
      try {
        genreService.delete(genre.getId());
        System.out.println(EmojiConstants.SUCCESS + " Genre deleted.");
      } catch (Exception e) {
        System.out.println(EmojiConstants.ERROR + " Error deleting genre: " + e.getMessage());
      }
    } else {
      System.out.println("Deletion cancelled.");
    }
  }

  private Genre selectGenre(String title) {
    List<Genre> genres = genreService.getAll();
    if (genres.isEmpty()) {
      System.out.println(EmojiConstants.WARNING + " No genres found.");
      return null;
    }

    String[] options = new String[genres.size() + 1];
    for (int i = 0; i < genres.size(); i++) {
      options[i] = String.format("%d. %s", (i + 1), genres.get(i).getName());
    }
    options[genres.size()] = "0. Cancel";

    JLineMenuRenderer.renderMenu(title, options);

    String input = scanner.nextLine().trim();
    if (input.equals("0")) {
      return null;
    }

    try {
      int index = Integer.parseInt(input) - 1;
      if (index >= 0 && index < genres.size()) {
        return genres.get(index);
      }
    } catch (NumberFormatException _) {
    }

    System.out.println(EmojiConstants.ERROR + " Invalid selection.");
    return null;
  }
}
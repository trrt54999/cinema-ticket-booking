package com.batko.cinematicketbooking.domain.data.core;

import java.io.File;

public final class StorageConfig {

  private static final String BASE_DIRECTORY = "database" + File.separator;

  public static final String USERS_FILE = BASE_DIRECTORY + "users.json";
  public static final String MOVIES_FILE = BASE_DIRECTORY + "movies.json";
  public static final String GENRES_FILE = BASE_DIRECTORY + "genres.json";
  public static final String MOVIE_GENRES_FILE = BASE_DIRECTORY + "movie_genres.json";
  public static final String HALLS_FILE = BASE_DIRECTORY + "halls.json";
  public static final String SEATS_FILE = BASE_DIRECTORY + "seats.json";
  public static final String SESSIONS_FILE = BASE_DIRECTORY + "sessions.json";
  public static final String TICKETS_FILE = BASE_DIRECTORY + "tickets.json";

  private StorageConfig() {
  }
}
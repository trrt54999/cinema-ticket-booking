package com.batko.cinematicketbooking.domain.data.core;

import com.batko.cinematicketbooking.domain.data.impl.json.JsonGenreRepository;
import com.batko.cinematicketbooking.domain.data.impl.json.JsonHallRepository;
import com.batko.cinematicketbooking.domain.data.impl.json.JsonMovieGenreRepository;
import com.batko.cinematicketbooking.domain.data.impl.json.JsonMovieRepository;
import com.batko.cinematicketbooking.domain.data.impl.json.JsonUserRepository;
import com.batko.cinematicketbooking.domain.data.impl.json.SeatJsonRepository;
import com.batko.cinematicketbooking.domain.data.impl.json.SessionJsonRepository;
import com.batko.cinematicketbooking.domain.data.impl.json.TicketJsonRepository;
import com.batko.cinematicketbooking.domain.data.repository.GenreRepository;
import com.batko.cinematicketbooking.domain.data.repository.HallRepository;
import com.batko.cinematicketbooking.domain.data.repository.MovieGenreRepository;
import com.batko.cinematicketbooking.domain.data.repository.MovieRepository;
import com.batko.cinematicketbooking.domain.data.repository.SeatRepository;
import com.batko.cinematicketbooking.domain.data.repository.SessionRepository;
import com.batko.cinematicketbooking.domain.data.repository.TicketRepository;
import com.batko.cinematicketbooking.domain.data.repository.UserRepository;

public class DataContext {

  private static UserRepository userRepository;
  private static MovieRepository movieRepository;
  private static GenreRepository genreRepository;
  private static MovieGenreRepository movieGenreRepository;
  private static HallRepository hallRepository;
  private static SeatRepository seatRepository;
  private static SessionRepository sessionRepository;
  private static TicketRepository ticketRepository;

  private DataContext() {
  }

  public static synchronized UserRepository getUserRepository() {
    if (userRepository == null) {
      userRepository = new JsonUserRepository(StorageConfig.USERS_FILE);
    }
    return userRepository;
  }

  public static synchronized MovieRepository getMovieRepository() {
    if (movieRepository == null) {
      movieRepository = new JsonMovieRepository(StorageConfig.MOVIES_FILE);
    }
    return movieRepository;
  }

  public static synchronized GenreRepository getGenreRepository() {
    if (genreRepository == null) {
      genreRepository = new JsonGenreRepository(StorageConfig.GENRES_FILE);
    }
    return genreRepository;
  }

  public static synchronized MovieGenreRepository getMovieGenreRepository() {
    if (movieGenreRepository == null) {
      movieGenreRepository = new JsonMovieGenreRepository(StorageConfig.MOVIE_GENRES_FILE);
    }
    return movieGenreRepository;
  }

  public static synchronized HallRepository getHallRepository() {
    if (hallRepository == null) {
      hallRepository = new JsonHallRepository(StorageConfig.HALLS_FILE);
    }
    return hallRepository;
  }

  public static synchronized SeatRepository getSeatRepository() {
    if (seatRepository == null) {
      seatRepository = new SeatJsonRepository(StorageConfig.SEATS_FILE);
    }
    return seatRepository;
  }

  public static synchronized SessionRepository getSessionRepository() {
    if (sessionRepository == null) {
      sessionRepository = new SessionJsonRepository(StorageConfig.SESSIONS_FILE);
    }
    return sessionRepository;
  }

  public static synchronized TicketRepository getTicketRepository() {
    if (ticketRepository == null) {
      ticketRepository = new TicketJsonRepository(StorageConfig.TICKETS_FILE);
    }
    return ticketRepository;
  }
}
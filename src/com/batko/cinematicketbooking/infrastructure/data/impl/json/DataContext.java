package com.batko.cinematicketbooking.infrastructure.data.impl.json;

import com.batko.cinematicketbooking.infrastructure.data.core.StorageConfig;
import com.batko.cinematicketbooking.infrastructure.data.repository.GenreRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.HallRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.MovieGenreRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.MovieRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.SeatRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.SessionRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.TicketRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.UserRepository;

public class DataContext {

  private UserRepository userRepository;
  private MovieRepository movieRepository;
  private GenreRepository genreRepository;
  private MovieGenreRepository movieGenreRepository;
  private HallRepository hallRepository;
  private SeatRepository seatRepository;
  private SessionRepository sessionRepository;
  private TicketRepository ticketRepository;

  private DataContext() {
  }

  public static DataContext getInstance() {
    return DataContextHolder.HOLDER_INSTANCE;
  }

  public synchronized UserRepository getUserRepository() {
    if (userRepository == null) {
      userRepository = new JsonUserRepository(StorageConfig.USERS_FILE);
    }
    return userRepository;
  }

  public synchronized MovieRepository getMovieRepository() {
    if (movieRepository == null) {
      movieRepository = new JsonMovieRepository(StorageConfig.MOVIES_FILE);
    }
    return movieRepository;
  }

  public synchronized GenreRepository getGenreRepository() {
    if (genreRepository == null) {
      genreRepository = new JsonGenreRepository(StorageConfig.GENRES_FILE);
    }
    return genreRepository;
  }

  public synchronized MovieGenreRepository getMovieGenreRepository() {
    if (movieGenreRepository == null) {
      movieGenreRepository = new JsonMovieGenreRepository(StorageConfig.MOVIE_GENRES_FILE);
    }
    return movieGenreRepository;
  }

  public synchronized HallRepository getHallRepository() {
    if (hallRepository == null) {
      hallRepository = new JsonHallRepository(StorageConfig.HALLS_FILE);
    }
    return hallRepository;
  }

  public synchronized SeatRepository getSeatRepository() {
    if (seatRepository == null) {
      seatRepository = new SeatJsonRepository(StorageConfig.SEATS_FILE);
    }
    return seatRepository;
  }

  public synchronized SessionRepository getSessionRepository() {
    if (sessionRepository == null) {
      sessionRepository = new SessionJsonRepository(StorageConfig.SESSIONS_FILE);
    }
    return sessionRepository;
  }

  public synchronized TicketRepository getTicketRepository() {
    if (ticketRepository == null) {
      ticketRepository = new TicketJsonRepository(StorageConfig.TICKETS_FILE);
    }
    return ticketRepository;
  }

  private static class DataContextHolder {

    public static final DataContext HOLDER_INSTANCE = new DataContext();
  }
}
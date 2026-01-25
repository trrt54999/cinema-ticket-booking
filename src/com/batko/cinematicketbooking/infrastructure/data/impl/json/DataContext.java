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
  
  private final UserRepository userRepository;
  private final MovieRepository movieRepository;
  private final GenreRepository genreRepository;
  private final MovieGenreRepository movieGenreRepository;
  private final HallRepository hallRepository;
  private final SeatRepository seatRepository;
  private final SessionRepository sessionRepository;
  private final TicketRepository ticketRepository;

  private DataContext() {
    this.userRepository = new JsonUserRepository(StorageConfig.USERS_FILE);
    this.movieRepository = new JsonMovieRepository(StorageConfig.MOVIES_FILE);
    this.genreRepository = new JsonGenreRepository(StorageConfig.GENRES_FILE);
    this.movieGenreRepository = new JsonMovieGenreRepository(StorageConfig.MOVIE_GENRES_FILE);
    this.hallRepository = new JsonHallRepository(StorageConfig.HALLS_FILE);
    this.seatRepository = new JsonSeatRepository(StorageConfig.SEATS_FILE);
    this.sessionRepository = new JsonSessionRepository(StorageConfig.SESSIONS_FILE);
    this.ticketRepository = new JsonTicketRepository(StorageConfig.TICKETS_FILE);
  }

  public static DataContext getInstance() {
    return DataContextHolder.HOLDER_INSTANCE;
  }

  public UserRepository getUserRepository() {
    return userRepository;
  }

  public MovieRepository getMovieRepository() {
    return movieRepository;
  }

  public GenreRepository getGenreRepository() {
    return genreRepository;
  }

  public MovieGenreRepository getMovieGenreRepository() {
    return movieGenreRepository;
  }

  public HallRepository getHallRepository() {
    return hallRepository;
  }

  public SeatRepository getSeatRepository() {
    return seatRepository;
  }

  public SessionRepository getSessionRepository() {
    return sessionRepository;
  }

  public TicketRepository getTicketRepository() {
    return ticketRepository;
  }

  private static class DataContextHolder {

    public static final DataContext HOLDER_INSTANCE = new DataContext();
  }
}
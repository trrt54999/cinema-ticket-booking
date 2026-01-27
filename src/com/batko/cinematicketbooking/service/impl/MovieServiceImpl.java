package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.domain.model.MovieGenre;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.repository.MovieGenreRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.MovieRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.SessionRepository;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.dto.movie.MovieStoreDto;
import com.batko.cinematicketbooking.service.dto.movie.MovieUpdateDto;
import java.util.List;
import java.util.UUID;

public class MovieServiceImpl implements MovieService {

  private final MovieRepository movieRepo;
  private final MovieGenreRepository movieGenreRepo;
  private final SessionRepository sessionRepo;

  private final UnitOfWork<Movie> movieUoW;
  private final UnitOfWork<MovieGenre> movieGenreUoW;

  public MovieServiceImpl(
      MovieRepository movieRepo,
      MovieGenreRepository movieGenreRepo,
      SessionRepository sessionRepo,
      UnitOfWork<Movie> movieUoW,
      UnitOfWork<MovieGenre> movieGenreUoW) {
    this.movieRepo = movieRepo;
    this.movieGenreRepo = movieGenreRepo;
    this.sessionRepo = sessionRepo;
    this.movieUoW = movieUoW;
    this.movieGenreUoW = movieGenreUoW;
  }

  @Override
  public Movie create(MovieStoreDto dto) {
    if (movieRepo.existsByTitle(dto.title())) {
      throw new IllegalArgumentException("Movie with title '" + dto.title() + "' already exists");
    }

    Movie movie = new Movie(
        dto.managerId(),
        dto.title(),
        dto.description(),
        dto.durationMinutes());
    movieUoW.registerNew(movie);
    movieUoW.commit();

    if (dto.genreIds() != null && !dto.genreIds().isEmpty()) {
      for (UUID genreId : dto.genreIds()) {
        MovieGenre link = new MovieGenre(movie.getId(), genreId);
        movieGenreUoW.registerNew(link);
      }
      movieGenreUoW.commit();
    }
    return movie;
  }

  @Override
  public Movie update(UUID movieId, MovieUpdateDto dto) {
    Movie movie = getById(movieId);
    boolean isDirty = false;

    if (dto.title() != null && !dto.title().equals(movie.getTitle())) {
      if (movieRepo.existsByTitle(dto.title())) {
        throw new IllegalArgumentException("Movie title already exists");
      }
      movie.setTitle(dto.title());
      isDirty = true;
    }

    if (dto.description() != null && !dto.description().equals(movie.getDescription())) {
      movie.setDescription(dto.description());
      isDirty = true;
    }

    if (dto.durationMinutes() != null && !dto.durationMinutes()
        .equals(movie.getDurationMinutes())) {
      movie.setDurationMinutes(dto.durationMinutes());
      isDirty = true;
    }

    if (isDirty) {
      movieUoW.registerDirty(movie);
      movieUoW.commit();
    }

    return movie;
  }

  @Override
  public void delete(UUID movieId) {
    if (!sessionRepo.findByMovieId(movieId).isEmpty()) {
      throw new IllegalStateException(
          "Cannot delete movie with active sessions. Delete sessions first.");
    }

    Movie movie = getById(movieId);

    List<MovieGenre> genres = movieGenreRepo.findByMovieId(movieId);
    for (MovieGenre genreLink : genres) {
      movieGenreUoW.registerDeleted(genreLink);
    }
    movieGenreUoW.commit();

    movieUoW.registerDeleted(movie);
    movieUoW.commit();
  }

  @Override
  public Movie getById(UUID id) {
    return movieRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Movie not found"));
  }

  @Override
  public List<Movie> getAll() {
    return movieRepo.findAll();
  }

  @Override
  public void addGenre(UUID movieId, UUID genreId) {
    boolean exists = movieGenreRepo.findByMovieId(movieId).stream()
        .anyMatch(mg -> mg.getGenreId().equals(genreId));

    if (exists) {
      throw new IllegalArgumentException("This movie already has this genre");
    }

    MovieGenre link = new MovieGenre(movieId, genreId);
    movieGenreUoW.registerNew(link);
    movieGenreUoW.commit();
  }

  @Override
  public void removeGenre(UUID movieId, UUID genreId) {
    MovieGenre link = movieGenreRepo.findByMovieId(movieId).stream()
        .filter(mg -> mg.getGenreId().equals(genreId))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Genre connection not found"));

    movieGenreUoW.registerDeleted(link);
    movieGenreUoW.commit();
  }
}
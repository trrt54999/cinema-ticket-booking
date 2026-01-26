package com.batko.cinematicketbooking.presentation;

import com.batko.cinematicketbooking.domain.model.Genre;
import com.batko.cinematicketbooking.domain.model.Hall;
import com.batko.cinematicketbooking.domain.model.Movie;
import com.batko.cinematicketbooking.domain.model.MovieGenre;
import com.batko.cinematicketbooking.domain.model.Seat;
import com.batko.cinematicketbooking.domain.model.Session;
import com.batko.cinematicketbooking.domain.model.Ticket;
import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.impl.json.DataContext;
import com.batko.cinematicketbooking.infrastructure.data.repository.GenreRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.HallRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.MovieGenreRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.MovieRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.SeatRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.SessionRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.TicketRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.UserRepository;
import com.batko.cinematicketbooking.infrastructure.email.EmailService;
import com.batko.cinematicketbooking.infrastructure.email.EmailServiceImpl;
import com.batko.cinematicketbooking.service.contract.AuthService;
import com.batko.cinematicketbooking.service.contract.GenreService;
import com.batko.cinematicketbooking.service.contract.HallService;
import com.batko.cinematicketbooking.service.contract.MovieGenreService;
import com.batko.cinematicketbooking.service.contract.MovieService;
import com.batko.cinematicketbooking.service.contract.SeatGeneratorService;
import com.batko.cinematicketbooking.service.contract.SeatService;
import com.batko.cinematicketbooking.service.contract.SessionService;
import com.batko.cinematicketbooking.service.contract.TicketService;
import com.batko.cinematicketbooking.service.contract.UserService;
import com.batko.cinematicketbooking.service.impl.AuthServiceImpl;
import com.batko.cinematicketbooking.service.impl.GenreServiceImpl;
import com.batko.cinematicketbooking.service.impl.HallServiceImpl;
import com.batko.cinematicketbooking.service.impl.MovieGenreServiceImpl;
import com.batko.cinematicketbooking.service.impl.MovieServiceImpl;
import com.batko.cinematicketbooking.service.impl.SeatGeneratorServiceImpl;
import com.batko.cinematicketbooking.service.impl.SeatServiceImpl;
import com.batko.cinematicketbooking.service.impl.SessionServiceImpl;
import com.batko.cinematicketbooking.service.impl.TicketServiceImpl;
import com.batko.cinematicketbooking.service.impl.UserServiceImpl;

public class Main {


  static void main(String[] args) {
    DataContext dataContext = DataContext.getInstance();

    UserRepository userRepository = dataContext.getUserRepository();
    UnitOfWork<User> userUoW = new UnitOfWork<>(userRepository);

    GenreRepository genreRepository = dataContext.getGenreRepository();
    UnitOfWork<Genre> genreUoW = new UnitOfWork<>(genreRepository);

    MovieRepository movieRepository = dataContext.getMovieRepository();
    UnitOfWork<Movie> movieUoW = new UnitOfWork<>(movieRepository);

    MovieGenreRepository movieGenreRepository = dataContext.getMovieGenreRepository();
    UnitOfWork<MovieGenre> movieGenreUoW = new UnitOfWork<>(movieGenreRepository);

    HallRepository hallRepository = dataContext.getHallRepository();
    UnitOfWork<Hall> hallUoW = new UnitOfWork<>(hallRepository);

    SessionRepository sessionRepository = dataContext.getSessionRepository();
    UnitOfWork<Session> sessionUoW = new UnitOfWork<>(sessionRepository);

    SeatRepository seatRepository = dataContext.getSeatRepository();
    UnitOfWork<Seat> seatUoW = new UnitOfWork<>(seatRepository);

    TicketRepository ticketRepository = dataContext.getTicketRepository();
    UnitOfWork<Ticket> ticketUoW = new UnitOfWork<>(ticketRepository);

    EmailService emailService = new EmailServiceImpl();
    AuthService authService = new AuthServiceImpl(userRepository, userUoW, emailService);
    GenreService genreService = new GenreServiceImpl(genreRepository, genreUoW);
    HallService hallService = new HallServiceImpl(hallRepository, hallUoW);
    MovieGenreService movieGenreService = new MovieGenreServiceImpl(movieGenreRepository,
        movieGenreUoW);
    MovieService movieService = new MovieServiceImpl(movieRepository, movieGenreRepository,
        movieUoW, movieGenreUoW);
    SeatService seatService = new SeatServiceImpl(seatRepository, hallRepository, seatUoW);
    SessionService sessionService = new SessionServiceImpl(sessionRepository, movieRepository,
        sessionUoW);
    TicketService ticketService = new TicketServiceImpl(ticketRepository, ticketUoW);
    UserService userService = new UserServiceImpl(userRepository, userUoW);
    SeatGeneratorService seatGeneratorService = new SeatGeneratorServiceImpl(seatService);

    AuthMenu authMenu = new AuthMenu(
        authService,
        genreService,
        movieService,
        sessionService,
        hallService,
        ticketService,
        seatService,
        seatGeneratorService
    );
    authMenu.run();
  }
}
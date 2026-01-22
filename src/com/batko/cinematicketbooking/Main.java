package com.batko.cinematicketbooking;

import com.batko.cinematicketbooking.domain.enums.UserRole;
import com.batko.cinematicketbooking.domain.model.Hall;
import com.batko.cinematicketbooking.domain.model.User;
import com.batko.cinematicketbooking.infrastructure.data.core.UnitOfWork;
import com.batko.cinematicketbooking.infrastructure.data.impl.json.DataContext;
import com.batko.cinematicketbooking.infrastructure.data.repository.HallRepository;
import com.batko.cinematicketbooking.infrastructure.data.repository.UserRepository;

public class Main {

  static void main(String[] args) {
    System.out.println("Hello world");

    DataContext context = DataContext.getInstance();

    HallRepository hallRepo = context.getHallRepository();
    UserRepository userRepo = context.getUserRepository();

    UnitOfWork<User> userUoW = new UnitOfWork<>(userRepo);
    UnitOfWork<Hall> hallUoW = new UnitOfWork<>(hallRepo);

    Hall h = new Hall("Cinema1", 3, 5);

    User u2 = new User("Lesyawe", "Ukrainka", "lesya@test.com", "pass", 28, UserRole.USER);
    User u3 = new User("Lesyawqe", "Ukrainka", "admin@test.com", "pass", 28, UserRole.USER);

    userUoW.registerNew(u2);
    userUoW.registerNew(u3);
    hallUoW.registerNew(h);
    System.out.println("Юзери додані в чергу (UoW), але файл ще пустий/старий.");

    // admin шукає в json, а його ще немає тому u3.setAge
    userRepo.findByEmail("admin@test.com").ifPresent(admin -> {
      admin.setAge(99);
      u3.setAge(99);
      userUoW.registerDirty(admin);
    });

    hallUoW.commit();
    userUoW.commit();

    System.out.println("Всі зміни записані у файл!");
  }
}
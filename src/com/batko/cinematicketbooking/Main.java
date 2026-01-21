package com.batko.cinematicketbooking;

import com.batko.cinematicketbooking.domain.data.JsonUserRepository;
import com.batko.cinematicketbooking.domain.data.UnitOfWork;
import com.batko.cinematicketbooking.domain.enums.UserRole;
import com.batko.cinematicketbooking.domain.model.User;

public class Main {

  static void main(String[] args) {
    System.out.println("Hello world");

    JsonUserRepository userRepo = new JsonUserRepository("users.json");
    UnitOfWork<User> userUoW = new UnitOfWork<>(userRepo, User::getId);

    User u2 = new User("Lesyawe", "Ukrainka", "lesya@test.com", "pass", 28, UserRole.USER);
    User u3 = new User("Lesyawqe", "Ukrainka", "admin@test.com", "pass", 28, UserRole.USER);

    userUoW.registerNew(u2);
    userUoW.registerNew(u3);
    System.out.println("Юзери додані в чергу (UoW), але файл ще пустий/старий.");

    // admin шукає в json, а його ще немає тому u3.setAge
    userRepo.findByEmail("admin@test.com").ifPresent(admin -> {
      admin.setAge(99);
      u3.setAge(99);
      userUoW.registerDirty(admin);
    });

    userUoW.commit();

    System.out.println("Всі зміни записані у файл!");
  }
}
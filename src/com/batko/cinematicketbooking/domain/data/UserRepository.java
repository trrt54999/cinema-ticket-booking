package com.batko.cinematicketbooking.domain.data;

import com.batko.cinematicketbooking.domain.enums.UserRole;
import com.batko.cinematicketbooking.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends Repository<User> {

  Optional<User> findByEmail(String email);

  List<User> findByLastName(String lastName);

  List<User> findByRole(UserRole role);

  boolean existsByEmail(String email);
}
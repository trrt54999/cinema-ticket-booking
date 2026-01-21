package com.batko.cinematicketbooking.domain.data.impl.json;

import com.batko.cinematicketbooking.domain.data.repository.UserRepository;
import com.batko.cinematicketbooking.domain.enums.UserRole;
import com.batko.cinematicketbooking.domain.model.User;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JsonUserRepository extends CachedJsonRepository<User> implements UserRepository {

  public JsonUserRepository(String filename) {
    super(
        filename,
        new TypeToken<ArrayList<User>>() {
        }.getType()
    );
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return findFirstBy(user ->
        user.getEmail().equalsIgnoreCase(email)
    );
  }

  @Override
  public List<User> findByLastName(String lastName) {
    return findBy(user ->
        user.getLastName().equalsIgnoreCase(lastName));
  }

  @Override
  public List<User> findByRole(UserRole role) {
    return findBy(user -> user.getRole() == role);
  }

  @Override
  public boolean existsByEmail(String email) {
    return findByEmail(email).isPresent();
  }
}
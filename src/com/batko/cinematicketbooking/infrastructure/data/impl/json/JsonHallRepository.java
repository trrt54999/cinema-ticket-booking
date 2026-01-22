package com.batko.cinematicketbooking.infrastructure.data.impl.json;

import com.batko.cinematicketbooking.domain.model.Hall;
import com.batko.cinematicketbooking.infrastructure.data.repository.HallRepository;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Optional;

public class JsonHallRepository extends CachedJsonRepository<Hall> implements HallRepository {

  JsonHallRepository(String filename) {
    super(
        filename,
        new TypeToken<ArrayList<Hall>>() {
        }.getType()
    );
  }


  @Override
  public Optional<Hall> findByName(String name) {
    return findFirstBy(hall -> hall.getName().equalsIgnoreCase(name));
  }
}
package com.batko.cinematicketbooking.domain.data.repository;

import com.batko.cinematicketbooking.domain.model.Hall;
import java.util.Optional;

public interface HallRepository extends Repository<Hall> {

  Optional<Hall> findByName(String name);
}
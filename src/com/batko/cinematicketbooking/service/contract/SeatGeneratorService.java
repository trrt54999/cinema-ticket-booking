package com.batko.cinematicketbooking.service.contract;

import com.batko.cinematicketbooking.domain.model.Hall;

@FunctionalInterface
public interface SeatGeneratorService {

  void generateSeatsForNewHall(Hall hall, int vipRowsCount);
}
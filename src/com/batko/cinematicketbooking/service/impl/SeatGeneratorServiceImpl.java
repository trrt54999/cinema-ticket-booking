package com.batko.cinematicketbooking.service.impl;

import com.batko.cinematicketbooking.domain.enums.SeatType;
import com.batko.cinematicketbooking.domain.model.Hall;
import com.batko.cinematicketbooking.service.contract.SeatGeneratorService;
import com.batko.cinematicketbooking.service.contract.SeatService;
import com.batko.cinematicketbooking.service.dto.seat.SeatStoreDto;

public class SeatGeneratorServiceImpl implements SeatGeneratorService {

  private final SeatService seatService;

  public SeatGeneratorServiceImpl(SeatService seatService) {
    this.seatService = seatService;
  }

  @Override
  public void generateSeatsForNewHall(Hall hall, int vipRowsCount) {
    int count = 0;
    int vipStartRow = hall.getRows() - vipRowsCount;

    for (int r = 1; r <= hall.getRows(); r++) {
      SeatType type = (r > vipStartRow) ? SeatType.VIP : SeatType.DEFAULT;

      for (int n = 1; n <= hall.getSeatsPerRow(); n++) {
        try {
          SeatStoreDto seatDto = new SeatStoreDto(hall.getId(), r, n, type);
          seatService.create(seatDto);
          count++;
        } catch (Exception e) {
          System.out.println("Error creating seat: " + e.getMessage());
        }
      }
    }
    System.out.println("Successfully generated " + count + " seats for " + hall.getName() +
        " (" + vipRowsCount + " VIP rows).");
  }
}
package com.batko.cinematicketbooking.service.dto.session;

import java.time.LocalDateTime;
import java.util.UUID;

public record SessionUpdateDto(UUID hallId, UUID movieId, Integer price, LocalDateTime startTime) {

}
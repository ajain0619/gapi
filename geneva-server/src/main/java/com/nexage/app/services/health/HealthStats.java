package com.nexage.app.services.health;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class HealthStats {

  String name;
  boolean alive;
  long millis;
}

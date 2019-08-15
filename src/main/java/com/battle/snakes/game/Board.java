package com.battle.snakes.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board {

  private Integer height;
  private Integer width;
  private List<Coordinate> food;
  private List<Snake> snakes;
}

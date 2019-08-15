package com.battle.snakes.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndRequest {

  private Game game;
  private Integer turn;
  private Board board;
  private Snake you;
}

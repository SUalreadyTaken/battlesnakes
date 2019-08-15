
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
public class Snake {

  private String id;
  private String name;
  private Integer health;
  private List<Coordinate> body;
}

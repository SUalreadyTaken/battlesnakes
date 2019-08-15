
package com.battle.snakes.game;

public enum MoveType {

  UP("up"),
  DOWN("down"),
  LEFT("left"),
  RIGHT("right");

  private String move;

  MoveType(String move) {
    this.move = move;
  }

  public String getValue() {
    return move.toLowerCase();
  }
}

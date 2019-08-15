package com.battle.snakes.game;

public enum HeadType {

  BELUGA("beluga"),
  BENDR("bendr"),
  DEAD("dead"),
  EVIL("evil"),
  FANG("fang"),
  PIXEL("pixel"),
  REGULAR("regular"),
  SAFE("safe"),
  SAND_WORM("sand-worm"),
  SHADES("shades"),
  SILLY("silly"),
  SMILE("smile"),
  TONGUE("tongue");

  private String name;

  HeadType(String name) {
    this.name = name;
  }

  public String getValue() {
    return name.toLowerCase();
  }
}

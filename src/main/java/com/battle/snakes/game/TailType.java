
package com.battle.snakes.game;

public enum TailType {

  BLOCKBUM("block-bum"),
  BOLT("bolt"),
  CURLED("curled"),
  FAT_RATTLE("fat-rattle"),
  FRECKLED("freckled"),
  HOOK("hook"),
  PIXEL("pixel"),
  REGULAR("regular"),
  ROUNDBUM("round-bum"),
  SHARP("sharp"),
  SKINNY("skinny"),
  SMALL_RATTLE("small-rattle");

  private String tail;

  TailType(String tail) {
    this.tail = tail;
  }

  public String getValue() {
    return tail.toLowerCase();
  }
}

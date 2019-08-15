package com.battle.snakes.model;

import com.battle.snakes.game.MoveType;

public class MoveAndMovesCount {

    private int moveCount;

    private MoveType moveType;

    public MoveAndMovesCount(int moveCount, MoveType moveType) {
        this.moveCount = moveCount;
        this.moveType = moveType;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int leastMoves) {
        this.moveCount = leastMoves;
    }

    public MoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }
}

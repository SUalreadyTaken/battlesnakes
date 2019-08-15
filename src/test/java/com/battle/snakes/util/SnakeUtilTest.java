package com.battle.snakes.util;

import com.battle.snakes.game.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SnakeUtilTest {

  //////////////////////////////////////////
  /*   DO NOT EDIT BELOW THIS LINE   */
  //////////////////////////////////////////

  static final int BOARD_WIDTH = 15;
  static final int BOARD_HEIGHT = 15;

  @Test
  void getNearestMoveToTarget() {
    Coordinate target = createCoordinate(3, 4);
    List<MoveType> moves = new ArrayList<>(EnumSet.allOf(MoveType.class));

    assertEquals(MoveType.DOWN, SnakeUtil.getNearestMoveToTarget(target, createCoordinate(3, 3), moves));
    assertEquals(MoveType.UP, SnakeUtil.getNearestMoveToTarget(target, createCoordinate(3, 5), moves));
    assertEquals(MoveType.LEFT, SnakeUtil.getNearestMoveToTarget(target, createCoordinate(4, 4), moves));
    assertEquals(MoveType.RIGHT, SnakeUtil.getNearestMoveToTarget(target, createCoordinate(2, 4), moves));
  }

  @Test
  void getNearestCoordinateToTarget() {

    Coordinate expected = createCoordinate(5, 5);
    Coordinate target = createCoordinate(0, 0);

    List<Coordinate> coordinates = new ArrayList<>();
    coordinates.add(expected);
    coordinates.add(createCoordinate(5, 6));
    coordinates.add(createCoordinate(6, 6));

    Coordinate actual = SnakeUtil.getNearestCoordinateToTarget(target, coordinates);

    assertEquals(expected, actual);
  }

  @Test
  void getDistance() {

    Coordinate start = createCoordinate(0, 0);

    assertEquals(0, SnakeUtil.getDistance(start, start));
    assertEquals(2.23606797749979, SnakeUtil.getDistance(start, createCoordinate(1, 2)));
    assertEquals(4.123105625617661, SnakeUtil.getDistance(start, createCoordinate(1, 4)));
  }

  @Test
  void isInBounds() {

    Coordinate coordinate = createCoordinate(1, 2);

    assertTrue(SnakeUtil.isInBounds(createBoard(2, 3), coordinate));
    assertFalse(SnakeUtil.isInBounds(createBoard(1, 2), coordinate));
    assertFalse(SnakeUtil.isInBounds(createBoard(2, 1), coordinate));
    assertFalse(SnakeUtil.isInBounds(createBoard(-1, 0), coordinate));
    assertFalse(SnakeUtil.isInBounds(createBoard(0, -1), coordinate));
  }

  @Test
  void getNextMoveCoords() {

    Coordinate head = createCoordinate(1, 1);

    Coordinate down = SnakeUtil.getNextMoveCoords(MoveType.DOWN, head);
    Coordinate up = SnakeUtil.getNextMoveCoords(MoveType.UP, head);
    Coordinate left = SnakeUtil.getNextMoveCoords(MoveType.LEFT, head);
    Coordinate right = SnakeUtil.getNextMoveCoords(MoveType.RIGHT, head);

    assertEquals(down, createCoordinate(1, 2));
    assertEquals(up, createCoordinate(1, 0));
    assertEquals(left, createCoordinate(0, 1));
    assertEquals(right, createCoordinate(2, 1));
  }

  @Test
  void getAllowedMoves_whenSnakeInBounds() {

    List<MoveType> expected = new ArrayList<>(EnumSet.allOf(MoveType.class));
    Snake snake = createSnake(5, 5);
    List<MoveType> moves = SnakeUtil.getAllowedMoves(createMoveRequestWithSnake(snake, BOARD_WIDTH, BOARD_HEIGHT));

    assertTrue(expected.containsAll(moves));
  }

  @Test
  void getAllowedMoves_whenSnakeInLeftTopCorner() {

    List<MoveType> expected = new ArrayList<>();
    expected.add(MoveType.DOWN);
    expected.add(MoveType.RIGHT);
    Snake snake = createSnake(0, 0);

    MoveRequest request = createMoveRequestWithSnake(snake, BOARD_WIDTH, BOARD_HEIGHT);
    List<MoveType> moves = SnakeUtil.getAllowedMoves(request);

    assertTrue(moves.containsAll(expected));
  }

  @Test
  void getAllowedMoves_whenSnakeInRightTopCorner() {

    List<MoveType> expected = new ArrayList<>();
    expected.add(MoveType.DOWN);
    expected.add(MoveType.LEFT);
    Snake snake = createSnake(BOARD_WIDTH - 1, 0);

    MoveRequest actual = createMoveRequestWithSnake(snake, BOARD_WIDTH, BOARD_HEIGHT);
    List<MoveType> moves = SnakeUtil.getAllowedMoves(actual);

    assertTrue(moves.containsAll(expected));
  }

  @Test
  void getAllowedMoves_whenSnakeInLeftBottomCorner() {

    List<MoveType> expected = new ArrayList<>();
    expected.add(MoveType.UP);
    expected.add(MoveType.RIGHT);
    Snake snake = createSnake(0, BOARD_HEIGHT - 1);

    MoveRequest actual = createMoveRequestWithSnake(snake, BOARD_WIDTH, BOARD_HEIGHT);
    List<MoveType> moves = SnakeUtil.getAllowedMoves(actual);

    assertTrue(moves.containsAll(expected));
  }

  @Test
  void getAllowedMoves_whenSnakeInRightBottomCorner() {

    List<MoveType> expected = new ArrayList<>();
    expected.add(MoveType.LEFT);
    expected.add(MoveType.UP);
    Snake snake = createSnake(BOARD_WIDTH - 1, BOARD_HEIGHT - 1);

    MoveRequest actual = createMoveRequestWithSnake(snake, BOARD_WIDTH, BOARD_HEIGHT);
    List<MoveType> moves = SnakeUtil.getAllowedMoves(actual);

    assertTrue(moves.containsAll(expected));
  }

  @Test
  void getAllowedMoves_whenSnakeSurroundedWithBodySegments() {

    List<MoveType> expected = new ArrayList<>();
    expected.add(MoveType.DOWN);
    List<Coordinate> body = new ArrayList<>();
    body.add(createCoordinate(0,0));
    body.add(createCoordinate(1,0));

    Snake snake = createSnake(body);

    MoveRequest actual = createMoveRequestWithSnake(snake, BOARD_WIDTH, BOARD_HEIGHT);
    List<MoveType> moves = SnakeUtil.getAllowedMoves(actual);

    assertTrue(moves.containsAll(expected));
  }

  private MoveRequest createMoveRequest(int width, int height) {

    Game game = Game.builder()
      .id("3e02b354-ae29-4c3e-8c5b-26a04c764f8c")
      .build();

    List<Coordinate> food = new ArrayList<>();
    food.add(Coordinate.builder()
      .x(2)
      .y(4)
      .build()
    );

    Snake snake = createSnake(8, 10);

    List<Snake> snakes = new ArrayList<>();
    snakes.add(snake);

    Board board = Board.builder()
      .width(width)
      .height(height)
      .food(food)
      .snakes(snakes)
      .build();

    return MoveRequest.builder()
      .game(game)
      .board(board)
      .turn(1)
      .you(createSnake(5, 5))
      .build();
  }

  private Snake createSnake(int x, int y) {

    List<Coordinate> body = new ArrayList<>();
    body.add(Coordinate.builder()
      .x(x)
      .y(y)
      .build());

    return Snake.builder()
      .body(body)
      .build();
  }

  private Snake createSnake(List<Coordinate> body) {

    return Snake.builder()
      .body(body)
      .build();
  }

  private Board createBoard(int width, int height) {

    return Board.builder()
      .width(width)
      .height(height)
      .build();
  }

  private Coordinate createCoordinate(int x, int y) {

    return Coordinate.builder()
      .x(x)
      .y(y)
      .build();
  }

  private MoveRequest createMoveRequestWithSnake(Snake snake, int width, int height) {

    MoveRequest request = createMoveRequest(width, height);
    request.setYou(snake);

    return request;
  }
}

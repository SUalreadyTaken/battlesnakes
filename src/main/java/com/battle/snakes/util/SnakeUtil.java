package com.battle.snakes.util;


import com.battle.snakes.game.*;
import com.battle.snakes.model.MoveAndMovesCount;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class SnakeUtil {

    private static final Random RANDOM = new Random();

    private static List<MoveType> allMoves = new ArrayList<>(EnumSet.allOf(MoveType.class));

    static boolean isInBounds(Board board, Coordinate coordinate) {
        return board.getWidth() > coordinate.getX()
                && board.getHeight() > coordinate.getY()
                && coordinate.getX() >= 0
                && coordinate.getY() >= 0;
    }

    public static Coordinate getNextMoveCoords(MoveType moveType, Coordinate start) {
        int x = start.getX();
        int y = start.getY();
        switch (moveType) {
            case UP:
                y -= 1;
                break;
            case DOWN:
                y += 1;
                break;
            case LEFT:
                x -= 1;
                break;
            case RIGHT:
                x += 1;
                break;
        }

        return Coordinate
                .builder()
                .x(x)
                .y(y)
                .build();
    }

    public static List<MoveType> getAllowedMoves(MoveRequest request) {

        Snake mySnake = request.getYou();
        Board board = request.getBoard();
        return allowedMoves(getOtherSnakes(mySnake, board), request.getYou().getBody(), board);
    }


    public static List<MoveType> getAllowedMovesForGenius(MoveRequest request) {

        Snake mySnake = request.getYou();
        Board board = request.getBoard();
        return allowedMovesForGenius(getOtherSnakes(mySnake, board), request.getYou().getBody(), board);
    }

    private static List<Snake> getOtherSnakes(Snake mySnake, Board board) {
        List<Snake> otherSnakes = board.getSnakes();
        otherSnakes.remove(mySnake);
        return otherSnakes;
    }

    static double getDistance(Coordinate first, Coordinate second) {
        return Math.sqrt(Math.pow(second.getX() - first.getX(), 2) + Math.pow(second.getY() - first.getY(), 2));
    }

    public static MoveType getNearestMoveToTarget(Coordinate nearestFood, Coordinate head, List<MoveType> allowedMoves) {
        double currentDistance = getDistance(head, nearestFood);
        MoveType bestMove = allowedMoves.get(0);
        double smallestDistance = getDistance((getNextMoveCoords(bestMove, head)), nearestFood);
        for (MoveType move : allowedMoves) {
            double moveDistance = getDistance(getNextMoveCoords(move, head), nearestFood);
            if (moveDistance < currentDistance && moveDistance < smallestDistance) {
                bestMove = move;
                smallestDistance = moveDistance;
            }
        }
        return bestMove;
    }

    public static Coordinate getNearestCoordinateToTarget(Coordinate head, List<Coordinate> foods) {
        Coordinate smallest = foods.get(0);
        double smallestDistance = getDistance(foods.get(0), head);
        for (Coordinate food : foods) {
            double tmpDistance = getDistance(food, head);
            if (tmpDistance < smallestDistance) {
                smallestDistance = tmpDistance;
                smallest = food;
            }

        }
        return smallest;
    }

    private static List<MoveType> allowedMoves(List<Snake> otherSnakes, List<Coordinate> mySnake, Board board) {

        List<MoveType> allowedMoves = new ArrayList<>();

        Coordinate lastLocation = mySnake.get(0);

        List<Coordinate> notAllowedCoordinates = new ArrayList<>();

        for (Snake snake : otherSnakes) {
            notAllowedCoordinates.addAll(snake.getBody());

        }

        return getAllowedMoveTypes(mySnake, board, allowedMoves, lastLocation, notAllowedCoordinates);
    }


    public static List<MoveType> allowedMovesForGenius(List<Snake> otherSnakes, List<Coordinate> mySnake, Board board) {

        List<MoveType> allowedMoves = new ArrayList<>();

        Coordinate lastLocation = mySnake.get(0);

        List<Coordinate> notAllowedCoordinates = new ArrayList<>();

        for (Snake snake : otherSnakes) {
            // only 1 enemy... then add only bigger head movements
            if (otherSnakes.size() == 1) {
                if (snake.getBody().size() >= mySnake.size()) {
                    for (MoveType enemyMove : allMoves) {
                        Coordinate possibleMove = getNextMoveCoords(enemyMove, snake.getBody().get(0));
                        notAllowedCoordinates.add(possibleMove);
                    }
                }
            } else {
                // more than 1 enemy.. then add equal and bigger head movements
                // equal means both of us will die and none of us wins
                if (snake.getBody().size() > mySnake.size()) {
                    for (MoveType enemyMove : allMoves) {
                        Coordinate possibleMove = getNextMoveCoords(enemyMove, snake.getBody().get(0));
                        notAllowedCoordinates.add(possibleMove);
                    }
                }
            }
            notAllowedCoordinates.addAll(snake.getBody());

        }

        return getAllowedMoveTypes(mySnake, board, allowedMoves, lastLocation, notAllowedCoordinates);
    }

    private static List<MoveType> getAllowedMoveTypes(List<Coordinate> mySnake, Board board, List<MoveType> allowedMoves, Coordinate lastLocation, List<Coordinate> notAllowedCoordinates) {
        notAllowedCoordinates.addAll(mySnake);
        notAllowedCoordinates.remove(notAllowedCoordinates.size() - 1);

        for (MoveType move : allMoves) {
            Coordinate nextMove = getNextMoveCoords(move, lastLocation);
            if (isInBounds(board, nextMove) && !notAllowedCoordinates.contains(nextMove)) {
                allowedMoves.add(move);
            }
        }
        return allowedMoves;
    }

    public static List<Coordinate> foodsCloseToEnemy(List<Snake> otherSnakes, List<Coordinate> food, List<Coordinate> myBody) {

        for (Snake otherSnake : otherSnakes) {
            for (MoveType move : allMoves) {
                Coordinate nextMove = getNextMoveCoords(move, otherSnake.getBody().get(0));
                if (otherSnake.getBody().size() + 1 > myBody.size()) {
                    food.remove(nextMove);
                }
            }
        }

        return food;
    }

    public static List<MoveType> killMoves(List<Coordinate> mySnake, List<Snake> otherSnakes, Board board, List<MoveType> myMoves) {

        List<Coordinate> weakerEnemyMoves = new ArrayList<>();
        List<MoveType> result = new ArrayList<>();

        for (Snake enemy : otherSnakes) {
            if (enemy.getBody().size() < mySnake.size()) {
                List<MoveType> enemyMoves = SnakeUtil.allowedMoves(otherSnakes, enemy.getBody(), board);
                for (MoveType move : enemyMoves) {
                    weakerEnemyMoves.add(getNextMoveCoords(move, enemy.getBody().get(0)));
                }
            }
        }

        for (MoveType myPossibleMoves : myMoves) {
            if (weakerEnemyMoves.contains(getNextMoveCoords(myPossibleMoves, mySnake.get(0)))) {
                result.add(myPossibleMoves);
            }
        }


        return result;
    }

    public static boolean canMoveToTheFoodWithThisMove(MoveType moveType, Coordinate nearestFood, List<Coordinate> body,
                                                       List<Snake> otherSnakes, Board board, int calc, int currentMove) {

        Coordinate newHead = getNextMoveCoords(moveType, body.get(0));
        List<Coordinate> newBody = createNewBody(board.getFood(), newHead, body);

        if (newHead.equals(nearestFood)) {
            //reached food return true;
            return true;
        }

        calc--;
        if (calc <= 0) {
            System.out.println("Too Many Calcs canMoveToTheFoodWithThisMove");
            return false;
        }

        List<Snake> newEnemySnakes = new ArrayList<>();
        List<Snake> modifiedOtherSnakes = getOtherSnakesWithMyBody(otherSnakes, newBody);

        for (int i = 0; i < modifiedOtherSnakes.size() - 1; i++) {
            List<MoveType> allowedMoves = allowedMoves(modifiedOtherSnakes, modifiedOtherSnakes.get(i).getBody(), board);
            if (!allowedMoves.isEmpty()) {
                Coordinate enemyHead = modifiedOtherSnakes.get(i).getBody().get(0);
                MoveType nearestMove = getNearestMoveToTarget(nearestFood, enemyHead, allowedMoves);
                Coordinate enemyNewHead = getNextMoveCoords(nearestMove, enemyHead);
                if (nearestFood.equals(enemyNewHead)) {
                    // i moved first didnt get to food..
                    // but enemy did so that food is take .. return false
                    // wont be a problem exiting for loop anyway
                    //modifiedOtherSnakes.remove(mySnake);
                    return false;
                }
                newSnakeToList(newEnemySnakes, enemyNewHead, modifiedOtherSnakes.get(i));
            } else {
                //no allowed moves add old body
                newEnemySnakes.add(modifiedOtherSnakes.get(i));
            }
        }

        List<MoveType> newAllowedMoves = allowedMovesForGenius(newEnemySnakes, newBody, board);

        if (newAllowedMoves.isEmpty()) {
            // cant reach to food
            return false;
        }

        currentMove++;
        MoveType newMove = getNearestMoveToTarget(nearestFood, newHead, newAllowedMoves);
        return canMoveToTheFoodWithThisMove(newMove, nearestFood, newBody, newEnemySnakes, board, calc, currentMove);

    }

    //im able to move to the food
    public static int moveToFood(Coordinate nearestFood, MoveType move, List<Snake> otherSnakes, List<Coordinate> myBody,
                                 Board board, int leastMoves) {

        leastMoves++;
        Coordinate newHead = getNextMoveCoords(move, myBody.get(0));
        List<Coordinate> newBody = createNewBody(board.getFood(), newHead, myBody);

        if (newHead.equals(nearestFood)) {
            //reached food return moveCount;
            return leastMoves;
        }

        List<Snake> newEnemySnakes = new ArrayList<>();
        // so that enemies can consider my body too
        List<Snake> modifiedOtherSnakes = getOtherSnakesWithMyBody(otherSnakes, newBody);

        for (int i = 0; i < modifiedOtherSnakes.size() - 1; i++) {
            List<MoveType> allowedMoves = allowedMoves(modifiedOtherSnakes, modifiedOtherSnakes.get(i).getBody(), board);
            if (!allowedMoves.isEmpty()) {
                Coordinate enemyHead = modifiedOtherSnakes.get(i).getBody().get(0);
                MoveType nearestMove = getNearestMoveToTarget(nearestFood, enemyHead, allowedMoves);
                Coordinate enemyNewHead = getNextMoveCoords(nearestMove, enemyHead);
                if (nearestFood.equals(enemyNewHead)) {
                    log.error("Should never reach here");
                }
                newSnakeToList(newEnemySnakes, enemyNewHead, modifiedOtherSnakes.get(i));
            } else {
                //no allowed moves add old body
                newEnemySnakes.add(modifiedOtherSnakes.get(i));
            }
        }

        List<MoveType> newAllowedMoves = allowedMovesForGenius(newEnemySnakes, newBody, board);

        if (newAllowedMoves.isEmpty()) {
            // should not reach.. just in case
            log.error("Should not reach");
            return 10000;
        }

        MoveType newMove = getNearestMoveToTarget(nearestFood, newHead, newAllowedMoves);

        return moveToFood(nearestFood, newMove, newEnemySnakes, newBody, board, leastMoves);

    }

    public static boolean canMoveThisManyMoves(int neededMoves, int currentMove, List<Snake> otherSnakes, List<Coordinate> body,
                                               Board board, List<Coordinate> foodList) {

        if (currentMove >= neededMoves) {
            return true;
        }

        //others 1st

        List<MoveType> myAllowedMoves = allowedMovesForGenius(otherSnakes, body, board);
        if (myAllowedMoves.isEmpty()) {
            return false;
        }

        currentMove++;

        for (MoveType move : myAllowedMoves) {
            Coordinate newHead = getNextMoveCoords(move, body.get(0));
            List<Coordinate> newBody = createNewBody(board.getFood(), newHead, body);
            List<Snake> newSnakes = enemySnakesMoved(otherSnakes, body, board, foodList);
            if (canMoveThisManyMoves(neededMoves, currentMove, newSnakes, newBody, board, foodList)) {
                return true;
            }
        }

        return false;
    }


    public static int evadeMoveCount(MoveType move, List<Snake> otherSnakes, List<Coordinate> body,
                                     Board board, List<Coordinate> foodList, int moveCount) {

        moveCount++;


        if ((body.size() * 2) < moveCount) {
            return moveCount;
        }

        Coordinate newHead = getNextMoveCoords(move, body.get(0));
        List<Coordinate> newBody;

        if (!foodList.isEmpty()) {
            newBody = createNewBody(foodList, newHead, body);
        } else {
            newBody = createNewBody(Collections.emptyList(), newHead, body);
        }

        List<Snake> newSnakes = enemySnakesMoved(otherSnakes, body, board, foodList);

        List<MoveType> myNewAllowedMoves = allowedMovesForGenius(newSnakes, newBody, board);

        if (myNewAllowedMoves.isEmpty()) {
            return moveCount;
        }

        int tmpMoveCount = moveCount;
        List<MoveAndMovesCount> mostMovesList = new ArrayList<>();
        for (MoveType moveType : myNewAllowedMoves) {
            int bestMoveCount = evadeMoveCount(moveType, newSnakes, newBody, board, foodList, tmpMoveCount);
            if (bestMoveCount > (newBody.size() * 2)) {
                return bestMoveCount;
            } else {
                //create model and list of best
                MoveAndMovesCount mostMoves = new MoveAndMovesCount(bestMoveCount, moveType);
                mostMovesList.add(mostMoves);
            }
        }

        mostMovesList.sort(Comparator.comparing(MoveAndMovesCount::getMoveCount).reversed());

        return mostMovesList.get(0).getMoveCount();
    }

    public static List<Snake> enemySnakesMoved(List<Snake> enemySnakes, List<Coordinate> myBody, Board board, List<Coordinate> foodList) {
        List<Snake> tmpOtherSnake = getOtherSnakesWithMyBody(enemySnakes, myBody);
        List<Snake> newSnakes = new ArrayList<>();
//        List<Coordinate> tmpFood = new ArrayList<>(foodList);
        for (int i = 0; i < tmpOtherSnake.size() - 1; i++) {
            Snake enemy = tmpOtherSnake.get(i);
            Coordinate oldEnemyHead = enemy.getBody().get(0);
            List<MoveType> allowedMoves = allowedMoves(tmpOtherSnake, enemy.getBody(), board);
            if (!allowedMoves.isEmpty()) {
                if (!foodList.isEmpty()) {
                    Coordinate nearestFood = getNearestCoordinateToTarget(oldEnemyHead, foodList);
                    MoveType enemyMove = getNearestMoveToTarget(nearestFood, oldEnemyHead, allowedMoves);
                    Coordinate newEnemyHead = getNextMoveCoords(enemyMove, oldEnemyHead);
                    List<Coordinate> newEnemyBody = createNewBody(foodList, newEnemyHead, enemy.getBody());
                    //tmpFood.removeIf(newEnemyBody::contains);
                    Snake newEnemySnake = new Snake();
                    newEnemySnake.setBody(newEnemyBody);
                    newSnakes.add(newEnemySnake);
                } else {
                    // go for my head
                    Coordinate myHead = myBody.get(0);
                    MoveType enemyMove = getNearestMoveToTarget(myHead, oldEnemyHead, allowedMoves);
                    Coordinate newEnemyHead = getNextMoveCoords(enemyMove, oldEnemyHead);
                    newSnakeToList(newSnakes, newEnemyHead, enemy);
                }
            } else {
                //add just old enemy
                newSnakes.add(enemy);
            }
        }
        return newSnakes;
    }

    private static void newSnakeToList(List<Snake> newSnakeList, Coordinate enemyNewHead, Snake snake) {
        //foodList doesnt matter here
        List<Coordinate> enemyNewBody = createNewBody(Collections.emptyList(), enemyNewHead, snake.getBody());
        Snake newEnemySnake = new Snake();
        newEnemySnake.setBody(enemyNewBody);
        newSnakeList.add(newEnemySnake);
    }

    private static List<Snake> getOtherSnakesWithMyBody(List<Snake> otherSnakes, List<Coordinate> newBody) {
        List<Snake> modifiedOtherSnakes = new ArrayList<>(otherSnakes);
        Snake mySnake = new Snake();
        mySnake.setBody(newBody);
        modifiedOtherSnakes.add(mySnake);
        return modifiedOtherSnakes;
    }

    public static List<Coordinate> createNewBody(List<Coordinate> foodList, Coordinate newHead, List<Coordinate> oldBody) {
        List<Coordinate> newBody = new ArrayList<>();
        newBody.add(newHead);
        if (!foodList.isEmpty()) {
            if (foodList.contains(newHead)) {
                newBody.addAll(oldBody);
                foodList.remove(newHead);
            } else {
                for (int i = 0; i < oldBody.size() - 1; i++) {
                    newBody.add(oldBody.get(i));
                }
            }
        } else {
            for (int i = 0; i < oldBody.size() - 1; i++) {
                newBody.add(oldBody.get(i));
            }
        }
        return newBody;
    }

}

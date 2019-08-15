package com.battle.snakes;

import com.battle.snakes.game.*;
import com.battle.snakes.model.MoveAndMovesCount;
import com.battle.snakes.util.SnakeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/mad-genius")
public class GeniusSnakeController extends BaseController {

    @RequestMapping(value = "/start", method = RequestMethod.POST, produces = "application/json")
    public StartResponse start(@RequestBody StartRequest request) {

        log.info(request.toString());

        return StartResponse.builder()
                .color("#a5031b")
                .headType(HeadType.FANG.getValue())
                .tailType(TailType.FRECKLED.getValue())
                .build();
    }

    @RequestMapping(value = "/end", method = RequestMethod.POST)
    public Object end(@RequestBody EndRequest request) {

        log.info(request.toString());
        if (request.getBoard().getSnakes().size() > 0) {
            if (request.getYou().getId().equals(request.getBoard().getSnakes().get(0).getId())) {
                System.out.println("WIN");
            } else {
                System.out.println("LOSS");
            }
        } else {
            System.out.println("DRAW");
        }

        return new HashMap<String, Object>();
    }

    @RequestMapping(value = "/move", method = RequestMethod.POST, produces = "application/json")
    public MoveResponse move(@RequestBody MoveRequest request) {

        log.info("Turn >> " + request.getTurn().toString() + " Name >> " + request.getYou().getName() + " head >> " +
                request.getYou().getBody().get(0) + " mySize >> " + request.getYou().getBody().size() + " board >> " + request.getBoard());

        long start = System.currentTimeMillis();

        List<MoveType> moves = SnakeUtil.getAllowedMovesForGenius(request);
        final List<Coordinate> body = Collections.unmodifiableList(request.getYou().getBody());


        final Board board = request.getBoard();

        List<Snake> tmpOtherSnakes = board.getSnakes();
        tmpOtherSnakes.remove(request.getYou());
        final List<Snake> otherSnakes = Collections.unmodifiableList(tmpOtherSnakes);

        for (Snake otherSnake : otherSnakes) {
            System.out.println("Enemy name >> " + otherSnake.getName() + " | size >> " + otherSnake.getBody().size());
        }

        // genius moves empty
        if (moves.isEmpty()) {
            moves = SnakeUtil.getAllowedMoves(request);
            System.out.println("No genius moves taking default");
            if (moves.isEmpty()) {
                System.out.println("Fked going UP");
                System.out.println("Time >> " + totalTime(start));
                return MoveResponse.builder()
                        .move(MoveType.UP.getValue())
                        .build();
            }
        }

        // if 1 move check if i can go body size
        // if no take allowed moves
        // and get the most moves and return it

        if (moves.size() == 1) {
            System.out.println("Only 1 move left");
            // check if i can survive with it
            // if not take allowedMoves
            System.out.println("Time >> " + totalTime(start));
            return MoveResponse.builder().move(moves.get(0).getValue()).build();
        }


        System.out.println("So from start my move choices >> " + moves);

        Coordinate head = body.get(0);

        // remove foods that are in reach of the enemy
        List<Coordinate> foodList = SnakeUtil.foodsCloseToEnemy(otherSnakes, request.getBoard().getFood(), body);

        List<MoveType> killMoves = SnakeUtil.killMoves(body, otherSnakes, board, moves);

        //go for kill
        if (!killMoves.isEmpty()) {
            // possible killer moves
            System.out.println("Possible killMoves");
            System.out.println("THe kill moves are >> " + killMoves.toString());

            List<MoveAndMovesCount> mostMovesList = new ArrayList<>();
            for (MoveType move : killMoves) {
                // newHead and newBody
                int mostMoves;
                //calc = 100;
                int bestMostAmount = 0;
                Coordinate newHeadCoordinate = SnakeUtil.getNextMoveCoords(move, body.get(0));
                List<Coordinate> tmpFoods = new ArrayList<>(board.getFood());
                List<Coordinate> newBody = SnakeUtil.createNewBody(tmpFoods, newHeadCoordinate, body);
                List<Snake> newEnemySnakes = SnakeUtil.enemySnakesMoved(otherSnakes, newBody, board, tmpFoods);
//                if (foodList.contains(newHeadCoordinate)) {
//                    newBody.add(newHeadCoordinate);
//                    newBody.addAll(body);
//                } else {
//                    newBody.add(newHeadCoordinate);
//                    for (int i = 0; i < body.size() - 1; i++) {
//                        newBody.add(body.get(i));
//                    }
//                }
                List<MoveType> newAllowedMoves = SnakeUtil.allowedMovesForGenius(newEnemySnakes, newBody, board);
                for (MoveType newMove : newAllowedMoves) {
                    //List<Coordinate> tmpFoods = new ArrayList<>(board.getFood());
                    mostMoves = SnakeUtil.evadeMoveCount(newMove, newEnemySnakes, body, board, tmpFoods, 0);
                    if (mostMoves > bestMostAmount) {
                        bestMostAmount = mostMoves;
                    }
                }
                MoveAndMovesCount m = new MoveAndMovesCount(bestMostAmount, move);
                mostMovesList.add(m);

            }
            mostMovesList.sort(Comparator.comparing(MoveAndMovesCount::getMoveCount).reversed());
            if (mostMovesList.get(0).getMoveCount() > body.size()) {
                for (MoveAndMovesCount m : mostMovesList) {
                    System.out.println("Kill move >> " + m.getMoveType() + " count >> " + m.getMoveCount());
                }
                System.out.println("Can kill and evade going >>  " + mostMovesList.get(0).getMoveType());
                System.out.println("Time >> " + totalTime(start));
                return MoveResponse.builder().move(mostMovesList.get(0).getMoveType().getValue()).build();
            }

        }

        // no food on the board go for best move
        if (foodList.isEmpty()) {
            System.out.println("NoFood");
            List<MoveAndMovesCount> mostMovesList = new ArrayList<>();
            for (MoveType move : moves) {
                int tmp = SnakeUtil.evadeMoveCount(move, otherSnakes, body, board, Collections.emptyList(), 0);
                MoveAndMovesCount mostMoves = new MoveAndMovesCount(tmp, move);
                mostMovesList.add(mostMoves);
            }

            // sort take 1st

            mostMovesList.sort(Comparator.comparing(MoveAndMovesCount::getMoveCount).reversed());

            for (MoveAndMovesCount m : mostMovesList) {
                System.out.println("Empty food move >> " + m.getMoveType() + " moves > " + m.getMoveCount());
            }

            System.out.println("foodList empty evade >> " + mostMovesList.get(0).getMoveType());
            System.out.println("Time >> " + totalTime(start));
            return MoveResponse.builder()
                    .move(mostMovesList.get(0).getMoveType().getValue())
                    .build();
        }

        Coordinate nearestFood = SnakeUtil.getNearestCoordinateToTarget(head, foodList);


        int calc = 250;
        //main idea
        while (!foodList.isEmpty()) {
            System.out.println("Selected food >> " + nearestFood);
            List<MoveType> movesThatLeadToFood = new ArrayList<>();
            for (MoveType moveType : moves) {
                if (SnakeUtil.canMoveToTheFoodWithThisMove(moveType, nearestFood, body, otherSnakes, board, calc, 0)) {
                    movesThatLeadToFood.add(moveType);
                }
            }

            // can move to food before enemy
            if (!movesThatLeadToFood.isEmpty()) {
                List<MoveAndMovesCount> moveAndMovesCountList = new ArrayList<>();
                for (MoveType move : movesThatLeadToFood) {
                    int leastMoves = SnakeUtil.moveToFood(nearestFood, move, otherSnakes, body, board, calc);
                    if (leastMoves != 10000) {
                        moveAndMovesCountList.add(new MoveAndMovesCount(leastMoves, move));
                    }
                }

                // should never be empty
                if (!moveAndMovesCountList.isEmpty()) {
                    moveAndMovesCountList.sort(Comparator.comparing(MoveAndMovesCount::getMoveCount));
                    for (MoveAndMovesCount moveAndMovesCount : moveAndMovesCountList) {
                        Coordinate newHead = SnakeUtil.getNextMoveCoords(moveAndMovesCount.getMoveType(), body.get(0));
                        List<Coordinate> tmpFoods = new ArrayList<>(board.getFood());
                        List<Coordinate> newBody = SnakeUtil.createNewBody(tmpFoods, newHead, body);

                        List<Snake> newSnakes = SnakeUtil.enemySnakesMoved(otherSnakes, body, board, tmpFoods);
                        //List<Snake> newSnakes = getNewEnemySnakes(body, board, otherSnakes);

                        // can still move my body length after going for the food
                        if (SnakeUtil.canMoveThisManyMoves(newBody.size() + 1, 1, newSnakes, newBody, board, tmpFoods)) {
                            System.out.println("Should be ok going >> " + moveAndMovesCount.getMoveType());
                            System.out.println("Time >> " + totalTime(start));
                            return MoveResponse.builder().move(moveAndMovesCount.getMoveType().getValue()).build();
                        }
                        System.out.println("get new least moves");
                    }
                }
            }

            foodList.remove(nearestFood);
            if (foodList.isEmpty()) {
                break;
            }
            nearestFood = SnakeUtil.getNearestCoordinateToTarget(head, foodList);

        }


        //-!--!--!--!--!--!--!--!--!--!-
        //-!--!--!--!--!--!--!--!--!--!-
        System.out.println("!!! nothing else ");

        int tmpMostMoves;
        int tmpInt = 0;
        MoveType bestMove = moves.get(0);

//        List<Snake> newSnakes = SnakeUtil.enemySnakesMoved(otherSnakes, body, board);
//
//        moves = SnakeUtil.allowedMovesForGenius(newSnakes, body, board);
//        if (!moves.isEmpty()) {
//            moves = SnakeUtil.allowedMoves(otherSnakes, body, board);
//            if (moves.isEmpty()) {
//                System.out.println("Fked up in the end going UP");
//                System.out.println("Time >> " + totalTime(start));
//                return MoveResponse.builder()
//                        .move(MoveType.UP.getValue())
//                        .build();
//            }
//        }

        if (moves.size() == 1) {
            System.out.println("One move left in the end");
            return MoveResponse.builder().move(moves.get(0).getValue()).build();
        }

        for (MoveType moveType : moves) {
            List<Coordinate> tmpFoods = new ArrayList<>(board.getFood());
            tmpMostMoves = SnakeUtil.evadeMoveCount(moveType, otherSnakes, body, board, tmpFoods, 0);
            System.out.println("notingElseMatters move >> " + moveType + " movesLeftIfiGoThere >> " + tmpMostMoves);
            if (tmpMostMoves > tmpInt) {
                bestMove = moveType;
                tmpInt = tmpMostMoves;
            }
        }

        //nothing else matters
        System.out.println("Default going >> " + bestMove);
        System.out.println("Time >> " + totalTime(start));
        return MoveResponse.builder().move(bestMove.getValue()).build();

    }

    private String totalTime(Long start) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - start) + " ms";
    }


}

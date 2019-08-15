package com.battle.snakes;

import com.battle.snakes.game.*;
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
@RequestMapping("/smart")
public class SmartSnakeController extends BaseController {

  @RequestMapping(value = "/start", method = RequestMethod.POST, produces = "application/json")
  public StartResponse start(@RequestBody StartRequest request) {

    log.info(request.toString());

    //#a3a51d
    //#1da580
    //#1b75ad
    //#261aaf

    List<String> colors = Arrays.asList("#a3a51d", "#1da580", "#1b75ad", "#261aaf");
    int randomIndex = new Random().nextInt(colors.size());

    return StartResponse.builder()
      .color(colors.get(randomIndex))
      .headType(HeadType.SAFE.getValue())
      .tailType(TailType.ROUNDBUM.getValue())
      .build();
  }

  @RequestMapping(value = "/end", method = RequestMethod.POST)
  public Object end(@RequestBody EndRequest request) {

    log.info(request.toString());

    return new HashMap<String, Object>();
  }

  @RequestMapping(value = "/move", method = RequestMethod.POST, produces = "application/json")
  public MoveResponse move(@RequestBody MoveRequest request) {

//    log.info(request.toString());
    log.info("Turn >> " + request.getTurn().toString() + " Name >> " + request.getYou().getName());
    List<MoveType> moves = SnakeUtil.getAllowedMoves(request);
    List<Coordinate> body = request.getYou().getBody();

    if (moves.isEmpty()) {
      return MoveResponse.builder()
        .move(MoveType.LEFT.getValue())
        .build();
    }

    Coordinate head = body.get(0);

    List<Coordinate> food = request.getBoard().getFood();
    Coordinate nearest = SnakeUtil.getNearestCoordinateToTarget(head, food);

    return MoveResponse.builder()
      .move(SnakeUtil.getNearestMoveToTarget(nearest, head, moves).getValue())
      .build();
  }
}

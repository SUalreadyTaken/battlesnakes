package com.battle.snakes.util;

import com.battle.snakes.GeniusSnakeController;
import com.battle.snakes.game.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@WebMvcTest(GeniusSnakeController.class)
class GeniusSnakeTest {

	private final String url = "http://localhost:8080/mad-genius";

	@Autowired
	private MockMvc mvc;

	private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
																		 MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
	private static final ObjectMapper mapper = new ObjectMapper().configure(SerializationFeature.WRAP_ROOT_VALUE, false);
	private static final ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();


	@Test
	void eatsFood() throws Exception {
		/*
		F - food | H - head | M - my | E - enemy | T - tail | x - empty spot
		x	x	x	x	x
		x	F	x	x	x
		x	MH	x	x	x
		x	MB	x	x	x
		x	MT	x	x	x
		 */

		List<Coordinate> food = Stream.of(createCoordinate(2, 1), createCoordinate(4, 1)).collect(Collectors.toList());

		List<Coordinate> body = createCoordinateList(new ArrayList<>(Arrays.asList(1, 1, 1)),
													 new ArrayList<>(Arrays.asList(1, 2, 3)));

		List<Snake> snakes = Stream.of(createSnake(body)).collect(Collectors.toList());
		Snake snake = createSnake(body);
		Board board = Board.builder()
				.width(5)
				.height(5)
				.food(food)
				.snakes(snakes)
				.build();

		MoveResponse resultMove = getMoveResponse(ow.writeValueAsString(createMoveRequest(board, snake)));

		MoveResponse expectedMove = MoveResponse.builder().move(MoveType.RIGHT.getValue()).build();
		assertEquals(expectedMove, resultMove);

	}

	@Test
	void goesForKillNotFood() throws Exception {
		/*
		F - food | H - head | M - my | E - enemy | T - tail | x - empty spot
		x	x	x	x	x
		x	F	x	x	x
		x	MH	x	x	x
		x	MB	EH	x	x
		x	MT	ET	x	x
		Enemy is smaller so going right instead
		 */
		MoveResponse expectedMove = MoveResponse.builder().move(MoveType.RIGHT.getValue()).build();

		List<Coordinate> food = Stream.of(createCoordinate(1, 1)).collect(Collectors.toList());
		List<Coordinate> myBody = createCoordinateList(new ArrayList<>(Arrays.asList(1, 1, 1)),
													   new ArrayList<>(Arrays.asList(2, 3, 4)));
		List<Coordinate> enemyBody = Stream.of(createCoordinate(2, 3), createCoordinate(2, 4)).collect(Collectors.toList());
		List<Snake> snakes = Stream.of(createSnake(myBody), createSnake(enemyBody)).collect(Collectors.toList());

		Snake mySnake = createSnake(myBody);

		Board board = Board.builder()
				.width(5)
				.height(5)
				.food(food)
				.snakes(snakes)
				.build();

		MoveResponse resultMove = getMoveResponse(ow.writeValueAsString(createMoveRequest(board, mySnake)));

		assertEquals(expectedMove, resultMove);
	}

	@Test
	void evades() throws Exception {
		/*
		F - food | H - head | M - my | E - enemy | T - tail | x - empty spot
		x	x	x	x	x
		x	F	x	x	x
		x	EH	x	x	x
		x	EB	MH	x	x
		x	ET	MT	x	x
		my snake is smaller so must evade doesnt go for the shortest distance to food
		 */

		MoveResponse expectedMove = MoveResponse.builder().move(MoveType.RIGHT.getValue()).build();

		List<Coordinate> myBody = Stream.of(createCoordinate(2, 3), createCoordinate(2, 4)).collect(Collectors.toList());
		List<Coordinate> enemyBody = createCoordinateList(new ArrayList<>(Arrays.asList(1, 1, 1)),
														  new ArrayList<>(Arrays.asList(2, 3, 4)));
		List<Snake> snakes = Stream.of(createSnake(myBody), createSnake(enemyBody)).collect(Collectors.toList());

		Snake mySnake = createSnake(myBody);

		Board board = Board.builder()
				.width(5)
				.height(5)
				.food(new ArrayList<>())
				.snakes(snakes)
				.build();

		MoveResponse resultMove = getMoveResponse(ow.writeValueAsString(createMoveRequest(board, mySnake)));

		assertEquals(expectedMove, resultMove);
	}

	@Test
	void mostMoves() throws Exception {
		/*
		F - food | H - head | M - my | E - enemy | T - tail | x - empty spot
		B	B	T
		B	H	x
		B	B	x
		expected that snake goes Right and Up not Down to follow his tail and get most moves
		 */

		List<MoveResponse> responseList = new ArrayList<>();
		List<MoveResponse> expectedList = createExpectedList(new ArrayList<>(Arrays.asList("right", "up", "left")));

		List<Coordinate> myBody = createCoordinateList(
				new ArrayList<>(Arrays.asList(1, 1, 0, 0, 0, 1, 2)),
				new ArrayList<>(Arrays.asList(1, 2, 2, 1, 0, 0, 0)));
		List<Snake> snakes = Stream.of(createSnake(myBody)).collect(Collectors.toList());

		Snake mySnake = createSnake(myBody);

		Board board = Board.builder()
				.width(3)
				.height(3)
				.food(new ArrayList<>())
				.snakes(snakes)
				.build();

		MoveResponse resultMove = getMoveResponse(ow.writeValueAsString(createMoveRequest(board, mySnake)));
		responseList.add(resultMove);

		for (int i = 0; i < 2; i++) {
			System.out.println("--TEST MOVE" + (i + 1) + "--");
			resultMove = getMoveResponse(responseList, 3, 3, myBody, mySnake, resultMove);
		}


		for (int i = 0; i < expectedList.size(); i++) {
			assertEquals(expectedList.get(i), responseList.get(i));
		}

	}

	private List<MoveResponse> createExpectedList(List<String> expectedMoves) {
		List<MoveResponse> result = new ArrayList<>();
		for (String expectedMove : expectedMoves) {
			switch (expectedMove.toLowerCase()) {
				case "up":
					result.add(new MoveResponse("up"));
					break;
				case "down":
					result.add(new MoveResponse("down"));
					break;
				case "left":
					result.add(new MoveResponse("left"));
					break;
				case "right":
					result.add(new MoveResponse("right"));
					break;
			}
		}
		return result;
	}

	private MoveResponse getMoveResponse(List<MoveResponse> responseList, int width, int height, List<Coordinate> myBody, Snake mySnake, MoveResponse resultMove) throws Exception {
		MoveResponse response = nextMove(width, height, new ArrayList<>(), myBody, mySnake, resultMove);
		responseList.add(response);
		resultMove = response;
		return resultMove;
	}

	private MoveResponse nextMove(int width, int height, List<Coordinate> food, List<Coordinate> myBody, Snake mySnake,
								  MoveResponse resultMove) throws Exception {

		Coordinate nextMove = SnakeUtil.getNextMoveCoords(MoveType.valueOf(resultMove.getMove().toUpperCase()), mySnake.getBody().get(0));
		myBody.add(0, nextMove);

		if (!food.isEmpty() && food.contains(nextMove)) {
			food.remove(nextMove);
		} else {
			myBody.remove(myBody.size() - 1);
		}
		mySnake.setBody(myBody);

		Snake mySnakeTmp = createSnake(myBody);
		List<Snake> snakes = Stream.of(createSnake(myBody)).collect(Collectors.toList());

		Board board = Board.builder()
				.width(width)
				.height(height)
				.food(food)
				.snakes(snakes)
				.build();

		return getMoveResponse(ow.writeValueAsString(createMoveRequest(board, mySnakeTmp)));
	}

	private List<Coordinate> createCoordinateList(ArrayList<Integer> xList, ArrayList<Integer> yList) throws Exception {
		List<Coordinate> result = new ArrayList<>();
		if (xList.size() == yList.size()) {
			for (int i = 0; i < xList.size(); i++) {
				result.add(new Coordinate(xList.get(i), yList.get(i)));
			}
		} else {
			throw new Exception("createCoordinateList() x and y list ain't the same size");
		}
		return result;
	}

	private MoveResponse getMoveResponse(String requestJson) throws Exception {

		MvcResult result = mvc.perform(post(url + "/move")
											   .contentType(APPLICATION_JSON_UTF8)
											   .content(requestJson)).andExpect(status().isOk()).andReturn();
		return mapper.readValue(result.getResponse().getContentAsString(), MoveResponse.class);

	}


	private MoveRequest createMoveRequest(Board board, Snake you) {

		Game game = Game.builder()
				.id("3e02b354-ae29-4c3e-8c5b-26a04c764f8c")
				.build();

		return MoveRequest.builder()
				.game(game)
				.board(board)
				.turn(1)
				.you(you)
				.build();
	}

	private Snake createSnake(List<Coordinate> body) {

		return Snake.builder()
				.body(body)
				.build();
	}

	private Coordinate createCoordinate(int x, int y) {

		return Coordinate.builder()
				.x(x)
				.y(y)
				.build();
	}

}

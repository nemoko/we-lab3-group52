package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Spieler;
import at.ac.tuwien.big.we14.lab2.api.*;
import at.ac.tuwien.big.we14.lab2.api.impl.*;
import play.cache.Cache;
import play.mvc.*;
import views.html.*;

//TODO: session http://www.playframework.com/documentation/1.2/cache
//TODO: timeleft
//TODO: nach der 5. Runde soll noch roundover kommen

public class Application extends Controller {

	public static Result authentication() {
		return ok(authentication.render(""));
	}

	public static Result index() {
		return ok(index.render(""));
	}

	public static Result registration() {
		return ok(registration.render(""));
	}

	public static Result quiz_new_player() {
		
		QuizGame game = (QuizGame) Cache.get("game");
		User user = new Spieler();
		user.setName("Tomas");
		
		if(game == null){
			// Neues Spiel starten
			QuizFactory factory = new PlayQuizFactory("conf/data.de.json", user);
			game = factory.createQuizGame();
		} else {
			user = game.getPlayers().get(0);
		}
		
		game.startNewRound(); // start new game/round
		Round round = game.getCurrentRound();// current round
		Question question = round.getCurrentQuestion(user); // current question
															// of user

		// ArrayList mit Informationen des Spiels befüllen
		ArrayList<String> game_infos = new ArrayList<String>();

		game_infos.add(game.getPlayers().get(0).getName());
		game_infos.add(game.getPlayers().get(1).getName());
		game_infos.add("unknown");
		game_infos.add("unknown");
		game_infos.add("unknown");
		game_infos.add("unknown");
		game_infos.add("unknown");
		game_infos.add("unknown");
		game_infos.add(question.getCategory().getName());
		game_infos.add(question.getText());

		// Alle Antworten in String umwandeln
		ArrayList<Choice> choices = (ArrayList<Choice>) question
				.getAllChoices(); // all possible choices for a question
		ArrayList<String> allChoicesInString = new ArrayList<String>();
		for (Choice c : choices) {
			allChoicesInString.add(c.getText());
		}

		// Das Spiel im Cache speichern
		Cache.set("game", game);

		return ok(quiz.render(game_infos, allChoicesInString));
	}

	public static Result quiz() {
		
		// Das Spiel vom Cache laden
		QuizGame game = (QuizGame) Cache.get("game");

		// get request value from submitted form
		Map<String, String[]> map = request().body().asFormUrlEncoded();
		
		// get selected answers
		String[] checkedVal = map.get("answers");

		User user = game.getPlayers().get(0);
		User computer = game.getPlayers().get(1);

		Round round = game.getCurrentRound();// current round

		List<Choice> choices_clicked;

		if (checkedVal != null) {
			choices_clicked = stringToChoice(round.getCurrentQuestion(user)
					.getAllChoices(), checkedVal);
		} else {
			choices_clicked = new ArrayList<Choice>();
		}

		game.answerCurrentQuestion(user, choices_clicked, 10);

		if (game.isGameOver()) {
			return quizover();
		} else if (game.isRoundOver()) {
			return roundover();
		}

		Question question = round.getCurrentQuestion(user); // current question
															// of user
		question.getAllChoices(); // all possible choices for a question

		// ArrayList mit Informationen des Spiels befüllen
		ArrayList<String> game_infos = new ArrayList<String>();

		game_infos.add(user.getName());
		game_infos.add(computer.getName());

		game_infos.add(getCorrectString(round.getAnswer(0, user).isCorrect()));
		game_infos.add(getCorrectString(round.getAnswer(0, computer).isCorrect()));

		if (round.getAnswer(1, user) != null) {
			game_infos.add(getCorrectString(round.getAnswer(1, user)
					.isCorrect()));
			game_infos.add(getCorrectString(round.getAnswer(1, computer)
					.isCorrect()));
		} else {
			game_infos.add("unknown");
			game_infos.add("unknown");
		}

		if (round.getAnswer(2, user) != null) {
			game_infos.add(getCorrectString(round.getAnswer(2, user)
					.isCorrect()));
			game_infos.add(getCorrectString(round.getAnswer(2, computer)
					.isCorrect()));
		} else {
			game_infos.add("unknown");
			game_infos.add("unknown");
		}

		game_infos.add(question.getCategory().getName());
		game_infos.add(question.getText());

		question = round.getCurrentQuestion(user); // current question of user

		// Alle Antworten in String umwandeln
		ArrayList<Choice> choices = (ArrayList<Choice>) question
				.getAllChoices(); // all possible choices for a question
		ArrayList<String> allChoicesInString = new ArrayList<String>();
		for (Choice c : choices) {
			allChoicesInString.add(c.getText());
		}

		Cache.set("game", game);

		return ok(quiz.render(game_infos, allChoicesInString));
	}

	private static String getCorrectString(boolean correct) {
		if (correct) {
			return "correct";
		} else {
			return "incorrect";
		}
	}

	// Change String[] of selected answers to List of Choices
	private static List<Choice> stringToChoice(List<Choice> allChoices,
			String[] checkedVal) {

		ArrayList<Choice> selectedChoices = new ArrayList<Choice>();

		for (Choice c : allChoices) {
			for (int i = 0; i < checkedVal.length; i++) {
				if (c.getText().equals(checkedVal[i])) {
					selectedChoices.add(c);
				}
			}
		}
		return selectedChoices;
	}

	public static Result roundover() {

		// Das Spiel vom Cache laden
		QuizGame game = (QuizGame) Cache.get("game");
		Round round = game.getCurrentRound();
		User user = game.getPlayers().get(0);
		User computer = game.getPlayers().get(1);

		// ArrayList mit Informationen des Spiels befüllen
		ArrayList<String> game_infos = new ArrayList<String>();

		String winner = "Kein Gewinner";
		if(round.getRoundWinner() != null){
			winner = round.getRoundWinner().getName();
		}
		game_infos.add(winner);
		game_infos.add("" + game.getCurrentRoundCount());
		game_infos.add(game.getPlayers().get(0).getName());
		game_infos.add(game.getPlayers().get(1).getName());
		game_infos.add(getCorrectString(round.getAnswer(0, user).isCorrect()));
		game_infos.add(getCorrectString(round.getAnswer(1, user).isCorrect()));
		game_infos.add(getCorrectString(round.getAnswer(2, user).isCorrect()));
		game_infos.add("" + game.getWonRounds(user));
		game_infos.add(getCorrectString(round.getAnswer(0, computer).isCorrect()));
		game_infos.add(getCorrectString(round.getAnswer(1, computer).isCorrect()));
		game_infos.add(getCorrectString(round.getAnswer(2, computer).isCorrect()));
		game_infos.add("" + game.getWonRounds(computer));

		return ok(roundover.render(game_infos));
	}

	public static Result quizover() {
		
		QuizGame game = (QuizGame) Cache.get("game");
		
		// ArrayList mit Informationen des Spiels befüllen
		ArrayList<String> game_infos = new ArrayList<String>();
		
		String winner = "Kein Gewinner";
		if(game.getWinner() != null){
			winner = game.getWinner().getName();
		}
		game_infos.add(winner);
		game_infos.add(""+game.getWonRounds(game.getPlayers().get(0)));
		game_infos.add(""+game.getWonRounds(game.getPlayers().get(1)));
		
		Cache.set("game", null, 0);
		
		return ok(quizover.render(game_infos));
	}

}

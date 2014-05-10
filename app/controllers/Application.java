package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.big.we14.lab2.api.QuizGame;
import at.ac.tuwien.big.we14.lab2.api.User;
import models.Spieler;
import at.ac.tuwien.big.we14.lab2.api.*;
import at.ac.tuwien.big.we14.lab2.api.impl.*;

import org.h2.engine.*;

import play.i18n.Lang;
import play.cache.Cache;
import play.data.Form;
import play.data.validation.Constraints;
import play.mvc.*;
import views.html.*;
import static play.data.Form.form;


public class Application extends Controller {

	final static Form<Spieler> signupForm = form(Spieler.class)
			.bindFromRequest();

	// http://www.playframework.com/documentation/2.1.1/JavaGuide4
	public static Result authentication() {
		session().clear();

		if(session("uuid") != null && Cache.get(session("uuid")) != null){
			Cache.remove(session("uuid"));
		}
		return ok(authentication.render(form(Login.class)));
	}

	/**
	 * Login class used by Authentication Form.
	 */
	public static class Login {

		@Constraints.Required
		public String username;

		@Constraints.Required
		public String password;

		/**
		 * Validate the authentication.
		 * 
		 * @return null if validation ok, string with details otherwise
		 */
		public String validate() {
			// TODO implement
			return null;
		}
	}

	// /**
	// * Register class used by Registration Form.
	// */
	// public static class Register {
	//
	// @Constraints.Required
	// public String username;
	//
	// @Constraints.Required
	// public String password;
	//
	// /**
	// * Validate the authentication.
	// *
	// * @return null if validation ok, string with details otherwise
	// */
	// public String validate() {
	// if (isBlank(username)) {
	// return "Full name is required";
	// }
	//
	// if (isBlank(password)) {
	// return "Password is required";
	// }
	//
	// return null;
	// }
	//
	// private boolean isBlank(String input) {
	// return input == null || input.isEmpty() || input.trim().isEmpty();
	// }
	// }

	@play.db.jpa.Transactional
	public static Result authenticate() { // TODO check if user exists in DB
		Form<Login> loginForm = form(Login.class).bindFromRequest();

		if (!loginForm.hasErrors()) {
			session().clear();
			session("username", loginForm.get().username);

			if(session("uuid") != null && Cache.get(session("uuid")) != null){
				Cache.remove(session("uuid"));
			}

			
			if (SignUp.authenticate(loginForm.get().username,
					loginForm.get().password)) {
				return redirect(routes.Application.index());
			}
		}

		return badRequest(authentication.render(loginForm));
	}

	public static Result logout() {
		session().clear();
		flash("success", "You've been logged out");
		return redirect(routes.Application.authentication());
	}

	@Security.Authenticated(Secured.class)
	public static Result index() {
		return ok(index.render(""));
	}

	public static Result registration() {
		return ok(registration.render("", signupForm));
	}

	@Security.Authenticated(Secured.class)
	public static Result quiz_new_player() {

		// Check if session has ID. If not, set it
		if (session("uuid") == null)
			giveSessionID();

		QuizGame game = (QuizGame) Cache.get(session("uuid"));

		User user = new Spieler();

		user.setName(session("username"));

		if (game == null) {
			// Neues Spiel starten
			QuizFactory factory = new PlayQuizFactory("conf/data.de.json", user);
			game = factory.createQuizGame();
			session("last_roundover", "false");
		} else {
			user = game.getPlayers().get(0);

			if (game.isGameOver() && session("last_roundover").equals("true")) {
				return quizover();
			}
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
		Cache.set(session("uuid"), game);

		return ok(quiz.render(game_infos, allChoicesInString));
	}

	@Security.Authenticated(Secured.class)
	private static void giveSessionID() {
		String uuid = java.util.UUID.randomUUID().toString();
		session("uuid", uuid);
	}

	@Security.Authenticated(Secured.class)
	public static Result quiz() {

		// Das Spiel vom Cache laden
		QuizGame game = (QuizGame) Cache.get(session("uuid"));

		if (game.isGameOver() && 
				session("last_roundover").equals("true")) {
			return quizover();
		}

		// get request value from submitted form
		Map<String, String[]> map = request().body().asFormUrlEncoded();

		// get selected answers
		String[] checkedVal = map.get("answers");
		String time_left = map.get("timeleftvalue")[0];
		
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

		game.answerCurrentQuestion(user, choices_clicked, Integer.parseInt(time_left));

		if (game.isRoundOver()) {
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
		game_infos.add(getCorrectString(round.getAnswer(0, computer)
				.isCorrect()));

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

		Cache.set(session("uuid"), game);

		return ok(quiz.render(game_infos, allChoicesInString));
	}

	@Security.Authenticated(Secured.class)
	public static Result quiz_info() {

		if(session("uuid") != null && Cache.get(session("uuid")) != null){
			Cache.remove(session("uuid"));
		}
		
		return quiz_new_player();
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

	@Security.Authenticated(Secured.class)
	public static Result roundover() {

		// Das Spiel vom Cache laden
		QuizGame game = (QuizGame) Cache.get(session("uuid"));

		if (game.isGameOver()) {
			session("last_roundover", "true");
		}

		Round round = game.getCurrentRound();
		User user = game.getPlayers().get(0);
		User computer = game.getPlayers().get(1);

		// ArrayList mit Informationen des Spiels befüllen
		ArrayList<String> game_infos = new ArrayList<String>();

		String winner = "Kein Gewinner";
		if (round.getRoundWinner() != null) {
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
		game_infos.add(getCorrectString(round.getAnswer(0, computer)
				.isCorrect()));
		game_infos.add(getCorrectString(round.getAnswer(1, computer)
				.isCorrect()));
		game_infos.add(getCorrectString(round.getAnswer(2, computer)
				.isCorrect()));
		game_infos.add("" + game.getWonRounds(computer));

		return ok(roundover.render(game_infos));
	}

	@Security.Authenticated(Secured.class)
	public static Result quizover() {

		QuizGame game = (QuizGame) Cache.get(session("uuid"));

		// ArrayList mit Informationen des Spiels befüllen
		ArrayList<String> game_infos = new ArrayList<String>();

		String winner = "Kein Gewinner";
		if (game.getWinner() != null) {
			winner = game.getWinner().getName();
		}
		game_infos.add(winner);
		game_infos.add("" + game.getWonRounds(game.getPlayers().get(0)));
		game_infos.add("" + game.getWonRounds(game.getPlayers().get(1)));

		Cache.set(session("uuid"), null, 0);

		return ok(quizover.render(game_infos));
	}
}
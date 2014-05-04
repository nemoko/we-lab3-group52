package controllers;

import play.mvc.*;
import views.html.*;

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
        return ok(quiz.render(""));
    }
    
    public static Result quiz() {
        return ok(quiz.render(""));
    }
    
    public static Result quizover() {
        return ok(quizover.render(""));
    }
}

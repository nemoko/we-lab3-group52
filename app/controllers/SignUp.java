package controllers;

import models.Spieler;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.*;
import views.html.authentication;
import views.html.registration;

import static play.data.Form.*;

public class SignUp extends Controller {

    /**
     * Handle the form submission.
     */
    @Transactional
    public static Result submit() {
        Form<Spieler> filledForm = form(Spieler.class).bindFromRequest();

        // Check if the username is valid //TODO check if username already exists
        if(!filledForm.hasErrors()) {
            if(filledForm.get().username.equals("admin") || filledForm.get().username.equals("guest")) {
                filledForm.reject("username", "This username is already taken");
            }
        }

        if(filledForm.hasErrors()) {
            return badRequest(
                    registration.render("", filledForm)
            );
        } else {
            filledForm.get().save();
        }
        return redirect(routes.Application.authentication());
    }




}

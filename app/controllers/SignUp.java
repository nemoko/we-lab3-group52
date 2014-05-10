package controllers;

import models.Spieler;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.*;

import views.html.registration;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;

import static play.data.Form.*;

public class SignUp extends Controller {

    /**
     * Handle the form submission.
     */
    @Transactional
    public static Result submit() {
        Form<Spieler> filledForm = form(Spieler.class).bindFromRequest();

        // Check if the username is valid //TODO check if username already exists
        if(filledForm.hasErrors()) {
            return badRequest(
                    registration.render("", filledForm)
            );
        } else {
            if(filledForm.get().username.equals("admin") || filledForm.get().username.equals("guest"))
                filledForm.reject("username", "This username is already taken");

            if(findSpieler(filledForm.get().username) != null) {
                filledForm.reject("username", "This username is already taken");
            }

            filledForm.get().save();
        }
        return redirect(routes.Application.authentication()); //TODO call without Form?
    }

    @Transactional
    public static Spieler findSpieler(String username) {
        Form<Spieler> filledForm = form(Spieler.class).bindFromRequest();
        EntityManager em = JPA.em();
        String query = "SELECT s FROM Spieler s";

        TypedQuery<Spieler> tq = em.createQuery(query, Spieler.class);

        List<Spieler> allPlayers = (List<Spieler>) tq.getResultList();

        for(Spieler s : allPlayers) {
            if(s.getUsername().equals(username)) return s;
        }

        return null;
    }

    @Transactional
    public static boolean authenticate(String username, String password) {
        Spieler target = findSpieler(username);

        if(target == null) return false;
        if(password.equals(target.getPassword())) return true;

        return false;
    }


}

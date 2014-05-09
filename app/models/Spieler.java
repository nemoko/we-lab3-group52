package models;

import play.data.validation.Constraints;
import play.db.jpa.*;
import java.util.*;
import play.data.format.*;

import javax.persistence.*;
import javax.validation.Constraint;

import at.ac.tuwien.big.we14.lab2.api.User;

@Entity
public class Spieler implements User {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

    @Constraints.Required
    private String vorname;

    @Constraints.Required
    private String nachname;

    @Formats.DateTime(pattern="yyyy-MM-dd")
    public Date birthday;

    @Constraints.Required
    @Constraints.MinLength(4)
    @Constraints.MaxLength(16)
    public String username;

    @Constraints.Required
    public String password;

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    @Override
    public String getName() {
        return getVorname() + getNachname();
    }

    @Override
    public void setName(String s) {
        setVorname(s.substring(0,s.lastIndexOf(' ')));
        setNachname(s.substring(s.lastIndexOf(' ')));
    }

    @Transactional
    public void save() {
        JPA.em().persist(this);
    }
}

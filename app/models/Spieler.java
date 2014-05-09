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
    public String vorname;

    @Constraints.Required
    public String nachname;

    @Formats.DateTime(pattern="yyyy-MM-dd")
    public Date birthday;

    @Constraints.Required
    @Constraints.MinLength(4)
    @Constraints.MaxLength(16)
    public String username;

    @Constraints.Required
    public String password;

	@Override
	public String getName() {
		return nachname;
	}

	@Override
	public void setName(String name) {
		this.nachname = name;
	}

    @Transactional
    public void save() {
        JPA.em().persist(this);
    }
}

package models;

import play.data.validation.Constraints;
import play.db.jpa.*;
import java.util.*;
import play.data.format.*;

import javax.persistence.*;

import at.ac.tuwien.big.we14.lab2.api.User;

@Entity
@Access(AccessType.FIELD)
public class Spieler implements User {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	 
    public String vorname;

    public String nachname;

    @Formats.DateTime(pattern="yyyy-MM-dd")
    public Date birthday;

    public enum gender {
        male, female;
    }

    public static List<String> gender() {
        List<String> all = new ArrayList<String>();
        all.add("male");
        all.add("female");

        return all;
    }

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
}

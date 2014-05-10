package models;

import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.jpa.*;
import java.util.*;
import play.data.format.*;

import javax.persistence.*;
import javax.validation.Constraint;


import at.ac.tuwien.big.we14.lab2.api.User;

@javax.persistence.Entity
public class Spieler implements User {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

    private String vorname;

    private String nachname;

    public enum Gender {
        male,female;


        public static List<String> gender() {
            List<String> all = new ArrayList<String>();
            all.add("male");
            all.add("female");

            return all;
        }
    }

    private Gender gender;

    @Formats.DateTime(pattern="yyyy-MM-dd")
    public Date birthday;

    @Constraints.MinLength(4)
    @Constraints.MaxLength(16)
    public String username;

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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getName() {
        return getVorname() + " " + getNachname();
    }

    @Override
    public void setName(String s) {
        if(s.contains(" ")) {
            setVorname(s.substring(0, s.lastIndexOf(' ')));
            setNachname(s.substring(s.lastIndexOf(' ')));
        } else {
            setVorname(s);
            setNachname("");
        }
    }

    @Transactional
    public void save() {
        JPA.em().persist(this);
    }
}

package models;

import play.data.validation.Constraints;
import play.db.jpa.JPA;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Access(AccessType.FIELD)
public class Spieler {

	@Id
	public long id;
	 
    public String vorname;

    public String nachname;

    public String birthday;

    public enum geschlecht { male, female };

    @Constraints.Required
    @Constraints.MinLength(4)
    @Constraints.MaxLength(16)
    public String username;

    @Constraints.Required
    public String password;



}

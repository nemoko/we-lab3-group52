package models;

import play.data.validation.Constraints;
import play.db.jpa.*;
import java.util.*;
import play.data.format.*;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;

import at.ac.tuwien.big.we14.lab2.api.User;

@Entity
@Access(AccessType.FIELD)
public class Spieler implements User{

	@Id
	public Long id;
	 
    public String vorname;

    public String nachname;

    @Formats.DateTime(pattern="yyyy-MM-dd")
    public Date birthday;

    public Boolean geschlecht;

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

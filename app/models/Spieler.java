package models;

import play.data.validation.Constraints;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Access(AccessType.FIELD)
public class Spieler {

	@Id
	public long id;
	 
    @Constraints.Required
    @Constraints.MinLength(4)
    @Constraints.MaxLength(16)
    public String name;

    @Constraints.Required
    public String password;

    
    public void save() {
        if(this.Spieler.id == null) {
            this.Spieler = null;
        } else {
            this.Spieler = Spieler.findById(Spieler.id);
        }
        JPA.em().persist(this);
    }

}

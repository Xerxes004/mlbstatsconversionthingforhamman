package bo;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
@SuppressWarnings("serial")
@Entity(name = "teamseasonplayer")
public class TeamSeasonPlayer implements Serializable {
	@EmbeddedId
	TeamSeasonPlayerId id;
	
	@Embeddable
	static class TeamSeasonPlayerId implements Serializable {
		@ManyToOne
		@JoinColumn()
	}

}

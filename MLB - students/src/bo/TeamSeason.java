package bo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@SuppressWarnings("serial")
@Entity(name = "teamseason")
public class TeamSeason implements Serializable{
	@EmbeddedId
	TeamSeasonId id;
	
	@Embeddable
	static class TeamSeasonId implements Serializable {
		@ManyToOne
		@JoinColumn(name = "teamid", referencedColumnName = "teamid", insertable = false, updatable = false)
		Team team;
		@Column(name="year")
		Integer seasonYear;
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof TeamSeasonId)){
				return false;
			}
			TeamSeasonId other = (TeamSeasonId)obj;
			// in order for two different object of this type to be equal,
			// they must be for the same year and for the same player
			return (this.team==other.team &&
					this.seasonYear==other.seasonYear);
		}
		 
		@Override
		public int hashCode() {
			Integer hash = 0;
			if (this.team != null) hash += this.team.hashCode();
			if (this.seasonYear != null) hash += this.seasonYear.hashCode();
			return hash;
		}
	}
	
	@Column
	Integer gamesPlayed;
	@Column
	Integer wins;
	@Column
	Integer losses;
	@Column
	Integer rank;
	@Column
	Integer totalAttendance;
	
	public TeamSeasonId getId() {
		return id;
	}
	
	public void setId(TeamSeasonId id) {
		this.id = id;
	}
	
	public Integer getGamesPlayed() {
		return gamesPlayed;
	}
	
	public void setGamesPlayed(Integer gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
	}
	
	public Integer getWins() {
		return wins;
	}
	
	public void setWins(Integer wins) {
		this.wins = wins;
	}
	
	public Integer getLosses() {
		return losses;
	}
	
	public void setLosses(Integer losses) {
		this.losses = losses;
	}
	
	public Integer getRank() {
		return rank;
	}
	
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	
	public Integer getTotalAttendance() {
		return totalAttendance;
	}
	
	public void setTotalAttendance(Integer totalAttendance) {
		this.totalAttendance = totalAttendance;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof TeamSeason)){
			return false;
		}
		TeamSeason other = (TeamSeason)obj;
		// One-to-One with PlayerSeason so this works 
		return other.getId().equals(this.getId());
	}
	 
	@Override
	public int hashCode() {
		return this.getId().hashCode();
	}
}

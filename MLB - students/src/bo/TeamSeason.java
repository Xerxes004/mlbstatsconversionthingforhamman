package bo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 * TeamSeason contains the necessary fields and the mapping
 * of those fields to the database.
 * @author Wesley Kelly
 * @author Joel D. Sabol
 *
 */
@SuppressWarnings("serial")
@Entity(name = "teamseason")
public class TeamSeason implements Serializable{
	public TeamSeason() {}
	public TeamSeason(TeamSeasonId id, Set<Player> roster, Integer gamesPlayed, Integer wins, Integer losses,
			Integer rank, Integer totalAttendance) {
		this.id = id;
		this.roster = roster;
		this.gamesPlayed = gamesPlayed;
		this.wins = wins;
		this.losses = losses;
		this.rank = rank;
		this.totalAttendance = totalAttendance;
	}

	@EmbeddedId
	TeamSeasonId id;
	
	@Embeddable
	static class TeamSeasonId implements Serializable {
		@ManyToOne
		@JoinColumn(name="teamid", referencedColumnName = "teamid", insertable = false, updatable = false)
		Team team;
		@Column(name="year")
		Integer seasonYear;

		TeamSeasonId () {}
		
		TeamSeasonId (Team team, Integer season) {
			this.team = team;
			this.seasonYear = season;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof TeamSeasonId)){
				return false;
			}
			TeamSeasonId other = (TeamSeasonId)obj;
			// in order for two different object of this type to be equal,
			// they must be for the same team and for the same year
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
	
	// Maps the many to many relationship which defines teamseasonplayer in MLB DB
	@ManyToMany(fetch=FetchType.LAZY , cascade=CascadeType.ALL)
	@JoinTable(name = "teamseasonplayer",
		joinColumns = {
			@JoinColumn(name="teamid", referencedColumnName="teamid"),
			@JoinColumn(name="year", referencedColumnName="year")
		},
		inverseJoinColumns={@JoinColumn(name="playerid", referencedColumnName="playerid")}
	)
	Set<Player> roster = new HashSet<Player>();

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
	
	public TeamSeason (Team team, Integer year) {
		this.id = new TeamSeasonId(team, year);
	}
	
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
	
	public void addPlayerToRoster(Player player) {
		this.roster.add(player);
	}
	
	public Set<Player> getRoster () {
		return this.roster;
	}
	
	public void setRoster(Set<Player> roster) {
		this.roster = roster;
	}
	
	public void setTeam (Team team) {
		this.id.team = team;
	}
	
	public Team getTeam () {
		return this.id.team;
	}
	
	public void setYear(Integer year) {
		this.id.seasonYear = year;
	}
	
	public Integer getYear() {
		return this.id.seasonYear;
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

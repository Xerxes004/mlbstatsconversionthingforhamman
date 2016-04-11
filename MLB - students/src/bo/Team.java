package bo;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name = "team")
public class Team {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer teamId;
	
	@OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, mappedBy="id.team")
	@Fetch(FetchMode.JOIN)
	Set<TeamSeason> teamSeasons = new HashSet<TeamSeason>();
	
	/*@OneToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL, mappedBy="id.player")
	@Fetch(FetchMode.JOIN)
	Set<TeamSeasonPlayer> teamSeasons = new HashSet<TeamSeasonPlayer>();
	*/
	
	@Column
	String name;
	@Column
	String league;
	@Column
	Integer yearFounded;
	@Column
	Integer yearLast;
	
	// utility function
	public TeamSeason getTeamSeason(Integer year) {
		for (TeamSeason ts : teamSeasons) {
			if (ts.getYear().equals(year)) return ts;
		}
		return null;
	}

	public void addTeamSeason(TeamSeason ts) {
		this.teamSeasons.add(ts);
	}

	public Integer getTeamId() {
		return teamId;
	}
	
	public void setTeamId(Integer teamId) {
		this.teamId = teamId;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLeague() {
		return league;
	}
	
	public void setLeague(String league) {
		this.league = league;
	}
	
	public Integer getYearFounded() {
		return yearFounded;
	}
	
	public void setYearFounded(Integer yearFounded) {
		this.yearFounded = yearFounded;
	}
	
	public Integer getYearLast() {
		return yearLast;
	}
	
	public void setYearLast(Integer yearLast) {
		this.yearLast = yearLast;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Team)){
			return false;
		}
		Team other = (Team) obj;
		return (this.getName().equalsIgnoreCase(other.getName()) &&
				this.getYearFounded()==other.getYearFounded() &&
				this.getYearLast()==other.getYearLast());
	}
	 
	@Override
	public int hashCode() {
		Integer hash = 0;
		if (this.getName()!=null) hash += this.getName().hashCode(); 
		if (this.getYearFounded()!=null) hash += this.getYearFounded().hashCode();
		if (this.getYearLast()!=null) hash += this.getYearLast().hashCode();
		return hash;
	}	
}

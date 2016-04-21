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

/**
 * Team contains the necessary fields and the mapping
 * of those fields to the database.
 * @author Wesley Kelly
 * @author Joel D. Sabol
 *
 */
@Entity(name = "team")
public class Team {
	public Team(){}
	
	public Team(Integer teamId, Set<TeamSeason> seasons, String name, String league, Integer yearFounded,
			Integer yearLast) {
		this.teamId = teamId;
		this.seasons = seasons;
		this.name = name;
		this.league = league;
		this.yearFounded = yearFounded;
		this.yearLast = yearLast;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer teamId;
	
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="id.team")
	@Fetch(FetchMode.JOIN)
	Set<TeamSeason> seasons = new HashSet<TeamSeason>();
	
	@Column
	String name;
	@Column
	String league;
	@Column
	Integer yearFounded;
	@Column
	Integer yearLast;
	
	public TeamSeason getTeamSeason(Integer year) {
		for (TeamSeason ts : seasons) {
			if (ts.getYear().equals(year)) {
				return ts;
			}
		}
		return null;
	}
	
	public Integer getId(){
		return teamId;
	}

	public Set<TeamSeason> getSeasons() {
		return seasons;
	}

	public void setSeasons(Set<TeamSeason> seasons) {
		this.seasons = seasons;
	}

	public void addTeamSeason(TeamSeason ts) {
		this.seasons.add(ts);
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

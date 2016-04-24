package bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name = "player")
public class Player {
	
	public Player(){}
	
	public Player(Integer playerId, Set<String> positions, Set<PlayerSeason> seasons, Set<TeamSeason> teams,
			String name, String givenName, Date birthDay, Date deathDay, String battingHand, String throwingHand,
			String birthCity, String birthState, Date firstGame, Date lastGame) {
		this.playerId = playerId;
		this.positions = positions;
		this.seasons = seasons;
		this.teams = teams;
		this.name = name;
		this.givenName = givenName;
		this.birthDay = birthDay;
		this.deathDay = deathDay;
		this.battingHand = battingHand;
		this.throwingHand = throwingHand;
		this.birthCity = birthCity;
		this.birthState = birthState;
		this.firstGame = firstGame;
		this.lastGame = lastGame;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer playerId;
	
	@ElementCollection
	@CollectionTable(name = "playerposition", joinColumns = @JoinColumn(name = "playerid"))
	@Column(name = "position")
	@Fetch(FetchMode.JOIN)
	Set<String> positions = new HashSet<String>();
	
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="id.player")
	@Fetch(FetchMode.JOIN)
	Set<PlayerSeason> seasons = new HashSet<PlayerSeason>();

	// Referenced side of the many to many relation defined in TeamSeason
	@ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="roster")

	@Fetch(FetchMode.JOIN)
	Set<TeamSeason> teams = new HashSet<TeamSeason>();

	@Column
	String name;
	@Column
	String givenName;
	@Column
	Date birthDay;
	@Column
	Date deathDay;
	@Column
	String battingHand;
	@Column
	String throwingHand;
	@Column
	String birthCity;
	@Column
	String birthState;
	@Column
	Date firstGame;
	@Column
	Date lastGame;

	// utility function
	public PlayerSeason getPlayerSeason(Integer year) {
		for (PlayerSeason ps : seasons) {
			if (ps.getYear().equals(year)) return ps;
		}
		return null;
	}
	
	public List<TeamSeason> getTeamSeason(Integer year) {
		List<TeamSeason> list = new ArrayList<>();
		for(TeamSeason teamSeason : teams){
			if(teamSeason.getYear().equals(year)){
				list.add(teamSeason);
			}
		}
		return list;
	}
	
	public void addPosition(String p) {
		positions.add(p);
	}

	public Set<String> getPositions() {
		return positions;
	}

	public void setPositions(Set<String> positions) {
		this.positions = positions;
	}
	
	public void addTeamSeason(TeamSeason s) {
		teams.add(s);
	}

	public void addPlayerSeason(PlayerSeason s) {
		seasons.add(s);
	}

	public Set<PlayerSeason> getSeasons() {
		return seasons;
	}
	
	public void setSeasons(Set<PlayerSeason> seasons) {
		this.seasons = seasons;
	}
	
	public Integer getId() {
		return playerId;
	}
	public void setId(Integer id) {
		this.playerId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String nickName) {
		this.givenName = nickName;
	}

	public String getBattingHand() {
		return battingHand;
	}

	public void setBattingHand(String battingHand) {
		this.battingHand = battingHand;
	}

	public String getThrowingHand() {
		return throwingHand;
	}

	public void setThrowingHand(String throwingHand) {
		this.throwingHand = throwingHand;
	}

	public String getBirthCity() {
		return birthCity;
	}

	public void setBirthCity(String birthCity) {
		this.birthCity = birthCity;
	}

	public String getBirthState() {
		return birthState;
	}

	public void setBirthState(String birthState) {
		this.birthState = birthState;
	}

	public Date getFirstGame() {
		return firstGame;
	}

	public void setFirstGame(Date firstGame) {
		this.firstGame = firstGame;
	}

	public Date getLastGame() {
		return lastGame;
	}

	public void setLastGame(Date lastGame) {
		this.lastGame = lastGame;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public Date getDeathDay() {
		return deathDay;
	}

	public void setDeathDay(Date deathDay) {
		this.deathDay = deathDay;
	}
	
	public Set<TeamSeason> getTeams() {
		return teams;
	}

	public void setTeams(Set<TeamSeason> teams) {
		this.teams = teams;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Player)){
			return false;
		}
		Player other = (Player) obj;
		return (this.getName().equalsIgnoreCase(other.getName()) &&
				this.getBirthDay()==other.getBirthDay() &&
				this.getDeathDay()==other.getDeathDay());
	}
	 
	@Override
	public int hashCode() {
		Integer hash = 0;
		if (this.getName()!=null) hash += this.getName().hashCode(); 
		if (this.getBirthDay()!=null) hash += this.getBirthDay().hashCode();
		if (this.getDeathDay()!=null) hash += this.getDeathDay().hashCode();
		return hash;
	}
}

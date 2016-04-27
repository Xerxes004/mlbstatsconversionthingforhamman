package conversion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.sql.CallableStatement;

import bo.BattingStats;
import bo.CatchingStats;
import bo.FieldingStats;
import bo.PitchingStats;
import bo.Player;
import bo.PlayerSeason;
import bo.Team;
import bo.TeamSeason;
import dataaccesslayer.HibernateUtil;

public class Convert {

	static Connection conn;

	static final String MYSQL_CONN_URL = "jdbc:mysql://192.168.74.129:3306/mlb?user=joel&password=password"; 
	
	// This map of players is used when creating rosters.
	static Map<String, Player> players = new HashMap<String, Player>();
	
	public static void main(String[] args) {
		try {
			long startTime = System.currentTimeMillis();
			conn = DriverManager.getConnection(MYSQL_CONN_URL);
			// Converts players and seasons
			convertPlayers();
			convertTeams();
			
			long endTime = System.currentTimeMillis();
			long elapsed = (endTime - startTime) / (1000*60);
			System.out.println("Elapsed time in mins: " + elapsed);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (!conn.isClosed()) conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		HibernateUtil.getSessionFactory().close();
	}
	
	/**
	 * Converts all teams by creating teams and hydrating them from the 
	 * old database and persisting them to the new one.
	 */
	public static void convertTeams() {
		try {
			// Select all teams in the database along with pertinent information
			PreparedStatement ps = conn.prepareStatement(
					"select t.franchid, franchName as name, lgid, " +
					"(select min(yearid) from Teams where franchid = t.franchid) yearFounded,"+
					" max(yearid) as yearLast "+
					"from Teams t, TeamsFranchises tf " +
					"where t.franchid = tf.franchid " +
					"and yearid = (select max(yearid) from Teams where franchid = t.franchid) " +
					"group by t.franchid, t.lgid");
			ResultSet rs = ps.executeQuery();
			
			int count = 0;
			while(rs.next()) {
				// Provides some feedback to the user
				System.out.println(++count);
				
				// Extracts data from the results
				String franchId = rs.getString("franchID");
				String name = rs.getString("name");
				String league = rs.getString("lgID");
				Integer yearFounded = rs.getInt("yearFounded");
				Integer yearLast = rs.getInt("yearLast");
				
				if (franchId == null || franchId.isEmpty() ||
					name == null || name.isEmpty() ||
					league == null || league.isEmpty() ||
					yearFounded == null || yearFounded.toString().isEmpty() ||
					yearLast == null || yearLast.toString().isEmpty())
				{
					continue;
				}
				
				// Create a new team and populate fields
				Team team = new Team();
				team.setName(name);
				team.setLeague(league);
				team.setYearFounded(yearFounded);
				team.setYearLast(yearLast);
				
				// Adds all seasons to the current team
				addSeasons(team, franchId);	
				
				// Persist the team to the new database
				// TeamSeason and TeamSeasonPlayer entries all cascade
				HibernateUtil.persistTeam(team);				
			}
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Adds seasons to the team provided.
	 * @param team The team to add a season to.
	 * @param franchId The id that uniquely identifies a team.
	 */
	public static void addSeasons(Team team, String franchId) {
		try {
			//  We prepared this statement:
			//
			//	DELIMITER //
			//  CREATE PROCEDURE getSeasonInfo(IN f VARCHAR(3))
			//	BEGIN
			//		SELECT yearId, teamId, G, W, L, Rank, Attendance
			//		FROM Teams
			//		WHERE franchId = f;
			// 	END // 
			//	DELIMITER ;
			
			CallableStatement cs = conn.prepareCall("{call mlb.getSeasonInfo(?)}");
			cs.setString(1, franchId);
			ResultSet rs = cs.executeQuery();
			
			TeamSeason season = null;
			while (rs.next()) {
				int year = rs.getInt("yearID");
				season = team.getTeamSeason(year);

				if (season==null) {
					season = new TeamSeason(team, year);
					team.addTeamSeason(season);
					Integer gamesPlayed = rs.getInt("G");
					String teamId = rs.getString("teamId");
					Integer wins = rs.getInt("W");
					Integer losses = rs.getInt("L");
					Integer rank = rs.getInt("Rank");
					Integer attend = rs.getInt("attendance");
					
					if (gamesPlayed == null || 
						wins == null ||
						losses == null ||
						rank == null ||
						attend == null)
						continue;
					
					// Populates the season with appropriate data
					season.setGamesPlayed(gamesPlayed);
					season.setWins(wins);
					season.setLosses(losses);
					season.setRank(rank);
					season.setTotalAttendance(attend);

					// Add the roster of players for this particular team this season
					addRoster(season, teamId, year);
					
					// Add this season to the team's TeamSeasons
					team.addTeamSeason(season);
				}
			}
			
			rs.close();
			cs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a roster to the team provided.
	 * @param season The season to add a roster to.
	 * @param teamId The id of the team whose roster is being populated.
	 * @param year The year corresponding to the season.
	 */
	public static void addRoster(TeamSeason season, String teamId, Integer year) {
		try {
			// Selects all players are players associated with a team during a specific season 
			PreparedStatement ps = conn.prepareStatement("select " + 
					"playerId " +
					"from Appearances " +
					"where teamId = ? and yearId = ?");
			ps.setString(1, teamId);
			ps.setString(2, year.toString());

			ResultSet rs = ps.executeQuery();
			
			// Adds all players in the result to the roster
			while (rs.next()) {
				String playerId = rs.getString("playerId");
				
				Player player = players.get(playerId);
				
				if (player != null) {
					season.addPlayerToRoster(player);
				}
			}
			
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Converts all players by creating teams and hydrating them from the 
	 * old database and persisting them to the new one.
	 */
	public static void convertPlayers() {
		try {
			PreparedStatement ps = conn.prepareStatement("select " + 
						"playerID, " + 
						"nameFirst, " + 
						"nameLast, " + 
						"nameGiven, "+ 
						"birthDay, " + 
						"birthMonth, " + 
						"birthYear, " + 
						"deathDay, "+ 
						"deathMonth, " + 
						"deathYear, " + 
						"bats, " + 
						"throws, " + 
						"birthCity, " + 
						"birthState, " + 
						"debut, " + 
						"finalGame " +
						"from Master");
			
			ResultSet rs = ps.executeQuery();
			int count=0; // for progress feedback only
			while (rs.next()) {
				count++;
				// this just gives us some progress feedback
				if (count % 1000 == 0) System.out.println("num players: " + count);
				String pid = rs.getString("playerID");
				String firstName = rs.getString("nameFirst");
				String lastName = rs.getString("nameLast");
				// this check is for data scrubbing
				// don't want to bring anybody over that doesn't have a pid, firstname and lastname
				if (pid == null	|| pid.isEmpty() || 
					firstName == null || firstName.isEmpty() ||
					lastName == null || lastName.isEmpty()) continue;
				
				Player p = new Player();
				p.setName(firstName + " " + lastName);
				p.setGivenName(rs.getString("nameGiven"));
				java.util.Date birthDay = convertIntsToDate(rs.getInt("birthYear"), rs.getInt("birthMonth"), rs.getInt("birthDay"));
				if (birthDay!=null) p.setBirthDay(birthDay);
				java.util.Date deathDay = convertIntsToDate(rs.getInt("deathYear"), rs.getInt("deathMonth"), rs.getInt("deathDay"));
				if (deathDay!=null) p.setDeathDay(deathDay);
				// need to do some data scrubbing for bats and throws columns
				String hand = rs.getString("bats");
				if (hand!=null && hand.equalsIgnoreCase("B")) {
					hand = "S";
				} 
				p.setBattingHand(hand);
				hand = rs.getString("throws");
				p.setThrowingHand(hand);
				p.setBirthCity(rs.getString("birthCity"));
				p.setBirthState(rs.getString("birthState"));
				java.util.Date firstGame = rs.getDate("debut");
				if (firstGame!=null) p.setFirstGame(firstGame);
				java.util.Date lastGame = rs.getDate("finalGame");
				if (lastGame!=null) p.setLastGame(lastGame);
				
				// Add all positions to the current player
				addPositions(p, pid);
				
				// players bio collected, now go after stats
				addSeasons(p, pid);
				
				// add this player to the map in order to build rosters later
				players.put(pid, p);
				
				// This should be unnecessary because everything will cascade when team is populated
				// we can now persist player, and the seasons and stats will cascade
				HibernateUtil.persistPlayer(p);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a new date from the year month and day provided.
	 * @param year The year component of the date.
	 * @param month The month component of the date.
	 * @param day The day component of the date.
	 * @return A date comprised of the year, month, and day provided.
	 */
	private static java.util.Date convertIntsToDate(int year, int month, int day) {
		Calendar c = new GregorianCalendar();
		java.util.Date d=null;
		// if year is 0, then date wasn't populated in MySQL database
		if (year!=0) {
			c.set(year, month-1, day);
			d = c.getTime();
		}
		return d;
	}
	
	/**
	 * Adds positions to players
	 * @param p The player to add positions to.
	 * @param pid The playerID used to select player positions.
	 */
	public static void addPositions(Player p, String pid) {
		Set<String> positions = new HashSet<String>();
		try {
			PreparedStatement ps = conn.prepareStatement("select " +
					"distinct pos " +
					"from Fielding " +
					"where playerID = ?;");
			ps.setString(1, pid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String pos = rs.getString("pos");
				positions.add(pos);
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		p.setPositions(positions);
	}

	/**
	 * Adds seasons to players.
	 * @param player The player to add seasons to.
	 * @param playerId The playerID of the player provided.
	 */
	public static void addSeasons(Player player, String playerId) {
		try {
			PreparedStatement ps = conn.prepareStatement("select " + 
					"yearID, " + 
					"teamID, " +
					"lgId, " +
					"sum(G) as gamesPlayed " + 
					"from Batting " + 
					"where playerID = ? " + 
					"group by yearID, teamID, lgID;");
			ps.setString(1, playerId);
			ResultSet rs = ps.executeQuery();
			PlayerSeason s = null;
			while (rs.next()) {
				int yid = rs.getInt("yearID");
				s = player.getPlayerSeason(yid);
				// it is possible to see more than one of these per player if he switched teams
				// set all of these attrs the first time we see this playerseason
				if (s==null) {
					s = new PlayerSeason(player,yid);
					player.addPlayerSeason(s);
					s.setGamesPlayed(rs.getInt("gamesPlayed"));
					double salary = getSalary(playerId, yid);
					s.setSalary(salary);
					BattingStats batting = getBatting(s,playerId,yid);
					s.setBattingStats(batting);
					FieldingStats fielding = getFielding(s,playerId,yid);
					s.setFieldingStats(fielding);
					PitchingStats pitching = getPitching(s,playerId,yid);
					s.setPitchingStats(pitching);
					CatchingStats catching = getCatching(s,playerId,yid);
					s.setCatchingStats(catching);
				// set this the consecutive time(s) so it is the total games played regardless of team	
				} else {
					s.setGamesPlayed(rs.getInt("gamesPlayed")+s.getGamesPlayed());
				}
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Gets the salary of the specified player.
	 * @param playerID The playerID of the current player.
	 * @param yearID The year used to uniquely identify the salary.
	 * @return The salary of the current player.
	 */
	public static double getSalary(String playerID, Integer yid) {
		double salary = 0;
		try {
			PreparedStatement ps = conn.prepareStatement("select " + 
					"sum(salary) as salary " + 
					"from Salaries " + 
					"where playerID = ? " + 
					"and yearID = ? ;");
			ps.setString(1, playerID);
			ps.setInt(2, yid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				salary = rs.getDouble("salary");
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salary;
	}

	/**
	 * Gets the batting statistics of the specified player.
	 * @param playerSeason The player season to insert the stats into.
	 * @param playerID The unique id of the player.
	 * @param yearID The year defining the season to get stats for.
	 * @return The batting stats of a player for a given season.
	 */
	public static BattingStats getBatting(PlayerSeason playerSeason, String playerID, Integer yearID) {
		BattingStats s = new BattingStats();
		try {
			PreparedStatement ps = conn.prepareStatement("select "	+ "" +
					"sum(AB) as atBats, " + 
					"sum(H) as hits, " + 
					"sum(2B) as doubles, " + 
					"sum(3B) as triples, " + 
					"sum(HR) as homeRuns, " + 
					"sum(RBI) as runsBattedIn, " + 
					"sum(SO) as strikeouts, " + 
					"sum(BB) as walks, " + 
					"sum(HBP) as hitByPitch, " + 
					"sum(IBB) as intentionalWalks, " + 
					"sum(SB) as steals, " + 
					"sum(CS) as stealsAttempted " + 
					"from Batting " + 
					"where playerID = ? " + 
					"and yearID = ? ;");
			ps.setString(1, playerID);
			ps.setInt(2, yearID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				s.setId(playerSeason);
				s.setAtBats(rs.getInt("atBats"));
				s.setHits(rs.getInt("hits"));
				s.setDoubles(rs.getInt("doubles"));
				s.setTriples(rs.getInt("triples"));
				s.setHomeRuns(rs.getInt("homeRuns"));
				s.setRunsBattedIn(rs.getInt("runsBattedIn"));
				s.setStrikeouts(rs.getInt("strikeouts"));
				s.setWalks(rs.getInt("walks"));
				s.setHitByPitch(rs.getInt("hitByPitch"));
				s.setIntentionalWalks(rs.getInt("intentionalWalks"));
				s.setSteals(rs.getInt("steals"));
				s.setStealsAttempted(rs.getInt("stealsAttempted"));
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	/**
	 * Gets the fielding statistics of the specified player.
	 * @param playerSeason The player season to insert the stats into.
	 * @param playerID The unique id of the player.
	 * @param yearID The year defining the season to get stats for.
	 * @return The fielding stats of the player.
	 */
	public static FieldingStats getFielding(PlayerSeason playerSeason, String playerID, Integer yearID) {
		FieldingStats s = new FieldingStats();
		try {
			PreparedStatement ps = conn.prepareStatement("select " +
					"sum(E) as errors, " +
					"sum(PO) as putOuts " +
					"from Fielding " +
					"where playerID = ? " + 
					"and yearID = ? ;");
			ps.setString(1, playerID);
			ps.setInt(2, yearID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				s.setId(playerSeason);
				s.setErrors(rs.getInt("errors"));
				s.setPutOuts(rs.getInt("putOuts"));
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	/**
	 * Gets the pitching statistics of the specified player.
	 * @param playerSeason The player season to insert the stats into.
	 * @param playerID The unique id of the player.
	 * @param yearID The year defining the season to get stats for.
	 * @return The pitching stats of the player.
	 */
	public static PitchingStats getPitching(PlayerSeason playerSeason, String playerID, Integer yearID) {
		PitchingStats s = new PitchingStats();
		try {
			PreparedStatement ps = conn.prepareStatement("select " +
					"sum(IPOuts) as outsPitched, " + 
					"sum(ER) as earnedRunsAllowed, " +
					"sum(HR) as homeRunsAllowed, " + 
					"sum(SO) as strikeouts, " +
					"sum(BB) as walks, " + 
					"sum(W) as wins, " +
					"sum(L) as losses, " + 
					"sum(WP) as wildPitches, " +
					"sum(BFP) as battersFaced, " + 
					"sum(HBP) as hitBatters, " +
					"sum(SV) as saves " + 
					"from Pitching " +
					"where playerID = ? " + 
					"and yearID = ? ;");
			ps.setString(1, playerID);
			ps.setInt(2, yearID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				s.setId(playerSeason);
				s.setOutsPitched(rs.getInt("outsPitched"));
				s.setEarnedRunsAllowed(rs.getInt("earnedRunsAllowed"));
				s.setHomeRunsAllowed(rs.getInt("homeRunsAllowed"));
				s.setStrikeouts(rs.getInt("strikeouts"));
				s.setWalks(rs.getInt("walks"));
				s.setWins(rs.getInt("wins"));
				s.setLosses(rs.getInt("losses"));
				s.setWildPitches(rs.getInt("wildPitches"));
				s.setBattersFaced(rs.getInt("battersFaced"));
				s.setHitBatters(rs.getInt("hitBatters"));
				s.setSaves(rs.getInt("saves"));
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}
	
	/**
	 * Gets the catching statistics of the specified player.
	 * @param playerSeason The player season to insert the stats into.
	 * @param playerID The unique id of the player.
	 * @param yearID The year defining the season to get stats for.
	 * @return The catching stats of the player.
	 */
	public static CatchingStats getCatching(PlayerSeason playerSeason, String playerID, Integer yearID) {
		CatchingStats s = new CatchingStats();
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("select " +
					"sum(PB) as passedBalls, " +
					"sum(WP) as wildPitches, " +
					"sum(SB) as stealsAllowed, " +
					"sum(CS) as stealsCaught " +
					"from Fielding " +
					"where playerID = ? " + 
					"and yearID = ? ;");
			ps.setString(1, playerID);
			ps.setInt(2, yearID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				s.setId(playerSeason);
				s.setPassedBalls(rs.getInt("passedBalls"));
				s.setWildPitches(rs.getInt("wildPitches"));
				s.setStealsAllowed(rs.getInt("stealsAllowed"));
				s.setStealsCaught(rs.getInt("stealsCaught"));
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			ps.toString();
			e.printStackTrace();
		}
		return s;
	}


}
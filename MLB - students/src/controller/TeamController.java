
package controller;

import dataaccesslayer.HibernateUtil;
import util.MLBUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.json.*;

import bo.Player;
import bo.PlayerSeason;
import bo.Team;
import bo.TeamSeason;
import view.TeamView;

/**
 * 
 * @author Wesley Kely
 * @author Joel D. Sabol
 *
 */
public class TeamController extends BaseController {
	
	/**
	 * 
	 */
	public void initSSP(String query) {
		System.out.println("building team html");
		view = new TeamView();
		processSSP(query);
	}

	/**
	 * Initializes the controller to respond to a JSON request.
	 */
	@Override
	public void initJSON(String query) {
		System.out.println("Building team json");
		view = new TeamView();
		processJSON(query);
	}
	
	/**
	 * Determines which dynamic HTML page to render.
	 */
	@Override
	protected void performAction() {
		String action = keyVals.get("action");
		if (action == null) {
			return;
		}

		System.out.println("Performing action: " + action);
		switch (action.toLowerCase()) {
		case ACT_SEARCHFORM:
			processSearchForm();
			break;
		case ACT_SEARCH:
			processSearch();
			break;
		case ACT_DETAIL:
			processDetails();
			break;
		case ACT_ROSTER:
			processRoster();
			break;
		default:
			break;
		}
	}

	/**
	 * Determines which JSON operation to perform.
	 */
	@Override
	protected void performJSONAction() {
		// Get and validate action
		String action = keyVals.get("action");
		if (action == null) {
			return;
		}
		System.out.println("Performing action: " + action);

		switch (action.toLowerCase()) {
		case ACT_SEARCH:
			processJSONSearch();
			break;
		case ACT_DETAIL:
			processJSONDetails();
			break;
		case ACT_ROSTER:
			processJSONRoster();
			break;
		default:
			break;
		}
	}

	/**
	 * Processes the HTML search page.
	 */
	protected void processSearchForm() {
		view.buildSearchForm();
	}

	/**
	 * Processes HTML search requests.
	 */
	protected void processSearch() {
		String teamName = keyVals.get("name");
		if (teamName == null) {
			return;
		}
		
		String valExact = keyVals.get("on");
		boolean exactMatch = valExact != null && valExact.equalsIgnoreCase("on");
		List<Team> teams = HibernateUtil.retrieveTeamsByName(teamName, exactMatch, -1);
		
		if(teams == null){
			return;
		}
		
		view.printSearchResultsMessage(teamName, exactMatch);
		buildTeamSearchResultsTable(teams);
		view.buildLinkToSearch();
		view.buildFooter(view);
	}

	/**
	 * Processes JSON search requests.
	 */
	protected void processJSONSearch() {
		// Get and validate the name param
		String teamName = keyVals.get("name");
		if (teamName == null) {
			return;
		}

		// Get and validate the page number param
		String page = keyVals.get("page");
		Integer pageNum = 0;
		if (page != null) {
			pageNum = new Integer(page);
		}

		// Get the list of teams on the current page and the total count
		List<Team> teams = HibernateUtil.retrieveTeamsByName(teamName, false, pageNum);
		Integer count = HibernateUtil.retrieveTeamsByNameCount(teamName, false);
		if(teams == null){
			return;
		}
		
		// Try building JSON object
		try {
			buildTeamSearchResultsJSON(teams, count);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes the HTML details page request.
	 */
	protected void processDetails() {
		String teamId = keyVals.get("id");
		if (teamId == null) {
			return;
		}
		Team team = (Team) HibernateUtil.retrieveTeamById(Integer.valueOf(teamId));
		if (team == null){
			return;
		}
		
		List<Team> list = new ArrayList<>();
		list.add(team);
		view.buildHeader(team);
		
		view.buildCharts(teamId);

		buildTeamDetailsTable(team);
		view.buildLinkToSearch();
		view.buildFooter(view);
	}

	/**
	 * Processes the JSON details page request.
	 */
	protected void processJSONDetails() {
		String teamId = keyVals.get("id");
		if (teamId == null) {
			return;
		}
		Team team = (Team) HibernateUtil.retrieveTeamById(Integer.valueOf(teamId));
		if (team == null) {
			return;
		}

		try {
			buildTeamDetailsJSON(team);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Processes the roster HTML page request.
	 */
	protected void processRoster() {
		Integer teamId = new Integer(keyVals.get("id"));
		Integer year = new Integer(keyVals.get("year"));

		TeamSeason teamSeason = HibernateUtil.retrieveTeamSeason(teamId, year);
		if(teamSeason == null){
			return;
		}
		
		Team team = teamSeason.getTeam();
		
		String linkToTeam = view.encodeLink(new String[] { "id" }, new String[] { team.getId().toString() },
				team.getName(), BaseController.ACT_DETAIL, BaseController.SSP_TEAM);
    	
		view.buildHeader(teamSeason, linkToTeam);
		buildRosterTable(teamSeason);
	}

	/**
	 * Processes the roster JSON page request.
	 */
	protected void processJSONRoster() {
		Integer teamId = new Integer(keyVals.get("id"));
		Integer year = new Integer(keyVals.get("year"));

		TeamSeason teamSeason = HibernateUtil.retrieveTeamSeason(teamId, year);
		if(teamSeason == null){
			return;
		}
		
		try {
			buildJSONRosterTable(teamSeason);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builds the JSON object representing the search results and build a page
	 * with the string representation.
	 * 
	 * @param teams The page of teams to incorporate in the search results.
	 * @param count The total count of all teams in the search result.
	 * @throws JSONException If an element cannot be placed into a JSON object.
	 */
	private void buildTeamSearchResultsJSON(List<Team> teams, Integer count) throws JSONException {
		JSONObject teamList = new JSONObject();
		teamList.put("count", count);
		JSONArray teamArray = new JSONArray();
		teams.forEach((entry) -> {
			JSONObject to = new JSONObject();
			try {
				to.put("id", entry.getId());
				to.put("name", entry.getName());
				to.put("league", entry.getLeague());
				to.put("yearfounded", entry.getYearFounded().toString());
				to.put("yearlast", entry.getYearLast().toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			teamArray.put(to);
		});
		teamList.put("items", teamArray);
		view.buildJSON(teamList.toString());
	}

	/**
	 * Builds a table containing the search results and renders it to HTML.
	 * @param teams the teams to include in the search results.
	 */
	private void buildTeamSearchResultsTable(List<Team> teams) {
		String[][] table = new String[teams.size() + 1][4];
		table[0][0] = "Name";
		table[0][1] = "League";
		table[0][2] = "Year Founded";
		table[0][3] = "Year Last";
		for (int i = 1; i <= teams.size(); i++) {
			Team team = teams.get(i - 1);
			String teamName = team.getName().toString();
			String teamID = team.getId().toString();
			// table[i][0] = view.encodeLink(new String[] { "id" }, new String[]
			// { teamID }, teamID, ACT_DETAIL, SSP_TEAM);
			table[i][0] = view.encodeLink(new String[] { "id" }, new String[] { teamID }, teamName, ACT_DETAIL,
					SSP_TEAM);
			table[i][1] = team.getLeague();
			table[i][2] = team.getYearFounded().toString();
			table[i][3] = team.getYearLast().toString();
		}
		view.buildTable(table);
	}

	/**
	 * Builds a JSON object containing the team details and renders it to a string.
	 * @param team The team to pull details from.
	 * @throws JSONException
	 */
	private void buildTeamDetailsJSON(Team team) throws JSONException {
		JSONObject to = new JSONObject();
		to.put("name", team.getName());
		to.put("league", team.getLeague());
		to.put("yearfounded", team.getYearFounded().toString());
		to.put("yearlast", team.getYearLast().toString());

		JSONArray seasons = new JSONArray();
		List<TeamSeason> ts = new ArrayList<>(team.getSeasons());
		Collections.sort(ts, TeamSeason.teamSeasonComparator);
		ts.forEach((entry) -> {
			JSONObject season = new JSONObject();
			try {
				season.put("year", entry.getYear());
				season.put("gamesplayed", entry.getGamesPlayed());
				season.put("wins", entry.getWins());
				season.put("losses", entry.getLosses());
				season.put("rank", entry.getRank());
				season.put("attendance", entry.getTotalAttendance());
			} catch (Exception e) {
				e.printStackTrace();
			}
			seasons.put(season);
		});
		to.put("seasons", seasons);
		view.buildJSON(to.toString());
	}

	/**
	 * Builds a table containing the details of the provided team.
	 * @param team The provided team.
	 */
	private void buildTeamDetailsTable(Team team) {
		Set<TeamSeason> ts = team.getSeasons();
		List<TeamSeason> entries = new ArrayList<>(ts);
		Collections.sort(entries, TeamSeason.teamSeasonComparator);
		Collections.reverse(entries);
		String[][] seasonTable = new String[ts.size() + 1][7];

		seasonTable[0][0] = "Season";
		seasonTable[0][1] = "Roster";
		seasonTable[0][2] = "Games Played";
		seasonTable[0][3] = "Wins";
		seasonTable[0][4] = "Losses";
		seasonTable[0][5] = "Rank";
		seasonTable[0][6] = "Attendance";

		int i = 1;
		for (TeamSeason entry : entries) {
			String teamId = entry.getTeam().getId().toString();
			String year = entry.getYear().toString();
			String link = view.encodeLink(new String[] { "id", "year" }, new String[] { teamId, year }, "Roster",
					ACT_ROSTER, SSP_TEAM);
			seasonTable[i][0] = year;
			seasonTable[i][1] = link;
			seasonTable[i][2] = entry.getGamesPlayed().toString();
			seasonTable[i][3] = entry.getWins().toString();
			seasonTable[i][4] = entry.getLosses().toString();
			seasonTable[i][5] = entry.getRank().toString();
			seasonTable[i][6] = MLBUtil.INTEGER_FORMAT.format(entry.getTotalAttendance());
			i++;
		}

		view.buildTable(seasonTable, "season-table");
	}	

	/**
	 * Builds a JSON representation of the roster for a given team season.
	 * @param teamSeason The team season containg the necessary information.
	 * @return
	 * @throws JSONException
	 */
	private void buildJSONRosterTable(TeamSeason teamSeason) throws JSONException {
		Team team = teamSeason.getTeam();
		Integer year = teamSeason.getYear();
		
		// Build the team season information
		JSONObject jsRoster = new JSONObject();
		jsRoster.put("name", team.getName());
		jsRoster.put("league", team.getLeague());
		jsRoster.put("year", year);
		jsRoster.put("payroll", "@TODO");

		// Build the roster component
		JSONArray roster = new JSONArray();
		teamSeason.getRoster().forEach((player) -> {
			PlayerSeason playerSeason = player.getPlayerSeason(year);
			JSONObject jsPlayer = new JSONObject();
			try {
				jsPlayer.put("name", player.getName());
				jsPlayer.put("gamesplayed", playerSeason.getGamesPlayed().toString());
				jsPlayer.put("salary", MLBUtil.DOLLAR_FORMAT.format(playerSeason.getSalary()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			roster.put(jsPlayer);
		});
		jsRoster.put("seasons", roster);
		
		view.buildJSON(jsRoster.toString());
	}

	/**
	 * Builds a roster table from the provided team season.
	 * @param teamSeason The team season to generate the roster from.
	 */
	private void buildRosterTable(TeamSeason teamSeason) {
		//view.setHeader(buildHeader(teamSeason));

		ArrayList<Player> roster = new ArrayList<>(teamSeason.getRoster());
		String rosterTable[][] = new String[roster.size() + 1][3];
		rosterTable[0][0] = "Name";
		rosterTable[0][1] = "Games Played";
		rosterTable[0][2] = "Salary";

		int i = 1;
		for (Player player : roster) {
			PlayerSeason playerSeason = player.getPlayerSeason(teamSeason.getYear());
			rosterTable[i][0] = view.encodeLink(new String[] { "id" }, new String[] { player.getId().toString() },
					player.getName(), ACT_DETAIL, SSP_PLAYER);
			rosterTable[i][1] = playerSeason.getGamesPlayed().toString();
			rosterTable[i][2] = MLBUtil.DOLLAR_FORMAT.format(playerSeason.getSalary());
			i++;
		}

		view.buildTable(rosterTable, "roster-table");
	}

}


package controller;

import dataaccesslayer.HibernateUtil;

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


public class TeamController extends BaseController {

	@Override
	protected void performAction() {
		String action = keyVals.get("action");
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

	@Override
	protected void performJSONAction() {
		String action = keyVals.get("action");
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

	@Override
	public void initSSP(String query) {
		System.out.println("building team html");
		view = new TeamView();
		processSSP(query);
	}

	@Override
	public void initJSON(String query) {
		System.out.println("Building team json");
		view = new TeamView();
		processJSON(query);
	}

	protected void processSearchForm() {
		view.buildSearchForm();
	}

	protected void processSearch() {
		String teamName = keyVals.get("name");
		if (teamName == null) {
			return;
		}
		String valExact = keyVals.get("on");
		boolean exactMatch = valExact != null && valExact.equalsIgnoreCase("on");
		List<Team> teams = HibernateUtil.retrieveTeamsByName(teamName, exactMatch, 0);
		view.printSearchResultsMessage(teamName, exactMatch);
		buildSearchResultsTableTeam(teams);
		view.buildLinkToSearch();
	}
	
	protected void processJSONSearch() {
		String teamName = keyVals.get("name");
		if (teamName == null) {
			return;
		}
		String page = keyVals.get("page");
		Integer pageNum;
		if(page != null){
			pageNum = new Integer(page);
		}else{
			pageNum = new Integer(0);
		}
		String valExact = keyVals.get("on");
		boolean exactMatch = valExact != null && valExact.equalsIgnoreCase("on");
		List<Team> teams = HibernateUtil.retrieveTeamsByName(teamName, exactMatch, pageNum);
		Integer count = HibernateUtil.retrieveTeamsByNameCount(teamName, exactMatch);
		try {
			buildJSONSearchResults(teams, count);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void processDetails() {
		String teamId = keyVals.get("id");
		if (teamId == null) {
			return;
		}
		Team team = (Team) HibernateUtil.retrieveTeamById(Integer.valueOf(teamId));
		if (team == null)
			return;
		buildSearchResultsTableTeamDetail(team);
		view.buildLinkToSearch();
	}
	
	protected void processJSONDetails() {
		String teamId = keyVals.get("id");
		if (teamId == null) {
			return;
		}
		Team team = (Team) HibernateUtil.retrieveTeamById(Integer.valueOf(teamId));
		if (team == null){
			return;
		}
		
		try {
			buildJSONSearchResultsTableTeamDetail(team);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void processRoster() {
		Integer teamId = new Integer(keyVals.get("id"));
		Integer year = new Integer(keyVals.get("year"));

		TeamSeason teamSeason = HibernateUtil.retrieveTeamSeason(teamId, year);

		buildRosterTable(teamSeason);
	}
	
	protected void processJSONRoster() {
		Integer teamId = new Integer(keyVals.get("id"));
		Integer year = new Integer(keyVals.get("year"));

		TeamSeason teamSeason = HibernateUtil.retrieveTeamSeason(teamId, year);
		
		try {
			buildJSONRosterTable(teamSeason);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private JSONObject buildJSONSearchResults(List<Team> teams, Integer count) throws JSONException {
		JSONObject teamList = new JSONObject();
		teamList.put("count", count);
		JSONArray teamArray = new JSONArray();
		teams.forEach((entry) -> {
			JSONObject to = new JSONObject();
			try {
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
		return teamList;
	}

	private void buildSearchResultsTableTeam(List<Team> teams) {
		String[][] table = new String[teams.size() + 1][4];
		// table[0][0] = "Id";
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

	private JSONObject buildJSONSearchResultsTableTeamDetail(Team team) throws JSONException {
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
				season.put("year", entry.getYear().toString());
				season.put("gamesplayed", entry.getGamesPlayed().toString());
				season.put("wins", entry.getWins().toString());
				season.put("losses", entry.getLosses().toString());
				season.put("rank", entry.getRank().toString());
				season.put("attendance", entry.getTotalAttendance().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			seasons.put(season);
		});
		to.put("seasons", seasons);
		view.buildJSON(to.toString());
		return to;
	}

	// individual team result
	private void buildSearchResultsTableTeamDetail(Team team) {
		/*
		 * String[][] table = new String[2][4]; //table[0][0] = "Id";
		 * table[0][0] = "Name"; table[0][1] = "League"; table[0][2] =
		 * "Year Founded"; table[0][3] = "Year Last";
		 * 
		 * //table[1][0] = team.getId().toString(); table[1][0] =
		 * team.getName(); table[1][1] = team.getLeague(); table[1][2] =
		 * team.getYearFounded().toString(); table[1][3] =
		 * team.getYearLast().toString(); view.buildTable(table);
		 */

		buildHeader(team);

		Set<TeamSeason> ts = team.getSeasons();
		List<TeamSeason> entries = new ArrayList<>(ts);
		Collections.sort(entries, TeamSeason.teamSeasonComparator);
		String[][] seasonTable = new String[ts.size() + 1][7];

		seasonTable[0][0] = "Season";
		seasonTable[0][5] = "Rank";	
		seasonTable[0][1] = "Games Played";
		seasonTable[0][2] = "Roster";
		seasonTable[0][3] = "Wins";
		seasonTable[0][4] = "Losses";
		seasonTable[0][6] = "Attendance";

		int i = 1;
		for (TeamSeason entry : entries) {
			String teamId = entry.getTeam().getId().toString();
			String year = entry.getYear().toString();
			String link = view.encodeLink(new String[] { "id", "year" }, new String[] { teamId, year }, "Roster",
					ACT_ROSTER, SSP_TEAM);
			seasonTable[i][0] = year;
			seasonTable[i][1] = entry.getGamesPlayed().toString();
			seasonTable[i][2] = link;
			seasonTable[i][3] = entry.getWins().toString();
			seasonTable[i][4] = entry.getLosses().toString();
			seasonTable[i][5] = entry.getRank().toString();
			seasonTable[i][6] = entry.getTotalAttendance().toString();
			i++;
		}

		view.buildTable(seasonTable);
	}
	
	// build header for specific team season
	private void buildHeader(TeamSeason teamSeason)
    {
        Team team = teamSeason.getTeam();
        int year = teamSeason.getYear();
        StringBuilder header = new StringBuilder();
        String logo = view.getLogo(team.getName());
        if (logo != null)
        {
            header.append("<img id='logo' src='")
                .append(view.getLogo(team.getName()))
                .append("'")
                .append(" alt='")
                .append(team.getName())
                .append("'")
                .append(" />");
        }
        String league = team.getLeague();
        if (league.equals("NL"))
        {
        	league = "National League";
        } 
        else if (league.equals("AL"))
        {
        	league = "American League";
        }
        header.append("<h1>")
        	.append(year)
        	.append(" ")
            .append(team.getName())
            .append("</h1>")
            .append("<h2>")
            .append(league)
            .append("</h2>")
            .append("<h3>Roster</h3>");

        view.setHeader(header.toString());
    }
	
	// build header for team overview
	private void buildHeader(Team team)
    {
        StringBuilder header = new StringBuilder();
        String logo = view.getLogo(team.getName());
        if (logo != null)
        {
            header.append("<img id='logo' src='")
                .append(view.getLogo(team.getName()))
                .append("'")
                .append(" alt='")
                .append(team.getName())
                .append("'")
                .append(" />");
        }
        String league = team.getLeague();
        if (league.equals("NL"))
        {
        	league = "National League";
        } 
        else if (league.equals("AL"))
        {
        	league = "American League";
        }
        header.append("<h1>")
            .append(team.getName())
            .append("</h1>")
            .append("<h2>")
            .append(league)
            .append("</h2>")
            .append("<h3>Roster</h3>");

        view.setHeader(header.toString());
    }

	private JSONObject buildJSONRosterTable(TeamSeason teamSeason) throws JSONException {
		Team team = teamSeason.getTeam();
		Integer year = teamSeason.getYear();
		JSONObject jsRoster = new JSONObject();
		jsRoster.put("name", team.getName());
		jsRoster.put("league", team.getLeague());
		jsRoster.put("year", year);
		jsRoster.put("payroll", "@TODO");
		
		JSONArray roster = new JSONArray();
		teamSeason.getRoster().forEach((player)->{
			PlayerSeason playerSeason = player.getPlayerSeason(year);
			JSONObject jsPlayer = new JSONObject();
			try {
				jsPlayer.put("name", player.getName());
				jsPlayer.put("gamesplayed", playerSeason.getGamesPlayed().toString());
				jsPlayer.put("salary", DOLLAR_FORMAT.format(playerSeason.getSalary()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			roster.put(jsPlayer);
		});
		
		jsRoster.put("seasons", roster);
		view.buildJSON(jsRoster.toString());
		return jsRoster;
	}

	// search results
	private void buildRosterTable(TeamSeason teamSeason) {
		buildHeader(teamSeason);
		
		/*String[][] teamTable = new String[2][4];
		Team team = teamSeason.getTeam();
		Integer year = teamSeason.getYear();
		teamTable[0][0] = "Name";
		teamTable[0][1] = "League";
		teamTable[0][2] = "Year";
		teamTable[0][3] = "Player Payroll";
		teamTable[1][0] = team.getName();
		teamTable[1][1] = team.getLeague();
		teamTable[1][2] = year.toString();
		teamTable[1][3] = "@TODO";

		view.buildTable(teamTable);*/

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
			rosterTable[i][2] = DOLLAR_FORMAT.format(playerSeason.getSalary());
			i++;
		}

		view.buildTable(rosterTable, "roster-table");
	}

}

package controller;

import dataaccesslayer.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
	public void init(String query) {
		System.out.println("building team html");
		view = new TeamView();
		process(query);
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
		List<Team> teams = HibernateUtil.retrieveTeamsByName(teamName, exactMatch);
		view.printSearchResultsMessage(teamName, exactMatch);
		buildSearchResultsTableTeam(teams);
		view.buildLinkToSearch();
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

	protected void processRoster() {
		Integer teamId = new Integer(keyVals.get("id"));
		Integer year = new Integer(keyVals.get("year"));
		
		TeamSeason teamSeason = HibernateUtil.retrieveTeamSeason(teamId, year);
		
		buildRosterTable(teamSeason);
	}

	private void buildSearchResultsTableTeam(List<Team> teams) {
		String[][] table = new String[teams.size() + 1][5];
		table[0][0] = "Id";
		table[0][1] = "Name";
		table[0][2] = "League";
		table[0][3] = "Year Founded";
		table[0][4] = "Year Last";
		for (int i = 1; i <= teams.size(); i++) {
			Team team = teams.get(i - 1);
			String teamID = team.getId().toString();
			table[i][0] = view.encodeLink(new String[] { "id" }, new String[] { teamID }, teamID, ACT_DETAIL, SSP_TEAM);
			table[i][1] = team.getName();
			table[i][2] = team.getLeague();
			table[i][3] = team.getYearFounded().toString();
			table[i][4] = team.getYearLast().toString();
		}
		view.buildTable(table);
	}

	// individual team result
	private void buildSearchResultsTableTeamDetail(Team team) {
		/*String[][] table = new String[2][4];
		//table[0][0] = "Id";
		table[0][0] = "Name";
		table[0][1] = "League";
		table[0][2] = "Year Founded";
		table[0][3] = "Year Last";

		//table[1][0] = team.getId().toString();
		table[1][0] = team.getName();
		table[1][1] = team.getLeague();
		table[1][2] = team.getYearFounded().toString();
		table[1][3] = team.getYearLast().toString();
		view.buildTable(table);*/
		
		StringBuilder header = new StringBuilder();
		header.append("<img id='logo' src='")
		  	  .append(view.getLogo(team.getName()))
		  	  .append("' />")
			  .append("<h1>")
			  .append(team.getName())
			  .append("</h1>")
			  .append("<h3>League - ")
			  .append(team.getLeague())
			  .append("</h3>")
			  .append(team.getYearFounded())
			  .append(" - ")
			  .append(team.getYearLast())
			  ;
		
		view.setHeader(header.toString());

		Set<TeamSeason> ts = team.getSeasons();
		ArrayList<TeamSeason> entries = new ArrayList<>(ts);
		String[][] seasonTable = new String[ts.size() + 1][7];

		seasonTable[0][0] = "Year";
		seasonTable[0][1] = "Games Played";
		seasonTable[0][2] = "Roster";
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
	
	// search results
	private void buildRosterTable(TeamSeason teamSeason){
		String[][] teamTable = new String[2][4];
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
		
		view.buildTable(teamTable);
		
		ArrayList<Player> roster = new ArrayList<>(teamSeason.getRoster());
		String rosterTable[][] = new String[roster.size() + 1][3];
		rosterTable[0][0] = "Name";
		rosterTable[0][1] = "Games Played";
		rosterTable[0][2] = "Salary";
		
		int i = 1;
		for(Player player : roster){
			PlayerSeason playerSeason = player.getPlayerSeason(year);
			rosterTable[i][0] = view.encodeLink(new String[]{"id"}, new String[]{player.getId().toString()}, player.getName(), ACT_DETAIL, SSP_PLAYER);
			rosterTable[i][1] = playerSeason.getGamesPlayed().toString();
			rosterTable[i][2] = DOLLAR_FORMAT.format(playerSeason.getSalary());
			i++;
		}
		
		view.buildTable(rosterTable);
	}

}

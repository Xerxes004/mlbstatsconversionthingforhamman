package controller;

import dataaccesslayer.HibernateUtil;

import java.util.List;

import bo.Team;
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
		if(teamName == null){ return;}
		String valExact = keyVals.get("on");
		boolean exactMatch = valExact != null && valExact.equalsIgnoreCase("on");
		List<Team> teams = HibernateUtil.retrieveTeamsByName(teamName, exactMatch);
		System.out.println(teams.size());
		view.printSearchResultsMessage(teamName, exactMatch);
		buildSearchResultsTableTeam(teams);
		view.buildLinkToSearch();
	}

	protected void processDetails() {

	}
	
	private void buildSearchResultsTableTeam(List<Team> teams){
		String[][] table = new String[teams.size() + 1][6];
		table[0][0] = "Id";
		table[0][1] = "Name";
		table[0][2] = "League";
		table[0][4] = "Year Founded";
		table[0][5] = "Year Last";
		for(int i = 1; i <= teams.size(); i++){
			Team team = teams.get(i-1);
			String teamID = team.getId().toString();
			table[i][0] = view.encodeLink(new String[]{"id"}, new String[]{teamID},	teamID, ACT_DETAIL, SSP_TEAM);
			table[i][1] = team.getName();
			table[i][2] = team.getLeague();
			table[i][3] = team.getYearFounded().toString();
			table[i][4] = team.getYearLast().toString();
		}
		view.buildTable(table);
	}

}

package controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import view.PlayerView;
import bo.BattingStats;
import bo.Player;
import bo.PlayerCareerStats;
import bo.PlayerSeason;
import bo.Team;
import bo.TeamSeason;
import dataaccesslayer.HibernateUtil;

/**
 * 
 * @author Seth Hamman
 * @author Wesley Kelly
 * @author Joel D. Sabol
 *
 */
public class PlayerController extends BaseController {

	@Override
	public void initSSP(String query) {
		System.out.println("Building dynamic html for player");
		view = new PlayerView();
		processSSP(query);
	}

	@Override
	public void initJSON(String query) {
		System.out.println("Building JSON for player");
		view = new PlayerView();
		processJSON(query);
	}

	/**
	 * Determines which JSON operation to perform.
	 */
	@Override
	protected void performJSONAction() {
		String action = keyVals.get("action");
		if (action == null) {
			return;
		}
		
		System.out.println("playercontroller performing action: " + action);
		
		switch (action.toLowerCase()) {
		case ACT_SEARCH:
			processJSONSearch();
			break;
		case ACT_DETAIL:
			processJSONDetails();
			break;
		default:
			break;
		}
	};

	/**
	 * Determines which dynamic HTML page to render.
	 */
	@Override
	protected void performAction() {
		String action = keyVals.get("action");
		if (action == null) {
			return;
		}
		
		System.out.println("playercontroller performing action: " + action);
		
		switch (action.toLowerCase()) {
		case ACT_SEARCHFORM:
			processSearchForm();
			break;
		case ACT_SEARCH:
			processHTMLSearch();
			break;
		case ACT_DETAIL:
			processHTMLDetails();
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
	 * Processes JSON search requests.
	 */
	protected final void processJSONSearch() {
		String name = keyVals.get("name");
		String v = keyVals.get("exact");
		String p = keyVals.get("page");
		Integer page = 0;
		if(p != null){
			page = Integer.parseInt(p);
		}
		boolean exact = (v != null && v.equalsIgnoreCase("on"));
		List<Player> players = HibernateUtil.retrievePlayersByName(name, exact, page);
		Integer count = HibernateUtil.retrieveCountPlayersByName(name, exact);
		try {
			buildPlayerSearchResultsJSON(players, count);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Processes HTML search requests.
	 */
	protected final void processHTMLSearch() {
		String name = keyVals.get("name");
		if (name == null) {
			return;
		}
		String v = keyVals.get("exact");
		boolean exact = (v != null && v.equalsIgnoreCase("on"));
		List<Player> bos = HibernateUtil.retrievePlayersByName(name, exact, -1);
		view.printSearchResultsMessage(name, exact);
		buildPlayerSearchResultsTable(bos);
		StringBuilder footer = new StringBuilder();
		footer.append(view.buildLinkToSearch())
			  .append("<a href=\"index.htm\">Home</a>\r\n");
		view.setFooter(footer.toString());
	}

	/**
	 * Processes the JSON details page request.
	 */
	protected final void processJSONDetails(){
		String id = keyVals.get("id");
		if (id == null) {
			return;
		}
		Player player = (Player) HibernateUtil.retrievePlayerById(Integer.valueOf(id));
		if (player == null){
			return;
		}
		try {
			buildPlayerDetailsJSON(player);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Processes the HTML details page request.
	 */
	protected final void processHTMLDetails() {
		String id = keyVals.get("id");
		if (id == null) {
			return;
		}
		Player p = (Player) HibernateUtil.retrievePlayerById(Integer.valueOf(id));
		if (p == null){
			return;
		}
		List<Player> list = new ArrayList<>();
		list.add(p);
		// Build the basic player information
		buildPlayerSearchResultsTable(list);
		
		// Build the charts
		view.buildCharts(id);
		
		// Build the season information
		buildPlayerDetailsTable(p);
		
		StringBuilder footer = new StringBuilder();
		footer.append(view.buildLinkToSearch())
			  .append("<a href=\"index.htm\">Home</a>\r\n");
		view.setFooter(footer.toString());
	}

	/**
	 * Builds the JSON object representing the search results and build a page
	 * with the string representation.
	 * 
	 * @param playerSearchResults The page list of players to incorporate in the search results.
	 * @param count The total count of all players in the search result.
	 * @throws JSONException
	 */
	private void buildPlayerSearchResultsJSON(List<Player> playerSearchResults, Integer count) throws JSONException {
		JSONObject jsPlayerObj = new JSONObject();
		jsPlayerObj.put("count", count);
		JSONArray jsPlayers = new JSONArray();

		jsPlayerObj.put("items", jsPlayers);

		playerSearchResults.forEach((player) -> {
			JSONObject jsPlayer = new JSONObject();
			PlayerCareerStats stats = new PlayerCareerStats(player);
			try {
				jsPlayer.put("id", player.getId());
				jsPlayer.put("name", player.getName());
				jsPlayer.put("lifetimesalary", stats.getSalary());
				jsPlayer.put("gamesplayed", stats.getGamesPlayed());
				
				Date first = player.getFirstGame();
				if(first != null){
					jsPlayer.put("firstgame", DATE_FORMAT.format(first));
				}
				
				Date last = player.getLastGame();
				if(last != null){
					jsPlayer.put("lastgame", DATE_FORMAT.format(player.getLastGame()));
				}
				
				jsPlayer.put("careerhomeruns", stats.getHomeRuns());
				jsPlayer.put("careerhits", stats.getHits());
				jsPlayer.put("careerbattingavg", DOUBLE_FORMAT.format(stats.getBattingAverage()));
				jsPlayer.put("careersteals", stats.getSteals());
			} catch (Exception e) {
				e.printStackTrace();
			}
			jsPlayers.put(jsPlayer);
		});
		view.buildJSON(jsPlayerObj.toString());
	}

	/**
	 * Builds a table containing the search results and renders it to HTML.
	 * @param playerSearchResults The players to include in the search results.
	 */
	private void buildPlayerSearchResultsTable(List<Player> playerSearchResults) {
		// need a row for the table headers
		String[][] table = new String[playerSearchResults.size() + 1][9];

		table[0][0] = "Name";
		table[0][1] = "Lifetime Salary";
		table[0][2] = "Games Played";
		table[0][3] = "First Game";
		table[0][4] = "Last Game";
		table[0][5] = "Career Home Runs";
		table[0][6] = "Career Hits";
		table[0][7] = "Career Batting Average";
		table[0][8] = "Career Steals";
		for (int i = 0; i < playerSearchResults.size(); i++) {
			Player player = playerSearchResults.get(i);
			PlayerCareerStats stats = new PlayerCareerStats(player);
			String pid = player.getId().toString();
			table[i + 1][0] = view.encodeLink(new String[] { "id" }, new String[] { pid }, player.getName(), ACT_DETAIL,
					SSP_PLAYER);
			table[i + 1][1] = DOLLAR_FORMAT.format(stats.getSalary());
			table[i + 1][2] = stats.getGamesPlayed().toString();
			table[i + 1][3] = formatDate(player.getFirstGame());
			table[i + 1][4] = formatDate(player.getLastGame());
			table[i + 1][5] = stats.getHomeRuns().toString();
			table[i + 1][6] = stats.getHits().toString();
			table[i + 1][7] = DOUBLE_FORMAT.format(stats.getBattingAverage());
			table[i + 1][8] = stats.getSteals().toString();
		}
		view.buildTable(table);
		
		StringBuilder footer = new StringBuilder();
		footer.append(view.buildLinkToSearch())
			  .append("<a href=\"index.htm\">Home</a>\r\n");
		view.setFooter(footer.toString());
	}

	/**
	 * Builds a JSON object containing the player details and renders it to a string.
	 * @param player The player to pull details from.
	 * @throws JSONException
	 */
	private void buildPlayerDetailsJSON(Player player) throws JSONException {
		Set<String> positions = player.getPositions();

		// Generate the player
		JSONObject jsPlayer = new JSONObject();
		jsPlayer.put("name", player.getName());
		jsPlayer.put("givenname", player.getGivenName());
		jsPlayer.put("birthday", formatDate(player.getBirthDay()));
		jsPlayer.put("deathday", formatDate(player.getDeathDay()));
		jsPlayer.put("hometown", player.getBirthCity() + ", " + player.getBirthState());

		// Generate the positions
		JSONArray jsPositions = new JSONArray();
		jsPlayer.put("positions", jsPositions);
		positions.forEach((position) -> {
			jsPositions.put(position);
		});
		
		// Get the seasons
		List<PlayerSeason> seasonList = new ArrayList<PlayerSeason>(player.getSeasons());
		// Sort them based on the year
		Collections.sort(seasonList, PlayerSeason.playerSeasonsComparator);
		
		// Generate the seasons
		JSONArray jsSeasons = new JSONArray();
		
		// Add seasons to player
		jsPlayer.put("items", jsSeasons);
		
		seasonList.forEach((season)->{
			JSONObject jsSeason = new JSONObject();
			jsSeasons.put(jsSeason);
			BattingStats bs = season.getBattingStats();
			Integer year = season.getYear();
			try {
				jsSeason.put("year", year);
				jsSeason.put("gamesplayed", season.getGamesPlayed());
				jsSeason.put("salary", season.getSalary());
				jsSeason.put("hits", bs.getHits());
				jsSeason.put("atbats", bs.getAtBats());
				jsSeason.put("battingavg", DOUBLE_FORMAT.format(season.getBattingAverage()));
				jsSeason.put("homeruns", bs.getHomeRuns());
				JSONArray jsTeams = new JSONArray();
				season.getPlayer().getTeamSeason(year).forEach((teamseason)->{
					JSONObject jsTeam = new JSONObject();
					jsTeams.put(jsTeam);
					Team team = teamseason.getTeam();
					try {
						jsTeam.put("id", team.getId());
						jsTeam.put("name", team.getName());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				});
				jsSeason.put("teams", jsTeams);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		});
		
		view.buildJSON(jsPlayer.toString());
	}

	/**
	 * Builds a table containing the details of the provided player.
	 * @param player The provided player.
	 */
	private void buildPlayerDetailsTable(Player player) {
		Set<PlayerSeason> seasons = player.getSeasons();
		List<PlayerSeason> list = new ArrayList<PlayerSeason>(seasons);
		Collections.sort(list, PlayerSeason.playerSeasonsComparator);
		Collections.reverse(list);
		view.setHeader(buildPlayerHeader(player));
		
		// Build seasons table
		String[][] seasonTable = new String[seasons.size() + 1][8];
		seasonTable[0][0] = "Year";
		seasonTable[0][1] = "Games Played";
		seasonTable[0][2] = "Salary";
		seasonTable[0][3] = "Teams(s)";
		seasonTable[0][4] = "Hits";
		seasonTable[0][5] = "At Bats";
		seasonTable[0][6] = "Batting Average";
		seasonTable[0][7] = "Home Runs";

		int i = 0;
		for (PlayerSeason ps : list) {
			i++;
			List<TeamSeason> teams = ps.getPlayer().getTeamSeason(ps.getYear());
			StringBuilder sb = new StringBuilder();
			for (TeamSeason ts : teams) {
				sb.append(view.encodeLink(new String[] { "id" }, new String[] { ts.getTeam().getId().toString() },
						ts.getTeam().getName(), ACT_DETAIL, SSP_TEAM) + " ");
			}
			if(sb.length() > 0){
				sb.deleteCharAt(sb.length() - 1);
			}
			seasonTable[i][0] = ps.getYear().toString();
			seasonTable[i][1] = ps.getGamesPlayed().toString();
			seasonTable[i][2] = DOLLAR_FORMAT.format(ps.getSalary());
			seasonTable[i][3] = sb.toString();
			seasonTable[i][4] = ps.getBattingStats().getHits().toString();
			seasonTable[i][5] = ps.getBattingStats().getAtBats().toString();
			seasonTable[i][6] = DOUBLE_FORMAT.format(ps.getBattingAverage());
			seasonTable[i][7] = ps.getBattingStats().getHomeRuns().toString();
		}
		view.buildTable(seasonTable);
		
		StringBuilder footer = new StringBuilder();
		footer.append(view.buildLinkToSearch())
			  .append("<a href=\"index.htm\">Home</a>\r\n");
		view.setFooter(footer.toString());
	}
	
	/**
	 * Builds the header for a specific player
	 * @param player The player.
	 * @return The string representation of the header.
	 */
	private String buildPlayerHeader(Player player)
	{
		StringBuilder s = new StringBuilder();
		Date birthdate = player.getBirthDay();
		Date deathdate = player.getDeathDay();
		String birthday = birthdate != null ? DATE_FORMAT.format(birthdate) : "";
		String deathday = deathdate != null ? DATE_FORMAT.format(deathdate) : "";
		s.append("<h1>")
         .append(player.getName())
         .append(" (").append(player.getGivenName()).append(")\r\n")
         .append("</h1>")
         .append("<h2>")
         .append(birthday).append(" - ").append(deathday)
         .append("</h2>")
         .append("<h2>Born in ")
         .append(player.getBirthCity()).append(", ").append(player.getBirthState())            
         .append("</h2>")
         .append("<h2>")
         .append(player.getPositions())
         .append("</h2>");
        return s.toString();
	}
}

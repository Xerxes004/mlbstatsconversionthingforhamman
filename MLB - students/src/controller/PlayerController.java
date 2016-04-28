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
import util.MLBUtil;

/**
 * 
 * @author Seth Hamman
 * @author Wesley Kelly
 * @author Joel D. Sabol
 *
 */
public class PlayerController extends BaseController {
	
	private static final int SEARCH_TABLE_COLUMNS = 9;
	private static final int DETAILS_TABLE_COLUMNS = 8;
	private static final int HEADER_ROW = 0;

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
		List<Player> bos = HibernateUtil.retrievePlayersByName(name, exact, ALL_PAGES);
		view.printSearchResultsMessage(name, exact);
		buildPlayerSearchResultsTable(bos);
		//StringBuilder footer = new StringBuilder();
		view.buildFooter(view);
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
		Player player = (Player) HibernateUtil.retrievePlayerById(Integer.valueOf(id));
		if (player == null){
			return;
		}
		List<Player> list = new ArrayList<>();
		list.add(player);
		// Build the basic player information
		buildPlayerSearchResultsTable(list);
		
		// Build the charts
		view.buildCharts(id);
		view.buildHeader(player);
		
		// Build the season information
		buildPlayerDetailsTable(player);
		
		view.buildFooter(view);
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
					jsPlayer.put("firstgame", MLBUtil.DATE_FORMAT.format(first));
				}
				
				Date last = player.getLastGame();
				if(last != null){
					jsPlayer.put("lastgame", MLBUtil.DATE_FORMAT.format(player.getLastGame()));
				}
				jsPlayer.put("careerhomeruns", stats.getHomeRuns());
				jsPlayer.put("careerhits", stats.getHits());
				jsPlayer.put("careerbattingavg", MLBUtil.DOUBLE_FORMAT.format(stats.getBattingAverage()));
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
		String[][] table = new String[playerSearchResults.size() + TABLE_HEADER_SIZE][SEARCH_TABLE_COLUMNS];
		final int NAME_COL = 0;
		final int LIFETIME_SALARY_COL = 1;
		final int GAMES_PLAYED_COL = 2;
		final int FIRST_GAME_COL = 3;
		final int LAST_GAME_COL = 4;
		final int CAREER_HOME_RUNS_COL = 5;
		final int CAREER_HITS_COL = 6;
		final int CAREER_BATTING_AVERAGE_ROW = 7;
		final int CAREER_STEALS_COL = 8;
		
		table[HEADER_ROW][NAME_COL] = "Name";
		table[HEADER_ROW][LIFETIME_SALARY_COL] = "Lifetime Salary";
		table[HEADER_ROW][GAMES_PLAYED_COL] = "Games Played";
		table[HEADER_ROW][FIRST_GAME_COL] = "First Game";
		table[HEADER_ROW][LAST_GAME_COL] = "Last Game";
		table[HEADER_ROW][CAREER_HOME_RUNS_COL] = "Career Home Runs";
		table[HEADER_ROW][CAREER_HITS_COL] = "Career Hits";
		table[HEADER_ROW][CAREER_BATTING_AVERAGE_ROW] = "Career Batting Average";
		table[HEADER_ROW][CAREER_STEALS_COL] = "Career Steals";
		
		for (int i = 0; i < playerSearchResults.size(); i++) {
			Player player = playerSearchResults.get(i);
			PlayerCareerStats stats = new PlayerCareerStats(player);
			String pid = player.getId().toString();
			table[i + 1][NAME_COL] = view.encodeLink(new String[] { "id" }, new String[] { pid }, player.getName(), ACT_DETAIL,
					SSP_PLAYER);
			table[i + 1][LIFETIME_SALARY_COL] = MLBUtil.DOLLAR_FORMAT.format(stats.getSalary());
			table[i + 1][GAMES_PLAYED_COL] = stats.getGamesPlayed().toString();
			
			Date firstGameDate = player.getFirstGame();
			String firstGame = firstGameDate != null ? MLBUtil.DATE_FORMAT.format(firstGameDate) : "N/A";
			table[i + TABLE_HEADER_SIZE][FIRST_GAME_COL] = firstGame;
			
			Date lastGameDate = player.getLastGame();
			String lastGame = lastGameDate != null ? MLBUtil.DATE_FORMAT.format(lastGameDate) : "N/A";
			table[i + TABLE_HEADER_SIZE][LAST_GAME_COL] = lastGame;
			
			table[i + TABLE_HEADER_SIZE][CAREER_HOME_RUNS_COL] = stats.getHomeRuns().toString();
			table[i + TABLE_HEADER_SIZE][CAREER_HITS_COL] = stats.getHits().toString();
			table[i + TABLE_HEADER_SIZE][CAREER_BATTING_AVERAGE_ROW] = MLBUtil.DOUBLE_FORMAT.format(stats.getBattingAverage());
			table[i + TABLE_HEADER_SIZE][CAREER_STEALS_COL] = stats.getSteals().toString();
		}
		
		view.buildTable(table);
		view.buildFooter(view);
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
		
		Date temp = player.getBirthDay();
		String birthDay = temp != null ? MLBUtil.DATE_FORMAT.format(temp) : "N/A";
		jsPlayer.put("birthday", birthDay);
		
		temp = player.getBirthDay();
		String deathDay = temp != null ? MLBUtil.DATE_FORMAT.format(temp) : "N/A";
		jsPlayer.put("deathday", deathDay);
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
				jsSeason.put("battingavg", MLBUtil.DOUBLE_FORMAT.format(season.getBattingAverage()));
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
		
		// Build seasons table
		String[][] seasonTable = new String[seasons.size() + TABLE_HEADER_SIZE][DETAILS_TABLE_COLUMNS];
		final int YEAR_COL = 0;
		final int GAMES_PLAYED_COL = 1;
		final int SALARY_COL = 2;
		final int TEAMS_COL = 3;
		final int HITS_COL = 4;
		final int AT_BATS_COL = 5;
		final int BATTING_AVERAGE_COL = 6;
		final int HOME_RUNS_COL = 7;
		
		seasonTable[HEADER_ROW][YEAR_COL] = "Year";
		seasonTable[HEADER_ROW][GAMES_PLAYED_COL] = "Games Played";
		seasonTable[HEADER_ROW][SALARY_COL] = "Salary";
		seasonTable[HEADER_ROW][TEAMS_COL] = "Teams(s)";
		seasonTable[HEADER_ROW][HITS_COL] = "Hits";
		seasonTable[HEADER_ROW][AT_BATS_COL] = "At Bats";
		seasonTable[HEADER_ROW][BATTING_AVERAGE_COL] = "Batting Average";
		seasonTable[HEADER_ROW][HOME_RUNS_COL] = "Home Runs";

		int i = 0;
		for (PlayerSeason ps : list) {
			i++;
			List<TeamSeason> teams = player.getTeamSeason(ps.getYear());
			StringBuilder sb = new StringBuilder();
			for (TeamSeason ts : teams) {
				sb.append(view.encodeLink(new String[] { "id" }, new String[] { ts.getTeam().getId().toString() },
						ts.getTeam().getName(), ACT_DETAIL, SSP_TEAM) + " ");
			}
			if(sb.length() > 0){
				sb.deleteCharAt(sb.length() - 1);
			}
			seasonTable[i][YEAR_COL] = ps.getYear().toString();
			seasonTable[i][GAMES_PLAYED_COL] = ps.getGamesPlayed().toString();
			seasonTable[i][SALARY_COL] = MLBUtil.DOLLAR_FORMAT.format(ps.getSalary());
			seasonTable[i][TEAMS_COL] = sb.toString();
			seasonTable[i][HITS_COL] = ps.getBattingStats().getHits().toString();
			seasonTable[i][AT_BATS_COL] = ps.getBattingStats().getAtBats().toString();
			seasonTable[i][BATTING_AVERAGE_COL] = MLBUtil.DOUBLE_FORMAT.format(ps.getBattingAverage());
			seasonTable[i][HOME_RUNS_COL] = ps.getBattingStats().getHomeRuns().toString();
		}
		view.buildTable(seasonTable);
	}
}

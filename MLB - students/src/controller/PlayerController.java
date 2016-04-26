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

public class PlayerController extends BaseController {

	@Override
	public void initSSP(String query) {
		System.out.println("building dynamic html for player");
		view = new PlayerView();
		processSSP(query);
	}

	@Override
	public void initJSON(String query) {
		System.out.println("Building JSON for player");
		view = new PlayerView();
		processJSON(query);
	}

	protected void performJSONAction() {
		String action = keyVals.get("action");
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

	@Override
	protected void performAction() {
		String action = keyVals.get("action");
		System.out.println("playercontroller performing action: " + action);
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

	protected void processSearchForm() {
		view.buildSearchForm();
	}

	protected final void processJSONSearch() {
		String name = keyVals.get("name");
		String v = keyVals.get("exact");
		boolean exact = (v != null && v.equalsIgnoreCase("on"));
		List<Player> players = HibernateUtil.retrievePlayersByName(name, exact, 0);
		Integer count = HibernateUtil.retrieveCountPlayersByName(name, exact);
		try {
			buidJSONSearchResultsTablePlayer(players, count);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected final void processSearch() {
		String name = keyVals.get("name");
		if (name == null) {
			return;
		}
		String v = keyVals.get("exact");
		boolean exact = (v != null && v.equalsIgnoreCase("on"));
		List<Player> bos = HibernateUtil.retrievePlayersByName(name, exact, -1);
		view.printSearchResultsMessage(name, exact);
		buildSearchResultsTablePlayer(bos);
		StringBuilder footer = new StringBuilder();
		footer.append(view.buildLinkToSearch())
			  .append("<a href=\"index.htm\">Home</a>\r\n");
		view.setFooter(footer.toString());
	}

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
			buildJSONSearchResultsTablePlayerDetail(player);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected final void processDetails() {
		String id = keyVals.get("id");
		if (id == null) {
			return;
		}
		Player p = (Player) HibernateUtil.retrievePlayerById(Integer.valueOf(id));
		if (p == null)
			return;
		List<Player> list = new ArrayList<>();
		list.add(p);
		buildSearchResultsTablePlayer(list);
		buildSearchResultsTablePlayerDetail(p);
		((PlayerView)view).buildCharts(id);
		StringBuilder footer = new StringBuilder();
		footer.append(view.buildLinkToSearch())
			  .append("<a href=\"index.htm\">Home</a>\r\n");
		view.setFooter(footer.toString());
	}

	private void buidJSONSearchResultsTablePlayer(List<Player> players, Integer count) throws JSONException {
		JSONObject jsPlayerObj = new JSONObject();
		jsPlayerObj.put("count", count);
		JSONArray jsPlayers = new JSONArray();

		jsPlayerObj.put("items", jsPlayers);

		players.forEach((player) -> {
			JSONObject jsPlayer = new JSONObject();
			PlayerCareerStats stats = new PlayerCareerStats(player);
			try {
				jsPlayer.put("id", player.getId());
				jsPlayer.put("name", player.getName());
				jsPlayer.put("lifetimesalary", stats.getSalary());
				jsPlayer.put("gamesplayed", stats.getGamesPlayed());
				jsPlayer.put("firstgame", DATE_FORMAT.format(player.getFirstGame()));
				jsPlayer.put("lastgame", DATE_FORMAT.format(player.getLastGame()));
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

	private void buildSearchResultsTablePlayer(List<Player> bos) {
		// need a row for the table headers
		String[][] table = new String[bos.size() + 1][9];

		table[0][0] = "Name";
		table[0][1] = "Lifetime Salary";
		table[0][2] = "Games Played";
		table[0][3] = "First Game";
		table[0][4] = "Last Game";
		table[0][5] = "Career Home Runs";
		table[0][6] = "Career Hits";
		table[0][7] = "Career Batting Average";
		table[0][8] = "Career Steals";
		for (int i = 0; i < bos.size(); i++) {
			Player p = bos.get(i);
			PlayerCareerStats stats = new PlayerCareerStats(p);
			String pid = p.getId().toString();
			table[i + 1][0] = view.encodeLink(new String[] { "id" }, new String[] { pid }, p.getName(), ACT_DETAIL,
					SSP_PLAYER);
			table[i + 1][1] = DOLLAR_FORMAT.format(stats.getSalary());
			table[i + 1][2] = stats.getGamesPlayed().toString();
			table[i + 1][3] = formatDate(p.getFirstGame());
			table[i + 1][4] = formatDate(p.getLastGame());
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

	private void buildJSONSearchResultsTablePlayerDetail(Player player) throws JSONException {
		Set<String> positions = player.getPositions();

		JSONObject jsPlayer = new JSONObject();

		jsPlayer.put("name", player.getName());
		jsPlayer.put("givenname", player.getGivenName());
		jsPlayer.put("birthday", formatDate(player.getBirthDay()));
		jsPlayer.put("deathday", formatDate(player.getDeathDay()));
		jsPlayer.put("hometown", player.getBirthCity() + ", " + player.getBirthState());

		JSONArray jsPositions = new JSONArray();
		positions.forEach((position) -> {
			jsPositions.put(position);
		});

		jsPlayer.put("positions", jsPositions);

		// Get the seasons
		List<PlayerSeason> seasonList = new ArrayList<PlayerSeason>(player.getSeasons());
		// Sort them based on the year
		Collections.sort(seasonList, PlayerSeason.playerSeasonsComparator);
		JSONArray jsSeasons = new JSONArray();
		seasonList.forEach((season)->{
			JSONObject jsSeason = new JSONObject();
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
					Team team = teamseason.getTeam();
					try {
						jsTeam.put("id", team.getId());
						jsTeam.put("name", team.getName());
					} catch (JSONException e) {
						e.printStackTrace();
					}
					jsTeams.put(jsTeam);
				});
				jsSeason.put("teams", jsTeams);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			jsSeasons.put(jsSeason);
		});
		jsPlayer.put("items", jsSeasons);
		view.buildJSON(jsPlayer.toString());
	}

	private void buildSearchResultsTablePlayerDetail(Player player) {
		Set<PlayerSeason> seasons = player.getSeasons();
		List<PlayerSeason> list = new ArrayList<PlayerSeason>(seasons);
		Collections.sort(list, PlayerSeason.playerSeasonsComparator);
		view.setHeader(buildPlayerHeader(player));
		
		
		// now for seasons
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
			sb.deleteCharAt(sb.length() - 1);
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

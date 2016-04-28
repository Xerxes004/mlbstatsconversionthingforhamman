
package view;

import java.util.HashMap;

import bo.Player;
import bo.Team;
import bo.TeamSeason;

public class TeamView
    extends BaseView
{
    public TeamView()
    {
        title = "Team";

        // map team names to logo
        teamLogos = new HashMap<>();
        teamLogos.put("Boston Red Sox", "images/redsox.png");
        teamLogos.put("Arizona Diamondbacks", "images/dbacks.png");
        teamLogos.put("Los Angeles Angels of Anaheim", "images/laangels.png");
        teamLogos.put("Atlanta Braves", "images/atlbraves.png");
        teamLogos.put("Baltimore Orioles", "images/orioles.png");
        teamLogos.put("Chicago Cubs", "images/cubs.png");
        teamLogos.put("Chicago White Sox","images/whitesox.png");
        teamLogos.put("Cincinnati Reds","images/reds.png");
        teamLogos.put("Cleveland Indians","images/indians.jpg");
        teamLogos.put("Colorado Rockies","images/rockies.png");
        teamLogos.put("Detroit Tigers","images/tigers.png");
        teamLogos.put("Florida Marlins","images/marlins.png");
        teamLogos.put("Houston Astros","images/astros.png");
        teamLogos.put("Kansas City Royals","images/royals.png");
        teamLogos.put("Los Angeles Dodgers","images/dodgers.png");
        teamLogos.put("Milwaukee Brewers","images/brewers.png");
        teamLogos.put("Minnesota Twins","images/twins.png");
        teamLogos.put("New York Mets","images/mets.png");
        teamLogos.put("New York Yankees","images/yankees.png");
        teamLogos.put("Oakland Athletics","images/athletics.png");
        teamLogos.put("Philadelphia Phillies","images/phillies.png");
        teamLogos.put("Pittsburgh Pirates","images/pirates.png");
        teamLogos.put("San Diego Padres","images/padres.png");
        teamLogos.put("Seattle Mariners","images/mariners.png");
        teamLogos.put("San Francisco Giants","images/giants.png");
        teamLogos.put("St. Louis Cardinals","images/cardinals.png");
        teamLogos.put("Tampa Bay Rays","images/rays.png");
        teamLogos.put("Texas Rangers","images/rangers.png");
        teamLogos.put("Toronto Blue Jays","images/bluejays.png");
        teamLogos.put("Washington Nationals","images/nationals.png");
    }

    @Override
	public void buildSearchForm() {
		body.append("<form id='select2form' action=\"");
		body.append(title.toLowerCase());
		body.append(".ssp\" method=\"get\">\r\n");
		body.append("<h2>Dynamic Team Search</h2>");
		body.append("<div><select id='team-select' style='width:100%' name='id'><option></option></select></div>\r\n");
		body.append("<input type=\"hidden\" name=\"action\" value=\"details\">\r\n");
		body.append("</form>\r\n");
		body.append("<br/><br/>");
		body.append("<form action=\"");
		body.append(title.toLowerCase());
		body.append(".ssp\" method=\"get\">\r\n");
		body.append("<h2>Search All Teams</h2>");
		body.append("<input class='form-control input-lg' placeholder='Enter Team' type=\"text\" name=\"name\"><input type=\"checkbox\" name=\"exact\"> Exact Match?<br/>\r\n");
        body.append("<input type=\"hidden\" name=\"action\" value=\"search\">\r\n");
        body.append("<input class='btn btn-default' type=\"submit\" value=\"Submit\">\r\n");
		body.append("</form>\r\n");
	}
    
    @Override
    public void buildCharts(String id) {
    	body.append("<script src='highchartstheme.js'></script>");
		body.append("<script src='teamcharts.js'></script>");
		body.append("<script>var teamID=" + id + ";</script>");
    	body.append("<div id='chart-area'>");
		body.append("<div id='attendance'></div>");
		body.append("<div id='winslosses'></div>");
		body.append("</div>");
	}

    @Override
    public void buildHeader(Team team)
    {
    	String league = team.getLeague();
    	if (league.equals("NL"))
    	{
    		league = "National League";
    	}
    	else if (league.equals("AL"))
    	{
    		league = "American League";
    	}
    	
    	StringBuilder header = new StringBuilder();
    	
    	String logo = teamLogos.get(team.getName());
    	
    	if (logo != null)
    	{
    		header.append("<img id='logo' src='").append(logo).append("' />");
    	}
    	
    	header.append("<h1>").append(team.getName()).append("</h1>")
    	      .append("<h2>")
    	      .append(team.getYearFounded()).append(" - ").append(team.getYearLast())
    	      .append("</h2>")
    	      .append("<h2>").append(league).append("</h2>");
    	
    	super.header = header.toString();
    }
    
	/**
	 * Builds a header for the team roster page.
	 * @param teamSeason
	 * @return
	 */
    @Override
	public void buildHeader(TeamSeason teamSeason, String linkToTeam) {
    	Team team = teamSeason.getTeam();
    	
    	String league = team.getLeague();
    	if (league.equals("NL"))
    	{
    		league = "National League";
    	}
    	else if (league.equals("AL"))
    	{
    		league = "American League";
    	}
    	
    	StringBuilder header = new StringBuilder();
    	
    	String logo = teamLogos.get(team.getName());
    	
    	if (logo != null)
    	{
    		header.append("<img id='logo' src='").append(logo).append("' />");
    	}
    	
    	header.append("<h1>")
    		  .append(teamSeason.getYear()).append(" ").append(linkToTeam).append("</h1>")
    	      .append("<h2>").append(league).append("</h2>")
    	      .append("<h2>Team Roster</h2>");
    	
    	super.header = header.toString();
	}
}

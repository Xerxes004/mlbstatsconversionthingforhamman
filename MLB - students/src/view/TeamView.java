
package view;

import java.util.HashMap;

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
		body.append("<form action=\"");
		body.append(title.toLowerCase());
		body.append(".ssp\" method=\"get\">\r\n");
		body.append("Enter Team: <select id='team-select' class='js-example-responsive' name='name' style='width:50%'><option></option></select><input type=\"checkbox\" name=\"exact\"> Exact Match?\r\n");
		body.append("<input type=\"hidden\" name=\"action\" value=\"search\">\r\n");
		body.append("<input type=\"submit\" value=\"Submit\">\r\n");
		body.append("</form>\r\n");
	}
    
    
}

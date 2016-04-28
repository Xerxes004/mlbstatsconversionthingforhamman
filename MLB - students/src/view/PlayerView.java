
package view;

import java.util.Date;

import bo.Player;
import util.MLBUtil;

/**
 *
 * @author Wes Kelly
 * @author Joel D. Sabol
 */
public class PlayerView extends BaseView {

    public PlayerView() {
        title = "Player";
    }
    
    @Override
    public void buildSearchForm() {
    	body.append("<form id='select2form' action=\"");
		body.append(title.toLowerCase());
		body.append(".ssp\" method=\"get\">\r\n");
		body.append("<h2>Dynamic Player Search</h2>");
		body.append("Enter Player: <select id='player-select' class='js-example-responsive' name='id' style='width:50%'><option></option></select>\r\n");
		body.append("<input type=\"hidden\" name=\"action\" value=\"details\">\r\n");
		body.append("</form>\r\n");
		body.append("<br/><br/>");
        body.append("<form action=\"");
        body.append(title.toLowerCase());
        body.append(".ssp\" method=\"get\">\r\n");
        body.append("<h2>Search All Players</h2>");
        body.append("Enter name: <input type=\"text\" size=\"20\" name=\"name\"><input type=\"checkbox\" name=\"exact\"> Exact Match?\r\n");
        body.append("<input type=\"hidden\" name=\"action\" value=\"search\">\r\n");
        body.append("<input type=\"submit\" value=\"Submit\">\r\n");
        body.append("</form>\r\n"); 
    }
    
    public void buildCharts(String id) {
    	body.append("<script src='highchartstheme.js'></script>");
		body.append("<script src='playercharts.js'></script>");
		body.append("<script>var playerID=" + id + ";</script>");
    	body.append("<div id='chart-area'>");
		body.append("<div id='batavg'></div>");
		body.append("<div id='hits'></div>");
		body.append("<div id='homeruns'></div>");
		body.append("<div id='gamesplayed'></div>");
		body.append("</div>");
	}

    public void buildHeader(Player player)
    {
	    StringBuilder header = new StringBuilder();
		Date birthdate = player.getBirthDay();
		Date deathdate = player.getDeathDay();
		String birthday = birthdate != null ? MLBUtil.DATE_FORMAT.format(birthdate) : "";
		String deathday = deathdate != null ? MLBUtil.DATE_FORMAT.format(deathdate) : "";
		header.append("<h1>")
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
	    super.header = header.toString();
    }
}

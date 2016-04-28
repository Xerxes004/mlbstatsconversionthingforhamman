
package view;

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
    	body.append("<form class='form-horizontal' id='select2form' action=\"");
		body.append(title.toLowerCase());
		body.append(".ssp\" method=\"get\">\r\n");
		body.append("<h2>Dynamic Player Search</h2>");
		body.append("<div><select id='player-select' style='width:100%' name='id'><option></option></select></div>\r\n");
		body.append("<input type=\"hidden\" name=\"action\" value=\"details\">\r\n");
		body.append("</form>\r\n");
		body.append("<br/><br/>");
        body.append("<form action=\"");
        body.append(title.toLowerCase());
        body.append(".ssp\" method=\"get\">\r\n");
        body.append("<h2>Search All Players</h2>");
        body.append("<input class='form-control input-lg' placeholder='Enter Player' type=\"text\" name=\"name\"><input type=\"checkbox\" name=\"exact\"> Exact Match?<br/>\r\n");
        body.append("<input type=\"hidden\" name=\"action\" value=\"search\">\r\n");
        body.append("<input class='btn btn-default' type=\"submit\" value=\"Submit\">\r\n");
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
}

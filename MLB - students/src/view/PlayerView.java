/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 *
 * @author user
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
        body.append("Enter name: <input type=\"text\" size=\"20\" name=\"name\"><input type=\"checkbox\" name=\"exact\"> Exact Match?\r\n");
        body.append("<input type=\"hidden\" name=\"action\" value=\"search\">\r\n");
        body.append("<input type=\"submit\" value=\"Submit\">\r\n");
        body.append("</form>\r\n"); 
    }
    
    public void buildCharts(String id) {
		body.append("<div id='batavg' style='width:50%; min-width: 310px; height: 400px; margin 0 auto; float: left'></div>");
		body.append("<div id='hits' style='width:50%; min-width: 310px; height: 400px; margin 0 auto; float:left'></div>");
		body.append("<div id='homeruns' style='width:50%; min-width: 310px; height: 400px; margin 0 auto; float:left'></div>");
		body.append("<div id='gamesplayed' style='width:50%; min-width: 310px; height: 400px; margin 0 auto; float:left'></div>");
		body.append("<script>var playerID=" + id + ";</script>");
		body.append("<script src='highchartstheme.js'></script>");
		body.append("<script src='playercharts.js'></script>");
	}
}

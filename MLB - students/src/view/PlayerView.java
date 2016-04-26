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
        body.append("<form action=\"");
        body.append(title.toLowerCase());
        body.append(".ssp\" method=\"get\">\r\n");
        body.append("Enter name: <input type=\"text\" size=\"20\" name=\"name\"><input type=\"checkbox\" name=\"exact\"> Exact Match?\r\n");
        body.append("<input type=\"hidden\" name=\"action\" value=\"search\">\r\n");
        body.append("<input type=\"submit\" value=\"Submit\">\r\n");
        body.append("</form>\r\n"); 
    }
    
    public void buildCharts(String id) {
		body.append("<div id='batavg' style='min-width: 310px; height: 400px; margin 0 auto'></div>");
		body.append("<div id='hits' style='min-width: 310px; height: 400px; margin 0 auto'></div>");
		body.append("<div id='homeruns' style='min-width: 310px; height: 400px; margin 0 auto'></div>");
		body.append("<script>var playerID=" + id + ";</script>");
		body.append("<script src='playercharts.js'></script>");
	}

}

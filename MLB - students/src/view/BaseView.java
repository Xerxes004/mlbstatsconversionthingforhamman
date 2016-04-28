/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.util.Map;

import bo.Player;
import bo.Team;
import bo.TeamSeason;

/**
 *
 * @author user
 */
public abstract class BaseView
{

    protected String title;
    protected String header;
    protected String footer;
    protected Map<String, String> teamLogos;
    protected StringBuffer body = new StringBuffer();

    public abstract void buildSearchForm();

    public final String buildPage()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\r\n")
        	.append("<HTML>\r\n")
        	.append("<HEAD>\r\n")
        	.append("<TITLE>")
        	.append(title)
        	.append("</TITLE>\r\n")
        	.append("<script src='https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js'></script>")
        	.append("<link href='https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.2/css/select2.min.css' rel='stylesheet'/>")
        	.append("<script src='https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.2/js/select2.min.js'></script>")
        	.append("<script src='https://code.highcharts.com/highcharts.js'></script>")
        	.append("<script src='https://code.highcharts.com/modules/exporting.js'></script>")
        	//.append("<script src='https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js' integrity='sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS' crossorigin='anonymous'></script>")
        	.append("<link rel='stylesheet' href='http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css'>")
        	.append("<link rel='stylesheet' type='text/css' href='style.css'>\r\n")
        	.append("<script src='selector.js'></script>")
        	.append("</HEAD>\r\n")
        	.append("<BODY>\r\n")
        	.append("<div id='background-div'></div>")
        	.append("<img src='./images/mlb.png' id='mlb-logo' z-index='-1'>")
        	.append("<div id=\'wrapper\'>\r\n");
        	if (header != null)
        	{
	        	sb.append("<header>\r\n")
	        	  .append(header)
	        	  .append("</header>\r\n");
        	}
          sb.append(body);
          if (footer != null)
          {
        	sb.append("<footer>\r\n")
        	  .append(footer)
        	  .append("</footer>\r\n"); 
          }
          sb.append("</div>\r\n</BODY>\r\n")//end wrapper div
        	.append("</HTML>\r\n");
        return sb.toString();
    }
    
    public void buildHeader(Team team)
    {
    	this.header = "<h1>Default Team Header</h1>";
    }
    public void buildHeader(TeamSeason teamseason, String linkToTeam)
    {
    	this.header = "<h1>Default team season header</h1>";
    }
    public void buildHeader(Player player)
    {
    	this.header = "<h1>Default player header</h1>";
    }
    public void buildFooter(BaseView view)
    {
    	StringBuilder footer = new StringBuilder();
		footer.append(view.buildLinkToSearch())
			  .append("<a href=\"index.htm\">Home</a>\r\n");
		this.footer = footer.toString();
    }
    
    public final String buildJSONResponse(){
    	return body.toString();
    }

    public final String getLogo(String teamName)
    {
        if (teamLogos != null)
        {
            return teamLogos.get(teamName);
        }
        else
        {
            return "";
        }
    }

    public final String buildLinkToSearch()
    {
    	StringBuilder s = new StringBuilder();
        s.append("<a href=\"")
         .append(title.toLowerCase())
         .append(".ssp?action=searchform\">Search for a ")
         .append(title)
         .append("</a>\r\n");
        return s.toString();
    }

    public final void printMessage(String msg)
    {
        body.append("<p>");
        body.append(msg);
        body.append("</p>\r\n");
    }

    public final void printSearchResultsMessage(String name, boolean exact)
    {
        body.append("<p>");
        body.append(title);
        if (exact)
        {
            body.append("s with name matching '");
        }
        else
        {
            body.append("s with name containing '");
        }
        body.append(name);
        body.append("':</p>\r\n");
    }

    public final void buildTable(String[][] table)
    {
        body.append("<table>\r\n");
        // print table header row
        body.append("<tr>");
        for (int i = 0; i < table[0].length; i++)
        {
            body.append("<th>");
            body.append(table[0][i]);
            body.append("</th>\r\n");
        }
        body.append("</tr>\r\n");
        // print table rows
        for (int row = 1; row < table.length; row++)
        {
            body.append("<tr class='")
                .append(row % 2 == 0 ? "dr'>\r\n" : "lr'>\r\n");
            for (int col = 0; col < table[row].length; col++)
            {
                body.append("<td>")
                    .append(table[row][col])
                    .append("</td>\r\n");
            }
            body.append("</tr>\r\n");
        }
        body.append("</table>\r\n");
    }
    
    public final void buildTable(String[][] table, String tableId)
    {
        body.append("<table id='")
        	.append(tableId)
        	.append("'>\r\n");
        // print table header row
        body.append("<tr>");
        for (int i = 0; i < table[0].length; i++)
        {
            body.append("<th>");
            body.append(table[0][i]);
            body.append("</th>\r\n");
        }
        body.append("</tr>\r\n");
        // print table rows
        for (int row = 1; row < table.length; row++)
        {
            body.append("<tr class='")
                .append(row % 2 == 0 ? "dr'>\r\n" : "lr'>\r\n");
            for (int col = 0; col < table[row].length; col++)
            {
                body.append("<td>")
                    .append(table[row][col])
                    .append("</td>\r\n");
            }
            body.append("</tr>\r\n");
        }
        body.append("</table>\r\n");
    }

    /**
     * Encode a link in the proper format.
     *
     * @param key String[] of keys of the different args--length must match
     * val[]
     * @param val String[] of vals of the different args--length must match
     * key[]
     * @param display is what will be displayed as the link to click on
     * @param action is the action to take
     * @param ssp is either 'player' or 'team'
     */
    public final String encodeLink(String[] key, String[] val, String display, String action, String ssp)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<a href=\"");
        sb.append(ssp);
        sb.append(".ssp?");
        for (int i = 0; i < key.length; i++)
        {
            sb.append(key[i]);
            sb.append("=");
            sb.append(encodeURL(val[i]));
            sb.append("&");
        }
        sb.append("action=");
        sb.append(action);
        sb.append("\">");
        sb.append(display);
        sb.append("</a>");
        return sb.toString();
    }

    protected final String encodeURL(String s)
    {
        s = s.replace(" ", "+");
        return s;
    }
    
    public final void buildJSON(String s){
    	body.append(s);
    }
    
    public abstract void buildCharts(String id);
}

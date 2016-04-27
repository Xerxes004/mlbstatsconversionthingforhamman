package controller;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import view.BaseView;

public abstract class BaseController {

    protected DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    protected DecimalFormat DOLLAR_FORMAT = new DecimalFormat("$#,##0.00");
    protected DecimalFormat DOUBLE_FORMAT = new DecimalFormat(".000");
    protected DecimalFormat INTEGER_FORMAT = new DecimalFormat("#,###");
    protected Map<String, String> keyVals = new HashMap<String, String>();
    protected BaseView view;
    protected static final String ACT_SEARCHFORM = "searchform";
    protected static final String ACT_SEARCH = "search";
    protected static final String ACT_DETAIL = "details";
    protected static final String ACT_ROSTER = "roster";
    protected static final String DATA_JSON = "json";
    protected static final String SSP_PLAYER = "player";
    protected static final String SSP_TEAM = "team";

    protected final void processSSP(String query) {
        String q = decodeURL(query);
        parseQuery(q);
        performAction();
    }
    
    protected final void processJSON(String query){
    	String q = decodeURL(query);
    	parseQuery(q);
    	performJSONAction();
    }
    
    /**
     * Determines which dynamic HTML page to render.
     */
    protected abstract void performAction();
    
    /**
     * Determines which JSON operation to perform.
     */
    protected abstract void performJSONAction();
    
    /**
     * Initializes the controller to respond to a dynamic HTML request.
     * @param query The request to respond to.
     */
    public abstract void initSSP(String query);
    
    /**
     * Initializes the controller to respond to a JSON request.
     * @param query The request to respond to.
     */
    public abstract void initJSON(String query);
    	
	protected final String decodeURL(String s) {
        // spaces are replaced by '+' in textfields
        s = s.replaceAll("\\+", " ");
        // '=&' indicates the field was left blank; 
        // replace w/ space so String.split on '&' works
        s = s.replaceAll("=&", "= &");
        return s;
    }

    protected final void parseQuery(String query) {
        String[] queries;
        if (query.contains("&")) {
            queries = query.split("&");
        } else {
            queries = new String[]{query};
        }
        for (String q : queries) {
            String[] kvPair = q.split("=");
            String k = kvPair[0].trim();
            String v = kvPair[1].trim();
            keyVals.put(k, v);
            System.out.println("found keyvals=[" + k + "] [" + v + "]");
        }
    }

    public String response() {
        System.out.println("returning the dynamic webpage");
        return view.buildPage();
    }
    
    public String jsonResponse(){
    	return view.buildJSONResponse();
    }
    
    protected String formatDate(Date d) {
    	String dstr = "";
        if (d!=null) {
        	dstr = DATE_FORMAT.format(d);
        }
        return dstr;
    }
    
}
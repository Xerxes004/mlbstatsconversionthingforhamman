package view;

import java.util.HashMap;

public class TeamView extends BaseView {
		public TeamView() {
		title = "Team Overview";
		
		teamLogos = new HashMap<>();
		teamLogos.put("Boston Red Sox", "images/redsox.png");
	}

	@Override
	public void buildSearchForm() {
		body.append("<form action=\"");
		body.append(title.toLowerCase());
		body.append(".ssp\" method=\"get\">\r\n");
		body.append("Enter Team: <input type=\"text\" size=\"20\" name=\"name\"><input type=\"checkbox\" name=\"exact\"> Exact Match?\r\n");
		body.append("<input type=\"hidden\" name=\"action\" value=\"search\">\r\n");
		body.append("<input type=\"submit\" value=\"Submit\">\r\n");
		body.append("</form>\r\n");
	}
}

package controller;

import view.TeamView;

public class TeamController extends BaseController {

	@Override
	protected void performAction() {
		String action = keyVals.get("action");
		System.out.println("Performing action: " + action);
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

	@Override
	public void init(String query) {
		System.out.println("building team html");
		view = new TeamView();
		process(query);
	}

	protected void processSearchForm() {
		view.buildSearchForm();
	}

	protected void processSearch() {

	}

	protected void processDetails() {

	}

}

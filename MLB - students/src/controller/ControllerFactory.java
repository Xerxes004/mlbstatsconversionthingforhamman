/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

/**
 *
 * @author user
 */
public class ControllerFactory {

    private static final String SSP_PLAYER = "player";
    private static final String SSP_TEAM = "team";

    public static BaseController getServerApp(String name) {
        BaseController bsa = null;
        switch (name.toLowerCase()) {
		case SSP_PLAYER:
			bsa = new PlayerController();
			break;
		
		case SSP_TEAM:
			bsa = new TeamController();
			break;
		default:
			System.out.println("Unknown Controller");
			break;
		}
        return bsa;
    }
}


package controller;

import dataaccesslayer.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import bo.Player;
import bo.PlayerSeason;
import bo.Team;
import bo.TeamSeason;
import view.TeamView;

public class TeamController
    extends BaseController
{

    @Override
    protected void performAction()
    {
        String action = keyVals.get("action");
        System.out.println("Performing action: " + action);
        switch (action.toLowerCase())
        {
            case ACT_SEARCHFORM:
                processSearchForm();
                break;
            case ACT_SEARCH:
                processSearch();
                break;
            case ACT_DETAIL:
                processDetails();
                break;
            case ACT_ROSTER:
                processRoster();
                break;
            default:
                break;
        }
    }

    @Override
    public void init(String query)
    {
        System.out.println("building team html");
        view = new TeamView();
        process(query);
    }

    protected void processSearchForm()
    {
        view.buildSearchForm();
    }

    protected void processSearch()
    {
        String teamName = keyVals.get("name");
        if (teamName == null)
        {
            return;
        }
        String valExact = keyVals.get("on");
        boolean exactMatch = valExact != null && valExact.equalsIgnoreCase("on");
        List<Team> teams = HibernateUtil.retrieveTeamsByName(teamName, exactMatch);
        view.printSearchResultsMessage(teamName, exactMatch);
        buildSearchResultsTableTeam(teams);
        view.buildLinkToSearch();
    }

    protected void processDetails()
    {
        String teamId = keyVals.get("id");
        if (teamId == null)
        {
            return;
        }
        Team team = (Team) HibernateUtil.retrieveTeamById(Integer.valueOf(teamId));
        if (team == null)
        {
            return;
        }
        buildSearchResultsTableTeamDetail(team);
        view.buildLinkToSearch();
    }

    protected void processRoster()
    {
        Integer teamId = new Integer(keyVals.get("id"));
        Integer year = new Integer(keyVals.get("year"));

        TeamSeason teamSeason = HibernateUtil.retrieveTeamSeason(teamId, year);

        buildRosterTable(teamSeason);
    }

    private void buildSearchResultsTableTeam(List<Team> teams)
    {
        String[][] table = new String[teams.size() + 1][4];
        //table[0][0] = "Id";
        table[0][0] = "Name";
        table[0][1] = "League";
        table[0][2] = "Year Founded";
        table[0][3] = "Year Last";
        for (int i = 1; i <= teams.size(); i++)
        {
            Team team = teams.get(i - 1);
            String teamName = team.getName().toString();
            String teamID = team.getId().toString();
            //table[i][0] = view.encodeLink(new String[] { "id" }, new String[] { teamID }, teamID, ACT_DETAIL, SSP_TEAM);
            table[i][0] = view.encodeLink(new String[]
            {
                "id"
            }, new String[]
            {
                teamID
            }, teamName, ACT_DETAIL, SSP_TEAM);
            table[i][1] = team.getLeague();
            table[i][2] = team.getYearFounded().toString();
            table[i][3] = team.getYearLast().toString();
        }
        view.buildTable(table);
    }

    // individual team result
    private void buildSearchResultsTableTeamDetail(Team team)
    {
        buildHeader(team);

        Set<TeamSeason> ts = team.getSeasons();
        ArrayList<TeamSeason> entries = new ArrayList<>(ts);
        String[][] seasonTable = new String[ts.size() + 1][7];

        seasonTable[0][0] = "Year";
        seasonTable[0][1] = "Games Played";
        seasonTable[0][2] = "Roster";
        seasonTable[0][3] = "Wins";
        seasonTable[0][4] = "Losses";
        seasonTable[0][5] = "Rank";
        seasonTable[0][6] = "Attendance";

        int i = 1;
        for (TeamSeason entry : entries)
        {
            String teamId = entry.getTeam().getId().toString();
            String year = entry.getYear().toString();
            String link = view.encodeLink(new String[]
            {
                "id", "year"
            }, new String[]
            {
                teamId, year
            }, "Roster",
                ACT_ROSTER, SSP_TEAM);
            seasonTable[i][0] = year;
            seasonTable[i][1] = entry.getGamesPlayed().toString();
            seasonTable[i][2] = link;
            seasonTable[i][3] = entry.getWins().toString();
            seasonTable[i][4] = entry.getLosses().toString();
            seasonTable[i][5] = entry.getRank().toString();
            String attendance = INTEGER_FORMAT.format(entry.getTotalAttendance());
            seasonTable[i][6] = attendance.toString();
            i++;
        }

        view.buildTable(seasonTable);
    }

    private void buildHeader(Team team)
    {
        StringBuilder header = new StringBuilder();
        String logo = view.getLogo(team.getName());
        if (logo != null)
        {
            header.append("<img id='logo' src='")
                .append(view.getLogo(team.getName()))
                .append("'")
                .append(" alt='")
                .append(team.getName())
                .append("'")
                .append(" />");
        }
        header.append("<h1>")
            .append(team.getName())
            .append("</h1>")
            .append("<h3>League - ")
            .append(team.getLeague())
            .append("</h3>")
            .append(team.getYearFounded())
            .append(" - ")
            .append(team.getYearLast());

        view.setHeader(header.toString());
    }
    
    private void buildHeader(TeamSeason teamSeason)
    {
        Team team = teamSeason.getTeam();
        int year = teamSeason.getYear();
        StringBuilder header = new StringBuilder();
        String logo = view.getLogo(team.getName());
        if (logo != null)
        {
            header.append("<img id='logo' src='")
                .append(view.getLogo(team.getName()))
                .append("'")
                .append(" alt='")
                .append(team.getName())
                .append("'")
                .append(" />");
        }
        header.append("<h1>")
            .append(team.getName())
            .append("</h1>")
            .append("<h3>League - ")
            .append(team.getLeague())
            .append("</h3>")
            .append(year)
            .append(" Season Roster");

        view.setHeader(header.toString());
    }

    // search results
    private void buildRosterTable(TeamSeason teamSeason)
    {
        buildHeader(teamSeason);
        
        ArrayList<Player> roster = new ArrayList<>(teamSeason.getRoster());
        String rosterTable[][] = new String[roster.size() + 1][3];
        rosterTable[0][0] = "Name";
        rosterTable[0][1] = "Games Played";
        rosterTable[0][2] = "Salary";

        int i = 1;
        for (Player player : roster)
        {
            PlayerSeason playerSeason = player.getPlayerSeason(teamSeason.getYear());
            rosterTable[i][0] = view.encodeLink(new String[]
            {
                "id"
            }, new String[]
            {
                player.getId().toString()
            }, player.getName(), ACT_DETAIL, SSP_PLAYER);
            rosterTable[i][1] = playerSeason.getGamesPlayed().toString();
            rosterTable[i][2] = DOLLAR_FORMAT.format(playerSeason.getSalary());
            i++;
        }

        view.buildTable(rosterTable, "roster-table");
    }

}

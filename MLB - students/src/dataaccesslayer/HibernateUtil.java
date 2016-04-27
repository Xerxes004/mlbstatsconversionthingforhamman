package dataaccesslayer;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import bo.Player;
import bo.Team;
import bo.TeamSeason;

public class HibernateUtil {

	private static final SessionFactory sessionFactory;
	public static final int RESULTS_PER_PAGE = 6;

	static {
		try {
			Configuration cfg = new Configuration().addAnnotatedClass(bo.Player.class)
					.addAnnotatedClass(bo.TeamSeason.class).addAnnotatedClass(bo.PlayerSeason.class)
					.addAnnotatedClass(bo.BattingStats.class).addAnnotatedClass(bo.CatchingStats.class)
					.addAnnotatedClass(bo.FieldingStats.class).addAnnotatedClass(bo.PitchingStats.class)
					.addAnnotatedClass(bo.TeamSeason.class).addAnnotatedClass(bo.Team.class).configure();
			StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
					.applySettings(cfg.getProperties());
			sessionFactory = cfg.buildSessionFactory(builder.build());
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * Gets a player from the DB with the specified ID.
	 * @param id The id of the desired player.
	 * @return The hydrated player or null if it does not exist.
	 */
	public static Player retrievePlayerById(Integer id) {
		Player p = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();
		try {
			tx.begin();
			org.hibernate.Query query;
			query = session.createQuery("from bo.Player where id = :id ");
			query.setParameter("id", id);
			
			p = (Player) query.uniqueResult();
			if (p != null) {
				Hibernate.initialize(p.getSeasons());
				Hibernate.initialize(p.getTeams());
			}
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			if (session.isOpen())
				session.close();
		}
		return p;
	}

	/**
	 * Retrieves a list of players having the specified name.
	 * @param nameQuery The name of the player to be used in the query.
	 * @param exactMatch exactMatch Whether an exact match should be used.
	 * @param page The page of players to return; any number less than 0 will return all players.
	 * @return The list of players with the specified name, could potentially be an empty list.
	 */
	@SuppressWarnings("unchecked")
	public static List<Player> retrievePlayersByName(String nameQuery, Boolean exactMatch, Integer page) {
		List<Player> list = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();
		try {
			tx.begin();
			org.hibernate.Query query;
			if (exactMatch) {
				query = session.createQuery("from bo.Player p where name = :name order by p.name asc");
			} else {
				query = session.createQuery("from bo.Player p where name like '%' + :name + '%' order by p.name asc");
			}
			query.setParameter("name", nameQuery);
			if(page >= 0){
				query.setFirstResult(page * RESULTS_PER_PAGE);
				query.setMaxResults(RESULTS_PER_PAGE);
			}
			list = query.list();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			if (session.isOpen())
				session.close();
		}
		return list;
	}
	
	/**
	 * Gets the count of the players in the player search result.
	 * @param nameQuery The name of the player to be used in the query.
	 * @param exactMatch Whether an exact match should be used.
	 * @return The count of the players.
	 */
	public static Integer retrieveCountPlayersByName(String nameQuery, Boolean exactMatch) {
		Integer count = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();
		try {
			tx.begin();
			org.hibernate.Query query;
			
			// Choose the query based on whether an exact match is desired
			if (exactMatch) {
				query = session.createQuery("select count(*) from bo.Player p where name = :name");
			} else {
				query = session.createQuery("select count(*) from bo.Player p where name like '%' + :name + '%'");
			}
			query.setParameter("name", nameQuery);
			
			// Parses the int from the result
			count = java.lang.Math.toIntExact((Long) query.uniqueResult());
			
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			if (session.isOpen())
				session.close();
		}
		return count;
	}

	/**
	 * Persists a player to the database
	 * 
	 * @param player The player to persist to the database.
	 * @return True if the player was persisted to the database, false otherwise
	 */
	public static boolean persistPlayer(Player player) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();
		try {
			tx.begin();
			session.save(player);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			return false;
		} finally {
			if (session.isOpen())
				session.close();
		}
		return true;
	}

	/**
	 * Persists a team to the database
	 * 
	 * @param team The team to persist to the database.
	 * @return True if the team was persisted to the database, false otherwise
	 */
	public static boolean persistTeam(Team team) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();
		try {
			tx.begin();
			session.save(team);
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
			return false;
		} finally {
			if (session.isOpen())
				session.close();
		}
		return true;
	}

	/**
	 * Retrieves a unique team based on the ID.
	 * @param id The id of the team.
	 * @return The team having the specified ID or null if it does not exist.
	 */
	public static Team retrieveTeamById(Integer id) {
		Team team = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();
		try {
			tx.begin();
			org.hibernate.Query query;
			query = session.createQuery("from bo.Team where id = :id ");
			query.setParameter("id", id);
			
			// Gets the unique team from the query
			team = (Team) query.uniqueResult();
			if (team != null) {
				Hibernate.initialize(team.getSeasons());
			}
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			if (session.isOpen())
				session.close();
		}
		return team;
	}

	/**
	 * Gets page*RESULTS_PER_PAGE teams with the specified name from the database.
	 * @param nameQuery The name to search for.
	 * @param exactMatch Whether to search using an exact match.
	 * @param page The page to get, less then 0 returns all teams.
	 * @return A list containing a page of teams with the specified name, could be empty.
	 */
	@SuppressWarnings("unchecked")
	public static List<Team> retrieveTeamsByName(String nameQuery, Boolean exactMatch, Integer page) {
		List<Team> list = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();
		try {
			tx.begin();
			org.hibernate.Query query;
			if (exactMatch) {
				query = session.createQuery("from bo.Team t where name = :name order by t.name");
			} else {
				query = session.createQuery("from bo.Team t where name like '%' + :name + '%' order by t.name");
			}
			
			if(page >= 0){
				query.setFirstResult(page * RESULTS_PER_PAGE);
				query.setMaxResults(RESULTS_PER_PAGE);
			}
			
			query.setParameter("name", nameQuery);
			list = query.list();
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			if (session.isOpen())
				session.close();
		}
		return list;
	}
	
	/**
	 * Retrieves the total count of the teams with the specified name.
	 * @param nameQuery the name of the team.
	 * @param exactMatch Whether to search using exact match.
	 * @return The total count of the teams with the specified name.
	 */
	public static Integer retrieveTeamsByNameCount(String nameQuery, Boolean exactMatch) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();
		Integer count = null;
		try {
			tx.begin();
			org.hibernate.Query query;
			if (exactMatch) {
				query = session.createQuery("select count(*) from bo.Team t where name = :name");
			} else {
				query = session.createQuery("select count(*) from bo.Team t where name like '%' + :name + '%'");
			}
			query.setParameter("name", nameQuery);
			count = java.lang.Math.toIntExact((Long)query.uniqueResult());
			tx.commit();
		} catch (Exception e) {
			tx.rollback();
			e.printStackTrace();
		} finally {
			if (session.isOpen())
				session.close();
		}
		return count;
	}

	/**
	 * Retrieves a season of a given team and a specified year.
	 * @param teamId The id of the team.
	 * @param year The year of the season.
	 * @return The team season or null if it does not exist.
	 */
	public static TeamSeason retrieveTeamSeason(Integer teamId, Integer year) {
		TeamSeason teamSeason = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try {
			tx.begin();
			System.out.println("Querying DB");
			org.hibernate.Query query = session
					.createQuery("from bo.TeamSeason t where t.id.team.teamId = :teamId and t.id.seasonYear= :year");
			query.setParameter("teamId", teamId);
			query.setParameter("year", year);

			teamSeason = (TeamSeason) query.uniqueResult();
			if(teamSeason != null){
				Hibernate.initialize(teamSeason.getRoster());
			}

			tx.commit();
			System.out.println("Committing Transaction");
		} catch (Exception e) {
			System.out.println("Rolling Back Transaction");
			tx.rollback();
			e.printStackTrace();
		} finally {
			if (session.isOpen()) {
				session.close();
			}
		}
		return teamSeason;
	}
}
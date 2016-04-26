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

	public static Player retrievePlayerById(Integer id) {
		Player p = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();
		try {
			tx.begin();
			org.hibernate.Query query;
			query = session.createQuery("from bo.Player where id = :id ");
			query.setParameter("id", id);
			if (!query.list().isEmpty()) {
				p = (Player) query.list().get(0);
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
	
	public static Integer retrieveCountPlayersByName(String nameQuery, Boolean exactMatch) {
		Integer count = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();
		try {
			tx.begin();
			org.hibernate.Query query;
			if (exactMatch) {
				query = session.createQuery("select count(*) from bo.Player p where name = :name");
			} else {
				query = session.createQuery("select count(*) from bo.Player p where name like '%' + :name + '%'");
			}
			query.setParameter("name", nameQuery);
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
	 * @param player
	 *            The player to persist to the database.
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
	 * @param team
	 *            The team to persist to the database.
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

	public static Team retrieveTeamById(Integer id) {
		Team team = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();
		try {
			tx.begin();
			org.hibernate.Query query;
			query = session.createQuery("from bo.Team where id = :id ");
			query.setParameter("id", id);
			if (!query.list().isEmpty()) {
				team = (Team) query.list().get(0);
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
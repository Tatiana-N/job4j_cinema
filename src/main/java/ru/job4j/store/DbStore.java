package ru.job4j.store;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.persistence.Account;
import ru.job4j.persistence.Ticket;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@Slf4j
public class DbStore {
	private static final String ERROR = "ошибка при выполнении запроса к базе данных";
	private final BasicDataSource pool = new BasicDataSource();
	public DbStore() {
		try {
			Properties properties = new Properties();
			properties.load(DbStore.class.getClassLoader().getResourceAsStream("app.properties"));
			pool.setDriverClassName(properties.getProperty("db.driver"));
			pool.setUrl(properties.getProperty("db.url"));
			pool.setUsername(properties.getProperty("db.login"));
			pool.setPassword(properties.getProperty("db.password"));
			pool.setMaxIdle(10);
			pool.setMinIdle(5);
		} catch (IOException e) {
			log.error(ERROR, e);
			throw new RuntimeException("Не удалось установить соединение с базой данных");
		}
	}
	
	public Collection<Ticket> findAllTicketsWithBooked(int sessionId) {
		List<Ticket> ticketList = new ArrayList<>();
		try (PreparedStatement ps = pool.getConnection().prepareStatement("SELECT * FROM ticket "
				+ "WHERE session_id = 0 or session_id = ?")) {
			ps.setInt(1, sessionId);
		return 	getTicketListFromResultSet(ps);
		} catch (Exception e) {
			log.error(ERROR, e);
		}
		return ticketList;
	}
	
	public Collection<Ticket> findAllTicketsForDelete(Ticket ticket) {
		List<Ticket> ticketList = new ArrayList<>();
		try (PreparedStatement ps = pool.getConnection().prepareStatement("SELECT * FROM Ticket "
				+ "where row = ? and cell = ? and sesseon_id != 0")) {
			ps.setInt(1, ticket.getRow());
			ps.setInt(2, ticket.getCell());
		return	getTicketListFromResultSet(ps);
		} catch (Exception e) {
			log.error(ERROR, e);
		}
		return ticketList;
	}
	
	public Ticket create(Ticket ticket) {
		try (PreparedStatement ps = pool.getConnection().prepareStatement("INSERT into ticket  (account_id, film_id,session_id,row,cell)"
				+ "values (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, 1);
			ps.setInt(2, ticket.getFilmId());
			ps.setInt(3, ticket.getSessionId());
			ps.setInt(4, ticket.getRow());
			ps.setInt(5, ticket.getCell());
			ps.execute();
			try (ResultSet id = ps.getGeneratedKeys()) {
				if (id.next()) {
					ticket = new Ticket(id.getInt(1), ticket.getAccountId(), ticket.getCell(), ticket.getRow(), ticket.getFilmId(), ticket.getSessionId());
				}
			}
		} catch (SQLException throwable) {
			throwable.printStackTrace();
		}
		return ticket;
	}
	
	public Collection<Ticket> findAllTicketsForPayment(int sessionId) {
		try (PreparedStatement ps = pool.getConnection().prepareStatement("SELECT * FROM ticket WHERE session_id = ?")) {
			ps.setInt(1, sessionId);
			return getTicketListFromResultSet(ps);
		} catch (Exception e) {
			log.error(ERROR, e);
		}
		return new ArrayList<>();
	}
	
	private Collection<Ticket> getTicketListFromResultSet(PreparedStatement ps) throws SQLException {
		List<Ticket> ticketList = new ArrayList<>();
		try (ResultSet it = ps.executeQuery()) {
			while (it.next()) {
				ticketList.add(new Ticket(it.getInt("id"),
						it.getInt("account_id"),
						it.getInt("cell"),
						it.getInt("row"),
						it.getInt("film_id"),
						it.getInt("session_id")));
			}
		}
		return ticketList;
	}
	
	public Account save(Account account) {
		try (PreparedStatement ps = pool.getConnection().prepareStatement("INSERT into account  (username, email,phone) "
				+ "values (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, account.getUsername());
			ps.setString(2, account.getEmail());
			ps.setString(3, account.getPhone());
			ps.execute();
			try (ResultSet id = ps.getGeneratedKeys()) {
				if (id.next()) {
					account = new Account(id.getInt(1), account.getUsername(), account.getPhone(), account.getEmail());
				}
			}
		} catch (SQLException e) {
			log.error(ERROR, e);
		}
		return account;
	}
	
	public Account findAccount(Account account) {
		try (PreparedStatement ps = pool.getConnection().prepareStatement("SELECT * from account ")) {
			try (ResultSet id = ps.executeQuery()) {
				while (id.next()) {
					if (id.getString("phone").equals(account.getPhone())
							&& id.getString("email").equals(account.getEmail())) {
						account = new Account(id.getInt(1), account.getUsername(), account.getPhone(), account.getEmail());
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return account;
	}
	
	public Boolean update(Ticket ticket) {
		try (PreparedStatement ps = pool.getConnection().prepareStatement("UPDATE ticket SET account_id = ?, session_id = 0 "
				+ "where id = ?")) {
			ps.setInt(1, ticket.getAccountId());
			ps.setInt(2, ticket.getId());
			boolean execute = ps.execute();
			findAllTicketsForDelete(ticket).forEach(t -> deleteTicket(t.getId()));
			return execute;
		} catch (Exception e) {
			log.error(ERROR, e);
		}
		return false;
	}
	
	public void deleteTicket(Integer id) {
		try (PreparedStatement ps = pool.getConnection().prepareStatement("DELETE FROM ticket WHERE id = (?)")) {
			ps.setInt(1, id);
			ps.execute();
		} catch (Exception e) {
			log.error(ERROR, e);
		}
	}
}
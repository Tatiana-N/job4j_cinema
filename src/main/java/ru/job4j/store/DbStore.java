package ru.job4j.store;

import lombok.extern.slf4j.Slf4j;
import ru.job4j.persistence.Account;
import ru.job4j.persistence.Ticket;

import java.io.IOException;
import java.sql.*;
import java.util.*;

@Slf4j
public class DbStore {
	private final Connection connection;
	private static final String ERROR = "ошибка при выполнении запроса к базе данных";
	
	public DbStore() {
		try {
			Properties properties = new Properties();
			properties.load(DbStore.class.getClassLoader().getResourceAsStream("app.properties"));
			Class.forName(properties.getProperty("db.driver"));
			connection = DriverManager.getConnection(properties.getProperty("db.url"), properties.getProperty("db.login"),
					properties.getProperty("db.password"));
		} catch (SQLException | IOException | ClassNotFoundException e) {
			log.error(ERROR, e);
			throw new RuntimeException("Не удалось установить соединение с базой данных");
		}
	}
	
	public Collection<Ticket> findAllTicketsWithBooked(int sessionId) {
		return findAllTickets().stream()
				.filter(ticket -> ticket.getSessionId() == 0 || ticket.getSessionId() == sessionId).toList();
	}
	
	public Collection<Ticket> findAllTickets() {
		List<Ticket> ticketList = new ArrayList<>();
		try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM Ticket ")) {
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
		} catch (Exception e) {
			log.error(ERROR, e);
		}
		return ticketList;
	}
	
	public Ticket create(Ticket ticket) {
		try (PreparedStatement ps = connection.prepareStatement("INSERT into ticket  (account_id, film_id,session_id,row,cell) values "
				+ "(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, 1);
			ps.setInt(2, ticket.getFilmId());
			ps.setInt(3, ticket.getSessionId());
			ps.setInt(4, ticket.getRow());
			ps.setInt(5, ticket.getCell());
			ps.execute();
			try (ResultSet id = ps.getGeneratedKeys()) {
				if (id.next()) {
					ticket.setId(id.getInt(1));
				}
			}
		} catch (SQLException throwable) {
			throwable.printStackTrace();
		}
		return ticket;
	}
	
	public Collection<Ticket> findAllTicketsForPayment(int hashCode) {
		return findAllTicketsWithBooked(hashCode).stream()
				.filter(ticket -> ticket.getSessionId() == hashCode)
				.toList();
	}
	
	public Account save(Account account) {
		try (PreparedStatement ps = connection.prepareStatement("INSERT into account  (username, email,phone) values "
				+ "(?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, account.getUsername());
			ps.setString(2, account.getEmail());
			ps.setString(3, account.getPhone());
			ps.execute();
			try (ResultSet id = ps.getGeneratedKeys()) {
				if (id.next()) {
					account.setId(id.getInt(1));
				}
			}
		} catch (SQLException e) {
			log.error(ERROR, e);
		}
		return account;
	}
	
	public Account findAccount(Account account) {
		try (PreparedStatement ps = connection.prepareStatement("SELECT * from account ")) {
			try (ResultSet id = ps.executeQuery()) {
				while (id.next()) {
					if (id.getString("phone").equals(account.getPhone())
							&& id.getString("email").equals(account.getEmail())) {
						account.setId(id.getInt(1));
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return account;
	}
	
	public Boolean update(Ticket ticket) {
		try (PreparedStatement ps = connection
				.prepareStatement("UPDATE ticket SET account_id = ?, session_id = 0 where id = ?")) {
			ps.setInt(1, ticket.getAccountId());
			ps.setInt(2, ticket.getId());
			boolean execute = ps.execute();
			findAllTickets().stream().filter(tick -> tick.getRow() == ticket.getRow()
					&& tick.getCell() == ticket.getCell()
					&& tick.getSessionId() != 0).forEach(t -> deleteTicket(t.getId()));
			return execute;
		} catch (Exception e) {
			log.error(ERROR, e);
		}
		return false;
	}
	
	public void deleteTicket(Integer id) {
		try (PreparedStatement ps = connection.prepareStatement("DELETE FROM ticket WHERE id = (?)")) {
			ps.setInt(1, id);
			ps.execute();
		} catch (Exception e) {
			log.error(ERROR, e);
		}
	}
}
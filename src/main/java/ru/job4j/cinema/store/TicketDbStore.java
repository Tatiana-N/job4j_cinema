package ru.job4j.cinema.store;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.store.api.DbStoreAbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class TicketDbStore extends DbStoreAbs {
	private final UserDbStore userDbStore;
	private final SessionDbStore filmSessionDbStore;
	private final String findBySessionIdRowCell = "select * from tickets where session_id = ? and row_number=? and "
			+ "place_number=?";
	private final String findAllBySessionId = "select * from tickets where session_id=?";
	private final String add = "insert into tickets (session_id, row_number, place_number, user_id) VALUES (?,?,?,?)";
	private final BasicDataSource pool = getPool();
	
	public TicketDbStore(@Value("${db.driver}") String driver,
	                     @Value("${db.url}") String password,
	                     @Value("${db.login}")String user,
	                     @Value("${db.password}") String url,UserDbStore userDbStore, SessionDbStore filmSessionDbStore) {
		super(driver, password, user, url);
		this.userDbStore = userDbStore;
		this.filmSessionDbStore = filmSessionDbStore;
	}
	
	public Optional<Ticket> findBySessionIdRowCell(int sessionId, int row, int cell) {
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement =
				cn.prepareStatement(findBySessionIdRowCell)) {
			preparedStatement.setInt(1, sessionId);
			preparedStatement.setInt(2, row);
			preparedStatement.setInt(3, cell);
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				if (rs.next()) {
					return Optional.of(createTicket(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return Optional.empty();
	}
	
	public Optional<Ticket> add(Ticket ticket) {
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement = cn.prepareStatement(add,
				PreparedStatement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setInt(1, ticket.getSession().getId());
			preparedStatement.setInt(2, ticket.getRowNumber());
			preparedStatement.setInt(3, ticket.getPlaceNumber());
			preparedStatement.setInt(4, ticket.getUser().getId());
			preparedStatement.execute();
			try (ResultSet it = preparedStatement.getGeneratedKeys()) {
				if (it.next()) {
					ticket.setId(it.getInt(1));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return Optional.empty();
	}
	
	public Collection<Ticket> findAllBySessionId(int sessionId) {
		Collection<Ticket> tickets = new ArrayList<>();
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement =
				cn.prepareStatement(findAllBySessionId)) {
			preparedStatement.setInt(1, sessionId);
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				while (rs.next()) {
					tickets.add(createTicket(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return tickets;
	}
	
	private Ticket createTicket(ResultSet rs) throws SQLException {
		return new Ticket(rs.getInt("id"),
				userDbStore.findById(rs.getInt("user_id")).get(),
				rs.getInt("place_number"),
				rs.getInt("row_number"),
				filmSessionDbStore.findById(rs.getInt("session_id")).get());
	}
}

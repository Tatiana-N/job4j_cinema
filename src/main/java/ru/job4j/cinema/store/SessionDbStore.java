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
public class SessionDbStore extends DbStoreAbs {
	private FilmDbStore filmDbStore;
	private HallDbStore hallDbStore;
	private final String findByIdFilmSessions = "select * from film_sessions where film_id = ?";
	private final String findAll = "select * from film_sessions";
	private final String findById = "select * from film_sessions where id = ?";
	private final String newSession = "insert into film_sessions (film_id, halls_id, start_time, end_time) VALUES (?,?,'2023-10-13 13:00:03','2023-10-13 15:00:00');";
	private final BasicDataSource pool = getPool();
	
	public SessionDbStore(@Value("${db.driver}") String driver,
	                      @Value("${db.url}") String password,
	                      @Value("${db.login}")String user,
	                      @Value("${db.password}") String url,FilmDbStore filmDbStore, HallDbStore hallDbStore) {
		super(driver, password, user, url);
		this.filmDbStore = filmDbStore;
		this.hallDbStore = hallDbStore;
	}
	
	public Collection<FilmSession> findFilmSessions(int filmId) {
		Collection<FilmSession> collection = new ArrayList<>();
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement =
				cn.prepareStatement(findByIdFilmSessions)) {
			preparedStatement.setInt(1, filmId);
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				while (rs.next()) {
					collection.add(createFilmSession(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return collection;
	}
	
	public Collection<FilmSession> findAll() {
		Collection<FilmSession> collection = new ArrayList<>();
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement =
				cn.prepareStatement(findAll)) {
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				while (rs.next()) {
					collection.add(createFilmSession(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return collection;
	}
	
	public Optional<FilmSession> findById(int id) {
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement = cn.prepareStatement(findById)) {
			preparedStatement.setInt(1, id);
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				if (rs.next()) {
					return Optional.of(createFilmSession(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return Optional.empty();
	}
	
	public void setNewSession(int filmId, int hallId) {
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement = cn.prepareStatement(newSession)) {
			preparedStatement.setInt(1, filmId);
			preparedStatement.setInt(2, hallId);
			preparedStatement.execute();
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
	}
	
	private FilmSession createFilmSession(ResultSet rs) throws SQLException {
		return new FilmSession(rs.getInt("id"),
				filmDbStore.findById(rs.getInt("film_id")).get(),
				hallDbStore.findHall(rs.getInt("halls_id")).get(),
				rs.getTimestamp("start_time"),
				rs.getTimestamp("end_time"));
	}
}

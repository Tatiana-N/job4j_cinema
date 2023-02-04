package ru.job4j.cinema.store;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
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
@AllArgsConstructor
public class SessionDbStore extends DbStoreAbs {
	private FilmDbStore filmDbStore;
	private HallDbStore hallDbStore;
	private final String findByIdFilmSessions = "select * from film_sessions where film_id = ?";
	private final String findAll = "select * from film_sessions";
	private final String findById = "select * from film_sessions where id = ?";
	private final BasicDataSource pool = getPool();
	
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
	
	private FilmSession createFilmSession(ResultSet rs) throws SQLException {
		return new FilmSession(rs.getInt("id"),
				filmDbStore.findById(rs.getInt("film_id")).get(),
				hallDbStore.findHall(rs.getInt("halls_id")).get(),
				rs.getTimestamp("start_time"),
				rs.getTimestamp("end_time"));
	}
}

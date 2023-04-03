package ru.job4j.cinema.store;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.store.api.DbStoreAbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class GenreDbStore extends DbStoreAbs {
	private final String findByIdGenre = "select * from genres where id = ?";
	private final String findAll = "select * from genres";
	private final BasicDataSource pool = getPool();
	
	public Optional<Genre> findGenre(int id) {
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement =
				cn.prepareStatement(findByIdGenre)) {
			preparedStatement.setInt(1, id);
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				if (rs.next()) {
					return Optional.of(createGenre(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return Optional.empty();
	}
	
	public Collection<Genre> findAll() {
		List<Genre> genres = new ArrayList<>();
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement = cn.prepareStatement(findAll)) {
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				while (rs.next()) {
					genres.add(createGenre(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return genres;
	}
	
	private Genre createGenre(ResultSet rs) throws SQLException {
		return new Genre(rs.getInt("id"), rs.getString("name"));
	}
}

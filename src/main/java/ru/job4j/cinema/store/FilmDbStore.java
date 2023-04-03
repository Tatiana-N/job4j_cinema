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
public class FilmDbStore extends DbStoreAbs {
	private final FileDbStore fileDbStore;
	private final GenreDbStore genreDbStore;
	private final String findById = "select * from films where id = ?";
	private final String findAll = "select * from films";
	private final String add = "insert into films (name, description, year, genre_id, minimal_age, duration_in_minutes, "
			+ "file_id) values(?,?,?,?,?,?,?)";
	private final BasicDataSource pool = getPool();
	
	public Collection<Film> findAll() {
		Collection<Film> films = new ArrayList<>();
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement = cn.prepareStatement(findAll)) {
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				while (rs.next()) {
					films.add(createFilm(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return films;
	}
	
	public Optional<Film> findById(int id) {
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement = cn.prepareStatement(findById)) {
			preparedStatement.setInt(1, id);
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				if (rs.next()) {
					return Optional.of(createFilm(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return Optional.empty();
	}
	
	public Optional<Film> add(Film film) {
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement = cn.prepareStatement(add,
				PreparedStatement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setString(1, film.getName());
			preparedStatement.setString(2, film.getDescription());
			preparedStatement.setInt(3, film.getYear());
			preparedStatement.setInt(4, film.getGenre().getId());
			preparedStatement.setInt(5, film.getMinimalAge());
			preparedStatement.setInt(6, film.getDurationInMinutes());
			preparedStatement.setInt(7, film.getFile().getId());
			preparedStatement.execute();
			try (ResultSet it = preparedStatement.getGeneratedKeys()) {
				if (it.next()) {
					film.setId(it.getInt(1));
					return Optional.of(film);
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return Optional.empty();
	}
	
	private Film createFilm(ResultSet rs) throws SQLException {
		return new Film(rs.getInt("id"),
				rs.getString("name"),
				rs.getString("description"),
				rs.getInt("year"),
				genreDbStore.findGenre(rs.getInt("genre_id")).get(),
				rs.getInt("minimal_age"),
				rs.getInt("duration_in_minutes"),
				fileDbStore.findFile(rs.getInt("file_id")).get());
	}
}

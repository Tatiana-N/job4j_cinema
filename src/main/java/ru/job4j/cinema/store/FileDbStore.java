package ru.job4j.cinema.store;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.File;
import ru.job4j.cinema.store.api.DbStoreAbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Service
@Slf4j
public class FileDbStore extends DbStoreAbs {
	private final String findByIdFile = "select * from files where id = ?";
	private final BasicDataSource pool = getPool();
	
	public Optional<File> findFile(int id) {
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement =
				cn.prepareStatement(findByIdFile)) {
			preparedStatement.setInt(1, id);
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				if (rs.next()) {
					return Optional.of(createFile(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return Optional.empty();
	}
	
	private File createFile(ResultSet rs) throws SQLException {
		return new File(rs.getInt("id"),
				rs.getString("name"),
				rs.getString("path"));
	}
}

package ru.job4j.cinema.store;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Hall;
import ru.job4j.cinema.store.api.DbStoreAbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class HallDbStore extends DbStoreAbs {
	private final String findByIdHall = "select * from halls where id = ?";
	private final BasicDataSource pool = getPool();
	
	public Optional<Hall> findHall(int id) {
		try (Connection cn = pool.getConnection(); PreparedStatement preparedStatement =
				cn.prepareStatement(findByIdHall)) {
			preparedStatement.setInt(1, id);
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				if (rs.next()) {
					return Optional.of(createHall(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return Optional.empty();
	}
	
	private Hall createHall(ResultSet rs) throws SQLException {
		return new Hall(rs.getInt("id"),
				rs.getString("name"),
				rs.getString("description"),
				rs.getInt("row_count"),
				rs.getInt("place_count"));
	}
}

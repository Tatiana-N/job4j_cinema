package ru.job4j.cinema.store;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.store.api.DbStoreAbs;

import java.sql.*;
import java.util.Optional;

@Slf4j
@Service
public class UserDbStore extends DbStoreAbs {
	private final String findByEmail = "select * from users where email like ?";
	private final String findById = "select * from users where id = ?";
	private final String add = "insert into users (full_name, email, password) values(?,?,?)";
	private final BasicDataSource pool = getPool();
	
	public UserDbStore(@Value("${db.driver}") String driver,
	                   @Value("${db.url}") String password,
	                   @Value("${db.login}")String user,
	                   @Value("${db.password}") String url) {
		super(driver, password, user, url);
	}
	
	public Optional<User> findById(int id) {
		try (Connection connection = pool.getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(findById);
			preparedStatement.setInt(1, id);
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				if (rs.next()) {
					return Optional.of(creatUser(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return Optional.empty();
	}
	
	public Optional<User> findByEmail(String email) {
		try (Connection connection = pool.getConnection()) {
			PreparedStatement preparedStatement = connection.prepareStatement(findByEmail);
			preparedStatement.setString(1, email);
			preparedStatement.executeQuery();
			try (ResultSet rs = preparedStatement.getResultSet()) {
				if (rs.next()) {
					return Optional.of(creatUser(rs));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return Optional.empty();
	}
	
	public Optional<User> add(User user) {
		try (PreparedStatement preparedStatement = pool.getConnection().prepareStatement(add,
				PreparedStatement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setString(1, user.getFullName());
			preparedStatement.setString(2, user.getEmail());
			preparedStatement.setString(3, user.getPassword());
			preparedStatement.execute();
			try (ResultSet it = preparedStatement.getGeneratedKeys()) {
				if (it.next()) {
					user.setId(it.getInt(1));
				}
			}
		} catch (SQLException e) {
			log.error("Ошибка поиска в БД - " + e.getMessage());
		}
		return Optional.empty();
	}
	
	private User creatUser(ResultSet rs) throws SQLException {
		return new User(rs.getInt("id"),
				rs.getString("full_name"),
				rs.getString("password"),
				rs.getString("email"));
	}
}

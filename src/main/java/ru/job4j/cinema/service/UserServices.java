package ru.job4j.cinema.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.store.UserDbStore;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServices {
	private final UserDbStore dbStore;
	
	public Optional<User> findByEmail(String email) {
		return dbStore.findByEmail(email);
	}
	
	public Optional<User> add(User user) {
		return dbStore.add(user);
	}
}

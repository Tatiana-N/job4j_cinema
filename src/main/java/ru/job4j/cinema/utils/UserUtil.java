package ru.job4j.cinema.utils;

import ru.job4j.cinema.model.User;

public final class UserUtil {
	
	public static User getUser(User user) {
		return user == null ? new User(0, "Гость", "", "") : user;
	}
}

package ru.job4j.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
	private int id;
	private final String fullName;
	private final String password;
	private final String email;
}

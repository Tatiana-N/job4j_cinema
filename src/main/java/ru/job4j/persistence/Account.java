package ru.job4j.persistence;

import lombok.Data;

@Data
public class Account {
	private int id;
	private final String username;
	private final String phone;
	private final String email;
	
	public Account(int id, String username, String phone, String email) {
		this.id = id;
		this.username = username;
		this.phone = phone;
		this.email = email;
	}
}

package ru.job4j.cinema.model;

import lombok.Data;

@Data
public class Ticket {
	private int id;
	private final User user;
	private final int placeNumber;
	private final int rowNumber;
	private final FilmSession session;
	
	public Ticket(int id, User user, int placeNumber, int rowNumber, FilmSession session) {
		this.id = id;
		this.user = user;
		this.placeNumber = placeNumber;
		this.rowNumber = rowNumber;
		this.session = session;
	}
}

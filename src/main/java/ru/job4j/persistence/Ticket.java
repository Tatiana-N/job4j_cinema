package ru.job4j.persistence;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class Ticket {
	private final int id;
	private final int accountId;
	private final int cell;
	private final int row;
	private final int filmId;
	private final int sessionId;
	
	public Ticket(int id, int accountId, int cell, int row, int filmId, int sessionId) {
		this.id = id;
		this.accountId = accountId;
		this.cell = cell;
		this.row = row;
		this.filmId = filmId;
		this.sessionId = sessionId;
	}
}

package ru.job4j.persistence;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class Ticket {
	private  int id;
	private  int account_id;
	private final int cell;
	private final int row;
	private final int film_id;
	private  int session_id;
	
	public Ticket(int id, int account_id, int cell, int row, int film_id, int session_id) {
		this.id = id;
		this.account_id = account_id;
		this.cell = cell;
		this.row = row;
		this.film_id = film_id;
		this.session_id = session_id;
	}
}

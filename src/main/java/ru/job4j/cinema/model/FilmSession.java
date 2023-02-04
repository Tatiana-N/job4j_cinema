package ru.job4j.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class FilmSession {
	private int id;
	private final Film film;
	private final Hall hall;
	private final Timestamp startTime;
	private final Timestamp endTime;
}

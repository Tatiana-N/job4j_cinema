package ru.job4j.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Film {
	private int id;
	private final String name;
	private final String description;
	private final int year;
	private final Genre genre;
	private final int minimalAge;
	private final int durationInMinutes;
	private final File file;
}

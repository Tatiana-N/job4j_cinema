package ru.job4j.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Film {
	private int id;
	private String name;
	private String description;
	private int year;
	private Genre genre;
	private int minimalAge;
	private int durationInMinutes;
	private File file;
}

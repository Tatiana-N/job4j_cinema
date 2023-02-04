package ru.job4j.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Hall {
	private int id;
	private final String name;
	private final String description;
	private final int rowCount;
	private final int placeCount;
}

package ru.job4j.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Genre {
	private int id;
	private final String name;
}

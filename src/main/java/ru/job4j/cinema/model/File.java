package ru.job4j.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class File {
	private int id;
	private final String name;
	private final String path;
}

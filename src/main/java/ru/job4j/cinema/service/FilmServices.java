package ru.job4j.cinema.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.store.FilmDbStore;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class FilmServices {
	private FilmDbStore dbStore;
	
	public Collection<Film> findAll() {
		return dbStore.findAll();
	}
	
	public Optional<Film> findById(int id) {
		return dbStore.findById(id);
	}
	
	public Optional<Film> add(Film film) {
		return dbStore.add(film);
	}
}

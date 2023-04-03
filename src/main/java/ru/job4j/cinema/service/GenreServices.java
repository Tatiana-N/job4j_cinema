package ru.job4j.cinema.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Genre;
import ru.job4j.cinema.store.GenreDbStore;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class GenreServices {
	private GenreDbStore dbStore;
	
	public Collection<Genre> findAll() {
		return dbStore.findAll();
	}
	
	public Optional<Genre> findGenre(int id) {
		return dbStore.findGenre(id);
	}
}

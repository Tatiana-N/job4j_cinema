package ru.job4j.cinema.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.store.SessionDbStore;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class FilmSessionServices {
	private SessionDbStore dbStore;
	
	public Optional<FilmSession> findById(int id) {
		return dbStore.findById(id);
	}
	
	public Collection<FilmSession> findFilmSessions(int id) {
		return dbStore.findFilmSessions(id);
	}
	
	public Collection<FilmSession> findAll() {
		return dbStore.findAll();
	}
	
	public void setNewSession(int filmId, int hallId) {
		 dbStore.setNewSession(filmId, hallId);
	}
}

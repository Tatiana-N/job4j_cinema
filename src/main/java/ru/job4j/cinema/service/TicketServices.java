package ru.job4j.cinema.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.store.TicketDbStore;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TicketServices {
	private TicketDbStore dbStore;
	
	public Optional<Ticket> add(Ticket ticket) {
		return dbStore.add(ticket);
	}
	
	public Collection<Ticket> findAllBySessionId(int sessionId) {
		return dbStore.findAllBySessionId(sessionId);
	}
	
	public Optional<Ticket> findBySessionIdRowCell(Integer sessionId, int rowNumber, int placeNumber) {
		return dbStore.findBySessionIdRowCell(sessionId, rowNumber, placeNumber);
	}
}

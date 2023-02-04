package ru.job4j.cinema.controllers;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.job4j.cinema.model.*;
import ru.job4j.cinema.service.FileServices;
import ru.job4j.cinema.service.FilmServices;
import ru.job4j.cinema.service.FilmSessionServices;
import ru.job4j.cinema.service.TicketServices;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.job4j.cinema.utils.UserUtil.getUser;

@Controller
@AllArgsConstructor
public class FilmController {
	private final FilmServices filmServices;
	private final FileServices fileServices;
	private final FilmSessionServices filmSessionServices;
	private final TicketServices ticketServices;
	
	@GetMapping("/film/{filmId}")
	public String film(Model model, @PathVariable("filmId") Integer filmId, HttpSession httpSession) {
		
		model.addAttribute("filmSessions", filmSessionServices.findFilmSessions(filmId));
		model.addAttribute("film", filmServices.findById(filmId).get());
		model.addAttribute("user", getUser((User) httpSession.getAttribute("user")));
		return "film";
	}
	
	@GetMapping("/schedule")
	public String schedule(Model model, HttpSession httpSession) {
		model.addAttribute("filmSessions", filmSessionServices.findAll());
		model.addAttribute("films", filmServices.findAll());
		model.addAttribute("user", getUser((User) httpSession.getAttribute("user")));
		return "schedule";
	}
	
	@GetMapping("/films")
	public String films(Model model, HttpSession httpSession) {
		model.addAttribute("films", filmServices.findAll());
		model.addAttribute("user", getUser((User) httpSession.getAttribute("user")));
		return "films";
	}
	
	@GetMapping("/session/{sessionId}")
	public String session(Model model, @PathVariable("sessionId") Integer sessionId, HttpSession httpSession) {
		FilmSession filmSession = filmSessionServices.findById(sessionId).get();
		Set<Ticket> selectedTickets = (Set<Ticket>) httpSession.getAttribute("selectedTickets");
		selectedTickets = selectedTickets == null ? new HashSet<>() : selectedTickets;
		model.addAttribute("selectedTickets",
				selectedTickets.stream().filter(ticket -> ticket.getSession().getId() == sessionId).collect(Collectors.toList()));
		model.addAttribute("filmSession", filmSession);
		model.addAttribute("tickets", ticketServices.findAllBySessionId(filmSession.getId()));
		model.addAttribute("film", filmSession.getFilm());
		model.addAttribute("user", getUser((User) httpSession.getAttribute("user")));
		return "filmSession";
	}
	
	@GetMapping("/photoFilm/{fileId}")
	public ResponseEntity<Resource> download(@PathVariable("fileId") Integer fileId) throws IOException {
		Optional<File> fileById = fileServices.findFileById(fileId);
		byte[] bytes = Files.readAllBytes(Paths.get(fileById.get().getPath()));
		return ResponseEntity
				.ok()
				.headers(new HttpHeaders())
				.contentLength(bytes.length)
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(new ByteArrayResource(bytes));
	}
}

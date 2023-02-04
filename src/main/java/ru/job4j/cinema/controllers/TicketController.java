package ru.job4j.cinema.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cinema.model.Film;
import ru.job4j.cinema.model.FilmSession;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmSessionServices;
import ru.job4j.cinema.service.TicketServices;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static ru.job4j.cinema.utils.UserUtil.getUser;

@Controller
@AllArgsConstructor
public class TicketController {
	private final FilmSessionServices filmSessionServices;
	private final TicketServices ticketServices;
	
	@GetMapping("/ticket/{filmId}/{sessionId}/{ticketRow}/{ticketCell}")
	public String ticket(@PathVariable("filmId") Integer filmId,
	                     @PathVariable("sessionId") Integer sessionId,
	                     @PathVariable("ticketRow") Integer ticketRow,
	                     @PathVariable("ticketCell") Integer ticketCell,
	                     HttpSession httpSession) {
		User user = (User) httpSession.getAttribute("user");
		Optional<FilmSession> byId = filmSessionServices.findById(sessionId);
		Optional<Ticket> optionalTicket = ticketServices.findBySessionIdRowCell(sessionId, ticketRow, ticketCell);
		if (optionalTicket.isEmpty()) {
			Set<Ticket> selectedTickets = (Set<Ticket>) httpSession.getAttribute("selectedTickets");
			if (selectedTickets == null) {
				selectedTickets = new HashSet<>();
			}
			Ticket ticket = new Ticket(0, user, ticketCell, ticketRow, byId.get());
			if (!selectedTickets.add(ticket)) {
				selectedTickets.remove(ticket);
			}
			httpSession.setAttribute("selectedTickets", selectedTickets);
		}
		return "redirect:../../../../session/" + sessionId;
	}
	
	@PostMapping("/buyTicket/{sessionId}")
	public String buyTicket(@PathVariable("sessionId") Integer sessionId,
	                       HttpSession httpSession,
	                       RedirectAttributes redirectAttributes) {
		Optional<FilmSession> sessionOptional = filmSessionServices.findById(sessionId);
		FilmSession filmSession = sessionOptional.get();
		Film film = filmSession.getFilm();
		Set<Ticket> selectedTickets = (Set<Ticket>) httpSession.getAttribute("selectedTickets");
		if (selectedTickets.removeIf(ticket -> ticketServices.findBySessionIdRowCell(sessionId, ticket.getRowNumber(),
				ticket.getPlaceNumber()).isPresent())) {
			redirectAttributes.addFlashAttribute("error", "Выбранные билеты были куплены, сделайте свой выбор заново");
		} else {
			selectedTickets.forEach(ticketServices::add);
			redirectAttributes.addFlashAttribute("success", "Билеты куплены");
		}
		selectedTickets.removeIf(ticket -> ticketServices.findBySessionIdRowCell(sessionId, ticket.getRowNumber(),
				ticket.getPlaceNumber()).isPresent());
		httpSession.setAttribute("selectedTickets", selectedTickets);
		return "redirect:../session/" + sessionId;
	}
	
	@GetMapping("/buyTicket/{sessionId}")
	public String ticket(Model model,
	                     @PathVariable("sessionId") Integer sessionId,
	                     HttpSession httpSession,
	                     RedirectAttributes redirectAttributes) {
		Optional<FilmSession> byId = filmSessionServices.findById(sessionId);
		FilmSession filmSession = byId.get();
		Film film = filmSession.getFilm();
		Set<Ticket> selectedTickets = (Set<Ticket>) httpSession.getAttribute("selectedTickets");
		if (selectedTickets == null || selectedTickets.isEmpty()) {
			httpSession.setAttribute("selectedTickets", new HashSet<>());
			redirectAttributes.addFlashAttribute("error", "Ни один билет не выбран");
			return "redirect:../session/" + sessionId;
		}
		if (selectedTickets.removeIf(ticket -> ticketServices.findBySessionIdRowCell(sessionId, ticket.getRowNumber(),
				ticket.getPlaceNumber()).isPresent())) {
			redirectAttributes.addFlashAttribute("error", "Выбранные билеты уже куплены, сделайте свой выбор заново");
			return "redirect:../session/" + sessionId;
		}
		model.addAttribute("selectedTickets", selectedTickets);
		model.addAttribute("film", film);
		model.addAttribute("filmSession", filmSession);
		model.addAttribute("user", getUser((User) httpSession.getAttribute("user")));
		return "payment";
	}
}

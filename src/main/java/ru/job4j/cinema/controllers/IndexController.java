package ru.job4j.cinema.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.FilmServices;

import javax.servlet.http.HttpSession;

import static ru.job4j.cinema.utils.UserUtil.getUser;

@Controller
@AllArgsConstructor
public class IndexController {
	private final FilmServices filmServices;
	
	@GetMapping("/index")
	public String index(Model model, HttpSession session) {
		model.addAttribute("films", filmServices.findAll());
		model.addAttribute("user", getUser((User) session.getAttribute("user")));
		return "index";
	}
	
	@GetMapping("/")
	public String empty(Model model, HttpSession session) {
		return index(model, session);
	}
}

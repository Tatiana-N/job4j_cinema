package ru.job4j.cinema.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cinema.model.User;
import ru.job4j.cinema.service.UserServices;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static io.vavr.API.*;
import static ru.job4j.cinema.utils.UserUtil.getUser;

@Controller
@AllArgsConstructor
public class LoginController {
	UserServices userService;
	
	@GetMapping("/loginPage")
	public String loginPage(Model model, RedirectAttributes redirectAttributes, @RequestParam(name = "error", required =
			false) String error) {
		redirectAttributes.addFlashAttribute("error", error);
		model.addAttribute("user", getUser(null));
		return "login";
	}
	
	@PostMapping("/login")
	public String login(@ModelAttribute User user, RedirectAttributes redirectAttributes, HttpSession session) {
		Optional<User> byEmail = userService.findByEmail(user.getEmail());
		String error = Match(byEmail).of(Case($(Optional::isEmpty), "Пользователь с такой почтой не зарегистрирован"),
				Case($(n -> n.isPresent() && !n.get().getPassword().equals(user.getPassword())), "Пароль введен неверно"),
				Case($(), "ok"));
		if (!"ok".equals(error)) {
			redirectAttributes.addFlashAttribute("error", error);
			return "redirect:/loginPage";
		}
		session.setAttribute("user", byEmail.orElse(null));
		return "redirect:index";
	}
	
	@GetMapping("/registrationPage")
	public String registrationPage(Model model, RedirectAttributes redirectAttributes,
	                               @RequestParam(name = "error", required = false) String error) {
		redirectAttributes.addFlashAttribute("error", error);
		model.addAttribute(getUser(null));
		return "registration";
	}
	
	@PostMapping("/registration")
	public String registration(@ModelAttribute User user, HttpSession session, RedirectAttributes redirectAttributes) {
		Optional<User> byEmail = userService.findByEmail(user.getEmail());
		String error = Match(byEmail).of(Case($(Optional::isPresent), "Такой пользователь уже зарегистрирован"),
				Case($(n -> "".equals(user.getPassword().trim())), "Пароль заполнен неверно"),
				Case($(n -> "".equals(user.getEmail().trim())), "Почта указана неверно"),
				Case($(), "ok"));
		if (!"ok".equals(error)) {
			redirectAttributes.addFlashAttribute("error", error);
			return "redirect:/registrationPage";
		}
		userService.add(user);
		session.setAttribute("user", user);
		redirectAttributes.addFlashAttribute("success", "Вы успешно зарегистрированы");
		return "redirect:loginPage";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession httpSession) {
		httpSession.setAttribute("user", null);
		return "redirect:loginPage";
	}
}

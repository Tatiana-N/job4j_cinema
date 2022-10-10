package ru.job4j.services;

import ru.job4j.persistence.Account;
import ru.job4j.persistence.Ticket;
import ru.job4j.store.DbStore;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Collection;

public class PaymentServlet extends HttpServlet {
	private final DbStore dbStore = new DbStore();
	
	/*
	сохраняет забронированные билеты
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int rows = Integer.parseInt(request.getParameterMap().get("row")[0]);
		int cells = Integer.parseInt(request.getParameterMap().get("cell")[0]);
		dbStore.create(new Ticket(0, request.getSession().hashCode(), cells == 0 ? 12 : cells, rows, 1,
				request.getSession().hashCode()));
		response.sendRedirect(request.getContextPath() + "/go");
	}
	
	/*
	покупка билетов + аккаунт
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		request.setCharacterEncoding("UTF-8");
		Account account = new Account(0, request.getParameter("username"), request.getParameter("phone"),
				request.getParameter("email"));
		Account accountFromDb = dbStore.save(account);
		accountFromDb = accountFromDb.getId() == 0 ? dbStore.findAccount(account) : accountFromDb;
		if (accountFromDb.getId() == 0) {
			request.getRequestDispatcher("/go").forward(request, response);
		} else {
			Collection<Ticket> allTicketsThisSession = dbStore.findAllTicketsForPayment(request.getSession().hashCode());
			for (Ticket ticket : allTicketsThisSession) {
				Ticket newTicket = new Ticket(ticket.getId(), accountFromDb.getId(), ticket.getCell(), ticket.getRow(),
						ticket.getFilmId(), ticket.getSessionId());
				dbStore.update(newTicket);
			}
			response.sendRedirect(request.getContextPath() + "/go");
		}
	}
}

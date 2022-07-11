package ru.job4j.services;

import ru.job4j.persistence.Ticket;
import ru.job4j.store.DbStore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public class HallServlet extends HttpServlet {
	DbStore dbStore = new DbStore();
	
	/*
	отображает билеты в кинозале
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setIntHeader("Refresh", 5);
		request.setAttribute("tickets", dbStore.findAllTicketsWithBooked(request.getSession().hashCode()));
		setHallAttribute(request);
		request.getRequestDispatcher("index.jsp").forward(request, response);
	}
	
	/*
передает на страницу оплаты билеты и сумму
 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Collection<Ticket> allTicketsThisSession = dbStore.findAllTicketsForPayment(request.getSession().hashCode());
		request.setAttribute("tickets", allTicketsThisSession);
		setHallAttribute(request);
		request.setAttribute("sum", allTicketsThisSession.size() * 300);
		request.getRequestDispatcher("/payment.jsp").forward(request, response);
	}
	
	private void setHallAttribute(HttpServletRequest request) {
		/*
		в дальнейшем можно создать таблицу залов из которых брать размер
		*/
		request.setAttribute("rows", 9);
		request.setAttribute("cells", 12);
	}
}

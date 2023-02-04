package ru.job4j.cinema.filter;

import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Component
public class AuthFilter extends HttpFilter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		String requestURI = httpServletRequest.getRequestURI();
		if (!requestURI.toLowerCase(Locale.ROOT).contains("ticket")) {
			chain.doFilter(httpServletRequest, httpServletResponse);
			return;
		}
		if (httpServletRequest.getSession().getAttribute("user") == null) {
			httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/loginPage");
			return;
		}
		chain.doFilter(httpServletRequest, httpServletResponse);
	}
}

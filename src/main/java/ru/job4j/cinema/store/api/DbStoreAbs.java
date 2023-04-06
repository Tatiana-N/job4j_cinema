package ru.job4j.cinema.store.api;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
public class DbStoreAbs {
	private final BasicDataSource pool = new BasicDataSource();
	private final String driver;
	private final String password;
	private final String user;
	private final String url;
	
	public DbStoreAbs(String driver,
	                  String password,
	                  String user,
	                   String url) {
		this.driver = driver;
		this.password = password;
		this.user = user;
		this.url = url;
	}
	
	public BasicDataSource getPool() {
		if (pool.getUrl() == null) {
			setProperties();
		}
		return pool;
	}
	
	private void setProperties() {
			pool.setDriverClassName(driver);
			pool.setUrl(url);
			pool.setUsername(user);
			pool.setPassword(password);
			pool.setMaxIdle(10);
			pool.setMinIdle(5);
	}
}

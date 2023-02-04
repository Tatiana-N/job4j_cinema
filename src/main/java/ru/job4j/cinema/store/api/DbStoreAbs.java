package ru.job4j.cinema.store.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public abstract class DbStoreAbs {
	private final BasicDataSource pool = new BasicDataSource();
	
	public BasicDataSource getPool() {
		if (pool.getUrl() == null) {
			setProperties();
		}
		return pool;
	}
	
	private void setProperties() {
		try {
			Properties properties = new Properties();
			properties.load(DbStoreAbs.class.getClassLoader().getResourceAsStream("application.properties"));
			pool.setDriverClassName(properties.getProperty("db.driver"));
			pool.setUrl(properties.getProperty("db.url"));
			pool.setUsername(properties.getProperty("db.login"));
			pool.setPassword(properties.getProperty("db.password"));
			pool.setMaxIdle(10);
			pool.setMinIdle(5);
		} catch (IOException e) {
			log.error("Не удалось установить соединение с базой данных", e);
		}
	}
}

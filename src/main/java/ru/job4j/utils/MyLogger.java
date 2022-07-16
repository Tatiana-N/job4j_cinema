package ru.job4j.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

public class MyLogger {
	private static final Logger LOG = LogManager.getLogger(MyLogger.class.getName());
	
	public static Logger getLogger() {
		return LOG;
	}
}

package jdomaincrawler.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownHook extends Thread {
	
	private static Logger logger = LoggerFactory.getLogger(ShutdownHook.class);
	private ThreadExecutor executor;

	@Override
	public void run() {
		Process proc;
		executor.shutdownNow();
		logger.error("Programm beendet");	
	}

	public ShutdownHook(ThreadExecutor executor) {
		super();
		this.executor = executor;
	}

}

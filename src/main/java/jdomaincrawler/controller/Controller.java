package jdomaincrawler.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdomaincrawler.crawler.Crawler;
import jdomaincrawler.stripper.Stripper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

	private static Logger logger = LoggerFactory.getLogger(Controller.class);
	private ThreadExecutor threadExec = new ThreadExecutor();
	private Map<String, Integer> domainFilesMap;

	public void init() {
		PropertiesFactory.loadProperties("jdomaincrawler.properties", true);
		threadExec = new ThreadExecutor();
		threadExec.setMaxPoolSize(Integer.valueOf(PropertiesFactory
				.getProperties().getProperty("maxPoolSize", "8")));
		threadExec.setPoolSize(Integer.valueOf(PropertiesFactory
				.getProperties().getProperty("poolSize", "2")));
		threadExec.setTimeout(Integer.valueOf(PropertiesFactory.getProperties()
				.getProperty("timeout", "2")));
		domainFilesMap = new HashMap<String, Integer>();
	}

	private void generateCrawlers() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(PropertiesFactory
					.getProperties().getProperty("domainfile")));
			String domain;
			String domainName;
			Crawler crawler;
			while (reader.ready()) {
				domain = reader.readLine();
				domainName = new URL(domain).getHost();
				crawler = new Crawler(domain, PropertiesFactory.getProperties()
						.getProperty("crawlpath") + "/" + domainName, this);
				threadExec.executeTask(crawler);
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	private void generateStrippers(List<String> files, String domainOutput) {
		Stripper stripper;
		for (String file : files) {
			stripper = new Stripper(file, domainOutput, this);
			threadExec.executeTask(stripper);
		}
	}

	public synchronized void stripFinished(String domain) {

	}

	public synchronized void crawlFinished(String domain) {

	}
}

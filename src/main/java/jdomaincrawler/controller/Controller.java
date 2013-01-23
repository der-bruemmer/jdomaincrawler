package jdomaincrawler.controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jdomaincrawler.crawler.Crawler;
import jdomaincrawler.stripper.Stripper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {

	private static Logger logger = LoggerFactory.getLogger(Controller.class);
	private ThreadExecutor threadExec = new ThreadExecutor();
	private Map<String, Integer> domainFilesMap;
	private static String regexFile = "^.\\s(.+?)\\s+.*?";
	private static int numberOfDomains;
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
		threadExec.init();
	}

	public void generateCrawlers() {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(PropertiesFactory
					.getProperties().getProperty("domainfile")));

			Pattern pattern = Pattern.compile(regexFile);
			String domain;
			String domainName;
			Crawler crawler;
			Matcher m;
			while (reader.ready()) {
				m = pattern.matcher(reader.readLine());
				if (m.matches()) {
					domain = m.group(1);
					if (!domain.equals("dom")) {
						domainName = domain.replaceAll("\\.", "_");
						crawler = new Crawler(domain, PropertiesFactory
								.getProperties().getProperty("crawlpath",
										"~/crawler/")
								+ domainName, this);
						threadExec.executeTask(crawler);
						numberOfDomains++;
					}
				}
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public void generateStrippers(List<String> files, String domainOutput) {
		Stripper stripper;
		for (String file : files) {
			stripper = new Stripper(file, domainOutput, this);
			threadExec.executeTask(stripper);
		}
		numberOfDomains--;
		if(numberOfDomains==0){
			threadExec.shutdown();
		}
	}

	public void test() {
		for (int i = 0; i < 10; i++) {
			Crawler c = new Crawler("", "", this);
			Stripper s = new Stripper("", "", this);
			threadExec.executeTask(c);
			threadExec.executeTask(s);
		}
		threadExec.shutdown();
	}

	public synchronized void stripFinished(String domain) {

	}

	public synchronized void crawlFinished(String domain) {
		String dir = PropertiesFactory.getProperties().getProperty("crawlpath")
				+ domain.replaceAll("\\.", "_");
		threadExec.executeTask(new DirExplorer(dir, domain, this));
	}

	public static void main(String[] args) {
		Controller c = new Controller();
		c.init();
		c.generateCrawlers();
	}
}

package jdomaincrawler.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jdomaincrawler.controller.Controller;
import jdomaincrawler.controller.PropertiesFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Crawler implements Runnable {

	Logger logger = LoggerFactory.getLogger(Crawler.class);

	private String domain;
	private String path;
	private Controller controller;

	public Crawler(final String domain, final String path,
			final Controller controller) {
		super();
		this.domain = domain;
		this.path = path;
		this.controller = controller;
	}

	@Override
	public void run() {
		File f = new File(path);
		f.mkdirs();
		List<String> options = new ArrayList<String>();
		options.add(PropertiesFactory.getProperties().getProperty("httrackpath","")+"httrack");
		options.add(domain);
		options.add("-O");
		options.add(path);
		String[] optprop = PropertiesFactory.getProperties()
				.getProperty("httrack").split("\\s");
		options.addAll(Arrays.asList(optprop));
		ProcessBuilder builder = new ProcessBuilder(options);
		builder.directory(new File(path));
		builder.redirectErrorStream(true);
		Process process = null;
		BufferedReader reader = null;
		try {
			process = builder.start();
			reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = "";
			while (!Thread.currentThread().isInterrupted()) {
				line = reader.readLine();
				if (!line.isEmpty()) {
					logger.debug("httrack: {}", line);
				}
				if (line.contains("Thanks for using HTTrack!")) {
					break;
				}
			}

			process.waitFor();
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (InterruptedException e) {
			process.destroy();
		} finally {
			try {
				reader.close();
				process.destroy();
			} catch (IOException e) {
				logger.error(e.getMessage());
			} catch (NullPointerException e){
				logger.debug(e.getMessage());
			}
		}
		this.controller.crawlFinished(this.domain);
	}
}

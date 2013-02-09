package jdomaincrawler.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
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
	private int numberOfLines;
	private int crawled = 0;
	private int stripped = 0;
	private long offset = 0;
	private int readedLines = 0;
	private Map<String, Integer> filesToStrip;

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
		filesToStrip = new HashMap<String, Integer>();
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(threadExec));
		numberOfLines = this.countLines();
	}

	private int countLines() {
		LineNumberReader lnr = null;
		int lines = 0;
		try {
			lnr = new LineNumberReader(new FileReader(
					new File(PropertiesFactory.getProperties().getProperty(
							"domainfile"))));
			lnr.skip(Long.MAX_VALUE);
			lines = lnr.getLineNumber();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				lnr.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		return lines;
	}

	public synchronized void generateCrawlers() {
		RandomAccessFile reader=null;
		try {
			reader = new RandomAccessFile(PropertiesFactory
					.getProperties().getProperty("domainfile"), "r");
			reader.seek(offset);
			Pattern pattern = Pattern.compile(regexFile);
			String domain="";
			String domainName;
			Crawler crawler;
			Matcher m;
			String line=null;
			for (int i = 0; i < 100 && (line=reader.readLine())!=null; i++) {
				m = pattern.matcher(line);
				if (m.matches()) {
					domain = m.group(1);
					if (!domain.equals("dom")) {
						
						domainName = domain;// .replaceAll("\\.", "_");
						domainName = domainName.replaceAll("http://", "");
						domain = domain.replaceAll("http://", "");
						crawler = new Crawler(domain, PropertiesFactory
								.getProperties().getProperty("crawlpath",
										"~/crawler/")
								+ domainName, this);
						threadExec.executeTask(crawler);
						numberOfDomains++;
					} else {
						readedLines--;
						numberOfLines--;
					}
				} else {
					readedLines--;
					numberOfLines--;
				}
				readedLines++;
			}
			offset=reader.getFilePointer();
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void generateStrippers(final List<String[]> files,
			final String domainOutput) {
		Stripper stripper;
		filesToStrip.put(domainOutput, files.size());
		for (String[] file : files) {
			stripper = new Stripper(file[0], file[1], domainOutput, this);
			threadExec.executeTask(stripper);
		}
		numberOfDomains--;
		if (numberOfDomains == 0 && readedLines >= numberOfLines - 1) {
			threadExec.shutdown();
		}
	}

	public synchronized void stripFinished(final String domain) {
		filesToStrip.put(domain, filesToStrip.get(domain) - 1);
		if (filesToStrip.get(domain) == 0) {
			String domainName = domain.substring(domain.lastIndexOf("/"));
			logger.info("Textextraction of Domain {} finished ", domainName);
			domainName = PropertiesFactory.getProperties().getProperty(
					"crawlpath")
					+ "/" + domainName;
			try {
				Runtime.getRuntime().exec("rm -rf " + domainName);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			stripped++;
			logger.info("{} from {} domains stripped", crawled, numberOfLines);
		}
		
	}

	public synchronized void crawlFinished(final String domain) {
		 String dir =
		 PropertiesFactory.getProperties().getProperty("crawlpath")
		 + domain.replaceAll("\\.", "_");
		 threadExec.executeTask(new DirExplorer(dir, domain, this));
		crawled++;
		if (readedLines < numberOfLines - 1 && threadExec.getQueueSize()<1000) {
			this.generateCrawlers();
		}
		logger.info("{} from {} domains crawled", crawled, numberOfLines);
	}

	public static void main(String[] args) {
		PropertiesFactory.loadProperties("jdomaincrawler.properties", true);
		Controller c = new Controller();
		c.init();
		c.generateCrawlers();
	}

}

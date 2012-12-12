package jdomaincrawler.crawler;

import jdomaincrawler.controller.Controller;

public class Crawler implements Runnable{
	
	private String domain;
	private String path;
	private Controller controller;
	
	

	public Crawler(String domain, String path, Controller controller) {
		super();
		this.domain = domain;
		this.path = path;
		this.controller = controller;
	}



	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.controller.crawlFinished(this.domain);
	}

}

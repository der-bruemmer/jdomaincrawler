package jdomaincrawler.stripper;

import jdomaincrawler.controller.Controller;

public class Stripper implements Runnable {

	private String inputFile;
	private String outputFile;
	private Controller controller;
	
	public Stripper(String inputFile, String outputFile, Controller controller) {
		super();
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.controller = controller;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		this.controller.stripFinished(this.outputFile);
	}

}

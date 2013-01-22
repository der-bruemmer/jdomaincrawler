package jdomaincrawler.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jdomaincrawler.stripper.Stripper;

public class DirExplorer implements Runnable {

	private String dir = "/Users/didier/cralwer/";
	private Controller controller;
	private String domain;

	public DirExplorer(String dir, String domain,Controller controller) {
		super();
		this.dir = dir;
		this.domain=domain;
		this.controller = controller;
	}

	@Override
	public void run() {
		File f = new File(dir);
		List<String> files = findFiles(f.list(), f.getAbsolutePath());
		controller.generateStrippers(files, domain);
	}

	public List<String> findFiles(String[] files, String path) {
		File f;
		ArrayList<String> filePaths = new ArrayList<String>();
		for (String file : files) {
			f = new File(path + "/" + file);
			if (f.isDirectory()) {
				filePaths.addAll(findFiles(f.list(), f.getAbsolutePath()));
			} else {
				filePaths.add(f.getAbsolutePath());
			}
		}
		return filePaths;
	}
}

package jdomaincrawler.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

public class DirExplorer implements Runnable {

	private String dir = "";
	private Controller controller;
	private String domain;

	private static Logger logger = LoggerFactory.getLogger(DirExplorer.class);

	public DirExplorer(final String dir, final String domain,
			final Controller controller) {
		super();
		this.dir = dir;
		this.domain = domain;
		this.controller = controller;
	}

	@Override
	public void run() {
		// File f = new File(dir);
		// List<String> files = findFiles(f.list(), f.getAbsolutePath());
		// controller.generateStrippers(files, domain);
		List<String[]> files = new ArrayList<String[]>();
		try {
			CSVReader reader = new CSVReader(new FileReader(dir
					+ "/hts-cache/new.txt"), '\t');
			List<String[]> myEntries = reader.readAll();
			String[] line;
			String[] domain;
			for (int i = 1; i < myEntries.size(); i++) {
				line = myEntries.get(i);
				//überspringt leere Dateien
				if(line[1].equals("0/0") || line[3].contains("error")){
					continue;
				}
				domain = new String[2];
				domain[0] = line[8];
				domain[1] = line[7];
				//übernimmt nur Dateine mit Endung. html oder .htm
				
					files.add(domain);
			}
			controller.generateStrippers(files, PropertiesFactory
					.getProperties().getProperty("output", "")
					+ "/"
					+ this.domain);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	public List<String> findFiles(final String[] files, final String path) {
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

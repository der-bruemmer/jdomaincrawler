package jdomaincrawler.stripper;

import java.io.File;

import jdomaincrawler.controller.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_leipzig.asv.encodingdetector.utils.EncodingDetector;
import de.uni_leipzig.asv.html2text.impl.SimpleHTML2Text;
/**
 * 
 * @author didier
 *
 */
public class Stripper implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(Stripper.class);

	private String inputFile;
	private String outputFile;
	private Controller controller;

	/**
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @param controller
	 */
	public Stripper(final String inputFile, final String outputFile,
			final Controller controller) {
		super();
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.controller = controller;
	}

	@Override
	public final void run() {
		logger.debug("Stripping {}", inputFile);
		EncodingDetector detect = new EncodingDetector(new File(inputFile));
		String encoding = detect.getBestEncoding();
		// System.out.println(inputFile+"\t"+encoding);
		SimpleHTML2Text ht;
		ht = new SimpleHTML2Text(inputFile, encoding);

		this.controller.stripFinished(this.outputFile);
	}

}

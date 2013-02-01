package jdomaincrawler.stripper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import jdomaincrawler.controller.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.SimpleDateFormat;

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
	private String url;
	private Controller controller;

	/**
	 * 
	 * @param inputFile
	 * @param outputFile
	 * @param controller
	 */
	public Stripper(final String inputFile, final String url,
			final String outputFile, final Controller controller) {
		super();
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.controller = controller;
		this.url = url;

	}

	@Override
	public final void run() {

		logger.debug("Stripping {}", inputFile);
			EncodingDetector detect = new EncodingDetector(new File(inputFile));
			String encoding = detect.getBestEncoding();
			SimpleHTML2Text ht;
			ht = new SimpleHTML2Text(inputFile, encoding);
			this.controller.stripFinished(this.outputFile);
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd");
			StringBuffer buffer = new StringBuffer();
			buffer.append("<source>");
			buffer.append("<location>");
			buffer.append(url);
			buffer.append("</location>");
			buffer.append("<date>");
			buffer.append(sdf.format(new Date(System.currentTimeMillis())));
			buffer.append("</date>");
			buffer.append("<original_encoding>");
			buffer.append(encoding);
			buffer.append("</original_encoding>");
			buffer.append("</source>");
			buffer.append(ht.getUTF8Text());
			buffer.append("\n\n");
			Writer w = new Writer();
			w.write(buffer.toString(), outputFile);
	}

}

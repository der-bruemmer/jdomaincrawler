package jdomaincrawler.stripper;

import java.io.File;
import java.util.Date;

import jdomaincrawler.controller.Controller;
import jdomaincrawler.controller.PropertiesFactory;

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
	private Writer w;
	private int MAXLENGTH;
	private int MINLENGTH;

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
		this.w = new Writer();
		MAXLENGTH = Integer.parseInt(PropertiesFactory.getProperties()
				.getProperty("textmaxlength"));
		MINLENGTH = Integer.parseInt(PropertiesFactory.getProperties()
				.getProperty("textminlength"));
	}

	@Override
	public final void run() {
		logger.debug("Stripping {}", inputFile);
		File file = new File(inputFile);
		if (file.exists()) {
			EncodingDetector detect = new EncodingDetector(file);
			String encoding = detect.getBestEncoding();
			SimpleHTML2Text ht;
			ht = new SimpleHTML2Text(inputFile, encoding);
			String text = ht.getUTF8Text();
			text =text.trim();
			if (MINLENGTH <= text.length() && text.length() <= MAXLENGTH) {
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
				buffer.append(text);
				buffer.append("\n\n");
				w.write(buffer.toString(), outputFile);
			}
		}
		this.controller.stripFinished(this.outputFile);
	}
}

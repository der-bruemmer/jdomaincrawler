package jdomaincrawler.stripper;

import java.io.IOException;

import org.apache.commons.io.output.LockableFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Writer {

	private static final Logger logger = LoggerFactory.getLogger("Writer");

	public void write(final String text, final String file) {
		LockableFileWriter writer = null;
		while (writer == null) {
			try {
				writer = new LockableFileWriter(file, true);
			} catch (IOException e) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					logger.error(e1.getMessage());
				}
			}
		}
		try {
			writer.append(text);
			writer.flush();
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

}

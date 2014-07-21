import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

public class TestPdfToGif3 {
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		pdfToImage("/home/kulen/NmrMsdsETL/pdf_reader/50-50-0.pdf", "/home/kulen/NmrMsdsETL/pdf_reader/1.png");
		pdfToImage("/home/kulen/NmrMsdsETL/pdf_reader/51-67-2.pdf", "/home/kulen/NmrMsdsETL/pdf_reader/2.png");
		pdfToImage("/home/kulen/NmrMsdsETL/pdf_reader/53-43-0.pdf", "/home/kulen/NmrMsdsETL/pdf_reader/3.png");
		pdfToImage("/home/kulen/NmrMsdsETL/pdf_reader/58-08-2.pdf", "/home/kulen/NmrMsdsETL/pdf_reader/4.png");
		long spendTime = System.currentTimeMillis() - start;
		System.out.println("用时:" + spendTime);
	}

	public static void pdfToImage(String source, String target) {
		String filePath = source;

		Document document = new Document();
		try {
			document.setFile(filePath);
		} catch (Exception ex) {
		}

		// save page caputres to file.
		float scale = 1.3f;
		float rotation = 0f;

		// Paint each pages content to an image and write the image to file
		for (int i = 0; i < document.getNumberOfPages(); i++) {
			BufferedImage image = (BufferedImage) document.getPageImage(i, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, rotation, scale);
			RenderedImage rendImage = image;
			// capture the page image to file
			try {
				System.out.println("/t capturing page " + i);
				File file = new File(target);
				ImageIO.write(rendImage, "png", file);

			} catch (IOException e) {
				e.printStackTrace();
			}
			image.flush();
		}
		// clean up resources
		document.dispose();
	}
}
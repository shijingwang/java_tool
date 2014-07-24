package com.tool.msds;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

/**
 * @author kulen
 * @createTime Jul 21, 2014 7:44:14 PM
 * @desc
 */
public class PdfUtil {

	public void covertPdfToImage(String source, String target, String days) {
		File sourceDir = new File(source + days);
		File[] files = sourceDir.listFiles();
		File targetDir = new File(target + days);
		if (!targetDir.exists()) {
			targetDir.mkdirs();
		}
		System.out.println("需要转换的文件数量:" + files.length);
		for (File file : files) {
			if (file.length() <= 50 * 1024) {
				continue;
			}
			this.pdfToImage(file.getAbsolutePath(), String.format("%s%s/%s", target, days, file.getName().replace(".pdf", ".png")));
		}
		System.out.println("程序运行完成");

	}

	public boolean pdfToImage(String source, String target) {
		try {
			String filePath = source;
			Document document = new Document();
			document.setFile(filePath);

			float scale = 1.3f;
			float rotation = 0f;

			BufferedImage image = (BufferedImage) document.getPageImage(0, GraphicsRenderingHints.SCREEN, Page.BOUNDARY_CROPBOX, rotation, scale);
			RenderedImage rendImage = image;
			File file = new File(target);
			ImageIO.write(rendImage, "png", file);
			image.flush();
			document.dispose();
			System.out.println("Pdf文档成功转换:" + source);
			return true;
		} catch (Exception e) {
			System.out.println("Pdf文档成功转换失败:" + source + e.getMessage());
			return false;
		}
	}

	public static void main(String args[]) {
		PdfUtil pdfUtil = new PdfUtil();
		String source = "/home/kulen/NmrMsdsETL/nmrdb_file/";
		String target = "/home/kulen/NmrMsdsETL/nmrdb_file_image/";
		String[] days = { "2014-07-07", "2014-07-08", "2014-07-09", "2014-07-10", "2014-07-11", "2014-07-12", "2014-07-13", "2014-07-14", "2014-07-15", "2014-07-16" };
		for (String day : days) {
			pdfUtil.covertPdfToImage(source, target, day);
		}
	}
}

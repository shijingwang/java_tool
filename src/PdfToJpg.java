import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class PdfToJpg {
	
	public static void main(String args[]) throws Exception{
		long start = System.currentTimeMillis();
		pdfToJpg("/home/kulen/NmrMsdsETL/pdf_reader/50-50-0.pdf","/home/kulen/NmrMsdsETL/pdf_reader/1.jpg",1);
		long spendTime = System.currentTimeMillis() - start;
		System.out.println("用时:" + spendTime);
	}
	
	public static void pdfToJpg(String source, String target, int x) throws Exception {
		// 创建从中读取和向其中写入（可选）的随机访问文件流，R表示对其只是访问模式
		RandomAccessFile rea = new RandomAccessFile(new File(source), "r");

		// 将流读取到内存中，然后还映射一个PDF对象
		FileChannel channel = rea.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		PDFFile pdfFile = new PDFFile(buf);
		PDFPage page = pdfFile.getPage(1);

		
		int width=1200;
		int height=847;
		
		// get the width and height for the doc at the default zoom
		java.awt.Rectangle rect = new java.awt.Rectangle(0, 0, width, height);
		System.out.println("Width:" + rect.width);
		System.out.println("Height:" + rect.height);

		// generate the image
		
		java.awt.Image img = page.getImage(width, height, // width &
				rect, // clip rect
				null, // null for the ImageObserver
				true, // fill background with white
				true // block until drawing is done
				);

		BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		tag.getGraphics().drawImage(img, 0, 0, width, height, null);
		FileOutputStream out = new FileOutputStream(target); // 输出到文件流
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
		encoder.encode(tag); // JPEG编码
		out.close();
	}

	/**
	 * @param source
	 *            源PDF文件路径
	 * @param target
	 *            保存PDF文件路径
	 * @param pageNum
	 *            提取PDF中第pageNum页
	 * @throws Exception
	 */
	private static void pdfExtraction(String source, String target, int pageNum) throws Exception {
		// 1：创建PDF读取对象
		PdfReader pr = new PdfReader(source);
		System.out.println("this document " + pr.getNumberOfPages() + " page");

		// 2：将第page页转为提取，创建document对象
		Document doc = new Document(pr.getPageSize(pageNum));

		// 3：通过PdfCopy转其单独存储
		PdfCopy copy = new PdfCopy(doc, new FileOutputStream(new File(target)));
		doc.open();
		doc.newPage();

		// 4：获取第1页，装载到document中。
		PdfImportedPage page = copy.getImportedPage(pr, pageNum);
		copy.addPage(page);

		// 5：释放资源
		copy.close();
		doc.close();
		pr.close();
	}

	/**
	 * @param pdfFile
	 *            源PDF文件
	 * @param imgFile
	 *            图片文件
	 */
	private static void jpgToPdf(File pdfFile, File imgFile) throws Exception {
		// 文件转img
		InputStream is = new FileInputStream(pdfFile);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i; (i = is.read()) != -1;) {
			baos.write(i);
		}
		baos.flush();

		// 取得图像的宽和高。
		Image img = Image.getInstance(baos.toByteArray());
		float width = img.getWidth();
		float height = img.getHeight();
		img.setAbsolutePosition(0.0F, 0.0F);// 取消偏移
		System.out.println("width = " + width + "\theight" + height);

		// img转pdf
		Document doc = new Document(new Rectangle(width, height));
		PdfWriter pw = PdfWriter.getInstance(doc, new FileOutputStream(imgFile));
		doc.open();
		doc.add(img);

		// 释放资源
		System.out.println(doc.newPage());
		pw.flush();
		baos.close();
		doc.close();
		pw.close();
	}

}

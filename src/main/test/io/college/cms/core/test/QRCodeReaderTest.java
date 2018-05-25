package io.college.cms.core.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import net.glxn.qrgen.QRCode;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class QRCodeReaderTest {
	private int maxQrCodes = 900;
	private String pdfFileName = "TestData.pdf";

	@Test
	public void createQrCode() throws IOException {
		File file = QRCode.from("http://www.google.com").file();
		BufferedImage image = ImageIO.read(file);
		File outputFile = new File("image.png");
		ImageIO.write(image, "png", outputFile);
		Assert.assertEquals(true, outputFile.exists());
	}

	@Test(expected = AssertionFailedError.class)
	public void failedCreateQr() throws IOException {
		File file = QRCode.from("http://www.google.com").file();
		BufferedImage image = ImageIO.read(file);
		File outputFile = new File("image.png");
		ImageIO.write(image, "png", outputFile);
		outputFile.delete();
		Assert.assertEquals(true, outputFile.exists());
	}

	@Test
	public void createImageAndTextPDF() throws IOException, InterruptedException {
		PDDocument pdf = new PDDocument();

		PDPage page = new PDPage();

		PDImageXObject image = PDImageXObject.createFromFile(QRCode
				.from("http://locahost:8080/1.0/exams/qr-scan?username=ankitshahit&exam_name=exam1&subject_name=english")
				.withSize(10, 10).file().getAbsolutePath(), pdf);
		System.out.println(page.getCropBox().getHeight());
		System.out.println(page.getCropBox().getWidth());
		float width = 10;
		float height = page.getCropBox().getHeight() - 50.0f;
		PDPageContentStream contentStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND,
				true);

		for (int index = 0; index < maxQrCodes; index++) {
			if (width > page.getCropBox().getWidth()) {
				contentStream.close();
				pdf.addPage(page);
				page = new PDPage();

				width = 10;
				height = page.getCropBox().getHeight() - 50.0f;

				contentStream = new PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true);
				System.out.println("adding new page");
			}

			image = PDImageXObject.createFromByteArray(pdf,
					QRCode.from("" + index).withSize(10, 10).stream().toByteArray(), "index-name" + index);

			System.out.println("[width = " + width + "], [height = " + height + "]");
			contentStream.drawImage(image, width, height);
			PDFont font = PDType1Font.HELVETICA;
			contentStream.setFont(font, 8);

			contentStream.beginText();
			contentStream.newLineAtOffset(width, height - 5);
			contentStream.showText("Text7855");
			contentStream.endText();

			width += 50;
			// going to new row.
			if (index % 12 == 0 && (height > 0 && (height - 50 > 0)) && width < page.getCropBox().getWidth()) {
				height -= 50.0f;
				width = 10;
			}
		}
		contentStream.close();
		pdf.addPage(page);
		File outputFile = new File(pdfFileName);

		pdf.save(outputFile);
		pdf.close();
		System.out.println(outputFile.getAbsolutePath());
		Assert.assertEquals(true, outputFile.exists());
	}

	@Test
	public void readImages() throws InvalidPasswordException, IOException {
		PDDocument doc = PDDocument.load(new File(pdfFileName));
		List<String> textsFromBarCode = new ArrayList<>();
		doc.getPages().forEach(page -> {
			PDResources resource = page.getResources();
			resource.getXObjectNames().forEach(objectName -> {
				try {
					PDXObject xObject = resource.getXObject(objectName);
					if (!(xObject instanceof PDImageXObject)) {
						return;
					}
					PDImageXObject imageObject = (PDImageXObject) xObject;
					if (xObject != null && imageObject.getImage() != null) {
						BufferedImage bufferedImage = imageObject.getImage();

						LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
						BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

						try {
							Result result = new MultiFormatReader().decode(bitmap);
							textsFromBarCode.add(result.getText());
						} catch (NotFoundException e) {
							System.out.println("There is no QR code in the image");
						}

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		});
		doc.close();
		System.out.println(textsFromBarCode);

		Assert.assertTrue("Qr codes don't match.", CollectionUtils.size(textsFromBarCode) == maxQrCodes);

	}

}

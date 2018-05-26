package io.college.cms.core.application.pdf.factory;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import io.college.cms.core.exception.ApplicationException;
import lombok.Getter;
import lombok.NonNull;

public class PDFEngine implements IPdfEngine {
	public static int WIDTH_INDEX = 0;
	public static int HEIGHT_INDEX = 1;
	public static int FONT_SIZE_INDEX = 2;
	@Getter
	private PDPageContentStream contentStream;

	public PDFEngine(@NonNull PDPageContentStream stream) {
		this.contentStream = stream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.college.cms.core.application.pdf.factory.IPDFEngine#insertImage(org.
	 * apache.pdfbox.pdmodel.graphics.image.PDImageXObject, float)
	 */
	@Override
	public void insertImage(PDImageXObject image, float... args) throws ApplicationException {
		try {
			contentStream.drawImage(image, args[WIDTH_INDEX], args[HEIGHT_INDEX]);
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.college.cms.core.application.pdf.factory.IPDFEngine#insertText(org.
	 * apache.pdfbox.pdmodel.font.PDFont, java.lang.String, float)
	 */
	@Override
	public void insertText(PDFont font, String text, float... fs) throws ApplicationException {
		try {
			contentStream.setFont(font, fs[FONT_SIZE_INDEX]);
			contentStream.beginText();
			contentStream.newLineAtOffset(fs[WIDTH_INDEX], fs[HEIGHT_INDEX]);
			contentStream.showText(text);
			contentStream.endText();
		} catch (IOException e) {
			throw new ApplicationException(e);
		}
	}

}

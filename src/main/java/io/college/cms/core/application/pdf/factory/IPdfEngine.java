package io.college.cms.core.application.pdf.factory;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import io.college.cms.core.exception.ApplicationException;

public interface IPdfEngine {

	void insertImage(PDImageXObject image, float... args) throws ApplicationException;

	void insertText(PDFont font, String text, float... fs) throws ApplicationException;

	PDPageContentStream getContentStream();

}
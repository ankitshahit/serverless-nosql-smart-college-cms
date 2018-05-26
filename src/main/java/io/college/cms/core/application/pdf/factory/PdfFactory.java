package io.college.cms.core.application.pdf.factory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
public class PdfFactory {
	private PDDocument document;
	private PDPage pdPage;

	public PDPageContentStream createContentStream(PDPageContentStream.AppendMode appendMode, boolean compress)
			throws IOException {
		return new PDPageContentStream(document, pdPage, appendMode, compress);
	}

	public PDDocument addPage(PDDocument document, PDPage page) {
		document.addPage(page);
		return document;
	}

	public void setPage(PDPage pdPage) {
		this.pdPage = pdPage;
	}

	public void addPage() {
		this.document.addPage(this.pdPage);
	}

	public PDPage getPage() {
		return this.pdPage;
	}

	public void addPage(PDPage page) {
		document.addPage(page);
	}

	public void createPDFDocument() {
		document = new PDDocument();
	}

	public void createPage() {
		this.pdPage = new PDPage();
	}

	public void createPDFDocument(byte[] bytes) throws InvalidPasswordException, IOException {
		document = PDDocument.load(bytes);
	}

	public void createPDFDocument(InputStream input) throws InvalidPasswordException, IOException {
		document = PDDocument.load(input);
	}

	public void createPDFDocument(File file) throws InvalidPasswordException, IOException {
		document = PDDocument.load(file);
	}

	public PDDocument build() {
		return document;
	}

	public PDImageXObject createImage(byte[] bytes, String name) throws IOException {
		return PDImageXObject.createFromByteArray(this.document, bytes, name);
	}

	public void closeContentStream(PDPageContentStream contentStream) throws IOException {
		contentStream.close();
	}
}

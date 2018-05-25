package io.college.cms.core.application.pdf.factory;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.college.cms.core.exception.ResourceDeniedException;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(value = BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class PDFactory {
	public PDDocument createPDF() {
		return new PDDocument();
	}

	public void savePDF(PDDocument document, File outputFile) throws ResourceDeniedException {
		try {
			document.save(outputFile);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			throw new ResourceDeniedException(e);
		}
	}

	public PDPage createPage() {
		return new PDPage();
	}

	public PDDocument addPage(PDDocument doc, PDPage page) {
		doc.addPage(page);
		return doc;
	}
}

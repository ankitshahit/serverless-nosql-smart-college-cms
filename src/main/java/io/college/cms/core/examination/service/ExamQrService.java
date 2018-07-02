package io.college.cms.core.examination.service;

import java.io.File;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import io.college.cms.core.application.pdf.factory.IPdfEngine;
import io.college.cms.core.application.pdf.factory.PDFEngine;
import io.college.cms.core.application.pdf.factory.PdfFactory;
import io.college.cms.core.configuration.AppParams;
import io.college.cms.core.courses.controller.constants.SubjectType;
import io.college.cms.core.exception.ApplicationException;
import io.college.cms.core.exception.ExceptionHandler;
import io.college.cms.core.ui.model.ViewConstants;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.QRCode;

@Service
@Slf4j
public class ExamQrService {
	private AppParams app;

	public ExamQrService(AppParams app) {
		this.app = app;
	}

	@Async
	public void asyncFileDownload(String examName, String subjectName, SubjectType type, Runnable progressListener,
			Runnable successListener) {
		progressListener.run();

		successListener.run();
	}

	// we won't accept semester cuz it can be retrieved from exam name
	public File printForSubject(@NonNull String examName, @NonNull String subjectName, @NonNull SubjectType subjectType,
			@NonNull List<String> usernames) throws IllegalArgumentException, ApplicationException {
		File outputFile = new File(new StringBuilder().append(examName).append("_").append(subjectName).append("_")
				.append(subjectType.toString()).append("_").append(CollectionUtils.size(usernames)).toString());
		PdfFactory pdfFactory = new PdfFactory();
		try {
			IPdfEngine engine = null;

			pdfFactory.createPDFDocument();
			pdfFactory.createPage();
			engine = new PDFEngine(pdfFactory.createContentStream(AppendMode.APPEND, false));

			// spacing between two rows.
			float subtractHeight = 125.0f;
			// spacing between values.
			float addWidth = 115.0f;
			// initial weight to start from in case of a new page is created.
			float initialWidth = 10.f;
			// initial height to start from in case of a new page is created.
			float initialHeight = pdfFactory.getPage().getCropBox().getHeight() - subtractHeight;
			// loading seed values to width
			float width = initialWidth;
			// loading seed values to height
			float height = initialHeight;
			// providing spacing after image, this is to achieve text below qr
			// image.
			float textSubtractHeight = 25;
			// font size of text placed right below qr code.
			float fontSize = 10;
			// qr image height
			int qrHeight = 110;
			// qr image width
			int qrWidth = 110;
			// how many images should be placed; changing this would require to
			// re-adjust above variables to fit (qr + text) in tabular format.
			// maybe I should have chosen to search for tabular in pdf :/
			// TO-DO: look for tabular structure in apache pdf.
			float maxQrInRow = 5;

			// we need to start from 1 rather than 0 because 0 % anything is 0.
			// This results in addition of an extra column in case there's a
			// check for index = 0 value.
			// this adds an extra cell(value) making it have say 7 in row place
			// of 6
			for (float index = 1; index <= usernames.size(); index++) {
				// as we are just increasing the width to place values in a
				// single row, width greater than the max_width means we have
				// ran out of page.
				// time to get a new pdf page.
				// and probably reset the variables to initial adjustment
				// values.
				if (width > pdfFactory.getPage().getCropBox().getWidth()) {

					pdfFactory.closeContentStream(engine.getContentStream());
					pdfFactory.addPage();
					pdfFactory.createPage();
					width = initialWidth;
					height = initialHeight;
					engine = new PDFEngine(pdfFactory.createContentStream(AppendMode.APPEND, false));
				}

				String websiteLink = createQrAPILink(examName, subjectName, subjectType,
						usernames.get((int) index - 1));
				byte[] qr = QRCode.from(websiteLink).withSize(qrWidth, qrHeight).stream().toByteArray();
				engine.insertImage(pdfFactory.createImage(qr, ""), width, height);
				// seed value to place text just below the qr image
				engine.insertText(PDType1Font.HELVETICA,
						new StringBuilder().append(" [").append(usernames.get((int) index - 1)).append("] ").toString(),
						width, height + textSubtractHeight - textSubtractHeight, fontSize);

				width += addWidth;

				// we ran out of row, time to adjust variables for insertion at
				// new row.
				if (index != 0 && index % maxQrInRow == 0 && (height > 0 && (height - subtractHeight > 0))) {

					height -= subtractHeight;
					width = initialWidth;
				}
			}
			// we have to process this for the last pdf page.
			pdfFactory.closeContentStream(engine.getContentStream());
			pdfFactory.addPage();
			// saving the whole pdf document and closing it's stream
			// gracefully. Side note - it seems to be working!so yaay?.

		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
			throw new ApplicationException(ex);
		} finally {
			try {
				pdfFactory.build().save(outputFile);
				pdfFactory.build().close();
			} catch (Exception e) {
				LOGGER.error(ExceptionHandler.beautifyStackTrace(e));
			}

		}
		return outputFile;
	}

	/**
	 * sample url =
	 * \/exams/qr/feed?exam_name={exam_name}&subject_name={subject_name}&subject_type={subject_type}&username={username}
	 * 
	 * @param examName
	 * @param subjectName
	 * @param type
	 * @param username
	 * @return
	 * @throws IllegalArgumentException
	 */
	public String createQrAPILink(@NonNull String examName, @NonNull String subjectName, @NonNull SubjectType type,
			@NonNull String username) throws IllegalArgumentException {
		return new StringBuilder().append(app.getHost()).append("/").append(ViewConstants.UPDATE_MARKS_RESULTS)
				.append("/").append(examName).append("/").append(subjectName).append("/").append(type.toString())
				.append("/").append(username).toString();
	}
}

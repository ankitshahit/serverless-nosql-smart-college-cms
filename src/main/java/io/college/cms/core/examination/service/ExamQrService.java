package io.college.cms.core.examination.service;

import java.io.File;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.QRCode;

@Service
@Slf4j
public class ExamQrService {
	@Value("${host:http://localhost:8080}")
	private String host;

	public File printForSubject(@NonNull String examName, @NonNull String subjectName, @NonNull List<String> usernames)
			throws IllegalArgumentException {
		File outputFile = new File("testpdf.pdf");
		try {
			@Cleanup
			PDDocument document = new PDDocument();
			PDPage page = new PDPage();
			PDPageContentStream contentStream = new PDPageContentStream(document, page,
					PDPageContentStream.AppendMode.APPEND, true);
			PDImageXObject image = null;
			float width = 10;
			float height = page.getCropBox().getHeight() - 50.0f;

			for (int index = 0; index < usernames.size(); index++) {
				if (width > page.getCropBox().getWidth()) {
					contentStream.close();
					document.addPage(page);
					page = new PDPage();
					width = 10;
					height = page.getCropBox().getHeight() - 50.0f;
					contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND,
							true);
				}

				image = PDImageXObject.createFromByteArray(document,
						QRCode.from(createQrAPILink(examName, subjectName, usernames.get(index))).withSize(10, 10)
								.stream().toByteArray(),
						"");

				contentStream.drawImage(image, width, height);
				PDFont font = PDType1Font.HELVETICA;
				contentStream.setFont(font, 8);

				contentStream.beginText();
				contentStream.newLineAtOffset(width, height - 5);
				contentStream.showText(usernames.get(index));
				contentStream.endText();

				width += 50;
				// going to new row.
				if (index % 12 == 0 && (height > 0 && (height - 50 > 0)) && width < page.getCropBox().getWidth()) {
					height -= 50.0f;
					width = 10;
				}
			}
			contentStream.close();
			document.addPage(page);
			document.save(outputFile);
			document.close();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		return outputFile;
	}

	public String createQrAPILink(@NonNull String examName, String subjectName, @NonNull String username) {
		return new StringBuilder().append(host).append("/exams/qr/feed?").append("exam_name=").append(examName)
				.append("&").append("subject_name=").append(subjectName).append("&").append("username=")
				.append(username).toString();
	}
}

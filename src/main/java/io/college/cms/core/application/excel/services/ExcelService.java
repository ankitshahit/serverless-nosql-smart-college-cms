package io.college.cms.core.application.excel.services;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.vaadin.ui.ProgressBar;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Slf4j
public class ExcelService {
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;

	private List<String> headers;
	@Setter
	private String filename;

	public void setHeaderTitle(String... strings) {
		headers = Arrays.asList(strings);
	}

	@Async
	public void write(List<String> data, Consumer<File> fileConsumer, Consumer<String> errorMsgListener,
			Consumer<Float> progressListener, Runnable successListener) {
		try {
			if (headers == null) {
				errorMsgListener.accept("No headers provided.");
				return;
			}
			File file = new File("temp.xlsx");

			workbook = new XSSFWorkbook();
			sheet = workbook.createSheet();
			// each column
			int rowCount = 0;
			Row row = getRow(rowCount);
			Iterator<String> iterator = data.iterator();

			int index = 0;
			for (String string : headers) {
				writeCell(index++, row, string);
			}
			rowCount =1 ;
			row = getRow(rowCount);
			index = 0;
			progressListener.accept(50.0f);
			float progress = 50.0f;
			while (iterator.hasNext()) {
				writeCell(index, row, iterator.next());
				iterator.remove();
				if (index + 1 == CollectionUtils.size(headers)) {
					rowCount++;
					index = -1;
					row = getRow(rowCount);
					progressListener.accept(progress + 0.1f);
				}
				index++;
			}

			try (FileOutputStream outputStream = new FileOutputStream(file)) {
				workbook.write(outputStream);
				progressListener.accept(98.0f);
				workbook.close();
				progressListener.accept(99.0f);
				outputStream.close();
				progressListener.accept(99.4f);
				fileConsumer.accept(file);
				progressListener.accept(100.0f);
				successListener.run();
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
				errorMsgListener.accept("Unable to write to excel file.");
			}

		} catch (Exception ex) {
			LOGGER.error("error occurred");
		}
	}

	private Row getRow(int row) {
		return sheet.createRow(row);
	}

	private void writeCell(int cellNum, Row row, String value) {
		org.apache.poi.ss.usermodel.Cell cell = row.createCell(cellNum);
		cell.setCellValue(value);
	}
}

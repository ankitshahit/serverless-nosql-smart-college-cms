package io.college.cms.core.announcement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

import lombok.Cleanup;

public class GenerateHtml {
	public static void main(String[] args) throws IOException {
		/*@Cleanup
		Scanner writeData = new Scanner(System.in);
		System.out.println("how many rows to be written");
		long rows = Long.valueOf(writeData.nextLine());
		long count = 1;
		StringBuilder data = new StringBuilder();
		while (count <= rows) {
			data.append(writeData.nextLine());
			count++;
		}
		
		@Cleanup
		Writer write = new FileWriter(new File("F:\\workspace\\college.io\\src\\main\\resources\\details.txt"));
		write.write(data.toString());
		System.out.println("Written to file");*/
		@Cleanup
		Scanner sc = new Scanner(new File("F:\\workspace\\college.io\\src\\main\\resources\\details.txt"));
		StringBuilder sb = new StringBuilder();

		int columns = 6;
		int index = 1;
		while (sc.hasNextLine()) {
			sb.append(sc.nextLine());
		}
		StringTokenizer stringTokenizer = new StringTokenizer(sb.toString(), ";");
		StringBuilder htmlContent = new StringBuilder();
		int rowIndex = 1;
		htmlContent.append(
				"<tr style=\"background-color:#ADD8E6\"><th>SR</th><th>Description</th><th>HSN</th><th>QTY</th><th>Rate</th><th>Amount</th></tr>");
		htmlContent.append("<tr>");
		int rateData = 0;
		int quantityData = 0;
		int currentColumn = 1;
		long maxAmount = 0;
		while (stringTokenizer.hasMoreTokens() || rateData > 0) {

			if (currentColumn == 4) {
				quantityData = Integer.valueOf(String.valueOf(stringTokenizer.nextElement()));
				htmlContent.append("<td align=center>");
				htmlContent.append(String.valueOf(quantityData));
				htmlContent.append("</td>");

			} else if (currentColumn == 1) {
				htmlContent.append("<td width=20 align=center>").append(rowIndex).append("</td>");
			} else if (currentColumn == 5) {
				rateData = Integer.valueOf(String.valueOf(stringTokenizer.nextElement()));
				htmlContent.append("<td width=100 align=right>");
				htmlContent.append(String.format("%.2f", Double.valueOf(rateData)));
				htmlContent.append("</td>");
			} else if (currentColumn == 6) {
				maxAmount += quantityData * rateData;
				htmlContent.append("<td width=100 align=right>")
						.append(String.format("%.2f", Double.valueOf(quantityData * rateData))).append("</td>");
				rateData = 0;
			} else if (currentColumn == 3) {
				htmlContent.append("<td  width=50 align=center>");
				htmlContent.append(String.valueOf(stringTokenizer.nextElement()));
				htmlContent.append("</td>");
			} else {
				htmlContent.append("<td  width=150 align=center>");
				htmlContent.append(String.valueOf(stringTokenizer.nextElement()));
				htmlContent.append("</td>");
			}
			if (currentColumn == columns) {
				htmlContent.append("</tr>");
				htmlContent.append("<tr>");

				rowIndex++;
				currentColumn = 0;
				htmlContent.append("\n");
			}
			index++;
			currentColumn++;

		}
		htmlContent.append("</tr>");
		htmlContent.append("<tr><td colspan=\"6\" align=\"right\"><b>Total amount:</b>")
				.append(String.format("%.2f", Double.valueOf(maxAmount))).append("</td>	</tr>");
		double gst = Math.round(maxAmount * 5 / 100);
		htmlContent.append("<tr><td colspan=\"6\" align=\"right\"><b>GST 5%: </b>")
				.append(String.format("%.2f", Double.valueOf(gst))).append("</td>	</tr>");
		htmlContent.append("<tr><td colspan=\"6\" align=\"right\"><b>Total amount after GST: </b>")
				.append(String.format("%.2f", Double.valueOf(Math.round(maxAmount + gst)))).append("</td>	</tr>");

		System.out.println(htmlContent.toString());
	}

	public static String firstUpperCase(String value) {
		if (StringUtils.isEmpty(value)) {
			return "No Description";
		}
		return StringUtils.capitalize(value);
	}
}

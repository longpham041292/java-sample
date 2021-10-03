package asia.cmg.f8.common.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public final class FileExportUtils {

	private FileExportUtils() {
		// empty
	}

	private static final Logger LOG = LoggerFactory.getLogger(FileExportUtils.class);

	public static final String CSV_FILE_TYPE = "text/csv";

	public static StreamingResponseBody exportCSV(final List<?> beans, final String[] fileHeader, final String fileName,
			final HttpServletResponse response) throws IOException {

		LOG.info("exportCSV", fileName);
		response.setContentType(CSV_FILE_TYPE);

		final String headerKey = "Content-Disposition";
		final String headerValue = String.format("attachment; filename=\"%s\"", fileName);
		response.setHeader(headerKey, headerValue);

		return outputStream -> {
			ICsvBeanWriter csvWriter = null;
			Writer osw = null;
			try {
				osw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
				osw.write('\uFEFF');
				csvWriter = new CsvBeanWriter(osw, CsvPreference.STANDARD_PREFERENCE);
				csvWriter.writeHeader(fileHeader);
				for (final Object bean : beans) {
					csvWriter.write(bean, fileHeader);
				}
			} catch (final IOException exp) {
				LOG.error("Error when export CSV {}", exp);
			} finally {
				IOUtils.closeQuietly(csvWriter);
				IOUtils.closeQuietly(osw);
			}
		};
	}

	public static StreamingResponseBody exportMultipleCSV(final Map<String, ? extends List<?>> mapData,
			final Map<String, String[]> headersMap, final String fileName, final HttpServletResponse response) {
		LOG.info("exportMultipleCSV", fileName);
		response.setContentType(CSV_FILE_TYPE);

		final String headerKey = "Content-Disposition";
		final String headerValue = String.format("attachment; filename=\"%s\"", fileName);
		response.setHeader(headerKey, headerValue);

		return outputStream -> {
			ICsvBeanWriter csvWriter = null;
			Writer osw = null;
			try {
				osw = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
				osw.write('\uFEFF');
				csvWriter = new CsvBeanWriter(osw, CsvPreference.STANDARD_PREFERENCE);
				for (Entry<String, ? extends List<?>> entry : mapData.entrySet()) {
					try {
						csvWriter.writeHeader(CommonConstant.CSV_FILE_SEPARATOR);
						csvWriter.writeHeader(entry.getKey() + ".csv");
						csvWriter.writeHeader(CommonConstant.CSV_FILE_HEADER_SEPARATOR);
						final String[] fileHeader = headersMap.get(entry.getKey());
						csvWriter.writeHeader(fileHeader);
						for (final Object bean : entry.getValue()) {
							csvWriter.write(bean, fileHeader);
						}
					} catch (final IOException exp) {
						LOG.error("Error when export CSV {}", exp);
					}
				}
				csvWriter.writeHeader(CommonConstant.CSV_FILE_SEPARATOR);
			} finally {
				IOUtils.closeQuietly(csvWriter);
				IOUtils.closeQuietly(osw);
			}
		};
	}
}

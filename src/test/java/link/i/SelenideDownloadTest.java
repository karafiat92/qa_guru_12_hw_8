package link.i;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class SelenideDownloadTest {

    String zipPath = "src/test/resources/Archive.zip",
            zipFile = "Archive.zip",
            pdfFile = "Lorem.pdf",
            csvFile = "Fish.csv",
            xlsFile = "New line.xls",
            pdfContent = "Lorem ipsum dolor sit amet",
            xlsContent = "line";


    ClassLoader cl = SelenideDownloadTest.class.getClassLoader();

    @Test
    @DisplayName("Checking files in a zip-file")
    void zipTest() throws Exception {
        ZipFile zf = new ZipFile(new File(zipPath));
        ZipInputStream is = new ZipInputStream(cl.getResourceAsStream(zipFile));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            switch (entry.getName()) {
                case ("Lorem.pdf"):
                    assertEquals(entry.getName(), pdfFile);
                    try (InputStream inputStream = zf.getInputStream(entry)) {
                        assert inputStream != null;
                        PDF pdf = new PDF(inputStream);
                        org.assertj.core.api.Assertions.assertThat(pdf.text).contains(pdfContent);
                    }
                    break;
                case ("Fish.csv"):
                    assertEquals(entry.getName(), csvFile);
                    try (InputStream inputStream = zf.getInputStream(entry)) {
                        assert inputStream != null;
                        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                            List<String[]> content = reader.readAll();
                            org.assertj.core.api.Assertions.assertThat(content).contains(
                                    new String[]{"Lorem;"},
                                    new String[]{"ipsum;dolor"}
                            );
                        }
                    }
                    break;
                case ("New line.xls"):
                    assertEquals(entry.getName(), xlsFile);
                    try (InputStream inputStream = zf.getInputStream(entry)) {
                        assert inputStream != null;
                        XLS xls = new XLS(inputStream);
                        String value = xls.excel.getSheetAt(0).getRow(1).getCell(1).getStringCellValue();
                        org.assertj.core.api.Assertions.assertThat(value).isEqualTo(xlsContent);
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + entry.getName());
            }
        }
    }
}

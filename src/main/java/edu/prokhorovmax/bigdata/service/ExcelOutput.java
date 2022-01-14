package edu.prokhorovmax.bigdata.service;

import edu.prokhorovmax.bigdata.model.TestResult;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ExcelOutput {

    private static final Logger logger = LoggerFactory.getLogger(ExcelOutput.class.getName());

    public static void outputTestResults(List<TestResult> results) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Results");
        sheet.setColumnWidth(0, 5000);
        sheet.setColumnWidth(1, 5000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 5000);
        sheet.setColumnWidth(4, 5000);

        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setWrapText(true);

        // 0 row - titles
        XSSFCell cell;
        XSSFRow row = sheet.createRow(0);
        cell = row.createCell(0, CellType.STRING);
        cell.setCellStyle(style);

        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Upload time, s");
        cell.setCellStyle(style);


        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Download time, s");
        cell.setCellStyle(style);


        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Total time, s");
        cell.setCellStyle(style);

        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Mistake percent");
        cell.setCellStyle(style);

        // result rows
        for (int i = 1; i < results.size() + 1; i++) {
            row = sheet.createRow(i);
            cell = row.createCell(0, CellType.STRING);
            cell.setCellValue(results.get(i-1).getSegmentSize() + "");
            cell.setCellStyle(style);

            cell = row.createCell(1, CellType.NUMERIC);
            double value = (double) results.get(i - 1).getUploadTime() / 1000;
            BigDecimal bd = new BigDecimal(Double.toString(value));
            value = bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
            cell.setCellValue(value);
            cell.setCellStyle(style);

            cell = row.createCell(2, CellType.NUMERIC);
            value = (double) results.get(i - 1).getDownloadTime() / 1000;
            bd = new BigDecimal(Double.toString(value));
            value = bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
            cell.setCellValue(value);
            cell.setCellStyle(style);

            cell = row.createCell(3, CellType.NUMERIC);
            value = (double) results.get(i - 1).getFullTime() / 1000;
            bd = new BigDecimal(Double.toString(value));
            value = bd.setScale(2, RoundingMode.HALF_UP).doubleValue();
            cell.setCellValue(value);
            cell.setCellStyle(style);

            cell = row.createCell(4, CellType.NUMERIC);
            value = results.get(i - 1).getMistakePercent();
            bd = new BigDecimal(Double.toString(value));
            value = bd.setScale(4, RoundingMode.HALF_UP).doubleValue();
            cell.setCellValue(value);
            cell.setCellStyle(style);
        }
        File file = new File("Results.xlsx");
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        } catch (IOException ex) {
            logger.error("Error during excel output", ex);
        }
    }

}

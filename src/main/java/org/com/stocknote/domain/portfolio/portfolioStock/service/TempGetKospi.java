package org.com.stocknote.domain.portfolio.portfolioStock.service;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TempGetKospi {
  private static final String BASE_DIR = System.getProperty("user.dir");

  public static void downloadKospiMaster() throws Exception {
    String zipUrl = "https://new.real.download.dws.co.kr/common/master/kospi_code.mst.zip";
    String zipFile = BASE_DIR + "/kospi_code.zip";

    // Download zip file
    URL url = new URL(zipUrl);
    try (InputStream in = url.openStream();
        FileOutputStream out = new FileOutputStream(zipFile)) {
      byte[] buffer = new byte[1024];
      int len;
      while ((len = in.read(buffer)) != -1) {
        out.write(buffer, 0, len);
      }
    }

    // Extract zip file
    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
      zis.getNextEntry();
      try (FileOutputStream fos = new FileOutputStream(BASE_DIR + "/kospi_code.mst")) {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }
      }
    }

    // Delete zip file
    new File(zipFile).delete();
  }

  public static void processKospiMaster() throws Exception {
    String mstFile = BASE_DIR + "/kospi_code.mst";
    String tmpFile1 = BASE_DIR + "/kospi_code_part1.tmp";
    String tmpFile2 = BASE_DIR + "/kospi_code_part2.tmp";

    // Process MST file
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
        new FileInputStream(mstFile), "CP949"));
        BufferedWriter writer1 = new BufferedWriter(new FileWriter(tmpFile1));
        BufferedWriter writer2 = new BufferedWriter(new FileWriter(tmpFile2))) {

      String line;
      while ((line = reader.readLine()) != null) {
        String part1 = line.substring(0, line.length() - 228);
        String code = part1.substring(0, 9).trim();
        String stdCode = part1.substring(9, 21).trim();
        String name = part1.substring(21).trim();
        writer1.write(code + "," + stdCode + "," + name + "\n");

        String part2 = line.substring(line.length() - 228);
        writer2.write(part2 + "\n");
      }
    }

    // Create Excel workbook
    Workbook workbook = new XSSFWorkbook();
    Sheet sheet = workbook.createSheet("KOSPI");

    // Write headers and data
    List<String[]> data = new ArrayList<>();
    // Add your headers and data processing logic here

    // Write to Excel file
    try (FileOutputStream out = new FileOutputStream(BASE_DIR + "/kospi_code.xlsx")) {
      workbook.write(out);
    }

    // Cleanup
    new File(tmpFile1).delete();
    new File(tmpFile2).delete();
    new File(mstFile).delete();
  }

  public static void main(String[] args) {
    try {
      downloadKospiMaster();
      processKospiMaster();
      System.out.println("Done");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

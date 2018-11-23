package com.xml.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Utility {

    public static Phrase setFont(String text, int size, BaseColor color, int style) {
        FontSelector selector1 = new FontSelector();
        Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, size);
        f1.setStyle(style);
        f1.setColor(color);
        selector1.addFont(f1);
        Phrase ph = selector1.process(text);
        return ph;
    }

    public static PdfPCell setCellFonts(Phrase phrase, int horizontalAlignment, int verticalAlignment) {
        PdfPCell AlignCell = new PdfPCell(phrase);
        AlignCell.setHorizontalAlignment(horizontalAlignment);
        AlignCell.setVerticalAlignment(verticalAlignment);

        return AlignCell;
    }

    public static void main(String[] args) throws Exception {
        PDFReporter pdfReporter = new PDFReporter();
        Document pdf = pdfReporter.PDFReporter("TEST EXECUTION REPORT \n FOR \n" +
                "TRACELINK LIFE SCIENCES CLOUD\n" +
                "NEW RELEASE VALIDATION - PROD\n", "The information contained within this document is considered CONFIDENTIAL and is the property of xLM, LLC.");

        for (int i = 0; i < 11; i++) {

            PDFTestReportModel pdftest = new PDFTestReportModel("Test-" + i);
            for (int j = 1; j < 4; j++) {
                pdftest.setStepNum(String.valueOf(j));
                pdftest.setDescription("Executing the test number " + i + " Step number : " + j);
                pdftest.setexpected(Arrays.asList("Result should be either of PASS, FAIL, SKIP, NORUN for test number " + i));
                pdftest.setactuals(Arrays.asList("PASS", "./screenshots/xlm-logo.jpg"));
                pdftest.setTestResultTable();
            }
            pdf.add(pdftest.setTestExecutionTable(i + 2));
            pdf.add(new Paragraph("\n"));
        }

        pdf.close();
    }

    public static String[] getLogFiles() {
        File folder = new File("logs");
        String[] filename = new String[folder.listFiles().length];

        int i = 0;
        for (File file : folder.listFiles()) {
            if (!file.isDirectory()) {
                filename[i] = "./logs/" + file.getName();
                i++;
            }
        }
        return filename;
    }


    public static void main1(String[] args) throws IOException {

        String[] logFiles = getLogFiles();

        for (String file : logFiles) {
            System.out.println(file);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null) {
                System.out.println(st);
            }
            System.out.println("\n\n\n\n\n");
        }

    }

}

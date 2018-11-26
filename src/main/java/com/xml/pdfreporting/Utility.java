package com.xml.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.xml.pdfreporting.PDFTestReportModel.setExecutionStats;

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
        Document pdf = pdfReporter.PDFReporter();

        JSONArray testcases = gettestcases();
        for (int i = 0; i < testcases.size(); i++) {

            JSONObject testcase = (JSONObject) testcases.get(i);

            PDFTestReportModel pdftest = new PDFTestReportModel((String) testcase.get("testname"),
                    (String) testcase.get("test_objective"),
                    (String) testcase.get("test_acceptance"));

            JSONArray steps = (JSONArray) testcase.get("steps");
            for (int j = 0; j < steps.size(); j++) {
                JSONObject step = (JSONObject) steps.get(j);
                pdftest.setStepNum((String) step.get("step_number"));
                pdftest.setDescription((String) step.get("step_description"));
                pdftest.setexpected((java.util.List<String>) step.get("expected_result"));
                pdftest.setactuals((java.util.List<String>) step.get("actual_result"));
                pdftest.setTestResultTable();
            }
            pdf.add(pdftest.setTestExecutionTable(i + 2));
            pdf.add(new Paragraph("\n"));
        }
        
        pdf.add(setExecutionStats(testcases.size() + 2));
        pdf.close();
    }

    public static String[] getFiles(String path) {
        File folder = new File(path);
        String[] filename = new String[folder.listFiles().length];

        int i = 0;
        for (File file : folder.listFiles()) {
            if (!file.isDirectory()) {
                filename[i] = "./" + path + "/" + file.getName();
                i++;
            }
        }
        return filename;
    }


    public static String readFile(String path) throws IOException {

        String[] files = getFiles(path);
        String st = null;
        for (String file : files) {
            System.out.println(file);
            BufferedReader br = new BufferedReader(new FileReader(file));


            while ((st = br.readLine()) != null) {
                System.out.println(st);
            }

        }
        return st;
    }

    public static String formattedDate() {

        Date date = new Date();
        String strDateFormat = "ddMMyyhhmmssSSS";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date);
        return formattedDate;

    }

    public static JSONArray gettestcases() throws IOException, ParseException {

        String content = new String(Files.readAllBytes(Paths.get(("properties/testcase_logs.txt"))));

        JSONParser parser = new JSONParser();
        JSONObject logs = (JSONObject) parser.parse(content);

        return (JSONArray) logs.get("testcases");
    }
}

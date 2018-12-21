package com.xml.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.FontSelector;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Utility {

    static Phrase setFont(String text, int size, BaseColor color, int style) {
        FontSelector selector1 = new FontSelector();
        Font f1 = FontFactory.getFont(FontFactory.HELVETICA, size);
        f1.setStyle(style);
        f1.setColor(color);
        selector1.addFont(f1);
        return selector1.process(text);
    }

    public static void main(String[] args) throws Exception {
        JSONArray testcases = gettestcases();

        PDFReporter pdfReporter = new PDFReporter();
        Document pdf = pdfReporter.PDFReporter();


        for (int i = 0; i < testcases.size(); i++) {

            JSONObject testcase = (JSONObject) testcases.get(i);

            PDFTestReportModel pdftest = new PDFTestReportModel((String) testcase.get("testname"),
                    (String) testcase.get("test_objective"),
                    (String) testcase.get("test_acceptance"));

            JSONArray steps = (JSONArray) testcase.get("steps");
            for (Object step1 : steps) {
                JSONObject step = (JSONObject) step1;
                pdftest.setStepNum((String) step.get("step_number"));
                pdftest.setDescription((String) step.get("step_description"));
                pdftest.setexpected((List<String>) step.get("expected_result"));
                pdftest.setactuals((List<String>) step.get("actual_result"));
                pdftest.setTestResultTable();
            }
            pdf.add(pdftest.setTestExecutionTable(i + 3));
            pdf.add(new Paragraph("\n"));
        }

        pdfReporter.setTOC(pdf);

        pdf.close();
    }

    private static JSONArray gettestcases() throws IOException, ParseException {

        String content = new String(Files.readAllBytes(Paths.get(("properties/testcase_logs.txt"))));

        JSONParser parser = new JSONParser();
        JSONObject logs = (JSONObject) parser.parse(content);

        return (JSONArray) logs.get("testcases");
    }

    static Image imageCell(String imagePath) throws IOException, BadElementException {

        Image img = Image.getInstance(imagePath);
        img.setAlignment(Element.ALIGN_CENTER);
        img.scalePercent(60000 / img.getWidth());

        return img;

    }
}

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Utility {

    /**
     * @param text - the text that needs to be formatted
     * @param size - font size of the expected text
     * @param color - font color of the text
     * @param style - font style, namely Normal, Bold, Italics, BoldItalics
     * @return - a PDF Phrase that can be added directly to a cell
     */
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

    /**
     *
     * @return
     * @throws IOException
     * @throws ParseException
     */
    private static JSONArray gettestcases() throws IOException, ParseException {

        String content = new String(Files.readAllBytes(Paths.get(("properties/testcase_logs.txt"))));

        JSONParser parser = new JSONParser();
        JSONObject logs = (JSONObject) parser.parse(content);

        return (JSONArray) logs.get("testcases");
    }

    /**
     *
     * @param imagePath
     * @return
     * @throws IOException
     * @throws BadElementException
     */
    static Image imageCell(String imagePath) throws IOException, BadElementException {

        Image img = Image.getInstance(imagePath);
        img.setAlignment(Element.ALIGN_CENTER);
        img.scalePercent(60000 / img.getWidth());

        return img;

    }

    /**
     *
     * @param Int
     * @return
     */
    public static String RomanNumerals(int Int) {
        LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<String, Integer>();
        roman_numerals.put("M", 1000);
        roman_numerals.put("CM", 900);
        roman_numerals.put("D", 500);
        roman_numerals.put("CD", 400);
        roman_numerals.put("C", 100);
        roman_numerals.put("XC", 90);
        roman_numerals.put("L", 50);
        roman_numerals.put("XL", 40);
        roman_numerals.put("X", 10);
        roman_numerals.put("IX", 9);
        roman_numerals.put("V", 5);
        roman_numerals.put("IV", 4);
        roman_numerals.put("I", 1);
        String res = "";
        for (Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
            int matches = Int / entry.getValue();
            res += repeat(entry.getKey(), matches);
            Int = Int % entry.getValue();
        }
        return res;
    }

    public static String repeat(String s, int n) {
        if (s == null) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
}

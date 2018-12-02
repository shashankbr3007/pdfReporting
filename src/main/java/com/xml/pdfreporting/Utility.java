package com.xml.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
            pdf.add(pdftest.setTestExecutionTable(i + 3));
            pdf.add(new Paragraph("\n"));
        }



        pdf.close();


        //removeBlankPdfPages("Reports/SampleExecution.pdf", "Reports/SampleExecution_updated.pdf", false);
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

    public static void removeBlankPdfPages(String pdfSourceFile, String pdfDestinationFile, boolean debug) {
        try {
            // step 1: create new reader
            PdfReader r = new PdfReader(pdfSourceFile);
            RandomAccessFileOrArray raf = new RandomAccessFileOrArray(pdfSourceFile);
            Document document = new Document(r.getPageSizeWithRotation(1));
            // step 2: create a writer that listens to the document
            PdfCopy writer = new PdfCopy(document, new FileOutputStream(pdfDestinationFile));
            // step 3: we open the document
            document.open();
            // step 4: we add content
            PdfImportedPage page = null;


            //loop through each page and if the bs is larger than 20 than we know it is not blank.
            //if it is less than 20 than we don't include that blank page.
            for (int i = 1; i <= r.getNumberOfPages(); i++) {
                //get the page content
                byte bContent[] = r.getPageContent(i, raf);
                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                //write the content to an output stream
                bs.write(bContent);
                if (debug)
                    System.out.println("page content length of page " + i + " = " + bs.size());

                //add the page to the new pdf
                if (bs.size() > 1150) {
                    page = writer.getImportedPage(r, i);
                    writer.addPage(page);
                }
                bs.close();
            }
            //close everything
            document.close();
            writer.close();
            raf.close();
            r.close();
        } catch (Exception e) {
            //do what you need here
        }
    }
}

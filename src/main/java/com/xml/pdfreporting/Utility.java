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
import java.util.*;
import java.util.List;

public class Utility {

    public static Phrase setFont(String text, int size, BaseColor color, int style) {
        FontSelector selector1 = new FontSelector();
        Font f1 = FontFactory.getFont(FontFactory.HELVETICA, size);
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
        JSONArray testcases = gettestcases();

        PDFReporter pdfReporter = new PDFReporter();
        pdfReporter.setTestcaseCount(testcases.size() + 2);
        Document pdf = pdfReporter.PDFReporter();


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

        pdfReporter.setTOC(pdf);

        pdf.close();
    }

    /*private static void reorderTOC(int firstChapterPageNum) throws IOException, DocumentException {

        PdfReader reader = new PdfReader("./Reports/" + PDFReporter.fileName *//*+ "_" + formattedDate() *//* + ".pdf");
        int tocPageNum = reader.getNumberOfPages();
        reader.selectPages(String.format("%d, 1-%d", tocPageNum, firstChapterPageNum - 1));
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(PDFReporter.fileName));
        stamper.close();
    }*/

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
        String strDateFormat = "yyyyMMddhhmmssSSS";
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

    public static void updateBlankPdfPages() {
        try {
            // step 1: create new reader
            PdfReader r = new PdfReader("./Reports/" + PDFReporter.fileName /*+ "_" + formattedDate()*/ + ".pdf");
            RandomAccessFileOrArray raf = new RandomAccessFileOrArray("./Reports/" + PDFReporter.fileName /*+ "_" + formattedDate()*/ + ".pdf");
            Document document = new Document(r.getPageSizeWithRotation(1));
            // step 2: create a writer that listens to the document
            PdfWriter writer = writer = PdfWriter.getInstance(document,
                    new FileOutputStream("./Reports/" + PDFReporter.fileName /*+ "_" + formattedDate()*/ + ".pdf"));
            //PdfCopy writer = new PdfCopy(document, new FileOutputStream(pdfDestinationFile));
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
                if (true)
                    System.out.println("page content length of page " + i + " = " + bs.size());

                //add the page to the new pdf
                if (bs.size() < 1150) {
                    Paragraph toc = new Paragraph(setFont("Intentionally left blank", 14, BaseColor.DARK_GRAY, Font.BOLD));
                    toc.setAlignment(Element.ALIGN_MIDDLE);
                    document.add(toc);
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

    public static void main_2(String[] args) {

        List<String> actuals = new ArrayList<>();

        //actuals.add("./screenshots/snapscreen_1537926261355_1537926261404.png");
        actuals.add("2018-09-25 09:44:15 EDT	Executing the query to search Allocation Reports.");
        actuals.add("2018-09-25 09:44:21 EDT	Screenshot taken with search results for packaging code unit level.");

        actuals.add("2018-09-25 09:44:21 EDT	Screenshot taken with search results for packaging code unit level.");
        actuals.add("2018-09-25 09:44:21 EDT	Screenshot taken with search results for packaging code unit level.");
        //actuals.add("./screenshots/snapscreen_1537926261355_1537926261404.png");

        actuals.add("2018-09-25 09:44:21 EDT	Screenshot taken with search results for packaging code unit level.");
        actuals.add("2018-09-25 09:44:21 EDT	Screenshot taken with search results for packaging code unit level.");
        actuals.add("./screenshots/snapscreen_1537926261355_1537926261404.png");

        HashMap<String, List<String>> map = new HashMap<>();
        int imageCount = 0;
        map.put("imageCount_" + imageCount, new ArrayList<String>());
        for (int i = 0; i < actuals.size(); i++) {

            if (actuals.get(i).contains(".jpg") ||
                    actuals.get(i).contains(".png")) {
                if (map.containsKey("imageCount_" + imageCount)) {
                    (map.get("imageCount_" + imageCount)).add(actuals.get(i));
                } else {
                    map.put("imageCount_" + imageCount, new ArrayList<>(Arrays.asList(actuals.get(i))));
                }
                imageCount++;
            } else {
                if (map.containsKey("imageCount_" + imageCount)) {
                    (map.get("imageCount_" + imageCount)).add(actuals.get(i));
                } else {
                    map.put("imageCount_" + imageCount, new ArrayList<>(Arrays.asList(actuals.get(i))));
                }
            }

        }

        Set<String> keys = map.keySet();
    }

    public static void pageNum(Document document) throws IOException, DocumentException {

        BaseFont bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1257,
                BaseFont.EMBEDDED);
        PdfReader readerFinal = new PdfReader("./Reports/" + PDFReporter.fileName /*+ "_" + formattedDate()*/ + ".pdf");
        int totalPages = readerFinal.getNumberOfPages();
        PdfStamper stamp = new PdfStamper(readerFinal, new FileOutputStream("./Reports/" + PDFReporter.fileName + "_" + formattedDate() + ".pdf"));
        PdfContentByte over;

        for (int i = 1; i <= totalPages; i++) {

            over = stamp.getOverContent(i);

            over.beginText();
            over.setFontAndSize(bf, 8);


            //set x & y coordinates in matrix==we need page numbers at top
            over.setTextMatrix(268, PageSize.LEGAL.getHeight() - document.topMargin() - 10);

            over.showText("Page " + i + " of " + totalPages);
            over.endText();
        }

        stamp.close();
    }

    public static Image imageCell(String imagePath) throws IOException, BadElementException {

        Image img = Image.getInstance(imagePath);
        img.setAlignment(Element.ALIGN_CENTER);
        img.scalePercent(60000 / img.getWidth());

        return img;

    }
}

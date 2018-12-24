package com.xml.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static com.xml.pdfreporting.Utility.setFont;

class PDFReporter {

    static int totalIndexPages;
    private String fileName;
    private int firstChapterPageNum;
    private PdfWriter writer;
    private List<String> textComment;
    private String header;
    private String docnumber;
    private String revnumber;
    private String headerimage;
    private String footer;
    private JSONObject modeldetails;
    private JSONObject executiondetails;
    private JSONObject executionstats;
    private JSONObject watermark;
    private List<JSONObject> signatureDetails;
    private JSONObject testcasesummary;

    /**
     * @param document
     * @throws DocumentException
     * @function this method has the framework to set the review table on the PDF document
     */
    private static void setReviewcommentTable(Document document) throws DocumentException {
        PdfPTable review = new PdfPTable(new float[]{10F, 40F});

        PdfPCell line = new PdfPCell();
        line.setBorder(Rectangle.BOX);

        PdfPCell header = new PdfPCell();
        header.setBorder(Rectangle.BOX);
        header.setFixedHeight(30);
        header.setPhrase(new Paragraph(setFont("\t\tREVIEW COMMENTS:", 12, BaseColor.DARK_GRAY, Font.BOLD)));
        header.setColspan(2);
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        review.addCell(header);

        line.setPhrase(new Paragraph(setFont("\t\tCommented By", 11, BaseColor.DARK_GRAY, Font.BOLD)));
        review.addCell(line);

        line.setPhrase(new Paragraph(setFont("Comments", 11, BaseColor.DARK_GRAY, Font.BOLD)));
        review.addCell(line);

        line.setFixedHeight(180);
        line.setPhrase(new Paragraph("\n\n"));
        review.addCell(line);
        review.addCell(line);


        review.setWidthPercentage(85);
        document.add(review);
        document.newPage();

    }

    /**
     * @param details
     * @return
     * @function this method has the framework to set the model details table on the PDF document
     * Also it will set the execution details on the PDF document
     */
    private static PdfPTable setModelDetails(JSONObject details) {

        PdfPTable modelDetails = new PdfPTable(new float[]{10, 25, 40});
        modelDetails.setWidthPercentage(85);
        PdfPCell keyCell = new PdfPCell();
        keyCell.setFixedHeight(25);
        keyCell.setBorder(Rectangle.BOX);

        PdfPCell valueCell = new PdfPCell();
        valueCell.setFixedHeight(25);
        valueCell.setBorder(Rectangle.BOX);

        PdfPCell blankCell = new PdfPCell();
        blankCell.setPhrase(new Paragraph("\n"));
        blankCell.setBorder(Rectangle.NO_BORDER);

        for (String key : (Set<String>) details.keySet()) {
            keyCell.setPhrase(new Phrase(setFont(key, 11, BaseColor.BLACK, Font.NORMAL)));
            modelDetails.addCell(keyCell);
            valueCell.setPhrase(new Phrase(setFont(String.valueOf(details.get(key)), 11, BaseColor.BLACK, Font.NORMAL)));
            modelDetails.addCell(valueCell);
            modelDetails.addCell(blankCell);
        }
        return modelDetails;
    }

    /**
     * @param document
     * @throws DocumentException
     * @throws IOException
     * @function this method read the signature names and images from the prop file and set the signature table acoordingly
     */
    private void setSignatureTable(Document document) throws DocumentException, IOException {

        PdfPTable signature = new PdfPTable(new float[]{25F, 15F, 10F, 40F});
        int tableHeight = 310;

        PdfPCell dte = new PdfPCell();
        dte.setFixedHeight((tableHeight / 4) / signatureDetails.size());
        dte.setBorder(Rectangle.TOP);
        dte.setPhrase(new Phrase(setFont("Date", 11, BaseColor.BLACK, Font.NORMAL)));

        PdfPCell verticalgap = new PdfPCell();
        verticalgap.setFixedHeight((tableHeight / 4) / signatureDetails.size());
        verticalgap.setBorder(Rectangle.NO_BORDER);

        PdfPCell horizontalgap = new PdfPCell();
        horizontalgap.setFixedHeight((tableHeight) / signatureDetails.size());
        horizontalgap.setBorder(Rectangle.TOP);
        horizontalgap.setColspan(4);

        PdfPCell names = new PdfPCell();
        names.setFixedHeight((tableHeight / 4) / signatureDetails.size());
        names.setBorder(Rectangle.TOP);

        for (JSONObject approver : signatureDetails) {
            signature.addCell(horizontalgap);

            Image img = Image.getInstance(String.valueOf(approver.get("image")));
            img.scalePercent(20);
            img.setAlignment(Element.ALIGN_CENTER);
            PdfPCell nameImage = new PdfPCell();
            nameImage.setFixedHeight((tableHeight / 2) / signatureDetails.size());
            nameImage.setBorder(Rectangle.TOP);
            nameImage.addElement(img);

            names.setPhrase(new Phrase(setFont(String.valueOf(approver.get("approver")), 11, BaseColor.BLACK, Font.NORMAL)));
            signature.addCell(names);
            signature.addCell(nameImage);
            signature.addCell(verticalgap);
            signature.addCell(dte);

        }

        signature.setWidthPercentage(85);
        document.add(signature);
    }

    /**
     * @param document
     * @throws DocumentException
     * @function this method reads the data from template properties and sets the execution details of the model
     */
    private void setExecutionModelDetails(Document document) throws DocumentException {

        Chapter executionModelDetails = new Chapter(new Paragraph(setFont("ENVIRONMENT DETAILS ", 14, BaseColor.DARK_GRAY, Font.BOLD)), 1);

        if (modeldetails != null) {
            executionModelDetails.add(new Paragraph("\n"));
            Section modelDetail = executionModelDetails.addSection(new Paragraph(setFont("AUTOMATION MODEL VERSION RELEASE DETAILS", 12, BaseColor.DARK_GRAY, Font.BOLD)));
            modelDetail.add(new Paragraph("\n"));
            modelDetail.add(setModelDetails(modeldetails));
        }

        if (executiondetails != null) {
            executionModelDetails.add(new Paragraph("\n"));
            Section executionDetail = executionModelDetails.addSection(new Paragraph(setFont("TEST EXECUTION ENVIRONMENT DETAILS ", 12, BaseColor.DARK_GRAY, Font.BOLD)));
            executionDetail.add(new Paragraph("\n"));
            executionDetail.add(setModelDetails(executiondetails));
        }

        if (executionstats != null) {
            executionModelDetails.add(new Paragraph("\n"));
            Section executionStats = executionModelDetails.addSection(new Paragraph(setFont("EXECUTION STATISTICS", 12, BaseColor.DARK_GRAY, Font.BOLD)));
            executionStats.add(new Paragraph("\n"));
            executionStats.add(setModelDetails(executionstats));

        }

        document.add(executionModelDetails);
    }

    /**
     * @param document
     * @throws DocumentException
     * @function this method sets the conditions and benchmarks on which the execution results are being published
     */
    private void setPostExecutionApprovals(Document document) throws DocumentException {

        PdfPTable comments = new PdfPTable(1);
        comments.setWidthPercentage(85);

        PdfPCell header = new PdfPCell();
        header.setBorder(Rectangle.BOX);
        header.setFixedHeight(30);
        header.setPhrase(new Paragraph(setFont("\t\tPOST EXECUTION APPROVALS:", 11, BaseColor.DARK_GRAY, Font.BOLD)));
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        comments.addCell(header);

        PdfPCell comment = new PdfPCell();
        comment.setFixedHeight(25);
        comment.setBorder(Rectangle.NO_BORDER);

        comment.setPhrase(new Phrase(setFont("An individual’s review of the executed test suite consists of the following:", 11, BaseColor.BLACK, Font.NORMAL)));
        comments.addCell(comment);
        for (String text : textComment) {
            comment.setPhrase(new Phrase(setFont("\u2022\u00a0" + text.replace(' ', '\u00a0'), 11, BaseColor.BLACK, Font.NORMAL)));
            comments.addCell(comment);
        }

        document.add(comments);
        document.add(new Paragraph("\n\n"));
    }

    /**
     * @return
     * @throws Exception
     * @function this is the Main method which drives the PDF template design,
     * this has all the steps in sequence to prepare the PDF template
     */
    Document PDFReporter() throws Exception {

        setTemplateProps();
        Document document = new Document(PageSize.LEGAL.rotate());
        document.setMargins(30, 30, 65, 65);
        writer = PdfWriter.getInstance(document,
                new FileOutputStream(fileName));

        writer.setLinearPageMode();
        /*SET HEADER AND FOOTER FOR THE PDF*/
        HeaderFooterPageEvent event = new HeaderFooterPageEvent(writer);
        event.setHeader(header);
        event.setHeaderimage(headerimage);
        event.setFooter(footer);
        event.setDocnumber(docnumber);
        event.setRevnumber(revnumber);
        event.setWaterMarkText((String) watermark.get("waterMarkText"));
        event.setWaterMarkTextxPosition(Integer.valueOf((String) watermark.get("waterMarkTextxPosition")));
        event.setWaterMarkTextyPosition(Integer.valueOf((String) watermark.get("waterMarkTextyPosition")));

        writer.setPageEvent(event);

        /*OPEN THE PDF DOCUMENT FOR WRITING*/
        document.open();

        /*SET THE LOGO FOR THE DOCUMENT, THE IMAGE IS PICKED FROM SCREENSHOTS DIRECTORY*/
        setReportLogo(document);

        /*SET TEST SUMMARY REPORT WITH THE EXECUTION NUMERIC TABLES */
        setPostExecutionApprovals(document);
        setReviewcommentTable(document);
        setSignatureTable(document);
        HeaderFooterPageEvent.firstChapterPN = writer.getPageNumber();
        setExecutionModelDetails(document);

        if (testcasesummary != null) {
            setTestSummaryDetails(document);
        }

        return document;
    }

    /**
     * @param document
     * @throws IOException
     * @throws DocumentException
     * @function this methods sets the Test Summary report with a sequence daigram
     */
    private void setTestSummaryDetails(Document document) throws IOException, DocumentException {

        JSONArray testsummary = (JSONArray) testcasesummary.get("testcasesummary");
        float[] colWidth = new float[]{35, 15, 50};

        PdfPTable summaryTable = new PdfPTable(colWidth);

        PdfPCell header = new PdfPCell();
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);

        header.setPhrase(setFont("Test Case Title", 12, BaseColor.DARK_GRAY, Font.BOLD));
        summaryTable.addCell(header);

        header.setPhrase(setFont("Test Case Status", 12, BaseColor.DARK_GRAY, Font.BOLD));
        summaryTable.addCell(header);

        header.setPhrase(setFont("Test Case Results", 12, BaseColor.DARK_GRAY, Font.BOLD));
        summaryTable.addCell(header);

        PdfPCell value = new PdfPCell();
        value.setHorizontalAlignment(Element.ALIGN_CENTER);
        value.setVerticalAlignment(Element.ALIGN_MIDDLE);

        for (Object aTestsummary : testsummary) {
            JSONObject test = (JSONObject) aTestsummary;

            value.setPhrase(setFont((String) test.get("test_case_title"), 11, BaseColor.BLACK, Font.NORMAL));
            summaryTable.addCell(value);

            value.setPhrase(setFont((String) test.get("test_case_status"), 11, BaseColor.BLACK, Font.NORMAL));
            summaryTable.addCell(value);

            value.setPhrase(setFont((String) test.get("test_case_results"), 11, BaseColor.BLACK, Font.NORMAL));
            summaryTable.addCell(value);
        }

        PdfPCell blankCell = new PdfPCell();
        blankCell.setPhrase(new Paragraph("\n"));
        blankCell.setBorder(Rectangle.NO_BORDER);
        blankCell.setColspan(3);
        summaryTable.addCell(blankCell);
        summaryTable.addCell(blankCell);

        Image img = Image.getInstance((String) testcasesummary.get("testsequencechart"));
        img.setAlignment(Element.ALIGN_CENTER);

        PdfPCell seqImage = new PdfPCell();
        seqImage.setBorder(Rectangle.NO_BORDER);
        seqImage.addElement(img);
        seqImage.setColspan(3);
        summaryTable.addCell(seqImage);

        Chapter summaryChapter = new Chapter(2);
        summaryChapter.setTitle(new Paragraph(setFont("TEST EXECUTION SUMMARY ", 14, BaseColor.DARK_GRAY, Font.BOLD)));
        summaryChapter.add(new Paragraph("\n"));
        summaryChapter.add(summaryTable);

        document.add(summaryChapter);
    }

    /**
     * @param document
     * @throws Exception
     * @function this method sets the Report Logo on all the pages of the document header
     */
    private void setReportLogo(Document document) throws Exception {
        PdfPTable logo = new PdfPTable(1);
        logo.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        cell.setPhrase(new Phrase(setFont("\n" + header + "\n", 18, BaseColor.BLACK, Font.BOLD)));
        logo.addCell(cell);

        cell.setPhrase(new Phrase(setFont("\n" + "Confidential, Not for Recirculation" + "\n", 12, BaseColor.BLACK, Font.NORMAL)));
        logo.addCell(cell);

        cell.setPhrase(new Phrase(setFont("\n" + docnumber + "\n" + revnumber, 12, BaseColor.BLACK, Font.ITALIC)));
        logo.addCell(cell);

        Paragraph preface = new Paragraph();
        preface.setSpacingBefore(PageSize.LEGAL.getHeight() / 8);
        preface.add(logo);
        document.add(preface);
        document.newPage();
    }

    /**
     * @throws IOException
     * @function this method read the template properties from the JSON property files
     */
    private void setTemplateProps() throws IOException {


        String content = new String(Files.readAllBytes(Paths.get("properties/template_properties.txt")));


        JSONParser parser = new JSONParser();

        try {
            JSONObject propJSON = (JSONObject) parser.parse(content);
            header = (String) propJSON.get("header");
            headerimage = (String) propJSON.get("headerimage");
            footer = (String) propJSON.get("footer");


            if (propJSON.get("filename") != null) {
                fileName = "./Reports/" + propJSON.get("filename") + "_" + formattedDate() + ".pdf";
            }

            docnumber = (String) propJSON.get("docnumber");
            revnumber = (String) propJSON.get("revnumber");
            watermark = (JSONObject) propJSON.get("watermark");


            if (propJSON.containsKey("testsummary")) {
                testcasesummary = (JSONObject) propJSON.get("testsummary");
            } else {
                testcasesummary = null;
            }

            JSONArray array = (JSONArray) propJSON.get("execution_approvals");
            textComment = new ArrayList<>();
            for (Object anArray : array) {
                textComment.add(String.valueOf(anArray));
            }

            if (propJSON.containsKey("model_details")) {
                modeldetails = (JSONObject) propJSON.get("model_details");
            } else {
                modeldetails = null;
            }

            if (propJSON.containsKey("execution_details")) {
                executiondetails = (JSONObject) propJSON.get("execution_details");
            } else {
                executiondetails = null;
            }

            if (propJSON.containsKey("execution_stats")) {
                executionstats = (JSONObject) propJSON.get("execution_stats");
            } else {
                executionstats = null;
            }
            signatureDetails = new ArrayList<>();
            JSONArray signatureDetls = (JSONArray) propJSON.get("signature_table");
            for (Object signatureDetl : signatureDetls) {
                signatureDetails.add((JSONObject) signatureDetl);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

    /**
     * @param document
     * @throws DocumentException
     * @function this method creates a table of content and enters at the end of the document
     */
    void setTOC(Document document) throws DocumentException {
        Set<String> indexkeys = HeaderFooterPageEvent.index.keySet();
        firstChapterPageNum = HeaderFooterPageEvent.index.get(HeaderFooterPageEvent.index.keySet().toArray()[0]);

        document.newPage();
        Paragraph toc = new Paragraph(setFont("TABLE OF CONTENTS\n\n", 14, BaseColor.DARK_GRAY, Font.BOLD));
        toc.setAlignment(Element.ALIGN_CENTER);
        document.add(toc);
        HeaderFooterPageEvent.order = writer.getPageNumber();
        Chunk dottedLine = new Chunk(new DottedLineSeparator());
        Paragraph p;

        for (String key : indexkeys) {
            p = new Paragraph(setFont(key, 11, BaseColor.BLACK, Font.NORMAL));
            p.add(dottedLine);
            p.add(setFont(String.valueOf(HeaderFooterPageEvent.index.get(key)), 11, BaseColor.BLACK, Font.NORMAL));
            document.add(p);
        }

        reOrderPages(document);
        HeaderFooterPageEvent.finishorder = writer.getPageNumber();
    }

    /**
     * @param document
     * @function this method is designed to re-order Table of Content Pages
     * from the end of the document to the  beginning of the PDF
     */
    private void reOrderPages(Document document) {
        try {
            //When you add your table of contents, get its page number for the reordering:

            int firstIndexPage = HeaderFooterPageEvent.order;
            totalIndexPages = (writer.getPageNumber() - HeaderFooterPageEvent.order + 1);

            // always add to a new page before reordering pages.
            document.newPage();
            Paragraph toc = new Paragraph(setFont("Intentionally left blank", 18, BaseColor.DARK_GRAY, Font.NORMAL));
            toc.setAlignment(Element.ALIGN_CENTER);
            document.add(toc);

            // get the total number of pages that needs to be reordered
            int total = writer.reorderPages(null);

            // instantiate an array with the total number of pages
            int[] order = new int[total];
            //array one contains pages from 1 to the first chapter page
            int[] rangeOne = IntStream.rangeClosed(1, firstChapterPageNum - 1).toArray();
            //array two contains pages from first TOC page to the last TOC page
            int[] rangeTwo = IntStream.rangeClosed(firstIndexPage, firstIndexPage + totalIndexPages - 1).toArray();
            //array one contains pages from first Chapter page until the last chapter page
            int[] rangeThree = IntStream.rangeClosed(firstChapterPageNum, firstIndexPage - 1).toArray();
            int[] rangeFour = IntStream.rangeClosed(firstIndexPage + totalIndexPages, total).toArray();

            //merge all the above int arrays to form the final ordered PDF page array
            System.arraycopy(rangeOne, 0, order, 0, rangeOne.length);
            System.arraycopy(rangeTwo, 0, order, rangeOne.length, rangeTwo.length);
            System.arraycopy(rangeThree, 0, order, rangeOne.length + rangeTwo.length, rangeThree.length);
            System.arraycopy(rangeFour, 0, order, rangeOne.length + rangeTwo.length + rangeThree.length, rangeFour.length);

            writer.getDirectContent();

            // apply the new order
            writer.reorderPages(order);

            //document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private String formattedDate() {

        Date date = new Date();
        String strDateFormat = "yyyyMMddhhmmssSSS";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);

    }
}

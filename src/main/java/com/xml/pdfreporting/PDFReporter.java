package com.xml.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileOutputStream;
import java.util.Set;

import static com.xml.pdfreporting.Utility.setCellFonts;
import static com.xml.pdfreporting.Utility.setFont;

public class PDFReporter {


    private String objective;
    private String header;
    private String acceptance;

    public static void setSignatureTable(Document document) throws DocumentException {

        String[] approvers = new String[]{"Nagesh Nama, Chief Quality Officer", "Approver1", "Approver2", "Approver3", "Approver4"};
        PdfPTable signature = new PdfPTable(new float[]{40F, 10F, 40F});
        int tableHeight = 410;

        PdfPCell dte = new PdfPCell();
        dte.setFixedHeight(tableHeight / approvers.length);
        dte.setBorder(Rectangle.TOP);
        dte.setPhrase(new Phrase("Date"));

        PdfPCell gap = new PdfPCell();
        gap.setFixedHeight(tableHeight / approvers.length);
        gap.setBorder(Rectangle.NO_BORDER);
        gap.setBorderWidth(10);

        PdfPCell names = new PdfPCell();
        names.setFixedHeight(tableHeight / approvers.length);
        names.setBorder(Rectangle.TOP);

        for (String approver : approvers) {
            names.setPhrase(new Phrase(approver));
            signature.addCell(names);
            signature.addCell(gap);
            signature.addCell(dte);
        }

        signature.setWidthPercentage(85);
        document.add(new Paragraph("\n\n\n\n"));
        document.add(signature);
    }

    public static void setReviewcommentTable(Document document) throws DocumentException {
        PdfPTable review = new PdfPTable(new float[]{10F, 40F});

        PdfPCell line = new PdfPCell();
        line.setBorder(Rectangle.BOX);
        line.setFixedHeight(30);

        PdfPCell header = new PdfPCell();
        header.setBorder(Rectangle.BOX);
        header.setFixedHeight(30);
        header.setPhrase(new Paragraph("\t\tREVIEW COMMENTS:"));
        header.setColspan(2);
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        review.addCell(header);

        line.setPhrase(new Paragraph("\t\tCommented By"));
        review.addCell(line);

        line.setPhrase(new Paragraph("Comments"));
        review.addCell(line);

        for (int count = 0; count < 5; count++) {
            line.setPhrase(new Paragraph("\n\n"));
            review.addCell(line);
            review.addCell(line);
        }
        review.setWidthPercentage(85);
        document.add(review);
        document.newPage();

    }

    public static void setPostExecutionApprovals(Document document) throws DocumentException {

        PdfPTable comments = new PdfPTable(1);
        comments.setWidthPercentage(85);

        PdfPCell header = new PdfPCell();
        header.setBorder(Rectangle.BOX);
        header.setFixedHeight(30);
        header.setPhrase(new Paragraph("\t\tPOST EXECUTION APPROVALS:"));
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        comments.addCell(header);

        PdfPCell comment = new PdfPCell();
        comment.setFixedHeight(25);
        comment.setBorder(Rectangle.NO_BORDER);
        String[] textComment = new String[]{"Ensure the test scripts have been executed.", "Ensure that the documented evidence has been correctly produced for all applicable test steps.", "Ensure the exceptions/incidents have been documented to address test failures (if any).", "Confirm the output (Pass / Fail status) of the test based on documented evidence, if applicable.", "Completed by xLM Quality and Customer Quality who have sufficient knowledge to evaluate and verify the test execution."};

        comment.setPhrase(new Phrase("An individual’s review of the executed test suite consists of the following:"));
        comments.addCell(comment);
        for (String text : textComment) {
            comment.setPhrase(new Phrase("\u2022\u00a0" + text.replace(' ', '\u00a0')));
            comments.addCell(comment);
        }


        document.add(comments);
        document.add(new Paragraph("\n\n"));
    }

    public static void setExecutionModelDetails(Document document) throws DocumentException, ParseException {

        Chapter executionModelDetails = new Chapter(new Paragraph("ENVIRONMENT DETAILS "), 1);

        JSONParser parser = new JSONParser();
        JSONObject modeldetails = (JSONObject) parser.parse("{\"Model Name\":\"Veloxis_TL_Operational_Verification\",\"Model Version\":\"1.1\",\"Approved By\":\"Bhaskar Kende\",\"Approval Date\":\"October 6, 2018\"}\n");
        JSONObject executiondetails = (JSONObject) parser.parse("{\"Execution Start Date/Time\":\"2018-09-25 21:43:22 EDT\",\"Application URL\":\"Selenium:https://app.tracelink.com\",\"TraceLink Life Sciences Cloud Version\":\"Einstein 4 2018.4.5\",\"Browser\":\"firefox\",\"Server Name\":\"Ayodhya\",\"Server IP\":\"127.0.0.1\",\"Plugins used\":\"Selenium,Service\",\"Login User ID\":\"veloxis@continuousvalidation.com\",\"Login Status\":\"Successful\"}");

        executionModelDetails.add(new Paragraph("\n"));
        Section modelDetail = executionModelDetails.addSection(1, "AUTOMATION MODEL VERSION RELEASE DETAILS");
        modelDetail.add(new Paragraph("\n"));
        modelDetail.add(setModelDetails(modeldetails.keySet(), modeldetails));


        executionModelDetails.add(new Paragraph("\n"));
        Section executionDetail = executionModelDetails.addSection(2, "TEST EXECUTION ENVIRONMENT DETAILS ");
        executionDetail.add(new Paragraph("\n"));
        executionDetail.add(setModelDetails(executiondetails.keySet(), executiondetails));


        document.add(executionModelDetails);
        //document.add(new Paragraph("\n"));
    }

    public static PdfPTable setModelDetails(Set<String> keys, JSONObject details) throws DocumentException {

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

        for (String key : keys) {
            keyCell.setPhrase(new Phrase(key));
            modelDetails.addCell(keyCell);
            valueCell.setPhrase(new Phrase(String.valueOf(details.get(key))));
            modelDetails.addCell(valueCell);
            modelDetails.addCell(blankCell);
        }
        return modelDetails;
    }

    public static void setSummaryReport(Document document) throws Exception {

        document.add(new Paragraph("\n\n\n"));
        PdfPTable table = new PdfPTable(2);

        PdfPCell summaryCell = setCellFonts(setFont("Summary Report", 22, BaseColor.BLUE, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_CENTER);
        summaryCell.setColspan(2);
        table.addCell(summaryCell);

        Image img = Image.getInstance("./screenshots/xlm-logo.jpg");
        img.scalePercent(18);

        PdfPCell imageCell = new PdfPCell(img);
        imageCell.setRowspan(4);
        imageCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(imageCell);

        PdfPTable summarytable = new PdfPTable(4);
        summarytable.addCell(setCellFonts(setFont("Total", 14, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        summarytable.addCell(setCellFonts(setFont("Pass", 14, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        summarytable.addCell(setCellFonts(setFont("Fail", 14, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        summarytable.addCell(setCellFonts(setFont("Skip", 14, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_CENTER));

        summarytable.addCell(setCellFonts(setFont("total", 14, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        summarytable.addCell(setCellFonts(setFont("pass", 14, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        summarytable.addCell(setCellFonts(setFont("fail", 14, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_CENTER));
        summarytable.addCell(setCellFonts(setFont("skip", 14, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_CENTER));

        table.addCell(new Paragraph("\n\n\n"));
        table.addCell(summarytable);
        table.addCell(new Paragraph("\n\n\n"));


        document.add(table);
        document.add(new Paragraph("\n\n\n"));
    }

    public Document PDFReporter(String header, String footer) throws Exception {
        Document document = new Document(PageSize.LEGAL.rotate());
        document.setMargins(30, 30, 65, 65);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("./Reports/DemoAutomationReport.pdf"));


        this.objective = "objective";
        this.acceptance = "acceptance";
        this.header = header;

        /*SET HEADER AND FOOTER FOR THE PDF*/
        HeaderFooterPageEvent event = new HeaderFooterPageEvent(writer);
        event.setHeader(header);
        event.setFooter(footer);

        writer.setPageEvent(event);

        /*OPEN THE PDF DOCUMENT FOR WRITING*/
        document.open();

        /*SET THE LOGO FOR THE DOCUMENT, THE IMAGE IS PICKED FROM SCREENSHOTS DIRECTORY*/
        setReportLogo(document);

        /*SET TEST OBJECTIVE AND ACCEPTANCE CRITERIA FOR THE DOCUMENT*/
        //setTestObjective(document);

        /*SET TEST SUMMARY REPORT WITH THE EXECUTION NUMERIC TABLES */
        setPostExecutionApprovals(document);
        setReviewcommentTable(document);
        setSignatureTable(document);
        setExecutionModelDetails(document);
        document.newPage();

        return document;
    }

    public void setTestObjective(Document document) throws DocumentException {

        PdfPTable table = new PdfPTable(new float[]{15, 85});
        table.setWidthPercentage(100);

        table.addCell(setCellFonts(setFont("Test Objective", 14, BaseColor.BLUE, Font.NORMAL), Element.ALIGN_LEFT, Element.ALIGN_MIDDLE));
        table.addCell(setCellFonts(setFont(objective, 11, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_LEFT, Element.ALIGN_MIDDLE));

        table.addCell(setCellFonts(setFont("Test Acceptance", 14, BaseColor.BLUE, Font.NORMAL), Element.ALIGN_LEFT, Element.ALIGN_MIDDLE));
        table.addCell(setCellFonts(setFont(acceptance, 11, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_LEFT, Element.ALIGN_MIDDLE));

        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        document.add(table);
    }

    private void setReportLogo(Document document) throws Exception {
        PdfPTable logo = new PdfPTable(1);
        logo.setWidthPercentage(100);


        logo.addCell(setCellFonts(setFont("\n" + header + "\n", 18, BaseColor.BLACK, Font.BOLD), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE)).setBorder(0);

        logo.addCell(setCellFonts(setFont("\n" + "Confidential, Not for Recirculation" + "\n", 12, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE)).setBorder(0);

        logo.addCell(setCellFonts(setFont("\n" + "DOCUMENT #: 119200-TPE-001" + "\n" + "REV #: 00", 12, BaseColor.BLACK, Font.ITALIC), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE)).setBorder(0);


        Paragraph preface = new Paragraph();
        preface.setSpacingBefore(PageSize.LEGAL.getHeight() / 8);
        preface.add(logo);
        document.add(preface);
        document.newPage();
    }

}

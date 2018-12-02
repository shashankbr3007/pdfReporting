package com.xml.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.xml.pdfreporting.Utility.*;

public class PDFReporter {

    private List<String> textComment;
    private String header;
    private String docnumber;
    private String revnumber;
    private String headerimage;
    private String footer;
    private JSONObject modeldetails;
    private JSONObject executiondetails;
    private JSONObject executionstats;
    private List<JSONObject> signatureDetails;
    private JSONObject testcasesummary;
    private String fileName;

    public static void setReviewcommentTable(Document document) throws DocumentException {
        PdfPTable review = new PdfPTable(new float[]{10F, 40F});

        PdfPCell line = new PdfPCell();
        line.setBorder(Rectangle.BOX);


        PdfPCell header = new PdfPCell();
        header.setBorder(Rectangle.BOX);
        header.setFixedHeight(30);
        header.setPhrase(new Paragraph(setFont("\t\tREVIEW COMMENTS:", 12, BaseColor.BLACK, Font.NORMAL)));
        header.setColspan(2);
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
        review.addCell(header);

        line.setPhrase(new Paragraph(setFont("\t\tCommented By", 11, BaseColor.BLACK, Font.NORMAL)));
        review.addCell(line);

        line.setPhrase(new Paragraph(setFont("Comments", 11, BaseColor.BLACK, Font.NORMAL)));
        ;
        review.addCell(line);
        line.setFixedHeight(60);

        for (int count = 0; count < 3; count++) {
            line.setPhrase(new Paragraph("\n\n"));
            review.addCell(line);
            review.addCell(line);
        }

        review.setWidthPercentage(85);
        document.add(review);
        document.newPage();

    }

    public static PdfPTable setModelDetails(JSONObject details) {

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

    public void setSignatureTable(Document document) throws DocumentException, IOException {

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

    public void setExecutionModelDetails(Document document) throws DocumentException, ParseException {

        Chapter executionModelDetails = new Chapter(new Paragraph("ENVIRONMENT DETAILS "), 1);

        executionModelDetails.add(new Paragraph("\n"));
        Section modelDetail = executionModelDetails.addSection(new Paragraph(setFont("AUTOMATION MODEL VERSION RELEASE DETAILS", 12, BaseColor.BLACK, Font.NORMAL)));
        modelDetail.add(new Paragraph("\n"));
        modelDetail.add(setModelDetails(modeldetails));


        executionModelDetails.add(new Paragraph("\n"));
        Section executionDetail = executionModelDetails.addSection(new Paragraph(setFont("TEST EXECUTION ENVIRONMENT DETAILS ", 12, BaseColor.BLACK, Font.NORMAL)));
        executionDetail.add(new Paragraph("\n"));
        executionDetail.add(setModelDetails(executiondetails));

        executionModelDetails.add(new Paragraph("\n"));
        Section executionStats = executionModelDetails.addSection(new Paragraph(setFont("EXECUTION STATISTICS", 12, BaseColor.BLACK, Font.NORMAL)));
        executionStats.add(new Paragraph("\n"));
        executionStats.add(setModelDetails(executionstats));

        document.add(executionModelDetails);
    }

    public void setPostExecutionApprovals(Document document) throws DocumentException {

        PdfPTable comments = new PdfPTable(1);
        comments.setWidthPercentage(85);

        PdfPCell header = new PdfPCell();
        header.setBorder(Rectangle.BOX);
        header.setFixedHeight(30);
        header.setPhrase(new Paragraph(setFont("\t\tPOST EXECUTION APPROVALS:", 11, BaseColor.BLACK, Font.NORMAL)));
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

    public Document PDFReporter() throws Exception {

        setTemplateProps();
        Document document = new Document(PageSize.LEGAL.rotate());
        document.setMargins(30, 30, 65, 65);
        PdfWriter writer = PdfWriter.getInstance(document,
                new FileOutputStream("./Reports/" + fileName /*+ "_" + formattedDate() */ + ".pdf"));

        /*SET HEADER AND FOOTER FOR THE PDF*/
        HeaderFooterPageEvent event = new HeaderFooterPageEvent(writer);
        event.setHeader(header);
        event.setHeaderimage(headerimage);
        event.setFooter(footer);
        event.setDocnumber(docnumber);
        event.setRevnumber(revnumber);
        writer.setPageEvent(event);

        /*OPEN THE PDF DOCUMENT FOR WRITING*/
        document.open();

        /*SET THE LOGO FOR THE DOCUMENT, THE IMAGE IS PICKED FROM SCREENSHOTS DIRECTORY*/
        setReportLogo(document);

        /*SET TEST SUMMARY REPORT WITH THE EXECUTION NUMERIC TABLES */
        setPostExecutionApprovals(document);
        setReviewcommentTable(document);
        setSignatureTable(document);
        setExecutionModelDetails(document);
        setTestSummaryDetails(document);
        //document.newPage();

        return document;
    }

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

        for (int count = 0; count < testsummary.size(); count++) {
            JSONObject test = (JSONObject) testsummary.get(count);

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
        summaryChapter.setTitle(new Paragraph("TEST EXECUTION SUMMARY "));
        summaryChapter.add(new Paragraph("\n"));
        summaryChapter.add(summaryTable);

        document.add(summaryChapter);
    }

    private void setReportLogo(Document document) throws Exception {
        PdfPTable logo = new PdfPTable(1);
        logo.setWidthPercentage(100);


        logo.addCell(setCellFonts(setFont("\n" + header + "\n", 18, BaseColor.BLACK, Font.BOLD), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE)).setBorder(0);

        logo.addCell(setCellFonts(setFont("\n" + "Confidential, Not for Recirculation" + "\n", 12, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE)).setBorder(0);

        logo.addCell(setCellFonts(setFont("\n" + docnumber + "\n" + revnumber, 12, BaseColor.BLACK, Font.ITALIC), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE)).setBorder(0);


        Paragraph preface = new Paragraph();
        preface.setSpacingBefore(PageSize.LEGAL.getHeight() / 8);
        preface.add(logo);
        document.add(preface);
        document.newPage();
    }

    private void setTemplateProps() throws IOException {


        String content = new String(Files.readAllBytes(Paths.get("properties/template_properties.txt")));


        JSONParser parser = new JSONParser();

        try {
            JSONObject propJSON = (JSONObject) parser.parse(content);
            header = (String) propJSON.get("header");
            headerimage = (String) propJSON.get("headerimage");
            footer = (String) propJSON.get("footer");
            fileName = (String) propJSON.get("filename");
            docnumber = (String) propJSON.get("docnumber");
            revnumber = (String) propJSON.get("revnumber");
            testcasesummary = (JSONObject) propJSON.get("testsummary");

            JSONArray array = (JSONArray) propJSON.get("execution_approvals");
            textComment = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                textComment.add(String.valueOf(array.get(i)));
            }

            modeldetails = (JSONObject) propJSON.get("model_details");
            executiondetails = (JSONObject) propJSON.get("execution_details");
            executionstats = (JSONObject) propJSON.get("execution_stats");

            signatureDetails = new ArrayList<>();
            JSONArray signatureDetls = (JSONArray) propJSON.get("signature_table");
            for (int j = 0; j < signatureDetls.size(); j++) {
                signatureDetails.add((JSONObject) signatureDetls.get(j));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }

}

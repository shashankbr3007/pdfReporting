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
    private String headerimage;
    private String footer;
    private JSONObject modeldetails;
    private JSONObject executiondetails;
    private List<JSONObject> signatureDetails;
    private String fileName;

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
            keyCell.setPhrase(new Phrase(key));
            modelDetails.addCell(keyCell);
            valueCell.setPhrase(new Phrase(String.valueOf(details.get(key))));
            modelDetails.addCell(valueCell);
            modelDetails.addCell(blankCell);
        }
        return modelDetails;
    }

    public void setSignatureTable(Document document) throws DocumentException, IOException {

        String[] approvers = new String[]{"Nagesh Nama, Chief Quality Officer", "Approver1", "Approver2"/*, "Approver3", "Approver4"*/};
        PdfPTable signature = new PdfPTable(new float[]{25F, 15F, 10F, 40F});
        int tableHeight = 310;

        PdfPCell dte = new PdfPCell();
        dte.setFixedHeight((tableHeight / 4) / signatureDetails.size());
        dte.setBorder(Rectangle.TOP);
        dte.setPhrase(new Phrase("Date"));

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
            PdfPCell nameImage = new PdfPCell();
            nameImage.setFixedHeight((tableHeight / 2) / signatureDetails.size());
            nameImage.setBorder(Rectangle.TOP);
            nameImage.addElement(img);

            names.setPhrase(new Phrase(String.valueOf(approver.get("approver"))));
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
        Section modelDetail = executionModelDetails.addSection(1, "AUTOMATION MODEL VERSION RELEASE DETAILS");
        modelDetail.add(new Paragraph("\n"));
        modelDetail.add(setModelDetails(modeldetails));


        executionModelDetails.add(new Paragraph("\n"));
        Section executionDetail = executionModelDetails.addSection(2, "TEST EXECUTION ENVIRONMENT DETAILS ");
        executionDetail.add(new Paragraph("\n"));
        executionDetail.add(setModelDetails(executiondetails));


        document.add(executionModelDetails);
    }

    public void setPostExecutionApprovals(Document document) throws DocumentException {

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
        //String[] textComment = new String[]{"Ensure the test scripts have been executed.", "Ensure that the documented evidence has been correctly produced for all applicable test steps.", "Ensure the exceptions/incidents have been documented to address test failures (if any).", "Confirm the output (Pass / Fail status) of the test based on documented evidence, if applicable.", "Completed by xLM Quality and Customer Quality who have sufficient knowledge to evaluate and verify the test execution."};

        comment.setPhrase(new Phrase("An individual’s review of the executed test suite consists of the following:"));
        comments.addCell(comment);
        for (String text : textComment) {
            comment.setPhrase(new Phrase("\u2022\u00a0" + text.replace(' ', '\u00a0')));
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
                new FileOutputStream("./Reports/" + fileName + "_" + formattedDate() + ".pdf"));

        /*SET HEADER AND FOOTER FOR THE PDF*/
        HeaderFooterPageEvent event = new HeaderFooterPageEvent(writer);
        event.setHeader(header);
        event.setHeaderimage(headerimage);
        event.setFooter(footer);
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
        //document.newPage();

        return document;
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

    private void setTemplateProps() throws IOException {


        String content = new String(Files.readAllBytes(Paths.get("properties/template_properties.txt")));


        JSONParser parser = new JSONParser();

        try {
            JSONObject propJSON = (JSONObject) parser.parse(content);
            header = (String) propJSON.get("header");
            headerimage = (String) propJSON.get("headerimage");
            footer = (String) propJSON.get("footer");
            fileName = (String) propJSON.get("filename");

            JSONArray array = (JSONArray) propJSON.get("execution_approvals");
            textComment = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                textComment.add(String.valueOf(array.get(i)));
            }

            modeldetails = (JSONObject) propJSON.get("model_details");
            executiondetails = (JSONObject) propJSON.get("execution_details");

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

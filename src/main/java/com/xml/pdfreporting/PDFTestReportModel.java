package com.xml.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.IOException;
import java.util.List;

import static com.xml.pdfreporting.Utility.setFont;

public class PDFTestReportModel {

    private PdfPTable table;
    private String testName;
    private String stepNum;
    private String description;
    private List<String> expected;
    private String objective = " ";
    private String acceptance = " ";
    private List<String> actuals;

    public PDFTestReportModel(String testName, String Objec, String Acceptnce) {
        this.testName = testName;
        this.objective = Objec;
        this.acceptance = Acceptnce;
        defineTestExecutionTable();
    }

    public static PdfPCell setCellFonts(Phrase phrase, int horizontalAlignment, int verticalAlignment) {
        PdfPCell AlignCell = new PdfPCell(phrase);
        AlignCell.setHorizontalAlignment(horizontalAlignment);
        AlignCell.setVerticalAlignment(verticalAlignment);

        return AlignCell;
    }

    public static Phrase setFont1(String text, int size, BaseColor color, int font) {
        FontSelector selector1 = new FontSelector();
        Font f1 = FontFactory.getFont(FontFactory.TIMES_ROMAN, size, font);
        f1.setColor(color);
        selector1.addFont(f1);
        Phrase ph = selector1.process(text);
        return ph;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public List<String> getexpected() {
        return expected;
    }

    public void setexpected(List<String> expected) {
        this.expected = expected;
    }

    public List<String> getactuals() {
        return actuals;
    }

    public void setactuals(List<String> actuals) {
        this.actuals = actuals;
    }

    public void defineTestExecutionTable() {
        float[] columnWidth = {15, 25, 35, 35};
        table = new PdfPTable(columnWidth);
        //table.setKeepTogether(true);
        table.setWidthPercentage(100);

        PdfPCell blankCell = new PdfPCell();
        blankCell.setPhrase(new Paragraph("\n"));
        blankCell.setBorder(Rectangle.NO_BORDER);
        blankCell.setColspan(columnWidth.length);


        PdfPCell value = setCellFonts(setFont(objective, 11, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_LEFT, Element.ALIGN_MIDDLE);
        value.setColspan(columnWidth.length - 1);

        table.addCell(setCellFonts(setFont("Test Objective", 14, BaseColor.BLUE, Font.NORMAL), Element.ALIGN_LEFT, Element.ALIGN_MIDDLE));
        table.addCell(value);

        PdfPCell value1 = setCellFonts(setFont(acceptance, 11, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_LEFT, Element.ALIGN_MIDDLE);
        value1.setColspan(columnWidth.length - 1);


        table.addCell(setCellFonts(setFont("Test Acceptance", 14, BaseColor.BLUE, Font.NORMAL), Element.ALIGN_LEFT, Element.ALIGN_MIDDLE));
        table.addCell(value1);
        table.addCell(blankCell);

        table.addCell(setCellFonts(setFont("STEP #", 12, BaseColor.BLACK, Font.BOLD), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
        table.addCell(setCellFonts(setFont("STEP DESCRIPTION", 12, BaseColor.BLACK, Font.BOLD), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
        table.addCell(setCellFonts(setFont("EXPECTED RESULT", 12, BaseColor.BLACK, Font.BOLD), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
        table.addCell(setCellFonts(setFont("ACTUAL RESULT", 12, BaseColor.BLACK, Font.BOLD), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
    }

    public void setTestResultTable() throws IOException, BadElementException {

        table.addCell(setCellFonts(setFont(getStepNum(), 11, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_TOP));
        table.addCell(setCellFonts(setFont(getDescription(), 11, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_TOP));

        PdfPCell expectedCell = new PdfPCell();
        expectedCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        expectedCell.setVerticalAlignment(Element.ALIGN_TOP);
        for (int i = 0; i < getexpected().size(); i++) {
            if (getexpected().get(i).contains(".jpg") ||
                    getexpected().get(i).contains(".png")) {
                Image img = Image.getInstance(getexpected().get(i));
                img.scalePercent(30);
                expectedCell.addElement(img);
            } else {
                expectedCell.addElement(new Phrase(getexpected().get(i) + "\n"));
            }
        }
        table.addCell(expectedCell);

        PdfPCell actualCell = new PdfPCell();
        actualCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        actualCell.setVerticalAlignment(Element.ALIGN_TOP);
        for (int i = 0; i < getactuals().size(); i++) {
            if (getactuals().get(i).contains(".jpg") ||
                    getactuals().get(i).contains(".png")) {
                Image img = Image.getInstance(getactuals().get(i));
                img.scalePercent(30);
                actualCell.addElement(img);
            } else {
                actualCell.addElement(new Phrase(getactuals().get(i) + "\n\n"));
            }
        }
        table.addCell(actualCell);
    }

    public Chapter setTestExecutionTable(int testNumber) {

        Chapter testChapter = new Chapter(new Paragraph("TEST CASE : " + getTestName()), testNumber);
        testChapter.add(new Paragraph("\n"));
        testChapter.add(this.table);
        return testChapter;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public void setAcceptance(String acceptance) {
        this.acceptance = acceptance;
    }

    public String getStepNum() {
        return stepNum;
    }

    public void setStepNum(String stepNum) {
        this.stepNum = stepNum;
    }
}

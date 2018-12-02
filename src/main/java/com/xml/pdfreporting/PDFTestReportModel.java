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
    private PdfPTable objtable;
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
        float[] columnWidth = {2, 15, 20, 75};
        objtable = new PdfPTable(columnWidth);
        objtable.setWidthPercentage(100);

        PdfPCell header = new PdfPCell();
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        header.setBackgroundColor(BaseColor.LIGHT_GRAY);

        PdfPCell value = new PdfPCell();
        value.setColspan(columnWidth.length - 2);
        value.setHorizontalAlignment(Element.ALIGN_LEFT);
        value.setVerticalAlignment(Element.ALIGN_MIDDLE);

        PdfPCell blankCell = new PdfPCell();
        blankCell.setPhrase(new Paragraph("\n"));
        blankCell.setBorder(Rectangle.NO_BORDER);
        blankCell.setColspan(columnWidth.length);
        objtable.addCell(blankCell);

        header.setColspan(columnWidth.length - 2);
        header.setPhrase(new Phrase(setFont("TEST OBJECTIVE", 10, BaseColor.DARK_GRAY, Font.BOLD)));
        objtable.addCell(header);


        value.setPhrase(new Phrase(setFont(objective, 11, BaseColor.BLACK, Font.NORMAL)));
        objtable.addCell(value);

        header.setColspan(columnWidth.length - 2);
        header.setPhrase(new Phrase(setFont("TEST ACCEPTANCE", 10, BaseColor.DARK_GRAY, Font.BOLD)));
        objtable.addCell(header);

        value.setPhrase(new Phrase(setFont(acceptance, 11, BaseColor.BLACK, Font.NORMAL)));
        objtable.addCell(value);

        objtable.addCell(blankCell);

        table = new PdfPTable(columnWidth);
        table.setWidthPercentage(100);
        table.setSplitLate(true);
        table.setHeaderRows(1);

        header.setColspan(1);
        header.setPhrase(new Phrase(setFont("#", 10, BaseColor.DARK_GRAY, Font.BOLD)));
        table.addCell(header);

        header.setPhrase(new Phrase(setFont("STEP DESCRIPTION", 10, BaseColor.DARK_GRAY, Font.BOLD)));
        table.addCell(header);

        header.setPhrase(new Phrase(setFont("EXEPCTED RESULT", 10, BaseColor.DARK_GRAY, Font.BOLD)));
        table.addCell(header);

        header.setPhrase(new Phrase(setFont("ACTUAL RESULT", 10, BaseColor.DARK_GRAY, Font.BOLD)));
        table.addCell(header);


    }

    public void setTestResultTable() throws IOException, BadElementException {

        PdfPCell numberCell = new PdfPCell();
        numberCell.setBorder(Rectangle.BOX);
        numberCell.addElement(new Phrase(setFont(getStepNum(), 11, BaseColor.BLACK, Font.NORMAL)));
        numberCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        numberCell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(numberCell);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);


        PdfPCell descCell = new PdfPCell();
        descCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        descCell.setVerticalAlignment(Element.ALIGN_TOP);
        descCell.setBorder(Rectangle.BOX);
        descCell.addElement(new Phrase(setFont(getDescription(), 11, BaseColor.BLACK, Font.NORMAL)));
        table.addCell(descCell);


        PdfPCell expectedCell = new PdfPCell();
        expectedCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        expectedCell.setVerticalAlignment(Element.ALIGN_TOP);
        for (int i = 0; i < getexpected().size(); i++) {
            if (getexpected().get(i).contains(".jpg") ||
                    getexpected().get(i).contains(".png")) {
                Image img = Image.getInstance(getexpected().get(i));
                img.scalePercent(15);
                expectedCell.addElement(img);
            } else {
                expectedCell.addElement(new Phrase(setFont(getexpected().get(i) + ", ", 11, BaseColor.BLACK, Font.NORMAL)));
            }
        }
        table.addCell(expectedCell);

        PdfPCell actualCell = new PdfPCell();
        actualCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        actualCell.setVerticalAlignment(Element.ALIGN_TOP);
        for (int i = 0; i < getactuals().size(); i++) {
            if (getactuals().get(i).contains(".jpg") ||
                    getactuals().get(i).contains(".png")) {
                Image img = Image.getInstance(getactuals().get(i));
                img.setAlignment(Element.ALIGN_CENTER);
                img.scalePercent(55000 / img.getWidth());
                actualCell.addElement(img);
            } else {
                actualCell.addElement(new Phrase(setFont(getactuals().get(i) + ", ", 11, BaseColor.BLACK, Font.NORMAL)));
            }
        }
        table.addCell(actualCell);
    }

    public Chapter setTestExecutionTable(int testNumber) {

        Chapter testChapter = new Chapter(new Paragraph(setFont("TEST CASE : " + getTestName(), 14, BaseColor.BLACK, Font.NORMAL)), testNumber);
        testChapter.setBookmarkOpen(true);
        testChapter.add(new Phrase(""));

        Section testObj = testChapter.addSection(new Paragraph(setFont("Test Execution Benchmarks ", 12, BaseColor.BLACK, Font.NORMAL)));
        testObj.add(this.objtable);

        Section testExec = testChapter.addSection(new Paragraph(setFont("Test Execution Details ", 12, BaseColor.BLACK, Font.NORMAL)));
        testExec.add(new Phrase(""));
        testExec.add(this.table);

        return testChapter;
    }

    public String getStepNum() {
        return stepNum;
    }

    public void setStepNum(String stepNum) {
        this.stepNum = stepNum;
    }

}

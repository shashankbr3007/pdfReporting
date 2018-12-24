package com.xml.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.xml.pdfreporting.Utility.imageCell;
import static com.xml.pdfreporting.Utility.setFont;

class PDFTestReportModel {

    private PdfPTable table;
    private PdfPTable objtable;
    private String testName;
    private String stepNum;
    private String description;
    private List<String> expected;
    private String objective;
    private String acceptance;
    private List<String> actuals;

    PDFTestReportModel(String testName, String Objec, String Acceptnce) {
        this.testName = testName;
        objective = " ";
        this.objective = Objec;
        this.acceptance = Acceptnce;
        defineTestExecutionTable();
    }

    private String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    private String getTestName() {
        return testName;
    }

    private List<String> getexpected() {
        return expected;
    }

    void setexpected(List<String> expected) {
        this.expected = expected;
    }

    void setactuals(List<String> actuals) {
        this.actuals = actuals;
    }

    /**
     *this method defines the test execution table with all the template values
     * and placeholders for acutal execution values
     */
    private void defineTestExecutionTable() {
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

        header.setPhrase(new Phrase(setFont("EXPECTED RESULT", 10, BaseColor.DARK_GRAY, Font.BOLD)));
        table.addCell(header);

        header.setPhrase(new Phrase(setFont("ACTUAL RESULT", 10, BaseColor.DARK_GRAY, Font.BOLD)));
        table.addCell(header);


    }

    /**
     * @throws IOException
     * @throws BadElementException
     * @function this is the primary table with the sequence test execution placeholders and values from test case logs
     */
    void setTestResultTable() throws IOException, BadElementException {

        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell numberCell = new PdfPCell();
        numberCell.setBorder(Rectangle.BOX);
        numberCell.addElement(new Phrase(setFont(getStepNum(), 11, BaseColor.BLACK, Font.NORMAL)));
        numberCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        numberCell.setVerticalAlignment(Element.ALIGN_TOP);


        PdfPCell descCell = new PdfPCell();
        descCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        descCell.setVerticalAlignment(Element.ALIGN_TOP);
        descCell.setBorder(Rectangle.BOX);
        descCell.addElement(new Phrase(setFont(getDescription(), 11, BaseColor.BLACK, Font.NORMAL)));


        PdfPCell expectedCell = new PdfPCell();
        expectedCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        expectedCell.setVerticalAlignment(Element.ALIGN_TOP);

        for (int i = 0; i < getexpected().size(); i++) {
            expectedCell.addElement(new Phrase(setFont(getexpected().get(i) + ", ", 11, BaseColor.BLACK, Font.NORMAL)));
        }

        HashMap<String, PdfPCell> actualCells = new HashMap<>();
        int imageCount = 0;
        actualCells.put("imageCount_" + imageCount, new PdfPCell());
        for (String actual : actuals) {

            if (actual.contains(".jpg") ||
                    actual.contains(".png")) {
                if (actualCells.containsKey("imageCount_" + imageCount)) {
                    (actualCells.get("imageCount_" + imageCount)).addElement(imageCell(actual));
                } else {
                    PdfPCell cell = new PdfPCell();
                    cell.addElement(imageCell(actual));
                    actualCells.put("imageCount_" + imageCount, cell);
                }
                imageCount++;
            } else {
                if (actualCells.containsKey("imageCount_" + imageCount)) {
                    (actualCells.get("imageCount_" + imageCount)).addElement(new Phrase(setFont(actual + ", ", 11, BaseColor.BLACK, Font.NORMAL)));
                } else {
                    PdfPCell cell = new PdfPCell();
                    cell.addElement(new Phrase(setFont(actual + ", ", 11, BaseColor.BLACK, Font.NORMAL)));
                    actualCells.put("imageCount_" + imageCount, cell);
                }
            }

        }

        Object[] actualKeys = actualCells.keySet().toArray();
        for (int count = 0; count < actualKeys.length; count++) {
            if (count < 1) {
                table.addCell(numberCell);
                table.addCell(descCell);
                table.addCell(expectedCell);
                table.addCell(actualCells.get(actualKeys[count]));
            } else if (count < 2) {
                table.addCell(numberCell);
                descCell.addElement(setFont("Continued....", 11, BaseColor.BLACK, Font.NORMAL));
                table.addCell(descCell);
                expectedCell.addElement(setFont("Continued....", 11, BaseColor.BLACK, Font.NORMAL));
                table.addCell(expectedCell);
                table.addCell(actualCells.get(actualKeys[count]));
            } else {
                table.addCell(numberCell);
                table.addCell(descCell);
                table.addCell(expectedCell);
                table.addCell(actualCells.get(actualKeys[count]));

            }
        }
    }

    /**
     *
     * @param testNumber
     * @return
     * @function this is method which sets the chapter and section for all the execution tables
     */
    Chapter setTestExecutionTable(int testNumber) {

        Chapter testChapter = new Chapter(new Paragraph(setFont("TEST CASE : " + getTestName(), 14, BaseColor.DARK_GRAY, Font.BOLD)), testNumber);
        testChapter.setBookmarkOpen(true);
        testChapter.add(new Phrase(""));

        Section testObj = testChapter.addSection(new Paragraph(setFont("Test Execution Benchmarks ", 12, BaseColor.DARK_GRAY, Font.BOLD)));
        testObj.add(this.objtable);

        Section testExec = testChapter.addSection(new Paragraph(setFont("Test Execution Details ", 12, BaseColor.DARK_GRAY, Font.BOLD)));
        testExec.add(new Phrase(""));
        testExec.add(this.table);

        return testChapter;
    }

    private String getStepNum() {
        return stepNum;
    }

    void setStepNum(String stepNum) {
        this.stepNum = stepNum;
    }

}

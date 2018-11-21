package com.xml.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.FontSelector;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.IOException;
import java.util.List;

public class PDFTestReportModel {

    private String testName;
    private String description;
    private String expected;
    private List<String> actuals;

    public PDFTestReportModel(String testName) {
        this.testName = testName;
    }

    public static PdfPCell setCellFonts(Phrase phrase, int horizontalAlignment, int verticalAlignment) {
        PdfPCell AlignCell = new PdfPCell(phrase);
        AlignCell.setHorizontalAlignment(horizontalAlignment);
        AlignCell.setVerticalAlignment(verticalAlignment);

        return AlignCell;
    }

    public static Phrase setFont(String text, int size, BaseColor color, int font) {
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

    public String getexpected() {
        return expected;
    }

    public void setexpected(String expected) {
        this.expected = expected;
    }

    public List<String> getactuals() {
        return actuals;
    }

    public void setactuals(List actuals) {
        this.actuals = actuals;
    }

    public PdfPTable setTestResultTable() throws IOException, BadElementException {

        float[] columnWidth = {10, 25, 25, 40};
        PdfPTable table = new PdfPTable(columnWidth);
        table.setKeepTogether(true);
        table.setWidthPercentage(100);

        table.addCell(setCellFonts(setFont("TEST", 12, BaseColor.BLACK, Font.BOLD), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
        table.addCell(setCellFonts(setFont("STEP DESCRIPTION", 12, BaseColor.BLACK, Font.BOLD), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
        table.addCell(setCellFonts(setFont("EXPECTED RESULT", 12, BaseColor.BLACK, Font.BOLD), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
        table.addCell(setCellFonts(setFont("ACTUAL RESULT", 12, BaseColor.BLACK, Font.BOLD), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));

        table.addCell(setCellFonts(setFont(getTestName(), 11, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
        table.addCell(setCellFonts(setFont(getDescription(), 11, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));
        table.addCell(setCellFonts(setFont(getexpected(), 11, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));


        PdfPCell cell = new PdfPCell();
        for (int i = 0; i < getactuals().size(); i++) {
            if (getactuals().get(i).contains(".jpg") ||
                    getactuals().get(i).contains(".png")) {
                Image img = Image.getInstance(getactuals().get(i));
                img.scalePercent(30);
                cell.addElement(img);
            } else {
                cell.addElement(new Phrase(getactuals().get(i) + "\n\n"));
            }
        }
        cell.setPaddingLeft(25);
        table.addCell(cell);

        return table;
    }
}

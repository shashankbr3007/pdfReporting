package com.xml.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.xml.pdfreporting.Utility.setCellFonts;
import static com.xml.pdfreporting.Utility.setFont;


public class HeaderFooterPageEvent extends PdfPageEventHelper {

    Font FONT = new Font(Font.FontFamily.TIMES_ROMAN, 32, Font.BOLDITALIC, new GrayColor(0.85f));
    Map<String, Integer> index = new LinkedHashMap<String, Integer>();
    private PdfTemplate t;
    private Image total;
    private String header;
    private String headerimage;
    private String footer;
    private String docnumber;
    private String revnumber;
    private int page;

    public HeaderFooterPageEvent(PdfWriter writer) {
        Rectangle rect = new Rectangle(15, 15, 993, 597);
        writer.setBoxSize("art", rect);
    }

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public Phrase companyHeader() {
        FontSelector selector1 = new FontSelector();
        Font f1 = FontFactory.getFont(FontFactory.TIMES_BOLDITALIC, 12);
        f1.setColor(BaseColor.BLUE);
        selector1.addFont(f1);
        Phrase ph = selector1.process("DEMO AUTOMATION");
        return ph;
    }

    public void onOpenDocument(PdfWriter writer, Document document) {
        t = writer.getDirectContent().createTemplate(30, 16);
        try {
            total = Image.getInstance(t);
            total.setRole(PdfName.ARTIFACT);
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {

        page++;
        setborder(writer, document);
        PdfPTable header = new PdfPTable(3);
        try {
            header.setWidths(new int[]{25, 50, 25});
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        header.setWidthPercentage(100);
        header.getDefaultCell().setFixedHeight(10);
        header.setTotalWidth(950);
        header.setLockedWidth(true);

        try {
            Image img = Image.getInstance(headerimage);
            img.scalePercent(30);
            PdfPCell nameImage = new PdfPCell();
            nameImage.setBorder(Rectangle.BOTTOM);
            nameImage.addElement(img);

            header.addCell(nameImage);
            header.addCell(setCellFonts(setFont(getHeader(), 8, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_CENTER, Element.ALIGN_MIDDLE)).setBorder(Rectangle.BOTTOM);
            header.addCell(setCellFonts(setFont("\n" + docnumber + "\n" + revnumber, 8, BaseColor.BLACK, Font.NORMAL), Element.ALIGN_RIGHT, Element.ALIGN_MIDDLE)).setBorder(Rectangle.BOTTOM);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // write content
        PdfContentByte canvas = writer.getDirectContent();
        canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
        header.writeSelectedRows(0, -1, 30, 597, canvas);
        canvas.endMarkedContentSequence();

    }

    @Override
    public void onChapter(PdfWriter writer, Document document,
                          float paragraphPosition, Paragraph title) {

        index.put(title.getContent(), page);
    }

    @Override
    public void onSection(PdfWriter writer, Document document,
                          float paragraphPosition, int depth, Paragraph title) {
        onChapter(writer, document, paragraphPosition, title);
    }

    private Document setborder(PdfWriter writer, Document document) {
        Rectangle border = writer.getBoxSize("art");
        border.enableBorderSide(1);
        border.enableBorderSide(2);
        border.enableBorderSide(4);
        border.enableBorderSide(8);
        border.setBorderColor(BaseColor.BLACK);
        border.setBorderWidth(2);
        try {
            document.add(border);
        } catch (Exception e) {

        }

        return document;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {

        PdfPTable footer = new PdfPTable(3);
        try {
            // set defaults
            footer.setWidths(new int[]{24, 2, 1});
            footer.setWidthPercentage(100);
            footer.setTotalWidth(950);
            footer.setLockedWidth(true);
            footer.getDefaultCell().setFixedHeight(10);
            footer.getDefaultCell().setBorder(Rectangle.TOP);
            footer.getDefaultCell().setBorderColor(BaseColor.BLACK);

            // add copyright
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            footer.addCell(new Phrase(getFooter(), new Font(Font.FontFamily.TIMES_ROMAN, 8)));

            // add current page count
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            footer.addCell(new Phrase(String.format("Page %d of", writer.getPageNumber()), new Font(Font.FontFamily.TIMES_ROMAN, 8)));

            // add placeholder for total page count
            PdfPCell totalPageCount = new PdfPCell(total);
            totalPageCount.setBorder(Rectangle.TOP);
            totalPageCount.setBorderColor(BaseColor.BLACK);
            footer.addCell(totalPageCount);

            // write page
            PdfContentByte canvas1 = writer.getDirectContent();
            canvas1.beginMarkedContentSequence(PdfName.ARTIFACT);
            footer.writeSelectedRows(0, -5, 30, 35, canvas1);
            canvas1.endMarkedContentSequence();
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }


        ColumnText.showTextAligned(writer.getDirectContentUnder(), Element.ALIGN_CENTER, new Phrase("XLM LLC", FONT), 500, 300, 45);
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {

        int totalLength = String.valueOf(writer.getPageNumber()).length();
        int totalWidth = totalLength * 5;
        ColumnText.showTextAligned(t, Element.ALIGN_RIGHT,
                new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.FontFamily.TIMES_ROMAN, 8)),
                totalWidth, 6, 0);
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setHeaderimage(String headerimage) {
        this.headerimage = headerimage;
    }

    public void setRevnumber(String revnumber) {
        this.revnumber = revnumber;
    }

    public void setDocnumber(String docnumber) {
        this.docnumber = docnumber;
    }

    private static class IndexEvent extends PdfPageEventHelper {
        private int page;
        private boolean body;

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            // set page number on content
            if (body) {
                page++;
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                        new Phrase(page + ""), (document.right() + document.left()) / 2, document.bottom() - 18, 0);
            }
        }
    }
}

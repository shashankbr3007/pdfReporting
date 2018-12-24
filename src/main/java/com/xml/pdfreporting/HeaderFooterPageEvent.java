package com.xml.pdfreporting;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.xml.pdfreporting.PDFReporter.totalIndexPages;
import static com.xml.pdfreporting.Utility.RomanNumerals;
import static com.xml.pdfreporting.Utility.setFont;


public class HeaderFooterPageEvent extends PdfPageEventHelper {

    static Map<String, Integer> index = new LinkedHashMap<String, Integer>();
    static int order;
    static int firstChapterPN;
    static int finishorder;
    private PdfTemplate t;
    private Image total;
    private String header;
    private String headerimage;
    private String footer;
    private String docnumber;
    private String revnumber;
    private String waterMarkText;
    private int waterMarkTextxPosition;
    private int waterMarkTextyPosition;
    private int page;

    HeaderFooterPageEvent(PdfWriter writer) {
        Rectangle rect = new Rectangle(15, 15, 993, 597);
        writer.setBoxSize("art", rect);
    }

    private String getFooter() {
        return footer;
    }

    void setFooter(String footer) {
        this.footer = footer;
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

    /**
     * this is overriden method which sets the page widths, the header image
     * the document and revision numbers on the header of the page
     *
     * @param writer
     * @param document
     */
    @Override
    public void onStartPage(PdfWriter writer, Document document) {

        PdfPTable header = new PdfPTable(3);
        try {
            page++;
            setborder(writer, document);
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

            PdfPCell cell = new PdfPCell();
            cell.setBorder(Rectangle.BOTTOM);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            header.addCell(nameImage);
            cell.setPhrase(setFont(getHeader(), 8, BaseColor.BLACK, Font.NORMAL));
            header.addCell(cell);


            cell.setPhrase(setFont("\n" + docnumber + "\n" + revnumber, 8, BaseColor.BLACK, Font.NORMAL));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            header.addCell(cell);

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

   /* @Override
    public void onSection(PdfWriter writer, Document document,
                          float paragraphPosition, int depth, Paragraph title) {
        onChapter(writer, document, paragraphPosition, title);
    }*/

    /**
     * @param writer
     * @param document
     * @throws DocumentException
     * @fucntion creates a page border for each the PDF page
     */
    private void setborder(PdfWriter writer, Document document) throws DocumentException {
        Rectangle border = writer.getBoxSize("art");
        border.enableBorderSide(1);
        border.enableBorderSide(2);
        border.enableBorderSide(4);
        border.enableBorderSide(8);
        border.setBorderColor(BaseColor.BLACK);
        border.setBorderWidth(2);
        document.add(border);
    }

    /**
     * this method has all the code to set the  page numbers, footer texts, watermark     *
     * @param writer
     * @param document
     */
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


            // add placeholder for total page count
            footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
            PdfPCell totalPageCount = new PdfPCell(total);
            totalPageCount.setBorder(Rectangle.TOP);
            totalPageCount.setBorderColor(BaseColor.BLACK);

            // add blank cell for the Roman numerals page numbers
            PdfPCell blank = new PdfPCell();
            blank.addElement(new Phrase(" "));
            blank.setBorder(Rectangle.TOP);
            blank.setBorderColor(BaseColor.BLACK);


            //writing Roman numerals on the page number until the first chapter
            if (order == 0 && firstChapterPN == 0 && finishorder == 0) {
                footer.addCell(new Phrase(setFont(RomanNumerals(writer.getPageNumber()), 8, BaseColor.BLACK, Font.NORMAL)));
                footer.addCell(blank);
            }
            //writing page x of y number from the first chapter until the last chapter
            else if (order == 0 && firstChapterPN < writer.getPageNumber() && finishorder == 0) {
                footer.addCell(new Phrase(setFont(String.format("Page %s of", writer.getPageNumber() - firstChapterPN), 8, BaseColor.BLACK, Font.NORMAL)));
                footer.addCell(totalPageCount);
            }
            //writing Roman numerals on the page for first page of TOC
            else if (order > 0 && firstChapterPN > 0 && finishorder == 0) {
                footer.addCell(new Phrase(setFont(RomanNumerals(writer.getPageNumber() - (order - firstChapterPN - 1)), 8, BaseColor.BLACK, Font.NORMAL)));
                footer.addCell(blank);
            }
            //writing Roman numerals on the page until the last page of TOC
            else if (order == 0 && firstChapterPN == writer.getPageNumber() && finishorder == 0) {
                footer.addCell(new Phrase(setFont(RomanNumerals(writer.getPageNumber()), 8, BaseColor.BLACK, Font.NORMAL)));
                footer.addCell(blank);
            }
            //writing page x of y number from the last page of TOC until the end of the document
            else if (order > 0 && firstChapterPN > 0 && finishorder > 0) {
                footer.addCell(new Phrase(setFont(String.format("Page %s of", writer.getPageNumber() - firstChapterPN - totalIndexPages), 8, BaseColor.BLACK, Font.NORMAL)));
                footer.addCell(totalPageCount);
            }

            // write page
            PdfContentByte canvas1 = writer.getDirectContent();
            canvas1.beginMarkedContentSequence(PdfName.ARTIFACT);
            footer.writeSelectedRows(0, -5, 30, 35, canvas1);
            canvas1.endMarkedContentSequence();
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }

        //text to write the watermark on each page of the PDF document
        ColumnText.showTextAligned(writer.getDirectContentUnder(), Element.ALIGN_CENTER, new Phrase(setFont(waterMarkText, 50, BaseColor.LIGHT_GRAY, Font.NORMAL)), waterMarkTextxPosition, waterMarkTextyPosition, 45);
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {

        int totalLength = String.valueOf(writer.getPageNumber()).length();
        int totalWidth = totalLength * 5;
        ColumnText.showTextAligned(t, Element.ALIGN_RIGHT,
                new Phrase(setFont(String.valueOf(writer.getPageNumber() - firstChapterPN - totalIndexPages), 8, BaseColor.BLACK, Font.NORMAL)),
                totalWidth, 6, 0);

    }

    private String getHeader() {
        return header;
    }

    void setHeader(String header) {
        this.header = header;
    }

    void setHeaderimage(String headerimage) {
        this.headerimage = headerimage;
    }

    void setRevnumber(String revnumber) {
        this.revnumber = revnumber;
    }

    void setDocnumber(String docnumber) {
        this.docnumber = docnumber;
    }

    void setWaterMarkText(String waterMarkText) {
        this.waterMarkText = waterMarkText;
    }

    void setWaterMarkTextxPosition(int waterMarkTextxPosition) {
        this.waterMarkTextxPosition = waterMarkTextxPosition;
    }

    void setWaterMarkTextyPosition(int waterMarkTextyPosition) {
        this.waterMarkTextyPosition = waterMarkTextyPosition;
    }
}

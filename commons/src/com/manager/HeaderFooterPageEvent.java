package com.manager;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

public class HeaderFooterPageEvent extends PdfPageEventHelper
{
    Phrase phraseLine1=null;
    Phrase phraseLine2=null;
    HeaderFooterPageEvent(){
        super();
    }
    HeaderFooterPageEvent(Phrase phraseLine1,Phrase phraseLine2){
        this.phraseLine1=phraseLine1;
        this.phraseLine2=phraseLine2;
    }
    public void onStartPage(PdfWriter writer,Document document) {
        //Rectangle rect = writer.getBoxSize("art");
        //ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase("Top Left"), rect.getLeft(), rect.getTop(), 0);
        //ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER, new Phrase("Top Right"), rect.getRight(), rect.getTop(), 0);
    }
    public void onEndPage(PdfWriter writer,Document document) {
        Rectangle rect = writer.getBoxSize("art");
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,phraseLine1,420, rect.getBottom()+35,0);
        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_CENTER,phraseLine2,450, rect.getBottom()+15,0);
    }
}

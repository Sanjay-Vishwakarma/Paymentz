package com.invoice.vo;

import javax.ws.rs.FormParam;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 08-05-2017.
 */

@XmlRootElement(name="pagination")
@XmlAccessorType(XmlAccessType.FIELD)
public class Pagination
{

    @FormParam("pagination.pageno")
    int pageno;

    @FormParam("pagination.records")
    int records;

    @FormParam("pagination.start")
    String start;

    @FormParam("pagination.end")
    String end;

    @FormParam("pagination.fromdate")
    String fromdate;

    @FormParam("pagination.todate")
    String todate;


    public int getPageno()
    {
        return pageno;
    }

    public void setPageno(int pageno)
    {
        this.pageno = pageno;
    }

    public int getRecords()
    {
        return records;
    }

    public void setRecords(int records)
    {
        this.records = records;
    }

    public String getStart()
    {
        return start;
    }

    public void setStart(String start)
    {
        this.start = start;
    }

    public String getEnd()
    {
        return end;
    }

    public void setEnd(String end)
    {
        this.end = end;
    }

    public String getFromdate()
    {
        return fromdate;
    }

    public void setFromdate(String fromdate)
    {
        this.fromdate = fromdate;
    }

    public String getTodate()
    {
        return todate;
    }

    public void setTodate(String todate)
    {
        this.todate = todate;
    }


}

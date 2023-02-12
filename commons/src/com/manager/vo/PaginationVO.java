package com.manager.vo;

import com.directi.pg.Functions;
import org.owasp.esapi.ESAPI;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/12/14
 * Time: 2:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaginationVO
{
    int totalRecords;
    int recordsPerPage=15;
    int pageNo=1;
    int currentBlock=1;
    //String input values
    String inputs;
    String page;

    String startdate;

    String start;

    String end;

    String enddate;

    public void setStart(String start)
    {
        this.start = start;
    }

    public void setEnd(String end)
    {
        this.end = end;
    }

    public String getStartdate()
    {
        return startdate;
    }

    public void setStartdate(String startdate)
    {
        this.startdate = startdate;
    }

    public String getEnddate()
    {
        return enddate;
    }

    public void setEnddate(String enddate)
    {
        this.enddate = enddate;
    }


    //setter and Getter

    public int getTotalRecords()
    {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords)
    {
        this.totalRecords = totalRecords;
    }

    public int getRecordsPerPage()
    {
        return recordsPerPage;
    }

    public void setRecordsPerPage(int recordsPerPage)
    {
        this.recordsPerPage = recordsPerPage;
    }

    public int getPageNo()
    {
        return pageNo;
    }

    public void setPageNo(int pageNo)
    {
        this.pageNo = pageNo;
    }

    public int getCurrentBlock()
    {
        return currentBlock;
    }

    public void setCurrentBlock(int currentBlock)
    {
        this.currentBlock = currentBlock;
    }

    public String getInputs()
    {
        return inputs;
    }

    public void setInputs(String inputs)
    {
        this.inputs = inputs;
    }

    public String getPage()
    {
        return page;
    }

    public void setPage(String page)
    {
        this.page = page;
    }

    //start and end for Query Limit
    public int getStart()
    {
        return (pageNo-1)*recordsPerPage;
    }

    public int getEnd()
    {
        return recordsPerPage;
    }
}

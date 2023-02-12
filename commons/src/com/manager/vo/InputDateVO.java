package com.manager.vo;

import com.directi.pg.Functions;
import com.manager.utils.AccountUtil;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 8/1/14
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class InputDateVO
{
    String fdate;
    String fmonth;
    String fyear;

    String tdate;
    String tmonth;
    String tyear;

    String fdtstamp;
    String tdtstamp;

    String sMinTransactionDate="";
    String sMaxTransactionDate="";

    String reportingDate="";

    //creating instance of account Util
    AccountUtil accountUtil = new AccountUtil();

    public String getsMinTransactionDate()
    {

        if(Functions.checkStringNull(this.getFdtstamp())!=null)
        {
            sMinTransactionDate=accountUtil.new_convertDtStampToDate(this.getFdtstamp());
        }


        return sMinTransactionDate;
    }

    public void setsMinTransactionDate(String sMinTransactionDate)
    {
        this.sMinTransactionDate = sMinTransactionDate;
    }

    public String getsMaxTransactionDate()
    {
        if(Functions.checkStringNull(this.getTdtstamp())!=null)
        {
            sMaxTransactionDate=accountUtil.new_convertDtStampToDate(this.getTdtstamp());
        }

        return sMaxTransactionDate;
    }

    public void setsMaxTransactionDate(String sMaxTransactionDate)
    {
        this.sMaxTransactionDate = sMaxTransactionDate;
    }

    public String getFdtstamp()
    {
        return fdtstamp;
    }

    public void setFdtstamp(String fdtstamp)
    {
        this.fdtstamp = fdtstamp;
    }

    public String getTdtstamp()
    {
        return tdtstamp;
    }

    public void setTdtstamp(String tdtstamp)
    {
        this.tdtstamp = tdtstamp;
    }

    public String getFdate()
    {
        return fdate;
    }

    public void setFdate(String fdate)
    {
        this.fdate = fdate;
    }

    public String getFmonth()
    {
        return fmonth;
    }

    public void setFmonth(String fmonth)
    {
        this.fmonth = fmonth;
    }

    public String getFyear()
    {
        return fyear;
    }

    public void setFyear(String fyear)
    {
        this.fyear = fyear;
    }

    public String getTdate()
    {
        return tdate;
    }

    public void setTdate(String tdate)
    {
        this.tdate = tdate;
    }

    public String getTmonth()
    {
        return tmonth;
    }

    public void setTmonth(String tmonth)
    {
        this.tmonth = tmonth;
    }

    public String getTyear()
    {
        return tyear;
    }

    public void setTyear(String tyear)
    {
        this.tyear = tyear;
    }

    public String getReportingDate()
    {
        return reportingDate;
    }

    public void setReportingDate(String reportingDate)
    {
        this.reportingDate = reportingDate;
    }
}

package com.vo.applicationManagerVOs;

/**
 * Created by Pradeep on 09/07/2015.
 */
public class ConsolidatedApplicationVO
{

    String consolidated_id;
    String memberid;
    String pgtypeid;
    String gatewayname;
    String bankapplicationid;
    String status;
    String filename;
    String dtstamp;
    String timestamp;

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getDtstamp()
    {
        return dtstamp;
    }

    public void setDtstamp(String dtstamp)
    {
        this.dtstamp = dtstamp;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getBankapplicationid()
    {
        return bankapplicationid;
    }

    public void setBankapplicationid(String bankapplicationid)
    {
        this.bankapplicationid = bankapplicationid;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getPgtypeid()
    {
        return pgtypeid;
    }

    public void setPgtypeid(String pgtypeid)
    {
        this.pgtypeid = pgtypeid;
    }

    public String getGatewayname()
    {
        return gatewayname;
    }

    public void setGatewayname(String gatewayname)
    {
        this.gatewayname = gatewayname;
    }

    public String getMemberid()
    {
        return memberid;
    }

    public void setMemberid(String memberid)
    {
        this.memberid = memberid;
    }

    public String getConsolidated_id()
    {
        return consolidated_id;
    }

    public void setConsolidated_id(String consolidated_id)
    {
        this.consolidated_id = consolidated_id;
    }
}

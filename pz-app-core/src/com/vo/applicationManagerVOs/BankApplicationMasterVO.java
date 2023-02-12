package com.vo.applicationManagerVOs;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 5/15/15
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankApplicationMasterVO
{
    String bankapplicationid;
    String application_id;
    String member_id;
    String pgtypeid;
    String bankfilename;
    String status;
    String remark;
    String dtstamp;
    String timestamp;

    public String getBankapplicationid()
    {
        return bankapplicationid;
    }

    public void setBankapplicationid(String bankapplicationid)
    {
        this.bankapplicationid = bankapplicationid;
    }

    public String getApplication_id()
    {
        return application_id;
    }

    public void setApplication_id(String application_id)
    {
        this.application_id = application_id;
    }

    public String getMember_id()
    {
        return member_id;
    }

    public void setMember_id(String member_id)
    {
        this.member_id = member_id;
    }

    public String getPgtypeid()
    {
        return pgtypeid;
    }

    public void setPgtypeid(String pgtypeid)
    {
        this.pgtypeid = pgtypeid;
    }

    public String getBankfilename()
    {
        return bankfilename;
    }

    public void setBankfilename(String bankfilename)
    {
        this.bankfilename = bankfilename;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getDtstamp()
    {
        return dtstamp;
    }

    public void setDtstamp(String dtstamp)
    {
        this.dtstamp = dtstamp;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }
}

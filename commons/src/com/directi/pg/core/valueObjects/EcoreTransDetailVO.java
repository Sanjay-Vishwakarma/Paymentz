package com.directi.pg.core.valueObjects;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/15/12
 * Time: 2:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class EcoreTransDetailVO
{
    private String remark;

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public String getMd5info()
    {
        return md5info;
    }

    public void setMd5info(String md5info)
    {
        this.md5info = md5info;
    }

    public String getBirthday()
    {
        return birthday;
    }

    public void setBirthday(String birthday)
    {
        this.birthday = birthday;
    }

    public String getSsn()
    {
        return ssn;
    }

    public void setSsn(String ssn)
    {
        this.ssn = ssn;
    }

    public String getMd5key()
    {
        return md5key;
    }

    public void setMd5key(String md5key)
    {
        this.md5key = md5key;
    }

    public String getPaymentordernumber()
    {
        return paymentordernumber;
    }

    public void setPaymentordernumber(String paymentordernumber)
    {
        this.paymentordernumber = paymentordernumber;
    }

    public String getBillno()
    {
        return billno;
    }

    public void setBillno(String billno)
    {
        this.billno = billno;
    }

    public String getRefundamount()
    {
        return refundamount;
    }

    public void setRefundamount(String refundamount)
    {
        this.refundamount = refundamount;
    }

    public String getReturnurl()
    {
        return returnurl;
    }

    public void setReturnurl(String returnurl)
    {
        this.returnurl = returnurl;
    }

    public String getMerchno()
    {
        return merchno;
    }

    public void setMerchno(String merchno)
    {
        this.merchno = merchno;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }
    public String getOperation()
    {
        return operation;
    }

    public void setOperation(String operation)
    {
        this.operation = operation;
    }

    private String operation;

    private String md5info;
    private String birthday;
    private String ssn;
    private String md5key;
    private String paymentordernumber;
    private String billno;
    private String refundamount;
    private String returnurl;
    private String merchno;
    private String language;

    public EcoreTransDetailVO()
    {
    }

    public EcoreTransDetailVO(String remark, String md5info, String birthday, String ssn, String md5key, String paymentordernumber, String billno, String refundamount, String returnurl, String merchno, String language)
    {
        this.remark = remark;
        this.md5info = md5info;
        this.birthday = birthday;
        this.ssn = ssn;
        this.md5key = md5key;
        this.paymentordernumber = paymentordernumber;
        this.billno = billno;
        this.refundamount = refundamount;
        this.returnurl = returnurl;
        this.merchno = merchno;
        this.language = language;
        // this.cardType = cardType;
    }
}

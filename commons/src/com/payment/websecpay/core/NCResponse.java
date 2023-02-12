package com.payment.websecpay.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 8/3/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("ncresponse")
public class NCResponse
{

    @XStreamAsAttribute
    private String orderID;

    @XStreamAsAttribute
    @XStreamAlias("PAYID")
    private String payID;

    @XStreamAsAttribute
    @XStreamAlias("STATUS")
    private String status;

    @XStreamAsAttribute
    @XStreamAlias("NCSTATUS")
    private String ncStatus;

    @XStreamAsAttribute
    @XStreamAlias("NCERROR")
    private String ncError;

    @XStreamAsAttribute
    @XStreamAlias("NCERRORPLUS")
    private String ncErrorPlus;

    @XStreamAsAttribute
    @XStreamAlias("ACCEPTANCE")
    private String acceptance;

    @XStreamAsAttribute
    @XStreamAlias("IPCTY")
    private String ipcty;

    @XStreamAsAttribute
    @XStreamAlias("CCCTY")
    private String cccty;

    @XStreamAsAttribute
    @XStreamAlias("IDUsager")
    private String idUsager;

    @XStreamAlias("form3ds")
    private String form3DS;





    public String getOrderID()
    {
        return orderID;
    }

    public void setOrderID(String orderID)
    {
        this.orderID = orderID;
    }

    public String getPayID()
    {
        return payID;
    }

    public void setPayID(String payID)
    {
        this.payID = payID;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getNcStatus()
    {
        return ncStatus;
    }

    public void setNcStatus(String ncStatus)
    {
        this.ncStatus = ncStatus;
    }

    public String getNcError()
    {
        return ncError;
    }

    public void setNcError(String ncError)
    {
        this.ncError = ncError;
    }

    public String getNcErrorPlus()
    {
        return ncErrorPlus;
    }

    public void setNcErrorPlus(String ncErrorPlus)
    {
        this.ncErrorPlus = ncErrorPlus;
    }

    public String getAcceptance()
    {
        return acceptance;
    }

    public void setAcceptance(String acceptance)
    {
        this.acceptance = acceptance;
    }

    public String getIpcty()
    {
        return ipcty;
    }

    public void setIpcty(String ipcty)
    {
        this.ipcty = ipcty;
    }

    public String getCccty()
    {
        return cccty;
    }

    public void setCccty(String cccty)
    {
        this.cccty = cccty;
    }

    public String getIdUsager()
    {
        return idUsager;
    }

    public void setIdUsager(String idUsager)
    {
        this.idUsager = idUsager;
    }

    public String getForm3DS()
    {
        return form3DS;
    }

    public void setForm3DS(String form3DS)
    {
        this.form3DS = form3DS;
    }
}

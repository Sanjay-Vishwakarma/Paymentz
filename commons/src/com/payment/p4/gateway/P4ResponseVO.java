package com.payment.p4.gateway;

import com.payment.common.core.CommResponseVO;

/**
 * Created by Admin on 23/10/2015.
 */
public class P4ResponseVO extends CommResponseVO
{
    private String ShortID;

    private String FormularURL;

    private String RedirectURL;

    private String ShopperID;

    private String InvoiceID;

    private String UniqueID;

    private String MandateURL;

    private String RevokeMandateURL;

    private boolean isRecurring;



    public String getShortID()
    {
        return ShortID;
    }

    public void setShortID(String shortID)
    {
        ShortID = shortID;
    }

    public String getFormularURL()
    {
        return FormularURL;
    }

    public void setFormularURL(String formularURL)
    {
        FormularURL = formularURL;
    }

    public String getRedirectURL()
    {
        return RedirectURL;
    }

    public void setRedirectURL(String redirectURL)
    {
        RedirectURL = redirectURL;
    }

    public String getShopperID()
    {
        return ShopperID;
    }

    public void setShopperID(String shopperID)
    {
        ShopperID = shopperID;
    }

    public String getInvoiceID()
    {
        return InvoiceID;
    }

    public void setInvoiceID(String invoiceID)
    {
        InvoiceID = invoiceID;
    }

    public String getUniqueID()
    {
        return UniqueID;
    }

    public void setUniqueID(String uniqueID)
    {
        UniqueID = uniqueID;
    }

    public String getMandateURL()
    {
        return MandateURL;
    }

    public void setMandateURL(String mandateURL)
    {
        MandateURL = mandateURL;
    }

    public String getRevokeMandateURL()
    {
        return RevokeMandateURL;
    }

    public void setRevokeMandateURL(String revokeMandateURL)
    {
        RevokeMandateURL = revokeMandateURL;
    }

    public boolean isRecurring()
    {
        return isRecurring;
    }

    public void setRecurring(boolean isRecurring)
    {
        this.isRecurring = isRecurring;
    }
}

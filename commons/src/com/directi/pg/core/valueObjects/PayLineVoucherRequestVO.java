package com.directi.pg.core.valueObjects;

import com.payment.common.core.CommRequestVO;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 5, 2012
 * Time: 12:34:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayLineVoucherRequestVO  extends CommRequestVO
{

    // Voucher Id
    private String id;

    //Extra Address Details
    private String company;
    private String salutation;
    private String title;
    private String mobile;

    /*References to the Unique ID of another transaction.
    Only needed for the submission of following transaction types: Capture, Reversal and Refund.
    Chargeback and Deposit transactions contain the Reference ID in the response message.*/
    private String referenceId ;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getCompany()
    {
        return company;
    }

    public void setCompany(String company)
    {
        this.company = company;
    }

    public String getSalutation()
    {
        return salutation;
    }

    public void setSalutation(String salutation)
    {
        this.salutation = salutation;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getReferenceId()
    {
        return referenceId;
    }

    public void setReferenceId(String referenceId)
    {
        this.referenceId = referenceId;
    }

}

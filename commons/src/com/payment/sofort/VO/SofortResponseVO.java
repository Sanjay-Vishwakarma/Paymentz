package com.payment.sofort.VO;

import com.payment.common.core.CommResponseVO;
import com.sofort.lib.products.common.BankAccount;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 17/1/15
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class SofortResponseVO extends CommResponseVO
{
    
    private String paymentURL;
    private String projectId;
    private String paymentMethod;
    private BankAccount sender;
    private BankAccount recipient;
    private String amountRefunded;
    private String languageCode;
    private String partialRefundId;

    public String getPartialRefundId()
    {
        return partialRefundId;
    }

    public void setPartialRefundId(String partialRefundId)
    {
        this.partialRefundId = partialRefundId;
    }

    public String getLanguageCode()
    {
        return languageCode;
    }

    public void setLanguageCode(String languageCode)
    {
        this.languageCode = languageCode;
    }

    public String getAmountRefunded()
    {
        return amountRefunded;
    }

    public void setAmountRefunded(String amountRefunded)
    {
        this.amountRefunded = amountRefunded;
    }

    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    public String getPaymentMethod()
    {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod)
    {
        this.paymentMethod = paymentMethod;
    }

    public BankAccount getSender()
    {
        return sender;
    }

    public void setSender(BankAccount sender)
    {
        this.sender = sender;
    }

    public BankAccount getRecipient()
    {
        return recipient;
    }

    public void setRecipient(BankAccount recipient)
    {
        this.recipient = recipient;
    }

    public String getPaymentURL()
    {
        return paymentURL;
    }

    public void setPaymentURL(String paymentURL)
    {
        this.paymentURL = paymentURL;
    }
}

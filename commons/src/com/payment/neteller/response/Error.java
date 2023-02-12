package com.payment.neteller.response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sneha on 2/22/2017.
 */
public class Error
{
    private String code;
    private String message;
    private List<FieldErrors> transactionLinkses = new ArrayList<FieldErrors>();

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public List<FieldErrors> getTransactionLinkses()
    {
        return transactionLinkses;
    }

    public void setTransactionLinkses(List<FieldErrors> transactionLinkses)
    {
        this.transactionLinkses = transactionLinkses;
    }
}

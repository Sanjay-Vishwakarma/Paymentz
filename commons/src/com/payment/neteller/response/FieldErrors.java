package com.payment.neteller.response;

/**
 * Created by Sneha on 2/22/2017.
 */
public class FieldErrors
{
    private String field;
    private String error;

    public String getField()
    {
        return field;
    }

    public void setField(String field)
    {
        this.field = field;
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }
}

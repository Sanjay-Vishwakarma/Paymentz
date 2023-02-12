package com.directi.pg.core.qwipi.core.message;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/8/12
 * Time: 2:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class Settle
{
    private String code;

    private String text;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
}

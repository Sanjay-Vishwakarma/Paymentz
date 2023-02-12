package com.payment.FrickBank.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 8/1/13
 * Time: 4:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class Payment
{   @XStreamAsAttribute
    private String code="";

    public Presentation getPresentation()
    {
        return presentation;
    }

    public void setPresentation(Presentation presentation)
    {
        this.presentation = presentation;
    }
    @XStreamAlias("Presentation")
    private Presentation presentation;
    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }


}

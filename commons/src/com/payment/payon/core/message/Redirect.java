package com.payment.payon.core.message;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/10/12
 * Time: 10:29 AM
 * To change this template use File | Settings | File Templates.
 */

public class Redirect
{
    @XStreamAsAttribute
    private String url;

    @XStreamAlias("Parameter")
    @XStreamImplicit
    private List<Parameter> parameters=new ArrayList<Parameter>();

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public List<Parameter> getParameters()
    {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters)
    {
        this.parameters = parameters;
    }
}

package com.payment.wirecard.core;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 5:57 PM
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("WIRECARD_BXML")
public class WIRECARD_BXML
{
    public W_REQUEST getW_request()
    {
        return w_request;
    }

    public void setW_request(W_REQUEST w_request)
    {
        this.w_request = w_request;
    }

    @XStreamAlias("W_REQUEST")
    W_REQUEST w_request;

    public W_RESPONSE getW_response()
    {
        return w_response;
    }

    public void setW_response(W_RESPONSE w_response)
    {
        this.w_response = w_response;
    }

    @XStreamAlias("W_RESPONSE")
    W_RESPONSE w_response;


    @XStreamAlias("xmlns:xsi")
    @XStreamAsAttribute
    String xmlns;

    @XStreamAlias("xsi:noNamespaceSchemaLocation")
    @XStreamAsAttribute
    String xsi;

    public String getXmlns()
    {
        return xmlns;
    }

    public void setXmlns(String xmlns)
    {
        this.xmlns = xmlns;
    }

    public String getXsi()
    {
        return xsi;
    }

    public void setXsi(String xsi)
    {
        this.xsi = xsi;
    }
}

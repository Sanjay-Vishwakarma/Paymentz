package com.payment.common.core;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.

 * User: Chandan
 * Date: 5/13/13
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class Comm3DResponseVO extends CommResponseVO
{

    private String urlFor3DRedirect;
    private String paReq;
    private String terURL;
    private String redirectMethod;
    private Map<String,String> requestMap;
    private String md;
    private String connector;
    private String method;
    private String target;
    private String threeDSSessionData;
    private String creq;
    private String threeDSServerTransID;
    private String threeDSMethodData;
    private String OptionalCode;


    public String getUrlFor3DRedirect()
    {
        return urlFor3DRedirect;
    }

    public void setUrlFor3DRedirect(String urlFor3DRedirect)
    {
        this.urlFor3DRedirect = urlFor3DRedirect;
    }

    public String getPaReq()
    {
        return paReq;
    }

    public void setPaReq(String paReq)
    {
        this.paReq = paReq;
    }

    public String getMd()
    {
        return md;
    }

    public void setMd(String md)
    {
        this.md = md;
    }

    public Map<String, String> getRequestMap()
    {
        return requestMap;
    }

    public void setRequestMap(Map<String, String> requestMap)
    {
        this.requestMap = requestMap;
    }

    public String getTerURL()
    {
        return terURL;
    }

    public void setTerURL(String terURL)
    {
        this.terURL = terURL;
    }

    public String getRedirectMethod()
    {
        return redirectMethod;
    }

    public void setRedirectMethod(String redirectMethod)
    {
        this.redirectMethod = redirectMethod;
    }

    public String getConnector()
    {
        return connector;
    }

    public void setConnector(String connector)
    {
        this.connector = connector;
    }

    public String getMethod()
    {
        return method;
    }

    public void setMethod(String method)
    {
        this.method = method;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }

    public String getThreeDSSessionData()
    {
        return threeDSSessionData;
    }

    public void setThreeDSSessionData(String threeDSSessionData)
    {
        this.threeDSSessionData = threeDSSessionData;
    }

    public String getCreq()
    {
        return creq;
    }

    public void setCreq(String creq)
    {
        this.creq = creq;
    }

    public String getThreeDSServerTransID()
    {
        return threeDSServerTransID;
    }

    public void setThreeDSServerTransID(String threeDSServerTransID)
    {
        this.threeDSServerTransID = threeDSServerTransID;
    }

    public String getThreeDSMethodData()
    {
        return threeDSMethodData;
    }

    public void setThreeDSMethodData(String threeDSMethodData)
    {
        this.threeDSMethodData = threeDSMethodData;
    }

    public String getOptionalCode()
    {
        return OptionalCode;
    }

    public void setOptionalCode(String optionalCode)
    {
        OptionalCode = optionalCode;
    }
}

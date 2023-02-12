package com.payment.p4.vos.transactionBlock.frontEndBlock;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Niket on 10/2/2015.
 */
@XmlRootElement(name="Frontend")
@XmlAccessorType(XmlAccessType.FIELD)
public class Frontend
{
    @XmlElement(name = "ResponseUrl")
    String ResponseUrl;

    @XmlElement(name = "ResponseErrorUrl")
    String ResponseErrorUrl;

    @XmlElement(name = "InternalResponseUrl")
    String InternalResponseUrl;

    @XmlElement(name = "ProjectName")
    String ProjectName;

    @XmlElement(name = "SessionID")
    String SessionID;

    @XmlElement(name = "Language")
    String Language;

    @XmlElement(name = "FormularUrl")
    String FormularUrl;

    @XmlElement(name = "RedirectUrl")
    String RedirectUrl;

    @XmlElement(name = "MandateShowUrl")
    String MandateShowUrl;

    @XmlElement(name = "MandateRevokeUrl")
    String MandateRevokeUrl;


    public String getResponseUrl()
    {
        return ResponseUrl;
    }

    public void setResponseUrl(String responseUrl)
    {
        ResponseUrl = responseUrl;
    }

    public String getResponseErrorUrl()
    {
        return ResponseErrorUrl;
    }

    public void setResponseErrorUrl(String responseErrorUrl)
    {
        ResponseErrorUrl = responseErrorUrl;
    }

    public String getInternalResponseUrl()
    {
        return InternalResponseUrl;
    }

    public void setInternalResponseUrl(String internalResponseUrl)
    {
        InternalResponseUrl = internalResponseUrl;
    }

    public String getProjectName()
    {
        return ProjectName;
    }

    public void setProjectName(String projectName)
    {
        ProjectName = projectName;
    }

    public String getSessionID()
    {
        return SessionID;
    }

    public void setSessionID(String sessionID)
    {
        SessionID = sessionID;
    }

    public String getLanguage()
    {
        return Language;
    }

    public void setLanguage(String language)
    {
        Language = language;
    }

    public String getFormularUrl()
    {
        return FormularUrl;
    }

    public void setFormularUrl(String formularUrl)
    {
        FormularUrl = formularUrl;
    }

    public String getRedirectUrl()
    {
        return RedirectUrl;
    }

    public void setRedirectUrl(String redirectUrl)
    {
        RedirectUrl = redirectUrl;
    }

    public String getMandateRevokeUrl()
    {
        return MandateRevokeUrl;
    }

    public void setMandateRevokeUrl(String mandateRevokeUrl)
    {
        MandateRevokeUrl = mandateRevokeUrl;
    }

    public String getMandateShowUrl()
    {
        return MandateShowUrl;
    }

    public void setMandateShowUrl(String mandateShowUrl)
    {
        MandateShowUrl = mandateShowUrl;
    }
}

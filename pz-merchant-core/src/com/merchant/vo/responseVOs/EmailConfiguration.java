package com.merchant.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 01-Feb-22.
 */
@XmlRootElement(name="emailConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailConfiguration
{
    @XmlElement(name="isValidateEmail")
    private String isValidateEmail;

    @XmlElement(name="emailTemplateLanguage")
    private String emailTemplateLanguage;

    public String getIsValidateEmail()
    {
        return isValidateEmail;
    }

    public void setIsValidateEmail(String isValidateEmail)
    {
        this.isValidateEmail = isValidateEmail;
    }

    public String getEmailTemplateLanguage()
    {
        return emailTemplateLanguage;
    }

    public void setEmailTemplateLanguage(String emailTemplateLanguage)
    {
        this.emailTemplateLanguage = emailTemplateLanguage;
    }

    @Override
    public String toString()
    {
        return "EmailConfiguration{" +
                "isValidateEmail='" + isValidateEmail + '\'' +
                ", emailTemplateLanguage='" + emailTemplateLanguage + '\'' +
                '}';
    }
}

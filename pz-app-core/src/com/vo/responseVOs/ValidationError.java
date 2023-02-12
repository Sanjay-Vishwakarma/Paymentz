package com.vo.responseVOs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Niket on 2/13/2016.
 */
@XmlRootElement(name="ValidationError")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidationError
{
    @XmlElement(name = "fieldName")
    private String fieldName;

    @XmlElement(name = "message")
    private String message;

    @XmlElement(name = "code")
    private String resultCode;

    @XmlElement(name = "description")
    private String resultDescription;

    public String getResultCode()
    {
        return resultCode;
    }

    public void setResultCode(String resultCode)
    {
        this.resultCode = resultCode;
    }

    public String getResultDescription()
    {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription)
    {
        this.resultDescription = resultDescription;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}

package com.payment.p4.vos.queryBlock;

import com.payment.p4.vos.queryBlock.identificationsBlock.Identifications;
import com.payment.p4.vos.queryBlock.periodBlock.Period;
import com.payment.p4.vos.queryBlock.resultSetBlock.ResultSet;
import com.payment.p4.vos.queryBlock.scopeBlock.Scope;
import com.payment.p4.vos.queryBlock.typesBlock.Types;
import com.payment.p4.vos.transactionBlock.loginBlock.Login;
import com.payment.p4.vos.transactionBlock.processingBlock.Processing;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.xml.bind.annotation.*;

/**
 * Created by Admin on 28/10/2015.
 */
@XmlRootElement(name = "Query")
@XmlAccessorType(XmlAccessType.FIELD)
public class Query
{
    @XmlAttribute(name="mode")
    String mode;

    @XmlAttribute(name = "response")
    String response;

    @XmlElement(name = "Login")
    private Login Login;

    @XmlElement(name = "Scope")
    private Scope Scope;

    @XmlElement(name = "Period")
    private Period Period;

    @XmlElement(name = "Types")
    private Types Types;

    @XmlElement(name = "Identifications")
    private Identifications Identifications;

    @XmlElement(name = "Processing")
    private Processing Processing;

    @XmlPath("Processing/@code")
    private String code;

    @XmlElement(name = "ResultSet")
    private ResultSet ResultSet;



    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public String getResponse()
    {
        return response;
    }

    public void setResponse(String response)
    {
        this.response = response;
    }

    public com.payment.p4.vos.transactionBlock.loginBlock.Login getLogin()
    {
        return Login;
    }

    public void setLogin(com.payment.p4.vos.transactionBlock.loginBlock.Login login)
    {
        Login = login;
    }

    public Scope getScope()
    {
        return Scope;
    }

    public void setScope(Scope scope)
    {
        Scope = scope;
    }

    public Period getPeriod()
    {
        return Period;
    }

    public void setPeriod(Period period)
    {
        Period = period;
    }

    public Types getTypes()
    {
        return Types;
    }

    public void setTypes(Types types)
    {
        Types = types;
    }

    public Identifications getIdentifications()
    {
        return Identifications;
    }

    public void setIdentifications(Identifications identifications)
    {
        Identifications = identifications;
    }

    public com.payment.p4.vos.transactionBlock.processingBlock.Processing getProcessing()
    {
        return Processing;
    }

    public void setProcessing(com.payment.p4.vos.transactionBlock.processingBlock.Processing processing)
    {
        Processing = processing;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public com.payment.p4.vos.queryBlock.resultSetBlock.ResultSet getResultSet()
    {
        return ResultSet;
    }

    public void setResultSet(com.payment.p4.vos.queryBlock.resultSetBlock.ResultSet resultSet)
    {
        ResultSet = resultSet;
    }
}

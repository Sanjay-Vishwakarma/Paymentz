package com.directi.pg.core.valueObjects;


public class CupResponseVO extends GenericResponseVO
{
    private String	ResponseCode	;
    private String	Description	;
    private String	Reference	;
    private String	TransactionID	;
    private String	ProcessingTime	;
    private String	StatusCode	;
    private String	StatusDescription	;
    private String	AuthCode	;
    private String	ScrubResult	;
    private String	AVSResult	;
    private String	CVVResult	;
    private String	CustomerID	;
    private String	transType	;

    public String getTransType()
    {
        return transType;
    }

    public void setTransType(String transType)
    {
        this.transType = transType;
    }

    public String getResponseCode()
    {
        return ResponseCode;

    }

    public void setResponseCode(String responseCode)
    {
        ResponseCode = responseCode;
    }

    public String getDescription()
    {
        return Description;
    }

    public void setDescription(String description)
    {
        Description = description;
    }

    public String getReference()
    {
        return Reference;
    }

    public void setReference(String reference)
    {
        Reference = reference;
    }

    public String getTransactionID()
    {
        return TransactionID;
    }

    public void setTransactionID(String transactionID)
    {
        TransactionID = transactionID;
    }

    public String getProcessingTime()
    {
        return ProcessingTime;
    }

    public void setProcessingTime(String processingTime)
    {
        ProcessingTime = processingTime;
    }

    public String getStatusCode()
    {
        return StatusCode;
    }

    public void setStatusCode(String statusCode)
    {
        StatusCode = statusCode;
    }

    public String getStatusDescription()
    {
        return StatusDescription;
    }

    public void setStatusDescription(String statusDescription)
    {
        StatusDescription = statusDescription;
    }

    public String getAuthCode()
    {
        return AuthCode;
    }

    public void setAuthCode(String authCode)
    {
        AuthCode = authCode;
    }

    public String getScrubResult()
    {
        return ScrubResult;
    }

    public void setScrubResult(String scrubResult)
    {
        ScrubResult = scrubResult;
    }

    public String getAVSResult()
    {
        return AVSResult;
    }

    public void setAVSResult(String AVSResult)
    {
        this.AVSResult = AVSResult;
    }

    public String getCVVResult()
    {
        return CVVResult;
    }

    public void setCVVResult(String CVVResult)
    {
        this.CVVResult = CVVResult;
    }

    public String getCustomerID()
    {
        return CustomerID;
    }

    public void setCustomerID(String customerID)
    {
        CustomerID = customerID;
    }


}
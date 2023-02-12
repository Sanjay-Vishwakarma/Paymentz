package com.payment.response;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Apr 4, 2013
 * Time: 8:17:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZInquiryResponse  extends PZResponse
{
    //common fields

    String responseTransactionId;             //PaymentId from gateway
    String responseTransactionStatus;
    String responseCode;
    String responseDescription;
    String responseTime;
    String responseDescriptor;
    String responseHashinfo;
    String responseAmount;
    String responseCurrency;
    String responseTransactionTime;
    String mid;
    String authCode;
    String transactionType;

    //extra fields
    String  responseName1;         //name of the response will be column name (extra field)
    String  responseValue1;        //value of the response

    String  responseName2;
    String  responseValue2;

    String  responseName3;
    String  responseValue3;

    public String getResponseTransactionId()
    {
        return responseTransactionId;
    }

    public void setResponseTransactionId(String responseTransactionId)
    {
        this.responseTransactionId = responseTransactionId;
    }

    public String getResponseTransactionStatus()
    {
        return responseTransactionStatus;
    }

    public void setResponseTransactionStatus(String responseTransactionStatus)
    {
        this.responseTransactionStatus = responseTransactionStatus;
    }

    public String getResponseCode()
    {
        return responseCode;
    }

    public void setResponseCode(String responseCode)
    {
        this.responseCode = responseCode;
    }

    public String getResponseDescription()
    {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription)
    {
        this.responseDescription = responseDescription;
    }

    public String getResponseTime()
    {
        return responseTime;
    }

    public void setResponseTime(String responseTime)
    {
        this.responseTime = responseTime;
    }

    public String getResponseDescriptor()
    {
        return responseDescriptor;
    }

    public void setResponseDescriptor(String responseDescriptor)
    {
        this.responseDescriptor = responseDescriptor;
    }

    public String getResponseHashinfo()
    {
        return responseHashinfo;
    }

    public void setResponseHashinfo(String responseHashinfo)
    {
        this.responseHashinfo = responseHashinfo;
    }

    public String getResponseName1()
    {
        return responseName1;
    }

    public void setResponseName1(String responseName1)
    {
        this.responseName1 = responseName1;
    }

    public String getResponseValue1()
    {
        return responseValue1;
    }

    public void setResponseValue1(String responseValue1)
    {
        this.responseValue1 = responseValue1;
    }

    public String getResponseName2()
    {
        return responseName2;
    }

    public void setResponseName2(String responseName2)
    {
        this.responseName2 = responseName2;
    }

    public String getResponseValue2()
    {
        return responseValue2;
    }

    public void setResponseValue2(String responseValue2)
    {
        this.responseValue2 = responseValue2;
    }

    public String getResponseName3()
    {
        return responseName3;
    }

    public void setResponseName3(String responseName3)
    {
        this.responseName3 = responseName3;
    }

    public String getResponseValue3()
    {
        return responseValue3;
    }

    public void setResponseValue3(String responseValue3)
    {
        this.responseValue3 = responseValue3;
    }

    public String getResponseAmount()
    {
        return responseAmount;
    }

    public void setResponseAmount(String responseAmount)
    {
        this.responseAmount = responseAmount;
    }

    public String getResponseCurrency()
    {
        return responseCurrency;
    }

    public void setResponseCurrency(String responseCurrency)
    {
        this.responseCurrency = responseCurrency;
    }

    public String getResponseTransactionTime()
    {
        return responseTransactionTime;
    }

    public void setResponseTransactionTime(String responseTransactionTime)
    {
        this.responseTransactionTime = responseTransactionTime;
    }

    public String getMid()
    {
        return mid;
    }

    public void setMid(String mid)
    {
        this.mid = mid;
    }

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getTransactionType()
    {
        return transactionType;
    }

    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }
}

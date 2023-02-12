package com.payment.payvision.core.type;

import com.payment.payvision.core.message.CdcEntry;
import org.apache.james.mime4j.dom.datetime.DateTime;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by admin on 5/13/2016.
 */
@XmlRootElement(name="Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class Response
{

    @XmlElement(name = "BankCode")
    private int BankCode;

    @XmlElement(name = "BankMessage")
    private String BankMessage;

    @XmlElement(name = "BankApprovalCode")
    private String BankApprovalCode;

    @XmlElement(name = "CVVResult")
    private long CVVResult;

    @XmlElement(name = "Warning")
    private String Warning;

    @XmlElement(name = "CardId")
    private DateTime CardId;

    @XmlElement(name = "CardGuid")
    private String CardGuid;

    @XmlElement(name = "Cdc")
    private CdcEntry Cdc;

    public int getBankCode()
    {
        return BankCode;
    }

    public void setBankCode(int bankCode)
    {
        BankCode = bankCode;
    }

    public String getBankMessage()
    {
        return BankMessage;
    }

    public void setBankMessage(String bankMessage)
    {
        BankMessage = bankMessage;
    }

    public String getBankApprovalCode()
    {
        return BankApprovalCode;
    }

    public void setBankApprovalCode(String bankApprovalCode)
    {
        BankApprovalCode = bankApprovalCode;
    }

    public long getCVVResult()
    {
        return CVVResult;
    }

    public void setCVVResult(long CVVResult)
    {
        this.CVVResult = CVVResult;
    }

    public String getWarning()
    {
        return Warning;
    }

    public void setWarning(String warning)
    {
        Warning = warning;
    }

    public DateTime getCardId()
    {
        return CardId;
    }

    public void setCardId(DateTime cardId)
    {
        CardId = cardId;
    }

    public String getCardGuid()
    {
        return CardGuid;
    }

    public void setCardGuid(String cardGuid)
    {
        CardGuid = cardGuid;
    }

    public CdcEntry getCdc()
    {
        return Cdc;
    }

    public void setCdc(CdcEntry cdc)
    {
        Cdc = cdc;
    }
}

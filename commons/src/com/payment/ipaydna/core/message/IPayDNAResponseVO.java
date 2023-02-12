package com.payment.ipaydna.core.message;

import com.payment.common.core.CommResponseVO;

/**
 * Created by IntelliJ IDEA.
 * User: saurabh.b
 * Date: 12/27/13
 * Time: 9:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class IPayDNAResponseVO  extends CommResponseVO
{
    String INVOICENO;     //
    String AuthorizationCode;
    String BatchID;     //
    String AVSResponse;
    String CurrencyText;
    String ReferralOrderReference;
    String SettlementDate;
    String SettlementStatusText;


    public String getSettlementDate()
    {
        return SettlementDate;
    }

    public void setSettlementDate(String settlementDate)
    {
        SettlementDate = settlementDate;
    }

    public String getSettlementStatusText()
    {
        return SettlementStatusText;
    }

    public void setSettlementStatusText(String settlementStatusText)
    {
        SettlementStatusText = settlementStatusText;
    }





    public String getReferralOrderReference()
    {
        return ReferralOrderReference;
    }

    public void setReferralOrderReference(String referralOrderReference)
    {
        ReferralOrderReference = referralOrderReference;
    }

                              //referralorderreference
    public String getINVOICENO()
    {
        return INVOICENO;
    }

    public void setINVOICENO(String INVOICENO)
    {
        this.INVOICENO = INVOICENO;
    }

    public String getCurrencyText()
    {
        return CurrencyText;
    }

    public void setCurrencyText(String currencyText)
    {
        CurrencyText = currencyText;
    }

    public String getAuthorizationCode()
    {
        return AuthorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode)
    {
        AuthorizationCode = authorizationCode;
    }

    public String getBatchID()
    {
        return BatchID;
    }

    public void setBatchID(String batchID)
    {
        BatchID = batchID;
    }



    public String getAVSResponse()
    {
        return AVSResponse;
    }

    public void setAVSResponse(String AVSResponse)
    {
        this.AVSResponse = AVSResponse;
    }





}

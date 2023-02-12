package com.payment.nestpay;

import com.payment.common.core.Comm3DResponseVO;

/**
 * Created by Admin on 11/15/18.
 */
public class NestPayResponseVO extends Comm3DResponseVO
{
    private String OrderId;
    private String Response;
    private String AuthCode;
    private String HostRefNum;
    private String ProcReturnCode;
    private String TransId;
    private String ErrMsg;
    private String SETTLEID;
    private String TRXDATE;
    private String ERRORCODE;
    private String NUMBEROFINSTALLMENTS1;
    private String CARDISSUER;
    private String ADVICEDINSTALLMENTTYPE;
    private String DIGERTAKSITTUTARI1;
    private String INTERESTRATE2;
    private String DIGERTAKSITTUTARI2;
    private String TOTALAMOUNTDUE1;
    private String INTERESTRATE1;
    private String TOTALAMOUNTDUE2;
    private String NUMBEROFINSTALLMENTS2;
    private String ANNUALPERCENTAGE1;
    private String ANNUALPERCENTAGE2;
    private String INSTALLMENTTYPE;
    private String INSTALLMENTOPTION;
    private String CARDBRAND;
    private String NUMBEROFINSTALLMENTOPTIONS;
    private String ADVICEINSTALLMENTEXIST;
    private String INSTALLMENTFEE2;
    private String INSTALLMENTFEE1;
    private String NUMCODE;
    private String ILKTAKSITTUTARI2;
    private String ILKTAKSITTUTARI1;

    public String getOrderId()
    {
        return OrderId;
    }

    public void setOrderId(String orderId)
    {
        OrderId = orderId;
    }

    public String getResponse()
    {
        return Response;
    }

    public void setResponse(String response)
    {
        Response = response;
    }

    public String getAuthCode()
    {
        return AuthCode;
    }

    public void setAuthCode(String authCode)
    {
        AuthCode = authCode;
    }

    public String getHostRefNum()
    {
        return HostRefNum;
    }

    public void setHostRefNum(String hostRefNum)
    {
        HostRefNum = hostRefNum;
    }

    public String getProcReturnCode()
    {
        return ProcReturnCode;
    }

    public void setProcReturnCode(String procReturnCode)
    {
        ProcReturnCode = procReturnCode;
    }

    public String getTransId()
    {
        return TransId;
    }

    public void setTransId(String transId)
    {
        TransId = transId;
    }

    public String getSETTLEID()
    {
        return SETTLEID;
    }

    public void setSETTLEID(String SETTLEID)
    {
        this.SETTLEID = SETTLEID;
    }

    public String getTRXDATE()
    {
        return TRXDATE;
    }

    public void setTRXDATE(String TRXDATE)
    {
        this.TRXDATE = TRXDATE;
    }

    public String getERRORCODE()
    {
        return ERRORCODE;
    }

    public void setERRORCODE(String ERRORCODE)
    {
        this.ERRORCODE = ERRORCODE;
    }

    public String getNUMBEROFINSTALLMENTS1()
    {
        return NUMBEROFINSTALLMENTS1;
    }

    public void setNUMBEROFINSTALLMENTS1(String NUMBEROFINSTALLMENTS1)
    {
        this.NUMBEROFINSTALLMENTS1 = NUMBEROFINSTALLMENTS1;
    }

    public String getCARDISSUER()
    {
        return CARDISSUER;
    }

    public void setCARDISSUER(String CARDISSUER)
    {
        this.CARDISSUER = CARDISSUER;
    }

    public String getADVICEDINSTALLMENTTYPE()
    {
        return ADVICEDINSTALLMENTTYPE;
    }

    public void setADVICEDINSTALLMENTTYPE(String ADVICEDINSTALLMENTTYPE)
    {
        this.ADVICEDINSTALLMENTTYPE = ADVICEDINSTALLMENTTYPE;
    }

    public String getDIGERTAKSITTUTARI1()
    {
        return DIGERTAKSITTUTARI1;
    }

    public void setDIGERTAKSITTUTARI1(String DIGERTAKSITTUTARI1)
    {
        this.DIGERTAKSITTUTARI1 = DIGERTAKSITTUTARI1;
    }

    public String getINTERESTRATE2()
    {
        return INTERESTRATE2;
    }

    public void setINTERESTRATE2(String INTERESTRATE2)
    {
        this.INTERESTRATE2 = INTERESTRATE2;
    }

    public String getDIGERTAKSITTUTARI2()
    {
        return DIGERTAKSITTUTARI2;
    }

    public void setDIGERTAKSITTUTARI2(String DIGERTAKSITTUTARI2)
    {
        this.DIGERTAKSITTUTARI2 = DIGERTAKSITTUTARI2;
    }

    public String getTOTALAMOUNTDUE1()
    {
        return TOTALAMOUNTDUE1;
    }

    public void setTOTALAMOUNTDUE1(String TOTALAMOUNTDUE1)
    {
        this.TOTALAMOUNTDUE1 = TOTALAMOUNTDUE1;
    }

    public String getINTERESTRATE1()
    {
        return INTERESTRATE1;
    }

    public void setINTERESTRATE1(String INTERESTRATE1)
    {
        this.INTERESTRATE1 = INTERESTRATE1;
    }

    public String getTOTALAMOUNTDUE2()
    {
        return TOTALAMOUNTDUE2;
    }

    public void setTOTALAMOUNTDUE2(String TOTALAMOUNTDUE2)
    {
        this.TOTALAMOUNTDUE2 = TOTALAMOUNTDUE2;
    }

    public String getNUMBEROFINSTALLMENTS2()
    {
        return NUMBEROFINSTALLMENTS2;
    }

    public void setNUMBEROFINSTALLMENTS2(String NUMBEROFINSTALLMENTS2)
    {
        this.NUMBEROFINSTALLMENTS2 = NUMBEROFINSTALLMENTS2;
    }

    public String getANNUALPERCENTAGE1()
    {
        return ANNUALPERCENTAGE1;
    }

    public void setANNUALPERCENTAGE1(String ANNUALPERCENTAGE1)
    {
        this.ANNUALPERCENTAGE1 = ANNUALPERCENTAGE1;
    }

    public String getANNUALPERCENTAGE2()
    {
        return ANNUALPERCENTAGE2;
    }

    public void setANNUALPERCENTAGE2(String ANNUALPERCENTAGE2)
    {
        this.ANNUALPERCENTAGE2 = ANNUALPERCENTAGE2;
    }

    public String getINSTALLMENTTYPE()
    {
        return INSTALLMENTTYPE;
    }

    public void setINSTALLMENTTYPE(String INSTALLMENTTYPE)
    {
        this.INSTALLMENTTYPE = INSTALLMENTTYPE;
    }

    public String getINSTALLMENTOPTION()
    {
        return INSTALLMENTOPTION;
    }

    public void setINSTALLMENTOPTION(String INSTALLMENTOPTION)
    {
        this.INSTALLMENTOPTION = INSTALLMENTOPTION;
    }

    public String getCARDBRAND()
    {
        return CARDBRAND;
    }

    public void setCARDBRAND(String CARDBRAND)
    {
        this.CARDBRAND = CARDBRAND;
    }

    public String getNUMBEROFINSTALLMENTOPTIONS()
    {
        return NUMBEROFINSTALLMENTOPTIONS;
    }

    public void setNUMBEROFINSTALLMENTOPTIONS(String NUMBEROFINSTALLMENTOPTIONS)
    {
        this.NUMBEROFINSTALLMENTOPTIONS = NUMBEROFINSTALLMENTOPTIONS;
    }

    public String getADVICEINSTALLMENTEXIST()
    {
        return ADVICEINSTALLMENTEXIST;
    }

    public void setADVICEINSTALLMENTEXIST(String ADVICEINSTALLMENTEXIST)
    {
        this.ADVICEINSTALLMENTEXIST = ADVICEINSTALLMENTEXIST;
    }

    public String getINSTALLMENTFEE2()
    {
        return INSTALLMENTFEE2;
    }

    public void setINSTALLMENTFEE2(String INSTALLMENTFEE2)
    {
        this.INSTALLMENTFEE2 = INSTALLMENTFEE2;
    }

    public String getINSTALLMENTFEE1()
    {
        return INSTALLMENTFEE1;
    }

    public void setINSTALLMENTFEE1(String INSTALLMENTFEE1)
    {
        this.INSTALLMENTFEE1 = INSTALLMENTFEE1;
    }

    public String getNUMCODE()
    {
        return NUMCODE;
    }

    public void setNUMCODE(String NUMCODE)
    {
        this.NUMCODE = NUMCODE;
    }

    public String getILKTAKSITTUTARI2()
    {
        return ILKTAKSITTUTARI2;
    }

    public void setILKTAKSITTUTARI2(String ILKTAKSITTUTARI2)
    {
        this.ILKTAKSITTUTARI2 = ILKTAKSITTUTARI2;
    }

    public String getILKTAKSITTUTARI1()
    {
        return ILKTAKSITTUTARI1;
    }

    public void setILKTAKSITTUTARI1(String ILKTAKSITTUTARI1)
    {
        this.ILKTAKSITTUTARI1 = ILKTAKSITTUTARI1;
    }

    public String getErrMsg()
    {
        return ErrMsg;
    }

    public void setErrMsg(String errMsg)
    {
        ErrMsg = errMsg;
    }
}

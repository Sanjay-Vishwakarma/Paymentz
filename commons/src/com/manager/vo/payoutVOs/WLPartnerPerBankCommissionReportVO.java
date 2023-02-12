package com.manager.vo.payoutVOs;

import com.directi.pg.core.GatewayType;
import com.manager.vo.TransactionSummaryVO;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Sandip
 * Date: 12/12/16
 * Time: 2:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class WLPartnerPerBankCommissionReportVO
{
    HashMap<String, WLPartnerCommissionDetailsVO> transactionFeeHashMap;
    TransactionSummaryVO transactionSummaryVO;
    GatewayType gatewayType;

    String processingCurrency;
    double transactionFeeAmount;
    double otherFeeAmount;
    double conversionRate;
    double convertedAmount;

    public TransactionSummaryVO getTransactionSummaryVO()
    {
        return transactionSummaryVO;
    }

    public void setTransactionSummaryVO(TransactionSummaryVO transactionSummaryVO)
    {
        this.transactionSummaryVO = transactionSummaryVO;
    }

    public double getTransactionFeeAmount()
    {
        return transactionFeeAmount;
    }

    public void setTransactionFeeAmount(double transactionFeeAmount)
    {
        this.transactionFeeAmount = transactionFeeAmount;
    }

    public double getOtherFeeAmount()
    {
        return otherFeeAmount;
    }

    public void setOtherFeeAmount(double otherFeeAmount)
    {
        this.otherFeeAmount = otherFeeAmount;
    }

    public GatewayType getGatewayType()
    {
        return gatewayType;
    }

    public void setGatewayType(GatewayType gatewayType)
    {
        this.gatewayType = gatewayType;
    }

    public HashMap<String, WLPartnerCommissionDetailsVO> getTransactionFeeHashMap()
    {
        return transactionFeeHashMap;
    }

    public void setTransactionFeeHashMap(HashMap<String, WLPartnerCommissionDetailsVO> transactionFeeHashMap)
    {
        this.transactionFeeHashMap = transactionFeeHashMap;
    }

    public String getProcessingCurrency()
    {
        return processingCurrency;
    }

    public void setProcessingCurrency(String processingCurrency)
    {
        this.processingCurrency = processingCurrency;
    }

    public double getConversionRate()
    {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate)
    {
        this.conversionRate = conversionRate;
    }

    public double getConvertedAmount()
    {
        return convertedAmount;
    }

    public void setConvertedAmount(double convertedAmount)
    {
        this.convertedAmount = convertedAmount;
    }
}

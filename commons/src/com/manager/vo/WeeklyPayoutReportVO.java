package com.manager.vo;

import com.manager.vo.payoutVOs.MerchantRandomChargesVO;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 8/1/14
 * Time: 4:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class WeeklyPayoutReportVO
{
    TerminalVO terminalVO;

    RollingReserveDateVO rollingReserveDateVO;
    SettlementDateVO settlementDateVO;
    List<ChargeVO> chargeVOList;
    List<MerchantRandomChargesVO> merchantRandomChargesVOList;
    HashMap<String,MerchantRandomChargesVO> merchantRandomChargesVOListNew;
    TransactionSummaryVO transactionSummaryVO;
    HashMap<String, String> exchangeRate;
    HashMap<String, String> dynamicCountAmountMap;
    List<TerminalVO> pendingList;
    List<TerminalVO> RequestTerminalVO;

    public TerminalVO getTerminalVO()
    {
        return terminalVO;
    }
    public void setTerminalVO(TerminalVO terminalVO)
    {
        this.terminalVO = terminalVO;
    }
    public RollingReserveDateVO getRollingReserveDateVO()
    {
        return rollingReserveDateVO;
    }
    public void setRollingReserveDateVO(RollingReserveDateVO rollingReserveDateVO)
    {
        this.rollingReserveDateVO = rollingReserveDateVO;
    }
    public SettlementDateVO getSettlementDateVO()
    {
        return settlementDateVO;
    }
    public void setSettlementDateVO(SettlementDateVO settlementDateVO)
    {
        this.settlementDateVO = settlementDateVO;
    }

    public List<ChargeVO> getChargeVOList()
    {
        return chargeVOList;
    }

    public void setChargeVOList(List<ChargeVO> chargeVOList)
    {
        this.chargeVOList = chargeVOList;
    }

    public TransactionSummaryVO getTransactionSummaryVO()
    {
        return transactionSummaryVO;
    }
    public void setTransactionSummaryVO(TransactionSummaryVO transactionSummaryVO)
    {
        this.transactionSummaryVO = transactionSummaryVO;
    }

    public List<MerchantRandomChargesVO> getMerchantRandomChargesVOList()
    {
        return merchantRandomChargesVOList;
    }

    public void setMerchantRandomChargesVOList(List<MerchantRandomChargesVO> merchantRandomChargesVOList)
    {
        this.merchantRandomChargesVOList = merchantRandomChargesVOList;
    }

    public HashMap<String, MerchantRandomChargesVO> getMerchantRandomChargesVOListNew()
    {
        return merchantRandomChargesVOListNew;
    }

    public void setMerchantRandomChargesVOListNew(HashMap<String, MerchantRandomChargesVO> merchantRandomChargesVOListNew)
    {
        this.merchantRandomChargesVOListNew = merchantRandomChargesVOListNew;
    }

    public HashMap<String, String> getExchangeRate()
    {
        return exchangeRate;
    }

    public void setExchangeRate(HashMap<String, String> exchangeRate)
    {
        this.exchangeRate = exchangeRate;
    }

    public HashMap<String, String> getDynamicCountAmountMap()
    {
        return dynamicCountAmountMap;
    }

    public void setDynamicCountAmountMap(HashMap<String, String> dynamicCountAmountMap)
    {
        this.dynamicCountAmountMap = dynamicCountAmountMap;
    }

    public List<TerminalVO> getPendingList()
    {
        return pendingList;
    }

    public void setPendingList(List<TerminalVO> pendingList)
    {
        this.pendingList = pendingList;
    }

    public List<TerminalVO> getRequestTerminalVO()
    {
        return RequestTerminalVO;
    }

    public void setRequestTerminalVO(List<TerminalVO> requestTerminalVO)
    {
        RequestTerminalVO = requestTerminalVO;
    }
}

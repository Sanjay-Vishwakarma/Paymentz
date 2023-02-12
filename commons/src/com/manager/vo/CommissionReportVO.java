package com.manager.vo;


/**
 * Created by sandip on 10/14/2015.
 */
public class CommissionReportVO
{
    TerminalVO terminalVO;
    MerchantDetailsVO merchantDetailsVO;
    SettlementDateVO settlementDateVO;
    TransactionSummaryVO transactionSummaryVO;
    PartnerDetailsVO partnerDetailsVO;


    public TerminalVO getTerminalVO()
    {
        return terminalVO;
    }

    public void setTerminalVO(TerminalVO terminalVO)
    {
        this.terminalVO = terminalVO;
    }

    public MerchantDetailsVO getMerchantDetailsVO()
    {
        return merchantDetailsVO;
    }

    public void setMerchantDetailsVO(MerchantDetailsVO merchantDetailsVO)
    {
        this.merchantDetailsVO = merchantDetailsVO;
    }

    public SettlementDateVO getSettlementDateVO()
    {
        return settlementDateVO;
    }

    public void setSettlementDateVO(SettlementDateVO settlementDateVO)
    {
        this.settlementDateVO = settlementDateVO;
    }

    public TransactionSummaryVO getTransactionSummaryVO()
    {
        return transactionSummaryVO;
    }

    public void setTransactionSummaryVO(TransactionSummaryVO transactionSummaryVO)
    {
        this.transactionSummaryVO = transactionSummaryVO;
    }

    public PartnerDetailsVO getPartnerDetailsVO()
    {
        return partnerDetailsVO;
    }

    public void setPartnerDetailsVO(PartnerDetailsVO partnerDetailsVO)
    {
        this.partnerDetailsVO = partnerDetailsVO;
    }
}

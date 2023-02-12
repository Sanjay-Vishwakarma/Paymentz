package com.manager.vo.payoutVOs;

import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.RollingReserveDateVO;
import com.manager.vo.SettlementDateVO;
import com.manager.vo.TerminalVO;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 1/10/15
 * Time: 8:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantPayoutReportVO
{
    MerchantDetailsVO merchantDetailsVO;
    SettlementDateVO settlementDateVO;
    RollingReserveDateVO rollingReserveDateVO;

    List<ChargeDetailsVO> chargesDetailsVOsList;
    List<GrossChargeVO> grossTypeChargeVOList;
    List<ReserveGeneratedVO> reserveGeneratedVOList;
    List<ReserveRefundVO> reserveRefundVOsList;

    ReserveGeneratedVO reserveGeneratedVO;
    WireChargeVO wireChargeVO;
    TerminalVO terminalVO;

    String reportingDate;
    String currency;
    Boolean isWireReport;
    String setupFeeCoveredDateUpTo;

    Double merchantTotalProcessingAmount;
    Double merchantTotalChargesAmount;
    Double merchantRollingReserveAmount;
    Double merchantRollingReleasedAmount;
    Double merchantWirePaidAmount;
    Double merchantWireUnpaidAmount;
    Double merchantTotalBalanceAmount;
    Double merchantTotalFundedAmount;
    Double merchantTotalSettledAmount;
    Double merchantTotalDeclinedAmount;
    Double merchantTotalReversedAmount;
    Double merchantTotalChargebackAmount;

    long verifyOrderCount;
    long refundAlertCount;
    long retrivalRequestCount;

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

    public RollingReserveDateVO getRollingReserveDateVO()
    {
        return rollingReserveDateVO;
    }

    public void setRollingReserveDateVO(RollingReserveDateVO rollingReserveDateVO)
    {
        this.rollingReserveDateVO = rollingReserveDateVO;
    }

    public List<ChargeDetailsVO> getChargesDetailsVOsList()
    {
        return chargesDetailsVOsList;
    }

    public void setChargesDetailsVOsList(List<ChargeDetailsVO> chargesDetailsVOsList)
    {
        this.chargesDetailsVOsList = chargesDetailsVOsList;
    }

    public List<GrossChargeVO> getGrossTypeChargeVOList()
    {
        return grossTypeChargeVOList;
    }

    public List<ReserveGeneratedVO> getReserveGeneratedVOList()
    {
        return reserveGeneratedVOList;
    }

    public void setReserveGeneratedVOList(List<ReserveGeneratedVO> reserveGeneratedVOList)
    {
        this.reserveGeneratedVOList = reserveGeneratedVOList;
    }

    public void setGrossTypeChargeVOList(List<GrossChargeVO> grossTypeChargeVOList)
    {
        this.grossTypeChargeVOList = grossTypeChargeVOList;
    }

    public List<ReserveRefundVO> getReserveRefundVOsList()
    {
        return reserveRefundVOsList;
    }

    public void setReserveRefundVOsList(List<ReserveRefundVO> reserveRefundVOsList)
    {
        this.reserveRefundVOsList = reserveRefundVOsList;
    }

    public ReserveGeneratedVO getReserveGeneratedVO()
    {
        return reserveGeneratedVO;
    }

    public void setReserveGeneratedVO(ReserveGeneratedVO reserveGeneratedVO)
    {
        this.reserveGeneratedVO = reserveGeneratedVO;
    }

    public WireChargeVO getWireChargeVO()
    {
        return wireChargeVO;
    }

    public void setWireChargeVO(WireChargeVO wireChargeVO)
    {
        this.wireChargeVO = wireChargeVO;
    }

    public TerminalVO getTerminalVO()
    {
        return terminalVO;
    }

    public void setTerminalVO(TerminalVO terminalVO)
    {
        this.terminalVO = terminalVO;
    }

    public String getReportingDate()
    {
        return reportingDate;
    }

    public void setReportingDate(String reportingDate)
    {
        this.reportingDate = reportingDate;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public Double getMerchantTotalChargesAmount()
    {
        return merchantTotalChargesAmount;
    }

    public void setMerchantTotalChargesAmount(Double merchantTotalChargesAmount)
    {
        this.merchantTotalChargesAmount = merchantTotalChargesAmount;
    }

    public Double getMerchantRollingReserveAmount()
    {
        return merchantRollingReserveAmount;
    }

    public void setMerchantRollingReserveAmount(Double merchantRollingReserveAmount)
    {
        this.merchantRollingReserveAmount = merchantRollingReserveAmount;
    }

    public Double getMerchantRollingReleasedAmount()
    {
        return merchantRollingReleasedAmount;
    }

    public void setMerchantRollingReleasedAmount(Double merchantRollingReleasedAmount)
    {
        this.merchantRollingReleasedAmount = merchantRollingReleasedAmount;
    }

    public Double getMerchantWirePaidAmount()
    {
        return merchantWirePaidAmount;
    }

    public void setMerchantWirePaidAmount(Double merchantWirePaidAmount)
    {
        this.merchantWirePaidAmount = merchantWirePaidAmount;
    }

    public Double getMerchantWireUnpaidAmount()
    {
        return merchantWireUnpaidAmount;
    }

    public void setMerchantWireUnpaidAmount(Double merchantWireUnpaidAmount)
    {
        this.merchantWireUnpaidAmount = merchantWireUnpaidAmount;
    }

    public Double getMerchantTotalBalanceAmount()
    {
        return merchantTotalBalanceAmount;
    }

    public void setMerchantTotalBalanceAmount(Double merchantTotalBalanceAmount)
    {
        this.merchantTotalBalanceAmount = merchantTotalBalanceAmount;
    }

    public Double getMerchantTotalFundedAmount()
    {
        return merchantTotalFundedAmount;
    }

    public void setMerchantTotalFundedAmount(Double merchantTotalFundedAmount)
    {
        this.merchantTotalFundedAmount = merchantTotalFundedAmount;
    }

    public Double getMerchantTotalSettledAmount()
    {
        return merchantTotalSettledAmount;
    }

    public void setMerchantTotalSettledAmount(Double merchantTotalSettledAmount)
    {
        this.merchantTotalSettledAmount = merchantTotalSettledAmount;
    }

    public Double getMerchantTotalDeclinedAmount()
    {
        return merchantTotalDeclinedAmount;
    }

    public void setMerchantTotalDeclinedAmount(Double merchantTotalDeclinedAmount)
    {
        this.merchantTotalDeclinedAmount = merchantTotalDeclinedAmount;
    }

    public Double getMerchantTotalReversedAmount()
    {
        return merchantTotalReversedAmount;
    }

    public void setMerchantTotalReversedAmount(Double merchantTotalReversedAmount)
    {
        this.merchantTotalReversedAmount = merchantTotalReversedAmount;
    }

    public Double getMerchantTotalChargebackAmount()
    {
        return merchantTotalChargebackAmount;
    }

    public void setMerchantTotalChargebackAmount(Double merchantTotalChargebackAmount)
    {
        this.merchantTotalChargebackAmount = merchantTotalChargebackAmount;
    }

    public long getVerifyOrderCount()
    {
        return verifyOrderCount;
    }

    public void setVerifyOrderCount(long verifyOrderCount)
    {
        this.verifyOrderCount = verifyOrderCount;
    }

    public long getRefundAlertCount()
    {
        return refundAlertCount;
    }

    public void setRefundAlertCount(long refundAlertCount)
    {
        this.refundAlertCount = refundAlertCount;
    }

    public long getRetrivalRequestCount()
    {
        return retrivalRequestCount;
    }

    public void setRetrivalRequestCount(long retrivalRequestCount)
    {
        this.retrivalRequestCount = retrivalRequestCount;
    }

    public Double getMerchantTotalProcessingAmount()
    {
        return merchantTotalProcessingAmount;
    }

    public void setMerchantTotalProcessingAmount(Double merchantTotalProcessingAmount)
    {
        this.merchantTotalProcessingAmount = merchantTotalProcessingAmount;
    }

    public Boolean getWireReport()
    {
        return isWireReport;
    }

    public void setWireReport(Boolean wireReport)
    {
        isWireReport = wireReport;
    }

    public String getSetupFeeCoveredDateUpTo()
    {
        return setupFeeCoveredDateUpTo;
    }

    public void setSetupFeeCoveredDateUpTo(String setupFeeCoveredDateUpTo)
    {
        this.setupFeeCoveredDateUpTo = setupFeeCoveredDateUpTo;
    }
}

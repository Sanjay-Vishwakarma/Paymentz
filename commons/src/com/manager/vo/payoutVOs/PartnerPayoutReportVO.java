package com.manager.vo.payoutVOs;

import com.manager.vo.*;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 1/12/15
 * Time: 8:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerPayoutReportVO
{
    String reportingDate;
    String currency;
    boolean isWireReport;

    Double partnerTotalChargesAmount;
    Double partnerWirePaidAmount;
    Double partnerWireUnpaidAmount;
    Double partnerTotalBalanceAmount;
    Double partnerTotalFundedAmount;

    PartnerDetailsVO partnerDetailsVO;
    MerchantDetailsVO merchantDetailsVO;
    SettlementDateVO settlementDateVO;
    TerminalVO terminalVO;

    List<ChargeDetailsVO> chargesDetailsVOs;
    List<GrossChargeVO> grossTypeChargeVOList;
    List<SetupChargeVO> setupChargeVOList;
    WireChargeVO wireChargeVO;
    HashMap<String,List<ChargeDetailsVO>> stringListHashMap;

    HashMap<String,HashMap<String,List<ChargeDetailsVO>>> stringHashMapHashMap;

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

    public Double getPartnerTotalChargesAmount()
    {
        return partnerTotalChargesAmount;
    }

    public void setPartnerTotalChargesAmount(Double partnerTotalChargesAmount)
    {
        this.partnerTotalChargesAmount = partnerTotalChargesAmount;
    }

    public Double getPartnerWirePaidAmount()
    {
        return partnerWirePaidAmount;
    }

    public void setPartnerWirePaidAmount(Double partnerWirePaidAmount)
    {
        this.partnerWirePaidAmount = partnerWirePaidAmount;
    }

    public Double getPartnerWireUnpaidAmount()
    {
        return partnerWireUnpaidAmount;
    }

    public void setPartnerWireUnpaidAmount(Double partnerWireUnpaidAmount)
    {
        this.partnerWireUnpaidAmount = partnerWireUnpaidAmount;
    }

    public Double getPartnerTotalBalanceAmount()
    {
        return partnerTotalBalanceAmount;
    }

    public void setPartnerTotalBalanceAmount(Double partnerTotalBalanceAmount)
    {
        this.partnerTotalBalanceAmount = partnerTotalBalanceAmount;
    }

    public Double getPartnerTotalFundedAmount()
    {
        return partnerTotalFundedAmount;
    }

    public void setPartnerTotalFundedAmount(Double partnerTotalFundedAmount)
    {
        this.partnerTotalFundedAmount = partnerTotalFundedAmount;
    }

    public PartnerDetailsVO getPartnerDetailsVO()
    {
        return partnerDetailsVO;
    }

    public void setPartnerDetailsVO(PartnerDetailsVO partnerDetailsVO)
    {
        this.partnerDetailsVO = partnerDetailsVO;
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

    public TerminalVO getTerminalVO()
    {
        return terminalVO;
    }

    public void setTerminalVO(TerminalVO terminalVO)
    {
        this.terminalVO = terminalVO;
    }

    public List<ChargeDetailsVO> getChargesDetailsVOs()
    {
        return chargesDetailsVOs;
    }

    public void setChargesDetailsVOs(List<ChargeDetailsVO> chargesDetailsVOs)
    {
        this.chargesDetailsVOs = chargesDetailsVOs;
    }

    public List<GrossChargeVO> getGrossTypeChargeVOList()
    {
        return grossTypeChargeVOList;
    }

    public void setGrossTypeChargeVOList(List<GrossChargeVO> grossTypeChargeVOList)
    {
        this.grossTypeChargeVOList = grossTypeChargeVOList;
    }

    public List<SetupChargeVO> getSetupChargeVOList()
    {
        return setupChargeVOList;
    }

    public void setSetupChargeVOList(List<SetupChargeVO> setupChargeVOList)
    {
        this.setupChargeVOList = setupChargeVOList;
    }

    public WireChargeVO getWireChargeVO()
    {
        return wireChargeVO;
    }

    public void setWireChargeVO(WireChargeVO wireChargeVO)
    {
        this.wireChargeVO = wireChargeVO;
    }

    public HashMap<String, List<ChargeDetailsVO>> getStringListHashMap()
    {
        return stringListHashMap;
    }

    public void setStringListHashMap(HashMap<String, List<ChargeDetailsVO>> stringListHashMap)
    {
        this.stringListHashMap = stringListHashMap;
    }

    public HashMap<String, HashMap<String, List<ChargeDetailsVO>>> getStringHashMapHashMap()
    {
        return stringHashMapHashMap;
    }

    public void setStringHashMapHashMap(HashMap<String, HashMap<String, List<ChargeDetailsVO>>> stringHashMapHashMap)
    {
        this.stringHashMapHashMap = stringHashMapHashMap;
    }

    public boolean isWireReport()
    {
        return isWireReport;
    }

    public void setWireReport(boolean wireReport)
    {
        isWireReport = wireReport;
    }
}

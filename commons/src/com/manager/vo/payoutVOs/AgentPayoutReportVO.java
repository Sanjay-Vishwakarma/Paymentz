package com.manager.vo.payoutVOs;

import com.manager.vo.*;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 12/12/14
 * Time: 5:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentPayoutReportVO
{
   String reportingDate;
   String currency;
   boolean isWireReport;

   Double agentTotalChargesAmount;
   Double agentWirePaidAmount;
   Double agentWireUnpaidAmount;
   Double agentTotalBalanceAmount;
   Double agentTotalFundedAmount;

   AgentDetailsVO agentDetailsVO;
   PartnerDetailsVO partnerDetailsVO;
   MerchantDetailsVO merchantDetailsVO;
   SettlementDateVO settlementDateVO;
   TerminalVO terminalVO;

   List<ChargeDetailsVO> chargesDetailsVOs;
   List<GrossChargeVO> grossTypeChargeVOList;
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
    public boolean isWireReport()
    {
        return isWireReport;
    }

    public void setWireReport(boolean wireReport)
    {
        isWireReport = wireReport;
    }

    public Double getAgentTotalChargesAmount()
    {
        return agentTotalChargesAmount;
    }

    public void setAgentTotalChargesAmount(Double agentTotalChargesAmount)
    {
        this.agentTotalChargesAmount = agentTotalChargesAmount;
    }

    public Double getAgentWirePaidAmount()
    {
        return agentWirePaidAmount;
    }

    public void setAgentWirePaidAmount(Double agentWirePaidAmount)
    {
        this.agentWirePaidAmount = agentWirePaidAmount;
    }

    public Double getAgentWireUnpaidAmount()
    {
        return agentWireUnpaidAmount;
    }

    public void setAgentWireUnpaidAmount(Double agentWireUnpaidAmount)
    {
        this.agentWireUnpaidAmount = agentWireUnpaidAmount;
    }

    public Double getAgentTotalBalanceAmount()
    {
        return agentTotalBalanceAmount;
    }

    public void setAgentTotalBalanceAmount(Double agentTotalBalanceAmount)
    {
        this.agentTotalBalanceAmount = agentTotalBalanceAmount;
    }

    public Double getAgentTotalFundedAmount()
    {
        return agentTotalFundedAmount;
    }

    public void setAgentTotalFundedAmount(Double agentTotalFundedAmount)
    {
        this.agentTotalFundedAmount = agentTotalFundedAmount;
    }

    public AgentDetailsVO getAgentDetailsVO()
    {
        return agentDetailsVO;
    }

    public void setAgentDetailsVO(AgentDetailsVO agentDetailsVO)
    {
        this.agentDetailsVO = agentDetailsVO;
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

    public WireChargeVO getWireChargeVO()
    {
        return wireChargeVO;
    }

    public void setWireChargeVO(WireChargeVO wireChargeVO)
    {
        this.wireChargeVO = wireChargeVO;
    }

    public List<GrossChargeVO> getGrossTypeChargeVOList()
    {
        return grossTypeChargeVOList;
    }

    public void setGrossTypeChargeVOList(List<GrossChargeVO> grossTypeChargeVOList)
    {
        this.grossTypeChargeVOList = grossTypeChargeVOList;
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

    public PartnerDetailsVO getPartnerDetailsVO()
    {
        return partnerDetailsVO;
    }

    public void setPartnerDetailsVO(PartnerDetailsVO partnerDetailsVO)
    {
        this.partnerDetailsVO = partnerDetailsVO;
    }
}

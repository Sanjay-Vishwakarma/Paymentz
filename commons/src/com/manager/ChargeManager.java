package com.manager;

import com.directi.pg.core.GatewayAccount;
import com.manager.dao.ChargesDAO;
import com.manager.vo.AgentCommissionVO;
import com.manager.vo.ChargeVO;
import com.manager.vo.PartnerCommissionVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.payoutVOs.ChargeMasterVO;
import com.manager.vo.payoutVOs.ChargeVersionVO;
import com.manager.vo.payoutVOs.MerchantRandomChargesVO;
import com.manager.vo.payoutVOs.WLPartnerCommissionVO;
import com.payment.exceptionHandler.PZDBViolationException;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: sandip
 * Date: 26/7/14
 * Time: 8:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChargeManager
{
    private ChargesDAO chargesDAO=new ChargesDAO();

    public String addNewBusinessCharge(ChargeMasterVO chargeMasterVO)throws PZDBViolationException
    {
        return chargesDAO.addNewBusinessCharge(chargeMasterVO);
    }
    public boolean updateBusinessCharge(ChargeMasterVO chargeMasterVO)throws PZDBViolationException
    {
        return chargesDAO.updateBusinessCharge(chargeMasterVO);
    }
    public ChargeMasterVO getBusinessChargeDetails(String businessChargeId)throws PZDBViolationException
    {
        return chargesDAO.getBusinessChargeDetails(businessChargeId);
    }
    public String getMerchantChargeVersionRate(String memberAccountChargeMappingId,String reportingDate)
    {
        return chargesDAO.getMerchantChargeVersionRate(memberAccountChargeMappingId, reportingDate);
    }
    public String getAgentChargeVersionRate(String memberAccountChargeMappingId,String reportingDate)
    {
        return chargesDAO.getAgentChargeVersionRate(memberAccountChargeMappingId, reportingDate);
    }
    public String getBankAgentChargeVersionRate(String gatewayAccountChargeMappingId,String reportingDate)
    {
        return chargesDAO.getAgentChargeVersionRate(gatewayAccountChargeMappingId, reportingDate);
    }
    public String getPartnerChargeVersionRate(String memberAccountChargeMappingId,String reportingDate)
    {
        return chargesDAO.getPartnerChargeVersionRate(memberAccountChargeMappingId, reportingDate);
    }
    public String getBankPartnerChargeVersionRate(String gatewayAccountChargeMappingId,String reportingDate)
    {
        return chargesDAO.getBankPartnerChargeVersionRate(gatewayAccountChargeMappingId, reportingDate);
    }
    public List<ChargeVO> getGatewayAccountCharges(GatewayAccount gatewayAccount)throws PZDBViolationException
    {
        return chargesDAO.getGatewayAccountCharges(gatewayAccount);
    }
    /*public String addNewChargeOnMemberTerminal(ChargeVO chargeVO)
    {
       return "";//definition is pending
    }*/
    public int addNewChargeOnGatewayAccount(ChargeVO chargeVO)
    {
        return chargesDAO.addNewChargeOnGatewayAccount(chargeVO);
    }
    public String applyCommissionOnWLPartner(WLPartnerCommissionVO wlPartnerCommissionVO)throws PZDBViolationException
    {
        return chargesDAO.applyCommissionOnWLPartner(wlPartnerCommissionVO);
    }
    public boolean updateWLPartnerCommissionValue(WLPartnerCommissionVO wlPartnerCommissionVO)throws PZDBViolationException
    {
        return chargesDAO.updateWLPartnerCommissionValue(wlPartnerCommissionVO);
    }
    public WLPartnerCommissionVO getWLPartnerCommissionDetails(String commissionId)throws PZDBViolationException
    {
        return chargesDAO.getWLPartnerCommissionDetails(commissionId);
    }

    public boolean checkSequenceNoAvailabilityWLPartner(WLPartnerCommissionVO wlPartnerCommissionVO)
    {
        return  chargesDAO.checkSequenceNoAvailabilityWLPartner(wlPartnerCommissionVO);
    }

    public boolean checkCommissionAvailabilityWLPartner(WLPartnerCommissionVO wlPartnerCommissionVO)
    {
        return  chargesDAO.checkCommissionAvailabilityWLPartner(wlPartnerCommissionVO);
    }

    public boolean checkSequenceNoAvailability(ChargeVO chargeVO)
    {
        return  chargesDAO.checkSequenceNoAvailability(chargeVO);
    }
    public boolean checkMemberSequenceNoAvailability(ChargeVO chargeVO)
    {
        return  chargesDAO.checkMemberSequenceNoAvailability(chargeVO);
    }
    public boolean isChargeApplied(ChargeVO chargeVO)
    {
        return chargesDAO.checkChargeAvailability(chargeVO);
    }
    /*public String createChargeVersionOnMemberTerminal(ChargeVO chargeVO)
    {
       return "";//definition is pending
    }*/
    public String createChargeVersionOnGatewayAccount(ChargeVersionVO chargeVersionVO)
    {
        return chargesDAO.createChargeVersionOnGatewayAccount(chargeVersionVO);
    }

    public String updateChargeVersionOnGatewayAccount(ChargeVersionVO chargeVersionVO)
    {
        return chargesDAO.updateChargeVersionOnGatewayAccount(chargeVersionVO);
    }

    public String addMerchantRandomCharge(MerchantRandomChargesVO merchantRandomChargesVO) throws PZDBViolationException
    {
        return chargesDAO.addMerchantRandomCharge(merchantRandomChargesVO);
    }
    public MerchantRandomChargesVO getMerchantRandomChargeDetails(String merchantrdmchargeid) throws PZDBViolationException
    {
        return chargesDAO.getMerchantRandomChargeDetails(merchantrdmchargeid);
    }
    public String updateMerchantRandomChargeDetails(MerchantRandomChargesVO merchantRandomChargesVO)throws PZDBViolationException
    {
        return chargesDAO.updateMerchantRandomChargeDetails(merchantRandomChargesVO);
    }
    public List<MerchantRandomChargesVO> getMerchantRandomChargesList(String bankwireId,String memberId,String terminalId)throws PZDBViolationException
    {
        return chargesDAO.getMerchantRandomChargesList(bankwireId, memberId, terminalId);
    }

    public HashMap<String,MerchantRandomChargesVO> getMerchantRandomChargesListVO(String bankwireId,String memberId,String terminalId)throws PZDBViolationException
    {
        return chargesDAO.getMerchantRandomChargesListVO(bankwireId, memberId, terminalId);
    }
    public String addNewPartnerCommission(PartnerCommissionVO partnerCommissionVO)throws PZDBViolationException
    {
        return chargesDAO.addNewPartnerCommission(partnerCommissionVO);
    }
    public List<PartnerCommissionVO> getPartnerCommissionOnTerminal(String partnerId,String memberId,String terminalId)throws PZDBViolationException
    {
        return chargesDAO.getPartnerCommissionOnTerminal(partnerId, memberId, terminalId);
    }
    public List<AgentCommissionVO> getAgentCommissionOnTerminal(String agentId,String memberId,String terminalId)throws PZDBViolationException
    {
        return chargesDAO.getAgentCommissionOnTerminal(agentId, memberId, terminalId);
    }
    public boolean updatePartnerCommissionValue(PartnerCommissionVO partnerCommissionVO)throws PZDBViolationException
    {
        return chargesDAO.updatePartnerCommissionValue(partnerCommissionVO);
    }
    public PartnerCommissionVO getPartnerCommissionDetails(String commissionId)throws PZDBViolationException
    {
        return chargesDAO.getPartnerCommissionDetails(commissionId);
    }
    public String addNewAgentCommission(AgentCommissionVO agentCommissionVO) throws PZDBViolationException
    {
        return chargesDAO.addNewAgentCommission(agentCommissionVO);
    }
    public boolean updateAgentCommissionValue(AgentCommissionVO agentCommissionVO)throws PZDBViolationException
    {
        return chargesDAO.updateAgentCommissionValue(agentCommissionVO);
    }
    public AgentCommissionVO getAgentCommissionDetails(String commissionId)throws PZDBViolationException
    {
        return chargesDAO.getAgentCommissionDetails(commissionId);
    }
    public List<ChargeVO> getTobeDebitedAgentCharges(TerminalVO terminalVO)throws PZDBViolationException
    {
        return chargesDAO.getTobeDebitedAgentCharges(terminalVO);
    }
    public List<ChargeVO> getTobeDebitedPartnerCharges(TerminalVO terminalVO)throws PZDBViolationException
    {
        return chargesDAO.getTobeDebitedPartnerCharges(terminalVO);
    }
    public List<ChargeVO> getListOfCharges()throws PZDBViolationException
    {
        return chargesDAO.getListOfCharges();
    }
    public Hashtable getListOfGatewayAccountCharges(String pgtypeid,String currency,String accountId, String chargeId, String gateway, int start, int end, String actionExecutorId,String actionExecutorName)throws  PZDBViolationException
    {
        return chargesDAO.getListOfGatewayAccountCharges(pgtypeid,currency,accountId, chargeId, gateway, start,end, actionExecutorId,actionExecutorName);
    }

    public HashMap getListOfGatewayAccountCharge(String mappingId)throws  PZDBViolationException
    {
        return chargesDAO.getListOfGatewayAccountCharge(mappingId);
    }

    public HashMap getGatewayAccountCharges(String mappingId)throws  PZDBViolationException
    {
        return chargesDAO.getGatewayAccountCharges(mappingId);
    }

    public HashMap getGatewayAccountChargeDate(String mappingId)throws  PZDBViolationException
    {
        return chargesDAO.getGatewayAccountChargeDate(mappingId);
    }

    public Hashtable getMemberAccountCharges(String memberId, String accountId, String payModeId, String cardTypeId, String chargeId, String mChargeValue, String aChargeValue, String pChargeValue, String chargeType, String terminalid, String gateway, String currency, int start, int end, String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        return chargesDAO.getMemberAccountCharges(memberId, accountId, payModeId, cardTypeId, chargeId, mChargeValue, aChargeValue, pChargeValue, chargeType, terminalid, gateway, currency, start, end,actionExecutorId,actionExecutorName);
    }
    public Hashtable getMemberAccountChargesForExport(String memberId, String accountId, String chargeId, String chargeType, String terminalid) throws PZDBViolationException
    {
        return chargesDAO.getMemberAccountChargesForExport(memberId, accountId,chargeId, chargeType, terminalid);
    }

    public boolean isChargeAppliedOnMerchantAccount(ChargeVO chargeVO) throws Exception
    {
        return chargesDAO.isChargeAppliedOnMerchantAccount(chargeVO);
    }
    public void deleteChargeDetails(ChargeVO chargeVO) throws Exception
    {
        chargesDAO.deleteChargeDetails(chargeVO);
    }
    public void deleteCommissionDetails(AgentCommissionVO chargeVO) throws Exception
    {
        chargesDAO.deleteCommissionDetails(chargeVO);
    }
    public ChargeVO getMerchantChargeDetails(String mappingId)throws PZDBViolationException
    {
        return chargesDAO.getMerchantChargeDetails(mappingId);
    }
    public boolean updateMerchantCharge(ChargeVO chargeVO) throws PZDBViolationException
    {
        return chargesDAO.updateMerchantCharge(chargeVO);
    }
    public List<ChargeVO> getDynamicChargesAsPerTerminal(TerminalVO terminalVO)throws PZDBViolationException
    {
        return chargesDAO.getDynamicChargesAsPerTerminal(terminalVO);
    }
    public ChargeVO getChargeDetails(String mappingId)throws PZDBViolationException
    {
        return chargesDAO.getChargeDetails(mappingId);
    }

    public HashMap getChargeInfo(String mappingId) throws PZDBViolationException
    {
        return chargesDAO.getChargeInfo(mappingId);
    }
    public List<ChargeVO> getListOfTerminalCharges(String memberId,String terminalId)throws PZDBViolationException
    {
        return chargesDAO.getListOfTerminalCharges(memberId,terminalId);
    }

    public boolean Negativebalanceshow(String chargeid)throws PZDBViolationException
    {
        return chargesDAO.Negativebalanceshow(chargeid);
    }
    public boolean checkAgentSequenceNoAvailability(AgentCommissionVO agentCommissionVO)
    {
        return chargesDAO.checkAgentSequenceNoAvailability(agentCommissionVO);
    }
    public List<AgentCommissionVO> getListOfTerminalChargesAgent(String memberId,String agentid,String terminalId) throws PZDBViolationException
    {
        return chargesDAO.getListOfTerminalChargesAgent(memberId, agentid, terminalId);
    }
    public String insertNewAgentCommission(AgentCommissionVO commissionVO) throws PZDBViolationException
    {
        return chargesDAO.insertNewAgentCommission(commissionVO);
    }
}
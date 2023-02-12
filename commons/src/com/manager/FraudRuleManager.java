package com.manager;
import com.directi.pg.Logger;
import com.manager.vo.fraudruleconfVOs.FraudRuleChangeIntimationVO;
import com.manager.vo.fraudruleconfVOs.FraudRuleChangeTrackerVO;
import com.manager.dao.FraudRuleDAO;
import com.manager.vo.PaginationVO;
import com.manager.vo.fraudruleconfVOs.*;
import com.payment.exceptionHandler.PZDBViolationException;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 7/11/15
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class FraudRuleManager
{
    Logger logger=new Logger(FraudRuleManager.class.getName());
    FraudRuleDAO ruleDAO=new FraudRuleDAO();

    public List<RuleMasterVO> getSuBAccountLevelRiskRuleList(String fsSubAccountId, PaginationVO paginationVO)throws PZDBViolationException
    {
        return ruleDAO.getSuBAccountLevelRiskRuleList(fsSubAccountId, paginationVO);
    }
    public String updateMemberSubAccountRuleMapping(String score,String status,String ruleId,String fsSubAccountId)throws PZDBViolationException
    {
        return ruleDAO.updateMemberSubAccountRuleMapping(score, status, ruleId, fsSubAccountId);
    }
    public FraudSystemSubAccountVO getFraudServiceSubAccountDetails(String subAccountId)throws PZDBViolationException
    {
        return ruleDAO.getFraudServiceSubAccountDetails(subAccountId);
    }
    public String addNewFraudRule(RuleMasterVO ruleMasterVO)throws PZDBViolationException
    {
        return ruleDAO.addNewFraudRule(ruleMasterVO);
    }
    public String addNewFraudSystemAccount(FraudSystemAccountVO accountVO)throws PZDBViolationException
    {
        return ruleDAO.addNewFraudSystemAccount(accountVO);
    }
    public String allocateNewFraudSystemAccount(String partnerId, String fsAccountId, String isActive)throws PZDBViolationException
    {
        return ruleDAO.allocateNewFraudSystemAccount(partnerId, fsAccountId, isActive);
    }
    public String addNewFraudSystemSubAccount(FraudSystemSubAccountVO subAccountVO)throws PZDBViolationException
    {
        return ruleDAO.addNewFraudSystemSubAccount(subAccountVO);
    }

    public String DeleteFraudSystemAccount(String Partnerid, String accountid)throws PZDBViolationException
    {
        return ruleDAO.DeleteFraudSystemAccount(Partnerid,accountid);
    }

    public String addNewFraudSubAccountRuleMap(FraudSystemSubAccountRuleVO fraudSystemSubAccountRuleVO)throws PZDBViolationException
    {
        return ruleDAO.addNewFraudSubAccountRuleMap(fraudSystemSubAccountRuleVO);
    }

    public String addNewFraudAccountRuleMap(FraudSystemAccountRuleVO fraudSystemAccountRuleVO)throws PZDBViolationException
    {
        return ruleDAO.addNewFraudAccountRuleMap(fraudSystemAccountRuleVO);
    }

    public String addNewMerchantFraudAccount (MerchantFraudAccountVO fraudAccountVO) throws PZDBViolationException
    {
        return ruleDAO.addNewMerchantFraudAccount(fraudAccountVO);
    }
    public boolean isFraudRuleUnique(String ruleName)throws PZDBViolationException
    {
        return ruleDAO.isFraudRuleUnique(ruleName);
    }
    public boolean isFraudAccountRuleMapUnique (String fsaccountid, String ruleid) throws PZDBViolationException
    {
        return ruleDAO.isFraudAccountRuleMapUnique(fsaccountid, ruleid);
    }
    public boolean isFraudSubAccountRuleMapUnique(String fsSubAccountId, String ruleid) throws PZDBViolationException
    {
        return ruleDAO.isFraudSubAccountRuleMapUnique(fsSubAccountId, ruleid);
    }
    public boolean isFraudSystemAccountUnique (String accountname) throws PZDBViolationException
    {
        return ruleDAO.isFraudSystemAccountUnique(accountname);
    }
    public boolean isAlreadyAllocated(String partnerId, String accountName) throws PZDBViolationException
    {
        return ruleDAO.isAlreadyAllocated(partnerId, accountName);
    }
    public boolean isFraudSystemSubAccountUnique(String fsaccountid, String subaccountname) throws PZDBViolationException
    {
        return  ruleDAO.isFraudSystemSubAccountUnique(fsaccountid, subaccountname);
    }
    public boolean isFraudSystemSubAccountUnique(String subAccountName) throws PZDBViolationException
    {
        return  ruleDAO.isFraudSystemSubAccountUnique(subAccountName);
    }
    public boolean isMerchantFraudAccountUnique(String memberid, String fssubaccountid) throws PZDBViolationException
    {
        return ruleDAO.isMerchantFraudAccountUnique(memberid, fssubaccountid);
    }
    public boolean isMerchantFraudAccountAvailable(String memberId,String subaccountname) throws PZDBViolationException
    {
        return ruleDAO.isMerchantFraudAccountAvailable(memberId,subaccountname);
    }
    public boolean isMerchantInternalFraudAccountAvailable(String memberId,String subaccountname) throws PZDBViolationException
    {
        return ruleDAO.isMerchantInternalFraudAccountAvailable(memberId,subaccountname);
    }
    public FraudSystemAccountRuleVO getAccountLevelFraudRuleDetails(String fsAccountId,String ruleId)throws PZDBViolationException
    {
        return ruleDAO.getAccountLevelFraudRuleDetails(fsAccountId, ruleId);
    }
    public String updateAccountLevelFraudRule(FraudSystemAccountRuleVO fraudSystemAccountRuleVO)throws PZDBViolationException
    {
        return ruleDAO.updateAccountLevelFraudRule(fraudSystemAccountRuleVO);
    }
    public String updateSubAccountLevelFraudRule(FraudSystemSubAccountRuleVO fraudSystemSubAccountRuleVO)throws PZDBViolationException
    {
        return ruleDAO.updateSubAccountLevelFraudRule(fraudSystemSubAccountRuleVO);
    }
    public FraudSystemSubAccountRuleVO getSubAccountLevelFraudRuleDetails(String fssubaccountid,String ruleId) throws  PZDBViolationException
    {
        return ruleDAO.getSubAccountLevelFraudRuleDetails(fssubaccountid, ruleId);
    }
    public FraudSystemAccountVO getFraudAccountDetails (String fsAccountId) throws PZDBViolationException
    {
        return ruleDAO.getFraudAccountDetails(fsAccountId);
    }
    public String updateFraudAccount (FraudSystemAccountVO fraudSystemAccountVO) throws PZDBViolationException
    {
        return ruleDAO.updateFraudAccount(fraudSystemAccountVO);
    }
    public FraudSystemSubAccountVO getFraudSubAccountDetails(String fsSubAccountId, String fsaccountid) throws PZDBViolationException
    {
        return ruleDAO.getFraudSubAccountDetails(fsSubAccountId, fsaccountid);
    }
    public String updateFraudSubAccount (FraudSystemSubAccountVO subAccountVO) throws PZDBViolationException
    {
        return ruleDAO.updateFraudSubAccount(subAccountVO);
    }
    public MerchantFraudAccountVO getMerchantFraudAccountDetails(String merchantfraudserviceid, String fssubaccountid) throws PZDBViolationException
    {
        return ruleDAO.getMerchantFraudAccountDetails(merchantfraudserviceid, fssubaccountid);
    }
    public String updateMerchantFraudAccount (MerchantFraudAccountVO fraudAccountVO) throws PZDBViolationException
    {
        return ruleDAO.updateMerchantFraudAccount(fraudAccountVO);
    }
    public String updateMerchantFraudAccountFROMPSP (MerchantFraudAccountVO fraudAccountVO) throws PZDBViolationException
    {
        return ruleDAO.updateMerchantFraudAccountFROMPSP(fraudAccountVO);
    }
    public List<RuleMasterVO> getAccountLevelRiskRuleList(String fsAccountId)throws PZDBViolationException
    {
        return ruleDAO.getAccountLevelRiskRuleList(fsAccountId);
    }
    public String updateAccountRuleMapping (String score, String status, String fsaccountid, String ruleid) throws PZDBViolationException
    {
        return ruleDAO.updateAccountRuleMapping(score, status, fsaccountid,ruleid);
    }
    public int createFraudRuleChangeIntimation(FraudRuleChangeIntimationVO intimationVO)throws PZDBViolationException
    {
        return ruleDAO.createFraudRuleChangeIntimation(intimationVO);
    }
    public String executeFraudRuleChangeTracker(FraudRuleChangeTrackerVO changeTrackerVO)throws PZDBViolationException
    {
        return ruleDAO.executeFraudRuleChangeTracker(changeTrackerVO);
    }
    public String handleSubAccountFraudRuleChangeRequest(SubAccountRuleChangeRequestVO changeRequestVO)throws PZDBViolationException
    {
        return ruleDAO.handleSubAccountFraudRuleChangeRequest(changeRequestVO);
    }
    public String handleAccountFraudRuleChangeRequest(AccountRuleChangeRequestVO changeRequestVO)throws PZDBViolationException
    {
        return ruleDAO.handleAccountFraudRuleChangeRequest(changeRequestVO);
    }
    public List<RuleMasterVO> getAccountLevelRiskRuleList(String fsAccountId,PaginationVO paginationVO)throws PZDBViolationException
    {
        return ruleDAO.getAccountLevelRiskRuleList(fsAccountId,paginationVO);
    }
    public List<FraudRuleChangeIntimationVO> getFraudRuleChargeIntimation(String fsaccountid,String status, PaginationVO paginationVO)throws PZDBViolationException
    {
        return ruleDAO.getFraudRuleChargeIntimation(fsaccountid,status,paginationVO);
    }
    public List<FraudRuleChangeTrackerVO> getFraudRuleChangeTracker(String[] intimationids)throws PZDBViolationException
    {
        return ruleDAO.getFraudRuleChangeTracker(intimationids);
    }
    public String updateRuleIntimationStatus (String intimationid) throws PZDBViolationException
    {
        return ruleDAO.updateRuleIntimationStatus(intimationid);
    }
    public String isStatusIntimated ( String[] intimationids) throws PZDBViolationException
    {
        return ruleDAO.isStatusIntimated(intimationids);
    }
    public boolean isMerchantUnique(String mid)throws  PZDBViolationException
    {
        return ruleDAO.isMerchantUnique(mid);
    }
    public boolean isFraudAccountUnique(String fssubaccountid)throws  PZDBViolationException
    {
        return ruleDAO.isFraudAccountUnique(fssubaccountid);
    }
    public String addNewFraudSystemSubAccountFROMPSP(FraudSystemSubAccountVO subAccountVO)throws PZDBViolationException
    {
        return ruleDAO.addNewFraudSystemSubAccountFROMPSP(subAccountVO);
    }
    public List<RuleMasterVO> getInternalSubLevelRiskRuleList(String merchantid)throws PZDBViolationException
    {
        return ruleDAO.getInternalSubLevelRiskRuleList(merchantid);
    }
    public List<RuleMasterVO> getInternalAccountLevelRiskRuleList(String merchantid)throws PZDBViolationException
    {
        return ruleDAO.getInternalAccountLevelRiskRuleList(merchantid);
    }
   /* public List<RuleMasterVO> getPartnerInternalLevelRiskRuleList(String fssubaccountid)throws PZDBViolationException
    {
        return ruleDAO.getPartnerInternalLevelRiskRuleList(fssubaccountid);
    }*/
    public List<RuleMasterVO> getPartnerInternalLevelRiskRuleList(String merchantid)throws PZDBViolationException
    {
        return ruleDAO.getPartnerInternalLevelRiskRuleList(merchantid);
    }

    public FraudSystemSubAccountVO getFraudServicePartnerAccountDetails(String fsaccountid) throws PZDBViolationException
    {
        return ruleDAO.getFraudServicePartnerAccountDetails(fsaccountid);
    }

    public List<RuleMasterVO> getPartnerInternalAccountLevelRiskRuleList(String partnerId) throws PZDBViolationException
    {
        return ruleDAO.getPartnerInternalAccountLevelRiskRuleList(partnerId);
    }

    public String getUpdateInternalAccountLevelRiskRuleList(List<RuleMasterVO> list,String partnerid) throws PZDBViolationException
    {
        return ruleDAO.getUpdateInternalAccountLevelRiskRuleList(list,partnerid);
    }

    public String getUpdateInternalSubAccountLevelRiskRuleList(List<RuleMasterVO> list,String merchantid) throws PZDBViolationException
    {
        return ruleDAO.getUpdateInternalSubAccountLevelRiskRuleList(list, merchantid);
    }

    public Set getCardType()throws PZDBViolationException
    {
        return  ruleDAO.getCardType();
    }
}
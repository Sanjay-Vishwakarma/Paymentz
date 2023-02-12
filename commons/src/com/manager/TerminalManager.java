package com.manager;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayAccountService;
import com.manager.dao.TerminalDAO;
import com.manager.vo.TerminalVO;
import com.manager.vo.merchantmonitoring.TerminalLimitsVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 26/7/14
 * Time: 8:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class TerminalManager
{
    Logger logger = new Logger(TerminalManager.class.getName());
    TerminalDAO terminalDAO = new TerminalDAO();

    Functions functions = new Functions();

    public List<TerminalVO> getTerminalsByMerchantId(String merchantId) throws PZDBViolationException
    {
        return terminalDAO.getTerminalsByMerchantId(merchantId);
    }
    public List<TerminalVO> getTerminalsByMerchantId(String merchantId, String role) throws PZDBViolationException
    {
        return terminalDAO.getTerminalsByMerchantId(merchantId,role);
    }

    public String getCardType(String cardtypeid) throws PZDBViolationException
    {
        return terminalDAO.getCardType(cardtypeid);
    }

    public String getPaymentType(String paymodeid) throws PZDBViolationException
    {
        return terminalDAO.getPaymentType(paymodeid);
    }
    public List<TerminalVO> getTerminalsByAccountID(String accountId, String cardtypeid,String paymodid) throws PZDBViolationException
    {
        return terminalDAO.getTerminalsByAccountID(accountId,cardtypeid, paymodid);
    }
    public List<TerminalVO> getTerminalsByAccountIdForAgent(String accountId) throws PZDBViolationException
    {
        return terminalDAO.getTerminalsByAccountIdForAgent(accountId);
    }
    public List<TerminalVO> getTerminalDetailsForAgent(String agentId,String startDate,String endDate) throws PZDBViolationException
    {
        return terminalDAO.getTerminalDetailsForAgent(agentId,startDate,endDate);
    }
    public List<TerminalVO> getFilterListBasedOnInput(String agentId,String memberId,String accountId) throws PZDBViolationException
    {
        return terminalDAO.getFilterListBasedOnInput(agentId,memberId,accountId);
    }
    public List<TerminalVO> getSettledTerminalList(String bankWireId,String cardtypeid, String paymodid) throws PZDBViolationException
    {
        return terminalDAO.getSettledTerminalList(bankWireId,cardtypeid, paymodid);
    }
    public List<TerminalVO> getSettledTerminalListForAgent(String bankWireId) throws PZDBViolationException
    {
        return terminalDAO.getSettledTerminalListForAgent(bankWireId);
    }
    public List<TerminalVO> getSettledTerminalListForConsolidatedAgent(String agentId,String startDate,String endDate) throws PZDBViolationException
    {
        return terminalDAO.getSettledTerminalListForConsolidatedAgent(agentId,startDate,endDate);
    }
    public List<TerminalVO> getTerminalsByMemberAccountID(String memberId,String accountId) //sandip
    {
        return terminalDAO.getTerminalsByMemberAccountID(memberId, accountId);
    }
    public List<TerminalVO> getTerminalsByMemberAccountIdForPayoutReport(String memberId,String accountId) //sandip
    {
        return terminalDAO.getTerminalsByMemberAccountIdForPayoutReport(memberId,accountId);
    }
    public TerminalVO getTerminalsByMemberAccountIdForPayoutReportRequest(String memberId,String accountId,String terminalId)
    {
        return terminalDAO.getTerminalsByMemberAccountIdForPayoutReport(memberId,accountId,terminalId);
    }
    public TerminalVO getTerminalByTerminalId(String terminalId) throws PZDBViolationException
    {
        return terminalDAO.getTerminalByTerminalId(terminalId);
    }
    public TerminalVO getActiveInActiveTerminalInfo(String terminalId) throws PZDBViolationException
    {
        return terminalDAO.getActiveInActiveTerminalInfo(terminalId);
    }
    public boolean isChargesMappedTerminal(String terminalId) throws PZDBViolationException
    {
        return terminalDAO.isChargesMappedWithTerminal(terminalId);
    }

    public HashMap<String,String> getGateWayHash(TerminalVO terminalVO) throws PZDBViolationException
    {
        if(functions.isValueNull(terminalVO.getAccountId()))
        {
            HashMap<String,String> gatewayHashForAccountId = new HashMap<String, String>();
            String gateway = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId()).getGateway();
            String displayname = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId()).getDisplayName();
            gatewayHashForAccountId.put(gateway,displayname);
            return gatewayHashForAccountId;
        }
        else
        {
            return terminalDAO.getGatewayHash(terminalVO);
        }
    }
    public String getMemberTerminalId(String memberId,String accountId,String payModeId,String cardTypeId)//sandip
    {
        return  terminalDAO.getMemberTerminalId(memberId,accountId,payModeId,cardTypeId);
    }
    //getting list of terminalVo according to the agentId
    public List<TerminalVO> getTerminalsByAgentId(String agentId) throws PZDBViolationException
    {
        return terminalDAO.getTerminalsByAgentId(agentId);
    }
    public TerminalVO getMemberTerminalInfo(String memberId,String terminalId)throws PZDBViolationException
    {
        return terminalDAO.getMemberTerminalInfo(memberId, terminalId);
    }
    public TerminalVO getTerminalMerchantsInFo(String memberId,String terminalId)throws PZDBViolationException
    {
        return terminalDAO.getTerminalMerchantsInFo(memberId, terminalId);
    }
    public List<TerminalVO> getTerminalsByPartnerId(String partnerId) throws PZDBViolationException
    {
        return terminalDAO.getTerminalsByPartnerId(partnerId);
    }
    public List<TerminalVO> getActiveTerminalsByPartnerId(String partnerId) throws PZDBViolationException
    {
        return terminalDAO.getActiveTerminalsByPartnerId(partnerId);
    }

    public Map<String,List<TerminalVO>> getTerminalsByMemberIdForPartner(String partnerId) throws PZDBViolationException
    {
        return terminalDAO.getTerminalsByMemberIdForPartner(partnerId);
    }
    public List<TerminalVO> getTerminalsByMemberIdAndPartner(String partnerId, String memberId) throws PZDBViolationException
    {
        return terminalDAO.getTerminalsByMemberIdAndPartner(partnerId,memberId);
    }
    public List<TerminalVO> getAllTerminalsByMemberId(String partnerId, String memberId) throws PZDBViolationException
    {
        return terminalDAO.getAllTerminalsByMemberId(partnerId,memberId);
    }

    public  boolean isTokenizationActiveOnTerminal(String memberID,String terminalId)throws PZDBViolationException
    {
        return terminalDAO.isTokenizationActiveOnTerminal(memberID, terminalId);
    }
    public TerminalVO getMemberTerminalfromMemberAndTerminal(String memberID,String terminalId,String accountId) throws PZDBViolationException
    {
        return terminalDAO.getMemberTerminalfromMemberAndTerminal(memberID, terminalId,accountId);
    }
    public TerminalVO getMemberTerminalfromMemberAndTerminalAndCurrency(String memberID,String terminalId, String currency,String accountId) throws PZDBViolationException
    {
        return terminalDAO.getMemberTerminalfromMemberAndTerminalAndCurrency(memberID, terminalId,currency,accountId);
    }
    public boolean isMemberMappedWithTerminal(String memberId, String terminalId)throws Exception
    {
        return terminalDAO.isMemberMappedWithTerminal(memberId, terminalId);
    }
    public TerminalVO getMemberTerminalfromTerminal(String terminalId, String memberId,String accountId) throws PZDBViolationException
    {
        return terminalDAO.getMemberTerminalfromTerminal(terminalId, memberId,accountId);
    }
    public TerminalVO getMemberTerminalfromTerminal(String terminalId) throws PZDBViolationException
    {
        return terminalDAO.getMemberTerminalfromTerminal(terminalId);
    }
    public List<TerminalVO> getTerminalListByAgent(String agentId) throws PZDBViolationException
    {
        return terminalDAO.getTerminalListByAgent(agentId);
    }
    /* public List<TerminalVO> getMemberTerminalList(String memberId) throws PZDBViolationException
     {
         return terminalDAO.getMemberTerminalList(memberId);
     }*/
    public String getTerminalByAgentId(String agentId) throws PZDBViolationException
    {
        return terminalDAO.getTerminalByAgentId(agentId);
    }
    public String getTerminalByMemberIdAndAgentId(String memberid,String agentId) throws PZDBViolationException
    {
        return terminalDAO.getTerminalByMemberIdAndAgentId(memberid, agentId);
    }
    public List<TerminalVO> getMemberandUserTerminalList(String merchantid, String currency) throws PZDBViolationException
    {
        return terminalDAO.getMemberandUserTerminalList(merchantid, currency);
    }

    public List<TerminalVO> getMemberandTerminalList(String merchantid, String currency) throws PZDBViolationException
    {
        return terminalDAO.getMemberandTerminalList(merchantid, currency);
    }
    public List<TerminalVO> getCurrencyList(String merchantId,String role) throws PZDBViolationException
    {
        return terminalDAO.getCurrencyList(merchantId, role);
    }
   /* public List<TerminalVO> getMemberandUserCurrencyList(String merchantId,String role) throws PZDBViolationException
    {
        return terminalDAO.getMemberandUserCurrencyList(merchantId, role);
    }*/
    public TerminalVO getCardIdAndPaymodeIdFromPaymentBrand(String memberId,String paymodeId, String cardTypeId, String currency) throws PZDBViolationException
    {
        return terminalDAO.getCardIdAndPaymodeIdFromPaymentBrand(memberId, paymodeId, cardTypeId, currency);
    }

    public TerminalVO getCardIdAndPaymodeIdFromPaymentBrand(String memberId,String paymodeId, String cardTypeId, String currency, String accountId) throws PZDBViolationException
    {
        return terminalDAO.getCardIdAndPaymodeIdFromPaymentBrand(memberId, paymodeId, cardTypeId, currency, accountId);
    }

    public HashMap getPaymdeCardTerminalVO(String memberId,String currency,String accountId) throws PZDBViolationException
    {
        return terminalDAO.getPaymdeCardTerminalVO(memberId, currency,accountId);
    }

    public HashMap getMultipleCurrencyPaymdeCardTerminalVO(String memberId,String currency1,String currency2,String accountId) throws PZDBViolationException
    {
        return terminalDAO.getMultipleCurrencyPaymdeCardTerminalVO(memberId, currency1,currency2,accountId);
    }

    public HashMap getPaymdeCardTerminalVO(String memberId,String currency, String paymodeId,String accountId) throws PZDBViolationException
    {
        return terminalDAO.getPaymdeCardTerminalVO(memberId,currency,paymodeId,accountId);
    }

    public HashMap getPaymdeCardTerminalVO(String memberId,String paymodeId,String currency, String currency1,String accountId) throws PZDBViolationException
    {
        return terminalDAO.getPaymdeCardTerminalVO(memberId,paymodeId,currency,currency1,accountId);
    }

    public HashMap getPaymdeCardTerminalVOfromPaymodeCardType(String memberId,String currency, String paymodeId,String cardId,String accountId) throws PZDBViolationException
    {
        return terminalDAO.getPaymdeCardTerminalVOfromPaymodeCardId(memberId, currency, paymodeId, cardId,accountId);
    }

    public HashMap getPaymdeCardTerminalVOfromPaymodeCardType(String memberId, String paymodeId,String cardId,String currency,String currency1,String accountId) throws PZDBViolationException
    {
        return terminalDAO.getPaymdeCardTerminalVOfromPaymodeCardId(memberId, paymodeId, cardId,currency,currency1,accountId);
    }

    public HashMap getPaymdeCardTerminalVOfromTerminalID(String memberId,String currency,String currency1, String terminalID,String accountId) throws PZDBViolationException
    {
        return terminalDAO.getPaymdeCardTerminalVOfromTerminalID(memberId,currency,currency1,terminalID,accountId);
    }

    public HashMap getPaymdeCardTerminalVOfromTerminalID(String memberId,String currency, String terminalID,String accountId) throws PZDBViolationException
    {
        return terminalDAO.getPaymdeCardTerminalVOfromTerminalID(memberId,currency,terminalID,accountId);
    }

    public TerminalVO getCardIdAndPaymodeIdFromTerminal(String memberId, String terminalId,String currency,String paymodeId, String cardTypeId) throws PZDBViolationException
    {
        return terminalDAO.getCardIdAndPaymodeIdFromTerminal(memberId, terminalId, currency, paymodeId, cardTypeId);
    }
    public TerminalVO getTerminalFromPaymodeCardtypeMemberidCurrency(String memberId, String paymentType, String cardType, String currency)
    {
        return terminalDAO.getTerminalFromPaymodeCardtypeMemberidCurrency(memberId, paymentType, cardType, currency);
    }

    public List<TerminalVO> getAllNewTerminals() throws PZDBViolationException
    {
        return terminalDAO.getAllNewTerminals();
    }

    public List<TerminalVO> getAllOldTerminals() throws PZDBViolationException
    {
        return terminalDAO.getAllOldTerminals();
    }
    public List<TerminalVO> getAllTerminals() throws PZDBViolationException
    {
        return terminalDAO.getAllTerminals();
    }

    public boolean doInactiveTerminalProcessing(String terminalId)
    {
        return terminalDAO.doInactiveTerminal(terminalId);
    }
    public Map<String,List<TerminalVO>> getAllTerminalsGroupByMerchant()throws PZDBViolationException
    {
        return terminalDAO.getAllTerminalsGroupByMerchant();
    }
    public Map<String,List<TerminalVO>> getNewTerminalsGroupByMerchant()throws PZDBViolationException
    {
        return terminalDAO.getNewTerminalsGroupByMerchant();
    }
    public Map<String,List<TerminalVO>> getOldTerminalsGroupByMerchant()throws PZDBViolationException
    {
        return terminalDAO.getOldTerminalsGroupByMerchant();
    }
    public Map<String,List<TerminalVO>> getAuthCaptureModeAllTerminalsGroupByMerchant()throws PZDBViolationException
    {
        return terminalDAO.getAuthCaptureModeAllTerminalsGroupByMerchant();
    }

    public TerminalVO getMemberTerminalWithActivationDetails(String memberId, String terminalId) throws PZDBViolationException
    {
        return terminalDAO.getMemberTerminalWithActivationDetails(memberId, terminalId);
    }

    public TerminalLimitsVO getMemberTerminalProcessingLimitVO(String memberId, String terminalId) throws PZDBViolationException
    {
        return terminalDAO.getMemberTerminalProcessingLimitVO(memberId, terminalId);
    }

    public TerminalVO getMemberTerminalDetails(String terminalid, String memberId) throws PZDBViolationException
    {
        return terminalDAO.getMemberTerminalDetails(terminalid, memberId);
    }
    public TerminalVO getMemberTerminalDetails(String terminalid, String memberId, String currency) throws PZDBViolationException
    {
        return terminalDAO.getMemberTerminalDetails(terminalid, memberId, currency);
    }
    public TerminalVO getPartnerTerminalfromPartnerAndTerminal(String partnerId,String terminalId) throws PZDBViolationException
    {
        return terminalDAO.getPartnerTerminalfromPartnerAndTerminal(partnerId, terminalId);
    }
    public boolean isPartnersMerchantMappedWithTerminal(String partnerId,String terminalId) throws PZDBViolationException
    {
        return terminalDAO.isPartnersMerchantMappedWithTerminal(partnerId, terminalId);
    }
    public TerminalVO getTerminalDetailsWithPartnerId(String partnerId,String paymodeId, String cardTypeId, String currency) throws PZDBViolationException
    {
        return terminalDAO.getTerminalDetailsWithPartnerId(partnerId, paymodeId, cardTypeId, currency);
    }
    /*public List<TerminalVO> getCardtypePaymenttypeForRESTRequest(String login, String workflow) throws PZDBViolationException
    {
        return terminalDAO.getCardtypePaymenttypeForRESTRequest(login,workflow);
    }*/
    public TerminalVO getPartnersTerminalDetails(String partnerId, String payModeId, String cardTypeId, String currency) throws PZDBViolationException
    {
        return terminalDAO.getPartnersTerminalDetails(partnerId, payModeId, cardTypeId, currency);
    }
    public boolean isGatewayCurrencyExistWithMember(String memberId, String currency) throws PZDBViolationException
    {
        return terminalDAO.isGatewayCurrencyExistWithMember(memberId, currency);
    }
    public List<TerminalVO> getCreateRegistrationForMerchant(String memberid) throws PZDBViolationException
    {
        return terminalDAO.getCreateRegistrationForMerchant(memberid);
    }
    public List<TerminalVO> getAllMappedTerminals() throws PZDBViolationException
    {
        return terminalDAO.getAllMappedTerminals();
    }
    public List<TerminalVO> getAllMappedTerminals1(String gatewayid, String accountid, String memberid) throws PZDBViolationException
    {
        return terminalDAO.getAllMappedTerminals1(gatewayid, accountid, memberid);
    }
    public List<TerminalVO> getAllMappedTerminal() throws PZDBViolationException
    {
        return terminalDAO.getAllMappedTerminal();
    }

    public List<TerminalVO> getAllMappedTerminalsForCommon() throws PZDBViolationException
    {
        return terminalDAO.getAllMappedTerminalsForCommon();
    }
    public List<TerminalVO> getAllMappedAccounts() throws PZDBViolationException
    {
        return terminalDAO.getAllMappedAccounts();
    }

    public Hashtable getListOfTerminalDetails(String memberId, String accountId)throws  PZDBViolationException
    {
        return terminalDAO.getListOfTerminalDetails(memberId, accountId);
    }

    public  LinkedHashMap<String,String> loadcardtypeids()
    {
        return  terminalDAO.loadcardtypeids();
    }

    public  LinkedHashMap<String,String> loadPaymodeids()
    {
        return  terminalDAO.loadPaymodeids();
    }

    public  boolean isMasterCardSupported(String accountId)throws Exception
    {
        return  terminalDAO.isMasterCardSupported(accountId);
    }

    public  boolean isTerminalUnique(String memberId,String accountId,String payModeId,String cardTypeId)throws Exception
    {
        return  terminalDAO.isTerminalUnique(memberId,accountId,payModeId,cardTypeId);
    }

    public  String updateTerminalConfiguration(TerminalVO terminalVO)throws SQLException,SystemError
    {
        return  terminalDAO.updateTerminalConfiguration(terminalVO);
    }

    public  String masterCardTerminalConfiguration(TerminalVO terminalVO)throws SQLException,SystemError
    {
        return  terminalDAO.masterCardTerminalConfiguration(terminalVO);
    }

    public  String deleteTerminalConfiguration(TerminalVO terminalVO)throws SQLException,SystemError
    {
        return  terminalDAO.deleteTerminalConfiguration(terminalVO);
    }
    public TreeMap<Integer,Integer> getTerminals()
    {
        return terminalDAO.getTerminals();
    }

    public LinkedHashMap<String,Integer> getCardTypeID ()

    {
        return terminalDAO.loadcardtypeid();
    }
    public LinkedHashMap<String,Integer> getPaymodeType ()

    {
        return terminalDAO.loadPaymodeid();
    }

    public boolean isValidTerminal(String memberId, String accountId,String terminalId,String payModeId, String cardTypeId)throws Exception
    {
        return terminalDAO.isValidTerminal(memberId,accountId,terminalId,payModeId,cardTypeId);
    }
    public LinkedHashMap<String,TerminalVO> getTerminalMap()
    {
        return terminalDAO.getTerminalMap();
    }

    public TerminalVO getMemberTerminalDetailsForTerminalChange(String memberId,String payModeId,String cardTypeId,String currency) throws PZDBViolationException
    {
        return terminalDAO.getMemberTerminalDetailsForTerminalChange(memberId, payModeId, cardTypeId, currency);
    }

    public TerminalVO getBinRoutingTerminalDetails(String firstSix,String memberId,String paymodeid,String cardtypeid,String currency)
    {
        return terminalDAO.getBinRoutingTerminalDetails(firstSix, memberId,paymodeid,cardtypeid,currency);
    }

    public TerminalVO getBinRoutingTerminalDetails(String memberId,String paymodeid,String cardtypeid,String currency)
    {
        return terminalDAO.getRoutedTerminalDetails(memberId,paymodeid,cardtypeid,currency);
    }

    public TerminalVO getCardIdAndPaymodeIdFromPaymentBrandforBinRouting(String memberId,String paymodeId, String cardTypeId, String currency,String firstSix) throws PZDBViolationException
    {
        return terminalDAO.getCardIdAndPaymodeIdFromPaymentBrandforBinRouting(memberId, paymodeId, cardTypeId, currency, firstSix);
    }

    public TerminalVO getAccountIdTerminalVOforBinRouting(String memberId,int paymodeId, int cardTypeId,String firstSix) throws PZDBViolationException
    {
        return terminalDAO.getAccountIdTerminalVOforBinRouting(memberId, paymodeId, cardTypeId,firstSix);
    }

    public LinkedHashMap<String,TerminalVO> getTerminalFromCurrency(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        return terminalDAO.getTerminalFromCurrency(commonValidatorVO);
    }
    public TerminalVO getBinRoutingTerminalDetailsByCardNumber(String cNum,String memberId,String paymodeid,String cardtypeid,String currency)
    {
        return terminalDAO.getBinRoutingTerminalDetailsByCardNumber(cNum,memberId,paymodeid,cardtypeid,currency);
    }

    public TerminalVO getCardIdAndPaymodeIdFromPaymentBrandforBinRoutingByCardNumber(String memberId,String paymodeId, String cardTypeId, String currency,String cNum) throws PZDBViolationException
    {
        return  terminalDAO.getCardIdAndPaymodeIdFromPaymentBrandforBinRoutingByCardNumber(memberId,paymodeId,cardTypeId,currency,cNum);
    }
    public TerminalVO getAccountIdTerminalVOforBinRoutingByCardNumber(String toid, int paymenttype, int cardtype,String cNum)
    {
        return terminalDAO.getAccountIdTerminalVOforBinRoutingByCardNumber(toid,paymenttype,cardtype,cNum);
    }
    public StringBuffer getVendorsTerminalByMemberid(String memberid) throws PZDBViolationException
    {
        return terminalDAO.getVendorsTerminalByMemberid(memberid);
    }
    public TerminalVO getTerminalCardIdAndPaymodeIdFromPaymentBrand(String memberId,String paymodeId, String cardTypeId, String currency) throws PZDBViolationException
    {
        return  terminalDAO.getTerminalCardIdAndPaymodeIdFromPaymentBrand(memberId,paymodeId,cardTypeId,currency);
    }

    public HashMap getListofPaymentandCurrencyMapByToid(String toid)throws PZDBViolationException
    {
        return  terminalDAO.getListofPaymentandCurrencyMapByToid(toid);
    }

    public HashMap getListofPaymentandCardtypeByToid(String toid,String currency)throws PZDBViolationException
    {
        return  terminalDAO.getListofPaymentandCardtypeByToid(toid,currency);
    }

    public HashMap getPaymdeCardTerminalVOFromMemberId(String memberId,String currency) throws PZDBViolationException
    {
        return terminalDAO.getPaymdeCardTerminalVOFromMemberId(memberId,currency);
    }
    public Map<String,Map<String,Set<String>>> getPaymentAndCardTypePerCurrency(String memberId) throws PZDBViolationException
    {
        return terminalDAO.getPaymentAndCardTypePerCurrency(memberId);
    }
    public TerminalVO getBinCountryRoutingTerminalDetails(String country,String memberId,String paymodeid,String cardtypeid,String currency)
    {
        return terminalDAO.getBinCountryRoutingTerminalDetails(country,memberId,paymodeid,cardtypeid,currency);
    }
    public TerminalVO getCardIdAndPaymodeIdFromPaymentBrandforBinCountryRouting(String memberId,String paymodeId, String cardTypeId, String currency,String country) throws PZDBViolationException
    {
        return terminalDAO.getCardIdAndPaymodeIdFromPaymentBrandforBinCountryRouting(memberId,paymodeId,cardTypeId,currency,country);
    }
    public TerminalVO getAccountIdTerminalVOforBinCountryRouting(String toid, int paymenttype, int cardtype,String country)
    {
        return terminalDAO.getAccountIdTerminalVOforBinCountryRouting(toid,paymenttype,cardtype,country);
    }
    public  TerminalVO getMemberUserTerminalDetails(String terminalid, String memberId, String currency,String userid) throws PZDBViolationException
    {
        return terminalDAO.getMemberUserTerminalDetails(terminalid,memberId,currency,userid);
    }
    public TerminalVO getCardIdAndPaymodeIdFromPaymentBrandMemberUser(String memberId,String paymodeId, String cardTypeId, String currency,String userid) throws PZDBViolationException
    {
        return terminalDAO.getCardIdAndPaymodeIdFromPaymentBrandMemberUser(memberId, paymodeId, cardTypeId, currency, userid);
    }
    public boolean isGatewayCurrencyExistWithMemberUser(String memberId, String currency,String userid) throws PZDBViolationException
    {
        return terminalDAO.isGatewayCurrencyExistWithMemberUser(memberId, currency, userid);
    }
    public TerminalVO getRoutingTerminalByFromAccountId(String fromAccountId,String memberId,String paymodeId,String cardTypeId) throws PZDBViolationException
    {
        return terminalDAO.getRoutingTerminalByFromAccountId(fromAccountId,memberId,paymodeId,cardTypeId);
    }
    public String getPreviousWireDetails(String agentId)
    {
        return terminalDAO.getPreviousWireDetails(agentId);
    }


    public LinkedList<TerminalVO> getPayoutActiveTerminal(String memberID) throws PZDBViolationException
    {
        return terminalDAO.getPayoutActiveTerminal(memberID);
    }

    public   Map<String,HashMap> getPayoutAmountLimit() throws PZDBViolationException
    {
        return terminalDAO.getPayoutAmountLimit();
    }

    public  Map<String,String> getPayoutAmountLimitByAccountid(String accountid) throws PZDBViolationException
    {
        return terminalDAO.getPayoutAmountLimitByAccountid(accountid);
    }

    public  Boolean updatePayoutTransactionAmount (String accountid, String  currentPayoutAmount )
    {
        return terminalDAO.updatePayoutTransactionAmount(accountid, currentPayoutAmount);
    }
    public List<TerminalVO> getTerminalsByAccAndBankId(String accountId,String parentBankId,String cardtypeid,String paymodid) throws PZDBViolationException
    {
        return terminalDAO.getTerminalsByAccAndBankId(accountId, parentBankId,cardtypeid, paymodid);
    }
    public List<TerminalVO> getGenratedReportByAccountID(String accountId) throws PZDBViolationException
    {
        return terminalDAO.getGenratedReportByAccountID(accountId);
    }
}
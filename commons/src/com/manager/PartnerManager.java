package com.manager;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.dao.FileExtractionDao;
import com.manager.dao.PartnerDAO;
import com.manager.vo.*;
import com.manager.vo.fileRelatedVOs.UploadLabelVO;
import com.manager.vo.merchantmonitoring.DateVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/11/15
 * Time: 1:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class PartnerManager
{
    private static Logger logger = new Logger(PartnerManager.class.getName());

    /// Declare Static Class Of Partner Clss ///
    private static PartnerDAO partnerDAO = new PartnerDAO();

    public CommonValidatorVO getPartnerDetailFromMemberId(String toid,CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        return partnerDAO.getPartnerDetails(toid,commonValidatorVO);
    }
    public PartnerDetailsVO getselfPartnerDetails(String partnerid) throws PZDBViolationException
    {
        return partnerDAO.getselfPartnerDetails(partnerid);
    }
    public boolean updatePartnerTemplateDetails(Map<String, Object> partnerTemplateInformation, String partnerId) throws PZDBViolationException
    {
        return partnerDAO.updatePartnerTemplateDetails(partnerTemplateInformation, partnerId);
    }
    public boolean insertPartnerTemplateDetails(Map<String, Object> partnerTemplateInformation, String partnerId) throws PZDBViolationException
    {
        return partnerDAO.insertPartnerTemplatePreferences(partnerTemplateInformation, partnerId);
    }
    public boolean deletePartnerTemplateDetails(Map<String, Object> partnerTemplateInformation, String partnerId) throws PZDBViolationException
    {
        return partnerDAO.deletePartnerTemplatePreferences(partnerTemplateInformation, partnerId);
    }
    public Map<String,Object> getPartnerSavedMemberTemplateDetails(String partnerId) throws PZDBViolationException
    {
        return partnerDAO.getPartnerSavedMemberTemplateDetails(partnerId);
    }

 /*   public Map<String,Object> getPartnerSavedTemplateDetails(String partnerId) throws PZDBViolationException
    {
        return partnerDAO.getPartnerSavedTemplateDetails(partnerId);
    }*/
    /*public PartnerDetailsVO getthemePartnerDetails(String partnerid) throws PZDBViolationException
    {
        logger.debug("Manager getthemePartnerDetails........."+partnerid);
        return partnerDAO.getthemePartnerDetails(partnerid);
    }*/

    public void ChangeTemplate(Hashtable details, String merchantid) throws SystemError
    {
        PartnerDAO.ChangeTemplate(details,merchantid);
    }
    public Map<String,Object> getSavedMemberTemplateDetails(String memberId) throws PZDBViolationException
    {
        return partnerDAO.getSavedMemberTemplateDetails(memberId);
    }
    public boolean insertMemberTemplateDetails(Map<String, Object> merchantTemplateInformation, String memberId) throws PZDBViolationException
    {
        return partnerDAO.insertTemplatePreferences(merchantTemplateInformation, memberId);
    }

    public boolean updateMemberTemplateDetails(Map<String, Object> merchantTemplateInformation, String memberId) throws PZDBViolationException
    {
        return partnerDAO.updateTemplatePreferences(merchantTemplateInformation, memberId);
    }

    public boolean deleteMemberTemplateDetails(Map<String, Object> merchantTemplateInformation, String memberId) throws PZDBViolationException
    {
        return partnerDAO.deleteMemberTemplateDetails(merchantTemplateInformation,memberId);
    }

    public  Map<String,UploadLabelVO> getListOfUploadLabel(String functionalUsage)
    {
        FileExtractionDao fileExtractionDao = new FileExtractionDao();
        return fileExtractionDao.getListOfUploadLabelWithAlternateNameAsKey(functionalUsage);
    }
    public PartnerDetailsVO getPartnerDetails(String partnerId) throws PZDBViolationException
    {
        return partnerDAO.getPartnerDetails(partnerId);
    }
    public PartnerDetailsVO getPartnerDetailsForIFE(String partnerID) throws PZDBViolationException
    {
        return partnerDAO.getPartnerDetailsForIFE(partnerID);
    }

    public Hashtable getPartnerWireList(String toid,String accountid,String terminalid,String partnerId,String gateway,String is_paid,String fdtstamp, String tdtstamp,int pageno, int records)
    {
        return partnerDAO.getPartnerWireList(toid, accountid, terminalid, partnerId, gateway, is_paid, fdtstamp, tdtstamp, pageno, records);
    }

   public TreeMap<Integer,String> loadGatewayAccounts(String partnerid,String accountid)
    {
        return partnerDAO.loadGatewayAccounts(partnerid, accountid);
    }
    public TreeMap<Integer,String> loadMerchantsGatewayAccounts(String partnerid,String memberid)
    {
        return partnerDAO.loadMerchantsGatewayAccounts(partnerid, memberid);
    }
    public TreeMap<Integer,String> loadGatewayAccount(String partnerid)
    {
        return partnerDAO.loadGatewayAccount(partnerid);
    }
    public TreeMap<Integer,String> loadGatewayAccountForPartner(String partnerid)
    {
        return partnerDAO.loadGatewayAccountForPartner(partnerid);
    }
    public Hashtable getMembermapping(String accountid,String memberid ,String partnerid,String paymodeid, String cardtypeid,String ActiveOrInactive,String ispayoutactive)
    {
        return partnerDAO.getMemberMapping(accountid, memberid, partnerid,paymodeid,cardtypeid,ActiveOrInactive,ispayoutactive);
    }
    public boolean isTerminalUnique(String memberId,String accountId,String payModeId,String cardTypeId) throws Exception

    {
        return partnerDAO.isTerminalUnique(memberId, accountId, payModeId, cardTypeId);
    }

    public  boolean isMasterCardSupported(String accountId)throws Exception
    {
        return partnerDAO.isMasterCardSupported(accountId);
    }

    public  boolean isChackForAccountId(String partnerid,String accountIdAfterFillter)throws Exception
    {
        return partnerDAO.isChackForAccountId(partnerid,accountIdAfterFillter);
    }

    public List<MerchantDetailsVO> getMemberListForFraud(String partnerId)throws Exception
    {
        return partnerDAO.getMemberListForFraud(partnerId);
    }

    public List<PartnerDetailsVO> getAllWhitelabelPartners()throws Exception
    {
        return partnerDAO.getAllWhitelabelPartners();
    }

    public boolean isUniqueTheme(DefaultThemeVO defaultThemeVO)throws PZDBViolationException
    {
        return partnerDAO.isUniqueTheme(defaultThemeVO);
    }

    public boolean isUniqueCurrentTheme(CurrentThemeVO currentThemeVO)throws PZDBViolationException
    {
        return partnerDAO.isUniqueCurrentTheme(currentThemeVO);
    }

    public String addNewDefaultTheme(DefaultThemeVO defaultThemeVO)throws PZDBViolationException
    {
        return partnerDAO.addNewDefaultTheme(defaultThemeVO);
    }

    public String addNewCurrentTheme(CurrentThemeVO currentThemeVO)throws PZDBViolationException
    {
        return partnerDAO.addNewCurrentTheme(currentThemeVO);
    }
    public PartnerDefaultConfigVO getPartnerDefaultConfig(String partnerId) throws PZDBViolationException
    {
        return partnerDAO.getPartnerDefaultConfig(partnerId);
    }

    public String getPartnerKey(String partnerId)
    {
        return partnerDAO.getPartnerKey(partnerId);
    }

    public Hashtable<String,String> getTotalSalesAmount(String partnerName, String currency, String payBrand, String payMode,DateVO dateVO,String dashboard_value,String status) throws PZDBViolationException
    {
        return partnerDAO.getTotalSalesAmount(partnerName, currency, payBrand, payMode,dateVO,dashboard_value,status);
    }

    public String getTotalSettledAmount(String partnerName, String currency, String payBrand, String payMode) throws PZDBViolationException
    {
        return partnerDAO.getTotalSettledAmount(partnerName, currency, payBrand, payMode);
    }

    public String getTotalRefund(String partnerName, String currency, String payBrand, String payMode) throws PZDBViolationException
    {
        return partnerDAO.getTotalTotalRefundAmount(partnerName, currency, payBrand, payMode);
    }

    public String getTotalChargeback(String partnerName, String currency, String payBrand, String payMode) throws PZDBViolationException
    {
        return partnerDAO.getTotalChargebackAmount(partnerName, currency, payBrand, payMode);
    }

   /* public Hashtable<String,String> getTotalDeclinedAmount(String partnerName, String currency, String payBrand, String payMode, DateVO dateVO,String dashboard_value,String status) throws PZDBViolationException
    {
        return partnerDAO.getTotalDeclinedAmount(partnerName, currency, payBrand, payMode,dateVO,dashboard_value,status);
    }*/
    public HashMap<String,String> getTotalAmountandCount(String partnerName, String currency, String payBrand, String payMode,DateVO dateVO,String dashboard_value,String status)throws PZDBViolationException
    {
        return partnerDAO.getTotalAmountandCount(partnerName,currency,payBrand,payMode,dateVO,dashboard_value,status);
    }
    public StringBuffer getMonthlyTransactionDetailsForPartnerNew(String partnerName,DateVO dateVO, String currency, String payBrand, String payMode,String dashboard_value,String status)
    {
        return partnerDAO.getMonthlyTransactionDetailsForPartnerNew(partnerName,dateVO,currency,payBrand,payMode,dashboard_value,status);
    }
    public List<String> getValidCurrencyListForPartner(String partnerName) throws PZDBViolationException
    {
        return partnerDAO.getValidCurrencyListForPartner(partnerName);
    }
    ///Select Option PayBrand List
    public HashMap<String,String> getValidPayBrandListForPartner(String partnerName,String bandList) throws PZDBViolationException
    {
        return partnerDAO.getValidPayBrandListForPartner(partnerName, bandList);
    }
    ///Select Option PayModelListList
    public HashMap<String,String> getValidPayModeListForPartner(String partnerName) throws PZDBViolationException
    {
        return partnerDAO.getValidPayModeListForPartner(partnerName);
    }

    ///Select Option PayCurrency
    public HashMap<String, String> getSalesPerCurrencyChartForPartner(String partnerName, String currency, String payBrand, String payMode,DateVO dateVO,String dashboard_value,String status) throws PZDBViolationException
    {
        return partnerDAO.getSalesPerCurrencyChartForPartner(partnerName,currency, payBrand, payMode,dateVO,dashboard_value,status);
    }

    public HashMap<String, String> getbincountrysuccessful(String totype ,String toid ,String country, String tdtstamp, String fdtstamp, String accountid, String currency) throws PZDBViolationException
    {
        return partnerDAO.getbincountrysuccessful(totype,toid,country,tdtstamp,fdtstamp ,accountid ,currency);
    }

    public HashMap<String, String> getbincountryfailed(String totype,String toid ,String country, String tdtstamp, String fdtstamp, String accountid,String currency) throws PZDBViolationException
    {
        return partnerDAO.getbincountryfailed(totype,toid,country, tdtstamp, fdtstamp, accountid,currency);
    }

    public HashMap<String, String> getipcountrysuccessful(String totype ,String toid,String tdtstamp, String fdtstamp, String accountid , String currency) throws PZDBViolationException
    {
        return partnerDAO.getipcountrysuccessful(totype,toid,tdtstamp, fdtstamp, accountid, currency);
    }

    public HashMap<String, String> getipcountryfailed(String totype ,String toid ,String tdtstamp, String fdtstamp, String accountid,String currency) throws PZDBViolationException
    {
        return partnerDAO.getipcountryfailed(totype,toid,tdtstamp, fdtstamp, accountid,currency);
    }


    public HashMap<String, String> getStatusChartForPartner(String partnerName, String currency, String payBrand, String payMode,DateVO dateVO,String dashboard_value,String status) throws PZDBViolationException
    {
        return partnerDAO.getValidStatusChartForPartner(partnerName, currency, payBrand, payMode,dateVO,dashboard_value,status);
    }
    public String getMonthlyTransactionDetailsForPartner(String partnerName, DateVO dateVO, String currency, String payBrand, String payMode) throws PZDBViolationException
    {
        return partnerDAO.getMonthlyTransactionDetailsForPartner(partnerName, dateVO, currency, payBrand, payMode);
    }
    public HashMap getReport(String toid,String partnerId,String fdtstamp,String tdtstamp, String currency,String accountid) throws SystemError
    {
        return partnerDAO.getReport(toid, partnerId, fdtstamp, tdtstamp, currency,accountid);
    }

    public HashMap getReportpercentage(String toid,String partnerId,String fdtstamp,String tdtstamp, String currency, String accountid) throws SystemError
    {
        return partnerDAO.getReportpercentage(toid, partnerId, fdtstamp, tdtstamp, currency, accountid);
    }
   /* public HashMap getReportpercentage(String toid,String partnerId,String fdtstamp,String tdtstamp, String currency) throws SystemError
    {
        return partnerDAO.getReportpercentage(toid, partnerId, fdtstamp, tdtstamp, currency);
    }*/

    public HashMap getPartnerMerchantRefundList(String description,String accountid,String trakingid,String memberId, String partnerId,int records,int pageno,int totalrecords) throws SystemError
    {
        return partnerDAO.getPartnerMerchantRefundList(description, accountid,trakingid,memberId, partnerId,records,pageno,totalrecords);
    }
    public int getRefundListCountNew(String description,String accountid,String trackingid,String memberId, String partnerId, int records, int pageno)throws SystemError
    {
        return partnerDAO.getRefundListCountNew(description,accountid,trackingid,memberId,partnerId,records,pageno);
    }
    public HashMap getPartnerMerchantfraudSettingList(String memberId, String partnerId) throws SystemError
    {
        return partnerDAO.getPartnerMerchantfraudSettingList(memberId, partnerId);
    }
    public HashMap getPartnerMerchantCallbackSettingList(String memberId, String partnerId)throws SystemError
    {
        return partnerDAO.getPartnerMerchantCallbackSettingList(memberId, partnerId);
    }
    public HashMap getPartnerBackOfficeAccessList(String memberId, String partnerId, String month, String year) throws SystemError
    {
        return partnerDAO.getPartnerBackOfficeAccessList(memberId, partnerId, month, year);
    }
    public HashMap getBlockedMerchantList(String partnerId, int pageno, int records) throws SystemError
    {
        return partnerDAO.getBlockedMerchantList(partnerId, pageno, records);
    }
    public boolean getUnBlockedAccount(String login) throws SystemError
    {
        return partnerDAO.getUnBlockedAccount(login);
    }
    public HashMap getMerchantUserUnblockedAccountList(String partnerId, int pageno, int records) throws SystemError
    {
        return partnerDAO.getMerchantUserUnblockedAccountList(partnerId, pageno, records);
    }
    public boolean getUnBlockedUserAccount(String login) throws SystemError
    {
        return partnerDAO.getUnBlockedUserAccount(login);
    }
    public TreeMap<String, String> getAccountsDetailsMemberid(String memberid, String accountid, String partnerid)
    {
        return partnerDAO.getAccountsDetailsMemberid(memberid, accountid, partnerid);
    }
    public TreeMap<String, String> getAccountsDetailsMemberid_superpartner(String memberid, String accountid, String partnerid)
    {
        return partnerDAO.getAccountsDetailsMemberid_superpartner(memberid, accountid, partnerid);
    }
    public TreeMap<String, String> getAccountDetailsMemberid(String memberid, String partnerid)
    {
        return partnerDAO.getAccountDetailsMemberid(memberid, partnerid);
    }
    public List<PartnerDetailsVO> getPartnerDetailsForExportTransactionCron() throws PZDBViolationException
    {
        return partnerDAO.getPartnerDetailsForExportTransactionCron();
    }
    public HashMap getReportSucccessCount(String toid, String partnerId, String fdtstamp, String tdtstamp, String currency, String accountid) throws SystemError
    {
        return partnerDAO.getReportSucccessCount(toid,partnerId,fdtstamp,tdtstamp,currency,accountid);
    }
    public HashMap getReportpercentageSuccessCount(String toid, String partnerId, String fdtstamp, String tdtstamp, String currency, String accountid) throws SystemError
    {
        return partnerDAO.getReportpercentageSuccessCount(toid,partnerId,fdtstamp,tdtstamp,currency,accountid);
    }
}
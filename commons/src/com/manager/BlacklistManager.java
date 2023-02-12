package com.manager;

import com.directi.pg.AuditTrailVO;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.PzEncryptor;
import com.manager.dao.BlacklistDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/**
 * Created by 123 on 5/5/2015.
 */
public class BlacklistManager
{
    BlacklistDAO blacklistDAO = new BlacklistDAO();
    private static Logger log = new Logger(BlacklistManager.class.getName());
    Functions functions = new Functions();

    public int insertBlacklistedCards(String firstSix, String lastFour,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        int count = blacklistDAO.insertBlackListCard(firstSix, lastFour,actionExecutorId,actionExecutorName);
        return count;
    }

    public int insertBlacklistedCards(String firstSix, String lastFour,String actionExecutorId,String actionExecutorName, String reason,String remark) throws PZDBViolationException
    {
        List<BlacklistVO> listVO = new ArrayList<BlacklistVO>();
        PaginationVO paginationVO = new PaginationVO();
        listVO = blacklistDAO.getBlackListedCardsPages(firstSix, lastFour, actionExecutorId,actionExecutorName,remark, paginationVO,reason);
        int count = 0;
        if (listVO.size() == 1)
        {
            return count;
        }
        else
        {
            if (functions.isValueNull(firstSix) && functions.isValueNull(lastFour))
            {
                count = blacklistDAO.insertBlackListCard(firstSix, lastFour, actionExecutorId, actionExecutorName, reason, remark);
            }
        }
        return count;
    }
    //Block IP
    public List<BlacklistVO> getBlockedip(String ip) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedIp(ip);
    }

    public int insertBlockedIpAddress(String ip,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        List<BlacklistVO> listVO = new ArrayList<BlacklistVO>();
        listVO = blacklistDAO.getBlockedIp(ip);
        int count = 0;
        if (listVO.size() == 1)
        {
            return count;
        }
        else if (listVO.size() == 0)
        {
            count = blacklistDAO.insertBlockedIp(ip);
        }
        return count;
    }

    public void unblockIpAddress(String ipAddress) throws PZDBViolationException
    {
        blacklistDAO.unblockIpAddress(ipAddress);
    }

    //Block Email
    public List<BlacklistVO> getBlockedEmail(String email,String reason,String remark,PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedEmailAddress(email,reason,remark,paginationVO);
    }

    public int insertBlockedEmailAddress(String email, String reason,String remark,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        List<BlacklistVO> listVO = new ArrayList<BlacklistVO>();
        PaginationVO paginationVO = new PaginationVO();
        listVO = blacklistDAO.getBlockedEmailAddress(email,reason,remark,paginationVO);
        int count = 0;
        if (listVO.size() == 1)
        {
            return count;
        }
        else
        {
            if (functions.isValueNull(email))
            {
                count = blacklistDAO.insertBlockedEmail(email, reason,remark,actionExecutorId, actionExecutorName);
            }
        }
        return count;
    }
    public void unblockEmailAddress(String id) throws PZDBViolationException
    {
        blacklistDAO.unblockEmailAddress(id);
    }

    //block names
    public List<BlacklistVO> getBlockedName(String name,String reason,String remark,PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedName(name,reason, remark,paginationVO);
    }

    public int insertBlockedName(String name,String reason,String remark,String actionExecutorId,String actionExecutorName) throws PZDBViolationException {
        List<BlacklistVO> listVO = new ArrayList<BlacklistVO>();
        PaginationVO paginationVO = new PaginationVO();
        listVO = blacklistDAO.getBlockedName(name,reason,remark, paginationVO);
        int count = 0;
        if (listVO.size() == 1)
        {
            return count;
        }
        else
        {
            count = blacklistDAO.insertBlockedName(name,reason,remark,actionExecutorId,actionExecutorName);
        }
        return count;
    }

    public void unblockName(String id) throws PZDBViolationException
    {
        blacklistDAO.unblockName(id);
    }

    //block country
    public List<BlacklistVO> getBlockedCountryPage(String country, String accountId, String memberId, PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedCountryPage(country, accountId, memberId, paginationVO);
    }

    public List<BlacklistVO> getBlockedCountryPartner(String country, String accountId, String memberId, int records, int pageno, HttpServletRequest req) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedCountryPartner(country, accountId, memberId, records, pageno, req);
    }

    public List<BlacklistVO>getBlockVPAAddress(String vpaAddress,String reason,int records,int pageno,HttpServletRequest req)throws PZDBViolationException
    {
        return blacklistDAO.getBlockVPAAddress(vpaAddress,reason,records,pageno,req);
    }
    public List<BlacklistVO>getBlockEmail(String email,String reason,String remark,int records,int pageno,HttpServletRequest req)throws PZDBViolationException
    {
        return blacklistDAO.getBlockEmail(email, reason,remark, records, pageno, req);
    }
    public int insertBlockedCountry(String country, String code, String telCc, String three_digit_country_code, String accountId, String memberId,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        List<BlacklistVO> listVO = new ArrayList();
        PaginationVO paginationVO = new PaginationVO();
        listVO = blacklistDAO.getBlockedCountry(country, accountId, memberId);
        int count = 0;
        if (listVO.size() == 1)
        {
            return count;
        }
        else
        {
            count = blacklistDAO.insertBlockedCountry(country, code, telCc, three_digit_country_code, accountId, memberId,actionExecutorId,actionExecutorName);
        }
        return count;
    }

    public void unblockCountry(String Id) throws PZDBViolationException
    {
        blacklistDAO.unblockCountry(Id);
    }

    public List<BlacklistVO> getBlockedBin(String startBin, String endBin, String accountId, String memberId, String reason,String remark, String role_interface) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedBin(startBin, endBin, accountId, memberId, reason,remark,role_interface);
    }

    public List<BlacklistVO> getBlockedBinPage(String startBin, String endBin, String accountId, String memberId, String reason,String remark,PaginationVO paginationVO,String role_interface) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedBinPage(startBin, endBin, accountId, memberId,reason,remark, paginationVO,role_interface);
    }

    public int addBin(String startBin, String endBin, String accountId, String memberId,String reason,String remark, String actionExecutorId,String actionExecutorName,String role_interface) throws PZDBViolationException
    {
        List<BlacklistVO> listVO = new ArrayList();
        listVO = blacklistDAO.getBlockedBin(startBin, endBin, accountId, memberId, reason,remark,role_interface);
        int count = 0;
        if (listVO.size() == 1)
        {
            return count;
        }
        else
        {
            count = blacklistDAO.addBin(startBin, endBin, accountId, memberId,reason,remark,actionExecutorId,actionExecutorName,role_interface);
        }
        return count;
    }

    public void unblockBin(String Id) throws PZDBViolationException
    {
        blacklistDAO.unblockBin(Id);
    }

    public void addCustomerNameBatch(Set<String> nameList, String reason) throws PZDBViolationException
    {
        blacklistDAO.addCustomerNameBatch(nameList, reason);
    }

    public void addCustomerCardBatch(Set<String> cardList, String reason,String remark,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        blacklistDAO.addCustomerCardBatch(cardList, reason,remark,auditTrailVO);
    }

    public void addCustomerEmailBatch(Set<String> emailList, String reason,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        blacklistDAO.addCustomerEmailBatch(emailList, reason,auditTrailVO);
    }

    public void addCustomerNameBatch(String customerName, String reason,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        blacklistDAO.addCustomerNameBatch(customerName, reason,auditTrailVO);
    }

    public void addCustomerCardBatch(String cardNumber, String reason,String remark,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        blacklistDAO.addCustomerCardBatch(cardNumber, reason,remark,auditTrailVO);
    }

    public void addCustomerEmailBatch(String cardHolderEmail, String reason,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        blacklistDAO.addCustomerEmailBatch(cardHolderEmail, reason,auditTrailVO);
    }
    public void addCustomerVPAAddressBatch(String vpaAddress, String reason,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        blacklistDAO.addCustomerVPAAddressBatch(vpaAddress,reason,auditTrailVO);
    }
    public void blacklistEntities(String trackingId, String reason,String remark,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        TransactionManager transactionManager = new TransactionManager();
        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
        Functions functions= new Functions();
        if(functions.isValueNull(transactionDetailsVO.getName()))
        {
            String customerName = transactionDetailsVO.getName();
            if(!blacklistDAO.isCardNameBlacklist(customerName))
                addCustomerNameBatch(customerName, reason,auditTrailVO);
        }
        String customerEmail = transactionDetailsVO.getEmailaddr();
        String phone = transactionDetailsVO.getTelno();
        String cardNumber="";
        if(functions.isValueNull(transactionDetailsVO.getCcnum()))
         cardNumber = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());

        if(functions.isValueNull(customerEmail) && !" ".equalsIgnoreCase(customerEmail))
            addCustomerEmailBatch(customerEmail, reason,auditTrailVO);
        if(functions.isValueNull(cardNumber))
            addCustomerCardBatch(cardNumber, reason,remark,auditTrailVO);
        /*if(functions.isValueNull(phone))
            addCustomerPhone(phone, reason, auditTrailVO);*/
    }

    public void blacklistEntitiesBatch(String trackingId, String reason,String remark,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        TransactionManager transactionManager = new TransactionManager();
        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

        String customerName = transactionDetailsVO.getName();
        String customerEmail = transactionDetailsVO.getEmailaddr();
        String cardNumber = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());

        addCustomerNameBatch(customerName, reason,auditTrailVO);
        addCustomerEmailBatch(customerEmail, reason,auditTrailVO);
        addCustomerCardBatch(cardNumber, reason,remark,auditTrailVO);
    }

    public int uploadBins(List<String> queryBatch) throws PZDBViolationException
    {
        return blacklistDAO.uploadBins(queryBatch);
    }


    //Block Ip Address // Method Start
    public boolean chackmemberId(String memberId) throws PZDBViolationException
    {
        return blacklistDAO.chackmemberId(memberId);
    }

    public List<BlacklistVO> getBlockedip(String memberId,String AllIp,String selectIpVersion , PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedip(memberId,AllIp,selectIpVersion ,paginationVO);
    }

    public List<BlacklistVO> getBlockedipOne(String memberId,String AllIp,String selectIpVersion , PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedipOne(memberId, AllIp, selectIpVersion, paginationVO);
    }

    public List<BlacklistVO> getBlockedipForPartner(String memberId,String AllIp,String selectIpVersion , String actionExecutorId,String actionExecutorName, PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedipForPartner(memberId, AllIp, selectIpVersion,actionExecutorId,actionExecutorName, paginationVO);
    }

    public List<BlacklistVO> getBlockedAllIP(String AllIp,String memberId, String selectIpVersion, PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedAllIP(AllIp, memberId, selectIpVersion, paginationVO);
    }

    public Hashtable retrievIPForMerchant(String memberId, String AllIp, String selectIpVersion, PaginationVO paginationVO, boolean isLimit) throws PZDBViolationException
    {
        return blacklistDAO.retrievIPForMerchant( memberId, AllIp,  selectIpVersion, paginationVO, isLimit);
    }

    public boolean insertIPForMerchant(String memberId, String AllIp, String selectIpVersion, String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        return blacklistDAO.insertIPForMerchant(memberId, AllIp, selectIpVersion,actionExecutorId,actionExecutorName);
    }
    public boolean insertIPForMerchant( String AllIp, String selectIpVersion, String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        return blacklistDAO.insertIPForMerchant( AllIp, selectIpVersion,actionExecutorId,actionExecutorName);
    }

    public boolean deleteIPForMerchant(String id) throws PZDBViolationException
    {
        return blacklistDAO.deleteIPForMerchant(id);
    }

    public boolean checkForGlobal(String allIP ) throws PZDBViolationException
    {
        return blacklistDAO.checkForGlobal(allIP);
    }

    public List<BlacklistVO> getBlackListedVpaAddress(String vpaAddress,String reason, PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlackListedVpaAddress(vpaAddress,reason, paginationVO);
    }
    public List<BlacklistVO> getBlackListedPhoneNo(String phone,String reason, PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlackListedPhoneNo(phone, reason, paginationVO);
    }

    public int insertBlockedVpaAddress(String vpaAddress, String reason,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        List<BlacklistVO> listVO = new ArrayList();
        PaginationVO paginationVO = new PaginationVO();
        listVO = blacklistDAO.getBlackListedVpaAddress(vpaAddress,reason, paginationVO);
        int count = 0;
        if (listVO.size() == 1)
        {
            return count;
        }
        else
        {
            if (functions.isValueNull(vpaAddress) && functions.isValueNull(reason))
            {
                count = blacklistDAO.insertBlockedVpaAddress(vpaAddress, reason, actionExecutorId, actionExecutorName);
            }


        }
        return count;
    }
    public int insertBlockedVpaAddres(String vpaAddress, String reason,String remark,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        List<BlacklistVO> listVO = new ArrayList();
        PaginationVO paginationVO = new PaginationVO();
        listVO = blacklistDAO.getBlackListedVpaAddress(vpaAddress,reason, paginationVO);
        int count = 0;
        if (listVO.size() == 1)
        {
            return count;
        }
        else
        {
            if (functions.isValueNull(vpaAddress) && functions.isValueNull(reason))
            {
                count = blacklistDAO.insertBlockedvpaAddress(vpaAddress, reason, remark,actionExecutorId, actionExecutorName);
            }


        }
        return count;
    }
    public int insertBlockedPhone(String phone, String reason,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        List<BlacklistVO> listVO = new ArrayList();
        PaginationVO paginationVO = new PaginationVO();
        listVO = blacklistDAO.getBlackListedPhoneNo(phone, reason, paginationVO);
        int count = 0;
        if (listVO.size() == 1)
        {
            return count;
        }
        else
        {
            count = blacklistDAO.insertBlockedPhone(phone, reason, actionExecutorId, actionExecutorName);

        }
        return count;
    }

    public int insertBlacklistip(String memberId, String allIP,String selectIpVersion,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        List<BlacklistVO> listVO = new ArrayList();
        PaginationVO paginationVO = new PaginationVO();
        listVO = blacklistDAO.getBlockedipForPartner(memberId, allIP, selectIpVersion,actionExecutorId,actionExecutorName, paginationVO);
        int count = 0;
        if (listVO.size() == 1)
        {
            System.out.println("listvo");
            return count;
        }
        else
        {
            count = blacklistDAO.insertBlacklistIp(memberId, allIP, selectIpVersion, actionExecutorId, actionExecutorName);

        }
        return count;
    }


    public void unblockVpaAddress(String Id) throws PZDBViolationException
    {
        blacklistDAO.unblockVpaAddress(Id);
    }
    public void unblockPhoneNo(String Id) throws PZDBViolationException
    {
        blacklistDAO.unblockPhoneNo(Id);
    }

     public void commonBlackListing(BlacklistVO blacklistVO) throws PZDBViolationException
    {
        BlacklistManager blacklistManager=new BlacklistManager();
        Functions functions=new Functions();

        String vpaAddress=blacklistVO.getVpaAddress();
        String reason=blacklistVO.getRemark();
        String actionExecutorId=blacklistVO.getActionExecutorId();
        String actionExecutorName=blacklistVO.getActionExecutorName();
        String ip=blacklistVO.getIpAddress();
        String email=blacklistVO.getEmailAddress();
        String name=blacklistVO.getName();
        String firstSix=blacklistVO.getFirstSix();
        String lastFour=blacklistVO.getLastFour();
        String remark=reason;

        if(functions.isValueNull(vpaAddress)){
            log.debug("inside vpa block condition  vpaAddress-->"+vpaAddress+" reason"+reason+" actionExecutorId"+actionExecutorId+" actionExecutorName"+actionExecutorName);
            blacklistManager.insertBlockedVpaAddress(vpaAddress, reason,actionExecutorId, actionExecutorName);
        }
        if(functions.isValueNull(ip))
        {
            log.debug("inside IpAddress block condition  IpAddress-->"+ip+" actionExecutorId"+actionExecutorId+" actionExecutorName"+actionExecutorName);
            blacklistManager.insertBlockedIpAddress(ip, actionExecutorId, actionExecutorName);
        }
        if(functions.isValueNull(email))
        {
            log.debug("inside email block condition  email-->"+email+" actionExecutorId"+actionExecutorId+" actionExecutorName"+actionExecutorName);
            blacklistManager.insertBlockedEmailAddress(email, reason,remark,actionExecutorId, actionExecutorName);
        }
        if(functions.isValueNull(name))
        {
            log.debug("inside name block condition  name-->"+name+" actionExecutorId"+actionExecutorId+" actionExecutorName"+actionExecutorName);
            blacklistManager.insertBlockedName(name,reason,remark, actionExecutorId, actionExecutorName);
        }
        if(functions.isValueNull(firstSix)&&functions.isValueNull(lastFour))
        {
            log.debug("inside name Cards condition  Cards-->"+firstSix+"****"+lastFour+" actionExecutorId"+actionExecutorId+" actionExecutorName"+actionExecutorName);
            blacklistManager.insertBlacklistedCards(firstSix, lastFour, actionExecutorId, actionExecutorName, reason, "Fraud Received");
        }
    }
    public void addCustomerPhone(String phone, String reason,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        blacklistDAO.addCustomerPhone(phone,reason,auditTrailVO);
    }
    public void addCustomerPhoneBatch(Set<String> phoneList, String reason,AuditTrailVO auditTrailVO) throws PZDBViolationException
    {
        blacklistDAO.addCustomerPhoneBatch(phoneList,reason,auditTrailVO);
    }
    public List<BlacklistVO> getBlockedCountryPages(String country, String accountId, String memberId,String reason,String remark, PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedCountryPages(country, accountId, memberId, reason, remark, paginationVO);
    }
    public int insertBlockedCountrys(String country, String code, String telCc, String three_digit_country_code, String accountId, String memberId,String actionExecutorId,String actionExecutorName,String reason,String remark) throws PZDBViolationException
    {
        List<BlacklistVO> listVO = new ArrayList();
        PaginationVO paginationVO = new PaginationVO();
        listVO = blacklistDAO.getBlockedCountrys(country, accountId, memberId, reason,remark);
        int count = 0;
        if (listVO.size() == 1)
        {
            return count;
        }
        else
        {
            count = blacklistDAO.insertBlockedCountrys(country, code, telCc, three_digit_country_code, accountId, memberId,actionExecutorId,actionExecutorName,reason,remark);
        }
        return count;
    }
    public List<BlacklistVO> getBlockedipForAdmin(String memberId,String AllIp,String selectIpVersion , String actionExecutorId,String actionExecutorName,String reason,String remark, PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedipForAdmin(memberId, AllIp, selectIpVersion, actionExecutorId, actionExecutorName, reason, remark, paginationVO);
    }
    public boolean insertIPForAdmin( String AllIp, String selectIpVersion, String actionExecutorId,String actionExecutorName,String reason,String remark) throws PZDBViolationException
    {
        return blacklistDAO.insertIPForAdmin(AllIp, selectIpVersion, actionExecutorId, actionExecutorName,reason,remark);
    }
    public boolean insertIPForAdmin(String memberId, String AllIp, String selectIpVersion, String actionExecutorId,String actionExecutorName,String reason,String remark) throws PZDBViolationException
    {
        return blacklistDAO.insertIPForAdmin(memberId, AllIp, selectIpVersion, actionExecutorId, actionExecutorName,reason,remark);
    }
    public List<BlacklistVO> getBlockedipOnes(String memberId,String AllIp,String selectIpVersion ,String reason,String remark, PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedipOnes(memberId, AllIp, selectIpVersion, reason, remark, paginationVO);
    }
    public List<BlacklistVO> getBlockedips(String memberId,String AllIp,String selectIpVersion ,String reason,String remark, PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlockedips(memberId,AllIp,selectIpVersion ,reason,remark,paginationVO);
    }
    public void unblockBankAccountNo(String Id) throws PZDBViolationException
    {
        blacklistDAO.unblockBankAccountNo(Id);
    }
    public List<BlacklistVO> getBlackListedBankAccNo(String bankAccount,String reason, PaginationVO paginationVO) throws PZDBViolationException
    {
        return blacklistDAO.getBlackListedBankAccNo(bankAccount, reason, paginationVO);
    }
    public int insertBlockedBankAccNo(String bankAccountno, String reason,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        List<BlacklistVO> listVO = new ArrayList();
        PaginationVO paginationVO = new PaginationVO();
        listVO = blacklistDAO.getBlackListedBankAccNo(bankAccountno, reason, paginationVO);
        int count = 0;
        if (listVO.size() == 1)
        {
            return count;
        }
        else
        {
            if (functions.isValueNull(bankAccountno) && functions.isValueNull(reason))
            {
                count = blacklistDAO.insertBlockedBankAccNo(bankAccountno, reason, actionExecutorId, actionExecutorName);
            }
        }
        return count;
    }
    public List<BlacklistVO> getBlockAccountNumber(String bankaccountno, String reason, int records, int pageno, HttpServletRequest req) throws PZDBViolationException
    {
        return blacklistDAO.getBlockAccountNumber(bankaccountno, reason,  records, pageno, req);
    }
    public int insertBlockedBankAccNumber(String bankAccountno, String reason,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        List<BlacklistVO> listVO = new ArrayList();
        PaginationVO paginationVO = new PaginationVO();
        listVO = blacklistDAO.getBlackListedBankAccNo(bankAccountno, reason, paginationVO);
        int count = 0;
        if (listVO.size() == 1)
        {
            return count;
        }
        else
        {
            if (functions.isValueNull(bankAccountno) && functions.isValueNull(reason))
            {
                count = blacklistDAO.insertBlockedBankAccNo(bankAccountno, reason, actionExecutorId, actionExecutorName);
            }
        }
        return count;
    }

}

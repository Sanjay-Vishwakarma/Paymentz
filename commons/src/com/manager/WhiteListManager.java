package com.manager;

import com.directi.pg.Functions;
import com.directi.pg.PzEncryptor;
import com.directi.pg.WhitelistingDetailsVO;
import com.manager.dao.WhiteListDAO;
import com.manager.vo.PaginationVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.exceptionHandler.PZDBViolationException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

/*Created with IntelliJ IDEA.
 * User: Mahima
 * Date: 25/03/18
 * Time: 2:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class WhiteListManager
{
    WhiteListDAO whiteListDAO=new WhiteListDAO();
    public Hashtable getWhiteListEmailDetails(String memberId,String accountId,String emailAddr,String firstSix,String lastFour,int  start,int end,String isTemp, String actionExecutorId,String actionExecutorName)throws PZDBViolationException
    {
        return whiteListDAO.getWhiteListEmailDetails(memberId,accountId,emailAddr,firstSix,lastFour,start,end,isTemp,actionExecutorId,actionExecutorName);
    }
    public Hashtable getCommonWhiteList(String isTemp,String mappingId)throws PZDBViolationException
    {
        return whiteListDAO.getCommonWhiteList(isTemp,mappingId);
    }
    public Hashtable getWhiteListEmailDetailsForExport(String memberId,String accountId,String emailAddr,String firstSix,String lastFour,String actionExecutorId,String actionExecutorName,String isTemp)throws PZDBViolationException
    {
        return whiteListDAO.getWhiteListEmailDetailsForExport(memberId, accountId, emailAddr, firstSix, lastFour, actionExecutorId,actionExecutorName,isTemp);
    }
    public boolean isRecordAvailableForMember(String memberId, String accountId, String firstSix, String lastFour, String emailAddress,String cardHolderName,String ipAddress,String expiryDate) throws PZDBViolationException
    {
        return whiteListDAO.isRecordAvailableForMember(memberId, accountId, firstSix, lastFour, emailAddress,cardHolderName,ipAddress,expiryDate);
    }
    public boolean isRecordAvailableForMember(String memberId, String accountId, String firstSix, String lastFour,String cardHolderName,String ipAddress,String expiryDate) throws PZDBViolationException
    {
        return whiteListDAO.isRecordAvailableForMember(memberId, accountId, firstSix, lastFour,cardHolderName,ipAddress,expiryDate);
    }
    public boolean isRecordAvailableInSystem(String memberId,String firstSix, String lastFour, String emailAddress) throws PZDBViolationException
    {
        return whiteListDAO.isRecordAvailableInSystem(memberId,firstSix, lastFour, emailAddress);
    }
    public boolean addCard(String firstSix, String lastFour, String emailAddr, String accountId, String memberId,String cardHolderName,String ipAddress,String expiryDate,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        return whiteListDAO.addCard(firstSix, lastFour, emailAddr, accountId, memberId,cardHolderName,ipAddress,expiryDate,actionExecutorId,actionExecutorName);
    }
    public boolean removeCardEmailEntry(String isTemp,String mappingId)throws PZDBViolationException
    {
        return whiteListDAO.removeCardEmailEntry(isTemp,mappingId);
    }
    public boolean updateCardEmailEntry(String mappingId)throws PZDBViolationException
    {
        return whiteListDAO.updateCardEmailEntry(mappingId);
    }
    public boolean removeWhitelistBinEntry(String Id)throws PZDBViolationException
    {
        return whiteListDAO.removeWhitelistBinEntry(Id);
    }
    public void whiteListEntities(String trackingId)throws PZDBViolationException
    {
        Functions functions=new Functions();
        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(trackingId);
        String customerEmail=transactionDetailsVO.getEmailaddr();
        String cardNumber="";
        if(functions.isValueNull(transactionDetailsVO.getCcnum()))
         cardNumber= PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
        //boolean result=removeCardEmail(cardNumber,customerEmail);
        //boolean result=updateCardEmail(cardNumber,customerEmail);
    }
    public boolean removeCardEmail(String cardNumber,String eMail)throws PZDBViolationException
    {
        return whiteListDAO.removeCardEmail(cardNumber, eMail);
    }
    public boolean updateCardEmail(String cardNumber,String eMail)throws PZDBViolationException
    {
        return whiteListDAO.updateCardEmail(cardNumber, eMail);
    }
    public void removeCardEmailEntry(Set<String> emailList,Set<String> cardList)throws PZDBViolationException
    {
        whiteListDAO.removeCardEmailEntry(emailList, cardList);
    }
    public void updateCardEmailEntry(Set<String> emailList,Set<String> cardList)throws PZDBViolationException
    {
        whiteListDAO.updateCardEmailEntry(emailList,cardList);
    }
    public int uploadCards(List<String> queryBatch)throws PZDBViolationException
    {
        return whiteListDAO.uploadCards(queryBatch);
    }

    public List<WhitelistingDetailsVO> getWhiteListBin(String startBin,String endBin,String accountId,String memberId, String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        return whiteListDAO.getWhiteListBin(startBin, endBin, accountId, memberId);
    }
    public List<WhitelistingDetailsVO> getWhiteListBinPage(String startBin,String endBin,String startCard,String endCard,String accountId,String memberId,String actionExecutorId,String actionExecutorName,PaginationVO paginationVo) throws PZDBViolationException
    {
        return whiteListDAO.getWhiteListBinPage(startBin, endBin,startCard,endCard, accountId, memberId,actionExecutorId,actionExecutorName, paginationVo);
    }
    public int addBin(String startBin,String endBin,String startCard,String endCard,String accountId,String memberId, String actionExecutorId,String actionExecutorName)throws PZDBViolationException
    {
        List<WhitelistingDetailsVO> listVO  = new ArrayList();
        PaginationVO paginationVO           = null;
        listVO      = whiteListDAO.getWhiteListBinPage(startBin, endBin,startCard,endCard, accountId, memberId,actionExecutorId, actionExecutorName,paginationVO);
        int count   = 0;
        if(listVO.size() ==1 )
        {
            return count;
        }
        else
        {
            count = whiteListDAO.addBin(startBin,endBin,startCard,endCard,accountId,memberId,actionExecutorId,actionExecutorName);
        }
        return count;
    }
    public void unblockBin(String startBin,String endBin,String accountId,String memberId) throws PZDBViolationException
    {
        whiteListDAO.unblockBin(startBin,endBin,accountId,memberId);
    }
    public boolean isRecordAvailableOnOtherGroup(String firstSix, String lastFour, String gateway, String companyName) throws PZDBViolationException
    {
        return whiteListDAO.isRecordAvailableOnOtherGroup(firstSix, lastFour, gateway, companyName);
    }
    public boolean isRecordAvailableOnOtherGroup(String firstSix, String lastFour, String emailAddress, String gateway, String companyName) throws PZDBViolationException
    {
        return whiteListDAO.isRecordAvailableOnOtherGroup(firstSix, lastFour, emailAddress, gateway, companyName);
    }
    public int uploadBins(List<String> queryBatch)throws PZDBViolationException
    {
        return whiteListDAO.uploadBins(queryBatch);
    }
    public String isRecordAvailableForPerMember(String memberId, String accountId, String firstSix, String lastFour,String cardHolderName,String ipAddress,String expiryDate) throws PZDBViolationException
    {
        return whiteListDAO.isRecordAvailableForPerMember(memberId,accountId,firstSix,lastFour,cardHolderName,ipAddress,expiryDate);
    }
    public String isRecordAvailableForPerMember(String memberId, String accountId, String firstSix, String lastFour, String emailAddress,String cardHolderName,String ipAddress,String expiryDate) throws PZDBViolationException
    {
        return whiteListDAO.isRecordAvailableForPerMember(memberId,accountId,firstSix,lastFour,emailAddress,cardHolderName,ipAddress,expiryDate);
    }
    public boolean insertBinCountryRoutingDetails(String memberId,String accountId,List<String> countryList,String actionExecutorId,String actionExecutorName)
    {
        return whiteListDAO.insertBinCountryRoutingDetails(memberId,accountId,countryList,actionExecutorId,actionExecutorName);
    }
    public List<String> getWhiteListBinCountry(String country,String accountId,String memberId,String actionExecutorId,String actionExecutorName) throws PZDBViolationException
    {
        return whiteListDAO.getWhiteListBinCountry(country,accountId,memberId,actionExecutorId,actionExecutorName);
    }
    public boolean deleteBinCountry(String id )
    {
        return whiteListDAO.deleteBinCountry(id);
    }
    public List<WhitelistingDetailsVO> getWhiteListBinCountryPage (String country, String accountId, String memberId,String actionExecutorId,String actionExecutorName, PaginationVO paginationVO)throws PZDBViolationException
    {
       return whiteListDAO.getWhiteListBinCountryPage(country,accountId,memberId,actionExecutorId,actionExecutorName,paginationVO);
    }
    public String getWhitelistingLevel(String memberId)throws PZDBViolationException
    {
        return whiteListDAO.getWhitelistingLevel(memberId);
    }
    public boolean isRecordAvailableInSystemCustomerLevel(String firstSix, String lastFour, String emailAddress,String customerId,String memberId,String accountId)throws PZDBViolationException
    {
        return whiteListDAO.isRecordAvailableInSystemCustomerLevel(firstSix,lastFour,emailAddress,customerId,memberId,accountId);
    }
    public boolean addCardForCustomer(String firstSix, String lastFour, String emailAddr, String accountId, String memberId,String customerId) throws PZDBViolationException
    {
        return whiteListDAO.addCardForCustomer(firstSix, lastFour, emailAddr, accountId, memberId,customerId);
    }
}

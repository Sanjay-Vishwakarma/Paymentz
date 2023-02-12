package com.manager;

import com.directi.pg.Logger;
import com.manager.dao.RecurringDAO;
import com.manager.vo.PaginationVO;
import com.manager.vo.RecurringBillingVO;
import com.payment.exceptionHandler.PZDBViolationException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 3/27/15
 * Time: 3:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecurringManager
{
    private static Logger log = new Logger(RecurringManager.class.getName());
    RecurringDAO recurringDAO = new RecurringDAO();
    RecurringBillingVO recurringBillingVO = new RecurringBillingVO();

    public void insertRecurringDetailsEntry(RecurringBillingVO recurringBillingVO) throws PZDBViolationException
    {
        recurringDAO.insertRecurringTransactionEntry(recurringBillingVO);
    }

    public void insertRecurringSubscriptionEntry(RecurringBillingVO recurringBillingVO) throws PZDBViolationException
    {
        recurringDAO.insertRecurringSubscriptionDetails(recurringBillingVO);
    }

    public List<RecurringBillingVO> getRBSubscriptionDetails(RecurringBillingVO recurringBillingVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        /*RecurringListVO recurringListVO = new RecurringListVO();
        recurringListVO.setRecurringBillingVOList(recurringDAO.getRecurringSubscriptionDetails(trackingid, rbid, memberid,paginationVO));
        log.debug("recurring total size---" + recurringListVO.getRecurringBillingVOList().size());
        log.debug("recurring total size---"+paginationVO.getTotalRecords());
        recurringListVO.setPaginationVO(paginationVO);
        return recurringListVO;*/

        List<RecurringBillingVO> listRBVO = recurringDAO.getRecurringSubscriptionDetails(recurringBillingVO,paginationVO);
        //paginationVO.setTotalRecords(listRBVO.size());
        return listRBVO;
    }

    public List<RecurringBillingVO> getRecurringTransactionList(String rbid) throws PZDBViolationException
    {
        return recurringDAO.getRecurringTransactionDetailsfromRBID(rbid);
    }

    public void updateRecurringSubDetails(RecurringBillingVO recurringBillingVO,String rbid)throws PZDBViolationException
    {
        recurringDAO.updateRecurringSubscriptionDetails(recurringBillingVO,rbid);
    }

    public void deleteRecurringDetails(String rbid)throws PZDBViolationException
    {
        recurringDAO.deleteRecurringSubscription(rbid);
    }

    public void updateRecurringStatusDetails(RecurringBillingVO recurringBillingVO,String rbid)throws PZDBViolationException
    {
        recurringDAO.updateActivateDeactivateStatus(recurringBillingVO,rbid);
    }

    public void updateRbidForSuccessfullRebill(String rbid,String first_six,String last_four, String trackingID) throws PZDBViolationException
    {
        recurringDAO.updateRbidForSuccessfullRebill(rbid, first_six, last_four, trackingID);
    }

    public void updateSubscriptionAfterBankCall(String rbid, String first_six, String last_four, String trackingId, String bankTransactionId) throws PZDBViolationException
    {
        recurringDAO.updateSubscriptionAfterBankCall(rbid, first_six, last_four, trackingId, bankTransactionId);
    }
    public void insertEntryForPFSManualRebill ( String trackingId,String amount,String name,String first_six,String last_four,String rbid) throws PZDBViolationException
    {
        recurringDAO.insertEntryForPFSManualRebill(trackingId,amount,name,first_six,last_four,rbid);
    }

    public void deleteEntryForPFSRebill(String originatingTrackingId) throws PZDBViolationException
    {
        recurringDAO.deleteEntryForPFSRebill(originatingTrackingId);
    }
    public Boolean isRecurringTrackingIdMatchesWithMerchantToken(String token,String originatingTrackingid,String toid) throws PZDBViolationException
    {
        return recurringDAO.isRecurringTrackingIdMatchesWithMerchantToken(token, originatingTrackingid,toid);
    }
    public Boolean isRecurringTrackingIdMatchesPartnerWithToken(String token,String originatingTrackingid,String partnerId) throws PZDBViolationException
    {
        return recurringDAO.isRecurringTrackingIdMatchesPartnerWithToken(token, originatingTrackingid, partnerId);
    }
    public List<RecurringBillingVO> getRBSubscriptionDetailsPartner(RecurringBillingVO recurringBillingVO,PaginationVO paginationVO) throws PZDBViolationException
    {
        List<RecurringBillingVO> listRBVO = recurringDAO.getRecurringSubscriptionDetailsPartner(recurringBillingVO,paginationVO);
        return listRBVO;
    }
}

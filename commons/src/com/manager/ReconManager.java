package com.manager;

import com.manager.dao.ReconDao;
import com.manager.vo.ManualReconVO;
import com.payment.exceptionHandler.PZDBViolationException;
import java.util.Hashtable;
/**
 * Created by Jitendra on 4/4/2018.
 */

public class ReconManager
{
    private ReconDao reconDao=new ReconDao();

    public Hashtable getReconListTransaction(String accountId,String partnerId, String fromDate1, String toDate1, String memberId, StringBuffer trackingId,String paymentId ,String orderId, String status, int start, int end)throws  PZDBViolationException
    {
        return reconDao.getReconListTransaction(accountId,partnerId,fromDate1,toDate1,memberId,trackingId,paymentId,orderId,status,start,end);
    }

    public boolean giveFailedTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        return reconDao.giveFailedTreatment(manualReconVO);
    }

    public boolean giveAuthFailedTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        return reconDao.giveAuthFailedTreatment(manualReconVO);
    }
    public boolean giveAuthSuccessTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        return reconDao.giveAuthSuccessTreatment(manualReconVO);
    }

    public boolean giveAuthStartedTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        return reconDao.giveAuthStartedTreatment(manualReconVO);
    }

    public boolean giveAuthStarted3dTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        return reconDao.giveAuthStarted3DTreatment(manualReconVO);
    }

    public boolean giveCaptureSuccessTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        return reconDao.giveCaptureSuccessTreatment(manualReconVO);
    }

    public boolean giveCaptureFailedTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        return reconDao.giveCaptureFailedTreatment(manualReconVO);
    }

    public boolean giveReversedTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        return reconDao.giveReversedTreatment(manualReconVO);
    }
    public boolean giveChargebackReversedTreatment(ManualReconVO manualReconVO)throws PZDBViolationException
    {
        return reconDao.giveChargebackReversedTreatment(manualReconVO);
    }

}
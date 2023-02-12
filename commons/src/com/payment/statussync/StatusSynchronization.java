package com.payment.statussync;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;

import java.sql.Connection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 7/10/14
 * Time: 3:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class StatusSynchronization
{
    private static Logger log = new Logger(StatusSynchronization.class.getName());
    private static SystemAccessLogger accessLogger = new SystemAccessLogger(StatusSynchronization.class.getName());
    Functions functions=new Functions();
    public String allTransactionSynchronizationCron(String fromDate,String toDtate)
    {
        StatusSyncDAO statusSyncDAO= new StatusSyncDAO();
        String resMsg="";
        Connection connection=null;
        //MailService mailService=new MailService();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        HashMap transactionResultList=new HashMap();
        try
        {
            String previousFlagStatus="";
            String updatedFlagStatus="";

            connection= Database.getConnection();
            List<StatusSyncVO> statusSyncVOList = statusSyncDAO.getTransactionlist(fromDate,toDtate,connection);
            for(StatusSyncVO statusSyncVO : statusSyncVOList)
            {
                String binEntryMsg="Record Exist";
                if(!statusSyncDAO.isTransPresentInBinDetail(statusSyncVO.getTrackingId(),connection))
                {
                    statusSyncDAO.insertInToBinDetails(statusSyncVO,connection);
                    binEntryMsg="Record Inserted";
                }

                previousFlagStatus = statusSyncDAO.getFlagStatus(statusSyncVO.getTrackingId(),connection);
                statusSyncDAO.updateStatusSyncTransactionCronFlag(statusSyncVO.getTrackingId(),statusSyncVO.getStatus(),connection);
                updatedFlagStatus = statusSyncDAO.getFlagStatus(statusSyncVO.getTrackingId(),connection);

                LinkedHashMap transresult=new LinkedHashMap();
                transresult.put("TRACKINGID",statusSyncVO.getTrackingId());
                transresult.put("ACCOUNTID",functions.isValueNull(statusSyncVO.getAccountId()) ? statusSyncVO.getAccountId() : "-");
                transresult.put("STATUS",statusSyncVO.getStatus());
                transresult.put("BINDETAIL RECORD STATUS",binEntryMsg);
                transresult.put("PREVIOUS FLAG STATUS",previousFlagStatus);
                transresult.put("CURRENT FLAG STATUS",updatedFlagStatus);
                transactionResultList.put(statusSyncVO.getTrackingId(),transresult);
            }
            if(statusSyncVOList.size()>0)
                resMsg = asynchronousMailService.getDetailTable(transactionResultList);
            else
                resMsg = "";
            transactionResultList.put(MailPlaceHolder.DATE,fromDate+"     TO "+toDtate);
            transactionResultList.put(MailPlaceHolder.MULTIPALTRANSACTION,resMsg);
        }
        catch (Exception systemError)
        {
            log.error("SystemError",systemError);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        asynchronousMailService.sendMerchantSignup(MailEventEnum.ADMIN_STATUS_SYNCRONIZATION_CRON,transactionResultList);
        return resMsg;
    }
}

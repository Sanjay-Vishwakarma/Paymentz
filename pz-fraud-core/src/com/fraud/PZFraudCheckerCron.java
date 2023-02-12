package com.fraud;
import com.directi.pg.Logger;
import com.fraud.utils.ReadFraudServiceRequest;
import com.fraud.vo.PZFraudRequestVO;
import com.fraud.vo.PZFraudResponseVO;
import com.manager.dao.FraudTransactionDAO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 9/18/14
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZFraudCheckerCron
{
    static Logger logger = new Logger(PZFraudCheckerCron.class.getName());
    FraudTransactionDAO fraudTransactionDAO =new FraudTransactionDAO();
    FraudUtils fraudUtils=new FraudUtils();
    public void newTransaction()
    {
        //Step1:Get All The Active Members
        String accountId=null;
        String tableName=null;
        PZFraudProcessor pzFraudProcessor=new PZFraudProcessor();
        Set<String> set=fraudTransactionDAO.getMembersForOfflineFraudCheck();
        Iterator iterator=set.iterator();
        while(iterator.hasNext())
        {
            String memberId=(String)iterator.next();
            logger.debug("Member for offline fraud transaction check=="+memberId);

            //Step2:Get Fraud System Id To Call Fraud Processor
            int fsid=fraudTransactionDAO.getFraudSystemIdByMemberId(memberId);

            //Step3:Check Current Fraud System Work For Offline Fraud Check
            boolean isWorkOffline=fraudTransactionDAO.isFraudSystemWorkOffline(fsid);
            if(isWorkOffline==false)
            {
               logger.debug(FraudSystemService.getFSGateway(String.valueOf(fsid))+" Not Set For Offline Fraud Checking So Skip Fraud Checking");
               continue;
            }

            //Step4: Getting AccountId For Active MemberId
            accountId= fraudTransactionDAO.getAccountIdByMemberId(memberId);
            if(accountId==null)
            {
               logger.debug("Members AccountId Is Not Found So Skip Fraud Checking");
               continue;
            }

            //Step5: Getting tableName based on accountId
            tableName=fraudUtils.getTableNameFromAccountId(accountId);
            if(tableName==null || "transaction_icicicredit".equals(tableName))
            {
                logger.debug("Member "+memberId +" trans in"+tableName+" So Skip Fraud Checking");
                continue;
            }

            Set failedTransId=fraudTransactionDAO.getFraudCheckFailedTrackingId(memberId,accountId,"Process Failed");
            Iterator itr=failedTransId.iterator();
            while (itr.hasNext())
            {
                String failedTrackingId=(String)itr.next();
                logger.debug("Process Failed Trackiong Id=="+failedTrackingId);
                HashMap transDetail=fraudTransactionDAO.getTransactionDetailByTrackingId(tableName,failedTrackingId);
                if(transDetail!=null)
                {
                    ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();
                    PZFraudRequestVO requestVO= readFraudServiceRequest.getPZFraudRequestVO(transDetail);
                    PZFraudResponseVO pzFraudResponseVO=pzFraudProcessor.newTransaction(requestVO,String.valueOf(fsid));
                }
            }


            //Step6: Getting MaxTracking Id From fraud_transaction table
            int sMaxTrackingId= fraudTransactionDAO.getMaxTrackingIdByStatus(memberId,accountId,"Process Successfully");
            if(sMaxTrackingId==0)
            {
                //Step6: Getting first tracking Id Of member
                sMaxTrackingId= fraudTransactionDAO.getMemberFirstTransaction(memberId,accountId,tableName);
            }
            logger.debug("Tracking Id To Pick Up The Transaction"+sMaxTrackingId);
            //Step7: Fraud Checking process
            ArrayList<HashMap> transList = null;
            if("transaction_common".equals(tableName))
            {
                logger.debug("Enter Into Table===="+tableName);
                logger.debug("Member Id====="+memberId);
                transList = fraudTransactionDAO.getTransDetailsForFraudCheckByMemberId(tableName, sMaxTrackingId, memberId,accountId);
                logger.debug("transactions founds===="+transList.size());
                for(HashMap transactionDetails: transList)
                {
                    ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();
                    PZFraudRequestVO requestVO= readFraudServiceRequest.getPZFraudRequestVO(transactionDetails);
                    PZFraudResponseVO pzFraudResponseVO=pzFraudProcessor.newTransaction(requestVO,String.valueOf(fsid));
                    if(pzFraudResponseVO.getErrorList().size()>0)
                    {
                       logger.debug("Transaction Not Received By PZFraudProcessor COZ=="+pzFraudResponseVO.getErrorList());
                    }
                    /*if(pzFraudResponseVO.getResponseCode().equals("0"))
                    {
                        if(Integer.parseInt(pzFraudResponseVO.getScore())>Integer.parseInt(fraudTransactionDAO.getMerchantAutoReversalScore(memberId)))
                        {
                            logger.debug("Transaction Must Be Refunded");
                            //Write Code Here For Refund the transaction
                        }
                    }*/

                }
            }
            if("transaction_ecore".equals(tableName))
            {
                logger.debug("Enter Into Table===="+tableName);
                logger.debug("Member Id====="+memberId);
                transList = fraudTransactionDAO.getTransDetailsForFraudCheckByMemberId(tableName, sMaxTrackingId, memberId,accountId);
                logger.debug("transactions founds===="+transList.size());
                for(HashMap transactionDetails: transList)
                {
                    ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();
                    PZFraudRequestVO requestVO= readFraudServiceRequest.getPZFraudRequestVO(transactionDetails);
                    PZFraudResponseVO pzFraudResponseVO=pzFraudProcessor.newTransaction(requestVO,String.valueOf(fsid));
                    if(pzFraudResponseVO.getErrorList().size()>0)
                    {
                        logger.debug("Transaction Not Received By PZFraudProcessor COZ=="+pzFraudResponseVO.getErrorList());
                    }
                    /*if(pzFraudResponseVO.getResponseCode().equals("0"))
                    {
                        if(Integer.parseInt(pzFraudResponseVO.getScore())>Integer.parseInt(fraudTransactionDAO.getMerchantAutoReversalScore(memberId)))
                        {
                            logger.debug("Transaction Must Be Refunded");
                            //Write Code Here For Refund the transaction
                        }
                    }*/
                }
            }
            if("transaction_qwipi".equals(tableName))
            {
                logger.debug("Enter Into Table===="+tableName);
                logger.debug("Member Id====="+memberId);
                transList = fraudTransactionDAO.getTransDetailsForFraudCheckByMemberId(tableName, sMaxTrackingId, memberId,accountId);
                logger.debug("transactions founds===="+transList.size());
                for(HashMap transactionDetails: transList)
                {
                    ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();
                    PZFraudRequestVO requestVO= readFraudServiceRequest.getPZFraudRequestVO(transactionDetails);
                    PZFraudResponseVO pzFraudResponseVO=pzFraudProcessor.newTransaction(requestVO,String.valueOf(fsid));
                    if(pzFraudResponseVO.getErrorList().size()>0)
                    {
                        logger.debug("Transaction Not Received By PZFraudProcessor COZ=="+pzFraudResponseVO.getErrorList());
                    }
                    /*logger.debug("PZFraud Error List==== "+pzFraudResponseVO.getErrorList());
                    if(pzFraudResponseVO.getResponseCode().equals("0"))
                    {
                        if(Integer.parseInt(pzFraudResponseVO.getScore())>Integer.parseInt(fraudTransactionDAO.getMerchantAutoReversalScore(memberId)))
                        {
                            logger.debug("Transaction Must Be Refunded");
                            //Write Code Here For Refund the transaction
                        }
                    }*/
                }
            }

        }

    }
    public void updateTransaction()
    {
        PZFraudProcessor pzFraudProcessor=new PZFraudProcessor();
        PZFraudRequestVO pzFraudRequestVO=new PZFraudRequestVO();
        //Step1:Get All The Active Members
        String accountId=null;
        String tableName=null;
        Set<String> set=fraudTransactionDAO.getMembersForOfflineFraudCheck();
        Iterator iterator=set.iterator();
        while(iterator.hasNext())
        {
            String memberId=(String)iterator.next();
            //Step2:Get Fraud System Id To Call Fraud Processor
            int fsid=fraudTransactionDAO.getFraudSystemIdByMemberId(memberId);

            //Step3:Check Current Fraud System Work For Offline Fraud Check
            boolean isWorkOffline=fraudTransactionDAO.isFraudSystemWorkOffline(fsid);
            if(isWorkOffline==false)
                continue;

            //Step4: Getting AccountId For Active MemberId
            accountId= fraudTransactionDAO.getAccountIdByMemberId(memberId);
            if(accountId==null)
                continue;

            //Step5: Getting tableName based on accountId
            tableName=fraudUtils.getTableNameFromAccountId(accountId);
            if(tableName==null || "transaction_icicicredit".equals(tableName))
                continue;

            ArrayList<HashMap> listOfTransactions=fraudTransactionDAO.getTransDetailsFraudUpdateToAT(memberId,tableName);
            for(HashMap transDetails: listOfTransactions)
            {
                pzFraudRequestVO.setFstransid((String) transDetails.get("fstransid"));
                pzFraudRequestVO.setPzfraudtransid((String) transDetails.get("fraudtransid"));
                pzFraudRequestVO.setStatus((String) transDetails.get("status"));
                PZFraudResponseVO pzFraudResponseVO=pzFraudProcessor.updateTransaction(pzFraudRequestVO,String.valueOf(fsid));
                logger.debug("Response Code====="+pzFraudResponseVO.getResponseCode());
                logger.debug("Response Description====="+pzFraudResponseVO.getDescription());
                logger.debug("Response ErrorList====="+pzFraudResponseVO.getErrorList());
            }

        }

    }




}

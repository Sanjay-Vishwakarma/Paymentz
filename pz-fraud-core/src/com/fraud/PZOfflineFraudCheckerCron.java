package com.fraud;
import com.directi.pg.Logger;
import com.fraud.utils.ReadFraudServiceRequest;
import com.fraud.vo.PZFraudRequestVO;
import com.fraud.vo.PZFraudResponseVO;
import com.manager.dao.FraudTransactionDAO;
import com.manager.dao.MerchantDAO;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 9/18/14
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZOfflineFraudCheckerCron
{
    static Logger logger = new Logger(PZOfflineFraudCheckerCron.class.getName());
    FraudTransactionDAO fraudTransactionDAO =new FraudTransactionDAO();
    FraudUtils fraudUtils=new FraudUtils();
    MerchantDAO merchantDAO=new MerchantDAO();
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
                continue;
            //Check for fraud account mappped with merchant
            FraudAccountDetailsVO accountDetailsVO=merchantDAO.getFraudAccountDetails(memberId);
            if(accountDetailsVO==null)
            {
                logger.debug("Fraud account is not found for==="+memberId);
                continue;
            }

            //Step4: Getting AccountId For Active MemberId
            Set<String>  accountIds=fraudTransactionDAO.getAccountsIdByMemberId(memberId);
            Iterator accountIdsItr=accountIds.iterator();
            logger.debug(memberId+" Mapped With Account====="+accountIds);
            while (accountIdsItr.hasNext())
            {
                accountId=(String)accountIdsItr.next();

                //Step5: Getting tableName based on accountId
                tableName=fraudUtils.getTableNameFromAccountId(accountId);
                if(tableName==null || "transaction_icicicredit".equals(tableName))
                    continue;

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
                        requestVO.setFraudAccountDetailsVO(accountDetailsVO);
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
                    logger.debug("AccountId====="+accountId);
                    transList = fraudTransactionDAO.getTransDetailsForFraudCheckByMemberId(tableName, sMaxTrackingId, memberId,accountId);
                    logger.debug("transactions founds===="+transList.size());
                    for(HashMap transactionDetails: transList)
                    {
                        ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();
                        PZFraudRequestVO requestVO= readFraudServiceRequest.getPZFraudRequestVO(transactionDetails);
                        requestVO.setFraudAccountDetailsVO(accountDetailsVO);
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
                    logger.debug("AccountId====="+accountId);
                    transList = fraudTransactionDAO.getTransDetailsForFraudCheckByMemberId(tableName, sMaxTrackingId, memberId,accountId);
                    logger.debug("transactions founds===="+transList.size());
                    for(HashMap transactionDetails: transList)
                    {
                        ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();
                        PZFraudRequestVO requestVO= readFraudServiceRequest.getPZFraudRequestVO(transactionDetails);
                        requestVO.setFraudAccountDetailsVO(accountDetailsVO);
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
                    logger.debug("AccountId====="+accountId);
                    transList = fraudTransactionDAO.getTransDetailsForFraudCheckByMemberId(tableName, sMaxTrackingId, memberId,accountId);
                    logger.debug("transactions founds===="+transList.size());
                    for(HashMap transactionDetails: transList)
                    {
                        ReadFraudServiceRequest readFraudServiceRequest =new ReadFraudServiceRequest();
                        PZFraudRequestVO requestVO= readFraudServiceRequest.getPZFraudRequestVO(transactionDetails);
                        requestVO.setFraudAccountDetailsVO(accountDetailsVO);
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
            Set<String>  accountIds=fraudTransactionDAO.getAccountsIdByMemberId(memberId);
            Iterator accountIdsItr=accountIds.iterator();
            logger.debug(memberId+" Mapped With Account====="+accountIds);
            while (accountIdsItr.hasNext())
            {
                accountId=(String)accountIdsItr.next();

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
    public void sendFraudTransactionAlert()
    {
        Map<String,Map<String,Map<String,Map<String,String>>>> list=fraudTransactionDAO.getHighRiskTransactionDetailsForAlertCron();

        Map<String,Map<String,Map<String,String>>> fraudRefundTransDetails=list.get("merchantsRefundTransactions");

        Map<String,Map<String,Map<String,String>>> fraudIntimationTransDetails=list.get("merchantsFraudIntimationTransactions");

        Map<String,Map<String,Map<String,String>>> fraudFailureTransDetails=list.get("adminFraudIntimationTransactions");

        sendFraudIntimationAlertToMerchants(fraudIntimationTransDetails);

        sendRefundIntimationAlertToMerchants(fraudRefundTransDetails);

        sendFraudIntimationToAdmin(fraudFailureTransDetails);

    }
    private void sendFraudIntimationAlertToMerchants(Map<String, Map<String, Map<String, String>>> highRiskTransListForMerchant)
    {
        MailService mailService=new MailService();

        Set s=highRiskTransListForMerchant.keySet();

        Iterator itr=s.iterator();

        String memberid=null;

        Map<String,Map<String,String>> transactiondetails=null;

        while(itr.hasNext())
        {
            memberid=itr.next().toString();
            transactiondetails=highRiskTransListForMerchant.get(memberid);
            Set trankingIds=transactiondetails.keySet();

            HashMap cbCountRecord=new HashMap();
            cbCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTable(transactiondetails));
            cbCountRecord.put(MailPlaceHolder.TOID,memberid.toString());
            mailService.sendMail(MailEventEnum.MEMBER_DAILY_STATUS_FRAUD_REPORT, cbCountRecord);
            fraudTransactionDAO.updateIsAlertSentStatus(trankingIds);
        }
    }

    private void sendRefundIntimationAlertToMerchants(Map<String, Map<String, Map<String, String>>> highRiskTransListForMerchant)
    {
        MailService mailService=new MailService();

        Set s=highRiskTransListForMerchant.keySet();

        Iterator itr=s.iterator();

        String memberid=null;

        Map<String,Map<String,String>> transactiondetails=null;

        while(itr.hasNext())
        {
            memberid=itr.next().toString();
            transactiondetails=highRiskTransListForMerchant.get(memberid);
            Set trankingIds=transactiondetails.keySet();

            HashMap cbCountRecord=new HashMap();
            cbCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTable(transactiondetails));
            cbCountRecord.put(MailPlaceHolder.TOID,memberid.toString());
            mailService.sendMail(MailEventEnum.HIGH_RISK_REFUND_TRANSACTION_INTIMATION,cbCountRecord);
            fraudTransactionDAO.updateIsAlertSentForRefund(trankingIds);
        }
    }

    private void sendFraudIntimationToAdmin(Map<String, Map<String, Map<String, String>>> merchantsFailureTransaction)
    {
        Map<String,Map<String,String>> transactiondetails=null;
        Map<String,Map<String,String>> newtransactiondetails=new HashMap<String, Map<String, String>>();
        Map<String,Map<String,String>> list=null;
        Set set=merchantsFailureTransaction.keySet();
        Iterator itr=set.iterator();
        String memberid=null;
        while(itr.hasNext())
        {
            memberid=itr.next().toString();
            transactiondetails=merchantsFailureTransaction.get(memberid);
            Set innerSet=transactiondetails.keySet();
            Iterator innerItr=innerSet.iterator();
            while(innerItr.hasNext())
            {
                Map<String,String> transDetails=new HashMap<String,String>();
                String trakingId=(String)innerItr.next();
                transDetails=transactiondetails.get(trakingId);
                newtransactiondetails.put(trakingId,transDetails);
            }
        }
        if(newtransactiondetails.size()>0)
        {
            MailService mailService=new MailService();
            HashMap cbCountRecord=new HashMap();
            cbCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getDetailTable(newtransactiondetails));
            cbCountRecord.put(MailPlaceHolder.TOID,memberid.toString());
            cbCountRecord.put(MailPlaceHolder.TODATE,"13-06-1014");
            cbCountRecord.put(MailPlaceHolder.FROMDATE,"13-06-1013");
            Set trackingIds=newtransactiondetails.keySet();
            mailService.sendMail(MailEventEnum.HIGH_RISK_FRAUD_TRANSACTION_INTIMATION,cbCountRecord);
            //fraudTransactionDAO.updateIsAlertSentStatus(trackingIds);
        }

    }



}

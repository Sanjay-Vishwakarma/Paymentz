package com.payment.fraudAPI;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.core.GatewayTypeService;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.ecore.core.EcorePaymentProcess;
import com.payment.ecore.core.request.EcoreRefundRequest;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZRefundResponse;
import com.payment.response.PZResponseStatus;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: sandip
 * Date: 5/20/14
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class ATFraudCheckerCron
{
    private static Logger log = new Logger(ATFraudCheckerCron.class.getName());

    private static String merchant_id = "INT-V8224F32s78416U";

    private static String password = "JbYKc7OxE7sJran";

    FraudTransactionDBA ftd = new FraudTransactionDBA();

    ATRiskAPI at = new ATRiskAPI();

    public void newTransactionAPI() throws SQLException
    {
        int defaultTrackingId = 1000;

        int maxTrackingId = ftd.getMaxTrackingidForFraud();

        if(maxTrackingId==0)
        {
            maxTrackingId=defaultTrackingId;
        }

        /* log.debug("Max Trackingid = " + maxTrackingId);*/

        Set<String> gateways = GatewayTypeService.getGateways();

        Set<String> tablenames = Database.getTableSet(gateways);

        Hashtable refundDetails;

        Set reversedTransaction=new HashSet();

        for(String tableName : tablenames)
        {
            ArrayList<HashMap> transList = null;
            if("transaction_common".equals(tableName))
            {
                transList = ftd.getTransactionDetailsForFraudCheck(tableName, maxTrackingId);

                /*System.out.println("Total Transactions get from the transaction_common"+transList.size());*/
                 if(transList.size()>0)
                 {
                     for(HashMap transDetails: transList)
                     {
                          if(checkNewTransaction(transDetails))
                         {
                             try
                             {
                                 refundDetails=reverseForCommon((String)transDetails.get("trackingid"),(String)transDetails.get("memberid"),(String)transDetails.get("accountId"),(String)transDetails.get("amount"),"Fraud Transaction");

                                 reversedTransaction.add((String)refundDetails.get("trackingid"));

                                 log.debug("Reversed Successfully form transaction_common======trackingId"+(String)refundDetails.get("trackingid"));

                                 /* System.out.println("Reversed Successfully form transaction_common======trackingId"+(String)refundDetails.get("trackingid"));*/
                             }
                             catch (SystemError e)
                             {
                                 log.debug("Reverse Error from transaction_common======trackingId"+(String)transDetails.get("trackingid"));

                                 /*System.out.println("Reverse Error from transaction_common======trackingId"+(String)transDetails.get("trackingid"));*/
                             }
                         }
                     }

                 }

            }
            if("transaction_ecore".equals(tableName))
            {
                transList = ftd.getTransactionDetailsForFraudCheck(tableName, maxTrackingId);

                /*System.out.println("Total Transactions get from the transaction_ecore"+transList.size());*/

                if(transList.size()>0)
                {
                    for(HashMap transDetails: transList)
                    {
                        if(checkNewTransaction(transDetails))
                        {
                            try
                            {
                                refundDetails=reverseForEcore((String)transDetails.get("memberid"),(String)transDetails.get("accountId"),(String)transDetails.get("trackingid"),(String)transDetails.get("amount"),"Farud Reason");

                                reversedTransaction.add((String)refundDetails.get("trackingid"));

                                log.debug("Reversed Successfully form transaction_ecore======trackingId"+(String)refundDetails.get("trackingid"));

                                /*System.out.println("Reversed Successfully form transaction_ecore======trackingId"+(String)refundDetails.get("trackingid"));*/

                            }
                            catch (SystemError e)
                            {
                                log.debug("Reverse Error from transaction_ecore======trackingId"+(String)transDetails.get("trackingid"));

                                /* System.out.println("Reverse Error from transaction_ecore======trackingId"+(String)transDetails.get("trackingid"));*/
                            }
                        }
                    }
                }

            }
            if("transaction_qwipi".equals(tableName))
            {
                transList = ftd.getTransactionDetailsForFraudCheck(tableName, maxTrackingId);

               //* System.out.println("Total Transactions get from the transaction_qwipi"+transList.size());*//*

                if(transList.size()>0)
                {
                    for(HashMap transDetails: transList)
                    {
                        if(checkNewTransaction(transDetails))
                        {
                            try
                            {
                                refundDetails=reverseForQwipi((String)transDetails.get("trackingid"),(String)transDetails.get("amount"));

                                reversedTransaction.add((String)refundDetails.get("trackingid"));

                                log.debug("Reversed Successfully form transaction_qwipi======trackingId"+(String)refundDetails.get("trackingid"));

                            }
                            catch (SystemError e)
                            {
                                log.debug("Reverse Error from transaction_qwipi======trackingId"+(String)transDetails.get("trackingid"));

                            }
                        }
                    }
                }

            }

        }
        if(reversedTransaction.size()>0)
        {
            ftd.updateIsReversedStatus(reversedTransaction);
            log.debug("Total Successfully Reversed Transactions=====:"+reversedTransaction.size());
        }

    }

    public void updateTransactionAPI()
    {
        Set<String> gateways = GatewayTypeService.getGateways();

        Set<String> tablenames = Database.getTableSet(gateways);

        for(String tableName : tablenames)
        {
            if(!("transaction_icicicredit").equals(tableName))
            {
                ArrayList<HashMap> listOfTransactions= ftd.getTransactionDetailsForFraudCheckUpdate(tableName);

                for(HashMap transDetails: listOfTransactions)
                {
                    transDetails.put("pzstatus",(String)transDetails.get("status"));
                    transDetails.put("status",getATStatus(getPZTransactionStatus((String)transDetails.get("status"))));
                    //transDetails.put("status",getATStatus(PZTransactionStatus.CHARGEBACK));
                    transDetails.put("reason","Not Provided");

                    checkUpdateTransaction(transDetails,(String)transDetails.get("status"));
                }
            }
        }
    }

    public void sendFraudTransactionAlert()
    {
        Map<String,Map<String,Map<String,Map<String,String>>>> list=ftd.getHighRiskTransactionDetailsForAlertCron();

        Map<String,Map<String,Map<String,String>>> fraudRefundTransDetails=list.get("merchantsRefundTransactions");

        Map<String,Map<String,Map<String,String>>> fraudIntimationTransDetails=list.get("merchantsFraudIntimationTransactions");

        Map<String,Map<String,Map<String,String>>> fraudFailureTransDetails=list.get("adminFraudIntimationTransactions");

        sendFraudIntimationAlertToMerchants(fraudIntimationTransDetails);

        sendRefundIntimationAlertToMerchants(fraudRefundTransDetails);

        sendFailureIntimationToAdmin(fraudFailureTransDetails);

    }

    private Hashtable reverseForCommon(String trackingid,String toid,String accountId,String refundamount,String refundreason)  throws SystemError
    {
        PZRefundRequest refundRequest = new PZRefundRequest();
        PZRefundResponse response = new PZRefundResponse();
        Hashtable refundDetails = null;
        try
        {
            AbstractPaymentProcess process = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingid), Integer.parseInt(accountId));
            InetAddress ip=InetAddress.getLocalHost();
            refundRequest.setMemberId(Integer.valueOf(toid));
            refundRequest.setAccountId(Integer.parseInt(accountId));
            refundRequest.setTrackingId(Integer.parseInt(trackingid));
            refundRequest.setRefundAmount(refundamount);
            refundRequest.setRefundReason(refundreason);
            refundRequest.setAdmin(false);
            refundRequest.setIpAddress(ip.getHostAddress());
            refundRequest.setFraud(true);
            //getting responce
            response = process.refund(refundRequest);
            PZResponseStatus status = response.getStatus();

            /*System.out.println("Payment Gateway Response======:"+response.getResponseDesceiption());*/

            if (PZResponseStatus.ERROR.equals(status))
            {
                throw new SystemError();
            }
            else if (PZResponseStatus.FAILED.equals(status))
            {
                throw new SystemError();
            }
            if (response != null && (response.getStatus()).equals(PZResponseStatus.SUCCESS))
            {
                refundDetails = new Hashtable();
                refundDetails.put("trackingid",trackingid);
                refundDetails.put("description",response.getResponseDesceiption());

            }
            else
            {
                throw new SystemError();
            }
        }
        catch(Exception e)
        {
            throw new SystemError();
        }
        return  refundDetails;
    }

    private Hashtable reverseForEcore(String toid,String accountId,String trackingId,String refundAmount,String refundReason)  throws SystemError
    {

        Hashtable refundDetails=null;
        try
        {
            EcoreRefundRequest refundRequest= new EcoreRefundRequest();
            AbstractPaymentProcess payment  = new EcorePaymentProcess();

            refundRequest.setMemberId(Integer.valueOf(toid));
            refundRequest.setAccountId(Integer.parseInt(accountId));
            refundRequest.setTrackingId(Integer.parseInt(trackingId));
            refundRequest.setRefundAmount(refundAmount);
            refundRequest.setRefundReason(refundReason);
            refundRequest.setFraud(true);
            refundRequest.setAdmin(false);
            PZRefundResponse response = payment.refund(refundRequest);
            PZResponseStatus status = response.getStatus();

            if(PZResponseStatus.ERROR.equals(status))
            {
                throw new Exception();
            }
            else if(PZResponseStatus.FAILED.equals(status))
            {
                throw new SystemError();
            }
            if (response != null && (response.getStatus()).equals(PZResponseStatus.SUCCESS) )
            {
                refundDetails = new Hashtable();
                refundDetails.put("trackingid",response.getTrackingId());
                refundDetails.put("description",response.getResponseDesceiption());
            }
            else
            {
                throw new SystemError();
            }

        }
        catch (SystemError se)
        {
            throw new SystemError();
        }
        catch (Exception e)
        {
            throw new SystemError();
        }

        return refundDetails;

    }

    private Hashtable reverseForQwipi(String trackingid,String refundamount) throws SystemError
    {

        return ftd.reverseQwipiTransaction(trackingid,refundamount);

    }

    public void checkBinAPI(String trackingid)
    {
        if(trackingid==null ||trackingid.equals(""))
        {
            log.info("Tracking Id is null or empty");

            return;
        }
        HashMap transDetails = ftd.getTransactionDetailsForBinCheck(trackingid);

        checkBin(trackingid,(String)transDetails.get("bin"));

    }

    public void checkCardAPI(String trackingid)
    {
        if(trackingid==null ||trackingid.equals(""))
        {
            log.info("Tracking Id is null or empty");
            return;
        }

        HashMap transDetails = ftd.getTransactionDetailsForBinCheck(trackingid);

        checkCard(trackingid,(String)transDetails.get("bin"),(String)transDetails.get("last_4"));

    }

    public void checkEmailAPI(String trackingid) throws SQLException
    {
        if(trackingid==null ||trackingid.equals(""))
        {
            log.info("Tracking Id is null or empty");
            return;
        }

        Set<String> gateways = GatewayTypeService.getGateways();
        Set<String> tablenames = Database.getTableSet(gateways);
        for(String tableName : tablenames)
        {
            HashMap transDetails = null;

            log.debug("=====Looking for transactions in table =="+tableName);

            if(!tableName.equals("transaction_icicicredit"))
            {
                transDetails = ftd.getTransactionDetailsForEmail(tableName, Integer.parseInt(trackingid));
                checkEmail(trackingid,(String)transDetails.get("email"));

            }
        }
    }

    private boolean checkNewTransaction(HashMap transDetails)
    {
        boolean isAutoReverse=false;
        int defaultMinDepositLimit = 10;
        int defaultDailyDepositLimit = 1000;
        int defaultWeeklyDepositLimit = 1000;
        int defaultMonthlyDepositLimit = 10000;

        NewTransactionRequestVO requestVO = new NewTransactionRequestVO();
        transDetails.put("status", getATStatus(getPZTransactionStatus((String) transDetails.get("status"))));
        requestVO.setHashMap(transDetails);

        requestVO.setAmount(String.valueOf(Math.round(Double.parseDouble(requestVO.getAmount()))));   // Roundoff's the amount

        if(requestVO.getDeposit_limits_dl_min()==null || requestVO.getDeposit_limits_dl_min().equals(""))
            requestVO.setDeposit_limits_dl_min(String.valueOf(defaultMinDepositLimit));

        if(requestVO.getDeposit_limits_dl_daily()==null || requestVO.getDeposit_limits_dl_daily().equals(""))
            requestVO.setDeposit_limits_dl_daily(String.valueOf(defaultDailyDepositLimit));

        if(requestVO.getDeposit_limits_dl_weekly()==null || requestVO.getDeposit_limits_dl_weekly().equals("") )
            requestVO.setDeposit_limits_dl_weekly(String.valueOf(defaultWeeklyDepositLimit)) ;

        if(requestVO.getDeposit_limits_dl_monthly()==null || requestVO.getDeposit_limits_dl_monthly().equals(""))
            requestVO.setDeposit_limits_dl_monthly(String.valueOf(defaultMonthlyDepositLimit));

        if(requestVO.getIp()==null || requestVO.getIp().equals(""))
            requestVO.setIp("197.168.1.1");

        requestVO.setMerchant_id(merchant_id);
        requestVO.setPassword(password);

        NewTransactionResponseVO responseVO = at.newTransaction(requestVO);

        if(responseVO.getStatus()!=null)
            isAutoReverse=ftd.updateForNewTransaction(responseVO, (String) transDetails.get("trackingid"), (String) transDetails.get("memberid"),(String)transDetails.get("amount"));
        else
            log.info("No response from Fraud Gateway");

        return isAutoReverse;
    }

    private void checkUpdateTransaction(HashMap transDetails,String transactionStatus)
    {
        UpdateTransactionRequestVO requestVO = new UpdateTransactionRequestVO();
        requestVO.setHashMap(transDetails);
        requestVO.setMerchant_id(merchant_id);
        requestVO.setPassword(password);
        UpdateTransactionResponseVO responseVO = at.updateTransaction(requestVO);

        if(responseVO.getStatus()!=null)
        {
            ftd.updateForUpdateTransaction((String)transDetails.get("pzstatus"), responseVO);
        }

    }

    private void checkBin(String trackingid, String bin)
    {
        if(bin.isEmpty() || trackingid.isEmpty())
        {
            log.info("Bin is null or empty");
            return;
        }

        BinCheckRequestVO requestVO = new BinCheckRequestVO();
        requestVO.setMerchant_id(merchant_id);
        requestVO.setPassword(password);
        requestVO.setBin(bin);
        BinCheckResponseVO responseVO = at.binCheck(requestVO);

        if(responseVO.getStatus()!=null)
            ftd.updateForBinCheck(responseVO, trackingid, bin);
        else
            log.info("No response from Fraud Gateway");
    }

    private void checkCard(String trackingid, String bin, String lastFour)
    {
        if(bin.isEmpty() || trackingid.isEmpty())
        {
            log.info("Bin is null or empty");

            return;
        }

        CardNumberCheckRequestVO requestVO = new CardNumberCheckRequestVO();
        requestVO.setMerchant_id(merchant_id);
        requestVO.setPassword(password);
        requestVO.setBin(bin);
        requestVO.setLast_4(lastFour);
        //Calling Fraud API for score
        CardNumberCheckResponseVO responseVO = at.cardNumberCheck(requestVO);

        //Insert into Card details table the transaction score received from Fraud Engine
        if(responseVO.getStatus()!=null)
            ftd.updateFraudForCardCheck(responseVO, trackingid,bin,lastFour);
        else
            log.info("No response from Fraud Gateway");
    }

    private void checkEmail(String trackingid, String email)
    {
        if(email.isEmpty() || trackingid.isEmpty())
        {
            log.info("Email Id is null or empty");
            return;
        }

        EmailCheckRequestVO requestVO = new EmailCheckRequestVO();
        String[] emails = email.split("@");
        String email_user_partial = emails[0];
        String email_domain = emails[1] ;
        requestVO.setMerchant_id(merchant_id);
        requestVO.setPassword(password);
        requestVO.setEmm_user_partial(email_user_partial);
        requestVO.setEmm_domain(email_domain);

        EmailCheckResponseVO responseVO = at.emailCheck(requestVO);

        if(responseVO.getStatus()!=null)
            ftd.updateForEmailCheck(responseVO, trackingid, email);
        else
            log.info("No response from Fraud Gateway");

    }

    private void sendFraudIntimationAlertToMerchants(Map<String, Map<String, Map<String, String>>> highRiskTransListForMerchant)
    {
        //MailService mailService=new MailService();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();

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
            cbCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION, asynchronousMailService.getDetailTable(transactiondetails));
            cbCountRecord.put(MailPlaceHolder.TOID,memberid.toString());
            asynchronousMailService.sendMerchantSignup(MailEventEnum.MEMBER_DAILY_STATUS_FRAUD_REPORT, cbCountRecord);
            ftd.updateIsAlertSentStatus(trankingIds);
        }
    }

    private void sendRefundIntimationAlertToMerchants(Map<String, Map<String, Map<String, String>>> highRiskTransListForMerchant)
    {
        //MailService mailService=new MailService();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();

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
            cbCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION, asynchronousMailService.getDetailTable(transactiondetails));
            cbCountRecord.put(MailPlaceHolder.TOID,memberid.toString());
            asynchronousMailService.sendMerchantSignup(MailEventEnum.HIGH_RISK_REFUND_TRANSACTION_INTIMATION, cbCountRecord);
            ftd.updateIsAlertSentStatus(trankingIds);
        }
    }

    private void sendFailureIntimationToAdmin(Map<String,Map<String,Map<String,String>>> merchantsFailureTransaction)
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
            //MailService mailService=new MailService();
            AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
            HashMap cbCountRecord=new HashMap();
            cbCountRecord.put(MailPlaceHolder.MULTIPALTRANSACTION, asynchronousMailService.getDetailTable(newtransactiondetails));
            cbCountRecord.put(MailPlaceHolder.TOID,memberid.toString());
            cbCountRecord.put(MailPlaceHolder.TODATE,"13-06-1014");
            cbCountRecord.put(MailPlaceHolder.FROMDATE,"13-06-1013");
            Set trackingIds=newtransactiondetails.keySet();
            asynchronousMailService.sendMerchantSignup(MailEventEnum.HIGH_RISK_FRAUD_TRANSACTION_INTIMATION,cbCountRecord);
            ftd.updateIsAlertSentStatus(trackingIds);
        }

    }

    private PZTransactionStatus getPZTransactionStatus(String status)
    {

        PZTransactionStatus pzTransactionStatus = null;

        if(PZTransactionStatus.AUTH_FAILED.toString().equals(status))
        {
            pzTransactionStatus = PZTransactionStatus.AUTH_FAILED;
        }
        else if (PZTransactionStatus.SETTLED.toString().equals(status))
        {
            pzTransactionStatus = PZTransactionStatus.SETTLED;
        }
        else if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(status))
        {
            pzTransactionStatus = PZTransactionStatus.CAPTURE_SUCCESS;
        }
        else if(PZTransactionStatus.CHARGEBACK.toString().equals(status))
        {
            pzTransactionStatus = PZTransactionStatus.CHARGEBACK;
        }
        else if(PZTransactionStatus.REVERSED.toString().equals(status))
        {
            pzTransactionStatus = PZTransactionStatus.REVERSED;
        }
        return  pzTransactionStatus;

    }

    private String getATStatus(PZTransactionStatus status)
    {
        String atStatus=null;
        switch(status)
        {
            case BEGUN:
                atStatus = ATTransactionStatus.PASS_VALIDATION.toString();
                break;

            case AUTH_STARTED:
                atStatus = ATTransactionStatus.PASS_VALIDATION.toString();
                break;

            case PROOF_REQUIRED:
                atStatus = ATTransactionStatus.PENDING.toString();
                break;

            case AUTH_SUCCESS:
                atStatus = ATTransactionStatus.APPROVED.toString();
                break;

            case AUTH_FAILED:
                atStatus = ATTransactionStatus.DECLINED_BY_BANK_GATWAY.toString();
                break;

            case AUTH_CANCELLED:
                atStatus = ATTransactionStatus.ABANDON.toString();
                break;

            case CAPTURE_STARTED:
                atStatus = ATTransactionStatus.PENDING.toString();
                break;

            case CAPTURE_SUCCESS:
                atStatus = ATTransactionStatus.APPROVED.toString();
                break;

            case CAPTURE_FAILED:
                atStatus = ATTransactionStatus.DECLINED_BY_BANK_GATWAY.toString();
                break;

            case SETTLED:
                atStatus = ATTransactionStatus.SETTLED.toString();
                break;

            case MARKED_FOR_REVERSAL:
                atStatus = ATTransactionStatus.REFUND.toString();
                break;

            case REVERSED:
                atStatus = ATTransactionStatus.REFUND.toString();
                break;

            case FAILED:
                atStatus = ATTransactionStatus.FAILED_VALIDATION.toString();
                break;

            case CHARGEBACK:
                atStatus = ATTransactionStatus.CHARGEBACK.toString();
                break;

            case RETRIEVAL_REQUEST:
                atStatus = ATTransactionStatus.UNDEFINED.toString();
                break;

            default:
                break;
        }

        return atStatus;
    }

    /*public void autoReverseTransaction()
    {

        Set<String> gateways = GatewayTypeService.getGateways();

        Set<String> tablenames = Database.getTableSet(gateways);

        ArrayList<HashMap> listOfFraudTransactions;

        List failList = new ArrayList();

        List successlist = new ArrayList();

        Map<String,Map<String,String>> reverseSuccessTransactionDetails=new HashMap<String, Map<String, String>>();

        Map<String,Map<String,String>> reversefailureTransactionDetails=new HashMap<String, Map<String, String>>();

        Hashtable refundDetails;

        for(String tableName : tablenames)
        {

            if("transaction_common".equals(tableName))
            {
                listOfFraudTransactions= ftd.getTransactionDetailsForReverse(tableName);

               *//* log.debug("list of transactions from transaction_common:" + listOfFraudTransactions);*//*

                 for(HashMap hashmap:listOfFraudTransactions)
                 {
                     try
                     {
                        refundDetails=reverseForCommon((String)hashmap.get("trackingid"),(String)hashmap.get("toid"),(String)hashmap.get("accountId"),(String)hashmap.get("refundamount"),(String)hashmap.get("reason"));

                       //successlist.add(refundDetails);

                       reverseSuccessTransactionDetails.put((String)hashmap.get("trackingid"),refundDetails);

                     }
                     catch (SystemError e)
                     {
                        //add the transaction into the errorListTransaction
                         //failList.add(hashmap);

                         reversefailureTransactionDetails.put((String)hashmap.get("trackingid"),hashmap);
                         *//*log.debug("failure transactions from transaction_common======"+failList);*//*
                     }
                 }

            }
            if("transaction_ecore".equals(tableName))
            {
              listOfFraudTransactions= ftd.getTransactionDetailsForReverse(tableName);

              *//*log.debug("list of transactions from transaction_ecore" + listOfFraudTransactions);*//*

                for(HashMap hashmap:listOfFraudTransactions)
                {
                    try
                    {
                        refundDetails=reverseForEcore((String)hashmap.get("toid"),(String)hashmap.get("accountId"),(String)hashmap.get("trackingid"),(String)hashmap.get("refundamount"),(String)hashmap.get("reason"));

                        //successlist.add(refundDetails);

                        reverseSuccessTransactionDetails.put((String)hashmap.get("trackingid"),hashmap);

                    }
                    catch (SystemError e)
                    {
                        //failList.add(hashmap);

                        reversefailureTransactionDetails.put((String)hashmap.get("trackingid"),hashmap);

                       *//*log.debug("failure transactions from transaction_ecore======"+failList);*//*
                    }
                }

            }

            if("transaction_qwipi".equals(tableName))
            {
                listOfFraudTransactions= ftd.getTransactionDetailsForReverse(tableName);

                *//*log.debug("list of transactions from transaction_qwipi" + listOfFraudTransactions);*//*

                for(HashMap hashmap:listOfFraudTransactions)
                {
                     try
                    {
                         refundDetails=reverseForQwipi(hashmap);
                        //successlist.add(refundDetails);

                         reverseSuccessTransactionDetails.put((String)hashmap.get("trackingid"),hashmap);
                    }
                    catch (SystemError e)
                    {
                        //failList.add(hashmap);

                        reversefailureTransactionDetails.put((String)hashmap.get("trackingid"),hashmap);
                    }
                }
            }

        }
        MailService mailService=new MailService();
        System.out.println("Success LIst======================"+mailService.getDetailTable(reverseSuccessTransactionDetails));
        System.out.println("Failure LIst======================"+mailService.getDetailTable(reversefailureTransactionDetails));


    }
   */

}



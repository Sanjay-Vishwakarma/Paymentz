package com.directi.pg;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.BlacklistManager;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.PartnerDAO;
import com.manager.dao.PaymentDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.LetzPay.LetzPayPaymentGateway;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.airtel.AirtelUgandaPaymentGateway;
import com.payment.apexpay.ApexPayPaymentGateway;
import com.payment.bhartiPay.BhartiPayPaymentGateway;
import com.payment.common.core.*;
import com.payment.easypaymentz.EasyPaymentzPaymentGateway;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.imoneypay.IMoneyPayPaymentGateway;
import com.payment.mtn.MTNUgandaPaymentGateway;
import com.payment.payg.PayGPaymentGateway;
import com.payment.payneteasy.PayneteasyGateway;
import com.payment.qikpay.QikpayPaymentGateway;
import com.payment.qikpayv2.QikPayV2PaymentGateway;
import com.payment.request.PZInquiryRequest;
import com.payment.safexpay.SafexPayPaymentGateway;
import com.payment.statussync.StatusSyncDAO;
import com.payment.verve.VervePaymentGateway;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by Admin on 7/23/2020.
 */
public class CommonBankTransactionInquiry
{
    TransactionLogger transactionLogger = new TransactionLogger(CommonBankTransactionInquiry.class.getName());
    Functions functions                 = new Functions();
    public HashMap commomTransactionInquiryStatus(String trackingid,String status,String terminalId ,String accountId, String gatewayName,String curr,String memberId,String startDate, String endDate)
    {
        HashMap responsemap             = new HashMap();
        PZInquiryRequest inquiryRequest = new PZInquiryRequest();
        StatusSyncDAO statusSyncDAO     = new StatusSyncDAO();
        Functions functions             = new Functions();
        Connection con                  = null;
        StringBuffer yTransaction       = new StringBuffer();
        StringBuffer nTransaction       = new StringBuffer();

        String description  = "";
        String toid         = "";
        String amount       = "";
        String currency     = "";
        String trackingId   = "";
        String accntId      = "";
        String transactionStatus = "";

        int successCounter      = 0;
        int failCounter         = 0;
        int exceptionCounter    = 0;
        int reversedCounter     = 0;
        int pendingCounter      = 0;

        List<TransactionDetailsVO> transactionList  = getTrackingIDTransactionList(trackingid, status, accountId, terminalId, startDate, endDate, memberId, gatewayName, curr);
        int totalAuthstartedCount                   = transactionList.size();
        transactionLogger.error("Total transactions found in" + status + " ------- " + totalAuthstartedCount);
        TransactionDetailsVO depositTransactionDetailsVO        =new TransactionDetailsVO();
        List <TransactionDetailsVO> addDepositTransactionInBatch= new ArrayList<>();
        CommonPaymentProcess commonPaymentProcess               = new CommonPaymentProcess();
        MerchantDAO merchantDAO             = new MerchantDAO();
        for (TransactionDetailsVO transactionDetailsVO : transactionList)
        {
            try
            {
                BlacklistManager blacklistManager   =new BlacklistManager();
                BlacklistVO blacklistVO             =new BlacklistVO();
                amount                              = transactionDetailsVO.getAmount();
                String paymentId                    = transactionDetailsVO.getPaymentId();
                currency                            = transactionDetailsVO.getCurrency();
                description                         = transactionDetailsVO.getOrderDescription();
                String dbStatus                     = transactionDetailsVO.getStatus();
                trackingId                          = transactionDetailsVO.getTrackingid();
                accntId                             = transactionDetailsVO.getAccountId();
                String ipaddress                    = transactionDetailsVO.getIpAddress();
                String timestamp                    = transactionDetailsVO.getTransactionTime();
                String notificationUrl              = "";
                toid                = transactionDetailsVO.getToid();
                String ipAddress    = transactionDetailsVO.getIpAddress();
                String custIp       = transactionDetailsVO.getCustomerIp();
                String custId       = transactionDetailsVO.getCustomerId();
                String email        = transactionDetailsVO.getEmailaddr();
                String toType        = transactionDetailsVO.getTotype();
                String ccnum        = "";
                if(functions.isValueNull(transactionDetailsVO.getCcnum())){
                    ccnum = transactionDetailsVO.getCcnum();
                }
                String firstSix     = "";
                String lastFour     = "";
                String resstatus    = "";
                String cardHolderName = transactionDetailsVO.getName();

                //get gateway object
                transactionLogger.error("calling AbstractPaymentGateway  accountId=" + accntId);
                AbstractPaymentGateway pg   = AbstractPaymentGateway.getGateway(accntId);
                GatewayAccount account      = GatewayAccountService.getGatewayAccount(String.valueOf(accntId));
                String merchantId           = account.getMerchantId();
                String alias                = account.getAliasName();
                String username             = account.getFRAUD_FTP_USERNAME();
                String password             = account.getFRAUD_FTP_PASSWORD();
                String displayname          = account.getDisplayName();

                CommMerchantVO merchantAccountVO = new CommMerchantVO();
                merchantAccountVO.setMerchantId(merchantId);
                merchantAccountVO.setPassword(password);
                merchantAccountVO.setMerchantUsername(username);
                merchantAccountVO.setDisplayName(displayname);
                merchantAccountVO.setAliasName(alias);
                merchantAccountVO.setAccountId(String.valueOf(accntId));

                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toid);

                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                commTransactionDetailsVO.setOrderId(trackingId);
                commTransactionDetailsVO.setPreviousTransactionId(paymentId);
                commTransactionDetailsVO.setResponsetime(timestamp);
                commTransactionDetailsVO.setAmount(amount);
                commTransactionDetailsVO.setCurrency(currency);
                commTransactionDetailsVO.setOrderDesc(description);
                commTransactionDetailsVO.setPrevTransactionStatus(dbStatus);
                commTransactionDetailsVO.setResponseHashInfo(paymentId);
                commTransactionDetailsVO.setSessionId(transactionDetailsVO.getPodBatch());
                commTransactionDetailsVO.setTotype(toType);



                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                commAddressDetailsVO.setIp(ipaddress);
                commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());


                CommRequestVO commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(accntId));
                // AbstractPaymentProcess paymentProcess =  PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accntId));

                commRequestVO.setCommMerchantVO(merchantAccountVO);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);

                inquiryRequest.setTrackingId(Integer.parseInt(trackingId));
                // paymentProcess.setInquiryVOParamsExtension(commRequestVO, inquiryRequest);
                CommResponseVO commResponseVO = null;

                ActionEntry entry           = new ActionEntry();
                AuditTrailVO auditTrailVO   = new AuditTrailVO();

                auditTrailVO.setActionExecutorName("AcquirerInquiryCron");
                auditTrailVO.setActionExecutorId("0");
                if(functions.isValueNull(ccnum)){
                    ccnum       = PzEncryptor.decryptPAN(ccnum);
                    firstSix    = functions.getFirstSix(ccnum);
                    lastFour    = functions.getLastFour(ccnum);
                }
                transactionLogger.error("calling processInquiry() for : trackingId =" + trackingId + "  accountId=" + accntId + "  memberId=" + merchantId);
              /*  if ("rubixpay".equalsIgnoreCase(gatewayName) || "vervepay".equalsIgnoreCase(gatewayName))
                {
                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, commRequestVO, auditTrailVO, null);

                }*/
                //call Inquiry
                commResponseVO = (CommResponseVO) pg.processInquiry(commRequestVO);
                if (commResponseVO == null)
                {
                    commResponseVO = (CommResponseVO) pg.processQuery(trackingId, commRequestVO);
                }

                //update new status
                transactionStatus       = "";
                String transactionid    = "";
                String remark           = "";
                String updateStatus     = "";
                String responseamount   = "";
                String actionEntryAction    = "";
                String actionEntryStatus    = "";
                String RESPONSE_CODE        = "";

               if (functions.isValueNull(commResponseVO.getAmount()))
                {
                    responseamount = commResponseVO.getAmount();
                }


                if (functions.isValueNull(commResponseVO.getTransactionStatus()))
                {
                    transactionStatus = commResponseVO.getTransactionStatus();
                }

                if (functions.isValueNull(commResponseVO.getRemark()))
                {
                    remark = commResponseVO.getRemark();
                }

                if(functions.isValueNull(responseamount))
                {
                    Double compRsAmount = Double.valueOf(responseamount);
                    Double compDbAmount = Double.valueOf(amount);
                    transactionLogger.error("common inquiry response amount --->" + compRsAmount);
                    transactionLogger.error(" DB Amount--->" + compDbAmount);
                    if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(transactionStatus))
                    {
                        transactionStatus = "failed";
                        remark = "Failed-IRA";
                        transactionLogger.error("inside else Amount incorrect--->" + responseamount);
                        RESPONSE_CODE   = "11111";
                        amount          = responseamount;
                        commResponseVO.setRemark(remark);
                        commResponseVO.setDescription(remark);
                        blacklistVO.setVpaAddress(custId);
                        blacklistVO.setIpAddress(custIp);
                        blacklistVO.setEmailAddress(email);
                        blacklistVO.setActionExecutorId(toid);
                        blacklistVO.setActionExecutorName("CommonBankTransactionInquiry");
                        blacklistVO.setRemark("IncorrectAmount Trackingid : "+trackingId);
                        blacklistVO.setFirstSix(firstSix);
                        blacklistVO.setLastFour(lastFour);
                        blacklistVO.setName(cardHolderName);
                        blacklistManager.commonBlackListing(blacklistVO);
                    }
                }

                if (functions.isValueNull(commResponseVO.getTransactionId()))
                {
                    if("vervepay".equalsIgnoreCase(gatewayName)){
                        transactionid = commResponseVO.getResponseHashInfo();
                    }
                    else{
                        transactionid = commResponseVO.getTransactionId();
                    }

                }


                transactionLogger.error("transactionStatus--------------->" + transactionStatus);
                boolean isStatusChnged  = false;
                String transactionmail  = "false";
                String RefundMail       = "false";
                String mailStatus       = "";
                SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                PaymentDAO paymentDAO = new PaymentDAO();

                if (transactionStatus.equalsIgnoreCase("success"))
                {
                    depositTransactionDetailsVO=new TransactionDetailsVO();
                    notificationUrl              = transactionDetailsVO.getNotificationUrl();
                    successCounter++;
                    resstatus       = "success";
                    updateStatus    = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    notificationUrl  = transactionDetailsVO.getNotificationUrl();
                    transactionLogger.error("inside success transactionStatus--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingId);
                    transactionLogger.error("amount--------------->" + amount);
                  /*  paymentDAO.updateTransactionAfterResponse("transaction_common", updateStatus, amount, "", transactionid, remark, "", trackingId);
                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                    //update status flags
                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, updateStatus);*/

                    depositTransactionDetailsVO.setStatus(updateStatus);
                    depositTransactionDetailsVO.setAmount(amount);
                    depositTransactionDetailsVO.setDescription(commResponseVO.getDescription());
                    depositTransactionDetailsVO.setPaymentId(commResponseVO.getTransactionId());
                    depositTransactionDetailsVO.setTrackingid(trackingId);
                    depositTransactionDetailsVO.setBillingDesc(commResponseVO.getDescription());
                    depositTransactionDetailsVO.setRemark(commResponseVO.getDescription());
                    depositTransactionDetailsVO.setResponseHashInfo(commResponseVO.getTransactionId());
                    depositTransactionDetailsVO.setActionExecutorId(auditTrailVO.getActionExecutorId());
                    depositTransactionDetailsVO.setActionExecutorName(auditTrailVO.getActionExecutorName());
                    depositTransactionDetailsVO.setAction(ActionEntry.ACTION_CAPTURE_SUCCESSFUL);

                    depositTransactionDetailsVO.setAccountId(transactionDetailsVO.getAccountId());
                    depositTransactionDetailsVO.setPaymodeId(transactionDetailsVO.getPaymodeId());
                    depositTransactionDetailsVO.setCardTypeId(transactionDetailsVO.getCardTypeId());
                    depositTransactionDetailsVO.setCustomerIp(transactionDetailsVO.getCustomerIp());
                    depositTransactionDetailsVO.setToid(transactionDetailsVO.getToid());
                    depositTransactionDetailsVO.setTotype(transactionDetailsVO.getTotype());//totype
                    depositTransactionDetailsVO.setFromid(transactionDetailsVO.getFromid());//fromid
                    depositTransactionDetailsVO.setFromtype(transactionDetailsVO.getFromtype());//fromtype
                    depositTransactionDetailsVO.setDescription(transactionDetailsVO.getDescription());
                    depositTransactionDetailsVO.setOrderDescription(transactionDetailsVO.getOrderDescription());
                    depositTransactionDetailsVO.setTemplateamount(transactionDetailsVO.getTemplateamount());
                    depositTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
                    depositTransactionDetailsVO.setRedirectURL(transactionDetailsVO.getRedirectURL());
                    depositTransactionDetailsVO.setNotificationUrl(transactionDetailsVO.getNotificationUrl());
                    depositTransactionDetailsVO.setFirstName(transactionDetailsVO.getFirstName());//fn
                    depositTransactionDetailsVO.setLastName(transactionDetailsVO.getLastName());//ln
                    depositTransactionDetailsVO.setName(transactionDetailsVO.getName());//name
                    depositTransactionDetailsVO.setCcnum(transactionDetailsVO.getCcnum());//ccnum
                    depositTransactionDetailsVO.setExpdate(transactionDetailsVO.getExpdate());//expdt
                    depositTransactionDetailsVO.setCardtype(transactionDetailsVO.getCardtype());//cardtype
                    depositTransactionDetailsVO.setEmailaddr(transactionDetailsVO.getEmailaddr());
                    depositTransactionDetailsVO.setIpAddress(transactionDetailsVO.getIpAddress());
                    depositTransactionDetailsVO.setCountry(transactionDetailsVO.getCountry());//country
                    depositTransactionDetailsVO.setState(transactionDetailsVO.getState());//state
                    depositTransactionDetailsVO.setCity(transactionDetailsVO.getCity());//city
                    depositTransactionDetailsVO.setStreet(transactionDetailsVO.getStreet());//street
                    depositTransactionDetailsVO.setZip(transactionDetailsVO.getZip());//zip
                    depositTransactionDetailsVO.setTelcc(transactionDetailsVO.getTelcc());//telcc
                    depositTransactionDetailsVO.setTelno(transactionDetailsVO.getTelno());//telno
                    depositTransactionDetailsVO.setHttpHeader(transactionDetailsVO.getHttpHeader());
                    depositTransactionDetailsVO.setTemplatecurrency(transactionDetailsVO.getTemplatecurrency());
                    depositTransactionDetailsVO.setEmailaddr(transactionDetailsVO.getEmailaddr());
                    depositTransactionDetailsVO.setCustomerId(transactionDetailsVO.getCustomerId());
                    depositTransactionDetailsVO.setEci(transactionDetailsVO.getEci());
                    depositTransactionDetailsVO.setTerminalId(transactionDetailsVO.getTerminalId());
                    depositTransactionDetailsVO.setVersion(transactionDetailsVO.getVersion());
                    depositTransactionDetailsVO.setCaptureAmount(amount);
                    depositTransactionDetailsVO.setRefundAmount(transactionDetailsVO.getRefundAmount());
                    depositTransactionDetailsVO.setEmiCount(transactionDetailsVO.getEmiCount());
                    depositTransactionDetailsVO.setTransactionTime(transactionDetailsVO.getTransactionTime());
                    depositTransactionDetailsVO.setWalletAmount(transactionDetailsVO.getWalletAmount());
                    depositTransactionDetailsVO.setWalletCurrency(transactionDetailsVO.getWalletCurrency());
                    depositTransactionDetailsVO.setTransactionType(transactionDetailsVO.getTransactionType());
                    depositTransactionDetailsVO.setTransactionMode(transactionDetailsVO.getTransactionMode());
                    depositTransactionDetailsVO.setAuthorization_code("");
                    depositTransactionDetailsVO.setBankReferenceId(commResponseVO.getTransactionId());
                    depositTransactionDetailsVO.setTotype(transactionDetailsVO.getTotype());
                    depositTransactionDetailsVO.setMerchantDetailsVO(merchantDetailsVO);

                   // depositTransactionDetailsVO.setAccountId(accntId);
                    addDepositTransactionInBatch.add(depositTransactionDetailsVO);






                    isStatusChnged  = true;
                    transactionmail = "true";
                    if (remark != null && !remark.equals(""))
                    {
                        mailStatus = "Successful (" + remark + ")";
                    }
                    else
                    {
                        mailStatus = "Successful";
                    }

                    yTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, updateStatus));
                }
                else if (transactionStatus.equalsIgnoreCase("reversed"))
                {
                    notificationUrl              = transactionDetailsVO.getNotificationUrl();
                    resstatus="reversed";
                    if (Double.parseDouble(amount) > Double.parseDouble(String.valueOf(responseamount)))
                    {
                        transactionLogger.error("inside partial refund");
                        actionEntryAction = ActionEntry.ACTION_PARTIAL_REFUND;
                        actionEntryStatus = ActionEntry.STATUS_PARTIAL_REFUND;
                    }
                    if (Double.parseDouble(amount) == Double.parseDouble(String.valueOf(responseamount)))
                    {
                        transactionLogger.error("inside full refund");
                        actionEntryAction = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                        actionEntryStatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                    }
                    reversedCounter++;
                    updateStatus    = PZTransactionStatus.REVERSED.toString();
                    notificationUrl  = transactionDetailsVO.getNotificationUrl();
                    transactionLogger.error("inside reversed transactionStatus--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingId);
                    transactionLogger.error("amount--------------->" + amount);
                    transactionLogger.error("refundamount--------------->" + responseamount);
                    // entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, commRequestVO, auditTrailVO, null);
                    paymentDAO.updatereversedTransactionAfterResponse("transaction_common", updateStatus, responseamount, "", transactionid, remark, "", trackingId);
                    entry.actionEntryForCommon(trackingId, responseamount, actionEntryAction, actionEntryStatus, commResponseVO, auditTrailVO, null);
                    //update status flags
                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, updateStatus);
                    isStatusChnged = true;
                    RefundMail = "true";
                    mailStatus = "successful";

                    yTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, updateStatus));
                }
                else if (transactionStatus.equalsIgnoreCase("fail") || transactionStatus.equalsIgnoreCase("failed") )
                {
                    depositTransactionDetailsVO=new TransactionDetailsVO();
                    notificationUrl              = transactionDetailsVO.getNotificationUrl();
                    resstatus   = "failed";
                    failCounter++;
                    updateStatus = PZTransactionStatus.AUTH_FAILED.toString();
                    notificationUrl  = transactionDetailsVO.getNotificationUrl();
                    transactionLogger.error("inside fail transactionStatus--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingId);
                    transactionLogger.error("amount--------------->" + amount);
                    transactionLogger.error("accntId--------------->" + accntId);
                    amount="0.00";
                    // entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, commRequestVO, auditTrailVO, null);
                  /*  paymentDAO.updateTransactionAfterResponse("transaction_common", updateStatus, amount, "", transactionid, remark, "", trackingId);
                    StringBuffer dbBuffer = new StringBuffer();

                   *//* if("11111".equalsIgnoreCase(RESPONSE_CODE)){
                        dbBuffer.append("update transaction_common set captureamount='"+responseamount+"' where trackingid =" + trackingId);
                        transactionLogger.error("Update Query incorrect amount case ---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                    }*//*

                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                    //update status flags
                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, updateStatus);
                    */

                   depositTransactionDetailsVO.setStatus(updateStatus);
                   depositTransactionDetailsVO.setAmount(amount);
                   depositTransactionDetailsVO.setDescription(commResponseVO.getDescription());
                   depositTransactionDetailsVO.setPaymentId(commResponseVO.getTransactionId());
                   depositTransactionDetailsVO.setTrackingid(trackingId);
                   depositTransactionDetailsVO.setBillingDesc(commResponseVO.getDescription());
                   depositTransactionDetailsVO.setRemark(commResponseVO.getDescription());
                   depositTransactionDetailsVO.setResponseHashInfo(commResponseVO.getTransactionId());
                   depositTransactionDetailsVO.setActionExecutorId(auditTrailVO.getActionExecutorId());
                   depositTransactionDetailsVO.setActionExecutorName(auditTrailVO.getActionExecutorName());
                   depositTransactionDetailsVO.setAction(ActionEntry.ACTION_AUTHORISTION_FAILED);

                    depositTransactionDetailsVO.setAccountId(transactionDetailsVO.getAccountId());
                    depositTransactionDetailsVO.setPaymodeId(transactionDetailsVO.getPaymodeId());
                    depositTransactionDetailsVO.setCardTypeId(transactionDetailsVO.getCardTypeId());
                    depositTransactionDetailsVO.setCustomerIp(transactionDetailsVO.getCustomerIp());
                    depositTransactionDetailsVO.setToid(transactionDetailsVO.getToid());
                    depositTransactionDetailsVO.setTotype(transactionDetailsVO.getTotype());//totype
                    depositTransactionDetailsVO.setFromid(transactionDetailsVO.getFromid());//fromid
                    depositTransactionDetailsVO.setFromtype(transactionDetailsVO.getFromtype());//fromtype
                    depositTransactionDetailsVO.setDescription(transactionDetailsVO.getDescription());
                    depositTransactionDetailsVO.setOrderDescription(transactionDetailsVO.getOrderDescription());
                    depositTransactionDetailsVO.setTemplateamount(transactionDetailsVO.getTemplateamount());
                    depositTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
                    depositTransactionDetailsVO.setRedirectURL(transactionDetailsVO.getRedirectURL());
                    depositTransactionDetailsVO.setNotificationUrl(transactionDetailsVO.getNotificationUrl());
                    depositTransactionDetailsVO.setFirstName(transactionDetailsVO.getFirstName());//fn
                    depositTransactionDetailsVO.setLastName(transactionDetailsVO.getLastName());//ln
                    depositTransactionDetailsVO.setName(transactionDetailsVO.getName());//name
                    depositTransactionDetailsVO.setCcnum(transactionDetailsVO.getCcnum());//ccnum
                    depositTransactionDetailsVO.setExpdate(transactionDetailsVO.getExpdate());//expdt
                    depositTransactionDetailsVO.setCardtype(transactionDetailsVO.getCardtype());//cardtype
                    depositTransactionDetailsVO.setEmailaddr(transactionDetailsVO.getEmailaddr());
                    depositTransactionDetailsVO.setIpAddress(transactionDetailsVO.getIpAddress());
                    depositTransactionDetailsVO.setCountry(transactionDetailsVO.getCountry());//country
                    depositTransactionDetailsVO.setState(transactionDetailsVO.getState());//state
                    depositTransactionDetailsVO.setCity(transactionDetailsVO.getCity());//city
                    depositTransactionDetailsVO.setStreet(transactionDetailsVO.getStreet());//street
                    depositTransactionDetailsVO.setZip(transactionDetailsVO.getZip());//zip
                    depositTransactionDetailsVO.setTelcc(transactionDetailsVO.getTelcc());//telcc
                    depositTransactionDetailsVO.setTelno(transactionDetailsVO.getTelno());//telno
                    depositTransactionDetailsVO.setHttpHeader(transactionDetailsVO.getHttpHeader());
                    depositTransactionDetailsVO.setTemplatecurrency(transactionDetailsVO.getTemplatecurrency());
                    depositTransactionDetailsVO.setEmailaddr(transactionDetailsVO.getEmailaddr());
                    depositTransactionDetailsVO.setCustomerId(transactionDetailsVO.getCustomerId());
                    depositTransactionDetailsVO.setEci(transactionDetailsVO.getEci());
                    depositTransactionDetailsVO.setTerminalId(transactionDetailsVO.getTerminalId());
                    depositTransactionDetailsVO.setVersion(transactionDetailsVO.getVersion());

                    depositTransactionDetailsVO.setRefundAmount(transactionDetailsVO.getRefundAmount());
                    depositTransactionDetailsVO.setEmiCount(transactionDetailsVO.getEmiCount());
                    depositTransactionDetailsVO.setTransactionTime(transactionDetailsVO.getTransactionTime());
                    depositTransactionDetailsVO.setWalletAmount(transactionDetailsVO.getWalletAmount());
                    depositTransactionDetailsVO.setWalletCurrency(transactionDetailsVO.getWalletCurrency());
                    depositTransactionDetailsVO.setTransactionType(transactionDetailsVO.getTransactionType());
                    depositTransactionDetailsVO.setTransactionMode(transactionDetailsVO.getTransactionMode());
                    depositTransactionDetailsVO.setAuthorization_code("");
                    depositTransactionDetailsVO.setBankReferenceId(commResponseVO.getTransactionId());
                    depositTransactionDetailsVO.setTotype(transactionDetailsVO.getTotype());
                    depositTransactionDetailsVO.setMerchantDetailsVO(merchantDetailsVO);


                   addDepositTransactionInBatch.add(depositTransactionDetailsVO);
                    isStatusChnged = true;
                    transactionmail = "true";
                    if (remark != null && !remark.equals(""))
                    {
                        mailStatus = "Transaction Declined ( " + remark + " )";
                    }
                    else
                    {
                        mailStatus = "Transaction Declined";
                    }

                    yTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, updateStatus));

                }
                else
                {
                    pendingCounter++;
                    yTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, PZTransactionStatus.CAPTURE_STARTED.toString()));
                }

                //Sending Notification on NotificationURL
               /* MerchantDAO merchantDAO             = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                transactionLogger.error("Notificatin Sending to---" + notificationUrl + "--isStatusChnged--" + isStatusChnged + "--trackingId=" + trackingId + "notification send flag" + merchantDetailsVO.getReconciliationNotification());
                if (functions.isValueNull(notificationUrl) && isStatusChnged && "Y".equals(merchantDetailsVO.getReconciliationNotification()))
                {
                    TransactionManager transactionManager = new TransactionManager();
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    AsyncNotificationService asyncNotificationService   = AsyncNotificationService.getInstance();
                    TransactionDetailsVO updatedTransactionDetailsVO    = transactionManager.getTransDetailFromCommon(trackingId);
                    updatedTransactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    updatedTransactionDetailsVO.setBillingDesc(displayname);
                    if (functions.isValueNull(ccnum))
                        updatedTransactionDetailsVO.setCcnum(ccnum);
                    if (functions.isValueNull(updatedTransactionDetailsVO.getExpdate()))
                        updatedTransactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(updatedTransactionDetailsVO.getExpdate()));
                    asyncNotificationService.sendNotification(updatedTransactionDetailsVO, trackingId, resstatus, remark);
                }
*/
             /*   if (transactionmail.equals("true"))
                {
                    sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, trackingId, mailStatus, remark, GatewayAccountService.getGatewayAccount(accntId).getDisplayName().toString());
                }
                if (RefundMail.equals("true"))
                {
                    sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.REFUND_TRANSACTION, trackingId, mailStatus, remark, null);
                }*/
            }
            catch (PZConstraintViolationException e)
            {
                transactionLogger.error("PZConstraintViolationException--------------->", e);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (SystemError systemError)
            {
                transactionLogger.error("SystemError--------------->", systemError);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZDBViolationException--------------->", e);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (PZTechnicalViolationException e)
            {
                transactionLogger.error("PZTechnicalViolationException--------------->", e);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (PZGenericConstraintViolationException e)
            {
                transactionLogger.error("PZGenericConstraintViolationException--------------->", e);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (Exception e1)
            {
                transactionLogger.error("Exception--------------->", e1);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            finally
            {
             //   Database.closeConnection(con);
            }
        }
        //batch update
        transactionLogger.error("after for loop addDepositTransactionInBatch ----"+addDepositTransactionInBatch.size());
        updateTransactionStatusAfterResponse(addDepositTransactionInBatch);

        StringBuffer success = new StringBuffer();
        StringBuffer sHeader = new StringBuffer();
        success.append("<u>Transaction Report <b>Bank Transaction Inquiry</b>" + "</u><br>");
        success.append("<br>");

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        success.append("<b>Date and Time : </b>"+String.valueOf(dateFormat.format(date))+"<br>");

        if("authstarted".equalsIgnoreCase(status))
        {
            success.append("<b>Total Transactions in Authstarted/Capturestarted : </b>" + transactionList.size() + "<br>");
        }
        else if("authfailed".equalsIgnoreCase(status))
        {
            success.append("<b>Total Transactions in Authfailed : </b>" + transactionList.size() + "<br>");
        }
        success.append("<b>Failed Transactions: </b>" + failCounter + "<br>");
        success.append("<b>Capture Successful : </b>" + successCounter + "<br>");
        success.append("<b>Not Processed Transactions : </b>" + exceptionCounter + "<br>");
        success.append("<b>Reverse Counter : </b>" + reversedCounter + "<br>");
        success.append("<b>Pending Counter : </b>" + pendingCounter + "<br>");

        transactionLogger.error("Total Transactions ===== " + transactionList.size());
        transactionLogger.error("successCounter ===== " + successCounter);
        transactionLogger.error("failCounter ===== " + failCounter);
        transactionLogger.error("exceptionCounter ===== " + exceptionCounter);
        transactionLogger.error("reversedCounter ===== " + reversedCounter);
        transactionLogger.error("pendingCounter ===== " + pendingCounter);

        int tAdd = successCounter + failCounter + exceptionCounter + reversedCounter;

        transactionLogger.error("tAdd ===== " + tAdd);

        success.append("<b>Total Transactions Processed : </b>"+tAdd+"<br>");

        //Auth/Capture started transaction table

        sHeader.append("<br>");
        sHeader.append("<b><u>Transactions Details</b></u><br>");
        sHeader.append("<br>");
        sHeader.append(getTableHeader());
        sHeader.append("<br>");
        success.append("<br>");
        sHeader.append(yTransaction);
        sHeader.append("<br>");
        sHeader.append("</table>");

        if(exceptionCounter > 0)
        {
            sHeader.append("<br>");
            sHeader.append("<b><u>Exception while processing</b></u><br>");
            sHeader.append("<br>");
            sHeader.append(getTableHeader());
            sHeader.append("<br>");
            success.append("<br>");
            sHeader.append(nTransaction);
            sHeader.append("<br>");
            sHeader.append("</table>");
        }


        success.append(sHeader);

        transactionLogger.debug("total count---"+success);

        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();

        if("authstarted".equalsIgnoreCase(status))
        {
            asynchronousMailService.sendEmail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT, "", "", success.toString(), null);
        }
        else if("authfailed".equalsIgnoreCase(status))
        {
            transactionLogger.error("sending email for authfailed");
            asynchronousMailService.sendEmail(MailEventEnum.ADMIN_AUTHFAILED_CRON_REPORT, "", "", success.toString(), null);
        }


        transactionLogger.debug("success ===== " + success.toString());
      //  transactionLogger.error("out side forloop--------------->");

        responsemap.put("successCounter", successCounter);
        responsemap.put("reversedCounter", reversedCounter);
        responsemap.put("failCounter", failCounter);
        responsemap.put("authstartedCount", totalAuthstartedCount);
        responsemap.put("MarkedforreversalCount", totalAuthstartedCount);
        responsemap.put("exceptionCounter", exceptionCounter);
        responsemap.put("pendingCounter", pendingCounter);
        transactionLogger.error("before method return --------------->");
        return responsemap;

    }

    public HashMap commomTransactionInquiryStatusPartner(String trackingid, String status, String terminalId, String accountId, String gatewayName, String curr, String memberId, String startDate, String endDate, String partnerid)
    {
        HashMap responsemap             = new HashMap();
        PZInquiryRequest inquiryRequest = new PZInquiryRequest();
        StatusSyncDAO statusSyncDAO     = new StatusSyncDAO();
        Functions functions             = new Functions();
        PartnerDAO partnerDAO           = new PartnerDAO();
        String partnerName              = "";
        partnerName                     = partnerDAO.getPartnerName(partnerid);
        int successCounter              = 0;
        int failCounter                 = 0;
        int exceptionCounter            = 0;
        int reversedCounter             = 0;
        List<TransactionDetailsVO> transactionList  = getTrackingIDTransactionList(trackingid, status, accountId, terminalId, startDate, endDate, memberId, gatewayName, curr, partnerName);
        int totalAuthstartedCount                   = transactionList.size();
        transactionLogger.error("Total transactions found in" + status + " ------- " + totalAuthstartedCount);

        CommonPaymentProcess commonPaymentProcess = new CommonPaymentProcess();
        for (TransactionDetailsVO transactionDetailsVO : transactionList)
        {
            try
            {
                String amount       = transactionDetailsVO.getAmount();
                String paymentId    = transactionDetailsVO.getPaymentId();
                String currency     = transactionDetailsVO.getCurrency();
                String description  = transactionDetailsVO.getOrderDescription();
                String dbStatus     = transactionDetailsVO.getStatus();
                String trackingId   = transactionDetailsVO.getTrackingid();
                String accntId      = transactionDetailsVO.getAccountId();
                String ipaddress    = transactionDetailsVO.getIpAddress();
                String timestamp    = transactionDetailsVO.getTransactionTime();
                String notificationUrl  = transactionDetailsVO.getNotificationUrl();
                String toid             = transactionDetailsVO.getToid();

                //get gateway object
                transactionLogger.error("calling AbstractPaymentGateway  accountId=" + accntId);
                AbstractPaymentGateway pg   = AbstractPaymentGateway.getGateway(accntId);
                GatewayAccount account      = GatewayAccountService.getGatewayAccount(String.valueOf(accntId));
                String merchantId           = account.getMerchantId();
                String alias                = account.getAliasName();
                String username             = account.getFRAUD_FTP_USERNAME();
                String password             = account.getFRAUD_FTP_PASSWORD();
                String displayname          = account.getDisplayName();

                CommMerchantVO merchantAccountVO = new CommMerchantVO();
                merchantAccountVO.setMerchantId(merchantId);
                merchantAccountVO.setPassword(password);
                merchantAccountVO.setMerchantUsername(username);
                merchantAccountVO.setDisplayName(displayname);
                merchantAccountVO.setAliasName(alias);
                merchantAccountVO.setAccountId(String.valueOf(accntId));

                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                commTransactionDetailsVO.setOrderId(trackingId);
                commTransactionDetailsVO.setPreviousTransactionId(paymentId);
                commTransactionDetailsVO.setResponsetime(timestamp);
                commTransactionDetailsVO.setAmount(amount);
                commTransactionDetailsVO.setCurrency(currency);
                commTransactionDetailsVO.setOrderDesc(description);
                commTransactionDetailsVO.setPrevTransactionStatus(dbStatus);
                commTransactionDetailsVO.setResponseHashInfo(paymentId);
                commTransactionDetailsVO.setSessionId(transactionDetailsVO.getPodBatch());
                commTransactionDetailsVO.setTotype(transactionDetailsVO.getTotype());

                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                commAddressDetailsVO.setIp(ipaddress);
                commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());


                CommRequestVO commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(accntId));
                // AbstractPaymentProcess paymentProcess =  PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accntId));

                commRequestVO.setCommMerchantVO(merchantAccountVO);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);

                inquiryRequest.setTrackingId(Integer.parseInt(trackingId));
                // paymentProcess.setInquiryVOParamsExtension(commRequestVO, inquiryRequest);
                CommResponseVO commResponseVO = null;

                ActionEntry entry           = new ActionEntry();
                AuditTrailVO auditTrailVO   = new AuditTrailVO();

                auditTrailVO.setActionExecutorName("AcquirerInquiryCron");
                auditTrailVO.setActionExecutorId("0");

                transactionLogger.error("calling processInquiry() for : trackingId =" + trackingId + "  accountId=" + accntId + "  memberId=" + merchantId);
               /* if ("rubixpay".equalsIgnoreCase(gatewayName) || "vervepay".equalsIgnoreCase(gatewayName))
                {
                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, commRequestVO, auditTrailVO, null);

                }*/
                //call Inquiry
                commResponseVO = (CommResponseVO) pg.processInquiry(commRequestVO);
                if (commResponseVO == null)
                {
                    commResponseVO = (CommResponseVO) pg.processQuery(trackingId, commRequestVO);
                }

                //update new status
                String transactionStatus    = "";
                String transactionid        = "";
                String remark               = "";
                String updateStatus         = "";
                String responseamount       = "";
                String actionEntryAction    = "";
                String actionEntryStatus    = "";

                if (functions.isValueNull(commResponseVO.getTransactionStatus()))
                {
                    transactionStatus = commResponseVO.getTransactionStatus();
                }
                if (functions.isValueNull(commResponseVO.getTransactionId()))
                {
                    transactionid = commResponseVO.getTransactionId();
                }
                if (functions.isValueNull(commResponseVO.getRemark()))
                {
                    remark = commResponseVO.getRemark();
                }
                if (functions.isValueNull(commResponseVO.getAmount()))
                {
                    responseamount = commResponseVO.getAmount();
                }
                transactionLogger.error("transactionStatus--------------->" + transactionStatus);
                boolean isStatusChnged  = false;
                String transactionmail  = "false";
                String RefundMail       = "false";
                String mailStatus       = "";
                SendTransactionEventMailUtil sendTransactionEventMail   = new SendTransactionEventMailUtil();
                PaymentDAO paymentDAO                                   = new PaymentDAO();

                if (transactionStatus.equalsIgnoreCase("success"))
                {
                    successCounter++;
                    updateStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    transactionLogger.error("inside success transactionStatus--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingId);
                    transactionLogger.error("amount--------------->" + amount);
                    paymentDAO.updateTransactionAfterResponse("transaction_common", updateStatus, amount, "", transactionid, remark, "", trackingId);
                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                    //update status flags
                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, updateStatus);
                    isStatusChnged  = true;
                    transactionmail = "true";
                    if (remark != null && !remark.equals(""))
                    {
                        mailStatus = "Successful (" + remark + ")";
                    }
                    else
                    {
                        mailStatus = "Successful";
                    }
                }
                else if (transactionStatus.equalsIgnoreCase("reversed"))
                {
                    if (Double.parseDouble(amount) > Double.parseDouble(String.valueOf(responseamount)))
                    {
                        transactionLogger.error("inside partial refund");
                        actionEntryAction = ActionEntry.ACTION_PARTIAL_REFUND;
                        actionEntryStatus = ActionEntry.STATUS_PARTIAL_REFUND;
                    }
                    if (Double.parseDouble(amount) == Double.parseDouble(String.valueOf(responseamount)))
                    {
                        transactionLogger.error("inside full refund");
                        actionEntryAction = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                        actionEntryStatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                    }
                    reversedCounter++;
                    updateStatus = PZTransactionStatus.REVERSED.toString();
                    transactionLogger.error("inside reversed transactionStatus--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingId);
                    transactionLogger.error("amount--------------->" + amount);
                    transactionLogger.error("refundamount--------------->" + responseamount);
                    // entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, commRequestVO, auditTrailVO, null);
                    paymentDAO.updatereversedTransactionAfterResponse("transaction_common", updateStatus, responseamount, "", transactionid, remark, "", trackingId);
                    entry.actionEntryForCommon(trackingId, responseamount, actionEntryAction, actionEntryStatus, commResponseVO, auditTrailVO, null);
                    //update status flags
                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, updateStatus);
                    isStatusChnged  = true;
                    RefundMail      = "true";
                    mailStatus      = "successful";
                }
                else if (transactionStatus.equalsIgnoreCase("fail") || transactionStatus.equalsIgnoreCase("failed"))
                {
                    failCounter++;
                    updateStatus = PZTransactionStatus.AUTH_FAILED.toString();
                    transactionLogger.error("inside fail transactionStatus--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingId);
                    transactionLogger.error("amount--------------->" + amount);
                    // entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, commRequestVO, auditTrailVO, null);
                    paymentDAO.updateTransactionAfterResponse("transaction_common", updateStatus, amount, "", transactionid, remark, "", trackingId);
                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                    //update status flags
                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, updateStatus);
                    isStatusChnged  = true;
                    transactionmail = "true";
                    if (remark != null && !remark.equals(""))
                    {
                        mailStatus = "Transaction Declined ( " + remark + " )";
                    }
                    else
                    {
                        mailStatus = "Transaction Declined";
                    }
                }

                //Sending Notification on NotificationURL
                MerchantDAO merchantDAO             = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toid);

                if(!functions.isValueNull(notificationUrl) && functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                    notificationUrl  = merchantDetailsVO.getNotificationUrl();
                }
                transactionLogger.error("Notificatin Sending to---" + notificationUrl + "--isStatusChnged--" + isStatusChnged + "--trackingId=" + trackingId + "notification send flag" + merchantDetailsVO.getReconciliationNotification());

                if (functions.isValueNull(notificationUrl) && isStatusChnged && "Y".equals(merchantDetailsVO.getReconciliationNotification()))
                {
                    TransactionManager transactionManager = new TransactionManager();
                    transactionLogger.error("inside sending notification---" + notificationUrl);

                    AsyncNotificationService asyncNotificationService   = AsyncNotificationService.getInstance();
                    TransactionDetailsVO updatedTransactionDetailsVO    = transactionManager.getTransDetailFromCommon(trackingId);
                    updatedTransactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    updatedTransactionDetailsVO.setBillingDesc(displayname);
                    if (functions.isValueNull(updatedTransactionDetailsVO.getCcnum()))
                        updatedTransactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(updatedTransactionDetailsVO.getCcnum()));
                    if (functions.isValueNull(updatedTransactionDetailsVO.getExpdate()))
                        updatedTransactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(updatedTransactionDetailsVO.getExpdate()));

                    if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                        updatedTransactionDetailsVO.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                    }else{
                        updatedTransactionDetailsVO.setMerchantNotificationUrl("");
                    }
                    asyncNotificationService.sendNotification(updatedTransactionDetailsVO, trackingId, transactionStatus, remark);
                }

                if (transactionmail.equals("true"))
                {
                    sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, trackingId, mailStatus, remark, GatewayAccountService.getGatewayAccount(accntId).getDisplayName().toString());
                }
                if (RefundMail.equals("true"))
                {
                    sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.REFUND_TRANSACTION, trackingId, mailStatus, remark, null);
                }


            }
            catch (PZConstraintViolationException e)
            {
                transactionLogger.error("PZConstraintViolationException--------------->", e);

                exceptionCounter++;
            }
            catch (SystemError systemError)
            {
                transactionLogger.error("SystemError--------------->", systemError);
                exceptionCounter++;
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZDBViolationException--------------->", e);
                exceptionCounter++;
            }
            catch (PZTechnicalViolationException e)
            {
                transactionLogger.error("PZTechnicalViolationException--------------->", e);
                exceptionCounter++;
            }
            catch (PZGenericConstraintViolationException e)
            {
                transactionLogger.error("PZGenericConstraintViolationException--------------->", e);
                exceptionCounter++;
            }
            catch (Exception e1)
            {
                transactionLogger.error("Exception--------------->", e1);
                exceptionCounter++;
            }

        }
        transactionLogger.error("out side forloop--------------->");

        responsemap.put("successCounter", successCounter);
        responsemap.put("reversedCounter", reversedCounter);
        responsemap.put("failCounter", failCounter);
        responsemap.put("authstartedCount", totalAuthstartedCount);
        responsemap.put("MarkedforreversalCount", totalAuthstartedCount);
        responsemap.put("exceptionCounter", exceptionCounter);
        transactionLogger.error("before method return--------------->");
        return responsemap;

    }
    // bankTransactionsNotFoundInquiry  getTransactionList
    public HashMap bankTransactionsNotFoundInquiry(String trackingid,String terminalId ,String accountId, String gatewayName,String curr,String memberId,String startDate, String endDate,String status)
    {
        HashMap responsemap             = new HashMap();
        PZInquiryRequest inquiryRequest = new PZInquiryRequest();
        StatusSyncDAO statusSyncDAO     = new StatusSyncDAO();
        Functions functions             = new Functions();
        Connection con                  = null;
        StringBuffer yTransaction       = new StringBuffer();
        StringBuffer nTransaction       = new StringBuffer();

        String description  = "";
        String toid         = "";
        String amount       = "";
        String currency     = "";
        String trackingId   = "";
        String accntId      = "";
        String transactionStatus = "";

        int successCounter      = 0;
        int failCounter         = 0;
        int exceptionCounter    = 0;
        int reversedCounter     = 0;
        int pendingCounter      = 0;

        List<TransactionDetailsVO> transactionList  = getTransactionList(trackingid, status, accountId, terminalId, startDate, endDate, memberId, gatewayName, curr);
        int totalAuthstartedCount                   = transactionList.size();
        transactionLogger.error("Total transactions found in" + status + " ------- " + totalAuthstartedCount);

        CommonPaymentProcess commonPaymentProcess = new CommonPaymentProcess();
        for (TransactionDetailsVO transactionDetailsVO : transactionList)
        {
            try
            {
                BlacklistManager blacklistManager=new BlacklistManager();
                BlacklistVO blacklistVO=new BlacklistVO();
                amount = transactionDetailsVO.getAmount();
                String paymentId = transactionDetailsVO.getPaymentId();
                currency = transactionDetailsVO.getCurrency();
                description = transactionDetailsVO.getOrderDescription();
                String dbStatus = transactionDetailsVO.getStatus();
                trackingId = transactionDetailsVO.getTrackingid();
                accntId = transactionDetailsVO.getAccountId();
                String ipaddress = transactionDetailsVO.getIpAddress();
                String timestamp = transactionDetailsVO.getTransactionTime();
                String notificationUrl = transactionDetailsVO.getNotificationUrl();
                toid = transactionDetailsVO.getToid();
                String ipAddress = transactionDetailsVO.getIpAddress();
                String custIp = transactionDetailsVO.getCustomerIp();
                String custId = transactionDetailsVO.getCustomerId();
                String email =transactionDetailsVO.getEmailaddr();
                String ccnum ="";
                String firstSix = "";
                String lastFour = "";
                String resstatus = "";
                String cardHolderName =transactionDetailsVO.getName();

                //get gateway object
                transactionLogger.error("calling AbstractPaymentGateway  accountId=" + accntId);
                AbstractPaymentGateway pg = AbstractPaymentGateway.getGateway(accntId);
                GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accntId));
                String merchantId = account.getMerchantId();
                String alias = account.getAliasName();
                String username = account.getFRAUD_FTP_USERNAME();
                String password = account.getFRAUD_FTP_PASSWORD();
                String displayname = account.getDisplayName();

                CommMerchantVO merchantAccountVO = new CommMerchantVO();
                merchantAccountVO.setMerchantId(merchantId);
                merchantAccountVO.setPassword(password);
                merchantAccountVO.setMerchantUsername(username);
                merchantAccountVO.setDisplayName(displayname);
                merchantAccountVO.setAliasName(alias);
                merchantAccountVO.setAccountId(String.valueOf(accntId));

                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                commTransactionDetailsVO.setOrderId(trackingId);
                commTransactionDetailsVO.setPreviousTransactionId(paymentId);
                commTransactionDetailsVO.setResponsetime(timestamp);
                commTransactionDetailsVO.setAmount(amount);
                commTransactionDetailsVO.setCurrency(currency);
                commTransactionDetailsVO.setOrderDesc(description);
                commTransactionDetailsVO.setPrevTransactionStatus(dbStatus);
                commTransactionDetailsVO.setResponseHashInfo(paymentId);

                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                commAddressDetailsVO.setIp(ipaddress);


                CommRequestVO commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(accntId));
                // AbstractPaymentProcess paymentProcess =  PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accntId));

                commRequestVO.setCommMerchantVO(merchantAccountVO);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);

                inquiryRequest.setTrackingId(Integer.parseInt(trackingId));
                // paymentProcess.setInquiryVOParamsExtension(commRequestVO, inquiryRequest);
                CommResponseVO commResponseVO = null;

                ActionEntry entry = new ActionEntry();
                AuditTrailVO auditTrailVO = new AuditTrailVO();

                auditTrailVO.setActionExecutorName("AcquirerInquiryCron");
                auditTrailVO.setActionExecutorId("0");
                if(functions.isValueNull(ccnum)){
                    ccnum= PzEncryptor.decryptPAN(ccnum);
                    firstSix=functions.getFirstSix(ccnum);
                    lastFour=functions.getLastFour(ccnum);
                }
                transactionLogger.error("calling processInquiry() for : trackingId =" + trackingId + "  accountId=" + accntId + "  memberId=" + merchantId);
              /*  if ("rubixpay".equalsIgnoreCase(gatewayName) || "vervepay".equalsIgnoreCase(gatewayName))
                {
                    entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, commRequestVO, auditTrailVO, null);

                }*/
                //call Inquiry
                commResponseVO = (CommResponseVO) pg.processInquiry(commRequestVO);
                if (commResponseVO == null)
                {
                    commResponseVO = (CommResponseVO) pg.processQuery(trackingId, commRequestVO);
                }

                //update new status
                transactionStatus = "";
                String transactionid = "";
                String remark = "";
                String updateStatus = "";
                String responseamount = "";
                String actionEntryAction = "";
                String actionEntryStatus = "";
                String RESPONSE_CODE = "";
                if (functions.isValueNull(commResponseVO.getAmount()))
                {
                    responseamount = commResponseVO.getAmount();
                }


                if (functions.isValueNull(commResponseVO.getTransactionStatus()))
                {
                    transactionStatus = commResponseVO.getTransactionStatus();
                }

                if (functions.isValueNull(commResponseVO.getRemark()))
                {
                    remark = commResponseVO.getRemark();
                }

                if(functions.isValueNull(responseamount))
                {
                    Double compRsAmount = Double.valueOf(responseamount);
                    Double compDbAmount = Double.valueOf(amount);
                    transactionLogger.error("common inquiry response amount --->" + compRsAmount);
                    transactionLogger.error(" DB Amount--->" + compDbAmount);
                    if (!compDbAmount.equals(compRsAmount) && "success".equalsIgnoreCase(transactionStatus))
                    {
                        transactionStatus = "failed";
                        remark = "Failed-IRA";
                        transactionLogger.error("inside else Amount incorrect--->" + responseamount);
                        RESPONSE_CODE = "11111";
                        amount = responseamount;
                        commResponseVO.setRemark(remark);
                        commResponseVO.setDescription(remark);
                        blacklistVO.setVpaAddress(custId);
                        blacklistVO.setIpAddress(custIp);
                        blacklistVO.setEmailAddress(email);
                        blacklistVO.setActionExecutorId(toid);
                        blacklistVO.setActionExecutorName("CommonBankTransactionInquiry");
                        blacklistVO.setRemark("IncorrectAmount");
                        blacklistVO.setFirstSix(firstSix);
                        blacklistVO.setLastFour(lastFour);
                        blacklistVO.setName(cardHolderName);
                        blacklistManager.commonBlackListing(blacklistVO);
                    }
                }

                if (functions.isValueNull(commResponseVO.getTransactionId()))
                {
                    if("vervepay".equalsIgnoreCase(gatewayName)){
                        transactionid = commResponseVO.getResponseHashInfo();
                    }
                    else{
                        transactionid = commResponseVO.getTransactionId();
                    }

                }


                transactionLogger.error("transactionStatus--------------->" + transactionStatus);
                boolean isStatusChnged = false;
                String transactionmail = "false";
                String RefundMail = "false";
                String mailStatus = "";
                SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                PaymentDAO paymentDAO = new PaymentDAO();

                if (transactionStatus.equalsIgnoreCase("success"))
                {
                    successCounter++;
                    resstatus="success";
                    updateStatus = PZTransactionStatus.CAPTURE_SUCCESS.toString();
                    transactionLogger.error("inside success transactionStatus--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingId);
                    transactionLogger.error("amount--------------->" + amount);
                    paymentDAO.updateTransactionAfterResponse("transaction_common", updateStatus, amount, "", transactionid, remark, "", trackingId);
                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, auditTrailVO, null);
                    //update status flags
                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, updateStatus);
                    isStatusChnged = true;
                    transactionmail = "true";
                    if (remark != null && !remark.equals(""))
                    {
                        mailStatus = "Successful (" + remark + ")";
                    }
                    else
                    {
                        mailStatus = "Successful";
                    }

                    yTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, updateStatus));
                }

                else if (transactionStatus.equalsIgnoreCase("fail") || transactionStatus.equalsIgnoreCase("failed") )
                {
                    resstatus="failed";
                  //  con = Database.getConnection();
                    failCounter++;
                    updateStatus = PZTransactionStatus.AUTH_FAILED.toString();
                    transactionLogger.error("inside fail transactionStatus--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingId);
                    transactionLogger.error("amount--------------->" + amount);
                    transactionLogger.error("accntId--------------->" + accntId);
                    // entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, commRequestVO, auditTrailVO, null);
                    paymentDAO.updateTransactionAfterResponse("transaction_common", updateStatus, amount, "", transactionid, remark, "", trackingId);
                    StringBuffer dbBuffer = new StringBuffer();

                   /* if("11111".equalsIgnoreCase(RESPONSE_CODE)){
                        dbBuffer.append("update transaction_common set captureamount='"+responseamount+"' where trackingid =" + trackingId);
                        transactionLogger.error("Update Query incorrect amount case ---" + dbBuffer);
                        Database.executeUpdate(dbBuffer.toString(), con);
                    }*/

                    entry.actionEntryForCommon(trackingId, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, auditTrailVO, null);
                    //update status flags
                    statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, updateStatus);
                    isStatusChnged = true;
                    transactionmail = "true";
                    if (remark != null && !remark.equals(""))
                    {
                        mailStatus = "Transaction Declined ( " + remark + " )";
                    }
                    else
                    {
                        mailStatus = "Transaction Declined";
                    }

                    yTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, updateStatus));

                }
                else
                {
                    pendingCounter++;
                    yTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, PZTransactionStatus.CAPTURE_STARTED.toString()));
                }

                //Sending Notification on NotificationURL
                MerchantDAO merchantDAO = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toid);

                if(!functions.isValueNull(notificationUrl) && functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                    notificationUrl  = merchantDetailsVO.getNotificationUrl();
                }

                transactionLogger.error("Notificatin Sending to---" + notificationUrl + "--isStatusChnged--" + isStatusChnged + "--trackingId=" + trackingId + "notification send flag" + merchantDetailsVO.getReconciliationNotification());
                if (functions.isValueNull(notificationUrl) && isStatusChnged && "Y".equals(merchantDetailsVO.getReconciliationNotification()))
                {
                    TransactionManager transactionManager = new TransactionManager();
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    TransactionDetailsVO updatedTransactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                    updatedTransactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    updatedTransactionDetailsVO.setBillingDesc(displayname);
                    if (functions.isValueNull(ccnum))
                        updatedTransactionDetailsVO.setCcnum(ccnum);
                    if (functions.isValueNull(updatedTransactionDetailsVO.getExpdate()))
                        updatedTransactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(updatedTransactionDetailsVO.getExpdate()));

                    if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                        updatedTransactionDetailsVO.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                    }else{
                        updatedTransactionDetailsVO.setMerchantNotificationUrl("");
                    }
                    asyncNotificationService.sendNotification(updatedTransactionDetailsVO, trackingId, resstatus, remark);
                }

                if (transactionmail.equals("true"))
                {
                    sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, trackingId, mailStatus, remark, GatewayAccountService.getGatewayAccount(accntId).getDisplayName().toString());
                }
                if (RefundMail.equals("true"))
                {
                    sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.REFUND_TRANSACTION, trackingId, mailStatus, remark, null);
                }

            }
            catch (PZConstraintViolationException e)
            {
                transactionLogger.error("PZConstraintViolationException--------------->", e);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (SystemError systemError)
            {
                transactionLogger.error("SystemError--------------->", systemError);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZDBViolationException--------------->", e);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (PZTechnicalViolationException e)
            {
                transactionLogger.error("PZTechnicalViolationException--------------->", e);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (PZGenericConstraintViolationException e)
            {
                transactionLogger.error("PZGenericConstraintViolationException--------------->", e);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (Exception e1)
            {
                transactionLogger.error("Exception--------------->", e1);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            finally
            {
              //  Database.closeConnection(con);
            }
        }
        StringBuffer success = new StringBuffer();
        StringBuffer sHeader = new StringBuffer();
        success.append("<u>Transaction Report <b>Bank Transaction Inquiry</b>" + "</u><br>");
        success.append("<br>");

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        success.append("<b>Date and Time : </b>"+String.valueOf(dateFormat.format(date))+"<br>");

        if("authstarted".equalsIgnoreCase(status))
        {
            success.append("<b>Total Transactions in Authstarted/Capturestarted : </b>" + transactionList.size() + "<br>");
        }
        else if("authfailed".equalsIgnoreCase(status))
        {
            success.append("<b>Total Transactions in Authfailed : </b>" + transactionList.size() + "<br>");
        }
        success.append("<b>Failed Transactions: </b>" + failCounter + "<br>");
        success.append("<b>Capture Successful : </b>" + successCounter + "<br>");
        success.append("<b>Not Processed Transactions : </b>" + exceptionCounter + "<br>");
        success.append("<b>Reverse Counter : </b>" + reversedCounter + "<br>");
        success.append("<b>Pending Counter : </b>" + pendingCounter + "<br>");

        transactionLogger.error("Total Transactions ===== " + transactionList.size());
        transactionLogger.error("successCounter ===== " + successCounter);
        transactionLogger.error("failCounter ===== " + failCounter);
        transactionLogger.error("exceptionCounter ===== " + exceptionCounter);
        transactionLogger.error("reversedCounter ===== " + reversedCounter);
        transactionLogger.error("pendingCounter ===== " + pendingCounter);

        int tAdd = successCounter + failCounter + exceptionCounter + reversedCounter;

        transactionLogger.error("tAdd ===== " + tAdd);

        success.append("<b>Total Transactions Processed : </b>"+tAdd+"<br>");

        //Auth/Capture started transaction table

        sHeader.append("<br>");
        sHeader.append("<b><u>Transactions Details</b></u><br>");
        sHeader.append("<br>");
        sHeader.append(getTableHeader());
        sHeader.append("<br>");
        success.append("<br>");
        sHeader.append(yTransaction);
        sHeader.append("<br>");
        sHeader.append("</table>");

        if(exceptionCounter > 0)
        {
            sHeader.append("<br>");
            sHeader.append("<b><u>Exception while processing</b></u><br>");
            sHeader.append("<br>");
            sHeader.append(getTableHeader());
            sHeader.append("<br>");
            success.append("<br>");
            sHeader.append(nTransaction);
            sHeader.append("<br>");
            sHeader.append("</table>");
        }


        success.append(sHeader);

        transactionLogger.debug("total count---"+success);

        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();

        if("authstarted".equalsIgnoreCase(status))
        {
            asynchronousMailService.sendEmail(MailEventEnum.ADMIN_AUTHSTARTED_CRON_REPORT, "", "", success.toString(), null);
        }
        else if("authfailed".equalsIgnoreCase(status))
        {
            transactionLogger.error("sending email for authfailed");
            asynchronousMailService.sendEmail(MailEventEnum.ADMIN_AUTHFAILED_CRON_REPORT, "", "", success.toString(), null);
        }


        transactionLogger.debug("success ===== " + success.toString());
        transactionLogger.error("out side forloop--------------->");
        responsemap.put("successCounter", successCounter);
        responsemap.put("reversedCounter", reversedCounter);
        responsemap.put("failCounter", failCounter);
        responsemap.put("authstartedCount", totalAuthstartedCount);
        responsemap.put("MarkedforreversalCount", totalAuthstartedCount);
        responsemap.put("exceptionCounter", exceptionCounter);
        responsemap.put("pendingCounter", pendingCounter);
        transactionLogger.error("before method return --------------->");
        return responsemap;

    }

        public List<TransactionDetailsVO> getTrackingIDTransactionList(String trackingid, String status, String accountId, String terminalId, String startDate, String endDate, String memberId, String fromType, String gatwaycurrency)
        {

        Connection conn         = null;
        PreparedStatement pstmt = null;
        Functions functions     = new Functions();

        List<TransactionDetailsVO> transactionDetailsVOList = new ArrayList<>();
        TransactionDetailsVO transactionDetailsVO           = null;
        try
        {
            conn                = Database.getConnection();



            StringBuffer query1 = new StringBuffer("SELECT trackingid,accountid,paymodeid,cardtypeid,customerIp,toid,totype,fromid,fromtype,description,orderdescription,templateamount,amount,currency,redirecturl,notificationUrl,status,firstname,lastname,name,ccnum,expdate,cardtype,emailaddr,ipaddress,country,state,city,street,zip,telnocc,telno,httpheader,paymentid,templatecurrency,emailaddr,customerId,eci,terminalid,version,captureamount,refundamount,emiCount,timestamp,walletAmount,walletCurrency,transaction_type,transaction_mode,authorization_code,remark,podbatch FROM transaction_common WHERE fromtype=? AND  STATUS=? ");

            if (functions.isValueNull(memberId))
            {
                query1.append(" AND toid =" + memberId);
            }
            if (functions.isValueNull(accountId))
            {
                query1.append(" AND accountid =" + accountId);
            }
            if (functions.isValueNull(terminalId))
            {
                query1.append(" AND terminalid =" + terminalId);
            }

            if (functions.isValueNull(gatwaycurrency))
            {
                query1.append(" AND currency = '" + gatwaycurrency + "'");
            }
            if (functions.isValueNull(trackingid))
            {
                query1.append(" AND trackingid = '" + trackingid + "'");
            }
            if (functions.isValueNull(startDate) && functions.isValueNull(endDate))
            {
                query1.append("AND FROM_UNIXTIME(dtstamp) >='" + startDate + "' AND FROM_UNIXTIME(dtstamp) <='" + endDate + "'");
            }

            pstmt = conn.prepareStatement(query1.toString());
            pstmt.setString(1, fromType);
            pstmt.setString(2, status);
            transactionLogger.error("Ouery1-----" + pstmt);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();

                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setPaymodeId(resultSet.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(resultSet.getString("cardtypeid"));
                transactionDetailsVO.setCustomerIp(resultSet.getString("customerIp"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setTemplateamount(resultSet.getString("templateamount"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setRedirectURL(resultSet.getString("redirecturl"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(resultSet.getString("httpheader"));//httpheadet
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setTemplatecurrency(resultSet.getString("templatecurrency"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setEci(resultSet.getString("eci"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setVersion(resultSet.getString("version"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setEmiCount(resultSet.getString("emiCount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setWalletAmount(resultSet.getString("walletAmount"));
                transactionDetailsVO.setWalletCurrency(resultSet.getString("walletCurrency"));
                transactionDetailsVO.setTransactionType(resultSet.getString("transaction_type"));
                transactionDetailsVO.setTransactionMode(resultSet.getString("transaction_mode"));
                transactionDetailsVO.setAuthorization_code(resultSet.getString("authorization_code"));
                transactionDetailsVO.setBankReferenceId(resultSet.getString("paymentid"));
                transactionDetailsVO.setRemark(resultSet.getString("remark"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));


                transactionDetailsVOList.add(transactionDetailsVO);
            }
        }

        catch (Exception e)
        {
            transactionLogger.error("Exception while reading authstarted transactions----", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVOList;
    }



    public HashMap commomPayoutInquiryStatus(String trackingid, String status, String terminalId, String accountId, String gatewayName, String curr, String memberId, String startDate, String endDate)
    {
        HashMap responsemap             = new HashMap();
        PZInquiryRequest inquiryRequest = new PZInquiryRequest();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        LimitChecker limitChecker=new LimitChecker();
        StringBuffer yTransaction = new StringBuffer();
        StringBuffer nTransaction = new StringBuffer();
        String description = "";
        String toid = "";
        String amount = "";
        String currency = "";
        String trackingId = "";
        String accntId = "";
        String transactionStatus = "";
        String responsetransactionstatus = "";
        String action = "";
        String responsetransactionid = "";
        String responsecode = "";
        String actionExId = "";
        String responseTime = "";
        String responsedescription = "";
        String responsedescriptor = "";
        String actionExname = "";
        String errorName = "";
        String arn = "";
        String transType = "";
        String walletCurrency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String walletAmount = "";
        String rrn = "";
        String authCode = "";
        String responsehashinfo = "";
        String sb = null;
        String sb2 = null;
        List mainTableUpdateBatchquery=new ArrayList<String>();
        List detailTableInsertBatchquery=new ArrayList<String>();
        int batchCounter = 50;
        int successCounter = 0;
        int failCounter = 0;
        int exceptionCounter = 0;
        int reversedCounter = 0;
        int pendingCounter = 0;
        List<TransactionDetailsVO> transactionList  = getTrackingIDTransactionList(trackingid, status, accountId, terminalId, startDate, endDate, memberId, gatewayName, curr);
        int totalAuthstartedCount                    = transactionList.size();
        transactionLogger.error("Total payout transactions found in" + status + " ------- " + totalAuthstartedCount);
        List <TransactionDetailsVO> addPayoutTransactionInBatch= new ArrayList<>();
        TransactionDetailsVO payoutTransactionDetailsVO = new TransactionDetailsVO();
        CommResponseVO payoutcommResponseVO1            = new CommResponseVO();
        CommonPaymentProcess commonPaymentProcess       = new CommonPaymentProcess();

        MerchantDAO merchantDAO = new MerchantDAO();

        for (TransactionDetailsVO transactionDetailsVO : transactionList)
        {
            try
            {


                amount              = transactionDetailsVO.getAmount();
                String paymentId    = transactionDetailsVO.getPaymentId();
                currency            = transactionDetailsVO.getCurrency();
                description         = transactionDetailsVO.getOrderDescription();
                String dbStatus     = transactionDetailsVO.getStatus();
                trackingId          = transactionDetailsVO.getTrackingid();
                accntId             = transactionDetailsVO.getAccountId();
                String ipaddress    = transactionDetailsVO.getIpAddress();
                String timestamp    = transactionDetailsVO.getTransactionTime();
                String notificationUrl      = transactionDetailsVO.getNotificationUrl();
                toid                        = transactionDetailsVO.getToid();
                String Authorization_code   = transactionDetailsVO.getAuthorization_code();
                transactionLogger.error("common bank txt inquiry Authorization_code--------------->"+ Authorization_code);

                //get gateway object
                transactionLogger.error("calling AbstractPaymentGateway  accountId=" + accntId);
                AbstractPaymentGateway pg   = AbstractPaymentGateway.getGateway(accntId);
                GatewayAccount account      = GatewayAccountService.getGatewayAccount(String.valueOf(accntId));
                String merchantId           = account.getMerchantId();
                String alias                = account.getAliasName();
                String username             = account.getFRAUD_FTP_USERNAME();
                String password             = account.getFRAUD_FTP_PASSWORD();
                String displayname          = account.getDisplayName();

                CommMerchantVO merchantAccountVO = new CommMerchantVO();
                merchantAccountVO.setMerchantId(merchantId);
                merchantAccountVO.setPassword(password);
                merchantAccountVO.setMerchantUsername(username);
                merchantAccountVO.setDisplayName(displayname);
                merchantAccountVO.setAliasName(alias);
                merchantAccountVO.setAccountId(String.valueOf(accntId));

                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                commTransactionDetailsVO.setOrderId(trackingId);
                commTransactionDetailsVO.setPreviousTransactionId(paymentId);
                commTransactionDetailsVO.setResponsetime(timestamp);
                commTransactionDetailsVO.setAmount(amount);
                commTransactionDetailsVO.setCurrency(currency);
                commTransactionDetailsVO.setOrderDesc(description);
                commTransactionDetailsVO.setPrevTransactionStatus(dbStatus);
                commTransactionDetailsVO.setResponseHashInfo(paymentId);
                commTransactionDetailsVO.setAuthorization_code(Authorization_code);
                commTransactionDetailsVO.setSessionId(transactionDetailsVO.getPodBatch());

                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toid);

                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                commAddressDetailsVO.setIp(ipaddress);


                CommRequestVO commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(accntId));
                // AbstractPaymentProcess paymentProcess =  PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accntId));

                commRequestVO.setCommMerchantVO(merchantAccountVO);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);

                inquiryRequest.setTrackingId(Integer.parseInt(trackingId));
                // paymentProcess.setInquiryVOParamsExtension(commRequestVO, inquiryRequest);
                CommResponseVO commResponseVO = null;

                ActionEntry entry = new ActionEntry();
                AuditTrailVO auditTrailVO = new AuditTrailVO();

                auditTrailVO.setActionExecutorName("AcquirerInquiryCron");
                auditTrailVO.setActionExecutorId("0");

                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingId), Integer.parseInt(accntId));

                int actionEntry = 0;

                transactionLogger.error("calling processInquiry() for : trackingId =" + trackingId + "  accountId=" + accntId + "  memberId=" + merchantId);
                transactionLogger.error("gatewayName------>" + gatewayName);
                //call Inquiry
                SafexPayPaymentGateway safexPayPaymentGateway           = new SafexPayPaymentGateway(accntId);
                VervePaymentGateway vervePaymentGateway                 = new VervePaymentGateway(accntId);
                BhartiPayPaymentGateway bhartiPayPaymentGateway         = new BhartiPayPaymentGateway(accntId);
                QikpayPaymentGateway qikpayPaymentGateway               = new QikpayPaymentGateway(accntId);
                LetzPayPaymentGateway letzPayPaymentGateway             = new LetzPayPaymentGateway(accntId);
                PayneteasyGateway payneteasyGateway                     = new PayneteasyGateway(accntId);
                EasyPaymentzPaymentGateway easyPaymentzPaymentGateway   = new EasyPaymentzPaymentGateway(accntId);
                IMoneyPayPaymentGateway iMoneyPayPaymentGateway         = new IMoneyPayPaymentGateway(accntId);
                PayGPaymentGateway payGPaymentGateway                   = new PayGPaymentGateway(accntId);
                AirtelUgandaPaymentGateway airtelUgandaPaymentGateway   = new AirtelUgandaPaymentGateway(accntId);
                MTNUgandaPaymentGateway mtnUgandaPaymentGateway         = new MTNUgandaPaymentGateway(accntId);
                if (VervePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) vervePaymentGateway.processPayoutInquiry(trackingId, commRequestVO);

                }else if (BhartiPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {

                    commResponseVO = (CommResponseVO) bhartiPayPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);

                }else if (QikpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {

                    commResponseVO = (CommResponseVO) qikpayPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);

                }
                else if (LetzPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {

                    commResponseVO = (CommResponseVO) letzPayPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);

                }
                else if(PayneteasyGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) payneteasyGateway.processQuery(trackingId,commRequestVO);
                }
                else if(EasyPaymentzPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) easyPaymentzPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);

                }else if(IMoneyPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) iMoneyPayPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);
                }
                else if(PayGPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) payGPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);
                }
                else if(AirtelUgandaPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) airtelUgandaPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);
                }
                else if(MTNUgandaPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) mtnUgandaPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);
                }
                else
                {
                    commResponseVO = (CommResponseVO) safexPayPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);

                }


                //update new status
                String transactionid        = "";
                String remark               = "";
                String updateStatus         = "";
                String responseamount       = "";
                String actionEntryAction    = "";
                String actionEntryStatus    = "";

                if (functions.isValueNull(commResponseVO.getTransactionStatus()))
                {
                    transactionStatus = commResponseVO.getTransactionStatus();
                }
                if (functions.isValueNull(commResponseVO.getTransactionId()))
                {
                    transactionid = commResponseVO.getTransactionId();
                }
                if (functions.isValueNull(commResponseVO.getRemark()))
                {
                    remark = commResponseVO.getRemark();
                }
                if (functions.isValueNull(commResponseVO.getAmount()))
                {
                    responseamount = commResponseVO.getAmount();
                }
                transactionLogger.error("payout Status--------------->" + transactionStatus);
                boolean isStatusChnged = false;
                String mailStatus = "";

                PaymentManager paymentManager = new PaymentManager();
                if (transactionStatus.equalsIgnoreCase("success"))
                {
                    payoutTransactionDetailsVO=new TransactionDetailsVO();
                    successCounter++;
                    updateStatus = PZTransactionStatus.PAYOUT_SUCCESS.toString();
                    transactionLogger.error("inside success payout Status--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingId);
                    transactionLogger.error("amount--------------->" + amount);


                    payoutTransactionDetailsVO.setStatus("payoutsuccessful");
                    payoutTransactionDetailsVO.setAmount(amount);
                    payoutTransactionDetailsVO.setDescription(commResponseVO.getDescription());
                    payoutTransactionDetailsVO.setPaymentId(commResponseVO.getTransactionId());
                    payoutTransactionDetailsVO.setTrackingid(trackingId);
                    payoutTransactionDetailsVO.setBillingDesc(commResponseVO.getDescription());
                    payoutTransactionDetailsVO.setRemark(commResponseVO.getDescription());
                    payoutTransactionDetailsVO.setResponseHashInfo(commResponseVO.getTransactionId());
                    payoutTransactionDetailsVO.setActionExecutorId(auditTrailVO.getActionExecutorId());
                    payoutTransactionDetailsVO.setActionExecutorName(auditTrailVO.getActionExecutorName());
                    payoutTransactionDetailsVO.setAction(ActionEntry.ACTION_PAYOUT_SUCCESSFUL);
                    payoutTransactionDetailsVO.setAccountId(accntId);


                    payoutTransactionDetailsVO.setAccountId(transactionDetailsVO.getAccountId());
                    payoutTransactionDetailsVO.setPaymodeId(transactionDetailsVO.getPaymodeId());
                    payoutTransactionDetailsVO.setCardTypeId(transactionDetailsVO.getCardTypeId());
                    payoutTransactionDetailsVO.setCustomerIp(transactionDetailsVO.getCustomerIp());
                    payoutTransactionDetailsVO.setToid(transactionDetailsVO.getToid());
                    payoutTransactionDetailsVO.setTotype(transactionDetailsVO.getTotype());//totype
                    payoutTransactionDetailsVO.setFromid(transactionDetailsVO.getFromid());//fromid
                    payoutTransactionDetailsVO.setFromtype(transactionDetailsVO.getFromtype());//fromtype
                    payoutTransactionDetailsVO.setDescription(transactionDetailsVO.getDescription());
                    payoutTransactionDetailsVO.setOrderDescription(transactionDetailsVO.getOrderDescription());
                    payoutTransactionDetailsVO.setTemplateamount(transactionDetailsVO.getTemplateamount());
                    payoutTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
                    payoutTransactionDetailsVO.setRedirectURL(transactionDetailsVO.getRedirectURL());
                    payoutTransactionDetailsVO.setNotificationUrl(transactionDetailsVO.getNotificationUrl());
                    payoutTransactionDetailsVO.setFirstName(transactionDetailsVO.getFirstName());//fn
                    payoutTransactionDetailsVO.setLastName(transactionDetailsVO.getLastName());//ln
                    payoutTransactionDetailsVO.setName(transactionDetailsVO.getName());//name
                    payoutTransactionDetailsVO.setCcnum(transactionDetailsVO.getCcnum());//ccnum
                    payoutTransactionDetailsVO.setExpdate(transactionDetailsVO.getExpdate());//expdt
                    payoutTransactionDetailsVO.setCardtype(transactionDetailsVO.getCardtype());//cardtype
                    payoutTransactionDetailsVO.setEmailaddr(transactionDetailsVO.getEmailaddr());
                    payoutTransactionDetailsVO.setIpAddress(transactionDetailsVO.getIpAddress());
                    payoutTransactionDetailsVO.setCountry(transactionDetailsVO.getCountry());//country
                    payoutTransactionDetailsVO.setState(transactionDetailsVO.getState());//state
                    payoutTransactionDetailsVO.setCity(transactionDetailsVO.getCity());//city
                    payoutTransactionDetailsVO.setStreet(transactionDetailsVO.getStreet());//street
                    payoutTransactionDetailsVO.setZip(transactionDetailsVO.getZip());//zip
                    payoutTransactionDetailsVO.setTelcc(transactionDetailsVO.getTelcc());//telcc
                    payoutTransactionDetailsVO.setTelno(transactionDetailsVO.getTelno());//telno
                    payoutTransactionDetailsVO.setHttpHeader(transactionDetailsVO.getHttpHeader());
                    payoutTransactionDetailsVO.setTemplatecurrency(transactionDetailsVO.getTemplatecurrency());
                    payoutTransactionDetailsVO.setEmailaddr(transactionDetailsVO.getEmailaddr());
                    payoutTransactionDetailsVO.setCustomerId(transactionDetailsVO.getCustomerId());
                    payoutTransactionDetailsVO.setEci(transactionDetailsVO.getEci());
                    payoutTransactionDetailsVO.setTerminalId(transactionDetailsVO.getTerminalId());
                    payoutTransactionDetailsVO.setVersion(transactionDetailsVO.getVersion());
                    payoutTransactionDetailsVO.setCaptureAmount(amount);
                    payoutTransactionDetailsVO.setRefundAmount(transactionDetailsVO.getRefundAmount());
                    payoutTransactionDetailsVO.setEmiCount(transactionDetailsVO.getEmiCount());
                    payoutTransactionDetailsVO.setTransactionTime(transactionDetailsVO.getTransactionTime());
                    payoutTransactionDetailsVO.setWalletAmount(transactionDetailsVO.getWalletAmount());
                    payoutTransactionDetailsVO.setWalletCurrency(transactionDetailsVO.getWalletCurrency());
                    payoutTransactionDetailsVO.setTransactionType(transactionDetailsVO.getTransactionType());
                    payoutTransactionDetailsVO.setTransactionMode(transactionDetailsVO.getTransactionMode());
                    payoutTransactionDetailsVO.setAuthorization_code("");
                    payoutTransactionDetailsVO.setBankReferenceId(commResponseVO.getTransactionId());
                    payoutTransactionDetailsVO.setTotype(transactionDetailsVO.getTotype());
                    payoutTransactionDetailsVO.setMerchantDetailsVO(merchantDetailsVO);

                    addPayoutTransactionInBatch.add(payoutTransactionDetailsVO);
                 //   paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), ActionEntry.STATUS_PAYOUT_SUCCESSFUL, amount, commResponseVO.getDescription(), commResponseVO.getTransactionId(),null);

                 //   actionEntry = paymentProcess.actionEntry(String.valueOf(trackingId), amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, commResponseVO, null, auditTrailVO, "Payout Created Successfully");
                    //update status flags
                    // statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, updateStatus);
                    isStatusChnged = true;
                    mailStatus = "SUCCESS";
                  //  limitChecker.updatePayoutAmountOnAccountid(accntId, amount);


                }

                else if (transactionStatus.equalsIgnoreCase("fail") || transactionStatus.equalsIgnoreCase("failed"))
                {
                    payoutTransactionDetailsVO=new TransactionDetailsVO();
                    failCounter++;
                    updateStatus = PZTransactionStatus.PAYOUT_FAILED.toString();
                    transactionLogger.error("inside fail payout Status--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingId);
                    transactionLogger.error("amount--------------->" + amount);
                    amount  = "0.00";



                    payoutTransactionDetailsVO.setStatus("payoutfailed");
                    payoutTransactionDetailsVO.setAmount(amount);
                    payoutTransactionDetailsVO.setDescription(commResponseVO.getDescription());
                    payoutTransactionDetailsVO.setPaymentId(commResponseVO.getTransactionId());
                    payoutTransactionDetailsVO.setTrackingid(trackingId);
                    payoutTransactionDetailsVO.setBillingDesc(commResponseVO.getDescription());
                    payoutTransactionDetailsVO.setRemark(commResponseVO.getDescription());
                    payoutTransactionDetailsVO.setResponseHashInfo(commResponseVO.getTransactionId());
                    payoutTransactionDetailsVO.setActionExecutorId(auditTrailVO.getActionExecutorId());
                    payoutTransactionDetailsVO.setActionExecutorName(auditTrailVO.getActionExecutorName());
                    payoutTransactionDetailsVO.setAction(ActionEntry.ACTION_PAYOUT_FAILED);

                    payoutTransactionDetailsVO.setAccountId(transactionDetailsVO.getAccountId());
                    payoutTransactionDetailsVO.setPaymodeId(transactionDetailsVO.getPaymodeId());
                    payoutTransactionDetailsVO.setCardTypeId(transactionDetailsVO.getCardTypeId());
                    payoutTransactionDetailsVO.setCustomerIp(transactionDetailsVO.getCustomerIp());
                    payoutTransactionDetailsVO.setToid(transactionDetailsVO.getToid());
                    payoutTransactionDetailsVO.setTotype(transactionDetailsVO.getTotype());//totype
                    payoutTransactionDetailsVO.setFromid(transactionDetailsVO.getFromid());//fromid
                    payoutTransactionDetailsVO.setFromtype(transactionDetailsVO.getFromtype());//fromtype
                    payoutTransactionDetailsVO.setDescription(transactionDetailsVO.getDescription());
                    payoutTransactionDetailsVO.setOrderDescription(transactionDetailsVO.getOrderDescription());
                    payoutTransactionDetailsVO.setTemplateamount(transactionDetailsVO.getTemplateamount());
                    payoutTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
                    payoutTransactionDetailsVO.setRedirectURL(transactionDetailsVO.getRedirectURL());
                    payoutTransactionDetailsVO.setNotificationUrl(transactionDetailsVO.getNotificationUrl());
                    payoutTransactionDetailsVO.setFirstName(transactionDetailsVO.getFirstName());//fn
                    payoutTransactionDetailsVO.setLastName(transactionDetailsVO.getLastName());//ln
                    payoutTransactionDetailsVO.setName(transactionDetailsVO.getName());//name
                    payoutTransactionDetailsVO.setCcnum(transactionDetailsVO.getCcnum());//ccnum
                    payoutTransactionDetailsVO.setExpdate(transactionDetailsVO.getExpdate());//expdt
                    payoutTransactionDetailsVO.setCardtype(transactionDetailsVO.getCardtype());//cardtype
                    payoutTransactionDetailsVO.setEmailaddr(transactionDetailsVO.getEmailaddr());
                    payoutTransactionDetailsVO.setIpAddress(transactionDetailsVO.getIpAddress());
                    payoutTransactionDetailsVO.setCountry(transactionDetailsVO.getCountry());//country
                    payoutTransactionDetailsVO.setState(transactionDetailsVO.getState());//state
                    payoutTransactionDetailsVO.setCity(transactionDetailsVO.getCity());//city
                    payoutTransactionDetailsVO.setStreet(transactionDetailsVO.getStreet());//street
                    payoutTransactionDetailsVO.setZip(transactionDetailsVO.getZip());//zip
                    payoutTransactionDetailsVO.setTelcc(transactionDetailsVO.getTelcc());//telcc
                    payoutTransactionDetailsVO.setTelno(transactionDetailsVO.getTelno());//telno
                    payoutTransactionDetailsVO.setHttpHeader(transactionDetailsVO.getHttpHeader());
                    payoutTransactionDetailsVO.setTemplatecurrency(transactionDetailsVO.getTemplatecurrency());
                    payoutTransactionDetailsVO.setEmailaddr(transactionDetailsVO.getEmailaddr());
                    payoutTransactionDetailsVO.setCustomerId(transactionDetailsVO.getCustomerId());
                    payoutTransactionDetailsVO.setEci(transactionDetailsVO.getEci());
                    payoutTransactionDetailsVO.setTerminalId(transactionDetailsVO.getTerminalId());
                    payoutTransactionDetailsVO.setVersion(transactionDetailsVO.getVersion());
                    payoutTransactionDetailsVO.setRefundAmount(transactionDetailsVO.getRefundAmount());
                    payoutTransactionDetailsVO.setEmiCount(transactionDetailsVO.getEmiCount());
                    payoutTransactionDetailsVO.setTransactionTime(transactionDetailsVO.getTransactionTime());
                    payoutTransactionDetailsVO.setWalletAmount(transactionDetailsVO.getWalletAmount());
                    payoutTransactionDetailsVO.setWalletCurrency(transactionDetailsVO.getWalletCurrency());
                    payoutTransactionDetailsVO.setTransactionType(transactionDetailsVO.getTransactionType());
                    payoutTransactionDetailsVO.setTransactionMode(transactionDetailsVO.getTransactionMode());
                    payoutTransactionDetailsVO.setAuthorization_code("");
                    payoutTransactionDetailsVO.setBankReferenceId(commResponseVO.getTransactionId());
                    payoutTransactionDetailsVO.setTotype(transactionDetailsVO.getTotype());
                    payoutTransactionDetailsVO.setMerchantDetailsVO(merchantDetailsVO);


                    addPayoutTransactionInBatch.add(payoutTransactionDetailsVO);
                    // entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, commRequestVO, auditTrailVO, null);
                  //  paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), ActionEntry.STATUS_PAYOUT_FAILED, amount, commResponseVO.getDescription(), commResponseVO.getTransactionId(),null);
                    transactionLogger.error("after main table update --------------->"+trackingId);
                 //   actionEntry = paymentProcess.actionEntry(String.valueOf(trackingId), amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, null, auditTrailVO, "Payout Created Successfully");
                    transactionLogger.error("after detail table update --------------->"+trackingId);
                    //update status flags
                    //statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, updateStatus);
                    transactionLogger.error("after ReconciliationTransactionCronFlag table update --------------->"+trackingId);
                    isStatusChnged = true;
                    mailStatus = "FAILED";


                }

                else
                {
                    pendingCounter++;

                }

               //todo callbatch method



                //updateTransactionStatusAfterResponse(addPayoutTransactionInBatch);
                //Sending Notification on NotificationURL
                /*transactionLogger.error("Notificatin Sending to---" + notificationUrl + "---" + trackingId);
                MerchantDAO merchantDAO = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                transactionLogger.error("ReconciliationNotification flag for ---" + toid + "---" + merchantDetailsVO.getReconciliationNotification());
                transactionLogger.error("Notificatin Sending to---" + notificationUrl + "--isStatusChnged--" + isStatusChnged + "--trackingId=" + trackingId);
                if (functions.isValueNull(notificationUrl) && isStatusChnged && "Y".equals(merchantDetailsVO.getReconciliationNotification()))
                {
                    TransactionManager transactionManager = new TransactionManager();
                    transactionLogger.error("inside sending notification---" + notificationUrl);

                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    TransactionDetailsVO updatedTransactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                    updatedTransactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    updatedTransactionDetailsVO.setBillingDesc(displayname);
                    if (functions.isValueNull(updatedTransactionDetailsVO.getCcnum()))
                        updatedTransactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(updatedTransactionDetailsVO.getCcnum()));
                    if (functions.isValueNull(updatedTransactionDetailsVO.getExpdate()))
                        updatedTransactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(updatedTransactionDetailsVO.getExpdate()));
                    asyncNotificationService.sendNotification(updatedTransactionDetailsVO, trackingId, mailStatus, remark);
                }
                SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PAYOUT_TRANSACTION, trackingId, mailStatus, remark, null);*/

            }
            catch (PZConstraintViolationException e)
            {
                transactionLogger.error("PZConstraintViolationException--------------->", e);
                exceptionCounter++;
            }
            catch (SystemError systemError)
            {
                transactionLogger.error("SystemError--------------->", systemError);

                exceptionCounter++;
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZDBViolationException--------------->", e);

                exceptionCounter++;
            }
            catch (PZTechnicalViolationException e)
            {
                transactionLogger.error("PZTechnicalViolationException--------------->", e);

                exceptionCounter++;
            }
            catch (PZGenericConstraintViolationException e)
            {
                transactionLogger.error("PZGenericConstraintViolationException--------------->", e);

                exceptionCounter++;
            }
            catch (Exception e1)
            {
                transactionLogger.error("Exception PayoutInquiryStatus----", e1);

                exceptionCounter++;
            }
        }
        transactionLogger.error("after for loop addPayoutTransactionInBatch ----"+addPayoutTransactionInBatch.size());
        updatePayoutTransactionStatusAfterResponse(addPayoutTransactionInBatch);
        responsemap.put("payoutsuccessCounter", successCounter);
        responsemap.put("reversedCounter", reversedCounter);
        responsemap.put("payoutfailCounter", failCounter);
        responsemap.put("payoutstartedCount", totalAuthstartedCount);
        responsemap.put("MarkedforreversalCount", totalAuthstartedCount);
        responsemap.put("exceptionCounter", exceptionCounter);
        return responsemap;

    }


    public void updateTransactionStatusAfterResponse(List<TransactionDetailsVO> payoutTransactionList )
    {
        transactionLogger.debug("updateTransactionStatusAfterResponse-----");
        int newDetailId = 0;
        String responsecode = "";
        String dateTime = "";
        String responsetransactionid = "";
        String responsetransactionstatus = "";
        String responseTime = "";
        String responsedescription = "";
        String responsedescriptor = "";
        String responsehashinfo = "";
        String transType = "";
        String ipaddress = "";
        String actionExId = "0";
        String actionExname = "";
        String errorName = "";
        String arn = "";
        String rrn = "";
        String authCode = "";
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String walletAmount = "";
        String walletCurrency = "";
        String trackingId = "";
        String amount = "";
        String action = "";
        String status = "";
        String remark = "";
        String paymentId = "";
        String separater = ",";
        String accountid = "";
        int bin_BatchCount =0;
        StringBuilder trackingIds=new StringBuilder();
        HashMap <String,String>accountBalanceHM=new HashMap();
        LimitChecker limitChecker=new LimitChecker();
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmtMain=null;
        PreparedStatement pstmtUpdateBin=null;
        ResultSet rs = null;
        Connection con = null;
        int count=0;
        int batchSize=500;
        try
        {
            con = Database.getConnection();
            transactionLogger.error("TransactionList size-->"+payoutTransactionList.size());
            if (payoutTransactionList != null)
            {
                String updateMainSql  ="update transaction_common set status=?, remark=?,paymentid=?,captureamount=? where trackingid=?";
                String sql            ="insert into transaction_common_details(trackingid,amount,action,status,remark," +
                                       "responsetransactionid,responsetransactionstatus,responsecode,responseTime,responseDescription,responsedescriptor,responsehashinfo,transtype,ipaddress,actionexecutorid,actionexecutorname,errorName,arn,currency,templateamount,templatecurrency,walletAmount,walletCurrency,rrn,authorization_code) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                String updateBinFlags ="UPDATE bin_details SET isSuccessful=?,isSettled=?,isRefund=?,isChargeback=?,isFraud=?,isRollingReserveKept=?,isRollingReserveReleased=? WHERE icicitransid=?";

                pstmtMain       = con.prepareStatement(updateMainSql);
                pstmt2          = con.prepareStatement(sql);
                pstmtUpdateBin  = con.prepareStatement(updateBinFlags);
                con.setAutoCommit(false);
                for (TransactionDetailsVO payoutTransactionDetailsVO : payoutTransactionList)
                {
                    responsedescriptor="";
                    responsehashinfo ="";
                    ipaddress = "";
                    errorName = "";
                    arn = "";
                    rrn = "";
                    authCode = "";
                    currency = "";
                    tmpl_amount = "";
                    tmpl_currency = "";
                    walletAmount = "";
                    walletCurrency = "";
                    accountid=payoutTransactionDetailsVO.getAccountId();
                    trackingId = payoutTransactionDetailsVO.getTrackingid();
                    amount = payoutTransactionDetailsVO.getAmount();
                    action = payoutTransactionDetailsVO.getAction();
                    status = payoutTransactionDetailsVO.getStatus();
                    remark = payoutTransactionDetailsVO.getRemark();
                    paymentId = payoutTransactionDetailsVO.getPaymentId();
                    pstmtMain.setString(1, status);
                    pstmtMain.setString(2, remark);
                    pstmtMain.setString(3, paymentId);
                    pstmtMain.setString(4, amount);
                    pstmtMain.setString(5, trackingId);
                    pstmtMain.addBatch();
                    transactionLogger.error("trackingId-->"+trackingId);
                    count++;

                    // detail table entry
                    responsecode            = "";
                    responsetransactionid   = String.valueOf(payoutTransactionDetailsVO.getPaymentId());
                    responsedescription     = payoutTransactionDetailsVO.getDescription();
                    responseTime            = "";
                    responsetransactionstatus = payoutTransactionDetailsVO.getStatus();
                    if (functions.isValueNull(payoutTransactionDetailsVO.getStatus()) && "capturesuccess".equalsIgnoreCase(payoutTransactionDetailsVO.getStatus().trim()))
                    {
                        responsedescriptor = payoutTransactionDetailsVO.getBillingDesc();
                    }
                    transType = "";
                    if (functions.isValueNull(payoutTransactionDetailsVO.getResponseHashInfo()))
                    {
                        responsehashinfo = payoutTransactionDetailsVO.getResponseHashInfo();

                    }
                    actionExId      = payoutTransactionDetailsVO.getActionExecutorId();
                    actionExname    = payoutTransactionDetailsVO.getActionExecutorName();

                   // detail table insert prepareStatement
                    pstmt2.setString(1, trackingId);
                    pstmt2.setString(2, amount);
                    pstmt2.setString(3, action);
                    pstmt2.setString(4, status);
                    pstmt2.setString(5, remark);
                    pstmt2.setString(6, responsetransactionid);
                    pstmt2.setString(7, responsetransactionstatus);
                    pstmt2.setString(8, responsecode);
                    pstmt2.setString(9, responseTime);
                    pstmt2.setString(10, responsedescription);
                    pstmt2.setString(11, responsedescriptor);
                    pstmt2.setString(12, responsehashinfo);
                    pstmt2.setString(13, transType);
                    pstmt2.setString(14, ipaddress);
                    pstmt2.setString(15, actionExId);
                    pstmt2.setString(16, actionExname);
                    pstmt2.setString(17, errorName);
                    pstmt2.setString(18, arn);
                    pstmt2.setString(19, currency);
                    pstmt2.setString(20, tmpl_amount);
                    pstmt2.setString(21, tmpl_currency);
                    pstmt2.setString(22, walletAmount);
                    pstmt2.setString(23, walletCurrency);
                    pstmt2.setString(24, rrn);
                    pstmt2.setString(25, authCode);
                    pstmt2.addBatch();

                // Bin table

                    if (status.equalsIgnoreCase("authfailed") || status.equalsIgnoreCase("capturefailed"))
                    {
                        pstmtUpdateBin.setString(1, "N");
                        pstmtUpdateBin.setString(2, "N");
                        pstmtUpdateBin.setString(3, "N");
                        pstmtUpdateBin.setString(4, "N");
                        pstmtUpdateBin.setString(5, "N");
                        pstmtUpdateBin.setString(6, "N");
                        pstmtUpdateBin.setString(7, "Y");
                    }
                    else if (status.equalsIgnoreCase("capturesuccess"))
                    {
                        pstmtUpdateBin.setString(1, "Y");
                        pstmtUpdateBin.setString(2, "Y");
                        pstmtUpdateBin.setString(3, "N");
                        pstmtUpdateBin.setString(4, "N");
                        pstmtUpdateBin.setString(5, "N");
                        pstmtUpdateBin.setString(6, "Y");
                        pstmtUpdateBin.setString(7, "N");
                    }
                        pstmtUpdateBin.setString(8, trackingId);
                        pstmtUpdateBin.addBatch();
                        bin_BatchCount++;

                        //trackingIds.append(trackingId+separater);
                        if (count % batchSize == 0)
                        {
                        transactionLogger.error("recon Transaction batch maintable query-->"+pstmtMain);
                        int[] updateMainCounts= pstmtMain.executeBatch();
                        transactionLogger.error("Transaction batch maintable executed count-->"+Arrays.toString(updateMainCounts));

                        if(updateMainCounts.length>0){
                        transactionLogger.error("recon Transaction batch detailtable query-->"+pstmt2);
                        int[] updateDetailCounts= pstmt2.executeBatch();
                        transactionLogger.error("Transaction batch detailtable executed count-->"+Arrays.toString(updateDetailCounts));

                        if(updateDetailCounts.length>0){
                         transactionLogger.error("recon Transaction batch UpdateBin queryt-->"+pstmtUpdateBin);
                         int[] updateBinCount= pstmtUpdateBin.executeBatch();
                         con.commit();
                        transactionLogger.error("Transaction batch UpdateBin executed count-->"+Arrays.toString(updateDetailCounts));

                        if(updateBinCount.length>0){
                        //String returnmessage=sendNotificationAfterRecon(trackingIds.toString().substring(0, trackingIds.length() - 1));
                        String returnmessage = sendNotificationAfterRecon(payoutTransactionList);
                        transactionLogger.error("after  batch update notification -->"+returnmessage);

                            trackingIds=new StringBuilder();
                         }
                        }
                      }
                    }

                }
                        // execute remaining query
                        transactionLogger.error("outside  loop  batch count-->"+count);
                        transactionLogger.error("recon Transaction batch maintable query-->"+pstmtMain);
                        int[] updateMainCounts= pstmtMain.executeBatch();
                        transactionLogger.error(" Transaction batch maintable executed count-->"+Arrays.toString(updateMainCounts));

                        if(updateMainCounts.length>0){
                        transactionLogger.error("  recon Transaction batch detailtable query-->"+pstmt2);
                        int[] updateDetailCounts= pstmt2.executeBatch();
                        transactionLogger.error(" Transaction recon batch detailtable executed count-->"+Arrays.toString(updateDetailCounts));

                        if(updateDetailCounts.length>0 ){
                        transactionLogger.error("recon Transaction batch UpdateBin queryt-->"+pstmtUpdateBin);
                        int[] updateBinCount= pstmtUpdateBin.executeBatch();
                        con.commit();
                        transactionLogger.error("Transaction recon batch UpdateBin executed count-->"+Arrays.toString(updateDetailCounts));

                        if(updateBinCount.length>0){
                        String returnmessage=sendNotificationAfterRecon(payoutTransactionList);
                        transactionLogger.error("outside  loop  after batch update notification -->" + returnmessage);

                        trackingIds=new StringBuilder();

                        } }
                }

            }
        }
        catch (SQLException systemError)
        {
            transactionLogger.error("SQLException :::::", systemError);

        }
        catch (Exception e)
        {
            transactionLogger.error(" paymentDAO Exception :::::", e);

        }
        finally
        {
            Database.closeConnection(con);
        }
    }
    //  deposit
    public void updatePayoutTransactionStatusAfterResponse(List<TransactionDetailsVO> payoutTransactionList )
    {
        transactionLogger.debug("updatePayoutTransactionStatusAfterResponse-----");
        int newDetailId = 0;
        String responsecode = "";
        String dateTime = "";
        String responsetransactionid = "";
        String responsetransactionstatus = "";
        String responseTime = "";
        String responsedescription = "";
        String responsedescriptor = "";
        String responsehashinfo = "";
        String transType = "";
        String ipaddress = "";
        String actionExId = "0";
        String actionExname = "";
        String errorName = "";
        String arn = "";
        String rrn = "";
        String authCode = "";
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String walletAmount = "";
        String walletCurrency = "";
        String trackingId = "";
        String amount = "";
        String action = "";
        String status = "";
        String remark = "";
        String paymentId = "";
        String separater = ",";
        String accountid = "";
        int bin_BatchCount =0;
        StringBuilder trackingIds=new StringBuilder();
        HashMap <String,String>accountBalanceHM=new HashMap();
        LimitChecker limitChecker=new LimitChecker();
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmtMain=null;
        PreparedStatement pstmtUpdateBin=null;
        ResultSet rs = null;
        Connection con = null;
        int count=0;
        int batchSize=500;
        try
        {
            con = Database.getConnection();

            transactionLogger.error("payoutTransactionList size-->"+payoutTransactionList.size());
            if (payoutTransactionList != null)
            {
                String updateMainSql  ="update transaction_common set status=?,payoutamount=?, remark=?,paymentid=? where trackingid=?";
                String sql            ="insert into transaction_common_details(trackingid,amount,action,status,remark," +
                        "responsetransactionid,responsetransactionstatus,responsecode,responseTime,responseDescription,responsedescriptor,responsehashinfo,transtype,ipaddress,actionexecutorid,actionexecutorname,errorName,arn,currency,templateamount,templatecurrency,walletAmount,walletCurrency,rrn,authorization_code) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
               /* String updateBinFlags ="UPDATE bin_details SET isSuccessful=?,isSettled=?,isRefund=?,isChargeback=?,isFraud=?,isRollingReserveKept=?,isRollingReserveReleased=? WHERE icicitransid=?";
*/
                pstmtMain = con.prepareStatement(updateMainSql);
                pstmt2 = con.prepareStatement(sql);
              //  pstmtUpdateBin = con.prepareStatement(updateBinFlags);
                con.setAutoCommit(false);
                for (TransactionDetailsVO payoutTransactionDetailsVO : payoutTransactionList)
                {
                    responsedescriptor="";
                    responsehashinfo ="";
                    ipaddress = "";
                    errorName = "";
                    arn = "";
                    rrn = "";
                    authCode = "";
                    currency = "";
                    tmpl_amount = "";
                    tmpl_currency = "";
                    walletAmount = "";
                    walletCurrency = "";
                    accountid       = payoutTransactionDetailsVO.getAccountId();
                    trackingId      = payoutTransactionDetailsVO.getTrackingid();
                    amount          = payoutTransactionDetailsVO.getAmount();
                    action          = payoutTransactionDetailsVO.getAction();
                    status          = payoutTransactionDetailsVO.getStatus();
                    remark          = payoutTransactionDetailsVO.getRemark();
                    paymentId       = payoutTransactionDetailsVO.getPaymentId();
                    pstmtMain.setString(1, status);
                    pstmtMain.setString(2, amount);
                    pstmtMain.setString(3, remark);
                    pstmtMain.setString(4, paymentId);
                    pstmtMain.setString(5, trackingId);
                    pstmtMain.addBatch();
                    count++;

                    // detail table entry
                    responsecode = "";
                    responsetransactionid = String.valueOf(payoutTransactionDetailsVO.getPaymentId());
                    responsedescription = payoutTransactionDetailsVO.getDescription();
                    responseTime = "";
                    responsetransactionstatus = payoutTransactionDetailsVO.getStatus();
                    if (functions.isValueNull(payoutTransactionDetailsVO.getStatus()) && "payoutsuccessful".equalsIgnoreCase(payoutTransactionDetailsVO.getStatus().trim()))
                    {
                        responsedescriptor = payoutTransactionDetailsVO.getBillingDesc();
                    }
                    transType = "";
                    if (functions.isValueNull(payoutTransactionDetailsVO.getResponseHashInfo()))
                    {
                        responsehashinfo = payoutTransactionDetailsVO.getResponseHashInfo();

                    }
                    actionExId      = payoutTransactionDetailsVO.getActionExecutorId();
                    actionExname    = payoutTransactionDetailsVO.getActionExecutorName();

                    // detail table insert prepareStatement
                    pstmt2.setString(1, trackingId);
                    pstmt2.setString(2, amount);
                    pstmt2.setString(3, action);
                    pstmt2.setString(4, status);
                    pstmt2.setString(5, remark);
                    pstmt2.setString(6, responsetransactionid);
                    pstmt2.setString(7, responsetransactionstatus);
                    pstmt2.setString(8, responsecode);
                    pstmt2.setString(9, responseTime);
                    pstmt2.setString(10, responsedescription);
                    pstmt2.setString(11, responsedescriptor);
                    pstmt2.setString(12, responsehashinfo);
                    pstmt2.setString(13, transType);
                    pstmt2.setString(14, ipaddress);
                    pstmt2.setString(15, actionExId);
                    pstmt2.setString(16, actionExname);
                    pstmt2.setString(17, errorName);
                    pstmt2.setString(18, arn);
                    pstmt2.setString(19, currency);
                    pstmt2.setString(20, tmpl_amount);
                    pstmt2.setString(21, tmpl_currency);
                    pstmt2.setString(22, walletAmount);
                    pstmt2.setString(23, walletCurrency);
                    pstmt2.setString(24, rrn);
                    pstmt2.setString(25, authCode);
                    pstmt2.addBatch();

                    trackingIds.append(trackingId+separater);
                    if(functions.isValueNull(accountid)&&"payoutsuccessful".equalsIgnoreCase(status))
                    {
                        if(!accountBalanceHM.containsKey(accountid)){
                            accountBalanceHM.put(accountid,amount);
                        }
                        else {
                            double tempammount= Double.parseDouble(accountBalanceHM.get(accountid));
                            double addAmount=Double.parseDouble(amount)+tempammount;
                            accountBalanceHM.put(accountid,addAmount+"");
                        }
                    }
                    if (count % batchSize == 0)
                    {
                    transactionLogger.error("recon payout batch maintable query-->"+pstmtMain);
                    int[] updateMainCounts= pstmtMain.executeBatch();
                    transactionLogger.error("payout batch maintable executed count-->"+Arrays.toString(updateMainCounts));

                    if(updateMainCounts.length>0){
                    transactionLogger.error("recon payout batch detailtable query-->"+pstmt2);
                    int[] updateDetailCounts= pstmt2.executeBatch();
                    con.commit();
                    transactionLogger.error("payout recon batch detailtable executed count-->"+Arrays.toString(updateMainCounts));

                    if(updateDetailCounts.length>0 ){

                    String returnmessage = sendNotificationAfterRecon(payoutTransactionList);
                    transactionLogger.error("after batch update notification -->"+returnmessage);

                    if(!accountBalanceHM.isEmpty())
                    {
                        for(Map.Entry<String,String> accountEntry:accountBalanceHM.entrySet()){
                            transactionLogger.error("payout account -->"+accountEntry.getKey()+"total amount-->"+accountEntry.getValue());
                            limitChecker.updatePayoutAmountOnAccountid(accountEntry.getKey(), accountEntry.getValue());
                        }
                    }
                    accountBalanceHM=new HashMap<>();
                    trackingIds=new StringBuilder();

                            }

                        }
                    }

                }
                    // execute remaining query
                    transactionLogger.error("outside  loop  payout batch count-->"+count);
                    transactionLogger.error("recon payout batch maintable query-->"+pstmtMain);
                    int[] updateMainCounts= pstmtMain.executeBatch();
                    transactionLogger.error("payout batch maintable executed count-->"+Arrays.toString(updateMainCounts));

                    if(updateMainCounts.length>0){
                    transactionLogger.error("  recon payout batch detailtable query-->"+pstmt2);
                    int[] updateDetailCounts= pstmt2.executeBatch();
                    transactionLogger.error("payout recon batch detailtable executed count-->"+Arrays.toString(updateMainCounts));
                    con.commit();
                    if(updateDetailCounts.length>0 ){
                    transactionLogger.error("recon payout batch UpdateBin queryt-->" + pstmtUpdateBin);
                    String returnmessage=sendNotificationAfterRecon(payoutTransactionList);
                    transactionLogger.error("outside  loop  after batch update notification -->" + returnmessage);

                    if(!accountBalanceHM.isEmpty())
                    {
                        for(Map.Entry<String,String> accountEntry:accountBalanceHM.entrySet()){
                            transactionLogger.error("account -->"+accountEntry.getKey()+"total amount-->"+accountEntry.getValue());
                            limitChecker.updatePayoutAmountOnAccountid(accountEntry.getKey(), accountEntry.getValue());
                        }
                    }
                    accountBalanceHM=new HashMap<>();
                    trackingIds=new StringBuilder();
                          }
                }

            }
        }
        catch (SQLException systemError)
        {
            transactionLogger.error("SQLException :::::", systemError);

        }
        catch (Exception e)
        {
            transactionLogger.error(" paymentDAO Exception :::::", e);

        }
        finally
        {
            Database.closeConnection(con);
        }
    }
   // send notification
   public String sendNotificationAfterRecon(List<TransactionDetailsVO> transactionList)  {
       TransactionManager transactionManager = new TransactionManager();
       String notificationUrl = "";
       String toid = "";
       String accountId = "";
       String clKey = "";
       String ccnum = "";
       String expDate = "";
       String billingDesc = "";
       String status = "";
       String message = "";
       String trackingId = "";
       String returnstatus = "";
       int notification_count = 0;
       String resStatus="Notification Sending failed!!";
       try
       {
           Functions functions                      = new Functions();
           MerchantDetailsVO merchantDetailsVO      = null;
           MerchantDAO merchantDAO                  = new MerchantDAO();
           if (transactionList != null && transactionList.size() > 0 )
           {
              // List<TransactionDetailsVO> transactionList = getTrackingIDTransactionList2(trackingids);
               for (TransactionDetailsVO transactionDetailsVO : transactionList)
               {

                   if (transactionDetailsVO != null && functions.isValueNull(transactionDetailsVO.getTrackingid()))
                   {
                       notificationUrl  = transactionDetailsVO.getNotificationUrl();
                       toid             = transactionDetailsVO.getToid();
                       accountId        = transactionDetailsVO.getAccountId();
                       status           = transactionDetailsVO.getStatus();
                       message          = transactionDetailsVO.getRemark();
                       trackingId       = transactionDetailsVO.getTrackingid();
                       if(PZTransactionStatus.PAYOUT_SUCCESS.toString().equalsIgnoreCase(status)||PZTransactionStatus.CAPTURE_SUCCESS.toString().equalsIgnoreCase(status)){
                           billingDesc= GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                           status="success";
                       }
                       if(PZTransactionStatus.PAYOUT_FAILED.toString().equalsIgnoreCase(status)||PZTransactionStatus.AUTH_FAILED.toString().equalsIgnoreCase(status)){
                           status="failed";
                           billingDesc="";
                       }
                       if(PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(status)||PZTransactionStatus.PAYOUT_STARTED.toString().equalsIgnoreCase(status)){
                           status="pending";
                       }

                       if (functions.isValueNull(transactionDetailsVO.getCcnum()))
                       {
                           ccnum = PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                       }
                       if (functions.isValueNull(transactionDetailsVO.getExpdate()))
                       {
                           expDate = PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                       }
                       //merchantDetailsVO  = merchantDAO.getMemberDetails(toid);
                       merchantDetailsVO    = transactionDetailsVO.getMerchantDetailsVO();
                       if (merchantDetailsVO != null)
                       {
                           clKey = merchantDetailsVO.getKey();
                           if(!functions.isValueNull(notificationUrl) && functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                               notificationUrl  = merchantDetailsVO.getNotificationUrl();
                           }
                       }

                       transactionLogger.error("notificationUrl and status "+status+"-->"+notificationUrl);
                       if (functions.isValueNull(notificationUrl)&&!"pending".equalsIgnoreCase(status))
                       {
                           notification_count       =notification_count++;
                           transactionLogger.error("inside sending Notification condition notification_count"+notification_count+"-->"+notificationUrl);
                           transactionLogger.error("billingDesc--->"+billingDesc);
                           transactionDetailsVO.setBillingDesc(billingDesc);
                           transactionDetailsVO.setExpdate(expDate);
                           transactionDetailsVO.setCcnum(ccnum);
                           transactionDetailsVO.setSecretKey(clKey);

                           if(functions.isValueNull(merchantDetailsVO.getNotificationUrl())){
                               transactionDetailsVO.setMerchantNotificationUrl(merchantDetailsVO.getNotificationUrl());
                           }else{
                               transactionDetailsVO.setMerchantNotificationUrl("");
                           }

                           AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                           asyncNotificationService.sendNotification(transactionDetailsVO,trackingId,status,message);
                           resStatus="Notification sent!!";

                       }
                   }
               }
           }
       }

       catch (Exception e)
       {
           transactionLogger.error("Exception------>",e);
       }


       return returnstatus="success";
   }


    public List<TransactionDetailsVO> getTrackingIDTransactionList2(String trackingid)
    {

        Connection conn         = null;
        PreparedStatement pstmt = null;
        Functions functions     = new Functions();

        List<TransactionDetailsVO> transactionDetailsVOList = new ArrayList<>();
        TransactionDetailsVO transactionDetailsVO           = null;
        try
        {
            conn                = Database.getConnection();
            StringBuffer query1 = new StringBuffer("select trackingid,accountid,paymodeid,cardtypeid,customerIp,toid,totype,fromid,fromtype,description,orderdescription,templateamount,amount,currency,redirecturl,notificationUrl,status,firstname,lastname,name,ccnum,expdate,cardtype,emailaddr,ipaddress,country,state,city,street,zip,telnocc,telno,httpheader,paymentid,templatecurrency,emailaddr,customerId,eci,terminalid,version,captureamount,refundamount,emiCount,timestamp,walletAmount,walletCurrency,transaction_type,transaction_mode,authorization_code,remark from transaction_common where trackingid in(" + trackingid +")");

            pstmt = conn.prepareStatement(query1.toString());
            transactionLogger.error("sending notification list Ouery1-----" + pstmt);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setPaymodeId(resultSet.getString("paymodeid"));
                transactionDetailsVO.setCardTypeId(resultSet.getString("cardtypeid"));
                transactionDetailsVO.setCustomerIp(resultSet.getString("customerIp"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTotype(resultSet.getString("totype"));//totype
                transactionDetailsVO.setFromid(resultSet.getString("fromid"));//fromid
                transactionDetailsVO.setFromtype(resultSet.getString("fromtype"));//fromtype
                transactionDetailsVO.setDescription(resultSet.getString("description"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setTemplateamount(resultSet.getString("templateamount"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setRedirectURL(resultSet.getString("redirecturl"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setFirstName(resultSet.getString("firstname"));//fn
                transactionDetailsVO.setLastName(resultSet.getString("lastname"));//ln
                transactionDetailsVO.setName(resultSet.getString("name"));//name
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));//ccnum
                transactionDetailsVO.setExpdate(resultSet.getString("expdate"));//expdt
                transactionDetailsVO.setCardtype(resultSet.getString("cardtype"));//cardtype
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));//country
                transactionDetailsVO.setState(resultSet.getString("state"));//state
                transactionDetailsVO.setCity(resultSet.getString("city"));//city
                transactionDetailsVO.setStreet(resultSet.getString("street"));//street
                transactionDetailsVO.setZip(resultSet.getString("zip"));//zip
                transactionDetailsVO.setTelcc(resultSet.getString("telnocc"));//telcc
                transactionDetailsVO.setTelno(resultSet.getString("telno"));//telno
                transactionDetailsVO.setHttpHeader(resultSet.getString("httpheader"));//httpheadet
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setTemplatecurrency(resultSet.getString("templatecurrency"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setCustomerId(resultSet.getString("customerId"));
                transactionDetailsVO.setEci(resultSet.getString("eci"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setVersion(resultSet.getString("version"));
                transactionDetailsVO.setCaptureAmount(resultSet.getString("captureamount"));
                transactionDetailsVO.setRefundAmount(resultSet.getString("refundamount"));
                transactionDetailsVO.setEmiCount(resultSet.getString("emiCount"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setWalletAmount(resultSet.getString("walletAmount"));
                transactionDetailsVO.setWalletCurrency(resultSet.getString("walletCurrency"));
                transactionDetailsVO.setTransactionType(resultSet.getString("transaction_type"));
                transactionDetailsVO.setTransactionMode(resultSet.getString("transaction_mode"));
                transactionDetailsVO.setAuthorization_code(resultSet.getString("authorization_code"));
                transactionDetailsVO.setBankReferenceId(resultSet.getString("paymentid"));
                transactionDetailsVO.setRemark(resultSet.getString("remark"));
                transactionDetailsVOList.add(transactionDetailsVO);
            }
        }

        catch (Exception e)
        {
            transactionLogger.error("Exception while reading authstarted transactions----", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVOList;
    }

/*    public HashMap commomPayoutInquiryStatus(String trackingid, String status, String terminalId, String accountId, String gatewayName, String curr, String memberId, String startDate, String endDate)
    {
        HashMap responsemap             = new HashMap();
        PZInquiryRequest inquiryRequest = new PZInquiryRequest();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        Functions functions = new Functions();
        LimitChecker limitChecker=new LimitChecker();

        StringBuffer yTransaction = new StringBuffer();
        StringBuffer nTransaction = new StringBuffer();
        String description = "";
        String toid = "";
        String amount = "";
        String currency = "";
        String trackingId = "";
        String accntId = "";
        String transactionStatus = "";

        int successCounter = 0;
        int failCounter = 0;
        int exceptionCounter = 0;
        int reversedCounter = 0;
        int pendingCounter = 0;
        List<TransactionDetailsVO> transactionList = getTrackingIDTransactionList(trackingid, status, accountId, terminalId, startDate, endDate, memberId, gatewayName, curr);
        int totalAuthstartedCount = transactionList.size();
        transactionLogger.error("Total payout transactions found in" + status + " ------- " + totalAuthstartedCount);

        CommonPaymentProcess commonPaymentProcess = new CommonPaymentProcess();
        for (TransactionDetailsVO transactionDetailsVO : transactionList)
        {
            try
            {


                amount              = transactionDetailsVO.getAmount();
                String paymentId    = transactionDetailsVO.getPaymentId();
                currency            = transactionDetailsVO.getCurrency();
                description         = transactionDetailsVO.getOrderDescription();
                String dbStatus     = transactionDetailsVO.getStatus();
                trackingId          = transactionDetailsVO.getTrackingid();
                accntId             = transactionDetailsVO.getAccountId();
                String ipaddress    = transactionDetailsVO.getIpAddress();
                String timestamp    = transactionDetailsVO.getTransactionTime();
                String notificationUrl      = "";
                toid                        = transactionDetailsVO.getToid();
                String Authorization_code   = transactionDetailsVO.getAuthorization_code();
                transactionLogger.error("common bank txt inquiry Authorization_code--------------->"+ Authorization_code);

                //get gateway object
                transactionLogger.error("calling AbstractPaymentGateway  accountId=" + accntId);
                AbstractPaymentGateway pg   = AbstractPaymentGateway.getGateway(accntId);
                GatewayAccount account      = GatewayAccountService.getGatewayAccount(String.valueOf(accntId));
                String merchantId           = account.getMerchantId();
                String alias                = account.getAliasName();
                String username             = account.getFRAUD_FTP_USERNAME();
                String password             = account.getFRAUD_FTP_PASSWORD();
                String displayname          = account.getDisplayName();

                CommMerchantVO merchantAccountVO = new CommMerchantVO();
                merchantAccountVO.setMerchantId(merchantId);
                merchantAccountVO.setPassword(password);
                merchantAccountVO.setMerchantUsername(username);
                merchantAccountVO.setDisplayName(displayname);
                merchantAccountVO.setAliasName(alias);
                merchantAccountVO.setAccountId(String.valueOf(accntId));

                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                commTransactionDetailsVO.setOrderId(trackingId);
                commTransactionDetailsVO.setPreviousTransactionId(paymentId);
                commTransactionDetailsVO.setResponsetime(timestamp);
                commTransactionDetailsVO.setAmount(amount);
                commTransactionDetailsVO.setCurrency(currency);
                commTransactionDetailsVO.setOrderDesc(description);
                commTransactionDetailsVO.setPrevTransactionStatus(dbStatus);
                commTransactionDetailsVO.setResponseHashInfo(paymentId);
                commTransactionDetailsVO.setAuthorization_code(Authorization_code);

                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                commAddressDetailsVO.setIp(ipaddress);


                CommRequestVO commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(accntId));
                // AbstractPaymentProcess paymentProcess =  PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accntId));

                commRequestVO.setCommMerchantVO(merchantAccountVO);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);

                inquiryRequest.setTrackingId(Integer.parseInt(trackingId));
                // paymentProcess.setInquiryVOParamsExtension(commRequestVO, inquiryRequest);
                CommResponseVO commResponseVO = null;

                ActionEntry entry = new ActionEntry();
                AuditTrailVO auditTrailVO = new AuditTrailVO();

                auditTrailVO.setActionExecutorName("AcquirerInquiryCron");
                auditTrailVO.setActionExecutorId("0");

                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingId), Integer.parseInt(accntId));

                int actionEntry = 0;

                transactionLogger.error("calling processInquiry() for : trackingId =" + trackingId + "  accountId=" + accntId + "  memberId=" + merchantId);
                transactionLogger.error("gatewayName------>" + gatewayName);
                //call Inquiry
                SafexPayPaymentGateway safexPayPaymentGateway           = new SafexPayPaymentGateway(accntId);
                VervePaymentGateway vervePaymentGateway                 = new VervePaymentGateway(accntId);
                BhartiPayPaymentGateway bhartiPayPaymentGateway         = new BhartiPayPaymentGateway(accntId);
                QikpayPaymentGateway qikpayPaymentGateway               = new QikpayPaymentGateway(accntId);
                LetzPayPaymentGateway letzPayPaymentGateway             = new LetzPayPaymentGateway(accntId);
                PayneteasyGateway payneteasyGateway                     = new PayneteasyGateway(accntId);
                EasyPaymentzPaymentGateway easyPaymentzPaymentGateway   = new EasyPaymentzPaymentGateway(accntId);
                IMoneyPayPaymentGateway iMoneyPayPaymentGateway         = new IMoneyPayPaymentGateway(accntId);
                PayGPaymentGateway payGPaymentGateway                   = new PayGPaymentGateway(accntId);
                ApexPayPaymentGateway apexPayPaymentGateway             = new ApexPayPaymentGateway(accntId);
                QikPayV2PaymentGateway qikPayV2PaymentGateway = new QikPayV2PaymentGateway(accntId);

                if (VervePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) vervePaymentGateway.processPayoutInquiry(trackingId, commRequestVO);

                }else if (BhartiPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {

                    commResponseVO = (CommResponseVO) bhartiPayPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);

                }else if (QikpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {

                    commResponseVO = (CommResponseVO) qikpayPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);

                }
                else if (LetzPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {

                    commResponseVO = (CommResponseVO) letzPayPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);

                }
                else if(PayneteasyGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) payneteasyGateway.processQuery(trackingId,commRequestVO);
                }
                else if(EasyPaymentzPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) easyPaymentzPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);

                }else if(IMoneyPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) iMoneyPayPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);
                }else if(PayGPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) payGPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);
                }*//*else if(ApexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) apexPayPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);
                }else if(QikPayV2PaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                {
                    commResponseVO = (CommResponseVO) qikPayV2PaymentGateway.processPayoutInquiry(trackingId, commRequestVO);
                }
                else
                {
                    commResponseVO = (CommResponseVO) safexPayPaymentGateway.processPayoutInquiry(trackingId, commRequestVO);

                }


                //update new status
                String transactionid        = "";
                String remark               = "";
                String updateStatus         = "";
                String responseamount       = "";
                String actionEntryAction    = "";
                String actionEntryStatus    = "";

                if (functions.isValueNull(commResponseVO.getTransactionStatus()))
                {
                    transactionStatus = commResponseVO.getTransactionStatus();
                }
                if (functions.isValueNull(commResponseVO.getTransactionId()))
                {
                    transactionid = commResponseVO.getTransactionId();
                }
                if (functions.isValueNull(commResponseVO.getRemark()))
                {
                    remark = commResponseVO.getRemark();
                }
                if (functions.isValueNull(commResponseVO.getAmount()))
                {
                    responseamount = commResponseVO.getAmount();
                }
                transactionLogger.error("payout Status--------------->" + transactionStatus);
                boolean isStatusChnged = false;
                String mailStatus = "";
                //String txtid = commResponseVO.getTransactionId();



                // PaymentDAO paymentDAO = new PaymentDAO();
                PaymentManager paymentManager = new PaymentManager();
                if (transactionStatus.equalsIgnoreCase("success"))
                {
                    notificationUrl      = transactionDetailsVO.getNotificationUrl();
                    successCounter++;
                    updateStatus         = PZTransactionStatus.PAYOUT_SUCCESS.toString();
                    notificationUrl      = transactionDetailsVO.getNotificationUrl();
                    transactionLogger.error("inside success payout Status--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingId);
                    transactionLogger.error("amount--------------->" + amount);

                    paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), ActionEntry.STATUS_PAYOUT_SUCCESSFUL, amount, commResponseVO.getDescription(), commResponseVO.getTransactionId(),null);

                    actionEntry = paymentProcess.actionEntry(String.valueOf(trackingId), amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, commResponseVO, null, auditTrailVO, "Payout Created Successfully");
                    //update status flags
                   // statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, updateStatus);
                    isStatusChnged = true;
                    mailStatus = "SUCCESS";
                    limitChecker.updatePayoutAmountOnAccountid(accntId, amount);

                    yTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, updateStatus));
                }

                else if (transactionStatus.equalsIgnoreCase("fail") || transactionStatus.equalsIgnoreCase("failed"))
                {
                    notificationUrl      = transactionDetailsVO.getNotificationUrl();
                    failCounter++;
                    updateStatus         = PZTransactionStatus.PAYOUT_FAILED.toString();
                    notificationUrl      = transactionDetailsVO.getNotificationUrl();
                    transactionLogger.error("inside fail payout Status--------------->" + transactionStatus);
                    transactionLogger.error("trackingId--------------->" + trackingId);
                    transactionLogger.error("amount--------------->" + amount);
                    // entry.actionEntryFor3DCommon(trackingId, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, null, commRequestVO, auditTrailVO, null);
                    paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), ActionEntry.STATUS_PAYOUT_FAILED, amount, commResponseVO.getDescription(), commResponseVO.getTransactionId(),null);
                    transactionLogger.error("after main table update --------------->"+trackingId);
                    actionEntry = paymentProcess.actionEntry(String.valueOf(trackingId), amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, commResponseVO, null, auditTrailVO, "Payout Created Successfully");
                    transactionLogger.error("after detail table update --------------->"+trackingId);
                    //update status flags
                    //statusSyncDAO.updateReconciliationTransactionCronFlag(trackingId, updateStatus);
                    transactionLogger.error("after ReconciliationTransactionCronFlag table update --------------->"+trackingId);
                    isStatusChnged = true;
                    mailStatus = "FAILED";

                    yTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, updateStatus));
                }

                else
                {
                    pendingCounter++;
                    yTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, PZTransactionStatus.PAYOUT_STARTED.toString()));
                }

                //Sending Notification on NotificationURL
                transactionLogger.error("Notificatin Sending to---" + notificationUrl + "---" + trackingId);
                MerchantDAO merchantDAO = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toid);
                transactionLogger.error("ReconciliationNotification flag for ---" + toid + "---" + merchantDetailsVO.getReconciliationNotification());
                transactionLogger.error("Notificatin Sending to---" + notificationUrl + "--isStatusChnged--" + isStatusChnged + "--trackingId=" + trackingId);
                if (functions.isValueNull(notificationUrl) && isStatusChnged && "Y".equals(merchantDetailsVO.getReconciliationNotification()))
                {
                    TransactionManager transactionManager = new TransactionManager();
                    transactionLogger.error("inside sending notification---" + notificationUrl);
                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    TransactionDetailsVO updatedTransactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                    updatedTransactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    updatedTransactionDetailsVO.setBillingDesc(displayname);
                    if (functions.isValueNull(updatedTransactionDetailsVO.getCcnum()))
                        updatedTransactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(updatedTransactionDetailsVO.getCcnum()));
                    if (functions.isValueNull(updatedTransactionDetailsVO.getExpdate()))
                        updatedTransactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(updatedTransactionDetailsVO.getExpdate()));
                    asyncNotificationService.sendNotification(updatedTransactionDetailsVO, trackingId, mailStatus, remark);
                }
                SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PAYOUT_TRANSACTION, trackingId, mailStatus, remark, null);

            }
            catch (PZConstraintViolationException e)
            {
                transactionLogger.error("PZConstraintViolationException--------------->", e);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (SystemError systemError)
            {
                transactionLogger.error("SystemError--------------->", systemError);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZDBViolationException--------------->", e);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (PZTechnicalViolationException e)
            {
                transactionLogger.error("PZTechnicalViolationException--------------->", e);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (PZGenericConstraintViolationException e)
            {
                transactionLogger.error("PZGenericConstraintViolationException--------------->", e);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
            catch (Exception e1)
            {
                transactionLogger.error("Exception PayoutInquiryStatus----", e1);
                nTransaction.append(setTableData(trackingId, accntId, toid, description, currency, amount, "Error while proccessing"));
                exceptionCounter++;
            }
        }
        StringBuffer success = new StringBuffer();
        StringBuffer sHeader = new StringBuffer();
        success.append("<u>Transaction Report <b>Bank Transaction Inquiry</b>" + "</u><br>");
        success.append("<br>");

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        success.append("<b>Date and Time : </b>"+String.valueOf(dateFormat.format(date))+"<br>");

        if("payoutstarted".equalsIgnoreCase(status))
        {
            success.append("<b>Total Transactions in Payoutstarted : </b>" + transactionList.size() + "<br>");
        }
        else if("payoutfailed".equalsIgnoreCase(status))
        {
            success.append("<b>Total Transactions in Payoutfailed : </b>" + transactionList.size() + "<br>");
        }
        success.append("<b>Failed Transactions: </b>" + failCounter + "<br>");
        success.append("<b>Capture Successful : </b>" + successCounter + "<br>");
        success.append("<b>Not Processed Transactions : </b>" + exceptionCounter + "<br>");
        success.append("<b>Pending Counter : </b>" + pendingCounter + "<br>");

        transactionLogger.error("Total Transactions ===== " + transactionList.size());
        transactionLogger.error("successCounter ===== " + successCounter);
        transactionLogger.error("failCounter ===== " + failCounter);
        transactionLogger.error("exceptionCounter ===== " + exceptionCounter);
        transactionLogger.error("pendingCounter ===== " + pendingCounter);

        int tAdd = successCounter + failCounter + exceptionCounter;

        transactionLogger.error("tAdd ===== " + tAdd);

        success.append("<b>Total Transactions Processed : </b>"+tAdd+"<br>");

        //Auth/Capture started transaction table

        sHeader.append("<br>");
        sHeader.append("<b><u>Transactions Details</b></u><br>");
        sHeader.append("<br>");
        sHeader.append(getTableHeader());
        sHeader.append("<br>");
        success.append("<br>");
        sHeader.append(yTransaction);
        sHeader.append("<br>");
        sHeader.append("</table>");

        if(exceptionCounter > 0)
        {
            sHeader.append("<br>");
            sHeader.append("<b><u>Exception while processing</b></u><br>");
            sHeader.append("<br>");
            sHeader.append(getTableHeader());
            sHeader.append("<br>");
            success.append("<br>");
            sHeader.append(nTransaction);
            sHeader.append("<br>");
            sHeader.append("</table>");
        }


        success.append(sHeader);

        transactionLogger.debug("total count---"+success);

        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();

        if("payoutstarted".equalsIgnoreCase(status))
        {
            transactionLogger.error("sending email for payoutstarted");
            asynchronousMailService.sendEmail(MailEventEnum.ADMIN_PAYOUTSTARTED_CRON_REPORT, "", "", success.toString(), null);
        }
        else if("payoutfailed".equalsIgnoreCase(status))
        {
            transactionLogger.error("sending email for payoutfailed");
            asynchronousMailService.sendEmail(MailEventEnum.ADMIN_PAYOUTFAILED_CRON_REPORT, "", "", success.toString(), null);
        }


        transactionLogger.debug("success ===== " + success.toString());
        responsemap.put("payoutsuccessCounter", successCounter);
        responsemap.put("reversedCounter", reversedCounter);
        responsemap.put("payoutfailCounter", failCounter);
        responsemap.put("payoutstartedCount", totalAuthstartedCount);
        responsemap.put("MarkedforreversalCount", totalAuthstartedCount);
        responsemap.put("exceptionCounter", exceptionCounter);
        return responsemap;

    }*/




    public List<TransactionDetailsVO> getTrackingIDTransactionList(String trackingid, String status, String accountId, String terminalId, String startDate, String endDate, String memberId, String fromType, String gatwaycurrency, String totype)
    {

        Connection conn = null;
        PreparedStatement pstmt = null;
        Functions functions = new Functions();

        List<TransactionDetailsVO> transactionDetailsVOList = new ArrayList<>();
        TransactionDetailsVO transactionDetailsVO = null;
        try
        {
            conn = Database.getConnection();
            StringBuffer query1 = new StringBuffer("SELECT trackingid,accountid,toid,terminalid,paymentid,amount,currency,orderdescription,status,ipaddress,timestamp,notificationUrl, country, podbatch  FROM transaction_common WHERE fromtype=? AND  STATUS=?");
            if (functions.isValueNull(memberId))
            {
                query1.append(" AND toid =" + memberId);
            }
            if (functions.isValueNull(accountId))
            {
                query1.append(" AND accountid =" + accountId);
            }
            if (functions.isValueNull(terminalId))
            {
                query1.append(" AND terminalid =" + terminalId);
            }

            if (functions.isValueNull(gatwaycurrency))
            {
                query1.append(" AND currency = '" + gatwaycurrency + "'");
            }
            if (functions.isValueNull(trackingid))
            {
                query1.append(" AND trackingid = '" + trackingid + "'");
            }
            if (functions.isValueNull(totype))
            {
                query1.append(" AND totype = '" + totype + "'");
            }
            if (functions.isValueNull(startDate) && functions.isValueNull(endDate))
            {
                query1.append("AND FROM_UNIXTIME(dtstamp) >='" + startDate + "' AND FROM_UNIXTIME(dtstamp) <='" + endDate + "'");
            }

            pstmt = conn.prepareStatement(query1.toString());
            pstmt.setString(1, fromType);
            pstmt.setString(2, status);
            transactionLogger.error("Query1-----" + pstmt);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setCountry(resultSet.getString("country"));
                transactionDetailsVO.setPodBatch(resultSet.getString("podbatch"));

                transactionDetailsVOList.add(transactionDetailsVO);
            }
        }

        catch (Exception e)
        {
            transactionLogger.error("Exception while reading authstarted transactions----", e);
        }

        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVOList;
    }

    public StringBuffer setTableData(String trackingid, String accountid, String toid, String sDescription, String currency, String captureAmount, String status)
    {
        StringBuffer nTransaction = new StringBuffer();

        transactionLogger.error("inside settabledata trackingid ===== " + trackingid);
        transactionLogger.error("inside settabledata accountid ===== " + accountid);
        transactionLogger.error("inside settabledata toid ===== " + toid);
        transactionLogger.error("inside settabledata currency ===== " + currency);
        transactionLogger.error("inside settabledata captureAmount ===== " + captureAmount);
        transactionLogger.error("inside settabledata status ===== " + captureAmount);

        nTransaction.append("<TR>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+trackingid+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+accountid+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+toid+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+sDescription+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+currency+" "+captureAmount+"</p>");
        nTransaction.append("</TD>");
        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" color=\"#001963\" font-family=\"sans-serif\" font-size=\"2px\">"+status+"</p>");
        nTransaction.append("</TD>");

        nTransaction.append("</TR>");
        return nTransaction;
    }

    public StringBuffer getTableHeader()
    {
        StringBuffer sHeader = new StringBuffer();

        sHeader.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\" >");
        sHeader.append("<TR>");
        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">TrackingID</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">AccountID</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">MerchantID</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">OrderID</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Amount</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" bgcolor=\"#0f8c93\">");
        sHeader.append("<b><p align=\"center\"><font color=\"#FFFFFF\" family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Status</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("</TR>");
        return sHeader;
    }


    public HashMap authStartedToAuthFailedTransaction(String trackingid, String terminalId, String accountId, String gatewayName, String gatwaycurrency, String memberId, String startDate, String endDate,String Statustochange)
    {
        transactionLogger.error("::: Inside AuthFailedCron method :::");

        StringBuffer success        = new StringBuffer();
        StringBuffer queryString    = new StringBuffer();
        Functions functions         = new Functions();
        HashMap responsemap         = new HashMap();

        Connection connection   = null;
        ResultSet resultSet     = null;
        PreparedStatement ps    = null;

        //String trackingid   = null;
        String amount       = null;
        String accountid    = null;
        String toid         = null;
        String currency     = null;
        String status       = null;
        String sDescription = null;
        String timestamp    = null;
        String notificationUrl = "";

        ActionEntry entry = new ActionEntry();
        int actionEntry         = 0;
        int authstartedCounter  = 0;
        int begunCounter        = 0;
        int failedCounter       = 0;

        String paymentid    = "";
        String FailedStatus = "", action ="",EntryStaus="",notiStatus="",ActionExecuter="";
        if(Statustochange.equals("authstarted"))
        {
            FailedStatus = "authfailed";
            action =ActionEntry.ACTION_FAILED;
            EntryStaus=ActionEntry.STATUS_FAILED;
            notiStatus="Failed";
            ActionExecuter="AuthFailedAutomaticCron";

        }

        else{
            FailedStatus ="payoutfailed";
            action =ActionEntry.ACTION_PAYOUT_FAILED;
            EntryStaus=ActionEntry.STATUS_PAYOUT_FAILED;
            notiStatus="PayoutFailed";
            ActionExecuter="PayoutFailedAutomaticCron";
        }

        try
        {

            connection      = Database.getConnection();
           // String wCond    = "FROM transaction_common AS t WHERE t.STATUS IN ('authstarted') AND t.fromtype IN('vervepay') AND DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -36 HOUR)>=FROM_UNIXTIME(dtstamp) ";
            String wCond    = "FROM transaction_common AS t WHERE t.STATUS IN ('"+Statustochange+"') ";
           // String selectQuery = "SELECT t.trackingid, t.toid, t.paymentid, t.status, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency " + wCond;
            String selectQuery = "SELECT t.trackingid, t.toid, t.paymentid, t.status, t.notificationUrl, t.amount,t.timestamp, t.description, t.remark, t.accountid, t.fromid, t.fromtype, t.currency ";

            queryString.append(wCond);

            if (functions.isValueNull(gatewayName))
            {
                queryString.append(" AND fromtype ='"+ gatewayName+"'");
            }
            if (functions.isValueNull(memberId))
            {
                queryString.append(" AND toid =" + memberId);
            }
            if (functions.isValueNull(accountId))
            {
                queryString.append(" AND accountid =" + accountId);
            }
            /*if (functions.isValueNull(terminalId))
            {
                queryString.append(" AND terminalid =" + terminalId);
            }*/

            /*if (functions.isValueNull(gatwaycurrency))
            {
                queryString.append(" AND currency = '" + gatwaycurrency + "'");
            }
            if (functions.isValueNull(trackingid))
            {
                queryString.append(" AND trackingid = '" + trackingid + "'");
            }*/

            if (functions.isValueNull(startDate) && functions.isValueNull(endDate))
            {
                queryString.append("   AND FROM_UNIXTIME(dtstamp) >='" + startDate + "'  AND FROM_UNIXTIME(dtstamp) <='" + endDate + "'");
            }

            String cQuery = "SELECT count(*) " + queryString.toString();

            selectQuery   = selectQuery+  " "+queryString.toString();
            transactionLogger.error("authFailedCron selectQuery -----------" + selectQuery);
            transactionLogger.error("authFailedCron selectCountQuery -----------" + cQuery);

            ResultSet rset = Database.executeQuery(cQuery.toString(), connection);
            if(rset.next()){
                authstartedCounter = rset.getInt(1);
            }
            // Database.closeResultSet(rset);
            ps          = connection.prepareStatement(selectQuery);
            resultSet   = ps.executeQuery();

            transactionLogger.error("Select Query AuthFailedAutomaticCron---" + ps+ "Authstartedcounter---"+authstartedCounter);

            while (resultSet.next())
            {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date       = new Date();
                trackingid      = resultSet.getString("trackingid");
                amount          = resultSet.getString("amount");
                accountid       = resultSet.getString("accountid");
                toid            = resultSet.getString("toid");
                paymentid       = resultSet.getString("paymentid");
                sDescription    = resultSet.getString("description");
                status          = resultSet.getString("status");
                currency        = resultSet.getString("currency");
                timestamp       = resultSet.getString("timestamp");
                notificationUrl = resultSet.getString("notificationUrl");

                StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
                AuditTrailVO auditTrailVO   = new AuditTrailVO();

                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName(ActionExecuter);

                CommRequestVO requestVO                             = new CommRequestVO();
                CommTransactionDetailsVO commTransactionDetailsVO   = new CommTransactionDetailsVO();
                CommAddressDetailsVO commAddressDetailsVO           = new CommAddressDetailsVO();

                commTransactionDetailsVO.setOrderId(trackingid);
                commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                commTransactionDetailsVO.setResponseHashInfo(paymentid);
                commTransactionDetailsVO.setResponsetime(String.valueOf(dateFormat.format(date)));

                requestVO.setTransDetailsVO(commTransactionDetailsVO);
                requestVO.setAddressDetailsVO(commAddressDetailsVO);
                //requestVO.getCommMerchantVO().setMerchantId("108");
                transactionLogger.error("Select Query AuthFailedAutomaticCron-------" + trackingid);
                transactionLogger.error("-----dbstatus-----"+status);
                //main table update
                CommResponseVO responseVO = new CommResponseVO() ;

                responseVO.setStatus(FailedStatus);
                responseVO.setTransactionId(" ");
                responseVO.setRemark("failed");
                responseVO.setDescription("failed");

                updateMainTableEntry(FailedStatus, trackingid);
                //Detail Table Entry
                entry.actionEntryForCommon(trackingid, amount,action,EntryStaus, responseVO, auditTrailVO, null);
                failedCounter++;

                statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid,FailedStatus);

                transactionLogger.error("Notificatin Sending to---"+notificationUrl+"---"+trackingid+":::::"+FailedStatus);

                if(functions.isValueNull(notificationUrl))
                {
                    TransactionManager transactionManager   = new TransactionManager();
                    MerchantDAO merchantDAO                 = new MerchantDAO();
                    transactionLogger.error("-----toid-----"+toid);
                    MerchantDetailsVO merchantDetailsVO =  merchantDAO.getMemberDetails(toid);
                    transactionLogger.error("inside AuthFailedAutomaticCron sending notification---" + notificationUrl);

                    AsyncNotificationService asyncNotificationService   = AsyncNotificationService.getInstance();
                    TransactionDetailsVO transactionDetailsVO           = transactionManager.getTransDetailFromCommon(trackingid);
                    transactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());

                    if(functions.isValueNull(transactionDetailsVO.getCcnum())){
                        transactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum()));
                    }
                    if(functions.isValueNull(transactionDetailsVO.getExpdate())){
                        transactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate()));
                    }

                    transactionDetailsVO.setBillingDesc(GatewayAccountService.getGatewayAccount(accountid).getDisplayName().toString());
                    asyncNotificationService.sendNotification(transactionDetailsVO, trackingid, notiStatus, responseVO.getRemark());
                    AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                    asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid),notiStatus, "failed", GatewayAccountService.getGatewayAccount(accountid).getDisplayName().toString());

                }
            }
            responsemap.put("failCounter",failedCounter);


            transactionLogger.error("AuthFailedAutomaticCron authstartedCounter------------------>"+authstartedCounter);
            transactionLogger.error("AuthFailedAutomaticCron authFailedCounter------------------>"+failedCounter);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException",e);
            PZExceptionHandler.raiseAndHandleDBViolationException(AuthFailedAutomaticCron.class.getName(), "authFailedAutomaticCron()", null, "common", "DB Exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause(), toid, "AuthFailedAutomaticCron");
        }
        catch (PZDBViolationException pzd)
        {
            transactionLogger.error("PZDBViolationException", pzd);
            PZExceptionHandler.handleDBCVEException(pzd, toid, "AuthFailedAutomaticCron");
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError", systemError);
            PZExceptionHandler.raiseAndHandleDBViolationException(AuthFailedAutomaticCron.class.getName(), "authFailedAutomaticCron()", null, "common", "DB Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), toid, "AuthFailedAutomaticCron");
        }
        catch(PZGenericConstraintViolationException ge){
            transactionLogger.error("PZGenericConstraintViolationException",ge);
        }
        finally
        {
            if(connection!=null){
                Database.closeConnection(connection);
            }
            //Database.closeResultSet(resultSet);
        }

        transactionLogger.debug("total count---"+success);

        return responsemap;

    }

    public void updateMainTableEntry(String transStatus, String trackingid)
    {
        transactionLogger.error("-----inside main update-----"+transStatus);
        Functions functions = new Functions();
        Connection connection = null;
        try
        {
            connection              = Database.getConnection();
            String updateQuery1 = "";
            if("authfailed".equals(transStatus))
            {
                updateQuery1     = "UPDATE transaction_common SET status=?, failuretimestamp = ? WHERE trackingid=?";
                PreparedStatement ps2 = connection.prepareStatement(updateQuery1);
                ps2.setString(1, transStatus);
                ps2.setString(2, functions.getTimestamp());
                ps2.setString(3, trackingid);
                ps2.executeUpdate();
            }
            else
            {
                updateQuery1     = "UPDATE transaction_common SET status=? WHERE trackingid=?";
                PreparedStatement ps2   = connection.prepareStatement(updateQuery1);
                ps2.setString(1, transStatus);
                ps2.setString(2, trackingid);
                ps2.executeUpdate();
            }
        }
        catch (SQLException se)
        {
            transactionLogger.error("SQLException---", se);
        }
        catch (SystemError s)
        {
            transactionLogger.error("SQLException---", s);
        }
        finally
        {
            if(connection!=null)
                Database.closeConnection(connection);
        }
    }

    public List<TransactionDetailsVO> getTransactionList(String trackingid, String status, String accountId, String terminalId, String startDate, String endDate, String memberId, String fromType, String gatwaycurrency)
    {

        Connection conn         = null;
        PreparedStatement pstmt = null;
        Functions functions     = new Functions();

        List<TransactionDetailsVO> transactionDetailsVOList = new ArrayList<>();
        TransactionDetailsVO transactionDetailsVO           = null;
        try
        {
            conn                = Database.getConnection();
            StringBuffer query1 = new StringBuffer("SELECT trackingid,accountid,toid,terminalid,paymentid,amount,currency,orderdescription,status,ipaddress,timestamp,notificationUrl,authorization_code ,customerId , emailaddr ,customerIp ,ccnum,name FROM transaction_common WHERE FROM_UNIXTIME(dtstamp) < DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -144 HOUR) AND STATUS='authstarted' AND  fromtype=? "); //FROM_UNIXTIME(dtstamp) < DATE_ADD(CURRENT_TIMESTAMP, INTERVAL -144 HOUR) AND

            if (functions.isValueNull(memberId))
            {
                query1.append(" AND toid =" + memberId);
            }
            if (functions.isValueNull(accountId))
            {
                query1.append(" AND accountid =" + accountId);
            }
            if (functions.isValueNull(terminalId))
            {
                query1.append(" AND terminalid =" + terminalId);
            }

            if (functions.isValueNull(gatwaycurrency))
            {
                query1.append(" AND currency = '" + gatwaycurrency + "'");
            }

            /*if (functions.isValueNull(startDate) && functions.isValueNull(endDate))
            {
                query1.append("AND FROM_UNIXTIME(dtstamp) >='" + startDate + "' AND FROM_UNIXTIME(dtstamp) <='" + endDate + "'");
            }*/

            pstmt = conn.prepareStatement(query1.toString());
            pstmt.setString(1, fromType);
            transactionLogger.error("Ouery getTransactionList  for -----" + pstmt);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next())
            {
                transactionDetailsVO = new TransactionDetailsVO();
                transactionDetailsVO.setTrackingid(resultSet.getString("trackingid"));
                transactionDetailsVO.setAccountId(resultSet.getString("accountid"));
                transactionDetailsVO.setToid(resultSet.getString("toid"));
                transactionDetailsVO.setTerminalId(resultSet.getString("terminalid"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setPaymentId(resultSet.getString("paymentid"));
                transactionDetailsVO.setAmount(resultSet.getString("amount"));
                transactionDetailsVO.setCurrency(resultSet.getString("currency"));
                transactionDetailsVO.setOrderDescription(resultSet.getString("orderdescription"));
                transactionDetailsVO.setStatus(resultSet.getString("status"));
                transactionDetailsVO.setIpAddress(resultSet.getString("ipaddress"));
                transactionDetailsVO.setTransactionTime(resultSet.getString("timestamp"));
                transactionDetailsVO.setNotificationUrl(resultSet.getString("notificationUrl"));
                transactionDetailsVO.setAuthorization_code(resultSet.getString("Authorization_code"));
                transactionDetailsVO.setCustomerId(resultSet.getString("CustomerId"));
                transactionDetailsVO.setCcnum(resultSet.getString("ccnum"));
                transactionDetailsVO.setEmailaddr(resultSet.getString("emailaddr"));
                transactionDetailsVO.setName(resultSet.getString("name"));
                transactionDetailsVO.setCustomerIp(resultSet.getString("customerIp"));


                transactionDetailsVOList.add(transactionDetailsVO);
            }
        }

        catch (Exception e)
        {
            transactionLogger.error("Exception while reading authstarted transactions----", e);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return transactionDetailsVOList;
    }

}
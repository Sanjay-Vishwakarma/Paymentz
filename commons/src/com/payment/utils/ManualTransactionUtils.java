package com.payment.utils;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.PfsPaymentGateway;
import com.directi.pg.core.paymentgateway.QwipiPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.ManualRecurringManager;
import com.manager.PaymentManager;
import com.manager.RecurringManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.RecurringDAO;
import com.manager.vo.ManualRebillResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.RecurringBillingVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Ecommpay.EcommpayPaymentGateway;
import com.payment.billdesk.BillDeskPaymentGateway;
import com.payment.borgun.core.BorgunPaymentGateway;
import com.payment.cashflow.core.CashFlowAccounts;
import com.payment.cashflow.core.CashFlowPaymentGateway;
import com.payment.common.core.*;
import com.payment.easypay.EasyPaymentGateway;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.nexi.NexiPaymentGateway;
import com.payment.payeezy.PayeezyPaymentGateway;
import com.payment.payforasia.core.PayforasiaAccount;
import com.payment.payforasia.core.PayforasiaPaymentGateway;
import com.payment.paynetics.core.PayneticsGateway;
import com.payment.payonOppwa.PayonOppwaPaymentGateway;
import com.payment.powercash21.PowerCash21PaymentGateway;
import com.payment.processing.core.ProcessingPaymentGateway;
import com.payment.secureTrading.SecureTradingGateway;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.xcepts.XceptsPaymentGateway;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;

/**
 * Created by Jinesh on 1/18/2016.
 */
public class ManualTransactionUtils
{
    private static Logger log = new Logger(ManualTransactionUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ManualTransactionUtils.class.getName());

    public ManualRebillResponseVO manualSingleCall(CommonValidatorVO commonValidatorVO,ServletContext application) throws IOException
    {
        log.debug("Enterred into ManualTransactionUtils");
        transactionLogger.debug("Enterred into ManualTransactionUtils");

        AbstractPaymentGateway abstractPaymentGateway = null;
        ManualRecurringManager manualRecurringManager = new ManualRecurringManager();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVo = new AuditTrailVO();
        Transaction transaction = new Transaction();
        ManualRebillResponseVO manualRebillResponseVO = new ManualRebillResponseVO();
        PaymentManager paymentManager = new PaymentManager();
        TransactionUtilsDAO transactionUtilsDAO=new TransactionUtilsDAO();

        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();

        String fromtype = commonValidatorVO.getTransDetailsVO().getFromtype();
        String accountId = commonValidatorVO.getMerchantDetailsVO().getAccountId();
        String trackingId = commonValidatorVO.getTrackingid();
        String header = commonValidatorVO.getTransDetailsVO().getHeader();
        String description = commonValidatorVO.getTransDetailsVO().getOrderId();
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String key = commonValidatorVO.getMerchantDetailsVO().getKey();
        String algoName = commonValidatorVO.getMerchantDetailsVO().getChecksumAlgo();
        String toId=commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String merchantIp = commonValidatorVO.getAddressDetailsVO().getIp();
        String cvv        = commonValidatorVO.getTransDetailsVO().getCvv();

        String respStatus = "";
        String billingDescriptor = "";
        String mailtransactionStatus = "";
        String error="";

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        RecurringManager recurringManager = new RecurringManager();

        try
        {
            if (QwipiPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                Hashtable qwipiMidDetails = new Hashtable();
                GenericCardDetailsVO genericCardDetailsVO = new GenericCardDetailsVO();
                QwipiAddressDetailsVO qwipiAddressDetailsVO = new QwipiAddressDetailsVO();
                QwipiTransDetailsVO qwipiTransDetailsVO = new QwipiTransDetailsVO();
                QwipiResponseVO qwipiResponseVO = new QwipiResponseVO();
                String MD5INFO = "";

                QwipiRequestVO qwipiRequestVO = null;

                abstractPaymentGateway = AbstractPaymentGateway.getGateway(accountId);

                commonValidatorVO = manualRecurringManager.getQwipiDetailsForManualRebill(trackingId);
                if ((commonValidatorVO == null || commMerchantVO.equals("")) || (commonValidatorVO.getTransDetailsVO().getResponseOrderNumber() == null || commonValidatorVO.getTransDetailsVO().getResponseOrderNumber().equals("")))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    //calCheckSumAndWriteStatusForRebill(pWriter, "", description, amount, "N", "No Record found for given Details", key, algoName, null);
                    commonValidatorVO.setErrorMsg("Functionality is not supported");
                    calCheckSumAndWriteStatusForRebill(commonValidatorVO,manualRebillResponseVO,"N");
                    return manualRebillResponseVO;
                }
                auditTrailVo.setActionExecutorId(toId);
                auditTrailVo.setActionExecutorName("Manual Rebill");

                int newTrackingId = manualRecurringManager.insertNewTransactionQwipi(commonValidatorVO, "authstarted", header, description, amount);

                entry.actionEntryForQwipi(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, merchantIp, qwipiResponseVO, auditTrailVo);

                qwipiMidDetails = transaction.getMidKeyForQwipi(accountId);

                MD5INFO = Functions.convertmd5(commonValidatorVO.getTransDetailsVO().getResponseOrderNumber() + qwipiMidDetails.get("midkey"));

                qwipiTransDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getResponseOrderNumber());
                qwipiTransDetailsVO.setBillNo(description);
                qwipiTransDetailsVO.setAmount(amount);
                qwipiAddressDetailsVO.setMd5info(MD5INFO);

                qwipiRequestVO = new QwipiRequestVO(genericCardDetailsVO, qwipiAddressDetailsVO, qwipiTransDetailsVO);

                qwipiResponseVO = (QwipiResponseVO) abstractPaymentGateway.processRebilling(String.valueOf(newTrackingId), qwipiRequestVO);

                if (qwipiResponseVO.getResultCode().equals("0") || qwipiResponseVO.getResultCode() == "0")//success - 0
                {
                    manualRecurringManager.updateQwipiRebillAfterResponse(qwipiResponseVO, "capturesuccess", String.valueOf(newTrackingId));
                    entry.actionEntryForQwipi(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, merchantIp, qwipiResponseVO, auditTrailVo);
                    commonValidatorVO.setErrorMsg(qwipiResponseVO.getRemark());
                    respStatus = "capturesuccess";
                    billingDescriptor = qwipiResponseVO.getBillingDescriptor();
                }
                else if (qwipiResponseVO.getResultCode().equals("1") || qwipiResponseVO.getResultCode() == "1")//authfail - 1
                {
                    manualRecurringManager.updateQwipiRebillAfterResponse(qwipiResponseVO, "authfailed", String.valueOf(newTrackingId));
                    entry.actionEntryForQwipi(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, merchantIp, qwipiResponseVO, auditTrailVo);
                    commonValidatorVO.setErrorMsg(qwipiResponseVO.getRemark());
                    respStatus = "authfailed";

                }
                else//fail
                {
                    manualRecurringManager.updateQwipiRebillAfterResponse(qwipiResponseVO, "failed", String.valueOf(newTrackingId));
                    entry.actionEntryForQwipi(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_FAILED, ActionEntry.STATUS_FAILED, merchantIp, qwipiResponseVO, auditTrailVo);
                    commonValidatorVO.setErrorMsg(qwipiResponseVO.getRemark());
                    respStatus = "failed";

                }
                commonValidatorVO.setTrackingid(String.valueOf(newTrackingId));
                manualRebillResponseVO.setTrackingId(String.valueOf(newTrackingId));
            }
            else if(PfsPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                CommRequestVO commRequestVO = new CommRequestVO();
                CommResponseVO responseVO = new CommResponseVO();

                String currency = null;
                GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
                GenericResponseVO genericResponseVO = new GenericResponseVO();
                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();

                RecurringBillingVO recurringBillingVO = new RecurringBillingVO();
                RecurringDAO recurringDAO = new RecurringDAO();

                commonValidatorVO = manualRecurringManager.getCommontransactionDetails(trackingId);


                if (commonValidatorVO == null || commMerchantVO.equals(""))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    commonValidatorVO.setErrorMsg("Functionality is not supported");
                    calCheckSumAndWriteStatusForRebill(commonValidatorVO, manualRebillResponseVO, "N");
                    return manualRebillResponseVO;
                }
                else
                {
                    currency = commonValidatorVO.getTransDetailsVO().getCurrency();

                    auditTrailVo.setActionExecutorId(toId);
                    auditTrailVo.setActionExecutorName("Manual Rebill");

                    abstractPaymentGateway = AbstractPaymentGateway.getGateway(accountId);

                    commonValidatorVO.getTransDetailsVO().setOrderId(description);
                    commonValidatorVO.getTransDetailsVO().setAmount(amount);
                    commonValidatorVO.getTransDetailsVO().setHeader(header);
                    int newTrackingId = manualRecurringManager.insertAuthStartedForCommon(commonValidatorVO, "authstarted");
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, responseVO, auditTrailVo, merchantIp);

                    commRequestVO.setCommMerchantVO(commMerchantVO);

                    recurringBillingVO.setRbid(commonValidatorVO.getRecurringBillingVO().getRbid());
                    commTransactionDetailsVO.setAmount(amount);
                    commTransactionDetailsVO.setCurrency(currency);
                    commMerchantVO.setMerchantUsername(username);
                    commMerchantVO.setPassword(password);

                    commRequestVO.setRecurringBillingVO(recurringBillingVO);
                    commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

                    responseVO = (CommResponseVO) abstractPaymentGateway.processRebilling(String.valueOf(newTrackingId), commRequestVO);

                    String paymentid = responseVO.getTransactionId();
                    String remark = responseVO.getRemark();
                    String dateTime = responseVO.getResponseTime();
                    String status = responseVO.getStatus();

                    if (responseVO.getStatus().equalsIgnoreCase("success"))
                    {
                        billingDescriptor = responseVO.getDescriptor();
                        paymentManager.updateTransactionAfterResponseForCommon("capturesuccess", amount, "", paymentid, remark, dateTime, String.valueOf(newTrackingId));
                        entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVo, null);
                        recurringBillingVO = recurringDAO.getRecurringSubscriptionDetails(trackingId);

                        recurringBillingVO.setRecurring_subscrition_id(recurringBillingVO.getRecurring_subscrition_id());
                        recurringBillingVO.setParentBankTransactionID("");//response idi.e messageid
                        recurringBillingVO.setBankRecurringBillingID(commonValidatorVO.getRecurringBillingVO().getRbid());//197
                        recurringBillingVO.setNewBankTransactionID(responseVO.getTransactionId());//R6
                        recurringBillingVO.setParentPzTransactionID("");//R5
                        recurringBillingVO.setAmount(amount);
                        recurringBillingVO.setDescription(description);//R3
                        recurringBillingVO.setNewPzTransactionID(String.valueOf(newTrackingId));//newTrackingid
                        recurringBillingVO.setRecurringRunDate(String.valueOf(responseVO.getResponseTime()));
                        recurringBillingVO.setTransactionStatus(status);

                        recurringManager.insertRecurringDetailsEntry(recurringBillingVO);
                        commonValidatorVO.setErrorMsg(responseVO.getRemark());

                        respStatus = "capturesuccess";
                        mailtransactionStatus = "success";
                    }
                    else
                    {
                        paymentManager.updateTransactionAfterResponseForCommon("authfailed", amount, "", paymentid, remark, dateTime, String.valueOf(newTrackingId));
                        entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVo, null);
                        commonValidatorVO.setErrorMsg(responseVO.getRemark());
                        respStatus = "authfailed";
                        mailtransactionStatus = "failed";
                    }
                    commonValidatorVO.setTrackingid(String.valueOf(newTrackingId));
                    manualRebillResponseVO.setTrackingId(String.valueOf(newTrackingId));
                }
            }
            else if (CashFlowPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                log.debug("inside cashflow block----");
                CommRequestVO commRequestVO = new CommRequestVO();
                CommResponseVO responseVO = new CommResponseVO();
                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                RecurringBillingVO recurringBillingVO = new RecurringBillingVO();
                RecurringDAO recurringDAO = new RecurringDAO();

                CashFlowAccounts cashFlowAccounts = new CashFlowAccounts();
                abstractPaymentGateway = AbstractPaymentGateway.getGateway(accountId);
                log.debug("abstractPaymentGateway-----"+abstractPaymentGateway);

                commonValidatorVO = manualRecurringManager.getCommontransactionDetails(trackingId);
                log.debug("ResponseTransactionId----" + commonValidatorVO.getTransDetailsVO().getOrderId());
                if(commonValidatorVO == null || commMerchantVO == null || commonValidatorVO.getTransDetailsVO().getOrderId() == null || commonValidatorVO.getTransDetailsVO().getOrderId().equals(""))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    commonValidatorVO.setErrorMsg("Functionality is not supported");
                    calCheckSumAndWriteStatusForRebill(commonValidatorVO,manualRebillResponseVO,"N");
                    return manualRebillResponseVO;
                }

                Hashtable getMid = cashFlowAccounts.getMidPassword(accountId);
                String authid = URLEncoder.encode((String) getMid.get("mid"), "UTF-8");
                auditTrailVo.setActionExecutorId(toId);
                auditTrailVo.setActionExecutorName("Manual Rebill");

                commonValidatorVO.getTransDetailsVO().setOrderId(description);
                commonValidatorVO.getTransDetailsVO().setAmount(amount);
                commonValidatorVO.getTransDetailsVO().setHeader(header);
                commonValidatorVO.getTransDetailsVO().setFromid(authid);
                commonValidatorVO.getTransDetailsVO().setFromtype(fromtype);
                int newTrackingId = manualRecurringManager.insertAuthStartedForCommon(commonValidatorVO, "authStarted");

                entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, responseVO, auditTrailVo, merchantIp);

                commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
                commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                //commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());

                commTransactionDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                commTransactionDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());

                commAddressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
                commAddressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
                commAddressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
                commAddressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
                commAddressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
                commAddressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
                commAddressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
                commAddressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
                recurringBillingVO.setRbid(commonValidatorVO.getRecurringBillingVO().getRbid());

                commRequestVO.setCardDetailsVO(commCardDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setRecurringBillingVO(recurringBillingVO);

                responseVO = (CommResponseVO) abstractPaymentGateway.processRebilling(String.valueOf(newTrackingId), commRequestVO);

                String paymentid = responseVO.getTransactionId();
                String remark = responseVO.getRemark();
                String dateTime = responseVO.getResponseTime();
                String status = responseVO.getStatus();

                if(responseVO.getStatus().equalsIgnoreCase("success"))
                {
                    paymentManager.updateTransactionAfterResponseForCommon("capturesuccess", amount, "", paymentid, remark, dateTime, String.valueOf(newTrackingId));
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVo, null);
                    recurringBillingVO = recurringDAO.getRecurringSubscriptionDetails(trackingId);

                    recurringBillingVO.setRecurring_subscrition_id(recurringBillingVO.getRecurring_subscrition_id());
                    recurringBillingVO.setParentBankTransactionID("");//response idi.e messageid
                    recurringBillingVO.setBankRecurringBillingID(commonValidatorVO.getRecurringBillingVO().getRbid());//197
                    recurringBillingVO.setNewBankTransactionID(responseVO.getTransactionId());//R6
                    recurringBillingVO.setParentPzTransactionID("");//R5
                    recurringBillingVO.setAmount(amount);
                    recurringBillingVO.setDescription(description);//R3
                    recurringBillingVO.setNewPzTransactionID(String.valueOf(newTrackingId));//newTrackingid
                    recurringBillingVO.setRecurringRunDate(String.valueOf(responseVO.getResponseTime()));
                    recurringBillingVO.setTransactionStatus(status);

                    recurringManager.insertRecurringDetailsEntry(recurringBillingVO);
                    commonValidatorVO.setErrorMsg(responseVO.getRemark());

                    respStatus = "capturesuccess";
                    billingDescriptor = responseVO.getDescriptor();
                }
                else if (responseVO.getStatus().equalsIgnoreCase("fail"))
                {
                    paymentManager.updateTransactionAfterResponseForCommon("authfailed", amount, "", paymentid, remark, dateTime, String.valueOf(newTrackingId));
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVo, null);
                    respStatus = "authfailed";
                }
                else//fail
                {
                    paymentManager.updateTransactionAfterResponseForCommon("failed", amount, "", paymentid, remark, dateTime, String.valueOf(newTrackingId));
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVo, null);
                    respStatus = "failed";
                }
                commonValidatorVO.setTrackingid(String.valueOf(newTrackingId));
                commonValidatorVO.setErrorMsg(responseVO.getRemark());
                manualRebillResponseVO.setTrackingId(String.valueOf(newTrackingId));
            }
            //POWERCASH21 RECURRING

            else if(PowerCash21PaymentGateway.GATEWAY_TYPE.equals(fromtype)){
                log.debug("inside POWERCASH21PAYMENTGATEWAY REBILLING block----");
                CommRequestVO commRequestVO = new CommRequestVO();
                CommResponseVO responseVO = new CommResponseVO();
                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
                RecurringBillingVO recurringBillingVO = new RecurringBillingVO();
                RecurringDAO recurringDAO = new RecurringDAO();
                abstractPaymentGateway = AbstractPaymentGateway.getGateway(accountId);
                log.debug("abstractPaymentGateway-----"+abstractPaymentGateway);
                commonValidatorVO = manualRecurringManager.getCommontransactionDetails(trackingId);
                log.debug("ResponseTransactionId----" + commonValidatorVO.getTransDetailsVO().getOrderId());
                String notificationUrl=commonValidatorVO.getMerchantDetailsVO().getNotificationUrl();
                if(commonValidatorVO == null || commMerchantVO == null || commonValidatorVO.getTransDetailsVO().getOrderId() == null || commonValidatorVO.getTransDetailsVO().getOrderId().equals(""))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    commonValidatorVO.setErrorMsg("Functionality is not supported");
                    calCheckSumAndWriteStatusForRebill(commonValidatorVO,manualRebillResponseVO,"N");
                    return manualRebillResponseVO;
                }

                auditTrailVo.setActionExecutorId(toId);
                auditTrailVo.setActionExecutorName("Manual Rebill");

                commonValidatorVO.getTransDetailsVO().setOrderId(description);
                commonValidatorVO.getTransDetailsVO().setAmount(amount);
                commonValidatorVO.getTransDetailsVO().setHeader(header);
               // commonValidatorVO.getTransDetailsVO().setFromid(authid);
                commonValidatorVO.getTransDetailsVO().setFromtype(fromtype);
                int newTrackingId = manualRecurringManager.insertAuthStartedForCommon(commonValidatorVO, "authStarted");

                entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, responseVO, auditTrailVo, merchantIp);

                commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
                commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
                commTransactionDetailsVO.setPaymentid(commonValidatorVO.getTransDetailsVO().getPaymentid());
                commAddressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
                commTransactionDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                commTransactionDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());

                commAddressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
                commAddressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
                commAddressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
                commAddressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
                commAddressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
                commAddressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
                commAddressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
                commAddressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
                recurringBillingVO.setRbid(commonValidatorVO.getRecurringBillingVO().getRbid());
                commRequestVO.setCardDetailsVO(commCardDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setRecurringBillingVO(recurringBillingVO);

                responseVO = (CommResponseVO) abstractPaymentGateway.processRebilling(String.valueOf(newTrackingId), commRequestVO);

                String paymentid = responseVO.getTransactionId();
                String remark = responseVO.getRemark();
                String dateTime = responseVO.getResponseTime();
                String status = responseVO.getStatus();

                if(responseVO.getStatus().equalsIgnoreCase("success"))
                {
                    paymentManager.updateTransactionAfterResponseForCommon("capturesuccess", amount, "", paymentid, remark, dateTime, String.valueOf(newTrackingId));
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVo, null);
                    recurringBillingVO = recurringDAO.getRecurringSubscriptionDetails(trackingId);
                    recurringBillingVO.setRecurring_subscrition_id(recurringBillingVO.getRecurring_subscrition_id());
                    recurringBillingVO.setParentBankTransactionID(recurringBillingVO.getOriginalBankTransactionId());
                    recurringBillingVO.setBankRecurringBillingID(commonValidatorVO.getRecurringBillingVO().getRbid());//197
                    recurringBillingVO.setNewBankTransactionID(responseVO.getTransactionId());//R6
                    recurringBillingVO.setParentPzTransactionID(trackingId);
                    recurringBillingVO.setAmount(amount);
                    recurringBillingVO.setDescription(description);//R3
                    recurringBillingVO.setNewPzTransactionID(String.valueOf(newTrackingId));//newTrackingid
                    recurringBillingVO.setRecurringRunDate(String.valueOf(responseVO.getResponseTime()));
                    recurringBillingVO.setTransactionStatus(status);
                    recurringManager.insertRecurringDetailsEntry(recurringBillingVO);
                    commonValidatorVO.setErrorMsg(responseVO.getRemark());

                    respStatus = "capturesuccess";
                    billingDescriptor = responseVO.getDescriptor();
                }
                else if (responseVO.getStatus().equalsIgnoreCase("fail"))
                {
                    paymentManager.updateTransactionAfterResponseForCommon("authfailed", amount, "", paymentid, remark, dateTime, String.valueOf(newTrackingId));
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVo, null);
                    respStatus = "authfailed";
                }
                else//fail
                {
                    paymentManager.updateTransactionAfterResponseForCommon("failed", amount, "", paymentid, remark, dateTime, String.valueOf(newTrackingId));
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVo, null);
                    respStatus = "failed";
                }
                commonValidatorVO.setTrackingid(String.valueOf(newTrackingId));
                commonValidatorVO.setErrorMsg(responseVO.getRemark());
                manualRebillResponseVO.setTrackingId(String.valueOf(newTrackingId));

            }

            //Payforasia  recurring
            else if (PayforasiaPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                CommRequestVO commRequestVO = new CommRequestVO();
                CommResponseVO responseVO = new CommResponseVO();
                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();

                PayforasiaAccount payforasiaAccount = new PayforasiaAccount();
                abstractPaymentGateway = AbstractPaymentGateway.getGateway(accountId);
                Functions functions = new Functions();
                commonValidatorVO = manualRecurringManager.getPayforasiaManualRebilldata(commonValidatorVO, trackingId);
                log.debug("ResponseTrans actionId----" + commonValidatorVO.getTransDetailsVO().getOrderId());
                if(commonValidatorVO == null || commMerchantVO == null || commonValidatorVO.getTransDetailsVO().getOrderId() == null || commonValidatorVO.getTransDetailsVO().getOrderId().equals(""))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    commonValidatorVO.setErrorMsg("Functionality is not supported");
                    calCheckSumAndWriteStatusForRebill(commonValidatorVO,manualRebillResponseVO,"N");
                    return manualRebillResponseVO;
                }

                /*Hashtable getMid = payforasiaAccount.getValuesFromDb(accountId);//Changed*/
               /* String authid = URLEncoder.encode((String) getMid.get("mid"), "UTF-8");*/

                auditTrailVo.setActionExecutorId(toId);
                auditTrailVo.setActionExecutorName("Manual Rebill");

                commonValidatorVO.getTransDetailsVO().setOrderId(description);
                commonValidatorVO.getTransDetailsVO().setAmount(amount);
                commonValidatorVO.getTransDetailsVO().setHeader(header);
                commonValidatorVO.getTransDetailsVO().setFromtype(fromtype);

               /* commonValidatorVO.getTransDetailsVO().setFromid(authid);//Changed*/

                int newTrackingId = manualRecurringManager.insertAuthStartedForCommon(commonValidatorVO, "authStarted");

                entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, responseVO, auditTrailVo, merchantIp);

                commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
                commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                //commCardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());


                commTransactionDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                commTransactionDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
                commTransactionDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());



                commAddressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
                commAddressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
                commAddressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
                commAddressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
                commAddressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
                commAddressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
                commAddressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
                commAddressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
                commAddressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
                commAddressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());

                commRequestVO.setCardDetailsVO(commCardDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);

                responseVO = (CommResponseVO) abstractPaymentGateway.processRebilling(String.valueOf(newTrackingId), commRequestVO);

                String paymentid = responseVO.getTransactionId();
                String remark = responseVO.getRemark();
                String dateTime = responseVO.getResponseTime();

                if(responseVO.getStatus().equalsIgnoreCase("success"))
                {
                    paymentManager.updateTransactionAfterResponseForCommon("capturesuccess", amount, "", paymentid, remark, dateTime, String.valueOf(newTrackingId));
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVo, null);
                    respStatus = "capturesuccess";
                    billingDescriptor = responseVO.getDescriptor();
                }
                else if (responseVO.getStatus().equalsIgnoreCase("fail"))
                {
                    paymentManager.updateTransactionAfterResponseForCommon("authfailed", amount, "", paymentid, remark, dateTime, String.valueOf(newTrackingId));
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVo, null);
                    respStatus = "authfailed";
                }
                else//fail
                {
                    paymentManager.updateTransactionAfterResponseForCommon("failed", amount, "", paymentid, remark, dateTime, String.valueOf(newTrackingId));
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVo, null);
                    respStatus = "failed";
                }
                commonValidatorVO.setTrackingid(String.valueOf(newTrackingId));
                commonValidatorVO.setErrorMsg(responseVO.getRemark());
                manualRebillResponseVO.setTrackingId(String.valueOf(newTrackingId));
            }
            else if (BorgunPaymentGateway.GATEWAY_TYPE.equals(fromtype) || PayneticsGateway.GATEWAY_TYPE.equals(fromtype) || ProcessingPaymentGateway.GATEWAY_TYPE.equals(fromtype)|| BillDeskPaymentGateway.GATEWAY_TYPE.equals(fromtype) || NexiPaymentGateway.GATEWAY_TYPE.equals(fromtype) || PayeezyPaymentGateway.GATEWAY_TYPE.equals(fromtype)|| EcommpayPaymentGateway.GATEWAY_TYPE.equals(fromtype)|| PayonOppwaPaymentGateway.GATEWAY_TYPE.equals(fromtype)|| SecureTradingGateway.GATEWAY_TYPE.equals(fromtype) || EasyPaymentGateway.GATEWAY_TYPE.equals(fromtype) || XceptsPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                CommRequestVO commRequestVO = new CommRequestVO();
                commRequestVO=getCommonRequestVO(commonValidatorVO,fromtype);
                CommResponseVO responseVO = new CommResponseVO();
                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
                CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
                RecurringBillingVO recurringBillingVO = new RecurringBillingVO();
                RecurringDAO recurringDAO = new RecurringDAO();
                Functions functions=new Functions();
                String currency = null;
                //String cvv=paymentManager.getCvv(commonValidatorVO);
                commonValidatorVO = manualRecurringManager.getCommontransactionDetails(trackingId);
                String transaction_mode="Non-3D";
                String notificationUrl=commonValidatorVO.getMerchantDetailsVO().getNotificationUrl();
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getTransactionmode())){
                    transaction_mode=commonValidatorVO.getTransDetailsVO().getTransactionmode();
                }
                if (commonValidatorVO == null || commMerchantVO.equals(""))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    commonValidatorVO.setErrorMsg("Functionality is not supported");
                    calCheckSumAndWriteStatusForRebill(commonValidatorVO, manualRebillResponseVO, "N");
                    return manualRebillResponseVO;
                }

                else if("authfailed".equalsIgnoreCase(commonValidatorVO.getStatus())||"failed".equalsIgnoreCase(commonValidatorVO.getStatus())
                        && (EcommpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype) || PayonOppwaPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype) || SecureTradingGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype) || EasyPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype) || XceptsPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype)))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    commonValidatorVO.setErrorMsg("No Records Found");
                    calCheckSumAndWriteStatusForRebill(commonValidatorVO, manualRebillResponseVO, "N");
                    return manualRebillResponseVO;
                }
                else
                {

                    auditTrailVo.setActionExecutorId(toId);
                    auditTrailVo.setActionExecutorName("Manual Rebill");
                    abstractPaymentGateway = AbstractPaymentGateway.getGateway(accountId);
                    if(PayneticsGateway.GATEWAY_TYPE.equals(fromtype) || BillDeskPaymentGateway.GATEWAY_TYPE.equals(fromtype) || PayeezyPaymentGateway.GATEWAY_TYPE.equals(fromtype) || EasyPaymentGateway.GATEWAY_TYPE.equals(fromtype)|| XceptsPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                    {
                        String expiryDate = PzEncryptor.decryptExpiryDate(commonValidatorVO.getCardDetailsVO().getExpMonth());
                        String dateArr[] = expiryDate.split("/");
                        String expMonth = dateArr[0];
                        String year = dateArr[1];
                        commCardDetailsVO.setExpMonth(expMonth);
                        commCardDetailsVO.setExpYear(year);
                        commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(commonValidatorVO.getCardDetailsVO().getCardNum()));
                        /*if(functions.isValueNull(cvv))
                            commCardDetailsVO.setcVV(PzEncryptor.decryptCVV(cvv));*/
                    }
                   /*commCardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
                   commCardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());*/

                    commonValidatorVO.getTransDetailsVO().setOrderId(description);
                    commonValidatorVO.getTransDetailsVO().setAmount(amount);
                    commonValidatorVO.getTransDetailsVO().setHeader(header);
                    commonValidatorVO.getMerchantDetailsVO().setKey(key);

                    int newTrackingId = manualRecurringManager.insertAuthStartedForCommon(commonValidatorVO, "authstarted");
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, responseVO, auditTrailVo, merchantIp);

                    recurringBillingVO.setRbid(commonValidatorVO.getRecurringBillingVO().getRbid());
                    commTransactionDetailsVO.setAmount(amount);
                    commTransactionDetailsVO.setCurrency(commonValidatorVO.getMerchantDetailsVO().getCurrency());

                    commTransactionDetailsVO.setPreviousTransactionId(trackingId);
                    commTransactionDetailsVO.setCvv(cvv);
                    commTransactionDetailsVO.setPaymentid(commonValidatorVO.getTransDetailsVO().getPaymentid());
                    commTransactionDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());

                    commAddressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
                    commAddressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
                    commAddressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
                    commAddressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
                    commAddressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
                    commAddressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
                    commAddressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
                    if (commonValidatorVO.getAddressDetailsVO().getCountry() != null && commonValidatorVO.getAddressDetailsVO().getCountry() != "")
                    {
                        commAddressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
                    }
                    if(functions.isValueNull(merchantIp))
                    {
                        commAddressDetailsVO.setIp(merchantIp);
                    }
                    else
                    {
                        commAddressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
                    }
                    commAddressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                    commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                    commRequestVO.setRecurringBillingVO(recurringBillingVO);
                    commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                    commRequestVO.setCardDetailsVO(commCardDetailsVO);
                    commRequestVO.setCommMerchantVO(commMerchantVO);


                    responseVO = (CommResponseVO) abstractPaymentGateway.processRebilling(String.valueOf(newTrackingId), commRequestVO);

                    String paymentid = responseVO.getTransactionId();
                    String remark = responseVO.getDescription();
                    String dateTime = responseVO.getResponseTime();
                    String status = responseVO.getStatus();

                    System.out.println("Status in Rebill---"+status);
                    if (responseVO.getStatus().equalsIgnoreCase("success"))
                    {
                        billingDescriptor = responseVO.getDescriptor();
                        transactionUtilsDAO.updateTransactionAfterResponse("transaction_common", "capturesuccess", amount, commonValidatorVO.getAddressDetailsVO().getIp(), "", paymentid, remark, dateTime, String.valueOf(newTrackingId), responseVO.getRrn(), responseVO.getArn(), responseVO.getAuthCode(), transaction_mode);
                        entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVo, null);

                        recurringBillingVO = recurringDAO.getRecurringSubscriptionDetails(trackingId);
                        recurringBillingVO.setRecurring_subscrition_id(recurringBillingVO.getRecurring_subscrition_id());
                        recurringBillingVO.setParentBankTransactionID(recurringBillingVO.getOriginalBankTransactionId());
                        recurringBillingVO.setBankRecurringBillingID(commonValidatorVO.getRecurringBillingVO().getRbid());
                        recurringBillingVO.setNewBankTransactionID(responseVO.getTransactionId());
                        recurringBillingVO.setParentPzTransactionID(trackingId);
                        recurringBillingVO.setAmount(amount);
                        recurringBillingVO.setDescription(description);
                        recurringBillingVO.setNewPzTransactionID(String.valueOf(newTrackingId));//newTrackingid
                        recurringBillingVO.setRecurringRunDate(String.valueOf(responseVO.getResponseTime()));
                        recurringBillingVO.setTransactionStatus(status);
                        recurringManager.insertRecurringDetailsEntry(recurringBillingVO);
                        commonValidatorVO.setErrorMsg(responseVO.getDescription());
                        respStatus = "capturesuccess";
                        mailtransactionStatus = "success";
                    }
                    else
                    {
                        transactionUtilsDAO.updateTransactionAfterResponse("transaction_common", "authfailed", amount, commonValidatorVO.getAddressDetailsVO().getIp(), "", paymentid, remark, dateTime, String.valueOf(newTrackingId), responseVO.getRrn(), responseVO.getArn(), responseVO.getAuthCode(), transaction_mode);
                        entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, ActionEntry.STATUS_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVo, null);
                        recurringBillingVO = recurringDAO.getRecurringSubscriptionDetails(trackingId);
                        recurringBillingVO.setRecurring_subscrition_id(recurringBillingVO.getRecurring_subscrition_id());
                        recurringBillingVO.setParentBankTransactionID(recurringBillingVO.getOriginalBankTransactionId());
                        recurringBillingVO.setBankRecurringBillingID(commonValidatorVO.getRecurringBillingVO().getRbid());
                        recurringBillingVO.setNewBankTransactionID(responseVO.getTransactionId());
                        recurringBillingVO.setParentPzTransactionID(trackingId);
                        recurringBillingVO.setAmount(amount);
                        recurringBillingVO.setDescription(description);
                        recurringBillingVO.setNewPzTransactionID(String.valueOf(newTrackingId));
                        recurringBillingVO.setRecurringRunDate(String.valueOf(responseVO.getResponseTime()));
                        recurringBillingVO.setTransactionStatus(status);
                        recurringManager.insertRecurringDetailsEntry(recurringBillingVO);
                        commonValidatorVO.setErrorMsg(responseVO.getDescription());
                        respStatus = "authfailed";
                        mailtransactionStatus = "failed";
                    }
                    commonValidatorVO.setTrackingid(String.valueOf(newTrackingId));
                    manualRebillResponseVO.setTrackingId(String.valueOf(newTrackingId));
                    }
                transactionLogger.error("Notificatin Sending to---" + notificationUrl + "---" + manualRebillResponseVO.getTrackingId());
                MerchantDAO merchantDAO = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                if (functions.isValueNull(notificationUrl))
                {
                    TransactionManager transactionManager = new TransactionManager();
                    transactionLogger.error("inside sending notification---" + notificationUrl);

                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    TransactionDetailsVO updatedTransactionDetailsVO = transactionManager.getTransDetailFromCommon(manualRebillResponseVO.getTrackingId());
                    updatedTransactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    updatedTransactionDetailsVO.setBillingDesc(responseVO.getDescriptor());
                    if (functions.isValueNull(updatedTransactionDetailsVO.getCcnum()))
                        updatedTransactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(updatedTransactionDetailsVO.getCcnum()));
                    if (functions.isValueNull(updatedTransactionDetailsVO.getExpdate()))
                        updatedTransactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(updatedTransactionDetailsVO.getExpdate()));
                    asyncNotificationService.sendNotification(updatedTransactionDetailsVO, manualRebillResponseVO.getTrackingId(), respStatus, responseVO.getDescription());
                }
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED);
                error = "This functionality is not supported for your gateway. Kindly contact technical support for assistant.";
                commonValidatorVO.setErrorMsg(error);
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                calCheckSumAndWriteStatusForRebill(commonValidatorVO,manualRebillResponseVO,"N");
                return manualRebillResponseVO;
            }
            commonValidatorVO.getTransDetailsVO().setAmount(amount);
            commonValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDescriptor);
            commonValidatorVO.getTransDetailsVO().setOrderId(description);
        }

        catch (PZConstraintViolationException e)
        {
            log.error("PZConstraintViolationException---",e);
            PZExceptionHandler.handleCVEException(e, toId, PZOperations.MANUAL_REBILL);
            commonValidatorVO.setErrorMsg(e.getPzConstraint().getMessage());
            calCheckSumAndWriteStatusForRebill(commonValidatorVO,manualRebillResponseVO,"N");
            return manualRebillResponseVO;

        }
        catch (PZDBViolationException e)
        {
            log.error("db violation exception",e);
            transactionLogger.error("PZConstraintViolationException in SingleCallManualRebill---",e);
            PZExceptionHandler.handleDBCVEException(e, toId, PZOperations.MANUAL_REBILL);
            commonValidatorVO.setErrorMsg("Internal Error Occured, Please contact Customer Support");
            calCheckSumAndWriteStatusForRebill(commonValidatorVO,manualRebillResponseVO,"N");
            return manualRebillResponseVO;

        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalViolation Exception",e);
            PZExceptionHandler.handleTechicalCVEException(e, toId, PZOperations.MANUAL_REBILL);
            commonValidatorVO.setErrorMsg("We have encountered an internal error while processing your request");
            calCheckSumAndWriteStatusForRebill(commonValidatorVO,manualRebillResponseVO,"N");
            return manualRebillResponseVO;

        }
        catch (SystemError systemError)
        {

            log.error("System Error",systemError);
            transactionLogger.error("systemerror", systemError);
            commonValidatorVO.setErrorMsg("Internal Error Occured, Please contact Customer Support");
            calCheckSumAndWriteStatusForRebill(commonValidatorVO,manualRebillResponseVO,"N");
            return manualRebillResponseVO;

        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("NoSuchAlgorithmException",e);
            transactionLogger.error("ERROR", e);
            commonValidatorVO.setErrorMsg("Internal Error Occured, Please contact Customer Support");
            calCheckSumAndWriteStatusForRebill(commonValidatorVO,manualRebillResponseVO,"N");
            return manualRebillResponseVO;
        }
        catch (PZGenericConstraintViolationException e)
        {
            log.error("NoSuchAlgorithmException",e);
            transactionLogger.error("ERROR", e);
            commonValidatorVO.setErrorMsg("Internal Error Occured, Please contact Customer Support");
            calCheckSumAndWriteStatusForRebill(commonValidatorVO,manualRebillResponseVO,"N");
            return manualRebillResponseVO;
        }
        calCheckSumAndWriteStatusForRebill(commonValidatorVO, manualRebillResponseVO,getResponseStatus(respStatus));
        return manualRebillResponseVO;
    }
    public void calCheckSumAndWriteStatusForRebill(CommonValidatorVO commonValidatorVO,ManualRebillResponseVO manualRebillResponseVO,String status)
    {
        String checkSum = null;
        String amount ="";
        String billingDiscriptor=" ";
        String trackingId="";
        String description="";

        if(commonValidatorVO.getTransDetailsVO()!=null)
        {
            if (null != commonValidatorVO.getTransDetailsVO().getAmount())
            {
                amount = commonValidatorVO.getTransDetailsVO().getAmount();
            }
            if (commonValidatorVO.getTransDetailsVO().getBillingDiscriptor() != null)
            {
                billingDiscriptor = commonValidatorVO.getTransDetailsVO().getBillingDiscriptor();
            }
            if (commonValidatorVO.getTrackingid() != null)
            {
                trackingId = commonValidatorVO.getTrackingid();
            }
            if (commonValidatorVO.getTransDetailsVO().getOrderId() != null)
            {
                description = commonValidatorVO.getTransDetailsVO().getOrderId();
            }
            try
            {
                checkSum = Checksum.generateChecksumV2(description, amount, status, commonValidatorVO.getMerchantDetailsVO().getKey(), commonValidatorVO.getMerchantDetailsVO().getChecksumAlgo());
                log.debug("ManualTransactionUtils.checkSum:::::" + checkSum);
            }
            catch (NoSuchAlgorithmException e)
            {
                log.error("No such algorithm error", e);
                status = "N";
                String statusMsg = e.getMessage();
                PZExceptionHandler.raiseAndHandleTechnicalViolationException("TransactionUtility.java", "calCheckSumAndWriteStatusForRebill()", null, "Transaction", "No Algorithm Exception:::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause(), null, PZOperations.MANUAL_REBILL);
            }
        }
        manualRebillResponseVO.setBillingDescriptor(billingDiscriptor);
        manualRebillResponseVO.setChecksum(checkSum);
        manualRebillResponseVO.setDescription(description);
        manualRebillResponseVO.setAmount(amount);
        manualRebillResponseVO.setStatus(status);
        manualRebillResponseVO.setErrorMessage(commonValidatorVO.getErrorMsg());
        manualRebillResponseVO.setTrackingId(trackingId);
        //pWriter.write(trackingId+ " : " + description  + " : " + amountStr + " : " +status + " : " + errorMsg + " : " + checkSum + " : " + billingDiscriptor);
    }
    private String getResponseStatus(String respState)
    {
        Functions functions=new Functions();
        String state="N";
        if(functions.isValueNull(respState))
        {
            if(respState.equals("capturesuccess"))
            {
                state="Y";
            }
            if(respState.equals("pending"))
            {
                state="P";
            }

        }
        return state;
    }
    private CommRequestVO getCommonRequestVO(CommonValidatorVO commonValidatorVO,String fromtype) throws PZDBViolationException
    {
        CommRequestVO commRequestVO = new CommRequestVO();
        Functions functions=new Functions();
        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();

        merchantAccountVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount()))
            addressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency()))
            addressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);

        return commRequestVO;
    }

}
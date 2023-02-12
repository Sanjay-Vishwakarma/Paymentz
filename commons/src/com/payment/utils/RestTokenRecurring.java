package com.payment.utils;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.PfsPaymentGateway;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.ManualRecurringManager;
import com.manager.PaymentManager;
import com.manager.RecurringManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.RecurringDAO;
import com.manager.helper.RestTransactionHelper;
import com.manager.utils.TransactionUtil;
import com.manager.vo.*;
import com.payment.Ecommpay.EcommpayPaymentGateway;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.borgun.core.BorgunPaymentGateway;
import com.payment.cashflow.core.CashFlowPaymentGateway;
import com.payment.common.core.*;
import com.payment.easypay.EasyPaymentGateway;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.nexi.NexiPaymentGateway;
import com.payment.p4.gateway.P4DirectDebitPaymentGateway;
import com.payment.payonOppwa.PayonOppwaPaymentGateway;
import com.payment.powercash21.PowerCash21PaymentGateway;
import com.payment.secureTrading.SecureTradingGateway;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.xcepts.XceptsPaymentGateway;

import javax.servlet.ServletContext;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jinesh on 1/18/2016.
 */
public class RestTokenRecurring
{
    private static Logger log = new Logger(RestTokenRecurring.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(RestTokenRecurring.class.getName());

    public ManualRebillResponseVO manualSingleCall(CommonValidatorVO commonValidatorVO,ServletContext application) throws PZDBViolationException,PZConstraintViolationException, PZGenericConstraintViolationException
    {
        log.debug("Enterred into ManualTransactionUtils");
        transactionLogger.debug("Enterred into ManualTransactionUtils");

        Functions functions = new Functions();
        AbstractPaymentGateway abstractPaymentGateway = null;
        ManualRecurringManager manualRecurringManager = new ManualRecurringManager();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        ActionEntry entry = new ActionEntry();
        AuditTrailVO auditTrailVo = new AuditTrailVO();
        ManualRebillResponseVO manualRebillResponseVO = new ManualRebillResponseVO();
        PaymentManager paymentManager = new PaymentManager();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        TransactionUtil transactionUtil = new TransactionUtil();
        TransactionUtilsDAO transactionUtilsDAO=new TransactionUtilsDAO();

        String respStatus = "";
        String billingDescriptor = "";

        String trackingId = commonValidatorVO.getTrackingid();
        String token = commonValidatorVO.getToken();
        String username = "";
        String password = "";
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String accountId = commonValidatorVO.getMerchantDetailsVO().getAccountId();
        String merchantIp = commonValidatorVO.getAddressDetailsVO().getIp();
        String toId = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String header = commonValidatorVO.getTransDetailsVO().getHeader();
        String emiCount= commonValidatorVO.getTransDetailsVO().getEmiCount();
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyMMdd_HHmmss");
        String currentSystemDate = dateFormater.format(new Date());
        String description = trackingId+"_"+currentSystemDate;
        String fromtype = GatewayAccountService.getGatewayAccount(accountId).getGateway();
        String cardNum = "";
        String expDate = "";
        String name = "";
        String street = "";
        String state = "";
        String zip = "";
        String country = "";
        String city = "";
        String lastName = "";
        String ip = "";
        String email = "";
        String currency = "";
        String cvv  = "";

        if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getcVV()))
            cvv = PzEncryptor.encryptCVV(commonValidatorVO.getCardDetailsVO().getcVV());
        else if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getCvv()))
            cvv = PzEncryptor.encryptCVV(commonValidatorVO.getTransDetailsVO().getCvv());

        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        username = account.getFRAUD_FTP_USERNAME();
        password = account.getFRAUD_FTP_PASSWORD();

        if ("N".equalsIgnoreCase(account.getIsRecurring()))
        {
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_RECURRINGALLOW, ErrorMessages.INVALID_IS_RECURRING));
            PZExceptionHandler.raiseConstraintViolationException(RestTokenRecurring.class.getName(), "processTransaction()", null, "TransactionServices", ErrorMessages.INVALID_IS_RECURRING, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO,ErrorMessages.INVALID_IS_RECURRING ,new Throwable(ErrorMessages.INVALID_IS_RECURRING));
        }

        try
        {
            /*if (QwipiPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                Hashtable qwipiMidDetails = new Hashtable();
                GenericCardDetailsVO genericCardDetailsVO = new GenericCardDetailsVO();
                QwipiAddressDetailsVO qwipiAddressDetailsVO = new QwipiAddressDetailsVO();
                QwipiTransDetailsVO qwipiTransDetailsVO = new QwipiTransDetailsVO();
                QwipiResponseVO qwipiResponseVO = new QwipiResponseVO();
                String MD5INFO = "";

                QwipiRequestVO qwipiRequestVO = null;

                abstractPaymentGateway = (QwipiPaymentGateway) AbstractPaymentGateway.getGateway(accountId);

                commonValidatorVO = manualRecurringManager.getQwipiDetailsForManualRebill(trackingId);
                if ((commonValidatorVO == null || commMerchantVO.equals("")) || (commonValidatorVO.getTransDetailsVO().getResponseOrderNumber() == null || commonValidatorVO.getTransDetailsVO().getResponseOrderNumber().equals("")))
                {
                    //calCheckSumAndWriteStatusForRebill(pWriter, "", description, amount, "N", "No Record found for given Details", key, algoName, null);
                    commonValidatorVO.setErrorMsg("No Record found for given Details");
                    calCheckSumAndWriteStatusForRebill(commonValidatorVO,manualRebillResponseVO,"N");
                    return manualRebillResponseVO;
                }
                auditTrailVo.setActionExecutorId(toId);
                auditTrailVo.setActionExecutorName("Manual Rebill");

                int newTrackingId = manualRecurringManager.insertNewTransactionQwipi(commonValidatorVO, "authstarted", header, description, amount);

                entry.actionEntryForQwipi(String.valueOf(newTrackingId), amount, entry.STATUS_AUTHORISTION_STARTED, entry.STATUS_AUTHORISTION_STARTED, merchantIp, qwipiResponseVO, auditTrailVo);

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
                    entry.actionEntryForQwipi(String.valueOf(newTrackingId), amount, entry.STATUS_CAPTURE_SUCCESSFUL, entry.STATUS_CAPTURE_SUCCESSFUL, merchantIp, qwipiResponseVO, auditTrailVo);
                    commonValidatorVO.setErrorMsg(qwipiResponseVO.getRemark());
                    respStatus = "capturesuccess";
                    billingDescriptor = qwipiResponseVO.getBillingDescriptor();
                }
                else if (qwipiResponseVO.getResultCode().equals("1") || qwipiResponseVO.getResultCode() == "1")//authfail - 1
                {
                    manualRecurringManager.updateQwipiRebillAfterResponse(qwipiResponseVO, "authfailed", String.valueOf(newTrackingId));
                    entry.actionEntryForQwipi(String.valueOf(newTrackingId), amount, entry.STATUS_AUTHORISTION_FAILED, entry.STATUS_AUTHORISTION_FAILED, merchantIp, qwipiResponseVO, auditTrailVo);
                    commonValidatorVO.setErrorMsg(qwipiResponseVO.getRemark());
                    respStatus = "authfailed";

                }
                else//fail
                {
                    manualRecurringManager.updateQwipiRebillAfterResponse(qwipiResponseVO, "failed", String.valueOf(newTrackingId));
                    entry.actionEntryForQwipi(String.valueOf(newTrackingId), amount, entry.STATUS_FAILED, entry.STATUS_FAILED, merchantIp, qwipiResponseVO, auditTrailVo);
                    commonValidatorVO.setErrorMsg(qwipiResponseVO.getRemark());
                    respStatus = "failed";

                }
                commonValidatorVO.setTrackingid(String.valueOf(newTrackingId));
            }
            else*/
            if (Functions.checkAPIGateways(fromtype))
            {
                CommRequestVO commRequestVO = new CommRequestVO();
                CommResponseVO responseVO = new CommResponseVO();

                GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
                GenericResponseVO genericResponseVO = new GenericResponseVO();
                CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
                CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
                CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();

                RecurringBillingVO recurringBillingVO = new RecurringBillingVO();
                RecurringManager recurringManager = new RecurringManager();
                RecurringDAO recurringDAO = new RecurringDAO();
                RestTransactionHelper restTransactionHelper = new RestTransactionHelper();

                log.debug("trackingid---" + trackingId);
                commonValidatorVO = manualRecurringManager.getCommontransactionDetails(trackingId);
                log.debug("rbid---" + commonValidatorVO.getRecurringBillingVO().getRbid());
                String transaction_mode="Non-3D";
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getTransactionmode())){
                transaction_mode=commonValidatorVO.getTransDetailsVO().getTransactionmode();
                }
                String notificationUrl=commonValidatorVO.getMerchantDetailsVO().getNotificationUrl();
                log.debug("transctionmode---" + transaction_mode);
                if (commonValidatorVO == null || commonValidatorVO.equals(""))
                {
                    //commonValidatorVO.setErrorMsg("No Record found for given Details");
                    errorCodeListVO = restTransactionHelper.getErrorVO(ErrorName.VALIDATION_REQUEST_NULL);
                    PZExceptionHandler.raiseConstraintViolationException("RestTokenRecurring.java", "manualSingleCall", null, "common", ErrorMessages.INVALID_TRACKINGID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }
                auditTrailVo.setActionExecutorId(toId);
                auditTrailVo.setActionExecutorName("Manual Rebill");

                if (PfsPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    abstractPaymentGateway = (PfsPaymentGateway) AbstractPaymentGateway.getGateway(accountId);
                }
                else if (P4DirectDebitPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    abstractPaymentGateway = (P4DirectDebitPaymentGateway) AbstractPaymentGateway.getGateway(accountId);
                }
                else if (CashFlowPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    abstractPaymentGateway = (CashFlowPaymentGateway) AbstractPaymentGateway.getGateway(accountId);
                }
                else if (BorgunPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    abstractPaymentGateway = (BorgunPaymentGateway) AbstractPaymentGateway.getGateway(accountId);
                }
                else if (NexiPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    abstractPaymentGateway = (NexiPaymentGateway) AbstractPaymentGateway.getGateway(accountId);
                }
                else if(EcommpayPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    abstractPaymentGateway = (EcommpayPaymentGateway) AbstractPaymentGateway.getGateway(accountId);
                }
                else if(PayonOppwaPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    abstractPaymentGateway = (PayonOppwaPaymentGateway) AbstractPaymentGateway.getGateway(accountId);
                }
                else if(SecureTradingGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    abstractPaymentGateway = (SecureTradingGateway) AbstractPaymentGateway.getGateway(accountId);
                }
                else if(PowerCash21PaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    abstractPaymentGateway = (PowerCash21PaymentGateway) PowerCash21PaymentGateway.getGateway(accountId);
                }
                else if(XceptsPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    abstractPaymentGateway = (XceptsPaymentGateway) AbstractPaymentGateway.getGateway(accountId);
                }
                else if(EasyPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    abstractPaymentGateway = (EasyPaymentGateway) AbstractPaymentGateway.getGateway(accountId);
                }
                else
                {
                    errorCodeListVO = restTransactionHelper.getErrorVO(ErrorName.SYS_RECURRINGALLOW);
                    PZExceptionHandler.raiseConstraintViolationException("RestTokenRecurring.java", "manualSingleCall", null, "common", ErrorMessages.SYS_RECURRINGALLOW, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }

                if("authfailed".equalsIgnoreCase(commonValidatorVO.getStatus()) && (EcommpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype) || SecureTradingGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype) || PayonOppwaPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype) || PowerCash21PaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype) || EasyPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype) || XceptsPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(fromtype)))
                {
                    errorCodeListVO = restTransactionHelper.getErrorVO(ErrorName.SYS_NO_RECORD_FOUND);
                    PZExceptionHandler.raiseConstraintViolationException("RestTokenRecurring.java", "manualSingleCall", null, "common", ErrorMessages.SYS_RECURRINGALLOW, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }

                commonValidatorVO.getTransDetailsVO().setOrderId(trackingId);
                commonValidatorVO.getTransDetailsVO().setAmount(amount);
                commonValidatorVO.getTransDetailsVO().setHeader(header);
                commonValidatorVO.getTransDetailsVO().setEmiCount(emiCount);

                String firstSix = "";
                if(!commonValidatorVO.getCardDetailsVO().getCardNum().equals(""))
                {
                    String card = PzEncryptor.decryptPAN(commonValidatorVO.getCardDetailsVO().getCardNum());
                    firstSix = functions.getFirstSix(card);
                }
                BinResponseVO binResponseVO = new BinResponseVO();
                binResponseVO = functions.getBinDetails(firstSix);
                commonValidatorVO.getCardDetailsVO().setBin_card_type(binResponseVO.getCardtype());
                commonValidatorVO.getCardDetailsVO().setBin_card_category(binResponseVO.getCardcategory());
                commonValidatorVO.getCardDetailsVO().setBin_brand(binResponseVO.getBrand());
                commonValidatorVO.getCardDetailsVO().setBin_usage_type(binResponseVO.getUsagetype());
                commonValidatorVO.getCardDetailsVO().setBin_sub_brand(binResponseVO.getSubbrand());
                commonValidatorVO.getCardDetailsVO().setCountry_code_A3(binResponseVO.getCountrycodeA3());
                commonValidatorVO.getCardDetailsVO().setCountry_code_A2(binResponseVO.getCountrycodeA2());
                commonValidatorVO.getCardDetailsVO().setTrans_type(binResponseVO.getTranstype());

                int newTrackingId = manualRecurringManager.insertAuthStartedForCommon(commonValidatorVO, "authstarted");
                entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, entry.STATUS_AUTHORISTION_STARTED, entry.STATUS_AUTHORISTION_STARTED, responseVO, auditTrailVo, merchantIp);

                cardNum = commonValidatorVO.getCardDetailsVO().getCardNum();
                expDate = commonValidatorVO.getCardDetailsVO().getExpMonth();
                name = commonValidatorVO.getAddressDetailsVO().getFirstname();
                lastName = commonValidatorVO.getAddressDetailsVO().getLastname();
                street = commonValidatorVO.getAddressDetailsVO().getStreet();
                state = commonValidatorVO.getAddressDetailsVO().getState();
                zip = commonValidatorVO.getAddressDetailsVO().getZipCode();
                country = commonValidatorVO.getAddressDetailsVO().getCountry();
                city = commonValidatorVO.getAddressDetailsVO().getCity();
                ip = commonValidatorVO.getAddressDetailsVO().getIp();
                email = commonValidatorVO.getAddressDetailsVO().getEmail();
                currency = commonValidatorVO.getTransDetailsVO().getCurrency();
                recurringBillingVO.setRbid(commonValidatorVO.getRecurringBillingVO().getRbid());
                commTransactionDetailsVO.setAmount(amount);
                commTransactionDetailsVO.setCurrency(currency);
                commTransactionDetailsVO.setOrderId(trackingId);
                commTransactionDetailsVO.setPreviousTransactionId(trackingId);
                if(functions.isValueNull(commonValidatorVO.getTransDetailsVO().getPaymentid()))
                {
                    commTransactionDetailsVO.setPaymentid(commonValidatorVO.getTransDetailsVO().getPaymentid());
                }
                commTransactionDetailsVO.setCvv(cvv);
                commMerchantVO.setMerchantUsername(username);
                commMerchantVO.setPassword(password);
                commCardDetailsVO.setCardNum(cardNum);
                commCardDetailsVO.setExpMonth(expDate);
                commAddressDetailsVO.setFirstname(name);
                commAddressDetailsVO.setLastname(lastName);
                commAddressDetailsVO.setStreet(street);
                commAddressDetailsVO.setState(state);
                commAddressDetailsVO.setZipCode(zip);
                commAddressDetailsVO.setCountry(country);
                commAddressDetailsVO.setIp(ip);
                commAddressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                commAddressDetailsVO.setEmail(email);
                commAddressDetailsVO.setCity(city);

                commRequestVO.setCommMerchantVO(commMerchantVO);
                commRequestVO.setRecurringBillingVO(recurringBillingVO);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setCardDetailsVO(commCardDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);

                Date date3 = new Date();
                transactionLogger.debug("DirectTransaction.common.processRebilling start #########"+date3.getTime());
                responseVO = (CommResponseVO) abstractPaymentGateway.processRebilling(String.valueOf(newTrackingId), commRequestVO);
                transactionLogger.debug("DirectTransaction.common.processRebilling end #########"+new Date().getTime());
                transactionLogger.debug("DirectTransaction.common.processRebilling diff #########"+(new Date().getTime()-date3.getTime()));
                String paymentid = responseVO.getTransactionId();
                String remark = responseVO.getRemark();
                String dateTime = responseVO.getResponseTime();
                String status = responseVO.getStatus();

                if (responseVO.getStatus().equalsIgnoreCase("success"))
                {
                    billingDescriptor = responseVO.getDescriptor();
                    transactionLogger.debug("DirectTransaction.common.processRebilling diff #########" +transaction_mode);
                    transactionUtilsDAO.updateTransactionAfterResponse("transaction_common","capturesuccess", amount,ip,"",paymentid,remark,dateTime,String.valueOf(newTrackingId),responseVO.getRrn(),responseVO.getArn(),responseVO.getAuthCode(),transaction_mode);
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, entry.STATUS_CAPTURE_SUCCESSFUL, entry.STATUS_CAPTURE_SUCCESSFUL, responseVO, auditTrailVo, null);
                    recurringBillingVO = recurringDAO.getRecurringSubscriptionDetails(trackingId);

                    recurringBillingVO.setRecurring_subscrition_id(recurringBillingVO.getRecurring_subscrition_id());
                    recurringBillingVO.setParentBankTransactionID("");//response idi.e messageid
                    recurringBillingVO.setBankRecurringBillingID(commonValidatorVO.getRecurringBillingVO().getRbid());//197
                    recurringBillingVO.setNewBankTransactionID(responseVO.getTransactionId());//R6
                    recurringBillingVO.setParentPzTransactionID(trackingId);//R5
                    recurringBillingVO.setAmount(amount);
                    recurringBillingVO.setDescription(trackingId);//R3
                    recurringBillingVO.setNewPzTransactionID(String.valueOf(newTrackingId));//newTrackingid
                    recurringBillingVO.setRecurringRunDate(String.valueOf(responseVO.getResponseTime()));
                    recurringBillingVO.setTransactionStatus(status);

                    recurringManager.insertRecurringDetailsEntry(recurringBillingVO);
                    commonValidatorVO.setErrorMsg(responseVO.getRemark());
                    respStatus = "capturesuccess";
                }
                else
                {
                    transactionUtilsDAO.updateTransactionAfterResponse("transaction_common","authfailed", amount,ip,"",paymentid,remark,dateTime,String.valueOf(newTrackingId),responseVO.getRrn(),responseVO.getArn(),responseVO.getAuthCode(),transaction_mode);
                    entry.actionEntryForCommon(String.valueOf(newTrackingId), amount, entry.STATUS_AUTHORISTION_FAILED, entry.STATUS_AUTHORISTION_FAILED, responseVO, auditTrailVo, null);
                    commonValidatorVO.setErrorMsg(responseVO.getRemark());
                    respStatus = "authfailed";
                }
                transactionLogger.error("Notificatin Sending to---" + notificationUrl + "---" + String.valueOf(newTrackingId));
                MerchantDAO merchantDAO = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(toId);
                if (functions.isValueNull(notificationUrl))
                {
                    TransactionManager transactionManager = new TransactionManager();
                    transactionLogger.error("inside sending notification---" + notificationUrl);

                    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    TransactionDetailsVO updatedTransactionDetailsVO = transactionManager.getTransDetailFromCommon(String.valueOf(newTrackingId));
                    updatedTransactionDetailsVO.setSecretKey(merchantDetailsVO.getKey());
                    updatedTransactionDetailsVO.setBillingDesc(responseVO.getDescriptor());
                    if (functions.isValueNull(updatedTransactionDetailsVO.getCcnum()))
                        updatedTransactionDetailsVO.setCcnum(PzEncryptor.decryptPAN(updatedTransactionDetailsVO.getCcnum()));
                    if (functions.isValueNull(updatedTransactionDetailsVO.getExpdate()))
                        updatedTransactionDetailsVO.setExpdate(PzEncryptor.decryptExpiryDate(updatedTransactionDetailsVO.getExpdate()));
                    asyncNotificationService.sendNotification(updatedTransactionDetailsVO, String.valueOf(newTrackingId), respStatus, responseVO.getDescription());
                }
                commonValidatorVO.setTrackingid(String.valueOf(newTrackingId));
                manualRebillResponseVO.setBillingDescriptor(responseVO.getDescriptor());
                manualRebillResponseVO.setDescription(responseVO.getDescription());
                manualRebillResponseVO.setErrorMessage(commonValidatorVO.getErrorMsg());
                manualRebillResponseVO.setTrackingId(commonValidatorVO.getTrackingid());
                manualRebillResponseVO.setStatus(status);
            }
        }
        catch (SystemError systemError)
        {
            log.error("System Error",systemError);
            transactionLogger.error("systemerror", systemError);
            commonValidatorVO.setErrorMsg("Internal Error Occured, Please contact Customer Support");
            return manualRebillResponseVO;
        }
        commonValidatorVO.getTransDetailsVO().setAmount(amount);
        commonValidatorVO.getTransDetailsVO().setBillingDiscriptor(billingDescriptor);
        commonValidatorVO.getTransDetailsVO().setOrderId(description);

        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
        asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), respStatus, null,null);

        return manualRebillResponseVO;
    }
}
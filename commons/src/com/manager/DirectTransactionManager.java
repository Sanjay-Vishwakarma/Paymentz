package com.manager;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.EcorePaymentGateway;
import com.directi.pg.core.paymentgateway.QwipiPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.manager.utils.TransactionUtil;
import com.manager.vo.BinResponseVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.RecurringBillingVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.checkers.PaymentChecker;
import com.payment.common.core.*;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.request.PZCancelRequest;
import com.payment.request.PZCaptureRequest;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZCancelResponse;
import com.payment.response.PZCaptureResponse;
import com.payment.response.PZRefundResponse;
import com.payment.response.PZResponseStatus;
import com.payment.statussync.StatusSyncDAO;
import com.payment.utils.TransactionUtilsDAO;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectCaptureValidatorVO;
import com.payment.validators.vo.DirectInquiryValidatorVO;
import com.payment.validators.vo.DirectRefundValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 5/28/15
 * Time: 8:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectTransactionManager
{
    static String defaultchargepercent = "500";
    static String INR_defaulttaxpercent = "1224";
    static String USD_defaulttaxpercent = "1224";
    private static Logger logger = new Logger(DirectTransactionManager.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DirectTransactionManager.class.getName());
    private Functions functions = new Functions();
    private TransactionUtil transactionUtil = new TransactionUtil();

    public DirectKitResponseVO processDirectTransaction(CommonValidatorVO commonValidatorVO) throws  PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        logger.debug("Enterred into DirectTransactionManager---");
        transactionLogger.debug("Enterred into DirectTransactionManager---");

        ActionEntry entry = new ActionEntry();
        Functions functions=new Functions();
        PaymentManager paymentManager=new PaymentManager();
        TransactionUtilsDAO transactionUtilsDAO=new TransactionUtilsDAO();
        SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
        AbstractPaymentGateway pg = null;
        Transaction transaction = new Transaction();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        AuditTrailVO auditTrailVO=new AuditTrailVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO= commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO= commonValidatorVO.getTransDetailsVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        int paymentType=0;
        int cardType=0;
        String toid=merchantDetailsVO.getMemberId();
        String totype=genericTransDetailsVO.getTotype();
        String error="";

        String respStatus=null;
        String respPaymentId=null;
        String mailtransactionStatus="Failed";
        String billingDiscriptor="";
        String respRemark="";
        String machineid="";
        String respDateTime=null;
        String header = genericTransDetailsVO.getHeader();
        String fStatus = "";
        String accountId = merchantDetailsVO.getAccountId();

        String fromtype = GatewayAccountService.getGatewayAccount(accountId).getGateway();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
        GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
        //TODO : remove icici
        String tableName = Database.getTableName(gatewayType.getGateway());

        //int trackingId= paymentManager.insertBegunTransactionEntry(commonValidatorVO,header);

        if (commonValidatorVO.getMerchantDetailsVO().getBinService().equalsIgnoreCase("Y"))
        {
            String firstSix = "";
            if (!commonValidatorVO.getCardDetailsVO().getCardNum().equals(""))
            {
                firstSix = functions.getFirstSix(commonValidatorVO.getCardDetailsVO().getCardNum());
            }

            BinResponseVO binResponseVO = new BinResponseVO();
            binResponseVO = functions.getBinDetails(firstSix,commonValidatorVO.getMerchantDetailsVO().getCountry());
            commonValidatorVO.getCardDetailsVO().setBin_card_type(binResponseVO.getCardtype());
            commonValidatorVO.getCardDetailsVO().setBin_card_category(binResponseVO.getCardcategory());
            commonValidatorVO.getCardDetailsVO().setBin_brand(binResponseVO.getBrand());
            commonValidatorVO.getCardDetailsVO().setBin_usage_type(binResponseVO.getUsagetype());
            commonValidatorVO.getCardDetailsVO().setBin_sub_brand(binResponseVO.getSubbrand());
            commonValidatorVO.getCardDetailsVO().setCountry_code_A3(binResponseVO.getCountrycodeA3());
            commonValidatorVO.getCardDetailsVO().setCountry_code_A2(binResponseVO.getCountrycodeA2());
            commonValidatorVO.getCardDetailsVO().setTrans_type(binResponseVO.getTranstype());
            commonValidatorVO.getCardDetailsVO().setCountryName(binResponseVO.getCountryname());
            commonValidatorVO.getCardDetailsVO().setIssuingBank(binResponseVO.getBank());
        }

        Date date1 = new Date();
        transactionLogger.debug("DirectTransactionManager.insertAuthStartedTransactionEntry start #######"+date1.getTime());
        //ToDO: Use switch case in action entry
        int trackingId = paymentManager.insertAuthStartedTransactionEntry(commonValidatorVO, auditTrailVO,null, tableName);
        transactionLogger.debug("DirectTransactionManager.insertAuthStartedTransactionEntry ends #######"+new Date().getTime());
        transactionLogger.debug("DirectTransactionManager.insertAuthStartedTransactionEntry diff #######"+(new Date().getTime()-date1.getTime()));

        commonValidatorVO.setTrackingid(String.valueOf(trackingId));

        auditTrailVO.setActionExecutorId(toid);
        auditTrailVO.setActionExecutorName("SOAP Process");
        try
        {
            Date date2 = new Date();
            transactionLogger.debug("DirectTransactionManager.getGateway start #######"+date2.getTime());
            pg =  AbstractPaymentGateway.getGateway(accountId);
            transactionLogger.debug("DirectTransactionManager.getGateway end #######"+new Date().getTime());
            transactionLogger.debug("DirectTransactionManager.getGateway diff #######"+(new Date().getTime()-date2.getTime()));

            //TODO High risk and fraude check add
            //Todo: correct checkAPIGateway method, use static list at class level
            if (Functions.checkAPIGateways(fromtype))
            {
                //tODo: Use switch inside createPaymentProcess method
                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()), Integer.parseInt(accountId));
                logger.debug("payment process instance::"+paymentProcess+" Payment gateway::"+pg);
                transactionLogger.debug("payment process instance::"+paymentProcess+" Payment gateway::"+pg);
                CommRequestVO commRequestVO = null;
                CommResponseVO transRespDetails = null;
                commRequestVO = getCommonRequestVO(commonValidatorVO);
                String status="";
                try
                {
                    if(merchantDetailsVO.getService().equalsIgnoreCase("N"))
                    {
                        transRespDetails = (CommResponseVO) pg.processAuthentication(String.valueOf(trackingId), commRequestVO);
                        status = "authsuccessful";
                    }
                    else
                    {
                        Date date3 = new Date();
                        transactionLogger.debug("DirectTransaction.common.processSale start #########"+date3.getTime());
                        transRespDetails = (CommResponseVO) pg.processSale(String.valueOf(trackingId), commRequestVO);
                        transactionLogger.debug("DirectTransaction.common.processSale end #########"+new Date().getTime());
                        transactionLogger.debug("DirectTransaction.common.processSale diff #########"+(new Date().getTime()-date3.getTime()));
                        status = "capturesuccess";
                    }
                }
                catch (PZTechnicalViolationException tve)
                {
                    logger.error("PZTechnicalViolationException in processTransaction---", tve);
                    transactionLogger.error("PZTechnicalViolationException in processTransaction---", tve);
                    String remarkEnum = tve.getPzTechnicalConstraint().getPzTechEnum().toString();
                    if (remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString()))
                    {
                        paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.FAILED.toString(), genericTransDetailsVO.getAmount(), "", null, PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString(), null, String.valueOf(trackingId));
                        int actionEntry = entry.actionEntryForCommon(String.valueOf(trackingId), genericTransDetailsVO.getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, transRespDetails, auditTrailVO, genericAddressDetailsVO.getIp());
                        respRemark = "Bank Connectivity Issue while processing your request";
                    }
                    else if (remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT.toString()))
                    {
                        paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.FAILED.toString(), genericTransDetailsVO.getAmount(), "", null, PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString(), null, String.valueOf(trackingId));
                        int actionEntry = entry.actionEntryForCommon(String.valueOf(trackingId), genericTransDetailsVO.getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, transRespDetails, auditTrailVO, genericAddressDetailsVO.getIp());
                        respRemark = "Your transaction is Timed out.Please check at bank side.";
                    }
                    else
                    {
                        paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.FAILED.toString(), genericTransDetailsVO.getAmount(), "", null, PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString(), null, String.valueOf(trackingId));
                        int actionEntry = entry.actionEntryForCommon(String.valueOf(trackingId), genericTransDetailsVO.getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, transRespDetails, auditTrailVO, genericAddressDetailsVO.getIp());
                        respRemark = "We have encountered an internal error while processing your request.";
                    }
                    transactionUtil.setSystemResponseAndErrorCodeListVO(directKitResponseVO, tve.getPzTechnicalConstraint().getErrorCodeListVO(), null, commonValidatorVO, PZResponseStatus.ERROR, respRemark, null);
                    PZExceptionHandler.handleTechicalCVEException(tve, toid, PZOperations.DIRECT_KIT_WEBSERVICE);
                    return directKitResponseVO;
                }
                logger.error("transRespDetails---"+transRespDetails.getStatus());
                transactionLogger.error("transRespDetails---"+transRespDetails.getStatus());
                if(transRespDetails!=null && functions.isValueNull(transRespDetails.getStatus()))
                {
                    logger.error("-----------status from gateway:::"+transRespDetails.getStatus());
                    transactionLogger.error("-----------status from gateway:::"+transRespDetails.getStatus());

                    if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                    {
                        if(status.equals("capturesuccess"))
                        {
                            respStatus="capturesuccess";
                            Date date4 = new Date();
                            transactionLogger.debug("DirectTransactionManager.actionEntry starts ############"+date4.getTime());
                            paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO);
                            transactionLogger.debug("DirectTransactionManager.actionEntry ends ############"+new Date().getTime());
                            transactionLogger.debug("DirectTransactionManager.actionEntry diff ############"+(new Date().getTime()-date4.getTime()));
                        }
                        else
                        {
                            respStatus="authsuccessful";
                            paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO);
                        }
                        billingDiscriptor = transRespDetails.getDescriptor();
                    }
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending3DConfirmation"))
                    {
                        //todo change error
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_INVALIDGATEWAY, ErrorMessages.INVALID_GATEWYAY));
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_INVALIDGATEWAY.toString(), ErrorType.SYSCHECK.toString());
                        PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processTransaction()", null, "TransactionServices", ErrorMessages.INVALID_GATEWYAY, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO,ErrorMessages.INVALID_GATEWYAY ,new Throwable(ErrorMessages.INVALID_GATEWYAY));
                    }
                    else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending"))
                    {
                        respStatus = "authstarted";
                        billingDiscriptor = pg.getDisplayName();
                    }
                    else
                    {
                        respStatus="authfailed";

                        Date date4 = new Date();
                        transactionLogger.debug("DirectTransactionManager.actionEntry starts ############"+date4.getTime());
                        paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, commRequestVO,auditTrailVO);
                        transactionLogger.debug("DirectTransactionManager.actionEntry ends ############"+new Date().getTime());
                        transactionLogger.debug("DirectTransactionManager.actionEntry diff ############"+(new Date().getTime()-date4.getTime()));
                    }
                    respDateTime = transRespDetails.getResponseTime();
                    respRemark = transRespDetails.getDescription();
                    respPaymentId =  transRespDetails.getTransactionId();

                    Date date5 = new Date();
                    transactionLogger.debug("DirectTransactionManager.updateTransactionAfterResponse starts ############"+date5.getTime());
                    transactionUtilsDAO.updateTransactionAfterResponse(tableName,respStatus,genericTransDetailsVO.getAmount(),genericAddressDetailsVO.getIp(),machineid,respPaymentId,respRemark,respDateTime,String.valueOf(trackingId),transRespDetails.getRrn(),transRespDetails.getArn(),transRespDetails.getAuthCode(),"Non-3D");
                    transactionLogger.debug("DirectTransactionManager.updateTransactionAfterResponse end ############"+new Date().getTime());
                    transactionLogger.debug("DirectTransactionManager.updateTransactionAfterResponse diff ############"+(new Date().getTime()-date5.getTime()));
                }

                //mailtransactionStatus = transactionUtilsDAO.sendTransactionMail(respStatus,String.valueOf(trackingId),respRemark);

            }

            else if(QwipiPaymentGateway.GATEWAY_TYPE.equals(fromtype))
            {
                QwipiResponseVO transRespDetails = null;
                QwipiRequestVO requestDetail = null;
                try
                {
                    requestDetail = getQRequestVO(commonValidatorVO);
                    transRespDetails = (QwipiResponseVO) pg.processSale(String.valueOf(trackingId), requestDetail);
                }
                catch (PZTechnicalViolationException tve)
                {
                    logger.error("PZTechnicalViolationException in processTransaction---", tve);
                    transactionLogger.error("PZTechnicalViolationException in processTransaction---", tve);
                    String remarkEnum = tve.getPzTechnicalConstraint().getPzTechEnum().toString();
                    if (remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString()))
                    {
                        transaction.updateTransactionStatusForQwipi("failed", String.valueOf(trackingId), PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString());
                        int actionEntry = entry.actionEntryForQwipi(String.valueOf(trackingId), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, genericAddressDetailsVO.getIp(), transRespDetails, auditTrailVO);
                        respRemark = "Bank Connectivity Issue while processing your request";
                    }
                    else if (remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT.toString()))
                    {
                        transaction.updateTransactionStatusForQwipi("authstarted", String.valueOf(trackingId), PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT.toString());
                        int actionEntry = entry.actionEntryForQwipi(String.valueOf(trackingId), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, genericAddressDetailsVO.getIp(), transRespDetails, auditTrailVO);
                        respRemark = "Your transaction is Timed out.Please check at bank side.";
                    }
                    else if(remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString()))
                    {
                        transaction.updateTransactionStatusForQwipi("failed", String.valueOf(trackingId), PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString());
                        int actionEntry = entry.actionEntryForQwipi(String.valueOf(trackingId), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, genericAddressDetailsVO.getIp(), transRespDetails, auditTrailVO);
                        respRemark = "We have encountered an internal error while processing your request.";
                    }
                    else if(remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION.toString()))
                    {
                        transaction.updateTransactionStatusForQwipi("failed", String.valueOf(trackingId), PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION.toString());
                        int actionEntry = entry.actionEntryForQwipi(String.valueOf(trackingId), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, genericAddressDetailsVO.getIp(), transRespDetails, auditTrailVO);
                        respRemark = "We have encountered an internal error while processing your request.";
                    }
                    else if(remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION.toString()))
                    {
                        transaction.updateTransactionStatusForQwipi("failed", String.valueOf(trackingId), PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION.toString());
                        int actionEntry = entry.actionEntryForQwipi(String.valueOf(trackingId), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, genericAddressDetailsVO.getIp(), transRespDetails, auditTrailVO);
                        respRemark = "We have encountered an internal error while processing your request.";
                    }

                    transactionUtil.setSystemResponseAndErrorCodeListVO(directKitResponseVO, tve.getPzTechnicalConstraint().getErrorCodeListVO(), null, commonValidatorVO, PZResponseStatus.ERROR, respRemark, null);
                    PZExceptionHandler.handleTechicalCVEException(tve, toid, PZOperations.DIRECT_KIT_WEBSERVICE);
                    return directKitResponseVO;
                }

                if(transRespDetails!=null)
                {
                    respDateTime=transRespDetails.getDateTime();
                    respRemark = transRespDetails.getRemark();
                    respPaymentId= transRespDetails.getPaymentOrderNo();

                    if ((transRespDetails.getResultCode().trim()).equals("0"))
                    {
                        respStatus = "capturesuccess";
                        paymentManager.updateCaptureSuccessTransactionForQwipiEcoreCommon(commonValidatorVO, String.valueOf(trackingId), auditTrailVO, tableName, machineid, respPaymentId, respDateTime, respRemark);
                        if(respRemark!=null && !respRemark.equals(""))
                        {
                            mailtransactionStatus = "Successful (" + respRemark + ")";
                        }
                        else
                        {
                            mailtransactionStatus = "Successful";
                        }
                    }
                    else
                    {
                        respStatus = "authfailed";
                        paymentManager.updateAuthFailedTransactionForQwipiEcoreCommon(commonValidatorVO, String.valueOf(trackingId), auditTrailVO, tableName, machineid, respPaymentId, respDateTime, respRemark);
                        if(respRemark!=null && !respRemark.equals(""))
                        {
                            mailtransactionStatus = "Failed (" + respRemark + ")";
                        }
                        else
                        {
                            mailtransactionStatus = "Failed";
                        }
                    }

                    if(!(transRespDetails.getBillingDescriptor().equals("")))
                    {
                        billingDiscriptor=transRespDetails.getBillingDescriptor();
                    }
                    else
                    {
                        billingDiscriptor = " ";
                    }

                    transactionUtilsDAO.updateTransactionAfterResponse(tableName,respStatus,genericTransDetailsVO.getAmount(),genericAddressDetailsVO.getIp(),machineid,respPaymentId,respRemark,respDateTime,String.valueOf(trackingId),"","","","Non-3D");
                }

                String statusMsg=getTransactionstatus(respStatus,respRemark);
                String display = "";
                transactionLogger.debug("statusMsg:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" + statusMsg);

                if(statusMsg.contains("Failed") || statusMsg.contains("Declined") || statusMsg.contains("fail")){
                    display ="style=\"display:none;\"";
                }
                HashMap mailData=new HashMap();
                mailData.put(MailPlaceHolder.TOID,toid);
                mailData.put(MailPlaceHolder.CustomerEmail,commonValidatorVO.getAddressDetailsVO().getEmail());
                mailData.put(MailPlaceHolder.TRACKINGID, String.valueOf(trackingId));
                mailData.put(MailPlaceHolder.IPADDRESS, commonValidatorVO.getAddressDetailsVO().getIp());
                mailData.put(MailPlaceHolder.ORDERDESCRIPTION,commonValidatorVO.getTransDetailsVO().getOrderDesc());
                mailData.put(MailPlaceHolder.DESC,commonValidatorVO.getTransDetailsVO().getOrderId());
                mailData.put(MailPlaceHolder.AMOUNT,commonValidatorVO.getTransDetailsVO().getAmount());
                mailData.put(MailPlaceHolder.CURRENCY,commonValidatorVO.getTransDetailsVO().getCurrency());
                mailData.put(MailPlaceHolder.DISPLAYNAME,transRespDetails.getBillingDescriptor());
                mailData.put(MailPlaceHolder.STATUS,statusMsg);
                mailData.put(MailPlaceHolder.NAME,commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+commonValidatorVO.getAddressDetailsVO().getLastname());
                mailData.put(MailPlaceHolder.DISPLAYTR, display);
                //MailService mailService = new MailService();
                AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
                asynchronousMailService.sendMerchantSignup(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION,mailData);
            }

            if("capturesuccess".equalsIgnoreCase(respStatus) || "authsuccessful".equalsIgnoreCase(respStatus))
            {
                fStatus = "Y";
            }
            else
            {
                fStatus = "N";
            }
        }

        catch (SystemError se)
        {
            logger.error("SystemError in DirectTransactionManager---", se);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException(DirectTransactionManager.class.getName(),"processDirectTransaction()",null,"common","Technical Exception",PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,se.getMessage(),se.getCause(),toid,"Transaction Web Service");
            transactionUtilsDAO.insertFailedTransaction(String.valueOf(trackingId), se.getMessage(), tableName, commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            transactionUtil.setSystemResponseAndErrorCodeListVO(directKitResponseVO,errorCodeListVO,null,commonValidatorVO,PZResponseStatus.FAILED,respRemark,billingDiscriptor);
        }
        transactionUtil.setSystemResponseAndErrorCodeListVO(directKitResponseVO,errorCodeListVO,null,commonValidatorVO,"Y".equalsIgnoreCase(fStatus)?PZResponseStatus.SUCCESS:PZResponseStatus.FAILED,respRemark,billingDiscriptor);
        return directKitResponseVO;
    }

    public DirectKitResponseVO processRefund(DirectRefundValidatorVO directRefundValidatorVO) throws PZDBViolationException, PZConstraintViolationException, PZTechnicalViolationException
    {
        //Supportive Class instance
        Transaction transaction = new Transaction();
        PaymentChecker paymentChecker = new PaymentChecker();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        TransactionEntry transactionEntry = new TransactionEntry();
        ActionEntry entry = new ActionEntry();
        SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
        DirectKitResponseVO directKitResponseVO=new DirectKitResponseVO();

        String stat = "";
        String statMessage = "";
        String trackingId = directRefundValidatorVO.getTrackingid();
        String refundAmt = directRefundValidatorVO.getRefundAmount();
        String refReason = directRefundValidatorVO.getRefundReason();
        String accountId = "";
        String requestedId = "";

        if("Y".equalsIgnoreCase(directRefundValidatorVO.getFlightMode()))
        {
            requestedId = directRefundValidatorVO.getParetnerId();
        }
        else
        {
            requestedId = directRefundValidatorVO.getMerchantDetailsVO().getMemberId();
        }

        Connection connection=null;
        try
        {
            connection=Database.getConnection();
            Hashtable commHash = transaction.getCaptureTransactionCommon(trackingId, requestedId,directRefundValidatorVO.getFlightMode());
            if (!commHash.isEmpty())
            {
                accountId = (String) commHash.get("accountid");

                String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();

                if (currency.equals("JPY"))
                {
                    if (!paymentChecker.isAmountValidForJPY(currency, refundAmt))
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TRANS_AMOUNT, ErrorMessages.JPY_CURRENCY));
                        PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processRefund()", null, "Common", ErrorMessages.JPY_CURRENCY, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.JPY_CURRENCY, new Throwable(ErrorMessages.JPY_CURRENCY));
                    }
                }
                else
                {
                    if (!Functions.checkAccuracy(refundAmt, 2))
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_AMOUNT, ErrorMessages.INVALID_AMOUNT));
                        PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processRefund()", null, "Common", ErrorMessages.INVALID_AMOUNT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_AMOUNT, new Throwable(ErrorMessages.INVALID_AMOUNT));
                    }
                }

                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountId));

                PZRefundRequest refundRequest = new PZRefundRequest();
                refundRequest.setAccountId(Integer.valueOf(accountId));
                refundRequest.setTrackingId(Integer.valueOf(trackingId));
                //refundRequest.setMemberId(Integer.valueOf(directRefundValidatorVO.getMerchantDetailsVO().getMemberId()));
                refundRequest.setMemberId(Integer.valueOf((String)commHash.get("toid")));
                refundRequest.setRefundAmount(refundAmt);
                refundRequest.setRefundReason(refReason);
                refundRequest.setIpAddress(directRefundValidatorVO.getMerchantIpAddress());
                //AuditTrailVO auditTrailVO1 = refundRequest.getAuditTrailVO();
                auditTrailVO.setActionExecutorId((String)commHash.get("toid"));
                auditTrailVO.setActionExecutorName("REST Reverse");
                refundRequest.setAuditTrailVO(auditTrailVO);

                PZRefundResponse refundResponse = paymentProcess.refund(refundRequest);
                PZResponseStatus responseStatus = refundResponse.getStatus();
                String refundDescription = refundResponse.getResponseDesceiption();


                if (PZResponseStatus.SUCCESS.equals(responseStatus))
                {
                    stat = "Y";
                    statusSyncDAO.updateAllRefundTransactionFlowFlag(trackingId, "reversed", connection);
                }
                else
                {
                    stat = "N";
                }
                statMessage = refundDescription;
            }
            else
            {
                QwipiRequestVO RequestDetail = null;
                QwipiResponseVO transRespDetails = null;
                QwipiTransDetailsVO TransDetail = new QwipiTransDetailsVO();
                GenericCardDetailsVO cardDetail = new GenericCardDetailsVO();
                QwipiAddressDetailsVO AddressDetail = new QwipiAddressDetailsVO();

                Hashtable transDetails = transaction.getCaptureTransactionQwipi(trackingId, directRefundValidatorVO.getMerchantDetailsVO().getMemberId());
                if (!transDetails.isEmpty())
                {
                    if (!Functions.checkAccuracy(refundAmt, 2))
                    {
                        errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_AMOUNT, ErrorMessages.INVALID_AMOUNT));
                        PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processRefund()", null, "Common", ErrorMessages.INVALID_AMOUNT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_AMOUNT, new Throwable(ErrorMessages.INVALID_AMOUNT));
                    }

                    String trackingid = (String) transDetails.get("trackingid");
                    String icicitransid = trackingid;
                    String transid = (String) transDetails.get("transid");
                    accountId = (String) transDetails.get("accountid");
                    String fixamt = (String) transDetails.get("fixamount");
                    String qwipi_paymentordernumber = (String) transDetails.get("qwipiPaymentOrderNumber");
                    String rsdescription = (String) transDetails.get("description");
                    String captureamount = (String) transDetails.get("amount");
                    String transactionDate = (String) transDetails.get("dtstamp");
                    String icicimerchantid = (String) transDetails.get("fromid");
                    String toid = (String) transDetails.get("toid");
                    String cardholdername = (String) transDetails.get("name");
                    //logger.debug("---"+(String)transDetails.get("chargeper")+"---"+(String)transDetails.get("T.taxper")+"---"+(String)transDetails.get("M.taxper"));
                    int chargeper = Integer.parseInt(transDetails.get("chargeper") + "");
                    int transactiontaxper = Integer.parseInt(transDetails.get("T.taxper") + "");
                    int currtaxper = 0;
                    if ((transDetails.get("M.taxper") != null))
                    {
                        currtaxper = Integer.parseInt(transDetails.get("M.taxper") + "");
                    }
                    GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                    String reversalCharges = (String) transDetails.get("M.reversalcharge");
                    String icicimerchanttype = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                    String transstatus = (String) transDetails.get("status");
                    BigDecimal amt = new BigDecimal(captureamount);
                    BigDecimal charge = new BigDecimal(chargeper);
                    String transtexper = String.valueOf(transactiontaxper);
                    String currenttex = String.valueOf(currtaxper);

                    int year = 0;
                    if (transactionDate != null)
                    {
                        long timeInSecs = Long.parseLong(transactionDate);
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(timeInSecs * 1000);
                        year = cal.get(Calendar.YEAR);
                    }

                    auditTrailVO.setActionExecutorId(directRefundValidatorVO.getMerchantDetailsVO().getMemberId());
                    auditTrailVO.setActionExecutorName("REST Reverse DK");
                    transactionEntry.newGenericRefundTransaction(icicitransid, new BigDecimal(refundAmt), accountId, null, transRespDetails, auditTrailVO);
                    transactionEntry.closeConnection();
                    TransDetail.setOperation("02");
                    TransDetail.setPaymentOrderNo(qwipi_paymentordernumber);
                    TransDetail.setBillNo(rsdescription);
                    TransDetail.setAmount(captureamount);
                    TransDetail.setRefundAmount(refundAmt);
                    Hashtable qwipiKsnFlag = transaction.getMidKeyForQwipi(accountId);
                    String md5Info = null;
                    md5Info = Functions.convertmd5(qwipi_paymentordernumber + rsdescription + captureamount + refundAmt + qwipiKsnFlag.get("midkey"));
                    AddressDetail.setMd5info(md5Info);
                    //TransDetail.setReturnURL();
                    //Now Reverse transaction on the gateway

                    logger.debug("callng processRefund");
                    transactionLogger.debug("callng processRefund");
                    AbstractPaymentGateway paymentGateway = AbstractPaymentGateway.getGateway(accountId);
                    RequestDetail = new QwipiRequestVO(cardDetail, AddressDetail, TransDetail);
                    RequestDetail.setKsnUrlFlag((String) qwipiKsnFlag.get("isksnurlflag"));
                    transRespDetails = (QwipiResponseVO) paymentGateway.processRefund(icicitransid, RequestDetail);
                    logger.debug("called processRefund" + transRespDetails.getBillNo() + transRespDetails.getPaymentOrderNo() +
                            transRespDetails.getResultCode());
                    transactionLogger.debug("called processRefund" + transRespDetails.getBillNo() + transRespDetails.getPaymentOrderNo() +
                            transRespDetails.getResultCode());

                    if (transRespDetails != null && (transRespDetails.getResultCode()).equals("100"))
                    {
                        Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                        StringBuffer sb = new StringBuffer();
                        sb.append("update transaction_qwipi set status='reversed'");

                        sb.append(",refundinfo='" + refReason + "'");

                        sb.append(",refundamount='" + ESAPI.encoder().encodeForSQL(MY, transRespDetails.getRefundAmount()) + "'");
                        sb.append(",refundcode='" + ESAPI.encoder().encodeForSQL(MY, transRespDetails.getResultCode()) + "'");

                        sb.append(" where trackingid=" + ESAPI.encoder().encodeForSQL(MY, icicitransid) + " and status='markedforreversal'");

                        int rows = transaction.executeUpdate(sb.toString());
                        logger.debug("No of Rows updated : " + rows + "<br>");
                        transactionLogger.debug("No of Rows updated : " + rows + "<br>");

                        if (rows == 1)
                        {
                            statusSyncDAO.updateAllRefundTransactionFlowFlag(trackingId, "reversed", connection);
                            //boolean refunded = true;
                            // preparing collections of refunds as per merchant
                            Hashtable refundDetails = new Hashtable();
                            refundDetails.put("icicitransid", icicitransid);
                            refundDetails.put("captureamount", captureamount);
                            refundDetails.put("refundamount", transRespDetails.getRefundAmount());
                            refundDetails.put("description", rsdescription);
                            refundDetails.put("accountid", accountId);
                            refundDetails.put("cardholdername", cardholdername);


                            int actionEntry = entry.actionEntryForQwipi(icicitransid, transRespDetails.getRefundAmount(), ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL, directRefundValidatorVO.getMerchantIpAddress(), transRespDetails, auditTrailVO);


                            stat = "Y";
                            statMessage = "Transaction has been successfully reversed";
                        }
                    }
                    else
                    {
                        stat = "N";
                        statMessage = "Transaction cannot be Reversed.";
                    }
                }
                else
                {
                    Hashtable ecoreTrans = transaction.getCaptureTransactionEcore(trackingId, directRefundValidatorVO.getMerchantDetailsVO().getMemberId());

                    if (!ecoreTrans.isEmpty())
                    {
                        String captureamount = (String) ecoreTrans.get("amount");
                        String transactionDate = (String) ecoreTrans.get("dtstamp");
                        accountId = (String) ecoreTrans.get("accountid");
                        String txStatus = (String) ecoreTrans.get("status");
                        BigDecimal amt = new BigDecimal(captureamount);


                        EcoreResponseVO ecoreTransRespDetails = new EcoreResponseVO();

                        if (txStatus.equals("capturesuccess"))
                        {
                            transactionEntry.newGenericRefundTransaction(trackingId, amt, accountId, directRefundValidatorVO.getRefundAmount(), ecoreTransRespDetails, auditTrailVO);
                            transactionEntry.closeConnection();
                        }

                        //Now Reverse transaction on the gateway
                        logger.debug("calling processRefund");
                        transactionLogger.debug("calling processRefund");

                        Hashtable ecoreHash = transaction.getMidKeyForEcore(accountId);
                        String mid = null;
                        String midKey = null;
                        if (!ecoreHash.isEmpty())
                        {
                            mid = (String) ecoreHash.get("mid");
                            midKey = (String) ecoreHash.get("midkey");
                        }

                        String transId = "";
                        if (transId != null)
                        {
                            transId = transaction.getEcorePaymentOrderNumber(trackingId);
                        }
                        EcorePaymentGateway paymentGateway = (EcorePaymentGateway) AbstractPaymentGateway.getGateway(accountId);
                        ecoreTransRespDetails = (EcoreResponseVO) paymentGateway.processRefund(mid, midKey, transId);

                        //Now Reverse transaction on the gateway

                        if (ecoreTransRespDetails != null && (ecoreTransRespDetails.getResponseCode()).equals("100"))
                        {
                            Codec MY = new MySQLCodec(MySQLCodec.Mode.STANDARD);

                            StringBuffer sb = new StringBuffer();
                            sb.append("update transaction_ecore set status='reversed', ecorePaymentOrderNumber ='" + ecoreTransRespDetails.getTransactionID());

                            sb.append("',refundinfo='" + ESAPI.encoder().encodeForSQL(MY, directRefundValidatorVO.getRefundAmount()) + "'");

                            //sb.append(",refundamount='"+ESAPI.encoder().encodeForSQL(MY,ecoreTransRespDetails.getRefundAmount())+"'");
                            sb.append(",refundcode='" + ESAPI.encoder().encodeForSQL(MY, ecoreTransRespDetails.getResponseCode()) + "'");

                            sb.append(" where trackingid=" + ESAPI.encoder().encodeForSQL(MY, trackingId) + " and status='markedforreversal'");

                            int rows = transaction.executeUpdate(sb.toString());
                            logger.debug("No of Rows updated : " + rows + "<br>");
                            transactionLogger.debug("No of Rows updated : " + rows + "<br>");

                            if (rows == 1)
                            {
                                // Start : Added for Action and Status Entry in Action History table
                                statusSyncDAO.updateAllRefundTransactionFlowFlag(trackingId, "reversed", connection);
                                int actionEntry = entry.actionEntryForEcore(trackingId, captureamount, ActionEntry.ACTION_REVERSAL_SUCCESSFUL, ActionEntry.STATUS_REVERSAL_SUCCESSFUL, directRefundValidatorVO.getMerchantIpAddress(), ecoreTransRespDetails, auditTrailVO);
                                stat = "Y";
                                statMessage = "Transaction has been successfully reversed";
                            }
                        }
                        else
                        {
                            stat = "N";
                            statMessage = "Transaction cannot be Reversed.";
                        }
                    }
                    else
                    {
                        stat = "N";
                        statMessage = "Transaction not found";
                    }
                }
            }

            //sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.REFUND_TRANSACTION, trackingId, statMessage, refReason);
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DirectTransactionManager.class.getName(), "processRefund()", null, "Common", "Internal Error : Please contact Support", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(DirectTransactionManager.class.getName(), "processRefund()", null, "Common", "System error while refunding transaction", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DirectTransactionManager.class.getName(), "processRefund()", null, "Common", "Internal Error : Please contact Support", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(connection);
        }
        transactionUtil.setRefundSystemResponseAndErrorCodeListVO(directKitResponseVO,errorCodeListVO,null,directRefundValidatorVO,"Y".equals(stat)?PZResponseStatus.SUCCESS:PZResponseStatus.FAILED,statMessage);
        return directKitResponseVO;
    }

    /*public List<DirectKitResponseVO> processInquiryWS(DirectInquiryValidatorVO directInquiryValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        //DirectKitResponseVO directKitResponseVO = null;
        ErrorCodeListVO errorCodeListVO = null;
        Transaction transaction = new Transaction();

        List<TransactionVO> listTransactionVO = new ArrayList<TransactionVO>();
        List<DirectKitResponseVO> listDirectKitResponseVO = new ArrayList<DirectKitResponseVO>();

        listTransactionVO = transaction.getTransactionDetailsList(directInquiryValidatorVO,"transaction_qwipi");

        if(listTransactionVO.size()>0)
        {
            transactionUtil.setInquirySystemResponseAndErrorCodeListVOforSplit(listDirectKitResponseVO,directInquiryValidatorVO, listTransactionVO, "Your record is found",PZResponseStatus.SUCCESS.name());
        }
        else
        {
            listTransactionVO = transaction.getTransactionDetailsList(directInquiryValidatorVO,"transaction_common");
            if(listTransactionVO.size()>0)
            {
                transactionUtil.setInquirySystemResponseAndErrorCodeListVOforSplit(listDirectKitResponseVO,directInquiryValidatorVO, listTransactionVO, "Your record is found",PZResponseStatus.SUCCESS.name());
            }
            else
            {


                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_INVALIDGATEWAY, ErrorMessages.INVALID_GATEWYAY));
                //PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processInquiry()", null, "Common", ErrorMessages.INVALID_GATEWYAY, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_GATEWYAY, new Throwable(ErrorMessages.INVALID_GATEWYAY));
                transactionUtil.setInquirySystemResponseAndErrorCodeListVO(directKitResponseVO, errorCodeListVO, null, directInquiryValidatorVO, PZResponseStatus.FAILED.name(), "Your record not found");
                //return listDirectKitResponseVO;
            }
        }

        return listDirectKitResponseVO;
    }*/

    public DirectKitResponseVO processInquiry(DirectInquiryValidatorVO directInquiryValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        Hashtable data = new Hashtable();
        Transaction transaction = new Transaction();
        String accountId=null;
        AbstractPaymentGateway pg = null;
        String fromtype=null;

        try
        {
            data = transaction.getQwipiTransDetails(directInquiryValidatorVO.getMerchantDetailsVO().getMemberId(), directInquiryValidatorVO.getTrackingId(), directInquiryValidatorVO.getDescription());
            if (null != data.get("accountid"))
                accountId = (String) data.get("accountid");

            else
            {
                data = transaction.getTransactionDetailsForCommon(directInquiryValidatorVO.getTrackingId(), directInquiryValidatorVO.getDescription());
                if (null != data.get("accountid"))
                {
                    accountId = (String) data.get("accountid");
                }
                else
                {
                    directKitResponseVO = transaction.getTransactionForRejected(directInquiryValidatorVO.getMerchantDetailsVO().getMemberId(), directInquiryValidatorVO.getDescription());
                    transactionUtil.setInquirySystemResponseAndErrorCodeListVO(directKitResponseVO, errorCodeListVO, null, directInquiryValidatorVO, directKitResponseVO.getStatus(),null, directKitResponseVO.getRemark());

                }


            }
            //accountId = (String) data.get("accountid");


            /*if (accountId == null)
            {
                System.out.println("inside if---");
                transactionUtil.setInquirySystemResponseAndErrorCodeListVO(directKitResponseVO, errorCodeListVO, null, directInquiryValidatorVO, PZResponseStatus.FAILED.name(), "Your record not found");
                return directKitResponseVO;
            }*/
            /*data = transaction.getTransactionDetailsForCommon(trackingid);

            accountId = (String)data.get("accountid");*/

            if (accountId != null)
            {

                pg = AbstractPaymentGateway.getGateway(accountId);
                fromtype = GatewayAccountService.getGatewayAccount(accountId).getGateway();

                //select payment gateway for payment start

                if (QwipiPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                {
                    Hashtable dataHash = new Hashtable();

                    dataHash = transaction.getTransactionDetails(directInquiryValidatorVO.getTrackingId(), directInquiryValidatorVO.getDescription(), "transaction_qwipi");

                    /*if (!dataHash.equals("") && dataHash != null)
                    {
                        dataHash = transaction.getTransactionForRejected(directInquiryValidatorVO.getMerchantDetailsVO().getMemberId(), directInquiryValidatorVO.getDescription());
                    }*/

                    if (!dataHash.equals("") && dataHash != null)
                    {
                        transactionUtil.setInquirySystemResponseAndErrorCodeListVO(directKitResponseVO, errorCodeListVO, null, directInquiryValidatorVO, (String) dataHash.get("status"),"Your record is found", (String) dataHash.get("remark"));
                    }
                    else
                    {
                        transactionUtil.setInquirySystemResponseAndErrorCodeListVO(directKitResponseVO, errorCodeListVO, null, directInquiryValidatorVO, PZResponseStatus.FAILED.name(), "Your record not found", null);
                    }
                }

                else if (Functions.checkAPIGateways(fromtype))
                {
                    Hashtable dataHash = new Hashtable();
                    logger.debug("trackingid---" + directInquiryValidatorVO.getTrackingId());
                    logger.debug("description---" + directInquiryValidatorVO.getDescription());

                    dataHash = transaction.getTransactionDetails(directInquiryValidatorVO.getTrackingId(), directInquiryValidatorVO.getDescription(), "transaction_common");

                    /*if (dataHash == null)
                    {
                        dataHash = transaction.getTransactionForRejected(directInquiryValidatorVO.getMerchantDetailsVO().getMemberId(), directInquiryValidatorVO.getDescription());
                    }
                    System.out.println("datahash----" + dataHash);*/
                    if (dataHash != null)
                    {
                        directInquiryValidatorVO.setTrackingId(String.valueOf(dataHash.get("trackingid")));
                        transactionUtil.setInquirySystemResponseAndErrorCodeListVO(directKitResponseVO, errorCodeListVO, null, directInquiryValidatorVO, (String) dataHash.get("status"),"Your record is found", String.valueOf(dataHash.get("remark")));
                    }
                    else
                    {
                        transactionUtil.setInquirySystemResponseAndErrorCodeListVO(directKitResponseVO, errorCodeListVO, null, directInquiryValidatorVO, PZResponseStatus.FAILED.name(), "Your record not found", null);
                    }
                }
                else
                {
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_INVALIDGATEWAY, ErrorMessages.INVALID_GATEWYAY));
                    PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processInquiry()", null, "Common", ErrorMessages.INVALID_GATEWYAY, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_GATEWYAY, new Throwable(ErrorMessages.INVALID_GATEWYAY));
                }
            }
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processInquiry()", null, "Common", ErrorMessages.INVALID_GATEWYAY, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, systemError.getMessage(), systemError.getCause());
        }

        return directKitResponseVO;
    }

    public DirectKitResponseVO processCapture(DirectCaptureValidatorVO directCaptureValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        StringBuffer status = new StringBuffer("");
        StringBuffer statusMsg = new StringBuffer("");

        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();


        Transaction transaction = new Transaction();
        PaymentChecker paymentChecker = new PaymentChecker();

        //String memberid = directCaptureValidatorVO.getMerchantDetailsVO().getMemberId();
        String captureAmt = directCaptureValidatorVO.getCaptureAmount();
        String trackingId = directCaptureValidatorVO.getTrackingid();
        PZCaptureResponse captureResponse=null;
        String stat = "";
        String statMessage = "";

        String requestedId = "";

        if("Y".equalsIgnoreCase(directCaptureValidatorVO.getFlightMode()))
        {
            requestedId = directCaptureValidatorVO.getParetnerId();
        }
        else
        {
            requestedId = directCaptureValidatorVO.getMerchantDetailsVO().getMemberId();
        }

        Hashtable authTransactionHash = transaction.getAuthTransactionCommon(requestedId,trackingId,directCaptureValidatorVO.getFlightMode());

        if(!authTransactionHash.isEmpty())
        {
            String amount = (String) authTransactionHash.get("amount");
            String accountId = (String) authTransactionHash.get("accountid");
            String currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();

            if(currency.equals("JPY"))
            {
                if(!paymentChecker.isAmountValidForJPY(currency,captureAmt))
                {
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TRANS_AMOUNT, ErrorMessages.JPY_CURRENCY));
                    PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processRefund()", null, "Common", ErrorMessages.JPY_CURRENCY, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.JPY_CURRENCY, new Throwable(ErrorMessages.JPY_CURRENCY));
                }
            }
            else
            {
                if (!Functions.checkAccuracy(captureAmt, 2))
                {
                    errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_AMOUNT, ErrorMessages.INVALID_AMOUNT));
                    PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processRefund()", null, "Common", ErrorMessages.INVALID_AMOUNT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_AMOUNT, new Throwable(ErrorMessages.INVALID_AMOUNT));

                }
            }
            if (new BigDecimal(captureAmt).compareTo(new BigDecimal(amount)) <= 0)
            {
                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingId), Integer.parseInt(accountId));

                PZCaptureRequest captureRequest = new PZCaptureRequest();
                captureRequest.setAccountId(Integer.valueOf(accountId));
                captureRequest.setMemberId(Integer.valueOf((String) authTransactionHash.get("toid")));
                captureRequest.setTrackingId(Integer.valueOf(trackingId));
                captureRequest.setAmount(Double.valueOf(captureAmt));
                captureRequest.setIpAddress(directCaptureValidatorVO.getMerchantIpAddress());
                captureRequest.setPod("Capture Transaction");

                AuditTrailVO auditTrailVO=new AuditTrailVO();
                auditTrailVO.setActionExecutorId((String) authTransactionHash.get("toid"));
                auditTrailVO.setActionExecutorName("REST Capture");
                captureRequest.setAuditTrailVO(auditTrailVO);

                captureResponse = paymentProcess.capture(captureRequest);
                PZResponseStatus responseStatus = captureResponse.getStatus();
                String captureDescription = captureResponse.getResponseDesceiption();

                /*if(captureResponse.getBankStatus()!=null)
                {
                    bankStatus=captureResponse.getBankStatus();
                }
                if(captureResponse.getCaptureCode()!=null)
                {
                    captureCode=captureResponse.getCaptureCode();
                }
                if(captureResponse.getResultCode()!=null)
                {
                    resultCode=captureResponse.getResultCode();
                }
                if(captureResponse.getResultDescription()!=null)
                {
                    resultDescription=captureResponse.getResultDescription();
                }*/
                if (PZResponseStatus.SUCCESS.equals(responseStatus))
                {
                    stat = "Y";
                }
                else
                {
                    stat = "N";
                }
                statMessage = captureDescription;
            }
            else
            {
                stat = "N";
                statMessage = "Capture Amount should be less than Authorized amount";
            }
        }
        else
        {
            stat = "N";
            statMessage = "Transaction not found";
        }
        transactionUtil.setCaptureSystemResponseAndErrorCodeListVO(directKitResponseVO, errorCodeListVO, null, directCaptureValidatorVO, stat, statMessage);
        return directKitResponseVO;
    }

    public DirectKitResponseVO processCancel(CommonValidatorVO commonValidatorVO) throws PZDBViolationException, PZConstraintViolationException
    {
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Merchants merchants = new Merchants();
        Transaction transaction = new Transaction();

        String description = "";
        String trackingid = "";
        String toid = "";
        String checksum = "";
        String key = "";
        String checksumAlgorithm = "";
        String activation = "";
        String partnerId = "";
        String accountId = "";
        String transactionStatus = "";
        String fromtype = "";
        String bankStatus = "";
        String resultCode = "";
        String resultDescription = "";
        String status = "";
        String statusMsg = "";

        Hashtable data=new Hashtable();

        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        PZCancelRequest cancelRequest = new PZCancelRequest();
        PZCancelResponse cancelResponse = new PZCancelResponse();

        description = commonValidatorVO.getTransDetailsVO().getOrderId();
        trackingid = commonValidatorVO.getTrackingid();
        toid = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        //String ipaddress = Functions.getIpAddress(request);
        checksum= commonValidatorVO.getTransDetailsVO().getChecksum();

        String requestedId = "";

        if("Y".equalsIgnoreCase(commonValidatorVO.getFlightMode()))
        {
            requestedId = commonValidatorVO.getParetnerId();
        }
        else
        {
            requestedId = commonValidatorVO.getMerchantDetailsVO().getMemberId();
        }

        /*Hashtable merchantHash = merchants.getMemberDetailsForTransaction(toid);
        if (!merchantHash.isEmpty())
        {
            key = (String) merchantHash.get("clkey");
            checksumAlgorithm = (String) merchantHash.get("checksumalgo");
            activation = (String) merchantHash.get("activation");
            partnerId=(String)merchantHash.get("partnerId");
        }
        else
        {
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.VALIDATION_TOID, ErrorMessages.INVALID_TOID));
            PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processCancel()", null, "Common", ErrorMessages.INVALID_TOID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, ErrorMessages.INVALID_TOID, new Throwable(ErrorMessages.INVALID_TOID));
        }*/

        /*PartnerManager partnerManager=new PartnerManager();
        PartnerDetailsVO partnerDetailsVO=partnerManager.getselfPartnerDetails(partnerId);
        responseType=partnerDetailsVO.getResponseType();
        responseLength=partnerDetailsVO.getResponseLength();*/

        data = transaction.getTransactionDetailsForCommon(trackingid,description,requestedId,commonValidatorVO.getFlightMode());
        if(data==null || data.size()==0)
        {
            String error1="Transaction not found for tracking ID-"+trackingid+" and description-"+description;
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_INVALIDTRANSACTION_DATA, error1));
            PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processCancel()", null, "Common",error1, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,error1,new Throwable(error1));
        }
        accountId = (String)data.get("accountid");
        transactionStatus= (String)data.get("status");
        if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(transactionStatus))
        {
            String error1 = "Captured transaction can not be cancel for trackingid "+trackingid;
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_VOID_TRANSACTION_NOTALLOWED, error1));
            PZExceptionHandler.raiseConstraintViolationException(DirectTransactionManager.class.getName(), "processCancel()", null, "Common",error1, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,error1,new Throwable(error1));
        }
        fromtype = GatewayAccountService.getGatewayAccount(accountId).getGateway();
        if (Functions.checkAPIGateways(fromtype))
        {
            cancelRequest = new PZCancelRequest();
            cancelRequest.setMemberId(Integer.parseInt((String)data.get("toid")));
            cancelRequest.setAccountId(Integer.parseInt(accountId));
            cancelRequest.setTrackingId(Integer.parseInt(trackingid));
            //cancelRequest.setIpAddress(commonValidatorVO.getAddressDetailsVO().getIp());
            cancelRequest.setCancelReason("Cancel Transaction " + trackingid);
            AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountId));

            //newly added
            AuditTrailVO auditTrailVO= new AuditTrailVO();
            auditTrailVO.setActionExecutorId((String)data.get("toid"));
            auditTrailVO.setActionExecutorName("REST Cancel");
            cancelRequest.setAuditTrailVO(auditTrailVO);

            if (fromtype != null && com.payment.sbm.core.SBMPaymentGateway.GATEWAY_TYPE.equals(fromtype) && PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(transactionStatus))
            {
                cancelResponse = paymentProcess.cancelCapture(cancelRequest);
            }
            else
            {
                cancelResponse = paymentProcess.cancel(cancelRequest);
            }

            logger.debug("status cancel transaction----" + cancelResponse.getStatus());
            /*if (cancelResponse.getBankStatus() != null)
            {
                bankStatus = cancelResponse.getBankStatus();
            }
            if (cancelResponse.getResultCode() != null)
            {
                resultCode = cancelResponse.getResultCode();
            }
            if (cancelResponse.getResultDescription() != null)
            {
                resultDescription = cancelResponse.getResultDescription();
            }*/

            PZResponseStatus status1 = cancelResponse.getStatus();
            if (PZResponseStatus.ERROR.equals(status1))
            {
                statusMsg = "Error while Cancel Transaction";
                status = "N";
            }
            else if (PZResponseStatus.FAILED.equals(status1))
            {
                statusMsg = "Transaction failed while cancel";
                status = "N";
            }
            else if (PZResponseStatus.SUCCESS.equals(status1))
            {
                statusMsg = "Cancel Transaction Process is successful";
                status = "Y";
            }
            else if (PZResponseStatus.PENDING.equals(status1))
            {
                statusMsg = cancelResponse.getResponseDesceiption();
                status = "N";
            }
        }
        transactionUtil.setCancelSystemResponseAndErrorCodeListVO(directKitResponseVO,errorCodeListVO,null,commonValidatorVO,status,statusMsg);
        return directKitResponseVO;
    }

    private QwipiRequestVO getQRequestVO(CommonValidatorVO commonValidatorVO)throws PZTechnicalViolationException
    {
        QwipiRequestVO RequestDetail=null;
        GenericCardDetailsVO cardDetail= commonValidatorVO.getCardDetailsVO();
        QwipiAddressDetailsVO AddressDetail= new QwipiAddressDetailsVO();
        QwipiTransDetailsVO TransDetail = new QwipiTransDetailsVO();
        Transaction transaction=new Transaction();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String tredtime=dateFormat.format(calendar.getTime()) ;
        String MD5key=null;
        String isKsnUrlFlag=null;
        Hashtable midHash = null;

        String MD5INFO= null;
        try
        {
            midHash = transaction.getMidKeyForQwipi(commonValidatorVO.getMerchantDetailsVO().getAccountId());
            if(!midHash.isEmpty())
            {
                MD5key = (String) midHash.get("midkey");
                isKsnUrlFlag= (String) midHash.get("isksnurlflag");
            }
            MD5INFO = Functions.convertmd5(commonValidatorVO.getTransDetailsVO().getFromid().trim()+commonValidatorVO.getTransDetailsVO().getOrderId().trim()+commonValidatorVO.getTransDetailsVO().getCurrency().trim()+commonValidatorVO.getTransDetailsVO().getAmount().trim()+tredtime.trim()+MD5key.trim());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DirectTransactionManager.class.getName(), "processTransaction()", null, "TransactionServices", "Internal error while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(DirectTransactionManager.class.getName(), "processTransaction()", null, "TransactionServices", "Internal error while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }

        cardDetail.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        cardDetail.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
        cardDetail.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        cardDetail.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear().substring(2));

        AddressDetail.setMd5key(MD5key);
        AddressDetail.setTime(tredtime);
        AddressDetail.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        AddressDetail.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        AddressDetail.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());
        AddressDetail.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        AddressDetail.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        AddressDetail.setProducts(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        AddressDetail.setState(commonValidatorVO.getAddressDetailsVO().getState());
        AddressDetail.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        AddressDetail.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        AddressDetail.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        AddressDetail.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        AddressDetail.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        AddressDetail.setMd5info(MD5INFO);
        AddressDetail.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        AddressDetail.setSsn(commonValidatorVO.getAddressDetailsVO().getSsn());

        TransDetail.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
        TransDetail.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        TransDetail.setOrderId(commonValidatorVO.getTrackingid());
        TransDetail.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderId());
        TransDetail.setMerNo(commonValidatorVO.getTransDetailsVO().getFromid());

        RequestDetail = new QwipiRequestVO(cardDetail,AddressDetail, TransDetail);
        RequestDetail.setKsnUrlFlag(isKsnUrlFlag);
        RequestDetail.setMd5Key(MD5key);
        RequestDetail.setMiddleName("");
        return RequestDetail;
    }

    private CommRequestVO getCommonRequestVO(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        RecurringBillingVO recurringBillingVO = commonValidatorVO.getRecurringBillingVO();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId = account.getMerchantId();
        String alias = account.getAliasName();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        String displayname = account.getDisplayName();

        cardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        cardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
        cardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        cardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
        cardDetailsVO.setCardType(Functions.getCardType(commonValidatorVO.getCardDetailsVO().getCardNum()));

        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        logger.error("card ip address-----> "+commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());

        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setMerchantOrderId(commonValidatorVO.getTransDetailsVO().getOrderId());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merctId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(displayname);
        merchantAccountVO.setAliasName(alias);

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setRecurringBillingVO(recurringBillingVO);

        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()),Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
        paymentProcess.setTransactionVOExtension(commRequestVO,commonValidatorVO);

        return commRequestVO;
    }

    public String getTransactionstatus(String status,String remark)
    {
        String mailtransactionStatus="Failed";
        if(status.equalsIgnoreCase("capturesuccess") || status.equalsIgnoreCase("authsuccessful"))
        {
            if(remark!=null && !remark.equals(""))
            {
                mailtransactionStatus="Successful ("+remark+")";
            }
            else
            {
                mailtransactionStatus="Successful";
            }
        }
        else if(status.equalsIgnoreCase("authstarted"))
        {
            if(remark!=null && !remark.equals(""))
            {
                mailtransactionStatus = remark;
            }
        }
        else if(status.equalsIgnoreCase("authfailed"))
        {
            if(remark!=null && !remark.equals(""))
            {
                mailtransactionStatus = mailtransactionStatus +" ("+remark+")";
            }
        }
        return mailtransactionStatus;
    }
}
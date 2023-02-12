package com.transaction.utils;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.fraud.FraudChecker;
import com.manager.PaymentManager;
import com.manager.RecurringManager;
import com.manager.TerminalManager;
import com.manager.TokenManager;
import com.manager.dao.FraudServiceDAO;
import com.manager.enums.ResponseLength;
import com.manager.vo.*;
import com.manager.vo.fraudruleconfVOs.FraudAccountDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Ecospend.EcospendPaymentGateway;
import com.payment.Ecospend.EcospendResponseVO;
import com.payment.Ecospend.EcospendUtils;
import com.payment.Enum.CardTypeEnum;
import com.payment.Enum.PaymentModeEnum;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.sms.AsynchronousSmsService;
import com.payment.utils.TransactionUtilsDAO;
import com.payment.validators.vo.CommonValidatorVO;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Created by Trupti on 5/7/2018.
 */
public class TransactionCoreUtils
{
    private final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.PfsServlet");
    final static String WAITSERVLET3D = rb.getString("3DSERVLET");
    private static Logger log = new Logger(TransactionCoreUtils.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(TransactionCoreUtils.class.getName());

    public VTResponseVO singleCall(CommonValidatorVO commonValidatorVO,ServletContext application) throws ServletException, IOException
    {
        log.debug("Enterred into TransactionUtils");
        transactionLogger.debug("Enterred into TransactionUtils");
        ActionEntry entry = new ActionEntry();
        Functions functions=new Functions();
        PaymentManager paymentManager=new PaymentManager();
        TransactionUtilsDAO transactionUtilsDAO=new TransactionUtilsDAO();
        SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
        AbstractPaymentGateway pg = null;
        AuditTrailVO auditTrailVO=new AuditTrailVO();

        RecurringManager recurringManager = new RecurringManager();
        RecurringBillingVO recurringBillingVO = new RecurringBillingVO();

        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO= commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO= commonValidatorVO.getTransDetailsVO();
        VTResponseVO vtResponseVO=new VTResponseVO();
        ActionEntry actionEntry = new ActionEntry();

        log.debug("header in Utils---"+genericTransDetailsVO.getHeader());
        int paymentType=0;
        int cardType=0;
        String toid=merchantDetailsVO.getMemberId();
        String totype=genericTransDetailsVO.getTotype();
        String error="";
        String fromType=null;
        String tableName =null;
        String respStatus=null;
        String respPaymentId=null;
        String errorName=null;
        String mailtransactionStatus="Failed";
        String billingDiscriptor="";
        String respRemark="";
        String machineid="";
        String respDateTime=null;
        String trackingid=null;
        String apiStatus="";
        String statusMsg="";
        String PANEntryType="";
        String generatedBy = commonValidatorVO.getMerchantDetailsVO().getLogin();

        if(commonValidatorVO==null)
        {
            commonValidatorVO.setErrorMsg("Transaction Data should not be empty.");
            calCheckSumAndStatusForSale(commonValidatorVO,vtResponseVO,"N");
            return vtResponseVO;
        }

        try
        {
            paymentType = Integer.parseInt(commonValidatorVO.getPaymentType());
            cardType = Integer.parseInt(commonValidatorVO.getCardType());
            fromType = genericTransDetailsVO.getFromtype();
            trackingid = commonValidatorVO.getTrackingid();

            GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
            GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
            tableName = Database.getTableName(gatewayType.getGateway());

            pg =  AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());

            if(merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                genericTransDetailsVO.setCurrency(genericTransDetailsVO.getCurrency());
            else
                genericTransDetailsVO.setCurrency(pg.getCurrency());

            auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            auditTrailVO.setActionExecutorName("MerchantVT");

            if(genericTransDetailsVO.getHeader().contains("PayProcess3DController"))
            {
                log.debug("header inside PayProcess3DController---"+genericTransDetailsVO.getHeader());
                auditTrailVO.setActionExecutorName("Customer");
            }
            else if(genericTransDetailsVO.getHeader().contains("SingleCallTokenTransaction"))
            {
                log.debug("header inside SingleCallTokenTransaction---"+genericTransDetailsVO.getHeader());
                auditTrailVO.setActionExecutorName("Merchant DK");
            }
            else
            {
                PANEntryType = "MoTo";
                log.debug("header inside MerchantVT---" + genericTransDetailsVO.getHeader());
                auditTrailVO.setActionExecutorName("MerchantVT");
            }


            if ((PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymentType || PaymentModeEnum.DEBIT_CARD_PAYMODE.ordinal() == paymentType) && (CardTypeEnum.VISA_CARDTYPE.ordinal() == cardType || CardTypeEnum.MASTER_CARD_CARDTYPE.ordinal() == cardType || CardTypeEnum.AMEX_CARDTYPE.ordinal() == cardType || CardTypeEnum.DINER_CARDTYPE.ordinal() == cardType) || CardTypeEnum.JCB.ordinal() == cardType || CardTypeEnum.MAESTRO.ordinal() == cardType || CardTypeEnum.INSTAPAYMENT.ordinal() == cardType || CardTypeEnum.DISC_CARDTYPE.ordinal() == cardType)
            {

                if(Functions.checkCommonProcessGateways(fromType))
                {
                    commonValidatorVO.setTrackingid(trackingid);
                    AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()), Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

                    CommRequestVO commRequestVO = null;
                    CommResponseVO transRespDetails = null;

                    commRequestVO = getCommonRequestVO(commonValidatorVO,fromType,PANEntryType);

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, commRequestVO, auditTrailVO, tableName);

                    //Make Online Fraud Checking Using Payment Fraud Processor
                    if ("Y".equalsIgnoreCase(merchantDetailsVO.getOnlineFraudCheck()))
                    {
                        FraudServiceDAO fraudServiceDAO = new FraudServiceDAO();
                        FraudAccountDetailsVO merchantFraudAccountVO = fraudServiceDAO.getMerchantFraudConfigurationDetails(toid);
                        if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                        {
                            commonValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                            FraudChecker fraudChecker = new FraudChecker();
                            fraudChecker.checkFraudBasedOnMerchantFlagNew(commonValidatorVO, merchantFraudAccountVO);
                            if (commonValidatorVO.isFraud())
                            {
                                respStatus = "failed";
                                mailtransactionStatus = "failed";
                                String remark = "High Risk Transaction Detected";
                                CommResponseVO commResponseVO = new CommResponseVO();
                                commResponseVO.setIpaddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                transactionUtilsDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid,"","","",null);
                                entry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount().toString(), ActionEntry.ACTION_FRAUD_VALIDATION_FAILED, ActionEntry.STATUS_FAILED, genericAddressDetailsVO.getCardHolderIpAddress(), commResponseVO, auditTrailVO);
                                SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                                sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, commonValidatorVO.getTrackingid(), mailtransactionStatus, remark,null);
                                AsynchronousSmsService smsService=new AsynchronousSmsService();
                                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, commonValidatorVO.getTrackingid(), mailtransactionStatus, remark,null);
                                commonValidatorVO.setErrorMsg("Fraud Validation Failed(Transaction +" + respStatus + ").");
                                calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
                                return vtResponseVO;
                            }
                        }
                    }

                    if("Y".equalsIgnoreCase(account.getIsRecurring()))
                    {
                        recurringBillingVO = new RecurringBillingVO();
                        if(commonValidatorVO.getRecurringBillingVO()!=null)
                        {
                            recurringBillingVO.setInterval(commonValidatorVO.getRecurringBillingVO().getInterval());
                            recurringBillingVO.setFrequency(commonValidatorVO.getRecurringBillingVO().getFrequency());
                            recurringBillingVO.setRunDate(commonValidatorVO.getRecurringBillingVO().getRunDate());
                        }
                        recurringBillingVO.setOriginTrackingId(trackingid);
                        recurringBillingVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                        recurringBillingVO.setCardHolderName(commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+commonValidatorVO.getAddressDetailsVO().getLastname());
                        recurringBillingVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                        recurringBillingVO.setTerminalid(commonValidatorVO.getTerminalId());
                        if("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsManualRecurring()))
                        {
                            recurringBillingVO.setRecurringType("Manual");
                            recurringManager.insertRecurringSubscriptionEntry(recurringBillingVO);
                        }
                        else if ("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsRecurring()))
                        {
                            recurringBillingVO.setRecurringType("Automatic");
                            recurringManager.insertRecurringSubscriptionEntry(recurringBillingVO);
                        }
                        commonValidatorVO.setRecurringBillingVO(recurringBillingVO);
                    }

                    String status="";
                    if(merchantDetailsVO.getService().equalsIgnoreCase("N"))
                    {
                        transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                        status = "authsuccessful";
                    }
                    else
                    {
                        transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                        status = "capturesuccess";
                    }

                    if(transRespDetails!=null && functions.isValueNull(transRespDetails.getStatus()))
                    {
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                        {
                            vtResponseVO.setIsSuccessful("Y");
                            if(status.equals("capturesuccess"))
                            {
                                respStatus="capturesuccess";
                                transactionLogger.error(commRequestVO.getAddressDetailsVO().getCardHolderIpAddress()+"<---->  cardholder IP Address");
                                paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO);
                            }
                            else
                            {
                                respStatus="authsuccessful";
                                paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO);
                            }


                            if (functions.isValueNull(transRespDetails.getDescriptor()))
                            {
                                billingDiscriptor = transRespDetails.getDescriptor();
                            }
                            else
                            {
                                billingDiscriptor = pg.getDisplayName();
                            }

                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending3DConfirmation"))
                        {
                            if(!functions.isValueNull(transRespDetails.getThreeDVersion()))
                                transRespDetails.setThreeDVersion("3Dv1");
                            paymentManager.updatePaymentIdForCommon(transRespDetails,trackingid);
                            paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, transRespDetails, commRequestVO, auditTrailVO);
                            Comm3DResponseVO response3D = (Comm3DResponseVO) transRespDetails;

                            String form = "";
                            if(fromType.equalsIgnoreCase("pbs") || fromType.equalsIgnoreCase("billdesk"))
                            {
                                form = paymentProcess.get3DConfirmationForm(commonValidatorVO, response3D);
                            }
                            else
                            {
                                form = paymentProcess.get3DConfirmationForm(trackingid, commonValidatorVO.getCtoken(), response3D);
                            }

                            vtResponseVO.setHtmlFormValue(form);
                            vtResponseVO.setStatus("pending3DConfirmation");
                            vtResponseVO.setIsSuccessful("Y");
                            vtResponseVO.setBillingDescriptor(billingDiscriptor);
                            calCheckSumAndStatusForSale(commonValidatorVO,vtResponseVO,"pending3DConfirmation");
                            return vtResponseVO;
                        }
                        else
                        {
                            respStatus="authfailed";
                            paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, commRequestVO,auditTrailVO);
                        }


                        respDateTime = transRespDetails.getResponseTime();
                        respRemark = transRespDetails.getDescription();
                        respPaymentId =  transRespDetails.getTransactionId();
                        errorName = transRespDetails.getErrorName();

                        if(ResponseLength.FULL.toString().equals(commonValidatorVO.getResponseLength()))
                        {
                            paymentProcess.setTransactionResponseVOParamsExtension(transRespDetails,vtResponseVO);
                        }
                        transactionUtilsDAO.updateTransactionAfterResponse(tableName,respStatus,genericTransDetailsVO.getAmount(),genericAddressDetailsVO.getIp(),machineid,respPaymentId,respRemark,respDateTime,trackingid,transRespDetails.getRrn(),transRespDetails.getArn(),transRespDetails.getAuthCode(),"Non-3D");
                    }
                    mailtransactionStatus = transactionUtilsDAO.sendTransactionMail(respStatus,trackingid,respRemark,merchantDetailsVO.getEmailSent(),billingDiscriptor);
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                }
                else
                {
                    //error message
                    commonValidatorVO.setErrorMsg("Transaction can not be Done.Could not find valid gateway");
                    calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
                    return vtResponseVO;
                }
                //mail conformation
                genericTransDetailsVO.setBillingDiscriptor(billingDiscriptor);
                commonValidatorVO.setErrorMsg(mailtransactionStatus);
                commonValidatorVO.setErrorName(errorName);
            }
            else
            {
                commonValidatorVO.setErrorMsg("Invalid Cardtype and PayMode, couldn't find valid gateway.");
                calCheckSumAndStatusForSale(commonValidatorVO,vtResponseVO,"N");
                return vtResponseVO;
            }
        }

        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException", e);
            PZExceptionHandler.handleGenericCVEException(e, toid, PZOperations.VT_SALE);
            calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
            return vtResponseVO;
        }
        catch (PZTechnicalViolationException tve)
        {
            transactionLogger.error("PZTechnicalViolationException in CommonPaymentProcess---", tve);
            String remarkEnum = tve.getPzTechnicalConstraint().getPzTechEnum().toString();
            if(remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SERVICE_EXCEPTION.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.AXISFAULT.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.HTTP_EXCEPTION.toString()))
            {
                transactionUtilsDAO.insertFailedTransaction(trackingid, PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE.toString(), tableName,commonValidatorVO.getTransDetailsVO().getAmount(),"");
                commonValidatorVO.setErrorMsg("Bank Connectivity Issue while processing your request.");
            }
            else if (tve.getPzTechnicalConstraint().getPzTechEnum().toString().equalsIgnoreCase(PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT.toString()))
            {
                commonValidatorVO.setErrorMsg("Your transaction is Timed out.Please check at bank side.");
            }
            else
            {
                commonValidatorVO.setErrorMsg("We have encountered an internal error while processing your request.");
            }
            PZExceptionHandler.handleTechicalCVEException(tve, toid, PZOperations.VT_SALE);
            calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
            return vtResponseVO;
        }
        catch (PZConstraintViolationException gce)
        {
            transactionLogger.error("PZConstraintViolationException---",gce);
            commonValidatorVO.setErrorMsg(gce.getMessage());
            calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
            return vtResponseVO;
        }
        catch (PZGenericConstraintViolationException gce)
        {
            transactionLogger.error("PZGenericConstraintViolationException---",gce);
            commonValidatorVO.setErrorMsg(gce.getMessage());
            try
            {
                paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.AUTH_FAILED.toString(),commonValidatorVO.getTransDetailsVO().getAmount(),null,null,null,null,String.valueOf(trackingid));
                actionEntry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount().toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(), null, auditTrailVO);
            }
            catch (PZDBViolationException dbe)
            {
                transactionLogger.error("DBViolation Exception in TransactionUtils---", dbe);
                PZExceptionHandler.handleDBCVEException(dbe,toid,"singleCall");
                apiStatus = "N";
                statusMsg = "Your transaction is fail. Please contact your Customer Support:::";
                vtResponseVO.setStatus(apiStatus);
                vtResponseVO.setStatusDescription(statusMsg);
                return vtResponseVO;
            }
            calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
            return vtResponseVO;
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError",systemError);
            mailtransactionStatus = "We have encountered an internal error while processing your request";
            transactionUtilsDAO.insertFailedTransaction(trackingid, "System Error while processing transaction:100", tableName, commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            //send Admin Mail
            sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_FAILED_TRANSACTION_NOTIFICATION,trackingid,respStatus,systemError.getStackTrace().toString(),billingDiscriptor);
            commonValidatorVO.setErrorMsg("We have encountered an internal error while processing your request");
            calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
            return vtResponseVO;
        }

        calCheckSumAndStatusForSale(commonValidatorVO,vtResponseVO,getResponceStatus(respStatus));
        return vtResponseVO;
    }

    public VTResponseVO singleCallPaybyLink(CommonValidatorVO commonValidatorVO,ServletContext application) throws ServletException, IOException
    {
        VTResponseVO vtResponseVO       = new VTResponseVO();
        PaymentManager paymentManager   = new PaymentManager();
        AuditTrailVO auditTrailVO       = new AuditTrailVO();
        System.out.println("inside singleCallPaybyLink");
        MerchantDetailsVO merchantDetailsVO             = commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();
        Functions functions                             = new Functions();
        //System.out.println("accountid---"+merchantDetailsVO.getAccountId());
        String trackingid   = commonValidatorVO.getTrackingid();
        String respStatus   = "";
        try
        {
            CommRequestVO commRequestVO         = null;
            CommResponseVO transRespDetails     = null;
            commRequestVO = getCommonRequestVO(commonValidatorVO, "", "");
           // commRequestVO.setCustomerId(commonValidatorVO.getCustomerId());

            if("ecospend".equalsIgnoreCase(genericTransDetailsVO.getFromtype()))
            {
                if(commRequestVO.getTransDetailsVO() != null){
                    commRequestVO.getTransDetailsVO().setToId(merchantDetailsVO.getMemberId());
                }

                EcospendPaymentGateway ecospendPaymentGateway = new EcospendPaymentGateway(merchantDetailsVO.getAccountId());
                transRespDetails = (CommResponseVO) ecospendPaymentGateway.processPaybyLink(trackingid, commRequestVO);

            }

            auditTrailVO.setActionExecutorId(merchantDetailsVO.getMemberId());
            auditTrailVO.setActionExecutorName("");
            commonValidatorVO.getAddressDetailsVO().getFirstname();

            paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, auditTrailVO);
            if(functions.isValueNull(transRespDetails.getStatus()) && transRespDetails.getStatus().equalsIgnoreCase("pending")){
                transRespDetails.setThreeDVersion("Non-3D");
                paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                paymentManager.insertEcospendDetails((EcospendResponseVO)transRespDetails,trackingid);
            }
            else
            {
                respStatus="authfailed";
                transactionLogger.debug("inside authfail --------------");

                paymentManager.updateTransactionForCommon(transRespDetails, respStatus, trackingid, auditTrailVO, "transaction_common", "", transRespDetails.getTransactionId(), transRespDetails.getResponseTime(), transRespDetails.getRemark());

            }
            if (functions.isValueNull(transRespDetails.getRedirectUrl()))
            {
                vtResponseVO.setStatus(ActionEntry.ACTION_AUTHORISTION_STARTED);
                vtResponseVO.setStatusDescription("Paybylink generated successfully.");
            }
            else
            {
                vtResponseVO.setStatus("Failed");
                vtResponseVO.setStatusDescription("Paybylink generation failed.");
            }
        }
        catch (PZDBViolationException dbe)
        {
            dbe.printStackTrace();
            log.error("PZDBViolationException---",dbe);
        }
        catch (PZTechnicalViolationException dbe)
        {
            dbe.printStackTrace();
            log.error("PZTechnicalViolationException---",dbe);
        }
        catch (PZConstraintViolationException dbe)
        {
            dbe.printStackTrace();
            log.error("PZConstraintViolationException---",dbe);
        }
        catch (PZGenericConstraintViolationException e)
        {
            e.printStackTrace();
        }
        return vtResponseVO;
    }

    public VTResponseVO singleCallVT(CommonValidatorVO commonValidatorVO,ServletContext application) throws ServletException, IOException
    {
        ActionEntry entry = new ActionEntry();
        Functions functions=new Functions();
        PaymentManager paymentManager=new PaymentManager();
        TransactionUtilsDAO transactionUtilsDAO=new TransactionUtilsDAO();
        SendTransactionEventMailUtil sendTransactionEventMailUtil=new SendTransactionEventMailUtil();
        AbstractPaymentGateway pg = null;
        AuditTrailVO auditTrailVO=new AuditTrailVO();

        RecurringManager recurringManager = new RecurringManager();
        RecurringBillingVO recurringBillingVO = new RecurringBillingVO();

        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();
        GenericAddressDetailsVO genericAddressDetailsVO= commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO= commonValidatorVO.getTransDetailsVO();
        VTResponseVO vtResponseVO=new VTResponseVO();
        ActionEntry actionEntry = new ActionEntry();

        int paymentType=0;
        int cardType=0;
        String toid=merchantDetailsVO.getMemberId();
        String totype=genericTransDetailsVO.getTotype();
        String error="";
        String fromType=null;
        String tableName =null;
        String respStatus=null;
        String respPaymentId=null;
        String errorName=null;
        String mailtransactionStatus="Failed";
        String billingDiscriptor="";
        String respRemark="";
        String machineid="";
        String respDateTime=null;
        String trackingid=null;
        String apiStatus="";
        String statusMsg="";
        String PANEntryType="";
        String generatedBy = commonValidatorVO.getMerchantDetailsVO().getLogin();

        //TOID & TOTYPE check with related partnerid
        if(commonValidatorVO==null)
        {
            commonValidatorVO.setErrorMsg("Transaction Data should not be empty.");
            calCheckSumAndStatusForSale(commonValidatorVO,vtResponseVO,"N");
            return vtResponseVO;
        }

        try
        {
            paymentType = Integer.parseInt(commonValidatorVO.getPaymentType());
            cardType = Integer.parseInt(commonValidatorVO.getCardType());
            fromType = genericTransDetailsVO.getFromtype();
            trackingid = commonValidatorVO.getTrackingid();

            GatewayAccount account = GatewayAccountService.getGatewayAccount(String.valueOf(commonValidatorVO.getMerchantDetailsVO().getAccountId()));
            GatewayType gatewayType = GatewayTypeService.getGatewayType(account.getPgTypeId());
            tableName = Database.getTableName(gatewayType.getGateway());

            pg =  AbstractPaymentGateway.getGateway(commonValidatorVO.getMerchantDetailsVO().getAccountId());

            if(merchantDetailsVO.getMultiCurrencySupport().equalsIgnoreCase("Y"))
                genericTransDetailsVO.setCurrency(genericTransDetailsVO.getCurrency());
            else
                genericTransDetailsVO.setCurrency(pg.getCurrency());

            auditTrailVO.setActionExecutorId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
            auditTrailVO.setActionExecutorName("MerchantVT");

            if(genericTransDetailsVO.getHeader().contains("PayProcess3DController"))
            {
                auditTrailVO.setActionExecutorName("Customer");
            }
            else if(genericTransDetailsVO.getHeader().contains("SingleCallTokenTransaction"))
            {
                auditTrailVO.setActionExecutorName("Merchant DK");
            }
            else
            {
                PANEntryType = "MoTo";
                auditTrailVO.setActionExecutorName("MerchantVT");
            }

            if ((PaymentModeEnum.CREDIT_CARD_PAYMODE.ordinal() == paymentType || PaymentModeEnum.DEBIT_CARD_PAYMODE.ordinal() == paymentType) && (CardTypeEnum.VISA_CARDTYPE.ordinal() == cardType || CardTypeEnum.MASTER_CARD_CARDTYPE.ordinal() == cardType || CardTypeEnum.AMEX_CARDTYPE.ordinal() == cardType || CardTypeEnum.DINER_CARDTYPE.ordinal() == cardType) || CardTypeEnum.JCB.ordinal() == cardType || CardTypeEnum.MAESTRO.ordinal() == cardType || CardTypeEnum.INSTAPAYMENT.ordinal() == cardType || CardTypeEnum.DISC_CARDTYPE.ordinal() == cardType)
            {
                if(Functions.checkCommonProcessGateways(fromType))
                {
                    commonValidatorVO.setTrackingid(trackingid);
                    AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()), Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

                    CommRequestVO commRequestVO = null;
                    CommResponseVO transRespDetails = null;
                    commRequestVO = getCommonRequestVO(commonValidatorVO,fromType,PANEntryType);

                    paymentManager.insertAuthStartedTransactionEntryForCommon(commonValidatorVO, trackingid, commRequestVO, auditTrailVO, tableName);

                    //Make Online Fraud Checking Using Payment Fraud Processor
                    if ("Y".equalsIgnoreCase(merchantDetailsVO.getOnlineFraudCheck()))
                    {
                        FraudServiceDAO fraudServiceDAO = new FraudServiceDAO();
                        FraudAccountDetailsVO merchantFraudAccountVO = fraudServiceDAO.getMerchantFraudConfigurationDetails(toid);
                        if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                        {
                            commonValidatorVO.setTime(Functions.convertDateDBFormat(Calendar.getInstance().getTime()));
                            FraudChecker fraudChecker = new FraudChecker();
                            fraudChecker.checkFraudBasedOnMerchantFlagNew(commonValidatorVO, merchantFraudAccountVO);
                            if (commonValidatorVO.isFraud())
                            {
                                respStatus = "failed";
                                mailtransactionStatus = "failed";
                                String remark = "High Risk Transaction Detected";
                                CommResponseVO commResponseVO = new CommResponseVO();
                                commResponseVO.setIpaddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                                transactionUtilsDAO.updateTransactionAfterResponse(tableName, respStatus, genericTransDetailsVO.getAmount(), genericAddressDetailsVO.getIp(), machineid, respPaymentId, respRemark, respDateTime, trackingid,"","","",null);
                                entry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), genericTransDetailsVO.getAmount().toString(), ActionEntry.ACTION_FRAUD_VALIDATION_FAILED, ActionEntry.STATUS_FAILED, genericAddressDetailsVO.getCardHolderIpAddress(), commResponseVO, auditTrailVO);
                                SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
                                sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, commonValidatorVO.getTrackingid(), mailtransactionStatus, remark,null);
                                AsynchronousSmsService smsService=new AsynchronousSmsService();
                                smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, commonValidatorVO.getTrackingid(), mailtransactionStatus, remark,null);
                                commonValidatorVO.setErrorMsg("Fraud Validation Failed(Transaction +" + respStatus + ").");
                                calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
                                return vtResponseVO;
                            }
                        }
                    }

                    if("Y".equalsIgnoreCase(account.getIsRecurring()))
                    {
                        String ccnum=commonValidatorVO.getCardDetailsVO().getCardNum();
                        recurringBillingVO = new RecurringBillingVO();
                        if(commonValidatorVO.getRecurringBillingVO()!=null)
                        {
                            recurringBillingVO.setInterval(commonValidatorVO.getRecurringBillingVO().getInterval());
                            recurringBillingVO.setFrequency(commonValidatorVO.getRecurringBillingVO().getFrequency());
                            recurringBillingVO.setRunDate(commonValidatorVO.getRecurringBillingVO().getRunDate());
                        }
                        recurringBillingVO.setOriginTrackingId(trackingid);
                        recurringBillingVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                        recurringBillingVO.setCardHolderName(commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+commonValidatorVO.getAddressDetailsVO().getLastname());
                        recurringBillingVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                        recurringBillingVO.setTerminalid(commonValidatorVO.getTerminalId());
                        recurringBillingVO.setFirstSix(ccnum.substring(0,6));
                        recurringBillingVO.setLastFour(ccnum.substring((ccnum.length()-4),ccnum.length()));
                        if("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsManualRecurring()))
                        {
                            recurringBillingVO.setRecurringType("Manual");
                            recurringManager.insertRecurringSubscriptionEntry(recurringBillingVO);
                        }
                        else if ("Y".equalsIgnoreCase(commonValidatorVO.getTerminalVO().getIsRecurring()))
                        {
                            recurringBillingVO.setRecurringType("Automatic");
                            recurringManager.insertRecurringSubscriptionEntry(recurringBillingVO);
                        }
                        commonValidatorVO.setRecurringBillingVO(recurringBillingVO);
                    }

                    String status="";
                    if(merchantDetailsVO.getService().equalsIgnoreCase("N"))
                    {
                        transRespDetails = (CommResponseVO) pg.processAuthentication(trackingid, commRequestVO);
                        status = "authsuccessful";
                    }
                    else
                    {
                        transRespDetails = (CommResponseVO) pg.processSale(trackingid, commRequestVO);
                        status = "capturesuccess";
                    }

                    if(transRespDetails!=null && functions.isValueNull(transRespDetails.getStatus()))
                    {
                        if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("success"))
                        {
                            vtResponseVO.setIsSuccessful("Y");
                            if(status.equals("capturesuccess"))
                            {
                                respStatus="capturesuccess";
                                paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO);
                            }
                            else
                            {
                                respStatus="authsuccessful";
                                paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, transRespDetails, commRequestVO,auditTrailVO);
                            }

                            if (functions.isValueNull(transRespDetails.getDescriptor()))
                            {
                                billingDiscriptor = transRespDetails.getDescriptor();
                            }
                            else
                            {
                                billingDiscriptor = pg.getDisplayName();
                            }
                        }
                        else if ((transRespDetails.getStatus().trim()).equalsIgnoreCase("pending3DConfirmation"))
                        {
                            if(!functions.isValueNull(transRespDetails.getThreeDVersion()))
                                transRespDetails.setThreeDVersion("3Dv1");
                            paymentManager.updatePaymentIdForCommon(transRespDetails, trackingid);
                            paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_3D_AUTHORISATION_STARTED, ActionEntry.STATUS_3D_AUTHORISATION_STARTED, transRespDetails, commRequestVO, auditTrailVO);
                            Comm3DResponseVO response3D = (Comm3DResponseVO) transRespDetails;

                            String form = "";
                            if(fromType.equalsIgnoreCase("pbs") || fromType.equalsIgnoreCase("billdesk"))
                            {
                                form = paymentProcess.get3DConfirmationForm(commonValidatorVO, response3D);
                            }
                            else
                            {
                                form = paymentProcess.get3DConfirmationFormVT(trackingid, commonValidatorVO.getCtoken(), response3D);
                            }

                            vtResponseVO.setHtmlFormValue(form);
                            vtResponseVO.setStatus("pending3DConfirmation");
                            vtResponseVO.setIsSuccessful("Y");
                            calCheckSumAndStatusForSale(commonValidatorVO,vtResponseVO,"pending3DConfirmation");
                            return vtResponseVO;
                        }
                        else
                        {
                            respStatus="authfailed";
                            paymentProcess.actionEntry(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, transRespDetails, commRequestVO,auditTrailVO);
                        }

                        respDateTime = transRespDetails.getResponseTime();
                        respRemark = transRespDetails.getDescription();
                        respPaymentId =  transRespDetails.getTransactionId();
                        errorName = transRespDetails.getErrorName();

                        if(ResponseLength.FULL.toString().equals(commonValidatorVO.getResponseLength()))
                        {
                            paymentProcess.setTransactionResponseVOParamsExtension(transRespDetails,vtResponseVO);
                        }
                        transactionUtilsDAO.updateTransactionAfterResponse(tableName,respStatus,genericTransDetailsVO.getAmount(),genericAddressDetailsVO.getIp(),machineid,respPaymentId,respRemark,respDateTime,trackingid,transRespDetails.getRrn(),transRespDetails.getArn(),transRespDetails.getAuthCode(),"Non-3D");
                    }
                    mailtransactionStatus = transactionUtilsDAO.sendTransactionMail(respStatus,trackingid,respRemark,merchantDetailsVO.getEmailSent(),billingDiscriptor);
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingid), mailtransactionStatus, null, billingDiscriptor);
                }
                else
                {
                    //error message
                    commonValidatorVO.setErrorMsg("Transaction can not be Done.Could not find valid gateway");
                    calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
                    return vtResponseVO;
                }
                //mail conformation
                genericTransDetailsVO.setBillingDiscriptor(billingDiscriptor);
                commonValidatorVO.setErrorMsg(mailtransactionStatus);
                commonValidatorVO.setErrorName(errorName);
            }
            else
            {
                commonValidatorVO.setErrorMsg("Invalid Cardtype and PayMode, couldn't find valid gateway.");
                calCheckSumAndStatusForSale(commonValidatorVO,vtResponseVO,"N");
                return vtResponseVO;
            }
        }

        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException", e);
            PZExceptionHandler.handleGenericCVEException(e, toid, PZOperations.VT_SALE);
            calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
            return vtResponseVO;
        }
        catch (PZTechnicalViolationException tve)
        {
            transactionLogger.error("PZTechnicalViolationException in CommonPaymentProcess---", tve);
            String remarkEnum = tve.getPzTechnicalConstraint().getPzTechEnum().toString();
            if(remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SERVICE_EXCEPTION.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.AXISFAULT.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.HTTP_EXCEPTION.toString()))
            {
                transactionUtilsDAO.insertFailedTransaction(trackingid, PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE.toString(), tableName,commonValidatorVO.getTransDetailsVO().getAmount(),"");
                commonValidatorVO.setErrorMsg("Bank Connectivity Issue while processing your request.");
            }
            else if (tve.getPzTechnicalConstraint().getPzTechEnum().toString().equalsIgnoreCase(PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT.toString()))
            {
                commonValidatorVO.setErrorMsg("Your transaction is Timed out.Please check at bank side.");
            }
            else
            {
                commonValidatorVO.setErrorMsg("We have encountered an internal error while processing your request.");
            }
            PZExceptionHandler.handleTechicalCVEException(tve, toid, PZOperations.VT_SALE);
            calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
            return vtResponseVO;
        }
        catch (PZConstraintViolationException gce)
        {
            transactionLogger.error("PZConstraintViolationException---",gce);
            commonValidatorVO.setErrorMsg(gce.getMessage());
            calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
            return vtResponseVO;
        }
        catch (PZGenericConstraintViolationException gce)
        {
            transactionLogger.error("PZGenericConstraintViolationException---",gce);
            commonValidatorVO.setErrorMsg(gce.getMessage());
            try
            {
                paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.AUTH_FAILED.toString(),commonValidatorVO.getTransDetailsVO().getAmount(),null,null,null,null,String.valueOf(trackingid));
                actionEntry.actionEntryForGenericTransaction(tableName, commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getAmount().toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress(), null, auditTrailVO);
            }
            catch (PZDBViolationException dbe)
            {
                transactionLogger.error("DBViolation Exception in TransactionUtils---", dbe);
                PZExceptionHandler.handleDBCVEException(dbe,toid,"singleCall");
                apiStatus = "N";
                statusMsg = "Your transaction is fail. Please contact your Customer Support:::";
                vtResponseVO.setStatus(apiStatus);
                vtResponseVO.setStatusDescription(statusMsg);
                return vtResponseVO;
            }
            calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
            return vtResponseVO;
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError",systemError);
            mailtransactionStatus = "We have encountered an internal error while processing your request";
            transactionUtilsDAO.insertFailedTransaction(trackingid, "System Error while processing transaction:100", tableName, commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
            //send Admin Mail
            sendTransactionEventMailUtil.sendTransactionEventMail(MailEventEnum.ADMIN_FAILED_TRANSACTION_NOTIFICATION,trackingid,respStatus,systemError.getStackTrace().toString(),billingDiscriptor);
            commonValidatorVO.setErrorMsg("We have encountered an internal error while processing your request");
            calCheckSumAndStatusForSale(commonValidatorVO, vtResponseVO, "N");
            return vtResponseVO;
        }

        calCheckSumAndStatusForSale(commonValidatorVO,vtResponseVO,getResponceStatus(respStatus));
        return vtResponseVO;
    }


    public void calCheckSumAndStatusForSale(CommonValidatorVO commonValidatorVO,VTResponseVO vtResponseVO,String status)
    {
        Functions functions = new Functions();
        String checkSum = null;
        String amount ="";
        String billingDiscriptor=" ";
        String trackingId="";
        String description="";
        String currency="";
        String fraudScore="";
        if (commonValidatorVO.getTransDetailsVO().getAmount() != null)
        {
            amount = commonValidatorVO.getTransDetailsVO().getAmount();
        }
        if(commonValidatorVO.getTransDetailsVO().getBillingDiscriptor()!=null)
        {
            billingDiscriptor=commonValidatorVO.getTransDetailsVO().getBillingDiscriptor();
        }
        if(commonValidatorVO.getTrackingid()!=null)
        {
            trackingId=commonValidatorVO.getTrackingid();
        }
        if(commonValidatorVO.getTransDetailsVO().getOrderId()!=null)
        {
            description=commonValidatorVO.getTransDetailsVO().getOrderId();
        }
        if(commonValidatorVO.getTransDetailsVO().getCurrency()!=null)
        {
            currency=commonValidatorVO.getTransDetailsVO().getCurrency();
        }
        if(commonValidatorVO.getFraudScore()!=null)
        {
            fraudScore=commonValidatorVO.getFraudScore();
        }
        try
        {
            checkSum = Checksum.generateChecksumV2(description,amount,status,commonValidatorVO.getMerchantDetailsVO().getKey(),commonValidatorVO.getMerchantDetailsVO().getChecksumAlgo());
        }
        catch (NoSuchAlgorithmException e)
        {
            status ="N";
            commonValidatorVO.setErrorMsg(e.getMessage());
        }

        vtResponseVO.setBillingDescriptor(billingDiscriptor);
        vtResponseVO.setCheckSum(checkSum);
        vtResponseVO.setOrderId(description);
        vtResponseVO.setResAmount(amount);
        vtResponseVO.setStatus(status);
        vtResponseVO.setStatusDescription(commonValidatorVO.getErrorMsg());
        vtResponseVO.setTrackingId(trackingId);
        vtResponseVO.setCurrency(currency);
        vtResponseVO.setFraudScore(fraudScore);
        if (functions.isValueNull(commonValidatorVO.getErrorName()))
            vtResponseVO.setErrorName(commonValidatorVO.getErrorName());
    }

    private CommRequestVO getCommonRequestVO(CommonValidatorVO commonValidatorVO,String fromType,String PANEntryType) throws PZDBViolationException
    {
        CommRequestVO commRequestVO = null;
        CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();
        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();
        CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
        CommDeviceDetailsVO deviceDetailsVO=new CommDeviceDetailsVO();
        RecurringBillingVO recurringBillingVO =new RecurringBillingVO();
        GatewayAccount account = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String merctId = account.getMerchantId();
        String alias = account.getAliasName();
        String username = account.getFRAUD_FTP_USERNAME();
        String password = account.getFRAUD_FTP_PASSWORD();
        String displayname = account.getDisplayName();
        Functions functions = new Functions();

        cardDetailsVO.setCardNum(commonValidatorVO.getCardDetailsVO().getCardNum());
        cardDetailsVO.setcVV(commonValidatorVO.getCardDetailsVO().getcVV());
        cardDetailsVO.setExpMonth(commonValidatorVO.getCardDetailsVO().getExpMonth());
        cardDetailsVO.setExpYear(commonValidatorVO.getCardDetailsVO().getExpYear());
        cardDetailsVO.setCardType(Functions.getCardType(commonValidatorVO.getCardDetailsVO().getCardNum()));

        addressDetailsVO.setFirstname(commonValidatorVO.getAddressDetailsVO().getFirstname());
        addressDetailsVO.setLastname(commonValidatorVO.getAddressDetailsVO().getLastname());
        addressDetailsVO.setCity(commonValidatorVO.getAddressDetailsVO().getCity());
        addressDetailsVO.setCountry(commonValidatorVO.getAddressDetailsVO().getCountry());
        addressDetailsVO.setTelnocc(commonValidatorVO.getAddressDetailsVO().getTelnocc());
        addressDetailsVO.setPhone(commonValidatorVO.getAddressDetailsVO().getPhone());
        addressDetailsVO.setEmail(commonValidatorVO.getAddressDetailsVO().getEmail());
        addressDetailsVO.setState(commonValidatorVO.getAddressDetailsVO().getState());
        addressDetailsVO.setStreet(commonValidatorVO.getAddressDetailsVO().getStreet());
        addressDetailsVO.setZipCode(commonValidatorVO.getAddressDetailsVO().getZipCode());
        addressDetailsVO.setBirthdate(commonValidatorVO.getAddressDetailsVO().getBirthdate());
        addressDetailsVO.setCardHolderIpAddress(commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
        addressDetailsVO.setIp(commonValidatorVO.getAddressDetailsVO().getIp());
        addressDetailsVO.setLanguage(commonValidatorVO.getAddressDetailsVO().getLanguage());

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount())){
            addressDetailsVO.setTmpl_amount(commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
        }else {
            addressDetailsVO.setTmpl_amount(commonValidatorVO.getTransDetailsVO().getAmount());
        }
        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency())){
            addressDetailsVO.setTmpl_currency(commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        }else {
            addressDetailsVO.setTmpl_currency(commonValidatorVO.getTransDetailsVO().getCurrency());
        }

        transDetailsVO.setAmount(commonValidatorVO.getTransDetailsVO().getAmount()); //Amount * 100 according to the docs
        transDetailsVO.setCurrency(commonValidatorVO.getTransDetailsVO().getCurrency());
        transDetailsVO.setOrderId(commonValidatorVO.getTrackingid());
        transDetailsVO.setOrderDesc(commonValidatorVO.getTransDetailsVO().getOrderDesc());
        transDetailsVO.setRedirectUrl(commonValidatorVO.getTransDetailsVO().getRedirectUrl());
        transDetailsVO.setCardType(commonValidatorVO.getCardType());
        transDetailsVO.setPaymentType(commonValidatorVO.getPaymentType());

        CommMerchantVO merchantAccountVO = new CommMerchantVO();
        merchantAccountVO.setMerchantId(merctId);
        merchantAccountVO.setPassword(password);
        merchantAccountVO.setMerchantUsername(username);
        merchantAccountVO.setDisplayName(displayname);
        merchantAccountVO.setAliasName(alias);
        merchantAccountVO.setAddress(commonValidatorVO.getMerchantDetailsVO().getAddress());
        merchantAccountVO.setBrandName(commonValidatorVO.getMerchantDetailsVO().getBrandName());
        transactionLogger.error("commonValidatorVO.getMerchantDetailsVO().getCountry()---->"+commonValidatorVO.getMerchantDetailsVO().getCountry());
        merchantAccountVO.setCountry(commonValidatorVO.getMerchantDetailsVO().getCountry());
        merchantAccountVO.setSitename(commonValidatorVO.getMerchantDetailsVO().getSiteName());
        String merchantTelNo = commonValidatorVO.getMerchantDetailsVO().getTelNo();
        String mPhone = "";

        if (functions.isValueNull(merchantTelNo) && merchantTelNo.contains("-"))
        {
            String[] phone = merchantTelNo.split("-");
            mPhone = phone[1];
        }
        else
        {
            mPhone = merchantTelNo;
        }
        if (functions.isValueNull(mPhone))
            merchantAccountVO.setMerchantSupportNumber(mPhone);
        log.error("host url "+commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        merchantAccountVO.setHostUrl(commonValidatorVO.getMerchantDetailsVO().getHostUrl());
        if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getCompany_name()))
            merchantAccountVO.setMerchantOrganizationName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMerchantLogo()) && commonValidatorVO.getMerchantDetailsVO().getMerchantLogo().equalsIgnoreCase("Y"))
            merchantAccountVO.setSupportName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
        else if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag()) && commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag().equalsIgnoreCase("Y"))
            merchantAccountVO.setSupportName(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
        String partnerTelNo = commonValidatorVO.getMerchantDetailsVO().getPartnerSupportContactNumber();
        String pPhone = "";
        if (functions.isValueNull(partnerTelNo) && partnerTelNo.contains("-"))
        {
            String[] phone = partnerTelNo.split("-");
            pPhone = phone[1];
        }
        else
        {
            pPhone = partnerTelNo;
        }
        if (functions.isValueNull(pPhone))
            merchantAccountVO.setPartnerSupportContactNumber(pPhone);

        if(commonValidatorVO.getTerminalVO() != null)
        {
            recurringBillingVO.setIsAutomaticRecurring(commonValidatorVO.getTerminalVO().getIsRecurring());
            recurringBillingVO.setIsManualRecurring(commonValidatorVO.getTerminalVO().getIsManualRecurring());
            recurringBillingVO.setRecurringType("Manual");
        }

        if(commonValidatorVO.getDeviceDetailsVO() !=null)
        {
            deviceDetailsVO.setUser_Agent(commonValidatorVO.getDeviceDetailsVO().getUser_Agent());
        }

        commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        if("PFS".equalsIgnoreCase(fromType))
        {
            transDetailsVO.setResponseHashInfo(UUID.randomUUID().toString());
        }
        else
        {
            transDetailsVO.setResponseHashInfo("");
        }
        transDetailsVO.setTerminalId(commonValidatorVO.getTerminalId());
        commRequestVO.setAddressDetailsVO(addressDetailsVO);
        commRequestVO.setCardDetailsVO(cardDetailsVO);
        commRequestVO.setCommMerchantVO(merchantAccountVO);
        commRequestVO.setTransDetailsVO(transDetailsVO);
        commRequestVO.setRecurringBillingVO(recurringBillingVO);
        commRequestVO.setIsPSTProcessingRequest(commonValidatorVO.getIsPSTProcessingRequest());
        commRequestVO.setReject3DCard(commonValidatorVO.getReject3DCard());
        commRequestVO.setPANEntryType(PANEntryType);
        commRequestVO.setCurrencyConversion(commonValidatorVO.getCurrencyConversion());
        commRequestVO.setConversionCurrency(commonValidatorVO.getConversionCurrency());
        commRequestVO.setCommDeviceDetailsVO(deviceDetailsVO);

        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(commonValidatorVO.getTrackingid()),Integer.parseInt(commonValidatorVO.getMerchantDetailsVO().getAccountId()));

        paymentProcess.setTransactionVOExtension(commRequestVO,commonValidatorVO);
        return commRequestVO;
    }
    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D){
        String form = "<form id=\"pay_form\" name=\"pay_form\" action=\"" +response3D.getUrlFor3DRedirect()+ "\" method=\"post\">\n" +
                "<input type=hidden name=\"PaReq\" value=\""+response3D.getPaReq()+"\"/>" +
                "<input type=hidden name=\"TermUrl\" value=\""+WAITSERVLET3D+"?t="+trackingId+"&ctoken="+ctoken+"\"/>" +
                "<input type=hidden name=\"MD\" value=\""+response3D.getMd()+"\"/>" +
                "</form>";
        return form;
    }
    private String getResponceStatus(String respState)
    {
        Functions functions=new Functions();
        String state="N";
        if(functions.isValueNull(respState))
        {
            if(respState.equals("capturesuccess")||respState.equals("authsuccessful"))
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

    public DirectCommResponseVO transactionAPI(CommonValidatorVO commonValidatorVO)
    {
        CommResponseVO commResponse = null;
        Functions functions = new Functions();
        AuditTrailVO auditTrailVO=new AuditTrailVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        FraudServiceDAO fraudServiceDAO=new FraudServiceDAO();
        CommRequestVO commRequestVO = null;
        AbstractPaymentGateway pg = null;
        SendTransactionEventMailUtil sendTransactionEventMail = new SendTransactionEventMailUtil();
        DirectCommResponseVO directCommResponseVO = new DirectCommResponseVO();
        PaymentManager paymentManager = new PaymentManager();
        String accountId=commonValidatorVO.getMerchantDetailsVO().getAccountId();
        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.valueOf(accountId));
        String cardHolderIpAddress=commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress();
        String header = commonValidatorVO.getTransDetailsVO().getHeader();
        String status = "";
        String apiStatus = "";
        String type = "";
        String transactionStatus = "";
        String statusMsg = "";
        String toid=commonValidatorVO.getMerchantDetailsVO().getMemberId();
        String amount=commonValidatorVO.getTransDetailsVO().getAmount();
        String ccnum= commonValidatorVO.getCardDetailsVO().getCardNum();
        String firstSix=ccnum.substring(0,6);
        String lastFour=ccnum.substring((ccnum.length()-4),ccnum.length());
        String cvv = commonValidatorVO.getCardDetailsVO().getcVV();
        String expmonth=commonValidatorVO.getCardDetailsVO().getExpMonth();
        String expyear=commonValidatorVO.getCardDetailsVO().getExpYear();

        String firstname= commonValidatorVO.getAddressDetailsVO().getFirstname();
        String lastname= commonValidatorVO.getAddressDetailsVO().getLastname();
        String street=commonValidatorVO.getAddressDetailsVO().getStreet();
        String country=commonValidatorVO.getAddressDetailsVO().getCountry();
        String city=commonValidatorVO.getAddressDetailsVO().getCity();
        String state= commonValidatorVO.getAddressDetailsVO().getState();
        String zip=commonValidatorVO.getAddressDetailsVO().getZipCode();
        String telno=commonValidatorVO.getAddressDetailsVO().getPhone();
        String telnocc=commonValidatorVO.getAddressDetailsVO().getTelnocc();
        String email=commonValidatorVO.getAddressDetailsVO().getEmail();
        String boiledname= commonValidatorVO.getAddressDetailsVO().getFirstname()+" "+commonValidatorVO.getAddressDetailsVO().getLastname();
        String ipaddress=commonValidatorVO.getAddressDetailsVO().getIp();
        String currency=commonValidatorVO.getMerchantDetailsVO().getCurrency();
        String description=commonValidatorVO.getTransDetailsVO().getOrderId();
        String displayName = "";
        String birthdate = commonValidatorVO.getAddressDetailsVO().getBirthdate();
        String language = commonValidatorVO.getAddressDetailsVO().getLanguage();
        String paymentType = commonValidatorVO.getPaymentType();
        String isRecurring = "";
        String recInterval = "";
        String recFrequency = "";
        String recRunDate = "";
        String isAutomaticRecurring = commonValidatorVO.getTerminalVO().getIsRecurring();
        String isManualRecurring = commonValidatorVO.getTerminalVO().getIsManualRecurring();
        String recurringType = "";
        String terminalid = commonValidatorVO.getTerminalId();
        String responseLength="";
        String token="";
        String mailtransactionStatus = "";
        String billingDescriptor = "";
        String generatedBy = commonValidatorVO.getMerchantDetailsVO().getLogin();

        String iban = commonValidatorVO.getCardDetailsVO().getIBAN();
        String bic = commonValidatorVO.getCardDetailsVO().getBIC();
        String mandateId = commonValidatorVO.getCardDetailsVO().getMandateId();

        if(commonValidatorVO.getResponseLength()!=null)
        {
            responseLength=commonValidatorVO.getResponseLength();
        }

        CommTransactionDetailsVO transDetailsVO = new CommTransactionDetailsVO();

        if(commonValidatorVO.getRecurringBillingVO()!=null)
        {
            isRecurring = commonValidatorVO.getRecurringBillingVO().getRecurring();
            recInterval = commonValidatorVO.getRecurringBillingVO().getInterval();
            recFrequency = commonValidatorVO.getRecurringBillingVO().getFrequency();
            recRunDate = commonValidatorVO.getRecurringBillingVO().getRunDate();
        }

        if(header!=null)
        {
            if(header.contains("VirtualSingleCall"))
            {
                auditTrailVO.setActionExecutorName("Merchant VT");
            }
            else
            {
                auditTrailVO.setActionExecutorName("Merchant DK");
            }

        }
        auditTrailVO.setActionExecutorId(toid);

        int trackingId = 0;
        int detailId=0;
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        directCommResponseVO.setDescriptor(account.getDisplayName());
        try
        {
            CommRequestVO commRequestVO1 = new CommRequestVO();
            CommAddressDetailsVO genericAddressDetailsVO1=new CommAddressDetailsVO();
            //commonValidatorVO.getAddressDetailsVO().setIp(cardHolderIpAddress);
            commRequestVO1.setAddressDetailsVO(genericAddressDetailsVO1);

            if("PFS".equalsIgnoreCase(commonValidatorVO.getTransDetailsVO().getFromtype()))
            {
                transDetailsVO.setResponseHashInfo(UUID.randomUUID().toString());//UUID set for PFS
            }
            commRequestVO1.setTransDetailsVO(transDetailsVO);
            CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();
            commCardDetailsVO.setIBAN(commonValidatorVO.getCardDetailsVO().getIBAN());
            commCardDetailsVO.setBIC(commonValidatorVO.getCardDetailsVO().getBIC());
            commCardDetailsVO.setMandateId(commonValidatorVO.getCardDetailsVO().getMandateId());

            commRequestVO1.setCardDetailsVO(commCardDetailsVO);
            commRequestVO1.getAddressDetailsVO().setCardHolderIpAddress(cardHolderIpAddress);
            trackingId=paymentManager.insertAuthStartedForCommon(commonValidatorVO,ActionEntry.STATUS_AUTHORISTION_STARTED);

            paymentProcess.actionEntry(String.valueOf(trackingId), commonValidatorVO.getTransDetailsVO().getAmount(), ActionEntry.ACTION_AUTHORISTION_STARTED, ActionEntry.STATUS_AUTHORISTION_STARTED, commResponse, commRequestVO1, auditTrailVO, null);

            directCommResponseVO.setTrackingid(String.valueOf(trackingId));

            //commRequestVO1=null;
            transactionLogger.debug("online fraud check---"+commonValidatorVO.getMerchantDetailsVO().getOnlineFraudCheck());
            if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getOnlineFraudCheck()))
            {
                //Make Online Fraud Checking Using Payment Fraud Processor

                FraudAccountDetailsVO merchantFraudAccountVO = fraudServiceDAO.getMerchantFraudConfigurationDetails(toid);

                if ("Y".equals(merchantFraudAccountVO.getIsOnlineFraudCheck()))
                {

                    commonValidatorVO.setTrackingid(String.valueOf(trackingId));
                    commonValidatorVO.setTime(Functions.convertDateDBFormat(new Date()));
                    commonValidatorVO.getMerchantDetailsVO().setAccountId(accountId);
                    FraudChecker onlineFraudChecker = new FraudChecker();
                    onlineFraudChecker.checkFraudBasedOnMerchantFlagNew(commonValidatorVO, merchantFraudAccountVO, account);
                    directCommResponseVO.setFraudScore(commonValidatorVO.getFraudScore());
                    directCommResponseVO.setRulesTriggered(commonValidatorVO.getRuleTriggered());
                    if (commonValidatorVO.isFraud())
                    {
                        //paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.FAILED.toString(),amount,machineId,null,"Fraud Transaction",null,String.valueOf(trackingId));
                        paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId), PZTransactionStatus.FAILED.toString(), amount, "Fraud Transaction", null,null);

                        paymentProcess.actionEntry(String.valueOf(trackingId), amount, ActionEntry.ACTION_FRAUD_VALIDATION_FAILED, ActionEntry.STATUS_FAILED, null, commRequestVO1, auditTrailVO, null);


                        apiStatus = "N";
                        statusMsg = "Fraud Validation Failed";
                        directCommResponseVO.setCommStatus(apiStatus);
                        directCommResponseVO.setCommStatusMessage(statusMsg);
                        //transactionUtil.setSystemResponseAndErrorCodeListVO(null,errorCodeListVO,ErrorName.SYS_FRAUD_TRANS,null,null,null,null);

                        mailtransactionStatus = "failed";
                        String remark = "High Risk Transaction Detected";

                        sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, remark,null);
                        AsynchronousSmsService smsService=new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_FRAUD_FAILED_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, remark,null);
                        return directCommResponseVO;
                    }
                }
            }

            displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
        }
        catch (PZDBViolationException dbe)
        {
            log.error("PZDBViolationException in CommonPaymentProcess---",dbe);
            transactionLogger.error("PZDBViolationException in CommonPaymentProcess---",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, toid, "transactionAPI");
            apiStatus = "N";
            statusMsg = "Internal Error Occured, Please contact Customer Support";
            directCommResponseVO.setCommStatus(apiStatus);
            //commResponse.set("errorCodeListVO", dbe.getPzdbConstraint().getErrorCodeListVO());
            directCommResponseVO.setCommStatusMessage(statusMsg);
            return directCommResponseVO;
        }

        try
        {

            pg = AbstractPaymentGateway.getGateway(accountId);
            String merchantId = account.getMerchantId();
            String alias = account.getAliasName();
            String username = account.getFRAUD_FTP_USERNAME();
            String password = account.getFRAUD_FTP_PASSWORD();
            String displayname = account.getDisplayName();

            CommCardDetailsVO cardDetailsVO = new CommCardDetailsVO();
            cardDetailsVO.setCardNum(ccnum);
            cardDetailsVO.setcVV(cvv);
            cardDetailsVO.setExpMonth(expmonth);
            cardDetailsVO.setExpYear(expyear);
            cardDetailsVO.setCardType(Functions.getCardType(ccnum));

            cardDetailsVO.setIBAN(iban);
            cardDetailsVO.setBIC(bic);
            cardDetailsVO.setMandateId(mandateId);

            CommAddressDetailsVO addressDetailsVO = new CommAddressDetailsVO();

            addressDetailsVO.setFirstname(firstname);
            addressDetailsVO.setLastname(lastname);
            addressDetailsVO.setCity(city);
            addressDetailsVO.setCountry(country);
            addressDetailsVO.setIp(ipaddress);
            addressDetailsVO.setCardHolderIpAddress(cardHolderIpAddress);
            addressDetailsVO.setTelnocc(telnocc);
            addressDetailsVO.setPhone(telno);
            addressDetailsVO.setEmail(email);
            addressDetailsVO.setState(state);
            addressDetailsVO.setStreet(street);
            addressDetailsVO.setZipCode(zip);
            addressDetailsVO.setBirthdate(birthdate);
            addressDetailsVO.setLanguage(language);
            addressDetailsVO.setCardHolderIpAddress(cardHolderIpAddress);

            BigDecimal txnAmount = new BigDecimal(amount);



            transDetailsVO.setAmount(String.valueOf(txnAmount));
            transDetailsVO.setCurrency(currency);
            transDetailsVO.setOrderId(description);
            transDetailsVO.setOrderDesc(description);
            transDetailsVO.setPaymentType(paymentType);
            transDetailsVO.setCardType(commonValidatorVO.getCardType());
            transDetailsVO.setTerminalId(commonValidatorVO.getTerminalId());

            CommMerchantVO merchantAccountVO = new CommMerchantVO();
            merchantAccountVO.setMerchantId(account.getMerchantId());
            merchantAccountVO.setPassword(account.getFRAUD_FTP_PASSWORD());

            merchantAccountVO.setMerchantId(merchantId);
            merchantAccountVO.setPassword(password);
            merchantAccountVO.setMerchantUsername(username);
            merchantAccountVO.setDisplayName(displayname);
            merchantAccountVO.setAliasName(alias);
            merchantAccountVO.setAddress(commonValidatorVO.getMerchantDetailsVO().getAddress());
            merchantAccountVO.setBrandName(commonValidatorVO.getMerchantDetailsVO().getBrandName());
            merchantAccountVO.setCountry(commonValidatorVO.getMerchantDetailsVO().getCountry());
            merchantAccountVO.setSitename(commonValidatorVO.getMerchantDetailsVO().getSiteName());
            String merchantTelno = commonValidatorVO.getMerchantDetailsVO().getTelNo();
            String mTelNo = "";

            if (functions.isValueNull(merchantTelno) && merchantTelno.contains("-"))
            {
                String[] phone = merchantTelno.split("-");
                mTelNo = phone[1];
            }
            else
            {
                mTelNo = merchantTelno;
            }
            if (functions.isValueNull(mTelNo))
                merchantAccountVO.setMerchantSupportNumber(mTelNo);
            if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getCompany_name()))
                merchantAccountVO.setMerchantOrganizationName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
            if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMerchantLogo()) && commonValidatorVO.getMerchantDetailsVO().getMerchantLogo().equalsIgnoreCase("Y"))
                merchantAccountVO.setSupportName(commonValidatorVO.getMerchantDetailsVO().getCompany_name());
            else if(functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag()) && commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag().equalsIgnoreCase("Y"))
                merchantAccountVO.setSupportName(commonValidatorVO.getMerchantDetailsVO().getPartnerName());
            String partnerTelNo = commonValidatorVO.getMerchantDetailsVO().getPartnerSupportContactNumber();
            String pPhone = "";
            if (functions.isValueNull(partnerTelNo) && partnerTelNo.contains("-"))
            {
                String[] phone = partnerTelNo.split("-");
                pPhone = phone[1];
            }
            else
            {
                pPhone = partnerTelNo;
            }
            if (functions.isValueNull(pPhone))
                merchantAccountVO.setPartnerSupportContactNumber(pPhone);

            commRequestVO = PaymentProcessRequestVOFactory.getRequestVOInstance(Integer.parseInt(accountId));



            //RecurringBillingVO set in CommRequestVO
            RecurringBillingVO recurringBillingVO = null;
            RecurringManager recurringManager = new RecurringManager();

            if((isRecurring!=null && !isRecurring.equals("") && "Y".equalsIgnoreCase(isRecurring)) || ("Y".equalsIgnoreCase(account.getIsRecurring())))
            {
                recurringBillingVO = new RecurringBillingVO();
                recurringBillingVO.setRecurring(isRecurring);
                recurringBillingVO.setInterval(recInterval);
                recurringBillingVO.setFrequency(recFrequency);
                recurringBillingVO.setRunDate(recRunDate);


                //insert for recurring subscription table
                recurringBillingVO.setOriginTrackingId(String.valueOf(trackingId));
                recurringBillingVO.setAmount(amount);
                recurringBillingVO.setCardHolderName(firstname+" "+lastname);
                recurringBillingVO.setMemberId(toid);
                recurringBillingVO.setTerminalid(terminalid);
                recurringBillingVO.setIsAutomaticRecurring(isAutomaticRecurring);
                recurringBillingVO.setIsManualRecurring(isManualRecurring);
                recurringBillingVO.setFirstSix(firstSix);
                recurringBillingVO.setLastFour(lastFour);
                if ("Y".equalsIgnoreCase(isManualRecurring))
                {
                    recurringType = "Manual";
                    recurringBillingVO.setRecurringType(recurringType);
                    recurringManager.insertRecurringSubscriptionEntry(recurringBillingVO);
                }
                else if ("Y".equalsIgnoreCase(isAutomaticRecurring))
                {
                    recurringType = "Automatic";
                    recurringBillingVO.setRecurringType(recurringType);
                    recurringManager.insertRecurringSubscriptionEntry(recurringBillingVO);
                }

                commRequestVO.setRecurringBillingVO(recurringBillingVO);
            }

            ReserveField2VO reserveField2VO = null;


            if(commonValidatorVO.getReserveField2VO()!=null)
            {
                reserveField2VO = new ReserveField2VO();
                reserveField2VO.setAccountType(commonValidatorVO.getReserveField2VO().getAccountType());
                reserveField2VO.setRoutingNumber(commonValidatorVO.getReserveField2VO().getRoutingNumber());
                reserveField2VO.setAccountNumber(commonValidatorVO.getReserveField2VO().getAccountNumber());
            }
            CommDeviceDetailsVO commDeviceDetailsVO = new CommDeviceDetailsVO();
            if(commonValidatorVO.getDeviceDetailsVO()!=null)
            {
                commDeviceDetailsVO.setUser_Agent(commonValidatorVO.getDeviceDetailsVO().getUser_Agent());
            }

            commRequestVO.setAddressDetailsVO(addressDetailsVO);
            commRequestVO.setCardDetailsVO(cardDetailsVO);
            commRequestVO.setCommMerchantVO(merchantAccountVO);
            commRequestVO.setTransDetailsVO(transDetailsVO);
            commRequestVO.setReserveField2VO(reserveField2VO);
            commRequestVO.setRecurringBillingVO(recurringBillingVO);
            commRequestVO.setIsPSTProcessingRequest(commonValidatorVO.getIsPSTProcessingRequest());
            commRequestVO.setReject3DCard(commonValidatorVO.getReject3DCard());
            commRequestVO.setCurrencyConversion(commonValidatorVO.getCurrencyConversion());
            commRequestVO.setConversionCurrency(commonValidatorVO.getConversionCurrency());
            commRequestVO.setCommDeviceDetailsVO(commDeviceDetailsVO);


            paymentProcess.setTransactionVOParamsExtension(commRequestVO, commonValidatorVO);
            StringBuffer sb = new StringBuffer();
            String isSuccessful="N";
            if("N".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getService()))
            {
                commResponse = (CommResponseVO) pg.processAuthentication(String.valueOf(trackingId), commRequestVO);
                if("Y".equals(commonValidatorVO.getCurrencyConversion()) && commResponse!=null && functions.isValueNull(commResponse.getAmount())){
                    amount=commResponse.getAmount();
                }
                if("success".equalsIgnoreCase(commResponse.getStatus().trim()))
                {
                    isSuccessful="Y";
                    status = ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL;
                    int actionEntry = paymentProcess.actionEntry(String.valueOf(trackingId), amount.toString(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponse, commRequestVO, auditTrailVO, commResponse.getRemark());
                    statusMsg = "Approved";
                    mailtransactionStatus = "successful";

                    if(functions.isValueNull(commResponse.getDescriptor()))
                    {
                        directCommResponseVO.setDescriptor(commResponse.getDescriptor());
                    }
                }
                else if("pending".equalsIgnoreCase(commResponse.getStatus().trim()))
                {
                    isSuccessful="Y";
                    mailtransactionStatus = "pending";
                }
                else
                {

                    int actionEntry = paymentProcess.actionEntry(String.valueOf(trackingId), amount.toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponse, commRequestVO, auditTrailVO, commResponse.getRemark());

                    statusMsg = "Failed";
                    status = ActionEntry.STATUS_AUTHORISTION_FAILED;
                    mailtransactionStatus = "failed";
                }
                type = "auth";

            }
            else /*if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getService()))*/
            {

                commResponse = (CommResponseVO) pg.processSale(String.valueOf(trackingId), commRequestVO);
                if("Y".equals(commonValidatorVO.getCurrencyConversion()) && commResponse!=null && functions.isValueNull(commResponse.getAmount())){
                    amount=commResponse.getAmount();
                }
                if("success".equalsIgnoreCase(commResponse.getStatus().trim()))
                {
                    isSuccessful="Y";
                    status = ActionEntry.STATUS_CAPTURE_SUCCESSFUL;
                    int actionEntry = paymentProcess.actionEntry(String.valueOf(trackingId), amount.toString(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponse, commRequestVO, auditTrailVO, commResponse.getRemark());
                    statusMsg = "Approved";
                    mailtransactionStatus = "successful";

                    if(functions.isValueNull(commResponse.getDescriptor()))
                    {
                        directCommResponseVO.setDescriptor(commResponse.getDescriptor());
                    }
                }
                else if("pending".equalsIgnoreCase(commResponse.getStatus().trim()))
                {
                    isSuccessful="Y";
                    mailtransactionStatus = "pending";
                    status = "pending";
                }
                else
                {
                    log.debug("status for fail----");
                    int actionEntry = paymentProcess.actionEntry(String.valueOf(trackingId), amount.toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponse, commRequestVO, auditTrailVO, commResponse.getRemark());

                    statusMsg = "Failed";
                    mailtransactionStatus = "failed";
                    status = ActionEntry.STATUS_CAPTURE_FAILED;
                }
                type = "sale";

            }
            paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingId),status,amount,commResponse.getDescription(),commResponse.getTransactionId(),"Non-3D");

            if("Y".equals(isSuccessful) && "Y".equals(commonValidatorVO.getMerchantDetailsVO().getIsTokenizationAllowed()))
            {
                TerminalManager terminalManager=new TerminalManager();
                if(terminalManager.isTokenizationActiveOnTerminal(toid,commonValidatorVO.getTerminalId()))
                {
                    TokenManager tokenManager = new TokenManager();
                    String strToken = tokenManager.isCardAvailable(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getCardDetailsVO().getCardNum());
                    if (functions.isValueNull(strToken))
                    {
                        token = strToken;
                    }
                    else
                    {
                        TokenRequestVO tokenRequestVO = new TokenRequestVO();
                        TokenResponseVO tokenResponseVO = null;
                        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

                        commCardDetailsVO.setCardNum(ccnum);
                        commCardDetailsVO.setExpMonth(expmonth);
                        commCardDetailsVO.setExpYear(expyear);
                        commCardDetailsVO.setcVV(cvv);

                        tokenRequestVO.setMemberId(toid);
                        tokenRequestVO.setTrackingId(String.valueOf(trackingId));
                        tokenRequestVO.setDescription(description);

                        tokenRequestVO.setCommCardDetailsVO(commCardDetailsVO);
                        tokenRequestVO.setAddressDetailsVO(addressDetailsVO);

                        tokenRequestVO.setGeneratedBy(generatedBy);

                        tokenResponseVO = tokenManager.createToken(tokenRequestVO);
                        if ("success".equals(tokenResponseVO.getStatus()))
                        {
                            token = tokenResponseVO.getToken();
                        }
                    }
                }

            }//todo to set token in vo
            directCommResponseVO.setToken(token);

            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                paymentProcess.setTransactionResponseVOParamsExtension(commResponse,directCommResponseVO);
            }
            if ("Y".equals(isSuccessful))
            {
                billingDescriptor = directCommResponseVO.getDescriptor();
            }
            //todo - send transaction maill start
            /*if("Y".equalsIgnoreCase(commonValidatorVO.getMerchantDetailsVO().getEmailSent()))
            {*/
            Date date72 = new Date();
            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
            asynchronousMailService.sendEmail(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null,billingDescriptor);


            AsynchronousSmsService smsService = new AsynchronousSmsService();
            smsService.sendSMS(MailEventEnum.PARTNERS_MERCHANT_SALE_TRANSACTION, String.valueOf(trackingId), mailtransactionStatus, null, billingDescriptor);

            if("success".equalsIgnoreCase(commResponse.getStatus()))
            {
                apiStatus = "Y";
                statusMsg = statusMsg+"---Your Transaction is successful";
                directCommResponseVO.setCommStatus(apiStatus);
                directCommResponseVO.setCommStatusMessage(statusMsg);
                return directCommResponseVO;
            }
            else if("pending".equalsIgnoreCase(commResponse.getStatus()))
            {

                apiStatus = "P";
                statusMsg = statusMsg + "--(" + commResponse.getDescription().replace(":"," ") + " )"   ;
                directCommResponseVO.setCommStatus(apiStatus);
                directCommResponseVO.setCommStatusMessage(statusMsg);
                return directCommResponseVO;
            }
            else if("pending3DConfirmation".equalsIgnoreCase(commResponse.getStatus()))
            {
                ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
                errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
                PZGenericConstraint genConstraint = new PZGenericConstraint("CommonPaymentProcess","transactionApi()",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
                throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
            }
            else
            {
                apiStatus = "N";
                statusMsg = statusMsg + "--(" + commResponse.getDescription().replace(":"," ") + " )";
                directCommResponseVO.setCommStatus(apiStatus);
                directCommResponseVO.setCommStatusMessage(statusMsg);
                return directCommResponseVO;
            }
        }
        catch (PZTechnicalViolationException tve)
        {
            log.error("PZTechnicalViolationException in CommonPaymentProcess---",tve);
            transactionLogger.error("PZTechnicalViolationException in CommonPaymentProcess---",tve);
            String remarkEnum = tve.getPzTechnicalConstraint().getPzTechEnum().toString();

            if(remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.MALFORMED_URL_EXCEPITON.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.PROTOCOL_EXCEPTION.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.SERVICE_EXCEPTION.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.AXISFAULT.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.HTTP_EXCEPTION.toString()) || remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.IOEXCEPTION.toString()))
            {
                try
                {
                    paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.FAILED.toString(),amount,"",null,PZTechnicalExceptionEnum.BANK_CONNECTIVITY_ISSUE.toString(),null,String.valueOf(trackingId));

                    paymentProcess.actionEntry(String.valueOf(trackingId), amount, ActionEntry.ACTION_FAILED, ActionEntry.STATUS_FAILED, commResponse, commRequestVO,auditTrailVO,PZTechnicalExceptionEnum.SSL_HANDSHAKE_ISSUE.toString());

                }
                catch (PZDBViolationException dbe)
                {
                    log.error("DBViolation Exception in CommonPaymentProcess---", dbe);
                    transactionLogger.error("DBViolation Exception in CommonPaymentProcess---", dbe);
                    PZExceptionHandler.handleDBCVEException(dbe,toid,"transactionAPI");
                    apiStatus = "N";
                    statusMsg = "Your transaction is fail. Please contact your Customer Support:::";
                    directCommResponseVO.setCommStatus(apiStatus);
                    directCommResponseVO.setCommStatusMessage(statusMsg);
                    return directCommResponseVO;
                }
                statusMsg = "Bank Connectivity Issue while processing your request";
            }
            else if(remarkEnum.equalsIgnoreCase(PZTechnicalExceptionEnum.GATEWAY_CONNECTION_TIMEOUT.toString()))
            {
                statusMsg = "Your transaction is Timed out.Please check with support team.";
            }
            else
            {
                statusMsg = "We have encountered technical error while processing your request";
            }
            PZExceptionHandler.handleTechicalCVEException(tve,toid,PZOperations.DIRECTKIT_SALE);
            apiStatus = "N";
            if(tve.getPzTechnicalConstraint().getErrorCodeListVO()!=null)
                directCommResponseVO.setCommErrorCode(tve.getPzTechnicalConstraint().getErrorCodeListVO().toString());
            directCommResponseVO.setCommStatus(apiStatus);
            directCommResponseVO.setCommStatusMessage(statusMsg);
            return directCommResponseVO;
        }
        catch (PZDBViolationException dbe)
        {
            log.error("DBViolation Exception in CommonPaymentProcess---", dbe);
            transactionLogger.error("DBViolation Exception in CommonPaymentProcess---", dbe);
            PZExceptionHandler.handleDBCVEException(dbe, toid, "transactionAPI");
            apiStatus = "N";
            statusMsg = "Your transaction is fail. Please contact your Customer Support:::";
            if(dbe.getPzdbConstraint().getErrorCodeListVO()!=null)
                directCommResponseVO.setCommErrorCode(dbe.getPzdbConstraint().getErrorCodeListVO().toString());
            directCommResponseVO.setCommStatus(apiStatus);
            directCommResponseVO.setCommStatusMessage(statusMsg);
            return directCommResponseVO;
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("Constraint Violation Exception in CommonPaymentProcess---", cve);
            transactionLogger.error("Constraint Violation Exception in CommonPaymentProcess---", cve);
            PZExceptionHandler.handleCVEException(cve, toid, "transactionAPI");
            apiStatus = "N";
            statusMsg = cve.getPzConstraint().getMessage();
            if(cve.getPzConstraint().getErrorCodeListVO()!=null)
                directCommResponseVO.setCommErrorCode(cve.getPzConstraint().getErrorCodeListVO().toString());
            directCommResponseVO.setCommStatus(apiStatus);
            directCommResponseVO.setCommStatusMessage(statusMsg);
            return directCommResponseVO;
        }
        catch (PZGenericConstraintViolationException gce)
        {
            log.error("PZGenericConstraintViolationException in CommonPaymentProcess---",gce);
            transactionLogger.error("PZGenericConstraintViolationException in CommonPaymentProcess---",gce);
            statusMsg = gce.getMessage();

            try
            {
                paymentManager.updateTransactionAfterResponseForCommon(PZTransactionStatus.AUTH_FAILED.toString(),amount,null,null,null,null,String.valueOf(trackingId));
                paymentProcess.actionEntry(String.valueOf(trackingId), amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, null, commRequestVO,auditTrailVO,null);

            }
            catch (PZDBViolationException dbe)
            {
                log.error("DBViolation Exception in CommonPaymentProcess---", dbe);
                transactionLogger.error("DBViolation Exception in CommonPaymentProcess---", dbe);
                PZExceptionHandler.handleDBCVEException(dbe,toid,"transactionAPI");
                apiStatus = "N";
                statusMsg = "Your transaction is fail. Please contact your Customer Support:::";
                directCommResponseVO.setCommStatus(apiStatus);
                directCommResponseVO.setCommStatusMessage(statusMsg);
                return directCommResponseVO;
            }

            PZExceptionHandler.handleGenericCVEException(gce,toid,"Merchant DK");
            apiStatus = "N";
            if(gce.getPzGenericConstraint().getErrorCodeListVO()!=null)
                directCommResponseVO.setCommErrorCode(gce.getPzGenericConstraint().getErrorCodeListVO().toString());
            directCommResponseVO.setCommStatus(apiStatus);
            directCommResponseVO.setCommStatusMessage(statusMsg);
            return directCommResponseVO;
        }
        catch (SystemError e)
        {
            log.error("systemerror", e);
            transactionLogger.error("systemerror", e);
            PZExceptionHandler.raiseAndHandleDBViolationException(CommonPaymentProcess.class.getName(),"transactionAPI()",null,"common","Exception::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,e.getMessage(),e.getCause(),toid,PZOperations.DIRECTKIT_SALE);
            apiStatus = "N";
            statusMsg = "Your transaction is fail ::-(" + commResponse.getDescription() + ")";
            directCommResponseVO.setCommStatus(apiStatus);
            directCommResponseVO.setCommStatusMessage(statusMsg);
            return directCommResponseVO;
        }
    }

}

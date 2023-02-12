package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.logicboxes.util.ApplicationProperties;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.checkers.PaymentChecker;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZRefundResponse;
import com.payment.response.PZResponseStatus;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.DirectRefundValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;
import payment.util.ReadRequest;
import payment.util.ReadXMLRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 2/13/13
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCallGenericReverse extends PzServlet
{
    private static Logger logger = new Logger(SingleCallGenericReverse.class.getName());
    boolean isLogEnabled = Boolean.parseBoolean(ApplicationProperties.getProperty("IS_LOG_ENABLED"));
    private static TransactionLogger transactionLogger = new TransactionLogger(SingleCallGenericReverse.class.getName());

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        TransactionUtility transactionError = new TransactionUtility();
        PrintWriter pWriter = response.getWriter();
        DirectRefundValidatorVO directRefundValidatorVO = null;
        String rType = request.getParameter("requesttype");
        StringBuffer status = new StringBuffer();

        try
        {
            if("XML".equalsIgnoreCase(rType))
            {
                String XMLData = request.getParameter("data");
                if(XMLData==null || XMLData.equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java","doPost()",null,"Transaction", ErrorMessages.INVALID_INPUT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
                }
                directRefundValidatorVO= ReadXMLRequest.readXmlRequestForRefund(XMLData);
                singleCallReverse(directRefundValidatorVO,request,response,pWriter);
            }
            else
            {
                directRefundValidatorVO = ReadRequest.getRequestParametersForRefund(request);
                singleCallReverse(directRefundValidatorVO,request,response,pWriter);
            }
        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("PZConstraintViolationException in SingleCallGenericReverse---",cve);
            PZExceptionHandler.handleCVEException(cve,null,PZOperations.DIRECTKIT_REFUND);
            status.append("N");
            StringBuffer statusDesc = new StringBuffer();
            statusDesc.append("Invalid Data");
            transactionError.calCheckSumAndWriteStatusForRefund(pWriter, null, null, status, statusDesc, null, null,null);
            return;
        }
    }

    public void singleCallReverse(DirectRefundValidatorVO directRefundValidatorVO, HttpServletRequest req, HttpServletResponse res, PrintWriter pWriter) throws ServletException, IOException
    {
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        Functions functions = new Functions();
        StringBuffer status = new StringBuffer("");
        StringBuffer statusMsg = new StringBuffer("");
        TransactionUtility transactionError = new TransactionUtility();
        Transaction transaction = new Transaction();
        PaymentChecker paymentChecker = new PaymentChecker();
        String key = "";
        String checksumAlgorithm = "";
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        String memberid = "";
        int refundAllowedDays = 180;
        String captureAmount = "";
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        RefundChecker refundChecker = new RefundChecker();
        MerchantDetailsVO merchantDetailsVO = directRefundValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO transDetailsVO = directRefundValidatorVO.getTransDetailsVO();

        try
        {
            directRefundValidatorVO = validateMandatoryParameters(directRefundValidatorVO);

            merchantDetailsVO = directRefundValidatorVO.getMerchantDetailsVO();
            if (!functions.isEmptyOrNull(directRefundValidatorVO.getErrorMsg()))
            {
                ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                errorCodeVO.setErrorReason(directRefundValidatorVO.getErrorMsg());
                errorCodeListVO.addListOfError(errorCodeVO);
                directRefundValidatorVO.setErrorCodeListVO(errorCodeListVO);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "Transaction", directRefundValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            memberid = merchantDetailsVO.getMemberId();
            String trackingIds = directRefundValidatorVO.getTrackingid();//comma seperated trckingids
            String refundamounts = directRefundValidatorVO.getRefundAmount(); //comma separated refund amount in sam sequesnce as description
            String reasons = directRefundValidatorVO.getRefundReason(); //comma separated refund reason in sam sequesnce as description
            String checksum = transDetailsVO.getChecksum();
            String merchantIpaddress = Functions.getIpAddress(req);
            if (isLogEnabled)
                logger.debug("ipaddress..." + merchantIpaddress);

            if (!paymentChecker.isIpWhitelistedForMember(merchantDetailsVO, merchantIpaddress))
            {
                status.append("N");
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "Transaction", ErrorMessages.WHITELIST_IP, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if (!refundChecker.isRefundAllowed(memberid))
            {
                status.append("N");
                errorCodeListVO = getErrorVO(ErrorName.SYS_REFUND_ALLOWED);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "Transaction", ErrorMessages.REFUND_ALLOWED, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            if (!Functions.isValidSQL(trackingIds) || !Functions.isValidSQL(refundamounts) || !Functions.isValidSQL(reasons))
            {
                status.append("N");
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALIDTRANSACTION_DATA);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "Transaction", ErrorMessages.INVALID_INPUT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            key = merchantDetailsVO.getKey();
            checksumAlgorithm = transDetailsVO.getChecksum();
            refundAllowedDays = Integer.parseInt(merchantDetailsVO.getRefundAllowedDays());

            if (transactionError.generateMD5ChecksumForRefund(memberid, trackingIds, refundamounts, key).equals(checksum))
            {
                String trackingIdArray[] = trackingIds.split(",");
                String refundAmountArray[] = refundamounts.split(",");
                String refundReasonArray[] = reasons.split(",");

                if (refundAmountArray.length != trackingIdArray.length)
                {
                    status.append("N");
                    errorCodeListVO = getErrorVO(ErrorName.SYS_MULTIPLE_RFAMT_CHECK);
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "Transaction", ErrorMessages.NOT_MATCH_REFAMT_TRACKINGID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }

                if (refundReasonArray.length != trackingIdArray.length)
                {
                    status.append("N");
                    errorCodeListVO = getErrorVO(ErrorName.SYS_MULTIPLE_RFRESON_CHECK);
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "Transaction", ErrorMessages.NOT_MATCH_REFREASON_TRACKINGID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                }

                StringBuffer statusBuffer = new StringBuffer("");
                StringBuffer statusMessageBuffer = new StringBuffer("");
                for (int i = 0; i < trackingIdArray.length; i++)
                {
                    String stat = "";
                    String statMessage = "";
                    String trackingId = trackingIdArray[i];
                    String refundAmt = refundAmountArray[i];
                    BigDecimal refAmt = new BigDecimal(refundAmt);
                    refAmt = refAmt.setScale(2, BigDecimal.ROUND_DOWN);
                    String reversedAmount = "";
                    String transStatus = "";

                    String refReason = refundReasonArray[i];
                    String accountId = "";
                    Hashtable commHash = transaction.getCaptureTransactionCommon(trackingId, memberid);
                    if (!commHash.isEmpty())
                    {
                        accountId = (String) commHash.get("accountid");
                        reversedAmount = (String) commHash.get("refundamount");
                        transStatus = (String) commHash.get("status");
                        captureAmount = (String) commHash.get("captureamount");
                        String currency = "";
                        if (functions.isValueNull((String) commHash.get("currency")))
                        {
                            currency = (String) commHash.get("currency");
                        }
                        else
                        {
                            currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
                        }
                        if (currency.equals("JPY"))
                        {
                            if (!paymentChecker.isAmountValidForJPY(currency, refundAmt))
                            {
                                status.append("N");
                                errorCodeListVO = getErrorVO(ErrorName.SYS_JPY_CURRENCY_CHECK);
                                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "Transaction", ErrorMessages.JPY_CURRENCY, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                            }
                        }
                        else
                        {
                            if (!Functions.checkAccuracy(refAmt.toString(), 2))
                            {
                                status.append("N");
                                errorCodeListVO = getErrorVO(ErrorName.VALIDATION_AMOUNT);
                                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "Transaction", ErrorMessages.INVALID_AMOUNT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                            }
                        }

                        String transactionDate = (String) commHash.get("transactiondate");
                        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        long d = Functions.DATEDIFF(transactionDate, targetFormat.format(new Date()));
                        if (d > refundAllowedDays)
                        {
                            status.append("N");
                            errorCodeListVO = getErrorVO(ErrorName.REFUND_ALLOWEDDAYS_VALIDATION);
                            PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "Transaction", ErrorMessages.REFUND_ALLOWEDDAYS_VALIDATION, PZConstraintExceptionEnum.INVALID_REFUND_REQUEST, errorCodeListVO, null, null);
                        }

                        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountId));

                        PZRefundRequest refundRequest = new PZRefundRequest();
                        refundRequest.setAccountId(Integer.valueOf(accountId));
                        refundRequest.setTrackingId(Integer.valueOf(trackingId));
                        refundRequest.setMemberId(Integer.valueOf(memberid));
                        refundRequest.setRefundAmount(refundAmt);
                        refundRequest.setCaptureAmount(captureAmount);
                        refundRequest.setCurrency(currency);
                        refundRequest.setRefundReason(refReason);
                        refundRequest.setIpAddress(merchantIpaddress);
                        refundRequest.setTransactionStatus(transStatus);
                        refundRequest.setReversedAmount(reversedAmount);
                        refundRequest.setTransactionStatus((String) commHash.get("transactionstatus"));
                        //newly added
                        auditTrailVO.setActionExecutorId(memberid);
                        auditTrailVO.setActionExecutorName("Reverse DK");
                        refundRequest.setAuditTrailVO(auditTrailVO);
                        if("N".equalsIgnoreCase(merchantDetailsVO.getMultipleRefund()) && transStatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString())){
                            statMessage="Multiple Refund is not allowed.";
                        }else {
                            PZRefundResponse refundResponse = paymentProcess.refund(refundRequest);
                            PZResponseStatus responseStatus = refundResponse.getStatus();
                            String refundDescription = refundResponse.getResponseDesceiption();
                            if (PZResponseStatus.SUCCESS.equals(responseStatus))
                            {
                                stat = "Y";
                                statusSyncDAO.updateAllRefundTransactionFlowFlag(trackingId, "reversed");

                            }
                            else
                            {
                                stat = "N";
                            }
                            statMessage = refundDescription;
                        }
                        if ("Y".equalsIgnoreCase(merchantDetailsVO.getEmailSent()) && "Y".equalsIgnoreCase(merchantDetailsVO.getIsRefundEmailSent()))
                        {
                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendEmail(MailEventEnum.REFUND_TRANSACTION, trackingId, statMessage, refReason, null);
                        }
                        AsynchronousSmsService smsService = new AsynchronousSmsService();
                        smsService.sendSMS(MailEventEnum.REFUND_TRANSACTION, trackingId, statMessage, refReason, null);
                    }
                    else
                    {
                        stat = "N";
                        statMessage = "Transaction not found";
                    }
                    statusBuffer.append(stat).append(",");
                    statusMessageBuffer.append(statMessage).append(",");
                }
                statusBuffer.deleteCharAt(statusBuffer.length() - 1);
                statusMessageBuffer.deleteCharAt(statusMessageBuffer.length() - 1);
                transactionError.calCheckSumAndWriteStatusForRefund(pWriter, trackingIds, refundamounts, statusBuffer, statusMessageBuffer, key, checksumAlgorithm, merchantDetailsVO);
            }
            else
            {
                status.append("N");
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "transaction", "Checksum- Illegal Access.", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("PZConstraintViolationException in SingleCallGenericReverse---", cve);
            PZExceptionHandler.handleCVEException(cve, memberid, PZOperations.DIRECTKIT_REFUND);
            statusMsg.append(errorCodeUtils.getSystemErrorCodeVOForDKIT(cve.getPzConstraint().getErrorCodeListVO()));
            status.append("N");
            transactionError.calCheckSumAndWriteStatusForRefund(pWriter, "", "", status, statusMsg, key, checksumAlgorithm, null);
            return;
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("PZDBViolationException in SingleCallGenericReverse---", dbe);
            PZExceptionHandler.handleDBCVEException(dbe, memberid, PZOperations.DIRECTKIT_REFUND);
            statusMsg.append("Internal Errror Occured : Please contact Customer support for further help");
            status.append("N");
            transactionError.calCheckSumAndWriteStatusForRefund(pWriter, "", "", status, statusMsg, key, checksumAlgorithm, null);
            return;
        }
        catch (PZTechnicalViolationException tve)
        {
            logger.error("PZTechnicalViolationException in SingleCallGenericReverse---", tve);
            PZExceptionHandler.handleTechicalCVEException(tve, memberid, PZOperations.DIRECTKIT_REFUND);
            statusMsg.append("Internal Errror Occured : Please contact Customer support for further help");
            status.append("N");
            transactionError.calCheckSumAndWriteStatusForRefund(pWriter, "", "", status, statusMsg, key, checksumAlgorithm, null);
            return;
        }
        catch (SystemError se)
        {
            logger.error("SystemError in SingleCallGenericReverse---", se);
            transactionLogger.error("SystemError in SingleCallGenericReverse---", se);
            PZExceptionHandler.raiseAndHandleGenericViolationException("SingleCallGenericReverse.java", "singleCallReverse()", null, "Transaction", "Internal Error : Please contact Support", null, se.getMessage(), se.getCause(), memberid, PZOperations.DIRECTKIT_REFUND);
        }
    }

    private DirectRefundValidatorVO validateMandatoryParameters(DirectRefundValidatorVO directRefundValidatorVO) throws PZDBViolationException
    {
        InputValidator inputValidator=new InputValidator();
        String error ="";
        GenericTransDetailsVO genericTransDetailsVO=directRefundValidatorVO.getTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO=directRefundValidatorVO.getMerchantDetailsVO();
        Functions functions = new Functions();
        ErrorCodeVO errorCodeVO = null;
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        String toid=merchantDetailsVO.getMemberId();

        if (!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length()>10 || !ESAPI.validator().isValidInput("toid",toid,"Numbers",10,false))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
            error = errorCodeVO.getErrorCode()+" "+ ErrorMessages.INVALID_TOID;
            directRefundValidatorVO.setErrorMsg(error);
            return directRefundValidatorVO;
        }
        else
        {
            merchantDetailsVO = getMerchantConfigDetails(toid);
            if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                error = errorCodeVO.getErrorCode() + " " + ErrorMessages.MISCONFIGURED_TOID;
                directRefundValidatorVO.setErrorMsg(error);
                return directRefundValidatorVO;
            }
        }

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.add(InputFields.TRACKINGID);
        inputMandatoryFieldsList.add(InputFields.TOID);
        inputMandatoryFieldsList.add(InputFields.AMOUNT);
        inputMandatoryFieldsList.add(InputFields.REASON);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(directRefundValidatorVO, inputMandatoryFieldsList, errorList, false);

        for(ValidationException validationException:errorList.errors())
        {
            PZValidationException pzValidationException=(PZValidationException)validationException;
            errorCodeVO = pzValidationException.getErrorCodeVO();
            error= error + errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + "," + errorCodeVO.getErrorReason()+ " | ";
        }

        if (functions.isValueNull(error))
            directRefundValidatorVO.setErrorMsg(error);
        directRefundValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        return directRefundValidatorVO;
    }
    private ErrorCodeListVO getErrorVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }

    protected MerchantDetailsVO getMerchantConfigDetails(String toid) throws PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();

        merchantDetailsVO = merchantDAO.getMemberDetails(toid);

        return merchantDetailsVO;
    }
}
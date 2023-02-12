package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.GatewayType;
import com.directi.pg.core.GatewayTypeService;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.logicboxes.util.ApplicationProperties;
import com.manager.dao.MerchantDAO;
import com.manager.enums.ResponseLength;
import com.manager.vo.MerchantDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PaymentProcessFactory;
import com.payment.checkers.PaymentChecker;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.request.PZCaptureRequest;
import com.payment.response.PZCaptureResponse;
import com.payment.response.PZResponseStatus;
import com.payment.sms.AsynchronousSmsService;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import payment.util.ReadRequest;
import payment.util.ReadXMLRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class SingleCallGenericCaptureServlet extends PzServlet
{
    private static final int MAX_CAPTURE_TRANSACTIONS = 10;

    private static Logger logger = new Logger(SingleCallGenericCaptureServlet.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SingleCallGenericCaptureServlet.class.getName());
    boolean isLogEnabled = Boolean.parseBoolean(ApplicationProperties.getProperty("IS_LOG_ENABLED"));

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Date date72 = new Date();
        transactionLogger.debug("CommonPaymentProcess send transaction maill start start time 72########" + date72.getTime());
        PrintWriter pWriter = response.getWriter();
        TransactionUtility transactionError = new TransactionUtility();
        response.setContentType("text/plain");
        //Hashtable requestHash=null;
        String rType = request.getParameter("requesttype");
        StringBuffer statusDesc = new StringBuffer();
        StringBuffer status = new StringBuffer();
        CommonValidatorVO commonValidatorVO = null;


        try
        {
            if(rType!=null && !rType.equals("") && rType.equalsIgnoreCase("XML"))
            {
                String XMLData = request.getParameter("data");
                if(XMLData==null || XMLData.equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericReverse.java", "doPost()", null, "Transaction", ErrorMessages.INVALID_INPUT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null, null, null);
                }
                commonValidatorVO = ReadXMLRequest.readXmlRequestForCapture(XMLData);
                singleCallCapture(commonValidatorVO,request,response,pWriter);
            }
            else
            {
                commonValidatorVO = ReadRequest.getRequestParametersForCapture(request);
                singleCallCapture(commonValidatorVO,request,response,pWriter);
            }
        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("PZConstraintViolationException in SingleCallGenericCapture---",cve);
            PZExceptionHandler.handleCVEException(cve,null, PZOperations.DIRECTKIT_CAPTURE);
            status.append("N");
            statusDesc.append("Invalid Data");
            transactionError.calCheckSumAndWriteStatusForCapture(pWriter, "", "", status, statusDesc, "", "",null);
            return;
        }

        transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail end time 72########" + new Date().getTime());
        transactionLogger.debug("CommonPaymentProcess sendTransactionEventMail diff time 72########" + (new Date().getTime() - date72.getTime()));
    }

    public void singleCallCapture(CommonValidatorVO commonValidatorVO, HttpServletRequest request, HttpServletResponse response, PrintWriter pWriter)throws ServletException,IOException
    {
        StringBuffer status = new StringBuffer("");
        StringBuffer statusMsg = new StringBuffer("");
        String error="";
        String key = "";
        String checksumAlgorithm = "";
        TransactionEntry transactionEntry = new TransactionEntry();
        TransactionUtility transactionError = new TransactionUtility();
        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        Functions functions=new Functions();
        Transaction transaction = new Transaction();
        Merchants merchants = new Merchants();
        PaymentChecker paymentChecker = new PaymentChecker();
        String  requesttype=request.getParameter("requesttype");
        String memberid = "";
        String partnerId="";
        String responseType=null;
        String responseLength=null;
        StringBuffer statusBuffer = new StringBuffer("");
        StringBuffer statusMessageBuffer = new StringBuffer("");
        //Hashtable transaDetails = new Hashtable();
        AuditTrailVO auditTrailVO=new AuditTrailVO();

        try
        {
            commonValidatorVO = validateMandatoryParameters(commonValidatorVO);
            if (!functions.isEmptyOrNull(commonValidatorVO.getErrorMsg()))
            {
                ErrorCodeVO errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(commonValidatorVO.getErrorMsg());
                errorCodeListVO.addListOfError(errorCodeVO);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericCaptureServlet.java", "singleCallCapture()", null, "Transaction",commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null,null);
            }

            MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();

            memberid =merchantDetailsVO.getMemberId();
            String trackingIds =commonValidatorVO.getTrackingid();         //comma seperated trckingid
            String captureamounts =commonValidatorVO.getTransDetailsVO().getAmount();  //comma separated capture amount in sam sequesnce as description
            String checksum =commonValidatorVO.getTransDetailsVO().getChecksum();
            String ipAddress = Functions.getIpAddress(request);
            String bankStatus="";
            String captureCode="";
            String resultCode="";
            String resultDescription="";
            String isExcessCaptureAllowed="";

            PZCaptureResponse captureResponse=null;

            if(!paymentChecker.isIpWhitelistedForMember(merchantDetailsVO,ipAddress))
            {
                error = "Merchant's IP is not white listed with us. Kindly Contact the pz Support Desk.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericCaptureServlet.java", "singleCallCapture()", null, "Transaction", ErrorMessages.WHITELIST_IP, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
            }

            if (merchantDetailsVO != null)
            {
                key = (String) merchantDetailsVO.getKey();
                checksumAlgorithm = merchantDetailsVO.getChecksumAlgo();
                isExcessCaptureAllowed=merchantDetailsVO.getIsExcessCaptureAllowed();
            }
            else
            {
                errorCodeListVO = getErrorVO(ErrorName.VALIDATION_TOID);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericCaptureServlet.java", "singleCallCapture()", null, "Transaction", ErrorMessages.WHITELIST_IP, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            responseType=merchantDetailsVO.getResponseType();
            responseLength=merchantDetailsVO.getResponseLength();

            if (Functions.verifyChecksumV2(memberid, trackingIds, captureamounts, checksum, key, checksumAlgorithm))
            {
                String trackingIdArray[] = trackingIds.split(",");
                if (trackingIdArray.length > MAX_CAPTURE_TRANSACTIONS)
                {
                    errorCodeListVO = getErrorVO(ErrorName.SYS_CAPTURE_TRANS_LIMIT_CHECK);
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericCaptureServlet.java", "singleCallCapture()", null, "Transaction", ErrorMessages.CAPTURE_TRANSACTION_LIMIT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
                }
                String captureAmountArray[] = captureamounts.split(",");
                if (captureAmountArray.length != trackingIdArray.length)
                {
                    errorCodeListVO = getErrorVO(ErrorName.SYS_MULTIPLE_CPTRANS_CHECK);
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericCaptureServlet.java", "singleCallCapture()", null, "Transaction", ErrorMessages.NOT_MATCH_CAPTUREAMT_TRACKINGID, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
                }
                for (int i = 0; i < trackingIdArray.length; i++)
                {
                    String stat = "";
                    String statMessage = "";
                    String trackingId = trackingIdArray[i];
                    String captureAmt = captureAmountArray[i];


                    Hashtable authTransactionHash = transaction.getAuthTransactionCommon(memberid,trackingId);

                    if(!authTransactionHash.isEmpty())
                    {
                        String amount = (String) authTransactionHash.get("amount");
                        String accountId = (String) authTransactionHash.get("accountid");
                        String currency="";
                        if(functions.isValueNull((String)authTransactionHash.get("currency")))
                        {
                             currency = (String) authTransactionHash.get("currency");
                        }
                        else
                        {
                             currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
                        }

                        if(currency.equals("JPY"))
                        {
                            if(!paymentChecker.isAmountValidForJPY(currency,captureAmt))
                            {
                                errorCodeListVO = getErrorVO(ErrorName.SYS_JPY_CURRENCY_CHECK);
                                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericCaptureServlet.java", "singleCallCapture()", null, "Transaction", ErrorMessages.JPY_CURRENCY, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO,null, null);
                            }
                        }
                        else
                        {
                            if (!Functions.checkAccuracy(captureAmt, 2))
                            {
                                errorCodeListVO = getErrorVO(ErrorName.VALIDATION_AMOUNT);
                                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericCaptureServlet.java", "singleCallCapture()", null, "Transaction", ErrorMessages.INVALID_AMOUNT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
                            }
                        }

                        boolean isOverCaptureRequest=isOverCaptureRequest(captureAmt,amount);
                        if (isLogEnabled)
                            logger.debug("over capture request?="+isOverCaptureRequest);
                        boolean isValidCaptureAmount=true;
                        boolean isOverCaptureAllowed=false;
                        boolean isValidCaptureRequest=true;

                        if("Y".equals(isExcessCaptureAllowed))
                        {
                            isOverCaptureAllowed=true;
                        }
                        if (isLogEnabled)
                            logger.debug("is over capture allowed to merchant?="+isOverCaptureAllowed);
                        if(isOverCaptureRequest)
                        {
                            if(isOverCaptureAllowed)
                            {
                                GatewayType gatewayType=GatewayTypeService.getGatewayType(GatewayAccountService.getGatewayAccount(accountId).getPgTypeId());
                                String overCapturePercentage=gatewayType.getExcessCapturePercentage();
                                double validOverCaptureAmount=(Double.valueOf(amount)*Double.valueOf(overCapturePercentage))/100;
                                if(Double.valueOf(captureAmt)>validOverCaptureAmount)
                                {
                                    isValidCaptureAmount=false;
                                }
                            }
                            else
                            {
                                isValidCaptureRequest=false;
                            }
                        }
                        if (isLogEnabled)
                            logger.debug("is valid capture amount?="+isValidCaptureAmount);
                        if(isValidCaptureRequest && isValidCaptureAmount)
                        {
                            AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(trackingId), Integer.parseInt(accountId));

                            PZCaptureRequest captureRequest = new PZCaptureRequest();
                            captureRequest.setAccountId(Integer.valueOf(accountId));
                            captureRequest.setMemberId(Integer.valueOf(memberid));
                            captureRequest.setTrackingId(Integer.valueOf(trackingId));
                            captureRequest.setAmount(Double.valueOf(captureAmt));
                            captureRequest.setIpAddress(ipAddress);
                            captureRequest.setCurrency(currency);
                            captureRequest.setPod("Capture Transaction");
                            //ADDed new

                            auditTrailVO.setActionExecutorId(memberid);
                            auditTrailVO.setActionExecutorName("Capture DK");
                            captureRequest.setAuditTrailVO(auditTrailVO);

                            captureResponse = paymentProcess.capture(captureRequest);
                            PZResponseStatus responseStatus = captureResponse.getStatus();
                            String captureDescription = captureResponse.getResponseDesceiption();

                            if(captureResponse.getBankStatus()!=null)
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
                            }
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
                        else if(!isOverCaptureAllowed)
                        {
                            stat = "N";
                            statMessage = "Over capture not activated to your account.";
                        }
                        else
                        {
                            stat = "N";
                            statMessage = "Invalid over capture amount";
                        }
                    }
                    else
                    {
                        stat = "N";
                        statMessage = "Transaction not found";
                    }
                    statusBuffer.append(stat).append(",");
                    statusMessageBuffer.append(statMessage).append(",");

                    if ("Y".equalsIgnoreCase(merchantDetailsVO.getEmailSent()))
                    {
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        asynchronousMailService.sendEmail(MailEventEnum.CAPTURE_TRANSACTION, trackingId, statMessage, statMessage, null);
                    }
                    AsynchronousSmsService smsService = new AsynchronousSmsService();
                    smsService.sendSMS(MailEventEnum.CAPTURE_TRANSACTION, trackingId, statMessage, statMessage, null);

                }
                statusBuffer.deleteCharAt(statusBuffer.length() - 1);
                statusMessageBuffer.deleteCharAt(statusMessageBuffer.length() - 1);
                transactionEntry.closeConnection();
                if(ResponseLength.FULL.toString().equals(responseLength))
                {
                    transactionError.calCheckSumAndWriteFullResponseForCapture(pWriter,trackingIds,captureamounts,statusBuffer,statusMessageBuffer,bankStatus,resultCode,resultDescription,captureCode,key,checksumAlgorithm,requesttype,responseType);
                    return;
                }
                else
                {
                    transactionError.calCheckSumAndWriteStatusForCapture(pWriter, trackingIds, captureamounts, statusBuffer, statusMessageBuffer, key, checksumAlgorithm,merchantDetailsVO);
                    return;
                }
            }
            else
            {
                String error1 =  "Checksum Illegal Access.";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericCaptureServlet.class", "singleCallCapture()", null, "Common", error1, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("PZConstraintViolationException in SingleCallGenericCapture---",cve);
            transactionLogger.error("PZConstraintViolationException in SingleCallGenericCapture---",cve);
            PZExceptionHandler.handleCVEException(cve, memberid, PZOperations.DIRECTKIT_CAPTURE);
            statusMsg.append(errorCodeUtils.getSystemErrorCodeVOForDKIT(cve.getPzConstraint().getErrorCodeListVO()));
            //System.out.println("");
            status.append("N");
            if (ResponseLength.FULL.toString().equals(responseLength))
            {
                StringBuffer errorDescription = new StringBuffer(errorCodeListVO.getListOfError().get(0).getErrorDescription());
                transactionError.calCheckSumAndWriteFullResponseForCapture(pWriter,"","",statusBuffer,errorDescription,"","","","",key,checksumAlgorithm,requesttype,responseType);
                return;
            }
            else
            {
                transactionError.calCheckSumAndWriteStatusForCapture(pWriter, "", "", status, statusMsg, key, checksumAlgorithm,null);
                return;
            }
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("PZDBViolationException in SingleCallGenericReverse---",dbe);
            transactionLogger.error("PZDBViolationException in SingleCallGenericReverse---",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, memberid, PZOperations.DIRECTKIT_CAPTURE);
            statusMsg.append("Internal Errror Occured : Please contact Customer support for further help");
            status.append("N");
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                transactionError.calCheckSumAndWriteFullResponseForCapture(pWriter,"","",statusBuffer,statusMessageBuffer,"","","","",key,checksumAlgorithm,requesttype,responseType);
                return;
            }
            else
            {
                transactionError.calCheckSumAndWriteStatusForCapture(pWriter, "", "", status, statusMsg, key, checksumAlgorithm,null);
                return;
            }

        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("NoSuchAlgorithmException" + e);
            transactionLogger.error("NoSuchAlgorithmException" + e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SingleCallGenericStatus", "singleCallStatus()", null, "Transaction", "No Such Algorithm Exception", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null, e.getMessage(), e.getCause(), memberid, PZOperations.DIRECTKIT_CAPTURE);
            statusMsg.append("No Such Algorithm Exception");
            status.append("N");
            if(ResponseLength.FULL.toString().equals(responseLength))
            {
                transactionError.calCheckSumAndWriteFullResponseForCapture(pWriter,"","",statusBuffer,statusMessageBuffer,"","","","",key,checksumAlgorithm,requesttype,responseType);
                return;
            }
            else
            {
                transactionError.calCheckSumAndWriteStatusForCapture(pWriter, "", "", status, statusMsg, key, checksumAlgorithm,null);
                return;
            }

        }
    }

    private CommonValidatorVO validateMandatoryParameters(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        InputValidator inputValidator=new InputValidator();
        String error ="";
        GenericTransDetailsVO genericTransDetailsVO=commonValidatorVO.getTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        Functions functions = new Functions();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();

        String toid=merchantDetailsVO.getMemberId();
        if (!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length()>10 || !ESAPI.validator().isValidInput("toid",toid,"Numbers",10,false))
        {
            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TOID);
            error = errorCodeVO.getErrorCode()+" "+ ErrorMessages.INVALID_TOID;
            commonValidatorVO.setErrorMsg(error);
            return commonValidatorVO;
        }
        else
        {
            merchantDetailsVO = getMerchantConfigDetails(toid);
            if (merchantDetailsVO == null || merchantDetailsVO.getMemberId() == null)
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_TOID_INVALID);
                error = errorCodeVO.getErrorCode() + " " + ErrorMessages.MISCONFIGURED_TOID;
                commonValidatorVO.setErrorMsg(error);
                return commonValidatorVO;
            }
        }

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.add(InputFields.TRACKINGID);
        inputMandatoryFieldsList.add(InputFields.CHECKSUM);
        inputMandatoryFieldsList.add(InputFields.TOID);
        inputMandatoryFieldsList.add(InputFields.AMOUNT);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
        if(!errorList.isEmpty())
        {
            for (InputFields inputFields : inputMandatoryFieldsList)
            {

                if (errorList.getError(inputFields.toString()) != null)
                {
                    PZValidationException pzValidationException = (PZValidationException) errorList.getError(inputFields.toString());
                    errorCodeVO = pzValidationException.getErrorCodeVO();
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error= error + errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription()+" | ";
                    commonValidatorVO.setErrorMsg(error);

                }
            }
        }

        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        return commonValidatorVO;
    }
    private ErrorCodeListVO getErrorVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();
        errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        //errorCodeVO.setErrorReason(reason);
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
    private boolean isOverCaptureRequest(String captureAmount,String authAmount)
    {
        boolean isOverCaptureRequest=false;
        if (new BigDecimal(captureAmount).compareTo(new BigDecimal(authAmount)) > 0)
        {
            isOverCaptureRequest=true;
        }
        return isOverCaptureRequest;
    }
}
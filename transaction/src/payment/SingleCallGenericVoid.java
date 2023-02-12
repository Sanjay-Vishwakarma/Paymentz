package payment;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.dao.MerchantDAO;
import com.manager.enums.ResponseLength;
import com.manager.vo.MerchantDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.PZTransactionStatus;
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
import com.payment.request.PZCancelRequest;
import com.payment.response.PZCancelResponse;
import com.payment.response.PZResponseStatus;
import com.payment.sbm.core.SBMPaymentGateway;
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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Admin on 5/21/2015.
 */
public class SingleCallGenericVoid extends PzServlet
{
    private static Logger log = new Logger(SingleCallGenericVoid.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SingleCallGenericVoid.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        Date date72 = new Date();
        TransactionUtility transactionError = new TransactionUtility();
        PrintWriter pWriter = res.getWriter();
        //Hashtable requestHash = null;
        CommonValidatorVO commonValidatorVO = null;

        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        String responseType = merchantDetailsVO.getResponseType();
        String responseLength = merchantDetailsVO.getResponseLength();

        String requestType = req.getParameter("requesttype");

        try
        {
            if(requestType!=null && !requestType.equals("") && "XML".equalsIgnoreCase(requestType))
            {
                String XMLData = req.getParameter("data");
                if(XMLData==null || XMLData.equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericStatus.java", "doPost()", null, "Transaction", ErrorMessages.INVALID_INPUT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, null, null, null);
                }

                commonValidatorVO = ReadXMLRequest.readXmlRequestForStatus(XMLData);
                singleCallVoid(commonValidatorVO, req, res, pWriter);
            }
            else
            {
                commonValidatorVO = ReadRequest.getRequestParametersForStatus(req);
                singleCallVoid(commonValidatorVO, req, res, pWriter);
            }
        }
        catch (PZConstraintViolationException cve)
        {
            transactionError.calCheckSumAndWriteStatusForVoid(pWriter, "", "0", "N", "Invalid Data", null, null, requestType,responseType);
            return;
        }
    }

    public void singleCallVoid(CommonValidatorVO commonValidatorVO, HttpServletRequest request, HttpServletResponse response, PrintWriter pWriter) throws ServletException, IOException
    {
        String statusMsg="";
        String status="";
        TransactionUtility transactionError = new TransactionUtility();
        String toid="";
        String trackingid="";
        String key="";
        String checksumAlgorithm="";
        String description="";
        String requesttype = request.getParameter("requesttype");
        String activation="";
        Transaction transaction = new Transaction();
        Merchants merchants = new Merchants();
        PaymentChecker paymentChecker = new PaymentChecker();
        TransactionUtility transactionUtility =  new TransactionUtility();
        String calchecksum="";
        String checksum="";
        Hashtable data=new Hashtable();
        String accountId="";
        String fromtype="";
        PZCancelRequest cancelRequest;
        PZCancelResponse res=null;
        String transactionStatus="";
        ErrorCodeListVO errorCodeListVO=new ErrorCodeListVO();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        Functions functions=new Functions();
        String bankStatus="";
        String resultCode="";
        String resultDescription="";
        String responseType="";
        String responseLength="";
        String partnerId="";
        String amount = "";
        String terminalId = "";
        AuditTrailVO auditTrailVO= new AuditTrailVO();
        try
        {
            Date date72 = new Date();
            commonValidatorVO = validateMandatoryParameters(commonValidatorVO);
            if (!functions.isEmptyOrNull(commonValidatorVO.getErrorMsg()))
            {
                ErrorCodeVO errorCodeVO=new ErrorCodeVO();
                errorCodeVO.setErrorReason(commonValidatorVO.getErrorMsg());
                errorCodeListVO.addListOfError(errorCodeVO);
                statusMsg = errorCodeUtils.getSystemErrorCodeVOForDKIT(errorCodeListVO);
                status = "N";
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericVoid.java","singleCallVoid()",null,"Transaction",commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null,null);

            }

            MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
            GenericTransDetailsVO transDetailsVO = commonValidatorVO.getTransDetailsVO();


            description = transDetailsVO.getOrderId();
            trackingid = commonValidatorVO.getTrackingid();
            toid = merchantDetailsVO.getMemberId();
            String ipaddress = Functions.getIpAddress(request);
            checksum= transDetailsVO.getChecksum();
            key = merchantDetailsVO.getKey();

            responseType=merchantDetailsVO.getResponseType();
            responseLength=merchantDetailsVO.getResponseLength();

            if(!paymentChecker.isIpWhitelistedForMember(merchantDetailsVO,ipaddress))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_IPWHITELIST_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericVoid.java", "singleCallVoid()", null, "Transaction", ErrorMessages.WHITELIST_IP, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
            }
            if(!merchantDetailsVO.getActivation().equals("Y"))
            {
                errorCodeListVO = getErrorVO(ErrorName.SYS_MEMBER_ACTIVATION_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericVoid.java", "singleCallVoid()", null, "transaction", null, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
            }

            calchecksum = transactionUtility.generateStatusChecksum(toid, description, trackingid, key);
            //System.out.println("calchecksum----"+calchecksum);
            //System.out.println("checksum----"+checksum);
            if(!checksum.equals(calchecksum))
            {
                String error1 =  "Checksum- Illegal Access. CheckSum mismatch";
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALID_CHECKSUM);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericVoid.java", "singleCallVoid()", null, "transaction", error1, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null, null);
            }
            data = transaction.getTransactionDetailsForCommon(trackingid,description,toid);
            if( data==null || data.size()==0)
            {
                String error1="Transaction not found for tracking ID"+trackingid+" and description "+description;
                errorCodeListVO = getErrorVO(ErrorName.SYS_INVALIDTRANSACTION_DATA);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericVoid.java", "singleCallVoid()", null, "transaction", error1, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null, null);
            }
            accountId = (String)data.get("accountid");
            amount = String.valueOf(data.get("amount"));
            transactionStatus= (String)data.get("status");
            terminalId = String.valueOf(data.get("terminalid"));
            if(PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(transactionStatus))
            {
                String error1 = "Captured transaction can not be cancel for trackingid "+trackingid;
                errorCodeListVO = getErrorVO(ErrorName.SYS_VOID_TRANSACTION_NOTALLOWED);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericVoid.java", "singleCallVoid()", null, "transaction", error1, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
           if(PZTransactionStatus.AUTH_CANCELLED.toString().equals(transactionStatus))
            {
                String error1 = "Transaction already cancelled";
                errorCodeListVO = getErrorVO(ErrorName.SYS_TRANSACTION_ALERDY_CANCELLED);
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericVoid.java", "singleCallVoid()", null, "transaction", error1, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
            fromtype = GatewayAccountService.getGatewayAccount(accountId).getGateway();
            if (Functions.checkAPIGateways(fromtype))
            {
                cancelRequest = new PZCancelRequest();
                cancelRequest.setMemberId(Integer.parseInt(toid));
                cancelRequest.setAccountId(Integer.parseInt(accountId));
                cancelRequest.setTrackingId(Integer.parseInt(trackingid));
                cancelRequest.setIpAddress(ipaddress);
                cancelRequest.setAmount(amount);
                cancelRequest.setTerminalId(terminalId);
                cancelRequest.setCancelReason("Cancel Transaction " + trackingid);
                AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(accountId));


                auditTrailVO.setActionExecutorId(toid);
                auditTrailVO.setActionExecutorName("Cancel DK");
                cancelRequest.setAuditTrailVO(auditTrailVO);
                if(fromtype != null && SBMPaymentGateway.GATEWAY_TYPE.equals(fromtype) && PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(transactionStatus))
                {
                    res = paymentProcess.cancelCapture(cancelRequest);
                }
                else
                {
                    Date date1 = new Date();
                    res = paymentProcess.cancel(cancelRequest);
                }

                log.debug("status cancel transaction----"+res.getStatus());
                if(res.getBankStatus()!=null)
                {
                   bankStatus=res.getBankStatus();
                }
                if(res.getResultCode()!=null)
                {
                   resultCode=res.getResultCode();
                }
                if(res.getResultDescription()!=null)
                {
                   resultDescription=res.getResultDescription();
                }

                PZResponseStatus status1 = res.getStatus();
                if (PZResponseStatus.ERROR.equals(status1))
                {
                    statusMsg = "Error while Cancel Transaction";
                    status = "N";
                   /* transactionError.calCheckSumAndWriteStatusForVoid(pWriter, description, String.valueOf(trackingid), status, statusMsg, key, checksumAlgorithm, requesttype);
                    return;*/
                }
                else if (PZResponseStatus.FAILED.equals(status1))
                {
                    statusMsg = "Transaction failed while cancel";
                    status = "N";
                    /*transactionError.calCheckSumAndWriteStatusForVoid(pWriter, description, String.valueOf(trackingid), status, statusMsg, key, checksumAlgorithm, requesttype);
                    return;*/
                }
                else if(PZResponseStatus.SUCCESS.equals(status1))
                {
                    //statusMsg="Cancel Transaction Process is successful";
                    statusMsg="Transaction has been successfully cancelled";
                    status = "Y";
                    /*transactionError.calCheckSumAndWriteStatusForVoid(pWriter, description, String.valueOf(trackingid), status, statusMsg, key, checksumAlgorithm, requesttype);
                    return;*/
                }
                else if(PZResponseStatus.PENDING.equals(status1))
                {
                    statusMsg=res.getResponseDesceiption();
                    status = "N";
                   /* transactionError.calCheckSumAndWriteStatusForVoid(pWriter, description, String.valueOf(trackingid), status, statusMsg, key, checksumAlgorithm, requesttype);
                    return;*/
                }
            }
        }
        catch (PZConstraintViolationException cve)
        {
            log.error("PZConstraintViolationException in SingleCallGenericVoid---",cve);
            PZExceptionHandler.handleCVEException(cve, toid, PZOperations.DIRECTKIT_VOID);
            statusMsg = errorCodeUtils.getSystemErrorCodeVOForDKIT(cve.getPzConstraint().getErrorCodeListVO());
            status = "N";
            /*transactionError.calCheckSumAndWriteStatusForVoid(pWriter, description,trackingid, status, statusMsg, key, checksumAlgorithm, requesttype);
            return;*/
        }
        catch (PZDBViolationException dbe)
        {
            log.error("PZDBViolationException in SingleCallGenericVoid---",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, toid, PZOperations.DIRECTKIT_CAPTURE);
            statusMsg = errorCodeUtils.getSystemErrorCodeVOForDKIT(dbe.getPzdbConstraint().getErrorCodeListVO());
            status = "N";
            /*transactionError.calCheckSumAndWriteStatusForVoid(pWriter, description,trackingid, status, statusMsg, key, checksumAlgorithm, requesttype);
            return;*/
        }
        catch (NoSuchAlgorithmException e)
        {
            log.error("NoSuchAlgorithmException"+e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SingleCallGenericStatus", "singleCallStatus()", null, "Transaction", "No Such Algorithm Exception", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause(), toid, PZOperations.DIRECTKIT_STATUS);
            status = "N";
            statusMsg = "ERROR:-: Invalid Merchant";
            /*transactionError.calCheckSumAndWriteStatusForVoid(pWriter, description, trackingid, status, statusMsg, key, checksumAlgorithm, requesttype);
            return;*/
        }

        if(ResponseLength.FULL.toString().equals(responseLength))
        {
            transactionError.calCheckSumAndWriteFullResponseForVoid(pWriter, description, trackingid, status, statusMsg,bankStatus,resultCode,resultDescription,key, checksumAlgorithm, requesttype,responseType);
            return;
        }
        else
        {
            transactionError.calCheckSumAndWriteStatusForVoid(pWriter, description, trackingid, status, statusMsg, key, checksumAlgorithm, requesttype,responseType);
            return;
        }

    }

    private CommonValidatorVO validateMandatoryParameters(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
        Date date72 = new Date();
        InputValidator inputValidator = new InputValidator();
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

                /*Transaction request rejected log entry with reason:Partner-PARTNER_MERCHANT_INVALID_CONFIGURATION*/
                // failedTransactionLogEntry.partnerMerchantMismatchInputEntry(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTransDetailsVO().getTotype(), commonValidatorVO.getRequestedIP(), commonValidatorVO.getAddressDetailsVO().getRequestedHost(), commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getRequestedHeader(), error, TransReqRejectCheck.PARTNER_MERCHANT_CONFIGURATION.toString(), commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress());
                commonValidatorVO.setErrorMsg(error);
                return commonValidatorVO;
            }
        }

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.add(InputFields.TRACKINGID);
        inputMandatoryFieldsList.add(InputFields.CHECKSUM);
        inputMandatoryFieldsList.add(InputFields.DESCRIPTION);
        inputMandatoryFieldsList.add(InputFields.TOID);

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
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error= error + errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription()+" | ";

                }
            }
            commonValidatorVO.setErrorMsg(error);
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);
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
}
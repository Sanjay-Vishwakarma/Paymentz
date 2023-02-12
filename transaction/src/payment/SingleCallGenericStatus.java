package payment;

import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.enums.ResponseLength;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.PaymentProcessFactory;
import com.payment.checkers.PaymentChecker;
import com.payment.common.core.CommInquiryResponseVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.exceptionHandler.operations.PZOperations;
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
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 1/9/13
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class SingleCallGenericStatus extends PzServlet
{
    private static Logger log = new Logger(SingleCallGenericStatus.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(SingleCallGenericStatus.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        TransactionUtility transactionError = new TransactionUtility();
        PrintWriter pWriter = res.getWriter();

        CommonValidatorVO commonValidatorVO = null;
        String responseType = "";
        String responseLength = "";

        String requestType = req.getParameter("requesttype");
        log.debug("===request type==="+requestType);
        transactionLogger.debug("===request type==="+requestType);
        try
        {
            if("XML".equalsIgnoreCase(requestType))
            {
                String XMLData = req.getParameter("data");
                if(XMLData==null || XMLData.equals(""))
                {
                    PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericStatus.java","doPost()",null,"Transaction", ErrorMessages.INVALID_INPUT, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
                }
                commonValidatorVO= ReadXMLRequest.readXmlRequestForStatus(XMLData);
                singleCallStatus(commonValidatorVO, req, res, pWriter);
            }
            else
            {
                commonValidatorVO = ReadRequest.getRequestParametersForStatus(req);
                singleCallStatus(commonValidatorVO, req, res, pWriter);
            }
        }
        catch (PZConstraintViolationException cve)
        {
            transactionError.calCheckSumAndWriteStatus(pWriter, "", "", "", "", "N", "Invalid Data", "", "", requestType,responseType);
            return;
        }
    }

    public void singleCallStatus(CommonValidatorVO commonValidatorVO, HttpServletRequest request, HttpServletResponse response, PrintWriter pWriter) throws ServletException, IOException
    {
        String description=null;
        String trackingid=null;
        String toid=null;
        String checksum=null;
        String status="";
        String statusMsg="";
        String key=null;
        String checksumAlgorithm=null;
        String accountId=null;
        Hashtable data=new Hashtable();

        String fromtype=null;
        AbstractPaymentGateway pg = null;
        String calchecksum=null;
        String activation=null;
        String partnerId=null;

        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        TransactionManager transactionManager=new TransactionManager();
        GenericTransDetailsVO transDetailsVO = commonValidatorVO.getTransDetailsVO();

        String responseType = merchantDetailsVO.getResponseType();
        String responseLenght = merchantDetailsVO.getResponseLength();

        TransactionUtility transactionUtility =  new TransactionUtility();
        TransactionUtility transactionError = new TransactionUtility();
        PaymentChecker paymentChecker = new PaymentChecker();
        Functions functions=new Functions();

        String requesttype = request.getParameter("requesttype");

        String ipaddress = Functions.getIpAddress(request);

        try
        {
            commonValidatorVO = validateMandatoryParameters(commonValidatorVO);
            if (functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericStatus.java","singleCallStatus()",null,"Transaction", ErrorMessages.INVALID_INPUT+"---"+commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
            transDetailsVO = commonValidatorVO.getTransDetailsVO();

            key = merchantDetailsVO.getKey();
            checksumAlgorithm = merchantDetailsVO.getChecksumAlgo();
            activation = merchantDetailsVO.getActivation();
            description = transDetailsVO.getOrderId();
            toid = merchantDetailsVO.getMemberId();
            trackingid = commonValidatorVO.getTrackingid();
            checksum = transDetailsVO.getChecksum();

            if(!"Y".equalsIgnoreCase(activation))
            {
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericStatus.java","singleCallStatus()",null,"Transaction", ErrorMessages.ACCOUNT_LIVE, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            commonValidatorVO=validateOptionalParameters(commonValidatorVO);
            if (functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericStatus.java","singleCallStatus()",null,"Transaction", ErrorMessages.INVALID_INPUT+"---"+commonValidatorVO.getErrorMsg(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null,null,null);
            }

            if(!paymentChecker.isIpWhitelistedForMember(merchantDetailsVO,ipaddress))
            {
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericStatus.java", "singleCallStatus()", null, "Transaction", ErrorMessages.WHITELIST_IP, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null, null, null);
            }

            if(!functions.isValueNull(description) && !functions.isValueNull(trackingid))
            {
                status="N";
                statusMsg="provide either Description or Trackingid both should not be empty";
                transactionError.calCheckSumAndWriteStatus(pWriter,description,trackingid,"","",status, statusMsg, key, checksumAlgorithm,requesttype,responseType);
                return;
            }

            calchecksum = transactionUtility.generateStatusChecksum(toid, description, trackingid, key);
            if(!checksum.equals(calchecksum))
            {
                PZExceptionHandler.raiseConstraintViolationException("SingleCallGenericStatus.java", "singleCallStatus()", null, "Transaction", ErrorMessages.CHECKSUM_MISMATCH, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null, null, null);
            }

            responseType=merchantDetailsVO.getResponseType();
            responseLenght=merchantDetailsVO.getResponseLength();

            List<TransactionDetailsVO> transactionDetailsVOList = transactionManager.getTransactionforStatusAPI("transaction_common",trackingid,description, toid);

            if(transactionDetailsVOList.size()==0)
            {
                //search transaction in common
                transactionDetailsVOList = transactionManager.getTransactionforStatusAPI("transaction_qwipi",trackingid,description, toid);

                if(transactionDetailsVOList.size()==0)
                {
                    status="N";
                    statusMsg="Your record not found";
                    transactionError.calCheckSumAndWriteStatus(pWriter,description,trackingid,"","",status, statusMsg, key, checksumAlgorithm,requesttype,responseType);
                    return;
                }
                else
                {
                    //set response for qwipi
                    for(TransactionDetailsVO transactionDetailsVO : transactionDetailsVOList)
                    {
                        statusMsg="Your record Found";
                        transactionError.calCheckSumAndWriteStatus(pWriter,transactionDetailsVO, statusMsg, key, checksumAlgorithm,responseType);
                        return;
                    }

                }

            }
            else
            {
                //set response for common
                CommInquiryResponseVO commInquiryResponseVO = null;
                for(TransactionDetailsVO transactionDetailsVO : transactionDetailsVOList)
                {
                    if(functions.isValueNull(transactionDetailsVO.getAccountId()) && !transactionDetailsVO.getAccountId().equals("0"))
                    {

                        AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(transactionDetailsVO.getAccountId()));
                        paymentProcess.getIntegrationSpecificTransactionDetails(transactionDetailsVO, transactionDetailsVO.getTrackingid(), toid);//set specific bank transaction details.
                        commInquiryResponseVO = paymentProcess.getCommonInquiryResponseVO(transactionDetailsVO);
                    }

                    if(ResponseLength.FULL.toString().equals(responseLenght) && commInquiryResponseVO != null)
                    {
                        statusMsg="Your record Found";
                        transactionError.calCheckSumAndWriteFullStatusResponse(pWriter, commInquiryResponseVO, statusMsg, key, checksumAlgorithm,responseType);
                    }
                    else
                    {
                        statusMsg="Your record Found";
                        transactionError.calCheckSumAndWriteStatus(pWriter,transactionDetailsVO, statusMsg, key, checksumAlgorithm,responseType);
                    }
                }
                return;
            }
        }
        catch (PZConstraintViolationException cve)
        {
            transactionLogger.error("PZConstraintViolationException:::::",cve);
            PZExceptionHandler.handleCVEException(cve,toid, PZOperations.DIRECTKIT_STATUS);
            statusMsg = cve.getPzConstraint().getMessage();
            status = "N";
            transactionError.calCheckSumAndWriteStatus(pWriter, description, trackingid, "","", status, statusMsg, key, checksumAlgorithm,requesttype,responseType);
            return;
        }
        catch (PZDBViolationException dbe)
        {
            transactionLogger.error("PZDBViolationException:::::",dbe);
            PZExceptionHandler.handleDBCVEException(dbe, toid, PZOperations.DIRECTKIT_STATUS);
            statusMsg = "Internal Errror Occured : Please contact Customer support for further help";
            status = "N";
            transactionError.calCheckSumAndWriteStatus(pWriter, description, trackingid, "", "", status, statusMsg, key, checksumAlgorithm, requesttype,responseType);
            return;
        }
        catch (NoSuchAlgorithmException e)
        {
            transactionLogger.error("NoSuchAlgorithmException:::::" + e);
            PZExceptionHandler.raiseAndHandleTechnicalViolationException("SingleCallGenericStatus", "singleCallStatus()", null, "Transaction", "No Such Algorithm Exception", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause(),toid,PZOperations.DIRECTKIT_STATUS);
            statusMsg = "Internal Errror Occured : Please contact Customer support for further help";
            status = "N";
            transactionError.calCheckSumAndWriteStatus(pWriter, description, trackingid, "","", status, statusMsg, key, checksumAlgorithm,requesttype,responseType);
            return;
        }
    }

    private CommonValidatorVO validateMandatoryParameters(CommonValidatorVO commonValidatorVO) throws PZDBViolationException
    {
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
                commonValidatorVO.setErrorMsg(error);
                return commonValidatorVO;
            }
        }

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        //inputMandatoryFieldsList.add(InputFields.TRACKINGID);
        inputMandatoryFieldsList.add(InputFields.CHECKSUM);
        //inputMandatoryFieldsList.add(InputFields.DESCRIPTION);
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
                    error= error + errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription()+" | ";

                }
            }
            commonValidatorVO.setErrorMsg(error);
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);

        return commonValidatorVO;
    }
    private CommonValidatorVO validateOptionalParameters(CommonValidatorVO commonValidatorVO)
    {
        InputValidator inputValidator = new InputValidator();
        String error ="";
        GenericTransDetailsVO genericTransDetailsVO=commonValidatorVO.getTransDetailsVO();
        MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();

        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.add(InputFields.TRACKINGID);
        inputMandatoryFieldsList.add(InputFields.DESCRIPTION);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,true);
        if(!errorList.isEmpty())
        {
            for (InputFields inputFields : inputMandatoryFieldsList)
            {

                if (errorList.getError(inputFields.toString()) != null)
                {
                    PZValidationException pzValidationException = (PZValidationException) errorList.getError(inputFields.toString());
                    errorCodeVO = pzValidationException.getErrorCodeVO();
                    error= error + errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription()+" | ";

                }
            }
            commonValidatorVO.setErrorMsg(error);
        }
        commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        commonValidatorVO.setTransDetailsVO(genericTransDetailsVO);

        return commonValidatorVO;
    }

    protected MerchantDetailsVO getMerchantConfigDetails(String toid) throws PZDBViolationException
    {
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO=new MerchantDAO();

        merchantDetailsVO = merchantDAO.getMemberDetails(toid);

        return merchantDetailsVO;
    }

}

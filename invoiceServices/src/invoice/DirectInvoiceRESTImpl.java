package invoice;

import com.auth.AuthFunctions;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.invoice.helper.InvoiceHelper;
import com.invoice.manager.InvoiceManager;
import com.invoice.validators.InvoiceInputValidator;
import com.invoice.vo.InvoiceVO;
import com.manager.vo.DirectKitResponseVO;
import com.merchant.vo.requestVOs.MerchantServiceRequest;
import com.merchant.vo.requestVOs.MerchantServiceRequestVO;
import com.merchant.vo.responseVOs.MerchantServiceResponseVO;
import com.merchant.vo.responseVOs.Result;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.response.PZResponseStatus;
import com.payment.validators.RestCommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import com.sun.jersey.api.core.InjectParam;
import utils.ReadInvoiceRequest;
import utils.WriteInvoiceResponse;
import vo.restVO.requestvo.InvoiceRequest;
import vo.restVO.requestvo.InvoiceRequestVO;
import vo.restVO.resposnevo.Invoice;
import vo.restVO.resposnevo.Response;
import vo.restVO.resposnevo.StatusInvoiceResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by Sneha on 2/9/2017.
 */

@Path("v1")
public class DirectInvoiceRESTImpl implements DirectInvoiceRESTService
{
    @Context
    HttpServletRequest request1;

    @Context
    HttpServletResponse response;

    private static ReadInvoiceRequest readInvoiceRequest = new ReadInvoiceRequest();
    private static WriteInvoiceResponse writeInvoiceResponse = new WriteInvoiceResponse();
    private static InvoiceManager invoiceManager = new InvoiceManager();
    private static InvoiceHelper invoiceHelper = new InvoiceHelper();
    private static InvoiceInputValidator restCommonInputValidator = new InvoiceInputValidator();
    private static RestCommonInputValidator commonInputValidator = new RestCommonInputValidator();

    private Logger logger = new Logger(DirectInvoiceRESTImpl.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(DirectInvoiceRESTImpl.class.getName());
    Functions functions = new Functions();

    public Response generate(@InjectParam InvoiceRequest paymentRequest)
    {

        if(request1.getQueryString()!=null){
            Response response=new Response();
            StatusInvoiceResult result = new StatusInvoiceResult();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setStatusInvoiceResult(result);

            return response;
        }
        InvoiceRequestVO invoiceRequestVO = readInvoiceRequest.getInvoiceRequestVO(paymentRequest);

        Response response = generateJSON(invoiceRequestVO);

        return response;
    }


    public Response generateJSON(InvoiceRequestVO paymentRequest)
    {
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        InvoiceVO invoiceVO = null;
        try
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if(authResponse==null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if( authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }

            invoiceVO = readInvoiceRequest.readGenerateInvoiceRequestJSON(paymentRequest, request1);
            if (invoiceVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO.setErrorCodeListVO(errorCodeListVO);
            invoiceVO = restCommonInputValidator.performValidationGenerateInvoiceParams(invoiceVO);
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO = invoiceHelper.performGenerateInvoiceVerification(invoiceVO);
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO.setIsCredential("0");
            invoiceVO.setRaisedby(invoiceVO.getMerchantDetailsVO().getLogin());
            invoiceVO = invoiceManager.processGenerateInvoice(invoiceVO);
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }

            writeInvoiceResponse.setGenerateInvoiceSuccessResponse(response, invoiceVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            transactionLogger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzConstraint().getErrorCodeListVO());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }
        return response;
    }


    public Response regenerate(@InjectParam InvoiceRequest paymentRequest)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        if(request1.getQueryString()!=null){
            Response response=new Response();
            StatusInvoiceResult result = new StatusInvoiceResult();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setStatusInvoiceResult(result);

            return response;
        }
        invoiceRequestVO = readInvoiceRequest.readRegenerateInvoiceRequest(paymentRequest);
        Response response = regenerateJSON(invoiceRequestVO);
        return response;
    }


    public Response regenerateJSON( InvoiceRequestVO paymentRequest)
    {
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        InvoiceVO invoiceVO = null;
        try
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if(authResponse==null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if( authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }

            invoiceVO = readInvoiceRequest.readRegenerateInvoiceRequestJSON(paymentRequest, request1);
            invoiceVO.setMerchantIpAddress(Functions.getIpAddress(request1));
            if (invoiceVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO.setErrorCodeListVO(errorCodeListVO);
            invoiceVO = restCommonInputValidator.performRegenerateInvoiceValidation(invoiceVO);
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }

            invoiceVO = invoiceHelper.performRegenerateInvoiceVerification(invoiceVO);
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO = invoiceManager.processRegenerateInvoice(invoiceVO);
            writeInvoiceResponse.setRegenerateInvoiceSuccessResponse(response, invoiceVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            transactionLogger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzConstraint().getErrorCodeListVO());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }
        catch (PZGenericConstraintViolationException e)
        {
            logger.error("PZ ParseException exception while invoice call via REST API", e);
            transactionLogger.error("PZ ParseException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::" + systemError.getMessage());
        }

        return response;
    }


    public Response cancel(@InjectParam InvoiceRequest paymentRequest)
    {


        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();

        if(request1.getQueryString()!=null){
            Response response=new Response();
            StatusInvoiceResult result = new StatusInvoiceResult();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setStatusInvoiceResult(result);

            return response;
        }
        invoiceRequestVO = readInvoiceRequest.readCancelInvoiceRequest(paymentRequest);

        Response response = cancelJSON(invoiceRequestVO);

        return response;
    }

    public Response cancelJSON( InvoiceRequestVO paymentRequest)
    {
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        InvoiceVO invoiceVO = null;

        try
        {

            String authResponse = request1.getAttribute("authfail").toString();
            if(authResponse==null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if( authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }

            invoiceVO = readInvoiceRequest.readCancelInvoiceRequestJSON(paymentRequest, request1);
            invoiceVO.setMerchantIpAddress(Functions.getIpAddress(request1));
            if (invoiceVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }

            invoiceVO.setErrorCodeListVO(errorCodeListVO);
            invoiceVO = restCommonInputValidator.performValidationCancelInvoiceParams(invoiceVO);
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO = invoiceHelper.performCancelRemindInvoiceVerification(invoiceVO);
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO = invoiceManager.processCancelInvoice(invoiceVO);
            writeInvoiceResponse.setSuccessCancelInvoiceresponse(response, invoiceVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            transactionLogger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzConstraint().getErrorCodeListVO());
        }
        catch (PZGenericConstraintViolationException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
        }
        /*catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }*/
        catch (SystemError systemError)
        {
            logger.error("SystemError::" + systemError.getMessage());
        }
        return response;
    }

    public Response remind(@InjectParam InvoiceRequest paymentRequest)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        if(request1.getQueryString()!=null){
            Response response=new Response();
            StatusInvoiceResult result = new StatusInvoiceResult();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setStatusInvoiceResult(result);

            return response;
        }
        invoiceRequestVO = readInvoiceRequest.readRemindInvoiceRequest(paymentRequest);
        Response response = remindJSON(invoiceRequestVO);
        return response;
    }

    public Response remindJSON( InvoiceRequestVO paymentRequest)
    {
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        InvoiceVO invoiceVO = null;
        try
        {

            String authResponse = request1.getAttribute("authfail").toString();
            if(authResponse==null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if( authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }

            invoiceVO = readInvoiceRequest.readRemindInvoiceRequestJSON(paymentRequest, request1);
            invoiceVO.setMerchantIpAddress(Functions.getIpAddress(request1));
            if (invoiceVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO.setErrorCodeListVO(errorCodeListVO);
            invoiceVO = restCommonInputValidator.performRemindInvoiceValidation(invoiceVO, "REST");
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }

            invoiceVO = invoiceHelper.performCancelRemindInvoiceVerification(invoiceVO);
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO = invoiceManager.processRemindInvoice(invoiceVO);
            writeInvoiceResponse.setRemindInvoiceSuccessResponse(response, invoiceVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            transactionLogger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzConstraint().getErrorCodeListVO());
        }
        catch (PZGenericConstraintViolationException e)
        {
            logger.error("PZ PZGenericConstraintViolationException while invoice call via REST API", e);
            transactionLogger.error("PZ PZGenericConstraintViolationException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
        }
        /*catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }*/
        catch (SystemError systemError)
        {
            logger.error("SystemError::" + systemError.getMessage());
        }

        return response;
    }

    public Response inquiry(@InjectParam InvoiceRequest paymentRequest)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        if(request1.getQueryString()!=null){
            Response response=new Response();
            StatusInvoiceResult result = new StatusInvoiceResult();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setStatusInvoiceResult(result);

            return response;
        }
        invoiceRequestVO = readInvoiceRequest.readInquiryInvoiceRequest(paymentRequest);
        Response response = inquiryJSON(invoiceRequestVO);

        return response;
    }
    public Response inquiryJSON(InvoiceRequestVO paymentRequest)
    {
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        InvoiceVO invoiceVO = null;
        try
        {

            String authResponse = request1.getAttribute("authfail").toString();
            if(authResponse==null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if( authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }

            invoiceVO = readInvoiceRequest.readInquiryInvoiceRequestJSON(paymentRequest, request1);
            invoiceVO.setMerchantIpAddress(Functions.getIpAddress(request1));
            if (invoiceVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO.setErrorCodeListVO(errorCodeListVO);
            invoiceVO = restCommonInputValidator.performInquiryInvoiceValidation(invoiceVO);
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }

            invoiceVO = invoiceHelper.performInquiryInvoiceVerification(invoiceVO);
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO = invoiceManager.processInquiryInvoice(invoiceVO);
            writeInvoiceResponse.setInquiryInvoiceSuccessResponse(response, invoiceVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            transactionLogger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzConstraint().getErrorCodeListVO());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }
        return response;
    }


    //consolidated for specific functionality
    public Response invoiceTransInquiry(@InjectParam InvoiceRequest paymentRequest)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();

        if(request1.getQueryString()!=null){
            Response response=new Response();
            StatusInvoiceResult result = new StatusInvoiceResult();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setStatusInvoiceResult(result);

            return response;
        }
        invoiceRequestVO = readInvoiceRequest.readInquiryInvoiceRequesttrans(paymentRequest);
        Response response = invoiceTransInquiryJSON(invoiceRequestVO);

        return response;
    }

    @Override
    public Response invoiceTransInquiryJSON( InvoiceRequestVO paymentRequest)
    {
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        InvoiceVO invoiceVO = null;
        String username="";
        try
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if(request1.getAttribute("username")!=null)
             username = request1.getAttribute("username").toString();
            if(authResponse==null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if( authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            invoiceVO = readInvoiceRequest.readInquiryInvoiceRequesttransJSON(paymentRequest, request1);
            invoiceVO.setMerchantIpAddress(Functions.getIpAddress(request1));
            if (invoiceVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO.setErrorCodeListVO(errorCodeListVO);
            invoiceVO = restCommonInputValidator.performInquiryInvoiceValidation(invoiceVO);
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            /*transactionLogger.error("username--->"+username);
            if(functions.isValueNull(username) && !username.equalsIgnoreCase(invoiceVO.getMerchantDetailsVO().getLogin()))
            {
                logger.error("Inside username not match---" + authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }*/
            invoiceVO = invoiceHelper.performInquiryInvoiceVerification(invoiceVO);
            if (!invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO = invoiceManager.processInvoiceTransInquiry(invoiceVO);
            writeInvoiceResponse.setTransInquiryInvoiceSuccessResponse(response, invoiceVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            transactionLogger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzConstraint().getErrorCodeListVO());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }
        return response;
    }



    public Response getInvoice(@InjectParam InvoiceRequest paymentRequest)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        if(request1.getQueryString()!=null){
            Response response=new Response();
            StatusInvoiceResult result = new StatusInvoiceResult();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setStatusInvoiceResult(result);
            logger.error("response---"+response);

            return response;
        }
        invoiceRequestVO = readInvoiceRequest.getInvoiceRequest(paymentRequest);
        Response response = getInvoiceJSON(invoiceRequestVO);
        return response;
    }

    public Response getInvoiceJSON( InvoiceRequestVO invoiceRequest)
    {
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        InvoiceVO invoiceVO = new InvoiceVO();
        Functions functions = new Functions();
        Invoice invoice = new Invoice();

        try
        {

            String authResponse = request1.getAttribute("authfail").toString();
            if(authResponse==null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if( authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }

            invoiceVO = readInvoiceRequest.getInvoiceRequestJSON(invoiceRequest);
            invoiceVO.setMerchantIpAddress(Functions.getIpAddress(request1));
            if (invoiceVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            invoiceVO.setErrorCodeListVO(errorCodeListVO);
            invoiceVO = restCommonInputValidator.performGetInvoiceValidation(invoiceVO, "REST");
            if (functions.isValueNull(invoiceVO.getErrorMsg()) || !invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            else
            {
                invoiceVO = invoiceHelper.merchantActivationChecks(invoiceVO);
                if (functions.isValueNull(invoiceVO.getErrorMsg()) || !invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                    return response;
                }
                else
                {
                    List<InvoiceVO> list = invoiceManager.processGetInvoiceList(invoiceVO);
                    writeInvoiceResponse.setInvoicelistresponse(response, invoiceVO, list);
                }
            }
        }

        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            transactionLogger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzConstraint().getErrorCodeListVO());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }
      /*  catch (SystemError systemError)
        {
            systemError.printStackTrace();
        }*/

        return response;
    }


    public Response getInvoiceDetails(@InjectParam InvoiceRequest paymentRequest)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        if(request1.getQueryString()!=null){
            Response response=new Response();
            StatusInvoiceResult result = new StatusInvoiceResult();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setStatusInvoiceResult(result);

            return response;
        }
        invoiceRequestVO = readInvoiceRequest.getInvoiceRequest(paymentRequest);
        Response response = getInvoiceDetailsJSON(invoiceRequestVO);
        return response;
    }




    public Response getInvoiceDetailsJSON( InvoiceRequestVO invoiceRequest)
    {
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        InvoiceVO invoiceVO = null;
        Functions functions = new Functions();


        try
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if(authResponse==null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if( authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            invoiceVO = readInvoiceRequest.getInvoiceRequestJSON(invoiceRequest);
            invoiceVO.setMerchantIpAddress(Functions.getIpAddress(request1));

            if (invoiceVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            else
            {
                invoiceVO.setErrorCodeListVO(errorCodeListVO);
                invoiceVO = restCommonInputValidator.performGetInvoiceDetailsValidation(invoiceVO, "REST");
                if (functions.isValueNull(invoiceVO.getErrorMsg()) || !invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                    return response;
                }
                else
                {
                    invoiceVO = invoiceHelper.merchantActivationChecks(invoiceVO);
                    if (functions.isValueNull(invoiceVO.getErrorMsg()) || !invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                        return response;
                    }
                    else
                    {
                        List<InvoiceVO> list = invoiceManager.processGetInvoiceDetails(invoiceVO);
                        writeInvoiceResponse.setInvoiceDetailsresponse(response, invoiceVO, list);
                    }
                }
            }
        }

        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            transactionLogger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzConstraint().getErrorCodeListVO());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }
        catch (ParseException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, null);
        }
      /*  catch (SystemError systemError)
        {
            systemError.printStackTrace();
        }*/

        return response;
    }


    public Response updateInvoiceConfigDetails(@InjectParam InvoiceRequest paymentRequest)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        if(request1.getQueryString()!=null){
            Response response=new Response();
            StatusInvoiceResult result = new StatusInvoiceResult();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setStatusInvoiceResult(result);

            return response;
        }
        invoiceRequestVO = readInvoiceRequest.setInvoiceConfigRequest(paymentRequest);
        Response response = updateInvoiceConfigDetailsJSON(invoiceRequestVO);
        return response;
    }

    public Response updateInvoiceConfigDetailsJSON( InvoiceRequestVO invoiceRequest)
    {
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        InvoiceVO invoiceVO = null;
        Functions functions = new Functions();

        try
        {

            String authResponse = request1.getAttribute("authfail").toString();
            if(authResponse==null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if( authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }

            invoiceVO = readInvoiceRequest.setInvoiceConfigRequestJSON(invoiceRequest);
            invoiceVO.setMerchantIpAddress(Functions.getIpAddress(request1));

            if (invoiceVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            else
            {
                invoiceVO.setErrorCodeListVO(errorCodeListVO);
                invoiceVO = restCommonInputValidator.performSetInvoiceConfigDetailsValidation(invoiceVO, "REST");
                if (functions.isValueNull(invoiceVO.getErrorMsg()) || !invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                    return response;
                }
                else
                {
                    invoiceVO = invoiceHelper.merchantActivationChecks(invoiceVO);
                    if (functions.isValueNull(invoiceVO.getErrorMsg()) || !invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                        return response;
                    }
                    else
                    {
                        String successStatus = invoiceManager.processSetInvoiceConfigDetails(invoiceVO);
                        invoiceVO.setStatus(successStatus);
                        writeInvoiceResponse.setSuccessInvoiceConfigDetailsResponse(response, invoiceVO);
                    }
                }
            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            transactionLogger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzConstraint().getErrorCodeListVO());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }
        return response;
    }

    public Response getInvoiceConfigDetails(@InjectParam InvoiceRequest paymentRequest)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        if(request1.getQueryString()!=null){
            Response response=new Response();
            StatusInvoiceResult result = new StatusInvoiceResult();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setStatusInvoiceResult(result);

            return response;
        }
        invoiceRequestVO = readInvoiceRequest.getInvoiceConfigRequest(paymentRequest);
        Response response = getInvoiceConfigDetailsJSON(invoiceRequestVO);
        return response;
    }

    public Response getInvoiceConfigDetailsJSON( InvoiceRequestVO invoiceRequest)
    {
        //System.out.println("inside method----");
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        InvoiceVO invoiceVO = null;
        Functions functions = new Functions();

        try
        {

            String authResponse = request1.getAttribute("authfail").toString();
            if(authResponse==null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if( authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }

            invoiceVO = readInvoiceRequest.getInvoiceConfigRequestJSON(invoiceRequest);
            invoiceVO.setMerchantIpAddress(Functions.getIpAddress(request1));

            if (invoiceVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            else
            {
                invoiceVO.setErrorCodeListVO(errorCodeListVO);
                invoiceVO = restCommonInputValidator.performGetInvoiceConfiguration(invoiceVO, "REST");
                if (functions.isValueNull(invoiceVO.getErrorMsg()) || !invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                    return response;
                }
                     else
                {
                    invoiceVO = invoiceHelper.merchantActivationChecks(invoiceVO);
                    if (functions.isValueNull(invoiceVO.getErrorMsg()) || !invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                        return response;
                    }
                    else
                    {
                        invoiceVO = invoiceManager.GetInvoiceConfig(invoiceVO);
                        writeInvoiceResponse.getInvoiceConfigDetailsResponse(response, invoiceVO);
                    }
                }
            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            transactionLogger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzConstraint().getErrorCodeListVO());
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZDBViolationException while invoice call via REST API", e);
        }
        return response;
    }



    public Response getOrderID(@InjectParam InvoiceRequest paymentRequest)
    {
        InvoiceRequestVO invoiceRequestVO = new InvoiceRequestVO();
        if(request1.getQueryString()!=null){
            Response response=new Response();
            StatusInvoiceResult result = new StatusInvoiceResult();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setStatusInvoiceResult(result);

            return response;
        }
        invoiceRequestVO = readInvoiceRequest.getOrderIdRequest(paymentRequest);
        Response response = getOrderIDJSON(invoiceRequestVO);
        return response;
    }

    public Response getOrderIDJSON( InvoiceRequestVO invoiceRequest)
    {
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        InvoiceVO invoiceVO = null;
        Functions functions = new Functions();

        try
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if(authResponse==null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if( authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---"+authResponse);
                writeInvoiceResponse.setFailAuthTokenResponse(response);
                return response;
            }


            invoiceVO = readInvoiceRequest.getOrderIdRequestJSON(invoiceRequest);
            invoiceVO.setMerchantIpAddress(Functions.getIpAddress(request1));

            if (invoiceVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                return response;
            }
            else
            {
                invoiceVO.setErrorCodeListVO(errorCodeListVO);
                invoiceVO = restCommonInputValidator.performGetOrderIdValidation(invoiceVO, "REST");
                if (functions.isValueNull(invoiceVO.getErrorMsg()) || !invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                    return response;
                }
                else
                {
                    invoiceVO = invoiceHelper.merchantActivationChecks(invoiceVO);
                    if (functions.isValueNull(invoiceVO.getErrorMsg()) || !invoiceVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, invoiceVO.getErrorCodeListVO());
                        return response;
                    }
                    else
                    {
                        String orderID = invoiceManager.processGetOrderID(invoiceVO);
                        if (functions.isValueNull(orderID))
                        {
                            invoiceVO.setDescription(orderID);
                            invoiceVO.setStatus("success");
                        }
                        writeInvoiceResponse.setOrderIDInResponse(response, invoiceVO);
                    }
                }
            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            transactionLogger.error("PZ PZConstraintViolationException while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzConstraint().getErrorCodeListVO());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while invoice call via REST API", e);
            transactionLogger.error("PZ Technical exception while invoice call via REST API", e);
            writeInvoiceResponse.setRestInvoiceResponseForError(response, invoiceVO, e.getPzGenericConstraint().getErrorCodeListVO());
        }
        return response;
    }



    public MerchantServiceResponseVO generateAuthToken(@InjectParam MerchantServiceRequest loginRequest)

    {
        if(request1.getQueryString()!=null){
            MerchantServiceResponseVO response=new MerchantServiceResponseVO();
            Result result=new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }

        MerchantServiceRequestVO merchantServiceRequestVO = readInvoiceRequest.getMerchantServiceRequestVO(loginRequest);

        MerchantServiceResponseVO merchantServiceResponseVO = generateAuthTokenJSON(merchantServiceRequestVO);

        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO generateAuthTokenJSON(MerchantServiceRequestVO loginRequest)
    {
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String IpAddress=request1.getRemoteAddr();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Functions functions = new Functions();
        try
        {
            commonValidatorVO = readInvoiceRequest.readRequestForMerchantLoginJSON(loginRequest);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performRestMerchantAuthTokenValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                writeInvoiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }
            request1.setAttribute("username",commonValidatorVO.getMerchantDetailsVO().getLogin());
            request1.setAttribute("password",commonValidatorVO.getMerchantDetailsVO().getPassword());
            request1.setAttribute("role","merchant");
            directKitResponseVO = invoiceManager.getAuthToken(commonValidatorVO, request1, response);
            writeInvoiceResponse.setSuccessAuthTokenResponse(merchantServiceResponseVO, directKitResponseVO, commonValidatorVO);

        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeInvoiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeInvoiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }



        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO getAuthToken(@InjectParam MerchantServiceRequest authRequest)
    {
        MerchantServiceResponseVO response1 = new MerchantServiceResponseVO();
        MerchantServiceRequestVO authRequestVO = new MerchantServiceRequestVO();

        if(request1.getQueryString()!=null){
            MerchantServiceResponseVO response=new MerchantServiceResponseVO();
            Result result=new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }
        authRequestVO = readInvoiceRequest.getNewAuthTokenRequest(authRequest);

        response1 = getAuthTokenJSON(authRequestVO);

        return response1;
    }

    public MerchantServiceResponseVO getAuthTokenJSON(MerchantServiceRequestVO loginRequest)
    {
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String IpAddress=request1.getRemoteAddr();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Functions functions = new Functions();
        AuthFunctions authFunctions = new AuthFunctions();
        try
        {
            commonValidatorVO = readInvoiceRequest.readRequestForgetNewAuthToken(loginRequest);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeInvoiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeInvoiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }
            else
            {

                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                commonValidatorVO = commonInputValidator.performGetNewAuthTokenValidation(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
                {
                    writeInvoiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return merchantServiceResponseVO;
                }

                request1.setAttribute("username", commonValidatorVO.getMerchantDetailsVO().getLogin());
                //request1.setAttribute("password", commonValidatorVO.getMerchantDetailsVO().getPassword());
                request1.setAttribute("role", "merchant");
                directKitResponseVO = invoiceManager.regenerateAuthToken(commonValidatorVO, request1, response);
                writeInvoiceResponse.setSuccessAuthTokenResponse(merchantServiceResponseVO, directKitResponseVO, commonValidatorVO);

            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            writeInvoiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZDBViolationException exception while Generating Token", e);
            transactionLogger.error("PZ PZDBViolationException exception while Generating Token", e);
            writeInvoiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }

        return merchantServiceResponseVO;
    }


   /* @Override



    public String test(@InjectParam InvoiceRequestVO paymentRequest)
    {
        logger.debug("Inside test----");
        String data = "Success";
       // logger.debug("paymentRequest--->"+paymentRequest.getMerchantInvoiceId());


        return data;
    }
*/
  /*  public static void main(String[] args)
    {
        DirectInvoiceRESTImpl directInvoiceREST = new DirectInvoiceRESTImpl();
        List<Item> items = new ArrayList<Item>();

        InvoiceRequest invoiceRequest = new InvoiceRequest();
        Item item = null;

        invoiceRequest.setMerchantInvoiceId("123456");
        item = new Item();
        item.setName("Panu");
        item.setPrice("0");
        items.add(item);

        item = new Item();
        item.setName("Panu1");
        item.setPrice("01");
        items.add(item);

        invoiceRequest.setItemList(it);

        directInvoiceREST.test(invoiceRequest);
    }*/
}
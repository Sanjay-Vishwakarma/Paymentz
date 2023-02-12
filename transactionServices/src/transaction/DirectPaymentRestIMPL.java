package transaction;

import com.auth.AuthFunctions;
import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.google.gson.Gson;
import com.manager.*;
import com.manager.dao.MerchantDAO;
import com.manager.dao.TerminalDAO;
import com.manager.dao.TransactionDAO;
import com.manager.helper.RegistrationHelper;
import com.manager.helper.TransactionHelper;
import com.manager.vo.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorType;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.response.PZResponseStatus;
import com.payment.utils.RestTokenRecurring;
import com.payment.validators.RestCommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectCaptureValidatorVO;
import com.payment.validators.vo.DirectRefundValidatorVO;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.resource.Singleton;
import com.transaction.manager.RegistrationManager;
import com.transaction.utils.ReadDirectTransactionRequest;
import com.transaction.utils.WriteDirectTransactionResponse;
import com.transaction.vo.restVO.RequestVO.RestPaymentRequest;
import com.transaction.vo.restVO.RequestVO.RestPaymentRequestVO;
import com.transaction.vo.restVO.ResponseVO.Response;
import com.transaction.vo.restVO.ResponseVO.Result;
import org.apache.log4j.LogManager;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.owasp.esapi.ESAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Sneha on 2/29/2016.
 */
@Singleton
@Path("v1")
public class DirectPaymentRestIMPL implements DirectPaymentRESTService
{
    public static final String PRE_AUTH = "PA";
    public static final String CAPTURE  = "CP";
    public static final String DEBIT    = "DB";
    public static final String CREDIT   = "CD";
    public static final String REFUND   = "RF";
    public static final String REVERSAL = "RV";
    public static final String INQUIRY  = "IN";
    public static final String DELETE   = "DL";
    @Context
    HttpServletRequest request1;
    @Context
    HttpServletResponse response;
    private RestDirectTransactionManager restDirectTransactionManager       = new RestDirectTransactionManager();
    private ReadDirectTransactionRequest readDirectTransactionRequest       = new ReadDirectTransactionRequest();
    private WriteDirectTransactionResponse writeDirectTransactionResponse   = new WriteDirectTransactionResponse();
    private TransactionHelper transactionHelper                             = new TransactionHelper();
    private Logger logger                                                   = new Logger(DirectPaymentRestIMPL.class.getName());
    private TransactionLogger transactionLogger                             = new TransactionLogger(DirectPaymentRestIMPL.class.getName());
    private RestCommonInputValidator restCommonInputValidator               = new RestCommonInputValidator();
    private RegistrationManager registrationManager                         = new RegistrationManager();
    Gson gson = new Gson();
    org.apache.log4j.Logger facileroLogger              = LogManager.getLogger("facilerolog");

    /*@Context
    MessageContext context;
    HttpServletRequest request2 = context.getHttpServletRequest();
*/
    public Response processTransaction(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1  = new Response();
            Result result       = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        Response response1      = new Response();
        restPaymentRequestVO    = readDirectTransactionRequest.readRequestForRestTransaction(paymentRequest);
        response1               = processTransactionJSON(restPaymentRequestVO);
        return response1;
    }


    public Response processTransactionJSON(RestPaymentRequestVO paymentRequest)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        logger.debug("Inside processTransaction:::");
        transactionLogger.debug("Inside processTransaction:::");
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO         = new ErrorCodeListVO();
        Functions functions                     = new Functions();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        String error = "";

        //ResponseVo of the client
        Response response                       = new Response();
        CommonValidatorVO directKitValidatorVO  = null;
        PaymentManager paymentManager           = new PaymentManager();
        String toType = "";

        try
        {
            logger.error("before if request1.getAttribute authtoken--" + request1.getAttribute("authfail"));

            if(request1.getAttribute("authfail")!=null)
            {
                logger.error("inside if request1.getAttribute authtoken--" + request1.getAttribute("authfail"));

                String authResponse = request1.getAttribute("authfail").toString();
                if (authResponse == null || authResponse.equals(""))
                {
                    directKitValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
                else if (authResponse.equals("false"))
                {
                    directKitValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
            }
            else
            {
                transactionLogger.debug("Inside invalid terminalid:::");
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_TRACKINGID, "Invalid TrackingID."));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide TrackingID");
                return response;
            }

            logger.debug("Payment type---" + paymentRequest.getPaymentType());
            if (paymentRequest != null && (!functions.isValueNull(paymentRequest.getPaymentType()) || !ESAPI.validator().isValidInput("transactionType", paymentRequest.getPaymentType(), "transactionType", 2, false)))
            {
                directKitValidatorVO = new CommonValidatorVO();
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_TYPE, "Invalid Payment Type,Accept only [0-9] with Max Length 2"));
                error                = "Invalid payment type, Payment type should not be empty";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_PAYMENT_TYPE.toString(), ErrorType.VALIDATION.toString());
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Transaction Type");
                return response;
            }
            else
            {
                if (paymentRequest != null && paymentRequest.getPaymentType() != null && (paymentRequest.getPaymentType().equals(PRE_AUTH) || paymentRequest.getPaymentType().equals(DEBIT)))
                {
                    String restRequest      = gson.toJson(paymentRequest);
                    JSONObject jsonObject2  = new JSONObject();
                    if(functions.isValueNull(restRequest))
                    {
                        try
                        {
                            JSONObject jsonObject   = new JSONObject(restRequest);
                            jsonObject2             = new JSONObject(restRequest);
                            if (jsonObject.has("cardVO"))
                            {
                                JSONObject cardJson = jsonObject.getJSONObject("cardVO");
                                String cardnumber   = "";
                                String expiryMonth  = "";
                                String expiryYear   = "";
                                String cvv          = "";
                                if(cardJson.has("number"))
                                cardnumber  = functions.maskingPan(cardJson.getString("number"));
                                if(cardJson.has("expiryMonth"))
                                expiryMonth = functions.maskingNumber(cardJson.getString("expiryMonth"));
                                if(cardJson.has("expiryYear"))
                                expiryYear = functions.maskingNumber(cardJson.getString("expiryYear"));
                                if(cardJson.has("cvv"))
                                cvv     = functions.maskingNumber(cardJson.getString("cvv"));

                                cardJson.put("number",cardnumber);
                                cardJson.put("expiryMonth",expiryMonth);
                                cardJson.put("expiryYear",expiryYear);
                                cardJson.put("cvv",cvv);
                                jsonObject.put("cardVO",cardJson);

                                jsonObject2 = new JSONObject(jsonObject.toString());
                            }
                            transactionLogger.error("Rest Request for processTransaction----"+jsonObject.toString());
                        }
                        catch (JSONException e)
                        {
                            transactionLogger.error("JSONException---->",e);
                        }
                    }
                    directKitValidatorVO    = readDirectTransactionRequest.readRequestForRestTransaction(paymentRequest, request1);
                    if (directKitValidatorVO == null)
                    {
                        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                        error = "Invalid request provided";
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_REQUEST_NULL.toString(), ErrorType.VALIDATION.toString());
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
                        return response;
                    }
                    else
                    {
                        directKitValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
                        directKitValidatorVO.setErrorCodeListVO(errorCodeListVO);
                        //getting terminalID from paymentBrand
                        if (functions.isValueNull(directKitValidatorVO.getRecurringBillingVO().getRecurringType()) && "REPEATED".equalsIgnoreCase(directKitValidatorVO.getRecurringBillingVO().getRecurringType()))
                        {
                            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_RECURRING_TYPE, "Invalid Recurring type"));
                            error = "Invalid recurring type";
                            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_RECURRING_TYPE.toString(), ErrorType.VALIDATION.toString());
                            writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide proper Recurring Type");
                            return response;
                        }
                        else
                        {
                           /* if(functions.isValueNull(directKitValidatorVO.getTransDetailsVO().getCurrency())&&"JPY".equalsIgnoreCase(directKitValidatorVO.getTransDetailsVO().getCurrency())){
                                directKitValidatorVO.getTransDetailsVO().setAmount(Functions.getAmount(directKitValidatorVO.getTransDetailsVO().getAmount()));
                            }*/
                            directKitValidatorVO    = restCommonInputValidator.performRestTransactionValidation(directKitValidatorVO);


                            if(directKitValidatorVO.getPartnerDetailsVO() != null && directKitValidatorVO.getPartnerDetailsVO().getCompanyName() !=null ){
                                toType = directKitValidatorVO.getPartnerDetailsVO().getCompanyName();
                            }
                            if("Facilero".equalsIgnoreCase(toType)){
                                facileroLogger.error("Rest process Transaction Merchant Request  -----> "+jsonObject2.toString());
                            }
                            if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                            {
                                writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());
                                return response;
                            }
                            else
                            {
                                transactionLogger.error("API REFERER from header---" + request1.getHeader("referer") + "--memberId--" + directKitValidatorVO.getMerchantDetailsVO().getMemberId());
                                transactionLogger.debug("HostedPaymentPage-----" + directKitValidatorVO.getMerchantDetailsVO().getHostedPaymentPage());
                                if (directKitValidatorVO.getMerchantDetailsVO().getHostedPaymentPage().equalsIgnoreCase("Y"))
                                {
                                    directKitResponseVO = restDirectTransactionManager.processHostedPaymentTransaction(directKitValidatorVO);
                                    writeDirectTransactionResponse.setHostedPaymentTransactionResponse(response, directKitResponseVO, directKitValidatorVO);
                                    return response;
                                }
                                else
                                {
                                    directKitValidatorVO    = transactionHelper.performRESTAPISystemCheck(directKitValidatorVO);
                                    if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                                    {
                                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());
                                        return response;
                                    }
                                    else
                                    {
                                        if (directKitValidatorVO.getPaymentMode().equalsIgnoreCase("CU") && directKitValidatorVO.getPaymentBrand().equalsIgnoreCase("CupUPI"))
                                        {
                                            transactionLogger.error("Inside PayMode-CupUpi PaymentBrand-CupUpi");
                                            Date date3      = new Date();
                                            transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction start #########" + date3.getTime());
                                            directKitResponseVO = restDirectTransactionManager.processDirectTransactionUPI(directKitValidatorVO); // new method for upi
                                            if (directKitResponseVO.isFraud())
                                            {
                                                writeDirectTransactionResponse.setRestTransactionResponseForInternalFraudCheck(response, directKitValidatorVO, errorCodeListVO, directKitResponseVO);
                                            }
                                            else
                                            {
                                                writeDirectTransactionResponse.setSuccessRestTransactionResponse(response, directKitResponseVO, directKitValidatorVO);
                                            }
                                            transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction end #########" + new Date().getTime());
                                            transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction diff #########" + (new Date().getTime() - date3.getTime()));
                                        }
                                        else if (directKitValidatorVO.getPaymentMode().equalsIgnoreCase("NB") || directKitValidatorVO.getPaymentMode().equalsIgnoreCase("PV")
                                                || directKitValidatorVO.getPaymentMode().equalsIgnoreCase("EW") || directKitValidatorVO.getPaymentMode().equalsIgnoreCase("BC") || directKitValidatorVO.getPaymentMode().equalsIgnoreCase("PB") || directKitValidatorVO.getPaymentMode().equalsIgnoreCase("CU") ||
                                                directKitValidatorVO.getPaymentBrand().equalsIgnoreCase("UNICREDIT") || directKitValidatorVO.getPaymentBrand().equalsIgnoreCase("AVISA") || directKitValidatorVO.getPaymentBrand().equalsIgnoreCase("AMC") || directKitValidatorVO.getPaymentBrand().equalsIgnoreCase("CLEARSETTLE") ||
                                                directKitValidatorVO.getPaymentMode().equalsIgnoreCase("EWI") || directKitValidatorVO.getPaymentMode().equalsIgnoreCase("NBI") || directKitValidatorVO.getPaymentMode().equalsIgnoreCase("UPI") || directKitValidatorVO.getPaymentMode().equalsIgnoreCase("ZOTA")||(directKitValidatorVO.getPaymentMode().equalsIgnoreCase("BT") && "JPBANK".equalsIgnoreCase(directKitValidatorVO.getPaymentBrand()))||directKitValidatorVO.getPaymentMode().equalsIgnoreCase("TM") ||directKitValidatorVO.getPaymentMode().equalsIgnoreCase("KCP") || directKitValidatorVO.getPaymentMode().equalsIgnoreCase("TWD")||directKitValidatorVO.getPaymentMode().equalsIgnoreCase("Gift")||directKitValidatorVO.getPaymentMode().equalsIgnoreCase("QKC")
                                                || directKitValidatorVO.getPaymentMode().equalsIgnoreCase("DOKU") || directKitValidatorVO.getPaymentMode().equalsIgnoreCase("FASTPAY") || directKitValidatorVO.getPaymentMode().equalsIgnoreCase("BITCOIN") || (directKitValidatorVO.getPaymentMode().equalsIgnoreCase("BT") && "Transfr".equalsIgnoreCase(directKitValidatorVO.getPaymentBrand()))|| (directKitValidatorVO.getPaymentMode().equalsIgnoreCase("BT")&& ("EFT".equalsIgnoreCase(directKitValidatorVO.getPaymentBrand()) ||"VFD".equalsIgnoreCase(directKitValidatorVO.getPaymentBrand()))))
                                        {
                                            directKitResponseVO = restDirectTransactionManager.processAsyncDirectTransaction(directKitValidatorVO);
                                            writeDirectTransactionResponse.setSuccessRestAsyncTransactionResponse(response, directKitResponseVO, directKitValidatorVO);
                                            return response;
                                        }
                                        else
                                        {
                                            Date date3 = new Date();
                                            transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction start #########" + date3.getTime());
                                            directKitResponseVO = restDirectTransactionManager.processDirectTransaction(directKitValidatorVO);
                                            if (directKitResponseVO.isFraud())
                                            {
                                                writeDirectTransactionResponse.setRestTransactionResponseForInternalFraudCheck(response, directKitValidatorVO, errorCodeListVO, directKitResponseVO);
                                            }
                                            else
                                            {
                                                if ("pending3DConfirmation".equalsIgnoreCase(directKitResponseVO.getStatus()))
                                                {
                                                    writeDirectTransactionResponse.setSuccessRest3DTransactionResponse(response, directKitResponseVO, directKitValidatorVO);
                                                }
                                                else
                                                {
                                                    writeDirectTransactionResponse.setSuccessRestTransactionResponse(response, directKitResponseVO, directKitValidatorVO);
                                                }
                                            }
                                            transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction end #########" + new Date().getTime());
                                            transactionLogger.debug("DirectTransactionRESTIMPL processDirectTransaction diff #########" + (new Date().getTime() - date3.getTime()));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    directKitValidatorVO = new CommonValidatorVO();
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Payment Request"));
                    error = "Invalid Payment Type";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_PAYMENT_TYPE.toString(), ErrorType.VALIDATION.toString());
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide proper Payment Type");
                }
            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZConstraint Violation Exception while placing transaction via Rest", e);
            transactionLogger.error("PZConstraint Violation Exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
        }
        catch (PZDBViolationException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("PZ DBViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (PZGenericConstraintViolationException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("PZ GenericConstraintViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzGenericConstraint().getMessage()/* "Internal Error occurred,Please contact support for more information"*/);
        }
        catch (NoSuchAlgorithmException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("SystemError while placing transaction via Rest", e);
            transactionLogger.error("SystemError while placing transaction via Rest", e);
            writeDirectTransactionResponse.setRestCaptureResponseForError(response, directKitValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        if("Facilero".equalsIgnoreCase(toType)){
            facileroLogger.error("Rest process Transaction Merchant Response ----> " + gson.toJson(response));
        }else{
            transactionLogger.error("Rest process Transaction Response ----> " + gson.toJson(response));
        }


        return response;
    }


    public Response processCaptureCancelRefund(String id, @InjectParam RestPaymentRequest paymentRequest)
    {
        if(request1.getQueryString() != null){
            Response response1  = new Response();
            Result result       = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        RestPaymentRequestVO restPaymentRequestVO   = new RestPaymentRequestVO();
        Response response1                          = new Response();

        restPaymentRequestVO    = readDirectTransactionRequest.readRequestForRestCaptureCancelRefundRecurring(paymentRequest);
        response1               = processCaptureCancelRefundJSON(id,restPaymentRequestVO);

        return response1;
    }

    public Response processCaptureCancelRefundJSON(String id, RestPaymentRequestVO paymentRequestVO)
    {
        transactionLogger.error("Inside rest in processCaptureCancelRefund :::");
        logger.error("Id----" + id);
        response.addHeader("Access-Control-Allow-Origin", "*");

        DirectKitResponseVO directKitResponseVO     = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO             = new ErrorCodeListVO();
        Functions functions                         = new Functions();
        GenericAddressDetailsVO addressDetailsVO    = null;
        GenericTransDetailsVO transDetailsVO        = new GenericTransDetailsVO();
        Response response                           = new Response();
        DirectCaptureValidatorVO directCaptureValidatorVO   = null;
        CommonValidatorVO commonValidatorVO                 = null;
        String encId    = "";
        try
        {
            encId = URLEncoder.encode(id, "UTF-8");
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while url encoded--------"+e);
        }
        /*String authResponse = request1.getAttribute("authfail").toString();
        if (authResponse==null || authResponse.equals(""))
        {
            logger.debug("Inside failResponse---"+authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;
        }
        else if(authResponse.equals("false"))
        {
            logger.debug("Inside failResponse---"+authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;ccom
        }*/
        //If ID is valid then procced furher
        transactionLogger.error("Rest process Capture Cancel Refund Request------" + gson.toJson(paymentRequestVO));
        commonValidatorVO       = readDirectTransactionRequest.readRequestForRestCaptureCancelRefundRecurringJSON(paymentRequestVO, request1);
        if (commonValidatorVO == null)
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Capture Request provided."));
            writeDirectTransactionResponse.setRestCaptureResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while transaction");
            return response;
        }
        if (commonValidatorVO.getTransactionType() != null && !commonValidatorVO.getTransactionType().equals(INQUIRY) && !ESAPI.validator().isValidInput("encId", encId, "Numbers", 50, false))
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_TRACKINGID, "Invalid TrackingID."));
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide TrackingID");
            return response;
        }
        if (commonValidatorVO != null && commonValidatorVO.getTransactionType() != null && commonValidatorVO.getTransactionType().equals(CAPTURE))
        {
            transactionLogger.error("Inside Rest Capture :::");
            commonValidatorVO.setTrackingid(encId);
            addressDetailsVO = new GenericAddressDetailsVO();
            addressDetailsVO.setIp(Functions.getIpAddress(request1));
            transDetailsVO.setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
            commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

            String error = "";
            if (!errorCodeListVO.getListOfError().isEmpty() && functions.isValueNull(error))
            {
                writeDirectTransactionResponse.setRestCaptureResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, error);
                return response;
            }
            try
            {
                commonValidatorVO = restCommonInputValidator.performRestCaptureValidation(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setRestCaptureResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }

                directCaptureValidatorVO = new DirectCaptureValidatorVO();
                directCaptureValidatorVO.setAddressDetailsVO(commonValidatorVO.getAddressDetailsVO());
                directCaptureValidatorVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());
                directCaptureValidatorVO.setTransDetailsVO(commonValidatorVO.getTransDetailsVO());
                directCaptureValidatorVO.setTrackingid(commonValidatorVO.getTrackingid());

                Date date3 = new Date();
                transactionLogger.debug("DirectTransactionRESTImpl processCapture start #########" + date3.getTime());
                directKitResponseVO = restDirectTransactionManager.processCapture(directCaptureValidatorVO);
                transactionLogger.debug("DirectTransactionRESTImpl processCapture end #########" + new Date().getTime());
                transactionLogger.debug("DirectTransactionRESTImpl processCapture diff #########" + (new Date().getTime() - date3.getTime()));
                writeDirectTransactionResponse.setSuccessRestCaptureResonse(response, directKitResponseVO, directCaptureValidatorVO,commonValidatorVO);

            }
            catch (PZDBViolationException e)
            {
                transactionLogger.debug("PZ Technical exception while capturing transaction via WebService" + e);
                writeDirectTransactionResponse.setRestCaptureResponseForError(response, directCaptureValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (PZConstraintViolationException e)
            {
                transactionLogger.debug("PZ PZConstraintViolationException exception while capturing transaction via WebService" + e);
                writeDirectTransactionResponse.setRestCaptureResponseForError(response, directCaptureValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (PZGenericConstraintViolationException e)
            {
                transactionLogger.debug("PZ Technical exception while capturing transaction via WebService" + e);
                writeDirectTransactionResponse.setRestCaptureResponseForError(response, directCaptureValidatorVO, e.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (NoSuchAlgorithmException e)
            {
                transactionLogger.debug("PZ Technical exception while capturing transaction via WebService" + e);
                writeDirectTransactionResponse.setRestCaptureResponseForError(response, directCaptureValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
        }
        else if (commonValidatorVO != null && commonValidatorVO.getTransactionType() != null && commonValidatorVO.getTransactionType().equals(REVERSAL))
        {
            transactionLogger.error("Inside Rest Reversal :::");
            commonValidatorVO.setTrackingid(encId);

            try
            {
                commonValidatorVO = restCommonInputValidator.performRestCancelValidation(commonValidatorVO);
                if ((!commonValidatorVO.getErrorMsg().isEmpty()) || (commonValidatorVO.getErrorCodeListVO() != null))
                {
                    writeDirectTransactionResponse.setRestCancelResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }

                errorCodeListVO = transactionHelper.getReferencedCaptureCancelRefundTransDetails(commonValidatorVO);
                if (errorCodeListVO != null)
                {
                    writeDirectTransactionResponse.setRestCancelResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }

                Date date3 = new Date();
                transactionLogger.debug("DirectTransactionRESTImpl processCancel start #########" + date3.getTime());
                directKitResponseVO = restDirectTransactionManager.processCancel(commonValidatorVO);
                transactionLogger.debug("DirectTransactionRESTImpl processCancel end #########" + new Date().getTime());
                transactionLogger.debug("DirectTransactionRESTImpl processCancel diff #########" + (new Date().getTime() - date3.getTime()));
                writeDirectTransactionResponse.setSuccessRestCancelResponse(response, directKitResponseVO, commonValidatorVO);

            }
            catch (PZDBViolationException e)
            {
                transactionLogger.debug("PZ Technical exception while cancelling transaction via WebService" + e);
                writeDirectTransactionResponse.setRestCancelResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (PZConstraintViolationException e)
            {
                transactionLogger.debug("PZ PZConstraintViolationException while cancelling transaction via WebService" + e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
            }
            catch (PZGenericConstraintViolationException e)
            {
                transactionLogger.debug("PZ GenericConstraintViolation while cancelling transaction via WebService" + e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzGenericConstraint().getMessage());
            }
            catch (NoSuchAlgorithmException e)
            {
                transactionLogger.debug("PZ Technical exception while cancelling transaction via WebService" + e);
                writeDirectTransactionResponse.setRestCaptureResponseForError(response, directCaptureValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
        }
        else if (commonValidatorVO != null && commonValidatorVO.getTransactionType() != null && commonValidatorVO.getTransactionType().equals(REFUND))
        {
            transactionLogger.error("Inside Rest Refund :::");
            commonValidatorVO.setTrackingid(encId);
            addressDetailsVO = new GenericAddressDetailsVO();
            addressDetailsVO.setIp(Functions.getIpAddress(request1));
            transDetailsVO.setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());

            commonValidatorVO.setAddressDetailsVO(addressDetailsVO);
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

            try
            {
                restCommonInputValidator.performRestRefundValidation(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setRestRefundResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }

                errorCodeListVO = transactionHelper.getReferencedCaptureCancelRefundTransDetails(commonValidatorVO);
                if (errorCodeListVO != null)
                {
                    writeDirectTransactionResponse.setRestRefundResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }

                DirectRefundValidatorVO directRefundValidatorVO = new DirectRefundValidatorVO();
                directRefundValidatorVO.setTrackingid(commonValidatorVO.getTrackingid());
                directRefundValidatorVO.setTransDetailsVO(commonValidatorVO.getTransDetailsVO());
                directRefundValidatorVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());
                directRefundValidatorVO.setMerchantIpAddress(commonValidatorVO.getAddressDetailsVO().getIp());
                directRefundValidatorVO.setHeader(transDetailsVO.getHeader());
                directRefundValidatorVO.setRefundAmount(commonValidatorVO.getTransDetailsVO().getAmount());
                if (commonValidatorVO.getMarketPlaceVOList() != null)
                {
                    directRefundValidatorVO.setMarketPlaceVOList(commonValidatorVO.getMarketPlaceVOList());
                }
                if (commonValidatorVO.getMarketPlaceVO() != null)
                {
                    directRefundValidatorVO.setMarketPlaceVO(commonValidatorVO.getMarketPlaceVO());
                }
                Date date3 = new Date();
                transactionLogger.debug("DirectTransactionRESTImpl processRefund start #########" + date3.getTime());
                directKitResponseVO = restDirectTransactionManager.processRefund(directRefundValidatorVO);
                transactionLogger.debug("DirectTransactionRESTImpl processRefund end #########" + new Date().getTime());
                transactionLogger.debug("DirectTransactionRESTImpl processRefund diff #########" + (new Date().getTime() - date3.getTime()));
                writeDirectTransactionResponse.setSuccessRestRefundResonse(response, directKitResponseVO, commonValidatorVO);
            }
            catch (PZTechnicalViolationException e)
            {
                transactionLogger.error("PZ Technical exception while refunding transaction via WebService", e);
                writeDirectTransactionResponse.setRestRefundResponseForError(response, commonValidatorVO, e.getPzTechnicalConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (PZConstraintViolationException e)
            {
                transactionLogger.error("PZ ConstraintViolation exception while refunding transaction via WebService" , e);
                writeDirectTransactionResponse.setRestRefundResponseForError(response, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (PZGenericConstraintViolationException e)
            {
                transactionLogger.error("PZ GenericConstraintViolation exception while refunding transaction via WebService" , e);
                writeDirectTransactionResponse.setRestRefundResponseForError(response, commonValidatorVO, e.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (NoSuchAlgorithmException e)
            {
                transactionLogger.error("PZ Technical exception while refunding transaction via WebService" , e);
                writeDirectTransactionResponse.setRestCaptureResponseForError(response, directCaptureValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
        }
        //REPEATED transaction with trackingId/ paymentId
        else if (null != paymentRequestVO && PRE_AUTH.equals(paymentRequestVO.getPaymentType()) || DEBIT.equals(paymentRequestVO.getPaymentType()))
        {
            try
            {
                //commonValidatorVO = readDirectTransactionRequest.readRequestForTokenTransactionRegistration(paymentRequestVO, request1);
                if (null == commonValidatorVO.getRecurringBillingVO())
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_TYPE));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, errorCodeListVO.getListOfError().get(0).getErrorDescription());
                    return response;
                }
                if ("REPEATED".equalsIgnoreCase(commonValidatorVO.getRecurringBillingVO().getRecurringType()))
                {
                    //transaction through trackingId
                    String accountId = "";
                    Transaction transaction = new Transaction();
                    ManualRebillResponseVO manualRebillResponseVO = null;

                    commonValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());

                    logger.debug("Entring into if REPEATED");
                    String trackingId = encId;
                    commonValidatorVO.setTrackingid(trackingId);

                    if (!functions.isValueNull(trackingId) || !functions.isNumericVal(trackingId) || trackingId.length() > 20 || !ESAPI.validator().isValidInput("trackingid", trackingId, "Numbers", 10, false))
                    {
                        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_TRACKINGID));
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, errorCodeListVO.getListOfError().get(0).getErrorDescription());
                        return response;
                    }

                    accountId = transaction.getAccountID(trackingId);
                    if (functions.isEmptyOrNull(accountId))
                    {
                        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_TRACKINGID));
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                        return response;
                    }

                    GatewayAccount account  = GatewayAccountService.getGatewayAccount(accountId);
                    String fromtype         = account.getGateway();
                    commonValidatorVO.getTransDetailsVO().setFromtype(fromtype);
                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                    commonValidatorVO = restCommonInputValidator.performRestRecurringTokenValidation(commonValidatorVO);
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                        return response;
                    }
                    manualRebillResponseVO                  = new ManualRebillResponseVO();
                    RestTokenRecurring restTokenRecurring   = new RestTokenRecurring();
                    commonValidatorVO.getMerchantDetailsVO().setAccountId(accountId);
                    Date date3 = new Date();
                    transactionLogger.debug("DirectTransactionRESTImpl manualSingleCall start #########" + date3.getTime());
                    manualRebillResponseVO = restTokenRecurring.manualSingleCall(commonValidatorVO, null);
                    transactionLogger.debug("DirectTransactionRESTImpl manualSingleCall end #########" + new Date().getTime());
                    transactionLogger.debug("DirectTransactionRESTImpl manualSingleCall diff #########" + (new Date().getTime() - date3.getTime()));
                    if (manualRebillResponseVO != null)
                    {
                        writeDirectTransactionResponse.setSuccessRestRecurringResponse(response, manualRebillResponseVO, commonValidatorVO);
                    }
                }
                else
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_RECURRING_TYPE, "Invalid recurring type"));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide proper Recurring Type");
                }
            }

            catch (PZConstraintViolationException e)
            {
                transactionLogger.debug("PZConstraint Violation Exception while placing transaction via WebService" + e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.debug("PZDBViolationException while placing transaction via WebService" + e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (PZTechnicalViolationException e)
            {
                transactionLogger.debug("PZ Technical exception while placing transaction via WebService" + e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzTechnicalConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (PZGenericConstraintViolationException e)
            {
                transactionLogger.debug("PZGenericConstraintViolationException while placing transaction via WebService" + e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, e.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "This functionality is not allowed for your gateway. please contact tech side for support");
            }
            catch (NoSuchAlgorithmException e)
            {
                transactionLogger.debug("PZ Technical exception while placing transaction via WebService" + e);
                writeDirectTransactionResponse.setRestCaptureResponseForError(response, commonValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
        }
        else if (null != paymentRequestVO && INQUIRY.equals(paymentRequestVO.getPaymentType()))
        {
            transactionLogger.error("Inside rest in processInquiry :::");
            logger.error("ID----" + encId);

            if (!functions.isValueNull(commonValidatorVO.getIdType()))
            {
                commonValidatorVO.setIdType("PID");
            }

            //CommonValidatorVO directKitValidatorVO = null;
            //directKitValidatorVO = readDirectTransactionRequest.readRequestForRestInquiry(paymentRequestVO, request1);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Reversal Request provided."));
                writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while cancelling transaction");
                return response;
            }

            try
            {
                ErrorCodeUtils errorCodeUtils   = new ErrorCodeUtils();
                if (commonValidatorVO.getIdType().equalsIgnoreCase("PID"))
                {
                    commonValidatorVO.setTrackingid(encId);
                }
                else if (commonValidatorVO.getIdType().equalsIgnoreCase("MID"))
                {
                    commonValidatorVO.getTransDetailsVO().setOrderId(encId);
                }else if(functions.isValueNull(commonValidatorVO.getIdType()))
                {
                    errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_ID_TYPE));
                    writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                commonValidatorVO = restCommonInputValidator.performRestInquiryValidation(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }

                directKitResponseVO = restDirectTransactionManager.processInquiry(commonValidatorVO);
                writeDirectTransactionResponse.setSuccessRestInquiryResponse(response, directKitResponseVO, commonValidatorVO);
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
                writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (PZConstraintViolationException e)
            {
                transactionLogger.error("PZ ConstraintViolation exception while inquiring transaction via WebService", e);
                writeDirectTransactionResponse.setRestRefundResponseForError(response, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (NoSuchAlgorithmException e)
            {
                transactionLogger.error("PZ Technical exception while inquiring transaction via WebService", e);
                writeDirectTransactionResponse.setRestCaptureResponseForError(response, commonValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
        }
        else
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_TYPE, "Invalid payment type provided."));
            writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while cancelling transaction");
            transactionLogger.error("Rest process Capture Cancel Refund Response------" + gson.toJson(response));
            return response;
        }
        transactionLogger.error("Rest process Capture Cancel Refund Response------" + gson.toJson(response));
        return response;
    }

    public Response processRegistrationTransaction(String id, @InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        Response response1 = new Response();

        restPaymentRequestVO = readDirectTransactionRequest.readRequestForTokenRegistrationTransaction(paymentRequest);

        response1 = processRegistrationTransactionJSON(id, restPaymentRequestVO);

        return response1;

    }

    public Response processRegistrationTransactionJSON(String id, RestPaymentRequestVO paymentRequestVO)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        DirectKitResponseVO directKitResponseVO         = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO                 = new ErrorCodeListVO();
        ManualRebillResponseVO manualRebillResponseVO   = null;
        Functions functions = new Functions();
        Response response   = new Response();
        CommonValidatorVO commonValidatorVO     = null;
        RegistrationHelper registrationHelper   = new RegistrationHelper();
        String paymentType                      = paymentRequestVO.getPaymentType();

        if (!ESAPI.validator().isValidInput("id", id, "onlyAlphanum", 32, false))
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_TOKEN, "Invalid registration id."));
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide registration id");
            return response;
        }

        if (functions.isValueNull(paymentType))
        {
            if (!ESAPI.validator().isValidInput("transactionType", paymentType, "transactionType", 2, false))
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_TYPE, "Invalid Payment Type,Accept only [0-9] with Max Length 5"));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Invalid Payment Type,Accept only [0-9] with Max Length 5");
                return response;
            }
        }

        String authResponse = request1.getAttribute("authfail").toString();
        if (authResponse == null || authResponse.equals(""))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;
        }
        else if (authResponse.equals("false"))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;
        }
        String restRequest=gson.toJson(paymentRequestVO);
        if(functions.isValueNull(restRequest))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(restRequest);
                if (jsonObject.has("cardVO"))
                {
                    JSONObject cardJson = jsonObject.getJSONObject("cardVO");
                    String cardnumber   = "";
                    String expiryMonth  = "";
                    String expiryYear   = "";
                    String cvv          = "";
                    if(cardJson.has("number"))
                        cardnumber = functions.maskingPan(cardJson.getString("number"));
                    if(cardJson.has("expiryMonth"))
                        expiryMonth = functions.maskingNumber(cardJson.getString("expiryMonth"));
                    if(cardJson.has("expiryYear"))
                        expiryYear = functions.maskingNumber(cardJson.getString("expiryYear"));
                    if(cardJson.has("cvv"))
                        cvv = functions.maskingNumber(cardJson.getString("cvv"));

                    cardJson.put("number",cardnumber);
                    cardJson.put("expiryMonth",expiryMonth);
                    cardJson.put("expiryYear",expiryYear);
                    cardJson.put("cvv",cvv);
                    jsonObject.put("cardVO",cardJson);
                }
                transactionLogger.error("Rest process Registration Transaction Request---" + jsonObject.toString());
            }
            catch (JSONException e)
            {
                transactionLogger.error("JSONException---->",e);
            }
        }
        commonValidatorVO = readDirectTransactionRequest.readRequestForTokenTransactionRegistration(paymentRequestVO, request1);
        if (paymentRequestVO != null && paymentRequestVO.getPaymentType() != null && (paymentRequestVO.getPaymentType().equals(DELETE)))
        {
            logger.debug("Inside processDeleteRegisteredToken :::");
            transactionLogger.debug("Inside Rest in processDeleteRegisteredToken :::");

            //commonValidatorVO = readDirectTransactionRequest.readRequestForRestDeleteToken(paymentRequestVO, request1);
            commonValidatorVO.setToken(id);

            if (!functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getMemberId()) && !functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPartnerId()))
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_MEMBERID_PARTNERID, "MemberId Or PartnerId invalid, MemberId Or PartnerId should not be empty and should be numeric with max length 10"));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "MemberId Or PartnerId invalid, MemberId Or PartnerId should not be empty and should be numeric with max length 10");
                return response;
            }

            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Delete Token Request provided."));
                writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while cancelling transaction");
                return response;
            }
            commonValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

            String error = "";
            if (!errorCodeListVO.getListOfError().isEmpty() && functions.isValueNull(error))
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, error);
                return response;
            }

            try
            {
                commonValidatorVO = restCommonInputValidator.performRestDeleteTokenValidation(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }
                commonValidatorVO = registrationHelper.performRESTAPIDeleteRegChecks(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }

                directKitResponseVO = restDirectTransactionManager.processDeleteRegisteredToken(commonValidatorVO);
                writeDirectTransactionResponse.setSuccessRestDeleteTokenResponse(response, directKitResponseVO, commonValidatorVO);
            }
            catch (PZDBViolationException e)
            {
                logger.error("PZ DBViolation exception while placing transaction via Rest", e);
                transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
                writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (PZConstraintViolationException e)
            {
                logger.error("PZ PZConstraintViolationException exception while placing transaction via Rest", e);
                transactionLogger.error("PZ PZConstraintViolationException exception while placing transaction via WebService", e);
                writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (NoSuchAlgorithmException e)
            {
                logger.error("PZ GenericConstraintViolation exception while placing transaction via Rest", e);
                transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
                writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, commonValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (Exception e)
            {
                logger.error("Exception while placing transaction via Rest", e);
                transactionLogger.error("Exception while placing transaction via WebService", e);
                writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
        }

        else if (paymentRequestVO != null && paymentRequestVO.getPaymentType() != null && (paymentRequestVO.getPaymentType().equals(PRE_AUTH) || paymentRequestVO.getPaymentType().equals(DEBIT)))
        {
            try
            {
                logger.debug("Inside processTokenTransactionRegistration()::");
                //commonValidatorVO = readDirectTransactionRequest.readRequestForTokenTransactionRegistration(paymentRequestVO, request1);
                commonValidatorVO.setToken(id);
                if (commonValidatorVO == null)
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, errorCodeListVO.getListOfError().get(0).getErrorDescription());
                    return response;
                }
                if (!ESAPI.validator().isValidInput("recurringType", commonValidatorVO.getRecurringBillingVO().getRecurringType(), "recurringType", 8, true))
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_RECURRING_TYPE, "Invalid Recurring Type."));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide recurring type");
                    return response;
                }
                else if (functions.isValueNull(commonValidatorVO.getRecurringBillingVO().getRecurringType()) && "REPEATED".equals(commonValidatorVO.getRecurringBillingVO().getRecurringType()))
                {
                    logger.debug("Inside REPEATED transaction via token::");
                    commonValidatorVO.setToken(id);
                    commonValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
                    commonValidatorVO.getAddressDetailsVO().setIp(Functions.getIpAddress(request1));
                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                    commonValidatorVO = restCommonInputValidator.performRestRecurringTokenValidation(commonValidatorVO);
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                        return response;
                    }
                    else
                    {
                        String accountId            = commonValidatorVO.getTerminalVO().getAccountId();
                        GatewayAccount account      = GatewayAccountService.getGatewayAccount(accountId);
                        String fromtype             = account.getGateway();
                        commonValidatorVO.getTransDetailsVO().setFromtype(fromtype);
                        manualRebillResponseVO                  = new ManualRebillResponseVO();
                        RestTokenRecurring restTokenRecurring   = new RestTokenRecurring();
                        Date date3              = new Date();
                        transactionLogger.debug("DirectTransactionRESTImpl manualSingleCall start #########" + date3.getTime());
                        manualRebillResponseVO = restTokenRecurring.manualSingleCall(commonValidatorVO, null);
                        transactionLogger.debug("DirectTransactionRESTImpl manualSingleCall end #########" + new Date().getTime());
                        transactionLogger.debug("DirectTransactionRESTImpl manualSingleCall diff #########" + (new Date().getTime() - date3.getTime()));
                        if (manualRebillResponseVO != null)
                        {
                            writeDirectTransactionResponse.setSuccessRestRecurringResponse(response, manualRebillResponseVO, commonValidatorVO);
                            return response;
                        }
                    }
                }

                else if (functions.isValueNull(commonValidatorVO.getRecurringBillingVO().getRecurringType()) && "INITIAL".equals(commonValidatorVO.getRecurringBillingVO().getRecurringType()))
                {
                    logger.debug("Inside token transaction with recurring INITIAL---");
                    String token = id;
                    commonValidatorVO.setToken(token);
                    commonValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                    commonValidatorVO = restCommonInputValidator.performInitialRecurringTokenValidation(commonValidatorVO);
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                        return response;
                    }
                    else
                    {
                        Date date3 = new Date();
                        transactionLogger.debug("DirectTransactionRESTImpl processDirectTransaction start #########" + date3.getTime());
                        directKitResponseVO = restDirectTransactionManager.processDirectTransaction(commonValidatorVO);
                        transactionLogger.debug("DirectTransactionRESTImpl processDirectTransaction end #########" + new Date().getTime());
                        transactionLogger.debug("DirectTransactionRESTImpl processDirectTransaction diff #########" + (new Date().getTime() - date3.getTime()));
                        writeDirectTransactionResponse.setSuccessRestTransactionResponse(response, directKitResponseVO, commonValidatorVO);
                    }
                }

                else if (!functions.isValueNull(commonValidatorVO.getRecurringBillingVO().getRecurringType()))
                {
                    logger.debug("Inside token transaction without recurring---");
                    String token = id;
                    commonValidatorVO.setToken(token);
                    commonValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                    commonValidatorVO = restCommonInputValidator.performRestTokenValidation(commonValidatorVO);
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                        return response;
                    }
                    else
                    {
                        Date date3 = new Date();
                        transactionLogger.debug("DirectTransactionRESTImpl processDirectTransaction start #########" + date3.getTime());
                        directKitResponseVO = restDirectTransactionManager.processDirectTransaction(commonValidatorVO);
                        transactionLogger.debug("DirectTransactionRESTImpl processDirectTransaction end #########" + new Date().getTime());
                        transactionLogger.debug("DirectTransactionRESTImpl processDirectTransaction diff #########" + (new Date().getTime() - date3.getTime()));
                        //writeDirectTransactionResponse.setSuccessRestTransactionResponse(response, directKitResponseVO, commonValidatorVO);

                        if ("pending3DConfirmation".equalsIgnoreCase(directKitResponseVO.getStatus()))
                        {
                            writeDirectTransactionResponse.setSuccessRest3DTransactionResponse(response, directKitResponseVO, commonValidatorVO);
                        }
                        else
                        {
                            writeDirectTransactionResponse.setSuccessRestTransactionResponse(response, directKitResponseVO, commonValidatorVO);
                        }
                    }
                }
                else
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Payment type/Action Type or registration id or should not be empty"));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide proper Payment Type/Action Type or registration id");
                }
            }
            catch (PZConstraintViolationException e)
            {
                logger.error("PZConstraint Violation Exception while placing transaction via Rest", e);
                transactionLogger.error("PZConstraint Violation Exception while placing transaction via WebService", e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
            }
            catch (PZDBViolationException e)
            {
                logger.error("PZ DBViolation exception while placing transaction via Rest", e);
                transactionLogger.error("PZDBViolationException while placing transaction via WebService", e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (PZTechnicalViolationException e)
            {
                logger.error("PZTechnicalViolationException while placing transaction via Rest", e);
                transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzTechnicalConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (PZGenericConstraintViolationException e)
            {
                logger.error("PZGenericConstraintViolationException while placing transaction via Rest", e);
                transactionLogger.error("PZGenericConstraintViolationException while placing transaction via WebService", e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "This functionality is not allowed for your gateway. please contact tech side for support");
            }
            catch (NoSuchAlgorithmException e)
            {
                logger.error("PZ GenericConstraintViolation exception while placing transaction via Rest", e);
                transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
        }
        else
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Payment type/Action Type or registration id or should not be empty"));
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide proper Payment Type/Action Type or registration id");
        }
        transactionLogger.error("Rest process Registration Transaction Response---" + gson.toJson(response));
        return response;
    }

    public Response processStandAloneTokenRegistration(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        Response response1 = new Response();

        restPaymentRequestVO = readDirectTransactionRequest.readStandAloneTokenRequest(paymentRequest);
        response1 = processStandAloneTokenRegistrationJSON(restPaymentRequestVO);

        return response1;
    }

    public Response processStandAloneTokenRegistrationJSON(RestPaymentRequestVO paymentRequestVO)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        TokenRequestVO tokenRequestVO = null;
        CommonValidatorVO commonValidatorVO = null;
        Response response = new Response();
        RegistrationHelper registrationHelper = new RegistrationHelper();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        TokenResponseVO tokenResponseVO = null;
        Functions functions = new Functions();

        try
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse != null && !authResponse.equals("") && authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---" + authResponse);
                writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                return response;
            }
            String restRequest=gson.toJson(paymentRequestVO);
            if(functions.isValueNull(restRequest))
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(restRequest);
                    if (jsonObject.has("cardVO"))
                    {
                        JSONObject cardJson=jsonObject.getJSONObject("cardVO");
                        String cardnumber="";
                        String expiryMonth="";
                        String expiryYear="";
                        String cvv="";
                        if(cardJson.has("number"))
                            cardnumber = functions.maskingPan(cardJson.getString("number"));
                        if(cardJson.has("expiryMonth"))
                            expiryMonth = functions.maskingNumber(cardJson.getString("expiryMonth"));
                        if(cardJson.has("expiryYear"))
                            expiryYear = functions.maskingNumber(cardJson.getString("expiryYear"));
                        if(cardJson.has("cvv"))
                            cvv = functions.maskingNumber(cardJson.getString("cvv"));

                        cardJson.put("number",cardnumber);
                        cardJson.put("expiryMonth",expiryMonth);
                        cardJson.put("expiryYear",expiryYear);
                        cardJson.put("cvv",cvv);
                        jsonObject.put("cardVO",cardJson);
                    }
                    transactionLogger.error("Rest process Stand Alone Token Registration Request---" + jsonObject.toString());
                }
                catch (JSONException e)
                {
                    transactionLogger.error("JSONException---->",e);
                }
            }

            commonValidatorVO = readDirectTransactionRequest.readRequestForTokenTransactionRegistration(paymentRequestVO, request1);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while cancelling transaction");
                return response;
            }

            commonValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

            commonValidatorVO = restCommonInputValidator.performRestTokenRegistrationValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return response;
            }
            else
            {
                commonValidatorVO = registrationHelper.performVerificationWithStandAloneToken(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }

                commonValidatorVO = transactionHelper.performRESTAPISystemCheck(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }
                tokenRequestVO = new TokenRequestVO();
                tokenRequestVO.setReserveField2VO(commonValidatorVO.getReserveField2VO());
                tokenRequestVO.setPartnerDetailsVO(commonValidatorVO.getPartnerDetailsVO());
                tokenRequestVO.setMerchantDetailsVO(commonValidatorVO.getMerchantDetailsVO());
                tokenRequestVO.setTransDetailsVO(commonValidatorVO.getTransDetailsVO());
                tokenRequestVO.setCardDetailsVO(commonValidatorVO.getCardDetailsVO());
                tokenRequestVO.setAddressDetailsVO(commonValidatorVO.getAddressDetailsVO());
                //tokenRequestVO.setGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
                if (functions.isValueNull(commonValidatorVO.getMerchantDetailsVO().getLogin()))
                {
                    tokenRequestVO.setRegistrationGeneratedBy(commonValidatorVO.getMerchantDetailsVO().getLogin());
                }
                else
                {
                    tokenRequestVO.setRegistrationGeneratedBy(commonValidatorVO.getPartnerDetailsVO().getCompanyName());
                }
                tokenRequestVO.setCardType(commonValidatorVO.getCardType());
                tokenRequestVO.setPaymentType(commonValidatorVO.getPaymentType());
                tokenRequestVO.setCustomerId(commonValidatorVO.getCustomerId());
                if (functions.isValueNull(commonValidatorVO.getPartnerDetailsVO().getPartnerId()))
                {
                    tokenRequestVO.setPartnerId(commonValidatorVO.getPartnerDetailsVO().getPartnerId());
                }
                if (functions.isValueNull(commonValidatorVO.getTerminalId())/* || (commonValidatorVO.getTerminalVO()!= null && functions.isValueNull(commonValidatorVO.getTerminalVO().getTerminalId()))*/)
                {
                    tokenRequestVO.setTerminalId(commonValidatorVO.getTerminalId());
                }

                Date date3 = new Date();
                transactionLogger.debug("DirectTransactionRESTImpl processTokenGeneration start #########" + date3.getTime());
                tokenResponseVO = restDirectTransactionManager.processTokenGeneration(tokenRequestVO);
                transactionLogger.debug("DirectTransactionRESTImpl processTokenGeneration end #########" + new Date().getTime());
                transactionLogger.debug("DirectTransactionRESTImpl processTokenGeneration diff #########" + (new Date().getTime() - date3.getTime()));
                transactionLogger.debug("NotificationUrl:::::" + commonValidatorVO.getTransDetailsVO().getNotificationUrl());
                if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getNotificationUrl()))
                {
                    com.directi.pg.AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                    asyncNotificationService.sendNotification(commonValidatorVO, tokenResponseVO.getStatus(), tokenResponseVO.getRegistrationToken());
                }
                writeDirectTransactionResponse.setSuccessRestTokenGenerationResponse(response, tokenResponseVO, commonValidatorVO);
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while placing transaction via Rest", e);
            transactionLogger.error("PZ PZConstraintViolationException while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, null);
        }
        catch (PZGenericConstraintViolationException e)
        {
            logger.error("PZ GenericConstraintViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("NoSuchAlgorithmException::" + e.getMessage());
        }
        transactionLogger.error("Rest process Stand Alone Token Registration Response---" + gson.toJson(response));
        return response;
    }

    @Override
    public String processCheckout()
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        logger.debug("Inside processCheckout:::");
        transactionLogger.debug("Inside processCheckout:::");

        String data = "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Razorpay Checkout</title>\n" +
                "    <link rel=\"icon\" href=\"data:;base64,=\">\n" +
                "    <meta http-equiv=\"pragma\" content=\"no-cache\">\n" +
                "    <meta http-equiv=\"cache-control\" content=\"no-cache\">\n" +
                "    <meta name=\"viewport\" content=\"user-scalable=no,width=device-width,initial-scale=1,maximum-scale=1\">\n" +
                "    <link rel=\"stylesheet\" href=\"http://localhost:8080/RazorPaySite/pz.Final/checkout.css\">\n" +
                "  </head>\n" +
                "  \n" +
                "  <script src=\"http://localhost:8080/RazorPaySite/pz.Final/checkout-frame.js\"></script>\n" +
                "  <style>@font-face{font-family:'lato';src:url(\"https://cdn.razorpay.com/lato3.eot?#iefix\") format('embedded-opentype'),url(\"https://cdn.razorpay.com/lato3.woff2\") format('woff2'),url(\"https://cdn.razorpay.com/lato3.woff\") format('woff'),url(\"https://cdn.razorpay.com/lato3.ttf\") format('truetype'),url(\"https://cdn.razorpay.com/lato3.svg#lato\") format('svg');font-weight:normal;font-style:normal}</style>\n" +
                "<div id=\"container\" class=\"mfix animations drishy font-loaded\" tabindex=\"0\"> <div id=\"backdrop\"></div> <i id=\"powered-by\"><a id=\"powered-link\" href=\"https://razorpay.com?ref=org-in-chk\" target=\"_blank\">\uE608</a></i> <div id=\"modal\" class=\"mchild\"> <div id=\"modal-inner\" class=\"shake\"> <div id=\"overlay\" class=\"showable\" style=\"display: none;\"></div> <div id=\"emi-wrap\" class=\"showable mfix\"></div> <div id=\"error-message\" class=\"showable\" style=\"display: none;\"> <div id=\"fd-t\"></div> <div class=\"spin\"><div></div></div> <div class=\"spin spin2\"><div></div></div> <span class=\"link\">Go to payment</span> <button id=\"fd-hide\" class=\"btn\">Retry</button> </div> <div id=\"content\"> <div id=\"header\"> <div id=\"modal-close\" class=\"close\">×</div>  <div id=\"logo\"> <img src=\"https://your-awesome-site.com/your_logo.jpg\"> </div>  <div id=\"merchant\">  <div id=\"merchant-name\">Merchant Name</div>  <div id=\"merchant-desc\">Purchase Description</div> <div id=\"amount\"><i>\uE600</i><span class=\"iph\">₹</span> 50</div> </div> </div> <div id=\"body\" class=\"\"> <div id=\"topbar\"> <div id=\"top-right\"> <div id=\"user\"></div> <div id=\"profile\"> <ul> <li>Log out</li> <li>Log out from all devices</li> </ul> </div> </div> <div id=\"top-left\"> <i class=\"back\">\uE604</i> <div id=\"tab-title\"></div> </div> </div> <form id=\"form\" method=\"POST\" novalidate=\"\" autocomplete=\"off\" onsubmit=\"return false\"> <div id=\"form-fields\"> <div id=\"form-common\" class=\"showable drishy screen\"> <div class=\"pad\"> <div class=\"elem-wrap\"><div class=\"elem elem-contact invalid mature\"> <i>\uE607</i> <div class=\"help\">Please enter a valid contact number</div> <label>Phone</label> <input class=\"input\" id=\"contact\" name=\"contact\" type=\"tel\" value=\"\"> </div></div> <div class=\"elem-wrap\"><div class=\"elem elem-email filled mature\"> <i>\uE603</i> <div class=\"help\">Please enter a valid email. Example:<br> you@example.com</div> <label>Email</label> <input class=\"input\" name=\"email\" type=\"email\" id=\"email\" required=\"\" value=\"support@razorpay.com\" pattern=\"^[a-zA-Z0-9.!#$%&amp;’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)+$\"> </div></div>  </div> <div class=\"\"> <div class=\"legend\">Select payment method</div> <div id=\"payment-options\" class=\"grid clear count-3\">  <div class=\"payment-option item\" tab=\"card\"><label><i>\uE609</i>Card/EMI</label></div>   <div class=\"payment-option item\" tab=\"netbanking\"><label><i>\uE60A</i>Netbanking</label></div>   <div class=\"payment-option item\" tab=\"wallet\"><label><i>\uE60B</i>Wallet</label></div>  </div> <div class=\"clear\"></div> </div> </div>  <div class=\"tab-content showable screen\" id=\"form-card\"> <div id=\"show-saved-cards\" class=\"text-btn left-card\">Use saved cards</div> <div id=\"saved-cards-container\"></div> <div id=\"show-add-card\" class=\"text-btn left-card\">Add another card</div> <div class=\"pad\"> <div id=\"add-card-container\"> <div class=\"elem-wrap two-third\"><div class=\"elem invalid\" id=\"elem-card\"> <div class=\"cardtype\" cardtype=\"\"></div> <label>Card Number</label> <i>\uE605</i> <span class=\"help\">Please enter a valid card number</span> <input class=\"input\" type=\"tel\" id=\"card_number\" name=\"card[number]\" required=\"\" autocomplete=\"off\" maxlength=\"19\" value=\"\"> </div></div> <div class=\"elem-wrap third\"><div class=\"elem elem-expiry invalid\"> <label>Expiry</label> <i>\uE606</i> <input class=\"input\" type=\"tel\" id=\"card_expiry\" name=\"card[expiry]\" placeholder=\"MM / YY\" required=\"\" pattern=\"(0[1-9]|1[0-2]) ?\\/ ?(1[6-9]|[2-9][0-9])\" maxlength=\"7\" value=\"\"> </div></div> <div class=\"elem-wrap two-third\"><div class=\"elem elem-name filled\"> <label>Card Holder's Name</label> <i>\uE602</i> <input class=\"input\" type=\"text\" id=\"card_name\" name=\"card[name]\" required=\"\" value=\"Harshil Mathur\" pattern=\"^[a-zA-Z .]+$\"> </div></div> <div class=\"elem-wrap third\"><div class=\"elem elem-cvv invalid\"> <label>CVV</label> <i>\uE604</i> <input class=\"input\" type=\"tel\" id=\"card_cvv\" inputmode=\"numeric\" name=\"card[cvv]\" maxlength=\"4\" required=\"\" pattern=\"[0-9]{4}\" value=\"\"> </div></div> <div class=\"clear\"></div>  <div id=\"should-save-card\" class=\"double\"> <label class=\"first\" for=\"save\" tabindex=\"0\"> <input type=\"checkbox\" id=\"save\" name=\"save\" value=\"1\" checked=\"\"> <span class=\"checkbox\"></span> Remember Card </label> <div class=\"second\"> <a id=\"moreinfo\" class=\"link\" href=\"https://razorpay.com/flashcheckout\" target=\"_blank\"> More Info </a> </div> <div class=\"clear\"></div> </div>  </div> <label id=\"nocvv-check\" for=\"nocvv\"> <input type=\"checkbox\" id=\"nocvv\" disabled=\"\"> <span class=\"checkbox\"></span> My Maestro Card doesn't have Expiry/CVV </label> <div id=\"elem-emi\" class=\"double\"> <div class=\"first disabled dropdown-parent\" tabindex=\"0\" id=\"emi-check-label\"> <span class=\"checkbox\"></span> Pay with EMI <div id=\"emi-select\" class=\"select dropdown\"> <div id=\"emi-plans-wrap\"></div> <div class=\"option\" value=\"\">Pay without EMI</div> </div> <span class=\"help dropdown\">EMI is available on HDFC, Kotak &amp; Axis Bank Credit Cards. Enter your credit card to avail.</span> </div> <div class=\"second\"> <span id=\"view-emi-plans\" class=\"link\"> <a>View EMI Plans</a> </span> </div> <div class=\"clear\"></div> </div> </div> </div>   <div class=\"tab-content showable screen\" id=\"form-netbanking\">  <div id=\"netb-banks\" class=\"clear grid count-3\">  <div class=\"netb-bank item radio-item\"> <input class=\"bank-radio\" id=\"bank-radio-SBIN\" type=\"radio\" name=\"bank\" value=\"SBIN\"> <label for=\"bank-radio-SBIN\" class=\"radio-label mfix\"> <div class=\"mchild l-1 item-inner\"> <img src=\"data:image/png;base64,R0lGODlhKAAoAMQQAPD2/EGI2sTa86fI7m2k4l6b3+Lt+dPk9nyt5SR21Hut5cXb9Jm/61CS3f///xVt0f///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAABAALAAAAAAoACgAAAXyICSO5GgMRPCsbEAMRinPslCweL4WAu2PgoZumGv0fiUAgsjMIQBIEUDVrK4C0N8hYe0+EgcfgOvtJrJJarmLlS3XZURJAIcfRcJ6uQHU20U3fmUFEAaCcCeHayiKZSlwCgsLCnBqXgcOmQ4HjUwKmpqUnToLoJkLo6SmDqipOJ+moq4smJqca5ZdDJkMlQRwDZl5jgPAwnAwxg7DXjGBXsHLa4QQdHvHZXfMVdHbTXx90NhddyJvVt1ecmld6VZtMmPo40xnPlvc9ENgSFPf+jng9Tunw92QJ1FIBBliEIeRhDNsFNHHA+KPE4/SuYCRMAQAOw==\"> <div>SBI</div> </div> </label> </div>  <div class=\"netb-bank item radio-item\"> <input class=\"bank-radio\" id=\"bank-radio-HDFC\" type=\"radio\" name=\"bank\" value=\"HDFC\"> <label for=\"bank-radio-HDFC\" class=\"radio-label mfix\"> <div class=\"mchild l-2 item-inner\"> <img src=\"data:image/png;base64,R0lGODlhKAAoAKIAAL/S4+4xN/WDh+/0+PJaXwBMj////+0jKiH5BAAAAAAALAAAAAAoACgAAAOqeLrca9C4SauL0uqtMP+VB46MSJLmCaZqh71wHBGcbNv0du9vrvFAg88S5A1DxduRklTWYIWodBoFwJaTGHVbsPaeLy7Vi8FeoGIpeXbmpdXN27sat827ddl9jSP4/1pzfAYCf39ZaG+DZg2BildgGHuQOjAAl5iZlwOUP3lflZ9soaJCkaKMJaUQqQ+rphuGsn5ls4YBLaoQuSsRvB8sv0y+wp67xUTEIwkAOw==\"> <div>HDFC</div> </div> </label> </div>  <div class=\"netb-bank item radio-item\"> <input class=\"bank-radio\" id=\"bank-radio-ICIC\" type=\"radio\" name=\"bank\" value=\"ICIC\"> <label for=\"bank-radio-ICIC\" class=\"radio-label mfix\"> <div class=\"mchild l-3 item-inner\"> <img src=\"data:image/png;base64,R0lGODlhKAAoAOZGAP/58evKy/ry8vzOk/qpQsRfZMJHLvqvUP7t1uGvsbUxL79SV9iVmPmjNbpFSueAKbU3PctVLfCPKPvCeP7z5PDX2PzIhvSWKNVkLOa8vsltcfu2Xc56frk4L850cNeUl/Xk5f3myd5yKtOHi+uHKfzUoMdOLf3arr5MSf/58r5ALv3gu9+EU9yipLo/PPXe1/rs5eCjlvTYytybl+qxle/FsMRZVvu8a+J5Kuq9sNlrK/7nyeOAN+Wee/3gvNyWifWcNeGopOuONvmdJ////7AqMP///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAAEYALAAAAAAoACgAAAf/gEaCg4SFhoICCR4oRY1FLh45h5OUgwEejpmZNi+VnoIBBZqjjhAVn5MwmJkQDAJEAgujKACohTUumgsVRL1EGaQztoIAPQqaECC+vcCjDsMALKQJy70cpEWnnwg8pBDVRCDYRUHbQsejBdWx4x+eCEDoowvLILLtlQANBuNFDAEBGEDoV8TdJAAHMBBcmMngoQkS+hVoESCDBoYxJpUYwo+UgwDVLhKUcYhCgwfYRryqxoCggVqGNnB0BhIckWv9WBw6MQSlpgIrwQkg2MGCIX1DTGjiYNNXAoIiEBgaMOSCJmpNe4nE1oHA0QZDRGQakbXX0H4PThjaOCRCpgAC+xg4KEKvWot+JhrAJERgyJAO/XgtE8X1wgBDCPwOIVjTbD8SXg1Z8EuCYLWn2EQMWXGob0/Ly7ZqwjBkQ0nFPrGpWzZwVIQhDSgcCqE44jimvgKQer15ElXF8kZh7dVSkw6/Eyj99qsUmzJfhBspwOH3QKXlnz1Wy6SCRPW9h7DPHEXWVwXppP0SAB9esd8LKkZlWKY7ggTFBKR6Yuv+gltHDlRDw32KHcDeJIm5VxsGBhjwQzXYbXAgJZ4p6B4F1figmFHDGMGThYrdYNMEE4TQ4SAHgOjXDlmdOAgFFbpnQVMuFgKATO4dkAI4NU4SwgQHEDDAjsv0OEggADs=\"> <div>ICICI</div> </div> </label> </div>  <div class=\"netb-bank item radio-item\"> <input class=\"bank-radio\" id=\"bank-radio-UTIB\" type=\"radio\" name=\"bank\" value=\"UTIB\"> <label for=\"bank-radio-UTIB\" class=\"radio-label mfix\"> <div class=\"mchild l-2 item-inner\"> <img src=\"data:image/png;base64,R0lGODlhKAAoAMQQAOvJ1vry9bM1Z7hDcfXk68JehtaTrvDX4eGuwsdrkNKGpMx5mr1Qe+a8zNuhuK4oXf///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAABAALAAAAAAoACgAAAXdICSOZGmeaKqubIsGgSunyjLf5PE8B44Xu4Jv1tjtEMNWYGB8DGJJlaG5M0RTBAF1R7ieEttdwlsChI0A8ohx3g3UEEfb2PMGtPMxWTHnknV9VmRAcwJQVwh9D0h2THNvZFN9aV4EikJkYH1dXmZ9CmqObQJ4bZArknMOqW0OK3d9kKJnhioLipRFnymeeSSEc3UmwG2cIpZ9mCWJgSZ8fYwjsI+H0qVnTySsZ9HLioIQyHPKJ2yFnMRnwie9bWO6czYrt5OzYbUrWX0MAP3+//3WqTgAsCAlOAivhAAAOw==\"> <div>Axis</div> </div> </label> </div>  <div class=\"netb-bank item radio-item\"> <input class=\"bank-radio\" id=\"bank-radio-KKBK\" type=\"radio\" name=\"bank\" value=\"KKBK\"> <label for=\"bank-radio-KKBK\" class=\"radio-label mfix\"> <div class=\"mchild l-3 item-inner\"> <img src=\"data:image/png;base64,R0lGODlhKAAoANUtAL/N3O/z9s/a5d/m7kBqlyBRhTBdjjsxYFB2oI+owoCcup+0y3cqTK/B1HCPsVB2n2CDqPvGyB41avRxdkovW+4qMmgsUfBHTf7x8fm4u4tviw82b+idpNy8xf3j5PNjaI2Mp7dqfPaOkntjg5QnQsEhM4hFYks9ad4eKRBEfe0cJP///wA4dP///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAAC0ALAAAAAAoACgAAAb/wJZwSCwaj8ikcslsFgOAqDTgdA4SiBRry92mEIlB9bggdM9oFmExFgIK6fi5AHAGEPKuZCNHUJUCBnksEhYlKgd5BgJJAlp5FiqSiIMpjEaOgywMk5SVl0MBgmgFcFyck4maBn9CDmcEDSuzKwtwqJIHKQoDtAMKj1wOQwBnELTIKwq4KhoBybMDo1x1LXhcBNDIHJMT2rQBplsILQNnvd8rEZIX6bTFXVddD8lQyOsVGO60ZlwJ/VsWRIMQrAAEAOsiaGvg4EGCZysadCEQjEWvTGlMiIAWqIulFQE8npklDs0IjhW9EACYZoW5OCkg+kqpiYtLOQCgTat55tlOui4Ck72CqUBByYqyBByVlSwDCJoBZwVLwdLBrAALHjiUSctDBZBG5wmINjGBR67fMFxQgWxAlGRm/b3komDfig+SOuwrKYYli7HpJkw6AVibgi7kWsDjkiIntAAhOukqjGzBmWothnaBwHSFAGDMVPGi1cDvsFA/44SuyaoIxjyrPx15rVqybCQd5XBCQYLCoEVL7sg54FuTHydveJLCXKWMcjVs2hC5kgUmGDHSk0CREqVV9u/gwy8JAgA7\"> <div>Kotak</div> </div> </label> </div>  <div class=\"netb-bank item radio-item\"> <input class=\"bank-radio\" id=\"bank-radio-YESB\" type=\"radio\" name=\"bank\" value=\"YESB\"> <label for=\"bank-radio-YESB\" class=\"radio-label mfix\"> <div class=\"mchild l-4 item-inner\"> <img src=\"data:image/png;base64,R0lGODlhKAAoANUxAMg0KRBcmfTW1NNcVNdqY8tBOOmtqfvx8c9PRjBypyBnoPjk4sJ9gKGTpL9EP8ptajxvn9WVlFl3nuGSjfDJxrSruY16j2yQtK5la6qCj8dfXFyFrdp4cbCdq2mCpZSWq52FlixkmaZ1gHiNrL5wcqu7zrWAh3ybu1VqkXR/npGIncNRTc56eEB9rb/T5MQmGwBRkv///wAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACH5BAEAADEALAAAAAAoACgAAAb/wJhwSCwaj0bDAslsNgUTp3QaOyAO1CySYNB6iQbEdywAUMZeKwHt5byWR5d8Tq/b74YX6y6H+f+AgYKCKAAAEIOJioswIQ4vIoySkgEYLwUJk5qJFi8vFpuhgBIALysKoqIJjy8pqaGVng8Br5sgngAStZoepS8Mu5MJA54FiMGLAQyeLxnIjA3MCKjPiRe+LyrVicPMGrTbgcrMLyPhgtHMJODnftfMABvtfy0E5CbzfgER5AUt+TAqkHvRAeCJAuQGUGtXb+CHfPsGztrVoqLFiwLJAShxsaPHj0UOEBhAEpunKGyOlBlYDEtKlSY9dXmJZCWzATSfYDuTk8nKBzU9n7zpGQQAOw==\"> <div>Yes</div> </div> </label> </div>  </div> <div class=\"elem-wrap pad\"><div id=\"nb-elem\" class=\"elem select invalid\"> <i class=\"select-arrow\">\uE601</i> <div class=\"help\">Please select a bank</div> <select id=\"bank-select\" name=\"bank\" required=\"\" class=\"input\" pattern=\"[\\w]+\"> <option selected=\"selected\" value=\"\">Other Banks</option>  <option value=\"ALLA\">Allahabad Bank</option>  <option value=\"ANDB\">Andhra Bank</option>  <option value=\"UTIB\">Axis Bank</option>  <option value=\"BBKM\">Bank of Bahrain &amp; Kuwait</option>  <option value=\"BARB_C\">Bank of Baroda - Corporate Banking</option>  <option value=\"BARB_R\">Bank of Baroda - Retail Banking</option>  <option value=\"BKID\">Bank of India</option>  <option value=\"MAHB\">Bank of Maharashtra</option>  <option value=\"CNRB\">Canara Bank</option>  <option value=\"CSBK\">Catholic Syrian Bank</option>  <option value=\"CBIN\">Central Bank of India</option>  <option value=\"CIUB\">City Union Bank</option>  <option value=\"CORP\">Corporation Bank</option>  <option value=\"COSB\">Cosmos Co-Operative Bank</option>  <option value=\"DBSS\">DBS Bank</option>  <option value=\"BKDN\">Dena Bank</option>  <option value=\"DEUT\">Deutsche Bank</option>  <option value=\"DCBL\">Development Credit Bank</option>  <option value=\"DLXB\">Dhanalakshmi Bank</option>  <option value=\"FDRL\">Federal Bank</option>  <option value=\"HDFC\">HDFC Bank</option>  <option value=\"ICIC\">ICICI Bank</option>  <option value=\"IBKL\">IDBI Bank</option>  <option value=\"IDFB\">IDFC Bank</option>  <option value=\"VYSA\">ING Vysya Bank</option>  <option value=\"IDIB\">Indian Bank</option>  <option value=\"IOBA\">Indian Overseas Bank</option>  <option value=\"INDB\">IndusInd Bank</option>  <option value=\"JAKA\">Jammu &amp; Kashmir Bank</option>  <option value=\"JSBP\">Janata Sahakari Bank Ltd</option>  <option value=\"KARB\">Karnataka Bank</option>  <option value=\"KVBL\">Karur Vysya Bank</option>  <option value=\"KKBK\">Kotak Mahindra Bank</option>  <option value=\"LAVB_C\">Lakshmi Vilas Bank - Corporate Banking</option>  <option value=\"LAVB_R\">Lakshmi Vilas Bank - Retail Banking</option>  <option value=\"NKGS\">North Kanara GSB Co-op. Bank</option>  <option value=\"ORBC\">Oriental Bank of Commerce</option>  <option value=\"PMCB\">Punjab &amp; Maharashtra Co-operative Bank</option>  <option value=\"PSIB\">Punjab &amp; Sind Bank</option>  <option value=\"PUNB_C\">Punjab National Bank - Corporate Banking</option>  <option value=\"PUNB_R\">Punjab National Bank - Retail Banking</option>  <option value=\"RATN\">Ratnakar Bank</option>  <option value=\"ABNA\">Royal Bank of Scotland</option>  <option value=\"SRCB\">Saraswat Co-Operative Bank</option>  <option value=\"SVCB\">Shamrao Vithal Co-operative Bank</option>  <option value=\"SIBL\">South Indian Bank</option>  <option value=\"SCBL\">Standard Chartered Bank</option>  <option value=\"SBBJ\">State Bank of Bikaner &amp; Jaipur</option>  <option value=\"SBHY\">State Bank of Hyderabad</option>  <option value=\"SBIN\">State Bank of India</option>  <option value=\"SBMY\">State Bank of Mysore</option>  <option value=\"STBP\">State Bank of Patiala</option>  <option value=\"SBTR\">State Bank of Travancore</option>  <option value=\"SYNB\">Syndicate Bank</option>  <option value=\"TNSC\">Tamil Nadu State Apex Co-Operative Bank</option>  <option value=\"TMBL\">Tamilnad Mercantile Bank</option>  <option value=\"UCBA\">UCO Bank</option>  <option value=\"UBIN\">Union Bank of India</option>  <option value=\"UTBI\">United Bank of India</option>  <option value=\"VIJB\">Vijaya Bank</option>  <option value=\"YESB\">Yes Bank</option>  </select> </div></div> </div>   <div class=\"tab-content showable screen\" id=\"form-wallet\">  <div id=\"wallets\" class=\"clear grid count-4\">  <div class=\"wallet item radio-item\"> <input type=\"radio\" name=\"wallet\" value=\"mobikwik\" id=\"wallet-radio-mobikwik\"> <label for=\"wallet-radio-mobikwik\" class=\"radio-label mfix\"> <img class=\"wallet-button colored mchild item-inner l-1\" style=\"height:19px\" src=\"data:image/gif;base64,R0lGODlhbAAfAKIHAJvl5ODt7UPS0L/w73/h4P///wDDwf///yH5BAEAAAcALAAAAABsAB8AAAP/eBfczgPIR4O6OOvNu/9gqBBFaWnDQAiCMYhwLM/cUJpHurZG77u0oHCYAdwKgJ9y2TsRn9CMjnUsEJjMQI8Q7QanPB/peFWulocW18sWlbFV609wC/cO27bek8QajFVvLjcDc3gGa3uKGIJLcXI9dISGPQCLlyN+gIE+NpM+AoWDKkaYbY0/j5CSn1uikXmmXqiVR5tlnq2IfQahSQKys0ysVmNyxCWvLm8ABIXAwVG0uQWIN1cBccoqPwAtlhm4HGVczQROB8XOMOSi0OFLxFfGK4/KAXaRG+L7eTwvF8IUYOdKnwZUubRYK3HO3o80cwAe7CGxCAtL/y68qQjC/5sAAO76zTnCwxg1kw8jVcwGDwiIjAd4cYwRcuKPbCbE2GP1ClgsfD9IZFr2A+CAixCBvLK0Q2KLdwo/skhhMKkAQcTCGDvSq5VPpcMGlsn3J4c/igp7KegDrRrFtVtw1XwTQBDOAsq2lvjlVZQNUM1AHRAkIZUoLv94vHNrAG6sf3L1yRwMimTQOJFaOWsy9sIrbm/T+QB5Vgk4BTwshIHmg7ILd0uHGjiibOENtpr7JK1IrvMFtoeTKnFSxtLwkJF7jda4xfJlrpmTdSpzqDEGd/wUwC6t5N12RJWKu6aqRGIZ2ku28uI5XW1rDGyzm+0VPAz5sgrE9HDLYv/42rL+UbYVEwNWtlcnT+2GQUniMUdfaeBshJohdkCTnHKxXGELHAx1F8FDari2WHv7lVAdaYgoOKFa81XSYor/+YCXDxYQUOAw3vgxRx5phXJUa2/4GEYA9YXWIheMGWBBkgAlB41igxnDi45UFuXYEi/Q4mKRLsn2QhgBvRcji2kFFgFZVer4zou9nIDLlOBwyRGUfSRSp4PkvfOGlmlisWYGRA6ADqApRANCAWj2mYWhmCQAADs=\"> </label> </div>  <div class=\"wallet item radio-item\"> <input type=\"radio\" name=\"wallet\" value=\"payzapp\" id=\"wallet-radio-payzapp\"> <label for=\"wallet-radio-payzapp\" class=\"radio-label mfix\"> <img class=\"wallet-button colored mchild item-inner l-2\" style=\"height:24px\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHAAAAAlCAMAAACtfZ09AAAAY1BMVEXuQEGBmb/+7+9BZZ/AzN/839/2n6DrICLpEBLxYGH7z9DsMDHvUFH4r7Dv8vf1j5Df5e/ycHHQ2edhf68iTI+gss8SP4dxjLcxWZewv9eQpcdRcqf5v8DzgIEDM3/oAQP///+4CVglAAAAIXRSTlP//////////////////////////////////////////wCfwdAhAAADRUlEQVR4Ab2Ue3PiOBDEZflh/DAG8whhpXV//095jKYlqyp1x20C239ESE755+6ZkcELZacaz/QyYN907Trg7wCdN7tV5N4PrJvjsEYZvBfo5rFdc9XvA9rJmzWJ6vAeYD93ubGknX0LsJ49RaUSelDv7VK7I6+1/x8YP3JGTKcHDoXqfoDoIr8vqGQBsJd1AeBXqgEmcdxwUlSNBVBzM9cKtCvVu5WyQPE7SgCH8KvU0wNweiznPVBHgwPNcjbGVNkGaNaoYwAmTPrcFsCv30kL8EFgJUul/E8A3Uo5miWwzXvpuCY5w1Q0jEYWNvh5AxbYXwncK/AUNkCdZp4GCbRrxkglE448NtsdwXfM4JuXjwCUJK+3wBDyfWGwm8Fag2v5qhAaplGB8nfqjwm4k2yTJikm8CnvBBCAYvBUBmAp+/AnL0anKY5GOAxt4HPn2BUR2AsfKEUXSOA7aM/cwJrdpZAC1POSHQOT6qQGpwiUtWO32FmsM/7G6GnNNrwwYHXyger2WK44C1xO6Jwdkwx6NdgiAqWcDZwsJnTsiHoIdAPUzjngEvvxsauhtaIKeXYhcNGzMp/f1ircE6hmKJc6Vv8DlPY9opaNdxKD1z2B0MMDsvFqGGJNIJ/wWdaxI2BgtStLfrcEDjFMnS/4DNQILIPpfNYGpjsiAtPgDS4LvvUQ4KRTy4GTwAcaLquqWog4FcWZwNgxyYajwdH7VsrEnmFttGMfmx4iE/bY1GqPx8YHG5XihXDKb23zpWjgIlL6EWn3Zey1JLERyY7a64Vwz29tsdHlQLh4jNSxGZDunbXy1+vYV/p28Nq+FoVGWnEwM4NdNOgfagOQgyfis34D9rLnfKp2AO5SKASdGCHKQLoz2WOaeRocY3xu2yKWGkkmYoYUy0jKLxrU8YxAXtupaB50O23AgecAv8xkwIjpUpd7dkaRDN6wAW/hBkKXzTw8IyTQfukZnwF7p6rhKAugekhLuDx+HdKvPaqw7/OZR5+avk/NgCh9eZLBt2RSon+i6dtAt/6ZBuN9GvwfGXwmM/omBfp94PQUtDOdn5wF9VNg+1/hHRke9Qpg8y/hzSm81wJt+zy8lwL98/BeCxwZ3vf0D5XUzxaJRoO1AAAAAElFTkSuQmCC\"> </label> </div>  <div class=\"wallet item radio-item\"> <input type=\"radio\" name=\"wallet\" value=\"payumoney\" id=\"wallet-radio-payumoney\"> <label for=\"wallet-radio-payumoney\" class=\"radio-label mfix\"> <img class=\"wallet-button colored mchild item-inner l-3\" style=\"height:18px\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAI0AAAAgCAMAAAAYAgunAAAAMFBMVEXT46mRuSr7/PeGsxakxk/I3JXz9+fr8ti30nTj7sqyzmqcwD+sy17d6b2+1oL///+7L4YrAAAAEHRSTlP///////////////////8A4CNdGQAAA+xJREFUeNrNV4tuIyEMNAbMw0D+/29vbEi2Fylt79RInSgrwMaeHWykpdv/gXMY5fbT+F82smL6PWyYtM42C/8KNkDTCH1+C5v5W9iwwdiMn2fDhm9QuIYyMxGt3rP8LBuW1or9yqeMuMBN9lBKTgG62I6fBZWujjTyZw3SBlyyO3BOqY/wlg7vMQYHXna+9ssRSC5OGVFXTm9hE6Jmw4L4/bU462LTjM16F5t+CqNHbSwFEGEs4GHw0ZM2oa6kXY7VN1wDB5/ZY+phr4iwupH54yLY1DOhEGZbfaAqcmMmPLal5Mx/s4lBFxU386xjjDVZch+jnk3csi0/nBqMsJLstHPdfTlj68m/8gc2DWxIQ0oaopL0MMo21KjyxCaGvGNIDdEQ1ogOJRgka9wYlotzONPeMPM9AX/NImon4mGR9YlNI5pz0kBR5BDOe2ocH7Vpnvf0l9VdXVWNUF81Wav5ahh72V6pJRh9GhY7N105d/hO7jGQhyJw/cAmgw0zu/oYTo10J51vL7RhhCQRbLC8IhN56SZIUZuIEPJDnIwkhWF1rmWY9MxlBUSBbcnpE7IqFkOhD13SNM6SYpVDeprvxebSRpDdF7FGNs/gaVqk5mRXgCPXGPbUG6XpCTQVGYxAMRsiNFe6A8NlBOM2J1YDSQ3J3VZUeaUN2KgPKmK5wIhyg0Kn5vzNuTtlAKEmm0I0gawgUhDBSl1AxKgdBF2F0VMJADPaJ4cECM23F3XzTTbhsKmHTdCEn0awuRdOwcTrzbWpawoShaB3NlOR8pTND2sTNdgvaBZzybBSRJpTN/uiYtRVna3Z+RHLgGmXzWGjnnDqxabc2fR/0kYzZTIUturpgo0W4OopwPIXT2L9BG6W3svG/IAOqtQxtM55rU17NIRVsXD9WxuNQ3hj50qNecDnmc1poxYCMWyWM8V+jgcweX3Qy2s2cMZToLU1/PSeurSBnqYsrCJWvlztIBoU4ovNvQOpiLRqnWjbOk+c57nmLsCNX7PBc99+y28/D1se2tgtAOtaq6Yxd+SUu1cnhv1iY7dVGkNBUZx1GHruCiF9JgOIHjYDItzZ4D323Y9HmmxsQrm04dJNZWCfd0mYRa9OSsbpojNcAmv2x/Eob9us6sag1n4bslJ1M+kQdxr+viUPS5ZqYzO60x6UyxrSKr4nYbarsyHyBS6TcqYmvGe0Lu1YmhtncetZK7JplXK2+MK+Re+e7nQGvHch1MMMXxztwJiYn78GBP/HjGKkZyN//7viU+uVxnvv6y+Yoqbgu+H1Kl+wYVoJ0vCbuRTqGtHZX7CRgVLLb5eGUvSL4EttKri8HS3pmOzDP+UnwTXbEK2hAAAAAElFTkSuQmCC\"> </label> </div>  <div class=\"wallet item radio-item\"> <input type=\"radio\" name=\"wallet\" value=\"olamoney\" id=\"wallet-radio-olamoney\"> <label for=\"wallet-radio-olamoney\" class=\"radio-label mfix\"> <img class=\"wallet-button colored mchild item-inner l-2\" style=\"height:22px\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAALwAAAAtCAYAAAAUcwFNAAAOt0lEQVR42u2dC3QU1RnHp9ZWrdWqtdoq7M7M7iYBBNFo8wAxug8SEA9qgWoVLHiqFK1aXzy0RjnFiOjuJohEEAR5CQgq8sbwEATlTUBAHlIQBJEoVAkIyfT/D7CTnZndmc1zPZ3/OfeQsN+dmZ353e9+33dvdoXaqsvkLj/PKvTfmBH2PpMR8g7PDPlmZAa9q/HznsyQdz1+n41/R2YF/c9nhP0d8grzzhFs2fopiZBnF/o6ZoR9b2QEvQfRFKsNg+EIBsEkDIJuWZO7nCfYspXMygwG8gDuRsJb28ZZICvk664oys8EW7aSSW0L218Nz7yAoNZ1w3HXZBcFbhZs2UoOr+6/NyPoO24Cbm29fWVm2PesYMtWY4mhBrxvAYFssBbyTcgZfe+5gi1bDamchflnI7Gc1qCwqyHOCl+x7zeCLVsNJUBXpELYGNB7Z+Ur+WcJtmzVt7KCvp6ErrEbKjgvCLZs1afaFPoy1QS18RsS2a6CLVv1IYYQCGVKkwJ2NYn9BquzFwq2bNW1ssP+e5IKdjWJHSjYslXH2wV+iUTxCwKWfM33fUbo1ssFW7bqSllhfx/ClawtK+gNCbZs1ZUQK3+shaxgQVApWlIc1frOyFd0diX9lbfXjoxqY1YW6+z6zeyjsxu3eoRy68g7LWw68x2wy5S26kTZrwUuQ9hQoYVs7pYSRavD5UeU7JA/YtN+eAdl9tagYqSeE/tE7PywG7vmH4Z2w5eNsuTls4PeLKGGcrlcbo8s3+5xSgNdTnGC2yEXuCWpVzOX66r09PRfaO1btGhxiccpjpdF8Q02tyi+kiPmnJvQOR2OdJckdU+kj9vtbqKeJzE1k2WPVdtWrVqd30ySnDhfC/ZLTU29wuomvqsk6fJErjEnJ+fs1mLri4xeayG2+H2aLLc0a2feW35+/lmtXK7LhASVkpJypWndfcDMgYaA9pn6eMSm1+SAMnR5N6Wi8qTO7s1PJ0Tsek4KKC8tyVMOH/taZ7d5/9Z6q8u3kGWH2ykNAbCfAfQKNKV6A/S78fo4ual8nQa8a9Hnh4itKK7ng7YcImZlnYc+7+AY6wiWhS4cZL92O8VDLqe0QB2E1oT30YXX6RHFjHhbRQg4rmsCrmsuzlN6un2A827CfViB43Rt2bLlxUIcof9+tOUau3j2PXCO+UYDAefdiGuZgzY7XpNxH+mECD9+3+12uJsncG+64jw70tLSfns6nPFONwLspqG3KMdPHNcBOnntdHVQzGlfBfLu7zbo7HYd2h2x6z/7lN3qL99TjNRpxJ+tQL8xoVF9ZcrVuNmfEwTzJm1zO6R7qgOP/4sAj5u8hsAneJO/Z18OOHomC979QlzDHPRZliLLPsGiCAL6fOgGhG7RnRlrAOJ6XnE7ndsw67yUKkmt2A/tlzwvPaDHKd/uEqV30Ta4mrquijVoOCBlOAkcp8jKwHQ75b/gvOMEjdiXAwGzbzqvI17j4DjTD+d9UBal99Nl2XT7SZqYJuLeLMR9ubHabkjftliQLdv5iQ7OA0e+rnrthiKvMnhxbhXIH24fbghy1zfvVbCYBbu8KrtJ658ytBtSUmSlWlPBPz6xAgE9gFuU1lmCXfXi36a4XLfVFnja4QEvi/R1SnskSfqjFeDpzTwO6SH0mwkgfmXpvYpyb/R7F31KCLzBcc+hR8brx/CzK96xGKoA0H/KTrEMx8qJBXyqy9VGdkhzZafcs7bAu53ua/h7grPnRISl/zJ9LxyYCGOjwjWW/WJBNmj+y4aA9hjfW+k+wU+Iq1rxJz0M7V5dOlK5e3wgYjdkSUfl6I9HdHardq+1FNa0KfJfYeWG4I1O0QBNaL9EmwUPFsYNmwaPt1kHvUNaCY/TtDbAI094Gn0q2a8a9FN5XVaAZ15BqJhzCCZiDEwPhnyhjQq8LmwYhfe8JKEZShT7ot86XFNzI+AxA12PMPB63MeFDAcbEniKAxcObWFKU+mGWDaS09mLjiMq9OIqZjzA8orvQHxeoQN09Cfjlb6zqsKUSDvw3+06u41fbVae1NiV7p+vsztRcVLxD+tsDn04kC6YCA/Diwd2JAo4wC01ldoxtIie7qRVBgOjH6dZ/Jww8AQE59quPSbDG8bZVoBPEVPSCDvBYrIXHyapP70YwTYCXkRMjxmghIM4UajQ7zlcz6guXTir6oE/BZXUy4UwjPemoYBXcwNXN84yuG+/076GkC3VjcGom1nbhHJTzSBbt7dUB+jOb3YpBYtyo0BeuustnV1lZSWS2ruj7KZvel4x0nNzXrSQuHpvMZ9CpSINwDtcDlebmOGHKC6Ktpfm8YEmCjwfIJLGN2OFTLiupawwWAGe4Qy9E0DpFcueyTAfKuNx/m4EPPpPwdQ/qEZVn2bN/kBHQW9qCLwKbSHOM4IhRAMBr/YXxeEYmEEOyuoVKDiXKXjtSX1JMuS93gyy8OLXDAEdufK+KJBHr+5taDd/29Aou+DSzkiGj+nsFm1bag582HeXKfDwOJpKzOR49rjpj2li+Q3wiNka4FebAY9+uWhlGtArq/+cAo9sBfgzMxXDFQ4S4+t2vgQghxFCI+CZ1OF6NnmcnmZCDeXGMTHb/DUW8BSrH6z0IJl8qCGBp1hO5fWkSe5OZ/4PFZ2HXU55Ol67QNehbdjnMIPs9lF3G4K8aOcbUSAzMd373T59taZsjcYuV1myY6nOrvzHcqVdUQcT4P1eC2WwkijoJCnuXhzZIXs14cd2PNC2KvBoAD6ed2acyBuvqfxsQVukGXyfE0ArwBMI2E9FTvCE0ZRNwKvX3lmpIfDVPTT+byMrMDUH3vm6S5ZfNAJeXyQQF6U45LaJAM9r5vFYmozZHOJieu04FbEAj8OwDTNsa/QpYfk15h4a/j2pGfTbDu7QAbr38OYokAeg9Dh+1WSd3cmKE0rhsj9F7J6ek6vkzylQjPTE+8/EvY4bCvOaW/BKszUee5pJrNpPk7iqHt4i8B6H6yGEQj9WO85JDLTHPJLkl1Fb14RYo/iwzYCnZICF/1vM9YQo6ESxGKD8m7/HAp7eD2HUDIJQc+ClIbw/xsDrY2q+xsWzRDw8S7C87piNiXMcMS+DVx+E+zSG5UqPKD8gxJOVz5YZuWKsYXw+bPldEZB7TvIrf3v7YUOQP9gyuJpdgAkqE1Wd3cxNc+NeR9thHS+2UCUZrPGqewiekS1hYHyu8fCzT8fwR60ADw8qA4zSaO8ul7DOfTrOHB19fOc39EpWgOfDZH94uUGR80nS1bCr8mjxgOfx8H+bCEwtQpqFKNX2sAI842hcVwHuxVvMQRoipIl6r6K4BeHsYpZh4xrzE8LMgL9n3P2GIM/7vOgUyAhncl71cc+LUvbDtzq7rQc/OhXOoN0EOx5z5e41Orvvyg9z60KsD3E6KlgQMvN28LaHor18VShxFx9E5Gaz/IcpUxtzu2X5kVRZvs4K8HzITNo0M0pZitPZ4YwNQxiGMlHQo7rAGNsEeLW/KC3GKmPK6TLjeJzzcUEjgkjgNYNgIkOSmsfH4hYOaHPg1bwBWzKmsaxJ2wYBXl2QKuSzMzUETEOt1MD3Hd6vr9aUraoC+dl57SN275XO0tkdP1muvPLRrbDLjdhxsclIf5/yWKy/c51ncbSfgxs8VlslYWhBcPAwCMwCgPkfrQ1u2lLGvlrg0W8n99Tg9RFssiSN5NSJY+Xg9a+qH4OVGq4OamadAdUTWIY/MvqbAq/GqS+zIoFENZvXzsFnCXiHK50xrbq9wboYJuCYr3OWMQFet2+JyTbO2z4pgccelfZWgJ+45h3D+Dy87A7l/qmqV350en9DkKdtfFbpPTUQseN2AiO9vXZarL00DwoWxRo7POFOwmW9SbvwsG5mfxX4mCXGg6he3IJ4/z0rSSkB5eqr5hjrCKIF4OltJQC/hOeLVaokiIYLTxig9NSamr5Z4n+/2+Hcw+SYv5sBr89pcG8APa876YBn4srPfDQD/oHJjxoCOmNzgRIo9qlxdmGucuxEuc5uw1dzsbtStWPjxjGt9h85YHh+VpQSmpKdTgmedC49q5W9NFwuFygV+PI42xBe405IlxhlU4EEr6+V/TUq9PLL9KA64I2Bfh591jBsMAFetykN1/muG40Ami7bO+QQ8wz1WIkDTzvMas9wHYD3KXmAV8OaKeaLPn5l/d7lytavV0W16aWv62xnfjZBZ7dqT4nObuDcwcryLz7VNqPNZOuEGoielQsQCDM+BWxHNR72BJM6xuBaT0bg0e+YsXcXNzIRdEnRq7QMl7jqFw8m2L2jOd5egJhpBXiW5mTE8fzZHHh9rsG9Jzjfdo8ohT2ipzXr5wz/6PmZACM57caqDt7fapynJfuZAG+++1MUJ+E+jmHeEW/zGBetzFqdAs+PvCZYydoyw/77hFqIcTm3FsDL9uGSOeLTRzjt0uPxIcbI+kOwebF64yougqUbAYTHhWoQIHoBFZQXWC9HoponmKhJkyaXeBCLnzkejjEEM0suvfxpT7yVK5s1rJmzInOtyfbg5kjgx3B7MOxLucimbg8WP+aWBrNdiDLCI67uWk18Yb8Px15mvD1YWis7pHnm24OlJbi2jiY7VLmDc0AiH740OymBD3m3VO2S/D+QJhlN9I8cLrVqy2oVvToHAJoroY1lnMUSEI/NWSTWa9wsZ6WxEGAyuC5QS5IWlB30t+YiVPIB77tDsGWrPoQ6+rgk+4iOFYItW/WlnGDni7A/fmtSwB70HWpXFJAEW7bq2cs3QxhxuJE9+8mqjWK2bDUI9IWBTo0Zz+MPyx8RbNlq2E8R9t+J5fzyBq7IVGaG/E8Jtmw11qcJA/r9DfWReghjbhNs2WpMcUmf371az1+A8EVWoe8awZatZPko7axQoBe88L46hZ3JcdjX1/5eJ1tJqcDYwPn8tj1UUcpqCfoPOEZRTnGnSwVbtn4SX3pW6PMB2lfR9losNZYB9DFZwUBn+1u4bf2kv94yI+z3oH6fw08VwL71JwD2YHzEdT/83J0Dg3+LykEi2LLViPofLKkZ6FmNtf0AAAAASUVORK5CYII=\"> </label> </div>  </div> </div>  <div id=\"form-otp\" class=\"tab-content showable screen\"> <div id=\"otp-prompt\"></div> <div id=\"add-funds\" class=\"add-funds\"> <div id=\"add-funds-action\" class=\"btn\">Add Funds</div> <div class=\"text-center\" style=\"margin-top: 20px;\"> <a id=\"choose-payment-method\" class=\"link\">Try different payment method</a> </div> </div> <div id=\"otp-section\"> <div id=\"otp-action\" class=\"btn\">Retry</div> <div id=\"otp-elem\" class=\"invalid\"> <div class=\"help\">Please enter 6 digit OTP</div> <input type=\"tel\" class=\"input\" name=\"otp\" id=\"otp\" maxlength=\"11\" autocomplete=\"off\" required=\"\"> </div> </div> <div class=\"spin\"><div></div></div> <div class=\"spin spin2\"><div></div></div> <div id=\"otp-sec-outer\"> <a id=\"otp-resend\" class=\"link\">Resend OTP</a> <a id=\"otp-sec\" class=\"link\">Skip Saved Cards</a> </div> </div> </div> <button id=\"footer\" type=\"submit\" class=\"button\"> <span class=\"pay-btn\">PAY &nbsp;<i>\uE600</i><span class=\"iph\">₹</span> 50</span> <span class=\"otp-btn\">Verify</span> </button> </form> </div> </div> </div> </div><style type=\"text/css\">.button, .btn{ background: #F37254;}.spin div, .link{ border-color: #F37254!important;}#payment-options i,.text-btn{ color: #F37254;}#header{ background: #F37254;}.merchant-image{ width: 60px; height: 60px; padding: 12px; border: 1px solid #eaeaea; background: #fff;}.option.active,.checked .checkbox,input[type=checkbox]:checked + .checkbox{ color: #fff; background: #F37254; border-color: #F37254;}.grid :checked+label { border-bottom: 2px solid #F37254;}</style></div>";

        return data;
    }

    @Override
    public Response getCardsAndAccounts(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        Response response1 = null;
        restPaymentRequestVO = readDirectTransactionRequest.readGetCardsAndAccountRequestForm(paymentRequest);
        response1 = getCardsAndAccountsJSON(restPaymentRequestVO);
        return response1;
    }

    public Response getCardsAndAccountsJSON(RestPaymentRequestVO paymentRequestVO)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");

        Response response = new Response();
        RegistrationHelper registrationHelper = new RegistrationHelper();
        CommonValidatorVO commonValidatorVO = null;

        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        List<RegistrationDetailVO> registrationDetailVOList = null;
        Functions functions = new Functions();
        TokenManager tokenManager = new TokenManager();

        String authResponse = request1.getAttribute("authfail").toString();
        if (authResponse != null && !authResponse.equals("") && authResponse.equals("false"))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;
        }

        transactionLogger.error(" get Cards And Accounts Request---" + gson.toJson(paymentRequestVO));
        commonValidatorVO = readDirectTransactionRequest.readGetCardsAndAccountRequest(paymentRequestVO, request1);

        if (commonValidatorVO == null)
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            writeDirectTransactionResponse.setErrorForGetCardAccountsList(response, null, null, errorCodeListVO);
            transactionLogger.error(" get Cards And Accounts Response---" + gson.toJson(response));
            return response;
        }

        try
        {
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());

            commonValidatorVO = restCommonInputValidator.performGetCardsAndAccountValidator(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeDirectTransactionResponse.setErrorForGetCardAccountsList(response, null, null, commonValidatorVO.getErrorCodeListVO());
                transactionLogger.error(" get Cards And Accounts Response---" + gson.toJson(response));
                return response;
            }

            commonValidatorVO = registrationHelper.performCustomerRegistrationChecksForToken(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeDirectTransactionResponse.setErrorForGetCardAccountsList(response, commonValidatorVO, null, commonValidatorVO.getErrorCodeListVO());
                transactionLogger.error(" get Cards And Accounts Response---" + gson.toJson(response));
                return response;
            }

            StringBuffer bankAccountId = new StringBuffer();
            if ("Y".equals(commonValidatorVO.getPartnerDetailsVO().getIsMerchantRequiredForCardRegistration()))
            {
                if (functions.isValueNull(commonValidatorVO.getCustomerId()))
                {
                    registrationDetailVOList = tokenManager.getRegistrationsByMerchantAndCustomer(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getCustomerId());
                }
                else
                {
                    registrationDetailVOList = tokenManager.getRegistrationsByMerchant(commonValidatorVO.getMerchantDetailsVO().getMemberId());
                }

                if (registrationDetailVOList == null)
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_RECURRING_TYPE, "Invalid Recurring Type."));
                    writeDirectTransactionResponse.setErrorForGetCardAccountsList(response, commonValidatorVO, null, errorCodeListVO);
                    transactionLogger.error(" get Cards And Accounts Response---" + gson.toJson(response));
                    return response;
                }
                else
                {
                    for (RegistrationDetailVO registrationDetailVO : registrationDetailVOList)
                    {
                        if (functions.isValueNull(registrationDetailVO.getTokenDetailsVO().getBankAccountId()))
                        {
                            bankAccountId.append(registrationDetailVO.getTokenDetailsVO().getBankAccountId() + ",");
                        }
                    }
                    if (functions.isValueNull(bankAccountId.toString()))
                    {
                        bankAccountId.append(bankAccountId.toString().substring(0, bankAccountId.toString().length() - 1));
                        registrationDetailVOList = tokenManager.getBankAccountDetails(bankAccountId.toString(), registrationDetailVOList);
                    }
                    writeDirectTransactionResponse.setCardAccountsListSuccessResponse(response, commonValidatorVO, registrationDetailVOList, errorCodeListVO);
                    transactionLogger.error(" get Cards And Accounts Response---" + gson.toJson(response));
                    return response;
                }
            }
            else
            {
                if (functions.isValueNull(commonValidatorVO.getCustomerId()))
                {
                    registrationDetailVOList = tokenManager.getRegistrationsByPartnerAndCustomer(commonValidatorVO.getPartnerDetailsVO().getPartnerId(), commonValidatorVO.getCustomerId());
                }
                else
                {
                    registrationDetailVOList = tokenManager.getCustomerRegistrationDetailsByPartner(commonValidatorVO.getPartnerDetailsVO().getPartnerId());
                }

                if (registrationDetailVOList == null)
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_RECURRING_TYPE, "Invalid Recurring Type."));
                    writeDirectTransactionResponse.setErrorForGetCardAccountsList(response, commonValidatorVO, null, errorCodeListVO);
                    transactionLogger.error(" get Cards And Accounts Response---" + gson.toJson(response));
                    return response;
                }
                else
                {
                    for (RegistrationDetailVO registrationDetailVO : registrationDetailVOList)
                    {
                        if (functions.isValueNull(registrationDetailVO.getTokenDetailsVO().getBankAccountId()))
                        {
                            bankAccountId.append(registrationDetailVO.getTokenDetailsVO().getBankAccountId() + ",");
                        }
                    }
                    if (functions.isValueNull(bankAccountId.toString()))
                    {
                        bankAccountId.append(bankAccountId.toString().substring(0, bankAccountId.toString().length() - 1));
                        registrationDetailVOList = tokenManager.getBankAccountDetails(bankAccountId.toString(), registrationDetailVOList);
                    }
                    writeDirectTransactionResponse.setCardAccountsListSuccessResponse(response, commonValidatorVO, registrationDetailVOList, errorCodeListVO);
                    transactionLogger.error(" get Cards And Accounts Response---" + gson.toJson(response));
                    return response;
                }
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ GenericConstraintViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setErrorForGetCardAccountsList(response, commonValidatorVO, null, errorCodeListVO);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setErrorForGetCardAccountsList(response, commonValidatorVO, null, e.getPzdbConstraint().getErrorCodeListVO());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while placing transaction via Rest", e);
            transactionLogger.error("PZ PZConstraintViolationException while placing transaction via WebService", e);
            writeDirectTransactionResponse.setErrorForGetCardAccountsList(response, commonValidatorVO, null, e.getPzConstraint().getErrorCodeListVO());
        }
        transactionLogger.error(" get Cards And Accounts Response---" + gson.toJson(response));
        return response;
    }

    public Response getTransactionDetails(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        Response response1 = new Response();

        restPaymentRequestVO = readDirectTransactionRequest.getTransactionRequestForm(paymentRequest);

        response1 = getTransactionDetailsJSON(restPaymentRequestVO);

        return response1;
    }

    public Response getTransactionDetailsJSON(RestPaymentRequestVO paymentRequestVO)
    {
        Response response                   = new Response();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        Functions functions                     = new Functions();
        RegistrationHelper registrationHelper   = new RegistrationHelper();
        CommonValidatorVO commonValidatorVO     = null;
        ErrorCodeListVO errorCodeList               = new ErrorCodeListVO();
        List<TransactionVO> transactionDetailVOList = new ArrayList<TransactionVO>();
        TransactionManager transactionManager       = new TransactionManager();
        GenericTransDetailsVO transDetailsVO        = new GenericTransDetailsVO();
        TransactionVO transactionVO                 = new TransactionVO();

        Functions function = new Functions();
        TokenManager tokenManager                   = new TokenManager();


        try
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse != null && !authResponse.equals("") && authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---" + authResponse);
                writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                return response;
            }

            transactionLogger.error("Get Rest Transaction Details JSON Request-----" + gson.toJson(paymentRequestVO));
            commonValidatorVO = readDirectTransactionRequest.getTransactionRequest(paymentRequestVO);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while cancelling transaction");
                return response;
            }
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performRestTransactionDetail(commonValidatorVO);
            if (functions.isValueNull(commonValidatorVO.getErrorMsg()) || !commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return response;
            }

            commonValidatorVO = registrationHelper.performChecksumVerification(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return response;
            }
            transactionDetailVOList = restDirectTransactionManager.processGetTransactionList(transactionVO, commonValidatorVO, transactionDetailVOList);
            writeDirectTransactionResponse.setTransactionDetailsresponse(response, commonValidatorVO, transactionDetailVOList);


        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ GenericConstraintViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setErrorForGetCardAccountsList(response, commonValidatorVO, null, errorCodeListVO);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setErrorForGetCardAccountsList(response, commonValidatorVO, null, e.getPzdbConstraint().getErrorCodeListVO());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while placing transaction via Rest", e);
            transactionLogger.error("PZ PZConstraintViolationException while placing transaction via WebService", e);
            writeDirectTransactionResponse.setErrorForGetCardAccountsList(response, commonValidatorVO, null, e.getPzConstraint().getErrorCodeListVO());
        }
        transactionLogger.error("Get Rest Transaction Details JSON Response-----" + gson.toJson(response));
        return response;
    }

    public Response generateAuthToken(@InjectParam RestPaymentRequest authRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1  = new Response();
            Result result       = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        Response response1                  = new Response();
        RestPaymentRequestVO authRequestVO  = new RestPaymentRequestVO();

        authRequestVO   = readDirectTransactionRequest.getAuthTokenRequest(authRequest);

        response1       = generateAuthTokenJSON(authRequestVO);

        return response1;
    }

    public Response generateAuthTokenJSON(RestPaymentRequestVO loginRequest)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        Response merchantServiceResponseVO      = new Response();
        CommonValidatorVO commonValidatorVO     = null;
        ErrorCodeListVO errorCodeListVO         = new ErrorCodeListVO();
        String IpAddress                        = request1.getRemoteAddr();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Functions functions                     = new Functions();
        try
        {
            //transactionLogger.error("Rest generate Auth Token JSON Request---"+gson.toJson(loginRequest));
            commonValidatorVO       = readDirectTransactionRequest.readRequestForMerchantLogin(loginRequest);
            logger.error("from commonValidatorVO login DirectPaymentRestIMPL---" + commonValidatorVO.getMerchantDetailsVO().getLogin());
            logger.error("from commonValidatorVO partnerid DirectPaymentRestIMPL---" + commonValidatorVO.getParetnerId());
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeDirectTransactionResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performRestMerchantAuthTokenValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                writeDirectTransactionResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }
            request1.setAttribute("username", commonValidatorVO.getMerchantDetailsVO().getLogin());
            request1.setAttribute("password", commonValidatorVO.getMerchantDetailsVO().getPassword());
            request1.setAttribute("role", "merchant");

            directKitResponseVO = registrationManager.getAuthToken(commonValidatorVO, request1, response);
            writeDirectTransactionResponse.setSuccessAuthTokenResponse1(merchantServiceResponseVO, directKitResponseVO, commonValidatorVO);

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            writeDirectTransactionResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            writeDirectTransactionResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        //transactionLogger.error("Rest generate Auth Token JSON Response---"+gson.toJson(merchantServiceResponseVO));
        return merchantServiceResponseVO;
    }

    public Response generatePartnerAuthToken(@InjectParam RestPaymentRequest authRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        Response response1 = new Response();
        RestPaymentRequestVO authRequestVO = new RestPaymentRequestVO();

        authRequestVO = readDirectTransactionRequest.getPartnerAuthTokenRequest(authRequest);

        response1 = generatePartnerAuthTokenJSON(authRequestVO);

        return response1;
    }

    public Response generatePartnerAuthTokenJSON(RestPaymentRequestVO loginRequest)
    {
        Response merchantServiceResponseVO      = new Response();
        CommonValidatorVO commonValidatorVO     = null;
        ErrorCodeListVO errorCodeListVO         = new ErrorCodeListVO();
        String IpAddress                        = request1.getRemoteAddr();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Functions functions                     = new Functions();
        try
        {
            //transactionLogger.error("Rest generate Auth Token JSON Request---"+gson.toJson(loginRequest));
            commonValidatorVO = readDirectTransactionRequest.readRequestForPartnerLogin(loginRequest);
            logger.error("from commonValidatorVO Partnerlogin DirectPaymentRestIMPL---" + commonValidatorVO.getPartnerName());
            logger.error("from commonValidatorVO partnerid DirectPaymentRestIMPL---" + commonValidatorVO.getParetnerId());
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeDirectTransactionResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performRestPartnerAuthTokenValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                writeDirectTransactionResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }

            request1.setAttribute("role", "partner");

            directKitResponseVO = registrationManager.getAuthToken(commonValidatorVO, request1, response);
            writeDirectTransactionResponse.setSuccessPartnerAuthTokenResponse(merchantServiceResponseVO, directKitResponseVO, commonValidatorVO);

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            writeDirectTransactionResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            writeDirectTransactionResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        //transactionLogger.error("Rest generate Auth Token JSON Response---"+gson.toJson(merchantServiceResponseVO));
        return merchantServiceResponseVO;
    }


    public Response invalidRegistrationId(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        response.addHeader("Access-Control-Allow-Origin", "*");
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();

        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REGISTRATIONID, "Invalid Request provided."));
        writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, errorCodeListVO.getListOfError().get(0).getErrorDescription());

        return response;
    }

    public Response processPayout(RestPaymentRequest request)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        Response response1 = new Response();
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();

        restPaymentRequestVO = readDirectTransactionRequest.readPayoutRequest(request);

        response1 = processPayoutJSON(restPaymentRequestVO);

        return response1;
    }

    private static URI getBaseURI(String url)
    {

        return UriBuilder.fromUri(url).build();

    }

    public Response processValidateCustomer(RestPaymentRequestVO paymentRequest)
    {
        Response response1 = new Response();
        Result result = new Result();
        result.setResultCode("0");
        result.setDescription("success");

        response1.setResult(result);

        return response1;
    }

    public Response processPaymentUpdate(RestPaymentRequestVO paymentRequest)
    {


        return null;
    }

    public Response processValidateandUpdateforExchanger(RestPaymentRequestVO paymentRequest)
    {
        Response response1 = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Functions functions = new Functions();
        response.addHeader("Access-Control-Allow-Origin", "*");
        logger.debug("Inside processCustomerValidation:::");
        transactionLogger.debug("Inside processCustomerValidation:::");

        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        CommonValidatorVO commonValidatorVO = null;

        try
        {

            String isValid = restDirectTransactionManager.getCustomerValidation(commonValidatorVO);

            if (isValid.equalsIgnoreCase("0"))
            {
                String isUpdated = restDirectTransactionManager.getPaymentUpdate(commonValidatorVO);
                if (isUpdated.equalsIgnoreCase("0"))
                    writeDirectTransactionResponse.setSuccessRestExchangerResponse(response1, directKitResponseVO, commonValidatorVO);

            }
        }
        catch (Exception e)
        {

        }
        return response1;
    }

    public Response processExchangerDeposit(RestPaymentRequestVO paymentRequest)
    {
        Response response1                  = new Response();
        ErrorCodeListVO errorCodeListVO     = new ErrorCodeListVO();
        Functions functions                 = new Functions();
        response.addHeader("Access-Control-Allow-Origin", "*");
        logger.debug("Inside processExchanger:::");
        transactionLogger.debug("Inside processExchanger:::");
        DirectKitResponseVO directKitResponseVO     = new DirectKitResponseVO();
        CommonValidatorVO directKitValidatorVO      = null;

        transactionLogger.error("Rest process Exchanger Deposit Request---" + gson.toJson(paymentRequest));
        directKitValidatorVO = readDirectTransactionRequest.readRequestForExchanger(paymentRequest, request1);
        try
        {
            directKitValidatorVO.setErrorCodeListVO(errorCodeListVO);
            directKitValidatorVO = restCommonInputValidator.performExchangerValidation(directKitValidatorVO, "REST");
            if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(directKitValidatorVO.getErrorMsg()))
            {
                writeDirectTransactionResponse.setExchangerResponseForError(response1, directKitValidatorVO, directKitValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());
                return response1;
            }
            else
            {
                directKitValidatorVO = transactionHelper.checksumValidationExchanger(directKitValidatorVO);

                directKitResponseVO = restDirectTransactionManager.processExchanger(directKitValidatorVO);
                writeDirectTransactionResponse.setSuccessRestExchangerResponse(response1, directKitResponseVO, directKitValidatorVO);
            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Exchanger", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Exchanger", e);
            writeDirectTransactionResponse.setLoginResponseForError(response1, directKitValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Exchanger", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Exchanger", e);
            writeDirectTransactionResponse.setLoginResponseForError(response1, directKitValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());

        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Exchanger", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Exchanger", e);
            writeDirectTransactionResponse.setLoginResponseForError(response1, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());

        }
        transactionLogger.error("Rest process Exchanger Deposit Response---" + gson.toJson(response1));
        return response1;
    }


    public Response processPayoutJSON(RestPaymentRequestVO requestVO)
    {
        Response response1                      = new Response();
        CommonValidatorVO commonValidatorVO     = null;
        ErrorCodeListVO errorCodeListVO         = new ErrorCodeListVO();
        Functions functions                     = new Functions();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        String authResponse = request1.getAttribute("authfail").toString();
        /*if (authResponse==null || authResponse.equals(""))
        {
            logger.debug("Inside failResponse---"+authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response1);
            return response1;
        }
        else if(authResponse.equals("false"))
        {
            logger.debug("Inside failResponse---"+authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response1);
            return response1;
        }*/
        String ipAddress    = Functions.getIpAddress(request1);
        Gson gson           = new Gson();
        transactionLogger.error("JSON Payout request::::" + gson.toJson(requestVO));
        commonValidatorVO = readDirectTransactionRequest.readPayoutRequestJSON(requestVO);
        if (commonValidatorVO == null)
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            writeDirectTransactionResponse.setPayoutResponseForError(response1, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
            return response1;
        }
        commonValidatorVO.setRequestedIP(ipAddress);
        try
        {
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performPayoutTransactionValidation(commonValidatorVO, "REST");
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                writeDirectTransactionResponse.setPayoutResponseForError(response1, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return response1;
            }
            else
            {
                commonValidatorVO   = transactionHelper.performSystemCheckForPayout(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
                {
                    writeDirectTransactionResponse.setPayoutResponseForError(response1, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response1;
                }
                else
                {
                    directKitResponseVO = restDirectTransactionManager.processPayout(commonValidatorVO);
                    writeDirectTransactionResponse.setSuccessRestPayoutResponse(response1, directKitResponseVO, commonValidatorVO);

                }
            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Payout", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Payout", e);
            writeDirectTransactionResponse.setLoginResponseForError(response1, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Payout", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Payout", e);
            writeDirectTransactionResponse.setLoginResponseForError(response1, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Payout", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Payout", e);
            writeDirectTransactionResponse.setLoginResponseForError(response1, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        transactionLogger.error("JSON Payout response::::" + gson.toJson(response1));
        return response1;
    }

    public Response getAuthToken(@InjectParam RestPaymentRequest authRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        Response response1 = new Response();
        RestPaymentRequestVO authRequestVO = new RestPaymentRequestVO();

        authRequestVO = readDirectTransactionRequest.getNewAuthTokenRequest(authRequest);

        response1 = getAuthTokenJSON(authRequestVO);

        return response1;
    }

    public Response getAuthTokenJSON(RestPaymentRequestVO loginRequest)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        Response merchantServiceResponseVO = new Response();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String IpAddress = request1.getRemoteAddr();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Functions functions = new Functions();
        AuthFunctions authFunctions = new AuthFunctions();
        try
        {
            //transactionLogger.error("Rest get Auth Token JSON Request---"+gson.toJson(loginRequest));
            commonValidatorVO = readDirectTransactionRequest.readRequestForgetNewAuthToken(loginRequest);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeDirectTransactionResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }
            else
            {

                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                commonValidatorVO = restCommonInputValidator.performGetNewAuthTokenValidation(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
                {
                    writeDirectTransactionResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return merchantServiceResponseVO;
                }

                request1.setAttribute("username", commonValidatorVO.getMerchantDetailsVO().getLogin());
                //request1.setAttribute("password", commonValidatorVO.getMerchantDetailsVO().getPassword());
                request1.setAttribute("role", commonValidatorVO.getMerchantDetailsVO().getRole());
                directKitResponseVO = registrationManager.regenerateAuthToken(commonValidatorVO, request1, response);
                writeDirectTransactionResponse.setSuccessAuthTokenResponse(merchantServiceResponseVO, directKitResponseVO, commonValidatorVO);

            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            writeDirectTransactionResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZDBViolationException exception while Generating Token", e);
            transactionLogger.error("PZ PZDBViolationException exception while Generating Token", e);
            writeDirectTransactionResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        //transactionLogger.error("Rest get Auth Token JSON Response---"+gson.toJson(merchantServiceResponseVO));
        return merchantServiceResponseVO;
    }


    public Response processValidateWalletDetails(RestPaymentRequestVO paymentRequestVO)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");

        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Functions functions = new Functions();
        Response response = new Response();
        CommonValidatorVO commonValidatorVO = null;
        String trackingId = paymentRequestVO.getId();

        String authResponse = request1.getAttribute("authfail").toString();
        if (authResponse == null || authResponse.equals(""))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;
        }
        else if (authResponse.equals("false"))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;
        }
        transactionLogger.error("Rest process Validate Wallet Details Request---" + gson.toJson(paymentRequestVO));

        transactionLogger.error("Rest process Validate Wallet Details Request ----- trackingID ---- " + trackingId);
        if (!functions.isValueNull(trackingId) || !functions.isNumericVal(trackingId) || trackingId.length() > 20 || !ESAPI.validator().isValidInput("trackingid", trackingId, "Numbers", 10, false))
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_TRACKINGID));
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, errorCodeListVO.getListOfError().get(0).getErrorDescription());
            return response;
        }

        try
        {
            commonValidatorVO = readDirectTransactionRequest.readRequestForWalletDetails(paymentRequestVO);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return response;
            }
            else
            {
                transactionLogger.error("Rest process Validate Wallet Details IN ELSE ========");

                commonValidatorVO = restCommonInputValidator.performWalletDetailsValidation(commonValidatorVO, "REST");

                transactionLogger.error("Rest process Validate Wallet Details AMOUNT========" + commonValidatorVO.getTransDetailsVO().getAmount());
                transactionLogger.error("Rest process Validate Wallet Details CURRENCY========" + commonValidatorVO.getTransDetailsVO().getCurrency());
                transactionLogger.error("Rest process Validate Wallet Details TRACKING ID========" + commonValidatorVO.getTrackingid());

                if (commonValidatorVO.getErrorCodeListVO() != null)
                {
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setRestCaptureResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                        return response;
                    }
                }
                directKitResponseVO = restDirectTransactionManager.processVerifyWalletDetials(commonValidatorVO);

                transactionLogger.error("Rest process Validate Wallet Details -------- directKitResponseVO wallet address -----" + directKitResponseVO.getWalletAddress());

                writeDirectTransactionResponse.setSuccessWalletAddressResponse(response, directKitResponseVO, commonValidatorVO);

                transactionLogger.error("Rest process Validate Wallet Details ----- RESPONSE walletAddress----- " + response.getWalletAddress());
            }

        }
        catch (Exception e)
        {
            logger.error("Exception", e);
        }

        transactionLogger.error("Rest process Validate Wallet Detials Response---" + gson.toJson(response));
        return response;
    }


    public Response processCheckConfirmationStatus(RestPaymentRequestVO paymentRequestVO)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");

        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Functions functions = new Functions();
        Response response = new Response();
        String trackingId = paymentRequestVO.getId();
        String transaction_status = "";
        String updateStatus = "";
        String message = "";

        if (functions.isValueNull(paymentRequestVO.getStatus()))
        {
            transaction_status = paymentRequestVO.getStatus();
        }

        String authResponse = request1.getAttribute("authfail").toString();
        if (authResponse == null || authResponse.equals(""))
        {
            transactionLogger.error("Inside failResponse---" + authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;
        }
        else if (authResponse.equals("false"))
        {
            transactionLogger.error("Inside failResponse---" + authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;
        }

        transactionLogger.error("Rest process Validate Wallet Details Request---" + gson.toJson(paymentRequestVO));
        transactionLogger.error("Rest process Validate Wallet Details Request ----- trackingID ---- " + trackingId);

        if (!functions.isValueNull(trackingId) || !functions.isNumericVal(trackingId) || trackingId.length() > 20 || !ESAPI.validator().isValidInput("trackingid", trackingId, "Numbers", 10, false))
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_TRACKINGID));
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, errorCodeListVO.getListOfError().get(0).getErrorDescription());
            return response;
        }

        try
        {
            Connection conn = null;
            PreparedStatement pst = null;
            ResultSet rs = null;

            transactionLogger.error("trackingId-----" + trackingId);
            transactionLogger.error("transaction_status -----" + transaction_status);

            if (functions.isValueNull(transaction_status))
            {
                if (transaction_status.equalsIgnoreCase("success"))
                {
                    transactionLogger.error("In if transaction_status -----------" + transaction_status);
                    try
                    {
                        conn = Database.getConnection();
                        String sql = "UPDATE transaction_common SET STATUS='capturesuccess' WHERE trackingid=?;";
                        pst = conn.prepareStatement(sql);
                        pst.setString(1, trackingId);
                        pst.executeUpdate();
                        transactionLogger.error("query-----" + pst);

                        updateStatus = "capturesuccess";
                        message = "success";
                    }
                    catch (Exception e)
                    {
                        transactionLogger.error("Exception-----", e);
                    }
                    finally
                    {
                        Database.closeConnection(conn);
                        Database.closePreparedStatement(pst);
                        Database.closeResultSet(rs);
                    }
                }
                else
                {
                    transactionLogger.error("In else transaction_status -----------" + transaction_status);
                    try
                    {
                        conn = Database.getConnection();
                        String sql = "UPDATE transaction_common SET STATUS='authfailed' WHERE trackingid=?;";
                        pst = conn.prepareStatement(sql);
                        pst.setString(1, trackingId);
                        pst.executeUpdate();
                        transactionLogger.error("query-----" + pst);

                        updateStatus = "authfailed";
                        message = "fail";
                    }
                    catch (Exception e)
                    {
                        transactionLogger.error("Exception-----", e);
                    }
                    finally
                    {
                        Database.closeConnection(conn);
                        Database.closePreparedStatement(pst);
                        Database.closeResultSet(rs);
                    }
                }

                response.setStatus("Transaction status updated successfully");
            }
            else
            {
                message = "fail";
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return response;
            }

            TransactionManager transactionManager = new TransactionManager();
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);

            transactionLogger.error("notification url ----------- " + transactionDetailsVO.getNotificationUrl());

            if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
            {
                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO, trackingId, updateStatus, message);
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }

        transactionLogger.error("Rest process Validate Wallet Detials Response---" + gson.toJson(response));
        return response;
    }

    // start of qr checkout

    public Response processQRCheckout(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1  = new Response();
            Result result       = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        Response response1 = new Response();
        restPaymentRequestVO = readDirectTransactionRequest.readRequestForQRCheckoutParams(paymentRequest);
        response1 = processQRCheckoutJSON(restPaymentRequestVO);
        return response1;
    }


    public Response processQRCheckoutJSON(RestPaymentRequestVO paymentRequest)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        transactionLogger.debug("Inside processQRCheckoutJSON  ------- ");
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO         = new ErrorCodeListVO();
        Functions functions                                 = new Functions();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        String error = "";

        //ResponseVo of the client
        Response response = new Response();
        CommonValidatorVO directKitValidatorVO = null;
        PaymentManager paymentManager = new PaymentManager();

        try
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                directKitValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if (authResponse.equals("false"))
            {
                directKitValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                return response;
            }

            if (paymentRequest != null)
            {
                transactionLogger.error("Rest Request for processTransaction----" + gson.toJson(paymentRequest));
                directKitValidatorVO = readDirectTransactionRequest.readRequestForQRCheckoutTransaction(paymentRequest, request1);

                MerchantDAO merchantDAO             = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(directKitValidatorVO.getMerchantDetailsVO().getMemberId());

                if (functions.isValueNull(merchantDetailsVO.getMemberId()))
                {
                    directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                }
                else
                {
                    transactionLogger.error("in if no record found");
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_TOID, "Invalid Request provided."));
                    error = "Invalid request provided";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_TOID.toString(), ErrorType.VALIDATION.toString());
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
                    return response;
                }

                if (directKitValidatorVO == null)
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                    error = "Invalid request provided";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_REQUEST_NULL.toString(), ErrorType.VALIDATION.toString());
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
                    return response;
                }
                else
                {
                    directKitValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
                    directKitValidatorVO.setErrorCodeListVO(errorCodeListVO);

                    directKitValidatorVO = restCommonInputValidator.performQRCheckoutValidation(directKitValidatorVO);

                    String uniqueorder = null;
                    transactionLogger.error("order id ----- " + directKitValidatorVO.getTransDetailsVO().getOrderId());
                    uniqueorder = transactionHelper.checkorderuniqueness(merchantDetailsVO.getMemberId(), "", directKitValidatorVO.getTransDetailsVO().getOrderId());
                    if (!uniqueorder.equals(""))
                    {
                        transactionLogger.error("In if unique order error-----" + uniqueorder);
                        error = directKitValidatorVO.getTransDetailsVO().getOrderId() + "-Duplicate Order Id " + uniqueorder;
                        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.SYS_UNIQUEORDER, "Your transaction is already being processed. Kindly try to place transaction with unique order ID."));
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_UNIQUEORDER.toString(), ErrorType.SYSCHECK.toString());
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Your transaction is already being processed. Kindly try to place transaction with unique order ID.");
                        return response;
                    }

                    transactionLogger.error("Error before Gateway-1--" + directKitValidatorVO.getErrorCodeListVO().getListOfError().size());

                    if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());
                        return response;
                    }
                    else
                    {
                        directKitResponseVO = restDirectTransactionManager.processQRCheckout(directKitValidatorVO);
                        writeDirectTransactionResponse.setSuccessQRCheckoutResponse(response, directKitResponseVO, directKitValidatorVO);
                        return response;
                    }
                }
            }
            else
            {
                directKitValidatorVO = new CommonValidatorVO();
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Payment Request"));
                error = "Invalid Payment Type";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_PAYMENT_TYPE.toString(), ErrorType.VALIDATION.toString());
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide proper Payment Type");
            }
        }
        catch (PZDBViolationException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("PZ DBViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (PZGenericConstraintViolationException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("PZ GenericConstraintViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzGenericConstraint().getMessage()/* "Internal Error occurred,Please contact support for more information"*/);
        }
        catch (NoSuchAlgorithmException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("SystemError while placing transaction via Rest", e);
            transactionLogger.error("SystemError while placing transaction via Rest", e);
            writeDirectTransactionResponse.setRestCaptureResponseForError(response, directKitValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        transactionLogger.error("Rest process Transaction Response----" + gson.toJson(response));
        return response;
    }

    // end of qr checkout

    public Response processQRTransaction(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        Response response1 = new Response();
        restPaymentRequestVO = readDirectTransactionRequest.readRequestForQRParams(paymentRequest);
        response1 = processQRTransactionJSON(restPaymentRequestVO);
        return response1;
    }


    public Response processQRTransactionJSON(RestPaymentRequestVO paymentRequest)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        transactionLogger.debug("Inside processQRTransactionJSON  ------- ");
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Functions functions = new Functions();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        String error = "";

        //ResponseVo of the client
        Response response = new Response();
        CommonValidatorVO directKitValidatorVO = null;
        PaymentManager paymentManager = new PaymentManager();

        try
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                directKitValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if (authResponse.equals("false"))
            {
                directKitValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                return response;
            }

            transactionLogger.error("Payment type---" + paymentRequest.getPaymentType());
            if (paymentRequest != null && (!functions.isValueNull(paymentRequest.getPaymentType()) || !ESAPI.validator().isValidInput("transactionType", paymentRequest.getPaymentType(), "transactionType", 2, false)))
            {
                directKitValidatorVO = new CommonValidatorVO();
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_TYPE, "Invalid Payment Type,Accept only [0-9] with Max Length 2"));
                error = "Invalid payment type, Payment type should not be empty";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_PAYMENT_TYPE.toString(), ErrorType.VALIDATION.toString());
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Transaction Type");
                return response;
            }
            else
            {
                if (paymentRequest != null && paymentRequest.getPaymentType() != null && (paymentRequest.getPaymentType().equals(PRE_AUTH)) || paymentRequest.getPaymentType().equals(DEBIT))
                {
                    transactionLogger.error("Rest Request for processTransaction----" + gson.toJson(paymentRequest));
                    directKitValidatorVO = readDirectTransactionRequest.readRequestForQRTransaction(paymentRequest, request1);
                    //transactionLogger.error("Error before Gateway 0---"+directKitValidatorVO.getErrorCodeListVO().getListOfError());
                    if (directKitValidatorVO == null)
                    {
                        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                        error = "Invalid request provided";
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_REQUEST_NULL.toString(), ErrorType.VALIDATION.toString());
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
                        return response;
                    }
                    else
                    {
                        String memID = "";
                        String orderId = "";
                        directKitValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
                        directKitValidatorVO.setErrorCodeListVO(errorCodeListVO);
                        directKitValidatorVO = restCommonInputValidator.performQRTransactionValidation(directKitValidatorVO);
                        //TODO request Amount and Currency check with DB Amount and Currency
                        transactionLogger.error("Error before Gateway-1--" + directKitValidatorVO.getErrorCodeListVO().getListOfError().size());
                        transactionLogger.error("Error before Gateway-1--" + directKitValidatorVO.getErrorCodeListVO().getListOfError());

                        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        {
                            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());
                            return response;
                        }

                        if ("PA".equalsIgnoreCase(directKitValidatorVO.getTransactionType()))
                        {
                            TransactionManager transactionManager = new TransactionManager();
                            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(directKitValidatorVO.getTrackingid());

                            transactionLogger.error("db amount = " + transactionDetailsVO.getAmount());
                            transactionLogger.error("request amount = " + paymentRequest.getAmount());
                            transactionLogger.error("db currency = " + transactionDetailsVO.getCurrency());
                            transactionLogger.error("request currency = " + paymentRequest.getCurrency());

                            double db_amount = Double.parseDouble(transactionDetailsVO.getAmount());
                            String db_currency = transactionDetailsVO.getCurrency();

                            if (functions.isEmptyOrNull(transactionDetailsVO.getToid()))
                            {
                                transactionLogger.error("in if no record found");
                                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.SYS_NO_RECORD_FOUND, "Invalid Request provided."));
                                error = "Invalid request provided";
                                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_NO_RECORD_FOUND.toString(), ErrorType.SYSCHECK.toString());
                                writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
                                return response;
                            }
                            else if (Double.parseDouble(paymentRequest.getAmount()) < db_amount)
                            {
                                transactionLogger.error("in else if amount not equal ---" + paymentRequest.getAmount());
                                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_AMOUNT, "Invalid amount / Amount is not matching with requested amount."));
                                error = "Invalid amount / Amount is not matching with requested amount.";
                                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_REFERENCE_AMOUNT.toString(), ErrorType.REFERENCE_VALIDATION.toString());
                                writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide a valid amount");
                                return response;
                            }
                            else if (!db_currency.equalsIgnoreCase(paymentRequest.getCurrency()))
                            {
                                transactionLogger.error("in else if Currency not equal ---" + paymentRequest.getCurrency());
                                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_CURRENCY, " Invalid currency / Currency is not matching with requested currency"));
                                error = " Invalid currency / Currency is not matching with requested currency.";
                                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_REFERENCE_CURRENCY.toString(), ErrorType.REFERENCE_VALIDATION.toString());
                                writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide a valid currency");
                                return response;
                            }
                            else if (functions.isEmptyOrNull(transactionDetailsVO.getStatus()) || (!transactionDetailsVO.getStatus().equalsIgnoreCase("begun") && !transactionDetailsVO.getStatus().equalsIgnoreCase("authstarted")))
                            {
                                transactionLogger.error("in else if status ---" + transactionDetailsVO.getStatus());
                                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_TRACKINGID, "Invalid payment ID or payment ID not found."));
                                error = "Invalid payment ID or payment ID not found.";
                                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_REFERENCE_TRACKINGID.toString(), ErrorType.REFERENCE_VALIDATION.toString());
                                writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide a valid payment Id");
                                return response;
                            }
                            // order id check
                            else if (!paymentRequest.getMerchantTransactionId().equalsIgnoreCase(transactionDetailsVO.getDescription()))
                            {
                                transactionLogger.error("in else if Merchant transaction id not equal to db value ---" + paymentRequest.getMerchantTransactionId());
                                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_ORDER_DESCRIPTION, "Merchant Transaction ID does not match"));
                                error = "Merchant Transaction ID does not match.";
                                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_ORDER_DESCRIPTION.toString(), ErrorType.REFERENCE_VALIDATION.toString());
                                writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide correct merchantTransactionId");
                                return response;
                            }
                            transactionLogger.error(" ---- member id ---- " + transactionDetailsVO.getToid());
                            transactionLogger.error(" order id in trans detail ---------" + transactionDetailsVO.getDescription());
                            memID = transactionDetailsVO.getToid();
                            orderId = transactionDetailsVO.getDescription();
                        }
                        else if ("DB".equalsIgnoreCase(directKitValidatorVO.getTransactionType()))
                        {
                            memID = directKitValidatorVO.getMerchantDetailsVO().getMemberId();
                        }

                        Date date1 = new Date();
                        transactionLogger.error("getMemberDetails Start time 1#######" + date1.getTime());

                        MerchantDAO merchantDAO = new MerchantDAO();
                        MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(memID);

                        transactionLogger.error("getMemberDetails end time 1 ######" + new Date().getTime());
                        transactionLogger.error("getMemberDetails diff time 1 ######" + (new Date().getTime() - date1.getTime()));

                        String payMode = directKitValidatorVO.getPaymentType();
                        String cardType = directKitValidatorVO.getCardType();
                        String walletCurrency = directKitValidatorVO.getTransDetailsVO().getWalletCurrency();

                        transactionLogger.error(" PAY MODE --- " + payMode);
                        transactionLogger.error(" Card Type --- " + cardType);

                        if (functions.isEmptyOrNull(payMode) || !payMode.equalsIgnoreCase("3") || functions.isEmptyOrNull(cardType) || !cardType.equalsIgnoreCase("64"))
                        {
                            transactionLogger.error("in if invalid paymode cardtype ");

                            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMODE_CARDTYPE, "Invalid payment Type & Card Type"));
                            error = "Invalid payment Type & Card Type";
                            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_PAYMODE_CARDTYPE.toString(), ErrorType.VALIDATION.toString());
                            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide a valid Payment Type & Card Type");
                            return response;
                        }

                        if ("DB".equalsIgnoreCase(directKitValidatorVO.getTransactionType()))
                        {
                            String uniqueorder = null;
                            transactionLogger.error("order id ----- " + directKitValidatorVO.getTransDetailsVO().getOrderId());
                            uniqueorder = transactionHelper.checkorderuniqueness(merchantDetailsVO.getMemberId(), "", directKitValidatorVO.getTransDetailsVO().getOrderId());
                            if (!uniqueorder.equals(""))
                            {
                                transactionLogger.error("In if unique order error-----" + uniqueorder);
                                error = directKitValidatorVO.getTransDetailsVO().getOrderId() + "-Duplicate Order Id " + uniqueorder;
                                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.SYS_UNIQUEORDER, "Your transaction is already being processed. Kindly try to place transaction with unique order ID."));
                                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_UNIQUEORDER.toString(), ErrorType.SYSCHECK.toString());
                                writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Your transaction is already being processed. Kindly try to place transaction with unique order ID.");
                                return response;
                            }
                        }

                        transactionLogger.error(" wallet Currency ------ " + directKitValidatorVO.getTransDetailsVO().getWalletCurrency());
                        transactionLogger.error(" Currency ------ " + directKitValidatorVO.getTransDetailsVO().getCurrency());

                        Date date2 = new Date();
                        transactionLogger.error("getCardIdAndPaymodeIdFromPaymentBrand Start time 2 #######" + date2.getTime());

                        TerminalDAO terminalDAO = new TerminalDAO();
                        TerminalVO terminalVO = terminalDAO.getCardIdAndPaymodeIdFromPaymentBrand(memID, payMode, cardType, directKitValidatorVO.getTransDetailsVO().getCurrency());

                        transactionLogger.error("getCardIdAndPaymodeIdFromPaymentBrand end time 2 ######" + new Date().getTime());
                        transactionLogger.error("getCardIdAndPaymodeIdFromPaymentBrand diff time 2 ######" + (new Date().getTime() - date2.getTime()));

                        if (terminalVO == null || functions.isEmptyOrNull(terminalVO.getTerminalId()))
                        {
                            transactionLogger.error("in if invalid terminal/currency  ");
                            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_CURRENCY, "Invalid currency, currency should not be empty and accepts only [a-z][A-Z] with max length 3"));
                            error = "Invalid currency, currency should not be empty and accepts only [a-z][A-Z] with max length 3";
                            failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_CURRENCY.toString(), ErrorType.VALIDATION.toString());
                            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide a valid Currency ");
                            return response;
                        }

                        transactionLogger.error(" After VO TERMINAL ------ " + terminalVO.getTerminalId());
                        merchantDetailsVO.setAccountId(terminalVO.getAccountId());
                        directKitValidatorVO.setTerminalId(terminalVO.getTerminalId());

                        transactionLogger.error(" After VO ACCOUNT iD ------ " + terminalVO.getAccountId());

                        if (functions.isValueNull(merchantDetailsVO.getNotificationUrl()))
                        {
                            transactionLogger.error(" After VO notification URL ------ " + merchantDetailsVO.getNotificationUrl());
                            directKitValidatorVO.getTransDetailsVO().setNotificationUrl(merchantDetailsVO.getNotificationUrl());
                        }

                        directKitValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                        directKitValidatorVO.setTerminalVO(terminalVO);


                        transactionLogger.error("Error before Gateway---" + directKitValidatorVO.getErrorCodeListVO().getListOfError().size());
                        transactionLogger.error("Error before Gateway---" + directKitValidatorVO.getErrorCodeListVO().getListOfError());
                        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        {
                            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());
                            return response;
                        }
                        else
                        {
                            Date date3 = new Date();
                            transactionLogger.error("processQRTransaction Start time 3 #######" + date3.getTime());

                            directKitResponseVO = restDirectTransactionManager.processQRTransaction(directKitValidatorVO);

                            transactionLogger.error("processQRTransaction end time 3 ######" + new Date().getTime());
                            transactionLogger.error("processQRTransaction diff time 3 ######" + (new Date().getTime() - date3.getTime()));

                            Date date4 = new Date();
                            transactionLogger.error("setSuccessWalletAddressResponse Start time 4 #######" + date4.getTime());

                            writeDirectTransactionResponse.setSuccessWalletAddressResponse(response, directKitResponseVO, directKitValidatorVO);

                            transactionLogger.error("setSuccessWalletAddressResponse end time 4 ######" + new Date().getTime());
                            transactionLogger.error("setSuccessWalletAddressResponse diff time 4 ######" + (new Date().getTime() - date4.getTime()));

                            return response;
                        }
                    }
                }
                else
                {
                    directKitValidatorVO = new CommonValidatorVO();
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Payment Request"));
                    error = "Invalid Payment Type";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_PAYMENT_TYPE.toString(), ErrorType.VALIDATION.toString());
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide proper Payment Type");
                }
            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZConstraint Violation Exception while placing transaction via Rest", e);
            transactionLogger.error("PZConstraint Violation Exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
        }
        catch (PZDBViolationException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("PZ DBViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (PZGenericConstraintViolationException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("PZ GenericConstraintViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzGenericConstraint().getMessage()/* "Internal Error occurred,Please contact support for more information"*/);
        }
        catch (NoSuchAlgorithmException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("SystemError while placing transaction via Rest", e);
            transactionLogger.error("SystemError while placing transaction via Rest", e);
            writeDirectTransactionResponse.setRestCaptureResponseForError(response, directKitValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        transactionLogger.error("Rest process Transaction Response----" + gson.toJson(response));
        return response;
    }

    public Response processQRConfirmation(String id, @InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        Response response1 = new Response();
        restPaymentRequestVO = readDirectTransactionRequest.readRequestForQRConfirm(paymentRequest);
        response1 = processQRConfirmationJSON(id, restPaymentRequestVO);

        return response1;

    }

    public Response processQRConfirmationJSON(String id, RestPaymentRequestVO paymentRequest)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");

        transactionLogger.debug("Inside processQRConfirmationJSON ------");
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Functions functions = new Functions();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();

        Result result = null;
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();

        //ResponseVo of the client
        Response response = new Response();
        CommonValidatorVO directKitValidatorVO = null;
        PaymentManager paymentManager = new PaymentManager();

        String error = "";
        String transaction_status = "";
        String db_status = "";
        String walletAmount = "";
        String walletCurrency = "";

        try
        {
            transactionLogger.error("Rest Request for processTransaction----" + gson.toJson(paymentRequest));
            transactionLogger.error("id -----------" + id);

            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                directKitValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if (authResponse.equals("false"))
            {
                directKitValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                return response;
            }

            if (!functions.isValueNull(id) || !functions.isNumericVal(id) || id.length() > 20 || !ESAPI.validator().isValidInput("id", id, "Numbers", 10, false))
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_TRACKINGID));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, errorCodeListVO.getListOfError().get(0).getErrorDescription());
                return response;
            }

            // Status
            if (functions.isEmptyOrNull(paymentRequest.getStatus()) || !ESAPI.validator().isValidInput("status", paymentRequest.getStatus(), "SafeString", 20, false))
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide valid request");
                return response;
            }
            else if (functions.isValueNull(paymentRequest.getStatus()))
            {
                transaction_status = paymentRequest.getStatus();
            }

            // wallet Amount
            if (paymentRequest.getCustomerVO() != null)
            {
                if (functions.isValueNull(paymentRequest.getCustomerVO().getWalletAmount()) && !ESAPI.validator().isValidInput("walletAmount", paymentRequest.getCustomerVO().getWalletAmount(), "Amount", 20, false))
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_WALLET_AMOUNT, "Invalid wallet amount ,wallet amount should not be empty"));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide valid walletAmount");
                    return response;
                }
                else if (functions.isValueNull(paymentRequest.getCustomerVO().getWalletAmount()))
                {
                    walletAmount = paymentRequest.getCustomerVO().getWalletAmount();
                }

                // wallet currency
                if (functions.isValueNull(paymentRequest.getCustomerVO().getWalletCurrency()) && !ESAPI.validator().isValidInput("walletCurrency", paymentRequest.getCustomerVO().getWalletCurrency(), "SafeString", 3, false))
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_WALLET_CURRENCY, "Invalid wallet currency ,wallet currency should not be empty"));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide valid walletCurrency");
                    return response;
                }
                else if (functions.isValueNull(paymentRequest.getCustomerVO().getWalletCurrency()))
                {
                    walletCurrency = paymentRequest.getCustomerVO().getWalletCurrency();
                }
            }

            directKitValidatorVO = new CommonValidatorVO();
            GenericAddressDetailsVO genericAddressDetailsVO = new GenericAddressDetailsVO();
            GenericTransDetailsVO genericTransDetailsVO = new GenericTransDetailsVO();
            MerchantDetailsVO genericMerchantDetailsVO = new MerchantDetailsVO();

            Date date1 = new Date();
            transactionLogger.error("transactionDetailsVO Start time 1#######" + date1.getTime());

            TransactionManager transactionManager = new TransactionManager();
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(id);


            transactionLogger.error("transactionDetailsVO end time 1 ######" + new Date().getTime());
            transactionLogger.error("transactionDetailsVO diff time 1 ######" + (new Date().getTime() - date1.getTime()));

/*
            Date date2 = new Date();
            transactionLogger.error("commTransactionDetailsVO Start time 2#######"+date2.getTime());

            CommTransactionDetailsVO commTransactionDetailsVO = transactionManager.getTransactionDetailFromCommonForQR(id);

            transactionLogger.error("commTransactionDetailsVO end time 2 ######"+new Date().getTime());
            transactionLogger.error("commTransactionDetailsVO diff time 2 ######"+(new Date().getTime()-date2.getTime()));
*/

            transactionLogger.error("walletAmount ---- " + walletAmount);
            transactionLogger.error("walletCurrency ---- " + walletCurrency);

            if (functions.isValueNull(walletAmount))
            {
                genericTransDetailsVO.setWalletAmount(walletAmount);
            }
            else if (functions.isValueNull(transactionDetailsVO.getWalletAmount()))
            {
                genericTransDetailsVO.setWalletAmount(transactionDetailsVO.getWalletAmount());
            }

            if (functions.isValueNull(walletCurrency))
            {
                genericTransDetailsVO.setWalletCurrency(walletCurrency);
            }
            else if (functions.isValueNull(transactionDetailsVO.getWalletCurrency()))
            {
                genericTransDetailsVO.setWalletCurrency(transactionDetailsVO.getWalletCurrency());
            }

            if (functions.isValueNull(transactionDetailsVO.getToid()))
            {
                genericMerchantDetailsVO.setMemberId(transactionDetailsVO.getToid());
            }

            if (functions.isValueNull(transactionDetailsVO.getAmount()))
            {
                genericTransDetailsVO.setAmount(transactionDetailsVO.getAmount());
            }

            if (functions.isValueNull(transactionDetailsVO.getCurrency()))
            {
                genericTransDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
            }

            if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
            {
                genericTransDetailsVO.setNotificationUrl(transactionDetailsVO.getNotificationUrl());
            }


            transactionLogger.error("wallet amount ---- " + genericTransDetailsVO.getWalletAmount());
            transactionLogger.error("wallet currency ---- " + genericTransDetailsVO.getWalletCurrency());
            transactionLogger.error("DB status ---- " + transactionDetailsVO.getStatus());

            db_status = transactionDetailsVO.getStatus();

            String updateStatus = "";
            String message = "";
            String captureAmount = "0.00";

            transactionLogger.error("trackingId-----" + id);
            transactionLogger.error("transaction_status -----" + transaction_status);

            if (db_status.equalsIgnoreCase("capturesuccess") || db_status.equalsIgnoreCase("capturefailed") ||
                    db_status.equalsIgnoreCase("authsuccessful") || db_status.equalsIgnoreCase("authfailed"))
            {
                transactionLogger.error("in if db status already updated ---" + db_status);
                response.setStatus("Transaction Already Updated , please enter a new id");
                return response;
            }
            else if (functions.isValueNull(transaction_status))
            {
                result = new Result();
                result.setDescription(directKitResponseVO.getStatusMsg());
                if (transaction_status.equalsIgnoreCase("success"))
                {
                    transactionLogger.error("In if transaction_status -----------" + transaction_status);
                   /* if(db_status.equalsIgnoreCase("authstarted"))
                    {
                        updateStatus = "authsuccessful";
                        message = "success";
                    }
                    else*/
                    if (db_status.equalsIgnoreCase("capturestarted") || db_status.equalsIgnoreCase("authstarted"))
                    {
                        updateStatus = "capturesuccess";
                        message = "success";
                        captureAmount = transactionDetailsVO.getAmount();
                    }
                    else
                    {
                        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_TRACKINGID));
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, errorCodeListVO.getListOfError().get(0).getErrorDescription());
                        return response;
                    }

                    errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.TRANSACTION_SUCCEED);
                    result.setResultCode(errorCodeVO.getApiCode());
                    result.setDescription(errorCodeVO.getApiDescription());
                }
                else if (transaction_status.equalsIgnoreCase("fail"))
                {
                    transactionLogger.error("In else transaction_status -----------" + transaction_status);
                    if (db_status.equalsIgnoreCase("authstarted"))
                    {
                        updateStatus = "authfailed";
                        message = "fail";
                    }
                    else if (db_status.equalsIgnoreCase("capturestarted"))
                    {
                        updateStatus = "capturefailed";
                        message = "fail";
                    }
                    else
                    {
                        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_TRACKINGID));
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, errorCodeListVO.getListOfError().get(0).getErrorDescription());
                        return response;
                    }

                    errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.TRANSACTION_REJECTED);
                    result.setResultCode(errorCodeVO.getApiCode());
                    result.setDescription(errorCodeVO.getApiDescription());
                }
                else if (transaction_status.equalsIgnoreCase("cancel"))
                {
                    transactionLogger.error("In else transaction_status -----------" + transaction_status);
                    if (db_status.equalsIgnoreCase("begun"))
                    {
                        updateStatus = "failed";
                        message = "cancel";
                    }
                    else
                    {
                        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REFERENCE_TRACKINGID));
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, errorCodeListVO.getListOfError().get(0).getErrorDescription());
                        return response;
                    }

                    errorCodeVO = errorCodeUtils.getSuccessErrorCode(ErrorName.CANCEL_SUCCESSFUL);
                    result.setResultCode(errorCodeVO.getApiCode());
                    result.setDescription(errorCodeVO.getApiDescription());
                }
                else
                {
                    message = "Invalid Request provided";
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, null);
                    return response;
                }

            }
            else
            {
                message = "Invalid Request provided";
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, null);
                return response;
            }


            directKitValidatorVO.setTrackingid(id);
            directKitValidatorVO.setTransDetailsVO(genericTransDetailsVO);
            directKitValidatorVO.setMerchantDetailsVO(genericMerchantDetailsVO);
            directKitValidatorVO.setAddressDetailsVO(genericAddressDetailsVO);

            Date date3 = new Date();
            transactionLogger.error("processQRConfirmation Start time 3#######" + date3.getTime());

            directKitResponseVO = restDirectTransactionManager.processQRConfirmation(directKitValidatorVO, updateStatus, captureAmount);
            //writeDirectTransactionResponse.setSuccessWalletAddressResponse(response, directKitResponseVO, directKitValidatorVO);

            transactionLogger.error("processQRConfirmation end time 3 ######" + new Date().getTime());
            transactionLogger.error("processQRConfirmation diff time 3 ######" + (new Date().getTime() - date3.getTime()));


            transactionLogger.error("notification url ----------- " + transactionDetailsVO.getNotificationUrl());
            if (functions.isValueNull(transactionDetailsVO.getNotificationUrl()))
            {
                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                asyncNotificationService.sendNotification(transactionDetailsVO, id, updateStatus, message);
            }

            response.setResult(result);
            response.setStatus("Transaction status updated successfully");
            return response;

        }
        catch (PZDBViolationException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("PZ DBViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (Exception e)
        {
            logger.error("Exception in QR confirmation ------- ", e);
        }

        transactionLogger.error("QR Confirmation Transaction Response----" + gson.toJson(response));

        return response;
    }


    public Response processQRInquiryStatus(String id, @InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        Response response1 = new Response();

        restPaymentRequestVO = readDirectTransactionRequest.readRequestForRestCaptureCancelRefundRecurring(paymentRequest);
        response1 = processQRInquiryStatusJSON(id, restPaymentRequestVO);

        return response1;
    }

    public Response processQRInquiryStatusJSON(String id, RestPaymentRequestVO paymentRequestVO)
    {
        transactionLogger.error("Inside rest in processQRInquiryStatusJSON :::");
        logger.error("Id----" + id);
        response.addHeader("Access-Control-Allow-Origin", "*");

        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Functions functions = new Functions();

        Response response = new Response();
        CommonValidatorVO commonValidatorVO = null;
        String encId = "";
        String error = "";
        try
        {
            encId = URLEncoder.encode(id, "UTF-8");
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception while url encoded--------" + e);
        }
        if (!ESAPI.validator().isValidInput("encId", encId, "Numbers", 50, false))
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_TRACKINGID, "Invalid TrackingID."));
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide TrackingID");
            return response;
        }

        String authResponse = request1.getAttribute("authfail").toString();
        if (authResponse == null || authResponse.equals(""))
        {
            commonValidatorVO = new CommonValidatorVO();
            logger.debug("Inside failResponse---" + authResponse);
            //error = "Authorization failed";
            //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;
        }
        else if (authResponse.equals("false"))
        {
            commonValidatorVO = new CommonValidatorVO();
            logger.debug("Inside failResponse---" + authResponse);
            //error = "Authorization failed";
            //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;
        }

        //If ID is valid then procced furher
        transactionLogger.error("QR Inquiry Status------" + gson.toJson(paymentRequestVO));
        commonValidatorVO = readDirectTransactionRequest.readRequestForRestCaptureCancelRefundRecurringJSON(paymentRequestVO, request1);
        if (commonValidatorVO == null)
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Capture Request provided."));
            writeDirectTransactionResponse.setRestCaptureResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while transaction");
            return response;
        }
        if (null != paymentRequestVO && INQUIRY.equals(paymentRequestVO.getPaymentType()))
        {
            transactionLogger.error("Inside qr Inquiry :::");
            logger.error("ID----" + encId);

            if (!functions.isValueNull(commonValidatorVO.getIdType()))
            {
                commonValidatorVO.setIdType("PID");
            }

            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Reversal Request provided."));
                writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while cancelling transaction");
                return response;
            }

            try
            {
                if (commonValidatorVO.getIdType().equalsIgnoreCase("PID"))
                {
                    commonValidatorVO.setTrackingid(encId);
                }
                else if (commonValidatorVO.getIdType().equalsIgnoreCase("MID"))
                {
                    commonValidatorVO.getTransDetailsVO().setOrderId(encId);
                }
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                commonValidatorVO = restCommonInputValidator.performQRInquiryValidation(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }

                Date date1 = new Date();
                transactionLogger.error("processInquiry Start time 1#######" + date1.getTime());
                directKitResponseVO = restDirectTransactionManager.processInquiry(commonValidatorVO);

                transactionLogger.error("processInquiry end time 1 ######" + new Date().getTime());
                transactionLogger.error("processInquiry diff time 1 ######" + (new Date().getTime() - date1.getTime()));


                Date date2 = new Date();
                transactionLogger.error("getMemberDetails Start time 2#######" + date2.getTime());

                MerchantDAO merchantDAO = new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(commonValidatorVO.getMerchantDetailsVO().getMemberId());

                transactionLogger.error("getMemberDetails end time 2 ######" + new Date().getTime());
                transactionLogger.error("getMemberDetails diff time 2 ######" + (new Date().getTime() - date2.getTime()));

                if (functions.isValueNull(merchantDetailsVO.getContact_persons()))
                {
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                }

                writeDirectTransactionResponse.setSuccessRestInquiryResponse(response, directKitResponseVO, commonValidatorVO);
            }
            catch (PZDBViolationException e)
            {
                transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
                writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (PZConstraintViolationException e)
            {
                transactionLogger.error("PZ ConstraintViolation exception while inquiring transaction via WebService", e);
                writeDirectTransactionResponse.setRestRefundResponseForError(response, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
            catch (NoSuchAlgorithmException e)
            {
                transactionLogger.error("PZ Technical exception while inquiring transaction via WebService", e);
                writeDirectTransactionResponse.setRestCaptureResponseForError(response, commonValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }

        }
        else
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_TYPE, "Invalid payment type provided."));
            writeDirectTransactionResponse.setRestDeleteTokenResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute while cancelling transaction");
            transactionLogger.error("QR Inquiry Status Response ------" + gson.toJson(response));
            return response;
        }
        transactionLogger.error("QR Inquiry Status Response ------" + gson.toJson(response));
        return response;
    }

    @Override
    public Response processGetInstallmentWithToken(String id, @InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        RestPaymentRequestVO restPaymentRequestVO = new RestPaymentRequestVO();
        Response response1 = new Response();

        restPaymentRequestVO = readDirectTransactionRequest.readRequestForTokenInstallmentCount(paymentRequest);

        response1 = processGetInstallmentWithTokenJSON(id, restPaymentRequestVO);

        return response1;
    }

    @Override
    public Response processGetInstallmentWithTokenJSON(String id, RestPaymentRequestVO paymentRequestVO)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");

        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Response response = new Response();
        CommonValidatorVO commonValidatorVO = null;

        if (!ESAPI.validator().isValidInput("id", id, "onlyAlphanum", 32, false))
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_TOKEN, "Invalid registration id."));
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide registration id");
            return response;
        }
        String authResponse = request1.getAttribute("authfail").toString();
        if (authResponse == null || authResponse.equals(""))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;
        }
        else if (authResponse.equals("false"))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response);
            return response;
        }

        transactionLogger.error("Rest Get Installment Count Request---" + gson.toJson(paymentRequestVO));
        commonValidatorVO = readDirectTransactionRequest.readRequestForTokenInstallmentCount(paymentRequestVO, request1);
        commonValidatorVO.setToken(id);
        if (commonValidatorVO == null)
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, errorCodeListVO.getListOfError().get(0).getErrorDescription());
            return response;
        }
        try
        {
            commonValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

            commonValidatorVO = restCommonInputValidator.performRestInstallmentCountValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return response;
            }
            else
            {
                directKitResponseVO = restDirectTransactionManager.processGetEmiCount(commonValidatorVO);
                if (directKitResponseVO == null)
                {
                    transactionLogger.error("--------Installment not found-----------");
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.SYS_EMI_NOT_FOUND, "Installment not Available."));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Installment not Available.");
                    return response;
                }
                boolean isInstallmetAvailable = Functions.checkInstallment(directKitResponseVO.getStartDate(), directKitResponseVO.getEndDate());
                if (isInstallmetAvailable)
                {
                    transactionLogger.error("--------Installment not found within period-----------");
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.SYS_EMI_NOT_FOUND, "Installment not Available."));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Installment not Available.");
                    return response;
                }
            }
            writeDirectTransactionResponse.setSuccessInstallmentCountResponse(response, directKitResponseVO, commonValidatorVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZConstraint Violation Exception while placing transaction via Rest", e);
            transactionLogger.error("PZConstraint Violation Exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZDBViolationException while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (PZGenericConstraintViolationException e)
        {
            logger.error("PZGenericConstraintViolationException while placing transaction via Rest", e);
            transactionLogger.error("PZGenericConstraintViolationException while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzGenericConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "This functionality is not allowed for your gateway. please contact tech side for support");
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ GenericConstraintViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (ParseException e)
        {
            logger.error("PZ ParseException exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        return response;
    }

    public Response invalidRegistrationIdForInstallment(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        response.addHeader("Access-Control-Allow-Origin", "*");
        Response response = new Response();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();

        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REGISTRATIONID, "Invalid Request provided."));
        writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, errorCodeListVO.getListOfError().get(0).getErrorDescription());

        return response;
    }

    @Override
    public Response processInitiateJSON(RestPaymentRequestVO restPaymentRequestVO)
    {

        response.addHeader("Access-Control-Allow-Origin", "*");
        logger.debug("Inside processInitiateAuthentcation:::");
        transactionLogger.debug("Inside processInitiateAuthentcation:::");
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Functions functions = new Functions();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        String error = "";

        //ResponseVo of the client
        Response response = new Response();
        CommonValidatorVO directKitValidatorVO = null;
        PaymentManager paymentManager = new PaymentManager();

        try
        {
            if (request1.getAttribute("authfail") != null)
            {
                String authResponse = request1.getAttribute("authfail").toString();
                if (authResponse == null || authResponse.equals(""))
                {
                    directKitValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
                else if (authResponse.equals("false"))
                {
                    directKitValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
            }
            else
            {
                transactionLogger.debug("Inside invalid terminalid:::");
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_TRACKINGID, "Invalid TrackingID."));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide TrackingID");
                return response;
            }

            logger.debug("Payment type---" + restPaymentRequestVO.getPaymentType());
            if (restPaymentRequestVO != null && (!functions.isValueNull(restPaymentRequestVO.getPaymentType()) || !ESAPI.validator().isValidInput("transactionType", restPaymentRequestVO.getPaymentType(), "transactionType", 2, false)))
            {
                directKitValidatorVO = new CommonValidatorVO();
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_TYPE, "Invalid Payment Type,Accept only [0-9] with Max Length 2"));
                error = "Invalid payment type, Payment type should not be empty";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_PAYMENT_TYPE.toString(), ErrorType.VALIDATION.toString());
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Transaction Type");
                return response;
            }
            else
            {
                if (restPaymentRequestVO != null && restPaymentRequestVO.getPaymentType() != null && (restPaymentRequestVO.getPaymentType().equals(PRE_AUTH) || restPaymentRequestVO.getPaymentType().equals(DEBIT)))
                {
                    transactionLogger.error("Rest Request for processInitiateAuthentication---" + gson.toJson(restPaymentRequestVO));
                    directKitValidatorVO = readDirectTransactionRequest.readInitiateAuthentication(restPaymentRequestVO, request1);
                    if (directKitValidatorVO == null)
                    {
                        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                        error = "Invalid request provided";
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_REQUEST_NULL.toString(), ErrorType.VALIDATION.toString());
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
                        return response;
                    }
                    else
                    {
                        directKitValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
                        directKitValidatorVO.setErrorCodeListVO(errorCodeListVO);
                        transactionLogger.error("Check performRestInitiateAuthenticationValidation-----");
                        directKitValidatorVO = restCommonInputValidator.performRestInitiateAuthenticationValidation(directKitValidatorVO);
                        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        {
                            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());
                            return response;
                        }
                        else
                        {
                            directKitValidatorVO = transactionHelper.performRESTAPISystemCheckForInitiateAuthentication(directKitValidatorVO);
                            if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                            {
                                writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());
                                return response;
                            }
                            else
                            {
                                Date date3 = new Date();
                                transactionLogger.debug("DirectTransactionRESTIMPL processInitiateAuthentication start #########" + date3.getTime());
                                directKitResponseVO = restDirectTransactionManager.processInitiateAuthentication(directKitValidatorVO);
                                writeDirectTransactionResponse.setInitiateAuthenticationResponse(response, directKitResponseVO, directKitValidatorVO);
                                transactionLogger.debug("DirectTransactionRESTIMPL processInitiateAuthentication end #########" + new Date().getTime());
                                transactionLogger.debug("DirectTransactionRESTIMPL processInitiateAuthentication diff #########" + (new Date().getTime() - date3.getTime()));

                            }
                        }
                    }
                }

                else
                {
                    directKitValidatorVO = new CommonValidatorVO();
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Payment Request"));
                    error = "Invalid Payment Type";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_PAYMENT_TYPE.toString(), ErrorType.VALIDATION.toString());
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide proper Payment Type");
                }
            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZConstraint Violation Exception while placing InitiateAuthentication via Rest", e);
            transactionLogger.error("PZConstraint Violation Exception while placing InitiateAuthentication via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
        }
        catch (PZDBViolationException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("PZ DBViolation exception while placing InitiateAuthentication via Rest", e);
            transactionLogger.error("PZ Technical exception while placing InitiateAuthentication via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (NoSuchAlgorithmException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("SystemError while placing InitiateAuthentication via Rest", e);
            transactionLogger.error("SystemError while placing InitiateAuthentication via Rest", e);
            writeDirectTransactionResponse.setRestCaptureResponseForError(response, directKitValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }

        transactionLogger.error("Rest process InitiateAuthentication Response----" + gson.toJson(response));
        return response;
    }

    @Override
    public Response processAuthenticateJSON(RestPaymentRequestVO restPaymentRequestVO)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        logger.debug("Inside processAuthenticateJSON:::");
        transactionLogger.debug("Inside processAuthenticateJSON:::");
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Functions functions = new Functions();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        String error = "";

        //ResponseVo of the client
        Response response = new Response();
        DirectCaptureValidatorVO directCaptureValidatorVO = null;
        CommonValidatorVO directKitValidatorVO = null;
        PaymentManager paymentManager = new PaymentManager();

        try
        {
            if (request1.getAttribute("authfail") != null)
            {
                String authResponse = request1.getAttribute("authfail").toString();
                if (authResponse == null || authResponse.equals(""))
                {
                    directKitValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
                else if (authResponse.equals("false"))
                {
                    directKitValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
            }
            else
            {
                transactionLogger.debug("Inside invalid terminalid:::");
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_TRACKINGID, "Invalid TrackingID."));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide TrackingID");
                return response;
            }

            logger.debug("Payment type---" + restPaymentRequestVO.getPaymentType());
            if (restPaymentRequestVO != null && (!functions.isValueNull(restPaymentRequestVO.getPaymentType()) || !ESAPI.validator().isValidInput("transactionType", restPaymentRequestVO.getPaymentType(), "transactionType", 2, false)))
            {
                directKitValidatorVO = new CommonValidatorVO();
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_PAYMENT_TYPE, "Invalid Payment Type,Accept only [0-9] with Max Length 2"));
                error = "Invalid payment type, Payment type should not be empty";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_PAYMENT_TYPE.toString(), ErrorType.VALIDATION.toString());
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Transaction Type");
                return response;
            }
            else
            {
                if (restPaymentRequestVO != null && restPaymentRequestVO.getPaymentType() != null && (restPaymentRequestVO.getPaymentType().equals(PRE_AUTH) || restPaymentRequestVO.getPaymentType().equals(DEBIT)))
                {
                    transactionLogger.error("Rest Request for processAuthenticateJSON---" + gson.toJson(restPaymentRequestVO));
                    directKitValidatorVO = readDirectTransactionRequest.readAuthenticate(restPaymentRequestVO, request1);
                    if (directKitValidatorVO == null)
                    {
                        errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                        error = "Invalid request provided";
                        failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_REQUEST_NULL.toString(), ErrorType.VALIDATION.toString());
                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
                        return response;
                    }
                    else
                    {
                        directKitValidatorVO.getTransDetailsVO().setHeader("Client =" + Functions.getIpAddress(request1) + ":" + request1.getServerPort() + ",X-Forwarded=" + request1.getServletPath());
                        directKitValidatorVO.setErrorCodeListVO(errorCodeListVO);
                        transactionLogger.error("Check processAuthenticateJSON-----");
                        directKitValidatorVO = restCommonInputValidator.performRestInitiateAuthenticationValidation(directKitValidatorVO);
                        if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                        {
                            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());
                            return response;
                        }
                        else
                        {
                            directKitValidatorVO = transactionHelper.performRESTAPISystemCheckForInitiateAuthentication(directKitValidatorVO);
                            if (!directKitValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                            {
                                writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, directKitValidatorVO.getErrorMsg());
                                return response;
                            }
                            else
                            {
                                Date date3 = new Date();
                                transactionLogger.debug("DirectTransactionRESTIMPL processAuthenticateJSON start #########" + date3.getTime());
                                directKitResponseVO = restDirectTransactionManager.processAuthenticate(directKitValidatorVO);
                                writeDirectTransactionResponse.setAuthenticateResponse(response, directKitResponseVO, directKitValidatorVO);
                                transactionLogger.debug("DirectTransactionRESTIMPL processAuthenticateJSON end #########" + new Date().getTime());
                                transactionLogger.debug("DirectTransactionRESTIMPL processAuthenticateJSON diff #########" + (new Date().getTime() - date3.getTime()));

                            }
                        }
                    }
                }

                else
                {
                    directKitValidatorVO = new CommonValidatorVO();
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Payment Request"));
                    error = "Invalid Payment Type";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_PAYMENT_TYPE.toString(), ErrorType.VALIDATION.toString());
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, null, errorCodeListVO, PZResponseStatus.ERROR, "Please provide proper Payment Type");
                }
            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZConstraint Violation Exception while placing InitiateAuthentication via Rest", e);
            transactionLogger.error("PZConstraint Violation Exception while placing InitiateAuthentication via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
        }
        catch (PZDBViolationException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("PZ DBViolation exception while placing InitiateAuthentication via Rest", e);
            transactionLogger.error("PZ Technical exception while placing InitiateAuthentication via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, directKitValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (NoSuchAlgorithmException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), directKitValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("SystemError while placing InitiateAuthentication via Rest", e);
            transactionLogger.error("SystemError while placing InitiateAuthentication via Rest", e);
            writeDirectTransactionResponse.setRestCaptureResponseForError(response, directKitValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }

        transactionLogger.error("Rest process InitiateAuthentication Response----" + gson.toJson(response));
        return response;
    }

    public Response processSendSmsCode(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        transactionLogger.debug("Inside processSendSmsCode ---");
        Response response1 = new Response();
        RestPaymentRequestVO restPaymentRequestVO = readDirectTransactionRequest.readSMSCode(paymentRequest);
        response1 = processSendSmsCodeJSON(restPaymentRequestVO);
        return response1;
    }

    public Response processSendSmsCodeJSON(RestPaymentRequestVO paymentRequest)
    {
        transactionLogger.debug("Inside processSendSmsCodeJSON ----");
        Response response1 = new Response();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        Response response = new Response();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        PaymentManager paymentManager = new PaymentManager();
        Functions functions = new Functions();
        //CommonValidatorVO directKitValidatorVO = null;
        String error = "";
        try
        {
            if (request1.getAttribute("authfail") != null)
            {
                String authResponse = request1.getAttribute("authfail").toString();
                if (authResponse == null || authResponse.equals(""))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
                else if (authResponse.equals("false"))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
            }
            // System.out.println("after authorization ----------------------------");
            commonValidatorVO = readDirectTransactionRequest.readSMSCodeJSON(paymentRequest);

            if (commonValidatorVO == null)
            {
                transactionLogger.debug("commonValidatorVO null ---");
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                error = "Invalid request provided";
                //  BELOW OR NOT ?
                //failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.VALIDATION_REQUEST_NULL.toString(), ErrorType.VALIDATION.toString());
                writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
                return response1;
            }
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

          /*  */
            commonValidatorVO = restCommonInputValidator.performSendSmsCodeValidation(commonValidatorVO);
            transactionLogger.debug("After validation --------------------------");
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return response1;
            }
            directKitResponseVO = restDirectTransactionManager.processSendSmsCode(commonValidatorVO);

            writeDirectTransactionResponse.setSuccessRestSendSmsResponse(response1, directKitResponseVO, commonValidatorVO);
        }
        catch (PZConstraintViolationException e)
        {
            try
            {
                if (functions.isValueNull(commonValidatorVO.getTrackingid()))
                {
                    paymentManager.updateDetailsTablewithErrorName(ErrorName.VALIDATION_TRACKINGID.toString(), commonValidatorVO.getTrackingid());

                }
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("PZConstraint Violation Exception while placing transaction via Rest", e);
            transactionLogger.error("PZConstraint Violation Exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
        }
        catch (PZDBViolationException e)
        {
            try
            {
                paymentManager.updateDetailsTablewithErrorName(ErrorName.SYS_INTERNAL_ERROR.toString(), commonValidatorVO.getTrackingid());
            }
            catch (PZDBViolationException d)
            {
                logger.error("----PZDBViolationException in update with error name-----", d);
            }
            logger.error("PZ DBViolation exception while placing transaction via Rest", e);
            transactionLogger.error("PZ Technical exception while placing transaction via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        return response1;
    }

    public Response processGetPaymentAndCardType(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        Response response1 = new Response();
        RestPaymentRequestVO restPaymentRequestVO = readDirectTransactionRequest.readProcessGetPaymentAndCardType(paymentRequest);
        response1 = processGetPaymentAndCardTypeJSON(restPaymentRequestVO);
        return response1;
    }

    public Response processGetPaymentAndCardTypeJSON(RestPaymentRequestVO paymentRequest)
    {
        Response response1 = new Response();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String error = "";
        if (request1.getAttribute("authfail") != null)
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                commonValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                writeDirectTransactionResponse.setFailAuthTokenResponse(response1);
                return response1;
            }
            else if (authResponse.equals("false"))
            {
                commonValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                writeDirectTransactionResponse.setFailAuthTokenResponse(response1);
                return response1;
            }
        }
        commonValidatorVO = readDirectTransactionRequest.readProcessGetPaymentAndCardTypeJSON(paymentRequest);
        if (commonValidatorVO == null)
        {
            transactionLogger.debug("commonValidatorVO null ---");
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            error = "Invalid request provided";
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
            return response1;
        }
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        try
        {
            commonValidatorVO = restCommonInputValidator.performRestGetPaymentAndCardTypeValidation(commonValidatorVO);
            directKitResponseVO = restDirectTransactionManager.processGetPaymentAndCardTypePerCurrency(commonValidatorVO);
            writeDirectTransactionResponse.setSuccessRestGetPaymentAndCardTypeResponse(response1, directKitResponseVO, commonValidatorVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZConstraint Violation Exception while placing processGetPaymentAndCardTypeJSON via Rest", e);
            transactionLogger.error("PZConstraint Violation Exception while placing processGetPaymentAndCardTypeJSON via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while placing processGetPaymentAndCardTypeJSON via Rest", e);
            transactionLogger.error("PZDBViolationException while placing processGetPaymentAndCardTypeJSON via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        return response1;
    }

    public Response processSaveTransactionReceipt(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        Response response1 = new Response();
        RestPaymentRequestVO restPaymentRequestVO = readDirectTransactionRequest.readProcessSaveTransactionReceipt(paymentRequest);
        response1 = processSaveTransactionReceiptJSON(restPaymentRequestVO);
        return response1;
    }

    public Response processSaveTransactionReceiptJSON(RestPaymentRequestVO paymentRequestVO)
    {
        Response response1 = new Response();
        CommonValidatorVO commonValidatorVO = null;
        DirectKitResponseVO directKitResponseVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String error = "";
        if (request1.getAttribute("authfail") != null)
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                commonValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                writeDirectTransactionResponse.setFailAuthTokenResponse(response1);
                return response1;
            }
            else if (authResponse.equals("false"))
            {
                commonValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                writeDirectTransactionResponse.setFailAuthTokenResponse(response1);
                return response1;
            }
        }
        commonValidatorVO = readDirectTransactionRequest.readProcessSaveTransactionReceiptJSON(paymentRequestVO);
        if (commonValidatorVO == null)
        {
            transactionLogger.debug("commonValidatorVO null ---");
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            error = "Invalid request provided";
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
            return response1;
        }
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        try
        {
            commonValidatorVO = restCommonInputValidator.performRestSaveTransactionReceiptValidation(commonValidatorVO);
            directKitResponseVO = restDirectTransactionManager.processSaveTransactionReceipt(commonValidatorVO);
            writeDirectTransactionResponse.setSuccessRestSaveTransactionReceiptResponse(response1, directKitResponseVO, commonValidatorVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZConstraint Violation Exception while placing processSaveTransactionReceiptJSON via Rest", e);
            transactionLogger.error("PZConstraint Violation Exception while placing processSaveTransactionReceiptJSON via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while placing processSaveTransactionReceiptJSON via Rest", e);
            transactionLogger.error("PZDBViolationException while placing processSaveTransactionReceiptJSON via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        return response1;
    }

    public Response processGetTransactionList(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        Response response1 = new Response();
        RestPaymentRequestVO restPaymentRequestVO = readDirectTransactionRequest.readProcessGetTransactionList(paymentRequest);
        response1 = processGetTransactionListJSON(restPaymentRequestVO);
        return response1;
    }


    public Response processGetTransactionListJSON(RestPaymentRequestVO paymentRequestVO)
    {
        Response response1                      = new Response();
        CommonValidatorVO commonValidatorVO     = null;
        DirectKitResponseVO directKitResponseVO = null;
        List<TransactionVO> transactionList     = null;
        ErrorCodeListVO errorCodeListVO         = new ErrorCodeListVO();
        String error = " ";
        if (request1.getAttribute("authfail") != null)
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                commonValidatorVO   = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error               = "Authorization failed";
                writeDirectTransactionResponse.setFailAuthTokenResponse(response1);
                return response1;
            }
            else if (authResponse.equals("false"))
            {
                commonValidatorVO   = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error               = "Authorization failed";
                writeDirectTransactionResponse.setFailAuthTokenResponse(response1);
                return response1;
            }
        }   // This method is for auth token
        Functions functions = new Functions();
        String restRequest  = gson.toJson(paymentRequestVO);
        if(functions.isValueNull(restRequest))
        {
            try
            {
                JSONObject jsonObject = new JSONObject(restRequest);
                if (jsonObject.has("cardVO"))
                {
                    JSONObject cardJson=jsonObject.getJSONObject("cardVO");
                    String cardnumber   = "";
                    String expiryMonth  = "";
                    String expiryYear   = "";
                    String cvv          = "";
                    if(cardJson.has("number"))
                        cardnumber = functions.maskingPan(cardJson.getString("number"));
                    if(cardJson.has("expiryMonth"))
                        expiryMonth = functions.maskingNumber(cardJson.getString("expiryMonth"));
                    if(cardJson.has("expiryYear"))
                        expiryYear = functions.maskingNumber(cardJson.getString("expiryYear"));
                    if(cardJson.has("cvv"))
                        cvv = functions.maskingNumber(cardJson.getString("cvv"));

                    cardJson.put("number",cardnumber);
                    cardJson.put("expiryMonth",expiryMonth);
                    cardJson.put("expiryYear",expiryYear);
                    cardJson.put("cvv",cvv);
                    jsonObject.put("cardVO",cardJson);
                }
                transactionLogger.error("Rest Request for processGetTransactionListJSON---" + jsonObject.toString());
            }
            catch (JSONException e)
            {
                transactionLogger.error("JSONException---->",e);
            }
        }

        commonValidatorVO = readDirectTransactionRequest.readProcessGetTransactionListJSON(paymentRequestVO);
        if (commonValidatorVO == null)
        {
            transactionLogger.debug("commonValidatorVO null ---");
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            error = "Invalid request provided";
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
            return response1;
        }
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        try
        {
            commonValidatorVO   = restCommonInputValidator.performRestGetTransactionListValidation(commonValidatorVO);
            transactionList     = restDirectTransactionManager.processGetTransactionListVirtualCheckout(commonValidatorVO);
            writeDirectTransactionResponse.setSuccessRestGetTransactionListResponse(response1, directKitResponseVO, commonValidatorVO, transactionList);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZConstraint Violation Exception while placing processSaveTransactionReceiptJSON via Rest", e);
            transactionLogger.error("PZConstraint Violation Exception while placing processSaveTransactionReceiptJSON via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while placing processSaveTransactionReceiptJSON via Rest", e);
            transactionLogger.error("PZDBViolationException while placing processSaveTransactionReceiptJSON via WebService", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("SystemError while placing InitiateAuthentication via Rest", e);
            transactionLogger.error("SystemError while placing InitiateAuthentication via Rest", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        // commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        return response1;
    }

    public Response processCustomerCardWhitelisting(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        Response response1 = new Response();
        RestPaymentRequestVO restPaymentRequestVO = readDirectTransactionRequest.readProcessCustomerCardWhitelisting(paymentRequest);
        response1 = processCustomerCardWhitelistingJSON(restPaymentRequestVO);
        return response1;
    }

    public Response processCustomerCardWhitelistingJSON(RestPaymentRequestVO paymentRequestVO)
    {
        Response response1 = new Response();
        CommonValidatorVO commonValidatorVO = null;
        if (request1.getAttribute("authfail") != null)
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---" + authResponse);
                writeDirectTransactionResponse.setFailAuthTokenResponse(response1);
                return response1;
            }
            else if (authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---" + authResponse);
                writeDirectTransactionResponse.setFailAuthTokenResponse(response1);
                return response1;
            }
        }
        transactionLogger.error("Rest Request for processCustomerCardWhitelistingJSON---" + gson.toJson(paymentRequestVO));
        commonValidatorVO = readDirectTransactionRequest.readProcessCardWhitelistingJSON(paymentRequestVO);
        if (commonValidatorVO != null)
        {
            try
            {
                commonValidatorVO = restCommonInputValidator.performRestCardWhitelistValidation(commonValidatorVO);
                commonValidatorVO = restDirectTransactionManager.processCardWhitelisting(commonValidatorVO);
                writeDirectTransactionResponse.setCardWhitelistingResponse(response1,commonValidatorVO);
            }
            catch (PZConstraintViolationException e)
            {
                logger.error("PZConstraint Violation Exception while placing processSaveTransactionReceiptJSON via Rest", e);
                transactionLogger.error("PZConstraint Violation Exception while placing processSaveTransactionReceiptJSON via WebService", e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
            }
            catch (PZDBViolationException e)
            {
                logger.error("PZ DBViolation exception while placing processSaveTransactionReceiptJSON via Rest", e);
                transactionLogger.error("PZDBViolationException while placing processSaveTransactionReceiptJSON via WebService", e);
                writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
            }
        }
        return response1;
    }
    public Response processGetDailySalesReport(@InjectParam RestPaymentRequest paymentRequest)
    {
        if (request1.getQueryString() != null)
        {
            Response response1 = new Response();
            Result result = new Result();
            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response1.setResult(result);
            return response1;
        }
        Response response1 = new Response();
        RestPaymentRequestVO restPaymentRequestVO = readDirectTransactionRequest.readProcessGetDailySalesReport(paymentRequest);
        response1 = processGetDailySalesReportJSON(restPaymentRequestVO);
        return response1;
    }


    public Response processGetDailySalesReportJSON(RestPaymentRequestVO paymentRequest){

        Response response1 = new Response();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String error = "";

        String authResponse = request1.getAttribute("authfail").toString();
        if (authResponse != null && !authResponse.equals("") && authResponse.equals("false"))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeDirectTransactionResponse.setFailAuthTokenResponse(response1);
            return response1;
        }

        commonValidatorVO = readDirectTransactionRequest.readProcessGetDailySalesReportJSON(paymentRequest);
        if (commonValidatorVO == null)
        {
            errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
            return response1;
        }
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        try
        {
            commonValidatorVO = restCommonInputValidator.performRestGetDailySalesValidation(commonValidatorVO);
            directKitResponseVO = restDirectTransactionManager.processGetDailySalesReport(commonValidatorVO);
            writeDirectTransactionResponse.setSuccessRestGetDailySalesReportResponse(response1, directKitResponseVO, commonValidatorVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("Exception ---- ", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
        }
        catch (PZDBViolationException e)
        {
            logger.error("Exception ---- ", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("Exception ---- ", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response1, commonValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        // commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        return response1;
    }

    @Override
    public Response createOTP(@InjectParam RestPaymentRequest merchantRequest)
    {
        transactionLogger.error("Inside createOTP ===== " + "Memberid = " + merchantRequest.getMerchant_id() + ", MerchantTransactionId = " + merchantRequest.getMerchantTransactionId() + ", MobileNumber = " + merchantRequest.getCustomer().getMobile() + ", email = " + merchantRequest.getCustomer().getEmail());

        if(request1.getQueryString()!=null){
            Response response=new Response();
            Result result=new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }

        RestPaymentRequestVO merchantRequestVO = readDirectTransactionRequest.getMerchantRequestVO(merchantRequest);

        Response merchantServiceResponseVO = createOTPJSON(merchantRequestVO);

        return merchantServiceResponseVO;
    }

    @Override
    public Response createOTPJSON(RestPaymentRequestVO merchantRequestVO)
    {
        transactionLogger.error("Inside createOTPJSON ===== " + "Memberid = " + merchantRequestVO.getMerchant_id() + ", MerchantTransactionId = " + merchantRequestVO.getMerchantTransactionId() + ", MobileNumber = " + merchantRequestVO.getCustomerVO().getMobile() + ", email = " + merchantRequestVO.getCustomerVO().getEmail());

        Response response = new Response();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String error = "";

        try
        {
        /*if (request1.getAttribute("authfail") != null)
        {
            logger.error("Inside if request1.getAttribute authtoken--" + request1.getAttribute("authfail"));

            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                commonValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
//                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                writeDirectTransactionResponse.setFailedOTPGeneratedResponse(merchantResponseVO, directKitResponseVO, commonValidatorVO);
                return merchantResponseVO;
            }
            else if (authResponse.equals("false"))
            {
                commonValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                directKitResponseVO.setStatus("failed");
                //  directKitResponseVO.setStatusMsg(ErrorMessages.SYS_OTP_SENT + otpStatus);
//                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(directKitValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                //   writeDirectTransactionResponse.setFailedAuthTokenResponse1(merchantResponseVO, directKitResponseVO, commonValidatorVO);
                writeDirectTransactionResponse.setFailedOTPGeneratedResponse(merchantResponseVO, directKitResponseVO, commonValidatorVO);
                return merchantResponseVO;
            }*/
   
            if(request1.getAttribute("authfail")!=null)
            {
                logger.error("inside if request1.getAttribute authtoken--" + request1.getAttribute("authfail"));

                String authResponse = request1.getAttribute("authfail").toString();
                if (authResponse == null || authResponse.equals(""))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
                else if (authResponse.equals("false"))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
            }
            synchronized (DirectPaymentRestIMPL.this)
            {

                commonValidatorVO = readDirectTransactionRequest.readRequestGenerateAppOTP(merchantRequestVO);

                if (commonValidatorVO == null)
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }
                else
                {

                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                    commonValidatorVO = restCommonInputValidator.performGenerateOTP(commonValidatorVO);
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {

                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                        return response;
                    }

                    directKitResponseVO = registrationManager.processGenerateOTP(commonValidatorVO);
                    writeDirectTransactionResponse.setSuccessOTPGeneratedResponse(response, directKitResponseVO, commonValidatorVO);
                    return response;
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while generate otp", e);
            transactionLogger.error("PZ Technical exception while generate otp", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while generate otp", e);
            transactionLogger.error("PZ PZConstraintViolationException while generate otp", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while generate otp", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while generate otp", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }

        return response;
    }

    @Override
    public Response processVerifyOTP(@InjectParam RestPaymentRequest merchantRequest)
    {
        transactionLogger.error("Inside processVerifyOTP ===== " + "MerchantTransactionId = " + merchantRequest.getMerchantTransactionId() + ", MobileNumber = " + merchantRequest.getCustomer().getMobile() + ", email = " + merchantRequest.getCustomer().getEmail() + ", smsOtp = " + merchantRequest.getCustomer().getSmsOtp() + ", emailOtp = " + merchantRequest.getCustomer().getEmailOTP());

        if(request1.getQueryString()!=null){
            Response response=new Response();
            Result result=new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }

        RestPaymentRequestVO merchantRequestVO = readDirectTransactionRequest.getMerchantRequestVO(merchantRequest);

        Response merchantServiceResponseVO = processVerifyOTPJSON(merchantRequestVO);

        return merchantServiceResponseVO;
    }

    @Override
    public Response processVerifyOTPJSON(RestPaymentRequestVO merchantRequestVO)
    {
        transactionLogger.error("Inside processVerifyOTPJSON ===== " + "MerchantTransactionId = " + merchantRequestVO.getMerchantTransactionId() + ", MobileNumber = " + merchantRequestVO.getCustomerVO().getMobile() + ", email = " + merchantRequestVO.getCustomerVO().getEmail() + ", smsOtp = " + merchantRequestVO.getCustomerVO().getSmsOtp() + ", emailOtp = " + merchantRequestVO.getCustomerVO().getEmailOtp());

        Response response = new Response();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        FailedTransactionLogEntry failedTransactionLogEntry = new FailedTransactionLogEntry();
        String error = "";

        try
        { if(request1.getAttribute("authfail")!=null)
        {
            logger.error("inside if request1.getAttribute authtoken--" + request1.getAttribute("authfail"));

            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                commonValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                return response;
            }
            else if (authResponse.equals("false"))
            {
                commonValidatorVO = new CommonValidatorVO();
                logger.debug("Inside failResponse---" + authResponse);
                error = "Authorization failed";
                failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                return response;
            }
        }

            commonValidatorVO = readDirectTransactionRequest.readVerifyOTP(merchantRequestVO);

            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return response;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performGenerateOTP(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return response;
            }

            directKitResponseVO = registrationManager.otpVerification(commonValidatorVO);
            writeDirectTransactionResponse.setSuccessOTVerificationResponse(response, directKitResponseVO, commonValidatorVO);
            return response;

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while merchant signup", e);
            transactionLogger.error("PZ Technical exception while merchant signup", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while merchant signup", e);
            transactionLogger.error("PZ PZConstraintViolationException while merchant signup", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        return response;
    }
    @Override
    public Response getPayoutBalance(String id, @InjectParam RestPaymentRequest paymentRequest)
    {
        return null;
    }

    @Override
    public Response getPayoutBalanceJSON(String Merchant_id, RestPaymentRequestVO paymentRequestVO)
    {
        transactionLogger.error("Inside getPayoutBalanceJSON ===== memberid--> "+Merchant_id);

        Response response                                       = new Response();
        CommonValidatorVO commonValidatorVO                     = null;
        ErrorCodeListVO errorCodeListVO                         = new ErrorCodeListVO();
        FailedTransactionLogEntry failedTransactionLogEntry     = new FailedTransactionLogEntry();
        DirectKitResponseVO directKitResponseVO                 = new DirectKitResponseVO();
        String error = "";

        try
        {

            if(request1.getAttribute("authfail")!=null)
            {
                logger.error("inside if request1.getAttribute authtoken--" + request1.getAttribute("authfail"));

                String authResponse = request1.getAttribute("authfail").toString();
                if (authResponse == null || authResponse.equals(""))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
                else if (authResponse.equals("false"))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
            }
            synchronized (DirectPaymentRestIMPL.this)
            {

                commonValidatorVO = readDirectTransactionRequest.readRequestGetpayoutBalance(paymentRequestVO, Merchant_id);

                if (commonValidatorVO == null)
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }
                else
                {

                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                  //  commonValidatorVO = restCommonInputValidator.performGenerateOTP(commonValidatorVO);
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {

                        writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                        return response;
                    }

                    directKitResponseVO = registrationManager.processGetPayoutBalance(commonValidatorVO);
                    writeDirectTransactionResponse.setSuccesspayoutBalanceResponse(response, directKitResponseVO, commonValidatorVO);
                    return response;
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while generate otp", e);
            transactionLogger.error("PZ Technical exception while generate otp", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }

        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while generate otp", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while generate otp", e);
            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }

        return response;

    }

    @Override
    public Response updateUpiTransactionDetails(@InjectParam RestPaymentRequest paymentRequest)
    {
        return null;
    }

    @Override
    public Response updateUpiTransactionDetailsJSON(RestPaymentRequestVO paymentRequestVO)
    {
        transactionLogger.error("Inside updateUpiTransactionDetailsJSON---> ");

        Functions functions                                     = new Functions();
        Response response                                       = new Response();
        CommonValidatorVO commonValidatorVO                     = null;
        ErrorCodeListVO errorCodeListVO                         = new ErrorCodeListVO();
        FailedTransactionLogEntry failedTransactionLogEntry     = new FailedTransactionLogEntry();
        DirectKitResponseVO directKitResponseVO                 = new DirectKitResponseVO();
        String error = "";
        TransactionDAO transactionDAO = new TransactionDAO();

        try
        {
            if(request1.getAttribute("authfail")!=null)
            {
                logger.error("inside if request1.getAttribute authtoken--" + request1.getAttribute("authfail"));

                String authResponse = request1.getAttribute("authfail").toString();
                if (authResponse == null || authResponse.equals(""))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
                else if (authResponse.equals("false"))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    failedTransactionLogEntry.genericBlockedInputTransactionRequestEntryForRejected(commonValidatorVO, error, ErrorName.SYS_AUTHTOKEN_FAILED.toString(), ErrorType.SYSCHECK.toString());
                    writeDirectTransactionResponse.setFailAuthTokenResponse(response);
                    return response;
                }
            }
            synchronized (DirectPaymentRestIMPL.this)
            {
                commonValidatorVO = readDirectTransactionRequest.readRequestUpdateUpiTxnDetails(paymentRequestVO);

                if (commonValidatorVO == null)
                {
                    errorCodeListVO.addListOfError(writeDirectTransactionResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                    writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return response;
                }
                else
                {
                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                    error = restCommonInputValidator.performUpiTxnDeatilsValidation(commonValidatorVO);

                    logger.error("validation error:"+error);
                    if (functions.isValueNull(error))
                    {
                        writeDirectTransactionResponse.setSuccessUpiTxnResponse(response, false, error);
                        return response;
                    }
                    else
                    {
                        boolean result = transactionDAO.updateUpiTransactionDetails(commonValidatorVO);
                        writeDirectTransactionResponse.setSuccessUpiTxnResponse(response,result,"");
                        return response;
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.error("PZ DBViolation exception while inserting upi transaction details", e);
            transactionLogger.error("PZ DBViolation exception while inserting upi transaction details", e);
//            writeDirectTransactionResponse.setRestTransactionResponseForError(response, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
            writeDirectTransactionResponse.setSuccessUpiTxnResponse(response, false,e.getMessage());
        }

        return response;
    }


}
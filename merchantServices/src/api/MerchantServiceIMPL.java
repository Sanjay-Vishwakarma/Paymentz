package api;

import com.auth.AuthFunctions;
import com.directi.pg.*;
import com.manager.dao.MerchantDAO;
import com.manager.enums.ActivityLogParameters;
import com.manager.helper.TransactionHelper;
import com.manager.vo.ActivityTrackerVOs;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.merchant.helper.RegistrationHelper;
import com.merchant.manager.RegistrationManager;
import com.merchant.utils.ReadMerchantServiceRequest;
import com.merchant.utils.WriteMerchantServiceResponse;
import com.merchant.vo.requestVOs.MerchantRequest;
import com.merchant.vo.requestVOs.MerchantRequestVO;
import com.merchant.vo.requestVOs.MerchantServiceRequest;
import com.merchant.vo.requestVOs.MerchantServiceRequestVO;
import com.merchant.vo.responseVOs.*;
import com.payment.emexpay.vo.request;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.response.PZResponseStatus;
import com.payment.validators.RestCommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.resource.Singleton;

import org.owasp.esapi.ESAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Sneha on 9/1/2016.
 */
@Singleton
@Path("v1")
public class MerchantServiceIMPL implements MerchantService
{
    private Logger logger = new Logger(MerchantServiceIMPL.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(MerchantServiceIMPL.class.getName());

    private ReadMerchantServiceRequest readMerchantServiceRequest = new ReadMerchantServiceRequest();
    private WriteMerchantServiceResponse writeMerchantServiceResponse = new WriteMerchantServiceResponse();
    private RestCommonInputValidator restCommonInputValidator = new RestCommonInputValidator();
    private RegistrationHelper registrationHelper = new RegistrationHelper();
    private RegistrationManager registrationManager = new RegistrationManager();

    @Context
    HttpServletResponse response;

    @Context
    HttpServletRequest request1;

    @Override
    public MerchantServiceResponseVO processGenerateOTP(@InjectParam MerchantServiceRequestVO merchantServiceRequestVO)
    {
        logger.debug("inside processGenerateOTP---");
        MerchantServiceResponseVO signupResponse = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }

        try
        {
            commonValidatorVO = readMerchantServiceRequest.readRequestForMerchantSignUp(merchantServiceRequestVO);

            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performRestMerchantSignUpValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }
            commonValidatorVO = registrationHelper.performRESTAPISystemCheckForMerchantSignup(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }

            //Generate OTP and Send SMS
            directKitResponseVO = registrationManager.processOtpGeneration(commonValidatorVO);
            writeMerchantServiceResponse.setSuccessOTPGenerateResponse(signupResponse, directKitResponseVO, commonValidatorVO);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while merchant signup", e);
            transactionLogger.error("PZ Technical exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while merchant signup", e);
            transactionLogger.error("PZ PZConstraintViolationException while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        return signupResponse;
    }

    public MerchantServiceResponseVO processGenerateOTPTwoStep(@InjectParam MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();

        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }
        merchantServiceRequestVO = readMerchantServiceRequest.getGenerateOTPRequest(merchantServiceRequest);

        merchantServiceResponseVO = processGenerateOTPTwoStepJSON(merchantServiceRequestVO);

        return merchantServiceResponseVO;
    }

    //new
    @Override
    public MerchantServiceResponseVO processGenerateOTPTwoStepJSON(MerchantServiceRequestVO merchantServiceRequestVO)
    {
        logger.debug("inside processGenerateOTPTwoStep---");
        MerchantServiceResponseVO signupResponse = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String error = "";

        try
        {
            /*if (request1.getAttribute("authfail") != null)
            {
                logger.debug("Inside if request1.getAttribute authtoken--" + request1.getAttribute("authfail"));

                String authResponse = request1.getAttribute("authfail").toString();
                if (authResponse == null || authResponse.equals(""))
                {
                    logger.debug("Inside failResponse---" + authResponse);
                    commonValidatorVO = new CommonValidatorVO();
                    error = "Authorization failed";
                    writeMerchantServiceResponse.setFailAuthTokenResponse(signupResponse);
                    return signupResponse;
                }
                else if (authResponse.equals("false"))
                {
                    logger.debug("Inside failResponse---" + authResponse);
                    commonValidatorVO = new CommonValidatorVO();
                    error = "Authorization failed";
                    directKitResponseVO.setStatus("failed");
                    writeMerchantServiceResponse.setFailAuthTokenResponse(signupResponse);
                    return signupResponse;
                }
            }*/

            synchronized (MerchantServiceIMPL.this)
            {
                commonValidatorVO = readMerchantServiceRequest.readRequestGenerateAppOTP(merchantServiceRequestVO);

                if (commonValidatorVO == null)
                {
                    errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                    writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return signupResponse;
                }
                else
                {

                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                    commonValidatorVO = restCommonInputValidator.performGenerateAppOTP(commonValidatorVO);
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                        return signupResponse;
                    }

                    commonValidatorVO = registrationHelper.performRESTAPISystemCheckForGenerateAppOTP(commonValidatorVO);
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {
                        writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                        return signupResponse;
                    }

                    directKitResponseVO = registrationManager.processOtpGeneration(commonValidatorVO);
                    writeMerchantServiceResponse.setSuccessOTPGeneratedResponse(signupResponse, directKitResponseVO, commonValidatorVO);
                    return signupResponse;
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while generate otp", e);
            transactionLogger.error("PZ Technical exception while generate otp", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while generate otp", e);
            transactionLogger.error("PZ PZConstraintViolationException while generate otp", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while generate otp", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while generate otp", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        return signupResponse;
    }

    @Override
    public MerchantServiceResponseVO ProcessVerifyGenerateAppOTP(@InjectParam MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();

        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }
        merchantServiceRequestVO = readMerchantServiceRequest.getMerchantVerifyOTPRequest(merchantServiceRequest);

        merchantServiceResponseVO = ProcessVerifyGenerateAppOTPJSON(merchantServiceRequestVO);

        return merchantServiceResponseVO;
    }

    @Override
    public MerchantServiceResponseVO ProcessVerifyGenerateAppOTPJSON(MerchantServiceRequestVO merchantServiceRequestVO)
    {
        logger.debug("inside processGenerateOTP---");
        MerchantServiceResponseVO signupResponse = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        try
        {
            commonValidatorVO = readMerchantServiceRequest.readVerifyGenerateAppOTP(merchantServiceRequestVO);

            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performVerifyAppOTP(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }

            commonValidatorVO = registrationHelper.performRESTAPISystemCheckForGenerateAppOTP(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }

            directKitResponseVO = registrationManager.processOtpVerification(commonValidatorVO);
            writeMerchantServiceResponse.setSuccessOTVerificationResponse(signupResponse, directKitResponseVO, commonValidatorVO);
            return signupResponse;


            //return signupResponse;
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while merchant signup", e);
            transactionLogger.error("PZ Technical exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while merchant signup", e);
            transactionLogger.error("PZ PZConstraintViolationException while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        return signupResponse;
    }


    public MerchantServiceResponseVO processMerchantSignUp(MerchantServiceRequest merchantServiceRequest)
    {
        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }
        MerchantServiceRequestVO merchantServiceRequestVO = readMerchantServiceRequest.getMerchantServiceRequestVO(merchantServiceRequest);

        MerchantServiceResponseVO merchantServiceResponseVO = processMerchantSignUpJSON(merchantServiceRequestVO);

        return merchantServiceResponseVO;
    }


    public MerchantServiceResponseVO processMerchantSignUpJSON(MerchantServiceRequestVO merchantServiceRequestVO)
    {
        MerchantServiceResponseVO signupResponse = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        try
        {
            commonValidatorVO = readMerchantServiceRequest.readRequestForMerchantSignUp(merchantServiceRequestVO);

            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }

            //  commonValidatorVO = restCommonInputValidator.performRestMerchantSignUpValidation(commonValidatorVO);
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performRestMerchantSignUpValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }

            request1.setAttribute("role", "merchant");
            ESAPI.httpUtilities().setCurrentHTTP(request1, null);
            directKitResponseVO = registrationManager.processMerchantSignUp(commonValidatorVO, request1);
            writeMerchantServiceResponse.setSuccessMerchantSignupResponse(signupResponse, directKitResponseVO, commonValidatorVO);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while merchant signup", e);
            transactionLogger.error("PZ Technical exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while merchant signup", e);
            transactionLogger.error("PZ PZConstraintViolationException while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        return signupResponse;
    }

    public MerchantServiceResponseVO generateAuthToken(@InjectParam MerchantServiceRequest loginRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();

        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }
        merchantServiceRequestVO = readMerchantServiceRequest.getMerchantServiceRequestVO(loginRequest);
        merchantServiceResponseVO = generateAuthTokenJSON(merchantServiceRequestVO);
        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO generateAuthTokenJSON(MerchantServiceRequestVO loginRequest)
    {
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String IpAddress = request1.getRemoteAddr();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Functions functions = new Functions();
        try
        {
            commonValidatorVO = readMerchantServiceRequest.readRequestForMerchantLogin(loginRequest);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performRestMerchantAuthTokenValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }
            request1.setAttribute("username", commonValidatorVO.getMerchantDetailsVO().getLogin());
            request1.setAttribute("password", commonValidatorVO.getMerchantDetailsVO().getPassword());
            request1.setAttribute("role", "merchant");
            directKitResponseVO = registrationManager.getAuthToken(commonValidatorVO, request1, response);
            writeMerchantServiceResponse.setSuccessAuthTokenResponse1(merchantServiceResponseVO, directKitResponseVO, commonValidatorVO);

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }

        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO processMerchantLogin(@InjectParam MerchantServiceRequest loginRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();
        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }
        merchantServiceRequestVO = readMerchantServiceRequest.getLoginFormURLRequest(loginRequest);

        merchantServiceResponseVO = processMerchantLoginJSON(merchantServiceRequestVO);

        return merchantServiceResponseVO;

    }


    public MerchantServiceResponseVO processMerchantLoginJSON(MerchantServiceRequestVO loginRequest)
    {
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Functions functions = new Functions();
        TransactionHelper transactionHelper = new TransactionHelper();
        String IpAddress = Functions.getIpAddress(request1);
        try
        {
            synchronized (MerchantServiceIMPL.this)
            {
                commonValidatorVO = readMerchantServiceRequest.readRequestForMerchantLogin(loginRequest);
                if (commonValidatorVO == null)
                {
                    errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                    writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return merchantServiceResponseVO;
                }
                commonValidatorVO.setRequestedIP(IpAddress);
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                commonValidatorVO = restCommonInputValidator.performRestMerchantLoginValidation(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
                {
                    writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return merchantServiceResponseVO;
                }
                commonValidatorVO = transactionHelper.merchantActivationChecks(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
                {
                    logger.debug("inside if performMerchantCurrenciesValidation---");
                    writeMerchantServiceResponse.setSignUpResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return merchantServiceResponseVO;
                }
                else
                {
                    request1.setAttribute("username", commonValidatorVO.getMerchantDetailsVO().getLogin());
                    request1.setAttribute("password", commonValidatorVO.getMerchantDetailsVO().getPassword());
                    request1.setAttribute("role", "merchant");
                    directKitResponseVO = registrationManager.processMerchantLogin(commonValidatorVO, request1, response);
                    writeMerchantServiceResponse.setSuccessMerchantLoginResponse(merchantServiceResponseVO, directKitResponseVO, commonValidatorVO);
                }

            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }


        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO processGetMerchantCurrencies(@InjectParam MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();
        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }
        merchantServiceRequestVO = readMerchantServiceRequest.getParamsForMerchantCurrencies(merchantServiceRequest);

        merchantServiceResponseVO = processGetMerchantCurrenciesJSON(merchantServiceRequestVO);

        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO processGetMerchantCurrenciesJSON(MerchantServiceRequestVO merchantServiceRequestVO)
    {
        logger.debug("Inside SignupIMPL getMerchantCurrencies---");
        MerchantServiceResponseVO signupResponse = new MerchantServiceResponseVO();
        Functions functions = new Functions();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        TransactionHelper transactionHelper = new TransactionHelper();

        String authResponse = request1.getAttribute("authfail").toString();
        if (authResponse == null || authResponse.equals(""))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeMerchantServiceResponse.setFailAuthTokenResponse(signupResponse);
            return signupResponse;
        }
        else if (authResponse.equals("false"))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeMerchantServiceResponse.setFailAuthTokenResponse(signupResponse);
            return signupResponse;
        }
        commonValidatorVO = readMerchantServiceRequest.readRequestToGetMerchantCurrencies(merchantServiceRequestVO);
        logger.debug("MemberID after Read REquest---" + commonValidatorVO.getMerchantDetailsVO().getMemberId());
        if (commonValidatorVO == null)
        {
            errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
            return signupResponse;
        }
        commonValidatorVO.setRequestedIP(Functions.getIpAddress(request1));
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        try
        {
            commonValidatorVO = restCommonInputValidator.performMerchantCurrenciesValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                logger.debug("inside if performMerchantCurrenciesValidation---");
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }
            else
            {
                commonValidatorVO = transactionHelper.merchantActivationChecks(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
                {
                    logger.debug("inside if performMerchantCurrenciesValidation---");
                    writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return signupResponse;
                }
                else
                {
                    logger.debug("inside else performMerchantCurrenciesValidation---");
                    directKitResponseVO = registrationManager.getAllMerchantCurrencies(commonValidatorVO);
                    writeMerchantServiceResponse.setMerchantCurrencyResponse(signupResponse, directKitResponseVO, commonValidatorVO);
                }
            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        return signupResponse;
    }

    public MerchantServiceResponseVO processChangePassword(@InjectParam MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();

        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }
        merchantServiceRequestVO = readMerchantServiceRequest.readMerchantChangePasswordRequest(merchantServiceRequest);

        merchantServiceResponseVO = processChangePasswordJSON(merchantServiceRequestVO);

        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO processChangePasswordJSON(MerchantServiceRequestVO merchantServiceRequestVO)
    {
        MerchantServiceResponseVO signupResponse = new MerchantServiceResponseVO();
        Functions functions = new Functions();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        commonValidatorVO = readMerchantServiceRequest.readRequestForChangePassword(merchantServiceRequestVO);

        String authResponse = request1.getAttribute("authfail").toString();
        if (authResponse == null || authResponse.equals(""))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeMerchantServiceResponse.setFailAuthTokenResponse(signupResponse);
            return signupResponse;
        }
        else if (authResponse.equals("false"))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeMerchantServiceResponse.setFailAuthTokenResponse(signupResponse);
            return signupResponse;
        }

        if (commonValidatorVO == null)
        {
            errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
            return signupResponse;
        }
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        try
        {
            commonValidatorVO = restCommonInputValidator.performMerchantChangePasswordValidation(commonValidatorVO);

            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }
            else
            {
                request1.setAttribute("role", commonValidatorVO.getMerchantDetailsVO().getRole());
                directKitResponseVO = registrationManager.processMerchantChangePassword(commonValidatorVO, request1, response);
                writeMerchantServiceResponse.setSuccessMerchantChangePasswordResponse(signupResponse, directKitResponseVO, commonValidatorVO);
                /*commonValidatorVO = registrationHelper.performChecksumVerificationForMerchantChangePassword(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return signupResponse;
                }
                else
                {
                    request1.setAttribute("role","merchant");
                    directKitResponseVO = registrationManager.processMerchantChangePassword(commonValidatorVO,request1,response);
                    writeMerchantServiceResponse.setSuccessMerchantChangePasswordResponse(signupResponse, directKitResponseVO, commonValidatorVO);
                }*/
            }

        }
        /*catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }*/
        catch (PZDBViolationException e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (SystemError e)
        {
            logger.error("PZ SystemError exception while merchant signup", e);
            transactionLogger.error("PZ SystemError exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        return signupResponse;
    }

    public MerchantServiceResponseVO processForgetPassword(@InjectParam MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();

        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }
        merchantServiceRequestVO = readMerchantServiceRequest.getForgotPasswordParamsAPI(merchantServiceRequest);

        merchantServiceResponseVO = processForgetPasswordJSON(merchantServiceRequestVO);

        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO processForgetPasswordJSON(MerchantServiceRequestVO merchantServiceRequestVO)
    {
        MerchantServiceResponseVO signupResponse = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Functions functions = new Functions();

        commonValidatorVO = readMerchantServiceRequest.readRequestForForgetPassword(merchantServiceRequestVO);
        if (commonValidatorVO == null)
        {
            errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
            return signupResponse;
        }
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

        try
        {

            commonValidatorVO = restCommonInputValidator.performMerchantForgetPasswordValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }
            else
            {
                request1.setAttribute("role", "merchant");
                directKitResponseVO = registrationManager.processMerchantForgetPassword(commonValidatorVO, request1, response);
                //System.out.println("status in impl---"+directKitResponseVO.getStatus());
                writeMerchantServiceResponse.setSuccessMerchantForgetPasswordResponse(signupResponse, directKitResponseVO, commonValidatorVO);
            }

        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant forgot password", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant forgot password", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (Exception e)
        {
            logger.error("PZ SystemError exception while merchant signup", e);
            transactionLogger.error("PZ SystemError exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        return signupResponse;
    }

    public MerchantServiceResponseVO processCustomerSignUp(MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceRequestVO merchantServiceRequestVO = new MerchantServiceRequestVO();
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();

        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }
        merchantServiceRequestVO = readMerchantServiceRequest.readCustomerSignupRequest(merchantServiceRequest);

        merchantServiceResponseVO = processCustomerSignUpJSON(merchantServiceRequestVO);

        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO processCustomerSignUpJSON(MerchantServiceRequestVO merchantServiceRequestVO)
    {
        response.addHeader("Access-Control-Allow-Origin", "*");
        logger.debug("INSIDE card holder registration::");
        MerchantServiceResponseVO signupResponse = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        try
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                logger.debug("Inside failResponse---" + authResponse);
                writeMerchantServiceResponse.setFailAuthTokenResponse(signupResponse);
                return signupResponse;
            }
            else if (authResponse.equals("false"))
            {
                logger.debug("Inside failResponse---" + authResponse);
                writeMerchantServiceResponse.setFailAuthTokenResponse(signupResponse);
                return signupResponse;
            }
            commonValidatorVO = readMerchantServiceRequest.readRequestForCustomerSignUp(merchantServiceRequestVO);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performRestCustomerRegistrationValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }

            commonValidatorVO = registrationHelper.performRESTAPISystemCheckForCardholderRegistration(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return signupResponse;
            }

            directKitResponseVO = registrationManager.processCCustomerRegistration(commonValidatorVO);
            writeMerchantServiceResponse.setSuccessRestCardholderRegistrationResponse(signupResponse, directKitResponseVO, commonValidatorVO);
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while customer sign up", e);
            transactionLogger.error("PZ Technical exception while customer sign up", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while customer sign up", e);
            transactionLogger.error("PZ PZConstraintViolationException while customer sign up", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while customer sign up", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while customer sign up", e);
            writeMerchantServiceResponse.setSignUpResponseForError(signupResponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        return signupResponse;
    }

    @Override
    public MerchantServiceResponseVO getaddress(MerchantServiceRequestVO addressrequest)
    {


        MerchantServiceResponseVO addressresponsevo = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String IpAddress = request1.getRemoteAddr();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Functions functions = new Functions();
        TransactionHelper transactionHelper = new TransactionHelper();
        String authResponse = request1.getAttribute("authfail").toString();
        if (authResponse == null || authResponse.equals(""))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeMerchantServiceResponse.setFailAuthTokenResponse(addressresponsevo);
            return addressresponsevo;
        }
        else if (authResponse.equals("false"))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeMerchantServiceResponse.setFailAuthTokenResponse(addressresponsevo);
            return addressresponsevo;
        }

        commonValidatorVO = readMerchantServiceRequest.readRequestToGetMerchantCurrencies(addressrequest);
        if (commonValidatorVO == null)
        {
            errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            writeMerchantServiceResponse.setLoginResponseForError(addressresponsevo, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
            return addressresponsevo;
        }
        commonValidatorVO.setRequestedIP(Functions.getIpAddress(request1));
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        try
        {
            commonValidatorVO = restCommonInputValidator.performMerchantCurrenciesValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                logger.debug("inside if performMerchantCurrenciesValidation---");
                writeMerchantServiceResponse.setSignUpResponseForError(addressresponsevo, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return addressresponsevo;
            }
            else
            {
                commonValidatorVO = transactionHelper.merchantActivationChecks(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
                {
                    logger.debug("inside if get Address Validation---");
                    writeMerchantServiceResponse.setSignUpResponseForError(addressresponsevo, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return addressresponsevo;
                }
                else
                {
                    logger.debug("inside else get address Validation---");
                    directKitResponseVO = registrationManager.Getaddressdetails(commonValidatorVO);
                    writeMerchantServiceResponse.setSuccessgetaddressResponse(addressresponsevo, directKitResponseVO, commonValidatorVO);
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while merchant get Address", e);
            transactionLogger.error("PZ Technical exception while merchant get Address", e);
            writeMerchantServiceResponse.setSignUpResponseForError(addressresponsevo, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while merchant get Address", e);
            transactionLogger.error("PZ PZConstraintViolationException while get Address", e);
            writeMerchantServiceResponse.setSignUpResponseForError(addressresponsevo, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ NoSuchAlgorithmException while merchant get Address", e);
            transactionLogger.error("PZ NoSuchAlgorithmException while get Address", e);
            writeMerchantServiceResponse.setSignUpResponseForError(addressresponsevo, commonValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        return addressresponsevo;
    }

    @Override
    public MerchantServiceResponseVO updateAddress(MerchantServiceRequestVO updateaddressrequest)
    {
        MerchantServiceResponseVO updateaddressresponse = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String IpAddress = request1.getRemoteAddr();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Functions functions = new Functions();
        TransactionHelper transactionHelper = new TransactionHelper();

        String authResponse = request1.getAttribute("authfail").toString();
        if (authResponse == null || authResponse.equals(""))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeMerchantServiceResponse.setFailAuthTokenResponse(updateaddressresponse);
            return updateaddressresponse;
        }
        else if (authResponse.equals("false"))
        {
            logger.debug("Inside failResponse---" + authResponse);
            writeMerchantServiceResponse.setFailAuthTokenResponse(updateaddressresponse);
            return updateaddressresponse;
        }
        commonValidatorVO = readMerchantServiceRequest.readRequestUpdateMerchantAddress(updateaddressrequest);
        if (commonValidatorVO == null)
        {
            errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            writeMerchantServiceResponse.setLoginResponseForError(updateaddressresponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
            return updateaddressresponse;
        }

        commonValidatorVO.setRequestedIP(Functions.getIpAddress(request1));
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        try
        {
            //System.out.println("inside try 1");
            commonValidatorVO = restCommonInputValidator.performMerchantAddressValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
            {
                //System.out.println("inside error----");
                logger.debug("inside if performMerchantCurrenciesValidation---");
                writeMerchantServiceResponse.setSignUpResponseForError(updateaddressresponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return updateaddressresponse;
            }
            else
            {
                commonValidatorVO = transactionHelper.merchantActivationChecks(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
                {
                    logger.debug("inside if get Address Validation---");
                    writeMerchantServiceResponse.setSignUpResponseForError(updateaddressresponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return updateaddressresponse;
                }
                else
                {
                    logger.debug("inside else get address Validation---");
                    //System.out.println("inside else get address Validation---");
                    String successStatus = registrationManager.updateaddressDetails(commonValidatorVO);
                    commonValidatorVO.setStatus(successStatus);
                    writeMerchantServiceResponse.setSuccessUpdateAddressDetailsResponse(updateaddressresponse, directKitResponseVO, commonValidatorVO);
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while merchant update Address", e);
            transactionLogger.error("PZ Technical exception while merchant update Address", e);
            writeMerchantServiceResponse.setSignUpResponseForError(updateaddressresponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while merchant update Address", e);
            transactionLogger.error("PZ PZConstraintViolationException while update Address", e);
            writeMerchantServiceResponse.setSignUpResponseForError(updateaddressresponse, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("PZ NoSuchAlgorithmException while merchant update Address", e);
            transactionLogger.error("PZ NoSuchAlgorithmException while update Address", e);
            writeMerchantServiceResponse.setSignUpResponseForError(updateaddressresponse, commonValidatorVO, null, PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }

        return updateaddressresponse;
    }

    @Override
    public MerchantServiceResponseVO failResponse(@InjectParam MerchantServiceRequestVO merchantServiceRequestVO)
    {
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();

        logger.debug("Inside failResponse---");
        writeMerchantServiceResponse.setFailAuthTokenResponse(merchantServiceResponseVO);
        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO getAuthToken(@InjectParam MerchantServiceRequest authRequest)
    {
        MerchantServiceResponseVO response1 = new MerchantServiceResponseVO();
        MerchantServiceRequestVO authRequestVO = new MerchantServiceRequestVO();

        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }
        authRequestVO = readMerchantServiceRequest.getNewAuthTokenRequest(authRequest);

        response1 = getAuthTokenJSON(authRequestVO);

        return response1;
    }

    public MerchantServiceResponseVO getAuthTokenJSON(MerchantServiceRequestVO loginRequest)
    {
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String IpAddress = request1.getRemoteAddr();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Functions functions = new Functions();
        AuthFunctions authFunctions = new AuthFunctions();
        try
        {
            commonValidatorVO = readMerchantServiceRequest.readRequestForgetNewAuthToken(loginRequest);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }
            else
            {

                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                commonValidatorVO = restCommonInputValidator.performGetNewAuthTokenValidation(commonValidatorVO);
                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty() || functions.isValueNull(commonValidatorVO.getErrorMsg()))
                {
                    writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return merchantServiceResponseVO;
                }

                request1.setAttribute("username", commonValidatorVO.getMerchantDetailsVO().getLogin());
                //request1.setAttribute("password", commonValidatorVO.getMerchantDetailsVO().getPassword());
                request1.setAttribute("role", commonValidatorVO.getMerchantDetailsVO().getRole());
                directKitResponseVO = registrationManager.regenerateAuthToken(commonValidatorVO, request1, response);
                writeMerchantServiceResponse.setSuccessAuthTokenResponse(merchantServiceResponseVO, directKitResponseVO, commonValidatorVO);

            }
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Generating Token", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZDBViolationException exception while Generating Token", e);
            transactionLogger.error("PZ PZDBViolationException exception while Generating Token", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }

        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO processemailVerification(MerchantServiceRequestVO verifyRequest)
    {

        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        try
        {
            commonValidatorVO = readMerchantServiceRequest.readRequestForverifymail(verifyRequest);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performverifymail(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setSignUpResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }

            request1.setAttribute("username", commonValidatorVO.getMerchantDetailsVO().getLogin());
            request1.setAttribute("partnerId", commonValidatorVO.getMerchantDetailsVO().getPartnerId());
            request1.setAttribute("partnerName", commonValidatorVO.getMerchantDetailsVO().getPartnerName());
            directKitResponseVO = registrationManager.processEmailVerify(commonValidatorVO, request1, response);
            writeMerchantServiceResponse.setSuccessVerifyMailResponse(merchantServiceResponseVO, directKitResponseVO, commonValidatorVO);


        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception", e);
            transactionLogger.error("PZ PZConstraintViolationException exception", e);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception", e);
            transactionLogger.error("PZ PZConstraintViolationException exception", e);
        }


        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO processMerchantLogout(@InjectParam MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();
        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }
        MerchantServiceRequestVO merchantServiceRequestVO = readMerchantServiceRequest.readMerchantLogoutRequest(merchantServiceRequest);
        merchantServiceResponseVO = processMerchantLogoutJSON(merchantServiceRequestVO);
        return merchantServiceResponseVO;
    }

    public MerchantServiceResponseVO processMerchantLogoutJSON(MerchantServiceRequestVO merchantServiceRequestVO)
    {
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        commonValidatorVO = readMerchantServiceRequest.readMerchantLogoutRequestJSON(merchantServiceRequestVO);
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        try
        {
            commonValidatorVO = restCommonInputValidator.performMerchantLogoutValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setSignUpResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }
            directKitResponseVO = registrationManager.removeAuthToken(commonValidatorVO);
            writeMerchantServiceResponse.setSuccessMerchantLogoutResponse(merchantServiceResponseVO, directKitResponseVO, commonValidatorVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception while Merchant Logout", e);
            transactionLogger.error("PZ PZConstraintViolationException exception while Merchant Logout", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZDBViolationException exception while Merchant Logout", e);
            transactionLogger.error("PZ PZDBViolationException exception while Merchant Logout", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        return merchantServiceResponseVO;
    }

    @Override
    public MerchantServiceResponseVO processSendReceiptEmail(@InjectParam MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();

        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;

        }

        MerchantServiceRequestVO merchantServiceRequestVO = readMerchantServiceRequest.readRequestForSendReceiptEmail(merchantServiceRequest);

        merchantServiceResponseVO = processSendReceiptEmailJSON(merchantServiceRequestVO);

        return merchantServiceResponseVO;
    }

    @Override
    public MerchantServiceResponseVO processSendReceiptEmailJSON(MerchantServiceRequestVO merchantServiceRequestVO)
    {

        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        // String paymentId =merchantServiceRequestVO.getPaymentId();
        try
        {
            commonValidatorVO = readMerchantServiceRequest.readRequestForSendReceiptEmail(merchantServiceRequestVO);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performRestMerchantSendReceiptEmailValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }

            directKitResponseVO = registrationManager.processSendReceiptEmail(commonValidatorVO, request1, response);
            writeMerchantServiceResponse.setSuccessSendReceiptEmailResponse(merchantServiceResponseVO, directKitResponseVO, commonValidatorVO);


        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception", e);
            transactionLogger.error("PZ PZConstraintViolationException exception", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception", e);
            transactionLogger.error("PZ PZConstraintViolationException exception", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("NoSuchAlgorithmException exception", e);
            transactionLogger.error("NoSuchAlgorithmException exception", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }


        return merchantServiceResponseVO;
    }

    @Override
    public MerchantServiceResponseVO processSendReceiptSms(@InjectParam MerchantServiceRequest merchantServiceRequest)
    {
        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();

        if (request1.getQueryString() != null)
        {
            MerchantServiceResponseVO response = new MerchantServiceResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;

        }

        MerchantServiceRequestVO merchantServiceRequestVO = readMerchantServiceRequest.readRequestForSendReceiptSms(merchantServiceRequest);

        merchantServiceResponseVO = processSendReceiptSmsJSON(merchantServiceRequestVO);

        return merchantServiceResponseVO;

    }

    @Override
    public MerchantServiceResponseVO processSendReceiptSmsJSON(MerchantServiceRequestVO merchantServiceRequestVO)
    {

        MerchantServiceResponseVO merchantServiceResponseVO = new MerchantServiceResponseVO();
        CommonValidatorVO commonValidatorVO = new CommonValidatorVO();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        try
        {
            commonValidatorVO = readMerchantServiceRequest.readRequestForSendReceiptSms(merchantServiceRequestVO);
            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performRestMerchantSendReceiptSmsValidation(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantServiceResponseVO;
            }

            directKitResponseVO = registrationManager.processSendReceiptSms(commonValidatorVO, request1, response);
            writeMerchantServiceResponse.setSuccessSendReceiptSmsResponse(merchantServiceResponseVO, directKitResponseVO, commonValidatorVO);


        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception", e);
            transactionLogger.error("PZ PZConstraintViolationException exception", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException exception", e);
            transactionLogger.error("PZ PZConstraintViolationException exception", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());

        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error("NoSuchAlgorithmException exception", e);
            transactionLogger.error("NoSuchAlgorithmException exception", e);
            writeMerchantServiceResponse.setLoginResponseForError(merchantServiceResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }


        return merchantServiceResponseVO;

    }

    @Override
    public MerchantResponseVO createOTP(@InjectParam MerchantRequest merchantRequest)
    {
        transactionLogger.error("createOTP ===== " + merchantRequest);

        if (request1.getQueryString() != null)
        {
            MerchantResponseVO response = new MerchantResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }

        MerchantRequestVO merchantRequestVO = readMerchantServiceRequest.getMerchantRequestVO(merchantRequest);

        MerchantResponseVO merchantServiceResponseVO = createOTPJSON(merchantRequestVO);

        return merchantServiceResponseVO;
    }

    @Override
    public MerchantResponseVO createOTPJSON(MerchantRequestVO merchantRequestVO)
    {
        transactionLogger.error("createOTPJSON ===== " + merchantRequestVO);

        MerchantResponseVO merchantResponseVO = new MerchantResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        WriteMerchantServiceResponse writeDirectTransactionResponse = new WriteMerchantServiceResponse();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String error = "";

        try
        {
            logger.error("before if request1.getAttribute authtoken--" + request1.getAttribute("authfail"));

            if (request1.getAttribute("authfail") != null)
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
                }
            }
            synchronized (MerchantServiceIMPL.this)
            {
                commonValidatorVO = readMerchantServiceRequest.readRequestGenerateAppOTP(merchantRequestVO);

                if (commonValidatorVO == null)
                {
                    errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                    writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return merchantResponseVO;
                }
                else
                {

                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                    commonValidatorVO = restCommonInputValidator.performGenerateOTP(commonValidatorVO);
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {

                        writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                        return merchantResponseVO;
                    }

                    directKitResponseVO = registrationManager.processGenerateOTP(commonValidatorVO);
                    writeMerchantServiceResponse.setSuccessOTPGeneratedResponse(merchantResponseVO, directKitResponseVO, commonValidatorVO);
                    return merchantResponseVO;
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while generate otp", e);
            transactionLogger.error("PZ Technical exception while generate otp", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while generate otp", e);
            transactionLogger.error("PZ PZConstraintViolationException while generate otp", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while generate otp", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while generate otp", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }

        return merchantResponseVO;
    }

    @Override
    public MerchantResponseVO processVerifyOTP(@InjectParam MerchantRequest merchantRequest)
    {
        transactionLogger.error("verifyOTP ===== " + merchantRequest);

        if (request1.getQueryString() != null)
        {
            MerchantResponseVO response = new MerchantResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }

        MerchantRequestVO merchantRequestVO = readMerchantServiceRequest.getMerchantRequestVO(merchantRequest);

        MerchantResponseVO merchantServiceResponseVO = processVerifyOTPJSON(merchantRequestVO);

        return merchantServiceResponseVO;
    }

    @Override
    public MerchantResponseVO processVerifyOTPJSON(MerchantRequestVO merchantRequestVO)
    {
        logger.debug("inside processVerifyOTPJSON --- ");
        MerchantResponseVO merchantResponseVO = new MerchantResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();

        try
        {
            commonValidatorVO = readMerchantServiceRequest.readVerifyOTP(merchantRequestVO);

            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantResponseVO;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performGenerateOTP(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantResponseVO;
            }

            directKitResponseVO = registrationManager.otpVerification(commonValidatorVO);
            writeMerchantServiceResponse.setSuccessOTVerificationResponse(merchantResponseVO, directKitResponseVO, commonValidatorVO);
            return merchantResponseVO;

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while merchant signup", e);
            transactionLogger.error("PZ Technical exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while merchant signup", e);
            transactionLogger.error("PZ PZConstraintViolationException while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        return merchantResponseVO;
    }

    @Override
    public MerchantResponseFlagsVO getMerchantFlag(@InjectParam MerchantRequest merchantRequest)
    {
        transactionLogger.error("Inside getMerchantFlag ===== " + "memberid = " + merchantRequest.getAuthentication().getMemberId());

        if (request1.getQueryString() != null)
        {
            MerchantResponseFlagsVO response = new MerchantResponseFlagsVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }

        MerchantRequestVO merchantRequestVO = readMerchantServiceRequest.getMerchantRequestVO(merchantRequest);
        MerchantResponseFlagsVO merchantResponseFlagsVO = getMerchantFlagJSON(merchantRequestVO);

        return merchantResponseFlagsVO;
    }

    @Override
    public MerchantResponseFlagsVO getMerchantFlagJSON(MerchantRequestVO merchantRequestVO)
    {

        String memberid = merchantRequestVO.getAuthentication().getMemberId();
        Functions functions = new Functions();
        MerchantResponseVO merchantResponseVO = new MerchantResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        WriteMerchantServiceResponse writeDirectTransactionResponse = new WriteMerchantServiceResponse();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        MerchantResponseFlagsVO merchantResponseFlagsVO = new MerchantResponseFlagsVO();
        String error = "";

        transactionLogger.error("Inside getMerchantFlagJSON ===== " + "memberid = " + memberid);
        if (request1.getAttribute("authfail") != null)
        {
            logger.error("Inside if request1.getAttribute authtoken--" + request1.getAttribute("authfail"));

            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                commonValidatorVO = new CommonValidatorVO();
                error = "Authorization failed";
                writeDirectTransactionResponse.setFailedMerchantFlagResponse(merchantResponseFlagsVO, directKitResponseVO, commonValidatorVO);
                return merchantResponseFlagsVO;
            }
            else if (authResponse.equals("false"))
            {
                commonValidatorVO = new CommonValidatorVO();
                error = "Authorization failed";
                directKitResponseVO.setStatus("failed");
                writeDirectTransactionResponse.setFailedMerchantFlagResponse(merchantResponseFlagsVO, directKitResponseVO, commonValidatorVO);
                return merchantResponseFlagsVO;
            }
        }
        if (!functions.isValueNull(memberid))
        {
            MerchantResponseFlagsVO response = new MerchantResponseFlagsVO();
            Result result = new Result();

            result.setDescription("Memberid cannot be empty");
            response.setResult(result);

            return response;
        }
        merchantResponseFlagsVO = readMerchantServiceRequest.getMerchantConfigFlags(memberid);
        return merchantResponseFlagsVO;
    }

    @Override
    public MerchantResponseVO getMerchantTheme(@InjectParam MerchantRequest merchantRequest)
    {
        transactionLogger.error("getMerchantTheme ===== " + merchantRequest);

        if (request1.getQueryString() != null)
        {
            MerchantResponseVO response = new MerchantResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }

        MerchantRequestVO merchantRequestVO = readMerchantServiceRequest.getMerchantRequestVO(merchantRequest);

        MerchantResponseVO merchantServiceResponseVO = getMerchantThemeJSON(merchantRequestVO);

        return merchantServiceResponseVO;
    }

    @Override
    public MerchantResponseVO getMerchantThemeJSON(MerchantRequestVO merchantRequestVO)
    {

        MerchantResponseVO merchantResponseVO = new MerchantResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        WriteMerchantServiceResponse writeDirectTransactionResponse = new WriteMerchantServiceResponse();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String error = "";

        try
        {

            if (request1.getAttribute("authfail") != null)
            {
                logger.error("Inside if request1.getAttribute authtoken--" + request1.getAttribute("authfail"));

                String authResponse = request1.getAttribute("authfail").toString();
                if (authResponse == null || authResponse.equals(""))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    writeDirectTransactionResponse.setFailedOTPGeneratedResponse(merchantResponseVO, directKitResponseVO, commonValidatorVO);
                    return merchantResponseVO;
                }
                else if (authResponse.equals("false"))
                {
                    commonValidatorVO = new CommonValidatorVO();
                    logger.debug("Inside failResponse---" + authResponse);
                    error = "Authorization failed";
                    directKitResponseVO.setStatus("failed");
                    writeDirectTransactionResponse.setFailedOTPGeneratedResponse(merchantResponseVO, directKitResponseVO, commonValidatorVO);
                    return merchantResponseVO;
                }
            }
            commonValidatorVO = readMerchantServiceRequest.readRequestForMerchantTheme(merchantRequestVO);

            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantResponseVO;
            }
            else
            {
                commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
                commonValidatorVO = restCommonInputValidator.performValidation(commonValidatorVO);

                if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                {
                    writeMerchantServiceResponse.setMerchantThemeResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return merchantResponseVO;
                }

                directKitResponseVO = registrationManager.getMerchantThemeByMerchantId(commonValidatorVO);
                writeMerchantServiceResponse.setSuccessMerchantThemeResponse(merchantResponseVO, directKitResponseVO, commonValidatorVO);
                return merchantResponseVO;
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while get Merchant Theme", e);
            transactionLogger.error("PZ Technical exception while get Merchant Theme", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while get Merchant Theme", e);
            transactionLogger.error("PZ PZConstraintViolationException while get Merchant Theme", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while get Merchant Theme", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while get Merchant Theme", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }

        return merchantResponseVO;
    }


    @Override
    public MerchantResponseVO getMemberAllTerminalFlags(@InjectParam MerchantRequest merchantRequest)
    {
        transactionLogger.error("getMemberAllTerminalFlags ===== " + merchantRequest);

        if (request1.getQueryString() != null)
        {
            MerchantResponseVO response = new MerchantResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }

        MerchantRequestVO merchantRequestVO = readMerchantServiceRequest.getMerchantRequestVO(merchantRequest);

        MerchantResponseVO merchantServiceResponseVO = getMemberAllTerminalFlagsJSON(merchantRequestVO);

        return merchantServiceResponseVO;
    }

    @Override
    public MerchantResponseVO getMemberAllTerminalFlagsJSON(MerchantRequestVO merchantRequestVO)
    {

        String memberid = merchantRequestVO.getAuthentication().getMemberId();
        Functions functions = new Functions();
        MerchantResponseVO merchantResponseVO = new MerchantResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        WriteMerchantServiceResponse writeDirectTransactionResponse = new WriteMerchantServiceResponse();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        MerchantResponseFlagsVO merchantResponseFlagsVO = new MerchantResponseFlagsVO();
        String error = "";

        transactionLogger.error("Inside getMemberAllTerminalFlagsJSON::" + "memberid = " + memberid);
        if (request1.getAttribute("authfail") != null)
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
            }

        }
        commonValidatorVO = readMerchantServiceRequest.readRequestTerminaldetails(merchantRequestVO);

        if (commonValidatorVO == null)
        {
            errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
            return merchantResponseVO;
        }
        else
        {
            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            try
            {
                commonValidatorVO = restCommonInputValidator.performValidation(commonValidatorVO);


            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setMerchantThemeResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantResponseVO;
            }


            writeMerchantServiceResponse.setSuccessMemberAllTerminalFlagsResponse(merchantResponseVO, directKitResponseVO, commonValidatorVO);
            return merchantResponseVO;
            }
            catch (PZDBViolationException e)
            {
                logger.error("PZ DBViolation exception while get Merchant Theme", e);
                transactionLogger.error("PZ Technical exception while get Merchant Theme", e);
                writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
            }
            catch (PZConstraintViolationException e)
            {
                logger.error("PZ PZConstraintViolationException while get Merchant Theme", e);
                transactionLogger.error("PZ PZConstraintViolationException while get Merchant Theme", e);
                writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
            }
            catch (NoSuchAlgorithmException e)
            {
                logger.error("PZ GenericConstraintViolation exception while get Merchant Theme", e);
                transactionLogger.error("PZ GenericConstraintViolation exception while get Merchant Theme", e);
                writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
            }
        }

        return merchantResponseVO;
    }


    @Override
    public MerchantResponseVO createLoginMerchantOTP(@InjectParam MerchantRequest merchantRequest)
    {
        transactionLogger.error("createOTP ===== " + merchantRequest);

        if (request1.getQueryString() != null)
        {
            MerchantResponseVO response = new MerchantResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }

        MerchantRequestVO merchantRequestVO = readMerchantServiceRequest.getMerchantRequestVO(merchantRequest);

        MerchantResponseVO merchantServiceResponseVO = createLoginMerchantOTPJSON(merchantRequestVO);

        return merchantServiceResponseVO;
    }

    @Override
    public MerchantResponseVO createLoginMerchantOTPJSON(MerchantRequestVO merchantRequestVO)
    {
        transactionLogger.error("createOTPJSON ===== " + merchantRequestVO);

        MerchantResponseVO merchantResponseVO = new MerchantResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        WriteMerchantServiceResponse writeDirectTransactionResponse = new WriteMerchantServiceResponse();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String error = "";

        try
        {
            logger.error("before if request1.getAttribute authtoken--" + request1.getAttribute("authfail"));

            if (request1.getAttribute("authfail") != null)
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
                }
            }
            synchronized (MerchantServiceIMPL.this)
            {
                commonValidatorVO = readMerchantServiceRequest.readRequestGenerateAppOTP(merchantRequestVO);

                if (commonValidatorVO == null)
                {
                    errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                    writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                    return merchantResponseVO;
                }
                else
                {

                    commonValidatorVO.setErrorCodeListVO(errorCodeListVO);

                    commonValidatorVO = restCommonInputValidator.performGenerateOTP(commonValidatorVO);
                    if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
                    {

                        writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                        return merchantResponseVO;
                    }

                    directKitResponseVO = registrationManager.processCreateLoginMerchantOTP(commonValidatorVO);
                    writeMerchantServiceResponse.setSuccessOTPGeneratedResponse(merchantResponseVO, directKitResponseVO, commonValidatorVO);
                    return merchantResponseVO;
                }
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while generate otp", e);
            transactionLogger.error("PZ Technical exception while generate otp", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while generate otp", e);
            transactionLogger.error("PZ PZConstraintViolationException while generate otp", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while generate otp", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while generate otp", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }

        return merchantResponseVO;
    }

    @Override
    public MerchantResponseVO processVerifyLoginMerchantOTP(@InjectParam MerchantRequest merchantRequest)
    {
        transactionLogger.error("verifyOTP ===== " + merchantRequest);

        if (request1.getQueryString() != null)
        {
            MerchantResponseVO response = new MerchantResponseVO();
            Result result = new Result();

            result.setDescription("Query String request is not supported ,Kindly pass the request in the body");
            response.setResult(result);

            return response;
        }

        MerchantRequestVO merchantRequestVO = readMerchantServiceRequest.getMerchantRequestVO(merchantRequest);

        MerchantResponseVO merchantServiceResponseVO = processVerifyLoginMerchantOTPJSON(merchantRequestVO);

        return merchantServiceResponseVO;
    }

    @Override
    public MerchantResponseVO processVerifyLoginMerchantOTPJSON(MerchantRequestVO merchantRequestVO)
    {
        logger.debug("inside processVerifyOTPJSON --- ");
        MerchantResponseVO merchantResponseVO = new MerchantResponseVO();
        CommonValidatorVO commonValidatorVO = null;
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        Functions functions=new Functions();
        ActivityTrackerVOs activityTrackerVOs = new ActivityTrackerVOs();
        Merchants merchants=new Merchants();
        MerchantDAO merchantDAO = new MerchantDAO();
        String membrId = "";
        MerchantDetailsVO merchantDetailsVO=null;
        String userId = "";
        String partnerid = "";
        String username=null;
        String merchantId="";


        try
        {
            commonValidatorVO = readMerchantServiceRequest.readVerifyOTP(merchantRequestVO);
            logger.debug("inside processVerifyOTPJSON commonValidatorVO --- "+commonValidatorVO.getMerchantDetailsVO().getTransactionID());
            logger.debug("inside processVerifyOTPJSON commonValidatorVO --- "+commonValidatorVO.getMerchantDetailsVO().getSmsOtp());
            logger.debug("inside processVerifyOTPJSON commonValidatorVO --- "+commonValidatorVO.getAddressDetailsVO().getPhone());
            logger.debug("inside processVerifyOTPJSON commonValidatorVO --- "+commonValidatorVO.getMerchantDetailsVO().getMemberId());

            if (commonValidatorVO == null)
            {
                errorCodeListVO.addListOfError(writeMerchantServiceResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
                writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantResponseVO;
            }

            commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
            commonValidatorVO = restCommonInputValidator.performGenerateOTP(commonValidatorVO);
            if (!commonValidatorVO.getErrorCodeListVO().getListOfError().isEmpty())
            {
                writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                return merchantResponseVO;
            }

            directKitResponseVO = registrationManager.loginMerchantOTPVerification(commonValidatorVO);

            membrId                             =commonValidatorVO.getMerchantDetailsVO().getTransactionID();

            String[] muValue=membrId.split("_");

            if(muValue[1].equals("M")){
                 merchantId=muValue[0];
                request1.setAttribute("role","Merchant");

                merchantDetailsVO = merchantDAO.getMemberDetails(merchantId);
                if (functions.isValueNull(merchantDetailsVO.getLogin()))
                {
                    username            = merchantDetailsVO.getLogin();
                }

            }
            else if(muValue[1].equals("MU")){

                userId=muValue[0];
                request1.setAttribute("role","submerchant");

                username =merchantDAO.getMemberDetailsForOtpVAlidationActivityTracker(userId);

            }
            if(functions.isValueNull(merchantDAO.getMemberDetails(merchantRequestVO.getAuthentication().getMemberId()).getPartnerId()))
            {
                partnerid           =merchantDAO.getMemberDetails(merchantRequestVO.getAuthentication().getMemberId()).getPartnerId();
            }
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);


            String Login=username;

            String remoteAddr = Functions.getIpAddress(request1);
            logger.error("remoteAddr::: " + remoteAddr);
            String ipcountry= functions.getIPCountryLong(remoteAddr);
            logger.error("ipcountry in success case :: " + ipcountry);
            int serverPort = request1.getServerPort();
            String servletPath = request1.getServletPath();
            String header = "Client =" + remoteAddr + ":" + serverPort + ",X-Forwarded=" + servletPath;
            System.out.println("");
            activityTrackerVOs.setInterface(ActivityLogParameters.MERCHANT.toString());
            if(functions.isValueNull(String.valueOf(request1.getAttribute("role"))) && (String.valueOf(request1.getAttribute("role"))).equalsIgnoreCase("submerchant"))
            {
                activityTrackerVOs.setUser_name(Login + "-" + userId);
                activityTrackerVOs.setRole(ActivityLogParameters.SUBMERCHANT.toString());
            }
            else
            {
                activityTrackerVOs.setUser_name(Login + "-" + merchantId);
                activityTrackerVOs.setRole(ActivityLogParameters.MERCHANT.toString());
            }
            logger.error("UserName set from Login.java----- "+activityTrackerVOs.getUser_name());
            activityTrackerVOs.setAction(ActivityLogParameters.OTP_VALIDATION.toString());
            activityTrackerVOs.setModule_name(ActivityLogParameters.LOGIN_MERCHANT.toString());
            activityTrackerVOs.setLable_values("Username=" + username+" ,Ip address="+remoteAddr+",Ip Country= "+ipcountry);
            if (directKitResponseVO.getStatus()=="success")
            {
              activityTrackerVOs.setDescription(ActivityLogParameters.OTP_VALIDATION_SUCCESS.toString() + "-" + username);
            }
            else{
               activityTrackerVOs.setDescription(ActivityLogParameters.OTP_VALIDATION_FAIL.toString() + "-" + username);
            }

            activityTrackerVOs.setIp(remoteAddr);
            activityTrackerVOs.setHeader(header);
            activityTrackerVOs.setPartnerId(partnerid);
            try
            {
                AsyncActivityTracker asyncActivityTracker = AsyncActivityTracker.getInstance();
                asyncActivityTracker.asyncActivity(activityTrackerVOs);
            }
            catch (Exception ex)
            {
                logger.error("Exception while AsyncActivityLog::::", ex);
            }


            writeMerchantServiceResponse.setSuccessOTVerificationResponse(merchantResponseVO, directKitResponseVO, commonValidatorVO);
            return merchantResponseVO;

        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while merchant signup", e);
            transactionLogger.error("PZ Technical exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZ PZConstraintViolationException while merchant signup", e);
            transactionLogger.error("PZ PZConstraintViolationException while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        catch (Exception e)
        {
            logger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            transactionLogger.error("PZ GenericConstraintViolation exception while merchant signup", e);
            writeMerchantServiceResponse.setSignUpResponseForError(merchantResponseVO, commonValidatorVO, commonValidatorVO.getErrorCodeListVO(), PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
        }
        return merchantResponseVO;
    }

}
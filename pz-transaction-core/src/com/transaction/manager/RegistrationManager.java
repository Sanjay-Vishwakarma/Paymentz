package com.transaction.manager;

import com.auth.AuthFunctions;
import com.directi.pg.*;
import com.manager.OTPManager;
import com.manager.utils.TransactionUtil;
import com.manager.vo.DirectKitResponseVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import java.util.HashMap;

/**
 * Created by Sneha on 9/1/2016.
 */
public class RegistrationManager
{
    Functions functions = new Functions();
    private Logger logger = new Logger(RegistrationManager.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(RegistrationManager.class.getName());
    private TransactionUtil transactionUtil = new TransactionUtil();

    public DirectKitResponseVO getAuthToken(CommonValidatorVO commonValidatorVO,@Context HttpServletRequest request,@Context HttpServletResponse response) throws PZConstraintViolationException
    {
        HttpSession session = Functions.getNewSession(request);
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String loginName = "";
        String key = "";

        String partnerid = commonValidatorVO.getParetnerId();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        AuthFunctions authFunctions = new AuthFunctions();

        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        Member member = null;
        Merchants merchants=new Merchants();

        String status = "";
        String authToken = "";
        String role = (String)request.getAttribute("role");
        if(role.equalsIgnoreCase("merchant"))
        {
            loginName = commonValidatorVO.getMerchantDetailsVO().getLogin();
            key = commonValidatorVO.getMerchantDetailsVO().getKey();
            directKitResponseVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());

        }
        else
        {
            loginName = commonValidatorVO.getPartnerName();
            key = commonValidatorVO.getPartnerDetailsVO().getPartnerKey();
        }
        try
        {
            if (functions.isValueNull(key))
            {
                authToken = authFunctions.getAuthToken(loginName,role);
            }
            else
            {
                member = merchants.authenticate(loginName, partnerid, request);
                User user = ESAPI.authenticator().login(request, response);
                authToken = authFunctions.getAuthToken(loginName, role);
            }
        }
        catch (SystemError e)
        {
            logger.error("error in catch system error---",e);
            String error = "Authentication failed";
            directKitResponseVO.setStatusMsg("token generation failed");
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TOKEN_GENERATION_FAILED, error));
            PZExceptionHandler.raiseConstraintViolationException(RegistrationManager.class.getName(), "processGenerateToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));

        }
        catch (AuthenticationException ae)
        {
            logger.error("error in catch authentication exception---",ae);
            String error = "Authentication failed";
            directKitResponseVO.setStatusMsg("token generation failed");
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TOKEN_GENERATION_FAILED, error));
            PZExceptionHandler.raiseConstraintViolationException(RegistrationManager.class.getName(), "processGenerateToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
        }
        logger.debug("successfull login----");
        status = "success";

        directKitResponseVO.setStatus(status);
        directKitResponseVO.setAuthToken(authToken);

        directKitResponseVO.setErrorCodeListVO(errorCodeListVO);


        return directKitResponseVO;
    }

    public DirectKitResponseVO regenerateAuthToken(CommonValidatorVO commonValidatorVO,@Context HttpServletRequest request,@Context HttpServletResponse response) throws PZConstraintViolationException
    {
        HttpSession session = Functions.getNewSession(request);
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String loginName = commonValidatorVO.getMerchantDetailsVO().getLogin();

        String partnerid = commonValidatorVO.getParetnerId();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        AuthFunctions authFunctions = new AuthFunctions();

        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        Member member = null;
        Merchants merchants=new Merchants();

        String status = "";
        String authToken = "";
        String error = "";
        String role = (String)request.getAttribute("role");
        try
        {
            boolean isTokenExpired = authFunctions.verifyExpiry(commonValidatorVO.getAuthToken(),loginName,role);
            if (isTokenExpired)
            {
                member = merchants.authenticate(loginName, partnerid, request);
                authToken = authFunctions.getAuthToken(loginName, role);
            }
            else
            {
                error = "Invalid Token";
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TOKEN_GENERATION_FAILED, error));
                PZExceptionHandler.raiseConstraintViolationException(RegistrationManager.class.getName(), "processGenerateToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));

            }
        }
        catch (Exception e)
        {
            logger.error("error in catch login---",e);
            error = "Authentication failed";
            directKitResponseVO.setStatusMsg("change password failed");
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TOKEN_GENERATION_FAILED, error));
            PZExceptionHandler.raiseConstraintViolationException(RegistrationManager.class.getName(), "processGenerateToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));

        }
        logger.debug("successfull login----");
        status = "success";

        directKitResponseVO.setStatus(status);
        directKitResponseVO.setAuthToken(authToken);
        directKitResponseVO.setMemberId(String.valueOf(member.memberid));
        directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
        return directKitResponseVO;
    }

    public DirectKitResponseVO processGenerateOTP(CommonValidatorVO commonValidatorVO)
    {
        OTPManager otpManager = new OTPManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String otpStatus = otpManager.insertOtp(commonValidatorVO);
        directKitResponseVO.setStatus(otpStatus);
        directKitResponseVO.setStatusMsg(ErrorMessages.SYS_OTP_SENT + otpStatus);
        return directKitResponseVO;
    }

    public DirectKitResponseVO otpVerification(CommonValidatorVO commonValidatorVO) throws PZConstraintViolationException
    {
        OTPManager otpManager = new OTPManager();
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String verifyStatus = otpManager.isVerifyMerchantOtp(commonValidatorVO);
        directKitResponseVO.setStatus(verifyStatus);
        directKitResponseVO.setStatusMsg(ErrorMessages.SYS_OTP_VERIFICATION + verifyStatus);
        return directKitResponseVO;
    }

    public DirectKitResponseVO processGetPayoutBalance(CommonValidatorVO commonValidatorVO)
    {

        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        HashMap payoutBalanceMap                = functions.payoutBalance(commonValidatorVO.getMerchantDetailsVO().getMemberId());
        String totalBalance                     = String.valueOf(payoutBalanceMap.get("totalPayoutBalance"));
        String currentPayoutBalance             = String.valueOf(payoutBalanceMap.get("currentPayoutBalance"));

        if(!"-9999.00".equalsIgnoreCase(totalBalance)){
            directKitResponseVO.setStatus("success");
            directKitResponseVO.setCaptureAmount(totalBalance);
            directKitResponseVO.setAmount(currentPayoutBalance);
        }else{
            directKitResponseVO.setStatus("failed");
        }

        return directKitResponseVO;
    }
}

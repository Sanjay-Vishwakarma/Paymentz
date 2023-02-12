package manager;

import com.auth.AuthFunctions;
import com.directi.pg.*;
import com.manager.utils.TransactionUtil;
import com.manager.vo.DirectKitResponseVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

/**
 * Created by Sneha on 9/1/2016.
 */
public class RegistrationManager
{
    private Logger logger = new Logger(RegistrationManager.class.getName());
    private TransactionLogger transactionLogger = new TransactionLogger(RegistrationManager.class.getName());
    private TransactionUtil transactionUtil = new TransactionUtil();
    Functions functions = new Functions();

    public DirectKitResponseVO getAuthToken(CommonValidatorVO commonValidatorVO,@Context HttpServletRequest request,@Context HttpServletResponse response) throws PZConstraintViolationException
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
        String key = "";
        String authToken = "";
        String role = (String)request.getAttribute("role");
        try
        {
            member = merchants.authenticate(loginName, partnerid,request);

            User user = ESAPI.authenticator().login(request, response);

            authToken = authFunctions.getAuthToken(loginName,role);

        }
        catch (Exception e)
        {
            logger.error("error in catch login---",e);
            String error = "Authentication failed";
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

}

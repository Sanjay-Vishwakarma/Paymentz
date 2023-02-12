package transaction;

import com.directi.pg.FraudDefenderLogger;
import com.directi.pg.Logger;
import com.google.gson.Gson;
import com.manager.RestFraudDefenderManager;
import com.manager.utils.FraudDefenderUtil;
import com.manager.vo.DirectKitResponseVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.response.PZResponseStatus;
import com.payment.validators.RestCommonInputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.resource.Singleton;
import com.transaction.manager.RegistrationManager;
import com.transaction.utils.ReadDirectTransactionRequest;
import com.transaction.utils.WriteDirectFraudDefenderResponse;
import com.transaction.vo.restVO.RequestVO.RestPaymentRequest;
import com.transaction.vo.restVO.RequestVO.RestPaymentRequestVO;
import com.transaction.vo.restVO.ResponseVO.FraudDefender;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.Date;


/**
 * Created by Admin on 4/16/2020.
 */

@Singleton
@Path("fraudDefender")
public class DirectFraudDefenderRestIMPL implements DirectFrauDefenderRestService
{
    @Context
    HttpServletRequest request1;
    @Context
    HttpServletResponse response;

    private RestFraudDefenderManager restDirectTransactionManager = new RestFraudDefenderManager();
    private ReadDirectTransactionRequest readDirectTransactionRequest = new ReadDirectTransactionRequest();
    private WriteDirectFraudDefenderResponse writeDirectFraudDefenderResponse = new WriteDirectFraudDefenderResponse();
    private FraudDefenderUtil fraudDefenderUtil = new FraudDefenderUtil();
    private Logger logger = new Logger(DirectFraudDefenderRestIMPL.class.getName());
    private FraudDefenderLogger fraudDefenderLogger = new FraudDefenderLogger(DirectFraudDefenderRestIMPL.class.getName());
    private RestCommonInputValidator restCommonInputValidator = new RestCommonInputValidator();
    private RegistrationManager registrationManager = new RegistrationManager();
    Gson gson=new Gson();

    @Override
    public FraudDefender queryFraudefender(@InjectParam RestPaymentRequest paymentRequest)
    {
        FraudDefender response1 = new FraudDefender();
        RestPaymentRequestVO restPaymentRequestVO = readDirectTransactionRequest.readQueryFraudefender(paymentRequest);
        response1=queryFraudefenderJSON(restPaymentRequestVO);
        return response1;
    }

    @Override
    public FraudDefender queryFraudefenderJSON(RestPaymentRequestVO paymentRequestVO)
    {
        FraudDefender response1 = new FraudDefender();
        CommonValidatorVO commonValidatorVO = null;
        DirectKitResponseVO directKitResponseVO = null;

        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String error = " ";


        if (request1.getAttribute("authfail") != null)
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                logger.error("Inside failResponse---" + authResponse);
                writeDirectFraudDefenderResponse.setFailAuthTokenResponse(response1);
                return response1;
            }
            else if (authResponse.equals("false"))
            {
                logger.error("Inside failResponse---" + authResponse);
                writeDirectFraudDefenderResponse.setFailAuthTokenResponse(response1);
                return response1;
            }
        }
        Date d1=new Date();
        fraudDefenderLogger.error("Fraud defender Query API Start Time ### "+d1.getTime());
        fraudDefenderLogger.error("Rest Request for QueryFraudefenderJSON---" + gson.toJson(paymentRequestVO));
        commonValidatorVO = readDirectTransactionRequest.readQueryFraudefenderJSON(paymentRequestVO);
        if (commonValidatorVO == null)
        {
            fraudDefenderLogger.error("commonValidatorVO null ---");
            errorCodeListVO.addListOfError(writeDirectFraudDefenderResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            error = "Invalid request provided";
            writeDirectFraudDefenderResponse.setRestFraudDefenderResponseForError(response1, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
            return response1;
        }
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        try
        {
            Date d2=new Date();
            fraudDefenderLogger.error("Validation Start Time ###"+d2.getTime());
            commonValidatorVO = restCommonInputValidator.performRestGetQueryFraudefenderValidation(commonValidatorVO);
            fraudDefenderLogger.error("Validation Diff Time ###"+(new Date().getTime()-d2.getTime()));
            d2=new Date();
            directKitResponseVO = restDirectTransactionManager.processQueryFraudefender(commonValidatorVO);
            fraudDefenderLogger.error("Process Diff Time ###"+(new Date().getTime()-d2.getTime()));
            writeDirectFraudDefenderResponse.setSuccessResponseQueryDefender(response1,directKitResponseVO, commonValidatorVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZConstraint Violation Exception while placing QueryFraudefenderJSON via Rest", e);
            fraudDefenderLogger.error("PZConstraint Violation Exception while placing QueryFraudefenderJSON via WebService", e);
            writeDirectFraudDefenderResponse.setRestFraudDefenderResponseForError(response1, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while placing QueryFraudefenderJSON via Rest", e);
            fraudDefenderLogger.error("PZDBViolationException while placing QueryFraudefenderJSON via WebService", e);
            writeDirectFraudDefenderResponse.setRestFraudDefenderResponseForError(response1, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        // commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        fraudDefenderLogger.error("Rest response for QueryFraudefenderJSON---" + gson.toJson(response1));
        fraudDefenderLogger.error("Fraud defender Query API End Time ### "+new Date().getTime());
        fraudDefenderLogger.error("Fraud defender Query API Diff Time ### "+(new Date().getTime()-d1.getTime()));
        return response1;
    }

    @Override
    public FraudDefender refundFraudefender(@InjectParam RestPaymentRequest paymentRequest)
    {
        FraudDefender response1 = new FraudDefender();
        RestPaymentRequestVO restPaymentRequestVO = readDirectTransactionRequest.readRefundFraudefender(paymentRequest);
        response1=refundFraudefenderJSON(restPaymentRequestVO);
        return response1;

    }

    @Override
    public FraudDefender refundFraudefenderJSON(RestPaymentRequestVO paymentRequestVO)
    {
        FraudDefender response1 = new FraudDefender();
        CommonValidatorVO commonValidatorVO = null;
        DirectKitResponseVO directKitResponseVO = null;

        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String error = " ";
        if (request1.getAttribute("authfail") != null)
        {
            String authResponse = request1.getAttribute("authfail").toString();
            if (authResponse == null || authResponse.equals(""))
            {
                logger.error("Inside failResponse---" + authResponse);
                writeDirectFraudDefenderResponse.setFailAuthTokenResponse(response1);
                return response1;
            }
            else if (authResponse.equals("false"))
            {
                logger.error("Inside failResponse---" + authResponse);
                writeDirectFraudDefenderResponse.setFailAuthTokenResponse(response1);
                return response1;
            }
        }
        Date d1=new Date();
        fraudDefenderLogger.error("Fraud defender refund API Start Time ### "+d1.getTime());
        fraudDefenderLogger.error("Rest Request for RefundFraudDefenderJSON---" + gson.toJson(paymentRequestVO));
        commonValidatorVO = readDirectTransactionRequest.readRefundFraudefenderJSON(paymentRequestVO);

        if (commonValidatorVO == null)
        {
            fraudDefenderLogger.error("commonValidatorVO null ---");
            errorCodeListVO.addListOfError(writeDirectFraudDefenderResponse.formSystemErrorCodeVO(ErrorName.VALIDATION_REQUEST_NULL, "Invalid Request provided."));
            error = "Invalid request provided";
            writeDirectFraudDefenderResponse.setRestFraudDefenderResponseForError(response1, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, "Please provide Request Attribute");
            return response1;
        }
        commonValidatorVO.setErrorCodeListVO(errorCodeListVO);
        try
        {
            commonValidatorVO = restCommonInputValidator.performRestGetRefundFraudefenderValidation(commonValidatorVO);
            errorCodeListVO = fraudDefenderUtil.getReferencedFraudDefenderRefundTransDetails(commonValidatorVO);
            if (errorCodeListVO != null)
            {  directKitResponseVO=new DirectKitResponseVO();
                 directKitResponseVO.setStatus("failed");
                writeDirectFraudDefenderResponse.setSuccessResponseRefundDefender(response1,directKitResponseVO, commonValidatorVO);
//                writeDirectFraudDefenderResponse.setRestFraudDefenderResponseForError(response1, commonValidatorVO, errorCodeListVO, PZResponseStatus.ERROR, commonValidatorVO.getErrorMsg());
                fraudDefenderLogger.error("Rest response for RefundFraudDefenderJSON---" + gson.toJson(response1));
                return response1;
            }
            directKitResponseVO = restDirectTransactionManager.processFruadDefenderRefund(commonValidatorVO);
            writeDirectFraudDefenderResponse.setSuccessResponseRefundDefender(response1,directKitResponseVO, commonValidatorVO);
        }
        catch (PZConstraintViolationException e)
        {
            logger.error("PZConstraint Violation Exception while placing QueryFraudefenderJSON via Rest", e);
            fraudDefenderLogger.error("PZConstraint Violation Exception while placing QueryFraudefenderJSON via WebService", e);
            writeDirectFraudDefenderResponse.setRestFraudDefenderResponseForError(response1, commonValidatorVO, e.getPzConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, e.getPzConstraint().getMessage());
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZ DBViolation exception while placing QueryFraudefenderJSON via Rest", e);
            fraudDefenderLogger.error("PZDBViolationException while placing QueryFraudefenderJSON via WebService", e);
            writeDirectFraudDefenderResponse.setRestFraudDefenderResponseForError(response1, commonValidatorVO, e.getPzdbConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        catch (PZTechnicalViolationException e)
        {
            logger.error("PZ DBViolation exception while placing QueryFraudefenderJSON via Rest", e);
            fraudDefenderLogger.error("PZDBViolationException while placing QueryFraudefenderJSON via WebService", e);
            writeDirectFraudDefenderResponse.setRestFraudDefenderResponseForError(response1, commonValidatorVO, e.getPzTechnicalConstraint().getErrorCodeListVO(), PZResponseStatus.ERROR, "Internal Error occurred,Please contact support for more information");
        }
        fraudDefenderLogger.error("Rest response for RefundFraudDefenderJSON---" + gson.toJson(response1));
        fraudDefenderLogger.error("Fraud defender refund API End Time ### "+new Date().getTime());
        fraudDefenderLogger.error("Fraud defender refund API Diff Time ### "+(new Date().getTime()-d1.getTime()));
        return response1;
    }
}


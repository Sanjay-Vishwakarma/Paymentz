package com.payment.duspaydirectdebit;

import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.ResourceBundle;

/**
 * Created by Jitendra on 24-Sep-19.
 */
public class DusPayDDPaymentGateway extends AbstractPaymentGateway
{
    public static final  String GATEWAY_TYPE="duspaydd";
    TransactionLogger transactionLogger=new TransactionLogger(DusPayDDPaymentGateway.class.getName());
    public DusPayDDPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processSale(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processSale ---");
        CommResponseVO commResponseVO= new CommResponseVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        // https://portal.duspay.com/echeckprocess.htm?id=1330&bid=q5TyQYa4Zu&type=9
        String id=gatewayAccount.getMerchantId();
        String bid=gatewayAccount.getFRAUD_FTP_PASSWORD();
        String type=gatewayAccount.getFRAUD_FTP_USERNAME();
        String redirectUrl="https://portal.duspay.com/echeckprocess.htm?id="+id+"&bid="+bid+"&type="+type;
        transactionLogger.debug("redirectUrl ---"+redirectUrl);

        commResponseVO.setStatus("pending");
        commResponseVO.setRedirectUrl(redirectUrl);

        return commResponseVO;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("DusPayDDPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by DusPay Direct Debit gateway. Please contact your Tech. support Team:::",null);
    }

    public String processAutoRedirect(CommonValidatorVO commonValidatorVO) throws PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("processAutoRedirect in DusPayDDPaymentGateway ----");
        String html="";
        DusPayDDUtils dusPayDDUtils=new DusPayDDUtils();
        CommRequestVO commRequestVO = null;
        CommResponseVO transRespDetails = null;
        commRequestVO = dusPayDDUtils.getCommRequestFromUtils(commonValidatorVO);

        try
        {
            transRespDetails = (CommResponseVO) this.processSale(commonValidatorVO.getTrackingid(), commRequestVO);
            if (transRespDetails != null && transRespDetails.getStatus().equalsIgnoreCase("pending"))
            {
                transactionLogger.debug("status ----"+transRespDetails.getStatus());
                html = dusPayDDUtils.getRedirectForm(commonValidatorVO.getTrackingid(),transRespDetails);
                transactionLogger.error("Html in processAutoRedirect -------" + html);
            }
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException in OneRoadPaymentGateway ---",e);
        }
        catch (Exception e)
        {
            transactionLogger.error("error", e);
        }
        return html;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}

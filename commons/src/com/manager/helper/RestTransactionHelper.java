package com.manager.helper;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TerminalManager;
import com.manager.utils.TransactionUtil;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.RecurringBillingVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.TokenDetailsVO;
import com.payment.checkers.PaymentChecker;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraint.PZConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.AbstractInputValidator;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputValidatorFactory;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.DirectCaptureValidatorVO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by Sneha on 2/24/2016.
 */
public class RestTransactionHelper
{
    private static Logger log = new Logger(RestTransactionHelper.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(RestTransactionHelper.class.getName());

    /*public CommonValidatorVO performRestTokenChecks(CommonValidatorVO commonValidatorVO, TokenDetailsVO tokenDetailsVO) throws PZConstraintViolationException, PZDBViolationException
    {
        System.out.println("inside helper----");
        log.debug("Inside performRestTokenChecks::");
        MerchantDetailsVO merchantDetailsVO= commonValidatorVO.getMerchantDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        TerminalVO terminalVO = null;
        LimitChecker limitChecker = new LimitChecker();
        PaymentChecker paymentChecker = new PaymentChecker();
        TransactionHelper transactionHelper = new TransactionHelper();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeListVO errorCodeListVO = null;
        Functions functions = new Functions();
        TerminalManager terminalManager = new TerminalManager();
        String error = "";

        if (functions.isValueNull(commonValidatorVO.getTerminalId()))
        {
            System.out.println("inside if helper-----");
            terminalVO = terminalManager.getCardIdAndPaymodeIdFromTerminal(commonValidatorVO.getMerchantDetailsVO().getMemberId(), commonValidatorVO.getTerminalId(), commonValidatorVO.getTransDetailsVO().getCurrency(), tokenDetailsVO.getPaymentType(), tokenDetailsVO.getCardType());
        }
        else
        {
            System.out.println("inside else helper----");
            terminalVO = terminalManager.getTerminalFromPaymodeCardtypeMemberidCurrency(commonValidatorVO.getMerchantDetailsVO().getMemberId(), tokenDetailsVO.getPaymentType(), tokenDetailsVO.getCardType(), genericTransDetailsVO.getCurrency());
        }
        if(terminalVO ==  null || functions.isValueNull(terminalVO.getCurrency()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_INVALID_CURRENCY_BRAND_MODE);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }
        commonValidatorVO.setTerminalVO(terminalVO);

        if("N".equals(terminalVO.getIsTokenizationActive()))
        {
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_TOKEN_ALLOWED);
            error = errorCodeVO.getErrorCode() + " " + errorCodeVO.getErrorDescription();
            commonValidatorVO.setErrorMsg(error);
            if (commonValidatorVO.getErrorCodeListVO() != null)
                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            return commonValidatorVO;
        }

        //Transaction limit checking
        limitChecker.checkTransactionAmount(genericTransDetailsVO.getAmount(), terminalVO.getMax_transaction_amount(), terminalVO.getMin_transaction_amount());

        if(genericTransDetailsVO.getCurrency().equals("JPY"))
        {
            if(!paymentChecker.isAmountValidForJPY(genericTransDetailsVO.getCurrency(),genericTransDetailsVO.getAmount()))
            {
                error = error + "JPY Currency does not have cent value after decimal. Please give .00 as decimal value";
                errorCodeListVO = getErrorVO(ErrorName.SYS_JPY_CURRENCY_CHECK);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO,null, null);
            }
        }

        //Unique order chacking
        String uniqueorder = null;
        String fromtype = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId()).getGateway();
        uniqueorder = transactionHelper.checkorderuniqueness(merchantDetailsVO.getMemberId(), fromtype, genericTransDetailsVO.getOrderId());
        if (!uniqueorder.equals(""))
        {
            error = error + (commonValidatorVO.getTransDetailsVO().getOrderId()+"-Duplicate Order Id " + uniqueorder);
            errorCodeListVO = getErrorVO(ErrorName.SYS_UNIQUEORDER);
            PZExceptionHandler.raiseConstraintViolationException("RestTransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,errorCodeListVO, null, null);
        }

        if(merchantDetailsVO.getIsBlacklistTransaction().equalsIgnoreCase("Y"))
        {
            if(!transactionHelper.isIpAddressBlocked(commonValidatorVO.getAddressDetailsVO().getIp()))
            {
                error = error + "Your IpAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDIP);
                PZExceptionHandler.raiseConstraintViolationException("RestTransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if(!transactionHelper.isEmailAddressBlocked(commonValidatorVO.getAddressDetailsVO().getEmail()))
            {
                error = error + "Your EmailAddress is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDEMAIL);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }

            if(!transactionHelper.isCountryBlocked(commonValidatorVO.getAddressDetailsVO().getCountry(), commonValidatorVO.getAddressDetailsVO().getTelnocc()))
            {
                error = error + "Your Country is Blocked:::Please contact support for further assistance";
                errorCodeListVO = getErrorVO(ErrorName.SYS_BLOCKEDCOUNTRY);
                PZExceptionHandler.raiseConstraintViolationException("TransactionHelper.class", "performCommonSystemChecksStep1()", null, "Common", error.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, null, null);
            }
        }
        return commonValidatorVO;
    }*/

    public ErrorCodeListVO getErrorVO(ErrorName errorName)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO =new ErrorCodeListVO();

        errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        errorCodeListVO.addListOfError(errorCodeVO);
        return errorCodeListVO;
    }
}
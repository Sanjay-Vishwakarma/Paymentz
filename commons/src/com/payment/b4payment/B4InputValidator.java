package com.payment.b4payment;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 19/1/2017.
 */
public class B4InputValidator extends CommonInputValidator
{
    private static Logger logger = new Logger(B4InputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(B4InputValidator.class.getName());
    private Functions functions = new Functions();


    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionname) throws PZDBViolationException
    {
        logger.debug("Inside B4 Input Validator");
        transactionLogger.debug("Inside B4 Input Validator");
        //System.out.println("Inside B4 Input Validator");
        String error = "";

        List<InputFields> parameter = new ArrayList<InputFields>();

        parameter.add(InputFields.AMOUNT);
        parameter.add(InputFields.BIC);
        parameter.add(InputFields.IBAN);
        parameter.add(InputFields.MANDATEID);
        parameter.add(InputFields.FIRSTNAME);
        parameter.add(InputFields.LASTNAME);
        parameter.add(InputFields.CURRENCY);


        ValidationErrorList errorList = new ValidationErrorList();

        InputValidationsforB4(commonValidatorVO, parameter, errorList, false);

        String EOL = "<BR>";

        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : parameter)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;

                }
            }
            return error;
        }
        MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
        String accountid = merchantDetailsVO.getAccountId();
        String toid = merchantDetailsVO.getMemberId();


        if (!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length() > 10 || !ESAPI.validator().isValidInput("toid", toid, "Numbers", 20, false))
        {
            error = ErrorMessages.INVALID_TOID;
        }
        else
        {
            merchantDetailsVO = getMerchantConfigDetails(toid, commonValidatorVO.getTransDetailsVO().getTotype());

            merchantDetailsVO.setAccountId(accountid);
            commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
        }
        return error;
    }

    private void InputValidationsforB4(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();

        for (InputFields inputFields1 : inputFields)
        {
            switch (inputFields1)
            {
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getAmount(), "AmountStr", 12, isOptional))
                        validationErrorList.addError(inputFields1.toString(), new ValidationException("Invalid Amount", "Invalid Amount ::::" + genericTransDetailsVO.getAmount()));
                    break;

                case BIC:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericCardDetailsVO.getBIC(), "alphanum", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REJECTED_BIC);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_REJECTED_BIC);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericCardDetailsVO.getBIC(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case IBAN:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericCardDetailsVO.getIBAN(), "alphanum", 25, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REJECTED_IBAN);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_REJECTED_IBAN);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericCardDetailsVO.getIBAN(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                /*case MANDATEID:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericCardDetailsVO.getMandateId(), "alphanum", 54, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_MANDATEID);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericCardDetailsVO.getMandateId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;*/

                case MANDATEID:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericCardDetailsVO.getMandateId(), "alphanum", 54, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_MANDATEID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_MANDATEID);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.MANDATEID_NOTVALID, ErrorMessages.MANDATEID_NOTVALID + ":::" + genericCardDetailsVO.getMandateId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case ORDERDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getOrderDesc(), "alphanum", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericTransDetailsVO.getOrderDesc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getFirstname(), "StrictString", 50, isOptional))
                        validationErrorList.addError(inputFields1.toString(), new ValidationException("Invalid Given Name", "Invalid Given Name::::" + genericAddressDetailsVO.getFirstname()));
                    break;

                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getLastname(), "StrictString", 50, isOptional))
                        validationErrorList.addError(inputFields1.toString(), new ValidationException("Invalid Family Name", "Invalid Family Name::::" + genericAddressDetailsVO.getLastname()));
                    break;
            }
        }
    }
}


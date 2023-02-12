package com.payment.payforasia.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 7/26/14
 * Time: 4:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayforAsiaInputValidator extends CommonInputValidator
{
    String error = "";
    private static Logger log = new Logger(PayforAsiaInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayforAsiaInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        String error = "";
        List<InputFields> param = new ArrayList<InputFields>();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();

        param.add(InputFields.COUNTRYCODE);
        param.add(InputFields.CITY);
        param.add(InputFields.STATE);
        param.add(InputFields.TELNO);
        param.add(InputFields.ZIP);
        param.add(InputFields.STREET);
        param.add(InputFields.CARDHOLDERIP);
        ValidationErrorList errorList = new ValidationErrorList();

        if ("Y".equalsIgnoreCase(addressValidation))
        {
            inputValidationForPayForAsia(commonValidatorVO, param, errorList, false);
            error = error + inputValiDatorUtils.getError(errorList,param,actionName);

            String EOL = "<BR>";
            if (!errorList.isEmpty())
            {
                for (InputFields inputFields : param)
                {
                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                    }
                }
            }
        }

        return error;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void inputValidationForPayForAsia(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        for (InputFields input : inputList)
        {
            switch (input)
            {
                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getPhone(), "Phone", 15, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TELNO, ErrorMessages.INVALID_TELNO + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCountry(), "CountryCode", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_COUNTRY_CODE, ErrorMessages.INVALID_COUNTRY_CODE + ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCity(), "City", 30, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CITY, ErrorMessages.INVALID_CITY + ":::" + genericAddressDetailsVO.getCity(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getStreet(), "SafeString", 150, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STREET, ErrorMessages.INVALID_STREET + ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getZipCode(), "Zip", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ZIP, ErrorMessages.INVALID_ZIP + ":::" + genericAddressDetailsVO.getZipCode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getState(), "State", 40, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STATE, ErrorMessages.INVALID_STATE + ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        //validationErrorList.addError(input.toString(),new ValidationException("Invalid State","Invalid State :::"+genericAddressDetailsVO.getState()));
                    }
                    break;
                case CARDHOLDERIP:
                    if (!(ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddressNew", 80, isOptional)))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMER_IP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CUSTOMER_IP, ErrorMessages.INVALID_CUSTOMER_IP + ":::" + genericAddressDetailsVO.getCardHolderIpAddress(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        //validationErrorList.addError(input.toString(),new ValidationException("Invalid State","Invalid State :::"+genericAddressDetailsVO.getState()));
                    }
                    break;
            }
        }
    }
}


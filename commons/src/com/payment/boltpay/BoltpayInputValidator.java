package com.payment.boltpay;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
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
 * Created by admin on 06-Dec-21.
 */
public class BoltpayInputValidator extends CommonInputValidator
{
    TransactionLogger transactionLogger= new TransactionLogger(BoltpayInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionname, String addressValidation)
    {

        transactionLogger.error("inside BoltpayInputValidator ----- " + actionname);
        String error="";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();

        List<InputFields> mandatoryParameter = new ArrayList();
        List<InputFields> addressParameter = new ArrayList();

        mandatoryParameter.add(InputFields.FIRSTNAME);
        mandatoryParameter.add(InputFields.LASTNAME);
        mandatoryParameter.add(InputFields.EMAILADDR);
        mandatoryParameter.add(InputFields.TELNO);
        mandatoryParameter.add(InputFields.TELCC);

        addressParameter.add(InputFields.COUNTRYCODE);
        addressParameter.add(InputFields.CITY);
        addressParameter.add(InputFields.ZIP);
        addressParameter.add(InputFields.STREET);
        addressParameter.add(InputFields.STATE);
        addressParameter.add(InputFields.CARDHOLDERIP);

        transactionLogger.error("addressValidation ==== " + addressValidation);

        ValidationErrorList errorList2 = new ValidationErrorList();
        InputValidationsforPayneteasy(commonValidatorVO, mandatoryParameter, errorList2, false);
        error = error + inputValiDatorUtils.getError(errorList2, mandatoryParameter, actionname);
        transactionLogger.error("mandatory parameter error ==== " + error);

        if("Y".equals(addressValidation))
        {
            ValidationErrorList errorList = new ValidationErrorList();
            InputValidationsforPayneteasy(commonValidatorVO, addressParameter, errorList, false);
            error = error + inputValiDatorUtils.getError(errorList, addressParameter, actionname);
            transactionLogger.error("error when addressValidation is Y ---> " + error);
        }
        else
        {
            ValidationErrorList errorList = new ValidationErrorList();
            InputValidationsforPayneteasy(commonValidatorVO, addressParameter, errorList, true);
            error = error + inputValiDatorUtils.getError(errorList, addressParameter, actionname);
            transactionLogger.error("error when addressValidation is N ---> " + error);

        }

        return error;
    }

    private void InputValidationsforPayneteasy(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();


        for (InputFields inputFields1 : inputFields) {
            switch (inputFields1)
            {
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getEmail(), "Email", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getPhone(), "Phone", 24, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case TELCC:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getTelnocc(), "OnlyNumber", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNOCC);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getTelnocc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getFirstname(), "AccentUsername", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getLastname(), "AccentUsername", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCountry(), "CountryCode", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCity(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getCity(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getStreet(), "SafeString", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getZipCode(), "Zip", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getZipCode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getState(), "SafeString", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case CARDHOLDERIP:
                    if (!(ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getIp(), "IPAddress", 45, isOptional) || ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getIp(), "IPAddressNew", 45, isOptional)))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMER_IP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CUSTOMER_IP);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getIp(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }
}

package com.payment.boombill;

import com.directi.pg.Functions;
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
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2022-01-24.
 */
public class BoomBillInputValidator extends CommonInputValidator
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(BoomBillInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        ErrorCodeUtils errorCodeUtils           = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO                 = new ErrorCodeVO();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator           = new InputValidator();
        String error                            = "";
        List<InputFields> mandatoryParams       = new ArrayList<InputFields>();


        mandatoryParams.add(InputFields.FIRSTNAME);
        mandatoryParams.add(InputFields.LASTNAME);
        mandatoryParams.add(InputFields.STREET);
        mandatoryParams.add(InputFields.STATE);
        mandatoryParams.add(InputFields.CITY);
        mandatoryParams.add(InputFields.COUNTRY);
        mandatoryParams.add(InputFields.EMAILADDR);
        mandatoryParams.add(InputFields.TELNO);
        mandatoryParams.add(InputFields.ZIP);
        mandatoryParams.add(InputFields.IPADDR);

        if("Y".equalsIgnoreCase(addressValidation))
        {
            ValidationErrorList errorList1 = new ValidationErrorList();
            InputValidations(commonValidatorVO, mandatoryParams, errorList1, false);
            error = error + inputValiDatorUtils.getError(errorList1, mandatoryParams, actionName);
        }
        else
        {
            ValidationErrorList errorList1 = new ValidationErrorList();
            InputValidations(commonValidatorVO, mandatoryParams, errorList1, true);
            error = error + inputValiDatorUtils.getError(errorList1, mandatoryParams, actionName);
        }
        transactionLogger.error("error  size------------>"+commonValidatorVO.getErrorCodeListVO().getListOfError().size());
        transactionLogger.error("error  BoomBillInputValidator------------>"+error+" "+addressValidation);

        return error;
    }

    public void InputValidations(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();


        Functions functions = new Functions();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();

        for (InputFields input : inputList)
        {
            ErrorCodeVO errorCodeVO = new ErrorCodeVO();
            switch (input)
            {
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getFirstname(), "SafeString", 50, isOptional) || functions.hasHTMLTags(genericAddressDetailsVO.getFirstname()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_FIRST_NAME, ErrorMessages.INVALID_FIRST_NAME + ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getLastname(), "SafeString", 50, isOptional) || functions.hasHTMLTags(genericAddressDetailsVO.getLastname()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LAST_NAME, ErrorMessages.INVALID_LAST_NAME + ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case COUNTRY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCountry(),"StrictString",2, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorDescription("Invalid country code, accepts only [a-z][A-Z] with max length 2");
                        errorCodeVO.setApiDescription("Invalid country code, accepts only [a-z][A-Z] with max length 2");
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null){
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                    }
                    break;
                case CITY:
                    if ( (functions.isValueNull(genericAddressDetailsVO.getCity()) && (genericAddressDetailsVO.getCity().trim()).equals("")) || !ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCity(), "City", 30, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CITY);
                        errorCodeVO.setErrorDescription("Invalid city, accepts only [0-9][a-z][A-Z].,-/[space]#;&~` with max length 30");
                        errorCodeVO.setApiDescription("Invalid city, accepts only [0-9][a-z][A-Z].,-/[space]#;&~` with max length 30");
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CITY, ErrorMessages.INVALID_CITY + ":::" + genericAddressDetailsVO.getCity(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getZipCode(), "Zip", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ZIP, ErrorMessages.INVALID_ZIP + ":::" + genericAddressDetailsVO.getZipCode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getState(), "StrictString", 40, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STATE, ErrorMessages.INVALID_STATE + ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getPhone(), "Phone", 24, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TELNO, ErrorMessages.INVALID_TELNO + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getStreet(), "SafeString", 150, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STREET, ErrorMessages.INVALID_STREET + ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getEmail(), "Email", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_EMAIL, ErrorMessages.INVALID_EMAIL + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case IPADDR:
                    if (!(ESAPI.validator().isValidInput(InputFields.IPADDR.name(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(InputFields.IPADDR.name(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddressNew", 80, isOptional)) || functions.hasHTMLTags(genericAddressDetailsVO.getCardHolderIpAddress()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.VALIDATION_IPADDRESS);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_IPADDRESS);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_IPADDRESS);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_IPADDRESS, ErrorMessages.INVALID_IPADDRESS + ":::" + genericAddressDetailsVO.getCardHolderIpAddress(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }
}

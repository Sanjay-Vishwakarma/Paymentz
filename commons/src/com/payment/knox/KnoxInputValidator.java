package com.payment.knox;

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
 * Created by Admin on 8/7/2020.
 */
public class KnoxInputValidator extends CommonInputValidator
{
        TransactionLogger transactionLogger= new TransactionLogger(KnoxInputValidator.class.getName());
        public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionname,String addressValidation)
        {
            transactionLogger.error(":::::inside TotalPayValidator:::::");
            InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
            InputValidator inputValidator = new InputValidator();
            String error = "";
            List<InputFields> parameter = new ArrayList();
            parameter.add(InputFields.EMAILADDR);
            parameter.add(InputFields.ZIP);
            parameter.add(InputFields.STREET);
            parameter.add(InputFields.TELNO);
            parameter.add(InputFields.CITY);
            parameter.add(InputFields.STATE);
            parameter.add(InputFields.CARDHOLDERIP);

            if("REST".equalsIgnoreCase(actionname))
            {
                parameter.add(InputFields.FIRSTNAME);
                parameter.add(InputFields.LASTNAME);
            }


            if("Y".equals(addressValidation))
            {
                ValidationErrorList errorList = new ValidationErrorList();
                ValidationErrorList errorList1 = new ValidationErrorList();
                InputValidationsforTotalPay(commonValidatorVO, parameter, errorList, false);
                error = error + inputValiDatorUtils.getError(errorList, parameter, actionname);

                String EOL = "<BR>";

                if (!errorList1.isEmpty())
                {
                    for (InputFields inputFields : parameter)
                    {
                        if (errorList1.getError(inputFields.toString()) != null)
                        {
                            transactionLogger.error(errorList1.getError(inputFields.toString()).getLogMessage());
                            error = error + errorList1.getError(inputFields.toString()).getMessage() + EOL;
                        }
                    }
                }
            }
            else
            {
                ValidationErrorList errorList = new ValidationErrorList();
                ValidationErrorList errorList1 = new ValidationErrorList();
                InputValidationsforTotalPay(commonValidatorVO, parameter, errorList, true);
                error = error + inputValiDatorUtils.getError(errorList, parameter, actionname);

                String EOL = "<BR>";

                if (!errorList1.isEmpty())
                {
                    for (InputFields inputFields : parameter)
                    {
                        if (errorList1.getError(inputFields.toString()) != null)
                        {
                            transactionLogger.error(errorList1.getError(inputFields.toString()).getLogMessage());
                            error = error + errorList1.getError(inputFields.toString()).getMessage() + EOL;
                        }
                    }
                }
            }
            return error;
        }

        private void InputValidationsforTotalPay(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
        {
            ErrorCodeVO errorCodeVO = new ErrorCodeVO();
            ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
            GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
            transactionLogger.debug("city val is ===="+genericAddressDetailsVO.getCity());
            for (InputFields inputFields1 : inputFields) {
                switch (inputFields1)
                {
                    case EMAILADDR:
                        if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getEmail(), "Email", 100, isOptional))
                        {
                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                        break;
                    case TELCC:
                        if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getTelnocc(), "OnlyNumber", 15, isOptional))
                        {
                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNOCC);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getTelnocc(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                        break;
                    case TELNO:
                        if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getPhone(), "OnlyNumber", 15, isOptional))
                        {
                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                        break;
                    case COUNTRYCODE:
                        if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCountry(), "CountryCode", 3, isOptional))
                        {
                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                        break;
                    case CITY:
                        if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCity(), "City", 50, isOptional))
                        {
                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getCity(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                        break;
                    case STREET:
                        if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getStreet(), "Description", 100, isOptional))
                        {
                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                        break;
                    case ZIP:
                        if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getZipCode(), "Zip", 10, isOptional))
                        {
                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getZipCode(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                        break;
                    case STATE:
                        if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getState(), "alphanum", 40, isOptional))
                        {
                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                        break;
                    case CARDHOLDERIP:
                        if (!(ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddressNew", 80, isOptional)))
                        {
                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMER_IP);
                            errorCodeVO.setErrorName(ErrorName.VALIDATION_CUSTOMER_IP);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException("Invalid IP Address", "Invalid IP Address :::" + genericAddressDetailsVO.getCardHolderIpAddress(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                        break;
                    case CUSTOMERID:
                        if (!ESAPI.validator().isValidInput(inputFields1.toString(), commonValidatorVO.getCustomerId(), "customerId", 100, isOptional))
                        {
                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMERID);
                            errorCodeVO.setErrorName(ErrorName.VALIDATION_CUSTOMERID);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_CUSTOMERID, ErrorMessages.INVALID_CUSTOMERID + ":::" + commonValidatorVO.getCustomerId(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                        break;
                    case BIRTHDATE:
                        if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getBirthdate(), "Numbers", 8, isOptional) /*|| !Functions.isValidDate(genericAddressDetailsVO.getBirthdate())*/)
                        {
                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_BIRTHDATE);
                            errorCodeVO.setErrorName(ErrorName.VALIDATION_BIRTHDATE);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_BIRTHDATE, ErrorMessages.INVALID_BIRTHDATE + ":::" + genericAddressDetailsVO.getBirthdate(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                        break;
                    case FIRSTNAME:
                        if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getFirstname(), "AccentUsername", 50, isOptional))
                        {
                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                            errorCodeVO.setErrorReason(ErrorMessages.INVALID_FIRST_NAME);
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
                            errorCodeVO.setErrorReason(ErrorMessages.INVALID_LAST_NAME);
                            errorCodeVO.setErrorName(ErrorName.VALIDATION_LASTNAME);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                        break;
                }
            }
        }
    }

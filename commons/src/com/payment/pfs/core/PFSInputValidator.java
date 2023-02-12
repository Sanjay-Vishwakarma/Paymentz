package com.payment.pfs.core;

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
import org.owasp.esapi.errors.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/9/15
 * Time: 1:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class PFSInputValidator extends CommonInputValidator
{   private static Logger log = new Logger(PFSInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PFSInputValidator.class.getName());
    public  String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName,String addressValidation)
    {
        String error="";
        List<InputFields> param=new ArrayList<InputFields>();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();

        param.add(InputFields.FIRSTNAME);
        param.add(InputFields.LASTNAME);
        param.add(InputFields.COUNTRYCODE);
        param.add(InputFields.CITY);
        param.add(InputFields.STATE);
        param.add(InputFields.TELNO);
        param.add(InputFields.ZIP);
        param.add(InputFields.STREET);
        ValidationErrorList errorList = new ValidationErrorList();
        if("Y".equalsIgnoreCase(addressValidation))
        {
            inputValidationsForPFS(commonValidatorVO, param, errorList, false);

            String EOL = "<BR>";
            if (!errorList.isEmpty())
            {
                for (InputFields inputFields : param)
                {
                    if (errorList.getError(inputFields.toString()) != null)
                    {
                       /* log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;*/
                        PZValidationException pzValidationException = (PZValidationException) errorList.getError(inputFields.toString());
                        ErrorCodeVO errorCodeVO = pzValidationException.getErrorCodeVO();
                        log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        //error = error + " " + errorCodeVO.getErrorDescription()+ EOL;
                        error = inputValiDatorUtils.getError(errorList,param,actionName);

                        //System.out.println("error---"+error);
                    }
                }
            }
        }

        return error;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public  void inputValidationsForPFS(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        for (InputFields input : inputList)
        {
            switch(input)
            {
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getFirstname(), "alphanum", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_FIRST_NAME, ErrorMessages.INVALID_FIRST_NAME + ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    } //validationErrorList.addError(input.toString(),new ValidationException("Invalid FirstName","Invalid FirstName :::"+genericAddressDetailsVO.getFirstname()));
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getLastname(), "alphanum", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_LAST_NAME, ErrorMessages.INVALID_LAST_NAME + ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        //validationErrorList.addError(input.toString(),new ValidationException("Invalid LastName","Invalid LastName :::"+genericAddressDetailsVO.getLastname()));
                    }
                        break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getPhone(), "OnlyNumber", 15, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_TELNO, ErrorMessages.INVALID_TELNO + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        //validationErrorList.addError(input.toString(),new ValidationException("Invalid Telephone Number","Invalid Telephone Number :::"+genericAddressDetailsVO.getPhone()));
                    }
                        break;
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCountry(), "StrictString", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_COUNTRY_CODE, ErrorMessages.INVALID_COUNTRY_CODE + ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        //validationErrorList.addError(input.toString(),new ValidationException("Invalid CountryCode","Invalid CountryCode :::"+genericAddressDetailsVO.getCountry()));
                    }
                        break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCity(), "alphanum", 30, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CITY, ErrorMessages.INVALID_CITY + ":::" + genericAddressDetailsVO.getCity(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        //validationErrorList.addError(input.toString(),new ValidationException("Invalid City","Invalid City :::"+genericAddressDetailsVO.getCity()));
                    }
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getStreet(), "alphanum", 150, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STREET, ErrorMessages.INVALID_STREET + ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        //validationErrorList.addError(input.toString(),new ValidationException("Invalid Address/Street","Invalid Address/Street :::"+genericAddressDetailsVO.getStreet()));
                    }
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getZipCode(), "alphanum", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_ZIP, ErrorMessages.INVALID_ZIP + ":::" + genericAddressDetailsVO.getZipCode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        //validationErrorList.addError(input.toString(),new ValidationException("Invalid Zip","Invalid Zip :::"+genericAddressDetailsVO.getZipCode()));
                    }
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getState(), "alphanum", 40, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_STATE, ErrorMessages.INVALID_STATE + ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        //validationErrorList.addError(input.toString(),new ValidationException("Invalid State","Invalid State :::"+genericAddressDetailsVO.getState()));
                    }
                    break;
            }
        }
    }
}


package com.payment.acqra;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.vo.CommonValidatorVO;
import com.payment.validators.vo.ReserveField2VO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 12/20/2019.
 */
public class AcqraInputValidator extends CommonInputValidator
{
    private static Logger log = new Logger(AcqraInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(AcqraInputValidator.class.getName());

    public  String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName,String addressValidation)
    {
        String error = "";
        List<InputFields> param = new ArrayList<InputFields>();

        param.add(InputFields.FIRSTNAME);
        param.add(InputFields.LASTNAME);
        param.add(InputFields.COUNTRYCODE);
        param.add(InputFields.CITY);
        //param.add(InputFields.STATE);
        param.add(InputFields.TELNO);
        param.add(InputFields.ZIP);
        param.add(InputFields.STREET);
        param.add(InputFields.CARDHOLDERIP);
        param.add(InputFields.EMAILADDR);

        if ("Y".equalsIgnoreCase(addressValidation))
        {
            ValidationErrorList errorList = new ValidationErrorList();
            transactionLogger.error("---------------Inside AcqraInputValidator class---------------------");
            InputValidationsForAcqra(commonValidatorVO, param, errorList, false);


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
        /*InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.add(InputFields.CARDHOLDERIP);
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList1,false);
        error = error + inputValiDatorUtils.getError(errorList1,inputMandatoryFieldsList,actionName);*/

        return error;
    }
    public  void InputValidationsForAcqra(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        ReserveField2VO reserveField2VO=commonValidatorVO.getReserveField2VO();
        String accountNumber="";
        String routingNumber="";
        if(reserveField2VO!=null)
        {
            accountNumber=reserveField2VO.getAccountNumber();
            routingNumber=reserveField2VO.getRoutingNumber();
        }

        for (InputFields input : inputList)
        {
            switch(input)
            {
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getFirstname(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getLastname(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELNO:
                    if(!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getPhone(), "OnlyNumber", 15, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCountry(), "CountryCode", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCity(), "alphanum", 30, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getCity(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getStreet(), "USPSAddress", 150, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getZipCode(), "Zip", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getZipCode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getState(), "alphanum", 40, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CARDHOLDERIP:
                    if (!(ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddressNew", 80, isOptional)))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMER_IP);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getCardHolderIpAddress(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getEmail(), "Email", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }
}

package com.payment.ReitumuBank.core;

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
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 2/10/15
 * Time: 8:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReitumuBankGatewayInputValidator extends CommonInputValidator
{
    private static Logger log = new Logger(ReitumuBankGatewayInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ReitumuBankGatewayInputValidator.class.getName());

    public  String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName,String addressValidation)
    {
        log.debug("inside validateIntegrationSpecificParameters for reitumu");
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        List<InputFields> param = new ArrayList<InputFields>();

        inputMandatoryFieldsList.add(InputFields.FIRSTNAME);
        inputMandatoryFieldsList.add(InputFields.LASTNAME);
        inputMandatoryFieldsList.add(InputFields.EMAILADDR);
        inputMandatoryFieldsList.add(InputFields.STREET);
        inputMandatoryFieldsList.add(InputFields.CITY);
        inputMandatoryFieldsList.add(InputFields.STATE);
        inputMandatoryFieldsList.add(InputFields.ZIP);
        inputMandatoryFieldsList.add(InputFields.TELNO);
        inputMandatoryFieldsList.add(InputFields.CARDHOLDERIP);
        param.add(InputFields.COUNTRYCODE);
        if("Y".equalsIgnoreCase(addressValidation))
        {
            ValidationErrorList errorList = new ValidationErrorList();
            ValidationErrorList errorList1 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList,false);
            error = error + inputValiDatorUtils.getError(errorList,inputMandatoryFieldsList,actionName);

            inputValidationsForReitumu(commonValidatorVO, param, errorList1, false);

            String EOL = "<BR>";
            if (!errorList1.isEmpty())
            {
                for (InputFields inputFields : param)
                {
                    if (errorList1.getError(inputFields.toString()) != null)
                    {
                        log.debug(errorList1.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList1.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList1.getError(inputFields.toString()).getMessage() + EOL;
                    }
                }
            }
        }
        return error;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public  void inputValidationsForReitumu(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        log.debug("inside reitumubankinputvalidator");
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        for (InputFields input : inputList)

        {
            switch(input)
            {
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
            }
        }
    }




}

package com.payment.opx;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.payment.validators.InputFields;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 12/11/2015.
 */
public class OPXInputValidator
{
    private static Logger log = new Logger(OPXInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(OPXInputValidator.class.getName());
    public  String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName,String addressValidation)
    {
        String error="";
        List<InputFields> param=new ArrayList<InputFields>();

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
            inputValidationsForOPX(commonValidatorVO, param, errorList, false);

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
        return error;
    }
    public  void inputValidationsForOPX(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        for (InputFields input : inputList)
        {
            switch(input)
            {
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getFirstname(), "alphanum", 50, isOptional))
                        validationErrorList.addError(input.toString(),new ValidationException("Invalid FirstName","Invalid FirstName :::"+genericAddressDetailsVO.getFirstname()));
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getLastname(), "alphanum", 50, isOptional))
                        validationErrorList.addError(input.toString(),new ValidationException("Invalid LastName","Invalid LastName :::"+genericAddressDetailsVO.getLastname()));
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getPhone(), "OnlyNumber", 15, isOptional))
                        validationErrorList.addError(input.toString(),new ValidationException("Invalid Telephone Number","Invalid Telephone Number :::"+genericAddressDetailsVO.getPhone()));
                    break;
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCountry(), "StrictString", 3, isOptional))
                        validationErrorList.addError(input.toString(),new ValidationException("Invalid CountryCode","Invalid CountryCode :::"+genericAddressDetailsVO.getCountry()));
                    break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCity(), "alphanum", 30, isOptional))
                        validationErrorList.addError(input.toString(),new ValidationException("Invalid City","Invalid City :::"+genericAddressDetailsVO.getCity()));
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getStreet(), "alphanum", 150, isOptional))
                        validationErrorList.addError(input.toString(),new ValidationException("Invalid Address/Street","Invalid Address/Street :::"+genericAddressDetailsVO.getStreet()));
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getZipCode(), "alphanum", 10, isOptional))
                        validationErrorList.addError(input.toString(),new ValidationException("Invalid Zip","Invalid Zip :::"+genericAddressDetailsVO.getZipCode()));
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getState(), "alphanum", 40, isOptional))
                        validationErrorList.addError(input.toString(),new ValidationException("Invalid State","Invalid State :::"+genericAddressDetailsVO.getState()));
                    break;
            }
        }
    }
}

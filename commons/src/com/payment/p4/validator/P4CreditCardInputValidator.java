package com.payment.p4.validator;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 23/10/2015.
 */
public class P4CreditCardInputValidator extends CommonInputValidator
{
    private static Logger logger= new Logger(P4CreditCardInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(P4CreditCardInputValidator.class.getName());
    private Functions functions = new Functions();


    private void InputValidationsforP4(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        for(InputFields inputFields1 : inputFields)
        {
            switch (inputFields1)
            {
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getFirstname(), "alphanum", 50, isOptional))
                        validationErrorList.addError(inputFields1.toString(),new ValidationException("Invalid FirstName","Invalid FirstName :::"+genericAddressDetailsVO.getFirstname()));
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getLastname(), "alphanum", 50, isOptional))
                        validationErrorList.addError(inputFields1.toString(),new ValidationException("Invalid LastName","Invalid LastName :::"+genericAddressDetailsVO.getLastname()));
                    break;
                case AMOUNT:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericTransDetailsVO.getAmount(), "AmountStr", 12, isOptional))
                        validationErrorList.addError(inputFields1.toString(),new ValidationException("Invalid Amount", "Invalid Amount ::::"+genericTransDetailsVO.getAmount()));
                    break;
                case CITY:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericAddressDetailsVO.getCity(), "StrictString", 30, isOptional))
                        validationErrorList.addError(inputFields1.toString(),new ValidationException("Invalid City", "Invalid City:::"+genericAddressDetailsVO.getCity()));
                    break;
                case STATE:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericAddressDetailsVO.getState(), "Address", 30, isOptional))
                        validationErrorList.addError(inputFields1.toString(),new ValidationException("Invalid State", "Invalid State::::"+genericAddressDetailsVO.getState()));
                    break;
                case TELNO:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericAddressDetailsVO.getTelnocc(), "Phone", 15, isOptional))
                        validationErrorList.addError(inputFields1.toString(),new ValidationException("Invalid TelNo", "Invalid TelNo::::"+genericAddressDetailsVO.getTelnocc()));
                    break;
                case STREET:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericAddressDetailsVO.getStreet(), "Address", 30, isOptional))
                        validationErrorList.addError(inputFields1.toString(),new ValidationException("Invalid Street", "Invalid Street::::"+genericAddressDetailsVO.getStreet()));
                    break;
                case COUNTRY:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericAddressDetailsVO.getCountry(), "alphanum", 2, isOptional))
                        validationErrorList.addError(inputFields1.toString(),new ValidationException("Invalid Country", "Invalid Country::::"+genericAddressDetailsVO.getCountry()));
                    break;
                case ZIP:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericAddressDetailsVO.getZipCode(), "Zip", 10, isOptional))
                        validationErrorList.addError(inputFields1.toString(),new ValidationException("Invalid Zip", "Invalid Zip::::"+genericAddressDetailsVO.getZipCode()));
                    break;
            }
        }
    }

    @Override
    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String action, String addressValidation)
    {
        logger.debug("P4 CREDIT SPECIFIC VALIDATION");
        String error="";

        String EOL ="<BR>";

        List<InputFields> parameter = new ArrayList<InputFields>();

        parameter.add(InputFields.FIRSTNAME);
        parameter.add(InputFields.LASTNAME);
        parameter.add(InputFields.AMOUNT);
        parameter.add(InputFields.TELNO);


        ValidationErrorList errorList = new ValidationErrorList();

        InputValidationsforP4(commonValidatorVO, parameter, errorList, false);


        if(!errorList.isEmpty())
        {
            for(InputFields inputFields : parameter)
            {
                if(errorList.getError(inputFields.toString()) !=null)
                {
                    error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;

                }
            }
        }

        parameter=new ArrayList<InputFields>();

        parameter.add(InputFields.CITY);
        parameter.add(InputFields.STATE);
        parameter.add(InputFields.STREET);
        parameter.add(InputFields.COUNTRY);
        parameter.add(InputFields.ZIP);

        if("Y".equalsIgnoreCase(addressValidation))
        {
            InputValidationsforP4(commonValidatorVO, parameter, errorList, false);
        }
        else
        {
            InputValidationsforP4(commonValidatorVO, parameter, errorList, true);
        }



        if(!errorList.isEmpty())
        {
            for(InputFields inputFields : parameter)
            {
                if(errorList.getError(inputFields.toString()) !=null)
                {
                    error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;

                }
            }
        }


        return error;
    }
}

package com.payment.clearsettle;

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
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sneha on 1/19/2017.
 */
public class ClearSettleInputValidator extends CommonInputValidator
{
    private static TransactionLogger transactionLogger = new TransactionLogger(ClearSettleInputValidator.class.getName());

    public  String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName, String addressValidation)
    {
        transactionLogger.error("inside validateIntegrationSpecificParameters for ClearSettle");
        String error = "";
        ValidationErrorList validationErrorList = new ValidationErrorList();
        List<InputFields> mandParam = new ArrayList<InputFields>();

        mandParam.add(InputFields.BIRTHDATE);
        mandParam.add(InputFields.CARDHOLDERIP);
        mandParam.add(InputFields.STREET);
        mandParam.add(InputFields.CITY);
        mandParam.add(InputFields.COUNTRY);
        mandParam.add(InputFields.STATE);
        mandParam.add(InputFields.ZIP);

        InputValidator inputValidator = new InputValidator();
        inputValidator.InputValidations(commonValidatorVO, mandParam, validationErrorList, false);
        String EOL = "<BR>";
        if (!validationErrorList.isEmpty())
        {
            for (InputFields inputFields : mandParam)
            {
                if (validationErrorList.getError(inputFields.toString()) != null)
                {
                    transactionLogger.error(validationErrorList.getError(inputFields.toString()).getLogMessage());
                    error = error + validationErrorList.getError(inputFields.toString()).getMessage() + EOL;
                }
            }
        }
        error=error+validateCountryCode(commonValidatorVO);
        return error;
    }

    public String  validateCountryCode(CommonValidatorVO commonValidatorVO)
    {
        String error="";
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        if (!ESAPI.validator().isValidInput("country", genericAddressDetailsVO.getCountry(), "StrictString",3,false))
        {
            error = "Invalid country code,country code should not be numeric value.";
        }
        return error;
    }
}

package com.payment.apco.core;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Roshan on 2/25/2017.
 */
public class ApcoPayInputValidator extends CommonInputValidator
{
    private static Logger log = new Logger(ApcoPayInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ApcoPayInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        log.debug("inside validateIntegrationSpecificParameters for apcopay");
        String error = "";

        if ("39".equals(commonValidatorVO.getCardType()))
        {
            InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
            InputValidator inputValidator = new InputValidator();
            List<InputFields> param = new ArrayList<InputFields>();

            param.add(InputFields.FIRSTNAME);
            param.add(InputFields.LASTNAME);
            param.add(InputFields.BIRTHDATE);
            param.add(InputFields.COUNTRYCODE);

            if ("Y".equalsIgnoreCase(addressValidation))
            {
                ValidationErrorList errorList = new ValidationErrorList();
                inputValidator.InputValidations(commonValidatorVO, param, errorList, false);
                error = error + inputValiDatorUtils.getError(errorList, param, actionName);
            }
            return error;
        }
        else
        {
            InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
            InputValidator inputValidator = new InputValidator();
            List<InputFields> param = new ArrayList<InputFields>();

            param.add(InputFields.EMAILADDR);
            param.add(InputFields.TELNO);
            param.add(InputFields.TELCC);
            param.add(InputFields.COUNTRYCODE);
            param.add(InputFields.CITY);
            param.add(InputFields.STATE);
            param.add(InputFields.ZIP);
            param.add(InputFields.STREET);

            if ("Y".equalsIgnoreCase(addressValidation))
            {
                ValidationErrorList errorList = new ValidationErrorList();
                inputValidator.InputValidations(commonValidatorVO, param, errorList, false);
                error = error + inputValiDatorUtils.getError(errorList, param, actionName);
            }
            return error;
        }
    }
}

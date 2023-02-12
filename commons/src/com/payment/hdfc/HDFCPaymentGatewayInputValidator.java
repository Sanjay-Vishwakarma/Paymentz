package com.payment.hdfc;

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
 * Created by Admin on 2022-03-08.
 */
public class HDFCPaymentGatewayInputValidator extends CommonInputValidator
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(HDFCPaymentGatewayInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator           = new InputValidator();
        String error                            = "";
        List<InputFields> mandatoryParams       = new ArrayList<InputFields>();

        mandatoryParams.add(InputFields.FIRSTNAME);
        mandatoryParams.add(InputFields.LASTNAME);
        mandatoryParams.add(InputFields.CARDHOLDER);

        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO, mandatoryParams, errorList1, false);
        error = error + inputValiDatorUtils.getError(errorList1, mandatoryParams, actionName);

        transactionLogger.error("error  size------------>"+commonValidatorVO.getErrorCodeListVO().getListOfError().size());
        transactionLogger.error("error  HDFCPaymentGatewayInputValidator------------>"+error+" "+addressValidation);

        return error;
    }
}

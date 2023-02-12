package com.payment.cajarural;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
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
 * Created by Admin on 12/23/2021.
 */
public class CajaRuralInputValidator extends CommonInputValidator
{
    TransactionLogger transactionLogger= new TransactionLogger(CajaRuralInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionname, String addressValidation)
    {

        transactionLogger.error("inside CajaRuralInputValidator ----- " + actionname);
        String error="";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator=new InputValidator();
        List<InputFields> mandatoryParameter = new ArrayList();

        mandatoryParameter.add(InputFields.CURRENCY);

        transactionLogger.error("addressValidation ==== " + addressValidation);

        ValidationErrorList errorList2 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO, mandatoryParameter, errorList2, false);
        error = error + inputValiDatorUtils.getError(errorList2, mandatoryParameter, actionname);
        transactionLogger.error("mandatory parameter error ==== " + error);

        return error;
    }
}


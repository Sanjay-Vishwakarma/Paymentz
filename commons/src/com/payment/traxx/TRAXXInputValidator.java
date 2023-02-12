package com.payment.traxx;

import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2022-01-24.
 */
public class TRAXXInputValidator extends CommonInputValidator
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(TRAXXInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        ErrorCodeUtils errorCodeUtils           = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO                 = new ErrorCodeVO();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator           = new InputValidator();
        String error                            = "";
        List<InputFields> param                 = new ArrayList<InputFields>();

        List<InputFields> parameter = new ArrayList();

        parameter.add(InputFields.EMAILADDR);
        parameter.add(InputFields.COUNTRYCODE);
        parameter.add(InputFields.CITY);
        parameter.add(InputFields.STATE);
        parameter.add(InputFields.ZIP);
        parameter.add(InputFields.STREET);
        parameter.add(InputFields.TELCC);
        parameter.add(InputFields.TELNO);
        parameter.add(InputFields.CARDHOLDERIP);
        parameter.add(InputFields.FIRSTNAME);
        parameter.add(InputFields.LASTNAME);


        if("Y".equals(addressValidation))
        {
            ValidationErrorList errorList1 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, param, errorList1, false);
            error = error + inputValiDatorUtils.getError(errorList1, param, actionName);
        }else{
            ValidationErrorList errorList1 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, param, errorList1, true);
            error = error + inputValiDatorUtils.getError(errorList1, param, actionName);
        }



        transactionLogger.error("error  size------------>"+commonValidatorVO.getErrorCodeListVO().getListOfError().size());
        transactionLogger.error("error  TRAXXInputValidator------------>"+error+" "+addressValidation);

        return error;
    }
}

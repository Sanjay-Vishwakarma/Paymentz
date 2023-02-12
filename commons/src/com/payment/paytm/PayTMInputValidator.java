package com.payment.paytm;

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
 * Created by Admin on 3/23/2021.
 */
public class PayTMInputValidator  extends CommonInputValidator
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(PayTMInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        ErrorCodeUtils errorCodeUtils           = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO                 = new ErrorCodeVO();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator           = new InputValidator();
        String error                            = "";
        List<InputFields> param                 = new ArrayList<InputFields>();

        if("UPI".equalsIgnoreCase(commonValidatorVO.getPaymentBrand())){
            param.add(InputFields.VPA_ADDRESS);
        }

        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO, param, errorList1, true);
        error = error + inputValiDatorUtils.getError(errorList1, param, actionName);

        transactionLogger.error("error  size------------>"+commonValidatorVO.getErrorCodeListVO().getListOfError().size());
        transactionLogger.error("error  PayUInputValidator------------>"+error+" "+addressValidation);

        return error;
    }
}

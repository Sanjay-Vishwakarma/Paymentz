package com.payment.smartfastpay;

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
public class SmartFastPayInputValidator extends CommonInputValidator
{
    private static TransactionLogger transactionLogger  = new TransactionLogger(SmartFastPayInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        ErrorCodeUtils errorCodeUtils           = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO                 = new ErrorCodeVO();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator           = new InputValidator();
        String error                            = "";
        List<InputFields> addressParameter      = new ArrayList();

        List<InputFields> parameter = new ArrayList();
        parameter.add(InputFields.FIRSTNAME);
        parameter.add(InputFields.LASTNAME);
        parameter.add(InputFields.EMAILADDR);

        transactionLogger.error("commonValidatorVO.getCardType  "+commonValidatorVO.getCardType());

        String payment_brand = SmartFastPayPaymentGatewayUtils.getPaymentBrand(commonValidatorVO.getCardType());

        addressParameter.add(InputFields.CITY);
        addressParameter.add(InputFields.ZIP);
        addressParameter.add(InputFields.STREET);
        addressParameter.add(InputFields.STATE);

        ValidationErrorList errorList1 = new ValidationErrorList();
        if("boleto".equalsIgnoreCase(payment_brand) && "Y".equalsIgnoreCase(addressValidation)){
            inputValidator.InputValidations(commonValidatorVO, addressParameter, errorList1, false);
            error = error + inputValiDatorUtils.getError(errorList1, addressParameter, actionName);
        }else if("boleto".equalsIgnoreCase(payment_brand)) {
            inputValidator.InputValidations(commonValidatorVO, addressParameter, errorList1, true);
            error = error + inputValiDatorUtils.getError(errorList1, addressParameter, actionName);
        }


        if("Y".equalsIgnoreCase(addressValidation)){
            inputValidator.InputValidations(commonValidatorVO, parameter, errorList1, false);
            error = error + inputValiDatorUtils.getError(errorList1, parameter, actionName);
        }else  {
            inputValidator.InputValidations(commonValidatorVO, parameter, errorList1, true);
            error = error + inputValiDatorUtils.getError(errorList1, parameter, actionName);
        }


        transactionLogger.error("error  size------------>"+commonValidatorVO.getErrorCodeListVO().getListOfError().size());
        transactionLogger.error("error  SmartFastPayInputValidator------------>"+error+" "+addressValidation);

        return error;
    }
}

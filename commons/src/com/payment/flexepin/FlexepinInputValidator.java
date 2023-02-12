package com.payment.flexepin;

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
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 2022-06-30.
 */
public class FlexepinInputValidator extends CommonInputValidator
{

    private static TransactionLogger transactionLogger  = new TransactionLogger(FlexepinInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        ErrorCodeUtils errorCodeUtils           = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO                 = new ErrorCodeVO();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator           = new InputValidator();
        String error                            = "";
        List<InputFields> mandatoryParams       = new ArrayList<InputFields>();

        mandatoryParams.add(InputFields.VOUCHER_NUMBER);
        mandatoryParams.add(InputFields.CARDHOLDERIP);

        ValidationErrorList errorList1 = new ValidationErrorList();
        InputValidations(commonValidatorVO, mandatoryParams, errorList1, false);
        error = error + inputValiDatorUtils.getError(errorList1, mandatoryParams, actionName);

        transactionLogger.error("error  size------------>"+commonValidatorVO.getErrorCodeListVO().getListOfError().size());
        transactionLogger.error("error  FlexepinInputValidator ------------>"+error+" "+addressValidation);

        return error;
    }

    private void InputValidations(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        Functions functions     = new Functions();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

        for (InputFields inputFields1 : inputFields) {
            switch (inputFields1)
            {
                case VOUCHER_NUMBER:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), commonValidatorVO.getCardDetailsVO().getVoucherNumber(), "Number", 16, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMERID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CUSTOMERID);
                        errorCodeVO.setErrorDescription("Invalid Voucher Code, It accepts only Number with max length 16");
                        errorCodeVO.setApiDescription("Invalid Voucher Code, It accepts only Number with max length 16");
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_CUSTOMERID, ErrorMessages.INVALID_CUSTOMERID+ ":::" + commonValidatorVO.getCardDetailsVO().getVoucherNumber(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CARDHOLDERIP:
                    if (!(ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddress", 50, isOptional) || ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddressNew", 80, isOptional)) || functions.hasHTMLTags(genericAddressDetailsVO.getCardHolderIpAddress()))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMER_IP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CUSTOMER_IP);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException("Invalid IP Address", "Invalid IP Address :::" + genericAddressDetailsVO.getIp(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }
    
}

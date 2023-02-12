package com.payment.Wirecardnew;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 08-02-2017.
 */
public class WirecardNinputValidator extends CommonInputValidator
{
    private static Logger logger = new Logger(WirecardNinputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(WirecardNinputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        String error = "";
        List<InputFields> param = new ArrayList<InputFields>();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();

        param.add(InputFields.CVV);
        //param.add(InputFields.REDIRECT_URL);
        param.add(InputFields.FIRSTNAME_WC);
        param.add(InputFields.LASTNAME_WC);


        ValidationErrorList errorList = new ValidationErrorList();

        if ("Y".equalsIgnoreCase(addressValidation))
        {
            inputValidationForWirecard(commonValidatorVO, param, errorList, false);
            error = error + inputValiDatorUtils.getError(errorList,param,actionName);

            String EOL = "<BR>";
            if (!errorList.isEmpty())
            {
                for (InputFields inputFields : param)
                {
                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                        //System.out.println("error in specific----"+error);
                    }
                }
            }
        }

        return error;
    }

    public void inputValidationForWirecard(CommonValidatorVO commonValidatorVO,List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO= commonValidatorVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        for (InputFields input : inputList)
        {
            switch (input)
            {
                case CVV:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericCardDetailsVO.getcVV(), "OnlyNumber", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV_WIRECARD);
                        validationErrorList.addError(input.toString(), new PZValidationException("", ""+ ":::" + genericCardDetailsVO.getcVV(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case REDIRECT_URL:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericTransDetailsVO.getRedirectUrl(), "URL", 100, isOptional) || genericTransDetailsVO.getRedirectUrl().length() > 100)
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REDIRECT_URL);
                        //errorCodeVO.setErrorReason(ErrorMessages.INVALID_REDIRECT_URL);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_REDIRECT_URL, ErrorMessages.INVALID_REDIRECT_URL + ":::" + genericTransDetailsVO.getRedirectUrl(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case FIRSTNAME_WC:

                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getFirstname(), "UserName", 32, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME_WC);
                        //System.out.println("error code for first name-----"+genericAddressDetailsVO.getFirstname());
                        //errorCodeVO.setErrorReason(ErrorMessages.INVALID_FIRST_NAME);
                        validationErrorList.addError(input.toString(), new PZValidationException("", "" + ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME_WC:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getLastname(), "UserName", 32, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME_WC);
                        //System.out.println("error code for first name-----"+genericAddressDetailsVO.getLastname());
                        //errorCodeVO.setErrorReason(ErrorMessages.INVALID_LAST_NAME);
                        validationErrorList.addError(input.toString(), new PZValidationException("", "" + ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;



            }
        }
    }
}

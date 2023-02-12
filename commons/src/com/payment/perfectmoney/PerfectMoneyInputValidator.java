package com.payment.perfectmoney;

import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
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
 * Created by Nikita on 2/25/2017.
 */
public class PerfectMoneyInputValidator extends CommonInputValidator
{
    private static Logger log = new Logger(PerfectMoneyInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PerfectMoneyInputValidator.class.getName());

    public  String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName,String addressValidation)
    {
        log.debug("inside validateIntegrationSpecificParameters for PerfectMoney");
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        List<InputFields> param = new ArrayList<InputFields>();

        param.add(InputFields.EMAILADDR);
        /*param.add(InputFields.TELNO);
        param.add(InputFields.TELCC);*/
        //param.add(InputFields.COUNTRYCODE);

        if("Y".equalsIgnoreCase(addressValidation))
        {
            ValidationErrorList errorList = new ValidationErrorList();
            ValidationErrorList errorList1 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO,param,errorList,false);
            error = error + inputValiDatorUtils.getError(errorList,param,actionName);

            inputValidationsForPerfectMoney(commonValidatorVO, param, errorList1, false);

            String EOL = "<BR>";
            if (!errorList1.isEmpty())
            {
                for (InputFields inputFields : param)
                {
                    if (errorList1.getError(inputFields.toString()) != null)
                    {
                        log.debug(errorList1.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList1.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList1.getError(inputFields.toString()).getMessage() + EOL;
                    }
                }
            }
        }
        return error;
    }

    public  void inputValidationsForPerfectMoney(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        log.debug("Inside PerfectMoneyInputValidator::::");
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        for (InputFields input : inputList)

        {
            switch(input)
            {
                /*case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getCountry(), "StrictString", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorDescription(), errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;*/
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getEmail(), "Email", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(input.toString(), new PZValidationException("", "" + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getPhone(), "Phone", 15, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(input.toString(), new PZValidationException("", "" + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getTelnocc(), "alphanum", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNOCC);
                        validationErrorList.addError(input.toString(), new PZValidationException("", "" + ":::" + genericAddressDetailsVO.getTelnocc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }
}

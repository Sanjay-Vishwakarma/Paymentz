package com.payment.vouchermoney;

import com.directi.pg.Logger;
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
 * Created by Nikita on 2/25/2017.
 */
public class VoucherMoneyInputValidator extends CommonInputValidator
{
    private static Logger log = new Logger(VoucherMoneyInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(VoucherMoneyInputValidator.class.getName());

    public  String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName,String addressValidation)
    {
        log.debug("inside validateIntegrationSpecificParameters for PerfectVOucher");
        String error="";
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        List<InputFields> param = new ArrayList<InputFields>();
        List<InputFields> mParam = new ArrayList<InputFields>();

        param.add(InputFields.EMAILADDR);
        param.add(InputFields.TELNO);
        param.add(InputFields.TELCC);
        mParam.add(InputFields.CUSTOMERID);
        //param.add(InputFields.COUNTRYCODE);

        ValidationErrorList errorList = new ValidationErrorList();
        //ValidationErrorList errorList1 = new ValidationErrorList();

        log.debug("address validation in VM Validator----" + addressValidation);
        if("Y".equalsIgnoreCase(addressValidation))
        {

            //inputValidator.InputValidations(commonValidatorVO,param,errorList,false);
            //error = error + inputValiDatorUtils.getError(errorList,param,actionName);

            inputValidationsForVoucherMoney(commonValidatorVO, param, errorList, false);

            String EOL = "<BR>";
            if (!errorList.isEmpty())
            {
                for (InputFields inputFields : param)
                {
                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                    }
                }
            }
        }
        /*else
        {
            inputValidationsForVoucherMoney(commonValidatorVO, param, errorList, true);

            String EOL = "<BR>";
            if (!errorList.isEmpty())
            {
                for (InputFields inputFields : param)
                {
                    if (errorList.getError(inputFields.toString()) != null)
                    {
                        log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                    }
                }
            }
        }*/

        //ValidationErrorList errorList = new ValidationErrorList();
        inputValidationsForVoucherMoney(commonValidatorVO, mParam, errorList, false);

        String EOL = "<BR>";
        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : mParam)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                }
            }
        }
        //System.out.println("error VM---"+error);
        return error;
    }

    public  void inputValidationsForVoucherMoney(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        log.debug("Inside VoucherMoneyInputValidator::::");
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        for (InputFields input : inputList)

        {
            switch(input)
            {
                case CUSTOMERID:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getCustomerId(), "Numbers", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMERID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CUSTOMERID);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CUSTOMERID, ErrorMessages.INVALID_CUSTOMERID + ":::" + commonValidatorVO.getCustomerId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                /*case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getEmail(), "Email", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorDescription(), errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;*/
                /*case TELNO:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getPhone(), "Phone", 15, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorDescription(), errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELCC:
                    if (!ESAPI.validator().isValidInput(input.toString(), genericAddressDetailsVO.getTelnocc(), "alphanum", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNOCC);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorDescription(), errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getTelnocc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;*/
            }
        }
    }
}

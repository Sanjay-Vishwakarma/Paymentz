package com.payment.payonOppwa;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
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
 * Created by Admin on 8/1/2020.
 */
public class PayOnOppwaInputValidator extends CommonInputValidator
{
    TransactionLogger transactionLogger         = new TransactionLogger(PayOnOppwaInputValidator.class.getName());

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionname,String addressValidation)
    {
        String error                            = "";
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        List<InputFields> parameter             = new ArrayList();
        String EOL                              = "<BR>";
        parameter.add(InputFields.FIRSTNAME);
        parameter.add(InputFields.LASTNAME);
        parameter.add(InputFields.CURRENCY);
    //    parameter.add(InputFields.TMPL_CURRENCY);
//        parameter.add(InputFields.STREET);


        if("Y".equals(addressValidation))
        {
            ValidationErrorList errorList       = new ValidationErrorList();
            ValidationErrorList errorList1      = new ValidationErrorList();
            InputValidationsforPayOnOppwa(commonValidatorVO, parameter, errorList, false);
            error       = error + inputValiDatorUtils.getError(errorList, parameter, actionname);


            if (!errorList1.isEmpty())
            {
                for (InputFields inputFields : parameter)
                {
                    if (errorList1.getError(inputFields.toString()) != null)
                    {
                        transactionLogger.error(errorList1.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList1.getError(inputFields.toString()).getMessage() + EOL;
                    }
                }
            }


        }
        else
        {
            ValidationErrorList errorList = new ValidationErrorList();
            ValidationErrorList errorList1 = new ValidationErrorList();
            InputValidationsforPayOnOppwa(commonValidatorVO, parameter, errorList, true);
            error = error + inputValiDatorUtils.getError(errorList, parameter, actionname);

            if (!errorList1.isEmpty())
            {
                for (InputFields inputFields : parameter)
                {
                    if (errorList1.getError(inputFields.toString()) != null)
                    {
                        transactionLogger.error(errorList1.getError(inputFields.toString()).getLogMessage());
                        error = error + errorList1.getError(inputFields.toString()).getMessage() + EOL;
                    }
                }
            }
        }

        return error;
    }
    private void InputValidationsforPayOnOppwa(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO                         = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils                   = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO     = commonValidatorVO.getTransDetailsVO();
      //  CommTransactionDetailsVO transDetailsVO = (CommTransactionDetailsVO) commonValidatorVO.getTransDetailsVO();
        for (InputFields inputFields1 : inputFields)
        {
            switch (inputFields1)
            {
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getFirstname(), "AccentUsername", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getLastname(), "AccentUsername", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

                case CURRENCY:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getCurrency(),"StrictString",3,isOptional)
                            )
                    {//String.valueOf(!genericTransDetailsVO.getCurrency().isEmpty())

                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CURRENCY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CURRENCY);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_CURRENCY);
                        //validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericTransDetailsVO.getCurrency(), errorCodeVO));
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_CURRENCY, ErrorMessages.INVALID_CURRENCY + ":::" + genericTransDetailsVO.getCurrency(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }
                    break;
          /*      case TMPL_CURRENCY:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getTmpl_currency(), "StrictString", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TMPL_CURRENCY);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_TMPL_CURRENCY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TMPL_CURRENCY);
                        //validationErrorList.addError(input.toString(), new ValidationException("Invalid Template Currency", "Invalid Template Currency :::" + genericAddressDetailsVO.getTmpl_currency(),errorCodeVO));
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_TMPL_CURRENCY, ErrorMessages.INVALID_TMPL_CURRENCY + ":::" + genericAddressDetailsVO.getTmpl_currency(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);

                    }*/
            /*    case STREET:
                    if (!ESAPI.validator().isValidInput(inputFields.toString(), genericAddressDetailsVO.getStreet(), "StrictString", 150, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STREET);
                        errorCodeVO.setErrorReason(ErrorMessages.INVALID_STREET);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_STREET, ErrorMessages.INVALID_STREET + ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;*/

            }
        }
    }
}

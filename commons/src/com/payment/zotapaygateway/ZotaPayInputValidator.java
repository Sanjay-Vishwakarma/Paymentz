package com.payment.zotapaygateway;

import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.europay.core.message.ErrorCode;
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
 * Created by Admin on 3/15/2021.
 */
// city,state,zip code,email address
public class ZotaPayInputValidator extends CommonInputValidator
{
    TransactionLogger transactionLogger = new TransactionLogger(ZotaPayInputValidator.class.getName());
   public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionname,String addressValidation)
   {
       transactionLogger.error("Inside ZotaPayInputValidator");
       InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
       String error="";
       List<InputFields> parameter = new ArrayList<>();
       List<InputFields>mandatoryParameter=new ArrayList<>();
       parameter.add(InputFields.CITY);
       parameter.add(InputFields.STATE);
       parameter.add(InputFields.ZIP);
       parameter.add(InputFields.EMAILADDR);

       if("Y".equalsIgnoreCase(addressValidation))
       {
           ValidationErrorList errorList = new ValidationErrorList();
           InputValidationsforZotapay(commonValidatorVO,parameter,errorList,false);
           error=error+inputValiDatorUtils.getError(errorList,parameter,actionname);
       }
       else
       {
           ValidationErrorList errorList= new ValidationErrorList();
           InputValidationsforZotapay(commonValidatorVO,parameter,errorList,true);
           error=error+inputValiDatorUtils.getError(errorList,parameter,actionname);

       }
       ValidationErrorList errorList = new ValidationErrorList();
       InputValidationsforZotapay(commonValidatorVO,parameter,errorList,false);
       error=error+inputValiDatorUtils.getError(errorList,parameter,actionname);
       return error;
   }

    private void InputValidationsforZotapay(CommonValidatorVO commonValidatorVO,List<InputFields> inputFields,ValidationErrorList validationErrorList,boolean isOptional)
    {
        transactionLogger.error("Inside ZotaPayInputValidator");
        ErrorCodeVO errorCodeVO =new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO=commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO=commonValidatorVO.getTransDetailsVO();
        for(InputFields inputFields1:inputFields)
        {
            switch (inputFields1)
            {
                case CITY:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericAddressDetailsVO.getCity(),"City",50,isOptional))
                    {
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(inputFields1.toString(),new PZValidationException(ErrorMessages.INVALID_CITY,ErrorMessages.INVALID_CITY+":::"+genericAddressDetailsVO.getCity(),errorCodeVO));
                        if(commonValidatorVO.getErrorCodeListVO()!=null)
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ZIP:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getZipCode(), "Zip", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getZipCode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getEmail(), "Email", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }
}

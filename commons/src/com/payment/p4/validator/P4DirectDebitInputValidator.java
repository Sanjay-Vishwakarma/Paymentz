package com.payment.p4.validator;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.TokenManager;
import com.manager.vo.MerchantDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
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
 * Created by admin on 1/12/2016.
 */
public class P4DirectDebitInputValidator extends CommonInputValidator
{
    private Functions functions = new Functions();
    private static Logger logger= new Logger(P4InputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(P4InputValidator.class.getName());


    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName,String addressValidation)
    {
        logger.debug("Inside P4DirectDebitInputValidator :::");
        String error="";
        String isMandate = "";
        isMandate = commonValidatorVO.getCardDetailsVO().getIsMandate();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();

        TokenManager tokenManager = new TokenManager();

        List<InputFields> conditionalParameter=new ArrayList<InputFields>();
        List<InputFields> mandatoryParameter=new ArrayList<InputFields>();
        List<InputFields> addressParameter=new ArrayList<InputFields>();
        InputValidator inputValidator = new InputValidator();

        mandatoryParameter.add(InputFields.ORDERID);
        mandatoryParameter.add(InputFields.ORDERDESCRIPTION);
        mandatoryParameter.add(InputFields.FIRSTNAME);
        mandatoryParameter.add(InputFields.LASTNAME);
        mandatoryParameter.add(InputFields.AMOUNT);

        addressParameter.add(InputFields.EMAILADDR);
        addressParameter.add(InputFields.CITY);
        addressParameter.add(InputFields.STATE);
        addressParameter.add(InputFields.ZIP);
        addressParameter.add(InputFields.STREET);
        addressParameter.add(InputFields.TELNO);
        addressParameter.add(InputFields.COUNTRY);
        addressParameter.add(InputFields.COUNTRYCODE);

        if(commonValidatorVO!=null && commonValidatorVO.getCardDetailsVO()!=null && isMandate.equals("tc"))
        {
            conditionalParameter.add(InputFields.MANDATEID);
        }
        else
        {
            conditionalParameter.add(InputFields.IBAN);
            conditionalParameter.add(InputFields.BIC);
        }

        ValidationErrorList validationErrorList1 = new ValidationErrorList();
        InputValidationsforP4(commonValidatorVO, conditionalParameter, validationErrorList1, false);
        InputValiDatorUtils inputValiDatorUtils1 = new InputValiDatorUtils();
        if(!validationErrorList1.isEmpty())
        {
            for (InputFields inputFields : conditionalParameter)
            {
                if (validationErrorList1.getError(inputFields.toString()) != null)
                {
                    error = error + validationErrorList1.getError(inputFields.toString()).getMessage();
                }
            }
        }

        if("Y".equalsIgnoreCase(addressValidation))
        {
            ValidationErrorList validationErrorList = new ValidationErrorList();
            InputValidationsforP4(commonValidatorVO, addressParameter, validationErrorList, false);
            InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
            if(!validationErrorList.isEmpty())
            {
                for (InputFields inputFields : addressParameter)
                {
                    if (validationErrorList.getError(inputFields.toString()) != null)
                    {
                        error = error + validationErrorList.getError(inputFields.toString()).getMessage();
                    }
                }
            }
        }
        else
        {
            ValidationErrorList validationErrorList = new ValidationErrorList();
            InputValidationsforP4(commonValidatorVO, addressParameter, validationErrorList, true);

            if(!validationErrorList.isEmpty())
            {
                for (InputFields inputFields : addressParameter)
                {
                    if (validationErrorList.getError(inputFields.toString()) != null)
                    {
                        error = error + validationErrorList.getError(inputFields.toString()).getMessage();
                    }
                }
            }
        }

        ValidationErrorList validationErrorList = new ValidationErrorList();
        InputValidationsforP4(commonValidatorVO, mandatoryParameter, validationErrorList, false);

        if(!validationErrorList.isEmpty())
        {
            for (InputFields inputFields : mandatoryParameter)
            {
                if (validationErrorList.getError(inputFields.toString()) != null)
                {
                    error = error + validationErrorList.getError(inputFields.toString()).getMessage();

                }
            }
        }
        else
        {

            MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
            String accountid = merchantDetailsVO.getAccountId();
            Functions functions = new Functions();
            String toid = merchantDetailsVO.getMemberId();

            try
            {
                if (!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length() > 10 || !ESAPI.validator().isValidInput("toid", toid, "Numbers", 20, false))
                {
                    error = ErrorMessages.INVALID_TOID;
                }
                else
                {

                    merchantDetailsVO = getMerchantConfigDetails(toid, commonValidatorVO.getTransDetailsVO().getTotype());

                    merchantDetailsVO.setAccountId(accountid);
                    commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
                }

                if(functions.isValueNull(error) && commonValidatorVO!=null && commonValidatorVO.getCardDetailsVO()!=null && functions.isValueNull(commonValidatorVO.getCardDetailsVO().getMandateId()) && !tokenManager.validateMandate(commonValidatorVO.getCardDetailsVO().getMandateId()) )
                {
                    errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_MANDATEID);
                    errorCodeVO.setErrorName(ErrorName.VALIDATION_MANDATEID);
                    error = errorCodeVO.getApiCode() + "_" + errorCodeVO.getApiDescription();
                    if (commonValidatorVO.getErrorCodeListVO() != null)
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                }

                if(functions.isValueNull(error) && commonValidatorVO!=null && commonValidatorVO.getTransDetailsVO()!=null )
                {

                }
            }
            catch (PZDBViolationException dbe)
            {
                logger.error("PZDVViolationException occur---", dbe);
                PZExceptionHandler.raiseAndHandleDBViolationException(P4DirectDebitInputValidator.class.getName(),"validateIntegrationSpecificParameters()",null,"common","Internal Error Occured. Please Contact Your Support Team.", PZDBExceptionEnum.SQL_EXCEPTION,null,"",new Throwable(dbe.getCause()),toid,null);
            }
        }
        return  error;
    }

    private void InputValidationsforP4(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO = commonValidatorVO.getCardDetailsVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();

        for(InputFields inputFields1 : inputFields)
        {
            switch (inputFields1)
            {
                case ORDERID:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getOrderId(), "SafeString", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericTransDetailsVO.getOrderId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ORDERDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getOrderDesc(), "alphanum", 100, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericTransDetailsVO.getOrderDesc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getFirstname(), "StrictString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getLastname(), "StrictString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case AMOUNT:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getAmount(), "AmountStr", 12, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_AMOUNT);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericTransDetailsVO.getAmount(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EMAILADDR:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getEmail(), "Email", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCity(), "StrictString", 30, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getCity(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getState(), "StrictString", 30, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ZIP:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericAddressDetailsVO.getZipCode(), "Zip", 10, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getZipCode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getStreet(), "Address", 30, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELNO:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), commonValidatorVO.getAddressDetailsVO().getPhone(), "Phone", 15, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + commonValidatorVO.getAddressDetailsVO().getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                /*case COUNTRY:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCountry(), "alphanum", 2, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(ErrorMessages.INVALID_COUNTRY_CODE, ErrorMessages.INVALID_COUNTRY_CODE + ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                        break;*/
                case IBAN:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericCardDetailsVO.getIBAN(), "alphanum", 25, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REJECTED_IBAN);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_REJECTED_IBAN);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericCardDetailsVO.getIBAN(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case BIC:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericCardDetailsVO.getBIC(), "alphanum", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REJECTED_BIC);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_REJECTED_BIC);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + ":::" + genericCardDetailsVO.getBIC(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case MANDATEID:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericCardDetailsVO.getMandateId(), "alphanum", 54, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_MANDATEID);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_MANDATEID);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericCardDetailsVO.getMandateId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case COUNTRYCODE:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCountry(), "CountryCode", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;

            }
        }
    }
}

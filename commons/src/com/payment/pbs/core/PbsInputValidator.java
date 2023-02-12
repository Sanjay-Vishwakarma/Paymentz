package com.payment.pbs.core;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.errors.ErrorMessages;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZValidationException;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 30/3/2016.
 */
public class PbsInputValidator extends CommonInputValidator
{
    private static Logger logger= new Logger(PbsInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PbsInputValidator.class.getName());
    private Functions functions = new Functions();

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String action,String addressValidation)
    {
        logger.debug("Inside PBSInputValidator--------->");
        transactionLogger.debug("Inside PBSInputValidator--------->");

        String error="";

        List<InputFields> conditionalParameter=new ArrayList<InputFields>();
        ValidationErrorList validationErrorList = new ValidationErrorList();

        conditionalParameter.add(InputFields.FIRSTNAME);
        conditionalParameter.add(InputFields.LASTNAME);
        conditionalParameter.add(InputFields.ORDERID);
        conditionalParameter.add(InputFields.AMOUNT);
        conditionalParameter.add(InputFields.CITY);
        conditionalParameter.add(InputFields.STATE);
        conditionalParameter.add(InputFields.TELNO);
        conditionalParameter.add(InputFields.TELCC);
        conditionalParameter.add(InputFields.STREET);
        conditionalParameter.add(InputFields.COUNTRY);
        conditionalParameter.add(InputFields.ZIP);
        //conditionalParameter.add(InputFields.ORDERDESCRIPTION);
        conditionalParameter.add(InputFields.EXPIRE_MONTH);
        conditionalParameter.add(InputFields.EXPIRE_YEAR);
        conditionalParameter.add(InputFields.CVV);
        conditionalParameter.add(InputFields.CARDHOLDERIP);

        InputValidationsforPbs(commonValidatorVO, conditionalParameter, validationErrorList, false);

        if(!validationErrorList.isEmpty())
        {
            for(ValidationException validationException:validationErrorList.errors())
            {
                error = error + validationException.getMessage()+"<BR>";
            }
        }
        else
        {

            MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
            String accountid = merchantDetailsVO.getAccountId();
            Functions functions = new Functions();
            String toid = merchantDetailsVO.getMemberId();
            GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();

            if (!functions.isValueNull(toid) || !functions.isNumericVal(toid) || toid.length() > 10 || !ESAPI.validator().isValidInput("toid", toid, "Numbers", 20, false))
            {
                error = ErrorMessages.INVALID_TOID;
            }
            else
            {
                try
                {
                    merchantDetailsVO = getMerchantConfigDetails(toid, commonValidatorVO.getTransDetailsVO().getTotype());
                }
                catch (PZDBViolationException e)
                {
                    logger.error("PZDBViolationException--->",e);
                }

                merchantDetailsVO.setAccountId(accountid);
                commonValidatorVO.setMerchantDetailsVO(merchantDetailsVO);
            }

            if(functions.isValueNull(error) && commonValidatorVO!=null && commonValidatorVO.getTransDetailsVO()!=null )
            {

            }
        }

        return  error;
    }

    private void InputValidationsforPbs(CommonValidatorVO commonValidatorVO, List<InputFields> inputFields, ValidationErrorList validationErrorList, boolean isOptional)
    {
        GenericAddressDetailsVO genericAddressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        GenericCardDetailsVO genericCardDetailsVO  = commonValidatorVO.getCardDetailsVO();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();

        for(InputFields inputFields1 : inputFields)
        {
            switch (inputFields1)
            {
                case FIRSTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getFirstname(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getFirstname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case LASTNAME:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getLastname(), "SafeString", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getLastname(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case AMOUNT:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericTransDetailsVO.getAmount(), "AmountStr", 12, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_AMOUNT);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericTransDetailsVO.getAmount(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CITY:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCity(), "SafeString", 30, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getCity(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STATE:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getState(), "SafeString", 40, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getState(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELNO:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericAddressDetailsVO.getPhone(), "Phone", 15, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getPhone(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STREET:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getStreet(), "SafeString", 150, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getStreet(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case COUNTRY:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericAddressDetailsVO.getCountry(), "CountryCode", 3, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getCountry(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ZIP:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(),genericAddressDetailsVO.getZipCode(), "Zip", 9, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getZipCode(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EMAIL:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getEmail(), "Email", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getEmail(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ORDERID:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getOrderId(), "Description", 35, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericTransDetailsVO.getOrderId(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CARDHOLDERIP:
                    if (!(ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddress", 80, isOptional) || ESAPI.validator().isValidInput(inputFields1.toString(), genericAddressDetailsVO.getCardHolderIpAddress(), "IPAddressNew", 80, isOptional)))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMER_IP);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericAddressDetailsVO.getCardHolderIpAddress(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                /*case ORDERDESCRIPTION:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getOrderDesc(), "Address", 255, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ORDER_DESCRIPTION);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericTransDetailsVO.getOrderDesc(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;*/
                case EXPIRE_MONTH:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericCardDetailsVO.getExpMonth(), "Months", 2, isOptional))



                        {

                            errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_MONTH);
                            validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericCardDetailsVO.getExpMonth(), errorCodeVO));
                            if (commonValidatorVO.getErrorCodeListVO() != null)
                                commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                        }
                    break;
                case EXPIRE_YEAR:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericCardDetailsVO.getExpYear(), "Years", 4, isOptional))
                    {

                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EXP_YEAR);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericCardDetailsVO.getExpYear(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CVV:
                    if(!ESAPI.validator().isValidInput(inputFields1.toString(), genericCardDetailsVO.getcVV(), "OnlyNumber", 4, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CVV);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericCardDetailsVO.getcVV(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case REDIRECT_URL:
                    if (!ESAPI.validator().isValidInput(inputFields1.toString(), genericTransDetailsVO.getRedirectUrl(), "URL", 100, isOptional))
                    {

                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_REDIRECT_URL);
                        validationErrorList.addError(inputFields1.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+ ":::" + genericTransDetailsVO.getRedirectUrl(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;


            }
        }
    }
}

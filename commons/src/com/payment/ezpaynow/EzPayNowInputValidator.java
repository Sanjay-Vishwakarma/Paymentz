package com.payment.ezpaynow;

import com.directi.pg.Functions;
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
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.ValidationErrorList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diksha on 29-Jan-20.
 */
public class EzPayNowInputValidator extends CommonInputValidator
{
    private static Logger log = new Logger(EzPayNowInputValidator.class.getName());
    private static TransactionLogger transactionLogger=new TransactionLogger(EzPayNowInputValidator.class.getName());

    public  String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO,String actionName,String addressValidation){
        String error="";
        List<InputFields> param=new ArrayList<>();
        Functions functions=new Functions();
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();


        param.add(InputFields.CHECKNUMBER);
        param.add(InputFields.ACCOUNTNUMBER);
        param.add(InputFields.ROUTINGNUMBER);
        param.add(InputFields.FIRSTNAME);
        param.add(InputFields.LASTNAME);
        param.add(InputFields.CARDHOLDERIP);
        transactionLogger.error(addressValidation);
        if ("Y".equalsIgnoreCase(addressValidation)){
            param.add(InputFields.STREET);
            param.add(InputFields.COUNTRYCODE);
            param.add(InputFields.ZIP);
            param.add(InputFields.TELNO);
            param.add(InputFields.EMAILADDR);

        }
        ValidationErrorList errorList=new ValidationErrorList();
        transactionLogger.error("------Inside EzPayNowInputValidator Class------");
        InputValidationsForEzPayNow(commonValidatorVO, param, errorList, false);
        String EOL="<BR>";
        if(!errorList.isEmpty()){
            for(InputFields inputFields:param){
                if (errorList.getError(inputFields.toString()) != null)
                {
                    log.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error+errorList.getError(inputFields.toString()).getMessage() + EOL;
                }
            }
        }
        String account_number="";
        String routing_number="";


            account_number =commonValidatorVO.getReserveField2VO().getAccountNumber();
            if (account_number.length()<8)
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ACCOUNT_NUMBER);
                if(!error.contains(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription()))
                {
                    error += errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + EOL;
                }
            }
            routing_number = commonValidatorVO.getReserveField2VO().getRoutingNumber();
            if (routing_number.length()<8)
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ROUTING_NUMBER);
                if(!error.contains(errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription()))
                {
                    error += errorCodeVO.getErrorCode() + "_" + errorCodeVO.getErrorDescription() + EOL;
                }
            }


        return error;
    }

    private void InputValidationsForEzPayNow(CommonValidatorVO commonValidatorVO, List<InputFields> inputList, ValidationErrorList validationErrorList, boolean isOptional)
    {
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        ErrorCodeUtils errorCodeUtils=new ErrorCodeUtils();
        GenericAddressDetailsVO genericAddressDetailsVO=commonValidatorVO.getAddressDetailsVO();

        for (InputFields input:inputList){
            switch (input){
                case FIRSTNAME:
                if(!ESAPI.validator().isValidInput(input.toString(),genericAddressDetailsVO.getFirstname(),"SafeString",50,isOptional)){
                    errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_FIRSTNAME);
                    errorCodeVO.setErrorName(ErrorName.VALIDATION_FIRSTNAME);
                    validationErrorList.addError(input.toString(),new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(),errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+":::"+genericAddressDetailsVO.getFirstname(),errorCodeVO));
                    if (commonValidatorVO.getErrorCodeListVO() != null)
                        commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                }
                    break;
                case LASTNAME:
                    if(!ESAPI.validator().isValidInput(input.toString(),genericAddressDetailsVO.getLastname(),"SafeString",50,isOptional)){
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_LASTNAME);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_LASTNAME);
                        validationErrorList.addError(input.toString(),new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(),errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+":::"+genericAddressDetailsVO.getLastname(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case TELNO:
                    System.out.println(genericAddressDetailsVO.getPhone());
                    if(!ESAPI.validator().isValidInput(input.toString(),genericAddressDetailsVO.getPhone(),"OnlyNumber",15,isOptional)){
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_TELNO);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_TELNO);
                        validationErrorList.addError(input.toString(),new PZValidationException(errorCodeVO.getApiCode()+"_"+errorCodeVO.getErrorDescription(),errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+":::"+genericAddressDetailsVO.getPhone(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case COUNTRYCODE:
                    if(!ESAPI.validator().isValidInput(input.toString(),genericAddressDetailsVO.getCountry(),"CountryCode",3,isOptional)){
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_COUNTRY_CODE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_COUNTRY_CODE);
                        validationErrorList.addError(input.toString(),new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(),errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+":::"+genericAddressDetailsVO.getCountry(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                       break;
                case CITY:
                    if(!ESAPI.validator().isValidInput(input.toString(),genericAddressDetailsVO.getCity(),"alphanum",30,isOptional)){
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CITY);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CITY);
                        validationErrorList.addError(input.toString(),new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(),errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+":::"+genericAddressDetailsVO.getCity(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                        break;
                case STREET:
                    if(!ESAPI.validator().isValidInput(input.toString(),genericAddressDetailsVO.getStreet(),"USPSAddress",150,isOptional)){
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STREET);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STREET);
                        validationErrorList.addError(input.toString(),new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(),errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+":::"+genericAddressDetailsVO.getStreet(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ZIP:
                    if(!ESAPI.validator().isValidInput(input.toString(),genericAddressDetailsVO.getZipCode(),"Zip",10,isOptional)){
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ZIP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ZIP);
                        validationErrorList.addError(input.toString(),new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(),errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+":::"+genericAddressDetailsVO.getZipCode(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case STATE:
                    if(!ESAPI.validator().isValidInput(input.toString(),genericAddressDetailsVO.getState(),"alphanum",40,isOptional)){
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_STATE);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_STATE);
                        validationErrorList.addError(input.toString(),new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(),errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+":::"+genericAddressDetailsVO.getState(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CARDHOLDERIP:
                    if(!(ESAPI.validator().isValidInput(input.toString(),genericAddressDetailsVO.getCardHolderIpAddress(),"IPAddress",50,isOptional)||ESAPI.validator().isValidInput(input.toString(),genericAddressDetailsVO.getCardHolderIpAddress(),"IPAddressNew",80,isOptional))){
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CUSTOMER_IP);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CUSTOMER_IP);
                        validationErrorList.addError(input.toString(),new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(),errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+":::"+genericAddressDetailsVO.getCardHolderIpAddress(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case EMAILADDR:
                    if(!ESAPI.validator().isValidInput(input.toString(),genericAddressDetailsVO.getEmail(),"Email",50,isOptional)){
                        errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_EMAIL);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_EMAIL);
                        validationErrorList.addError(input.toString(),new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(),errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription()+":::"+genericAddressDetailsVO.getEmail(),errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case CHECKNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getCheckNumber(), "OnlyNumber", 12, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_CHECK_NUMBER);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_CHECK_NUMBER);
                        validationErrorList.addError(input.toString(), new PZValidationException(ErrorMessages.INVALID_CHECK_NUMBER, ErrorMessages.INVALID_CHECK_NUMBER + ":::" + commonValidatorVO.getReserveField2VO().getCheckNumber(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ACCOUNTNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getAccountNumber(), "OnlyNumber", 50, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ACCOUNT_NUMBER);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ACCOUNT_NUMBER);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), ErrorMessages.INVALID_ACCOUNT_NUMBER + ":::" + commonValidatorVO.getReserveField2VO().getAccountNumber(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
                case ROUTINGNUMBER:
                    if (!ESAPI.validator().isValidInput(input.toString(), commonValidatorVO.getReserveField2VO().getRoutingNumber(), "OnlyNumber", 20, isOptional))
                    {
                        errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_ROUTING_NUMBER);
                        errorCodeVO.setErrorName(ErrorName.VALIDATION_ROUTING_NUMBER);
                        validationErrorList.addError(input.toString(), new PZValidationException(errorCodeVO.getErrorCode()+"_"+errorCodeVO.getErrorDescription(), ErrorMessages.INVALID_ROUTING_NUMBER + ":::" + commonValidatorVO.getReserveField2VO().getRoutingNumber(), errorCodeVO));
                        if (commonValidatorVO.getErrorCodeListVO() != null)
                            commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
                    }
                    break;
            }
        }
    }
}
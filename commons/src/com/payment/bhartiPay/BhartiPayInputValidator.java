package com.payment.bhartiPay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Transaction;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Devendra  on 2/7/2020.
 */
public class BhartiPayInputValidator extends CommonInputValidator
{
    TransactionLogger transactionLogger = new TransactionLogger(BhartiPayInputValidator.class.getName());
    final static ResourceBundle BPBANKS = LoadProperties.getProperty("com.directi.pg.BPBANKS");
    final static ResourceBundle BPWALLETS = LoadProperties.getProperty("com.directi.pg.BPWALLETS");


    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        ErrorCodeUtils errorCodeUtils =new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO=new ErrorCodeVO();
        String error="";
        if ("Y".equalsIgnoreCase(addressValidation))
        {
            if(checkValidationForPaymentProviderNBAndWA(commonValidatorVO.getTransDetailsVO().getPaymentProvider(),commonValidatorVO.getPaymentBrand()))
            {
                errorCodeVO=errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENT_PROVIDER);
                error="Payment Provider not found";
                if(commonValidatorVO.getErrorCodeListVO()!=null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            }
        }

        return error;
    }

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName)
    {
        String error = "";
        List<InputFields> param = new ArrayList<InputFields>();

        param.add(InputFields.FIRSTNAME);
        param.add(InputFields.LASTNAME);
        param.add(InputFields.CARDHOLDERIP);
        /*param.add(InputFields.COUNTRYCODE);
        param.add(InputFields.CITY);
        param.add(InputFields.TELNO);
        param.add(InputFields.ZIP);
        param.add(InputFields.STREET);
        param.add(InputFields.EMAILADDR);*/

        ValidationErrorList errorList = new ValidationErrorList();
        transactionLogger.error("---------------Inside AcqraInputValidator class---------------------");



        String EOL = "<BR>";
        if (!errorList.isEmpty())
        {
            for (InputFields inputFields : param)
            {
                if (errorList.getError(inputFields.toString()) != null)
                {
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    transactionLogger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error + errorList.getError(inputFields.toString()).getMessage() + EOL;
                }
            }
        }
        /*InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputMandatoryFieldsList = new ArrayList<InputFields>();
        inputMandatoryFieldsList.add(InputFields.CARDHOLDERIP);
        ValidationErrorList errorList1 = new ValidationErrorList();
        inputValidator.InputValidations(commonValidatorVO,inputMandatoryFieldsList,errorList1,false);
        error = error + inputValiDatorUtils.getError(errorList1,inputMandatoryFieldsList,actionName);*/

        return error;
    }

    public boolean checkValidationForPaymentProviderNBAndWA (String paymentProvider,String paymentBrand) throws UncheckedIOException
    {
        String provider_name="";
        List<String> bankList = new ArrayList<>();
        List<String> walletList = new ArrayList<>();
        Functions functions=new Functions();
        if(paymentBrand.equalsIgnoreCase("BPBANKS"))
        {
            if (functions.isValueNull(paymentProvider))
            {
                paymentProvider = paymentProvider.replaceAll(" ", "_");
                transactionLogger.error("paymentProvider is --- >" + paymentProvider);
                Enumeration<String> bankdata = BPBANKS.getKeys();
                while (bankdata.hasMoreElements())
                {
                    bankList.add(bankdata.nextElement());
                }
                transactionLogger.error("Banklist is --- >" + provider_name);
                if (!bankList.contains(paymentProvider))
                {
                    return true;
                }
                return false;

            }
        }
        else if (paymentBrand.equalsIgnoreCase("BPWALLETS"))
        {
            if (functions.isValueNull(paymentProvider))
            {
                paymentProvider = paymentProvider.replaceAll(" ", "_");
                transactionLogger.error("Wallets ProviderName --- >" + paymentProvider);
                Enumeration<String> bankdata = BPWALLETS.getKeys();
                while (bankdata.hasMoreElements())
                {
                    walletList.add(bankdata.nextElement());
                }
                transactionLogger.error("Banklist is --- >" + walletList);
                if (!walletList.contains(paymentProvider))
                {
                    return true;
                }
                return false;
            }
        }
        return false;

    }

}

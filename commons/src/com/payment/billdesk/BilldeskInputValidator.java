package com.payment.billdesk;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.CommonInputValidator;
import com.payment.validators.InputFields;
import com.payment.validators.InputValiDatorUtils;
import com.payment.validators.InputValidator;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ValidationErrorList;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by jeet on 24-01-2020.
 */
public class BilldeskInputValidator extends CommonInputValidator
{
    private static final TransactionLogger transactionLogger = new TransactionLogger(BilldeskInputValidator.class.getName());
    final static ResourceBundle BDBANKS = LoadProperties.getProperty("com.directi.pg.BDBANKS");
    final static ResourceBundle BDWALLETS = LoadProperties.getProperty("com.directi.pg.BDWALLETS");

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        String error = "";
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        InputValiDatorUtils inputValiDatorUtils=new InputValiDatorUtils();
        InputValidator inputValidator=new InputValidator();
        // address validation flag on account level

        String paymentmode=commonValidatorVO.getPaymentMode();

        if ("Y".equalsIgnoreCase(addressValidation))
        {

            if("NBI".equalsIgnoreCase(paymentmode)||"EWI".equalsIgnoreCase(paymentmode)){
            if (checkValidationForPaymentProviderNBAndWA(commonValidatorVO.getTransDetailsVO().getPaymentProvider(), commonValidatorVO.getPaymentBrand()))
            {
                errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENT_PROVIDER);
                error = "Payment Provider not found";
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            }

            }
            else if("UPI".equalsIgnoreCase(paymentmode)){
                List<InputFields> inputMandatoryFieldsListForOtherBanks = new ArrayList<InputFields>();
                inputMandatoryFieldsListForOtherBanks.addAll(inputValiDatorUtils.getRestUPIMandatoryParametersValidation());
                ValidationErrorList errorListForPaymitcoAccount = new ValidationErrorList();
                inputValidator.RestInputValidations(commonValidatorVO, inputMandatoryFieldsListForOtherBanks, errorListForPaymitcoAccount, false);
                error = error + inputValiDatorUtils.getError(errorListForPaymitcoAccount, inputMandatoryFieldsListForOtherBanks, actionName);
            }
        }
        return error;
    }

    public boolean checkValidationForPaymentProviderNBAndWA(String paymentProvider, String paymentBrand) throws UncheckedIOException
    {
        Functions functions = new Functions();
        List<String> bankList = new ArrayList<>();
        List<String> walletList = new ArrayList<>();
        String provider_name = "";

        if (functions.isValueNull(paymentBrand) && paymentBrand.equalsIgnoreCase("BDBANKS"))
        {
            if (functions.isValueNull(paymentProvider))
            {
                paymentProvider = paymentProvider.replaceAll(" ", "_");
                transactionLogger.error("paymentProvider is --- >" + paymentProvider);
                Enumeration<String> bankdata = BDBANKS.getKeys();
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

        else if (functions.isValueNull(paymentBrand) && paymentBrand.equalsIgnoreCase("BDWALLETS"))
        {
            if (functions.isValueNull(paymentProvider))
            {
                paymentProvider = paymentProvider.replaceAll(" ", "_");
                transactionLogger.error("paymentProvider is --- >" + paymentProvider);
                Enumeration<String> walletdata = BDWALLETS.getKeys();
                while (walletdata.hasMoreElements())
                {
                    walletList.add(walletdata.nextElement());
                }
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
package com.payment.LetzPay;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
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
 * Created by Admin on 3/24/2021.
 */
public class LetzPayInputValidator extends CommonInputValidator
{
    private static Logger log = new Logger(LetzPayInputValidator.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(LetzPayInputValidator.class.getName());
    final static ResourceBundle LZPBANKS = LoadProperties.getProperty("com.directi.pg.LZPBANKS");
    final static ResourceBundle LZPWALLETS = LoadProperties.getProperty("com.directi.pg.LZPWALLETS");

    public String validateIntegrationSpecificParameters(CommonValidatorVO commonValidatorVO, String actionName, String addressValidation)
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
        InputValiDatorUtils inputValiDatorUtils = new InputValiDatorUtils();
        InputValidator inputValidator = new InputValidator();
        String error = "";
        List<InputFields> param = new ArrayList<InputFields>();
        if ("UPI".equalsIgnoreCase(commonValidatorVO.getPaymentBrand()))
            param.add(InputFields.VPA_ADDRESS);

        if ("Y".equalsIgnoreCase(addressValidation))
        {
            if (checkValidationForPaymentProviderNBAndWA(commonValidatorVO.getTransDetailsVO().getPaymentProvider(), commonValidatorVO.getPaymentBrand()))
            {
                errorCodeVO = errorCodeUtils.getErrorCode(ErrorName.VALIDATION_PAYMENT_PROVIDER);
                error = "Payment Provider not found";
                if (commonValidatorVO.getErrorCodeListVO() != null)
                    commonValidatorVO.getErrorCodeListVO().addListOfError(errorCodeVO);
            }

            ValidationErrorList errorList1 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, param, errorList1, false);
            error = error + inputValiDatorUtils.getError(errorList1, param, actionName);
        }
        else
        {
            ValidationErrorList errorList1 = new ValidationErrorList();
            inputValidator.InputValidations(commonValidatorVO, param, errorList1, true);
            error = error + inputValiDatorUtils.getError(errorList1, param, actionName);
        }
        transactionLogger.error("error  size------------>" + commonValidatorVO.getErrorCodeListVO().getListOfError().size());
        transactionLogger.error("error------------>" + error);

        return error;

    }
    public boolean checkValidationForPaymentProviderNBAndWA (String paymentProvider,String paymentBrand) throws UncheckedIOException
    {
        String provider_name="";
        List<String> bankList = new ArrayList<>();
        List<String> walletList = new ArrayList<>();
        Functions functions=new Functions();
        if(functions.isValueNull(paymentBrand)&&paymentBrand.equalsIgnoreCase("LZPBANKS"))
        {
            if (functions.isValueNull(paymentProvider))
            {
                paymentProvider = paymentProvider.replaceAll(" ", "_");
                transactionLogger.error("paymentProvider is --- >" + paymentProvider);
                Enumeration<String> bankdata = LZPBANKS.getKeys();
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
        else if (paymentBrand.equalsIgnoreCase("LZPWALLETS"))
        {
            if (functions.isValueNull(paymentProvider))
            {
                paymentProvider = paymentProvider.replaceAll(" ", "_");
                transactionLogger.error("Wallets ProviderName --- >" + paymentProvider);
                Enumeration<String> bankdata = LZPWALLETS.getKeys();
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

package com.payment.inpay;

import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Hashtable;
import java.util.ResourceBundle;

/**
 * Created by Admin on 4/30/2016.
 */
public class InPayPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(InPayPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(InPayPaymentProcess.class.getName());
    final static ResourceBundle RB2 = LoadProperties.getProperty("com.directi.pg.AsyncRedirectionUrl");
    final static ResourceBundle INPAY = LoadProperties.getProperty("com.directi.pg.InPayServlet");


    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        log.debug("inside inpayprocess===");
        InPayAccount inPayAccount = new InPayAccount();
        AsyncParameterVO asyncParameterVO = null;
        String flowLayout = "multi_page";
        Hashtable dataHash = new Hashtable();
        dataHash = inPayAccount.getMidAndSecretKey(commonValidatorVO.getMerchantDetailsVO().getAccountId());

        try
        {

            String merchantid = (String) dataHash.get("mid");
            String secratKey = (String) dataHash.get("secretkey");
            String cMerchantid = URLEncoder.encode(merchantid);
            String cOrderid = URLEncoder.encode(String.valueOf(commonValidatorVO.getTrackingid()));
            String cAmount = URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getAmount());
            String cCurrency = URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getCurrency());
            String cOrderText = URLEncoder.encode(commonValidatorVO.getTransDetailsVO().getOrderId());
            String cFlowLayout = URLEncoder.encode(flowLayout);
            String cSecretKey = URLEncoder.encode(secratKey);
            String address = commonValidatorVO.getAddressDetailsVO().getStreet() + "," + commonValidatorVO.getAddressDetailsVO().getCity() + "," + commonValidatorVO.getAddressDetailsVO().getState() + "," + commonValidatorVO.getAddressDetailsVO().getCountry();
            String checksumString = "merchant_id=" + cMerchantid + "&order_id=" + cOrderid + "&amount=" + cAmount + "&currency=" + cCurrency + "&order_text=" + cOrderText +
                    "&flow_layout=" + cFlowLayout + "&secret_key=" + cSecretKey;

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("merchant_id");
            asyncParameterVO.setValue(merchantid);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("order_id");
            asyncParameterVO.setValue(commonValidatorVO.getTrackingid());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("amount");
            asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getAmount());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("currency");
            asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getCurrency());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("country");
            asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getCountry());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("order_text");
            asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getOrderId());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("flow_layout");
            asyncParameterVO.setValue("multi_page");
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("return_url");
            asyncParameterVO.setValue(INPAY.getString("FRONTEND"));
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("buyer_email");
            asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getEmail());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("checksum");
            asyncParameterVO.setValue(getMD5HashVal(checksumString));
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("buyer_name");
            asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname());
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("buyer_address");
            asyncParameterVO.setValue(address);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            directKitResponseVO.setBankRedirectionUrl(RB2.getString("INPAY_URL"));
        }
        catch (PZTechnicalViolationException tve)
        {
            log.error("error in InPay checksum---",tve);
        }
        return directKitResponseVO;
    }

    public static String getMD5HashVal(String str) throws PZTechnicalViolationException
    {
        String encryptedString = null;
        byte[] bytesToBeEncrypted;
        try
        {
            // convert string to bytes using a encoding scheme
            bytesToBeEncrypted = str.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] theDigest = md.digest(bytesToBeEncrypted);
            // convert each byte to a hexadecimal digit
            Formatter formatter = new Formatter();
            for (byte b : theDigest) {
                formatter.format("%02x", b);
            }
            encryptedString = formatter.toString().toLowerCase();
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SingleCallPaymentDAO.java", "getMD5HashVal()", null, "Transaction", "UnsupportedEncodingException raised::::", PZTechnicalExceptionEnum.UNSUPPORTING_ENCOADING_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException("SingleCallPaymentDAO.java","getMD5HashVal()",null,"Transaction","UnsupportedEncodingException raised::::", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        return encryptedString;
    }
}

package com.payment.pbs.core;

import com.directi.pg.Functions;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

/**
 * Created by 123 on 5/13/2016.
 */
public class PbsPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(PbsPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PbsPaymentProcess.class.getName());
    Functions functions=new Functions();
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.PbsServlet");

    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){

        String signInfo = "";
        try
        {
            signInfo =SHA256forSales(response3D.getPaReq(),response3D.getMd(),commonValidatorVO.getTrackingid(),commonValidatorVO.getTransDetailsVO().getCurrency(),commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getAddressDetailsVO().getFirstname(),commonValidatorVO.getAddressDetailsVO().getLastname(),commonValidatorVO.getCardDetailsVO().getCardNum(),commonValidatorVO.getCardDetailsVO().getExpYear(),commonValidatorVO.getCardDetailsVO().getExpMonth(),commonValidatorVO.getCardDetailsVO().getcVV(),commonValidatorVO.getAddressDetailsVO().getEmail(),response3D.getRemark());
        }
        catch (Exception e)
        {
            log.error("Exception occurs  while calculating SHA:::",e);
        }


        String form = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" +

                "<input type=hidden name=merNo id=merNo value=\""+response3D.getPaReq()+"\">"+
                "<input type=hidden name=gatewayNo id=gatewayNo value=\""+response3D.getMd()+"\">"+
                //"<input type=hidden name=signkey id=signkey value=\""+response3D.getMd()+"\">"+
                "<input type=hidden name=orderNo id=orderNo value=\""+commonValidatorVO.getTrackingid()+"\">"+
                "<input type=hidden name=orderCurrency id=orderCurrency value=\"" +commonValidatorVO.getTransDetailsVO().getCurrency()+"\">"+
                "<input type=hidden name=orderAmount id=orderAmount value=\""+commonValidatorVO.getTransDetailsVO().getAmount()+"\">"+
                "<input type=hidden name=returnUrl id=returnUrl value=\""+RB.getString("FRONTEND")+"\">"+
                "<input type=hidden name=signInfo id=signInfo value=\""+signInfo+"\">"+
                "<input type=hidden name=firstName id=firstName value=\""+commonValidatorVO.getAddressDetailsVO().getFirstname()+"\">"+
                "<input type=hidden name=lastName id=lastName value=\""+commonValidatorVO.getAddressDetailsVO().getLastname()+"\">"+
                "<input type=hidden name=email id=email value=\""+commonValidatorVO.getAddressDetailsVO().getEmail()+"\">"+
                "<input type=hidden name=phone id=phone value=\""+commonValidatorVO.getAddressDetailsVO().getPhone()+"\">"+
                "<input type=hidden name=paymentMethod id=paymentMethod value=\"Credit Card\">"+
                "<input type=hidden name=country id=country value=\""+commonValidatorVO.getAddressDetailsVO().getCountry()+"\">"+
                "<input type=hidden name=state id=state value=\""+commonValidatorVO.getAddressDetailsVO().getState()+"\">"+
                "<input type=hidden name=city id=city value=\""+commonValidatorVO.getAddressDetailsVO().getCity()+"\">"+
                "<input type=hidden name=address id=address value=\""+commonValidatorVO.getAddressDetailsVO().getStreet()+"\">"+
                "<input type=hidden name=zip id=zip value=\""+commonValidatorVO.getAddressDetailsVO().getZipCode()+"\">"+
                "<input type=hidden name=remark id=remark value=\""+commonValidatorVO.getTransDetailsVO().getOrderId()+"\">"+
                "<input type=hidden name=cardNo id=cardNo value=\""+commonValidatorVO.getCardDetailsVO().getCardNum()+"\">"+
                "<input type=hidden name=cardExpireMonth id=cardExpireMonth value=\""+commonValidatorVO.getCardDetailsVO().getExpMonth()+"\">"+
                "<input type=hidden name=cardExpireYear id=cardExpireYear value=\""+commonValidatorVO.getCardDetailsVO().getExpYear()+"\">"+
                "<input type=hidden name=cardSecurityCode id=cardSecurityCode value=\""+commonValidatorVO.getCardDetailsVO().getcVV()+"\">"+
                "<input type=hidden name=issuingBank id=issuingBank value=\"Issuing Bank\">"+
                "</form>" +
                "<script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        String formlog = "<form name=\"creditcard_checkout\" method=\"POST\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" +

                "<input type=hidden name=merNo id=merNo value=\""+response3D.getPaReq()+"\">"+
                "<input type=hidden name=gatewayNo id=gatewayNo value=\""+response3D.getMd()+"\">"+
                //"<input type=hidden name=signkey id=signkey value=\""+response3D.getMd()+"\">"+
                "<input type=hidden name=orderNo id=orderNo value=\""+commonValidatorVO.getTrackingid()+"\">"+
                "<input type=hidden name=orderCurrency id=orderCurrency value=\"" +commonValidatorVO.getTransDetailsVO().getCurrency()+"\">"+
                "<input type=hidden name=orderAmount id=orderAmount value=\""+commonValidatorVO.getTransDetailsVO().getAmount()+"\">"+
                "<input type=hidden name=returnUrl id=returnUrl value=\""+RB.getString("FRONTEND")+"\">"+
                "<input type=hidden name=signInfo id=signInfo value=\""+signInfo+"\">"+
                "<input type=hidden name=firstName id=firstName value=\""+commonValidatorVO.getAddressDetailsVO().getFirstname()+"\">"+
                "<input type=hidden name=lastName id=lastName value=\""+commonValidatorVO.getAddressDetailsVO().getLastname()+"\">"+
                "<input type=hidden name=email id=email value=\""+commonValidatorVO.getAddressDetailsVO().getEmail()+"\">"+
                "<input type=hidden name=phone id=phone value=\""+commonValidatorVO.getAddressDetailsVO().getPhone()+"\">"+
                "<input type=hidden name=paymentMethod id=paymentMethod value=\"Credit Card\">"+
                "<input type=hidden name=country id=country value=\""+commonValidatorVO.getAddressDetailsVO().getCountry()+"\">"+
                "<input type=hidden name=state id=state value=\""+commonValidatorVO.getAddressDetailsVO().getState()+"\">"+
                "<input type=hidden name=city id=city value=\""+commonValidatorVO.getAddressDetailsVO().getCity()+"\">"+
                "<input type=hidden name=address id=address value=\""+commonValidatorVO.getAddressDetailsVO().getStreet()+"\">"+
                "<input type=hidden name=zip id=zip value=\""+commonValidatorVO.getAddressDetailsVO().getZipCode()+"\">"+
                "<input type=hidden name=remark id=remark value=\""+commonValidatorVO.getTransDetailsVO().getOrderId()+"\">"+
                "<input type=hidden name=cardNo id=cardNo value=\""+functions.maskingPan(commonValidatorVO.getCardDetailsVO().getCardNum())+"\">"+
                "<input type=hidden name=cardExpireMonth id=cardExpireMonth value=\""+functions.maskingNumber(commonValidatorVO.getCardDetailsVO().getExpMonth())+"\">"+
                "<input type=hidden name=cardExpireYear id=cardExpireYear value=\""+functions.maskingNumber(commonValidatorVO.getCardDetailsVO().getExpYear())+"\">"+
                "<input type=hidden name=cardSecurityCode id=cardSecurityCode value=\""+ functions.maskingNumber(commonValidatorVO.getCardDetailsVO().getcVV())+"\">"+
                "<input type=hidden name=issuingBank id=issuingBank value=\"Issuing Bank\">"+
                "</form>" +
                "<script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionLogger.debug("PbsPaymentProcess Form---"+formlog.toString());
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D, CommonValidatorVO commonValidatorVO)
    {
        String signInfo = "";
        try
        {
            signInfo =SHA256forSales(response3D.getPaReq(),response3D.getMd(),commonValidatorVO.getTrackingid(),commonValidatorVO.getTransDetailsVO().getCurrency(),commonValidatorVO.getTransDetailsVO().getAmount(),commonValidatorVO.getAddressDetailsVO().getFirstname(),commonValidatorVO.getAddressDetailsVO().getLastname(),commonValidatorVO.getCardDetailsVO().getCardNum(),commonValidatorVO.getCardDetailsVO().getExpYear(),commonValidatorVO.getCardDetailsVO().getExpMonth(),commonValidatorVO.getCardDetailsVO().getcVV(),commonValidatorVO.getAddressDetailsVO().getEmail(),response3D.getRemark());
        }
        catch (Exception e)
        {
            log.error("Exception occurs  while calculating SHA:::",e);
        }

        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside pbs payment process---"+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("merNo");
        asyncParameterVO.setValue(response3D.getPaReq());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("gatewayNo");
        asyncParameterVO.setValue(response3D.getMd());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("signkey");
        asyncParameterVO.setValue(response3D.getMd());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("orderNo");
        asyncParameterVO.setValue(commonValidatorVO.getTrackingid());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("orderCurrency");
        asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getCurrency());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("orderAmount");
        asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getAmount());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("returnUrl");
        asyncParameterVO.setValue(RB.getString("FRONTEND"));
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("signInfo");
        asyncParameterVO.setValue(signInfo);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("firstName");
        asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getFirstname());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("lastName");
        asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getLastname());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("email");
        asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getEmail());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("phone");
        asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getPhone());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("paymentMethod");
        asyncParameterVO.setValue("Credit Card");
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("country");
        asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getCountry());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("state");
        asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getState());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("city");
        asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getCity());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("address");
        asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getStreet());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("zip");
        asyncParameterVO.setValue(commonValidatorVO.getAddressDetailsVO().getZipCode());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("remark");
        asyncParameterVO.setValue(commonValidatorVO.getTransDetailsVO().getOrderId());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("cardNo");
        asyncParameterVO.setValue(commonValidatorVO.getCardDetailsVO().getCardNum());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("cardExpireMonth");
        asyncParameterVO.setValue(commonValidatorVO.getCardDetailsVO().getExpMonth());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("cardExpireYear");
        asyncParameterVO.setValue(commonValidatorVO.getCardDetailsVO().getExpYear());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("cardSecurityCode");
        asyncParameterVO.setValue(commonValidatorVO.getCardDetailsVO().getcVV());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("issuingBank");
        asyncParameterVO.setValue("Issuing Bank");
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);
    }


    public static String SHA256forSales(String merchantNo, String gatewayNo, String orderNo, String orderCurrency, String orderAmount, String firstName, String lastName, String cardNo, String expiryYear, String expiryMonth, String cvv, String email, String signKey) throws PZTechnicalViolationException
    {
        String sha = merchantNo.trim() + gatewayNo.trim() + orderNo.trim() + orderCurrency.trim() + orderAmount.trim() + firstName.trim() + lastName.trim() + cardNo.trim() + expiryYear.trim() + expiryMonth.trim() + cvv.trim() + email.trim() + signKey;
        sha.trim();
        transactionLogger.error("sha256 combination---" + sha);

        StringBuffer hexString = new StringBuffer();
        try
        {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sha.getBytes("UTF-8"));
            for (int i = 0; i < hash.length; i++)
            {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PbsPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (UnsupportedEncodingException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(PbsPaymentGateway.class.getName(), "SHA256forSales()", null, "common", "UnsupportedEncodingException  while placing transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION,null, e.getMessage(), e.getCause());
        }

        return hexString.toString().trim();
    }

    @Override
    public String getSpecificVirtualTerminalJSP()
    {
        return "pbsspecificfields.jsp";    //To change body of overridden methods use File | Settings | File Templates.
    }
}

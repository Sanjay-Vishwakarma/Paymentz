package com.payment.apco.core;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.request.PZPayoutRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.ResourceBundle;

/**
 * Created by ThinkPadT410 on 2/10/2017.
 */
public class ApcoPayPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger=new TransactionLogger(ApcoPayPaymentProcess.class.getName());
    ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.ApcoPayServlet");
    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        CommAddressDetailsVO commAddressDetailsVO=requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=requestVO.getCardDetailsVO();
        TransactionManager transactionManager=new TransactionManager();
        TransactionDetailsVO transactionDetailsVO=transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));
        // commTransactionDetailsVO.setAmount(transactionDetailsVO.getAmount());
        commAddressDetailsVO.setFirstname(transactionDetailsVO.getFirstName());
        commAddressDetailsVO.setLastname(transactionDetailsVO.getLastName());
        commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commTransactionDetailsVO.setPreviousTransactionId(transactionDetailsVO.getPaymentId());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());
        commAddressDetailsVO.setLanguage(transactionDetailsVO.getLanguage());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.debug("inside apcoprocess===");
        AsyncParameterVO asyncParameterVO = null;
        Functions functions = new Functions();
        ApcoPayUtills apcoPayUtills = new ApcoPayUtills();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(commonValidatorVO.getMerchantDetailsVO().getAccountId());
        String profileId=gatewayAccount.getFRAUD_FTP_USERNAME();
        String secretWord=gatewayAccount.getFRAUD_FTP_PATH();
        String birthday = "";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getBirthdate()))
        {
            birthday = apcoPayUtills.getBirthday(commonValidatorVO.getAddressDetailsVO().getBirthdate(),null);
        }
        boolean isTest = gatewayAccount.isTest();
        String testString = "";
        if (isTest){
            testString = "<TEST />";
        }
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String currency = CurrencyCodeISO4217.getNumericCurrencyCode(commonValidatorVO.getTransDetailsVO().getCurrency());
        Integer currencyCode = Integer.parseInt(currency);
        String trackingId = commonValidatorVO.getTrackingid();
        String phoneNo = commonValidatorVO.getAddressDetailsVO().getPhone();
        String emailId = commonValidatorVO.getAddressDetailsVO().getEmail();
        String language = commonValidatorVO.getAddressDetailsVO().getLanguage();
        String name = commonValidatorVO.getAddressDetailsVO().getFirstname() + " " + commonValidatorVO.getAddressDetailsVO().getLastname();
        String street = commonValidatorVO.getAddressDetailsVO().getStreet() + "," + commonValidatorVO.getAddressDetailsVO().getStreet() + "," + commonValidatorVO.getAddressDetailsVO().getCity() + "," + commonValidatorVO.getAddressDetailsVO().getZipCode() + "," + commonValidatorVO.getAddressDetailsVO().getState();
        String country = commonValidatorVO.getAddressDetailsVO().getCountry();
        String redirectURLSuccess = rb.getString("APCOPAY_FRONTEND");
        String redirectURLFailed = rb.getString("APCOPAY_FRONTEND");
        String statusURL = rb.getString("APCOPAY_BACKEND");
        String actionType = "1";
        String cardTypeId = commonValidatorVO.getCardType();
        String apmName=apcoPayUtills.getAPMName(cardTypeId);
        String mainAcq = "";
        String addressXML = "";

        try
        {
            if ("AVISA".equalsIgnoreCase(apmName) || ("AMASTERC".equalsIgnoreCase(apmName)))
            {
                mainAcq = "<MainAcquirer>ALDRAPAY</MainAcquirer>";
                addressXML = "<RegName>" + name + "</RegName>" +
                        "<Address>" + street + "</Address>" +
                        "<RegCountry>" + country + "</RegCountry>" +
                        "<DOB>" + birthday + "</DOB>";
            }
            else if ("PVISA".equalsIgnoreCase(apmName) || ("RAVE".equalsIgnoreCase(apmName)))
            {
                addressXML = "<RegName>" + name + "</RegName>" +
                        "<Address>" + street + "</Address>" +
                        "<RegCountry>" + country + "</RegCountry>" +
                        "<DOB>" + birthday + "</DOB>";
            }

            //generate md5 hash from xml
            String xmlToPostForMD5 = "<Transaction hash=\"" + secretWord + "\">" +
                    "<ProfileID>" + profileId + "</ProfileID>" +
                    "<Value>" + amount + "</Value>" +
                    "<Curr>" + currencyCode + "</Curr>" +
                    "<Lang>" + language + "</Lang>" +
                    "<ORef>" + trackingId + "</ORef>" +
                    "<ClientAcc></ClientAcc>" +
                    "<MobileNo>" + phoneNo + "</MobileNo>" +
                    "<Email>" + emailId + "</Email>" +
                    "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                    "<FailedRedirectionURL>"+redirectURLFailed+"</FailedRedirectionURL>" +
                    "<UDF1 />" +
                    "<UDF2 />" +
                    "<UDF3 />" +
                    "<FastPay>" +
                    "<ListAllCards></ListAllCards>" +
                    "<NewCard1Try />" +
                    "<NewCardOnFail />" +
                    "<PromptCVV />" +
                    "<PromptExpiry />" +
                    "</FastPay>" +
                    "<ActionType>" + actionType + "</ActionType>" +
                    "<status_url>" + statusURL + "</status_url>" +
                    "<HideSSLLogo></HideSSLLogo>" +
                    "<AntiFraud>" +
                    "<Provider></Provider>" +
                    "</AntiFraud>" +
                    "" + testString + "" +
                    "<return_pspid></return_pspid>" +
                    "<ForcePayment>" + apmName + "</ForcePayment>" +
                    mainAcq +
                    addressXML +
                    "</Transaction>";
            transactionLogger.error("xmlToPostForMD5:::" + xmlToPostForMD5);
            String hash = apcoPayUtills.getMD5HashVal(xmlToPostForMD5);
            transactionLogger.error("hash:::" + hash);
            //use generated hash in below xml
            String xmlToPost = "<Transaction hash='" + hash + "'>" +
                    "<ProfileID>" + profileId + "</ProfileID>" +
                    "<Value>" + amount + "</Value>" +
                    "<Curr>" + currencyCode + "</Curr>" +
                    "<Lang>" + language + "</Lang>" +
                    "<ORef>" + trackingId + "</ORef>" +
                    "<ClientAcc></ClientAcc>" +
                    "<MobileNo>" + phoneNo + "</MobileNo>" +
                    "<Email>" + emailId + "</Email>" +
                    "<RedirectionURL>" + redirectURLSuccess + "</RedirectionURL>" +
                    "<FailedRedirectionURL>" + redirectURLFailed + "</FailedRedirectionURL>" +
                    "<UDF1 />" +
                    "<UDF2 />" +
                    "<UDF3 />" +
                    "<FastPay>" +
                    "<ListAllCards></ListAllCards>" +
                    "<NewCard1Try />" +
                    "<NewCardOnFail />" +
                    "<PromptCVV />" +
                    "<PromptExpiry />" +
                    "</FastPay>" +
                    "<ActionType>" + actionType + "</ActionType>" +
                    "<status_url>" + statusURL + "</status_url>" +
                    "<HideSSLLogo></HideSSLLogo>" +
                    "<AntiFraud>" +
                    "<Provider></Provider>" +
                    "</AntiFraud>" +
                    "" + testString + "" +
                    "<return_pspid></return_pspid>" +
                    "<ForcePayment>" + apmName + "</ForcePayment>" +
                    mainAcq +
                    addressXML +
                    "</Transaction>";
            transactionLogger.error("xmlToPost:::" + xmlToPost);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("params");
            asyncParameterVO.setValue(xmlToPost);
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            asyncParameterVO = new AsyncParameterVO();
            asyncParameterVO.setName("");
            asyncParameterVO.setValue("");
            directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

            directKitResponseVO.setBankRedirectionUrl(rb.getString("URL"));
            directKitResponseVO.setStatus("success");
        }
        catch (PZTechnicalViolationException tve)
        {
            transactionLogger.error("error in InPay checksum---",tve);
        }
        return directKitResponseVO;
    }

    public String get3DConfirmationFormVT(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=_blank";
        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\""+target+">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }

    public String get3DConfirmationForm(String trackingId, String ctoken, Comm3DResponseVO response3D)
    {
        transactionLogger.error("3d page displayed....." + response3D.getUrlFor3DRedirect());
        String target = "target=\"_parent\"";

        String form="<form name=\"launch3D\" method=\"POST\" action=\""+response3D.getUrlFor3DRedirect()+"\" "+target+">"+
                "<input type=\"hidden\" name=\"PaReq\" value=\""+response3D.getPaReq()+"\">"+
                "<input type=\"hidden\" name=\"TermUrl\" value=\""+response3D.getTerURL()+"\">"+
                "<input type=\"hidden\" name=\"MD\" value=\""+response3D.getMd()+"\">"+
                "</form>"+
                "<script language=\"javascript\"> document.launch3D.submit(); </script>";
        return form;
    }

    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D)
    {
        AsyncParameterVO asyncParameterVO = null;
        transactionLogger.debug("inside apco payment process---"+response3D.getUrlFor3DRedirect());
        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("launch3D");
        asyncParameterVO.setValue(response3D.getUrlFor3DRedirect());
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("");
        asyncParameterVO.setValue("");
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);


    }

}

package com.payment.auxpay_payment.core;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Nikita on 16/9/15.
 */
public class AuxPayPaymentGateway extends AbstractPaymentGateway
{
    //private static Logger log = new Logger(AuxPayPaymentGateway.class.getName());

    private static final ResourceBundle RBTemplate = LoadProperties.getProperty("com.directi.pg.3CharCountryList");
    private static final ResourceBundle RBNotification = LoadProperties.getProperty("com.directi.pg.AuxpayServlet");

    private static String NOTIFICATION_URL = RBNotification.getString("AUXPAY_NOTIFICATION");
    private static String STATUS_URL = RBNotification.getString("AUXPAY_STATUS");

    private static TransactionLogger transactionLogger = new TransactionLogger(AuxPayPaymentGateway.class.getName());

    public static final String GATEWAY_TYPE="auxpay";
    private final static String TESTURL = "https://directapi2.londonmultigames.com/wallet.ashx";

    public String getMaxWaitDays()
    {
        return null;
    }

    public AuxPayPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint genConstraint = new PZGenericConstraint("AuxPayPaymentGateway","processAuthentication",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);

    }

    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering processSale of AuxPayPaymentGateway");

        //CommRequestVO commRequestVO = null;
        CommRequestVO commRequestVO = (CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();

        GenericTransDetailsVO transDetailsRequestVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsRequestVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsRequestVO =commRequestVO.getAddressDetailsVO();

        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();//auxdirect_no3d
        String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();//testpass


        String countryCode="";
        if(addressDetailsRequestVO.getCountry().length()==2)
        {
            countryCode=RBTemplate.getString(addressDetailsRequestVO.getCountry()) ;
        }
        else
        {
            countryCode=addressDetailsRequestVO.getCountry();
        }

        //1st Step(Authentication)
        String stepOneRequest = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<wallet>\n" +
                "<version>2.0</version>\n" +
                "<action>CreateSession</action>\n" +
                "<username>"+merchantId+"</username>\n" +
                "<password>"+password+"</password>\n" +
                "</wallet>";

        transactionLogger.error("Authentication request step 1------->" +trackingID + "--" + stepOneRequest);

        String stepOneResponse = AuxPayUtills.doPostHTTPSURLConnection(TESTURL,stepOneRequest);

        transactionLogger.error("Response step 1------->"+trackingID + "--" +stepOneRequest);

        Map<String, String>  stepOneResponseMap = AuxPayUtills.readStepOneResponse(stepOneResponse);
        String sessionId = "";
        String firstName = URLEncoder.encode(addressDetailsRequestVO.getFirstname());
        String lastName = URLEncoder.encode(addressDetailsRequestVO.getLastname());
        transactionLogger.debug("session id----"+stepOneResponseMap.get("sessionid"));
        if((stepOneResponseMap.get("error_code")=="" || stepOneResponseMap.get("error_code").equals("")) && stepOneResponseMap.get("sessionid")!=null)
        {
            transactionLogger.debug("inside if before 2nd ste----");
            //2nd step(Merchant Registration)
            sessionId = stepOneResponseMap.get("sessionid");
            String billingRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<wallet>\n" +
                    "<action>CustomerBillingInfo</action>\n" +
                    "<sessionid>"+sessionId+"</sessionid>\n" +
                    "<extusername>-</extusername>\n" +
                    "<title>-</title>\n" +
                    "<firstname>"+firstName+"</firstname>\n" +
                    "<lastname>"+lastName+"</lastname>\n" +
                    "<dateofbirth>1990-01-01</dateofbirth>\n" +
                    "<address>"+URLEncoder.encode(addressDetailsRequestVO.getStreet())+"</address>\n" +
                    "<address2>-</address2>\n" +
                    "<telephone>"+URLEncoder.encode(addressDetailsRequestVO.getPhone())+"</telephone>\n" +
                    "<postcode>"+addressDetailsRequestVO.getZipCode()+"</postcode>\n" +
                    "<city>"+URLEncoder.encode(addressDetailsRequestVO.getCity())+"</city>\n" +
                    "<state>"+URLEncoder.encode(addressDetailsRequestVO.getState())+"</state>\n" +
                    "<country>"+countryCode+"</country>\n" +
                    "<userip>-</userip>\n" +
                    "<email>"+addressDetailsRequestVO.getEmail()+"</email>\n" +
                    "</wallet>";

            transactionLogger.error("XML billing request step 2------->" + trackingID + "--" +billingRequest);

            String billingResponse = AuxPayUtills.doPostHTTPSURLConnection(TESTURL, billingRequest);

            transactionLogger.error("XML billing response step 2------->" + trackingID + "--" +billingResponse);

            Map<String, String> stepTwoResponseMap = AuxPayUtills.readStepTwoResponse(billingResponse);
            transactionLogger.debug("XML billing response error code------->" + stepTwoResponseMap.get("error_code"));
            transactionLogger.debug("XML billing response sessionid------->" + stepTwoResponseMap.get("sessionid"));

            //PaymentNewCard
            if("paymentnewcard".equalsIgnoreCase(GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH()))
            {
                if (sessionId != null && (stepTwoResponseMap.get("error_code") == "" || stepTwoResponseMap.get("error_code").equals("")))
                {
                    transactionLogger.debug("inside if before 3rd ste----");

                    //3rd Step(Card Payment)
                    String cardRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                            "<wallet>\n" +
                            "<action>PaymentNewCard</action>\n" +
                            "<sessionid>" + sessionId + "</sessionid>\n" +
                            "<cardtype>" + cardDetailsRequestVO.getCardType() + "</cardtype>\n" +
                            "<cardnumber>" + cardDetailsRequestVO.getCardNum() + "</cardnumber>\n" +
                            "<nameoncard>" + firstName+" "+lastName + "</nameoncard>\n" +
                            "<startmonth>-</startmonth>\n" +
                            "<startyear>-</startyear>\n" +
                            "<expirymonth>" + cardDetailsRequestVO.getExpMonth() + "</expirymonth>\n" +
                            "<expiryyear>" + cardDetailsRequestVO.getExpYear() + "</expiryyear>\n" +
                            "<issuenumber>-</issuenumber>\n" +
                            "<cardnickname>-</cardnickname>\n" +
                            "<savecard>0</savecard>\n" +
                            "<merchanttxid>" + trackingID + "</merchanttxid>\n" +
                            "<securitycode>" + cardDetailsRequestVO.getcVV() + "</securitycode>\n" +
                            "<amount>" + transDetailsRequestVO.getAmount() + "</amount>\n" +
                            "<currency>" + transDetailsRequestVO.getCurrency() + "</currency>\n" +
                            //"<threedstatusurl>"+STATUS_URL+"</threedstatusurl>\n" +
                            "<threedstatusurl></threedstatusurl>\n" +
                            "<threedreturnurl>" + NOTIFICATION_URL + "</threedreturnurl>\n" +
                            "</wallet>";

                                   String cardRequestlog = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                            "<wallet>\n" +
                            "<action>PaymentNewCard</action>\n" +
                            "<sessionid>" + sessionId + "</sessionid>\n" +
                            "<cardtype>" + cardDetailsRequestVO.getCardType() + "</cardtype>\n" +
                            "<cardnumber>" + functions.maskingPan(cardDetailsRequestVO.getCardNum()) + "</cardnumber>\n" +
                            "<nameoncard>" + firstName+" "+lastName + "</nameoncard>\n" +
                            "<startmonth>-</startmonth>\n" +
                            "<startyear>-</startyear>\n" +
                            "<expirymonth>" + functions.maskingNumber(cardDetailsRequestVO.getExpMonth()) + "</expirymonth>\n" +
                            "<expiryyear>" + functions.maskingNumber(cardDetailsRequestVO.getExpYear()) + "</expiryyear>\n" +
                            "<issuenumber>-</issuenumber>\n" +
                            "<cardnickname>-</cardnickname>\n" +
                            "<savecard>0</savecard>\n" +
                            "<merchanttxid>" + trackingID + "</merchanttxid>\n" +
                            "<securitycode>" + functions.maskingNumber(cardDetailsRequestVO.getcVV()) + "</securitycode>\n" +
                            "<amount>" + transDetailsRequestVO.getAmount() + "</amount>\n" +
                            "<currency>" + transDetailsRequestVO.getCurrency() + "</currency>\n" +
                            //"<threedstatusurl>"+STATUS_URL+"</threedstatusurl>\n" +
                            "<threedstatusurl></threedstatusurl>\n" +
                            "<threedreturnurl>" + NOTIFICATION_URL + "</threedreturnurl>\n" +
                            "</wallet>";



                    transactionLogger.error("Card Request-----" +trackingID + "--" + cardRequestlog);

                    String cardResponse = AuxPayUtills.doPostHTTPSURLConnection(TESTURL, cardRequest);

                    transactionLogger.error("Card Response-----" +trackingID + "--" + cardResponse);

                    Map<String, String> stepThreeResponseMap = AuxPayUtills.readStepThreeResponse(cardResponse);
                    String status = "fail";
                    transactionLogger.debug("XML billing response error code------->" + stepTwoResponseMap.get("error_code"));
                    if (sessionId != null && (stepThreeResponseMap.get("error_code") == "" || stepThreeResponseMap.get("error_code").equals("")))
                    {
                        transactionLogger.debug("error code in response--------" + stepThreeResponseMap.get("error"));

                        if (stepThreeResponseMap.get("error") == "0" || stepThreeResponseMap.get("error").equals("0") && stepThreeResponseMap.get("redirecturl").equals(""))
                        {
                            status = "success";

                            commResponseVO.setStatus(status);
                            commResponseVO.setMerchantId((String) stepThreeResponseMap.get("merchanttxid"));
                            commResponseVO.setMerchantOrderId((String) stepThreeResponseMap.get("wallettxid"));
                            commResponseVO.setDescription("Transaction Approved");
                            commResponseVO.setTransactionId((String) stepThreeResponseMap.get("wallettxid"));
                            commResponseVO.setErrorCode((String) stepThreeResponseMap.get("error"));
                            commResponseVO.setRemark((String) stepThreeResponseMap.get("errormessage"));
                            commResponseVO.setTransactionStatus(status);
                            commResponseVO.setResponseHashInfo(sessionId);
                            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                        }
                        else if (stepThreeResponseMap.get("redirecturl") != null && !stepThreeResponseMap.get("redirecturl").equals(""))
                        {
                            status = "pending3DConfirmation";
                            commResponseVO.setStatus(status);
                            System.out.println("3d url---" + stepThreeResponseMap.get("redirecturl"));
                            transactionLogger.error("3d AuxPay url---" + stepThreeResponseMap.get("redirecturl"));
                            transactionLogger.error("3d AuxPay url---" + URLDecoder.decode(stepThreeResponseMap.get("redirecturl")));
                            commResponseVO.setUrlFor3DRedirect(URLDecoder.decode(stepThreeResponseMap.get("redirecturl")));
                        }
                        else
                        {
                            //step 3 error
                            transactionLogger.debug("inside else 3rd step--status--" + status);
                            commResponseVO.setStatus(status);
                            commResponseVO.setErrorCode((String) stepThreeResponseMap.get("error"));
                            commResponseVO.setRemark("Transaction Declined");
                            commResponseVO.setDescription((String) stepThreeResponseMap.get("errormessage"));
                        }

                        commResponseVO.setTransactionType("sale");
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Date date = new Date();
                        commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                        transactionLogger.debug("inside else 3rd step--status--" + status + "===" + stepThreeResponseMap.get("errormessage"));
                    }
                    else
                    {
                        transactionLogger.debug("inside else 2nd step----");
                        commResponseVO.setStatus("fail");
                        commResponseVO.setErrorCode((String) stepTwoResponseMap.get("error_code"));
                        commResponseVO.setRemark("Transaction Declined");
                        commResponseVO.setDescription((String) stepOneResponseMap.get("error_msg"));
                    }
                }
                else
                {
                    transactionLogger.debug("inside else 2nd step----");
                    commResponseVO.setStatus("fail");
                    commResponseVO.setErrorCode((String) stepTwoResponseMap.get("error_code"));
                    commResponseVO.setRemark("Transaction Declined");
                    commResponseVO.setDescription((String) stepOneResponseMap.get("error_msg"));
                }
            }

            //StoreNewCard
            else if("storenewcard".equalsIgnoreCase(GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH()))
            {
                String storeCardRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<wallet>\n" +
                        "<action>StoreNewCard</action>\n" +
                        "<sessionid>" + sessionId + "</sessionid>\n" +
                        "<cardtype>" + cardDetailsRequestVO.getCardType() + "</cardtype>\n" +
                        "<cardnumber>" + cardDetailsRequestVO.getCardNum() + "</cardnumber>\n" +
                        "<nameoncard>" + addressDetailsRequestVO.getFirstname() + " " + addressDetailsRequestVO.getLastname() + "</nameoncard>\n" +
                        "<startmonth>-</startmonth>\n" +
                        "<startyear>-</startyear>\n" +
                        "<expirymonth>" + cardDetailsRequestVO.getExpMonth() + "</expirymonth>\n" +
                        "<expiryyear>" + cardDetailsRequestVO.getExpYear() + "</expiryyear>\n" +
                        "<issuenumber></issuenumber>\n" +
                        "<cardnickname>-</cardnickname>\n" +
                        "<savecard>0</savecard>\n" +
                        "</wallet>";
                String storeCardRequestlog = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<wallet>\n" +
                        "<action>StoreNewCard</action>\n" +
                        "<sessionid>" + sessionId + "</sessionid>\n" +
                        "<cardtype>" + cardDetailsRequestVO.getCardType() + "</cardtype>\n" +
                        "<cardnumber>" + functions.maskingPan(cardDetailsRequestVO.getCardNum()) + "</cardnumber>\n" +
                        "<nameoncard>" + addressDetailsRequestVO.getFirstname() + " " + addressDetailsRequestVO.getLastname() + "</nameoncard>\n" +
                        "<startmonth>-</startmonth>\n" +
                        "<startyear>-</startyear>\n" +
                        "<expirymonth>" + functions.maskingNumber(cardDetailsRequestVO.getExpMonth()) + "</expirymonth>\n" +
                        "<expiryyear>" + functions.maskingNumber(cardDetailsRequestVO.getExpYear()) + "</expiryyear>\n" +
                        "<issuenumber></issuenumber>\n" +
                        "<cardnickname>-</cardnickname>\n" +
                        "<savecard>0</savecard>\n" +
                        "</wallet>";


                transactionLogger.error("Card Request-----" + trackingID + "--" +storeCardRequestlog);

                String storeCardResponse = AuxPayUtills.doPostHTTPSURLConnection(TESTURL, storeCardRequest);

                transactionLogger.error("Card Response-----" +trackingID + "--" + storeCardResponse);
                Map<String, String> stepStoreCardResponseMap = AuxPayUtills.readStoreCardResponse(storeCardResponse);

                transactionLogger.debug("stepStoreCardResponseMap---"+stepStoreCardResponseMap.get("error"));
                if (stepStoreCardResponseMap.get("error") == "0" || stepStoreCardResponseMap.get("error").equals("0") && sessionId!=null)
                {
                    String payCardVoucherRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?> \n" +
                            "<wallet> \n" +
                            "<action>PaymentCardVoucher</action> \n" +
                            "<sessionid>" + sessionId + "</sessionid>\n" +
                            "<customercardid>"+stepStoreCardResponseMap.get("customercardid")+"</customercardid> \n" +
                            "<merchanttxid>"+trackingID+"</merchanttxid> \n" +
                            "<securitycode>"+cardDetailsRequestVO.getcVV()+"</securitycode> \n" +
                            "<amount>" + transDetailsRequestVO.getAmount() + "</amount>\n" +
                            "<currency>" + transDetailsRequestVO.getCurrency() + "</currency>\n" +
                            //"<threedstatusurl>"+STATUS_URL+"</threedstatusurl>\n" +
                            "<threedstatusurl></threedstatusurl>\n" +
                            "<threedreturnurl>" + NOTIFICATION_URL + "</threedreturnurl>\n" +
                            "</wallet>";
                    String payCardVoucherRequestlog = "<?xml version=\"1.0\" encoding=\"utf-8\"?> \n" +
                            "<wallet> \n" +
                            "<action>PaymentCardVoucher</action> \n" +
                            "<sessionid>" + sessionId + "</sessionid>\n" +
                            "<customercardid>"+stepStoreCardResponseMap.get("customercardid")+"</customercardid> \n" +
                            "<merchanttxid>"+trackingID+"</merchanttxid> \n" +
                            "<securitycode>"+functions.maskingNumber(cardDetailsRequestVO.getcVV())+"</securitycode> \n" +
                            "<amount>" + transDetailsRequestVO.getAmount() + "</amount>\n" +
                            "<currency>" + transDetailsRequestVO.getCurrency() + "</currency>\n" +
                            //"<threedstatusurl>"+STATUS_URL+"</threedstatusurl>\n" +
                            "<threedstatusurl></threedstatusurl>\n" +
                            "<threedreturnurl>" + NOTIFICATION_URL + "</threedreturnurl>\n" +
                            "</wallet>";



                    transactionLogger.error("pay CardVoucher Requestt-----" + trackingID + "--" +payCardVoucherRequestlog);

                    String voucherCardResponse = AuxPayUtills.doPostHTTPSURLConnection(TESTURL, payCardVoucherRequest);

                    transactionLogger.error("VoucherCard Response-----" + trackingID + "--" +voucherCardResponse);

                    Map<String, String> voucherCardResponseMap = AuxPayUtills.readStepThreeResponse(voucherCardResponse);
                    String status = "fail";
                    transactionLogger.debug("XML billing response error code------->" + stepTwoResponseMap.get("error_code"));

                    if (voucherCardResponseMap.get("error") == "0" || voucherCardResponseMap.get("error").equals("0") && voucherCardResponseMap.get("redirecturl").equals(""))
                    {
                        status = "success";

                        commResponseVO.setStatus(status);
                        commResponseVO.setMerchantId((String) voucherCardResponseMap.get("merchanttxid"));
                        //commResponseVO.setMerchantOrderId((String) voucherCardResponseMap.get("wallettxid"));
                        commResponseVO.setDescription("Transaction Approved");
                        commResponseVO.setTransactionId((String) voucherCardResponseMap.get("wallettxid"));//Voucher Code
                        commResponseVO.setRemark((String) voucherCardResponseMap.get("voucherpin") + "---" + (String) voucherCardResponseMap.get("vouchercode"));//Voucher Pin
                        commResponseVO.setErrorCode((String) voucherCardResponseMap.get("error"));
                        //commResponseVO.setRemark((String) voucherCardResponseMap.get("errormessage"));
                        commResponseVO.setTransactionStatus(status);
                        commResponseVO.setResponseHashInfo(sessionId);
                        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    }
                    else if (voucherCardResponseMap.get("redirecturl") != null && !voucherCardResponseMap.get("redirecturl").equals(""))
                    {
                        status = "pending3DConfirmation";
                        commResponseVO.setStatus(status);
                        System.out.println("3d url---" + voucherCardResponseMap.get("redirecturl"));
                        transactionLogger.error("3d AuxPay url---" + voucherCardResponseMap.get("redirecturl"));
                        transactionLogger.error("3d AuxPay url---" + URLDecoder.decode(voucherCardResponseMap.get("redirecturl")));
                        commResponseVO.setUrlFor3DRedirect(URLDecoder.decode(voucherCardResponseMap.get("redirecturl")));
                    }
                    else
                    {
                        //step 3 error
                        transactionLogger.debug("inside else 3rd step--status--" + status);
                        commResponseVO.setStatus(status);
                        commResponseVO.setErrorCode((String) voucherCardResponseMap.get("error"));
                        commResponseVO.setRemark("Transaction Declined");
                        commResponseVO.setDescription((String) voucherCardResponseMap.get("errormessage"));
                    }

                    commResponseVO.setTransactionType("sale");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    transactionLogger.debug("inside else 3rd step--status--" + status + "===" + voucherCardResponseMap.get("errormessage"));
                }
                else
                {
                    transactionLogger.debug("inside else 1st step----");
                    commResponseVO.setStatus("fail");
                    commResponseVO.setErrorCode((String) stepOneResponseMap.get("error_code"));
                    commResponseVO.setRemark("Transaction Declined");
                    commResponseVO.setDescription((String) stepOneResponseMap.get("error_msg"));
                }
            }
        }
        else
        {
            transactionLogger.debug("inside else 1st step----");
            commResponseVO.setStatus("fail");
            commResponseVO.setErrorCode((String) stepOneResponseMap.get("error_code"));
            commResponseVO.setRemark("Transaction Declined");
            commResponseVO.setDescription((String) stepOneResponseMap.get("error_msg"));
        }

        transactionLogger.error("Authentication Response------->" +trackingID + "--" + stepOneResponse);

            return commResponseVO;
    }

    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        GenericAddressDetailsVO addressDetailsRequestVO =commRequestVO.getAddressDetailsVO();
        CommTransactionDetailsVO transactionDetailsVO=commRequestVO.getTransDetailsVO();

        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();//auxdirect_no3d
        String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();//testpass

        String stepOneRequest = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<wallet>\n" +
                "<version>2.0</version>\n" +
                "<action>CreateSession</action>\n" +
                "<username>"+merchantId+"</username>\n" +
                "<password>"+password+"</password>\n" +
                "</wallet>";

        transactionLogger.error("Authentication request------->" + trackingID + "--" +stepOneRequest);

        String stepOneResponse = AuxPayUtills.doPostHTTPSURLConnection(TESTURL, stepOneRequest);

        transactionLogger.error("Authentication Response------->" + trackingID + "--" +stepOneResponse);

        Map<String, String> stepOneResponseMap = AuxPayUtills.readStepOneResponse(stepOneResponse);
        String sessionId = "";
        transactionLogger.debug("session id----" + stepOneResponseMap.get("sessionid"));
        if ((stepOneResponseMap.get("error_code") == "" || stepOneResponseMap.get("error_code").equals("")) && stepOneResponseMap.get("sessionid") != null)
        {
            transactionLogger.debug("inside if before 2nd ste----");
            //2nd step(Merchant Registration)
            sessionId = stepOneResponseMap.get("sessionid");

            String billingRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<wallet>\n" +
                    "<action>CustomerBillingInfo</action>\n" +
                    "<sessionid>"+sessionId+"</sessionid>\n" +
                    "<extusername>-</extusername>\n" +
                    "<title>-</title>\n" +
                    "<firstname>-</firstname>\n" +
                    "<lastname>-</lastname>\n" +
                    "<dateofbirth>-</dateofbirth>\n" +
                    "<address>-</address>\n" +
                    "<address2>-</address2>\n" +
                    "<telephone>-</telephone>\n" +
                    "<postcode>-</postcode>\n" +
                    "<city>-</city>\n" +
                    "<state>-</state>\n" +
                    "<country>"+addressDetailsRequestVO.getCountry()+"</country>\n" +
                    "<userip>-</userip>\n" +
                    "<email>"+addressDetailsRequestVO.getEmail()+"</email>\n" +
                    "</wallet>";

            transactionLogger.error("XML billing request------->" + trackingID + "--" +billingRequest);

            String billingResponse = AuxPayUtills.doPostHTTPSURLConnection(TESTURL, billingRequest);

            transactionLogger.error("XML billing response------->" +trackingID + "--" + billingResponse);

            Map<String, String> stepTwoResponseMap = AuxPayUtills.readStepTwoResponse(billingResponse);
            transactionLogger.debug("XML billing response error code------->" + stepTwoResponseMap.get("error_code"));
            transactionLogger.debug("XML billing response sessionid------->" + stepTwoResponseMap.get("sessionid"));
            if (sessionId != null && (stepTwoResponseMap.get("error_code") == "" || stepTwoResponseMap.get("error_code").equals("")))
            {
                transactionLogger.debug("inside if before 3rd ste----");
                //3rd Step(Card Refund)
                String cardRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<wallet>\n" +
                        "<action>RefundCreditCard</action>\n" +
                        "<sessionid>"+sessionId+"</sessionid>\n" +
                        "<merchanttxid>"+trackingID+"</merchanttxid>\n" +
                        "<wallettxid>"+transactionDetailsVO.getPreviousTransactionId()+"</wallettxid>\n" +
                        "<amount>"+transactionDetailsVO.getAmount()+"</amount>\n" +
                        "<currency>"+transactionDetailsVO.getCurrency()+"</currency>\n" +
                        "</walle>";

                transactionLogger.error("card request step 3---" + cardRequest);

                String cardResponse = AuxPayUtills.doPostHTTPSURLConnection(TESTURL, cardRequest);
                transactionLogger.error("Card Response-----" + cardResponse);

                Map<String, String> stepThreeResponseMap = AuxPayUtills.readStepThreeResponse(cardResponse);
                String status = "fail";
                transactionLogger.debug("XML billing response error code------->" + stepTwoResponseMap.get("error_code"));
                /*if (stepThreeResponseMap.get("error_code") == null && stepThreeResponseMap.get("error_code") == "" && stepThreeResponseMap.get("error_code").equals(""))
                {*/
                transactionLogger.debug("error code in response--------"+stepThreeResponseMap.get("error"));

                if (stepThreeResponseMap.get("error")=="0" || stepThreeResponseMap.get("error").equals("0"))
                {
                    status = "success";

                    commResponseVO.setStatus(status);
                    commResponseVO.setMerchantId((String) stepThreeResponseMap.get("merchanttxid"));
                    commResponseVO.setMerchantOrderId((String) stepThreeResponseMap.get("wallettxid"));
                    commResponseVO.setResponseTime((String) stepThreeResponseMap.get("txtimestamp"));
                    commResponseVO.setAmount((String) stepThreeResponseMap.get("amount"));
                    commResponseVO.setTransactionId((String) stepThreeResponseMap.get("wallettxid"));
                    commResponseVO.setErrorCode((String) stepThreeResponseMap.get("error"));
                    commResponseVO.setRemark((String) stepThreeResponseMap.get("errormessage"));
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setResponseHashInfo(sessionId);
                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                }
                else
                {
                    //step 3 error
                    transactionLogger.debug("inside else 3rd step refund--status--"+status);
                    commResponseVO.setStatus(status);
                    commResponseVO.setErrorCode((String) stepThreeResponseMap.get("error"));
                    commResponseVO.setRemark("Transaction Declined");
                    commResponseVO.setDescription((String) stepThreeResponseMap.get("errormessage"));
                }
                commResponseVO.setTransactionType("sale");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                transactionLogger.debug("inside else 3rd step refund--status--"+status);
            }
            else
            {
                transactionLogger.debug("inside else 2nd step----");
                commResponseVO.setStatus("fail");
                commResponseVO.setErrorCode((String) stepTwoResponseMap.get("error_code"));
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription((String) stepOneResponseMap.get("error_msg"));
            }
        }
        else
        {
            transactionLogger.debug("inside else 1st step----");
            commResponseVO.setStatus("fail");
            commResponseVO.setErrorCode((String) stepOneResponseMap.get("error_code"));
            commResponseVO.setRemark("Transaction Declined");
            commResponseVO.setDescription((String) stepOneResponseMap.get("error_msg"));
        }
        return commResponseVO;
    }

    public GenericResponseVO processInquiry(String trackingId,GenericRequestVO requestVO)throws PZTechnicalViolationException
    {
        transactionLogger.debug("Entering in process inquiry method:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CommTransactionDetailsVO transDetailsRequestVO = commRequestVO.getTransDetailsVO();
        //GenericCardDetailsVO cardDetailsRequestVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsRequestVO =commRequestVO.getAddressDetailsVO();

        String merchantId = GatewayAccountService.getGatewayAccount(accountId).getMerchantId();//auxdirect_no3d
        String password = GatewayAccountService.getGatewayAccount(accountId).getPassword();//testpass

        String stepOneRequest = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<wallet>\n" +
                "<version>2.0</version>\n" +
                "<action>CreateSession</action>\n" +
                "<username>"+merchantId+"</username>\n" +
                "<password>"+password+"</password>\n" +
                "</wallet>";

        transactionLogger.error("Authentication request------->" + trackingId + "--" +stepOneRequest);

        String stepOneResponse = AuxPayUtills.doPostHTTPSURLConnection(TESTURL, stepOneRequest);

        transactionLogger.error("Authentication Response------->" +trackingId + "--" + stepOneResponse);

        Map<String, String> stepOneResponseMap = AuxPayUtills.readStepOneResponse(stepOneResponse);
        String sessionId = "";
        transactionLogger.debug("session id----" + stepOneResponseMap.get("sessionid"));
        if ((stepOneResponseMap.get("error_code") == "" || stepOneResponseMap.get("error_code").equals("")) && stepOneResponseMap.get("sessionid") != null)
        {
            transactionLogger.debug("inside if before 2nd ste----");
            //2nd step(Merchant Registration)
            sessionId = stepOneResponseMap.get("sessionid");

            String billingRequest = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                    "<wallet>\n" +
                    "<action>CustomerBillingInfo</action>\n" +
                    "<sessionid>"+sessionId+"</sessionid>\n" +
                    "<extusername>-</extusername>\n" +
                    "<title>-</title>\n" +
                    "<firstname></firstname>\n" +
                    "<lastname>-</lastname>\n" +
                    "<dateofbirth>-</dateofbirth>\n" +
                    "<address>-</address>\n" +
                    "<address2>-</address2>\n" +
                    "<telephone>-</telephone>\n" +
                    "<postcode>-</postcode>\n" +
                    "<city>-</city>\n" +
                    "<state>-</state>\n" +
                    "<country>"+addressDetailsRequestVO.getCountry()+"</country>\n" +
                    "<userip>-</userip>\n" +
                    "<email>"+addressDetailsRequestVO.getEmail()+"</email>\n" +
                    "</wallet>";

            transactionLogger.error("XML billing request------->" + trackingId + "--" +billingRequest);

            String billingResponse = AuxPayUtills.doPostHTTPSURLConnection(TESTURL, billingRequest);

            transactionLogger.error("XML billing response------->" + trackingId + "--" +billingResponse);

            Map<String, String> stepTwoResponseMap = AuxPayUtills.readStepTwoResponse(billingResponse);
            transactionLogger.debug("XML billing response error code------->" + stepTwoResponseMap.get("error_code"));
            transactionLogger.debug("XML billing response sessionid------->" + stepTwoResponseMap.get("sessionid"));
            if (sessionId != null && (stepTwoResponseMap.get("error_code") == "" || stepTwoResponseMap.get("error_code").equals("")))
            {
                transactionLogger.debug("inside if before 3rd ste----");
                //3rd Step(Transaction Inquiry)
                String transactionStatus = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<wallet>\n" +
                        "<action>TransactionStatus</action>\n" +
                        "<sessionid>"+sessionId+"</sessionid>\n" +
                        "<merchanttxid>"+trackingId+"</merchanttxid>\n" +
                        "<wallettxid>"+transDetailsRequestVO.getPreviousTransactionId()+"</wallettxid>\n" +
                        "</wallet>";

                transactionLogger.error("Transaction status request------->" + trackingId + "--" +transactionStatus);

                String transactionResponse = AuxPayUtills.doPostHTTPSURLConnection(TESTURL, transactionStatus);

                transactionLogger.error("Transaction status response------->" +trackingId + "--" + transactionResponse);

                Map<String, String> stepThreeResponseMap = AuxPayUtills.readStepThreeResponse(transactionResponse);
                String status = "fail";
                transactionLogger.debug("Transaction status response error code------->" + stepTwoResponseMap.get("error_code"));
                if (sessionId != null && (stepThreeResponseMap.get("error_code") == "" || stepThreeResponseMap.get("error_code").equals("")))
                {
                    transactionLogger.debug("error code in response--------" + stepThreeResponseMap.get("error"));

                    if (stepThreeResponseMap.get("error").equals("0"))
                    {
                        status = "success";

                        commResponseVO.setStatus(status);

                        commResponseVO.setMerchantId((String) stepThreeResponseMap.get("merchanttxid"));
                        commResponseVO.setMerchantOrderId((String) stepThreeResponseMap.get("wallettxid"));
                        commResponseVO.setResponseTime((String) stepThreeResponseMap.get("txtimestamp"));
                        commResponseVO.setAmount((String) stepThreeResponseMap.get("amount"));
                        commResponseVO.setTransactionType((String) stepThreeResponseMap.get("transactiontype"));
                        commResponseVO.setTransactionId((String) stepThreeResponseMap.get("wallettxid"));
                        //commResponseVO.setTransactionStatus(status);
                        //commResponseVO.setErrorCode((String) stepThreeResponseMap.get("error"));
                        commResponseVO.setDescription((String) stepThreeResponseMap.get("errormessage"));
                        commResponseVO.setResponseHashInfo(sessionId);
                        commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    }
                    else
                    {
                        //step 3 error
                        transactionLogger.debug("inside else 3rd step inquiry--status--" + status+"---error-message--"+stepThreeResponseMap.get("errormessage"));
                        commResponseVO.setStatus(status);
                        commResponseVO.setErrorCode((String) stepThreeResponseMap.get("error"));
                        commResponseVO.setRemark("Transaction Declined");
                        commResponseVO.setDescription((String) stepThreeResponseMap.get("errormessage"));
                    }
                    commResponseVO.setTransactionType("status");
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                    transactionLogger.debug("inside else 3rd step inquiry--status--" + status+"---error-message--"+stepThreeResponseMap.get("errormessage"));
                }
                else
                {
                    transactionLogger.debug("inside error response else 3rd step----");
                    commResponseVO.setStatus("fail");
                    commResponseVO.setErrorCode((String) stepThreeResponseMap.get("error_code"));
                    commResponseVO.setRemark("Transaction Declined");
                    commResponseVO.setDescription((String) stepThreeResponseMap.get("error_msg"));
                }
            }
            else
            {
                transactionLogger.debug("inside else 2nd step----");
                commResponseVO.setStatus("fail");
                commResponseVO.setErrorCode((String) stepTwoResponseMap.get("error_code"));
                commResponseVO.setRemark("Transaction Declined");
                commResponseVO.setDescription((String) stepOneResponseMap.get("error_msg"));
            }
        }
        else
        {
            transactionLogger.debug("inside else 1st step----");
            commResponseVO.setStatus("fail");
            commResponseVO.setErrorCode((String) stepOneResponseMap.get("error_code"));
            commResponseVO.setRemark("Transaction Declined");
            commResponseVO.setDescription((String) stepOneResponseMap.get("error_msg"));
        }
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("AuxPayPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }



}
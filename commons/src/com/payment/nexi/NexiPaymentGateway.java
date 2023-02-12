package com.payment.nexi;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.codehaus.jettison.json.JSONObject;

import java.util.*;

/**
 * Created by Admin on 5/23/2019.
 */
public class NexiPaymentGateway extends AbstractPaymentGateway
{
    private static TransactionLogger transactionLogger = new TransactionLogger(NexiPaymentGateway.class.getName());

    private static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.nexi");

    public static final String GATEWAY_TYPE = "nexi";
    //private String apikey = "ALIAS_WEB_00015678";
    //private String macKey = "Z189ABW25IK60YTRG43XZ35AX55FX0Z2";

    Functions functions=new Functions();

    public NexiPaymentGateway(String accountid){this.accountId=accountid;}

    @Override
    public String getMaxWaitDays() {return null;}

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        CommAccountInfoVO commAccountInfoVO = commRequestVO.getCommAccountInfoVO();
        String attemptThreeD = commRequestVO.getAttemptThreeD();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        NexiUtils nexiUtils = new NexiUtils();

        boolean isTest = gatewayAccount.isTest();
        String is3dSupported = gatewayAccount.get_3DSupportAccount();
        String isRecurring = gatewayAccount.getIsRecurring();
        String codiceGruppo=gatewayAccount.getMerchantId();
        String email_id = addressDetailsVO.getEmail();
        String chAccDate="";
        String chAccChangeDate="";
        String chAccPwChangeDate="";
        String destinationAddressUsageDate="";
        String paymentAccAgeDate="";
        if (commAccountInfoVO !=null)
        {
            chAccDate = commAccountInfoVO.getAccActivationDate();
            chAccChangeDate = commAccountInfoVO.getAccChangeDate();
            chAccPwChangeDate = commAccountInfoVO.getAccPwChangeDate();
            destinationAddressUsageDate = commAccountInfoVO.getAddressUseDate();
            paymentAccAgeDate = commAccountInfoVO.getPaymentAccActivationDate();
        }
        JSONObject req=new JSONObject();
        JSONObject res=new JSONObject();
        JSONObject informazioniSicurezza = new JSONObject();
        JSONObject buyer=new JSONObject();
        JSONObject destinationAddress=new JSONObject();
        JSONObject billingAddress=new JSONObject();
        JSONObject cardHolderAcctInfo=new JSONObject();
        JSONObject merchantRiskIndicator=new JSONObject();
        HashMap value = nexiUtils.getTransactionCount(codiceGruppo);
        try
        {
            transactionLogger.error("attemptThreeD:" + attemptThreeD);
            String pan = cardDetailsVO.getCardNum();
            String scadenza = cardDetailsVO.getExpYear()+cardDetailsVO.getExpMonth();
            String cvv = cardDetailsVO.getcVV();
            String importo = transDetailsVO.getAmount().replace(".", "");
            String divisa = CurrencyCodeISO4217.getNumericCurrencyCode(transDetailsVO.getCurrency());
            String codiceTransazione = trackingID;
            String member_id = gatewayAccount.getMerchantId();
            boolean isThreeD=false;

            String stringaMac="";
            String url="";
            long timeStamp = System.currentTimeMillis();

            String apiKey3D = "";
            String mac3D = "";
            String apiKeyNon3D = "";
            String macNon3D = "";

            Hashtable dataHash = NexiUtils.getValuesFromDb(accountId);

            apiKey3D = (String)dataHash.get("3d_api_key");
            mac3D = (String)dataHash.get("3d_mac");
            apiKeyNon3D = (String)dataHash.get("non3d_api_key");
            macNon3D = (String)dataHash.get("non3d_mac");

            //Direct - Non 3D "N"
            //3D - enrolled card "Y"
            //Only3D - 3D secure "O"
            if (("Only3D".equalsIgnoreCase(attemptThreeD)) || "O".equals(is3dSupported))
            {
                //informazioniSicurezza.append("transType","");
                if(commRequestVO.getAddressDetailsVO() != null)
                {
                    buyer.put("email", commRequestVO.getAddressDetailsVO().getEmail());
                    buyer.put("msisdn", commRequestVO.getAddressDetailsVO().getPhone());
                    buyer.put("account", commRequestVO.getCustomerId());//Customer ID
                    transactionLogger.error("account --->"+commRequestVO.getCustomerId());

                    destinationAddress.put("city", commRequestVO.getAddressDetailsVO().getCity());
                    destinationAddress.put("countryCode", commRequestVO.getAddressDetailsVO().getCountry());
                    destinationAddress.put("street", commRequestVO.getAddressDetailsVO().getStreet());
                    destinationAddress.put("street2", commRequestVO.getAddressDetailsVO().getStreet());
                    destinationAddress.put("street3", commRequestVO.getAddressDetailsVO().getStreet());
                    destinationAddress.put("postalCode", commRequestVO.getAddressDetailsVO().getZipCode());
                    //destinationAddress.put("State", commRequestVO.getAddressDetailsVO().getState());

                    billingAddress.put("city", commRequestVO.getAddressDetailsVO().getCity());
                    billingAddress.put("countryCode", commRequestVO.getAddressDetailsVO().getCountry());
                    billingAddress.put("street", commRequestVO.getAddressDetailsVO().getStreet());
                    billingAddress.put("street2", commRequestVO.getAddressDetailsVO().getStreet());
                    billingAddress.put("street3", commRequestVO.getAddressDetailsVO().getStreet());
                    billingAddress.put("postalCode", commRequestVO.getAddressDetailsVO().getZipCode());
                    // billingAddress.put("State", commRequestVO.getAddressDetailsVO().getState());
                    if (functions.isValueNull(chAccDate))
                    {
                        cardHolderAcctInfo.put("chAccDate", chAccDate);
                        cardHolderAcctInfo.put("chAccAgeIndicator", nexiUtils.getnum(chAccDate));
                    }

                    if (functions.isValueNull(chAccChangeDate))
                    {
                        cardHolderAcctInfo.put("chAccChangeDate", chAccChangeDate);
                        cardHolderAcctInfo.put("chAccChangeIndicator", nexiUtils.getnumber(chAccChangeDate));
                        transactionLogger.error("chAccChangeIndicator is --- >" + nexiUtils.getnumber(chAccChangeDate));
                    }
                    if (functions.isValueNull(chAccPwChangeDate))
                    {
                        cardHolderAcctInfo.put("chAccPwChangeDate", chAccPwChangeDate);
                        cardHolderAcctInfo.put("chAccPwChangeIndicator", nexiUtils.getnum(chAccPwChangeDate));
                        transactionLogger.error("chAccPwChangeIndicator is --- >" + nexiUtils.getnum(chAccPwChangeDate));
                    }
                    if (functions.isValueNull(destinationAddressUsageDate))
                    {
                        cardHolderAcctInfo.put("destinationAddressUsageDate", destinationAddressUsageDate);
                        cardHolderAcctInfo.put("destinationAddressUsageIndicator", nexiUtils.getnumber(destinationAddressUsageDate));
                    }
                    cardHolderAcctInfo.put("destinationNameIndicator","01");
                    cardHolderAcctInfo.put("nbPurchaseAccount",value.get("month"));//trxn in last 6 months
                    cardHolderAcctInfo.put("txnActivityDay",value.get("day"));//trxn in last 1 day
                    cardHolderAcctInfo.put("txnActivityYear",value.get("year"));//trxn in last 1 year
                    cardHolderAcctInfo.put("suspiciousAccActivity","01");
                    //cardHolderAcctInfo.put("provisionAttemptsDay","03");
                    if (functions.isValueNull(paymentAccAgeDate))
                    {
                        cardHolderAcctInfo.put("paymentAccAgeDate", paymentAccAgeDate);
                        cardHolderAcctInfo.put("paymentAccIndicator", nexiUtils.getnum(paymentAccAgeDate));
                    }

                    merchantRiskIndicator.put("deliveryEmail",email_id);
                    merchantRiskIndicator.put("deliveryTimeframe","01");

                    informazioniSicurezza.put("transType","10");
                    informazioniSicurezza.put("buyer",buyer);
                    informazioniSicurezza.put("destinationAddress",destinationAddress);
                    informazioniSicurezza.put("billingAddress",billingAddress);
                    informazioniSicurezza.put("cardHolderAcctInfo",cardHolderAcctInfo);
                    informazioniSicurezza.put("merchantRiskIndicator",merchantRiskIndicator);

                }

                stringaMac="apiKey="+apiKey3D+"codiceTransazione="+codiceTransazione+"divisa="+divisa+"importo="+importo+"timeStamp="+timeStamp+mac3D;

                String redirectMethod = commRequestVO.getTransDetailsVO().getRedirectMethod();
                String sRedirectMethod = "";
                if(functions.isValueNull(redirectMethod))
                    sRedirectMethod = "&method="+redirectMethod;

                req.put("apiKey", apiKey3D);//MID
                req.put("cvv", cvv);
                req.put("urlRisposta", RB.getString("TERM_URL") + codiceTransazione+sRedirectMethod);//Host Url
                if(isTest)
                    req.put("informazioniSicurezza", informazioniSicurezza);

                if("Y".equalsIgnoreCase(isRecurring))
                    url = RB.getString("AUTHORIZATION_3D_RECURRING_URL");
                else
                    url= RB.getString("AUTHORIZATION_3D_URL");
                isThreeD=true;
            }
            else if("Y".equals(is3dSupported) || ("3D".equalsIgnoreCase(attemptThreeD)))
            {
                if("3D".equalsIgnoreCase(attemptThreeD) || "Only3D".equalsIgnoreCase(attemptThreeD))
                {
                    //3D
                    //3Ds 2.0 parameters
                    if(commRequestVO.getAddressDetailsVO() != null)
                    {
                        buyer.put("email", commRequestVO.getAddressDetailsVO().getEmail());
                        buyer.put("msisdn", commRequestVO.getAddressDetailsVO().getPhone());
                        buyer.put("account", commRequestVO.getCustomerId());//Customer ID
                        transactionLogger.error("account --->"+commRequestVO.getCustomerId());

                        destinationAddress.put("city", commRequestVO.getAddressDetailsVO().getCity());
                        destinationAddress.put("countryCode", commRequestVO.getAddressDetailsVO().getCountry());
                        destinationAddress.put("street", commRequestVO.getAddressDetailsVO().getStreet());
                        destinationAddress.put("street2", commRequestVO.getAddressDetailsVO().getStreet());
                        destinationAddress.put("street3", commRequestVO.getAddressDetailsVO().getStreet());
                        destinationAddress.put("postalCode", commRequestVO.getAddressDetailsVO().getZipCode());
                        //destinationAddress.put("State", commRequestVO.getAddressDetailsVO().getState());

                        billingAddress.put("city", commRequestVO.getAddressDetailsVO().getCity());
                        billingAddress.put("countryCode", commRequestVO.getAddressDetailsVO().getCountry());
                        billingAddress.put("street", commRequestVO.getAddressDetailsVO().getStreet());
                        billingAddress.put("street2", commRequestVO.getAddressDetailsVO().getStreet());
                        billingAddress.put("street3", commRequestVO.getAddressDetailsVO().getStreet());
                        billingAddress.put("postalCode", commRequestVO.getAddressDetailsVO().getZipCode());
                        // billingAddress.put("State", commRequestVO.getAddressDetailsVO().getState());
                        if (functions.isValueNull(chAccDate))
                        {
                            cardHolderAcctInfo.put("chAccDate", chAccDate);
                            cardHolderAcctInfo.put("chAccAgeIndicator", nexiUtils.getnum(chAccDate));
                        }
                        if (functions.isValueNull(chAccChangeDate))
                        {
                            cardHolderAcctInfo.put("chAccChangeDate", chAccChangeDate);
                            cardHolderAcctInfo.put("chAccChangeIndicator", nexiUtils.getnumber(chAccChangeDate));
                            transactionLogger.error("chAccChangeIndicator is --- >" + nexiUtils.getnumber(chAccChangeDate));
                        }
                        if (functions.isValueNull(chAccPwChangeDate))
                        {
                            cardHolderAcctInfo.put("chAccPwChangeDate", chAccPwChangeDate);
                            cardHolderAcctInfo.put("chAccPwChangeIndicator", nexiUtils.getnum(chAccPwChangeDate));
                            transactionLogger.error("chAccPwChangeIndicator is --- >" + nexiUtils.getnum(chAccPwChangeDate));
                        }
                        if (functions.isValueNull(destinationAddressUsageDate))
                        {
                            cardHolderAcctInfo.put("destinationAddressUsageDate", destinationAddressUsageDate);
                            cardHolderAcctInfo.put("destinationAddressUsageIndicator", nexiUtils.getnumber(destinationAddressUsageDate));
                        }
                        cardHolderAcctInfo.put("destinationNameIndicator","01");
                        cardHolderAcctInfo.put("nbPurchaseAccount",value.get("month"));//trxn in last 6 months
                        cardHolderAcctInfo.put("txnActivityDay",value.get("day"));//trxn in last 1 day
                        cardHolderAcctInfo.put("txnActivityYear",value.get("year"));//trxn in last 1 year
                        cardHolderAcctInfo.put("suspiciousAccActivity","01");
                        //cardHolderAcctInfo.put("provisionAttemptsDay","03");
                        if (functions.isValueNull(paymentAccAgeDate))
                        {
                            cardHolderAcctInfo.put("paymentAccAgeDate", paymentAccAgeDate);
                            cardHolderAcctInfo.put("paymentAccIndicator", nexiUtils.getnum(paymentAccAgeDate));
                        }

                        merchantRiskIndicator.put("deliveryEmail",email_id);
                        merchantRiskIndicator.put("deliveryTimeframe","01");

                        informazioniSicurezza.put("transType","10");
                        informazioniSicurezza.put("buyer",buyer);
                        informazioniSicurezza.put("destinationAddress",destinationAddress);
                        informazioniSicurezza.put("billingAddress",billingAddress);
                        informazioniSicurezza.put("cardHolderAcctInfo",cardHolderAcctInfo);
                        informazioniSicurezza.put("merchantRiskIndicator",merchantRiskIndicator);

                    }
                    stringaMac="apiKey="+apiKey3D+"codiceTransazione="+codiceTransazione+"divisa="+divisa+"importo="+importo+"timeStamp="+timeStamp+mac3D;
                    String sRedirectMethod = "";
                    String redirectMethod = commRequestVO.getTransDetailsVO().getRedirectMethod();
                    if(functions.isValueNull(redirectMethod))
                        sRedirectMethod = "&method="+redirectMethod;
                    req.put("apiKey", apiKey3D);//MID
                    req.put("cvv", cvv);
                    req.put("urlRisposta", RB.getString("TERM_URL") + codiceTransazione+sRedirectMethod);//Host Url
                    if(isTest)
                        req.put("informazioniSicurezza", informazioniSicurezza);
                    if("Y".equalsIgnoreCase(isRecurring))
                        url = RB.getString("AUTHORIZATION_3D_RECURRING_URL");
                    else
                        url= RB.getString("AUTHORIZATION_3D_URL");
                    isThreeD=true;
                }
                else
                {
                    //Non 3D
                    req.put("apiKey", apiKeyNon3D);//MID
                    if("Y".equalsIgnoreCase(isRecurring))
                    {
                        stringaMac = "apiKey="+apiKeyNon3D+"numeroContratto="+codiceTransazione+"codiceTransazione="+codiceTransazione+"importo="+importo+"divisa="+divisa+"pan="+pan+"cvv="+cvv+"scadenza="+scadenza+"timeStamp="+timeStamp+macNon3D;
                        url = RB.getString("RECURRING_NON_3D_URL");
                        req.put("numeroContratto",codiceTransazione);
                        req.put("cvv", cvv);
                        req.put("codiceGruppo",codiceGruppo);
                    }
                    else
                    {
                        stringaMac = "apiKey=" + apiKeyNon3D + "codiceTransazione=" + codiceTransazione + "pan=" + pan + "scadenza=" + scadenza + "cvv=" + cvv + "importo=" + importo + "divisa=" + divisa + "timeStamp=" + timeStamp + macNon3D;
                        url = RB.getString("NON_3D_URL");
                    }

                }
            }
            else
            {
                //Non 3D
                req.put("apiKey", apiKeyNon3D);//MID
                if("Y".equalsIgnoreCase(isRecurring))
                {
                    stringaMac = "apiKey="+apiKeyNon3D+"numeroContratto="+codiceTransazione+"codiceTransazione="+codiceTransazione+"importo="+importo+"divisa="+divisa+"pan="+pan+"cvv="+cvv+"scadenza="+scadenza+"timeStamp="+timeStamp+macNon3D;
                    url = RB.getString("RECURRING_NON_3D_URL");
                    req.put("numeroContratto",codiceTransazione);
                    req.put("cvv", cvv);
                    req.put("codiceGruppo",codiceGruppo);
                }
                else
                {
                    stringaMac = "apiKey=" + apiKeyNon3D + "codiceTransazione=" + codiceTransazione + "pan=" + pan + "scadenza=" + scadenza + "cvv=" + cvv + "importo=" + importo + "divisa=" + divisa + "timeStamp=" + timeStamp + macNon3D;
                    url = RB.getString("NON_3D_URL");
                }

            }

            //String urlRisposta = //"http://localhost:8081/transaction/CommonBackEndServlet?trackingId=" + codiceTransazione;

            transactionLogger.error("Mac------->"+stringaMac);
            String calMac=NexiUtils.hashMac(stringaMac);
            req.put("codiceTransazione", codiceTransazione);//trackingid
            req.put("importo", importo);//amount
            req.put("divisa", divisa);//Currency code
            req.put("pan", pan);//card number
            req.put("scadenza", scadenza);//expiry Date
            req.put("timeStamp", String.valueOf(timeStamp));
            req.put("mac", calMac);
            String saleresponse = "";

            transactionLogger.error("Nexi reqest--------------->"+trackingID + "--" + req.toString());
            if(isTest)
            {
                saleresponse=NexiUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL")+url,req.toString());
            }
            else
            {
                saleresponse=NexiUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL")+url,req.toString());
            }
            transactionLogger.error("saleresponse ----------" + trackingID + "--" + saleresponse);
            res=new JSONObject(saleresponse);
            transactionLogger.error("saleresponse ----------" + trackingID + "--" + saleresponse);

            if(functions.isValueNull(saleresponse) && res.getString("esito").equals("OK"))
            {
                transactionLogger.error("isThreeD-->"+isThreeD);
                if(isThreeD)
                {

                    if(res.has("html"))
                    {
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(res.getString("html"));
                    }
                }
                else
                {
                    commResponseVO.setStatus("success");
                    transactionLogger.error("res.get(\"idOperazione\")-------->"+res.get("idOperazione"));
                    commResponseVO.setTransactionId((String) res.get("idOperazione"));
                    commResponseVO.setTransactionType((String) res.get("tipoTransazione"));
                    commResponseVO.setResponseTime((String) res.get("data"));
                    commResponseVO.setErrorCode((String) res.get("esito"));
                    commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
                    commResponseVO.setRemark("Transaction Successful");
                    commResponseVO.setDescription("Transaction Successful");
                }
            }
            else
            {
                commResponseVO.setStatus("failed");
                commResponseVO.setTransactionId((String) res.get("idOperazione"));
                commResponseVO.setDescription(res.getJSONObject("errore").getString("messaggio"));
                commResponseVO.setRemark(res.getJSONObject("errore").getString("messaggio"));
                commResponseVO.setCurrency(transDetailsVO.getCurrency());
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
            }


        }
        catch (Exception e)
        {
            transactionLogger.error("Exception--->",e);
        }
        return commResponseVO;
    }
    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processRefund-----");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        Hashtable dataHash = NexiUtils.getValuesFromDb(accountId);
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        JSONObject refundRequest=new JSONObject();
        JSONObject refundResponse=new JSONObject();
        String transType="";
        String apikey="";
        String secureKey="";
        String status="";
        if(functions.isValueNull(commTransactionDetailsVO.getTransactionType()))
            transType=commTransactionDetailsVO.getTransactionType();
        if("VBV_FULL".equalsIgnoreCase(transType))
        {
            apikey = (String)dataHash.get("3d_api_key");
            secureKey = (String)dataHash.get("3d_mac");
        }
        else
        {
            apikey = (String)dataHash.get("non3d_api_key");
            secureKey = (String)dataHash.get("non3d_mac");
        }
        try
        {
            String importo = commTransactionDetailsVO.getAmount().replace(".", "");
            String divisa = CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency());
            String codiceTransazione = trackingID;
            long timeStamp = System.currentTimeMillis();
            String stringaMac = "apiKey=" + apikey + "codiceTransazione=" + codiceTransazione + "divisa=" + divisa + "importo=" + importo + "timeStamp=" + timeStamp + secureKey;
            transactionLogger.error("stringMac---------->" + stringaMac);
            String macCalculated = NexiUtils.hashMac(stringaMac);

            refundRequest.put("apiKey", apikey);
            refundRequest.put("codiceTransazione", codiceTransazione);//Trackingid
            refundRequest.put("importo", importo);//amount
            refundRequest.put("divisa", divisa);//currency code
            refundRequest.put("timeStamp", String.valueOf(timeStamp));
            refundRequest.put("mac", macCalculated);
            boolean isTest = gatewayAccount.isTest();

            String refundRes = "";
            if (isTest)
            {
                refundRes = NexiUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL") + RB.getString("REFUND_URL"), refundRequest.toString());
            }
            else
            {
                refundRes = NexiUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL") + RB.getString("REFUND_URL"), refundRequest.toString());
            }
            refundResponse = new JSONObject(refundRes);
            if (refundResponse.getString("esito").equals("OK"))
            {
                status="success";
                commResponseVO.setStatus(status);
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                commResponseVO.setRemark("Transaction reverse "+status);
            }
            else
            {
                status="failed";
                commResponseVO.setStatus(status);
                commResponseVO.setRemark(refundResponse.getJSONObject("errore").getString("messaggio"));
            }
            commResponseVO.setDescription("Transaction reverse " + status);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception :::::" ,e);
        }
        return commResponseVO;
    }
    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws  PZGenericConstraintViolationException
    {
        transactionLogger.error(":::::Entering into  processQuery:::::");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        JSONObject inquiryReq=new JSONObject();
        JSONObject inquiryRes=new JSONObject();
        try
        {
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
            boolean isTest = gatewayAccount.isTest();
            Hashtable dataHash = NexiUtils.getValuesFromDb(accountId);
            String transType="";
            String apikey="";
            String secureKey="";

            transType=NexiUtils.getTransactionType(trackingID);
            if("VBV_FULL".equalsIgnoreCase(transType))
            {
                apikey = (String)dataHash.get("3d_api_key");
                secureKey = (String)dataHash.get("3d_mac");
            }
            else
            {
                apikey = (String)dataHash.get("non3d_api_key");
                secureKey = (String)dataHash.get("non3d_mac");
            }
            System.setProperty("https.protocols", "TLSv1.2");
            System.setProperty("jsse.enableSNIExtension", "false");
            String codiceTransazione = trackingID;
            long timeStamp = System.currentTimeMillis();
            String stringMac = "apiKey=" + apikey + "codiceTransazione=" + codiceTransazione + "timeStamp=" + timeStamp + secureKey;
            String calMac=NexiUtils.hashMac(stringMac);
            inquiryReq.put("apiKey", apikey);
            inquiryReq.put("codiceTransazione", codiceTransazione);
            inquiryReq.put("timeStamp", String.valueOf(timeStamp));
            inquiryReq.put("mac", calMac);
            String  response = null;
            if (isTest)
            {
                transactionLogger.debug(":::::inside isTest:::::");
                response=NexiUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL")+RB.getString("INQUIRY_URL"),inquiryReq.toString());
            }
            else
            {
                transactionLogger.debug(":::::inside Live:::::");
                response = NexiUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL")+RB.getString("INQUIRY_URL"), inquiryReq.toString());
            }
            inquiryRes=new JSONObject(response);
            String status = "";
            if (!response.equals("") && response != null){
                if (inquiryRes.getString("esito").equals("OK")){
                    status = "success";
                    //commResponseVO.setDescription((String) readStatus.get("description"));
                }
                else{
                    status = "fail";
                    // commResponseVO.setDescription((String) readStatus.get("description"));
                }
                transactionLogger.error("inquiryRes--------------->"+trackingID + "--" + inquiryRes.toString());
                JSONObject report=inquiryRes.getJSONArray("report").getJSONObject(0);
                String amount=report.getString("importo").substring(0,report.getString("importo").length()-2).concat(".00");
                commResponseVO.setStatus(status);
                commResponseVO.setMerchantId(apikey);
                commResponseVO.setMerchantOrderId((String) inquiryRes.getString("idOperazione"));
                commResponseVO.setTransactionId((String) report.getString("codiceTransazione"));
                commResponseVO.setTransactionType((String) report.getString("tipoTransazione"));
                commResponseVO.setTransactionStatus((String) report.getString("stato"));
                //commResponseVO.setResponseTime((String) readResponse.get("completion-time-stamp"));
                commResponseVO.setBankTransactionDate((String) report.getString("dataTransazione"));
                //commResponseVO.setErrorCode((String) readResponse.get("status code"));
                //commResponseVO.setRemark((String) readStatus.get("description"));
                commResponseVO.setAuthCode((String) report.getString("codiceAutorizzazione"));
                commResponseVO.setAmount(amount);
                commResponseVO.setCurrency(report.getString("divisa"));
                commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());
            }
        }
        catch(Exception e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingid, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        CommResponseVO commResponseVO=new CommResponseVO();
        JSONObject responseObj=new JSONObject();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        Hashtable dataHash = NexiUtils.getValuesFromDb(accountId);
        boolean isTest = gatewayAccount.isTest();
        String apikey=(String)dataHash.get("3d_api_key");
        String secureKey=(String)dataHash.get("3d_mac");
        String isRecurring=gatewayAccount.getIsRecurring();
        JSONObject req =new JSONObject();
        String currency=((CommRequestVO) requestVO).getTransDetailsVO().getCurrency();
        String amount=((CommRequestVO) requestVO).getTransDetailsVO().getAmount();
        String xpayNonce=((CommRequestVO) requestVO).getTransDetailsVO().getPreviousTransactionId();
        String divisa = CurrencyCodeISO4217.getNumericCurrencyCode(currency);
        String url="";
        try
        {
            long timeStamp3 = System.currentTimeMillis();
            String mac1 = "";
            req.put("apiKey", apikey);//mid
            req.put("codiceTransazione", trackingid);//trackingid
            req.put("importo", amount);//amount
            req.put("divisa", divisa);//currency code
            req.put("xpayNonce", xpayNonce);
            if ("Y".equalsIgnoreCase(isRecurring))
            {
                req.put("numeroContratto", trackingid);
                url = RB.getString("PAYMENT_3D_RECURRING_URL");
                mac1 = "apiKey=" + apikey + "numeroContratto=" + trackingid + "codiceTransazione=" + trackingid + "importo=" + amount + "divisa=" + divisa + "xpayNonce=" + xpayNonce + "timeStamp=" + timeStamp3 + secureKey;
            }
            else
            {
                url = RB.getString("PAYMENT_3D_URL");
                mac1 = "apiKey=" + apikey + "codiceTransazione=" + trackingid + "importo=" + amount + "divisa=" + divisa + "xpayNonce=" + xpayNonce + "timeStamp=" + timeStamp3 + secureKey;
            }
            mac1 = NexiUtils.hashMac(mac1);
            req.put("timeStamp", timeStamp3);
            req.put("mac", mac1);
            transactionLogger.error("Request ---" + req.toString());
            transactionLogger.error("url-------->" + url);
            String responseString = "";
            if (isTest)
            {
                responseString = NexiUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL") + url, req.toString());
            }
            else
            {
                responseString = NexiUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL") + url, req.toString());
            }
            responseObj = new JSONObject(responseString);
            transactionLogger.error("Responce 1---" + responseObj.toString());
            commResponseVO.setTransactionId(responseObj.getString("idOperazione"));
            commResponseVO.setStatus("fail");

            if ("OK".equalsIgnoreCase(responseObj.getString("esito")))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setTransactionType(responseObj.getString("tipoTransazione"));
            }
            else
                commResponseVO.setErrorName(responseObj.getJSONObject("errore").getString("messaggio"));
        }
        catch(Exception e){
            transactionLogger.error("Exception while connecting with bank", e);
            PZExceptionHandler.raiseTechnicalViolationException("WireCardNPaymentGateway.java", "processSale()", null, "common", "Technical Exception Occurred:::", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processRebilling(String trackingId,GenericRequestVO requestVO) throws PZTechnicalViolationException,PZConstraintViolationException
    {
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        Functions functions = new Functions();
        String recurringUrl=RB.getString("RECURRING_WITH_NON_3D");

        GatewayAccount account=GatewayAccountService.getGatewayAccount(accountId);
        Hashtable dataHash = NexiUtils.getValuesFromDb(accountId);

        String apikey = (String)dataHash.get("non3d_api_key");
        String macKey = (String)dataHash.get("non3d_mac");
        boolean isTest=account.isTest();
        CommTransactionDetailsVO genericTransDetailsVO = commRequestVO.getTransDetailsVO();
        transactionLogger.error("Previous trackingid---" + genericTransDetailsVO.getPreviousTransactionId());
        String expMonth = "";
        String year = "";
        if(commRequestVO.getCardDetailsVO()!=null)
        {
            String expiryDate = PzEncryptor.decryptExpiryDate(commRequestVO.getCardDetailsVO().getExpMonth());
            if(expiryDate.contains("/"))
            {
                String dateArr[] = expiryDate.split("/");
                expMonth = dateArr[0];
                year = dateArr[1];
            }

        }

        String numeroContratto=commRequestVO.getTransDetailsVO().getPreviousTransactionId();
        transactionLogger.error("numeroContratto-------->"+numeroContratto);
        String scadenza = year+expMonth;
        String importo = commRequestVO.getTransDetailsVO().getAmount().replace(".", "");
        String divisa = CurrencyCodeISO4217.getNumericCurrencyCode(commRequestVO.getTransDetailsVO().getCurrency());
        String codiceTransazione = trackingId;
        String codiceGruppo = account.getMerchantId();
        long timeStamp = System.currentTimeMillis();
        String stringaMac="apiKey="+apikey+"numeroContratto="+numeroContratto+"codiceTransazione="+codiceTransazione+"importo="+importo+"divisa="+divisa+"scadenza="+scadenza+"timeStamp="+timeStamp+macKey;

        String macCalculated = "";

        try
        {
            macCalculated = NexiUtils.hashMac(stringaMac);

            JSONObject json = new JSONObject();
            JSONObject responseObj = null;

            json.put("apiKey", apikey);
            json.put("numeroContratto", numeroContratto);//Contract Code
            json.put("codiceTransazione", codiceTransazione);//Tracking id
            json.put("importo", importo);//amouont
            json.put("divisa", divisa);//curreny code
            json.put("scadenza", scadenza);//expiry date
            json.put("codiceGruppo", codiceGruppo);//group code
            json.put("timeStamp", String.valueOf(timeStamp));
            json.put("mac", macCalculated);


            transactionLogger.error("Request ---"+json.toString());
            String responseString=null;

            if(isTest)
                responseString = NexiUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL")+recurringUrl,json.toString());
            else
                responseString = NexiUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL")+recurringUrl,json.toString());

            responseObj = new JSONObject(responseString);
            transactionLogger.error("Response ---" + responseObj.toString());

            if("OK".equals(responseObj.get("esito")))
            {
                commResponseVO.setStatus("success");
                commResponseVO.setDescription("Success");
                commResponseVO.setRemark("Success");
                commResponseVO.setResponseTime(responseObj.getString("data"));
                commResponseVO.setTransactionId(responseObj.getString("idOperazione"));
                commResponseVO.setTransactionType(responseObj.getString("tipoTransazione"));
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setDescription(responseObj.getJSONObject("errore").getString("messaggio"));
                commResponseVO.setRemark(responseObj.getJSONObject("errore").getString("messaggio"));
                commResponseVO.setTransactionId(responseObj.getString("idOperazione"));
            }
            commResponseVO.setCurrency(commRequestVO.getTransDetailsVO().getCurrency());
            commResponseVO.setTmpl_Amount(commRequestVO.getTransDetailsVO().getTemplateAmount());
            commResponseVO.setTmpl_Currency(commRequestVO.getTransDetailsVO().getTemplateCurrency());
            commResponseVO.setDescriptor(GatewayAccountService.getGatewayAccount(accountId).getDisplayName());


        }
        catch (Exception e)
        {
            transactionLogger.error("Exception------->"+e.getMessage());
        }

        return commResponseVO;
    }
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_FUNCTIONALITY_NOT_ALLOWED));
        PZGenericConstraint genConstraint = new PZGenericConstraint("BillDeskPaymentGateway", "processAuthentication", null, "common", "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint, "This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::", null);

    }
}

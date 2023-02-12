package com.payment.cybersource;

import com.cybersource.ws.client.Client;
import com.cybersource.ws.client.ClientException;
import com.cybersource.ws.client.FaultException;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.secureTrading.SecureTradingUtils;
import org.apache.commons.codec.binary.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Uday on 11/28/18.
 */
public class CyberSourcePaymentGateway extends AbstractPaymentGateway
{
    static final TransactionLogger transactionLogger= new TransactionLogger(CyberSourcePaymentGateway.class.getName());
    static final ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.cybs");

    public static  final String GATEWAY_TYPE="cybs";
    public CyberSourcePaymentGateway(String accountId){this.accountId=accountId;}

    public static void main(String[] args) throws IOException
    {
        String merchantId="";

      /*  ResourceBundle RB=null;
        if(merchantId.equalsIgnoreCase("ss170067078")){
            RB = LoadProperties.getProperty("com.directi.pg."+merchantId);

        }else if(merchantId.equalsIgnoreCase("tw170067094")){
            RB = LoadProperties.getProperty("com.directi.pg.cybs_tw170067094");

        }else if(merchantId.equalsIgnoreCase("tw170067102")){
            RB = LoadProperties.getProperty("com.directi.pg.cybs_tw170067102");
        }*/

        HashMap <String,String> request= new HashMap<>();
      // request.put("ccAuthService_run","true");
      //  request.put("payerAuthEnrollService_run","true");
       // request.put("authIndicator","1");
        request.put("ccCreditService_run","true");
       // request.put("ccCaptureService_run","true");
       // request.put("ccAuthService_authType","AUTOCAPTURE");
       // request.put("card_cardType","001");
        request.put("card_cardType","002");
        request.put("merchantReferenceCode","HBSBJFBJS");
        request.put("billTo_firstName","John");
        request.put("billTo_lastName","Doe");
        request.put("billTo_street1","1295 Charleston Road");
        request.put("billTo_city","Mountain View");
        request.put("billTo_state","CA");
        request.put("billTo_postalCode","94043");
        request.put("billTo_country","US");
        request.put("billTo_email","nobody@cybersource.com");
        request.put("billTo_ipAddress","10.7.7.7");
        request.put("billTo_phoneNumber","650-965-6000");
        request.put("card_accountNumber","5555555555554444");
       // request.put("card_accountNumber","4000000000000002");
        request.put("card_cvNumber","123");
        request.put("card_expirationMonth","12");
        request.put("card_expirationYear","2020");
        request.put("purchaseTotals_currency","USD");
        request.put("purchaseTotals_grandTotalAmount","10.00");
        request.put("invoiceHeader_merchantDescriptor", "UdayCorp");
        request.put("invoiceHeader_merchantDescriptorStreet","Malad");
      //  request.put("payerAuthEnrollService_merchantURL","http://localhost:8081/transaction/Common3DFrontEndServlet?trackingId=");
       // request.put("payerAuthEnrollService_authenticationTransactionID","kAMvSvQgsiB30Zh661p1");
       /* request.put("ccAuthService_commerceIndicator","vbv");
        request.put("ccAuthService_xid","MFUxWGdJZGZuMzZQZVRMTDhXODE=");
        request.put("ccAuthService_veresEnrolled","Y");
        request.put("ccAuthService_paresStatus","Y");
        request.put("ccAuthService_cavv","AAABAWFlmQAAAABjRWWZEEFgFz+=");*/






      /*  HashMap<String,String> request1= new HashMap<>();
        request1.put("payerAuthValidateService_run","true");
        request1.put("ccAuthService_run","true");
        request1.put("merchantReferenceCode","MRC-1211");
        request1.put("purchaseTotals_currency","USD");
        request1.put("purchaseTotals_grandTotalAmount","10.00");
        request1.put("payerAuthValidateServiceTransactionID","0U1XgIdfn36PeTLL8W8");
        request1.put("payerAuthValidateService_signedPARes","eNqdWNmS6jgSfSeCf6joeaS7vbB3UBUhr9ggg3fwmze8YBu8G3/9CKi6VX27YubO8IKUTh2llJkn015pYeH7jOq7deG/raBflnbgv0Te62+4ThwCwTtl49ne17bbhbkgfntb7YHilw+Fx2je+kKuuaFP+7XVLuz9qM+SJKMnYvmKtBu/KKNL9kb8if9JrrCPKdqocEM7q95WtptTgvQ2mS1JYrbC3qer1C8E5s3zT3adVCvsOV1hn+v29X1UIqO7yHuDnN6ZvCdavFXD3pItQ4EaEx52DPu6wu4aK8+u/DcSx+f4jJy+EJO/cOIvfLHCHvLV9Q4H0kuNsAkcx1fYV8kKXU/hZ+7tbTFBj37MVn53vWQ+0kCH+zFeYZ/GXe3sDf/7D6nepSvt8LaqovSrUcu7UcR8hT3kq7Kyq7p8O66w99HKtZvmDQBAAZNLUhnch7FimhbLcgHXj9BhHyor343e8CkyCv0/VoEkuBRRFaZ3U/8uWGF3U7CHP99WahRkaLPCf+nSJENeDKvq+heGtW37Zzv+81IEGDIYx/AlhhS8Mgr+9dtzle8J2enyPy2j7eySRa6dRL1dociAfhVevJcftn0Hoyl3JAJTWPoPBPWHS0yyP+4SfExMESb2PeiXk/3KLj8bW5T2H2Vo31MA+wnobaX4J/8eEf6Lrgivv/3rV1KDiQK/rP4fUz7M+IrwgWfYSe2/0cVOTH3JMkNyxu5h0BRMbcRGsFscXz/WPTVX2A/b3w/29OKX23oqht004CmVm3d0xRhXD7sBnbOJxmDoVNGN2xkvzcVGglsuWPiNtMicdB9zFdN11RTtruHJMjfYjBoODuUskvqMh7fW2B/2G+x2iD0q7cZuBnJec42LSoaMxYRaL1VFcTox5IL2tjtaIkxl0te3bmtFeLsdDvi4sHig2rNmAnzYi5RS4p7rnbGDoUpmEDQMuS6WtQf32anPoG7LqTNjOrLpZIJugvBYTUVo08ZwAPuS07TLbATo6WEOm3xz4eZpuK03s5EYJGNJ8NflbtmETUAtz1jYExQ5mXVpJoVkKmPirL3NLSpBSIbW1cY5azkx7JXQDmCFZ7K4pseqXtW7/Eaa4biRF7Qo8lx+OpzA2jip4PX1S1S9e2bj356eOEzxJWNX9nOk1k7su5VkI4agpVfaLrwos5MXxk8vLw8HFr+/7PTPB4BWkeBzTl9SRKeu//vL9hUivrogfVV73YXR5fcX+lVXV9jP+zw2pv2iik4orxBfQkFgzJ6mQakGoBUoEAg0UIWWkY/i5mIJYeNKQGY5SgatFbNbCM48IHSWCiFtGLBjNLClAgndF1oKKXZ5sc1Ft46Be5dS4IIIXQrdVLk6KVfK5LJyTON2JPUAjW8uw6qQAg9Eum1FeE7Ow4FlTnHbtK5QkVs2ODKGLDMMqHTrIOHCWiKOEcXIGvC5Fu8gw95g7HZSDKYSI9pIdnvK5G44+JDC9aKjeyA+bTpqIDE0KOMt3T7QBbZV9jafNFBe3E9+l63Z1pLuljhjobtHKDi+n0hjv5xIYKXGMYnETaUEKnrLPhG3bNsxx4N4ts1p6NAUJROwW2vAQff0QIEax35FURLHXAQ6maB0M27IK5QQA4kKznl4jvhli1N3LwCwo4G8APfnwwEdbNCMBTk79ohITPMwoTxSIERqz+kzd+IeY7CtS1NZsqykjLrUWSbB4XiupHjNB/5lTNldpfLIptlksibmSxTlzTGFUexTgttW2EzFfGZbK0u4hMyIGbeu6+50xDMeCM/CtWHlmald8YU81fFMyObFcBCWwlzOd4Yn4N5BBH3iTMKKvQFmo98gJ3h8cpsnLVcaWLyTcRmWFBWN1VbUGdVM4vogj3RxT9qIE8jAkQ6eQzXUmSDXQX5ghKlw9HdXPW+IujEPytWD4ujcZRC/GflEKOdjkgIqJS6ykDnuGiVbH/p8PRzgPquGbeiGcMYct6O8ySc8Ha2NdmFodL2t820pI1LeXHb9uSS63bpMZe9sh1OyxmArMEAG1GVctsOBrz1jaK1AFsQAQLrlaZPmVcDzI1ZAYSmfuJDYu7xR64QEBdZL7v48HpTEoinGOoiPyB4OjiSHWxrwHmjyBBV9WVcCXtEjx5CbsKOJAjSBXG2xgDjbQru+x5WCxxQVtNwF6CchgpFH4MMBYaqLSKJ8/AJD84zPNj37XfYywZEFkiYeWHxxaI4F5k1PAp6Ju91yWRt7B2VwsyErhpPOrq8U6lGvWIJOJ/5kd+k7sjInTCmN82mJY2Y3Na/7qGk458RqequlfBhcnHNdnMRmNJ8NB9h6PL4GhGFJbe+w07RUiCt9smgaV0TbHp3rPV1b1iSaMcUyX8Jiuyfi+hyM+g6beLi8KIKzbtH3ju9nvvqOwGicQQQWp18I7NsraH3mVwmM6e/Jd09UF6L4S5xMadyY9SHVPskqaA2Z5G4Wn9RHs2MckqiQc2NLpZjhwBmLLVSElgVPMmDA+aaayh3laqXnjtOA9k4DkOGUxB3LgZ4uG4/+O7GhcqbdqY3tJU36pDZN/iGD/PE/EhsDnsSGkJRJu37as2Eo45OcvhDYncxQ7e7YGMhPxBLSmoiacaNGmZvaiPoQEiU87gC07dbgjR5Z3XumEMg42wbW4uebp57BhyPS4kVw4dGhby0xVRCpCvYOTHiXMu01T/T6zLEvJ0HZZ7uK45PwmjezdmvzrX46jabn43UO2fQm6LBes5INGuUciWp1o5c71Iqku1kw97bMYpPxxIxeZkUXLy7UJB0jWtIn6eh0NHd8lobp/jLrQ6rJqbI59ctxyAabgzRjvY1UG8FwUOd9Zc7Adtfq0syh8o2sju+BAQAf+z01fY8fj21lGgLQbsFRPAqWAI7ORA5YiaIW5lpfto8ypul4GyikUaPYCD3eOKMbTtxHFCTxnRQghT/QmEA2KUpZRMJNHB80w9reBJybq8GNs4cD1NAUUAOnB1mokOUZYAaUwgbxHiNMJ0iOFsnzXUabdp5FYoti6KfSod9LByoZgUwt6o2OLccLzNhMaU4XIxnTdwebLNY3ogNjJyMIYkfPEmpWenI1l+JQdsONFm01uQymJyxXE4kYDrbWsUbdmrDRdhuL5YtIzIE2E8/5aHMIK0adBHF+5bLFLrZyUZmf7IJVRp4znWKi2lzrdRA7nXaag+EgbSOU/z2jixPDqKPjZqHlXHIjjcJYc8Hrr5JAEyMSiPwPEhCByHT7EZsYSriBAOdpNedVwRkzj5DUAVwH/0gdFNzfJM/XroABn13Bf+pyEAn8lz7ns8v50TWgLqd7djmc2DiIFO4pNRysQ1dC7u9gzI6hdmwlDdzMuyx+yPAfspiaQLls6aetPNuKht6zEgTlY2fUMYCW1cjw6vToZQLgTyJsW14z7wm+vP0qSQwH/4UmDEjBD5KA7yShOuQSR6spRASnD48MBx8+QUEKEBtIgKGpSN5QgUxP/OLkNIrM0ofiuJxV+7m8M3NaA2ScYdN4vMQCLMcdGLkyrQwHBTk6RfNzv7lRNzm3zuv+KE4XYwwcmo1ot3jFaVg8ZmMvOPudcU2Oqp+z++lOZ+AksNXTPF/DwNijNNnYl4xsinjdmT5tdFw06SbUxUvE7JRgWMXHGzyXmL6an7GJ5KuceXPySH7vFAjQavGzsiPffdR2QeixpZFORJP3wrC02iTNr5PIZdl/1PbvdIeDD20GPLQ1CugtcieF9d8neyCDiFtQfEJxLfQl0WejY5vZMiKmSl8XkR3rlTObErNFJZ2uPMuOi37tUNMLTFn30JWOUwkKVTfhfpFWgW8l66Teyj2eKyOXvEyT1h8OunYeJ+jNJ8+Rl9WY3mzXdbnApEs2d0fzKFf7Sl5DyvCdOTlr7X3DC86B3bqSeVk6+0DJTZ28WiHqGDY3a70E3yY79vnahP14lfp8yXp8THp87bp///j6Fezf7m01+Q==");
        request1.put("billTo_firstName","John");
        request1.put("billTo_lastName","Doe");
        request1.put("billTo_street1","1295 Charleston Road");
        request1.put("billTo_city","Mountain View");
        request1.put("billTo_state","CA");
        request1.put("billTo_postalCode","94043");
        request1.put("billTo_country","US");
        request1.put("billTo_email","nobody@cybersource.com");
        request1.put("billTo_ipAddress","10.7.7.7");
        request1.put("billTo_phoneNumber","650-965-6000");*/



        Properties props = CyberSourceUtils.convertResourceBundleToProperties(RB);

        System.out.println("merchantID-----"+props.getProperty("merchantID"));
        System.out.println("keyAlias-----"+props.getProperty("keyAlias"));
        System.out.println("keyPassword-----"+props.getProperty("keyPassword"));
        System.out.println("sendToProduction-----"+props.getProperty("sendToProduction"));
        System.out.println("serverURL-----"+props.getProperty("serverURL"));
        System.out.println("keysDirectory-----"+props.getProperty("keysDirectory"));

        boolean isTest=true;
        if(isTest){
            props.setProperty("sendToProduction","false");
            props.setProperty("serverURL","https://ics2wstesta.ic3.com/commerce/1.x/transactionProcessor");
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/test");
        }else {
            props.setProperty("sendToProduction","true");
            props.setProperty("serverURL","https://ics2ws.ic3.com/commerce/1.x/transactionProcessor");
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/live");
        }

        props.setProperty("merchantID","tw170067094");
        props.setProperty("keyAlias","tw170067094");
        props.setProperty("keyPassword","tw170067094");

        System.out.println("merchantID-----"+props.getProperty("merchantID"));
        System.out.println("keyAlias-----"+props.getProperty("keyAlias"));
        System.out.println("keyPassword-----"+props.getProperty("keyPassword"));
        System.out.println("sendToProduction-----"+props.getProperty("sendToProduction"));
        System.out.println("serverURL-----"+props.getProperty("serverURL"));
        System.out.println("keysDirectory-----"+props.getProperty("keysDirectory"));
        try{
            System.out.println("---before---");
            CyberSourceUtils.displayMap("Sale Request-----",request);
            Map<String,String> map= Client.runTransaction(request, props);
            CyberSourceUtils.displayMap("Sale Response-----",map);
        }catch (Exception e){
            System.out.println("Exception-----"+e);
        }





    }

   /* public static void main(String[] args)
    {
        Functions functions= new Functions();
        Map<String,String> map= new HashMap<>();
        map.put("names","Jhon");
        map.put("surnam","Doe");

        if(map!=null && !map.isEmpty() && map.size()!=0){
            String name="";
            if(functions.isValueNull(map.get("name"))){
                name=map.get("name");
                System.out.println("name-----"+name);
            }else {
                name="ubv";g
                System.out.println("name---"+name);
            }
        }

    }*/

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processSale-----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String is3DSupported=gatewayAccount.get_3DSupportAccount();
        String reject3DCard = commRequestVO.getReject3DCard();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        String merchantId=gatewayAccount.getMerchantId();
        Properties props = CyberSourceUtils.convertResourceBundleToProperties(RB);

        boolean isTest=gatewayAccount.isTest();
        if(isTest){
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/test");
            props.setProperty("sendToProduction","false");
            props.setProperty("serverURL","https://ics2wstesta.ic3.com/commerce/1.x/transactionProcessor");
        }else {
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/live");
            props.setProperty("sendToProduction","true");
            props.setProperty("serverURL","https://ics2ws.ic3.com/commerce/1.x/transactionProcessor");
        }

        props.setProperty("merchantID",merchantId);
        props.setProperty("keyAlias",merchantId);
        props.setProperty("keyPassword",merchantId);

        transactionLogger.error("merchantID-----" + props.getProperty("merchantID"));
        transactionLogger.error("keyAlias-----" + props.getProperty("keyAlias"));
        transactionLogger.error("keyPassword-----" + props.getProperty("keyPassword"));
        transactionLogger.error("sendToProduction-----" + props.getProperty("sendToProduction"));
        transactionLogger.error("serverURL-----" + props.getProperty("serverURL"));
        transactionLogger.error("keysDirectory-----"+props.getProperty("keysDirectory"));

        String addressValidation="";

        if(functions.isValueNull(gatewayAccount.getAddressValidation())){
            transactionLogger.error("addressValidation-----"+gatewayAccount.getAddressValidation());
            addressValidation=gatewayAccount.getAddressValidation();
        }

        /*String invoiceHeader_merchantDescriptor="";
        String invoiceHeader_merchantDescriptorStreet="";


        if(functions.isValueNull(commMerchantVO.getBrandName())){
            transactionLogger.error("invoiceHeader_merchantDescriptor-----"+commMerchantVO.getBrandName());
            invoiceHeader_merchantDescriptor=commMerchantVO.getBrandName();
        }
        if(functions.isValueNull(commMerchantVO.getAddress())){
            transactionLogger.error("invoiceHeader_merchantDescriptorStreet-----"+commMerchantVO.getAddress());
            invoiceHeader_merchantDescriptorStreet=commMerchantVO.getAddress();
        }*/

        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----"+termUrl);
        }


        try{


            HashMap <String,String> request= new HashMap<>();
            if("Y".equals(is3DSupported) || "O".equalsIgnoreCase(is3DSupported))
            {
                request.put("payerAuthEnrollService_run","true");
                request.put("card_cardType",CyberSourceUtils.getCardType(commTransactionDetailsVO.getCardType()));
                request.put("merchantReferenceCode",trackingID);
                request.put("card_accountNumber",commCardDetailsVO.getCardNum());
                request.put("card_cvNumber",commCardDetailsVO.getcVV());
                request.put("card_expirationMonth",commCardDetailsVO.getExpMonth());
                request.put("card_expirationYear",commCardDetailsVO.getExpYear());
                request.put("purchaseTotals_currency",commTransactionDetailsVO.getCurrency());
                request.put("purchaseTotals_grandTotalAmount",commTransactionDetailsVO.getAmount());

                CyberSourceUtils.displayMap("Enrollment Request-----", request);
                Map<String,String> map= Client.runTransaction(request, props);
                CyberSourceUtils.displayMap("Enrollment Response-----", map);

                if(map!=null && !map.isEmpty() && map.size()!=0){
                    String payerAuthEnrollReply_veresEnrolled="";
                    String requestID="";
                    String reasonCode="";
                    String payerAuthEnrollReply_acsURL="";
                    String payerAuthEnrollReply_paReq="";
                    String payerAuthEnrollReply_reasonCode="";
                    String payerAuthEnrollReply_authenticationTransactionID="";
                    String decision="";

                    if(functions.isValueNull(map.get("decision"))) {
                        decision = map.get("decision");
                    }if(functions.isValueNull(map.get("payerAuthEnrollReply_veresEnrolled"))) {
                        payerAuthEnrollReply_veresEnrolled = map.get("payerAuthEnrollReply_veresEnrolled");
                    }if(functions.isValueNull(map.get("requestID"))) {
                        requestID = map.get("requestID");
                    }if(functions.isValueNull(map.get("reasonCode"))) {
                        reasonCode = map.get("reasonCode");
                    }if(functions.isValueNull(map.get("payerAuthEnrollReply_reasonCode"))) {
                        payerAuthEnrollReply_reasonCode = map.get("payerAuthEnrollReply_reasonCode");
                    }if(functions.isValueNull(map.get("payerAuthEnrollReply_authenticationTransactionID"))) {
                        payerAuthEnrollReply_authenticationTransactionID = map.get("payerAuthEnrollReply_authenticationTransactionID");
                    }
                    if("Y".equals(payerAuthEnrollReply_veresEnrolled) && "475".equals(reasonCode) && "475".equals(payerAuthEnrollReply_reasonCode))
                    {

                        if ("Y".equals(reject3DCard))
                        {
                            transactionLogger.error("SYS: Rejecting 3d card as per configuration ");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescription("3D Enrolled Card");
                            commResponseVO.setRemark("3D Enrolled Card");
                            return commResponseVO;
                        }
                            if(functions.isValueNull(map.get("payerAuthEnrollReply_acsURL"))) {
                                payerAuthEnrollReply_acsURL = map.get("payerAuthEnrollReply_acsURL");
                            }if(functions.isValueNull(map.get("payerAuthEnrollReply_paReq"))) {
                                payerAuthEnrollReply_paReq = map.get("payerAuthEnrollReply_paReq");
                        }

                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(payerAuthEnrollReply_acsURL);
                        commResponseVO.setMd(payerAuthEnrollReply_authenticationTransactionID+"@"+commCardDetailsVO.getcVV());
                        commResponseVO.setPaReq(payerAuthEnrollReply_paReq);
                        commResponseVO.setTerURL(termUrl + trackingID);
                        commResponseVO.setTransactionId(requestID);
                        commResponseVO.setErrorCode(reasonCode);
                        commResponseVO.setRemark("SYS: Pending For 3D Authentication");
                        commResponseVO.setDescriptor("SYS: Pending For 3D Authentication");
                        commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());

                    }else if("N".equals(payerAuthEnrollReply_veresEnrolled) || "U".equals(payerAuthEnrollReply_veresEnrolled))
                    {

                        if ("O".equals(is3DSupported))
                        {
                            transactionLogger.error("SYS: Rejecting 3d card as per configuration");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setTransactionId(requestID);
                            commResponseVO.setDescription("SYS: Only 3D card Supported");
                            commResponseVO.setRemark("SYS: Only 3D card Supported");
                            return commResponseVO;
                        }
                        else
                        {
                            HashMap <String,String> request2= new HashMap<>();
                            request2.put("ccAuthService_run", "true");
                            request2.put("ccCaptureService_run", "true");
                            request2.put("ccAuthService_authType", "AUTOCAPTURE");
                            request2.put("merchantReferenceCode", trackingID);
                            if("Y".equalsIgnoreCase(addressValidation)){
                                request2.put("billTo_firstName", commAddressDetailsVO.getFirstname());
                                request2.put("billTo_lastName", commAddressDetailsVO.getLastname());
                                request2.put("billTo_street1", commAddressDetailsVO.getStreet());
                                request2.put("billTo_city", commAddressDetailsVO.getCity());
                                request2.put("billTo_state", commAddressDetailsVO.getState());
                                request2.put("billTo_postalCode", commAddressDetailsVO.getZipCode());
                                request2.put("billTo_country", commAddressDetailsVO.getCountry());
                                request2.put("billTo_email", commAddressDetailsVO.getEmail());
                                request2.put("billTo_ipAddress", commAddressDetailsVO.getCardHolderIpAddress());
                                request2.put("billTo_phoneNumber", commAddressDetailsVO.getPhone());
                            }
                            request2.put("card_accountNumber", commCardDetailsVO.getCardNum());
                            request2.put("card_cvNumber", commCardDetailsVO.getcVV());
                            request2.put("card_expirationMonth", commCardDetailsVO.getExpMonth());
                            request2.put("card_expirationYear", commCardDetailsVO.getExpYear());
                            request2.put("card_cardType", CyberSourceUtils.getCardType(commTransactionDetailsVO.getCardType()));
                            request2.put("purchaseTotals_currency", commTransactionDetailsVO.getCurrency());
                            request2.put("purchaseTotals_grandTotalAmount", commTransactionDetailsVO.getAmount());
                            request2.put("invoiceHeader_merchantDescriptor", gatewayAccount.getDisplayName());
                            request2.put("invoiceHeader_merchantDescriptorStreet",gatewayAccount.getFRAUD_FILE_SHORT_NAME());
                            request2.put("invoiceHeader_merchantDescriptorCity",gatewayAccount.getFRAUD_FTP_PATH());
                            request2.put("invoiceHeader_merchantDescriptorContact",gatewayAccount.getFRAUD_FTP_PATH());

                            CyberSourceUtils.displayMap("Sale Request-----", request2);
                            map = Client.runTransaction(request2, props);
                            CyberSourceUtils.displayMap("Sale Response-----", map);

                            if (map != null && !map.isEmpty() && map.size() != 0)
                            {

                                String ccAuthReply_authorizationCode = "";
                                String ccAuthReply_amount = "";
                                String ccCaptureReply_amount = "";
                                String ccAuthReply_authorizedDateTime = "";
                                String ccAuthReply_reasonCode = "";
                                String ccCaptureReply_reasonCode = "";
                                String purchaseTotals_currency = "";

                                if (functions.isValueNull(map.get("decision")))
                                {
                                    decision = map.get("decision");
                                }
                                if (functions.isValueNull(map.get("ccAuthReply_authorizationCode")))
                                {
                                    ccAuthReply_authorizationCode = map.get("ccAuthReply_authorizationCode");
                                }
                                if (functions.isValueNull(map.get("ccAuthReply_amount")))
                                {
                                    ccAuthReply_amount = map.get("ccAuthReply_amount");
                                }
                                if (functions.isValueNull(map.get("requestID")))
                                {
                                    requestID = map.get("requestID");
                                }
                                if (functions.isValueNull(map.get("ccAuthReply_authorizedDateTime")))
                                {
                                    ccAuthReply_authorizedDateTime = map.get("ccAuthReply_authorizedDateTime");
                                }
                                if (functions.isValueNull(map.get("reasonCode")))
                                {
                                    reasonCode = map.get("reasonCode");
                                }
                                if (functions.isValueNull(map.get("ccCaptureReply_amount")))
                                {
                                    ccCaptureReply_amount = map.get("ccCaptureReply_amount");
                                }
                                if (functions.isValueNull(map.get("ccAuthReply_reasonCode")))
                                {
                                    ccAuthReply_reasonCode = map.get("ccAuthReply_reasonCode");
                                }
                                if (functions.isValueNull(map.get("ccCaptureReply_reasonCode")))
                                {
                                    ccCaptureReply_reasonCode = map.get("ccCaptureReply_reasonCode");
                                }
                                if (functions.isValueNull(map.get("purchaseTotals_currency")))
                                {
                                    purchaseTotals_currency = map.get("purchaseTotals_currency");
                                }

                                if ("100".equals(reasonCode) && "ACCEPT".equalsIgnoreCase(decision) && "100".equals(ccAuthReply_reasonCode) && "100".equals(ccCaptureReply_reasonCode))
                                {
                                    commResponseVO.setStatus("success");
                                    commResponseVO.setRemark("SYS: Transaction Successful");
                                    commResponseVO.setDescription(decision + "/" + CyberSourceErrorCode.getDescription(reasonCode));
                                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                }
                                else
                                {
                                    commResponseVO.setStatus("fail");
                                    commResponseVO.setRemark("SYS: Transaction Failed");
                                    if (functions.isValueNull(CyberSourceErrorCode.getDescription(reasonCode)))
                                        commResponseVO.setDescription(decision + "/" + CyberSourceErrorCode.getDescription(reasonCode));
                                    else
                                         commResponseVO.setDescription(decision);
                                }

                                commResponseVO.setTransactionId(requestID);
                                commResponseVO.setAuthCode(ccAuthReply_authorizationCode);
                                commResponseVO.setErrorCode(reasonCode);
                                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                                commResponseVO.setAmount(ccCaptureReply_amount);
                                commResponseVO.setCurrency(purchaseTotals_currency);
                                commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                                commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                                commResponseVO.setBankTransactionDate(ccAuthReply_authorizedDateTime);
                            }
                        }
                    }else {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark("SYS: Transaction Failed");
                        if (functions.isValueNull(CyberSourceErrorCode.getDescription(reasonCode)))
                            commResponseVO.setDescription(decision + "/" + CyberSourceErrorCode.getDescription(reasonCode));
                        else
                            commResponseVO.setDescription(decision);
                       // commResponseVO.setDescription(decision);
                        commResponseVO.setTransactionId(requestID);
                        commResponseVO.setErrorCode(reasonCode);
                        commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    }

                }

            }
            else {

                request.put("ccAuthService_run","true");
                request.put("ccCaptureService_run","true");
                request.put("ccAuthService_authType","AUTOCAPTURE");
                request.put("merchantReferenceCode",trackingID);
                if("Y".equals(addressValidation)){
                    request.put("billTo_firstName",commAddressDetailsVO.getFirstname());
                    request.put("billTo_lastName",commAddressDetailsVO.getLastname());
                    request.put("billTo_street1",commAddressDetailsVO.getStreet());
                    request.put("billTo_city",commAddressDetailsVO.getCity());
                    request.put("billTo_state",commAddressDetailsVO.getState());
                    request.put("billTo_postalCode",commAddressDetailsVO.getZipCode());
                    request.put("billTo_country",commAddressDetailsVO.getCountry());
                    request.put("billTo_email",commAddressDetailsVO.getEmail());
                    request.put("billTo_ipAddress",commAddressDetailsVO.getCardHolderIpAddress());
                    request.put("billTo_phoneNumber",commAddressDetailsVO.getPhone());
                }
                request.put("card_accountNumber",commCardDetailsVO.getCardNum());
                request.put("card_cvNumber",commCardDetailsVO.getcVV());
                request.put("card_expirationMonth",commCardDetailsVO.getExpMonth());
                request.put("card_expirationYear",commCardDetailsVO.getExpYear());
                request.put("card_cardType", CyberSourceUtils.getCardType(commTransactionDetailsVO.getCardType()));
                request.put("purchaseTotals_currency",commTransactionDetailsVO.getCurrency());
                request.put("purchaseTotals_grandTotalAmount", commTransactionDetailsVO.getAmount());
                request.put("invoiceHeader_merchantDescriptor", gatewayAccount.getDisplayName());
                request.put("invoiceHeader_merchantDescriptorStreet",gatewayAccount.getFRAUD_FILE_SHORT_NAME());
                request.put("invoiceHeader_merchantDescriptorCity",gatewayAccount.getFRAUD_FTP_PATH());
                request.put("invoiceHeader_merchantDescriptorContact",gatewayAccount.getFRAUD_FTP_PATH());


                CyberSourceUtils.displayMap("Sale Request-----",request);
                Map<String,String> map= Client.runTransaction(request, props);
                CyberSourceUtils.displayMap("Sale Response-----",map);

                if(map!=null && !map.isEmpty() && map.size()!=0){

                    String decision="";
                    String ccAuthReply_authorizationCode="";
                    String ccAuthReply_amount="";
                    String ccCaptureReply_amount="";
                    String requestID="";
                    String ccAuthReply_authorizedDateTime="";
                    String  reasonCode="";
                    String ccAuthReply_reasonCode="";
                    String ccCaptureReply_reasonCode="";
                    String purchaseTotals_currency="";

                    if(functions.isValueNull(map.get("decision"))){
                        decision=map.get("decision");
                    }
                    if(functions.isValueNull(map.get("ccAuthReply_authorizationCode"))){
                        ccAuthReply_authorizationCode=map.get("ccAuthReply_authorizationCode");
                    }
                    if(functions.isValueNull(map.get("ccAuthReply_amount"))){
                        ccAuthReply_amount=map.get("ccAuthReply_amount");
                    }
                    if(functions.isValueNull(map.get("requestID"))){
                        requestID=map.get("requestID");
                    }
                    if(functions.isValueNull(map.get("ccAuthReply_authorizedDateTime"))){
                        ccAuthReply_authorizedDateTime=map.get("ccAuthReply_authorizedDateTime");
                    }
                    if(functions.isValueNull(map.get("reasonCode"))){
                        reasonCode=map.get("reasonCode");
                    }
                    if(functions.isValueNull(map.get("ccCaptureReply_amount"))){
                        ccCaptureReply_amount=map.get("ccCaptureReply_amount");
                    }
                    if(functions.isValueNull(map.get("ccAuthReply_reasonCode"))){
                        ccAuthReply_reasonCode=map.get("ccAuthReply_reasonCode");
                    }
                    if(functions.isValueNull(map.get("ccCaptureReply_reasonCode"))){
                        ccCaptureReply_reasonCode=map.get("ccCaptureReply_reasonCode");
                    }
                    if (functions.isValueNull(map.get("purchaseTotals_currency"))) {
                        purchaseTotals_currency = map.get("purchaseTotals_currency");
                    }

                    if("100".equals(reasonCode) && "ACCEPT".equalsIgnoreCase(decision) && "100".equals(ccAuthReply_reasonCode) && "100".equals(ccCaptureReply_reasonCode)){
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("SYS: Transaction Successful");
                        commResponseVO.setDescription(decision+ "/" +CyberSourceErrorCode.getDescription(reasonCode));
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }else {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark("SYS: Transaction Failed");
                        if (functions.isValueNull(CyberSourceErrorCode.getDescription(reasonCode)))
                            commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                        else
                            commResponseVO.setDescription(decision);
                    }
                    commResponseVO.setTransactionId(requestID);
                    commResponseVO.setAuthCode(ccAuthReply_authorizationCode);
                    commResponseVO.setErrorCode(reasonCode);
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    commResponseVO.setAmount(ccCaptureReply_amount);
                    commResponseVO.setCurrency(purchaseTotals_currency);
                    commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                    commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                    commResponseVO.setBankTransactionDate(ccAuthReply_authorizedDateTime);
                }
            }

        }catch (Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processAuthentication-----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String is3DSupported=gatewayAccount.get_3DSupportAccount();
        String reject3DCard = commRequestVO.getReject3DCard();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        String addressValidation="";

        String merchantId=gatewayAccount.getMerchantId();
        Properties props = CyberSourceUtils.convertResourceBundleToProperties(RB);

        boolean isTest=gatewayAccount.isTest();
        if(isTest){
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/test");
            props.setProperty("sendToProduction","false");
            props.setProperty("serverURL","https://ics2wstesta.ic3.com/commerce/1.x/transactionProcessor");
        }else {
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/live");
            props.setProperty("sendToProduction","true");
            props.setProperty("serverURL","https://ics2ws.ic3.com/commerce/1.x/transactionProcessor");
        }

        props.setProperty("merchantID",merchantId);
        props.setProperty("keyAlias",merchantId);
        props.setProperty("keyPassword",merchantId);

        transactionLogger.error("merchantID-----" + props.getProperty("merchantID"));
        transactionLogger.error("keyAlias-----" + props.getProperty("keyAlias"));
        transactionLogger.error("keyPassword-----" + props.getProperty("keyPassword"));
        transactionLogger.error("sendToProduction-----" + props.getProperty("sendToProduction"));
        transactionLogger.error("serverURL-----"+props.getProperty("serverURL"));
        transactionLogger.error("keysDirectory-----"+props.getProperty("keysDirectory"));

        if(functions.isValueNull(gatewayAccount.getAddressValidation())){
            transactionLogger.error("addressValidation-----"+gatewayAccount.getAddressValidation());
            addressValidation=gatewayAccount.getAddressValidation();
        }

       /* String invoiceHeader_merchantDescriptor="";
        String invoiceHeader_merchantDescriptorStreet="";


        if(functions.isValueNull(commMerchantVO.getBrandName())){
            transactionLogger.error("invoiceHeader_merchantDescriptor-----"+commMerchantVO.getBrandName());
            invoiceHeader_merchantDescriptor=commMerchantVO.getBrandName();
        }
        if(functions.isValueNull(commMerchantVO.getAddress())){
            transactionLogger.error("invoiceHeader_merchantDescriptorStreet-----"+commMerchantVO.getAddress());
            invoiceHeader_merchantDescriptorStreet=commMerchantVO.getAddress();
        }*/


        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://"+commMerchantVO.getHostUrl()+RB.getString("HOST_URL");
            transactionLogger.error("from host url----"+termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----"+termUrl);
        }

        try{

            HashMap <String,String> request= new HashMap<>();
            if("Y".equals(is3DSupported) || "O".equalsIgnoreCase(is3DSupported))
            {
                request.put("payerAuthEnrollService_run", "true");
                request.put("card_cardType", CyberSourceUtils.getCardType(commTransactionDetailsVO.getCardType()));
                request.put("merchantReferenceCode", trackingID);
                request.put("card_accountNumber", commCardDetailsVO.getCardNum());
                request.put("card_cvNumber", commCardDetailsVO.getcVV());
                request.put("card_expirationMonth", commCardDetailsVO.getExpMonth());
                request.put("card_expirationYear", commCardDetailsVO.getExpYear());
                request.put("purchaseTotals_currency",commTransactionDetailsVO.getCurrency());
                request.put("purchaseTotals_grandTotalAmount",commTransactionDetailsVO.getAmount());

                CyberSourceUtils.displayMap("Enrollment Request-----", request);
                Map<String, String> map = Client.runTransaction(request, props);
                CyberSourceUtils.displayMap("Enrollment Response-----", map);

                if (map != null && !map.isEmpty() && map.size() != 0)
                {
                    String payerAuthEnrollReply_veresEnrolled = "";
                    String requestID = "";
                    String reasonCode = "";
                    String payerAuthEnrollReply_acsURL = "";
                    String payerAuthEnrollReply_paReq = "";
                    String payerAuthEnrollReply_reasonCode = "";
                    String payerAuthEnrollReply_authenticationTransactionID = "";
                    String decision = "";

                    if (functions.isValueNull(map.get("decision")))
                    {
                        decision = map.get("decision");
                    }
                    if (functions.isValueNull(map.get("payerAuthEnrollReply_veresEnrolled")))
                    {
                        payerAuthEnrollReply_veresEnrolled = map.get("payerAuthEnrollReply_veresEnrolled");
                    }
                    if (functions.isValueNull(map.get("requestID")))
                    {
                        requestID = map.get("requestID");
                    }
                    if (functions.isValueNull(map.get("reasonCode")))
                    {
                        reasonCode = map.get("reasonCode");
                    }
                    if (functions.isValueNull(map.get("payerAuthEnrollReply_reasonCode")))
                    {
                        payerAuthEnrollReply_reasonCode = map.get("payerAuthEnrollReply_reasonCode");
                    }
                    if (functions.isValueNull(map.get("payerAuthEnrollReply_authenticationTransactionID")))
                    {
                        payerAuthEnrollReply_authenticationTransactionID = map.get("payerAuthEnrollReply_authenticationTransactionID");
                    }
                    if ("Y".equals(payerAuthEnrollReply_veresEnrolled) && "475".equals(reasonCode) && "475".equals(payerAuthEnrollReply_reasonCode))
                    {

                        if ("Y".equals(reject3DCard))
                        {
                            transactionLogger.error("SYS: Rejecting 3d card as per configuration ");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setDescription("3D Enrolled Card");
                            commResponseVO.setRemark("3D Enrolled Card");
                            return commResponseVO;
                        }
                        if (functions.isValueNull(map.get("payerAuthEnrollReply_acsURL")))
                        {
                            payerAuthEnrollReply_acsURL = map.get("payerAuthEnrollReply_acsURL");
                        }
                        if (functions.isValueNull(map.get("payerAuthEnrollReply_paReq")))
                        {
                            payerAuthEnrollReply_paReq = map.get("payerAuthEnrollReply_paReq");
                        }

                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setUrlFor3DRedirect(payerAuthEnrollReply_acsURL);
                        commResponseVO.setMd(payerAuthEnrollReply_authenticationTransactionID+"@"+commCardDetailsVO.getcVV());
                        commResponseVO.setPaReq(payerAuthEnrollReply_paReq);
                        commResponseVO.setTerURL(termUrl + trackingID);
                        commResponseVO.setTransactionId(requestID);
                        commResponseVO.setErrorCode(reasonCode);
                        commResponseVO.setRemark("SYS: Pending For 3D Authentication");
                        commResponseVO.setDescriptor("SYS: Pending For 3D Authentication");
                        commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());

                    }
                    else if ("N".equals(payerAuthEnrollReply_veresEnrolled) || "U".equals(payerAuthEnrollReply_veresEnrolled))
                    {
                        if ("O".equals(is3DSupported))
                        {
                            transactionLogger.error("SYS: Rejecting 3d card as per configuration");
                            commResponseVO.setStatus("failed");
                            commResponseVO.setTransactionId(requestID);
                            commResponseVO.setDescription("SYS: Only 3D card Supported");
                            commResponseVO.setRemark("SYS: Only 3D card Supported");
                            return commResponseVO;
                        }
                        else
                        {
                            HashMap <String,String> request2= new HashMap<>();
                            request2.put("ccAuthService_run", "true");
                            request2.put("authIndicator", "1");
                            request2.put("merchantReferenceCode", trackingID);
                            if("Y".equals(addressValidation)){
                                request2.put("billTo_firstName", commAddressDetailsVO.getFirstname());
                                request2.put("billTo_lastName", commAddressDetailsVO.getLastname());
                                request2.put("billTo_street1", commAddressDetailsVO.getStreet());
                                request2.put("billTo_city", commAddressDetailsVO.getCity());
                                request2.put("billTo_state", commAddressDetailsVO.getState());
                                request2.put("billTo_postalCode", commAddressDetailsVO.getZipCode());
                                request2.put("billTo_country", commAddressDetailsVO.getCountry());
                                request2.put("billTo_email", commAddressDetailsVO.getEmail());
                                request2.put("billTo_ipAddress", commAddressDetailsVO.getCardHolderIpAddress());
                                request2.put("billTo_phoneNumber", commAddressDetailsVO.getPhone());
                            }
                            request2.put("card_accountNumber", commCardDetailsVO.getCardNum());
                            request2.put("card_cvNumber", commCardDetailsVO.getcVV());
                            request2.put("card_expirationMonth", commCardDetailsVO.getExpMonth());
                            request2.put("card_expirationYear", commCardDetailsVO.getExpYear());
                            request2.put("card_cardType",CyberSourceUtils.getCardType(commTransactionDetailsVO.getCardType()));
                            request2.put("purchaseTotals_currency", commTransactionDetailsVO.getCurrency());
                            request2.put("purchaseTotals_grandTotalAmount", commTransactionDetailsVO.getAmount());
                            request2.put("invoiceHeader_merchantDescriptor", gatewayAccount.getDisplayName());
                            request2.put("invoiceHeader_merchantDescriptorStreet",gatewayAccount.getFRAUD_FILE_SHORT_NAME());
                            request2.put("invoiceHeader_merchantDescriptorCity",gatewayAccount.getFRAUD_FTP_PATH());
                            request2.put("invoiceHeader_merchantDescriptorContact",gatewayAccount.getFRAUD_FTP_PATH());

                            CyberSourceUtils.displayMap("Auth Request-----", request2);
                             map = Client.runTransaction(request2, props);
                            CyberSourceUtils.displayMap("Auth Response-----", map);

                            if (map != null && !map.isEmpty() && map.size() != 0)
                            {

                                String ccAuthReply_authorizationCode = "";
                                String ccAuthReply_amount = "";
                                String ccAuthReply_authorizedDateTime = "";
                                String ccAuthReply_reasonCode = "";
                                String purchaseTotals_currency = "";

                                if (functions.isValueNull(map.get("decision")))
                                {
                                    decision = map.get("decision");
                                }
                                if (functions.isValueNull(map.get("ccAuthReply_authorizationCode")))
                                {
                                    ccAuthReply_authorizationCode = map.get("ccAuthReply_authorizationCode");
                                }
                                if (functions.isValueNull(map.get("ccAuthReply_amount")))
                                {
                                    ccAuthReply_amount = map.get("ccAuthReply_amount");
                                }
                                if (functions.isValueNull(map.get("requestID")))
                                {
                                    requestID = map.get("requestID");
                                }
                                if (functions.isValueNull(map.get("ccAuthReply_authorizedDateTime")))
                                {
                                    ccAuthReply_authorizedDateTime = map.get("ccAuthReply_authorizedDateTime");
                                }
                                if (functions.isValueNull(map.get("reasonCode")))
                                {
                                    reasonCode = map.get("reasonCode");
                                }
                                if (functions.isValueNull(map.get("ccAuthReply_reasonCode")))
                                {
                                    ccAuthReply_reasonCode = map.get("ccAuthReply_reasonCode");
                                }
                                if (functions.isValueNull(map.get("purchaseTotals_currency")))
                                {
                                    purchaseTotals_currency = map.get("purchaseTotals_currency");
                                }

                                if ("100".equals(reasonCode) && "ACCEPT".equalsIgnoreCase(decision) && "100".equals(ccAuthReply_reasonCode))
                                {
                                    commResponseVO.setStatus("success");
                                    commResponseVO.setRemark("SYS: Transaction Successful");
                                    commResponseVO.setDescription(decision +"/"+CyberSourceErrorCode.getDescription(reasonCode));
                                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                }
                                else
                                {
                                    commResponseVO.setStatus("fail");
                                    commResponseVO.setRemark("SYS: Transaction Failed");
                                    if (functions.isValueNull(CyberSourceErrorCode.getDescription(reasonCode)))
                                        commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                                    else
                                        commResponseVO.setDescription(decision);
                                }
                                commResponseVO.setTransactionId(requestID);
                                commResponseVO.setAuthCode(ccAuthReply_authorizationCode);
                                commResponseVO.setErrorCode(reasonCode);
                                commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                                commResponseVO.setAmount(ccAuthReply_amount);
                                commResponseVO.setCurrency(purchaseTotals_currency);
                                commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                                commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                                commResponseVO.setBankTransactionDate(ccAuthReply_authorizedDateTime);
                            }
                        }
                    }else {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark("SYS: Transaction Failed");
                        if (functions.isValueNull(CyberSourceErrorCode.getDescription(reasonCode)))
                            commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                        else
                            commResponseVO.setDescription(decision);
                       // commResponseVO.setDescription(decision);
                        commResponseVO.setTransactionId(requestID);
                        commResponseVO.setErrorCode(reasonCode);
                        commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                    }
                }
            }else
            {
                request.put("ccAuthService_run", "true");
                request.put("authIndicator", "1");
                request.put("merchantReferenceCode", trackingID);
                if("Y".equals(addressValidation)){
                    request.put("billTo_firstName", commAddressDetailsVO.getFirstname());
                    request.put("billTo_lastName", commAddressDetailsVO.getLastname());
                    request.put("billTo_street1", commAddressDetailsVO.getStreet());
                    request.put("billTo_city", commAddressDetailsVO.getCity());
                    request.put("billTo_state", commAddressDetailsVO.getState());
                    request.put("billTo_postalCode", commAddressDetailsVO.getZipCode());
                    request.put("billTo_country", commAddressDetailsVO.getCountry());
                    request.put("billTo_email", commAddressDetailsVO.getEmail());
                    request.put("billTo_ipAddress", commAddressDetailsVO.getCardHolderIpAddress());
                    request.put("billTo_phoneNumber", commAddressDetailsVO.getPhone());
                }
                request.put("card_accountNumber", commCardDetailsVO.getCardNum());
                request.put("card_cvNumber", commCardDetailsVO.getcVV());
                request.put("card_expirationMonth", commCardDetailsVO.getExpMonth());
                request.put("card_expirationYear", commCardDetailsVO.getExpYear());
                request.put("card_cardType",CyberSourceUtils.getCardType(commTransactionDetailsVO.getCardType()));
                request.put("purchaseTotals_currency", commTransactionDetailsVO.getCurrency());
                request.put("purchaseTotals_grandTotalAmount", commTransactionDetailsVO.getAmount());
                request.put("invoiceHeader_merchantDescriptor", gatewayAccount.getDisplayName());
                request.put("invoiceHeader_merchantDescriptorStreet",gatewayAccount.getFRAUD_FILE_SHORT_NAME());
                request.put("invoiceHeader_merchantDescriptorCity",gatewayAccount.getFRAUD_FTP_PATH());
                request.put("invoiceHeader_merchantDescriptorContact",gatewayAccount.getFRAUD_FTP_PATH());

                CyberSourceUtils.displayMap("Auth Request-----", request);
                Map<String, String> map = Client.runTransaction(request, props);
                CyberSourceUtils.displayMap("Auth Response-----", map);

                if (map != null && !map.isEmpty() && map.size() != 0)
                {

                    String decision = "";
                    String ccAuthReply_authorizationCode = "";
                    String ccAuthReply_amount = "";
                    String requestID = "";
                    String ccAuthReply_authorizedDateTime = "";
                    String reasonCode = "";
                    String ccAuthReply_reasonCode = "";
                    String purchaseTotals_currency = "";

                    if (functions.isValueNull(map.get("decision")))
                    {
                        decision = map.get("decision");
                    }
                    if (functions.isValueNull(map.get("ccAuthReply_authorizationCode")))
                    {
                        ccAuthReply_authorizationCode = map.get("ccAuthReply_authorizationCode");
                    }
                    if (functions.isValueNull(map.get("ccAuthReply_amount")))
                    {
                        ccAuthReply_amount = map.get("ccAuthReply_amount");
                    }
                    if (functions.isValueNull(map.get("requestID")))
                    {
                        requestID = map.get("requestID");
                    }
                    if (functions.isValueNull(map.get("ccAuthReply_authorizedDateTime")))
                    {
                        ccAuthReply_authorizedDateTime = map.get("ccAuthReply_authorizedDateTime");
                    }
                    if (functions.isValueNull(map.get("reasonCode")))
                    {
                        reasonCode = map.get("reasonCode");
                    }
                    if (functions.isValueNull(map.get("ccAuthReply_reasonCode")))
                    {
                        ccAuthReply_reasonCode = map.get("ccAuthReply_reasonCode");
                    }
                    if (functions.isValueNull(map.get("purchaseTotals_currency")))
                    {
                        purchaseTotals_currency = map.get("purchaseTotals_currency");
                    }

                    if ("100".equals(reasonCode) && "ACCEPT".equalsIgnoreCase(decision) && "100".equals(ccAuthReply_reasonCode))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("SYS: Transaction Successful");
                        commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark("SYS: Transaction Failed");
                        if (functions.isValueNull(CyberSourceErrorCode.getDescription(reasonCode)))
                            commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                        else
                            commResponseVO.setDescription(decision);
                        //commResponseVO.setDescription(decision);
                    }
                    commResponseVO.setTransactionId(requestID);
                    commResponseVO.setAuthCode(ccAuthReply_authorizationCode);
                    commResponseVO.setErrorCode(reasonCode);
                    commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                    commResponseVO.setAmount(ccAuthReply_amount);
                    commResponseVO.setCurrency(purchaseTotals_currency);
                    commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                    commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                    commResponseVO.setBankTransactionDate(ccAuthReply_authorizedDateTime);
                }
            }

        }catch (FaultException e){
            transactionLogger.error("FaultException-----",e);
        }catch (ClientException e){
            transactionLogger.error("ClientException-----",e);
        }
        return commResponseVO;

    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processCapture-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        CommAddressDetailsVO commAddressDetailsVO= commRequestVO.getAddressDetailsVO();

        String merchantId=gatewayAccount.getMerchantId();
        Properties props = CyberSourceUtils.convertResourceBundleToProperties(RB);

        boolean isTest=gatewayAccount.isTest();
        if(isTest){
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/test");
            props.setProperty("sendToProduction","false");
            props.setProperty("serverURL","https://ics2wstesta.ic3.com/commerce/1.x/transactionProcessor");
        }else {
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/live");
            props.setProperty("sendToProduction","true");
            props.setProperty("serverURL","https://ics2ws.ic3.com/commerce/1.x/transactionProcessor");
        }

        props.setProperty("merchantID",merchantId);
        props.setProperty("keyAlias",merchantId);
        props.setProperty("keyPassword",merchantId);

        transactionLogger.error("merchantID-----" + props.getProperty("merchantID"));
        transactionLogger.error("keyAlias-----" + props.getProperty("keyAlias"));
        transactionLogger.error("keyPassword-----" + props.getProperty("keyPassword"));
        transactionLogger.error("sendToProduction-----" + props.getProperty("sendToProduction"));
        transactionLogger.error("serverURL-----"+props.getProperty("serverURL"));
        transactionLogger.error("keysDirectory-----"+props.getProperty("keysDirectory"));


        try{
            HashMap<String,String> request= new HashMap<>();
            request.put("ccCaptureService_run","true");
            request.put("merchantReferenceCode",trackingID);
            request.put("ccCaptureService_authRequestID",commTransactionDetailsVO.getPreviousTransactionId());
            request.put("purchaseTotals_currency",commTransactionDetailsVO.getCurrency());
            request.put("purchaseTotals_grandTotalAmount",commTransactionDetailsVO.getAmount());

            CyberSourceUtils.displayMap("Capture Request-----",request);
            Map<String,String> map=Client.runTransaction(request,props);
            CyberSourceUtils.displayMap("Capture Response-----",map);

            if(map!=null && !map.isEmpty() && map.size()!=0)
            {
                String decision="";
                String purchaseTotals_currency="";
                String requestID="";
                String reasonCode="";
                String ccCaptureReply_requestDateTime="";
                String ccCaptureReply_reasonCode="";
                String ccCaptureReply_amount="";
                if (functions.isValueNull(map.get("decision"))) {
                    decision = map.get("decision");
                }
                if (functions.isValueNull(map.get("purchaseTotals_currency"))) {
                    purchaseTotals_currency = map.get("purchaseTotals_currency");
                }
                if (functions.isValueNull(map.get("requestID"))) {
                    requestID = map.get("requestID");
                }
                if (functions.isValueNull(map.get("reasonCode"))) {
                    reasonCode = map.get("reasonCode");
                }
                if (functions.isValueNull(map.get("ccCaptureReply_requestDateTime"))) {
                    ccCaptureReply_requestDateTime = map.get("ccCaptureReply_requestDateTime");
                }
                if (functions.isValueNull(map.get("ccCaptureReply_reasonCode"))) {
                    ccCaptureReply_reasonCode = map.get("ccCaptureReply_reasonCode");
                }
                if (functions.isValueNull(map.get("ccCaptureReply_amount"))) {
                    ccCaptureReply_amount = map.get("ccCaptureReply_amount");
                }

                if("100".equals(reasonCode) && "ACCEPT".equalsIgnoreCase(decision) && "100".equals(ccCaptureReply_reasonCode)){
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("SYS: Transaction Successful");
                    commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("SYS: Transaction Failed");
                    if (functions.isValueNull(CyberSourceErrorCode.getDescription(reasonCode)))
                        commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                    else
                        commResponseVO.setDescription(decision);
                   // commResponseVO.setDescription(decision);
                }
                commResponseVO.setTransactionId(requestID);
                commResponseVO.setErrorCode(reasonCode);
                commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                commResponseVO.setAmount(ccCaptureReply_amount);
                commResponseVO.setCurrency(purchaseTotals_currency);
                commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                commResponseVO.setBankTransactionDate(ccCaptureReply_requestDateTime);
            }

        }catch (FaultException e){
            transactionLogger.error("FaultException-----",e);
        }catch (ClientException e){
            transactionLogger.error("ClientException-----",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processVoid-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        CommAddressDetailsVO commAddressDetailsVO= commRequestVO.getAddressDetailsVO();

        String merchantId=gatewayAccount.getMerchantId();
        Properties props = CyberSourceUtils.convertResourceBundleToProperties(RB);

        boolean isTest=gatewayAccount.isTest();
        if(isTest){
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/test");
            props.setProperty("sendToProduction","false");
            props.setProperty("serverURL","https://ics2wstesta.ic3.com/commerce/1.x/transactionProcessor");
        }else {
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/live");
            props.setProperty("sendToProduction","true");
            props.setProperty("serverURL","https://ics2ws.ic3.com/commerce/1.x/transactionProcessor");
        }

        props.setProperty("merchantID",merchantId);
        props.setProperty("keyAlias",merchantId);
        props.setProperty("keyPassword",merchantId);

        transactionLogger.error("merchantID-----" + props.getProperty("merchantID"));
        transactionLogger.error("keyAlias-----" + props.getProperty("keyAlias"));
        transactionLogger.error("keyPassword-----" + props.getProperty("keyPassword"));
        transactionLogger.error("sendToProduction-----" + props.getProperty("sendToProduction"));
        transactionLogger.error("serverURL-----"+props.getProperty("serverURL"));
        transactionLogger.error("keysDirectory-----"+props.getProperty("keysDirectory"));



        try{
            HashMap<String,String> request= new HashMap<>();
            request.put("ccAuthReversalService_run","true");
            request.put("merchantReferenceCode",trackingID);
            request.put("ccAuthReversalService_authRequestID",commTransactionDetailsVO.getPreviousTransactionId());
            request.put("purchaseTotals_currency",commTransactionDetailsVO.getCurrency());
            request.put("purchaseTotals_grandTotalAmount",commTransactionDetailsVO.getAmount());

            CyberSourceUtils.displayMap("Void Request-----",request);
            Map<String,String> map=Client.runTransaction(request,props);
            CyberSourceUtils.displayMap("Void Response-----",map);

            if(map!=null && !map.isEmpty() && map.size()!=0)
            {
                String decision="";
                String purchaseTotals_currency="";
                String requestID="";
                String reasonCode="";
                String ccAuthReversalReply_requestDateTime="";
                String ccAuthReversalReply_reasonCode="";
                String ccAuthReversalReply_amount="";
                if (functions.isValueNull(map.get("decision"))) {
                    decision = map.get("decision");
                }
                if (functions.isValueNull(map.get("purchaseTotals_currency"))) {
                    purchaseTotals_currency = map.get("purchaseTotals_currency");
                }
                if (functions.isValueNull(map.get("requestID"))) {
                    requestID = map.get("requestID");
                }
                if (functions.isValueNull(map.get("reasonCode"))) {
                    reasonCode = map.get("reasonCode");
                }
                if (functions.isValueNull(map.get("ccAuthReversalReply_requestDateTime"))) {
                    ccAuthReversalReply_requestDateTime = map.get("ccAuthReversalReply_requestDateTime");
                }
                if (functions.isValueNull(map.get("ccAuthReversalReply_reasonCode"))) {
                    ccAuthReversalReply_reasonCode = map.get("ccAuthReversalReply_reasonCode");
                }
                if (functions.isValueNull(map.get("ccAuthReversalReply_amount"))) {
                    ccAuthReversalReply_amount = map.get("ccAuthReversalReply_amount");
                }

                if("100".equals(reasonCode) && "ACCEPT".equalsIgnoreCase(decision) && "100".equals(ccAuthReversalReply_reasonCode)){
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("SYS: Transaction Successful");
                    commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("SYS: Transaction Failed");
                    if (functions.isValueNull(CyberSourceErrorCode.getDescription(reasonCode)))
                        commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                    else
                        commResponseVO.setDescription(decision);
                   // commResponseVO.setDescription(decision);
                }
                commResponseVO.setTransactionId(requestID);
                commResponseVO.setErrorCode(reasonCode);
                commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                commResponseVO.setAmount(ccAuthReversalReply_amount);
                commResponseVO.setCurrency(purchaseTotals_currency);
                commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                commResponseVO.setBankTransactionDate(ccAuthReversalReply_requestDateTime);
            }

        }catch (FaultException e){
            transactionLogger.error("FaultException-----",e);
        }catch (ClientException e){
            transactionLogger.error("ClientException-----",e);
        }
        return commResponseVO;

    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processRefund-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        CommAddressDetailsVO commAddressDetailsVO= commRequestVO.getAddressDetailsVO();

        String merchantId=gatewayAccount.getMerchantId();
        Properties props = CyberSourceUtils.convertResourceBundleToProperties(RB);

        boolean isTest=gatewayAccount.isTest();
        if(isTest){
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/test");
            props.setProperty("sendToProduction","false");
            props.setProperty("serverURL","https://ics2wstesta.ic3.com/commerce/1.x/transactionProcessor");
        }else {
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/live");
            props.setProperty("sendToProduction","true");
            props.setProperty("serverURL","https://ics2ws.ic3.com/commerce/1.x/transactionProcessor");
        }

        props.setProperty("merchantID",merchantId);
        props.setProperty("keyAlias",merchantId);
        props.setProperty("keyPassword",merchantId);

        transactionLogger.error("merchantID-----" + props.getProperty("merchantID"));
        transactionLogger.error("keyAlias-----" + props.getProperty("keyAlias"));
        transactionLogger.error("keyPassword-----" + props.getProperty("keyPassword"));
        transactionLogger.error("sendToProduction-----" + props.getProperty("sendToProduction"));
        transactionLogger.error("serverURL-----"+props.getProperty("serverURL"));
        transactionLogger.error("keysDirectory-----"+props.getProperty("keysDirectory"));


        try{
            HashMap<String,String> request= new HashMap<>();
            request.put("ccCreditService_run","true");
            request.put("merchantReferenceCode",trackingID);
            request.put("ccCreditService_captureRequestID",commTransactionDetailsVO.getPreviousTransactionId());
            request.put("purchaseTotals_currency",commTransactionDetailsVO.getCurrency());
            request.put("purchaseTotals_grandTotalAmount",commTransactionDetailsVO.getAmount());

            CyberSourceUtils.displayMap("Refund Request-----",request);
            Map<String,String> map=Client.runTransaction(request,props);
            CyberSourceUtils.displayMap("Refund Response-----",map);

            if(map!=null && !map.isEmpty() && map.size()!=0)
            {
                String decision="";
                String purchaseTotals_currency="";
                String requestID="";
                String reasonCode="";
                String ccCreditReply_requestDateTime="";
                String ccCreditReply_reasonCode="";
                String ccCreditReply_amount="";
                if (functions.isValueNull(map.get("decision"))) {
                    decision = map.get("decision");
                }
                if (functions.isValueNull(map.get("purchaseTotals_currency"))) {
                    purchaseTotals_currency = map.get("purchaseTotals_currency");
                }
                if (functions.isValueNull(map.get("requestID"))) {
                    requestID = map.get("requestID");
                }
                if (functions.isValueNull(map.get("reasonCode"))) {
                    reasonCode = map.get("reasonCode");
                }
                if (functions.isValueNull(map.get("ccCreditReply_requestDateTime"))) {
                    ccCreditReply_requestDateTime = map.get("ccCreditReply_requestDateTime");
                }
                if (functions.isValueNull(map.get("ccCreditReply_reasonCode"))) {
                    ccCreditReply_reasonCode = map.get("ccCreditReply_reasonCode");
                }
                if (functions.isValueNull(map.get("ccCreditReply_amount"))) {
                    ccCreditReply_amount = map.get("ccCreditReply_amount");
                }

                if("100".equals(reasonCode) && "ACCEPT".equalsIgnoreCase(decision) && "100".equals(ccCreditReply_reasonCode)){
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("SYS: Transaction Successful");
                    commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("SYS: Transaction Failed");
                    if (functions.isValueNull(CyberSourceErrorCode.getDescription(reasonCode)))
                        commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                    else
                        commResponseVO.setDescription(decision);
                   // commResponseVO.setDescription(decision);
                }
                commResponseVO.setTransactionId(requestID);
                commResponseVO.setErrorCode(reasonCode);
                commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                commResponseVO.setAmount(ccCreditReply_amount);
                commResponseVO.setCurrency(purchaseTotals_currency);
                commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                commResponseVO.setBankTransactionDate(ccCreditReply_requestDateTime);
            }

        }catch (FaultException e){
            transactionLogger.error("FaultException-----",e);
        }catch (ClientException e){
            transactionLogger.error("ClientException-----",e);
        }
        return commResponseVO;

    }

    @Override
    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processPayout-----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);

        String merchantId=gatewayAccount.getMerchantId();
        Properties props = CyberSourceUtils.convertResourceBundleToProperties(RB);

        boolean isTest=gatewayAccount.isTest();
        if(isTest){
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/test");
            props.setProperty("sendToProduction","false");
            props.setProperty("serverURL","https://ics2wstesta.ic3.com/commerce/1.x/transactionProcessor");
        }else {
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/live");
            props.setProperty("sendToProduction","true");
            props.setProperty("serverURL","https://ics2ws.ic3.com/commerce/1.x/transactionProcessor");
        }

        props.setProperty("merchantID",merchantId);
        props.setProperty("keyAlias",merchantId);
        props.setProperty("keyPassword",merchantId);

        transactionLogger.error("merchantID-----" + props.getProperty("merchantID"));
        transactionLogger.error("keyAlias-----" + props.getProperty("keyAlias"));
        transactionLogger.error("keyPassword-----" + props.getProperty("keyPassword"));
        transactionLogger.error("sendToProduction-----" + props.getProperty("sendToProduction"));
        transactionLogger.error("serverURL-----"+props.getProperty("serverURL"));
        transactionLogger.error("keysDirectory-----"+props.getProperty("keysDirectory"));


        try{

            HashMap <String,String> request= new HashMap<>();

            request.put("ccCreditService_run","true");
            request.put("merchantReferenceCode",trackingId);
            request.put("billTo_firstName",commAddressDetailsVO.getFirstname());
            request.put("billTo_lastName",commAddressDetailsVO.getLastname());
            request.put("billTo_street1",commAddressDetailsVO.getStreet());
            request.put("billTo_city",commAddressDetailsVO.getCity());
            request.put("billTo_state",commAddressDetailsVO.getState());
            request.put("billTo_postalCode",commAddressDetailsVO.getZipCode());
            request.put("billTo_country",commAddressDetailsVO.getCountry());
            request.put("billTo_email",commAddressDetailsVO.getEmail());
            request.put("billTo_ipAddress",commAddressDetailsVO.getCardHolderIpAddress());
            request.put("billTo_phoneNumber",commAddressDetailsVO.getPhone());
            request.put("card_accountNumber",commCardDetailsVO.getCardNum());
            request.put("card_expirationMonth",commCardDetailsVO.getExpMonth());
            request.put("card_expirationYear",commCardDetailsVO.getExpYear());
            request.put("card_cardType",CyberSourceUtils.getCardType(commTransactionDetailsVO.getCardType()));
            request.put("purchaseTotals_currency",commTransactionDetailsVO.getCurrency());
            request.put("purchaseTotals_grandTotalAmount",commTransactionDetailsVO.getAmount());

            CyberSourceUtils.displayMap("Payout Request-----",request);
            Map<String,String> map= Client.runTransaction(request, props);
            CyberSourceUtils.displayMap("Payout Response-----",map);

            if(map!=null && !map.isEmpty() && map.size()!=0){

                String decision="";
                String ccCreditReply_amount="";
                String requestID="";
                String ccCreditReply_requestDateTime="";
                String  reasonCode="";
                String ccCreditReply_reasonCode="";
                String purchaseTotals_currency="";

                if(functions.isValueNull(map.get("decision"))){
                    decision=map.get("decision");
                }
                if(functions.isValueNull(map.get("ccCreditReply_amount"))){
                    ccCreditReply_amount=map.get("ccCreditReply_amount");
                }
                if(functions.isValueNull(map.get("requestID"))){
                    requestID=map.get("requestID");
                }
                if(functions.isValueNull(map.get("ccCreditReply_requestDateTime"))){
                    ccCreditReply_requestDateTime=map.get("ccCreditReply_requestDateTime");
                }
                if(functions.isValueNull(map.get("reasonCode"))){
                    reasonCode=map.get("reasonCode");
                }
                if(functions.isValueNull(map.get("ccCreditReply_reasonCode"))){
                    ccCreditReply_reasonCode=map.get("ccCreditReply_reasonCode");
                }

                if (functions.isValueNull(map.get("purchaseTotals_currency"))) {
                    purchaseTotals_currency = map.get("purchaseTotals_currency");
                }

                if("100".equals(reasonCode) && "ACCEPT".equalsIgnoreCase(decision) && "100".equals(ccCreditReply_reasonCode) ){
                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark("SYS: Transaction Successful");
                    commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("SYS: Transaction Failed");
                    if (functions.isValueNull(CyberSourceErrorCode.getDescription(reasonCode)))
                        commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                    else
                        commResponseVO.setDescription(decision);
                   // commResponseVO.setDescription(decision);
                }
                commResponseVO.setTransactionId(requestID);
                commResponseVO.setErrorCode(reasonCode);
                commResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
                commResponseVO.setAmount(ccCreditReply_amount);
                commResponseVO.setCurrency(purchaseTotals_currency);
                commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                commResponseVO.setBankTransactionDate(ccCreditReply_requestDateTime);
            }

        }catch (Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("inside processInquiry -----");
        Functions functions = new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO= new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        String merchantId=gatewayAccount.getMerchantId();
        String username = gatewayAccount.getFRAUD_FTP_USERNAME();
        String password = gatewayAccount.getPassword();
        String credentials = new String(org.apache.commons.codec.binary.Base64.encodeBase64((username + ":" + password).getBytes()));
        transactionLogger.error("username:::::" + username);
        transactionLogger.error("password:::::" + password);
        String responseTime=commRequestVO.getTransDetailsVO().getResponsetime();
        String newResponseTime=CyberSourceUtils.getResponseTime(responseTime);
        String trackingId=commRequestVO.getTransDetailsVO().getOrderId();
        boolean isTest = gatewayAccount.isTest();
        String status="";

        String inquiryRequest = "" +
                "merchantID=" +merchantId +     //mid
                "&merchantReferenceNumber=" +trackingId+        //tracking id
                "&targetDate=" +newResponseTime+                   // tracking id's date
                "&type=transaction" +
                "&subtype=transactionDetail";

        transactionLogger.error("inquiryRequest-----"+inquiryRequest);
        String inquiryResponse="";

        if(isTest){
            transactionLogger.error("inside isTest-----" + RB.getString("Test_Inquiry_URL"));
            inquiryResponse = CyberSourceUtils.doPostHTTPSURLConnectionClient(inquiryRequest , RB.getString("Test_Inquiry_URL"), "BASIC", credentials);
        }else {
            transactionLogger.error("inside isLive-----" + RB.getString("Live_Inquiry_URL"));
            inquiryResponse = CyberSourceUtils.doPostHTTPSURLConnectionClient(inquiryRequest , RB.getString("Live_Inquiry_URL"), "BASIC", credentials);
        }
        transactionLogger.error("inquiryResponse ---"+inquiryResponse);

        if (functions.isValueNull(inquiryResponse))
        {
            // Inquire response will be html if mid is not active in test mode;
            // Inquire response will be xml if request is with right values or in live mode;
            transactionLogger.debug("inside function");

            if(inquiryResponse.contains("xml"))
            {
                transactionLogger.debug("inside if xml");
                Map readResponse = CyberSourceUtils.readSoapResponse(inquiryResponse);
                if (!readResponse.equals("") && readResponse != null)
                {
                    transactionLogger.debug("inside readresponse");
                    String rCode = (String) readResponse.get("RCode"); // success-1  fail-0
                    String rFlag = (String) readResponse.get("RFlag"); // success-SOK  fail-DAVSNO
                    String rMsg = (String) readResponse.get("RMsg");    // success-Request was processed successfully     fail- AVS check failed
                    String avsResultMapped = (String) readResponse.get("AVSResultMapped"); // success-1 fail-N
                    String avsResult = (String) readResponse.get("AVSResult"); // Success- This Tag Does Not Comes.   Fail-Comes with N

                    if(functions.isValueNull(rCode) && rCode.equals("1") )
                    {
                        status="success";
                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    }
                    else
                    {
                        status="fail";
                    }
                    transactionLogger.error("status-----"+status);
                    commResponseVO.setStatus(status);
                    commResponseVO.setRemark(rMsg);
                    commResponseVO.setDescription(rFlag + ":" + rMsg);
                    commResponseVO.setAuthCode("-");
                    commResponseVO.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
                    commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
                    commResponseVO.setResponseTime(responseTime);
                    commResponseVO.setMerchantId(merchantId);
                    // auth fail transaction in live also gives response with rcode value "0"
                    return commResponseVO;
                }
               /* else
                {
                    // This else is for if wrong tracking id or date is provided in request then no value in readResponse
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("invalid Details");
                    commResponseVO.setDescription("invalid Details");
                    commResponseVO.setAuthCode("-");
                    commResponseVO.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());
                    commResponseVO.setTransactionStatus("fail");
                    commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                    commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
                    commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
                    commResponseVO.setMerchantId(merchantId);
                    commResponseVO.setResponseTime(responseTime);
                    return  commResponseVO;
                }*/
            }
            else
            {
                // this else is for if not an XML response
                transactionLogger.debug("inside not xml");
                commResponseVO.setStatus("Error");
                commResponseVO.setRemark("Error");
                commResponseVO.setDescription("Error");
                commResponseVO.setAuthCode("-");
                commResponseVO.setTransactionId(commTransactionDetailsVO.getPreviousTransactionId());
                commResponseVO.setTransactionStatus("Error");
                commResponseVO.setTransactionType(PZProcessType.INQUIRY.toString());
                commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
                commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());
                commResponseVO.setMerchantId(merchantId);
                commResponseVO.setBankTransactionDate(responseTime);
            }
        }

        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processCommon3DAuthConfirmation-----");
        Functions functions= new Functions();
        Comm3DRequestVO commRequestVO= (Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        String Md=commRequestVO.getMd();
        String data[]= Md.split("@");
        String payerAuthValidateServiceTransactionID=data[0];
        String cvv=data[1];
        /*String invoiceHeader_merchantDescriptor="";
        String invoiceHeader_merchantDescriptorStreet="";*/

        String addressValidation="";

        String merchantId=gatewayAccount.getMerchantId();
        Properties props = CyberSourceUtils.convertResourceBundleToProperties(RB);

        boolean isTest=gatewayAccount.isTest();
        if(isTest){
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/test");
            props.setProperty("sendToProduction","false");
            props.setProperty("serverURL","https://ics2wstesta.ic3.com/commerce/1.x/transactionProcessor");
        }else {
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/live");
            props.setProperty("sendToProduction","true");
            props.setProperty("serverURL","https://ics2ws.ic3.com/commerce/1.x/transactionProcessor");
        }

        props.setProperty("merchantID",merchantId);
        props.setProperty("keyAlias",merchantId);
        props.setProperty("keyPassword",merchantId);

        transactionLogger.error("merchantID-----" + props.getProperty("merchantID"));
        transactionLogger.error("keyAlias-----" + props.getProperty("keyAlias"));
        transactionLogger.error("keyPassword-----" + props.getProperty("keyPassword"));
        transactionLogger.error("sendToProduction-----" + props.getProperty("sendToProduction"));
        transactionLogger.error("serverURL-----"+props.getProperty("serverURL"));
        transactionLogger.error("keysDirectory-----"+props.getProperty("keysDirectory"));


        if(functions.isValueNull(gatewayAccount.getAddressValidation())){
            transactionLogger.error("addressValidation-----"+gatewayAccount.getAddressValidation());
            addressValidation=gatewayAccount.getAddressValidation();
        }

       /* if(functions.isValueNull(commMerchantVO.getBrandName())){
            transactionLogger.error("invoiceHeader_merchantDescriptor-----"+commMerchantVO.getBrandName());
            invoiceHeader_merchantDescriptor=commMerchantVO.getBrandName();
        }
        if(functions.isValueNull(commMerchantVO.getAddress())){
            transactionLogger.error("invoiceHeader_merchantDescriptorStreet-----"+commMerchantVO.getAddress());
            invoiceHeader_merchantDescriptorStreet=commMerchantVO.getAddress();
        }*/

        try
        {
            HashMap<String, String> request = new HashMap<>();
            request.put("payerAuthValidateService_run", "true");
            request.put("merchantReferenceCode", trackingID);
            request.put("purchaseTotals_currency", commTransactionDetailsVO.getCurrency());
            request.put("purchaseTotals_grandTotalAmount", commTransactionDetailsVO.getAmount());
            request.put("payerAuthValidateServiceTransactionID", payerAuthValidateServiceTransactionID);
            request.put("payerAuthValidateService_signedPARes", commRequestVO.getPaRes());

            CyberSourceUtils.displayMap("Authentication Auth Request-----", request);
            Map<String, String> map = Client.runTransaction(request, props);
            CyberSourceUtils.displayMap("Authentication Auth Response-----", map);

            if (map != null && !map.isEmpty() && map.size() != 0)
            {
                String payerAuthValidateReply_authenticationStatusMessage = "";
                String decision = "";
                String payerAuthValidateReply_commerceIndicator = "";
                String payerAuthValidateReply_reasonCode = "";
                String payerAuthValidateReply_eci = "";
                String requestID = "";
                String payerAuthValidateReply_authenticationResult = "";
                String payerAuthValidateReply_paresStatus = "";
                String reasonCode = "";
                String payerAuthValidateReply_xid = "";
                String payerAuthValidateReply_cavv = "";

                if (functions.isValueNull(map.get("decision")))
                {
                    decision = map.get("decision");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_authenticationStatusMessage")))
                {
                    payerAuthValidateReply_authenticationStatusMessage = map.get("payerAuthValidateReply_authenticationStatusMessage");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_commerceIndicator")))
                {
                    payerAuthValidateReply_commerceIndicator = map.get("payerAuthValidateReply_commerceIndicator");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_reasonCode")))
                {
                    payerAuthValidateReply_reasonCode = map.get("payerAuthValidateReply_reasonCode");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_eci")))
                {
                    payerAuthValidateReply_eci = map.get("payerAuthValidateReply_eci");
                }
                if (functions.isValueNull(map.get("requestID")))
                {
                    requestID = map.get("requestID");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_authenticationResult")))
                {
                    payerAuthValidateReply_authenticationResult = map.get("payerAuthValidateReply_authenticationResult");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_paresStatus")))
                {
                    payerAuthValidateReply_paresStatus = map.get("payerAuthValidateReply_paresStatus");
                }
                if (functions.isValueNull(map.get("reasonCode")))
                {
                    reasonCode = map.get("reasonCode");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_xid")))
                {
                    payerAuthValidateReply_xid = map.get("payerAuthValidateReply_xid");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_cavv")))
                {
                    payerAuthValidateReply_cavv = map.get("payerAuthValidateReply_cavv");
                }
                if ("100".equals(reasonCode) && "100".equals(payerAuthValidateReply_reasonCode) && "ACCEPT".equalsIgnoreCase(decision) && "Success".equalsIgnoreCase(payerAuthValidateReply_authenticationStatusMessage))
                {

                    request = new HashMap<>();
                    request.put("ccAuthService_run", "true");
                    request.put("authIndicator", "1");
                    request.put("merchantReferenceCode", trackingID);
                    if("Y".equals(addressValidation)){
                        request.put("billTo_firstName", commAddressDetailsVO.getFirstname());
                        request.put("billTo_lastName", commAddressDetailsVO.getLastname());
                        request.put("billTo_street1", commAddressDetailsVO.getStreet());
                        request.put("billTo_city", commAddressDetailsVO.getCity());
                        request.put("billTo_state", commAddressDetailsVO.getState());
                        request.put("billTo_postalCode", commAddressDetailsVO.getZipCode());
                        request.put("billTo_country", commAddressDetailsVO.getCountry());
                        request.put("billTo_email", commAddressDetailsVO.getEmail());
                        request.put("billTo_ipAddress", commAddressDetailsVO.getCardHolderIpAddress());
                        request.put("billTo_phoneNumber", commAddressDetailsVO.getPhone());
                    }
                    request.put("card_accountNumber", commCardDetailsVO.getCardNum());
                    request.put("card_cvNumber", cvv);
                    request.put("card_expirationMonth", commCardDetailsVO.getExpMonth());
                    request.put("card_expirationYear", commCardDetailsVO.getExpYear());
                    request.put("purchaseTotals_currency", commTransactionDetailsVO.getCurrency());
                    request.put("purchaseTotals_grandTotalAmount", commTransactionDetailsVO.getAmount());
                    request.put("ccAuthService_commerceIndicator",payerAuthValidateReply_commerceIndicator);
                    request.put("ccAuthService_xid",payerAuthValidateReply_xid);
                    request.put("ccAuthService_paresStatus",payerAuthValidateReply_paresStatus);
                    request.put("ccAuthService_cavv",payerAuthValidateReply_cavv);
                    request.put("invoiceHeader_merchantDescriptor", gatewayAccount.getDisplayName());
                    request.put("invoiceHeader_merchantDescriptorStreet",gatewayAccount.getFRAUD_FILE_SHORT_NAME());
                    request.put("invoiceHeader_merchantDescriptorCity",gatewayAccount.getFRAUD_FTP_PATH());
                    request.put("invoiceHeader_merchantDescriptorContact",gatewayAccount.getFRAUD_FTP_PATH());

                    CyberSourceUtils.displayMap("Final Auth Request-----", request);
                    map = Client.runTransaction(request, props);
                    CyberSourceUtils.displayMap("Final Auth Response-----", map);

                    if (map != null && !map.isEmpty() && map.size() != 0)
                    {
                        String ccAuthReply_reasonCode = "";
                        String ccAuthReply_amount = "";
                        String purchaseTotals_currency="";
                        String ccAuthReply_authorizationCode="";
                        String ccAuthReply_authorizedDateTime="";

                        if (functions.isValueNull(map.get("decision"))) {
                            decision = map.get("decision");
                        }
                        if (functions.isValueNull(map.get("reasonCode"))) {
                            reasonCode = map.get("reasonCode");
                        }
                        if (functions.isValueNull(map.get("requestID"))) {
                            requestID = map.get("requestID");
                        }
                        if (functions.isValueNull(map.get("ccAuthReply_reasonCode"))){
                            ccAuthReply_reasonCode = map.get("ccAuthReply_reasonCode");
                        }
                        if (functions.isValueNull(map.get("ccAuthReply_amount"))){
                            ccAuthReply_amount = map.get("ccAuthReply_amount");
                        }
                        if (functions.isValueNull(map.get("purchaseTotals_currency"))){
                            purchaseTotals_currency = map.get("purchaseTotals_currency");
                        }
                        if (functions.isValueNull(map.get("ccAuthReply_authorizationCode"))) {
                            ccAuthReply_authorizationCode = map.get("ccAuthReply_authorizationCode");
                        }
                        if (functions.isValueNull(map.get("ccAuthReply_authorizedDateTime"))) {
                            ccAuthReply_authorizedDateTime = map.get("ccAuthReply_authorizedDateTime");
                        }

                        if("100".equals(reasonCode) && "ACCEPT".equalsIgnoreCase(decision) && "100".equals(ccAuthReply_reasonCode)){
                            commResponseVO.setStatus("success");
                            commResponseVO.setRemark("SYS: Transaction Successful");
                            commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }
                        else
                        {
                            commResponseVO.setStatus("fail");
                            commResponseVO.setRemark("SYS: Transaction Failed");
                            if (functions.isValueNull(CyberSourceErrorCode.getDescription(reasonCode)))
                                commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                            else
                                commResponseVO.setDescription(decision);
                            //commResponseVO.setDescription(decision);
                        }
                        commResponseVO.setTransactionId(requestID);
                        commResponseVO.setAuthCode(ccAuthReply_authorizationCode);
                        commResponseVO.setErrorCode(reasonCode);
                        commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
                        commResponseVO.setAmount(ccAuthReply_amount);
                        commResponseVO.setCurrency(purchaseTotals_currency);
                        commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                        commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                        commResponseVO.setBankTransactionDate(ccAuthReply_authorizedDateTime);
                        commResponseVO.setEci(payerAuthValidateReply_eci);
                        }

                    }
                }

        }catch (FaultException e){
            transactionLogger.error("FaultException-----",e);
        }catch (ClientException e){
            transactionLogger.error("ClientException-----",e);
        }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----inside processCommon3DSaleConfirmation-----");
        Functions functions= new Functions();
        Comm3DRequestVO commRequestVO= (Comm3DRequestVO) requestVO;
        Comm3DResponseVO commResponseVO= new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        /*String invoiceHeader_merchantDescriptor="";
        String invoiceHeader_merchantDescriptorStreet="";*/

        String merchantId=gatewayAccount.getMerchantId();
        Properties props = CyberSourceUtils.convertResourceBundleToProperties(RB);

        boolean isTest=gatewayAccount.isTest();
        if(isTest){
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/test");
            props.setProperty("sendToProduction","false");
            props.setProperty("serverURL","https://ics2wstesta.ic3.com/commerce/1.x/transactionProcessor");
        }else {
            props.setProperty("keysDirectory",""+props.getProperty("keysDirectory")+"/live");
            props.setProperty("sendToProduction","true");
            props.setProperty("serverURL","https://ics2ws.ic3.com/commerce/1.x/transactionProcessor");
        }

        props.setProperty("merchantID",merchantId);
        props.setProperty("keyAlias",merchantId);
        props.setProperty("keyPassword",merchantId);

        transactionLogger.error("merchantID-----" + props.getProperty("merchantID"));
        transactionLogger.error("keyAlias-----" + props.getProperty("keyAlias"));
        transactionLogger.error("keyPassword-----" + props.getProperty("keyPassword"));
        transactionLogger.error("sendToProduction-----" + props.getProperty("sendToProduction"));
        transactionLogger.error("serverURL-----"+props.getProperty("serverURL"));
        transactionLogger.error("keysDirectory-----"+props.getProperty("keysDirectory"));


        String addressValidation="";

        if(functions.isValueNull(gatewayAccount.getAddressValidation())){
            transactionLogger.error("addressValidation-----"+gatewayAccount.getAddressValidation());
            addressValidation=gatewayAccount.getAddressValidation();
        }


       /* if(functions.isValueNull(commMerchantVO.getBrandName())){
            transactionLogger.error("invoiceHeader_merchantDescriptor-----"+commMerchantVO.getBrandName());
            invoiceHeader_merchantDescriptor=commMerchantVO.getBrandName();
        }
        if(functions.isValueNull(commMerchantVO.getAddress())){
            transactionLogger.error("invoiceHeader_merchantDescriptorStreet-----"+commMerchantVO.getAddress());
            invoiceHeader_merchantDescriptorStreet=commMerchantVO.getAddress();
        }
*/
        try
        {
            HashMap<String, String> request = new HashMap<>();
            request.put("payerAuthValidateService_run", "true");
            request.put("merchantReferenceCode", trackingID);
            request.put("purchaseTotals_currency", commTransactionDetailsVO.getCurrency());
            request.put("purchaseTotals_grandTotalAmount", commTransactionDetailsVO.getAmount());
            request.put("payerAuthValidateServiceTransactionID", commRequestVO.getMd());
            request.put("payerAuthValidateService_signedPARes", commRequestVO.getPaRes());

            CyberSourceUtils.displayMap("Authentication Sale Request-----", request);
            Map<String, String> map = Client.runTransaction(request, props);
            CyberSourceUtils.displayMap("Authentication Sale Response-----", map);

            if (map != null && !map.isEmpty() && map.size() != 0)
            {
                String payerAuthValidateReply_authenticationStatusMessage = "";
                String decision = "";
                String payerAuthValidateReply_commerceIndicator = "";
                String payerAuthValidateReply_reasonCode = "";
                String payerAuthValidateReply_eci = "";
                String requestID = "";
                String payerAuthValidateReply_authenticationResult = "";
                String payerAuthValidateReply_paresStatus = "";
                String reasonCode = "";
                String payerAuthValidateReply_xid = "";
                String payerAuthValidateReply_cavv = "";

                if (functions.isValueNull(map.get("decision")))
                {
                    decision = map.get("decision");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_authenticationStatusMessage")))
                {
                    payerAuthValidateReply_authenticationStatusMessage = map.get("payerAuthValidateReply_authenticationStatusMessage");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_commerceIndicator")))
                {
                    payerAuthValidateReply_commerceIndicator = map.get("payerAuthValidateReply_commerceIndicator");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_reasonCode")))
                {
                    payerAuthValidateReply_reasonCode = map.get("payerAuthValidateReply_reasonCode");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_eci")))
                {
                    payerAuthValidateReply_eci = map.get("payerAuthValidateReply_eci");
                }
                if (functions.isValueNull(map.get("requestID")))
                {
                    requestID = map.get("requestID");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_authenticationResult")))
                {
                    payerAuthValidateReply_authenticationResult = map.get("payerAuthValidateReply_authenticationResult");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_paresStatus")))
                {
                    payerAuthValidateReply_paresStatus = map.get("payerAuthValidateReply_paresStatus");
                }
                if (functions.isValueNull(map.get("reasonCode")))
                {
                    reasonCode = map.get("reasonCode");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_xid")))
                {
                    payerAuthValidateReply_xid = map.get("payerAuthValidateReply_xid");
                }
                if (functions.isValueNull(map.get("payerAuthValidateReply_cavv")))
                {
                    payerAuthValidateReply_cavv = map.get("payerAuthValidateReply_cavv");
                }
                if ("100".equals(reasonCode) && "100".equals(payerAuthValidateReply_reasonCode) && "ACCEPT".equalsIgnoreCase(decision) && "Success".equalsIgnoreCase(payerAuthValidateReply_authenticationStatusMessage))
                {

                    request = new HashMap<>();
                    request.put("ccAuthService_run", "true");
                    request.put("ccCaptureService_run", "true");
                    request.put("ccAuthService_authType", "AUTOCAPTURE");
                    request.put("merchantReferenceCode", trackingID);
                    if("Y".equals(addressValidation)){
                        request.put("billTo_firstName", commAddressDetailsVO.getFirstname());
                        request.put("billTo_lastName", commAddressDetailsVO.getLastname());
                        request.put("billTo_street1", commAddressDetailsVO.getStreet());
                        request.put("billTo_city", commAddressDetailsVO.getCity());
                        request.put("billTo_state", commAddressDetailsVO.getState());
                        request.put("billTo_postalCode", commAddressDetailsVO.getZipCode());
                        request.put("billTo_country", commAddressDetailsVO.getCountry());
                        request.put("billTo_email", commAddressDetailsVO.getEmail());
                        request.put("billTo_ipAddress", commAddressDetailsVO.getCardHolderIpAddress());
                        request.put("billTo_phoneNumber", commAddressDetailsVO.getPhone());
                    }
                    request.put("card_accountNumber", commCardDetailsVO.getCardNum());
                    request.put("card_cvNumber", commCardDetailsVO.getcVV());
                    request.put("card_expirationMonth", commCardDetailsVO.getExpMonth());
                    request.put("card_expirationYear", commCardDetailsVO.getExpYear());
                    request.put("purchaseTotals_currency", commTransactionDetailsVO.getCurrency());
                    request.put("purchaseTotals_grandTotalAmount", commTransactionDetailsVO.getAmount());
                    request.put("ccAuthService_commerceIndicator",payerAuthValidateReply_commerceIndicator);
                    request.put("ccAuthService_xid",payerAuthValidateReply_xid);
                    request.put("ccAuthService_paresStatus",payerAuthValidateReply_paresStatus);
                    request.put("ccAuthService_cavv",payerAuthValidateReply_cavv);
                    request.put("invoiceHeader_merchantDescriptor", gatewayAccount.getDisplayName());
                    request.put("invoiceHeader_merchantDescriptorStreet",gatewayAccount.getFRAUD_FILE_SHORT_NAME());
                    request.put("invoiceHeader_merchantDescriptorCity",gatewayAccount.getFRAUD_FTP_PATH());
                    request.put("invoiceHeader_merchantDescriptorContact",gatewayAccount.getFRAUD_FTP_PATH());

                    CyberSourceUtils.displayMap("Final Sale Request-----", request);
                    map = Client.runTransaction(request, props);
                    CyberSourceUtils.displayMap("Final Sale Response-----", map);

                    if (map != null && !map.isEmpty() && map.size() != 0)
                    {
                        String ccAuthReply_reasonCode = "";
                        String ccCaptureReply_amount = "";
                        String purchaseTotals_currency="";
                        String ccAuthReply_authorizationCode="";
                        String ccCaptureReply_requestDateTime="";
                        String ccCaptureReply_reasonCode="";

                        if (functions.isValueNull(map.get("decision"))){
                            decision = map.get("decision");
                        }
                        if (functions.isValueNull(map.get("ccAuthReply_reasonCode"))){
                            ccAuthReply_reasonCode = map.get("ccAuthReply_reasonCode");
                        }
                        if (functions.isValueNull(map.get("ccCaptureReply_reasonCode"))){
                            ccCaptureReply_reasonCode = map.get("ccCaptureReply_reasonCode");
                        }
                        if (functions.isValueNull(map.get("ccCaptureReply_amount"))){
                            ccCaptureReply_amount = map.get("ccCaptureReply_amount");
                        }
                        if (functions.isValueNull(map.get("purchaseTotals_currency"))){
                             purchaseTotals_currency = map.get("purchaseTotals_currency");
                        }
                        if (functions.isValueNull(map.get("reasonCode"))){
                            reasonCode = map.get("reasonCode");
                        }
                        if (functions.isValueNull(map.get("ccAuthReply_authorizationCode"))){
                            ccAuthReply_authorizationCode = map.get("ccAuthReply_authorizationCode");
                        }
                        if (functions.isValueNull(map.get("requestID"))){
                            requestID = map.get("requestID");
                        }
                        if (functions.isValueNull(map.get("ccCaptureReply_requestDateTime"))){
                            ccCaptureReply_requestDateTime = map.get("ccCaptureReply_requestDateTime");
                        }

                        if("100".equals(reasonCode) && "ACCEPT".equalsIgnoreCase(decision) && "100".equals(ccAuthReply_reasonCode) && "100".equals(ccCaptureReply_reasonCode)){
                            commResponseVO.setStatus("success");
                            commResponseVO.setRemark("SYS: Transaction Successful");
                            commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                            commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                        }
                        else
                        {
                            commResponseVO.setStatus("fail");
                            commResponseVO.setRemark("SYS: Transaction Failed");
                            if (functions.isValueNull(CyberSourceErrorCode.getDescription(reasonCode)))
                                commResponseVO.setDescription(decision+"/"+CyberSourceErrorCode.getDescription(reasonCode));
                            else
                                commResponseVO.setDescription(decision);
                           // commResponseVO.setDescription(decision);
                        }
                        commResponseVO.setTransactionId(requestID);
                        commResponseVO.setAuthCode(ccAuthReply_authorizationCode);
                        commResponseVO.setErrorCode(reasonCode);
                        commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                        commResponseVO.setAmount(ccCaptureReply_amount);
                        commResponseVO.setCurrency(purchaseTotals_currency);
                        commResponseVO.setTmpl_Amount(commAddressDetailsVO.getTmpl_amount());
                        commResponseVO.setTmpl_Currency(commAddressDetailsVO.getTmpl_currency());
                        commResponseVO.setBankTransactionDate(ccCaptureReply_requestDateTime);
                        commResponseVO.setEci(payerAuthValidateReply_eci);
                    }

                }
            }

        }catch (FaultException e){
            transactionLogger.error("FaultException-----",e);
        }catch (ClientException e){
            transactionLogger.error("ClientException-----",e);
        }


        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}

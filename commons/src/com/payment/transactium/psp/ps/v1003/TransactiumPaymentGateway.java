package com.payment.transactium.psp.ps.v1003;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.TransactiumLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.google.gson.Gson;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.transactium.psp.ps.v1003.Transactium_Resin_Applications_PSPSoap_v1003.*;
import com.payment.utils.SSLUtils;
import org.apache.commons.codec.binary.Base64;

import javax.xml.rpc.ServiceException;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import static junit.framework.Assert.assertNotNull;

public class TransactiumPaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE="transctium";
    public static final String GATEWAY_TYPE_TRX="trx";
    public static final String GATEWAY_TYPE_TRANSACTIUM_1="trans1";

    //TransactiumLogger transactionLogger= new TransactiumLogger(TransactiumPaymentGateway.class.getName());
    TransactionLogger transactionLogger= new TransactionLogger(TransactiumPaymentGateway.class.getName());
    ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.transactium");
    Functions functions = new Functions();
    public TransactiumPaymentGateway(String acoountId){this.accountId=acoountId;}

    public static void main(String[] args)
    {
        try
        {
            org.apache.axis.types.UnsignedInt amount=new org.apache.axis.types.UnsignedInt();
            amount.setValue((long) 100.00);

            String pares ="eNrVWFev4ziy/iuN3kdjRlm2Bu6zoHKwZCuHN1mSFa1sK/z6K5/Tp6en7yywe5/2EjBMlshiseqrj+H4z/lefXkm/ZA39bevyO/w1y//fDtaWZ8krJlEjz55O6rJMIRp8iWPv301LQHeCmsEX9+OF2Akw7t4D2MIjG+i76reNk2/o0fos7np6KMsrMe3Yxh1tKS94S818BH63jzek15i35CtoFs5Qh/tI/TnwMvjVRs2g+Y8flNZMP348ZptrK86zdlV8+0IvXoc43BM3lAYOcA4jH1BkD9w6g+UOELv8mP7UgfuzWPTjbws+Vlw3FbeJ3W0vB3w7dOP1jGZ26ZOth6biT/qR+hP29qwfoN/Kq8Fbbo36dHy3o5jfv+rTQTyB4IdoXf5cRjD8TG8+Ufoe+0Yhc/nGwCAYfyKLmmulYIKVktB5ybwKtta37sckyh/g7fFvf7fR4Eqbfp8zO4vU/8qOEIvU6D38L0dzTytt8n65MuGhXr49jUbx/YPCJqm6fcJ+73pUwjdFgLBFLR1iIc8/cfXj1FJLNW35j8axoR1U+dRWOVrOG7IUJMxa+IvP2z7OzWW8dKEQAbH/Lap+i1C8Pq3l2QDHbHphP5e6U8r+3dm+dXYfgh/G7IQeU3wi6K3o5Hckhciki+2IX37+o8f+GfzNBnG/8t8n3P9rOFTnxNWj+RtuhWiWHAkENxbugzPEj48VB12BR3+9jnuo+cR+mHgd+s/QvWTSz46pmqEXm0JmdLIiOpAPchX8XBZKOTURomDPPhwyrSZAqeDNnjKUkNoPT8x6mbuhXGOvf208lzG+ObzCWcl7lrY1Kul0QX3h883ieZUwX0lTlApw3tjIRCbSoKlHSiESs4DdQsmx4/3F5VZZLvQUtqU5D3VhJZKEebOwcLY1/OYnklpmsgh2fFiCgJlllW2sgaNnECURTB/P4+3glMt5zqTczcvA0Zy+I6+UrC2G9skxsIBlmm29HEd5se+IHM4q3GrtyQ7jL0DPXoGOumiTqZnj01OnXcAGiK5RNzp/ghh5W6OK6/em7dZcZAMP7OJqg72zp58iRwIW720pwPGLpdrgfSMdVkXdfr27SfIfI+IkiwfEfAImGLDMfyoMUk/5rcNuxslqZLEBhbDgBJNwSTRIJUccBZFWR3zvZ+IJNMGhlHh1LriIVumKoAFxuwEU7pirM7RzGQDVUhndgUanWoODVSLrmjZ4SlWtzhHpVUBIDbHzKrqlLLrCM4a8xoSeka14WhiJp91dF3hJsM1HY1W9cPE6u8ykZsCLnAJNPCkmbPA5UN/ajGc9rwK1OK7xlPVh4n56C9wk+zYK0j4CZ61gpvUAsxqwa0qy4cv2Zn9q0wVwcyvwPlut8reief17twitKqvFVUEngyrhj1xHzaeWDC7ehXYgafBNua0sWDPrAVOH+MbleYD2Ya5n3wRqXRpVNc6qKKCM1Rw+PDF5mgdpYYrJmeBcJiZFcgf/X0LVI61xYSWipeOssvKXKAmmAY6xwNwZoB+AK/vTKpsdQ7khR1Yd6rXLnZ22cAh1QFh3pUgCt3rFIeXXOE4CpI5ds0fjqztQVZxiOx0LoavtjiIeb0jjMzip44NNU9xrELiLvMDVdmcnLMgHcMGQpE7o4TW6jnLYOvakxlaYNhQnTHxFbO3vJKkEpxJDKvgJ0VeFTXAYIx/ZAKtJvKh9lWyY1vIZjHDsyLzerXMU4KJzMrdAUUa0EVz2XI0/T1EE3bWClvO0c9dCmV4covOgaxNsXCuynTArLrbsui5FNFlV7LCHRX2Z/KGd5xNUR33DPyAtxS9oZpANm7qfmBPSk6LOJCGSwgINh/l/RwQAhfpZmp4pnOwxEh3mYJZbxwGTmk4jp0YeIQusUAH9N9hHeBbjujA7jyhDtMV38Gzc751KmhOtQINtklI3YXmMxZiqW6P5mrN1r07jId6cqYHUpzI+SaJbroWXh4XEOGrsGAIp4YhYo6DkwLbO660+o0/mhedZd0n0wNbbaaZihi3ZFSVDKq4w/e7c4pgKMROTB/LPEdXZFaXNASTLpPPignPocVBZMQ8HCzKF41uUGZMKa/uDlAXtDFB0Psx8hOYOOH36brXWuMU6GvibRFExSZ8psZtuZzahCHjdhb7kGw1bVLyXoPLG/dAGORSpP3i4ltisIrSDj4k5LegCiPMLGIlm7hkkh7tRR5cPZA2SjR78ZDLHmEHXDIekJ1pOeH4ZOr0kjvac+c+97N0hH5lp7+jK0540dUkfNKVAU5+RG1B24/mbJG5sOPu+d2xXzQiK00gZc9I21LlROtg2rxyUkH5noZ0pjKOo/6cwtaWwm2EcvNpBeOHbLDk6l2Wmi4Bb5Tw8D2jvaJEdmVoa2ujoatVEsevEUoVocvDoUs9VKOZBPBOGyw3827oaZkkOMUVRcZtTBGYtKFbn1QFCNUqF5VVV42NX1S1aOtGUxY3aayKv2T/XRS7+d+UfvUvvfmXTVPuAtjtu94wW50GSh8HkG4XE86u5b7kidoK9me/u5fIHkvx6zPFjZ0i4sg6rtFkP7QL2fTDLhgiFDJ54zah6k2dkPN0QTW1zjKqOcR05uzlJuFsJ6KmQoNdkWR3YwfEawGbYXfOuow+PKAqrExqFq/tcykhjliFQrDOU7NtAJLXlsnhYZKo9SBERaGF+ZkFkGyBxy2mnycs39lqdys1a/Z8TgFKDIs5KYr981xW3cQdduwgtfga9K3PQsYjZvFeF4XJM3U4PNDarK/LvXGLJj8PqiP5Soo8K10KJgrvNcqpr6PRwDHSV0PUAq4htfIS06gv3FlXCvbTdCUHnt3rfCPIsXXw8pIWo5ElL0FyiGusHYcN/xwA4Zl+sgz9sID5iptoqDS4HTiGBQLQRQikHFBF/YWNWALGqArDFOo+H+DcRm+vTQ0UdJr2dMrxtB4xwPC9UDTgiG02F8RYvBDoC7en+4YFk5pCtJzOui8pk7/F2ha33JtOFvD5jDcDjx78Vx7wRhN6arphEI9ZwG420MK//n5+t1s/vNu94XrDjT+JL6wZ8JmmfY5XmzQpQRHwdCSlYClvpgnJ21Jluv/X9Mz0A5sDNNq5/R2xdpxFwFosUCi7bRp5qSD5sHbMdv7C/eRJxMUEO/RzTzGxuYy9aqcZFMEJ5K/IvWWR9SxplyR3GsKsK4Qulkep6iTXoJVnYCm/kSWzXvyh0dDGG81JiA65CuyxlAwpRQL0cMKke+OI++gSWblrZH1/AM1DsqddxaftsDQbJ4piC6vWfcW5h0dqA9PuFsDKso/1A8GAwloq1dcNTB5IwPbWPl3Ok3hDx2d+UuoRYUxRXHYEQy74ITxzN7m57y1wTvGqqMrS4M8K69p2VCEyXYpjMtunBvICxJFwzzglChopQUhm3t6R51Kj4RNt99AZx/P9cycEbmqpQntQCm7+N+mZL7b0f5Kf9Kwn4wMx1bmtOQ3U9XlFLrvLaP16urF5wNLt/6YaTlPB8EFvmco592r1Le6pMvAn5d3fZSYtX2ujiu5EFgvVdorjB4nXqu160fqoneqetl5Rrf2gbnwWCuB/UinnfNB74G4wd5FNxwumPK3SuMdar5Mit50YwaquEXx2mk2m/ir7L9tW/gN61kr7Vin3tibQ0ZewYKS1i3vx8QFX5BoSHTfi2Z3p5OHZpt3sICRL/LhqUMNMgjSWN2V+mjlrGHfzxtiSTHewW3E69ojA6QrRO+ncG5Oe5kOHXTVYCk4ggldRahfXe9zDphz2Q2g82SFEp/66QtyIdKUKF1AjQs9zv217ikjJFNGVTIdv28bARQtRK9ONLVsevTVlMMK2ovDj7IiRyV1MDlk1DdlQPJtau3+23jI/T+bAQF7cH4S9xaToFVlmz+WL/gIjjw2mXC0rVh1IOIPaSTbNxcamzM0cLAgIz5nNbu6l77kRkBYTsVR/bim2nTwtN2XhspaRbbs3cbsGeCmV+GlucQ2F4tEPen5YDD0WH9vqJz3T1kaLGz3/v6dmej6ISxjrwK1w+kFjNjtvt0ReK6X1V2rebPqk5gQR0OwyALAzGq2BI6dGdlYSHUw/1mnA7zumPlF8koUPfJ9g5M0RtHw9l4tkUUAUmp40kJMUZoq348h7p1v+sNaySWSefBAOEXyB73PhmNF1v5STTky033muil49pc1PVuI77g0BPe92LbJQAmnA4BzWp+I+aQYN";
            Gson gson= new Gson();

            String stringSignature="integ_api*8585858*Integ_api1";
            String encodedStr = new String(stringSignature);
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte signature[] = md.digest(encodedStr.getBytes("UTF-8"));
            //System.out.println("Signature-----"+signature.toString());
            Request request= new Request();
            RequestAuthenticationType requestAuthenticationType= new RequestAuthenticationType();
            requestAuthenticationType.setUserName("integ_api");
            //requestAuthenticationType.setPassword("Integ_api1");
            requestAuthenticationType.setSignature(signature);
            RequestCardType requestCardType= new RequestCardType();
            requestCardType.setNumber("4012001037141112");   //4000000000000002  //4012001037141112
            requestCardType.setExpiryYYMM("1810");
            requestCardType.setCVV2("123");
            requestCardType.setHolderName("Uday Raj");

           /* RequestThreeDSType requestThreeDSType= new RequestThreeDSType();
            requestThreeDSType.setPARes(Base64.decodeBase64(pares.getBytes()));

            RequestLinkedType requestLinkedType= new RequestLinkedType();
            requestLinkedType.setReferenceId("STG0000DRZ");*/
            RequestTransactionType requestTransactionType= new RequestTransactionType();
            requestTransactionType.setAmount(amount);
            requestTransactionType.setCurrency("USD");
            RequestOptionsType requestOptionsType= new RequestOptionsType();
            requestOptionsType.setAcceptLanguage("En");
            requestOptionsType.setIPv4Address("192.168.0.1");
            requestOptionsType.setMAP(false);

            RequestReferencesType requestReferencesType= new RequestReferencesType();
            requestReferencesType.setMerchant("8585858");
            requestReferencesType.setOrder("Product8585888");

            RequestBillingType requestBillingType= new RequestBillingType();
            requestBillingType.setFullName("Uday Raj");
            requestBillingType.setEmail("udaybhan.rajbhar@pz.com");
            requestBillingType.setCityName("Mumbai");
            requestBillingType.setPhone("+919870850511");
            requestBillingType.setCountryCode("IN");
            requestBillingType.setPostalCode("400064");
            requestBillingType.setStreetName("Malad Mindspace");

            request.setAuthentication(requestAuthenticationType);
            request.setCard(requestCardType);
           // request.setThreeDS(requestThreeDSType);
            request.setOptions(requestOptionsType);
           // request.setLinked(requestLinkedType);
             request.setReferences(requestReferencesType);
             request.setTransaction(requestTransactionType);
             request.setBilling(requestBillingType);





            String req=gson.toJson(request);
           // System.out.println("request-----"+req);

            BasicHttpBinding_IPSPSoapStub stub = new BasicHttpBinding_IPSPSoapStub();
            PSPSoapAppLocator pspSoapAppLocator= new PSPSoapAppLocator();
           // pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress("https://psp.stg.transactium.com/ps/PSPSoap.v1003.svc");
            pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress("https://pspt.trxsecure.co.za/ps/PSPSoap.v1003.svc");

            stub=(BasicHttpBinding_IPSPSoapStub)pspSoapAppLocator.getBasicHttpBinding_IPSPSoap();
            assertNotNull("binding is null", stub);

            Response response=stub.process(RequestType.Sale,request);
            //  Response response=stub.process(RequestType.ThreeDSFinalisation,request);
            //   Response response=stub.threeDSFinalisation(request);





            //  Response response=stub.checkTransaction(requestAuthenticationType,"STG0000DLA");

           // System.out.println("Result-----" + response.getResult());

            String response1=gson.toJson(response);

            System.out.println("response--------"+response1);

            /*String [] errorMessage=response.getFraudScreening().getBlockReason();
            for (String s : errorMessage)
            {
                //System.out.println("s---"+s);
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            Calendar c= response.getResult().getTimeStamp();*/
            /*System.out.println("TimeZone-----"+c.getTimeZone());
            System.out.println("Time----"+c.getTime());
            System.out.println("Time in Millis----"+c.getTimeInMillis());*/

           // Timestamp i= new Timestamp(c.getTimeInMillis());

           /* System.out.println("Timestamp----"+i);
            System.out.println("Timestamp----"+i.toString());

            System.out.println("TimeStampbb---"+String.valueOf(dateFormat.format(i)));*/

         /*   byte[] PAReq=response.getThreeDS().getPAReq();
            String PaReq1 = new String(Base64.encodeBase64(PAReq));
*/
            //System.out.println("PaReq--------"+PaReq1);

           // String redirectUrl=response.getThreeDS().getRedirectURL();

            //System.out.println("RedirectUrl------"+redirectUrl);



           /* System.out.println("Remarks-----"+ response.getResult().getMessage());
            System.out.println(("Status-----"+response.getResult().getCode().getValue()));
            System.out.println(("ErrorMessage---" + response.getFraudScreening().getBlockReason()));
            System.out.println(("ErrorCode---" + String.valueOf(response.getFraudScreening().getIntBlockReason())));
            System.out.println(("ResponseTime---" + response.getResult().getTimeStamp()));
            System.out.println(("Transactionid---" + response.getTransaction().getId()));
            System.out.println("TransactionType----"+PZProcessType.SALE.toString());*/

        }catch (Exception e){
            //e.printStackTrace();
        }
    }

    public static String getCentAmount(String amount)
    {
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 100;
        Integer newAmount = dObj2.intValue();

        return newAmount.toString();
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("-----Enter's into processSale-----");
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO=new Comm3DResponseVO();
        Functions functions=new Functions();
        try{
           /* SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());*/
            System.setProperty("https.protocols", "TLSv1.2");

            CommCardDetailsVO commCardDetailsVO= commRequestVO.getCardDetailsVO();
            CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
            CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
            CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String gatewayType=gatewayAccount.getGateway();
            Gson gson= new Gson();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            String username=gatewayAccount.getMerchantId();
            String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
            String reject3DCard = commRequestVO.getReject3DCard();
            boolean isTest= gatewayAccount.isTest();
            String is3dSupported=gatewayAccount.get_3DSupportAccount();
            org.apache.axis.types.UnsignedInt amount=new org.apache.axis.types.UnsignedInt();
            amount.setValue(Long.parseLong(getCentAmount(commTransactionDetailsVO.getAmount())));

            String Test_Url="";
            String Live_Url="";

            transactionLogger.error("GatewayType-----" + gatewayType);

            if("trx".equalsIgnoreCase(gatewayType)){
                Test_Url=RB.getString("TEST_URL_TRX");
                Live_Url=RB.getString("LIVE_URL_TRX");

            }else {
                Test_Url=RB.getString("TEST_URL");
                Live_Url=RB.getString("LIVE_URL");
            }

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

            String currency="";
            String tmpl_amount="";
            String tmpl_currency="";
            if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
            {
                currency=commTransactionDetailsVO.getCurrency();
            }
            if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
            {
                tmpl_amount=commAddressDetailsVO.getTmpl_amount();
            }
            if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
            {
                tmpl_currency=commAddressDetailsVO.getTmpl_currency();
            }

            String stringSignature=""+username+"*"+trackingID+"*"+password+"";
            String encodedStr = new String(stringSignature);
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte signature[] = md.digest(encodedStr.getBytes("UTF-8"));

            String year=commCardDetailsVO.getExpYear();
            year=year.substring(2);
            String month=commCardDetailsVO.getExpMonth();

            String expiryYYMM=year.concat(month);

            RequestCardType requestCardType= new RequestCardType();
            requestCardType.setNumber(commCardDetailsVO.getCardNum());
            requestCardType.setExpiryYYMM(expiryYYMM);
            requestCardType.setCVV2(commCardDetailsVO.getcVV());
            requestCardType.setHolderName(removeLeadingTrailingSpace(commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname()));

            RequestCardType requestCardTypeLog= new RequestCardType();
            requestCardTypeLog.setNumber(functions.maskingPan(commCardDetailsVO.getCardNum()));
            requestCardTypeLog.setExpiryYYMM(functions.maskingNumber(expiryYYMM));
            requestCardTypeLog.setCVV2(functions.maskingNumber(commCardDetailsVO.getcVV()));
            requestCardTypeLog.setHolderName(removeLeadingTrailingSpace(commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname()));

            RequestAuthenticationType requestAuthenticationType= new RequestAuthenticationType();
            requestAuthenticationType.setUserName(username);
            requestAuthenticationType.setSignature(signature);


            RequestTransactionType requestTransactionType= new RequestTransactionType();
            requestTransactionType.setAmount(amount);
            requestTransactionType.setCurrency(commTransactionDetailsVO.getCurrency());

            RequestOptionsType requestOptionsType= new RequestOptionsType();
            requestOptionsType.setAcceptLanguage(removeLeadingTrailingSpace(commAddressDetailsVO.getLanguage()));
            requestOptionsType.setIPv4Address(commAddressDetailsVO.getCardHolderIpAddress());
            requestOptionsType.setMAP(false);

            RequestReferencesType requestReferencesType= new RequestReferencesType();
            requestReferencesType.setMerchant(trackingID);
            requestReferencesType.setOrder(commTransactionDetailsVO.getMerchantOrderId());

            RequestBillingType requestBillingType= new RequestBillingType();
            requestBillingType.setFullName(removeLeadingTrailingSpace(commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname()));
            requestBillingType.setEmail(removeLeadingTrailingSpace(commAddressDetailsVO.getEmail()));
            requestBillingType.setCityName(removeLeadingTrailingSpace(commAddressDetailsVO.getCity()));
            requestBillingType.setPhone(removeLeadingTrailingSpace(commAddressDetailsVO.getPhone()));
            requestBillingType.setCountryCode(removeLeadingTrailingSpace(commAddressDetailsVO.getCountry()));
            requestBillingType.setPostalCode(removeLeadingTrailingSpace(commAddressDetailsVO.getZipCode()));
            requestBillingType.setStreetName(removeLeadingTrailingSpace(commAddressDetailsVO.getStreet()));

            Request request= new Request();
            request.setAuthentication(requestAuthenticationType);
            request.setCard(requestCardType);
            request.setOptions(requestOptionsType);
            request.setReferences(requestReferencesType);
            request.setTransaction(requestTransactionType);
            request.setBilling(requestBillingType);

            Request requestLog= new Request();
            requestLog.setAuthentication(requestAuthenticationType);
            requestLog.setCard(requestCardTypeLog);
            requestLog.setOptions(requestOptionsType);
            requestLog.setReferences(requestReferencesType);
            requestLog.setTransaction(requestTransactionType);
            requestLog.setBilling(requestBillingType);

            String saleRequest=gson.toJson(request);
            String saleRequestLog=gson.toJson(requestLog);
            transactionLogger.error("sale Request---"+trackingID+"---"+saleRequestLog);

            BasicHttpBinding_IPSPSoapStub stub = new BasicHttpBinding_IPSPSoapStub();
            PSPSoapAppLocator pspSoapAppLocator= new PSPSoapAppLocator();

            if(isTest){
                transactionLogger.error("inside isTest-----" +Test_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Test_Url);
            }else
            {
                transactionLogger.error("inside Live-----"+Live_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Live_Url);
            }
            stub=(BasicHttpBinding_IPSPSoapStub)pspSoapAppLocator.getBasicHttpBinding_IPSPSoap();
            assertNotNull("binding is null", stub);

            Response response=null;

            if ("Y".equals(is3dSupported))
            {
                response = stub.process(RequestType.ThreeDSSale, request);
            }
            else
            {
                response = stub.process(RequestType.Sale, request);
            }
            if(response!=null){

                String errorMesaage="";
                String erroCode="";
                String saleResponse= gson.toJson(response);
                String declinedReason = "";
                transactionLogger.error("sale Response---"+trackingID+"--"+saleResponse);

                if(response.getFraudScreening()!=null)
                {
                    String[] errorMessage = response.getFraudScreening().getBlockReason();
                    if (errorMessage.length != 0)
                    {
                        for (String s : errorMessage)
                        {
                            if (s.equalsIgnoreCase("None"))
                            {
                                errorMesaage = response.getResult().getCode().getValue();
                            }
                            else
                            {
                                errorMesaage = s;
                            }
                        }
                    }
                    transactionLogger.error("response code-host code--"+response.getResult().getHostCode());
                    if(functions.isValueNull(response.getResult().getHostCode()))
                        erroCode = response.getResult().getHostCode();
                    else
                        erroCode= String.valueOf(response.getFraudScreening().getIntBlockReason());

                    if(functions.isValueNull(TransactiumErrorCode.getDescription(erroCode)))
                        declinedReason = TransactiumErrorCode.getDescription(erroCode);

                }
                else
                {
                    errorMesaage = response.getResult().getCode().getValue();
                }

                Calendar c= response.getResult().getTimeStamp();
                Timestamp timestamp= new Timestamp(c.getTimeInMillis());

                if("Enrolled".equalsIgnoreCase(response.getResult().getCode().getValue()))
                {
                    if ("Y".equals(reject3DCard))
                    {
                        transactionLogger.error("rejecting 3d card as per configuration ");
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription("3D Enrolled Card");
                        commResponseVO.setRemark("3D Enrolled Card");
                        return commResponseVO;
                    }
                    else if("Yes".equals(response.getThreeDS().getEnrolled().getValue()))
                    {
                        byte[] PAReq=response.getThreeDS().getPAReq();
                        String PaReq = new String(Base64.encodeBase64(PAReq));
                        String redirectURL=response.getThreeDS().getRedirectURL();
                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setPaReq(PaReq);
                        commResponseVO.setUrlFor3DRedirect(redirectURL);
                        commResponseVO.setTerURL(termUrl+trackingID);
                        commResponseVO.setMd(response.getTransaction().getId());
                        commResponseVO.setRemark("3D  Enrolled Card");
                        commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                    }
                }
                else if("Approved".equalsIgnoreCase(response.getResult().getCode().getValue()))
                {
                    if(response.getThreeDS()!=null)
                    {
                        commResponseVO.setEci(String.valueOf(response.getThreeDS().getECI()));
                    }
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    commResponseVO.setRemark(response.getResult().getMessage());
                }
                else
                {
                    if(response.getThreeDS()!=null)
                    {
                        commResponseVO.setEci(String.valueOf(response.getThreeDS().getECI()));
                    }
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    if(functions.isValueNull(declinedReason))
                        commResponseVO.setRemark(declinedReason);
                    else
                        commResponseVO.setRemark(response.getResult().getMessage());
                }
                if(functions.isValueNull(response.getReferences().getARN()))
                {
                    commResponseVO.setArn(response.getReferences().getARN());
                }
                commResponseVO.setDescription(errorMesaage+" "+declinedReason);
                commResponseVO.setErrorCode(erroCode);
                commResponseVO.setTransactionId(response.getTransaction().getId());
                commResponseVO.setTransactionStatus(response.getResult().getCode().getValue());
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(timestamp)));
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
            }
        }
        catch (RemoteException e)
        {
            transactionLogger.error("RemoteException:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processSale()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        catch (ServiceException se)
        {
            transactionLogger.error("ServiceException:::::",se);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processSale()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (NoSuchAlgorithmException ne)
        {
            transactionLogger.error("NoSuchAlgorithmException:::::",ne);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processSale()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause());
        }
        catch (UnsupportedEncodingException ue)
        {
            transactionLogger.error("UnsupportedEncodingException:::::",ue);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processSale()", null, "common", "Exception while placing  Sale transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----Enter's into processAuthentication-----");
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        Comm3DResponseVO commResponseVO=new Comm3DResponseVO();
        Functions functions= new Functions();
        try{
            /*SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());*/
            System.setProperty("https.protocols", "TLSv1.2");

            CommCardDetailsVO commCardDetailsVO= commRequestVO.getCardDetailsVO();
            CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
            CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
            CommMerchantVO commMerchantVO=commRequestVO.getCommMerchantVO();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String gatewayType=gatewayAccount.getGateway();
            Gson gson= new Gson();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


            String reject3DCard = commRequestVO.getReject3DCard();
            String username=gatewayAccount.getMerchantId();
            String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest= gatewayAccount.isTest();
            String is3dSupported=gatewayAccount.get_3DSupportAccount();
            org.apache.axis.types.UnsignedInt amount=new org.apache.axis.types.UnsignedInt();
            amount.setValue(Long.parseLong(getCentAmount(commTransactionDetailsVO.getAmount())));

            String Test_Url="";
            String Live_Url="";

            transactionLogger.error("GatewayType-----" + gatewayType);

            if("trx".equalsIgnoreCase(gatewayType)){
                Test_Url=RB.getString("TEST_URL_TRX");
                Live_Url=RB.getString("LIVE_URL_TRX");

            }else {
                Test_Url=RB.getString("TEST_URL");
                Live_Url=RB.getString("LIVE_URL");
            }


            transactionLogger.error("host url----"+commMerchantVO.getHostUrl());
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

            String currency="";
            String tmpl_amount="";
            String tmpl_currency="";
            if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
            {
                currency=commTransactionDetailsVO.getCurrency();
            }
            if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
            {
                tmpl_amount=commAddressDetailsVO.getTmpl_amount();
            }
            if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
            {
                tmpl_currency=commAddressDetailsVO.getTmpl_currency();
            }


            String stringSignature=""+username+"*"+trackingID+"*"+password+"";
            String encodedStr = new String(stringSignature);
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte signature[] = md.digest(encodedStr.getBytes("UTF-8"));

            String year=commCardDetailsVO.getExpYear();
            year=year.substring(2);
            String month=commCardDetailsVO.getExpMonth();

            String expiryYYMM=year.concat(month);

            RequestCardType requestCardType= new RequestCardType();
            requestCardType.setNumber(commCardDetailsVO.getCardNum());
            requestCardType.setExpiryYYMM(expiryYYMM);
            requestCardType.setCVV2(commCardDetailsVO.getcVV());
            requestCardType.setHolderName(removeLeadingTrailingSpace(commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname()));

            RequestCardType requestCardTypeLog= new RequestCardType();
            requestCardTypeLog.setNumber(functions.maskingPan(commCardDetailsVO.getCardNum()));
            requestCardTypeLog.setExpiryYYMM(functions.maskingNumber(expiryYYMM));
            requestCardTypeLog.setCVV2(functions.maskingNumber(commCardDetailsVO.getcVV()));
            requestCardTypeLog.setHolderName(removeLeadingTrailingSpace(commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname()));

            RequestAuthenticationType requestAuthenticationType= new RequestAuthenticationType();
            requestAuthenticationType.setUserName(username);
            requestAuthenticationType.setSignature(signature);


            RequestTransactionType requestTransactionType= new RequestTransactionType();
            requestTransactionType.setAmount(amount);
            requestTransactionType.setCurrency(commTransactionDetailsVO.getCurrency());

            RequestOptionsType requestOptionsType= new RequestOptionsType();
            requestOptionsType.setAcceptLanguage(removeLeadingTrailingSpace(commAddressDetailsVO.getLanguage()));
            requestOptionsType.setIPv4Address(commAddressDetailsVO.getCardHolderIpAddress());
            requestOptionsType.setMAP(false);

            RequestReferencesType requestReferencesType= new RequestReferencesType();
            requestReferencesType.setMerchant(trackingID);
            requestReferencesType.setOrder(commTransactionDetailsVO.getMerchantOrderId());

            RequestBillingType requestBillingType= new RequestBillingType();
            requestBillingType.setFullName(removeLeadingTrailingSpace(commAddressDetailsVO.getFirstname() + " " + commAddressDetailsVO.getLastname()));
            requestBillingType.setEmail(removeLeadingTrailingSpace(commAddressDetailsVO.getEmail()));
            requestBillingType.setCityName(removeLeadingTrailingSpace(commAddressDetailsVO.getCity()));
            requestBillingType.setPhone(removeLeadingTrailingSpace(commAddressDetailsVO.getPhone()));
            requestBillingType.setCountryCode(removeLeadingTrailingSpace(commAddressDetailsVO.getCountry()));
            requestBillingType.setPostalCode(removeLeadingTrailingSpace(commAddressDetailsVO.getZipCode()));
            requestBillingType.setStreetName(removeLeadingTrailingSpace(commAddressDetailsVO.getStreet()));

            Request request= new Request();
            request.setAuthentication(requestAuthenticationType);
            request.setCard(requestCardType);
            request.setOptions(requestOptionsType);
            request.setReferences(requestReferencesType);
            request.setTransaction(requestTransactionType);
            request.setBilling(requestBillingType);

            Request requestLog= new Request();
            requestLog.setAuthentication(requestAuthenticationType);
            requestLog.setCard(requestCardTypeLog);
            requestLog.setOptions(requestOptionsType);
            requestLog.setReferences(requestReferencesType);
            requestLog.setTransaction(requestTransactionType);
            requestLog.setBilling(requestBillingType);

            String authRequest=gson.toJson(request);
            String authRequestLog=gson.toJson(requestLog);
            transactionLogger.error("auth Request----"+trackingID+"--"+authRequestLog);

            BasicHttpBinding_IPSPSoapStub stub = new BasicHttpBinding_IPSPSoapStub();
            PSPSoapAppLocator pspSoapAppLocator= new PSPSoapAppLocator();

            if(isTest){
                transactionLogger.error("inside isTest-----" + Test_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Test_Url);
            }else
            {
                transactionLogger.error("inside Live-----"+Live_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Live_Url);
            }
            stub=(BasicHttpBinding_IPSPSoapStub)pspSoapAppLocator.getBasicHttpBinding_IPSPSoap();
            assertNotNull("binding is null", stub);

            Response response=new Response();
            if ("Y".equals(is3dSupported))
            {
                response = stub.process(RequestType.ThreeDSPreAuthorisation, request);
            }
            else
            {
                response = stub.process(RequestType.PreAuthorisation, request);
            }

            if(response!=null){

                String errorMesaage="";
                String erroCode="";
                String authResponse= gson.toJson(response);
                transactionLogger.error("auth Response---"+trackingID+"--"+authResponse);

                if(response.getFraudScreening()!=null)
                {
                    String[] errorMessage = response.getFraudScreening().getBlockReason();
                    if (errorMessage.length != 0)
                    {
                        for (String s : errorMessage)
                        {
                            if (s.equalsIgnoreCase("None"))
                            {
                                errorMesaage = response.getResult().getCode().getValue();
                            }
                            else
                            {
                                errorMesaage = s;
                            }
                        }
                    }

                    erroCode= String.valueOf(response.getFraudScreening().getIntBlockReason());
                }else {
                    errorMesaage = response.getResult().getCode().getValue();
                }

                Calendar c= response.getResult().getTimeStamp();
                Timestamp timestamp= new Timestamp(c.getTimeInMillis());

                if("Enrolled".equalsIgnoreCase(response.getResult().getCode().getValue()))
                {
                    if ("Y".equals(reject3DCard))
                    {
                        transactionLogger.error("rejecting 3d card as per configuration ");
                        commResponseVO.setStatus("failed");
                        commResponseVO.setDescription("3D Enrolled Card");
                        commResponseVO.setRemark("3D Enrolled Card");
                        return commResponseVO;
                    }
                    else if("Yes".equals(response.getThreeDS().getEnrolled().getValue()))
                    {
                        byte[] PAReq=response.getThreeDS().getPAReq();
                        String PaReq = new String(Base64.encodeBase64(PAReq));
                        String redirectURL=response.getThreeDS().getRedirectURL();

                        commResponseVO.setStatus("pending3DConfirmation");
                        commResponseVO.setPaReq(PaReq);
                        commResponseVO.setUrlFor3DRedirect(redirectURL);
                        commResponseVO.setTerURL(termUrl+trackingID);
                        commResponseVO.setMd(response.getTransaction().getId());
                        commResponseVO.setRemark("3D  Enrolled Card");
                        commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                    }
                }
                else if("Approved".equalsIgnoreCase(response.getResult().getCode().getValue()))
                {
                    if(response.getThreeDS()!=null){
                        commResponseVO.setEci(String.valueOf(response.getThreeDS().getECI()));
                    }
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    commResponseVO.setRemark(response.getResult().getMessage());
                }else
                {
                    if(response.getThreeDS()!=null){
                        commResponseVO.setEci(String.valueOf(response.getThreeDS().getECI()));
                    }
                    commResponseVO.setStatus("fail");
                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    commResponseVO.setRemark(response.getResult().getMessage());
                }
                if(functions.isValueNull(response.getReferences().getARN())){
                    commResponseVO.setArn(response.getReferences().getARN());
                }
                commResponseVO.setDescription(errorMesaage);
                commResponseVO.setErrorCode(erroCode);
                commResponseVO.setTransactionId(response.getTransaction().getId());
                commResponseVO.setTransactionStatus(response.getResult().getCode().getValue());
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(timestamp)));
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
            }
        }catch (RemoteException e){
            transactionLogger.error("RemoteException:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Exception while placing  Auth transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }catch (ServiceException se){
            transactionLogger.error("ServiceException:::::",se);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Exception while placing  Auth transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());

        }catch (NoSuchAlgorithmException ne){
            transactionLogger.error("NoSuchAlgorithmException:::::",ne);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Exception while placing  Auth transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause());

        }catch (UnsupportedEncodingException ue){
            transactionLogger.error("UnsupportedEncodingException:::::",ue);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processAuthentication()", null, "common", "Exception while placing  Auth transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());

        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Enter's into processCapture-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();
        try{
            /*SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());*/
            System.setProperty("https.protocols", "TLSv1.2");

            CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String gatewayType=gatewayAccount.getGateway();

            Gson gson= new Gson();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            String username=gatewayAccount.getMerchantId();
            String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest= gatewayAccount.isTest();
            org.apache.axis.types.UnsignedInt amount=new org.apache.axis.types.UnsignedInt();
            amount.setValue(Long.parseLong(getCentAmount(commTransactionDetailsVO.getAmount())));

            String Test_Url="";
            String Live_Url="";

            transactionLogger.error("GatewayType-----" + gatewayType);

            if("trx".equalsIgnoreCase(gatewayType)){
                Test_Url=RB.getString("TEST_URL_TRX");
                Live_Url=RB.getString("LIVE_URL_TRX");

            }else {
                Test_Url=RB.getString("TEST_URL");
                Live_Url=RB.getString("LIVE_URL");
            }

            String stringSignature=""+username+"*"+trackingID+"_C"+"*"+password+"";
            String encodedStr = new String(stringSignature);
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte signature[] = md.digest(encodedStr.getBytes("UTF-8"));

            RequestAuthenticationType requestAuthenticationType= new RequestAuthenticationType();
            requestAuthenticationType.setUserName(username);
            requestAuthenticationType.setSignature(signature);

            RequestLinkedType requestLinkedType= new RequestLinkedType();
            requestLinkedType.setReferenceId(commTransactionDetailsVO.getPreviousTransactionId());

            RequestTransactionType requestTransactionType= new RequestTransactionType();
            requestTransactionType.setAmount(amount);
            requestTransactionType.setCurrency(commTransactionDetailsVO.getCurrency());

            RequestReferencesType requestReferencesType= new RequestReferencesType();
            requestReferencesType.setMerchant(trackingID + "_C");

            Request request= new Request();
            request.setAuthentication(requestAuthenticationType);
            request.setReferences(requestReferencesType);
            request.setLinked(requestLinkedType);
            request.setTransaction(requestTransactionType);

            String captureRequest=gson.toJson(request);
            transactionLogger.error("capture Request------" + captureRequest);

            BasicHttpBinding_IPSPSoapStub stub = new BasicHttpBinding_IPSPSoapStub();
            PSPSoapAppLocator pspSoapAppLocator= new PSPSoapAppLocator();

            if(isTest){
                transactionLogger.error("inside isTest-----" +Test_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Test_Url);
            }else
            {
                transactionLogger.error("inside Live-----"+Live_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Live_Url);
            }
            stub=(BasicHttpBinding_IPSPSoapStub)pspSoapAppLocator.getBasicHttpBinding_IPSPSoap();
            assertNotNull("binding is null", stub);

            Response response=null;
            response = stub.process(RequestType.Completion, request);
            if(response!=null){

                String errorMesaage="";
                String erroCode="";
                String captureResponse= gson.toJson(response);
                transactionLogger.error("capture Response-----"+captureResponse);

                if(response.getFraudScreening()!=null)
                {
                    String[] errorMessage = response.getFraudScreening().getBlockReason();
                    if (errorMessage.length != 0)
                    {
                        for (String s : errorMessage)
                        {
                            if (s.equalsIgnoreCase("None"))
                            {
                                errorMesaage = response.getResult().getCode().getValue();
                            }
                            else
                            {
                                errorMesaage = s;
                            }
                        }
                    }

                    erroCode= String.valueOf(response.getFraudScreening().getIntBlockReason());
                }else {
                    errorMesaage = response.getResult().getCode().getValue();
                }

                Calendar c= response.getResult().getTimeStamp();
                Timestamp timestamp= new Timestamp(c.getTimeInMillis());

                if("Approved".equalsIgnoreCase(response.getResult().getCode().getValue())){
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                }
                if(functions.isValueNull(response.getReferences().getARN())){
                    commResponseVO.setArn(response.getReferences().getARN());
                }
                commResponseVO.setRemark(response.getResult().getMessage());
                commResponseVO.setDescription(errorMesaage);
                commResponseVO.setErrorCode(String.valueOf((response.getFraudScreening().getIntBlockReason())));
                commResponseVO.setTransactionId(response.getTransaction().getId());
                commResponseVO.setTransactionStatus(response.getResult().getCode().getValue());
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(timestamp)));
                commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
            }

        }catch (RemoteException e){
            transactionLogger.error("RemoteException:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processCapture()", null, "common", "Exception while placing  Capture transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }catch (ServiceException se){
            transactionLogger.error("ServiceException:::::",se);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processCapture()", null, "common", "Exception while placing  Capture transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());

        }catch (NoSuchAlgorithmException ne){
            transactionLogger.error("NoSuchAlgorithmException:::::",ne);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processCapture()", null, "common", "Exception while placing  Capture transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause());

        }catch (UnsupportedEncodingException ue){
            transactionLogger.error("UnsupportedEncodingException:::::",ue);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processCapture()", null, "common", "Exception while placing  Capture transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());

        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Enter's into processRefund-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();
        try{
            /*SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());*/
            System.setProperty("https.protocols", "TLSv1.2");

            CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String gatewayType=gatewayAccount.getGateway();
            Gson gson= new Gson();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String rCount = "";
            if (functions.isValueNull(commRequestVO.getCount()))
                rCount = commRequestVO.getCount();

            String username=gatewayAccount.getMerchantId();
            String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest= gatewayAccount.isTest();
            org.apache.axis.types.UnsignedInt amount=new org.apache.axis.types.UnsignedInt();
            amount.setValue(Long.parseLong(getCentAmount(commTransactionDetailsVO.getAmount())));

            String stringSignature=""+username+"*"+trackingID+"_R"+rCount+"*"+password+"";
            String encodedStr = new String(stringSignature);
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte signature[] = md.digest(encodedStr.getBytes("UTF-8"));

            RequestAuthenticationType requestAuthenticationType= new RequestAuthenticationType();
            requestAuthenticationType.setUserName(username);
            requestAuthenticationType.setSignature(signature);

            RequestLinkedType requestLinkedType= new RequestLinkedType();
            requestLinkedType.setReferenceId(commTransactionDetailsVO.getPreviousTransactionId());

            RequestTransactionType requestTransactionType= new RequestTransactionType();
            requestTransactionType.setAmount(amount);
            requestTransactionType.setCurrency(commTransactionDetailsVO.getCurrency());

            RequestReferencesType requestReferencesType= new RequestReferencesType();
            requestReferencesType.setMerchant(trackingID+"_R"+rCount);

            Request request= new Request();
            request.setAuthentication(requestAuthenticationType);
            request.setReferences(requestReferencesType);
            request.setLinked(requestLinkedType);
            request.setTransaction(requestTransactionType);

            String refundRequest=gson.toJson(request);
            transactionLogger.error("refund Request------"+refundRequest);

            BasicHttpBinding_IPSPSoapStub stub = new BasicHttpBinding_IPSPSoapStub();
            PSPSoapAppLocator pspSoapAppLocator= new PSPSoapAppLocator();
            String Test_Url="";
            String Live_Url="";

            transactionLogger.error("GatewayType-----" + gatewayType);

            if("trx".equalsIgnoreCase(gatewayType)){
                Test_Url=RB.getString("TEST_URL_TRX");
                Live_Url=RB.getString("LIVE_URL_TRX");

            }else {
                Test_Url=RB.getString("TEST_URL");
                Live_Url=RB.getString("LIVE_URL");
            }

            String currency="";
            if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
            {
                currency=commTransactionDetailsVO.getCurrency();
            }

            if(isTest){
                transactionLogger.error("inside isTest-----" +Test_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Test_Url);
            }else
            {
                transactionLogger.error("inside Live-----"+Live_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Live_Url);
            }
            stub=(BasicHttpBinding_IPSPSoapStub)pspSoapAppLocator.getBasicHttpBinding_IPSPSoap();
            assertNotNull("binding is null", stub);

            Response response=null;
            response = stub.process(RequestType.Refund, request);

            if(response!=null){

                String errorMesaage="";
                String erroCode="";
                String refundResponse= gson.toJson(response);
                transactionLogger.error("refund Response-----"+refundResponse);

                if(response.getFraudScreening()!=null)
                {
                    String[] errorMessage = response.getFraudScreening().getBlockReason();
                    if (errorMessage.length != 0)
                    {
                        for (String s : errorMessage)
                        {
                            if (s.equalsIgnoreCase("None"))
                            {
                                errorMesaage = response.getResult().getCode().getValue();
                            }
                            else
                            {
                                errorMesaage = s;
                            }
                        }
                    }

                    erroCode= String.valueOf(response.getFraudScreening().getIntBlockReason());
                }else {
                    errorMesaage = response.getResult().getCode().getValue();
                }

                Calendar c= response.getResult().getTimeStamp();
                Timestamp timestamp= new Timestamp(c.getTimeInMillis());

                if("Approved".equalsIgnoreCase(response.getResult().getCode().getValue())){
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                }
                if(functions.isValueNull(response.getReferences().getARN())){
                    commResponseVO.setArn(response.getReferences().getARN());
                }
                commResponseVO.setRemark(response.getResult().getMessage());
                commResponseVO.setDescription(errorMesaage);
                commResponseVO.setErrorCode(String.valueOf((response.getFraudScreening().getIntBlockReason())));
                commResponseVO.setTransactionId(response.getTransaction().getId());
                commResponseVO.setTransactionStatus(response.getResult().getCode().getValue());
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(timestamp)));
                commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                commResponseVO.setCurrency(currency);
            }

        }catch (RemoteException e){
            transactionLogger.error("RemoteException:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processRefund()", null, "common", "Exception while placing  Refund transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }catch (ServiceException se){
            transactionLogger.error("ServiceException:::::",se);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processRefund()", null, "common", "Exception while placing  Refund transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());

        }catch (NoSuchAlgorithmException ne){
            transactionLogger.error("NoSuchAlgorithmException:::::",ne);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processRefund()", null, "common", "Exception while placing  Refund transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause());

        }catch (UnsupportedEncodingException ue){
            transactionLogger.error("UnsupportedEncodingException:::::",ue);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processRefund()", null, "common", "Exception while placing  Refund transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());

        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processPayout(String trackingId, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZGenericConstraintViolationException
    {
        transactionLogger.error("-----Enter's into processPayout-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();
        try{
            /*SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());*/
            System.setProperty("https.protocols", "TLSv1.2");

            CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String gatewayType=gatewayAccount.getGateway();
            Gson gson= new Gson();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            String username=gatewayAccount.getMerchantId();
            String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest= gatewayAccount.isTest();
            org.apache.axis.types.UnsignedInt amount=new org.apache.axis.types.UnsignedInt();
            amount.setValue(Long.parseLong(getCentAmount(commTransactionDetailsVO.getAmount())));

            String stringSignature=""+username+"*"+trackingId+"*"+password+"";
            String encodedStr = new String(stringSignature);
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte signature[] = md.digest(encodedStr.getBytes("UTF-8"));

            RequestAuthenticationType requestAuthenticationType= new RequestAuthenticationType();
            requestAuthenticationType.setUserName(username);
            requestAuthenticationType.setSignature(signature);

            RequestLinkedType requestLinkedType= new RequestLinkedType();
            requestLinkedType.setReferenceId(commTransactionDetailsVO.getPaymentId());

            RequestTransactionType requestTransactionType= new RequestTransactionType();
            requestTransactionType.setAmount(amount);
            requestTransactionType.setCurrency(commTransactionDetailsVO.getCurrency());

            RequestReferencesType requestReferencesType= new RequestReferencesType();
            requestReferencesType.setMerchant(trackingId);

            Request request= new Request();
            request.setAuthentication(requestAuthenticationType);
            request.setReferences(requestReferencesType);
            request.setLinked(requestLinkedType);
            request.setTransaction(requestTransactionType);

            String payoutRequest=gson.toJson(request);
            transactionLogger.error("payout Request------"+payoutRequest);
            BasicHttpBinding_IPSPSoapStub stub = new BasicHttpBinding_IPSPSoapStub();
            PSPSoapAppLocator pspSoapAppLocator= new PSPSoapAppLocator();

            String Test_Url="";
            String Live_Url="";

            transactionLogger.error("GatewayType-----" + gatewayType);

            if("trx".equalsIgnoreCase(gatewayType)){
                Test_Url=RB.getString("TEST_URL_TRX");
                Live_Url=RB.getString("LIVE_URL_TRX");

            }else {
                Test_Url=RB.getString("TEST_URL");
                Live_Url=RB.getString("LIVE_URL");
            }

            if(isTest){
                transactionLogger.error("inside isTest-----" + Test_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Test_Url);
            }else
            {
                transactionLogger.error("inside Live-----"+Live_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Live_Url);
            }
            stub=(BasicHttpBinding_IPSPSoapStub)pspSoapAppLocator.getBasicHttpBinding_IPSPSoap();
            assertNotNull("binding is null", stub);

            Response response=null;
            response = stub.process(RequestType.CFT, request);

            if(response!=null){

                String errorMesaage="";
                String erroCode="";
                String payoutResponse= gson.toJson(response);
                transactionLogger.error("payout Response-----"+payoutResponse);

                if(response.getFraudScreening()!=null)
                {
                    String[] errorMessage = response.getFraudScreening().getBlockReason();
                    if (errorMessage.length != 0)
                    {
                        for (String s : errorMessage)
                        {
                            if (s.equalsIgnoreCase("None"))
                            {
                                errorMesaage = response.getResult().getCode().getValue();
                            }
                            else
                            {
                                errorMesaage = s;
                            }
                        }
                    }

                    erroCode= String.valueOf(response.getFraudScreening().getIntBlockReason());
                }else {
                    errorMesaage = response.getResult().getCode().getValue();
                }

                Calendar c= response.getResult().getTimeStamp();
                Timestamp timestamp= new Timestamp(c.getTimeInMillis());

                if("Approved".equalsIgnoreCase(response.getResult().getCode().getValue())){
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                }
                if(functions.isValueNull(response.getReferences().getARN())){
                    commResponseVO.setArn(response.getReferences().getARN());
                }
                commResponseVO.setRemark(response.getResult().getMessage());
                commResponseVO.setDescription(errorMesaage);
                commResponseVO.setErrorCode(String.valueOf((response.getFraudScreening().getIntBlockReason())));
                commResponseVO.setTransactionId(response.getTransaction().getId());
                commResponseVO.setTransactionStatus(response.getResult().getCode().getValue());
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(timestamp)));
                commResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
            }

        }catch (RemoteException e){
            transactionLogger.error("RemoteException:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processPayout()", null, "common", "Exception while placing  Payout transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }catch (ServiceException se){
            transactionLogger.error("ServiceException:::::",se);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processPayout()", null, "common", "Exception while placing  Payout transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());

        }catch (NoSuchAlgorithmException ne){
            transactionLogger.error("NoSuchAlgorithmException:::::",ne);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processPayout()", null, "common", "Exception while placing  Payout transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause());

        }catch (UnsupportedEncodingException ue){
            transactionLogger.error("UnsupportedEncodingException:::::",ue);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processPayout()", null, "common", "Exception while placing  Payout transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());

        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----Enter's into processVoid-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();
        try{
            /*SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());*/
            System.setProperty("https.protocols", "TLSv1.2");

            CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String gatewayType=gatewayAccount.getGateway();
            Gson gson= new Gson();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            String username=gatewayAccount.getMerchantId();
            String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest= gatewayAccount.isTest();
            org.apache.axis.types.UnsignedInt amount=new org.apache.axis.types.UnsignedInt();
            amount.setValue(Long.parseLong(getCentAmount(commTransactionDetailsVO.getAmount())));

            String stringSignature=""+username+"*"+trackingID+"_V"+"*"+password+"";
            String encodedStr = new String(stringSignature);
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte signature[] = md.digest(encodedStr.getBytes("UTF-8"));

            RequestAuthenticationType requestAuthenticationType= new RequestAuthenticationType();
            requestAuthenticationType.setUserName(username);
            requestAuthenticationType.setSignature(signature);

            RequestLinkedType requestLinkedType= new RequestLinkedType();
            requestLinkedType.setReferenceId(commTransactionDetailsVO.getPreviousTransactionId());

            RequestTransactionType requestTransactionType= new RequestTransactionType();
            requestTransactionType.setAmount(amount);
            requestTransactionType.setCurrency(commTransactionDetailsVO.getCurrency());

            RequestReferencesType requestReferencesType= new RequestReferencesType();
            requestReferencesType.setMerchant(trackingID+"_V");

            Request request= new Request();
            request.setAuthentication(requestAuthenticationType);
            request.setReferences(requestReferencesType);
            request.setLinked(requestLinkedType);
            request.setTransaction(requestTransactionType);

            String voidRequest=gson.toJson(request);
            transactionLogger.error("void Request------"+voidRequest);

            BasicHttpBinding_IPSPSoapStub stub = new BasicHttpBinding_IPSPSoapStub();
            PSPSoapAppLocator pspSoapAppLocator= new PSPSoapAppLocator();

            String Test_Url="";
            String Live_Url="";

            transactionLogger.error("GatewayType-----" + gatewayType);

            if("trx".equalsIgnoreCase(gatewayType)){
                Test_Url=RB.getString("TEST_URL_TRX");
                Live_Url=RB.getString("LIVE_URL_TRX");

            }else {
                Test_Url=RB.getString("TEST_URL");
                Live_Url=RB.getString("LIVE_URL");
            }


            if(isTest){
                transactionLogger.error("inside isTest-----" + Test_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Test_Url);
            }else
            {
                transactionLogger.error("inside Live-----"+Live_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Live_Url);
            }
            stub=(BasicHttpBinding_IPSPSoapStub)pspSoapAppLocator.getBasicHttpBinding_IPSPSoap();
            assertNotNull("binding is null", stub);

            Response response=null;
            response = stub.process(RequestType.Void, request);

            if(response!=null){

                String errorMesaage="";
                String erroCode="";
                String  errorCode="";
                String voidResponse= gson.toJson(response);
                transactionLogger.error("void Response-----"+voidResponse);

                if(response.getFraudScreening()!=null)
                {
                    String[] errorMessage = response.getFraudScreening().getBlockReason();
                    if (errorMessage.length != 0)
                    {
                        for (String s : errorMessage)
                        {
                            if (s.equalsIgnoreCase("None"))
                            {
                                errorMesaage = response.getResult().getCode().getValue();
                            }
                            else
                            {
                                errorMesaage = s;
                            }
                        }
                    }

                    erroCode= String.valueOf(response.getFraudScreening().getIntBlockReason());
                }else {
                    errorMesaage = response.getResult().getCode().getValue();
                }

                Calendar c= response.getResult().getTimeStamp();
                Timestamp timestamp= new Timestamp(c.getTimeInMillis());

                if("Approved".equalsIgnoreCase(response.getResult().getCode().getValue())){
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                }
                if(functions.isValueNull(response.getReferences().getARN())){
                    commResponseVO.setArn(response.getReferences().getARN());
                }
                commResponseVO.setRemark(response.getResult().getMessage());
                commResponseVO.setDescription(errorMesaage);
                commResponseVO.setErrorCode(errorCode);
                commResponseVO.setTransactionId(response.getTransaction().getId());
                commResponseVO.setTransactionStatus(response.getResult().getCode().getValue());
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(timestamp)));
                commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
            }

        }catch (RemoteException e){
            transactionLogger.error("RemoteException:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processVoid()", null, "common", "Exception while placing  Void transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }catch (ServiceException se){
            transactionLogger.error("ServiceException:::::",se);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processVoid()", null, "common", "Exception while placing  Void transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());

        }catch (NoSuchAlgorithmException ne){
            transactionLogger.error("NoSuchAlgorithmException:::::",ne);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processVoid()", null, "common", "Exception while placing  Void transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION, null, ne.getMessage(), ne.getCause());

        }catch (UnsupportedEncodingException ue){
            transactionLogger.error("UnsupportedEncodingException:::::",ue);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processVoid()", null, "common", "Exception while placing  Void transaction", PZTechnicalExceptionEnum.UNSUPPORTED_ENCOADING_EXCEPTION, null, ue.getMessage(), ue.getCause());

        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----Enter's into processInquiry-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();
        try{
            /*SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());*/
            System.setProperty("https.protocols", "TLSv1.2");

            CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String gatewayType=gatewayAccount.getGateway();
            Gson gson= new Gson();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            String username=gatewayAccount.getMerchantId();
            String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest= gatewayAccount.isTest();

            String amount=commTransactionDetailsVO.getAmount();
            String currency=commTransactionDetailsVO.getCurrency();

            String transactionId="";
            String orderId=commTransactionDetailsVO.getOrderId();
            String dbStatus=commTransactionDetailsVO.getPrevTransactionStatus();

            RequestAuthenticationType requestAuthenticationType= new RequestAuthenticationType();
            requestAuthenticationType.setUserName(username);
            requestAuthenticationType.setPassword(password);

            BasicHttpBinding_IPSPSoapStub stub = new BasicHttpBinding_IPSPSoapStub();
            PSPSoapAppLocator pspSoapAppLocator= new PSPSoapAppLocator();
            String Test_Url="";
            String Live_Url="";

            transactionLogger.error("GatewayType-----" + gatewayType);

            if("trx".equalsIgnoreCase(gatewayType)){
                Test_Url=RB.getString("TEST_URL_TRX");
                Live_Url=RB.getString("LIVE_URL_TRX");

            }else {
                Test_Url=RB.getString("TEST_URL");
                Live_Url=RB.getString("LIVE_URL");
            }


            if(isTest){
                transactionLogger.error("inside isTest-----" +Test_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Test_Url);
            }else
            {
                transactionLogger.error("inside Live-----"+Live_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Live_Url);
            }
            stub=(BasicHttpBinding_IPSPSoapStub)pspSoapAppLocator.getBasicHttpBinding_IPSPSoap();
            assertNotNull("binding is null", stub);

            Response response=null;

            if(dbStatus.equalsIgnoreCase("authstarted") || dbStatus.equalsIgnoreCase("authfailed")){
                transactionId=orderId;
                transactionLogger.debug("----inside--- "+dbStatus+"----"+transactionId);
                response = stub.checkTransaction(requestAuthenticationType,transactionId);
            }
            else if( dbStatus.equalsIgnoreCase("authsuccessful")){
                commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
                transactionId=orderId;
                transactionLogger.debug("----inside--- "+dbStatus+"----"+transactionId);
                response = stub.checkTransaction(requestAuthenticationType,transactionId);
            }
            else if(dbStatus.equalsIgnoreCase("capturestarted") || dbStatus.equalsIgnoreCase("capturesuccess") || dbStatus.equalsIgnoreCase("capturefailed") )
            {
                transactionLogger.debug("----inside--- " + dbStatus + "----" + transactionId);
                if (dbStatus.equalsIgnoreCase("capturesuccess")){

                    commResponseVO.setTransactionType(PZProcessType.SALE.toString());
                    transactionId=orderId;
                    transactionLogger.debug("----inside--- "+dbStatus+"----"+transactionId);
                    response = stub.checkTransaction(requestAuthenticationType,transactionId);
                    if(response==null){
                        commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                        transactionId=orderId+"_C";
                        transactionLogger.debug("----inside--- "+dbStatus+"----"+transactionId);
                        response = stub.checkTransaction(requestAuthenticationType,transactionId);
                    }
                }else{
                    commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());
                    transactionId=orderId+"_C";
                    transactionLogger.debug("----inside--- "+dbStatus+"----"+transactionId);
                    response = stub.checkTransaction(requestAuthenticationType,transactionId);
                }

            }else if(dbStatus.equalsIgnoreCase("markedforreversal") || dbStatus.equalsIgnoreCase("reversed")){

                commResponseVO.setTransactionType(PZProcessType.REFUND.toString());
                transactionId = orderId + "_R";
                transactionLogger.debug("----inside--- "+dbStatus+"----"+transactionId);
                response = stub.checkTransaction(requestAuthenticationType,transactionId);
            }else if(dbStatus.equalsIgnoreCase("cancelstarted") || dbStatus.equalsIgnoreCase("authcancelled")){
                commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());
                transactionId=orderId+"_V";
                transactionLogger.debug("----inside--- "+dbStatus+"----"+transactionId);
                response = stub.checkTransaction(requestAuthenticationType,transactionId);
            }else if(dbStatus.equalsIgnoreCase("payoutstarted") || dbStatus.equalsIgnoreCase("payoutsuccessful") ||dbStatus.equalsIgnoreCase("payoutfailed") ){
                commResponseVO.setTransactionType(PZProcessType.PAYOUT.toString());
                transactionId=orderId;
                transactionLogger.debug("----inside--- "+dbStatus+"----"+transactionId);
                response = stub.checkTransaction(requestAuthenticationType,transactionId);
            }
            if(response!=null){

                String errorMesaage="";
                String erroCode="";
                String inquiryResponse= gson.toJson(response);
                transactionLogger.error("inquiry Response-----"+inquiryResponse);

                if(response.getFraudScreening()!=null)
                {
                    String[] errorMessage = response.getFraudScreening().getBlockReason();
                    if (errorMessage.length != 0)
                    {
                        for (String s : errorMessage)
                        {
                            if (s.equalsIgnoreCase("None"))
                            {
                                errorMesaage = response.getResult().getCode().getValue();
                            }
                            else
                            {
                                errorMesaage = s;
                            }
                        }
                    }
                    erroCode= String.valueOf(response.getFraudScreening().getIntBlockReason());
                }else {
                    errorMesaage = response.getResult().getCode().getValue();
                }

                Calendar c= response.getResult().getTimeStamp();
                Timestamp timestamp= new Timestamp(c.getTimeInMillis());

                if("Approved".equalsIgnoreCase(response.getResult().getCode().getValue())){
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                }
                if(functions.isValueNull(response.getReferences().getARN())){
                    commResponseVO.setArn(response.getReferences().getARN());
                }
                commResponseVO.setMerchantId(username);
                commResponseVO.setRemark(response.getResult().getMessage());
                commResponseVO.setDescription(errorMesaage);
                commResponseVO.setErrorCode(String.valueOf((response.getFraudScreening().getIntBlockReason())));
                commResponseVO.setTransactionId(response.getTransaction().getId());
                commResponseVO.setTransactionStatus(response.getResult().getCode().getValue());
                commResponseVO.setBankTransactionDate(String.valueOf(dateFormat.format(timestamp)));
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
                commResponseVO.setAmount(amount);
                commResponseVO.setCurrency(currency);
                commResponseVO.setAuthCode("-");

            }else {
                commResponseVO.setMerchantId(username);
                commResponseVO.setRemark("Transaction not found");
                commResponseVO.setDescription("Transaction not found");
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(date)));
            }

        }catch (RemoteException e){
            transactionLogger.error("RemoteException:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processInquiry()", null, "common", "Exception while placing  Inquiry transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }catch (ServiceException se){
            transactionLogger.error("ServiceException:::::",se);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "processInquiry()", null, "common", "Exception while placing  Inquiry transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());

        }
        return commResponseVO;
    }

    public GenericResponseVO process3DConfirmation(GenericRequestVO requestVO,String PaRes,String Md) throws PZTechnicalViolationException,  PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----Enter's into process3DConfirmation-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        CommResponseVO commResponseVO=new CommResponseVO();
        try{
            /*SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());*/
            System.setProperty("https.protocols", "TLSv1.2");

            CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
            CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String gatewayType=gatewayAccount.getGateway();

            Gson gson= new Gson();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            String username=gatewayAccount.getMerchantId();
            String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest= gatewayAccount.isTest();
            org.apache.axis.types.UnsignedInt amount=new org.apache.axis.types.UnsignedInt();
            amount.setValue(Long.parseLong(getCentAmount(commTransactionDetailsVO.getAmount())));

            RequestAuthenticationType requestAuthenticationType= new RequestAuthenticationType();
            requestAuthenticationType.setUserName(username);
            requestAuthenticationType.setPassword(password);

            RequestThreeDSType requestThreeDSType= new RequestThreeDSType();
            requestThreeDSType.setPARes(Base64.decodeBase64(PaRes.getBytes()));

            RequestLinkedType requestLinkedType= new RequestLinkedType();
            requestLinkedType.setReferenceId(Md);

            Request request= new Request();
            request.setAuthentication(requestAuthenticationType);
            request.setThreeDS(requestThreeDSType);
            request.setLinked(requestLinkedType);


            String captureRequest=gson.toJson(request);
            transactionLogger.error("process3DConfirmation Request------"+captureRequest);

            BasicHttpBinding_IPSPSoapStub stub = new BasicHttpBinding_IPSPSoapStub();
            PSPSoapAppLocator pspSoapAppLocator= new PSPSoapAppLocator();

            String Test_Url="";
            String Live_Url="";

            transactionLogger.error("GatewayType-----" + gatewayType);

            if("trx".equalsIgnoreCase(gatewayType)){
                Test_Url=RB.getString("TEST_URL_TRX");
                Live_Url=RB.getString("LIVE_URL_TRX");

            }else {
                Test_Url=RB.getString("TEST_URL");
                Live_Url=RB.getString("LIVE_URL");
            }
            String currency="";
            String tmpl_amount="";
            String tmpl_currency="";
            if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
            {
                currency=commTransactionDetailsVO.getCurrency();
            }
            if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
            {
                tmpl_amount=commAddressDetailsVO.getTmpl_amount();
            }
            if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
            {
                tmpl_currency=commAddressDetailsVO.getTmpl_currency();
            }



            if(isTest){
                transactionLogger.error("inside isTest process3DConfirmation-----" + Test_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Test_Url);
            }else
            {
                transactionLogger.error("inside Live process3DConfirmation-----"+Live_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Live_Url);
            }
            stub=(BasicHttpBinding_IPSPSoapStub)pspSoapAppLocator.getBasicHttpBinding_IPSPSoap();
            assertNotNull("binding is null", stub);

            Response response=null;
            response = stub.process(RequestType.ThreeDSFinalisation, request);
            if(response!=null){

                String errorMesaage="";
                String erroCode="";
                String remark = "";
                String captureResponse= gson.toJson(response);
                transactionLogger.error("process3DConfirmation Response for---"+commTransactionDetailsVO.getPreviousTransactionId());
                transactionLogger.error("process3DConfirmation Response-----"+captureResponse);
                transactionLogger.error("process3DConfirmation Response Code-----"+response.getResult().getHostCode());

                if(response.getFraudScreening()!=null)
                {
                    String[] errorMessage = response.getFraudScreening().getBlockReason();
                    if (errorMessage.length != 0)
                    {
                        for (String s : errorMessage)
                        {
                            if (s.equalsIgnoreCase("None"))
                            {
                                errorMesaage = response.getResult().getCode().getValue();
                            }
                            else
                            {
                                errorMesaage = s;
                            }
                        }
                    }

                    //erroCode= String.valueOf(response.getFraudScreening().getIntBlockReason());
                }
                else
                {
                    errorMesaage = response.getResult().getCode().getValue();
                }

                erroCode = response.getResult().getHostCode();

                if(functions.isValueNull(erroCode))
                {
                    if(functions.isValueNull(TransactiumErrorCode.getDescription(erroCode)))
                    {
                        remark = TransactiumErrorCode.getDescription(erroCode);
                    }
                }

                Calendar c= response.getResult().getTimeStamp();
                Timestamp timestamp= new Timestamp(c.getTimeInMillis());

                if("Approved".equalsIgnoreCase(response.getResult().getCode().getValue())){
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                }
                if(functions.isValueNull(response.getReferences().getARN())){
                    commResponseVO.setArn(response.getReferences().getARN());
                }
                commResponseVO.setRemark(errorMesaage+" "+remark);
                commResponseVO.setDescription(errorMesaage +" "+remark);
                commResponseVO.setErrorCode(String.valueOf((response.getResult().getHostCode())));
                commResponseVO.setTransactionId(response.getTransaction().getId());
                commResponseVO.setTransactionStatus(response.getResult().getCode().getValue());
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(timestamp)));
                commResponseVO.setEci(String.valueOf(response.getThreeDS().getECI()));
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
            }

        }catch (RemoteException e){
            transactionLogger.error("RemoteException:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "process3DConfirmation()", null, "common", "Exception while placing  process3DConfirmation transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }catch (ServiceException se)
        {
            transactionLogger.error("ServiceException:::::", se);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "process3DConfirmation()", null, "common", "Exception while placing  process3DConfirmation transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCommon3DSaleConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----Enter's into process3DConfirmation-----");
        Functions functions= new Functions();
        Comm3DRequestVO commRequestVO=(Comm3DRequestVO)requestVO;
        Comm3DResponseVO commResponseVO=new Comm3DResponseVO();
        try{
            /*SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());*/
            System.setProperty("https.protocols", "TLSv1.2");

            String paRes=commRequestVO.getPaRes();
            String md=commRequestVO.getMd();

            CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
            CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String gatewayType=gatewayAccount.getGateway();

            Gson gson= new Gson();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            String username=gatewayAccount.getMerchantId();
            String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest= gatewayAccount.isTest();
            org.apache.axis.types.UnsignedInt amount=new org.apache.axis.types.UnsignedInt();
            amount.setValue(Long.parseLong(getCentAmount(commTransactionDetailsVO.getAmount())));

            RequestAuthenticationType requestAuthenticationType= new RequestAuthenticationType();
            requestAuthenticationType.setUserName(username);
            requestAuthenticationType.setPassword(password);

            RequestThreeDSType requestThreeDSType= new RequestThreeDSType();
            requestThreeDSType.setPARes(Base64.decodeBase64(paRes.getBytes()));

            RequestLinkedType requestLinkedType= new RequestLinkedType();
            requestLinkedType.setReferenceId(md);

            Request request= new Request();
            request.setAuthentication(requestAuthenticationType);
            request.setThreeDS(requestThreeDSType);
            request.setLinked(requestLinkedType);


            String captureRequest=gson.toJson(request);
            transactionLogger.error("process3DConfirmation Request------"+captureRequest);

            BasicHttpBinding_IPSPSoapStub stub = new BasicHttpBinding_IPSPSoapStub();
            PSPSoapAppLocator pspSoapAppLocator= new PSPSoapAppLocator();

            String Test_Url="";
            String Live_Url="";

            transactionLogger.error("GatewayType-----" + gatewayType);

            if("trx".equalsIgnoreCase(gatewayType)){
                Test_Url=RB.getString("TEST_URL_TRX");
                Live_Url=RB.getString("LIVE_URL_TRX");

            }else {
                Test_Url=RB.getString("TEST_URL");
                Live_Url=RB.getString("LIVE_URL");
            }
            String currency="";
            String tmpl_amount="";
            String tmpl_currency="";
            if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
            {
                currency=commTransactionDetailsVO.getCurrency();
            }
            if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
            {
                tmpl_amount=commAddressDetailsVO.getTmpl_amount();
            }
            if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
            {
                tmpl_currency=commAddressDetailsVO.getTmpl_currency();
            }



            if(isTest){
                transactionLogger.error("inside isTest process3DConfirmation-----" + Test_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Test_Url);
            }else
            {
                transactionLogger.error("inside Live process3DConfirmation-----"+Live_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Live_Url);
            }
            stub=(BasicHttpBinding_IPSPSoapStub)pspSoapAppLocator.getBasicHttpBinding_IPSPSoap();
            assertNotNull("binding is null", stub);

            Response response=null;
            response = stub.process(RequestType.ThreeDSFinalisation, request);
            if(response!=null){

                String errorMesaage="";
                String erroCode="";
                String remark = "";
                String captureResponse= gson.toJson(response);
                transactionLogger.error("process3DConfirmation Response for---"+commTransactionDetailsVO.getPreviousTransactionId());
                transactionLogger.error("process3DConfirmation Response-----"+captureResponse);
                transactionLogger.error("process3DConfirmation Response Code-----"+response.getResult().getHostCode());

                if(response.getFraudScreening()!=null)
                {
                    String[] errorMessage = response.getFraudScreening().getBlockReason();
                    if (errorMessage.length != 0)
                    {
                        for (String s : errorMessage)
                        {
                            if (s.equalsIgnoreCase("None"))
                            {
                                errorMesaage = response.getResult().getCode().getValue();
                            }
                            else
                            {
                                errorMesaage = s;
                            }
                        }
                    }

                    //erroCode= String.valueOf(response.getFraudScreening().getIntBlockReason());
                }
                else
                {
                    errorMesaage = response.getResult().getCode().getValue();
                }

                erroCode = response.getResult().getHostCode();

                if(functions.isValueNull(erroCode))
                {
                    if(functions.isValueNull(TransactiumErrorCode.getDescription(erroCode)))
                    {
                        remark = TransactiumErrorCode.getDescription(erroCode);
                    }
                }

                Calendar c= response.getResult().getTimeStamp();
                Timestamp timestamp= new Timestamp(c.getTimeInMillis());

                if("Approved".equalsIgnoreCase(response.getResult().getCode().getValue())){
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                }
                if(functions.isValueNull(response.getReferences().getARN())){
                    commResponseVO.setArn(response.getReferences().getARN());
                }
                commResponseVO.setRemark(errorMesaage+" "+remark);
                commResponseVO.setDescription(errorMesaage +" "+remark);
                commResponseVO.setErrorCode(String.valueOf((response.getResult().getHostCode())));
                commResponseVO.setTransactionId(response.getTransaction().getId());
                commResponseVO.setTransactionStatus(response.getResult().getCode().getValue());
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(timestamp)));
                commResponseVO.setEci(String.valueOf(response.getThreeDS().getECI()));
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
            }

        }catch (RemoteException e){
            transactionLogger.error("RemoteException:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "process3DConfirmation()", null, "common", "Exception while placing  process3DConfirmation transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }catch (ServiceException se)
        {
            transactionLogger.error("ServiceException:::::", se);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "process3DConfirmation()", null, "common", "Exception while placing  process3DConfirmation transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCommon3DAuthConfirmation(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException, UnsupportedEncodingException
    {
        transactionLogger.error("-----Enter's into process3DConfirmation-----");
        Functions functions= new Functions();
        Comm3DRequestVO commRequestVO=(Comm3DRequestVO)requestVO;
        Comm3DResponseVO commResponseVO=new Comm3DResponseVO();
        try{
            /*SSLUtils.setupSecurityProvider();
            SSLUtils.trustAllHttpsCertificates();
            System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
            java.security.Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());*/
            System.setProperty("https.protocols", "TLSv1.2");

            String paRes=commRequestVO.getPaRes();
            String md=commRequestVO.getMd();

            CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
            CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
            GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
            String gatewayType=gatewayAccount.getGateway();

            Gson gson= new Gson();
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

            String username=gatewayAccount.getMerchantId();
            String password=gatewayAccount.getFRAUD_FTP_PASSWORD();
            boolean isTest= gatewayAccount.isTest();
            org.apache.axis.types.UnsignedInt amount=new org.apache.axis.types.UnsignedInt();
            amount.setValue(Long.parseLong(getCentAmount(commTransactionDetailsVO.getAmount())));

            RequestAuthenticationType requestAuthenticationType= new RequestAuthenticationType();
            requestAuthenticationType.setUserName(username);
            requestAuthenticationType.setPassword(password);

            RequestThreeDSType requestThreeDSType= new RequestThreeDSType();
            requestThreeDSType.setPARes(Base64.decodeBase64(paRes.getBytes()));

            RequestLinkedType requestLinkedType= new RequestLinkedType();
            requestLinkedType.setReferenceId(md);

            Request request= new Request();
            request.setAuthentication(requestAuthenticationType);
            request.setThreeDS(requestThreeDSType);
            request.setLinked(requestLinkedType);


            String captureRequest=gson.toJson(request);
            transactionLogger.error("process3DConfirmation Request------"+captureRequest);

            BasicHttpBinding_IPSPSoapStub stub = new BasicHttpBinding_IPSPSoapStub();
            PSPSoapAppLocator pspSoapAppLocator= new PSPSoapAppLocator();

            String Test_Url="";
            String Live_Url="";

            transactionLogger.error("GatewayType-----" + gatewayType);

            if("trx".equalsIgnoreCase(gatewayType)){
                Test_Url=RB.getString("TEST_URL_TRX");
                Live_Url=RB.getString("LIVE_URL_TRX");

            }else {
                Test_Url=RB.getString("TEST_URL");
                Live_Url=RB.getString("LIVE_URL");
            }
            String currency="";
            String tmpl_amount="";
            String tmpl_currency="";
            if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
            {
                currency=commTransactionDetailsVO.getCurrency();
            }
            if (functions.isValueNull(commAddressDetailsVO.getTmpl_amount()))
            {
                tmpl_amount=commAddressDetailsVO.getTmpl_amount();
            }
            if (functions.isValueNull(commAddressDetailsVO.getTmpl_currency()))
            {
                tmpl_currency=commAddressDetailsVO.getTmpl_currency();
            }



            if(isTest){
                transactionLogger.error("inside isTest process3DConfirmation-----" + Test_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Test_Url);
            }else
            {
                transactionLogger.error("inside Live process3DConfirmation-----"+Live_Url);
                pspSoapAppLocator.setBasicHttpBinding_IPSPSoapEndpointAddress(Live_Url);
            }
            stub=(BasicHttpBinding_IPSPSoapStub)pspSoapAppLocator.getBasicHttpBinding_IPSPSoap();
            assertNotNull("binding is null", stub);

            Response response=null;
            response = stub.process(RequestType.ThreeDSFinalisation, request);
            if(response!=null){

                String errorMesaage="";
                String erroCode="";
                String remark = "";
                String captureResponse= gson.toJson(response);
                transactionLogger.error("process3DConfirmation Response for---"+commTransactionDetailsVO.getPreviousTransactionId());
                transactionLogger.error("process3DConfirmation Response-----"+captureResponse);
                transactionLogger.error("process3DConfirmation Response Code-----"+response.getResult().getHostCode());

                if(response.getFraudScreening()!=null)
                {
                    String[] errorMessage = response.getFraudScreening().getBlockReason();
                    if (errorMessage.length != 0)
                    {
                        for (String s : errorMessage)
                        {
                            if (s.equalsIgnoreCase("None"))
                            {
                                errorMesaage = response.getResult().getCode().getValue();
                            }
                            else
                            {
                                errorMesaage = s;
                            }
                        }
                    }

                    //erroCode= String.valueOf(response.getFraudScreening().getIntBlockReason());
                }
                else
                {
                    errorMesaage = response.getResult().getCode().getValue();
                }

                erroCode = response.getResult().getHostCode();

                if(functions.isValueNull(erroCode))
                {
                    if(functions.isValueNull(TransactiumErrorCode.getDescription(erroCode)))
                    {
                        remark = TransactiumErrorCode.getDescription(erroCode);
                    }
                }

                Calendar c= response.getResult().getTimeStamp();
                Timestamp timestamp= new Timestamp(c.getTimeInMillis());

                if("Approved".equalsIgnoreCase(response.getResult().getCode().getValue())){
                    commResponseVO.setStatus("success");
                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                }
                if(functions.isValueNull(response.getReferences().getARN())){
                    commResponseVO.setArn(response.getReferences().getARN());
                }
                commResponseVO.setRemark(errorMesaage+" "+remark);
                commResponseVO.setDescription(errorMesaage +" "+remark);
                commResponseVO.setErrorCode(String.valueOf((response.getResult().getHostCode())));
                commResponseVO.setTransactionId(response.getTransaction().getId());
                commResponseVO.setTransactionStatus(response.getResult().getCode().getValue());
                commResponseVO.setResponseTime(String.valueOf(dateFormat.format(timestamp)));
                commResponseVO.setEci(String.valueOf(response.getThreeDS().getECI()));
                commResponseVO.setCurrency(currency);
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
            }

        }catch (RemoteException e){
            transactionLogger.error("RemoteException:::::",e);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "process3DConfirmation()", null, "common", "Exception while placing  process3DConfirmation transaction", PZTechnicalExceptionEnum.REMOTE_EXCEPTION, null, e.getMessage(), e.getCause());
        }catch (ServiceException se)
        {
            transactionLogger.error("ServiceException:::::", se);
            PZExceptionHandler.raiseTechnicalViolationException(TransactiumPaymentGateway.class.getName(), "process3DConfirmation()", null, "common", "Exception while placing  process3DConfirmation transaction", PZTechnicalExceptionEnum.SERVICE_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        return commResponseVO;
    }

    public String removeLeadingTrailingSpace(String input)
    {
        System.out.println("input1-----"+input);
        if (functions.isValueNull(input))
        {
            input.trim();
        }
        System.out.println("input2-----"+input);
        return input;
    }
}

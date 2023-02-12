package com.payment.nestpay;

import com.directi.pg.Base64;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.iMerchantPay.iMerchantPayUtils;
import org.apache.commons.lang.RandomStringUtils;
import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by Admin on 7/4/2018.
 */
public class NestPayPaymentGateway extends AbstractPaymentGateway
{
    private static  TransactionLogger transactionLogger= new TransactionLogger(NestPayPaymentGateway.class.getName());
    private  static ResourceBundle RB= LoadProperties.getProperty("com.directi.pg.nestpay");
    public static  final String GATEWAY_TYPE="nestpay";

    public NestPayPaymentGateway(String accountId){
        this.accountId=accountId;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }

    public static void main(String[] args)
    {
        try
        {
          //  CommResponseVO commResponseVO= new CommResponseVO();
            String xmlRequest = "<CC5Request>\n" +
                    "    <Name>CREDITAPI</Name>\n" +
                    "    <Password>CREDIT13</Password>\n" +
                    "    <ClientId>130000012</ClientId>\n" +
                    "    <OrderId>454545</OrderId>\n" +
                    "    <Type>PreAuth</Type>\n" +
                    "    <Instalment>5</Instalment>\n" +
                    "    <IPAddress>192.168.0.14</IPAddress>\n" +
                    "    <Total>10.00</Total>\n" +
                    "    <Currency>946</Currency>\n" +
                    "    <Number>4848480011223288</Number>\n" +
                    "    <Expires>12/2018</Expires>\n" +
                    "    <Cvv2Val>000</Cvv2Val>\n" +
                    "    <BillTo>\n" +
                    "        <Name>Uday Raj</Name>\n" +
                    "        <Street1>malad</Street1>\n" +
                    "        <City>Mumbai</City>\n" +
                    "        <StateProv>MH</StateProv>\n" +
                    "        <PostalCode>400067</PostalCode>\n" +
                    "        <Country>IN</Country>\n" +
                    "    </BillTo>\n" +
                    "    <OrderItemList>\n" +
                    "       <OrderItem>\n" +
                    "           <Desc>Udyavhscbhc</Desc>\n" +
                    "       </OrderItem>\n" +
                    "    </OrderItemList>\n" +
                    "</CC5Request>";

//            System.out.println("xmlRequest-----" + xmlRequest);
            //System.out.println("Request-----"+gson.toJson(myjpay));

            String xmlResponse = NestPayUtils.doPostHTTPSURLConnectionClient("https://entegrasyon.asseco-see.com.tr/fim/api", xmlRequest);

  //          System.out.println("xmLResponse------" + xmlResponse);


          /*  String response="<?xml version=\"1.0\" encoding=\"ISO-8859-9\"?>\n" +
                    "<CC5Response>\n" +
                    "  <OrderId>77865</OrderId>\n" +
                    "  <GroupId>77865</GroupId>\n" +
                    "  <Response>Approved</Response>\n" +
                    "  <AuthCode>241753</AuthCode>\n" +
                    "  <HostRefNum>818515000968</HostRefNum>\n" +
                    "  <ProcReturnCode>00</ProcReturnCode>\n" +
                    "  <TransId>18185PLtB13065</TransId>\n" +
                    "  <ErrMsg>Hello</ErrMsg>\n" +
                    "  <Extra>\n" +
                    "    <SETTLEID>850</SETTLEID>\n" +
                    "    <TRXDATE>20180704 15:11:44</TRXDATE>\n" +
                    "    <ERRORCODE>435</ERRORCODE>\n" +
                    "    <NUMBEROFINSTALLMENTS1>12</NUMBEROFINSTALLMENTS1>\n" +
                    "    <CARDISSUER>T. IS BANKASI A.S.</CARDISSUER>\n" +
                    "    <ADVICEDINSTALLMENTTYPE>SINGLE</ADVICEDINSTALLMENTTYPE>\n" +
                    "    <DIGERTAKSITTUTARI1>000000020000</DIGERTAKSITTUTARI1>\n" +
                    "    <INTERESTRATE2>00022</INTERESTRATE2>\n" +
                    "    <DIGERTAKSITTUTARI2>000000055555</DIGERTAKSITTUTARI2>\n" +
                    "    <TOTALAMOUNTDUE1>000000200000</TOTALAMOUNTDUE1>\n" +
                    "    <INTERESTRATE1>00011</INTERESTRATE1>\n" +
                    "    <TOTALAMOUNTDUE2>000000066666</TOTALAMOUNTDUE2>\n" +
                    "    <NUMBEROFINSTALLMENTS2>13</NUMBEROFINSTALLMENTS2>\n" +
                    "    <ANNUALPERCENTAGE1>00020</ANNUALPERCENTAGE1>\n" +
                    "    <ANNUALPERCENTAGE2>00055</ANNUALPERCENTAGE2>\n" +
                    "    <INSTALLMENTTYPE>20</INSTALLMENTTYPE>\n" +
                    "    <INSTALLMENTOPTION>B</INSTALLMENTOPTION>\n" +
                    "    <CARDBRAND>VISA</CARDBRAND>\n" +
                    "    <NUMBEROFINSTALLMENTOPTIONS>2</NUMBEROFINSTALLMENTOPTIONS>\n" +
                    "    <ADVICEINSTALLMENTEXIST>true</ADVICEINSTALLMENTEXIST>\n" +
                    "    <INSTALLMENTFEE2>000000000033</INSTALLMENTFEE2>\n" +
                    "    <INSTALLMENTFEE1>000000020000</INSTALLMENTFEE1>\n" +
                    "    <NUMCODE>00</NUMCODE>\n" +
                    "    <ILKTAKSITTUTARI2>000000022222</ILKTAKSITTUTARI2>\n" +
                    "    <ILKTAKSITTUTARI1>000000020000</ILKTAKSITTUTARI1>\n" +
                    "  </Extra>\n" +
                    "</CC5Response>";
*/
            //commResponseVO = NestPayUtils.readSoapResponse(response);



          // String plaintext=NestPayUtils.SHA1("130000012","98747","10.00","http://localhost:8081/transaction/NestPayFrontEndServlet"," http://localhost:8081/transaction/NestPayFrontEndServlet","Auth","POIIYYH","SKEY12345");

            //System.out.println("plaintext-----"+plaintext);

            /*String saleRequest="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<CC5Request>\n" +
                    "    <Name>CREDITAPI</Name>\n" +
                    "    <Password>CREDIT13</Password>\n" +
                    "    <clientid>130000012</clientid>\n" +
                    "    <IPAddress>115.96.19.10</IPAddress>\n" +
                    "    <oid>74148</oid>\n" +
                    "    <Type>Auth</Type>\n" +
                    "    <Number>484848:3853896F398423B4CC7D401944DF09F69EF89F97E42A2A1C54AA63232F686020:3478:##130000012</Number>\n" +
                    "    <amount>10.00</amount>\n" +
                    "    <currency>946</currency>\n" +
                    "    <PayerTxnId>A2FQl/IjLbG4GtQUU45mqdGN4kI=</PayerTxnId>\n" +
                    "    <PayerSecurityLevel>05</PayerSecurityLevel>\n" +
                    "    <PayerAuthenticationCode>AAABBygIEAAAAAADQQgQAAAAAAA=</PayerAuthenticationCode>\n" +
                    "</CC5Request>";

            String req="<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
                    "<CC5Request> \n" +
                    "  <OrderId>57874</OrderId> \n" +
                    "  <Total>10.00</Total> \n" +
                    "  <Currency>946</Currency> \n" +
                    "  <PayerSecurityLevel>05</PayerSecurityLevel> \n" +
                    "  <PayerTxnId>8PB2y/8duPl0cdwPS927dCbOKbw=</PayerTxnId> \n" +
                    "  <PayerAuthenticationCode>AAABBIYWMAAAAAADQRYwAAAAAAA=</PayerAuthenticationCode>\n" +
                    "  <IPAddress>115.96.19.10</IPAddress> \n" +
                    "  <Number>484848:D4543E18C9C74702416DE745B77C18D54A8325BE47B9EB576CFEBBA32D875E32:3919:##130000012</Number> \n" +
                    "  <Type>Auth</Type> \n" +
                    "  <Name>CREDITAPI</Name> \n" +
                    "  <Password>CREDIT13</Password> \n" +
                    "  <ClientId>130000012</ClientId>\n" +
                    "</CC5Request>\n";


            String xmlResponse = NestPayUtils.doPostHTTPSURLConnectionClient("https://entegrasyon.asseco-see.com.tr/fim/api", req);

           System.out.println("xmlResponse-----"+xmlResponse);

*/
          //  String hash = new String(com.directi.pg.Base64.encode(plaintext.getBytes()));
          //  String hash =(new );

         //   System.out.println("hash------"+hash);




        }catch (Exception e){
            transactionLogger.error("Exception :::: ", e);
        }
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        NestPayResponseVO commResponseVO= new NestPayResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);

        String emiSupport="";
        String installment="";
        if("Y".equalsIgnoreCase(gatewayAccount.getEmiSupport())){
            if(functions.isValueNull(commTransactionDetailsVO.getEmiCount()))
            {
                installment = commTransactionDetailsVO.getEmiCount();
                emiSupport = "<Instalment>" + installment + "</Instalment>";
            }
        }

        transactionLogger.debug("emisupport Flag-----"+gatewayAccount.getEmiSupport());
        transactionLogger.debug("emisupport-----"+emiSupport);
        transactionLogger.debug("installment-----"+installment);
        transactionLogger.debug("CardholderipAddress-----"+commAddressDetailsVO.getCardHolderIpAddress());
        transactionLogger.debug("ipAddress-----"+commAddressDetailsVO.getIp());

        boolean isTest=gatewayAccount.isTest();
        String is3dSupported=gatewayAccount.get_3DSupportAccount();
        String clientid=gatewayAccount.getMerchantId();
        String name=gatewayAccount.getFRAUD_FTP_USERNAME();
        String password= gatewayAccount.getFRAUD_FTP_PASSWORD();
        String rnd=commTransactionDetailsVO.getOrderId();
        if(rnd.equals(trackingID)){
            rnd=commTransactionDetailsVO.getMerchantOrderId();
        }
        if(!functions.isValueNull(rnd)){
            rnd=commTransactionDetailsVO.getOrderDesc();
        }
        String storeKey=gatewayAccount.getFRAUD_FTP_PATH();
        String transType="Auth";  //here Auth means Sale

        transactionLogger.debug("rnd-----"+rnd);
        try{

            if("Y".equals(is3dSupported)){
                String url="";
                if(isTest){
                    url=RB.getString("3D_Test_Url");
                }else {
                    url=RB.getString("3D_Live_Url");
                }
                transactionLogger.debug("url----"+url);
                String hash=NestPayUtils.SHA1(clientid, trackingID, commTransactionDetailsVO.getAmount(), RB.getString("SuccessUrl"), RB.getString("FailUrl"), transType,installment, rnd, storeKey);
               // String html= NestPayUtils.generateAutoSubmitForm(trackingID, clientid, hash, transType,rnd, isTest, requestVO);

                transactionLogger.debug("language-----" + commAddressDetailsVO.getLanguage());
                Map fields = new HashMap();
                fields.put("clientid",clientid);
                fields.put("hash",hash);
                fields.put("transtype",transType);
                fields.put("amount",commTransactionDetailsVO.getAmount());
                fields.put("currency",CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency()));
                fields.put("oid",trackingID);
                fields.put("okUrl",RB.getString("SuccessUrl"));
                fields.put("failUrl",RB.getString("FailUrl"));
                fields.put("lang",commAddressDetailsVO.getLanguage());
                fields.put("rnd",rnd);
                fields.put("pan",commCardDetailsVO.getCardNum());
                fields.put("year",commCardDetailsVO.getExpYear());
                fields.put("month",commCardDetailsVO.getExpMonth());
                fields.put("cvv",commCardDetailsVO.getcVV());
                commResponseVO.setStatus("pending3DConfirmation");
                commResponseVO.setRequestMap(fields);
                commResponseVO.setUrlFor3DRedirect(url);

            }else {
                String saleRequest="<CC5Request>\n" +
                        "    <Name>"+name+"</Name>\n" +
                        "    <Password>"+password+"</Password>\n" +
                        "    <ClientId>"+clientid+"</ClientId>\n" +
                        "    <OrderId>"+trackingID+"</OrderId>\n" +
                        "    <Type>"+transType+"</Type>\n" + //here in this integration auth means is sale ,so please don't change .
                                emiSupport+
                        "    <IPAddress>"+commAddressDetailsVO.getCardHolderIpAddress()+"</IPAddress>\n" +
                        "    <Total>"+commTransactionDetailsVO.getAmount()+"</Total>\n" +
                        "    <Currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"</Currency>\n" +
                        "    <Number>"+commCardDetailsVO.getCardNum()+"</Number>\n" +
                        "    <Expires>"+commCardDetailsVO.getExpMonth()+"/"+commCardDetailsVO.getExpYear()+"</Expires>\n" +
                        "    <Cvv2Val>"+commCardDetailsVO.getcVV()+"</Cvv2Val>\n" +
                        "    <BillTo>\n" +
                        "        <Name>"+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"</Name>\n" +
                        "        <Street1>"+commAddressDetailsVO.getStreet()+"</Street1>\n" +
                        "        <City>"+commAddressDetailsVO.getCity()+"</City>\n" +
                        "        <StateProv>"+commAddressDetailsVO.getState()+"</StateProv>\n" +
                        "        <PostalCode>"+commAddressDetailsVO.getZipCode()+"</PostalCode>\n" +
                        "        <Country>"+commAddressDetailsVO.getCountry()+"</Country>\n" +
                        "    </BillTo>\n" +
                        "</CC5Request>";

                String saleRequestlog="<CC5Request>\n" +
                        "    <Name>"+name+"</Name>\n" +
                        "    <Password>"+password+"</Password>\n" +
                        "    <ClientId>"+clientid+"</ClientId>\n" +
                        "    <OrderId>"+trackingID+"</OrderId>\n" +
                        "    <Type>"+transType+"</Type>\n" + //here in this integration auth means is sale ,so please don't change .
                        emiSupport+
                        "    <IPAddress>"+commAddressDetailsVO.getCardHolderIpAddress()+"</IPAddress>\n" +
                        "    <Total>"+commTransactionDetailsVO.getAmount()+"</Total>\n" +
                        "    <Currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"</Currency>\n" +
                        "    <Number>"+functions.maskingPan(commCardDetailsVO.getCardNum())+"</Number>\n" +
                        "    <Expires>"+functions.maskingExpiry(commCardDetailsVO.getExpMonth()+"/"+commCardDetailsVO.getExpYear())+"</Expires>\n" +
                        "    <Cvv2Val>"+functions.maskingNumber(commCardDetailsVO.getcVV())+"</Cvv2Val>\n" +
                        "    <BillTo>\n" +
                        "        <Name>"+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"</Name>\n" +
                        "        <Street1>"+commAddressDetailsVO.getStreet()+"</Street1>\n" +
                        "        <City>"+commAddressDetailsVO.getCity()+"</City>\n" +
                        "        <StateProv>"+commAddressDetailsVO.getState()+"</StateProv>\n" +
                        "        <PostalCode>"+commAddressDetailsVO.getZipCode()+"</PostalCode>\n" +
                        "        <Country>"+commAddressDetailsVO.getCountry()+"</Country>\n" +
                        "    </BillTo>\n" +
                        "</CC5Request>";

                transactionLogger.error("saleRequest-----"+trackingID + "--" + saleRequestlog);

                String saleResponse="";
                if(isTest){
                    saleResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"),saleRequest);
                }else {
                    saleResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"),saleRequest);
                }
                transactionLogger.error("saleResponse-----"+trackingID + "--" + saleResponse);

                if(functions.isValueNull(saleResponse)){
                    commResponseVO = NestPayUtils.readSoapResponse(saleResponse,gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Internal Error");
                    commResponseVO.setDescription("Error");
                }
                commResponseVO.setTransactionType(PZProcessType.SALE.toString());
            }
        }catch(Exception e){
           transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        NestPayResponseVO commResponseVO= new NestPayResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();

        String is3dSupported=gatewayAccount.get_3DSupportAccount();
        String clientid=gatewayAccount.getMerchantId();
        String name=gatewayAccount.getFRAUD_FTP_USERNAME();
        String password= gatewayAccount.getFRAUD_FTP_PASSWORD();
        String rnd=commTransactionDetailsVO.getOrderId();
        if(rnd.equals(trackingID)){
            rnd=commTransactionDetailsVO.getMerchantOrderId();
        }
        if(!functions.isValueNull(rnd)){
            rnd=commTransactionDetailsVO.getOrderDesc();
        }
        String storeKey=gatewayAccount.getFRAUD_FTP_PATH();
        String transType="PreAuth";

        String emiSupport="";
        String installment="";
        if("Y".equalsIgnoreCase(gatewayAccount.getEmiSupport())){
            if(functions.isValueNull(commTransactionDetailsVO.getEmiCount()))
            {
                installment = commTransactionDetailsVO.getEmiCount();
                emiSupport = "<Instalment>" + installment + "</Instalment>";
            }
        }

        transactionLogger.debug("emisupport Flag-----"+gatewayAccount.getEmiSupport());
        transactionLogger.debug("emisupport-----"+emiSupport);
        transactionLogger.debug("installment-----"+installment);
        transactionLogger.debug("CardholderipAddress-----"+commAddressDetailsVO.getCardHolderIpAddress());
        transactionLogger.debug("ipAddress-----"+commAddressDetailsVO.getIp());


        try{
            transactionLogger.debug("clientid---"+clientid+"trackingID---"+trackingID+"amount--"+commTransactionDetailsVO.getAmount()+"---successurl--"+RB.getString("SuccessUrl")+"FailUrl---"+RB.getString("FailUrl")+"--transType--"+transType+"--rnd--"+rnd+"----storeKey---"+storeKey);
            if("Y".equals(is3dSupported)){
                String url="";
                if(isTest){
                    url=RB.getString("3D_Test_Url");
                }else {
                    url=RB.getString("3D_Live_Url");
                }
                transactionLogger.debug("url----"+url);
                String hash=NestPayUtils.SHA1(clientid, trackingID, commTransactionDetailsVO.getAmount(), RB.getString("SuccessUrl"), RB.getString("FailUrl"), transType,installment, rnd, storeKey);
             //   String html= NestPayUtils.generateAutoSubmitForm(trackingID, clientid, hash, transType,rnd, isTest, requestVO);
                transactionLogger.debug("language-----" + commAddressDetailsVO.getLanguage());
                Map fields = new HashMap();
                fields.put("clientid",clientid);
                fields.put("hash",hash);
                fields.put("transtype",transType);
                fields.put("amount",commTransactionDetailsVO.getAmount());
                fields.put("currency",CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency()));
                fields.put("oid",trackingID);
                fields.put("okUrl",RB.getString("SuccessUrl"));
                fields.put("failUrl",RB.getString("FailUrl"));
                fields.put("lang",commAddressDetailsVO.getLanguage());
                fields.put("rnd",rnd);
                fields.put("pan",commCardDetailsVO.getCardNum());
                fields.put("year",commCardDetailsVO.getExpYear());
                fields.put("month",commCardDetailsVO.getExpMonth());
                fields.put("cvv",commCardDetailsVO.getcVV());
                commResponseVO.setStatus("pending3DConfirmation");
                commResponseVO.setRequestMap(fields);
                commResponseVO.setUrlFor3DRedirect(url);
            }else {
                String authRequest="<CC5Request>\n" +
                        "    <Name>"+name+"</Name>\n" +
                        "    <Password>"+password+"</Password>\n" +
                        "    <ClientId>"+clientid+"</ClientId>\n" +
                        "    <OrderId>"+trackingID+"</OrderId>\n" +
                        "    <Type>"+transType+"</Type>\n" + //here in this integration auth means is sale ,so please don't change .
                             emiSupport+
                        "    <IPAddress>"+commAddressDetailsVO.getCardHolderIpAddress()+"</IPAddress>\n" +
                        "    <Total>"+commTransactionDetailsVO.getAmount()+"</Total>\n" +
                        "    <Currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"</Currency>\n" +
                        "    <Number>"+commCardDetailsVO.getCardNum()+"</Number>\n" +
                        "    <Expires>"+commCardDetailsVO.getExpMonth()+"/"+commCardDetailsVO.getExpYear()+"</Expires>\n" +
                        "    <Cvv2Val>"+commCardDetailsVO.getcVV()+"</Cvv2Val>\n" +
                        "    <BillTo>\n" +
                        "        <Name>"+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"</Name>\n" +
                        "        <Street1>"+commAddressDetailsVO.getStreet()+"</Street1>\n" +
                        "        <City>"+commAddressDetailsVO.getCity()+"</City>\n" +
                        "        <StateProv>"+commAddressDetailsVO.getState()+"</StateProv>\n" +
                        "        <PostalCode>"+commAddressDetailsVO.getZipCode()+"</PostalCode>\n" +
                        "        <Country>"+commAddressDetailsVO.getCountry()+"</Country>\n" +
                        "    </BillTo>\n" +
                        "</CC5Request>";
                String authRequestlog="<CC5Request>\n" +
                        "    <Name>"+name+"</Name>\n" +
                        "    <Password>"+password+"</Password>\n" +
                        "    <ClientId>"+clientid+"</ClientId>\n" +
                        "    <OrderId>"+trackingID+"</OrderId>\n" +
                        "    <Type>"+transType+"</Type>\n" + //here in this integration auth means is sale ,so please don't change .
                        emiSupport+
                        "    <IPAddress>"+commAddressDetailsVO.getCardHolderIpAddress()+"</IPAddress>\n" +
                        "    <Total>"+commTransactionDetailsVO.getAmount()+"</Total>\n" +
                        "    <Currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"</Currency>\n" +
                        "    <Number>"+functions.maskingPan(commCardDetailsVO.getCardNum())+"</Number>\n" +
                        "    <Expires>"+functions.maskingExpiry(commCardDetailsVO.getExpMonth()+"/"+commCardDetailsVO.getExpYear())+"</Expires>\n" +
                        "    <Cvv2Val>"+functions.maskingNumber(commCardDetailsVO.getcVV())+"</Cvv2Val>\n" +
                        "    <BillTo>\n" +
                        "        <Name>"+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"</Name>\n" +
                        "        <Street1>"+commAddressDetailsVO.getStreet()+"</Street1>\n" +
                        "        <City>"+commAddressDetailsVO.getCity()+"</City>\n" +
                        "        <StateProv>"+commAddressDetailsVO.getState()+"</StateProv>\n" +
                        "        <PostalCode>"+commAddressDetailsVO.getZipCode()+"</PostalCode>\n" +
                        "        <Country>"+commAddressDetailsVO.getCountry()+"</Country>\n" +
                        "    </BillTo>\n" +
                        "</CC5Request>";

                transactionLogger.error("authRequest-----"+trackingID + "--" + authRequestlog);

                String authResponse="";
                if(isTest){
                    authResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"),authRequest);
                }else {
                    authResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"),authRequest);
                }
                transactionLogger.error("authResponse-----"+trackingID + "--" + authResponse);

                if(functions.isValueNull(authResponse)){
                    commResponseVO = NestPayUtils.readSoapResponse(authResponse,gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Internal Error");
                    commResponseVO.setDescription("Error");
                }
                commResponseVO.setTransactionType(PZProcessType.AUTH.toString());
            }
        }catch(Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }


    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException, PZTechnicalViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processCapture-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        NestPayResponseVO commResponseVO= new NestPayResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        try{
                String captureRequest="<CC5Request> <Name>"+gatewayAccount.getFRAUD_FTP_USERNAME()+"</Name> <Password>"+gatewayAccount.getFRAUD_FTP_PASSWORD()+"</Password> <ClientId>"+gatewayAccount.getMerchantId()+"</ClientId> <Type>PostAuth</Type> <OrderId>"+commTransactionDetailsVO.getOrderId()+"</OrderId> </CC5Request>";

                transactionLogger.error("captureRequest-----"+trackingID + "--" + captureRequest);

                String captureResponse="";
                if(isTest){
                    captureResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"),captureRequest);
                }else {
                    captureResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"),captureRequest);
                }
                transactionLogger.error("captureResponse-----"+trackingID + "--" + captureResponse);

                if(functions.isValueNull(captureResponse)){
                    commResponseVO = NestPayUtils.readSoapResponse(captureResponse,gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Internal Error");
                    commResponseVO.setDescription("Error");
                }
                commResponseVO.setTransactionType(PZProcessType.CAPTURE.toString());

        }catch(Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }


    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processRefund-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        NestPayResponseVO commResponseVO= new NestPayResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        try{
            String refundRequest="<CC5Request> <Name>"+gatewayAccount.getFRAUD_FTP_USERNAME()+"</Name> <Password>"+gatewayAccount.getFRAUD_FTP_PASSWORD()+"</Password> <ClientId>"+gatewayAccount.getMerchantId()+"</ClientId> <Type>Credit</Type> <Total>"+commTransactionDetailsVO.getAmount()+"</Total> <OrderId>"+commTransactionDetailsVO.getOrderId()+"</OrderId> </CC5Request>";

            transactionLogger.error("refundRequest-----"+trackingID + "--" + refundRequest);

            String refundResponse="";
            if(isTest){
                refundResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"),refundRequest);
            }else {
                refundResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"),refundRequest);
            }
            transactionLogger.error("refundResponse-----"+trackingID + "--" + refundResponse);

            if(functions.isValueNull(refundResponse)){
                commResponseVO = NestPayUtils.readSoapResponse(refundResponse,gatewayAccount.getDisplayName());
            }else {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Internal Error");
                commResponseVO.setDescription("Error");
            }
            commResponseVO.setTransactionType(PZProcessType.REFUND.toString());

        }catch(Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }



    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException, PZDBViolationException
    {
        transactionLogger.error("-----inside processVoid-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        NestPayResponseVO commResponseVO= new NestPayResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        try{
            String voidRequest="<CC5Request> <Name>"+gatewayAccount.getFRAUD_FTP_USERNAME()+"</Name> <Password>"+gatewayAccount.getFRAUD_FTP_PASSWORD()+"</Password> <ClientId>"+gatewayAccount.getMerchantId()+"</ClientId> <Type>Void</Type> <OrderId>"+commTransactionDetailsVO.getOrderId()+"</OrderId> </CC5Request>";

            transactionLogger.error("voidRequest-----"+trackingID + "--" + voidRequest);

            String voidResponse="";
            if(isTest){
                voidResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"),voidRequest);
            }else {
                voidResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"),voidRequest);
            }
            transactionLogger.error("voidResponse-----"+trackingID + "--" + voidResponse);

            if(functions.isValueNull(voidResponse)){
                commResponseVO = NestPayUtils.readSoapResponse(voidResponse,gatewayAccount.getDisplayName());
            }else {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Internal Error");
                commResponseVO.setDescription("Error");
            }
            commResponseVO.setTransactionType(PZProcessType.CANCEL.toString());

        }catch(Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }


    @Override
    public GenericResponseVO processInquiry(GenericRequestVO requestVO) throws PZTechnicalViolationException, PZDBViolationException, PZConstraintViolationException
    {
        transactionLogger.error("-----inside processInquiry-----");
        Functions functions= new Functions();
        CommRequestVO commRequestVO=(CommRequestVO)requestVO;
        NestPayResponseVO commResponseVO= new NestPayResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        try{
            String inquiryRequest="<CC5Request> <Name>"+gatewayAccount.getFRAUD_FTP_USERNAME()+"</Name> <Password>"+gatewayAccount.getFRAUD_FTP_PASSWORD()+"</Password> <ClientId>"+gatewayAccount.getMerchantId()+"</ClientId> <OrderId>"+commTransactionDetailsVO.getOrderId()+"</OrderId> <Extra> <ORDERSTATUS>QUERY</ORDERSTATUS> </Extra> </CC5Request>";

            transactionLogger.error("inquiryRequest-----"+inquiryRequest);

            String inquiryResponse="";
            if(isTest){
                inquiryResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"),inquiryRequest);
            }else {
                inquiryResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"),inquiryRequest);
            }
            transactionLogger.error("inquiryResponse-----"+inquiryResponse);

            if(functions.isValueNull(inquiryResponse)){
                commResponseVO = NestPayUtils.readSoapResponse(inquiryResponse,gatewayAccount.getDisplayName());
            }else {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Internal Error");
                commResponseVO.setDescription("Error");
            }
            commResponseVO.setMerchantId(gatewayAccount.getMerchantId());
            commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
            commResponseVO.setCurrency(commTransactionDetailsVO.getCurrency());

        }catch(Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO process3DConfirmation(String trackingID, GenericRequestVO requestVO)
    {
        transactionLogger.error("-----inside process3DConfirmation-----");
        NestPayRequestVO commRequestVO=(NestPayRequestVO) requestVO;
        NestPayResponseVO commResponseVO= new NestPayResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        Functions functions= new Functions();
        String transType=commRequestVO.getTransType();
        String emiSupport="";
        String installment="";
        if("Y".equalsIgnoreCase(gatewayAccount.getEmiSupport())){
            if(functions.isValueNull(commTransactionDetailsVO.getEmiCount()))
            {
                installment = commTransactionDetailsVO.getEmiCount();
                emiSupport = "<Instalment>" + installment + "</Instalment>";
            }
        }

        transactionLogger.debug("emisupport Flag-----"+gatewayAccount.getEmiSupport());
        transactionLogger.debug("emisupport-----"+emiSupport);
        transactionLogger.debug("installment-----"+installment);
        transactionLogger.debug("CardholderipAddress-----"+commAddressDetailsVO.getCardHolderIpAddress());
        transactionLogger.debug("ipAddress-----"+commAddressDetailsVO.getIp());

        try{
            String final3DRequest="<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
                    "<CC5Request> \n" +
                    "  <OrderId>"+trackingID+"</OrderId> \n" +
                    "  <Total>"+commRequestVO.getTransDetailsVO().getAmount()+"</Total> \n" +
                    "  <Currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commRequestVO.getTransDetailsVO().getCurrency())+"</Currency> \n" +
                    "  <PayerSecurityLevel>"+commRequestVO.getEci()+"</PayerSecurityLevel> \n" +
                    "  <PayerTxnId>"+commRequestVO.getXid()+"</PayerTxnId> \n" +
                    "  <PayerAuthenticationCode>"+commRequestVO.getCavv()+"</PayerAuthenticationCode>\n" +
                    "  <Number>"+commRequestVO.getNumber()+"</Number> \n" +
                    "  <Type>"+transType+"</Type> \n" +
                         emiSupport+
                    "    <IPAddress>"+commAddressDetailsVO.getCardHolderIpAddress()+"</IPAddress>\n" +
                    "  <Name>"+gatewayAccount.getFRAUD_FTP_USERNAME()+"</Name> \n" +
                    "  <Password>"+gatewayAccount.getFRAUD_FTP_PASSWORD()+"</Password> \n" +
                    "  <ClientId>"+gatewayAccount.getMerchantId()+"</ClientId>\n" +
                    "</CC5Request>\n";
            String final3DRequestlog="<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n" +
                    "<CC5Request> \n" +
                    "  <OrderId>"+trackingID+"</OrderId> \n" +
                    "  <Total>"+commRequestVO.getTransDetailsVO().getAmount()+"</Total> \n" +
                    "  <Currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commRequestVO.getTransDetailsVO().getCurrency())+"</Currency> \n" +
                    "  <PayerSecurityLevel>"+commRequestVO.getEci()+"</PayerSecurityLevel> \n" +
                    "  <PayerTxnId>"+commRequestVO.getXid()+"</PayerTxnId> \n" +
                    "  <PayerAuthenticationCode>"+functions.maskingNumber(commRequestVO.getCavv())+"</PayerAuthenticationCode>\n" +
                    "  <Number>"+functions.maskingPan(commRequestVO.getNumber())+"</Number> \n" +
                    "  <Type>"+transType+"</Type> \n" +
                    emiSupport+
                    "    <IPAddress>"+commAddressDetailsVO.getCardHolderIpAddress()+"</IPAddress>\n" +
                    "  <Name>"+gatewayAccount.getFRAUD_FTP_USERNAME()+"</Name> \n" +
                    "  <Password>"+gatewayAccount.getFRAUD_FTP_PASSWORD()+"</Password> \n" +
                    "  <ClientId>"+gatewayAccount.getMerchantId()+"</ClientId>\n" +
                    "</CC5Request>\n";

            transactionLogger.error("final3DRequest-----"+trackingID + "--" + final3DRequestlog);

            String final3DResponse="";
            if(isTest){
                final3DResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"),final3DRequest);
            }else {
                final3DResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"),final3DRequest);
            }
            transactionLogger.error("final3DResponse-----"+trackingID + "--" + final3DResponse);

            if(functions.isValueNull(final3DResponse)){
                commResponseVO = NestPayUtils.readSoapResponse(final3DResponse,gatewayAccount.getDisplayName());
            }else {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Internal Error");
                commResponseVO.setDescription("Error");
            }

            if(transType.equals("PreAuth"))
            {
                commResponseVO.setTransactionType(PZProcessType.THREE_D_AUTH.toString());
            }else
            {
                commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
            }


        }catch(Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processSaleConfirmation(String trackingID, GenericRequestVO requestVO)
    {
        transactionLogger.error("-----inside processSaleConfirmation-----");
        Functions functions= new Functions();
        NestPayRequestVO commRequestVO=(NestPayRequestVO)requestVO;
        NestPayResponseVO commResponseVO= new NestPayResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String clientid=gatewayAccount.getMerchantId();
        String name=gatewayAccount.getFRAUD_FTP_USERNAME();
        String password= gatewayAccount.getFRAUD_FTP_PASSWORD();
        String transType="Auth";  //here Auth means Sale

        String emiSupport="";
        String installment="";
        if("Y".equalsIgnoreCase(gatewayAccount.getEmiSupport())){
            if(functions.isValueNull(commTransactionDetailsVO.getEmiCount()))
            {
                installment = commTransactionDetailsVO.getEmiCount();
                emiSupport = "<Instalment>" + installment + "</Instalment>";
            }
        }

        transactionLogger.debug("emisupport Flag-----"+gatewayAccount.getEmiSupport());
        transactionLogger.debug("emisupport-----"+emiSupport);
        transactionLogger.debug("installment-----"+installment);
        transactionLogger.debug("CardholderipAddress-----"+commAddressDetailsVO.getCardHolderIpAddress());
        transactionLogger.debug("ipAddress-----"+commAddressDetailsVO.getIp());

        try{
                String finalSaleRequest="<CC5Request>\n" +
                        "    <Name>"+name+"</Name>\n" +
                        "    <Password>"+password+"</Password>\n" +
                        "    <ClientId>"+clientid+"</ClientId>\n" +
                        "    <OrderId>"+trackingID+"</OrderId>\n" +
                        "    <Type>"+transType+"</Type>\n" + //here in this integration auth means is sale ,so please don't change .
                                emiSupport+
                        "    <IPAddress>"+commAddressDetailsVO.getCardHolderIpAddress()+"</IPAddress>\n" +
                        "    <Total>"+commTransactionDetailsVO.getAmount()+"</Total>\n" +
                        "    <Currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"</Currency>\n" +
                        "    <Number>"+commCardDetailsVO.getCardNum()+"</Number>\n" +
                        "    <Expires>"+commCardDetailsVO.getExpMonth()+"/"+commCardDetailsVO.getExpYear()+"</Expires>\n" +
                        "    <Cvv2Val>"+commCardDetailsVO.getcVV()+"</Cvv2Val>\n" +
                        "    <BillTo>\n" +
                        "        <Name>"+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"</Name>\n" +
                        "        <Street1>"+commAddressDetailsVO.getStreet()+"</Street1>\n" +
                        "        <City>"+commAddressDetailsVO.getCity()+"</City>\n" +
                        "        <StateProv>"+commAddressDetailsVO.getState()+"</StateProv>\n" +
                        "        <PostalCode>"+commAddressDetailsVO.getZipCode()+"</PostalCode>\n" +
                        "        <Country>"+commAddressDetailsVO.getCountry()+"</Country>\n" +
                        "    </BillTo>\n" +
                        "</CC5Request>";
            String finalSaleRequestlog="<CC5Request>\n" +
                    "    <Name>"+name+"</Name>\n" +
                    "    <Password>"+password+"</Password>\n" +
                    "    <ClientId>"+clientid+"</ClientId>\n" +
                    "    <OrderId>"+trackingID+"</OrderId>\n" +
                    "    <Type>"+transType+"</Type>\n" + //here in this integration auth means is sale ,so please don't change .
                    emiSupport+
                    "    <IPAddress>"+commAddressDetailsVO.getCardHolderIpAddress()+"</IPAddress>\n" +
                    "    <Total>"+commTransactionDetailsVO.getAmount()+"</Total>\n" +
                    "    <Currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"</Currency>\n" +
                    "    <Number>"+functions.maskingPan(commCardDetailsVO.getCardNum())+"</Number>\n" +
                    "    <Expires>"+functions.maskingExpiry(commCardDetailsVO.getExpMonth()+"/"+commCardDetailsVO.getExpYear())+"</Expires>\n" +
                    "    <Cvv2Val>"+functions.maskingNumber(commCardDetailsVO.getcVV())+"</Cvv2Val>\n" +
                    "    <BillTo>\n" +
                    "        <Name>"+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"</Name>\n" +
                    "        <Street1>"+commAddressDetailsVO.getStreet()+"</Street1>\n" +
                    "        <City>"+commAddressDetailsVO.getCity()+"</City>\n" +
                    "        <StateProv>"+commAddressDetailsVO.getState()+"</StateProv>\n" +
                    "        <PostalCode>"+commAddressDetailsVO.getZipCode()+"</PostalCode>\n" +
                    "        <Country>"+commAddressDetailsVO.getCountry()+"</Country>\n" +
                    "    </BillTo>\n" +
                    "</CC5Request>";
                transactionLogger.error("finalSaleRequest-----"+trackingID + "--" + finalSaleRequestlog);

                String finalSaleResponse="";
                if(isTest){
                    finalSaleResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"),finalSaleRequest);
                }else {
                    finalSaleResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"),finalSaleRequest);
                }
                transactionLogger.error("finalSaleResponse-----"+trackingID + "--" + finalSaleResponse);

                if(functions.isValueNull(finalSaleResponse)){
                    commResponseVO = NestPayUtils.readSoapResponse(finalSaleResponse,gatewayAccount.getDisplayName());
                }else {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Internal Error");
                    commResponseVO.setDescription("Error");
                }
                commResponseVO.setTransactionType(PZProcessType.SALE.toString());

        }catch(Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;

    }

    public GenericResponseVO processAuthConfirmation(String trackingID, GenericRequestVO requestVO)
    {
        transactionLogger.error("-----inside processAuthConfirmation-----");
        Functions functions= new Functions();
        NestPayRequestVO commRequestVO=(NestPayRequestVO)requestVO;
        NestPayResponseVO commResponseVO= new NestPayResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO=commRequestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=commRequestVO.getCardDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=commRequestVO.getAddressDetailsVO();
        GatewayAccount gatewayAccount= GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest=gatewayAccount.isTest();
        String clientid=gatewayAccount.getMerchantId();
        String name=gatewayAccount.getFRAUD_FTP_USERNAME();
        String password= gatewayAccount.getFRAUD_FTP_PASSWORD();

        String transType="PreAuth";  //here Auth means Sale
        String emiSupport="";
        String installment="";
        if("Y".equalsIgnoreCase(gatewayAccount.getEmiSupport())){
            if(functions.isValueNull(commTransactionDetailsVO.getEmiCount()))
            {
                installment = commTransactionDetailsVO.getEmiCount();
                emiSupport = "<Instalment>" + installment + "</Instalment>";
            }
        }

        transactionLogger.debug("emisupport Flag-----"+gatewayAccount.getEmiSupport());
        transactionLogger.debug("emisupport-----"+emiSupport);
        transactionLogger.debug("installment-----"+installment);
        transactionLogger.debug("CardholderipAddress-----"+commAddressDetailsVO.getCardHolderIpAddress());
        transactionLogger.debug("ipAddress-----"+commAddressDetailsVO.getIp());


        try{
            String finalAuthRequest="<CC5Request>\n" +
                    "    <Name>"+name+"</Name>\n" +
                    "    <Password>"+password+"</Password>\n" +
                    "    <ClientId>"+clientid+"</ClientId>\n" +
                    "    <OrderId>"+trackingID+"</OrderId>\n" +
                    "    <Type>"+transType+"</Type>\n" + //here in this integration auth means is sale ,so please don't change .
                    emiSupport+
                    "    <IPAddress>"+commAddressDetailsVO.getCardHolderIpAddress()+"</IPAddress>\n" +
                    "    <Total>"+commTransactionDetailsVO.getAmount()+"</Total>\n" +
                    "    <Currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"</Currency>\n" +
                    "    <Number>"+commCardDetailsVO.getCardNum()+"</Number>\n" +
                    "    <Expires>"+commCardDetailsVO.getExpMonth()+"/"+commCardDetailsVO.getExpYear()+"</Expires>\n" +
                    "    <Cvv2Val>"+commCardDetailsVO.getcVV()+"</Cvv2Val>\n" +
                    "    <BillTo>\n" +
                    "        <Name>"+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"</Name>\n" +
                    "        <Street1>"+commAddressDetailsVO.getStreet()+"</Street1>\n" +
                    "        <City>"+commAddressDetailsVO.getCity()+"</City>\n" +
                    "        <StateProv>"+commAddressDetailsVO.getState()+"</StateProv>\n" +
                    "        <PostalCode>"+commAddressDetailsVO.getZipCode()+"</PostalCode>\n" +
                    "        <Country>"+commAddressDetailsVO.getCountry()+"</Country>\n" +
                    "    </BillTo>\n" +
                    "</CC5Request>";

            String finalAuthRequestlog="<CC5Request>\n" +
                    "    <Name>"+name+"</Name>\n" +
                    "    <Password>"+password+"</Password>\n" +
                    "    <ClientId>"+clientid+"</ClientId>\n" +
                    "    <OrderId>"+trackingID+"</OrderId>\n" +
                    "    <Type>"+transType+"</Type>\n" + //here in this integration auth means is sale ,so please don't change .
                    emiSupport+
                    "    <IPAddress>"+commAddressDetailsVO.getCardHolderIpAddress()+"</IPAddress>\n" +
                    "    <Total>"+commTransactionDetailsVO.getAmount()+"</Total>\n" +
                    "    <Currency>"+CurrencyCodeISO4217.getNumericCurrencyCode(commTransactionDetailsVO.getCurrency())+"</Currency>\n" +
                    "    <Number>"+functions.maskingPan(commCardDetailsVO.getCardNum())+"</Number>\n" +
                    "    <Expires>"+functions.maskingExpiry(commCardDetailsVO.getExpMonth()+"/"+commCardDetailsVO.getExpYear())+"</Expires>\n" +
                    "    <Cvv2Val>"+functions.maskingNumber(commCardDetailsVO.getcVV())+"</Cvv2Val>\n" +
                    "    <BillTo>\n" +
                    "        <Name>"+commAddressDetailsVO.getFirstname()+" "+commAddressDetailsVO.getLastname()+"</Name>\n" +
                    "        <Street1>"+commAddressDetailsVO.getStreet()+"</Street1>\n" +
                    "        <City>"+commAddressDetailsVO.getCity()+"</City>\n" +
                    "        <StateProv>"+commAddressDetailsVO.getState()+"</StateProv>\n" +
                    "        <PostalCode>"+commAddressDetailsVO.getZipCode()+"</PostalCode>\n" +
                    "        <Country>"+commAddressDetailsVO.getCountry()+"</Country>\n" +
                    "    </BillTo>\n" +
                    "</CC5Request>";

            transactionLogger.error("finalAuthRequest-----"+trackingID + "--" + finalAuthRequestlog);

            String finalAuthResponse="";
            if(isTest){
                finalAuthResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("TEST_URL"),finalAuthRequest);
            }else {
                finalAuthResponse=NestPayUtils.doPostHTTPSURLConnectionClient(RB.getString("LIVE_URL"),finalAuthRequest);
            }
            transactionLogger.error("finalAuthResponse-----"+trackingID + "--" + finalAuthResponse);

            if(functions.isValueNull(finalAuthResponse)){
                commResponseVO = NestPayUtils.readSoapResponse(finalAuthResponse,gatewayAccount.getDisplayName());
            }else {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Internal Error");
                commResponseVO.setDescription("Error");
            }
            commResponseVO.setTransactionType(PZProcessType.AUTH.toString());

        }catch(Exception e){
            transactionLogger.error("Exception-----",e);
        }
        return commResponseVO;

    }
}

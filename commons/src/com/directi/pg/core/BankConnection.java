package com.directi.pg.core;

import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.core.ecore.EcoreUtils;
import com.directi.pg.core.paymentgateway.PfsPaymentGateway;
import com.directi.pg.core.qwipi.QwipiUtils;
import com.directi.pg.core.valueObjects.EcoreAddressDetailsVO;
import com.directi.pg.core.valueObjects.EcoreRequestVO;
import com.directi.pg.core.valueObjects.EcoreTransDetailsVO;
import com.directi.pg.core.valueObjects.GenericCardDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.ReitumuBank.core.ReitumuUtills;
import com.payment.alliedwalled.core.message.com._381808.service.MerchantLocator;
import com.payment.alliedwalled.core.message.com._381808.service.MerchantSoap12Stub;
import com.payment.borgun.core.BorgunPaymentGateway;
import com.payment.borgun.core.BorgunRequestVO;
import com.payment.borgun.core.GetAuthorization;
import com.payment.fraudAPI.ConnectionUtils;
import com.payment.gold24.core.Gold24PaymentGateway;
import com.payment.gold24.core.message.SalesRequest;
import com.payment.gold24.core.message.SalesResponse;
import com.payment.opx.OPXUtils;
import com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceSoap12Stub;
import com.payment.payforasia.core.PayforasiaPaymentGateway;
import com.payment.payforasia.core.PayforasiaUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Waheed
 * Date: 6/12/14
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankConnection
{
    private static Logger log = new Logger(BankConnection.class.getName());

    private final static String QWIPI_ARS_URL = "https://secure.qwipi.com/ars/payments.jsp";
    private final static String PAYQWIPI_KSN_URL = "https://secure.qwipi.com/ksn/payments.jsp";
    private final static String PAYECORE_URL = "https://gateway.ecorepay.cc/";
    private final static String PFS_CON_URL = "https://staging.prepaidfinancialservices.com/acqapi/Service.ashx";
    private final static String PAYFORASIA_URL = "https://safer2connect.com/TPInterface";
    private final static String REITUMU_LIVE_URL = "https://www2.1stpayments.net/gwprocessor2.php?a=init";
    private final static String OPX_URL = "https://gate.opx.io/process/debit";
    private static String ARENA_url = "https://80.179.60.230/payments/arenaplus.php";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.ArenaPlus");
    String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0 FirePHP/0.7.4";

    private String version = "1000";
    private int processor = 108;
    private int terminalId = 1;
    private int saleTransType = 1;
    public StringBuffer stringBuffer = new StringBuffer("<u>Successful Connection(s) are</u> :-\n<br>");
    public StringBuffer stringBuffer2 = new StringBuffer("<u>failed Connection(s) are</u> :-\n<br>");
    /**
     * Main Method
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            BankConnection bankConnection=new BankConnection();
            bankConnection.checkConnection();
        }
        catch (Exception e)
        {
            log.error("Exception In BankConnection ",e);
        }
    }


    /**
     * Check Connection for Paygateway
     */
    public void checkPayGateway()
    {
        try
        {
            BankConnection bankConnection = new BankConnection();
            log.debug("Pay GateWay Checking");
            PayGateway_ServiceSoap12Stub binding = new PayGateway_ServiceSoap12Stub();
            try
            {
                binding = (PayGateway_ServiceSoap12Stub)
                        new com.payment.payGateway.core.message.paygateway.process.PayGateway_ServiceLocator().getPayGateway_ServiceSoap12();
            }
            catch (Exception e)
            {
            }
            binding.setTimeout(60000);
            com.payment.payGateway.core.message.paygateway.process.TTransacResult response = null;
            response = binding.processTx("", "","","", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
            stringBuffer.append("Paygateway Connection--Successful");
            stringBuffer.append("<br>");
        }
        catch (Exception e)
        {
            stringBuffer2.append("Paygateway Connection Failed--");
            stringBuffer2.append("<br>");
            log.error("There was an Error while posting data to bank. Please contact your merchant"+ e.getMessage());
        }
    }

    /**
     * Check Connection for Gold24
     */
    public void checkGold24()
    {
        try
        {
            log.debug("Gold24 Checking");
            SalesRequest salesRequest = new SalesRequest();
            salesRequest.setMerchantorderid("");
            salesRequest.setMerchantid(10134);
            salesRequest.setPassword("");
            String amount = "55.00";
            int txnPaise = (new BigDecimal(amount).multiply(new BigDecimal(100.00))).intValue();
            salesRequest.setAmount(txnPaise);
            salesRequest.setCurrency("USD");
            salesRequest.setCardnumber("4242424242424242");
            salesRequest.setCardsecuritycode("123");
            salesRequest.setCardexpiremonth("12");
            salesRequest.setCardexpireyear("2015");
            salesRequest.setCardholderfirstname("");
            salesRequest.setCardholdersurname("");
            salesRequest.setCardholderip("");
            salesRequest.setCardholderaddress("");
            salesRequest.setCardholderzipcode("");
            salesRequest.setCardholdercity("");
            salesRequest.setCardholderstate("");
            salesRequest.setCardholdercountrycode("");
            salesRequest.setCardholderphone("");
            salesRequest.setCardholderemail("");
            SalesResponse salesResponse = new Gold24PaymentGateway().getCommBindingStub().sales(salesRequest);

            stringBuffer.append("Gold24 Connection--Successful");
            stringBuffer.append("<br>");
        }
        catch (Exception e)
        {
            stringBuffer2.append("Gold24 Connection Failed--");
            stringBuffer2.append("<br>");
            log.error("Connection problem while posting Data to Bank",e);
        }
    }
    /**
     * Check Connection for Ecore
     */
    public void checkEcore()
    {
        EcoreRequestVO requestVO = createEcoreRequestVO();
        String request = EcoreUtils.createTransactionRequestXML(requestVO, "Authorize");
        log.debug("Ecore Checking");
        try
        {
            String response = EcoreUtils.doPostHTTPSURLConnection(PAYECORE_URL,request);
            stringBuffer.append("Ecore Connection--Successful");
            stringBuffer.append("<br>");
        }
        catch(Exception e)
        {
            stringBuffer2.append("Ecore Connection--Failed--");
            stringBuffer2.append("<br>");
            log.error("There was an Error while posting data to bank. Please contact your merchant"+e.getMessage());
        }
    }
    /**
     * Check Connection for Qwipi
     */
    public void checkQwipi() throws Exception
    {
        log.debug("Qwipi Checking");
        QwipiUtils qwipiUtils = new QwipiUtils();
        String response=null;
        try
        {
            response = qwipiUtils.doPostHTTPSURLConnection(PAYQWIPI_KSN_URL,"");
            stringBuffer.append("Qwipi Connection--Successful");
            stringBuffer.append("<br>");
        }
        catch(Exception e)
        {
            stringBuffer2.append("Qwipi Connection--Failed--");
            stringBuffer2.append("<br>");
            log.error("There was an Error while posting data to bank. Please contact your merchant" + e);
        }
    }

    /**
     * Check Connection for PFS
     */
    public void checkPfs() throws Exception
    {
        log.debug("PFS Checking");
        PfsPaymentGateway pfsPaymentGateway = new PfsPaymentGateway("");   //todo : set account id
        String response = "";
        try
        {
            response = pfsPaymentGateway.doPostHTTPSURLConnection(PFS_CON_URL,"");
            stringBuffer.append("PFS Connection---Successful");
            stringBuffer.append("<br>");
        }
        catch(Exception e)
        {
            stringBuffer2.append("PFS Connection--Failed--");
            stringBuffer2.append("<br>");
            log.error("There was an Error while posting data to bank. Please contact your merchant" + e);
        }
    }

    /**
     * Check Connection for Payforasia
     */
    public void checkPayforasia() throws Exception
    {
        log.debug("Payforasia Checking");
        PayforasiaPaymentGateway gateway = new PayforasiaPaymentGateway("");
        PayforasiaUtils utils = new PayforasiaUtils();
        try
        {
            Map saleMap = new TreeMap();
            saleMap.put("", gateway.SHA256forSales("", "", "", "", "", "", "", "", "", "", "", "", "").toString());
            String req = utils.joinMapValue(saleMap, '&');
            String res = utils.doPostHTTPSURLConnection(PAYFORASIA_URL, req);
            stringBuffer.append("Payforasia Connection---Successful");
            stringBuffer.append("<br>");
        }
        catch(Exception e)
        {
            stringBuffer2.append("Payforasia Connection--Failed--");
            stringBuffer2.append("<br>");
            log.error("There was an Error while posting data to bank. Please contact your merchant" + e);
        }
    }

    /**
     * Check Connection for Reitumu
     */

    public void checkReitumu() throws Exception
    {
        log.debug("Payforasia Checking");
        ReitumuUtills reitumuUtills = new ReitumuUtills();
        Map<String, String> initMap = new TreeMap<String, String>();
        try
        {
            String res = reitumuUtills.doPostHTTPSURLConnection(REITUMU_LIVE_URL,initMap);
            stringBuffer.append("ReitumuBank Connection---Successful");
            stringBuffer.append("<br>");
        }
        catch(Exception e)
        {
            stringBuffer2.append("ReitumuBank Connection--Failed--");
            stringBuffer2.append("<br>");
            log.error("There was an Error while posting data to bank. Please contact your merchant" + e);
        }
    }

    /**
     * Check Connection for OPX
     */
    public void checkOPX() throws Exception
    {
        log.debug("OPX Checking");
        OPXUtils opxUtils = new OPXUtils();
        try
        {
            String res = opxUtils.doPostHTTPSURLConnection(OPX_URL,"","f3ce3ae7cfb481a547ff645566abd3bb7432d892b8d3b441bc7d9d42","a722c37c2e137955f47a974b844404a3d646afed4dca7e09da164321");
            stringBuffer.append("OPX Connection---Successful");
            stringBuffer.append("<br>");
        }
        catch(Exception e)
        {
            stringBuffer2.append("OPX Connection--Failed--");
            stringBuffer2.append("<br>");
            log.error("There was an Error while posting data to bank. Please contact your merchant" + e);
        }
    }


    /**
     * Check Connection for Arena
     */
/*    public void checkArena()
    {
        log.debug("Arena Plus Checking");
        String response = "";
        try
        {
            response = processRequest("");
            stringBuffer.append("Arena Plus Connection--Successful");
            stringBuffer.append("<BR>");
            log.debug("===Response for Arena plus==="+response.toString());
        }
        catch (Exception e)
        {
            stringBuffer2.append("Arena Plus connection--Failed--"+e.getMessage());
            stringBuffer2.append("<br>");
            log.error("There was an Error while posting data to bank. Please contact your merchant", e);
        }

    }*/
    /**
     * Check Connection for Borgan
     */
    public void checkBorgan()
    {
        String response,date;
        log.debug("Borgun Checking");
        try
        {
            BorgunRequestVO borgunRequestVO= null;
            GetAuthorization getAuthorization = new GetAuthorization();
            getAuthorization.setVersion(version);
            getAuthorization.setProcessor(processor);
            getAuthorization.setMerchantID("ee67f626-74f8-4766-b37f-99b5dbc182d7");
            getAuthorization.setTerminalID(terminalId);
            getAuthorization.setTransType(saleTransType);
            String amount =  "55.00";
            String newAmount = "55.00";
            getAuthorization.setTrAmount(newAmount);
            getAuthorization.setTrCurrency(840);
            getAuthorization.setCurrency("USD");
//            date = new BorgunPaymentGateway("").getDate();
//            getAuthorization.setDateAndTime(date);
            getAuthorization.setPan("4242424242424242");
            String year ="2015";
            String onlyYear = year.substring(2);
            getAuthorization.setExpDate("15");
            getAuthorization.setCvc2("123");
            String rrn ="0000000";
            getAuthorization.setRrn(rrn);
            response = new BorgunPaymentGateway("10134").make_request(getAuthorization);
            stringBuffer.append("Borgun Connection--Successful");
            stringBuffer.append("<br>");

        }
        catch (Exception e)
        {
            stringBuffer2.append("Borgun Connection--Failed--");
            stringBuffer2.append("<br>");
            log.error("Connection during posting data to Bank",e);
        }

    }

    /**
     * Check Connection for Allied
     */
    public void checkAllied()
    {
        log.debug("Allied Wallet Checking");
        try
        {
            MerchantSoap12Stub merchantSoap12Stub = new MerchantSoap12Stub();
            merchantSoap12Stub = (MerchantSoap12Stub) new MerchantLocator().getMerchantSoap12();
            merchantSoap12Stub.setTimeout(60000);
            com.payment.alliedwalled.core.message.com._381808.service.ExecuteResponse response = null;
            response = merchantSoap12Stub.executeCreditCard2("ee67f626-74f8-4766-b37f-99b5dbc182d7 ","b428568f-c860-4f79-820b-4b677261c14e", "", 55.00, "", "", "", "", "", "", "", "", "", "", "", "", 12, 2015, "", "");
            stringBuffer.append("Allied Wallet--Successful");
            stringBuffer.append("<br>");
        }
        catch (Exception e)
        {
            stringBuffer2.append("AlliedWallet Connection Failed--");
            stringBuffer2.append("<br>");
            log.debug("Exception while load account details  " + e);

        }

    }

    public void checkConnectivity(HashMap requestMap,String URL)
    {
        try
        {
            String response=null;
            ConnectionUtils connectionUtils = new ConnectionUtils();
            String reqParameters = connectionUtils.joinMapValue(requestMap, '&');
            response = connectionUtils.doPostHTTPSURLConnection(URL, reqParameters);
        }
        catch (Exception e)
        {
            log.error("Exception--->",e);
        }
    }
    private EcoreRequestVO createEcoreRequestVO()
    {
        GenericCardDetailsVO cardDetail= new GenericCardDetailsVO();
        EcoreAddressDetailsVO AddressDetail= new EcoreAddressDetailsVO();
        EcoreTransDetailsVO TransDetail = new EcoreTransDetailsVO();
        EcoreRequestVO requestDetail=null;
        //cardDetailVO.setCardHolderName(name);
        cardDetail.setCardNum("4543474002249996");
        cardDetail.setcVV("105");
        cardDetail.setExpMonth("06");
        cardDetail.setExpYear("17");
        AddressDetail.setTime("20121119052020");
        AddressDetail.setFirstname("Test");
        AddressDetail.setLastname("Test");
        AddressDetail.setLanguage("ENG");
        AddressDetail.setCity("Testville");
        AddressDetail.setCountry("IN");
        AddressDetail.setProducts("Order5007");
        AddressDetail.setState("TT");
        AddressDetail.setZipCode("1234");
        AddressDetail.setStreet("123 Test St");
        AddressDetail.setPhone("1234567890");
        AddressDetail.setEmail("test@test.com");
        AddressDetail.setIp("127.0.0.1");
        AddressDetail.setMd5info("1be3f2894feff8820ae3538ff05e7aaa");
        AddressDetail.setBirthdate("19751211");
        AddressDetail.setSsn("1234");
        AddressDetail.setMd5key("31F4EA0542E6F887F0BB8DF52A1EC899");
        TransDetail.setMerNo("88894");
        TransDetail.setAmount("100.67");
        TransDetail.setCurrency("USD");
        TransDetail.setOrderId("123");
        TransDetail.setOrderDesc("Order5007");
        requestDetail = new EcoreRequestVO(cardDetail,AddressDetail, TransDetail);
        return requestDetail;  //To change body of created methods use File | Settings | File Templates.
    }
    public String processRequest(String data)
    {
        String pkcs12file = RB.getString("arenapkcs12filepath");
        String trustStorePassword  = RB.getString("arenapkcs12storepassword");
        String responseXML = "";
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        log.error("the request : " + data.toString());
        StringBuffer reqBuffer = new StringBuffer();
        try {
            TrustManager trm = new X509TrustManager()
            {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                //@Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException
                {
                }
                //@Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException
                {
                }
            };

            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream keyInput = fullStream (pkcs12file);
            keyStore.load(keyInput,trustStorePassword.toCharArray());
            keyInput.close();

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, trustStorePassword.toCharArray());
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(keyManagerFactory.getKeyManagers(), new TrustManager[]{trm}, new SecureRandom());
            SSLContext.setDefault(context);
            HttpClient httpClient = new HttpClient();
            PostMethod post = new PostMethod(ARENA_url);
            post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            post.setRequestHeader("Accept-Encoding","gzip, deflate");
            post.setRequestHeader("Accept-Language","en");
            post.setRequestHeader("Connection","keep-alive");
            // can be commented out if not working
            //post.setRequestHeader("Host","80.179.60.230");
            post.setRequestHeader("User-Agent",USER_AGENT);
            post.setRequestHeader("x-insight", "activate");
            post.setRequestBody(data); // data to be send
            httpClient.executeMethod(post);
            responseXML = new String(post.getResponseBody());
            reqBuffer.append("Successful");
            reqBuffer.append("\n");
            reqBuffer.append("<br>");
        }
        catch (Exception ex)
        {
            log.error("Exception--->",ex);
            reqBuffer.append("Failed");
            reqBuffer.append("\n");
            reqBuffer.append("<br>");
        } finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {

                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                } catch (IOException e)
                {
                    log.error("Exception during 2 catch ARENAPLUS error :");
                }
            }
        }
        return responseXML;
    }

    public String checkConnection() throws Exception
    {
        BankConnection bankConnection = new BankConnection();

        //Testing Connections
        //bankConnection.checkPayGateway();
       // bankConnection.checkGold24();
        bankConnection.checkQwipi();
        //bankConnection.checkEcore();
        //bankConnection.checkArena();
        bankConnection.checkBorgan();
        bankConnection.checkPfs();
        bankConnection.checkAllied();
        bankConnection.checkPayforasia();
        bankConnection.checkReitumu();
        bankConnection.checkOPX();
        //MailService mailService=new MailService();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        HashMap mailContent=new HashMap();

        StringBuffer mainResult=new StringBuffer();
        mainResult.append(bankConnection.stringBuffer.toString());
        mainResult.append("<BR>");
        mainResult.append(bankConnection.stringBuffer2.toString());
        if(bankConnection.stringBuffer2!=null && bankConnection.stringBuffer2.length()>=37 && !bankConnection.stringBuffer2.toString().equals(""))
        {
            mailContent.put(MailPlaceHolder.STATUS, mainResult.toString());
            asynchronousMailService.sendMerchantSignup(MailEventEnum.BANK_CONNECTION_CHECKING_REPORT, mailContent);
            log.error("mail sent  "+bankConnection.stringBuffer2.length());
        }
        log.error("mail not sent  "+bankConnection.stringBuffer2.length());
        return mainResult.toString();
    }

    private static InputStream fullStream ( String fname ) throws IOException
    {
        FileInputStream fis = new FileInputStream(fname);
        DataInputStream dis = new DataInputStream(fis);
        byte[] bytes = new byte[dis.available()];
        dis.readFully(bytes);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return bais;
    }
}

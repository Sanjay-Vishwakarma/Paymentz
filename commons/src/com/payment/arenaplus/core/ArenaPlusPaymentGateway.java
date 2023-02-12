package com.payment.arenaplus.core;

import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.payment.common.core.CommResponseVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 12/7/13
 * Time: 2:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArenaPlusPaymentGateway extends AbstractPaymentGateway
{

    public static final String GATEWAY_TYPE = "ArenaPlus";
    private static Logger log = new Logger(ArenaPlusPaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ArenaPlusPaymentGateway.class.getName());

    private static String url = "https://80.179.60.230/payments/arenaplus.php";
    private static Integer sale_trans_type = 1;
    private static Integer refund_trans_type = 6;
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.ArenaPlus");
    private static String  trans_header = "news/56"; // trans_header field is move to gateway_account table Field name is passwd
    private static Integer business_number = 68;
    private static Integer trans_subacc = 1;
    private static Integer trans_code = 5964;  // trans_code field is move to gateway_account table Field name is username

//    String trustStorePassword = "qwedsa1234";
  //  String pkcs12file = "C:\\extrawork\\payment\\arenaplus\\keystore2.p12";
    String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0 FirePHP/0.7.4";

// String req = "trans_header=news/56&business_number=68&trans_number=1&trans_subacc=1&trans_type=1&trans_curr=987&trans_pay=12.12&card_type=1&card_number=411111111111111" +
  //       "&card_cvv=123&card_val=12.14&client_name=test&client_surname=abcd&client_mail=<emailaddress>.com&client_index=aa&client_city=testcity&client_country=US" +
    //     "&client_notes=transactiontest&lang=0&trans_code=5964";

    public  ArenaPlusPaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public ArenaPlusPaymentGateway()
    {
    }

    @Override
    public String getMaxWaitDays()
    {
        return "5";
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        ArenaPlusRequestVO arenaPlusRequestVO = (ArenaPlusRequestVO)requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        Integer card_type = 1;
        String response = "";

        StringBuilder sb = new StringBuilder();
        sb.append("trans_header=");
        sb.append(arenaPlusRequestVO.getCommMerchantVO().getPassword());// arenaPlusRequestVO.getCommMerchantVO().getMerchantUsername();
        sb.append("&business_number=");
        sb.append(arenaPlusRequestVO.getCommMerchantVO().getMerchantId());
        sb.append("&trans_subacc=");
        sb.append(trans_subacc);
        sb.append("&trans_code=");  // arenaPlusRequestVO.getCommMerchantVO().getPassword();
        sb.append(arenaPlusRequestVO.getCommMerchantVO().getMerchantUsername());
        sb.append("&lang=0");
        sb.append("&trans_number=");
        sb.append(trackingID);
        sb.append("&trans_type=");
        sb.append(sale_trans_type);

        sb.append("&trans_curr=");
        sb.append(arenaPlusRequestVO.getTransDetailsVO().getCurrency());
        sb.append("&trans_pay=");
        sb.append(arenaPlusRequestVO.getTransDetailsVO().getAmount());
        sb.append("&card_type=");

        String card = arenaPlusRequestVO.getCardDetailsVO().getCardType();
        if(card.equalsIgnoreCase("VISA"))
            card_type = 1;
        else
            card_type = 1;

        sb.append(card_type);

        sb.append("&card_number=");
        sb.append(arenaPlusRequestVO.getCardDetailsVO().getCardNum());
        sb.append("&card_cvv=");
        sb.append(arenaPlusRequestVO.getCardDetailsVO().getcVV());
        sb.append("&card_val=");

        String year = arenaPlusRequestVO.getCardDetailsVO().getExpYear();
        year = year.substring(2);
        sb.append(arenaPlusRequestVO.getCardDetailsVO().getExpMonth() + "." + year);

        sb.append("&client_name=");
        sb.append(arenaPlusRequestVO.getAddressDetailsVO().getFirstname());
        sb.append("&client_surname=");
        sb.append(arenaPlusRequestVO.getAddressDetailsVO().getLastname());
        sb.append("&client_mail=");
        sb.append(arenaPlusRequestVO.getAddressDetailsVO().getEmail());
        sb.append("&client_index=");
        sb.append(arenaPlusRequestVO.getAddressDetailsVO().getFirstname() + "-Sale-client-index");
        sb.append("&client_city=");
        sb.append(arenaPlusRequestVO.getAddressDetailsVO().getCity());
        sb.append("&client_country=");
        sb.append(arenaPlusRequestVO.getAddressDetailsVO().getCountry());
        sb.append("&client_notes=");
        sb.append("sale-request-" + trackingID);
        sb.append("&owner_name=");
        sb.append("owner-" + arenaPlusRequestVO.getCommMerchantVO().getMerchantUsername());


            response =   processRequest(sb.toString());


            String transaction_no = "";
            String description = "";
            String error_code = "";

            String[] split = response.split("&");
            for (int i = 0; i < split.length; i++) {
                String temp = split[i];
                String[] data = temp.split("=");
                String key,value;
                key = data[0];
                value = data[1];

                if(key.equalsIgnoreCase("errcode"))
                    error_code = value;
                if(key.equalsIgnoreCase("errname"))
                    description = value;
                if(key.equalsIgnoreCase("transaction"))
                    transaction_no = value;
            }

            if(error_code.equalsIgnoreCase("0")){
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionId(transaction_no);
                    commResponseVO.setDescription(description);
                    commResponseVO.setDescriptor(description);
            }else
            {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setErrorCode(error_code);
                    commResponseVO.setDescription(description);
                    commResponseVO.setTransactionId(transaction_no);
            }


        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID,GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        ArenaPlusRequestVO arenaPlusRequestVO = (ArenaPlusRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        String transactionId = arenaPlusRequestVO.getTransDetailsVO().getPreviousTransactionId();
        String response = "";

        StringBuilder sb = new StringBuilder();
        sb.append("trans_header=");
        sb.append(arenaPlusRequestVO.getCommMerchantVO().getPassword());     // arenaPlusRequestVO.getCommMerchantVO().getMerchantUsername();
        sb.append("&business_number=");
        sb.append(arenaPlusRequestVO.getCommMerchantVO().getMerchantId());
        sb.append("&trans_type=");
        sb.append(refund_trans_type);

        sb.append("&trans_subacc=");
        sb.append(trans_subacc); // arenaPlusRequestVO.getCommMerchantVO().getPassword();
        sb.append("&trans_code=");
        sb.append(arenaPlusRequestVO.getCommMerchantVO().getMerchantUsername());
        sb.append("&lang=0");

        sb.append("&trans_curr=");
        sb.append(arenaPlusRequestVO.getTransDetailsVO().getCurrency());
        sb.append("&trans_current=");
        sb.append(arenaPlusRequestVO.getTransDetailsVO().getPreviousTransactionId());

        sb.append("&client_notes=");
        sb.append("refund-tracking-Id-" + trackingID);
        sb.append("&recurrent_pay=");
        sb.append(arenaPlusRequestVO.getTransDetailsVO().getAmount());

            response = processRequest(sb.toString());

            String transaction_no = "";
            String description = "";
            String error_code = "";
            String[] split = response.split("&");
            for (int i = 0; i < split.length; i++) {
                String temp = split[i];
                String[] data = temp.split("=");
                String key,value;
                key = data[0];
                value = data[1];

                if(key.equalsIgnoreCase("errcode"))
                    error_code = value;
                if(key.equalsIgnoreCase("errname"))
                    description = value;
                if(key.equalsIgnoreCase("transaction"))
                    transaction_no = value;
            }

            if(error_code.equalsIgnoreCase("0")){
                    commResponseVO.setStatus("success");
                    commResponseVO.setTransactionId(transaction_no);
                    commResponseVO.setDescription(description);
                    commResponseVO.setDescriptor(description);
            }else
            {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setErrorCode(error_code);
                    commResponseVO.setDescription(description);
                    commResponseVO.setTransactionId(transaction_no);
            }

        return commResponseVO;
    }

    public String processRequest(String data) throws PZTechnicalViolationException
  {
        String pkcs12file = RB.getString("arenapkcs12filepath");
        String trustStorePassword  = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH();

        String responseXML = "";
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

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
                InputStream keyInput = fullStream (pkcs12file+ GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_SITE());
                keyStore.load(keyInput,trustStorePassword.toCharArray());
                keyInput.close();

           log.error("Arenaplus step 1:keyfacotry ") ;
           transactionLogger.error("Arenaplus step 1:keyfacotry ") ;
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
                keyManagerFactory.init(keyStore,trustStorePassword.toCharArray());

           log.error("Arenaplus step 1_1:sslcontext ");
           transactionLogger.error("Arenaplus step 1_1:sslcontext ");
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(keyManagerFactory.getKeyManagers(), new TrustManager[] { trm }, new SecureRandom());
                SSLContext.setDefault(context);

           log.error("Arenaplus step 2:httpclient") ;
           transactionLogger.error("Arenaplus step 2:httpclient") ;
                HttpClient httpClient = new HttpClient();
                PostMethod post = new PostMethod(url);

                post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                post.setRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                post.setRequestHeader("Accept-Encoding","gzip, deflate");
                post.setRequestHeader("Accept-Language","en");
                post.setRequestHeader("Connection","keep-alive");
               // can be commented out if not working
                //post.setRequestHeader("Host","80.179.60.230");
                post.setRequestHeader("User-Agent",USER_AGENT);
                post.setRequestHeader("x-insight","activate");

                log.error("Arenaplus step 3:postingrequest ");
                transactionLogger.error("Arenaplus step 3:postingrequest ");
                post.setRequestBody(data); // data to be send
                httpClient.executeMethod(post);
            //System.out.println("Arenaplus step 3:postingrequest ");
                responseXML = new String(post.getResponseBody());
            log.error("Arenaplus step 4:afterrequest ") ;
            transactionLogger.error("Arenaplus step 4:afterrequest ") ;
            //System.out.println("arenaplus response : " + responseXML);

        }
        catch (CertificateException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ArenaPlusPaymentGateway.class.getName(),"processRequest()",null,"Common","Certificate Exception while placing transaction", PZTechnicalExceptionEnum.CERTIFICATE_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (UnrecoverableKeyException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ArenaPlusPaymentGateway.class.getName(), "processRequest()", null, "Common", "UnrecoverableKey Exception while placing transaction", PZTechnicalExceptionEnum.UNRECOVERABLE_KEY_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (NoSuchAlgorithmException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ArenaPlusPaymentGateway.class.getName(),"processRequest()",null,"Common","NoSuchAlgorithm Exception while placing transaction", PZTechnicalExceptionEnum.NOSUCH_ALGO_EXCEPTION,null,e.getMessage(),e.getCause());
        }
        catch (KeyStoreException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ArenaPlusPaymentGateway.class.getName(), "processRequest()", null, "Common", "KeyStore Exception while placing transaction", PZTechnicalExceptionEnum.KEY_STORE_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (KeyManagementException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ArenaPlusPaymentGateway.class.getName(), "processRequest()", null, "Common", "KeyManagement Exception while placing transaction", PZTechnicalExceptionEnum.KEY_MANAGEMENT_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (HttpException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ArenaPlusPaymentGateway.class.getName(), "processRequest()", null, "Common", "Http Exception while placing transaction", PZTechnicalExceptionEnum.HTTP_EXCEPTION,null, e.getMessage(), e.getCause());
        }
        catch (IOException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(ArenaPlusPaymentGateway.class.getName(), "processRequest()", null, "Common", "IO Exception while placing transaction", PZTechnicalExceptionEnum.IOEXCEPTION,null, e.getMessage(), e.getCause());
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                log.error("Exception during 2 catch ARENAPLUS error :" + e.getMessage());
                transactionLogger.error("Exception during 2 catch ARENAPLUS error :" + e.getMessage());
                }
            }
        }

      return responseXML;
  }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("ArenaPlusPaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
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


   /*
  public static void main12(String[] args) throws SystemError
    {
        String https_url = "https://80.179.60.230/payments/arenaplus.php";
        String trustStorePassword = "q1w2e3";
        trustStorePassword = "qwedsa1234";
        String pkcs12file = "C:\\extrawork\\payment\\arenaplus\\keystore2.p12";
        String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0 FirePHP/0.7.4";
        //USER_AGENT = "Mozilla/4.0 (compatible; MSIE 5.01; Windows NT 5.0)\";


        String responseXML = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {


                TrustManager trm = new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    //@Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                            throws CertificateException
                    {
                    }

                    //@Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
                            throws CertificateException {
                    }
                };

                KeyStore keyStore = KeyStore.getInstance("PKCS12");


                InputStream keyInput = fullStream (pkcs12file);
                keyStore.load(keyInput, trustStorePassword.toCharArray());
                keyInput.close();
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
                keyManagerFactory.init(keyStore, trustStorePassword.toCharArray());

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(keyManagerFactory.getKeyManagers(), new TrustManager[] { trm }, new SecureRandom());

                SSLContext.setDefault(context);

//                String req = "trans_header=news/56&business_number=68&trans_number=1" +
//                         "&trans_subacc=1&trans_type=1&trans_curr=987&trans_pay=12.12" +
//                         "&card_type=1&card_number=411111111111111&card_cvv=123&card_val=12.14" +
//                         "&client_name=test&client_surname=abcd&client_mail=<emailaddress>"+
//                         "&client_index=aa&client_city=testcity&client_country=US&client_notes=transactiontest&lang=0&trans_code=5964";

                String req = "trans_header=news/56&business_number=68&trans_number=1231&trans_subacc=1&trans_type=1&trans_curr=987&trans_pay=12.12&card_type=1&card_number=411111111111111&card_cvv=123&card_val=12.14&client_name=test&client_surname=abcd&client_mail=<emailaddress>&client_index=aa&client_city=testcity&client_country=US&client_notes=transactiontest&lang=0&trans_code=5964";

                HttpClient httpClient = new HttpClient();
                PostMethod post = new PostMethod(https_url);

                post.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                */
                //post.setRequestHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    /*
                //post.setRequestHeader("Accept-Encoding","gzip, deflate");
                post.setRequestHeader("Accept-Language","en");
                post.setRequestHeader("Connection","keep-alive");
                post.setRequestHeader("Host","80.179.60.230");
                post.setRequestHeader("User-Agent",USER_AGENT);
                post.setRequestHeader("x-insight","activate");

                post.setRequestBody(req);
                httpClient.executeMethod(post);
                responseXML = new String(post.getResponseBody());

            System.out.println("respneXML " + responseXML);

        } catch (Exception ex) {
            ex.printStackTrace();
     //       log.error("Exception during URL Connection=  1" + ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }

        }
    }



    public static void mainadaa(String[] args)
    {
        String temp ="errcode=0&ername=Peadasarmitted transaction&transaction=12qw23";

        if(temp.contains("errcode=10&"))
            System.out.println("contan the sequence");
        else
            System.out.println("does not contain");

        int first = temp.indexOf("errname=");

        int second = temp.indexOf("&",first);
        System.out.println(" second : " + second);
        String substring = temp.substring(first + 8,second);
        System.out.println(" subsitng : " + substring);

        int s = temp.indexOf("transaction=");
        System.out.println(" trnsactin : " + s);
        int ss = temp.indexOf("&",s);
        System.out.println("ss " + ss);

        String sub;
        if(ss < 0)
            sub = temp.substring(s + 12);
        else
            sub = temp.substring(s+12,ss);

        System.out.println(" sub " + sub);

        //int second = temp.indexOf()

        System.out.println("the : " + first);




        if(temp.equalsIgnoreCase("errcode=10&"))
            System.out.println("not error");
        else
            System.out.println("got error");

        String temp1 ="errcode=1&errname=Permitted transaction&transaction=reference_number";



    }

    public static void main(String[] args)
    {
        //String response = "errcode=0&errname=Transaction not allowed&transaction=3456";
        String response = "errcode=10&errname=Transaction not allowed";

        String transaction_no = "";
        String description = "";
        String error_code = "";

        String[] split = response.split("&");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            String temp = split[i];
            String[] data = temp.split("=");
            String key,value;
            key = data[0];
            value = data[1];

            System.out.println(" key : value " + key + "--" + value);
            if(key.equalsIgnoreCase("errcode"))
                error_code = value;
            if(key.equalsIgnoreCase("errname"))
                description = value;
            if(key.equalsIgnoreCase("transaction"))
                transaction_no = value;
        }

        if(error_code.equalsIgnoreCase("0"))
        {
            System.out.println(" transaction : " + transaction_no);
            System.out.println(" error code " + error_code);
            System.out.println(" description : " + description);
        }else
        {
            System.out.println(" error code " + error_code);
            System.out.println(" desription : " + description);
        }

    }

    */

}
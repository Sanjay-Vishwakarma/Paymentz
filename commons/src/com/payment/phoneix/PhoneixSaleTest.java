package com.payment.phoneix;


import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import sun.misc.BASE64Encoder;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;

/**
 * Created by vivek on 9/10/2019.
 */
public class PhoneixSaleTest
{
    public static void main(String[] args)
    {
        String testURL = "https://process.txpmnt.net/member/remote_charge.asp";
        String companyNum="8375810";
        //String companyNum="5577822";
        String cardNum	="4387751111111111";
        String ExpMonth	="12";
        String ExpYear	="2024";
        String Member	="Vivek Joshi";
        String TypeCredit 	="1";
        String Amount 	="55.30";
        String Currency ="1";
        String CVV2 ="123";
        String PhoneNumber ="9874587962";
        String Email ="vivek.joshi@paymentz.com";
        String BillingZipCode ="400005";
        String BillingCountry ="IN";
        String Order ="test123";
        String redUrl="http://localhost:8081/transaction/PhoneixFrontEndServlet?trackingid="+1;
        String request="CompanyNum="+companyNum+"&TransType=1&CardNum="+cardNum+"&ExpMonth="+ExpMonth+"&ExpYear="+ExpYear+"&Member="+Member+"&TypeCredit="+TypeCredit+"&Payments=1&Amount="+Amount+"&Currency="+Currency+"&CVV2="+CVV2+"&PhoneNumber="+PhoneNumber+"&Email="+Email+"&BillingZipCode="+BillingZipCode+"&BillingCountry="+BillingCountry+"&Order="+Order+"&RetURL="+redUrl;
       //String request="CompanyNum="+companyNum+"&TransType=2&TypeCredit="+TypeCredit+"&Payments=1&Amount="+Amount+"&Currency="+Currency+"&TransApprovalID=30850";
        //String request="CompanyNum="+companyNum+"&TransType=0&TypeCredit="+TypeCredit+"&Amount="+Amount+"&RefTransID=20";
       //String request="CompanyNum=8375810&Order=106651&Signature=vz+8e6U2M99ClMPKoQ2QcKtu/219c90n/VjjgJ+Kr48=";
        //+"&PhoneNumber="+PhoneNumber+"&Email="+Email+"&BillingZipCode="+BillingZipCode+"&BillingCountry="+BillingCountry+"
        try
        {
            System.out.println("req---->"+request);
            String response = doHttpPostConnection(testURL, request);
            System.out.println("response---->"+response);
        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }

    }
    /*public static void main(String[] args)
    {
       // String testURL = "https://process.txpmnt.com/member/remote_charge.asp";
        String inqueryURL = "https://process.txpmnt.com/member/getStatus.asp";
        String companyNum="8375810";
        String order="2861681";
        String merchantkey="NTLV87VO6A";
        try
        {
            String signature=hashSignature(companyNum+order+merchantkey);

        String request="CompanyNum="+companyNum+"&Order="+order+"&Signature="+signature;
        //String request="CompanyNum="+companyNum+"&RefTransID="+RefTransID+"&Action="+Action+"&Amount="+Amount+"&TransApprovalID=18";
        //+"&PhoneNumber="+PhoneNumber+"&Email="+Email+"&BillingZipCode="+BillingZipCode+"&BillingCountry="+BillingCountry+"

            System.out.println("req---->"+request);
            String response = doHttpPostConnection(inqueryURL, request);
            System.out.println("response---->"+response);
        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }*/
    public static String doHttpPostConnection(String operationUrl,String req) throws PZTechnicalViolationException
    {
        InputStream inputStream;
        byte[] bytes=new byte[]{};
        try
        {
            URL url = new URL(null,operationUrl,new sun.net.www.protocol.https.Handler());
            // above first and last parameter in URL is used to handle ClassCalst Exception
            //com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl cannot be cast to javax.net.ssl.HttpsURLConnection
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            dataOutputStream.writeBytes(req);
            dataOutputStream.flush();
            dataOutputStream.close();
            int responseCode = conn.getResponseCode();

            if (responseCode >= 400)
            {
                inputStream = conn.getErrorStream();
            }
            else
            {
                inputStream = conn.getInputStream();
            }
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            PZExceptionHandler.raiseTechnicalViolationException("KortaPayUtils.java", "doPostHTTPSURLConnection()", null, "common", "IO Exception:::", PZTechnicalExceptionEnum.IOEXCEPTION, null, e.getMessage(), e.getCause());
        }
        return new String(bytes);
    }
    public static String hashSignature(String stringaMac) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        BASE64Encoder encoder = new BASE64Encoder();
        byte[] in = digest.digest(stringaMac.getBytes());

         String builder = "";
        builder=encoder.encode(in);
        return builder.toString();
    }
}

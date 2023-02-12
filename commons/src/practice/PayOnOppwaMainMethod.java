package practice;

/**
 * Created by Admin on 6/13/2020.
 */

import com.directi.pg.LoadProperties;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.payonOppwa.PayonOppwaUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
public class PayOnOppwaMainMethod
{




        public static void sale()
        {

            // ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payon-oppwa");
            String merchantId = "8ac7a4c77279a71001727f1c67bd133e";
            String transactionAmount = "10.00";
            String transactionCurrency = "EUR";
            String cardType = "VISA";
            String reqType = "DB";
            String trackingID = "12344";
            String customerCardNumber = "4711100000000000";
            String firstName = "Alex";
            String lastName = "Lee";
            String customerCardExpiryMonth = "12";
            String customerCardExpiryYear = "2021";
            String customerCvv = "123";
            String customeremail = "abc123@gmail.com";
            String hostIPAddress = "192.168.1.1";
            String customerCity = "Mumbai";
            String customerStreet = "Wall Street";
            String customerState = "MH";
            String customerZipCode = "400074";
            String customer_birthDate = "1995-05-12";
            String WalletId = "";
            String termUrl = "localhost:8081/transaction/NPOFrontEndServlet?trackingId=";
            String country = "IN";
            String authorizationToken = "OGE4Mjk0MTc1M2NjOGJkODAxNTNlNTM4ZmJkNzJhNDV8QXM2SG5LMmZwNw==";
            String testUrl = "https://test.oppwa.com//v1/payments";
            String recurringType = "INITIAL";
            String createRegistration = "true";
            try
            {
                String saleRequest = ""
                        + "authentication.entityId=" + merchantId
                        + "&amount=" + transactionAmount
                        + "&currency=" + transactionCurrency
                        + "&paymentBrand=" + cardType
                        + "&paymentType=" + reqType
                        + "&merchantTransactionId=" + trackingID
                        + "&card.number=" + customerCardNumber
                        + "&card.holder=" + firstName + " " + lastName
                        + "&card.expiryMonth=" + customerCardExpiryMonth
                        + "&card.expiryYear=" + customerCardExpiryYear
                        + "&card.cvv=" + customerCvv
                        + "&customer.givenName=" + firstName
                        + "&customer.surname=" + lastName
                        + "&customer.email=" + customeremail
                        + "&customer.ip=" + hostIPAddress
                        + "&billing.city=" + customerCity
                        + "&billing.street1=" + customerStreet
                        + "&billing.state=" + customerState
                        + "&billing.postcode=" + customerZipCode
                        + "&billing.country=" + country
                        + "&customer.birthDate=" + customer_birthDate
                        + "&customParameters[WalletId]=" + WalletId
                        + "&shopperResultUrl=" + URLEncoder.encode(termUrl + trackingID, "UTF-8")
                        + "&recurringType=" + recurringType
                        + "&createRegistration=" + createRegistration;


                System.out.println("request-------" + saleRequest);


           // transactionLogger.error("inside isTest-----" + RB.getString("TEST_URL"));
                String response = "";
                PayonOppwaUtils payonOppwaUtils = new PayonOppwaUtils();
                response = payonOppwaUtils.doPostHTTPSURLConnectionClient(testUrl, authorizationToken, saleRequest);
                System.out.println("response---------" + response);
            }
            catch (UnsupportedEncodingException e)
            {
                e.printStackTrace();
            }
            catch (PZTechnicalViolationException e)
            {
                e.printStackTrace();
            }
        }
       public static void recurring()
    {
        //  ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.payon-oppwa");
     //   String recurringurl="https://test.oppwa.com/v1/registrations/8ac7a4a07457debe017464bb73ba4b96/payments";         //Non 3d
       // String entityId="8ac7a4c77279a71001727f1c67bd133e";                                                               //Non 3d
       // String authorizationToken="OGE4Mjk0MTc1M2NjOGJkODAxNTNlNTM4ZmJkNzJhNDV8QXM2SG5LMmZwNw==";                         //Non 3d


        String recurringurl="https://test.oppwa.com/v1/registrations/8ac7a4a17467221e0174680343ff07e4/payments";         // 3D
      String entityId="8ac7a4c96c09c68c016c0a7226dc011d";                                                              // 3D
     String authorizationToken="OGFjN2E0Y2E2OTZiOWZjNzAxNjk2ZDIyZjVkMTAzM2F8dHNFeHNjQmE4Wg==";                       // 3D


        String request_recurring=""
                + "entityId="+entityId
                + "&amount=5.00"
                + "&currency=EUR"
                + "&paymentType=PA"
                + "&recurringType=REPEATED";



        System.out.println("request-------" + request_recurring);


        String response="";
        PayonOppwaUtils payonOppwaUtils=new PayonOppwaUtils();
        try
        {
            response = payonOppwaUtils.doPostHTTPSURLConnectionClient(recurringurl, authorizationToken, request_recurring);
            System.out.println("response---------" + response);
        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
        }
        public static void main(String[] args)
        {

        recurring();
       // sale();
        }

    }




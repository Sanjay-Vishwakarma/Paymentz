package com.payment.xpate;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.Enum.PZProcessType;
import com.payment.common.core.*;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Created by Jitendra on 12-Dec-19.
 */
public class XPatePaymentGateway extends AbstractPaymentGateway
{
    public static final String GATEWAY_TYPE = "xpate";
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.xpate");
    private static TransactionLogger transactionLogger = new TransactionLogger(XPatePaymentGateway.class.getName());
    private static Logger log = new Logger(XPatePaymentGateway.class.getName());

    public XPatePaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    /*public static void main(String[] args)
    {
        String PaRes="eJzVWVmTo0iSfudXpNU8aqs5haQ2VY4Fp5AEiBv0xn" +
                "2DxCGOXz8os47s6uq1ttmxXVu9ELjcPTzCj88j2P9zLIuXR9i0aV19+YT+hnx6CSu/DtIq/vLJ0LnP20//fN3rSROGjBb6fRO+7sWwbd04fEmDL582nkfiwdb7jIUB+ZlASe+z6/n+58hHcR9FkLWPe59e9xeghu2bwLhbJI" +
                "jdwrLeofhnYhd6n3cuiX+OCGQdothus9mFi8RXm14Xk37D9vC312Xyxk/cqnvdu/6dEqTXNUHsSHIPf33dl2EjMK/YmkSxhfr+tod/iF3656hd1jGmwatAg1g4PHaXCOlhg2vgCN+xMAJspv2yh58c+8DtwlcMwRCEQDcvyP" +
                "r39fZ3ZLOH3+j721MdKOt+0b3Ftts9/JGyX3asWTZ0eiUJfA9/f9uH462uwoVjWdv38R7+YdzNrV6RD79lzxYFT+pet1/3XVr+ySh8mfyNvm87t+vbV2cPfx3tfffxeM3AiD10HVd5s6YpFogsSoL337LYN5Z96KevyNOo5f" +
                "kmBYq4btIuKV/xd54fhD38NAV+c+7rXkvjapmsCV+WmKraL5+Srrv9DsPDMPw24L/VTQwv5iIwsoMXhqBN4398epcKA6GK6tc97VZ1lfpukc5ut3hbDLukDl6+T/grlbr61IrCKkt/XtR+9lGi+vykIDi6/gR/MOvvaPvZwK" +
                "Z1P7eJiz4VqWEUPr0Xvhiq8OXTP/5uJDNpHLbdvzP7t5nfNZhu0YevHK91Ul9NCUDTK3cRDyhLXzeK1h3rxYcfOffwd4uX8cd9/r4l74wZ0MK7zJlqZkxOq5zN2nfry2mTUs5FlR/ZxMcFnjDK5HLB2kCTmYuLs0DHKsgRF4" +
                "480MNg7leToRD1dLga4S1GIk5lYNbaqblGavZ5c4J7WR83BFnB/sm2gOTjtRuutult69fjrcfyIoRJaXdclzyzupN1LNtpld57IeZ3B0TZhLjNe2VVhxIZ0PWxJWSsaDVwTt2CFTXjhoSnJlCtGbnrddTG5+15pw3nsDrlto" +
                "TzK8UvusYir5cRrRjbksqR0hT/5mxO5tYVj6XeRhVzpcnKY9YOsWZHwuG3uzZc7yb9smvoftho/XS+zot9PRaSzgaR50aaTPrshVvUxq5gaxJik7bKatvTx0j58uV90z9s9P4UTu8esNfIjnE7932k9V4W+p3kLrlLS19wRr" +
                "PF88tTeCnDLyLlVvl/ycaXr4OvT/rL5byHfxZ/00eHTZdGSyItBUoUBNaYaXrxUAwGgVrKnQqOiKnX8oqc12tlV4HLKrzUkzIwinM81VchefgSUFiOUsAQO85Iz+BIxZIJUcDRQWHqoooM/OAwpqKc2FGVHFtCrvaRcexjLn" +
                "DBwy/HPMCK/KqzvUgPPEANlh7FQre4OeDNScG4CbrSlO3hx/Z6CG5X3ogNzMwC+1go2C6/atThahGxmLG1SDtvCsAoRgZ3ZAUWZQROKhzcnK6G9HjKQpotzYFlTssfqJNSjKKDkBuQUcrYQczAIGXGIOqmu9AmkVloer7QFF" +
                "xiEldkh+9LfK4QAgUligoxMODrEgdTUkqud2dWFsH35XC6cUy8sh0POvDehUWdYyR2yRFJpyndtYIecq11tZhMO9a6WZxBCRmQqDi/J3nK7waEem40ADINlC14/k/Hp2XMgmYt2Capw0lW8Qhl9fEwdxDFc+nZn9IDuaJvJm" +
                "qRXBuLisemMLoqz2q2PuBIWAmzUVYrGBUBvNhwNyQ3IjZWmNvrvr7nmxIjYzVwoAj2JpSMsRXVU12NCKaJgRsXi/lJaufhPhBVkatng7eNnFZ9fVt5AwPfHgWQLXgDb5N+e9y6Xj9Hq9SAmLboN4atluIB9MPkU5odKZFBKn" +
                "gxGfOmLWJVlkypvuFWsLY6MhES0gw5HGABYkbbIMREamWHZdtmK6jaHU/wasfrZ14s4o7E7lJSaqbmoNesYY466t8FOWE58TqJJHUMt/DUtEORWVsOcbUmOYSP0ko2uku6a4i+RCM7na76o9R4yYUvisAABVA1cUBE4TCKAl" +
                "8+vXfQRDa+EkN87ZPYcfPYEZfoBz+Hxn8m+p/BD/2N6E9FGnyL/utfRb9R7h5QQC9R7wiKgrNUxNTzITnd8Fs5+4eGvH5dwkEVWZABIALiqTRgBpaChyXYBuEQM6B55wEsdGRAGoPtM1UwUQfLE2CSDkbJvFPMLDzTZxSZ5c" +
                "k46MKDWn8KauNrULMAiu9kVVJEo4WI1Dca2jvS4+6exSlqpant4Wm90so8lk8w01ypVbLKJ5K47HK9rFuvgMOGVzoElyMGzjKoqhxQ2Ure3LebDPdnmjJ9TK03cuhczuONDfCYr+o+47LgdIsmvovk6jb7Z73eYKRFPXKc4h" +
                "fkMGaS0aAg4jrYWTa77SlOCfUzcekma53Q7IXHBi/Sjs1oERi6ZQrF9+bKbAfNWbCCoOF+x9k0bfUy3p55o7VO0NSFbNQkK5+dnGKnP5Aom2Gfr309v9G6fDlMl1Y4Fki724rHdpwE+BRNbNbGm1UYX4JHXgDHqkdJhT0Bmm" +
                "Mv5Q1LEtaWLCwWinJhpE3L3LdHiztMZ/x8J4k3jPm54P8KUS4q8yLSL+8NPF0H4YvQtn3YvCx8LzR4Axe37cKGdpvgxaqbIhjSheuDAB9WL9gTfH7Bt2CRof1NLIqZBYuw9BsW/VW4/jmaGJqKGfYsgvwtHyAqEWnTFD8U31" +
                "rnWDTxcbXwcynxSyU2sd3k8SruWmohasRwfi/oZ3Yy/5C10B/SVqP0qyWhfmkyHqYWAhsUXrrkgCYMwrsChh0LxciVWDfEnzNXgzxshwisRIkUYTM6uySPMy0YtBYzYZS4eqEJ6BtN/0oz6z/A7EeUhf4TheZbnYH+pzD7EW" +
                "Whd5hdvLnsy0+dA7V0Dkwcsxfw9LZS08uYApIbUnGU6SeBK9pNH4r6LGCVMwCvhvzDkCv3YEu1wnT0rkGOoezqMd13bGKjklCnILlsomI1wUnd2iQZNFnUeFN7u57dojF5Mna8gMIyTGu20EypSiVeT6eiZwbnAWMkWVUqFU" +
                "68Y+HcMJn99owC2lF8hAB0Hq6QjSTA4LqVbUWrQ69mWd6Q1xgCb0ioRn1WXcv57qCmSHZktixJZNuA7XBS9GPk6J6UlMm0pJj0gQCUM81X1BwLBE6ZJhq8JaFZWa5rGml8HpIq+iYs3Z1VYVJ5PaDKmHIdlgnXrTJzODyvRD" +
                "x1GjdIdv3V7YtQuh9358upgV2OY7wQoR7I5ew9bLnibtAgj4+jzFhGtqXBwALgytRm6TvIDFy+VXuaWeBDOcAgZn9V9SkqA8Fba3dQCJaLFWPQQVOpeKKiKyFD+1Hs7vWFKrFs0w6x678pEAaNqpbKW514KrlrVDz/v8vNOG" +
                "HB5s5rzc2nkPuWyeOBvvg6nv+q9HwFMrw49o4ZpBOKsmcjoqDo3KceEV90L2cC80yxp7odkclg/Dm0a92QM4r1d0i0PqmZdwuJW1rIizeqi2TwtxS/0oW2QqZWp26kCgXOjIaRdNZ6Vz92t+OtpuUchauBtFbVkbUmxjZ8Gp" +
                "7Me9td08CXeRKI0ckgWHo9UxJ7vtJ8hE6ViBUrKB4euc8YD8LBwg6uYOkunKh5GnhNzrs600sXyQ9eDkLylpEpudZvAxuzt2kwizVSi6V0etiMJM5VA3iI1HZ9u5IPTFZXclGWk1Uqox0YqDgPwWm+ZIdoKFZXMqcWG5v+kL" +
                "G+1I6bC9YYeglTHa5ZFnoitmYkSVBiurDld/qVP9KNiV+zggS7+N8HMrWuu/9dCGPm53GqCX8cp2Qyye+kDYLTA6wFEWQ4u1uLf3GcAv+3x6m/1VBCz47yraH8eoxa4AtboGqQdUCIM/V2jFr6wQ80zl36aCAq7UArbyvh2e" +
                "FoGjMbQCLVvpuciMZfQdMy63jOQPteLVr9aPy6WkDPcvGzuSpm9gIjjMvRKv52DuNNSlNpSlKmnyoGd3x4uBIrCDtACyylIkB4WltKgeDhzAJdS8YDQAgUMyzeitkTqBcvK8yto8uWdAUM03eaTiI5sd1o1wtC6ifPoG51CW" +
                "2uKFVvfUUBLNyJa0PucP0gBk2irzBXyLOdjtwAvhYmSaQlnulWYZZMm+4QDGhDhowNn2zn4BFcEZYOhODMoDa5oh3g8+rah9o6oKOziJeYO4+U3TWmzQRS6EVJt+18Sz2fMLknHxkzB8LcV72lLvmoMgpZ+yh01kwZoxaokR" +
                "H9VA/HCyxu7paqw8e29Eidj5FHWSr8TmFvpVnKVyEczbF3JrypNjHrmWj3wHPC5RFDE6EbZt9hOyTu4UpoNi13V9ckT0yncNbMszErrkDBQRjS+c4yMB2xy9QHrMNQSh36tUaiKlFla5kp56HkoWlsChCLFAB8pmSL38D2DW" +
                "HYQVnyyKFAtH2W3oGJl5hSkcsTyp7eWOCMFynkHc5ixVoajjvE6f5oMjLcjwKa3DgONzVN50S+/28OJwkjK+vZsLGBmswHQt9zFw+9qoRgclRnRfGssJirkDEkM/C7Cq2SY4ddTmjPmxuV4gHf53EAem8YKiphMC7wEuqgm9" +
                "7tfHFGygtEMIU0tA1n1+bTxpYBeCCOxyeXAhUNLmXKu1ak7oPijJbw2Z4S3JagjPq65maEwAJGA1YUeUGEF8TcGOvzXEPFDp/scQn2eyy0Oq4Iq1YV8TiG+WCotwhjKFikRlHLB8A4YDS8PW/h4bBid4f2lolMbhfXOrkIAj" +
                "J2JygJhIs1ezJFMrJxLrBLHvCH8ugTpj1G8sBi0hEoY1m7S+V1+FjmzEc+O900mvlohza4SN3WUFU8i6wH5DQUf8CYNSAI0P26psM/br7g77dhP+7J3m7q374uPC+XP351+BfjRQrB";
        String MD="7bb63d8b-2ed6-416b-abcc-fc13c1005c3b";
        String callBack="https://payments.xpate.com/s2s/d9274a73-eb59";
        XPatePaymentGateway xPatePaymentGateway=new XPatePaymentGateway("");
        xPatePaymentGateway.process3DSaleConfirmation(PaRes,MD,callBack,null);
    }*/
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {
        transactionLogger.error("Inside processsale --- ");

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        CommTransactionDetailsVO transactionDetailsVo = ((CommRequestVO)requestVO).getTransDetailsVO();
        boolean isTest = gatewayAccount.isTest();

        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("from host url----for "+trackingID+ "----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----for "+trackingID+ "----" + termUrl);
        }

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency()))
        {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }
        String ccNum="";
        String expiryMonth="";
        String expiryYear="";
        String cVV="";
        String email="";
        String firstName="";
        String lastName="";
        String cardHolderIp="";
        if(cardDetailsVO!=null)
        {
            if (functions.isValueNull(cardDetailsVO.getCardNum()))
                ccNum=cardDetailsVO.getCardNum();
            if (functions.isValueNull(cardDetailsVO.getExpMonth()))
                expiryMonth=cardDetailsVO.getExpMonth();
                transactionLogger.debug("expMonth---"+functions.maskingNumber(expiryMonth));
            if (functions.isValueNull(cardDetailsVO.getExpYear()))
                expiryYear=cardDetailsVO.getExpYear();
             transactionLogger.debug("expiryYear---"+functions.maskingNumber(expiryYear));
            if (functions.isValueNull(cardDetailsVO.getcVV()))
                cVV=cardDetailsVO.getcVV();
        }
        if (addressDetailsVO!=null)
        {
            if (functions.isValueNull(addressDetailsVO.getEmail()))
                email = addressDetailsVO.getEmail();
            if (functions.isValueNull(addressDetailsVO.getFirstname()))
                firstName = addressDetailsVO.getFirstname();
            if (functions.isValueNull(addressDetailsVO.getLastname()))
                lastName = addressDetailsVO.getLastname();

            if (functions.isValueNull(addressDetailsVO.getCardHolderIpAddress()))
            {
                cardHolderIp = addressDetailsVO.getCardHolderIpAddress();
            }
            else
            {
                cardHolderIp=addressDetailsVO.getIp();
            }
            InetAddress inetAddress= null;
            try
            {
                if(functions.isValueNull(cardHolderIp))
                {
                    inetAddress = InetAddress.getByName(cardHolderIp);

                    if (inetAddress instanceof Inet6Address)
                        cardHolderIp="0.0.0.0";
                }
            }
            catch (UnknownHostException e)
            {
                transactionLogger.error("UnknownHostException----->",e);
            }
        }
        String terminalID=transactionDetailsVo.getTerminalId();
        String conversion_currency="";
        transactionLogger.error("TerminalID----"+terminalID);
        String   amount=transDetailsVO.getAmount();
        if (functions.isValueNull(terminalID))
        {
            HashMap<String, String> hashMap = null;
            hashMap = XPateUtils.getTerminalConversionDetails(terminalID);
            String currency_conversion = hashMap.get("currency_conversion");
            conversion_currency=hashMap.get("conversion_currency");
            transactionLogger.error("Currency Conversion Flag---"+currency_conversion);
            if ("Y".equalsIgnoreCase(currency_conversion))
            {
                transactionLogger.error("conversion_currency---"+conversion_currency);
                transactionLogger.error("amount in "+currency+" ----"+amount);
                amount = XPateUtils.makeCurrencyConversion(currency,conversion_currency,amount);
                transactionLogger.error("after conversion in "+conversion_currency+" >> amount ----"+amount);
                currency=conversion_currency;
            }
        }

        String product_name=trackingID;

        if("JPY".equalsIgnoreCase(transDetailsVO.getCurrency()))
        {
            double amt= Double.parseDouble(amount);
            double roundOff=Math.round(amt);
            int value=(int)roundOff;
            amount=String.valueOf(value);
        }
        else
        {
            amount=XPateUtils.getCentAmount(amount);
        }

        transactionLogger.error(" final amount ----"+amount);

       // String brand_id = "51c7f981-c13d-4d4d-9bc4-6e8c795341b6";
        String brand_id = gatewayAccount.getMerchantId();
        transactionLogger.error("brand_id ---"+brand_id);
        String success_redirect =termUrl+trackingID+"&status=success";
        String failure_redirect = termUrl+trackingID+"&status=failed";
        String secretKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        //String secretKey = "6DsEClZCOTEygZx2nC7whttvadxTQqTpshaPY7e0oewGc1DBjg5oxzmGsdwK_qgyYsAqIaaenIjGrCNqwwPUEQ==";
        //String server2serverKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String server2serverKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH(); // s2s key
        transactionLogger.error("server2serverKey ---"+server2serverKey);
        String fail_message="";
        String fail_code="";

        try
        {

            String createPurchase_request =
                    "{\n" +
                            "\"client\": {" +
                            "\"email\": \"" + email+ "\"" +
                            "}," +
                            "\"purchase\": {" +
                            "\"currency\": \"" + currency + "\"," +
                            "\"products\": [" +
                            "{" +
                            "\"name\": \"" + product_name + "\"," +
                            "\"price\": " + amount + "" +
                            "}" +
                            "]" +
                            "}," +
                            "\"brand_id\": \""+brand_id+"\"," +
                            "\"success_redirect\": \""+success_redirect+"\","+
                            "\"failure_redirect\":  \""+failure_redirect+"\""+
                            "}";

            transactionLogger.error("createPurchase_request ---"+createPurchase_request+"---trackingid---"+trackingID);

            String createPurchase_response ="";
            if(isTest)
            {
                transactionLogger.error("inside isTest-----" + RB.getString("CreatePurchase_url"));
                createPurchase_response = XPateUtils.doPostHTTPSURLConnectionClient(RB.getString("CreatePurchase_url"), createPurchase_request, secretKey);
            }
            else
            {
                transactionLogger.error("inside isTest-----" + RB.getString("CreatePurchase_url"));
                createPurchase_response = XPateUtils.doPostHTTPSURLConnectionClient(RB.getString("CreatePurchase_url"), createPurchase_request, secretKey);
            }

            transactionLogger.error("createPurchase_response ---"+createPurchase_response+"---trackingid---"+trackingID);
            String direct_post_url="";
            String response_paymentId="";

            if (functions.isValueNull(createPurchase_response) && createPurchase_response.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(createPurchase_response.toString());

                if (jsonObject != null)
                {
                    if (jsonObject.has("status"))
                    {
                        String status = jsonObject.getString("status");
                        transactionLogger.error("status ---" + status);
                    }
                    if (jsonObject.has("checkout_url"))
                    {
                        String checkout_url = jsonObject.getString("checkout_url");
                        transactionLogger.error("checkout_url ---" + checkout_url);
                    }
                    if (jsonObject.has("direct_post_url"))
                    {
                         direct_post_url = jsonObject.getString("direct_post_url");
                        transactionLogger.error("direct_post_url ---" + direct_post_url);
                    }
                    if (jsonObject.has("id"))
                    {
                         response_paymentId = jsonObject.getString("id");
                        transactionLogger.error("response_paymentId ---" + response_paymentId);
                    }
                    if(jsonObject.has("__all__"))
                    {
                        transactionLogger.error("Inside _all_");
                        JSONArray jsonArray = jsonObject.getJSONArray("__all__");
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if(jsonObject1.has("message"))
                            {
                                fail_message = jsonObject1.getString("message");
                                transactionLogger.error("fail_message ---- " + fail_message);
                            }
                            if(jsonObject1.has("code")){
                                fail_code = jsonObject1.getString("code");
                                transactionLogger.error("fail_code -------" + fail_code);
                            }
                        }
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark(fail_message);
                        commResponseVO.setDescription(fail_code);
                        return commResponseVO;
                    }
                }
                if (!functions.isValueNull(direct_post_url))
                {
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark("Transaction Fail");
                    return commResponseVO;
                }
                else
                {
                    // step 2----------------------------------------------
                    String new_direct_post_url=direct_post_url+"?s2s=true";
                    transactionLogger.error(" server to server url --- "+new_direct_post_url);
                    String directPost_request =
                            "{\n" +
                                    "\"cardholder_name\": \""+firstName+" "+lastName+"\"," +
                                    "\"card_number\": \""+ccNum+"\","+
                                    "\"expires\": \""+expiryMonth+"/"+expiryYear.substring(2,4)+"\","+
                                    "\"cvc\": \""+cVV+"\","+
                                    "\"remember_card\": \""+"off"+"\","+
                                    "\"remote_ip\": \""+cardHolderIp+"\","+
                                    "\"skip_capture\":  \""+"false"+"\""+
                                    "}";

                    String directPost_request_logs =
                            "{\n" +
                                    "\"cardholder_name\": \""+firstName+" "+lastName+"\"," +
                                    "\"card_number\": \""+functions.maskingPan(ccNum)+"\","+
                                    "\"expires\": \""+functions.maskingExpiry(expiryMonth+"/"+expiryYear.substring(2,4))+"\","+
                                    "\"cvc\": \""+functions.maskingNumber(cVV)+"\","+
                                    "\"remember_card\": \""+"off"+"\","+
                                    "\"remote_ip\": \""+cardHolderIp+"\","+
                                    "\"skip_capture\":  \""+"false"+"\""+
                                    "}";

                    transactionLogger.error("directPost_request--- "+directPost_request_logs+"---trackingid---"+trackingID);

                    String directPost_response ="";
                    if(isTest)
                    {
                        transactionLogger.error("inside isTest-----" + new_direct_post_url);
                        directPost_response = XPateUtils.doPostHTTPSURLConnectionClient(new_direct_post_url, directPost_request, server2serverKey);
                    }
                    else
                    {
                        transactionLogger.error("inside isTest-----" + new_direct_post_url);
                        directPost_response = XPateUtils.doPostHTTPSURLConnectionClient(new_direct_post_url, directPost_request, server2serverKey);
                    }

                    transactionLogger.error("directPost_response ---"+directPost_response+"---trackingid---"+trackingID);


                    if (directPost_response != null)
                    {
                        JSONObject jsonObject1 = new JSONObject(directPost_response);
                        String directPost_response_status="";
                        String Method="";
                        String URL="";
                        String PaReq="";
                        String MD="";
                        String callback_url="";
                        if (jsonObject1 != null)
                        {
                            if (jsonObject1.has("status"))
                            {
                                directPost_response_status = jsonObject1.getString("status");
                                transactionLogger.error("directPost_response_status ---" + directPost_response_status);
                            }
                            if (jsonObject1.has("Method"))
                            {
                                Method = jsonObject1.getString("Method");
                                transactionLogger.error("Method ---" + Method);
                            }
                            if (jsonObject1.has("URL"))
                            {
                                URL = jsonObject1.getString("URL");
                                transactionLogger.error("URL ---" + URL);
                            }
                            if (jsonObject1.has("PaReq"))
                            {
                                PaReq = jsonObject1.getString("PaReq");
                                transactionLogger.error("PaReq ---" + PaReq);
                            }
                            if (jsonObject1.has("MD"))
                            {
                                MD = jsonObject1.getString("MD");
                                transactionLogger.error("MD ---" + MD);
                            }
                            if (jsonObject1.has("callback_url"))
                            {
                                callback_url = jsonObject1.getString("callback_url");
                                transactionLogger.error("callback_url ---" + callback_url);
                            }
                            transactionLogger.error("directPost_response_status ---"+directPost_response_status);

                            if (functions.isValueNull(directPost_response_status))
                            {
                                if (directPost_response_status.equalsIgnoreCase("executed"))
                                {
                                    transactionLogger.error("Inside non-3D");
                                    commResponseVO.setStatus("success");
                                    commResponseVO.setRemark(directPost_response_status);
                                    commResponseVO.setDescription(directPost_response_status);
                                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                }
                                else if (directPost_response_status.equalsIgnoreCase("3DS_required"))
                                {
                                    transactionLogger.error("Inside 3D ----");
                                    commResponseVO.setStatus("pending3DConfirmation");
                                    commResponseVO.setUrlFor3DRedirect(URL);
                                    commResponseVO.setPaReq(PaReq);
                                    commResponseVO.setMd(MD);
                                    commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                    commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                    commResponseVO.setDescription("SYS:3D Authentication Pending");
                                    commResponseVO.setRedirectUrl(callback_url);
                                    commResponseVO.setTerURL(termUrl+ trackingID+"&cb="+callback_url);
                                }
                                else
                                {
                                    transactionLogger.error("Inside else fail ---");
                                    commResponseVO.setStatus("fail");
                                    commResponseVO.setRemark(directPost_response_status);
                                    commResponseVO.setDescription(directPost_response_status);
                                }

                                if (functions.isValueNull(response_paymentId))
                                    commResponseVO.setTransactionId(response_paymentId);

                                commResponseVO.setAmount(transDetailsVO.getAmount());
                                commResponseVO.setTmpl_Currency(tmpl_currency);
                                commResponseVO.setTmpl_Amount(tmpl_amount);
                                commResponseVO.setCurrency(currency);
                            }
                            else
                            {
                                commResponseVO.setStatus("fail");
                                commResponseVO.setRemark("SYS:Transaction Fail");
                            }
                        }
                    }
                    else
                    {
                        transactionLogger.error("Inside directPost Response Null");
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark("SYS:Transaction Fail");
                    }

                }
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        transactionLogger.error("Inside processAuthentication --- ");
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        GenericCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        CommMerchantVO commMerchantVO = commRequestVO.getCommMerchantVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        boolean isTest = gatewayAccount.isTest();

        String termUrl = "";
        if (functions.isValueNull(commMerchantVO.getHostUrl()))
        {
            termUrl = "https://" + commMerchantVO.getHostUrl() + RB.getString("HOST_URL");
            transactionLogger.error("from host url----" + termUrl);
        }
        else
        {
            termUrl = RB.getString("TERM_URL");
            transactionLogger.error("from RB----" + termUrl);
        }

        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(transDetailsVO.getCurrency()))
        {
            currency = transDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }
        String fail_message="";
        String fail_code="";
        String ccNum="";
        String expiryMonth="";
        String expiryYear="";
        String cVV="";
        String email="";
        String firstName="";
        String lastName="";
        String cardHolderIp="";
        if(cardDetailsVO!=null)
        {
            if (functions.isValueNull(cardDetailsVO.getCardNum()))
                ccNum=cardDetailsVO.getCardNum();
            if (functions.isValueNull(cardDetailsVO.getExpMonth()))
                expiryMonth=cardDetailsVO.getExpMonth();
            transactionLogger.debug("expMonth---"+functions.maskingNumber(expiryMonth));
            if (functions.isValueNull(cardDetailsVO.getExpYear()))
                expiryYear=cardDetailsVO.getExpYear();
            transactionLogger.debug("expiryYear---"+functions.maskingNumber(expiryYear));
            if (functions.isValueNull(cardDetailsVO.getcVV()))
                cVV=cardDetailsVO.getcVV();
        }
        if (addressDetailsVO!=null)
        {
            if (functions.isValueNull(addressDetailsVO.getEmail()))
                email = addressDetailsVO.getEmail();
            if (functions.isValueNull(addressDetailsVO.getFirstname()))
                firstName = addressDetailsVO.getFirstname();
            if (functions.isValueNull(addressDetailsVO.getLastname()))
                lastName = addressDetailsVO.getLastname();
            if (functions.isValueNull(addressDetailsVO.getCardHolderIpAddress()))
            {
                cardHolderIp = addressDetailsVO.getCardHolderIpAddress();
            }
            else
            {
                cardHolderIp=addressDetailsVO.getIp();
            }
            InetAddress inetAddress= null;
            try
            {
                if(functions.isValueNull(cardHolderIp))
                {
                    inetAddress = InetAddress.getByName(cardHolderIp);

                    if (inetAddress instanceof Inet6Address)
                        cardHolderIp="0.0.0.0";
                }
            }
            catch (UnknownHostException e)
            {
                transactionLogger.error("UnknownHostException----->",e);
            }
        }

        String product_name=trackingID;
        String amount="";
        if("JPY".equalsIgnoreCase(transDetailsVO.getCurrency()))
        {
            double amt= Double.parseDouble(transDetailsVO.getAmount());
            double roundOff=Math.round(amt);
            int value=(int)roundOff;
            amount=String.valueOf(value);
        }
        else
        {
            amount=XPateUtils.getCentAmount(transDetailsVO.getAmount());
        }
        transactionLogger.error("amount ----"+amount);

        // String brand_id = "51c7f981-c13d-4d4d-9bc4-6e8c795341b6";
        String brand_id = gatewayAccount.getMerchantId();
        transactionLogger.error("brand_id ---"+brand_id);
        String success_redirect =termUrl+trackingID+"?status=success";
        String failure_redirect = termUrl+trackingID+"?status=failed";
        String secretKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        //String secretKey = "6DsEClZCOTEygZx2nC7whttvadxTQqTpshaPY7e0oewGc1DBjg5oxzmGsdwK_qgyYsAqIaaenIjGrCNqwwPUEQ==";
        //String server2serverKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_USERNAME();
        String server2serverKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PATH(); // s2s key
        transactionLogger.error("server2serverKey ---"+server2serverKey);
        String skip_capture = "true";

        try
        {

            String createPurchase_request =
                    "{\n" +
                            "\"client\": {" +
                            "\"email\": \"" + email + "\"" +
                            "}," +
                            "\"purchase\": {" +
                            "\"currency\": \"" + currency + "\"," +
                            "\"products\": [" +
                            "{" +
                            "\"name\": \"" + product_name + "\"," +
                            "\"price\": " + amount + "" +
                            "}" +
                            "]" +
                            "}," +
                            "\"brand_id\": \"" + brand_id + "\"," +
                            "\"skip_capture\": \"" + skip_capture + "\"," +
                            "\"success_redirect\": \"" + success_redirect + "\"," +
                            "\"failure_redirect\":  \"" + failure_redirect + "\"" +
                            "}";

            transactionLogger.error("createPurchase_request ---" + createPurchase_request + "---trackingid---" + trackingID);

            String createPurchase_response = "";
            if (isTest)
            {
                transactionLogger.error("inside isTest-----" + RB.getString("CreatePurchase_url"));
                createPurchase_response = XPateUtils.doPostHTTPSURLConnectionClient(RB.getString("CreatePurchase_url"), createPurchase_request, secretKey);
            }
            else
            {
                transactionLogger.error("inside isTest-----" + RB.getString("CreatePurchase_url"));
                createPurchase_response = XPateUtils.doPostHTTPSURLConnectionClient(RB.getString("CreatePurchase_url"), createPurchase_request, secretKey);
            }

            transactionLogger.error("createPurchase_response ---" + createPurchase_response + "---trackingid---" + trackingID);
            String direct_post_url = "";
            String response_paymentId = "";

            if (functions.isValueNull(createPurchase_response) && createPurchase_response.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(createPurchase_response.toString());

                if (jsonObject != null)
                {
                    if (jsonObject.has("status"))
                    {
                        String status = jsonObject.getString("status");
                        transactionLogger.error("status ---" + status);
                    }
                    if (jsonObject.has("checkout_url"))
                    {
                        String checkout_url = jsonObject.getString("checkout_url");
                        transactionLogger.error("checkout_url ---" + checkout_url);
                    }
                    if (jsonObject.has("direct_post_url"))
                    {
                        direct_post_url = jsonObject.getString("direct_post_url");
                        transactionLogger.error("direct_post_url ---" + direct_post_url);
                    }
                    if (jsonObject.has("id"))
                    {
                        response_paymentId = jsonObject.getString("id");
                        transactionLogger.error("response_paymentId ---" + response_paymentId);
                    }
                    if (jsonObject.has("__all__"))
                    {
                        transactionLogger.error("Inside _all_");
                        JSONArray jsonArray = jsonObject.getJSONArray("__all__");
                        for (int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1.has("message"))
                            {
                                fail_message = jsonObject1.getString("message");
                                transactionLogger.error("fail_message ---- " + fail_message);
                            }
                            if (jsonObject1.has("code"))
                            {
                                fail_code = jsonObject1.getString("code");
                                transactionLogger.error("fail_code -------" + fail_code);
                            }
                        }
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark(fail_message);
                        commResponseVO.setDescription(fail_code);
                        return commResponseVO;
                    }
                    if (!functions.isValueNull(direct_post_url))
                    {
                        commResponseVO.setStatus("fail");
                        commResponseVO.setRemark("Transaction Fail");
                        return commResponseVO;
                    }
                    else
                    {
                        // Step 2---------------------------------------------------------------------------------
                        String new_direct_post_url = direct_post_url + "?s2s=true";
                        transactionLogger.error(" server to server url --- " + new_direct_post_url);
                        String directPost_request =
                                "{\n" +
                                        "\"cardholder_name\": \"" + firstName + " " + lastName + "\"," +
                                        "\"card_number\": \"" + ccNum + "\"," +
                                        "\"expires\": \"" + expiryMonth + "/" + expiryYear.substring(2, 4) + "\"," +
                                        "\"cvc\": \"" + cVV + "\"," +
                                        "\"remember_card\": \"" + "off" + "\"," +
                                        "\"remote_ip\": \"" + cardHolderIp + "\"," +
                                        "\"skip_capture\":  \"" + "true" + "\"" +
                                        "}";

                        String directPost_request_logs =
                                "{\n" +
                                        "\"cardholder_name\": \"" + firstName + " " + lastName + "\"," +
                                        "\"card_number\": \"" + functions.maskingPan(ccNum) + "\"," +
                                        "\"expires\": \"" + functions.maskingExpiry(expiryMonth + "/" + expiryYear.substring(2, 4)) + "\"," +
                                        "\"cvc\": \"" + functions.maskingNumber(cVV) + "\"," +
                                        "\"remember_card\": \"" + "off" + "\"," +
                                        "\"remote_ip\": \"" + cardHolderIp + "\"," +
                                        "\"skip_capture\":  \"" + "true" + "\"" +
                                        "}";

                        transactionLogger.error("directPost_request--- " + directPost_request_logs + "---trackingid---" + trackingID);

                        String directPost_response = "";
                        if (isTest)
                        {
                            transactionLogger.error("inside isTest-----" + new_direct_post_url);
                            directPost_response = XPateUtils.doPostHTTPSURLConnectionClient(new_direct_post_url, directPost_request, server2serverKey);
                        }
                        else
                        {
                            transactionLogger.error("inside isTest-----" + new_direct_post_url);
                            directPost_response = XPateUtils.doPostHTTPSURLConnectionClient(new_direct_post_url, directPost_request, server2serverKey);
                        }

                        transactionLogger.error("directPost_response ---" + directPost_response + "---trackingid---" + trackingID);


                        if (directPost_response != null)
                        {
                            JSONObject jsonObject1 = new JSONObject(directPost_response);
                            String directPost_response_status = "";
                            String Method = "";
                            String URL = "";
                            String PaReq = "";
                            String MD = "";
                            String callback_url = "";
                            if (jsonObject1 != null)
                            {
                                if (jsonObject1.has("status"))
                                {
                                    directPost_response_status = jsonObject1.getString("status");
                                    transactionLogger.error("directPost_response_status ---" + directPost_response_status);
                                }
                                if (jsonObject1.has("Method"))
                                {
                                    Method = jsonObject1.getString("Method");
                                    transactionLogger.error("Method ---" + Method);
                                }
                                if (jsonObject1.has("URL"))
                                {
                                    URL = jsonObject1.getString("URL");
                                    transactionLogger.error("URL ---" + URL);
                                }
                                if (jsonObject1.has("PaReq"))
                                {
                                    PaReq = jsonObject1.getString("PaReq");
                                    transactionLogger.error("PaReq ---" + PaReq);
                                }
                                if (jsonObject1.has("MD"))
                                {
                                    MD = jsonObject1.getString("MD");
                                    transactionLogger.error("MD ---" + MD);
                                }
                                if (jsonObject1.has("callback_url"))
                                {
                                    callback_url = jsonObject1.getString("callback_url");
                                    transactionLogger.error("callback_url ---" + callback_url);
                                }
                                transactionLogger.error("directPost_response_status ---" + directPost_response_status);

                                if (functions.isValueNull(directPost_response_status))
                                {
                                    if (directPost_response_status.equalsIgnoreCase("executed") || directPost_response_status.equalsIgnoreCase("authorized"))
                                    {
                                        transactionLogger.error("inside non 3D");
                                        commResponseVO.setStatus("success");
                                        commResponseVO.setRemark(directPost_response_status);
                                        commResponseVO.setDescription(directPost_response_status);
                                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                    }
                                    else if (directPost_response_status.equalsIgnoreCase("3DS_required"))
                                    {
                                        transactionLogger.error("Inside 3D---- ");
                                        commResponseVO.setStatus("pending3DConfirmation");
                                        commResponseVO.setUrlFor3DRedirect(URL);
                                        commResponseVO.setPaReq(PaReq);
                                        commResponseVO.setMd(MD);
                                        commResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                        commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
                                        commResponseVO.setDescription("SYS:3D Authentication Pending");
                                        commResponseVO.setRedirectUrl(callback_url);
                                        commResponseVO.setTerURL(termUrl + trackingID + "&cb=" + callback_url);
                                    }
                                    else
                                    {
                                        transactionLogger.error("inside else fail ");
                                        commResponseVO.setStatus("fail");
                                        commResponseVO.setRemark(directPost_response_status);
                                        commResponseVO.setDescription(directPost_response_status);
                                    }

                                    if (functions.isValueNull(response_paymentId))
                                        commResponseVO.setTransactionId(response_paymentId);

                                    commResponseVO.setAmount(transDetailsVO.getAmount());
                                    commResponseVO.setTmpl_Currency(tmpl_currency);
                                    commResponseVO.setTmpl_Amount(tmpl_amount);
                                    commResponseVO.setCurrency(currency);
                                }
                                else
                                {
                                    commResponseVO.setStatus("fail");
                                    commResponseVO.setRemark("SYS:Transaction Fail");
                                }
                            }
                        }
                        else
                        {
                            transactionLogger.error("Inside directPost Response Null");
                            commResponseVO.setStatus("fail");
                            commResponseVO.setRemark("SYS:Transaction Fail");
                        }
                    }
                }
            }
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException-----", e);
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }
        return commResponseVO;
    }

    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processCapture ---");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        String transactionId=commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.debug("transactionId ---"+transactionId);

        String capture_url = "https://m.xpate.com/api/v1/purchases/"+transactionId+"/capture/";
        transactionLogger.error("capture_url ---"+capture_url);
        String secretKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency = commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }
        String capture_payment_id="";
        String fail_message="";
        String fail_code="";
        String attempts="";
        String type="";
        String successful="";

        try
        {
            String capture_response = XPateUtils.doHttpPostConnectionForRefund(capture_url, "", "Bearer", secretKey);
            transactionLogger.error("capture_response ---"+capture_response);

            if (functions.isValueNull(capture_response) && capture_response.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(capture_response);
                if (jsonObject.has("id"))
                {
                    capture_payment_id=jsonObject.getString("id");
                    transactionLogger.error("capture_payment_id ---"+capture_payment_id);
                }
                if(jsonObject.has("__all__"))
                {
                    transactionLogger.error("Inside _all_");
                    JSONArray jsonArray = jsonObject.getJSONArray("__all__");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        if(jsonObject1.has("message"))
                        {
                            fail_message = jsonObject1.getString("message");
                            transactionLogger.error("fail_message ---- " + fail_message);
                        }
                        if(jsonObject1.has("code")){
                            fail_code = jsonObject1.getString("code");
                            transactionLogger.error("fail_code -------" + fail_code);
                        }
                    }
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(fail_message);
                    commResponseVO.setDescription(fail_code);
                }
                else if (jsonObject.has("transaction_data"))
                {
                    transactionLogger.error("inside transaction_data ");
                    JSONObject jsonObject2 = jsonObject.getJSONObject("transaction_data");

                    if (jsonObject2.has("attempts"))
                    {
                        JSONArray jsonArray = jsonObject2.getJSONArray("attempts");
                        // for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        if (jsonObject1.has("type"))
                        {
                            type = jsonObject1.getString("type");
                            transactionLogger.error("type ---- " + type);
                        }
                        if (jsonObject1.has("successful"))
                        {
                            successful = jsonObject1.getString("successful");
                            transactionLogger.error("successful -------" + successful);
                        }
                        //  }
                    }
                    if (type.equalsIgnoreCase("capture") && successful.equalsIgnoreCase("true"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(type);
                        commResponseVO.setTransactionId(capture_payment_id);
                    }

                }
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                commResponseVO.setCurrency(currency);
                commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
                commResponseVO.setDescriptor(gatewayAccount.getDisplayName());
            }
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException-----", e);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException-----", e);
        }
        return  commResponseVO;
    }


    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException, PZConstraintViolationException
    {
        transactionLogger.error("Inside processRefund ---");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        String ref=commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.debug("PreviousTransactionId ref  ---"+commTransactionDetailsVO.getPreviousTransactionId());
        String amount="";
        if("JPY".equalsIgnoreCase(commTransactionDetailsVO.getCurrency()))
        {
            double amt= Double.parseDouble(commTransactionDetailsVO.getAmount());
            double roundOff=Math.round(amt);
            int value=(int)roundOff;
            amount=String.valueOf(value);
        }
        else
        {
            amount=XPateUtils.getCentAmount(commTransactionDetailsVO.getAmount());
        }

        int Amount_in_cent=Integer.parseInt(amount);
        transactionLogger.error("Amount_in_cent -------" + Amount_in_cent);

        String currency="";
        String tmpl_amount = "";
        String tmpl_currency = "";
        String payment_type="";
        String fail_message="";
        String fail_code="";
        String refund_payment_id="";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency=commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency = commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }

        String url = "https://m.xpate.com/api/v1/purchases/"+ref+"/refund/";
        transactionLogger.error("Refund url --- "+url);
        String secretKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        //String secretKey = "6DsEClZCOTEygZx2nC7whttvadxTQqTpshaPY7e0oewGc1DBjg5oxzmGsdwK_qgyYsAqIaaenIjGrCNqwwPUEQ==";
        try
        {
            String refund_request =
                    "{\n" +
                            "\"amount\": "+Amount_in_cent+""+
                            "}";

            transactionLogger.error("refund_request --- " + refund_request+"---trackingid---"+trackingID);

            String refund_response = XPateUtils.doHttpPostConnectionForRefund(url, refund_request, "Bearer", secretKey);
            transactionLogger.error("refund_response -----" + refund_response+"---trackingid---"+trackingID);

            if (functions.isValueNull(refund_response) && refund_response.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(refund_response);
                if (jsonObject.has("id"))
                {
                    refund_payment_id=jsonObject.getString("id");
                    transactionLogger.error("refund_payment_id ---"+refund_payment_id);
                }
                if(jsonObject.has("__all__"))
                {
                    transactionLogger.error("Inside _all_");
                    JSONArray jsonArray = jsonObject.getJSONArray("__all__");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        if(jsonObject1.has("message"))
                        {
                             fail_message = jsonObject1.getString("message");
                            transactionLogger.error("fail_message ---- " + fail_message);
                        }
                        if(jsonObject1.has("code")){
                             fail_code = jsonObject1.getString("code");
                            transactionLogger.error("fail_code -------" + fail_code);
                        }
                    }
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(fail_message);
                    commResponseVO.setDescription(fail_code);
                }
                else if (jsonObject.has("payment"))
                {
                    transactionLogger.error("Inside payment ---");
                    JSONObject jsonObject1 = jsonObject.getJSONObject("payment");
                     payment_type= jsonObject1.getString("payment_type");
                    if (payment_type.equalsIgnoreCase("refund"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark("Sys:Refund Success");
                        commResponseVO.setDescription("Sys:Refund Success");
                        commResponseVO.setTransactionId(refund_payment_id);
                    }
                }
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                commResponseVO.setCurrency(currency);
                commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
            }
        }
        catch (JSONException e)
        {
            PZExceptionHandler.raiseTechnicalViolationException(XPatePaymentGateway.class.getName(), "processrefund()", null, "common", "Technical Exception while placing the transaction", PZTechnicalExceptionEnum.JSON_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        return commResponseVO;
    }

    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processVoid ---");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        String transactionId=commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.debug("transactionId ---"+transactionId);

        String cancel_url = "https://m.xpate.com/api/v1/purchases/"+transactionId+"/release/";
        transactionLogger.error("capture_url ---"+cancel_url);
        String secretKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        String currency = "";
        String tmpl_amount = "";
        String tmpl_currency = "";
        if (functions.isValueNull(commTransactionDetailsVO.getCurrency()))
        {
            currency = commTransactionDetailsVO.getCurrency();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_amount()))
        {
            tmpl_amount = addressDetailsVO.getTmpl_amount();
        }
        if (functions.isValueNull(addressDetailsVO.getTmpl_currency()))
        {
            tmpl_currency = addressDetailsVO.getTmpl_currency();
        }
        String capture_payment_id="";
        String fail_message="";
        String fail_code="";
        String attempts="";
        String type="";
        String successful="";

        String cancel_response ="";
        try
        {
            cancel_response = XPateUtils.doHttpPostConnectionForRefund(cancel_url, "", "Bearer", secretKey);
            if (functions.isValueNull(cancel_response) && cancel_response.contains("{"))
            {
                JSONObject jsonObject = new JSONObject(cancel_response);
                if (jsonObject.has("id"))
                {
                    capture_payment_id=jsonObject.getString("id");
                    transactionLogger.error("capture_payment_id ---"+capture_payment_id);
                }
                if(jsonObject.has("__all__"))
                {
                    transactionLogger.error("Inside _all_");
                    JSONArray jsonArray = jsonObject.getJSONArray("__all__");
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        if(jsonObject1.has("message"))
                        {
                            fail_message = jsonObject1.getString("message");
                            transactionLogger.error("fail_message ---- " + fail_message);
                        }
                        if(jsonObject1.has("code")){
                            fail_code = jsonObject1.getString("code");
                            transactionLogger.error("fail_code -------" + fail_code);
                        }
                    }
                    commResponseVO.setStatus("fail");
                    commResponseVO.setRemark(fail_message);
                    commResponseVO.setDescription(fail_code);
                }
                else if (jsonObject.has("transaction_data"))
                {
                    transactionLogger.error("inside transaction_data ");
                    JSONObject jsonObject2 = jsonObject.getJSONObject("transaction_data");

                    if (jsonObject2.has("attempts"))
                    {
                        JSONArray jsonArray = jsonObject2.getJSONArray("attempts");
                        // for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                        if (jsonObject1.has("type"))
                        {
                            type = jsonObject1.getString("type");
                            transactionLogger.error("type ---- " + type);
                        }
                        if (jsonObject1.has("successful"))
                        {
                            successful = jsonObject1.getString("successful");
                            transactionLogger.error("successful -------" + successful);
                        }
                        //  }
                    }
                    if (type.equalsIgnoreCase("release") && successful.equalsIgnoreCase("true"))
                    {
                        commResponseVO.setStatus("success");
                        commResponseVO.setRemark(type);
                        commResponseVO.setTransactionId(capture_payment_id);
                    }

                }
                commResponseVO.setTmpl_Amount(tmpl_amount);
                commResponseVO.setTmpl_Currency(tmpl_currency);
                commResponseVO.setCurrency(currency);
                commResponseVO.setAmount(commTransactionDetailsVO.getAmount());
            }
        }
        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException-----", e);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException-----", e);
        }
        transactionLogger.error("cancel_response ---"+cancel_response);

        return commResponseVO;
    }

    public GenericResponseVO processInquiry( GenericRequestVO requestVO) throws PZConstraintViolationException
    {
        transactionLogger.error("Inside processInquiry ---");
        Functions functions= new Functions();
        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();
        CommTransactionDetailsVO commTransactionDetailsVO = commRequestVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
        String transactionId=commTransactionDetailsVO.getPreviousTransactionId();
        transactionLogger.debug("transactionId ---"+transactionId);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String brand_id = gatewayAccount.getMerchantId();
        String payment_type="";
        String currency="";
        String payment_id="";
        String amount_response="";
        String statusFromInquiry="";
        String timestamp="";

        String inquiry_url = "https://m.xpate.com/api/v1/purchases/"+transactionId+"/";
        transactionLogger.error("inquiry_url ---"+inquiry_url);
        String secretKey = GatewayAccountService.getGatewayAccount(accountId).getFRAUD_FTP_PASSWORD();
        try
        {
            String inquiryResponse = XPateUtils.doPostHTTPSURLConnectionInquiry(inquiry_url,secretKey);
            transactionLogger.error("inquiryResponse ---"+inquiryResponse);
            if (inquiryResponse != null)
            {
                JSONObject jsonObject = new JSONObject(inquiryResponse);
                if (jsonObject != null)
                {
                    if (jsonObject.has("payment") && !jsonObject.isNull("payment"))
                    {
                        transactionLogger.error("Inside payment ---");
                        JSONObject jsonObject1 = jsonObject.getJSONObject("payment");
                        if (jsonObject1!=null)
                        {
                            if (functions.isValueNull(jsonObject1.getString("payment_type")))
                                payment_type = jsonObject1.getString("payment_type");
                                transactionLogger.error("payment_type ---"+payment_type);
                            if (functions.isValueNull(jsonObject1.getString("currency")))
                                currency = jsonObject1.getString("currency");
                                transactionLogger.error("currency ---"+currency);
                            if (functions.isValueNull(jsonObject1.getString("amount")))
                                amount_response = jsonObject1.getString("amount");
                                transactionLogger.error("amount_response ---"+amount_response); // amint without decimal point
                            String last2=amount_response.substring(amount_response.length()-2,amount_response.length());
                            String first=amount_response.substring(0,amount_response.length()-2);
                            amount_response=first+"."+last2; // amount with decimal point
                        }
                    }else if(!jsonObject.isNull("purchase") && jsonObject.has("purchase"))
                    {
                        JSONObject jsonObject1 = jsonObject.getJSONObject("purchase");
                        if (jsonObject1!=null)
                        {
                            if (functions.isValueNull(jsonObject1.getString("currency")))
                                currency = jsonObject1.getString("currency");
                            if(!jsonObject1.isNull("products") && functions.isValueNull(jsonObject1.getString("products")))
                            {
                                JSONArray products=jsonObject1.getJSONArray("products");
                                if(products != null && products.length()>0)
                                {
                                    if(products.getJSONObject(0).has("price"))
                                    {
                                        amount_response = products.getJSONObject(0).getString("price");
                                        int amt=Integer.parseInt(amount_response);
                                        double dAmt=amt/100;
                                        amount_response=String.format("%.2f",dAmt);
                                    }
                                }
                            }
                        }
                    }
                    if (jsonObject.has("id"))
                    {
                        payment_id=jsonObject.getString("id");
                        transactionLogger.error("payment_id ---"+payment_id);
                    }
                    if (jsonObject.has("status"))
                    {
                        statusFromInquiry=jsonObject.getString("status");
                        transactionLogger.error("statusFromInquiry ---"+statusFromInquiry);
                    }
                    if (jsonObject.has("created_on"))
                    {
                        timestamp=jsonObject.getString("created_on");
                        long timestamp1=Long.parseLong(timestamp)*1000;
                        timestamp=dateFormat.format(new Timestamp(timestamp1));
                        transactionLogger.error("payment_id ---"+timestamp);

                    }


                    commResponseVO.setMerchantId(brand_id);
                    commResponseVO.setAuthCode("-");
                    if (functions.isValueNull(transactionId))
                        commResponseVO.setTransactionId(transactionId);
                    if (functions.isValueNull(statusFromInquiry))
                        commResponseVO.setTransactionType(statusFromInquiry);
                        commResponseVO.setTransactionStatus(statusFromInquiry);
                        commResponseVO.setDescription(statusFromInquiry);
                    if (functions.isValueNull(amount_response))
                        commResponseVO.setAmount(amount_response);
                    if (functions.isValueNull(currency))
                        commResponseVO.setCurrency(currency);
                    commResponseVO.setResponseTime("");
                    commResponseVO.setBankTransactionDate(timestamp);
                }
            }

        }

        catch (PZTechnicalViolationException e)
        {
            transactionLogger.error("PZTechnicalViolationException-----", e);
        }
        catch (JSONException e)
        {
            transactionLogger.error("JSONException-----", e);
        }

        return commResponseVO;
    }

    public GenericResponseVO process3DSaleConfirmation(String PaRes, String MD,String callBack,CommRequestVO commRequestVO)
    {
        transactionLogger.error("inside process3DAuthConfirmation ----- ");
        Comm3DResponseVO commResponseVO = new Comm3DResponseVO();
        Functions functions = new Functions();
        String status="";

        try
        {
            if(functions.isValueNull(MD) && MD.contains(":"))
                MD=MD.split(":")[0];
            String  request = "PaRes="+ URLEncoder.encode(PaRes, "UTF-8")+
                    "&MD="+MD;

            transactionLogger.error("process3DSaleConfirmation request ---- " + request);
            String response = XPateUtils.doHttpPostConnection(callBack, request);
            transactionLogger.error("process3DSaleConfirmation response ---- " + response);

            if (response != null)
            {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("status"))
                {
                    status=jsonObject.getString("status");
                }
                if (functions.isValueNull(status))
                {
                    if (status.equalsIgnoreCase("executed") || status.equalsIgnoreCase("authorized"))
                    {
                        commResponseVO.setStatus("success");
                    }
                    else
                    {
                        commResponseVO.setStatus("fail");
                    }
                    commResponseVO.setRemark(status);
                    commResponseVO.setDescription(status);
                }
            }
            else
            {
                commResponseVO.setStatus("fail");
                commResponseVO.setRemark("Sys:Transaction Fail No Response");
                commResponseVO.setDescription("Sys:Transaction Fail No Response");
            }
        }
        catch (Exception e)
        {
            transactionLogger.error("Exception-----", e);
        }
        return commResponseVO;
    }

    @Override
    public String getMaxWaitDays()
    {
        return null;
    }
}

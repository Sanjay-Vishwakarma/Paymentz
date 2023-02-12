package com.payment.nexi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONObject;

import java.security.MessageDigest;

/**
 * Created by Admin on 5/18/2019.
 */
public class NexiSSLFirstPayment
{
    public static void main(String[] args)
    {
        //System.out.println("Inside main");
        String recurringUrl = "https://int-ecommerce.nexi.it/ecomm/api/paga/pagaSSL";

        /*String apikey = "ALIAS_WEB_00015678";
        String macKey = "Z189ABW25IK60YTRG43XZ35AX55FX0Z2";*/

        String apikey = "ALIAS_RICO_00015679";
        String macKey = "X5IJ6ZVB636C9JIJSO4LQ4DIKYZUZER4";
        String numeroContratto="125429";
        String pan = "4000000000000002";
        String scadenza = "203012";
        String cvv = "123";
        String importo = "500";
        String divisa = "978";
        String codiceTransazione = "125429";
        String codiceGruppo = "GRP_28010";
        long timeStamp = System.currentTimeMillis();
        String stringaMac = "apiKey=" + apikey + "codiceTransazione=" + codiceTransazione + "pan=" + pan + "scadenza=" + scadenza + "cvv=" + cvv + "importo=" + importo + "divisa=" + divisa + "timeStamp=" + timeStamp + macKey;

        //System.out.println("mac ---" + stringaMac);
        String macCalculated = "";

        try
        {
            macCalculated = hashMac(stringaMac);
            //System.out.println("macCalculated ---" + macCalculated);

            JSONObject json = new JSONObject();
            JSONObject responseObj = null;
            json.put("apiKey", apikey);
            json.put("codiceTransazione", codiceTransazione);//trackingid
            json.put("importo", importo);//amount
            json.put("divisa", divisa);//Currency code
            json.put("pan", pan);//card number
            json.put("scadenza", scadenza);//expiry Date
            json.put("timeStamp", String.valueOf(timeStamp));
            json.put("mac", macCalculated);


            //System.out.println("Request ---"+json.toString());
            HttpPost request=null;
            HttpResponse response=null;
            HttpEntity entity=null;
            String responseString=null;
            StringEntity params=null;


            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            request = new HttpPost(recurringUrl);
            params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            response = httpClient.execute(request);
            entity = response.getEntity();
            responseString = EntityUtils.toString(entity, "UTF-8");
            responseObj = new JSONObject(responseString);
            //System.out.print("Responce ---"+responseObj.toString());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /*public static void main(String[] args)
    {
        System.out.println("Inside main");
        String testURL = "https://int-ecommerce.nexi.it/ecomm/api/recurring/creaNonceVerificaCarta";
        String liveURL = "https://ecommerce.nexi.it";
        String recurringUrl1 = "https://int-ecommerce.nexi.it/ecomm/api/recurring/creaNonceVerificaCarta";
        String recurringUrl = "https://int-ecommerce.nexi.it/ecomm/api/recurring/verificaCarta3DS";
        //String recurringUrl = "https://int-ecommerce.nexi.it/ecomm/api/recurring/creaNonceRico3DS";

        String apikey = "ALIAS_WEB_00015678";
        String macKey = "Z189ABW25IK60YTRG43XZ35AX55FX0Z2";
        String numeroContratto="86328";
        String pan = "4539970000000006";
        String scadenza = "202012";
        String cvv = "123";
        String importo = "500";
        String divisa = "978";
        String codiceTransazione = "125429";
        String codiceGruppo = "GRP_28010";
        String urlRisposta = "http://localhost:8081/transaction/CommonBackEndServlet?trackingId=" + codiceTransazione;
        // String urlRisposta = "http://localhost:8081/transaction/CommonBackEndServlet?trackingId=" + codiceTransazione;//"http://localhost:8081/transaction/CommonBackEndServlet?trackingId=" + codiceTransazione;
        long timeStamp = System.currentTimeMillis();
        String stringaMac="apiKey="+apikey+"pan="+pan+"scadenza="+scadenza+"cvv="+cvv+"timeStamp="+timeStamp+macKey;
        //String stringaMac="apiKey="+apikey+"numeroContratto="+numeroContratto+"codiceTransazione="+codiceTransazione+"importo="+importo+"codiceGruppo="+codiceGruppo+"pan="+pan+"timeStamp="+timeStamp+macKey;

        System.out.println("mac ---" + stringaMac);
        String macCalculated = "";

        try
        {
            macCalculated = hashMac(stringaMac);
            System.out.println("macCalculated ---" + macCalculated);

            JSONObject json = new JSONObject();
            JSONObject json1 = new JSONObject();
            JSONObject json2 = new JSONObject();
            JSONObject responseObj = null;

            json.put("apiKey", apikey);
            //json.put("numeroContratto", numeroContratto);
            json.put("pan", pan);
            //json.put("codiceTransazione", codiceTransazione);
            //json.put("importo", importo);
            json.put("scadenza", scadenza);
            json.put("cvv", cvv);
            json.put("urlRisposta", urlRisposta);
            //json.put("codiceGruppo", codiceGruppo);
            json.put("timeStamp", String.valueOf(timeStamp));
            json.put("mac", macCalculated);


            System.out.println("Request ---"+json.toString());
            HttpPost request=null;
            HttpResponse response=null;
            HttpEntity entity=null;
            String responseString=null;
            StringEntity params=null;


           CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            *//*request = new HttpPost(recurringUrl);
            params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            response = httpClient.execute(request);
            entity = response.getEntity();
            responseString = EntityUtils.toString(entity, "UTF-8");
            responseObj = new JSONObject(responseString);
            System.out.print("Responce ---"+responseObj.toString());*//*

            String esito="OK";
            String idOperazione="1018346";
            String xpayNonce="98349546-b969-3481-9e1a-8b9b7bc364c9";
            String timeStamp1="1559312095231";
            String mac="aa08f3a51ab2d24dbf475e52b871750e46ef4a0f";

            json1.put("esito",esito);
            json1.put("idOperazione",idOperazione);
            json1.put("xpayNonce",xpayNonce);
            json1.put("timeStamp",timeStamp);
            json1.put("mac",mac);
            String macCalculated1="";
            String stringaMac2="esito=" +esito+ "idOperazione="+idOperazione + "xpayNonce="+ xpayNonce + "timeStamp=" + timeStamp1 +macKey;
            macCalculated1=hashMac(stringaMac2);
            if(!macCalculated1.equalsIgnoreCase(mac))
            {
                System.out.println("Invalid Mac :"+macCalculated1);
            }

            String requestUrl="https://int-ecommerce.nexi.it/ecomm/api/paga/paga3DS";
            String codTrans="125420";
            String importo1 = "5000"; // 5000 = 50,00 EURO (indicare la cifra in centesimi)
            String divisa1 = "978"; // divisa 978 indica EUR
            long timeStamp3 = System.currentTimeMillis();
            String mac1="apiKey=" +apikey +"xpayNonce=" +xpayNonce + "timeStamp=" +timeStamp3+ macKey;
            mac1=hashMac(mac1);
            json2.put("apiKey",apikey);
            json2.put("xpayNonce",codTrans);
            json2.put("codiceGruppo","GruppoTest");
            json2.put("timeStamp",timeStamp3);
            json2.put("mac",mac1);
            System.out.println("Request ---"+json2.toString());
            httpClient = HttpClientBuilder.create().build();
            request = new HttpPost(requestUrl);
            params = new StringEntity(json2.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            response = httpClient.execute(request);
            entity = response.getEntity();
            responseString = EntityUtils.toString(entity, "UTF-8");

            // Parametri di ritorno
            responseObj = new JSONObject(responseString);

            System.out.print("Responce 1---"+responseObj.toString());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/
    public static String hashMac(String stringaMac) throws Exception
    {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] in = digest.digest(stringaMac.getBytes());
        // System.out.println("in"+in);

        final StringBuilder builder = new StringBuilder();
        // System.out.println("builder before ---"+builder);
        for (byte b : in)
        {
            builder.append(String.format("%02x", b)); // converting to hexa decimal
            // System.out.println("builder after ---"+builder);
        }
        return builder.toString();
    }
}

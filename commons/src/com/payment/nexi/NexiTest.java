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
public class NexiTest
{
    public static void main(String[] args)
    {
        //System.out.println("Inside main");
        // Sample Code - https://github.com/NexiPayments/XPay/tree/master/web-mobile/server-to-server

        // String testURL = "https://ecommerce.nexi.it/ecomm/ecomm/DispatcherServlet";
        String testURL = "https://int-ecommerce.nexi.it/ecomm/api/recurring/primoPagamentoSSL";
        //"https://int-ecommerce.nexi.it/ecomm/api/paga/autenticazione3DS";
        //  ecomm/api/paga/autenticazione3DS https://ecommerce.nexi.it/ecomm/ecomm/DispatcherServlet

        String liveURL = "https://ecommerce.nexi.it";

        String apikey = "ALIAS_RICO_00015679";
        String macKey = "X5IJ6ZVB636C9JIJSO4LQ4DIKYZUZER4";
        String pan = "4539970000000006";
        String scadenza = "202012";
        String cvv = "123";
        String importo = "100";
        String divisa = "978";
        String codiceTransazione = "12571";
        String codiceGruppo = "GRP_28010";
        // String urlRisposta = "http://localhost:8081/transaction/CommonBackEndServlet?trackingId=" + codiceTransazione;
        /*String urlRisposta = "";//"http://localhost:8081/transaction/CommonBackEndServlet?trackingId=" + codiceTransazione;
        long timeStamp = System.currentTimeMillis();
        String stringaMac = "apiKey="+apikey+"codiceTransazione="+codiceTransazione+"pan="+pan+"scadenza="+scadenza+"cvv="+cvv+"importo="+importo+"divisa="+divisa+"timeStamp="+timeStamp+macKey;

        System.out.println("mac ---" + stringaMac);
        String macCalculated = "";

        try
        {
            macCalculated = hashMac(stringaMac);
            System.out.println("macCalculated ---" + macCalculated);

            JSONObject json = new JSONObject();

            json.put("apiKey", apikey);
            json.put("codiceTransazione", codiceTransazione);
            json.put("importo", importo);
            json.put("divisa", divisa);
            json.put("pan", pan);
            json.put("scadenza", scadenza);
            //json.put("cvv", cvv);
            // json.put("urlRisposta", urlRisposta);
            json.put("timeStamp", String.valueOf(timeStamp));
            json.put("mac", macCalculated);

            System.out.println("Request ---"+json.toString());


            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(testURL);
            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

            // Parametri di ritorno
            JSONObject responseObj = new JSONObject(responseString);

            System.out.print("Responce ---"+responseObj.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
        String urlRisposta = "";//"http://localhost:8081/transaction/CommonBackEndServlet?trackingId=" + codiceTransazione;
        long timeStamp = System.currentTimeMillis();
        String stringaMac = "apiKey="+apikey+"numeroContratto="+codiceTransazione+"codiceTransazione="+codiceTransazione+"importo="+importo+"divisa="+divisa+"pan="+pan+"cvv="+cvv+"scadenza="+scadenza+"timeStamp="+timeStamp+macKey;

        //System.out.println("mac ---" + stringaMac);
        String macCalculated = "";

        try
        {
            macCalculated = hashMac(stringaMac);
            //System.out.println("macCalculated ---" + macCalculated);

            JSONObject json = new JSONObject();

            json.put("apiKey", apikey);
            json.put("codiceTransazione", codiceTransazione);
            json.put("importo", importo);
            json.put("divisa", divisa);
            json.put("pan", pan);
            json.put("scadenza", scadenza);
            json.put("numeroContratto", codiceTransazione);
            json.put("codiceGruppo", codiceGruppo);
            json.put("cvv", cvv);
            // json.put("urlRisposta", urlRisposta);
            json.put("timeStamp", String.valueOf(timeStamp));
            json.put("mac", macCalculated);

            //System.out.println("Request ---"+json.toString());


            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(testURL);
            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");

            // Parametri di ritorno
            JSONObject responseObj = new JSONObject(responseString);

            //System.out.print("Responce ---"+responseObj.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

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

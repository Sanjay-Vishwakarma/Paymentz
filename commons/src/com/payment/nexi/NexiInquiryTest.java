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
 * Created by Admin on 5/22/2019.
 */
public class NexiInquiryTest
{
    public static void main(String args[])
    {
        String testURL = "https://int-ecommerce.nexi.it/ecomm/api/bo/situazioneOrdine";
        String apikey = "ALIAS_RICO_00015679";
        String macKey = "X5IJ6ZVB636C9JIJSO4LQ4DIKYZUZER4";
        //String codiceTransazione = "125416";
        String codiceTransazione = "102160";
        // String urlRisposta = "http://localhost:8081/transaction/CommonBackEndServlet?trackingId=" + codiceTransazione;
        String urlRisposta = "";//"http://localhost:8081/transaction/CommonBackEndServlet?trackingId=" + codiceTransazione;
        long timeStamp = System.currentTimeMillis();
        String stringaMac = "apiKey=" + apikey + "codiceTransazione=" + codiceTransazione + "timeStamp=" + timeStamp + macKey;

        //System.out.println("mac ---" + stringaMac);
        String macCalculated = "";

        try
        {
            macCalculated = hashMac(stringaMac);
            //System.out.println("macCalculated ---" + macCalculated);

            JSONObject json = new JSONObject();

            json.put("apiKey", apikey);
            json.put("codiceTransazione", codiceTransazione);
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

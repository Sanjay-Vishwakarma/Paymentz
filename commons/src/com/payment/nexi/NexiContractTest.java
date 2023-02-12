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
public class NexiContractTest
{
    public static void main(String[] args)
    {
        //System.out.println("Inside main");
        String recurringUrl = "https://int-ecommerce.nexi.it/ecomm/api/contratti/dettagliContratto";

        String apikey = "ALIAS_RICO_00015679";
        String macKey = "X5IJ6ZVB636C9JIJSO4LQ4DIKYZUZER4";
        String numeroContratto="86330";
        String pan = "4539970000000006";
        String scadenza = "202012";
        String cvv = "123";
        String importo = "500";
        String divisa = "978";
        String codiceTransazione = "125429";
        long timeStamp = System.currentTimeMillis();
        String dataRegistrazioneDa="31/5/2019 00:00:00";
        String dataRegistrazioneA="1/6/2019 00:00:00";
        String stringaMac="apiKey="+apikey+"numeroContratto="+numeroContratto+"codiceFiscale="+"dataRegistrazioneDa="+dataRegistrazioneDa+"dataRegistrazioneA="+dataRegistrazioneA+"timeStamp="+timeStamp+macKey;
        //System.out.println("mac ---" + stringaMac);
        String macCalculated = "";

        try
        {
            macCalculated = hashMac(stringaMac);
            System.out.println("macCalculated ---" + macCalculated);

            JSONObject json = new JSONObject();
            JSONObject responseObj = null;

            json.put("apiKey", apikey);
            json.put("numeroContratto", numeroContratto);
            json.put("dataRegistrazioneDa", dataRegistrazioneDa);
            json.put("dataRegistrazioneA", dataRegistrazioneA);
            json.put("timeStamp", String.valueOf(timeStamp));
            json.put("mac", macCalculated);


            System.out.println("Request ---"+json.toString());
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

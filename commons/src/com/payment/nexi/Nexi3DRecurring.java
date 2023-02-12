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
 * Created by Admin on 6/19/2019.
 */
public class Nexi3DRecurring
{
    public static void main(String[] args)
    {
        String threeDUrl = "https://ecommerce.nexi.it/ecomm/api/recurring/creaNonceRico3DS";

        String liveURL = "https://ecommerce.nexi.it";
        //String threeDUrl = "https://int-ecommerce.nexi.it/ecomm/api/paga/autenticazione3DS";
        //String threeDUrl = "https://int-ecommerce.nexi.it/ecomm/api/recurring/creaNoncePrimo3DS";

       // String apikey = "ALIAS_WEB_00015678";
        //String apikey = "ALIAS_RICO_00015679";
        String apikey = "payment_3498145";
        //String macKey = "Z189ABW25IK60YTRG43XZ35AX55FX0Z2";
       //String macKey = "X5IJ6ZVB636C9JIJSO4LQ4DIKYZUZER4";
       String macKey = "8P49LG4C38p4SR6WA4S161MPO65WL71E8Z74qo72";
        String pan = "4539970000000006";
        String scadenza = "203012";
        String cvv = "123";
        String importo = "500";
        String divisa = "978";
        String codiceTransazione = "125428";
        // String urlRisposta = "http://localhost:8081/transaction/CommonBackEndServlet?trackingId=" + codiceTransazione;
        String urlRisposta = "http://localhost:8081/transaction/CommonBackEndServlet?trackingId=" + codiceTransazione;//"http://localhost:8081/transaction/CommonBackEndServlet?trackingId=" + codiceTransazione;
        long timeStamp = System.currentTimeMillis();
        String numeroContratto="86595";
        String codiceGruppo="VELOCE";
        String stringaMac="apiKey="+apikey+"numeroContratto="+numeroContratto+"codiceTransazione="+codiceTransazione+"importo="+importo+"divisa="+divisa+"codiceGruppo="+codiceGruppo+"timeStamp="+timeStamp+macKey;

        System.out.println("mac ---" + stringaMac);
        String macCalculated = "";
        String macCalculated1 = "";

        try
        {
            macCalculated = hashMac(stringaMac);
            System.out.println("macCalculated ---" + macCalculated);

            JSONObject json = new JSONObject();
            JSONObject json1 = new JSONObject();
            JSONObject json2 = new JSONObject();
            JSONObject responseObj = null;

            json.put("apiKey", apikey);
            json.put("numeroContratto", numeroContratto);
            json.put("codiceTransazione", codiceTransazione);
            json.put("importo", importo);
            json.put("divisa", divisa);
            //json.put("pan", pan);
            //json.put("scadenza", scadenza);
            json.put("codiceGruppo", codiceGruppo);
            //json.put("cvv", cvv);
            json.put("urlRisposta", urlRisposta);
            // json.put("urlRisposta", urlRisposta);
            json.put("timeStamp", String.valueOf(timeStamp));
            json.put("mac", macCalculated);

            System.out.println("Request ---"+json.toString());
            HttpPost request=null;
            HttpResponse response=null;
            HttpEntity entity=null;
            String responseString=null;
            StringEntity params=null;


            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            request = new HttpPost(threeDUrl);
            params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            response = httpClient.execute(request);
            entity = response.getEntity();
            responseString = EntityUtils.toString(entity, "UTF-8");
            responseObj = new JSONObject(responseString);
            System.out.print("Responce ---"+responseObj.toString());
            // Parametri di ritorno



            /*if(!responseObj.getString("esito").equals("OK"))
            {
                System.out.println("Esito 3D-Secure:" + responseObj.getJSONObject("esito") + "-" + responseObj.getJSONObject("messaggio"));
            }*/
            /*String esito="OK";
            String idOperazione="1013196";
            String xpayNonce="492b3443-9462-3dee-a911-4e7b0a6c640e";
            String timeStamp1="1558517956483";
            String mac="448783b4f55c69774fe95da8edff1e5035443d18";

            json1.put("esito",esito);
            json1.put("idOperazione",idOperazione);
            json1.put("xpayNonce",xpayNonce);
            json1.put("timeStamp",timeStamp);
            json1.put("mac",mac);

            String stringaMac2="esito=" +esito+ "idOperazione="+idOperazione + "xpayNonce="+ xpayNonce + "timeStamp=" + timeStamp1 +macKey;
            macCalculated1=hashMac(stringaMac2);
            if(!macCalculated1.equalsIgnoreCase(mac))
            {
                System.out.println("Invalid Mac :"+macCalculated1);
            }

            String requestUrl="https://int-ecommerce.nexi.it/ecomm/api/recurring/pagamentoRicorrente3D";
            String codTrans="125420";
            String importo1 = "5000"; // 5000 = 50,00 EURO (indicare la cifra in centesimi)
            String divisa1 = "978"; // divisa 978 indica EUR
            long timeStamp3 = System.currentTimeMillis();
            String mac1="apiKey=" +apikey + "codiceTransazione="+ codTrans  + "importo=" +importo1 +  "divisa=" + divisa1 + "xpayNonce=" +xpayNonce + "timeStamp=" +timeStamp3+ macKey;
             mac1=hashMac(mac1);
            json2.put("apiKey",apikey);
            json2.put("codiceTransazione",codTrans);
            json2.put("importo",importo1);
            json2.put("divisa",divisa1);
            json2.put("xpayNonce",xpayNonce);
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

            System.out.print("Responce 1---"+responseObj.toString());*/

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

package practice;

import com.directi.pg.*;
import com.google.gson.Gson;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * Created by Admin on 6/15/2020.
 */
public class Practice6
{
    public static void main(String[] args)
    {
        sale();
    }
    public static void sale()
    {
        try
        {
            String secretKey = "qwertyuiasdfghj12345t6y";
            String saleurl = "https://api.ecommpay.com/v2/payment/card/sale";
            int payment_type = 1;
            int amount = 500;
            int id = 129;
            int amount1 = 1;
            int rate = 1000;
            boolean fire_safety_act_indicator = true;
            int price = 1000;
            int tax = 8;
            int year = 2023;
            int month = 2;
            String returnUrl="http://localhost:8081/transaction/Common3DFrontEndServlet";
            String callbackUrl="https://staging.paymentz.com/transaction/CommonBackEndServlet";
            TreeMap<String,TreeMap<String,String>> requestHashMap=new TreeMap<>();

            TreeMap<String,String> general=new TreeMap<>();
            general.put("project_id","2345");
            general.put("payment_id","231");

            TreeMap<String,String> card=new TreeMap<>();
            card.put("pan","4242424242424242");
            card.put("year",String.valueOf(year));
            card.put("month",String.valueOf(month));
            card.put("card_holder","abc");

            TreeMap<String,String> customer=new TreeMap<>();
            customer.put("id","254");
            customer.put("country","IN");
            customer.put("city","Mumbai");
            customer.put("state","MH");
            customer.put("phone","08987786576");
            customer.put("day_of_birth","22-10-1993");
            customer.put("first_name","abc");
            customer.put("last_name","abc");
            customer.put("email","abc@gmail.com");
            customer.put("ip_address","192.34.43.1");
            customer.put("district","Thane");
            customer.put("street","abcdef");
            customer.put("address","abcdef");

            TreeMap<String,String> avs_data= new TreeMap<>();
            avs_data.put("avs_post_code","1");
            avs_data.put("avs_street_address","2");

            TreeMap<String,String> payment= new TreeMap<>();
            payment.put("amount",String.valueOf(amount));
            payment.put("currency","EUR 421");

            TreeMap<String,String>positions= new TreeMap<>();
            positions.put("price",String.valueOf(price));
            positions.put("position_description","1");
            positions.put("tax",String.valueOf(tax));

            TreeMap<String,String>payments= new TreeMap<>();
            payments.put("payment_type",String.valueOf(payment_type));
            payments.put("amount",String.valueOf(amount));

            TreeMap<String,String>interface_type=new TreeMap<>();
            interface_type.put("id",String.valueOf(id));

            // TreeMap<String,String>receipt_data=new TreeMap<>();


            TreeMap<String,String>room=new TreeMap<>();
            room.put("rate",String.valueOf(rate));
            room.put("fire_safety_act_indicator",String.valueOf(fire_safety_act_indicator));

            TreeMap<String,String>acs_return_url= new TreeMap<>();
            acs_return_url.put("return_url",returnUrl);
            acs_return_url.put("3ds_notification_url",callbackUrl);

            requestHashMap.put("general",general);
            requestHashMap.put("card",card);
            requestHashMap.put("customer",customer);
            requestHashMap.put("avs_data",avs_data);
            requestHashMap.put("payment",payment);
            requestHashMap.put("positions",positions);
            requestHashMap.put("payments",payments);
            requestHashMap.put("interface_type",interface_type);
            requestHashMap.put("room",room);
            requestHashMap.put("acs_return_url",acs_return_url);

            Gson gson=new Gson();
            String request=gson.toJson(requestHashMap);
            String signature=calculateSignature(gson.toJson(requestHashMap),secretKey);
            System.out.println("signature-->"+signature);
            general.put("signature",signature);
            requestHashMap.put("general",general);
            System.out.println("Request-->" + request);
            String response = doPostHTTPSURLConnectionClient(saleurl, request);
            System.out.println("response-->" + response);

        }

        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }



    public static String getSignature(List list,String privateKey)
    {
        Collections.sort(list);

        StringBuffer sb= new StringBuffer();
        for(int i=0;i<list.size();i++)
        {
            sb.append(list.get(i));
            if(i<list.size()-1)
            {
                sb.append("|");
            }
        }

        System.out.println("sb ===" + sb.toString());
        String signature = HmacSHA512(sb.toString(), privateKey);
        System.out.println("signature ="+signature);

        return signature;
    }

    public static String calculateSignature(TreeMap<String,TreeMap<String,String>> requestHashMap,String secretKey)
    {
        Functions functions=new Functions();
        String hash="";
        StringBuffer requestString=new StringBuffer();
        Set<String> keySet=requestHashMap.keySet();
        int i=0;
        int lastIndex=keySet.size()-1;
        for (String key:keySet)
        {
            TreeMap<String,String> innerTreeMap= requestHashMap.get(key);

            Set<String> innerKeySet=innerTreeMap.keySet();
            for (String innerKey:innerKeySet)
            {
                String value=innerTreeMap.get(innerKey);
                if (lastIndex == i)
                {
                    if (functions.isValueNull(value))
                        requestString.append(key+":"+innerKey + ":" + value);
                    else
                        requestString.append(key+":"+innerKey + ":" + "");
                }
                else
                {
                    if (functions.isValueNull(value))
                        requestString.append(key+":"+innerKey + ":" + value+";");
                    else
                        requestString.append(key+":"+innerKey + ":" + ";");
                }
            }
            i++;
        }
        System.out.println("requestString-->"+requestString);
        hash=HmacSHA512(requestString.toString(),secretKey);

        return hash;
    }
    public static String HmacSHA512(String message,String key)
    {
        MessageDigest md = null;
        try
        {
            Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512_HMAC.init(secret_key);


            byte raw[] = sha512_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));


            String hex = com.directi.pg.Base64.encode(raw);
            System.out.println("=====hex2 String=="+hex);
            return hex; //step 6
        }
        catch(Exception e)
        {
            System.out.println("exception "+ e);
            return null;
        }

    }

    public static String doPostHTTPSURLConnectionClient(String strURL,String req) throws PZTechnicalViolationException

    {
        String result = "";
        PostMethod post = new PostMethod(strURL);
        try
        {    HttpClient httpClient = new HttpClient();

            post.addRequestHeader("Content-Type", "application/json");
            post.setRequestBody(req);
            httpClient.executeMethod(post);

            String response = new String(post.getResponseBody());
            result= response;
        }
        catch (HttpException he){
            he.printStackTrace();
        }
        catch (IOException io){
            io.printStackTrace();
        }
        finally
        {
            post.releaseConnection();
        }
        return result;
    }

    public static String calculateSignature(String request,String secretKey) throws JSONException
    {
        Functions functions=new Functions();
        String hash="";
        StringBuffer requestString=new StringBuffer();
        JSONObject jsonObject=new JSONObject(request);
        Iterator iterator=jsonObject.keys();
        while (iterator.hasNext())
        {
            String key = (String) iterator.next();
            Object o = jsonObject.get(key);
            if (o instanceof JSONObject)
            {
                JSONObject innerJson1= (JSONObject) jsonObject.get(key);
                Iterator i=innerJson1.keys();
                while (i.hasNext())
                {
                    String key2 = (String) i.next();
                    Object o2 = innerJson1.get(key2);
                    if (o2 instanceof JSONObject)
                    {
                        JSONObject innerJson2= (JSONObject) innerJson1.get(key);
                        Iterator i2=innerJson2.keys();
                        while (i2.hasNext())
                        {
                            String key3 = (String) i2.next();
                            Object o3 = innerJson2.get(key3);
                            if (o2 instanceof JSONObject)
                            {
                                JSONObject innerJson3= (JSONObject) innerJson2.get(key);
                                Iterator i3=innerJson3.keys();
                                while (i3.hasNext())
                                {
                                    String key4 = (String) i3.next();
                                    String value = (String) innerJson2.get(key4);
                                    requestString.append(key+":"+key2+":"+key3+":"+key4+":"+value+";");
                                }

                            }
                            else
                            {
                                String value= (String) innerJson2.get(key3);
                                requestString.append(key+":"+key2+":"+key3+":"+value+";");
                            }
                        }

                    }
                    else
                    {
                        String value= (String) innerJson1.get(key2);
                        requestString.append(key+":"+key2+":"+value+";");
                    }
                }

            }
            else
            {
                String value= (String) jsonObject.get(key);
                requestString.append(key+":"+value+";");
            }
        }
        System.out.println("requestString-->"+requestString);
        hash=HmacSHA512(requestString.toString(),secretKey);

        return hash;
    }

}

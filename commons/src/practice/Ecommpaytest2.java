package practice;
    import com.directi.pg.Functions;
    import com.google.gson.Gson;
    import com.payment.Ecommpay.EcommpayUtils;
    import com.payment.exceptionHandler.PZTechnicalViolationException;
    import org.codehaus.jettison.json.JSONException;
    import com.payment.exceptionHandler.PZTechnicalViolationException;
    import com.transaction.vo.restVO.RequestVO.Date;
    import org.apache.commons.httpclient.HttpClient;
    import org.apache.commons.httpclient.HttpException;
    import org.apache.commons.httpclient.methods.PostMethod;
    import org.codehaus.jettison.json.JSONException;
    import org.codehaus.jettison.json.JSONObject;
    import sun.reflect.generics.tree.Tree;

    import javax.crypto.Cipher;
    import javax.crypto.Mac;
    import javax.crypto.SecretKey;
    import javax.crypto.spec.SecretKeySpec;
    import java.io.IOException;
    import java.nio.charset.StandardCharsets;
    import java.security.MessageDigest;
    import java.text.SimpleDateFormat;
    import java.util.*;
    import com.directi.pg.Base64;

    import static com.payment.Ecommpay.EcommpayUtils.calculateSignature;

public class Ecommpaytest2
    {
        public static void main(String[] args)
        {
       //  recurring3();
      //recurring2();
        //recurring1();
          recurring();
          //sale();
            //refund();

        }
        public static void recurring3()//MO/TO payment processing by using Payment Page
        {
            try
            {   EcommpayUtils ecommpayUtils = new EcommpayUtils();

                String secretKey = "55e1a71afa4afa809528bf1b767897f6344af784af4f802a7ee171e5127cbbfe1ed1f5cdab65cd1f58a4d3b73e7e2af3f1326210f70f2514355768ca6f31ed79";
                String recurringurl = "https://api.accentpay.com/v2/payment/card/recurring";
                int project_id=15561;
                long id = Long.parseLong("15941119476826");
                String trackingId="174372";
                int payment_amount =123;
                int moto_type=2;


                String returnUrl="http://localhost:8081/transaction/Common3DFrontEndServlet";
                String callbackUrl="https://staging.paymentz.com/transaction/CommonBackEndServlet";
                TreeMap<String,Object> requestHashMap=new TreeMap<>();

                TreeMap<String,Object> recurring = new TreeMap<>();
                recurring.put("payment_amount",payment_amount);
                recurring.put("payment_currency","EUR");
                recurring.put("moto_type",moto_type);
                recurring.put("customer_id","user123");

                TreeMap<String,Object> customer=new TreeMap<>();
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

                TreeMap<String,Object> general=new TreeMap<>();
                general.put("project_id",project_id);
                general.put("payment_id",trackingId);

                TreeMap<String,Object>acs_return_url= new TreeMap<>();
                acs_return_url.put("return_url",returnUrl);
                acs_return_url.put("3ds_notification_url",callbackUrl);

                TreeMap<String,Object> payment= new TreeMap<>();
                payment.put("amount",payment_amount);
                payment.put("currency","EUR");

                requestHashMap.put("recurring_id",id);
                requestHashMap.put("payment",payment);
                requestHashMap.put("general",general);
                requestHashMap.put("acs_return_url",acs_return_url);
                requestHashMap.put("recurring",recurring);
                requestHashMap.put("customer",customer);
                requestHashMap.put("recurring_register",true);



                Gson gson = new Gson();
                String signature = ecommpayUtils.calculateSignature(gson.toJson(requestHashMap), secretKey);
                System.out.println("signature-->" + signature);
                general.put("signature", signature);
                requestHashMap.put("general", general);
                String request = gson.toJson(requestHashMap);
                String response = doPostHTTPSURLConnectionClient(recurringurl, request);
                System.out.println("response-->" + response);

                org.json.JSONObject myResponse = new org.json.JSONObject(response);
                String status = (String)myResponse.get("status");
                System.out.println("status-->"+status);
                String request_id = (String)myResponse.get("request_id");
                System.out.println("request_id-->"+request_id);
                project_id = (int)myResponse.get("project_id");
                System.out.println("project_id-->"+project_id);
                String payment_id = (String)myResponse.get("payment_id");
                System.out.println("payment_id-->"+payment_id);

            }
            catch (PZTechnicalViolationException e)
            {
                e.printStackTrace();
            }
            catch (org.json.JSONException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }


        public static void recurring2()//
        {
            try
            {   EcommpayUtils ecommpayUtils = new EcommpayUtils();

                String secretKey = "55e1a71afa4afa809528bf1b767897f6344af784af4f802a7ee171e5127cbbfe1ed1f5cdab65cd1f58a4d3b73e7e2af3f1326210f70f2514355768ca6f31ed79";
                String recurringurl = "https://api.accentpay.com/v2/payment/card/recurring";
                int project_id=15561;
                 long id = Long.parseLong("15941119476826");
                String trackingId="174372";
                int payment_amount =123;
                int customer_ssn=90;

                String returnUrl="http://localhost:8081/transaction/Common3DFrontEndServlet";
                String callbackUrl="https://staging.paymentz.com/transaction/CommonBackEndServlet";
                TreeMap<String,Object> requestHashMap=new TreeMap<>();

                TreeMap<String,Object> recurring = new TreeMap<>();
                recurring.put("payment_amount",payment_amount);
                recurring.put("customer_ssn",customer_ssn);
                recurring.put("billing_address","R");
                recurring.put("billing_city","D");
                recurring.put("billing_country","10:00:00");
                recurring.put("billing_postal","25-12-2020");
                recurring.put("billing_region","A2323");
                recurring.put("customer_address","R");
                recurring.put("customer_birthplace","D");
                recurring.put("customer_city","10:00:00");
                recurring.put("customer_country","25-12-2020");
                recurring.put("customer_day_of_birth","A2323");
                recurring.put("customer_email","R");
                recurring.put("customer_first_name","D");
                recurring.put("customer_id","10:00:00");
                recurring.put("customer_last_name","25-12-2020");
                recurring.put("customer_middle_name","A2323");
                recurring.put("customer_phone","R");
                recurring.put("customer_state","D");
                recurring.put("customer_zip","10:00:00");
                recurring.put("language_code","25-12-2020");
                recurring.put("mode","A2323");
                recurring.put("signature","R");
                recurring.put("payment_description","D");
                recurring.put("payment_id","10:00:00");

                TreeMap<String,Object> customer=new TreeMap<>();
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



                TreeMap<String,Object> general=new TreeMap<>();
                general.put("project_id",project_id);
                general.put("payment_id",trackingId);


                TreeMap<String,Object>acs_return_url= new TreeMap<>();
                acs_return_url.put("return_url",returnUrl);
                acs_return_url.put("3ds_notification_url",callbackUrl);

                TreeMap<String,Object> payment= new TreeMap<>();
                payment.put("amount",payment_amount);
                payment.put("currency","EUR");

                requestHashMap.put("recurring_id",id);
                requestHashMap.put("payment",payment);
                requestHashMap.put("general",general);
                requestHashMap.put("acs_return_url",acs_return_url);
                requestHashMap.put("recurring",recurring);
                requestHashMap.put("customer",customer);
                requestHashMap.put("recurring_register",true);



                Gson gson = new Gson();
                String signature = ecommpayUtils.calculateSignature(gson.toJson(requestHashMap), secretKey);
                System.out.println("signature-->" + signature);
                general.put("signature", signature);
                requestHashMap.put("general", general);
                String request = gson.toJson(requestHashMap);
                String response = doPostHTTPSURLConnectionClient(recurringurl, request);
                System.out.println("response-->" + response);

                org.json.JSONObject myResponse = new org.json.JSONObject(response);
                String status = (String)myResponse.get("status");
                System.out.println("status-->"+status);
                String request_id = (String)myResponse.get("request_id");
                System.out.println("request_id-->"+request_id);
                project_id = (int)myResponse.get("project_id");
                System.out.println("project_id-->"+project_id);
                String payment_id = (String)myResponse.get("payment_id");
                System.out.println("payment_id-->"+payment_id);

            }
            catch (PZTechnicalViolationException e)
            {
                e.printStackTrace();
            }
            catch (org.json.JSONException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

        public static void recurring1()//Parameters in the recurring parameter of JSON object
        {
            try
            {   EcommpayUtils ecommpayUtils = new EcommpayUtils();

                String secretKey = "55e1a71afa4afa809528bf1b767897f6344af784af4f802a7ee171e5127cbbfe1ed1f5cdab65cd1f58a4d3b73e7e2af3f1326210f70f2514355768ca6f31ed79";
                String recurringurl = "https://api.accentpay.com/v2/payment/card/recurring";
                int project_id=15561;
                long id = Long.parseLong("15941119476826");

                String trackingId="174372";
                int amount=123;
                int expiry_day=25;
                int expiry_month=12;
                int expiry_year=2020;
                int interval=3;

                String returnUrl="http://localhost:8081/transaction/Common3DFrontEndServlet";
                String callbackUrl="https://staging.paymentz.com/transaction/CommonBackEndServlet";
                TreeMap<String,Object> requestHashMap=new TreeMap<>();

                TreeMap<String,Object> recurring = new TreeMap<>();
                recurring.put("register",true);
                recurring.put("type","R");
                recurring.put("amount",amount);
                recurring.put("expiry_day",expiry_day);
                recurring.put("expiry_month",expiry_month);
                recurring.put("expiry_year",expiry_year);
                recurring.put("interval",interval);
                recurring.put("period","D");
                recurring.put("time","10:00:00");
                recurring.put("start_date","25-12-2020");
                recurring.put("scheduled_payment_id",trackingId);

                TreeMap<String,Object> payment= new TreeMap<>();
                payment.put("amount",amount);
                payment.put("currency","EUR");

                TreeMap<String,Object> general=new TreeMap<>();
                general.put("project_id",project_id);
                general.put("payment_id",trackingId);

                TreeMap<String,Object> customer=new TreeMap<>();
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



                TreeMap<String,Object>acs_return_url= new TreeMap<>();
                acs_return_url.put("return_url",returnUrl);
                acs_return_url.put("3ds_notification_url",callbackUrl);

                requestHashMap.put("recurring_id",id);
                requestHashMap.put("general",general);
                requestHashMap.put("acs_return_url",acs_return_url);
                requestHashMap.put("recurring",recurring);
                requestHashMap.put("customer",customer);
                requestHashMap.put("payment",payment);
                requestHashMap.put("recurring_register",true);

                Gson gson = new Gson();
                String signature = ecommpayUtils.calculateSignature(gson.toJson(requestHashMap), secretKey);
                System.out.println("signature-->" + signature);
                general.put("signature", signature);
                requestHashMap.put("general", general);
                String request = gson.toJson(requestHashMap);
                String response = doPostHTTPSURLConnectionClient(recurringurl, request);
                System.out.println("response-->" + response);

                org.json.JSONObject myResponse = new org.json.JSONObject(response);
                String status = (String)myResponse.get("status");
                System.out.println("status-->"+status);
                String request_id = (String)myResponse.get("request_id");
                System.out.println("request_id-->"+request_id);
                project_id = (int)myResponse.get("project_id");
                System.out.println("project_id-->"+project_id);
                String payment_id = (String)myResponse.get("payment_id");
                System.out.println("payment_id-->"+payment_id);

            }
            catch (PZTechnicalViolationException e)
            {
                e.printStackTrace();
            }
            catch (org.json.JSONException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

        public static void recurring()
        {
            try
            {   EcommpayUtils ecommpayUtils = new EcommpayUtils();

                String secretKey = "55e1a71afa4afa809528bf1b767897f6344af784af4f802a7ee171e5127cbbfe1ed1f5cdab65cd1f58a4d3b73e7e2af3f1326210f70f2514355768ca6f31ed79";
                String recurringurl = "https://api.accentpay.com/v2/payment/card/recurring";

                int amount = 500;

                int year = 2023;
                int month = 2;
                int project_id=15561;
                int id =174374; //174373 , 1112646842
                String trackingId="181497";
                String returnUrl="http://localhost:8081/transaction/Common3DFrontEndServlet";
                String callbackUrl="https://staging.paymentz.com/transaction/CommonBackEndServlet";
                TreeMap<String,Object> requestHashMap=new TreeMap<>();

                TreeMap<String,Object> general=new TreeMap<>();
                general.put("project_id",project_id);
                general.put("payment_id",trackingId);

                TreeMap<String,Object> card=new TreeMap<>();
                card.put("pan","4000000000000077");
                card.put("year",year);
                card.put("month",month);
                card.put("cvv","123");
                card.put("card_holder","abc");

                TreeMap<String,Object> customer=new TreeMap<>();
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


                TreeMap<String,Object> payment= new TreeMap<>();
                payment.put("amount",amount);
                payment.put("currency","EUR");


                TreeMap<String,Object>acs_return_url= new TreeMap<>();
                acs_return_url.put("return_url",returnUrl);
                acs_return_url.put("3ds_notification_url",callbackUrl);

                TreeMap<String,Object> recurring= new TreeMap<>();
                payment.put("id",id);

                requestHashMap.put("recurring_id",id);
                requestHashMap.put("recurring",recurring);
                requestHashMap.put("general",general);
                requestHashMap.put("acs_return_url",acs_return_url);
                requestHashMap.put("customer",customer);
                requestHashMap.put("payment",payment);

                Gson gson = new Gson();
                String signature = ecommpayUtils.calculateSignature(gson.toJson(requestHashMap), secretKey);
                System.out.println("signature-->" + signature);
                general.put("signature", signature);
                requestHashMap.put("general", general);
                String request = gson.toJson(requestHashMap);
                String response = doPostHTTPSURLConnectionClient(recurringurl, request);
                System.out.println("response-->" + response);

                org.json.JSONObject myResponse = new org.json.JSONObject(response);
                String status = (String)myResponse.get("status");
                System.out.println("status-->"+status);
                String request_id = (String)myResponse.get("request_id");
                System.out.println("request_id-->"+request_id);
                project_id = (int)myResponse.get("project_id");
                System.out.println("project_id-->"+project_id);
                String payment_id = (String)myResponse.get("payment_id");
                System.out.println("payment_id-->"+payment_id);

            }
            catch (PZTechnicalViolationException e)
            {
                e.printStackTrace();
            }
            catch (org.json.JSONException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }

        public static void sale()
        {
            try
            {   EcommpayUtils ecommpayUtils = new EcommpayUtils();

                String secretKey = "55e1a71afa4afa809528bf1b767897f6344af784af4f802a7ee171e5127cbbfe1ed1f5cdab65cd1f58a4d3b73e7e2af3f1326210f70f2514355768ca6f31ed79";
                String saleurl = "https://api.accentpay.com/v2/payment/card/sale";
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
                int project_id=15561;
                String returnUrl="http://localhost:8081/transaction/Common3DFrontEndServlet";
                String callbackUrl="https://staging.paymentz.com/transaction/CommonBackEndServlet";
              TreeMap<String,TreeMap<String,Object>> requestHashMap=new TreeMap<>();
                //TreeMap<String,Object> requestHashMap=new TreeMap<>();

                TreeMap<String,Object> general=new TreeMap<>();
                general.put("project_id",project_id);
                general.put("payment_id","2311");

                TreeMap<String,Object> card=new TreeMap<>();
                card.put("pan","4000000000000077");
                card.put("year",year);
                card.put("month",month);
                card.put("cvv","123");
                card.put("card_holder","abc");

                TreeMap<String,Object> customer=new TreeMap<>();
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

                TreeMap<String,Object> avs_data= new TreeMap<>();
                avs_data.put("avs_post_code","1");
                avs_data.put("avs_street_address","2");

                TreeMap<String,Object> payment= new TreeMap<>();
                payment.put("amount",amount);
                payment.put("currency","EUR");

                TreeMap<String,Object>positions= new TreeMap<>();
                positions.put("price",String.valueOf(price));
                positions.put("position_description","1");
                positions.put("tax",String.valueOf(tax));

                TreeMap<String,Object>payments= new TreeMap<>();
                payments.put("payment_type",String.valueOf(payment_type));
                payments.put("amount",String.valueOf(amount));

                TreeMap<String,Object>acs_return_url= new TreeMap<>();
                acs_return_url.put("return_url",returnUrl);
                acs_return_url.put("3ds_notification_url",callbackUrl);

                requestHashMap.put("general",general);
                requestHashMap.put("card",card);
                requestHashMap.put("customer",customer);
                requestHashMap.put("avs_data",avs_data);
                requestHashMap.put("payment",payment);
                requestHashMap.put("positions",positions);
                requestHashMap.put("payments",payments);
                requestHashMap.put("acs_return_url",acs_return_url);


                Gson gson=new Gson();
                String signature=ecommpayUtils.calculateSignature(gson.toJson(requestHashMap), secretKey);
                System.out.println("signature-->"+signature);
                general.put("signature",signature);
                requestHashMap.put("general",general);


                String request=gson.toJson(requestHashMap);

                System.out.println("Request-->" + request);
                String response = doPostHTTPSURLConnectionClient(saleurl, request);
                System.out.println("response-->" + response);

                org.json.JSONObject myResponse = new org.json.JSONObject(response);
                String status = (String)myResponse.get("status");
                System.out.println("status-->"+status);
                String request_id = (String)myResponse.get("request_id");
                System.out.println("request_id-->"+request_id);
                project_id = (int)myResponse.get("project_id");
                System.out.println("project_id-->"+project_id);
                String payment_id = (String)myResponse.get("payment_id");
                System.out.println("payment_id-->"+payment_id);

            }
            catch (PZTechnicalViolationException e)
            {
                e.printStackTrace();
            }
            catch (org.json.JSONException e)
            {
                e.printStackTrace();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }


     /*   public static void refund()
        {
            try{
            String secretKey = "qwertyuiasdfghj12345t6y";
            String refundurl = "https://api.accentpay.com/v2/payment/card/refund";
            int payment_type = 1;
            int id = 129;
            String amount ="1000";
            int rate = 1000;
            boolean fire_safety_act_indicator = true;
            int price = 1000;
            int tax = 8;
            int year = 2023;
            int month = 2;
            int project_id = 2345;
            String returnUrl = "http://localhost:8081/transaction/Common3DFrontEndServlet";
            String callbackUrl = "https://staging.paymentz.com/transaction/CommonBackEndServlet";

            TreeMap<String,TreeMap<String,String>>requestHashMap= new TreeMap<>();
            TreeMap<String,String>general=new TreeMap<>();
            general.put("project_id",String.valueOf(project_id));
            general.put("payment_id","1231");

            TreeMap<String,String> payment= new TreeMap<>();
            payment.put("description", "abcdefghi");
            payment.put("amount",amount);
            payment.put("currency","JPY");



            requestHashMap.put("general",general);
            requestHashMap.put("payment",payment);

            String signature=calculateSignature(requestHashMap,secretKey);
            System.out.println("signature-->"+signature);
            general.put("signature",signature);
            requestHashMap.put("general",general);

            Gson gson=new Gson();
            String request=gson.toJson(requestHashMap);

            System.out.println("Request-->" + request);
            String response = doPostHTTPSURLConnectionClient(refundurl, request);
            System.out.println("response-->" + response);

            org.json.JSONObject myResponse = new org.json.JSONObject(response);
            String status = (String)myResponse.get("status");
            System.out.println("status-->"+status);
            String request_id = (String)myResponse.get("request_id");
            System.out.println("request_id-->"+request_id);
            project_id = (int)myResponse.get("project_id");
            System.out.println("project_id-->"+project_id);
            String payment_id = (String)myResponse.get("payment_id");
            System.out.println("payment_id-->"+payment_id);

        }
        catch (PZTechnicalViolationException e)
        {
            e.printStackTrace();
        }
        catch (org.json.JSONException e)
        {
            e.printStackTrace();
        }
        catch(ClassCastException cce)
        {
            cce.printStackTrace();
        }
    }*/
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
                                if (o3 instanceof JSONObject)
                                {
                                    JSONObject innerJson3= (JSONObject) innerJson2.get(key);
                                    Iterator i3=innerJson3.keys();
                                    while (i3.hasNext())
                                    {
                                        String key4 = (String) i3.next();
                                        Object value =innerJson2.get(key4);
                                        requestString.append(key+":"+key2+":"+key3+":"+key4+":"+value+";");
                                    }

                                }
                                else
                                {
                                    Object value=  innerJson2.get(key3);
                                    requestString.append(key+":"+key2+":"+key3+":"+value+";");
                                }
                            }

                        }
                        else
                        {
                            Object value= innerJson1.get(key2);
                            requestString.append(key+":"+key2+":"+value+";");
                        }
                    }

                }
                else
                {
                    Object value=  jsonObject.get(key);
                    requestString.append(key+":"+value+";");
                }
            }
            String reqString=requestString.substring(0,requestString.length()-1);
            System.out.println("requestString-->"+reqString);
            hash=HmacSHA512(reqString,secretKey);

            return hash;
        }
        private static String bytesToHex(byte[] hashInBytes) {

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hashInBytes.length; i++) {
                sb.append(Integer.toString((hashInBytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();

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
                String hex = bytesToHex(raw);
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

    }



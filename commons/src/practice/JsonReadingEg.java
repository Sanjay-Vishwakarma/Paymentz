package practice;


import org.codehaus.jettison.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 6/21/2020.
 */
public class JsonReadingEg
{
    public static void main(String[] args)
    {
        try
        {

            String response = "{\n" +
                    "\t\"project_id\": 15561,\n" +
                    "\t\"payment\": {\n" +
                    "\t\t\"id\": \"1219\",\n" +
                    "\t\t\"type\": \"purchase\",\n" +
                    "\t\t\"status\": \"error\",\n" +
                    "\t\t\"sum\": {\n" +
                    "\t\t\t\"amount\": 1016,\n" +
                    "\t\t\t\"currency\": \"EUR\"\n" +
                    "\t\t}\n" +
                    "\t},\n" +
                    "\t\"account\": {\n" +
                    "\t\t\"number\": \"447700******0006\"\n" +
                    "\t},\n" +
                    "\t\"customer\": {\n" +
                    "\t\t\"id\": \"254\",\n" +
                    "\t\t\"phone\": \"08987786576\"\n" +
                    "\t},\n" +
                    "\t\"errors\": [{\n" +
                    "\t\t\"field\": \"card.month\",\n" +
                    "\t\t\"message\": \"String value found, but an integer is required\"\n" +
                    "\t}, {\n" +
                    "\t\t\"field\": \"payment.amount\",\n" +
                    "\t\t\"message\": \"String value found, but an integer isrequired\"\n" +
                    "\t}],\n" +
                    "\t\"operation\": {\n" +
                    "\t\t\"code\": \"702\",\n" +
                    "\t\t\"message\": \"Malformedrequest\",\n" +
                    "\t\t\"type\": \"sale\",\n" +
                    "\t\t\"status\": \"decline\",\n" +
                    "\t\t\"request_id\": \"82e5a5a2a5fdda24a0fadaa48caf99d86c8231c8-8d1f7c6c465eea062947456063e2a82d3824d8a1-05002168\",\n" +
                    "\t\t\"sum_initial\": {\n" +
                    "\t\t\t\"amount\": \"100\",\n" +
                    "\t\t\t\"currency\": \"EUR\"\n" +
                    "\t\t}\n" +
                    "\t},\n" +
                    "\t\"signature\": \"RfNz7o\\/x9WKp2h1TeBie8SHr61l0jkaSYBO5AYbrDw4HIEPqDM28\\/PMvRSwGrfOT7rLyI4MoSSilElEYkLAQKA==\"\n" +
                    "}";
            String id = "";
            String request_id = "";
            String amount = "";
            String field = "";
            String message = "";
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("payment"))
            {
                if (jsonObject.getJSONObject("payment").has("id"))
                {
                    id = jsonObject.getJSONObject("payment").getString("id");
                }
            }
            if (jsonObject.has("operation"))
            {
                if (jsonObject.getJSONObject("operation").has("request_id"))
                {
                    request_id = jsonObject.getJSONObject("operation").getString("request_id");
                }
            }

       /*     if (jsonObject.has("errors"))
            {
                JSONArray jsonArray = new JSONArray("errors");
                if (jsonArray != null)
                {
                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject errors = jsonArray.getJSONObject(i);
                        {
                            if (i != 0)
                            {
                                field = errors.getString("field");
                                message = errors.getString("message");
                            }
                        }
                    }
                }
       */    /* if(jsonObject.has("operation"))
            {
                if(jsonObject.has("sum_initial"))
                {
                    if(jsonObject.getJSONObject("sum_initial").has("amount"))
                    {
                        amount=jsonObject.getJSONObject("sum_initial").getString("amount");
                    }
                }
            }*/
                System.out.println("id-->" + id);
                System.out.println("request_id->" + request_id);
                System.out.println("field->" + field);
                // System.out.println("amount-->"+amount);
            }
            catch(JSONException e)
            {
                e.printStackTrace();
            }
        }
         }
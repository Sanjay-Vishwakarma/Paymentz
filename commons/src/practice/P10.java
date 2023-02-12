package practice;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * Created by Admin on 6/25/2020.
 */
public class P10
{
    public static void main(String[] args)
    {
        String transactionId = "";
        String message = "";
        String request_id="";
        String token="";
        String status="";
        String code="";
        String type="";
        String eci="";
        String card_holder="";
        try
        {
            String request = "\t{\n" +
                    "\t\t\"project_id\":15561,\n" +
                    "\t\t\"payment\": {\n" +
                    "\t\t\t\"id\": \"126868\",\n" +
                    "\t\t\t\"type\": \"purchase\",\n" +
                    "\t\t\t\"status\": \"awaiting capture\",\n" +
                    "\t\t\t\"date\": \"2020 - 06 - 26 T09: 24: 34 + 0000 \",\n" +
                    "\t\t\t\"method\": \"card\",\n" +
                    "\t\t\t\"sum\": {\n" +
                    "\t\t\t\t\"amount\":90,\n" +
                    "\t\t\t\t\"currency\": \"JPY\"\n" +
                    "\t\t\t},\n" +
                    "\t\t\t\"description\": \"\"\n" +
                    "\t\t},\n" +
                    "\t\t\"account\": {\n" +
                    "\t\t\t\"number\": \"434469 ** ** ** 2508 \",\n" +
                    "\t\t\t\"token\": \"cd09880c0e8d7fa69b633a6e7d7a4cc0de14414dcac93a241d2061dabd56b9e9\",\n" +
                    "\t\t\t\"type\": \"visa\",\n" +
                    "\t\t\t\"card_holder\": \"TESTABC INDIA\",\n" +
                    "\t\t\t\"expiry_month\": \"01\",\n" +
                    "\t\t\t\"expiry_year\": \"2021\"\n" +
                    "\t\t},\n" +
                    "\t\t\"customer\": {\n" +
                    "\t\t\t\"phone\":\"7715934032\"\n" +
                    "\t\t},\n" +
                    "\t\t\"operation\": {\n" +
                    "\t\t\t\"id\":5004069000033502,\n" +
                    "\t\t\t\"type\": \"auth\",\n" +
                    "\t\t\t\"status \": \"success\",\n" +
                    "\t\t\t\"date\": \"2020 - 06 - 26 T09: 24: 34 + 0000 \",\n" +
                    "\t\t\t\"created_date\": \"2020 - 06 - 26 T09: 24: 24 + 0000 \",\n" +
                    "\t\t\t\"request_id\": \"395 c449089f046c709a27163ad9e55c0a12cf055 - a13c4934b5182b66200bb9db660af3a672cbd421 - 05004070 \",\n" +
                    "\t\t\t\"sum_initial\": {\n" +
                    "\t\t\t\t\"amount\": 90,\n" +
                    "\t\t\t\t\"currency\":\"JPY \"\n" +
                    "\t\t\t},\n" +
                    "\t\t\t\"sum_converted\": {\n" +
                    "\t\t\t\t\"amount\": 84,\n" +
                    "\t\t\t\t\"currency\": \"USD\"\n" +
                    "\t\t\t},\n" +
                    "\t\t\t\"code \": \"0 \",\n" +
                    "\t\t\t\"message\": \"Success\",\n" +
                    "\t\t\t\"eci\": \"05 \",\n" +
                    "\t\t\t\"provider\": {\n" +
                    "\t\t\t\t\"id\": 6,\n" +
                    "\t\t\t\t\"payment_id\": \"15931634742172 \",\n" +
                    "\t\t\t\t\"auth_code\": \"563253 \",\n" +
                    "\t\t\t\t\"endpoint_id\":6,\n" +
                    "\t\t\t\t\"date\":\"2018 - 02 - 07 T08: 34: 24 + 0000\"\n" +
                    "\t\t\t}\n" +
                    "\t\t},\n" +
                    "\t\t\"signature\": \"qS0ejX8SjrqBgLENsdM+\\/wt7RLY0q1JQEjN8pblteAxFia2PEbR9JRJQlTNmncjW2xfOefRh\\/RN9nuIrGjGFuw==\"\n" +
                    "\t}";
            JSONObject jsonObject = new JSONObject(request.toString());
            if (jsonObject != null)
            {
                if(jsonObject.has("account"))
                {
                    if(jsonObject.getJSONObject("account").has("card_holder"))
                    {
                        card_holder=jsonObject.getJSONObject("account").getString("card_holder");
                    }
                }
                if(jsonObject.has("operation"))
                {
                    if(jsonObject.getJSONObject("operation").has("eci"))
                    {
                        eci=jsonObject.getJSONObject("operation").getString("eci");
                    }
                    if (jsonObject.getJSONObject("operation").getJSONObject("provider").has("payment_id"))
                    {
                        transactionId = jsonObject.getJSONObject("operation").getJSONObject("provider").getString("payment_id");
                    }
                }
       }
            System.out.println("card_holder->" + card_holder);
            System.out.println("eci->" + eci);
            System.out.println("transactionId->" + transactionId);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
      }
        //jsonObject.getJSONObject("operations").getJSONObject("payment").getString("payment_id");
}



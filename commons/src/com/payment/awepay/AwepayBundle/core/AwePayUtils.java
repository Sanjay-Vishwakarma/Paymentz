package com.payment.awepay.AwepayBundle.core;

import com.directi.pg.TransactionLogger;
import org.codehaus.jettison.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 11/26/2018.
 */
public class AwePayUtils
{
    private static TransactionLogger transactionLogger= new TransactionLogger(AwePayUtils.class.getName());

   /* public static void main(String[] args) {
        String str = "m8amxabLp9Wi1HG2fLSHlXinZaSCr1+HbKNklW2HbKVZ1ajDq9amn4OtXdWry5ifaJZnlWiaaJVllWuTapFmm1rHqdOi1FmXedOY1aTRpdSYh2mmdIeY1KbRqYZopKHVnoZopnGInNOl0aaHbKOW0ZjHXJZ3n1rHqdOi1FmXec6myZfRm8ZYl3ifXcal1KPUXJZ11a3VXJZ3n1rHqdOi1FmXedWs0pmHbKVwiJnUqdClh2mkoM+Z0VmXe55Z1JnTrMqlx5iHbKOi1J3JoM+UzpPWr8qXh2mmdJJolmeWb5JnlGeZaJRjlW2Iq8qXn2qTaZhjiJXPptah1nGTXdScxnGUapNs"; *//* Replace with your encrypted string *//*
        System.out.println(str);

        String dec = decrypt(str, "a3b4b7"); *//* Replace with your RCode *//*
        System.out.println("value----"+dec);
        try {
            // String dec="status=EXC&txid=1543241217739183&error%5Bcode%5D=EE&error%5Bmsg%5D=Customer+failed+to+complete+authentication+within+the+time+limit+&ref1=HDHTDHGD&amount=5&sid=2329";
            str = java.net.URLDecoder.decode(dec, "UTF-8");
            System.out.println("strhub-----"+str);
            Map map=getQueryMap(str);
            System.out.println("map -----"+map);
            System.out.println(map.get("status"));
            System.out.println(map.get("txid"));
            System.out.println(map.get("error[code]"));
            System.out.println(map.get("error[msg]"));
            System.out.println(map.get("ref1"));
            System.out.println(map.get("amount"));
            System.out.println(map.get("sid"));
        } catch (Exception e) {
            System.out.println("Exception -----");
        }

    }
*/
    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String[] p = param.split("=");
            String name = p[0];
            if (p.length > 1)
            {
                String value = p[1];
                map.put(name, value);
                transactionLogger.debug("Value-----"+(name+":::"+value));
            }
        }

        return map;
    }

    public static String decrypt(String str, String key)
    {
        StringBuilder result = new StringBuilder();
        byte[] bytes = java.util.Base64.getDecoder().decode(str);
        for (int i = 0; i < bytes.length; i++)
        {
            int chr = java.lang.Byte.toUnsignedInt(bytes[i]);
            int posi = (i % key.length()) - 1;
            if (posi < 0) {
                posi = key.length() + posi;
            }
            char keychar = key.charAt(posi);

            result.append((char)(chr - (int)keychar));
        }
        return result.toString();
    }

    /*public static Map getQueryParams(String query)
    {
        try
        {
            Map params = new java.util.HashMap();
            for (String param : query.split("&"))
            {
                String[] pair = param.split("=");
                String key = java.net.URLDecoder.decode(pair[0], "UTF-8");
                String value = "";
                if (pair.length > 1)
                {
                    value = java.net.URLDecoder.decode(pair[1], "UTF-8");
                }

                java.util.List values = (List) params.get(key);
                if (values == null)
                {
                    values = new java.util.ArrayList();
                    params.put(key, values);
                }
                values.add(value);
            }
            return params;
        }
        catch (java.io.UnsupportedEncodingException ex)
        {
            throw new AssertionError(ex);
        }
    }*/
}

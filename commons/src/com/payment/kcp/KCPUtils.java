package com.payment.kcp;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * Created by Admin on 1/31/2020.
 */
public class KCPUtils
{
    private static TransactionLogger transactionLogger=new TransactionLogger(KCPUtils.class.getName());
    static HashMap<String,String> currencyMap=new HashMap<>();
    static {
        currencyMap.put("USD","840");
        currencyMap.put("JPY","JPY");
        currencyMap.put("KRW","410");
    }
    public static String getCentAmount(String amount)
    {
        BigDecimal bigDecimal = new BigDecimal(amount);
        bigDecimal = bigDecimal.multiply(new BigDecimal(100));

        Long newAmount = bigDecimal.longValue();
        return newAmount.toString();
    }
    public static String getJPYAmount(String amount)
    {
        double amt= Double.parseDouble(amount);
        double roundOff=Math.round(amt);
        int value=(int)roundOff;
        amount=String.valueOf(value);
        return amount.toString();
    }
    public static String getKRWAmount(String amount)
    {
        double amt= Double.parseDouble(amount);
        double roundOff=Math.round(amt);
        int value=(int)roundOff;
        amount=String.valueOf(value);
        return amount.toString();
    }
    public static String getKWDSupportedAmount(String amount)
    {
        transactionLogger.debug("formatting amount for KWD Currency ---")
        ;
        DecimalFormat d = new DecimalFormat("0");
        Double dObj2 = Double.valueOf(amount);
        dObj2= dObj2 * 1000;
        String amt = d.format(dObj2);
        return amt;
    }
    public static HashMap<String,String> getResponseMap(String response)
    {
        HashMap<String,String> responseMap = new HashMap<String,String>();
        Functions functions =new Functions();
        if(functions.isValueNull(response) && response.contains("\u001F"))
        {
            String a[]= response.split("\u001F");
            for(String res:a)
            {
                if(res.contains("="))
                {
                    String[] val=res.split("=");
                    if(val.length==2)
                    {
                        responseMap.put(val[0],val[1]);
                    }
                    else
                    {
                        responseMap.put(val[0],"");
                    }
                }
            }
        }
        return responseMap;
    }
    public static String getCurrency(String currency)
    {
        return currencyMap.get(currency);
    }
}

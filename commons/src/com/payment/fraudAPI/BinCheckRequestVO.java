package com.payment.fraudAPI;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 5/17/14
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class BinCheckRequestVO extends ATRequestVO
{
    String bin;

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }



    public HashMap getHashMap()
    {
        HashMap requestMap = new HashMap();
        requestMap.put("merchant_id", this.merchant_id);
        requestMap.put("password", this.password);
        requestMap.put("bin", this.bin);

        return requestMap;

    }

    public void setHashMap(HashMap requestHash)
    {
        this.merchant_id = (String) requestHash.get("merchant_id");
        this.password =  (String) requestHash.get("password");
        this.bin = (String) requestHash.get("bin");

    }
}
